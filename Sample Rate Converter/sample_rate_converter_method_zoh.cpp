/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    sample_rate_converter_method_zoh.cpp
 * @class   clauer::math::Reasmple
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   A Zero Order Hold converter (interpolated value is equal to the last value).
 *          The quality is poor but the conversion speed is blindlingly fast.
 * @see     http://en.wikipedia.org/wiki/Zero-order_hold
 *
 * This function collection implements a simple zero hold converter which brings not so good
 * signal reconstruction quality but the convertion algorithmus is incredible fast. This ZOH 
 * convertion algorithmus 
 */

//-------------------------------------------------------------------------------------------------

// stl headers
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//-------------------------------------------------------------------------------------------------

// local headers
#include "sample_rate_converter_common.h"

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
namespace Resample
{

//-------------------------------------------------------------------------------------------------

/**
 * Define the local private prototypes here
 */
 //@{
static int zoh_vari_process(SRC_PRIVATE* psrc, SRC_DATA* data);
static void zoh_reset(SRC_PRIVATE* psrc);
//@}

//-------------------------------------------------------------------------------------------------

#define	ZOH_MAGIC_MARKER	MAKE_MAGIC ('s', 'r', 'c', 'z', 'o', 'h')

//-------------------------------------------------------------------------------------------------

typedef struct
{	
  int		zoh_magic_marker;
	int		channels;
	int		reset;
	long	in_count, in_used;
	long	out_count, out_gen;
	float	last_value [1];
} ZOH_DATA;

//-------------------------------------------------------------------------------------------------

static int zoh_vari_process(SRC_PRIVATE *psrc, SRC_DATA *data)
{	
  ZOH_DATA 	*zoh;
	double		src_ratio, input_index, rem;
	int			  ch;

	if (psrc->private_data == NULL)
		return SRC_ERR_NO_PRIVATE;

	zoh = (ZOH_DATA*) psrc->private_data;

	if (zoh->reset)
	{	
    // If we have just been reset, set the last_value data.
		for (ch = 0; ch < zoh->channels; ch++)
			zoh->last_value [ch] = data->data_in [ch];
		zoh->reset = 0;
		};

	zoh->in_count = data->input_frames * zoh->channels;
	zoh->out_count = data->output_frames * zoh->channels;
	zoh->in_used = zoh->out_gen = 0;

	src_ratio = psrc->last_ratio;
	input_index = psrc->last_position;

	// Calculate samples before first sample in input array.
	while (input_index < 1.0 && zoh->out_gen < zoh->out_count)
	{
		if (zoh->in_used + zoh->channels * input_index >= zoh->in_count)
			break;

		if (zoh->out_count > 0 && fabs (psrc->last_ratio - data->src_ratio) > SRC_MIN_RATIO_DIFF)
			src_ratio = psrc->last_ratio + zoh->out_gen * (data->src_ratio - psrc->last_ratio) / zoh->out_count;

		for (ch = 0; ch < zoh->channels; ch++)
		{	data->data_out [zoh->out_gen] = zoh->last_value [ch];
			zoh->out_gen ++;
			};

		// Figure out the next index.
		input_index += 1.0 / src_ratio;
		};

	rem = fmod_one (input_index);
	zoh->in_used += zoh->channels * lrint (input_index - rem);
	input_index = rem;

	// Main processing loop.
	while (zoh->out_gen < zoh->out_count && zoh->in_used + zoh->channels * input_index <= zoh->in_count)
	{
		if (zoh->out_count > 0 && fabs (psrc->last_ratio - data->src_ratio) > SRC_MIN_RATIO_DIFF)
			src_ratio = psrc->last_ratio + zoh->out_gen * (data->src_ratio - psrc->last_ratio) / zoh->out_count;

		for (ch = 0; ch < zoh->channels; ch++)
		{	
      data->data_out [zoh->out_gen] = data->data_in [zoh->in_used - zoh->channels + ch];
			zoh->out_gen ++;
		};

		// Figure out the next index.
		input_index += 1.0 / src_ratio;
		rem = fmod_one (input_index);

		zoh->in_used += zoh->channels * lrint (input_index - rem);
		input_index = rem;
		};

	if (zoh->in_used > zoh->in_count)
	{	input_index += (zoh->in_used - zoh->in_count) / zoh->channels;
		zoh->in_used = zoh->in_count;
		};

	psrc->last_position = input_index;

	if (zoh->in_used > 0)
		for (ch = 0; ch < zoh->channels; ch++)
			zoh->last_value [ch] = data->data_in [zoh->in_used - zoh->channels + ch];

	// Save current ratio rather then target ratio.
	psrc->last_ratio = src_ratio;

	data->input_frames_used = zoh->in_used / zoh->channels;
	data->output_frames_gen = zoh->out_gen / zoh->channels;

	return SRC_ERR_NO_ERROR;
}

//-------------------------------------------------------------------------------------------------

/**
 * Converter initialization 
 */
int zoh_set_converter (SRC_PRIVATE *psrc, int src_enum)
{	ZOH_DATA *zoh = NULL;

	if (src_enum != SRC_ZERO_ORDER_HOLD)
		return SRC_ERR_BAD_CONVERTER;

	if (psrc->private_data != NULL)
	{	
    zoh = (ZOH_DATA*) psrc->private_data;
		if (zoh->zoh_magic_marker != ZOH_MAGIC_MARKER)
		{	
      free (psrc->private_data);
			psrc->private_data = NULL;
		};
	};

	if (psrc->private_data == NULL)
	{	
    zoh = (ZOH_DATA*) calloc (1, sizeof (*zoh) + psrc->channels * sizeof (float));
		if (zoh == NULL)
			return SRC_ERR_MALLOC_FAILED;
		psrc->private_data = zoh;
	};

	zoh->zoh_magic_marker = ZOH_MAGIC_MARKER;
	zoh->channels = psrc->channels;

	psrc->const_process = zoh_vari_process;
	psrc->vari_process = zoh_vari_process;
	psrc->reset = zoh_reset;

	zoh_reset (psrc);

	return SRC_ERR_NO_ERROR;
}

//-------------------------------------------------------------------------------------------------

/**
 * preparation for re using the data structures
 */
static void zoh_reset (SRC_PRIVATE *psrc)
{	
  ZOH_DATA *zoh;

	zoh = (ZOH_DATA*) psrc->private_data;
	if (zoh == NULL)
		return;

	zoh->channels = psrc->channels;
	zoh->reset = 1;
	memset (zoh->last_value, 0, sizeof (zoh->last_value [0]) * zoh->channels);

	return;
}

//-------------------------------------------------------------------------------------------------

} // namepace Resample

} // namepace math

} // namespace clauer

//-------------------------------------------------------------------------------------------------
