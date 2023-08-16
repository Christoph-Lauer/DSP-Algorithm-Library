/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    sample_rate_converter.h
 * @class   Reasmple
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
 * 
 * NOTE: the ZOH converter should bring the poorest results, followed from the 
 * linear converter. Best results bring the sinc converters.
 */

//-------------------------------------------------------------------------------------------------

#ifndef SAMPLE_RATE_CONVERTER
#define SAMPLE_RATE_CONVERTER

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
namespace Resample
{

//-------------------------------------------------------------------------------------------------

/// Opaque data type SRC_STATE
typedef struct SRC_STATE_tag SRC_STATE;

//-------------------------------------------------------------------------------------------------

/**
 * This struct holds the basic data structure for the resampling process.
 * @param   in                  A float pointer to the input vector.
 * @param   out                 A float vector to the preallocated output vector.
 * @param   imput_frames        The number of samples to convert.
 * @param   output_frames       The maximum number of output samples to generate.
 * @param   input_frames_used   This value is set while the transformation process
 *                              and up counts the used input samples while the resampling.
 * @param   output_frames_gen   This value is set while the transformation process
 *                              and up counts the generated output samples while the resampling process.
 * @param   end_of_input        This 1/0 value is set to 0 for streaming and set to 1
 *                              if if this was the last buffer which was resampled
 * @param   src_ratio           The rate of output_sample_rate / input_sample_rate.
 */
typedef struct
{	
  float*  data_in;
  float*  data_out;
	long	  input_frames;
  long    output_frames;
	long	  input_frames_used;    // will be set while the processing
  long    output_frames_gen;    // will be set while the processing
	int		  end_of_input;
  double	src_ratio;
} SRC_DATA;

//-------------------------------------------------------------------------------------------------

/**
 * The following enums can be used to set the interpolator type using the function src_set_converter().
 * SRC_SINC_BEST_QUALITY    This is a bandlimited interpolator derived from the mathematical sinc
 *                          function and this is the highest quality sinc based converter, providing
 *                          a worst case Signal-to-Noise Ratio (SNR) of 97 decibels (dB) at a bandwidth
 *                          of 97%. All three SRC_SINC_* converters are based on the techniques of
 *                          Julius O. Smith although this code was developed independantly.
 * SRC_SINC_MEDIUM_QUALITY  This is another bandlimited interpolator much like the previous one.
 *                          It has an SNR of 97dB and a bandwidth of 90%. The speed of the conversion
 *                          is much faster than the previous one.
 * SRC_SINC_FASTEST         This is the fastest bandlimited interpolator and has an SNR of 97dB and a bandwidth of 80%.
 * SRC_ZERO_ORDER_HOLD      A Zero Order Hold converter (interpolated value is equal to the last value).
 *                          The quality is poor but the conversion speed is blindlingly fast.
 * SRC_LINEAR               A linear converter. Again the quality is poor, but the conversion speed is
 *                          blindingly fast.
 *
 * NOTE: the ZOH converter should bring the poorest results, followed from the 
 * linear converter. Best results bring the sinc converters.
 */
enum
{
	SRC_SINC_BEST_QUALITY		  = 0,
	SRC_SINC_MEDIUM_QUALITY		= 1,
	SRC_SINC_FASTEST			    = 2,
	SRC_ZERO_ORDER_HOLD			  = 3,
	SRC_LINEAR					      = 4,
};

//-------------------------------------------------------------------------------------------------

/**
 *	This is the standard initialisation function : return an anonymous pointer to the
 *	internal state of the converter. Choose a converter from the enums below.
 *	Error returned in *error.
 */
SRC_STATE* src_new (int converter_type, int channels, int *error);

//-------------------------------------------------------------------------------------------------

/**
 *	Cleanup all internal allocations.
 *	Always returns NULL.
 */
SRC_STATE* src_delete (SRC_STATE *state);

//-------------------------------------------------------------------------------------------------

/**
 *	Standard processing function.
 *	Returns non zero on error.
 */
int src_process (SRC_STATE *state, SRC_DATA *data);

//-------------------------------------------------------------------------------------------------

/**
 *	Reset the internal SRC state. Does not modify the quality settings.
 *	Does not free any memory allocations. Returns non zero on error.
 */
int src_reset (SRC_STATE *state);

//-------------------------------------------------------------------------------------------------

} // namespace Resample

} // namespace math

} // namespece clauer

//-------------------------------------------------------------------------------------------------

#endif	// SAMPLE_RATE_CONVERTER
