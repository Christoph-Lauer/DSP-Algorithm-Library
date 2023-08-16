/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    hatrminic_analyse.cpp
 * @class   Harmonic Analyse
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Collects functions for the harmonic analyse
 * @see     http://en.wikipedia.org/wiki/Harmonic
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "harmonic_analyse.h"
#include "../Math Utilities/math_utilities.h"
#include "../Fourier Transformation/fourier_transformation.h"

// C Langunage Library headers
#include <cmath>
#include <cfloat>
 
//------------------------------------------------------------------------------------------------

/// THis value defines the number of harmonics and the length of the result vector
#define NUMBER_OF_HARMONICS 10

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
  
double* HarmonicAnalyse::harmonicAnalyse(double* signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, double* fundamentalFrequency, double* fundamentalFrequencyLevel, int extraction_method, double searchRadius)
{

  /////////////////////////////////////////////////////////
  // extract the spectrum and the fundamental frequency
  
  // first transform the spectrum into the frequency domain
  int zpLength;
  double* zpSignal = clauer::math::Utilities::autoZeroPadding(signal, length, &zpLength);
  double* spectrum = new double[zpLength/2];
  clauer::math::FourierTransformation::PowerSpectrum(zpLength, zpSignal, spectrum);
  // logarithmize the powerspectrum
  clauer::math::Utilities::lin2logArrayInPlace(spectrum, zpLength/2);
  // look for the peak in the given intervall
  int lowerIndex = static_cast<int>(lowerSearchFrequency * static_cast<double>(zpLength/2) / static_cast<double>(sampleRate/2));
  int upperIndex = static_cast<int>(upperSearchFrequency * static_cast<double>(zpLength/2) / static_cast<double>(sampleRate/2));
  double peakLevel = -DBL_MAX;
  int peakIndex = -1;
  for (int i=lowerIndex; i<upperIndex; i++)
    if (spectrum[i] > peakLevel)
    {
      peakLevel = spectrum[i];
      peakIndex = i;
    }
  // fundamental frequency results
  (*fundamentalFrequency)      = static_cast<double>(peakIndex) / static_cast<double>(zpLength/2) * static_cast<double>(sampleRate/2);
  (*fundamentalFrequencyLevel) = spectrum[peakIndex];
  // allocate the result vector
  double* harmonics = new double[NUMBER_OF_HARMONICS];

  
  /////////////////////////////////////////////////////////
  // EXACT_POSITION harmonic extraction method

  if (extraction_method == EXACT_POSITION)
  {
    for (int i=1; i<=NUMBER_OF_HARMONICS; i++)
    {
      if (peakIndex*i < zpLength/2)
      {
        harmonics[i-1]  = spectrum[peakIndex*i];
        harmonics[i-1] -= (*fundamentalFrequencyLevel);
      }
      else
        harmonics[i-1] = -DBL_MAX;
    }
  }
  
  
  /////////////////////////////////////////////////////////
  // SEARCH_IN_INTERVALL harmonic extraction method

  if (extraction_method == SEARCH_AROUND_RADIUS)
  {
    harmonics[0] = 0.0;
    // next the loop over the harmonics
    for (int i=1; i<=NUMBER_OF_HARMONICS; i++)
    {
      // this value defines the search border for the harmonics
      int borderIndex = i*static_cast<int>(searchRadius / 100.0 / 2.0 *static_cast<double>(zpLength/2));
      // the harmonic peak index
      int harmonicPeakIndex = peakIndex*(i+1);
      // the local peak value for the border area arround the harmonic
      double localPeak = -DBL_MAX;
      // the loop over the harmonics search border, first look for the peak in the border
      for (int index=(harmonicPeakIndex-borderIndex); index<(harmonicPeakIndex+borderIndex); index++)
      {
        // make sure the index is not out of the border of the vector length
        if (index >= zpLength/2) 
          continue;
        // look for the peak
        if (localPeak < spectrum[index])
          localPeak = spectrum[index];
      }
      // save the found harmonic
      harmonics[i]  = localPeak;
      harmonics[i] -= (*fundamentalFrequencyLevel);
    }
  }  
  
  // clean the grabbage
  delete[] zpSignal;
  delete[] spectrum;
  
  // give the result back
  return harmonics;
}

//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauerclauer
