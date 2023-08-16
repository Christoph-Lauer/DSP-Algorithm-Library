/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    normalizer.cpp
 * @class   Normalizer
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Class template file
 * @see     http://en.wikipedia.org/wiki/Audio_normalization
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "normalizer.h"

// Standard C Library headers
#include <cmath>
#include <cfloat>
#include <cassert>

// Input/Output Stream Library headers
#include <iostream>
using namespace std;

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------

void Normalizer::normalizePeak(double* samples, int length, double peakValue)
{
  // first search the peak 
  double peak = 0.0;
  for (int i=0; i<length; i++)
  {
    if (peak < fabs(samples[i]))
      peak = fabs(samples[i]);
  }
  // for debugging purposes
  cout << "Prevoius Peak: " << peak << " new Peak: " << peakValue << endl;
  // then multiplicate the vector with the new value
  multiplicateVectorByValue(samples, length, peakValue/peak);
}

//------------------------------------------------------------------------------------------------

void Normalizer::normalizeInterval(double* samples, int length, double minValue, double maxValue)
{
  // make sure that
  assert(maxValue > minValue);
  assert(samples != NULL);
  // first search the min and max values
  double min = DBL_MAX;
  double max = -DBL_MAX;
  for (int i=0; i<length; i++)
  {
    if (min > samples[i]) min = samples[i];
    if (max < samples[i]) max = samples[i];
  }
  double oldDelta = max - min;
  double newDelta = maxValue - minValue;
  // only for debugging purposes
  cout << "Prevoius [min...max] = [" << min << "..." << max << "] --> new [Min...max] = [" << minValue << "..." << maxValue << "]" << endl;
  // now normalize the peaks to the given interval
  for (int i=0; i<length; i++)
    samples[i] = (samples[i]-min) / oldDelta * newDelta + minValue;  
}

//------------------------------------------------------------------------------------------------

void Normalizer::normalizeAvg(double* samples, int length, double avgValue)
{
  // make sure that 
  assert(samples != NULL);
  // first calcluate the average value
  double sum = 0.0;
  for (int i=0; i<length; i++)
  {
    sum += samples[i];
  }
  double avg = sum / static_cast<double>(length);
  // only for debugging purposes
  cout << "Prevoius AVG: " << avg << " new AVG: " << avgValue << endl;
  // then multiplicate the vector with the new value
  multiplicateVectorByValue(samples, length, avgValue/avg);
}

//------------------------------------------------------------------------------------------------

void Normalizer::normalizeRms(double* samples, int length, double rmsValue)
{
  // make sure that 
  assert(samples != NULL);
  // first calcualte the the rms value
  double sqsum = 0.0;
  for (int i=0; i<length; i++)
  {
    sqsum += samples[i]*samples[i];
  }
  double rms = sqrt(sqsum/static_cast<double>(length));
  // only for debugging purposes
  cout << "Previous RMS: " << rms << " new RMS: " << rmsValue << endl;
  // then multiplicate the vector with the new value
  multiplicateVectorByValue(samples, length, rmsValue/rms);
}
 
//------------------------------------------------------------------------------------------------
 
void Normalizer::multiplicateVectorByValue(double* samples, int length, double factor)
{
  // make sure that 
  assert(samples != NULL);
  // then multiplicate
  for (int i=0; i<length; i++)
    samples[i] *= factor;
}

//------------------------------------------------------------------------------------------------
 
void Normalizer::addValueToVector(double* samples, int length, double summand)
{
  // make sure that 
  assert(samples != NULL);
  // then multiplicate
  for (int i=0; i<length; i++)
    samples[i] *= summand;
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer

//------------------------------------------------------------------------------------------------
