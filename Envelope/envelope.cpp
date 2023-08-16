/**
 * (c)      Christoph Lauer Engineeringclauer
 *
 * @file    envelope.h
 * @class   Envelope
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This function implements a basic envelope calculation which uses the hilbert transformation.
 * @see     http://www.numerix-dsp.com/envelope.html
 * @todo    finished and tested so far.
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "envelope.h"
#include "../Math Utilities/math_utilities.h"
#include "../Fourier Transformation/fourier_transformation.h"

// C Language Library headers
#include <cmath>
#include <cfloat>
#include <cstring>

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------

double* Envelope::calculateEnvelope(const double* inputSignal, const int length, const ENVELOPE_CALCULATION_METHOD method, double firLowPassAlpha)
{
  // first we need to change the length of the input vector to a power of two
  int zeroPaddedLength = 0;
  double* zeroPaddedTimeDomain = clauer::math::Utilities::autoZeroPadding(inputSignal, length, &zeroPaddedLength);
  
  // calculate the hilbert transformation of the zero padded input signal
  double* hilbertTransformation = fastHilbertTransform(zeroPaddedTimeDomain, zeroPaddedLength);
  
  // now switch between the different calculation methods
  double* envelope;

	// go here if the ABS method was selected
	if (method == ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE)
  {
    envelope = new double[zeroPaddedLength];
    for (int i=0; i<zeroPaddedLength; i++)
      envelope[i] = std::sqrt(zeroPaddedTimeDomain[i]*zeroPaddedTimeDomain[i] + hilbertTransformation[i]*hilbertTransformation[i]);
  }
  // go here if the FIR LOW PASS method was selected
  else
  {
    // comutate both signals and take the max of them
    double* maxEnvelope = new double[zeroPaddedLength];
    for (int i=0; i<zeroPaddedLength; i++)
      maxEnvelope[i] = std::max(std::abs(zeroPaddedTimeDomain[i]), std::abs(hilbertTransformation[i]));

    // try to dynamically determine the fir filter coefficient
    if (firLowPassAlpha == -1.0)
    {
      firLowPassAlpha = 1.0 - 1.0 / (static_cast<double>(length/100));
      if (firLowPassAlpha > 1.0) firLowPassAlpha = 1.0;
      if (firLowPassAlpha < 0.0) firLowPassAlpha = 0.0;
    }

    std::cout << "LENGTH="<< length << " FIR-LOWPASS-ALAPHA=" << firLowPassAlpha << std::endl;
    
    // extract the maximum of the envelope before the filter was applyed
    double maxBeforeFir = -DBL_MAX;
    for (int i=0; i<zeroPaddedLength; i++)
      if (maxBeforeFir < maxEnvelope[i]) maxBeforeFir = maxEnvelope[i];

    // call the FIR filter
    envelope = applyFirLowPass(maxEnvelope, zeroPaddedLength, firLowPassAlpha);

    // extract the maximum of the envelope before the filter was applyed
    double maxAfterFir = -DBL_MAX;
    for (int i=0; i<zeroPaddedLength; i++)
      if (maxAfterFir < envelope[i]) maxAfterFir = envelope[i];

    // normalize the amplitude back to the previous peak value
    for (int i=0; i<zeroPaddedLength; i++)
      envelope[i] *= maxBeforeFir / maxAfterFir;
      
    // waste the temporary array
    delete[] maxEnvelope;
  }

  // cut the result back to the origin length
  double* envelopeCut = new double[length];
  memcpy(envelopeCut, envelope, length * sizeof(double));
  
  // waste the grabage
  delete[] zeroPaddedTimeDomain;
  delete[] hilbertTransformation;
  delete[] envelope; 
  
  // give the result back
  return envelopeCut;
}

//------------------------------------------------------------------------------------------------

double* Envelope::applyFirLowPass(const double* input, const int length, const double alpha)
{
  // first allocate the output vector
  double* filtered = new double[length];
  filtered[0] = input[0];
  
  // now simple apply the FIR low pass filter
  for (int n=1; n<length; n++)
    filtered[n] = ( input[n] + alpha * filtered[n-1] );

  // finally return the calcualtion result
  return filtered;
}

//------------------------------------------------------------------------------------------------
 
double* Envelope::fastHilbertTransform(const double* timeDomain, const int length)
{
  // forst prepare some values for the transformation
  double* reTd  = new double[length];
  double* imTd  = new double[length];
  double* reFd  = new double[length];
  double* imFd  = new double[length];
  memcpy(reTd, timeDomain, length * sizeof(double)); 
  memset(imTd, 0, length * sizeof(double));

  // FASTEN YOUR SEAT BELLS, we now transform the signal into the frequency domain
  clauer::math::FourierTransformation::AutoFT(length, false, reTd, imTd, reFd, imFd);
  
  // now apply the hilbert transformation into the frequency domain cooresponding the 
  // formular "h(x(f)) = x(f) * (-j) * sign(f)" in the complex frequency domain.
  // note that in our special case, we zero out the DC and Nyquist components.
  for (int i=0; i<length; i++)
  {
    // first calculate the signum of the frequency for the hilbert transformation
    double sign = 0.0;
    if (i < length/2)  sign =  1.0; // for positive frequencies multiply the positive harmonics by j
    else               sign = -1.0; // for negative freuqencyies multiply the negative harmonics by -j
    if (i == 0)        sign =  0.0; // zero out the zeroth component
    if (i == length/2) sign =  0.0; // zero out the Nyquist component
    
    // now apply the "sign(f) * (-j)" formular for the hibert transformation in the complex freuqncy domain
    double newRe =  sign * imFd[i];
    double newIm = -sign * reFd[i];
    
    // copy the result values
    reFd[i] = newRe;
    imFd[i] = newIm;
  }
  
  // BACK TO EARTH, we transform the value back to the time domain
  clauer::math::FourierTransformation::AutoFT(length, true, reFd, imFd, reTd, imTd);
  
  // waste the grabbage
  delete[] imTd;
  delete[] reFd;
  delete[] imFd;
  
  // get the result back
  return reTd;
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
