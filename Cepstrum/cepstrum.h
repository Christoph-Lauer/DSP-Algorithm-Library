/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    cepstrum.h
 * @class   Cepstrum
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This class implements cepstral analyse
 * @see     http://en.wikipedia.org/wiki/Cepstrum
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_CEPSTRUM
#define CLAUER_MATH_CEPSTRUM
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------

/**
 * Like most of the mathematical classes this class also has only static members so no explizit
 * instanciation is necessary. The cepstrum class implement like the name says cepstral analysis methods.
 */
class Cepstrum
{

public:
 
//------------------------------------------------------------------------------------------------

 /**
  * This function calcualtes the cepstrum for the given time domain signal. The real cepstrum will be
  * calculated with the folowing formular: Cepstrum = ABS ( IFFT ( LOG ( ABS ( FFT ( x) ) ) ). Note 
  * that the resulting cepstrum will be calculated via an zeropadded fast fourier transformation which
  * has in the back transformation a symetie. Therefore the resulting cepstrum will be have at the 
  * end an zero zone.
  *
  * @param    samples                 The time domain input spectrum signal.
  * @param    length                  The length of the spectrum input signal.
  * @return                           The resulting cepstrum vectou will be pre allocated into this 
  *                                   function and has the same langth than the input signal.
  */
  static double* calculateRealCepstrum(double* samples, int length);
 
//------------------------------------------------------------------------------------------------

}; // class Cepstrum
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_CEPSTRUM
