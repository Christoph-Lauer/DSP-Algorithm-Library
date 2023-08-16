/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    sample_rate_converter_method_sinc.cpp
 * @class   clauer::math::Reasmple
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Resampling after the Theory of Ideal Bandlimited Interpolation.
 * @see     http://ccrma-www.stanford.edu/~jos/resample/
 * 
 * Bandlimited interpolation of discrete-time signals is a basic tool having extensive application in
 * digital signal processing. In general, the problem is to correctly compute signal values at arbitrary
 * continuous times from a set of discrete-time samples of the signal amplitude. In other words,
 * we must be able to interpolate the signal between samples. Since the original signal is always
 * assumed to be bandlimited to half the sampling rate, (otherwise aliasing distortion would occur upon
 * sampling), Shannon’s sampling theorem tells us the signal can be exactly and uniquely reconstructed
 * for all time from its samples by bandlimited interpolation.
 * There are many methods for interpolating discrete points. For example, Lagrange interpolation
 * is the classical technique of finding an order N polynomial which passes through N+1 given points.
 * The technique known as cubic splines fits a third-order polynomial through two points so as
 * to achieve a certain slope at one of the points. (This allows for a smooth chain of third-order
 * polynomial passing through a set of points.)
 * In digital audio, what matters is the audibility of interpolation error between samples. Since
 * Shannon’s sampling theorem says it is possible to restore an audio signal exactly from its samples,
 * it makes sense that the best digital audio interpolators would be based on that theory. Such “ideal”
 * interpolation is called bandlimited interpolation.
 *
 * The method implemented here uses three filer bank sets with different size where the smallest filter
 * has the worst quality and the longest filter brings the best quality rsults. The three filter 
 * coefficients sets are defined extranlly in three header files which are included above:
 *
 * SRC_SINC_BEST_QUALITY -  This is a bandlimited interpolator derived from the mathematical sinc 
 *                          function and this is the highest quality sinc based converter, providing
 *                          a worst case Signal-to-Noise Ratio (SNR) of 97 decibels (dB) at a bandwidth 
 *                          of 97%. All three SRC_SINC_* converters are based on the techniques of 
 *                          Julius O. Smith although this code was developed independantly.
 * SRC_SINC_MEDIUM_QUALITY -This is another bandlimited interpolator much like the previous one. It 
 *                          has an SNR of 97dB and a bandwidth of 90%. The speed of the conversion
 *                          is much faster than the previous one.
 * SRC_SINC_FASTEST -       This is the fastest bandlimited interpolator and has an SNR of 97dB and 
 *                          a bandwidth of 80%.
 *
 * Note that this file has noe real header file and appare from the generic.h
 * where the public interface functions are declared. The local function prototypes
 * are declared implicit into this file.
 */

//-------------------------------------------------------------------------------------------------

// Stl headers
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Local headers
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
 * This are the type/macro definitions for the filter definition files.
 */
 //@{
#define	SINC_MAGIC_MARKER	MAKE_MAGIC (' ', 's', 'i', 'n', 'c', ' ')
#define MAKE_INCREMENT_T(x) 	((increment_t) (x))
#define	SHIFT_BITS  12
#define	FP_ONE		  ((double) (((increment_t) 1) << SHIFT_BITS))
#define	INV_FP_ONE  (1.0 / FP_ONE)
typedef int32_t increment_t;   // filter data type   
typedef float	coeff_t;         // filter data type   
//@}

/**
 * Including the filter coefficient definition header files.
 * Filter files are available on three sizes reps. speed where 
 * the fastest filter hast the worst quality.
 */
//@{
#include "sample_rate_converter_filter_coefficients_low_qual.h"
#include "sample_rate_converter_filter_coefficients_mid_qual.h"
#include "sample_rate_converter_filter_coefficients_high_qual.h"
//@}

//-------------------------------------------------------------------------------------------------

/**
 * The basic Filter structure dfinition.
 */
typedef struct
{	
  int		  sinc_magic_marker;
	int		  channels;
	long	  in_count, in_used;
	long	  out_count, out_gen;
	int		  coeff_half_len, index_inc;
	double	src_ratio, input_index;
	coeff_t const	*coeffs;
	int		  b_current, b_end, b_real_end, b_len;
	float	  buffer [1];
} SINC_FILTER;

//-------------------------------------------------------------------------------------------------

/**
 * This are the prototype declarations for private not extern function in this file
 */
