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
 
// Local headers
#include "median_frequency.h"
#include "../Fourier Transformation/fourier_transformation.h"
#include "../Math Utilities/math_utilities.h"

// C++ Langunage Library headers
#include <iostream>

// C Langunage Library headers
#include <cfloat>
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 
double MedianFrequency::calcualteMedianFrequencyFromFrequencyDomainSignal(const double* frequencyDomainSignal, const int length, const int sampleRate)
{
   // first calcualte the whole integral value from the normalized spectrum
  long double sum = 0.0;
  for (int i=0; i<length; i++)
    sum += std::abs(frequencyDomainSignal[i]);
    
  // look for the median frequency on the point where the half integral sum value was reached
  long double tmpSum = 0.0;
  int medianIndex = 0;
  for (int i=0; i<length; i++)
  {
    tmpSum += std::abs(frequencyDomainSignal[i]);
    if (tmpSum > sum/2.0)
    {
      medianIndex = i;
      break;
    }
  }
  
  // calculate the median frequency
  double medianFrequency = static_cast<double>(sampleRate) /2.0 / static_cast<double>(length) * static_cast<double>(medianIndex);
  
  // return the result
  return medianFrequency;
}

//------------------------------------------------------------------------------------------------

double MedianFrequency::calcualteMedianFrequencyFromTimeDomainSignal(double* timeDomainSignal, int length, int sampleRate)
{
  // first zeropadd the signal to a length from a power of two
  int zeroPaddededLength = 0;
  double* zeroPaddedTimeDomainSignal = clauer::math::Utilities::autoZeroPadding(timeDomainSignal, length, &zeroPaddededLength);
  // transform the signal into the frequency domain
  double* powerSpectrum = new double[zeroPaddededLength/2];
  //std::cout << "Length="<< length << ", SampeRate=" << sampleRate << ", ZeroPaddedLength=" << zeroPaddededLength << std::endl;
  clauer::math::FourierTransformation::PowerSpectrum(zeroPaddededLength, zeroPaddedTimeDomainSignal, powerSpectrum);  
  // call the frequency domain median function
  double medianFrequency = calcualteMedianFrequencyFromFrequencyDomainSignal(powerSpectrum, zeroPaddededLength/2, sampleRate);
  // delete the zero padded time domain signal and the spectrum
  delete [] zeroPaddedTimeDomainSignal;
  delete [] powerSpectrum;
  // return the result
  return medianFrequency; 
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
