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
 
// Local headers
#include "short_time_fourier_transformation.h"
#include "../Math Utilities/math_utilities.h"
#include "../Fourier Transformation/fourier_transformation.h"

// C Langunage Library headers
#include <cmath>
#include <cstdio>
#include <cassert>
#include <cstring>

// C++ Language Library headers
#include <iostream>

//------------------------------------------------------------------------------------------------

using namespace std;
using namespace clauer::math;

//------------------------------------------------------------------------------------------------

#define DEBUG true

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 
double** ShortTimeFourierTransformation::generateShortTimeFourierTransformation(
                                            const double* data,
                                            const int length,
                                            const int sampleRate,
                                            int& timePoints,
                                            int& freqPoints,
                                            int fftLength,
                                            const double shift,
                                            const windowFunction timeWindow
                                          )
{
  // first check if there was any value given for the window sizes
  if (fftLength == 0)
  {
    // tyr to find the window sizes corresponding the asuptation that the ws should have the 
    // approximately the double size as the fteqPoints length. The 16/9 factor is for the adaption
    // of the screen and the image output.
    fftLength = clauer::math::Utilities::getNextPowerOfTwo(static_cast<int>(std::sqrt(length/shift)));
  }
  // check if the given value has a power of two
  else
    if (clauer::math::Utilities::isPowerOfTwo(fftLength) == false)
    {
      if (DEBUG == true) cout << "The given fftLength has not a power of two. Change the fftLength from " << fftLength;
      fftLength = clauer::math::Utilities::getNextPowerOfTwo(fftLength);  
      if (DEBUG == true) cout << " to a length of " << fftLength << std::endl;
    }
  // first calcualte the dimensions of the spectrogram matrix
  freqPoints = fftLength/2;
  int winShift = static_cast<int>(static_cast<double>(fftLength) * shift);
  timePoints = static_cast<int>(ceil(static_cast<double>(length) / static_cast<double>(winShift)));
  if (DEBUG == true)
    cout << "FFTLength=" << fftLength << " Shift=" << shift << " WinShift=" << winShift << " TimePoints=" << timePoints << " Frequencypoints=" << freqPoints << " Memory=" << timePoints*freqPoints*8.0/1024.0/1024.0 << "MB" << endl;
  
  // allocation the two dimensional array
  double **spectrogram = new double*[timePoints];
  for( int i=0; i<timePoints; i++)
    spectrogram[i] = new double[freqPoints];
  
  // the temporary time window
  double* tmpTimeDomain = new double[fftLength];
  
  // loop over the time windows
  for (int i=0, j=0; i<timePoints; i++, j+=winShift)
  {
    // copy the time domain window
    for (int k=0; k<fftLength; k++)
    {
      int index = j+k;
      if (index < length)
        tmpTimeDomain[k] = data[index];
      else
        tmpTimeDomain[k] = 0.0;
    } 
    // apply the selected window function
    switch (timeWindow)
    {
      case WINDOW_HAMMING:  Utilities::applyHammingWindow(tmpTimeDomain, fftLength);
      case WINDOW_HANN:     Utilities::applyHannWindow(tmpTimeDomain, fftLength);
      case WINDOW_BLACKMAN: Utilities::applyBlackmanWindow(tmpTimeDomain, fftLength);
      case WINDOW_TRIANGLE: Utilities::applyTriangleWindow(tmpTimeDomain, fftLength);
      case WINDOW_WELCH:    Utilities::applyWelchWindow(tmpTimeDomain, fftLength);
      case WINDOW_GAUSS:    Utilities::applyGaussWindow(tmpTimeDomain, fftLength);
      case WINDOW_COSINE:   Utilities::applyCosineWindow(tmpTimeDomain, fftLength);
      case WINDOW_ASYMEXPO: Utilities::applyAsymetricalExponentialWindow(tmpTimeDomain, fftLength);
    }
    // calculate the FFT
    FourierTransformation::PowerSpectrum(fftLength, tmpTimeDomain, spectrogram[i]);
  }
  
  // waste the grabage
  delete[] tmpTimeDomain;
  
  // return the spectrogram
  return spectrogram;
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace template

} // namepsace clauer
