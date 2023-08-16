/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    short_time_fourier_transformation.cpp
 * @class   ShortTimeFourierTransformation
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   A Spectrogram Implementation
 * @see     http://en.wikipedia.org/wiki/Spectrogram
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_SHORT_TIME_FOURIER_TRANSFORMATION
#define CLAUER_MATH_SHORT_TIME_FOURIER_TRANSFORMATION
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 

/**
 * This class implemets only one static function for the extraction of the so called spectrogram 
 * aka the sonogram or power spectrum of the short time fourier transformation. The function allocates
 * a two dimensional array for itself and try to work as autonomous as possible. The resulting array
  * can be accessed with spectrogram[time][frequency].
 */
class ShortTimeFourierTransformation
{

//------------------------------------------------------------------------------------------------

public:

//------------------------------------------------------------------------------------------------

// this enum declares the possible window functions for the short-time-FFT
  enum windowFunction
  {
    WINDOW_HAMMING,
    WINDOW_HANN,
    WINDOW_BLACKMAN,
    WINDOW_TRIANGLE,
    WINDOW_WELCH,
    WINDOW_GAUSS,
    WINDOW_COSINE,
    WINDOW_ASYMEXPO
  };

//------------------------------------------------------------------------------------------------

 /**
  * The spectrum will be generated with the fast fourier transformation. If no parameter for the fft
  * Length is given, the function try to find the best frequency resolution corresponding a screen 
  * size of 16/9. If no value for the shift is given the default value of 1/6 of the fftLength is 
  * taken. The reference values for the generated timePoints and the freqPoints will be set into this
  * function. The freqPoints value has half the length of the fftLength. 
  * NOTE: This function can be used to work fully autonomous with only the three first parameters 
  * (data, length and sampleRate) the generated freqPoints and timePoints values will be set from this
  * function. This function allocates the spectrum matrix internally for itself so not pre allocation
  * is necessary. 
  *
  * @param  data        This value represents the time domain input signal.
  * @param  length      The legnt of the input signal time domain vector.
  * @param  sampleRate  The sample rate of the time donain input signal.
  * @param  freqPoints  This return variable will be set to the number of generated frequency points
  *                     from this function. 
  * @param  timePoints  This return variable will be set to the number of generated time points from
  *                     this function.  
  * @param  fftLength   If the default value of 0 is given here this function ty to find the best 
  *                     fftLength autonomously for the best resolution from a image size of 16/9. A
  *                     given value must be have a power of two and influences the number of generated
  *                     time and frequency points.
  * @param  shift       The window shift value influences the overlaping and has a default vale of 1/6.
  * @param  windowFunct The window function used to smooth the single time domain fft input windows.
  * @return             This function returns a the allocated one dimensional specturm matrix with has
  *                     the dimension spectrogram[timePoints][freqPoints]. Note that NO pre allocation is 
  *                     necessary because the number of time domain points is calculated into this function.
  */
  static double** generateShortTimeFourierTransformation(
                                        const double* data,
                                        const int length,
                                        const int sampleRate,
                                        int& freqPoints,              // will be set while the calculation
                                        int& timePoints,              // will be set while the calculation
                                        int fftLength = 0,
                                        const double shift = 0.1666666666666666,
                                        const windowFunction = WINDOW_HAMMING
                                      );
 
//------------------------------------------------------------------------------------------------

}; // class ShortTimeFourierTransformation
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_SHORT_TIME_FOURIER_TRANSFORMATION
