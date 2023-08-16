/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    jitter_analysis.cpp
 * @class   JitterAnalysis
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   takes a jitter analysis of the given signal
 * @see     http://en.wikipedia.org/...
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "jitter_analysis.h"
#include "../Linear Predictive Coding/linear_predictive_coding.h" // including here the whole cpp file

// C Langunage Library headers
#include <cmath>
#include <cstdio>
#include <cassert>

// Input/Output Stream Library headers
#include <iostream>

// Stl headers
#include <string>
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 
double* JitterAnalysis::jitterAnalysis(const double* samples, const int nSamples, const int lpcPrevoius, const int lpcSpecSize, const int lpcWinShift,
                                       const int lpcCoeffs, const int sampleRate, const int lowerBandBorder, const int upperBandBorder, int* nPoints,
                                       bool peakReturn, bool weightResult)
{
  // first init some numbers
  int nyquistFrequency = sampleRate / 2;
  double frequencyResolution = static_cast<double>(nyquistFrequency) / static_cast<double>(lpcSpecSize);
  *nPoints = (nSamples - lpcPrevoius) / lpcWinShift + 1;
  double* jitter = new double[*nPoints];
  assert(*nPoints != 0);
  assert(lowerBandBorder < upperBandBorder);
  
  // then the main windowing loop
  int peakPos = 0;
  int previousPeakPos = 0;
  for (int i=0, j=0; i < (nSamples-lpcPrevoius); i += lpcWinShift, j++)
  {
    // first calcluate the lpc spectrum
    double* lpcSpectrum = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(samples+i, lpcPrevoius, lpcCoeffs, lpcSpecSize);
    // extract here the border values in the vector
    int lowerBorder = static_cast<int>(static_cast<double>(lpcSpecSize) / static_cast<double>(nyquistFrequency) * static_cast<double>(lowerBandBorder));
    int upperBorder = static_cast<int>(static_cast<double>(lpcSpecSize) / static_cast<double>(nyquistFrequency) * static_cast<double>(upperBandBorder));
    double peak = 0.0;
    // now search for the peak and store the frequency
    for (int f = lowerBorder; f < upperBorder; f++)  // loop over the band
    {
      // search the peak and the peak-posintion
      if (lpcSpectrum[f] > peak) 
      {
        peak = lpcSpectrum[f];
        peakPos = f;
      }
    }
    // for the first value there is no previous value stored
    if ( i == 0)
      previousPeakPos = peakPos;
    // set the jitter value
    if (peakReturn == true)
      jitter[j] = static_cast<double>(peakPos)                   * frequencyResolution;
    else
      jitter[j] = static_cast<double>(previousPeakPos - peakPos) * frequencyResolution;
    // weight the result if required
    if (weightResult == true)
      jitter[j] *= lpcSpectrum[peakPos];
    // store the old peak position
    previousPeakPos = peakPos;
  }
  
  // finally give the whole result back
  return(jitter);
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