//@{
static int sinc_vari_process (SRC_PRIVATE *psrc, SRC_DATA *data);
static double calc_output (SINC_FILTER *filter, increment_t increment, increment_t start_filter_index, int ch);
static void prepare_data (SINC_FILTER *filter, SRC_DATA *data, int half_filter_chan_len);
static void sinc_reset (SRC_PRIVATE *psrc);
//@}

//-------------------------------------------------------------------------------------------------

/**
 * This static inline functions are all for fast casting
 */
//@{
static inline increment_t double_to_fp (double x)
{
  if (sizeof (increment_t) == 8)
		return (lrint ((x) * FP_ONE)); // changed here from llrint to lrint --> should have no effect on small sized...
	return (lrint ((x) * FP_ONE));
}

static inline increment_t int_to_fp (int x)
{return (((increment_t) (x)) << SHIFT_BITS);}

static inline int fp_to_int (increment_t x)
{return (((x) >> SHIFT_BITS));}

static inline increment_t fp_fraction_part (increment_t x)
{return ((x) & ((((increment_t) 1) << SHIFT_BITS) - 1));}

static inline double fp_to_double (increment_t x)
{return fp_fraction_part (x) * INV_FP_ONE;}
//@}

//-------------------------------------------------------------------------------------------------

/**
 * This function sets the filter coefficient type of the 
 */
int sinc_set_converter (SRC_PRIVATE *psrc, int src_enum)
{	
  SINC_FILTER* filter, temp_filter;
	increment_t count;
	int bits;

	/* Quick sanity check. */
	if (SHIFT_BITS >= sizeof (increment_t) * 8 - 1)
		return SRC_ERR_SHIFT_BITS;

	if (psrc->private_data != NULL)
	{	
    filter = (SINC_FILTER*) psrc->private_data;
		if (filter->sinc_magic_marker != SINC_MAGIC_MARKER)
		{	
      free (psrc->private_data);
			psrc->private_data = NULL;
		};
	};

	memset (&temp_filter, 0, sizeof (temp_filter));

	temp_filter.sinc_magic_marker = SINC_MAGIC_MARKER;
	temp_filter.channels = psrc->channels;

	psrc->const_process = sinc_vari_process;
	psrc->vari_process = sinc_vari_process;
	psrc->reset = sinc_reset;

	switch (src_enum)
	{
    case SRC_SINC_FASTEST :
				temp_filter.coeffs = fastest_coeffs.coeffs;
				temp_filter.coeff_half_len = ARRAY_LEN (fastest_coeffs.coeffs) - 1;
				temp_filter.index_inc = fastest_coeffs.increment;
				break;

		case SRC_SINC_MEDIUM_QUALITY :
				temp_filter.coeffs = slow_mid_qual_coeffs.coeffs;
				temp_filter.coeff_half_len = ARRAY_LEN (slow_mid_qual_coeffs.coeffs) - 1;
				temp_filter.index_inc = slow_mid_qual_coeffs.increment;
				break;

		case SRC_SINC_BEST_QUALITY :
				temp_filter.coeffs = slow_high_qual_coeffs.coeffs;
				temp_filter.coeff_half_len = ARRAY_LEN (slow_high_qual_coeffs.coeffs) - 1;
				temp_filter.index_inc = slow_high_qual_coeffs.increment;
				break;

		default :
				return SRC_ERR_BAD_CONVERTER;
		};

	/*
	 * FIXME : This needs to be looked at more closely to see if there is
	 * a better way. Need to look at prepare_data () at the same time.
   */

	temp_filter.b_len = 1000 + 2 * lrint (0.5 + 2 * temp_filter.coeff_half_len / (temp_filter.index_inc * 1.0) * SRC_MAX_RATIO);
	temp_filter.b_len = MIN (temp_filter.b_len, 4096);
	temp_filter.b_len *= temp_filter.channels;

	if ((filter = (SINC_FILTER*) calloc (1, sizeof (SINC_FILTER) + sizeof (filter->buffer [0]) * (temp_filter.b_len + temp_filter.channels))) == NULL)
		return SRC_ERR_MALLOC_FAILED;

	*filter = temp_filter;
	memset (&temp_filter, 0xEE, sizeof (temp_filter));

	psrc->private_data = filter;

	sinc_reset (psrc);

	count = filter->coeff_half_len;
	for (bits = 0; (MAKE_INCREMENT_T (1) << bits) < count; bits++)
		count |= (MAKE_INCREMENT_T (1) << bits);

	if (bits + SHIFT_BITS - 1 >= (int) (sizeof (increment_t) * 8))
		return SRC_ERR_FILTER_LEN;

	return SRC_ERR_NO_ERROR;
}

