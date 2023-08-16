/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    digital_filter.h
 * @class   DigitalFilter
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This class implemts digital filters (filter with critical damping, Bessel, Butterworth, Tschebyscheff)
 * @see     http://en.wikipedia.org/wiki/Digital_filter
 * @see     http://en.wikipedia.org/wiki/Digital_biquad_filter
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

// C Languange Library headers
#include <cstddef>

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_DIGITAL_FILTER
#define CLAUER_MATH_DIGITAL_FILTER
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class collects a set of usefull digital filter functions which are available for the four base
 * types of digital filters (CriticalDamping, Bessel, Butterwirth and Tschebyscheff) implemnted with a low
 * pass, a high pass, a band-pass and a bandstop filter. The filter coefficients defined into this
 * class are predefined for the IIR filter. Higher filter orders are realized with multiple biquad filters 
 * in concatenation. A digital biquad filter is a second-order recursive linear filter, containing two
 * poles and two zeros. "Biquad" is an abbreviation of "biquadratic", which refers to the fact that 
 * in the Z domain, its transfer function is the ratio of two quadratic functions. 
 */

//------------------------------------------------------------------------------------------------

class DigitalFilter
{

//------------------------------------------------------------------------------------------------

public:

 /** 
  * This enumeration specifyes the type of the used filter coefficients. As desribed above four
  * digital filters are implenmeted, the Critical-Damping filter, the Bessel the Butterwoth and
  * Tschebyscheff filters.
  */
  enum DIGITAL_FILTER_TYPE
  {
    DIGITAL_FILTER_TYPE_CRITICAL_DAMPING = 0,
    DIGITAL_FILTER_TYPE_BESSEL,
    DIGITAL_FILTER_TYPE_BUTTERWORTH,
    DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF,
  };
  
//------------------------------------------------------------------------------------------------

 /**
  * This function defines the base functions for the other two functions, the band-pass and the 
  * band-stop filters. The low and high pass filters are defiend here in one fucntion because most 
  * of the code is identical for both. The implementation uses a standart IIR routine where the 
  * specification can be found in any literature or in the web. It can be switched between the low 
  * and the high pass filter with an simple flag. As follows the paramter description:
  *                              
  * @param inputSamples          The time domain input sample vector.
  * @param length                The length of the time domain input sample vector.
  * @param sampleRate            The sample rate of the time domain input sample vector.
  * @param type                  One of the four enumerations for the filter type.
  * @param cutOffFrequency       The cut off frequency for the low or the high pass.
  * @param filterOrder           The filter order must be an even value in the interval [2,4,6,8]
  * @param highPass              An LowPass is applayed to the inputSample values per default, if this
  *                              optional flag is set to true an high pass filter is applyed.
  * @return                      This implementation works inplace.
  */
  static void applyFilter(double* inputSamples, const int length, const int sampleRate, const DIGITAL_FILTER_TYPE type, const double cutOffFrequency, const int filterOrder, bool highPass = false);

 
//------------------------------------------------------------------------------------------------

 /**
  * This second pair of functions uses the base low and high pass filters for the implemntation of the 
  * band-pass and band-stop filters. Both functions hav the same fucntion signatures.
  *
  * @param inputSamples          The time domain input sample vector.
  * @param length                The length of the time domain input sample vector.
  * @param sampleRate            The sample rate of the time domain input sample vector.
  * @param type                  One of the four enumerations for the filter type.
  * @param cutOffFrequencyLow    The cut off frequency for the lower low pass side.
  * @param cutOffFrequencyHigh   The cut off frequency for the upper high pass side.
  * @param filterOrder           The filter order must be an even value 2,4,6,8...
  * @return                      The time domain output filteres signal.
  */
//@{
  static void bandPassFilter(double* inputSamples, const int length, const int sampleRate, const DIGITAL_FILTER_TYPE type, const double cutOffFrequencyLow, const double cutOffFrequencyHigh, const int filterOrder);

  static void bandStopFilter(double* inputSamples, const int length, const int sampleRate, const DIGITAL_FILTER_TYPE type, const double cutOffFrequencyLow, const double cutOffFrequencyHigh, const int filterOrder);
//@}
 
//------------------------------------------------------------------------------------------------

private:
/**
 * This pair of IIR filter coeffciients defines the IIR filter structures for the Critical-Damping,
 * the Bessel, the Butterworth and the Tschebyscheff filters. This matrices are the heart of the IIR
 * digital filter.
 */
//@{
  static const double biquadLowPassFilters[5][4][4];
  static const double biquadHighPassFilters[5][4][4];
//}@
 
//------------------------------------------------------------------------------------------------

}; // class DigitalFilter
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_DIGITAL_FILTER
