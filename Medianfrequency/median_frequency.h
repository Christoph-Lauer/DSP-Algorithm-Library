/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    median_frequency.h
 * @class   MedianFrequency
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This function calculates for the given spectrum the emdian frequency
 * @see     http://en.wikipedia.org/wiki/Median
 * @see     http://en.wikipedia.org/wiki/Frequency_spectrum
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_MEDIAN_FREQUENCY
#define CLAUER_MATH_MEDIAN_FREQUENCY
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class implements two static member functions for the for the calculation of the median frequency
 * for wither a time domain or a frequency domain input signal. The median frequency is the frequency 
 * where the integral on the left side is equal to the integral of the Right side.
 */
class MedianFrequency
{

public:
 
//------------------------------------------------------------------------------------------------

 /**
  * This function calculates the median frequency of a spectrum. The median frequency is the frequency
  * in the spectrum where the integral on the right side is equal to the integral on the left side. This 
  * function can be used for example to make a decision for the hight of of a signal or try to differ 
  * between a high and a low frequency distributed signal.
  *
  * @param    frequencyDomainSignal   The frequency domain input spectrum signal.
  * @param    length                  The length of the spectrum input signal.
  * @param    sampleRate              The sample rate or the double max-frequency of the input signal.
  * @return                           The median frequency will be returned.
  */
  static double calcualteMedianFrequencyFromFrequencyDomainSignal(const double* frequencyDomainSignal, const int length, const int sampleRate);
 
//------------------------------------------------------------------------------------------------
  
 /**
  * This fucntion calcualtes the median frequency for a given time domain signal. The time domain signal 
  * will be transfromed into the frequency domain and the above function will be called.
  *
  * @param    timeDomainSignal        The time domain input spectrum signal.
  * @param    length                  The length of the spectrum input signal.
  * @param    sampleRate              The sample rate or the double max-frequency of the input signal.
  * @return                           The median frequency will be returned.
  */
  static double calcualteMedianFrequencyFromTimeDomainSignal(double* timeDomainSignal, int length, int sampleRate);

//------------------------------------------------------------------------------------------------

}; // class MedianFrequency
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_MEDIAN_FREQUENCY