//-------------------------------------------------------------------------------------------------

/**
 * preparation for re using the data structures
 */
static void sinc_reset (SRC_PRIVATE *psrc)
{
  SINC_FILTER *filter;

	filter = (SINC_FILTER*) psrc->private_data;
	if (filter == NULL)
		return;

	filter->b_current = filter->b_end = 0;
	filter->b_real_end = -1;

	filter->src_ratio = filter->input_index = 0.0;

	memset (filter->buffer, 0, filter->b_len * sizeof (filter->buffer [0]));

	// Set this for a sanity check
	memset (filter->buffer + filter->b_len, 0xAA, filter->channels * sizeof (filter->buffer [0]));
}

//-------------------------------------------------------------------------------------------------

static int sinc_vari_process (SRC_PRIVATE *psrc, SRC_DATA *data)
{
  SINC_FILTER *filter;
	double		input_index, src_ratio, count, float_increment, terminate, rem;
	increment_t	increment, start_filter_index;
	int			half_filter_chan_len, samples_in_hand, ch;

	if (psrc->private_data == NULL)
		return SRC_ERR_NO_PRIVATE;

	filter = (SINC_FILTER*) psrc->private_data;

	// If there is not a problem, this will be optimised out.
	if (sizeof (filter->buffer [0]) != sizeof (data->data_in [0]))
		return SRC_ERR_SIZE_INCOMPATIBILITY;

	filter->in_count = data->input_frames * filter->channels;
	filter->out_count = data->output_frames * filter->channels;
	filter->in_used = filter->out_gen = 0;

	src_ratio = psrc->last_ratio;

	// Check the sample rate ratio wrt the buffer len.
	count = (filter->coeff_half_len + 2.0) / filter->index_inc;
	if (MIN (psrc->last_ratio, data->src_ratio) < 1.0)
		count /= MIN (psrc->last_ratio, data->src_ratio);

	// Maximum coefficientson either side of center point.
	half_filter_chan_len = filter->channels * (lrint (count) + 1);

	input_index = psrc->last_position;
	float_increment = filter->index_inc;

	rem = fmod_one (input_index);
	filter->b_current = (filter->b_current + filter->channels * lrint (input_index - rem)) % filter->b_len;
	input_index = rem;

	terminate = 1.0 / src_ratio + 1e-20;

	// Main processing loop.
	while (filter->out_gen < filter->out_count)
	{
		// Need to reload buffer ?
		samples_in_hand = (filter->b_end - filter->b_current + filter->b_len) % filter->b_len;

		if (samples_in_hand <= half_filter_chan_len)
		{ 
      prepare_data (filter, data, half_filter_chan_len);

			samples_in_hand = (filter->b_end - filter->b_current + filter->b_len) % filter->b_len;
			if (samples_in_hand <= half_filter_chan_len)
				break;
			};

		// This is the termination condition.
		if (filter->b_real_end >= 0)
		{	
      if (filter->b_current + input_index + terminate >= filter->b_real_end)
				break;
		};

		if (filter->out_count > 0 && fabs (psrc->last_ratio - data->src_ratio) > 1e-10)
			src_ratio = psrc->last_ratio + filter->out_gen * (data->src_ratio - psrc->last_ratio) / filter->out_count;

		float_increment = filter->index_inc * 1.0;
		if (src_ratio < 1.0)
			float_increment = filter->index_inc * src_ratio;

		increment = double_to_fp (float_increment);

		start_filter_index = double_to_fp (input_index * float_increment);

		for (ch = 0; ch < filter->channels; ch++)
		{
      data->data_out [filter->out_gen] = (float) ((float_increment / filter->index_inc) *
											calc_output (filter, increment, start_filter_index, ch));
			filter->out_gen ++;
		};

		// Figure out the next index.
		input_index += 1.0 / src_ratio;
		rem = fmod_one (input_index);

		filter->b_current = (filter->b_current + filter->channels * lrint (input_index - rem)) % filter->b_len;
		input_index = rem;
		};

	psrc->last_position = input_index;

	// Save current ratio rather then target ratio.
	psrc->last_ratio = src_ratio;

	data->input_frames_used = filter->in_used / filter->channels;
	data->output_frames_gen = filter->out_gen / filter->channels;

	return SRC_ERR_NO_ERROR;
}

//-------------------------------------------------------------------------------------------------

