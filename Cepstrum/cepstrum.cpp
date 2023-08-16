/**
 * (c)      
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
 
// Local headers
#include "cepstrum.h"
#include "../Fourier Transformation/fourier_transformation.h"
#include "../Math Utilities/math_utilities.h"

// C language Library headers
#include <cmath>
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 
double* Cepstrum::calculateRealCepstrum(double* samples, int length)
{
  // build the complex time domain input specreum
  int zeroPaddedLength = 0;
  double* realTd = clauer::math::Utilities::autoZeroPadding(samples, length, &zeroPaddedLength);
  double* imagTd = new double[zeroPaddedLength];
  memset(imagTd, 0, sizeof(double)*zeroPaddedLength);
  // tarnsform the time domain signal into the freqeuncy domain
  double* realFd = new double[zeroPaddedLength];
  double* imagFd = new double[zeroPaddedLength];
  memset(realFd, 0, sizeof(double)*zeroPaddedLength);
  memset(imagFd, 0, sizeof(double)*zeroPaddedLength);
  clauer::math::FourierTransformation::FFT(zeroPaddedLength, false, realTd, imagTd, realFd, imagFd);
  // build the abs and logarithmize the spectrum
  for (int i=0; i<zeroPaddedLength/2; i++)
    realFd[i] = std::log(std::sqrt(realFd[i]*realFd[i]+imagFd[i]*imagFd[i]));
  memset(imagFd, 0, sizeof(double)*zeroPaddedLength);
  // transform the signal back to the frequency domain
  clauer::math::FourierTransformation::FFT(zeroPaddedLength, true, realFd, imagFd, realTd, imagTd);
  // extract the cepstrum
  double* cepstrum = new double[length];
  for (int i=0; i<length; i++)
  {
    if (i < zeroPaddedLength/2)
      cepstrum[i] = std::sqrt(realTd[i]*realTd[i]+imagTd[i]*imagTd[i]);
    else 
      cepstrum[i] = 0.0;
  }
  // waste the grabage
  delete[] realFd;
  delete[] imagFd;
  delete[] realTd;
  delete[] imagTd;
  // give the result back
  return cepstrum;
}

 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
