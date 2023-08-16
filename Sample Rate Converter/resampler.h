/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    resampler.h
 * @class   Resampler
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Definition of public resampling interface
 * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
 * @todo    finished so far.
 *
 * @note    !!! PLEASE NOTE THAT THE THREE SINC-TYPE SAMPLE RATE CONVERTERS DOES NOTE WORK !!! 
 *          !!! WITH A RESAMPLING FACTOR FROM ABOUT 0.1 AND DEEPER !!! THIS EQUIVALENT TO  !!!
 *          !!! DOWNSAMPLING RATE FROM ABOUT 10 AND ABOVE. THIS IS NOT A BUG BUT MORE AN   !!!
 *          !!! RATHER UNCONVENTIONAL DOWNSAMPLING RATE WHICH IS NOT SUPPORTED BY THE SINC !!!
 *          !!! CONVERTERS.                                                                !!!
 *
 * @note    Here simple Benchmark result which unfold the performance of the different converters.
 *          Point of reference is the SINC_BEST converter which will be threated as factor one.
 *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000 
 *          LINEAR              :           0.0066                      0.0061
 *          ZERO_ORDER_HOLD     :           0.0064                      0.0064
 *          SINC_FASTEST        :           0.12                        0.12
 *          SINC_MEDIUM_QUALITY :           0.28                        0.29
 *          SINC_BEST_QUALITY   :           1.0                         1.0
 *
 * This class is a simple c++ wrapper to the sample rate converter c function collection
 * defined into the sample_rate_converter files. The functions offer a sample rate conversion with
 * three different methods. The SRC_LINEAR and the SRC_ZERO_ORDER_HOLD methods are really fast but
 * so bad in the results that the usage of the SRC_SINC_FASTEST should be the best solution in the 
 * most cases...
 *
 */

 //------------------------------------------------------------------------------------------------

 #ifndef CLAUER_MATH_RESAMPLER
 #define CLAUER_MATH_RESAMPLER
 
 //------------------------------------------------------------------------------------------------
 
 // Local headers
 #include "sample_rate_converter_common.h"
 
 //------------------------------------------------------------------------------------------------
 
 namespace clauer
 {
 namespace math
 {
 
 //------------------------------------------------------------------------------------------------
 
/**
 * This class implemets a basic wrapper class to the sample rate converter function collection defined 
 * in the sample_rate_converter files. This wrapper is not like the most other functions in the clauer::math
 * namespace implemented in a static contex so an instance of the class must be instanciated before
 * one of the two converter functions can be used.
 */
class Resampler
{
  public:

 /**
  * The following enums can be used to set the interpolator type.
  *
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
  * @note    Here simple Benchmark result which unfold the performance of the different converters.
  *          Point of reference is the SINC_BEST converter which will be threated as factor one.
  *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000
  *          LINEAR              :           0.0066                      0.0061
  *          ZERO_ORDER_HOLD     :           0.0064                      0.0064
  *          SINC_FASTEST        :           0.12                        0.12
  *          SINC_MEDIUM_QUALITY :           0.28                        0.29
  *          SINC_BEST_QUALITY   :           1.0                         1.0
  *
  */
  enum converter{SRC_SINC_BEST_QUALITY, SRC_SINC_MEDIUM_QUALITY, SRC_SINC_FASTEST, SRC_ZERO_ORDER_HOLD, SRC_LINEAR};
 
//------------------------------------------------------------------------------------------------
    
   /**
    * This is basic convertion wreapper function for the float data type. This function is not in place and 
    * the memory vector for the conversion must be allocated outside this function.
    * 
    * @note     !!! PLEASE NOTE THAT THE THREE SINC-TYPE SAMPLE RATE CONVERTERS DOES NOTE WORK !!! 
    *           !!! WITH A RESAMPLING FACTOR FROM ABOUT 0.1 AND DEEPER !!! THIS EQUIVALENT TO  !!!
    *           !!! ABOUT AND DOWNSAMPLING FACTOR FROM 10 AND ABOVE. THIS IS NOT A BUG BUT     !!!
    *           !!! MORE AN UNCONVENTIONAL DOWNSAMPLING RATE WHICH IS NOT SUPPORTED BY THE     !!!
    *           !!! SINC CONVERTERS.                                                           !!!
    *
    * @note     Here simple Benchmark result which unfold the performance of the different converterrs.
    *           Point of reference is the SINC_BEST converter which will be threated as factor one.
    *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000
    *           LINEAR              :           0.0066                      0.0061
    *           ZERO_ORDER_HOLD     :           0.0064                      0.0064
    *           SINC_FASTEST        :           0.12                        0.12
    *           SINC_MEDIUM_QUALITY :           0.28                        0.29
    *           SINC_BEST_QUALITY   :           1.0                         1.0
    * 
    * @param    in          A ponter to the input vector.
    * @param    out         A ponter to the input vector.
    * @param    samplesIn   The count of the Input samples.
    * @param    samplesOut  The count of the Output samples. Please not that the up/down sampling factor
    *                       will be calculated from the rate og the samplesIn / samplesOut. Note that the 
    *                       output smaples array must be pre allocated outside this function !!!
    * @param    converter   Here the used converter corresponding to the enum declaration above 
    *                       is declared.
    *                       !!! The resampler uses automatically the LINEAR converter for downsampling rates !!!
    *                       !!! lesser than 14 which corresponds to an resampling factor of 0.0714 if one of !!!
    *                       !!! the SINC converters was selected.                                            !!!
    */
    void doResampling(const float* in, float* out, const long samplesIn, const long samplesOut, int converter);
 
//------------------------------------------------------------------------------------------------

   /**
    * This function has exactely the same functionality as the float function above but it does 
    * internaly a casting from the double type to float vice versa because the sample rate convertion
    * is only defined for the float type. Simple casting tests shown that casting is very fast done on
    * moder processors so using the double type should ne be a problem with this function signature.
    *
    * @note     !!! PLEASE NOTE THAT THE THREE SINC-TYPE SAMPLE RATE CONVERTERS DOES NOTE WORK !!! 
    *           !!! WITH A RESAMPLING FACTOR FROM ABOUT 0.1 AND DEEPER !!! THIS EQUIVALENT TO  !!!
    *           !!! ABOUT AND DOWNSAMPLING FACTOR FROM 10 AND ABOVE. THIS IS NOT A BUG BUT     !!!
    *           !!! MORE AN UNCONVENTIONAL DOWNSAMPLING RATE WHICH IS NOT SUPPORTED BY THE     !!!
    *           !!! SINC CONVERTERS.                                                           !!!
    *
    * @note     Here simple Benchmark result which unfold the performance of the different converters.
    *           Point of reference is the SINC_BEST converter which will be threated as factor one.
    *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000
    *           LINEAR              :           0.0066                      0.0061
    *           ZERO_ORDER_HOLD     :           0.0064                      0.0064
    *           SINC_FASTEST        :           0.12                        0.12
    *           SINC_MEDIUM_QUALITY :           0.28                        0.29
    *           SINC_BEST_QUALITY   :           1.0                         1.0
    *
    * @param    in          A ponter to the input vector.
    * @param    out         A ponter to the input vector.
    * @param    samplesIn   The count of the Input samples.
    * @param    samplesOut  The count of the Output samples. Please not that the up/down sampling factor
    *                       will be calculated from the rate og the samplesIn / samplesOut. Note that the 
    *                       output smaples array must be pre allocated outside this function !!!
    * @param    converter   Here the used converter corresponding to the enum declaration above 
    *                       is declared.
    *                       !!! The resampler uses automatically the LINEAR converter for downsampling rates !!!
    *                       !!! lesser than 14 which corresponds to an resampling factor of 0.0714 if one of !!!
    *                       !!! the SINC converters was selected.                                            !!!
    */
    void doResampling(const double* in, double* out, const long samplesIn, const long samplesOut, int converter);
}; // class Resampler
 
//------------------------------------------------------------------------------------------------
 
} // namespace  math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_RESAMPLER
