/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    sample_rate_converter.cpp
 * @class   Autocorrelation
 * @version 0.5
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Function collection for the sample rate convertion.
 *
 * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
 * @todo    the whule function structure should be ordered in classes !
 *
 * This file collectes the the steering functions for the sample rate convertion function
 * collection which implements three kind of sample rate convertion methods. See the possible
 * conversion definition enumeration above for a more detailed description of the methds defined here.
 */
 
//-------------------------------------------------------------------------------------------------

// Stl headers
#include	<stdio.h>
#include	<stdlib.h>
#include	<string.h>
#include    <math.h>

// Local headers
#include	"sample_rate_converter.h"
#include	"sample_rate_converter_common.h"

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
namespace Resample
{

//-------------------------------------------------------------------------------------------------

// prototype declaration for the private functions in this file
static int psrc_set_converter (SRC_PRIVATE	*psrc, int converter_type);

//-------------------------------------------------------------------------------------------------

static inline int is_bad_src_ratio (double ratio)
{	
  return (ratio < (1.0 / SRC_MAX_RATIO) || ratio > (1.0 * SRC_MAX_RATIO));
}

//-------------------------------------------------------------------------------------------------

SRC_STATE* src_new (int converter_type, int channels, int *error)
{
  SRC_PRIVATE* psrc;
	if (error)
		*error = SRC_ERR_NO_ERROR;
	if (channels < 1)
	{
    if (error)
			*error = SRC_ERR_BAD_CHANNEL_COUNT;
		return NULL;
	};
	if ((psrc = (SRC_PRIVATE*) calloc (1, sizeof (*psrc))) == NULL)
	{	
    if (error)
			*error = SRC_ERR_MALLOC_FAILED;
		return NULL;
	};
	psrc->channels = channels;
	psrc->mode = SRC_MODE_PROCESS;
	if (psrc_set_converter (psrc, converter_type) != SRC_ERR_NO_ERROR)
	{
    if (error)
			*error = SRC_ERR_BAD_CONVERTER;
		free (psrc);
		psrc = NULL;
	};
	src_reset ((SRC_STATE*) psrc);
	return (SRC_STATE*) psrc;
}

//-------------------------------------------------------------------------------------------------

SRC_STATE* src_delete (SRC_STATE *state)
{	
  SRC_PRIVATE *psrc;
	psrc = (SRC_PRIVATE*) state;
	if (psrc)
	{	if (psrc->private_data)
			free (psrc->private_data);
		memset (psrc, 0, sizeof (SRC_PRIVATE));
		free (psrc);
		};
	return NULL;
}

//-------------------------------------------------------------------------------------------------

int src_process (SRC_STATE *state, SRC_DATA *data)
{
  SRC_PRIVATE *psrc;
	int error;
	psrc = (SRC_PRIVATE*) state;
	if (psrc == NULL)
		return SRC_ERR_BAD_STATE;
	if (psrc->vari_process == NULL || psrc->const_process == NULL)
		return SRC_ERR_BAD_PROC_PTR;
	if (psrc->mode != SRC_MODE_PROCESS)
		return SRC_ERR_BAD_MODE;
	/* Check for valid SRC_DATA first. */
	if (data == NULL)
		return SRC_ERR_BAD_DATA;
	/* Check src_ratio is in range. */
	if (is_bad_src_ratio (data->src_ratio))
		return SRC_ERR_BAD_SRC_RATIO;
	/* And that data_in and data_out are valid. */
	if (data->data_in == NULL || data->data_out == NULL)
		return SRC_ERR_BAD_DATA_PTR;
	if (data->data_in == NULL)
		data->input_frames = 0;
	if (data->input_frames < 0)
		data->input_frames = 0;
	if (data->output_frames < 0)
		data->output_frames = 0;
	if (data->data_in < data->data_out)
	{	if (data->data_in + data->input_frames * psrc->channels > data->data_out)
		{	
      /*-printf ("\n\ndata_in: %p    data_out: %p\n",
			(void*) (data->data_in + data->input_frames * psrc->channels), (void*) data->data_out);-*/
			return SRC_ERR_DATA_OVERLAP;
		};
	}
	else if (data->data_out + data->output_frames * psrc->channels > data->data_in)
	{
    /*-printf ("\n\ndata_in : %p   ouput frames: %ld    data_out: %p\n", (void*) data->data_in, data->output_frames, (void*) data->data_out);
		printf ("data_out: %p (%p)    data_in: %p\n", (void*) data->data_out,
		(void*) (data->data_out + data->input_frames * psrc->channels), (void*) data->data_in);-*/
		return SRC_ERR_DATA_OVERLAP;
	};
	/* Set the input and output counts to zero. */
	data->input_frames_used = 0;
	data->output_frames_gen = 0;
	/* Special case for when last_ratio has not been set. */
	if (psrc->last_ratio < (1.0 / SRC_MAX_RATIO))
		psrc->last_ratio = data->src_ratio;
	/* Now process. */
	if (fabs (psrc->last_ratio - data->src_ratio) < 1e-15)
		error = psrc->const_process (psrc, data);
	else
		error = psrc->vari_process (psrc, data);
	return error;
}

//-------------------------------------------------------------------------------------------------

int src_reset (SRC_STATE *state)
{	
  SRC_PRIVATE *psrc;
	if ((psrc = (SRC_PRIVATE*) state) == NULL)
		return SRC_ERR_BAD_STATE;
	if (psrc->reset != NULL)
		psrc->reset (psrc);
	psrc->last_position = 0.0;
	psrc->last_ratio = 0.0;
	psrc->error = SRC_ERR_NO_ERROR;
	return SRC_ERR_NO_ERROR;
}

//-------------------------------------------------------------------------------------------------

/**
 * Private function definition needed only in this file.
 */
static int psrc_set_converter (SRC_PRIVATE	*psrc, int converter_type)
{
	if (sinc_set_converter (psrc, converter_type) == SRC_ERR_NO_ERROR)
		return SRC_ERR_NO_ERROR;
	if (zoh_set_converter (psrc, converter_type) == SRC_ERR_NO_ERROR)
		return SRC_ERR_NO_ERROR;
	if (linear_set_converter (psrc, converter_type) == SRC_ERR_NO_ERROR)
		return SRC_ERR_NO_ERROR;
	return SRC_ERR_BAD_CONVERTER;
}

//-------------------------------------------------------------------------------------------------

} // namespace Resample

} // namespace math

} // namespace clauer