/**
 * Converter initialization 
 */
static void prepare_data (SINC_FILTER *filter, SRC_DATA *data, int half_filter_chan_len)
{	
  int len = 0;

	if (filter->b_real_end >= 0)
		return;	// This doesn't make sense, so return.

	if (filter->b_current == 0)
	{	
    // Initial state. Set up zeros at the start of the buffer an then load new data after that.
		len = filter->b_len - 2 * half_filter_chan_len;

		filter->b_current = filter->b_end = half_filter_chan_len;
		}
	else if (filter->b_end + half_filter_chan_len + filter->channels < filter->b_len)
	{	
    // Load data at current end position.
		len = MAX (filter->b_len - filter->b_current - half_filter_chan_len, 0);
		}
	else
	{	
    // Move data at end of buffer back to the start of the buffer.
		len = filter->b_end - filter->b_current;
		memmove (filter->buffer, filter->buffer + filter->b_current - half_filter_chan_len,
						(half_filter_chan_len + len) * sizeof (filter->buffer [0]));

		filter->b_current = half_filter_chan_len;
		filter->b_end = filter->b_current + len;

		// Now load data at current end of buffer.
		len = MAX (filter->b_len - filter->b_current - half_filter_chan_len, 0);
		};

	len = MIN (filter->in_count - filter->in_used, len);
	len -= (len % filter->channels);

	memcpy (filter->buffer + filter->b_end, data->data_in + filter->in_used,
						len * sizeof (filter->buffer [0]));

	filter->b_end += len;
	filter->in_used += len;

	if (filter->in_used == filter->in_count &&
			filter->b_end - filter->b_current < 2 * half_filter_chan_len && data->end_of_input)
	{	
    // Handle the case where all data in the current buffer has bee consumed and this is the last buffer.
		if (filter->b_len - filter->b_end < half_filter_chan_len + 5)
		{	
      // If necessary, move data down to the start of the buffer.
			len = filter->b_end - filter->b_current;
			memmove (filter->buffer, filter->buffer + filter->b_current - half_filter_chan_len,
							(half_filter_chan_len + len) * sizeof (filter->buffer [0]));

			filter->b_current = half_filter_chan_len;
			filter->b_end = filter->b_current + len;
			};

		filter->b_real_end = filter->b_end;
		len = half_filter_chan_len + 5;

		memset (filter->buffer + filter->b_end, 0, len * sizeof (filter->buffer [0]));
		filter->b_end += len;
		};

	return;
}

//-------------------------------------------------------------------------------------------------

/**
 * This function is the main processing loop.
 */
static double calc_output (SINC_FILTER *filter, increment_t increment, increment_t start_filter_index, int ch)
{	
  double		  fraction, left, right, icoeff;
	increment_t	filter_index, max_filter_index;
	int			    data_index, coeff_count, indx;

	// Convert input parameters into fixed point.
	max_filter_index = int_to_fp (filter->coeff_half_len);

	// First apply the left half of the filter.
	filter_index = start_filter_index;
	coeff_count = (max_filter_index - filter_index) / increment;
	filter_index = filter_index + coeff_count * increment;
	data_index = filter->b_current - filter->channels * coeff_count + ch;

	left = 0.0;
	do
	{	fraction = fp_to_double (filter_index);
		indx = fp_to_int (filter_index);

		icoeff = filter->coeffs [indx] + fraction * (filter->coeffs [indx + 1] - filter->coeffs [indx]);

		left += icoeff * filter->buffer [data_index];

		filter_index -= increment;
		data_index = data_index + filter->channels;
		}
	while (filter_index >= MAKE_INCREMENT_T (0));

	// Now apply the right half of the filter.
	filter_index = increment - start_filter_index;
	coeff_count = (max_filter_index - filter_index) / increment;
	filter_index = filter_index + coeff_count * increment;
	data_index = filter->b_current + filter->channels * (1 + coeff_count) + ch;

	right = 0.0;
	do
	{	fraction = fp_to_double (filter_index);
		indx = fp_to_int (filter_index);

		icoeff = filter->coeffs [indx] + fraction * (filter->coeffs [indx + 1] - filter->coeffs [indx]);

		right += icoeff * filter->buffer [data_index];

		filter_index -= increment;
		data_index = data_index - filter->channels;
		}
	while (filter_index > MAKE_INCREMENT_T (0));

	return (left + right);
}

//-------------------------------------------------------------------------------------------------

} // namespace Resample

} // namespace math

} // namepsace clauer
