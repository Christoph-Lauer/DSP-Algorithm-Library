/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    convolution.h
 * @class   Convolution
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This class collects static convolution operations.
 * @see     http://en.wikipedia.org/wiki/Convolution
 * @see     http://en.wikipedia.org/wiki/FFT
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_CONVOLUTION
#define CLAUER_MATH_CONVOLUTION
 
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class collects functions for the calculation of the convolution. One static function is for 
 * the "normal" convolution calculation corresponding the well known convolution equation in the time
 * domain. Another function calculates the so called fast convolution in the frequency domain with help
 * of the FFT. Like all other mathematic functions and classes both functions are implemented as 
 * static functions so no instanciation of the class is necessary. 
 */
class Convolution
{

public:
 
//------------------------------------------------------------------------------------------------

 /**
  * This static function calculates the standard convolution corresponding the well known convolution
  * equation. The calculation is performed into the time domain. The resulting vector is allocated inside
  * this function so no pre allocation is necessary. The return vector will have the same length than 
  * the length of both input vectors.
  * 
  * @param signal1    The first time domain input signal.
  * @param signal2    The secod time domain input signal.
  * @param legnth     The length of both signals.
  * @return           This function returns the convolution of signal1 and signal2 in the time domain.
  *                   NOTE: the result return vector will be allocated into this function so ne pre
  *                   allocation is necessary.
  */
  static double* calculateStandardConvolution(const double* signal1, const double* signal2, const int length);
   
//------------------------------------------------------------------------------------------------

 /**
  * This function performs a fast convolution in with the help of the FFT. The length of the input vector
  * can be have an arbitary length and must not have a length from a power of two. The concolution operation 
  * is done in the frequency domain. THe resulting vector will have a length from the next power of two.
  * @note  The this function allocates the signal vector for itself so no pre allocation is necessary.
  * @note  Because the circular nature of the FFT the result will be mirrored on the middle point !
  * 
  * @param signal1    The first time domain input signal.
  * @param signal2    The secod time domain input signal.
  * @param legnth     The length of both signals.
  * @return           This function returns the convolution of signal1 and signal2 in the time domain.
  *                   NOTE: the result return vector will be allocated into this function so ne pre
  *                   allocation is necessary.
  */
  static double* calculateFastConvolution(const double* sighnal1, const double* signal2, const int length);
 
//------------------------------------------------------------------------------------------------

}; // class Convolution
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_CONVOLUTION
