/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    damping_constant.h
 * @class   DampingConstant
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Try to propose the Damping Constant of an given input inpulse signal
 * @see     http://de.wikipedia.org/wiki/Dämpfungskonstante
 * @see     http://en.wikipedia.org/wiki/Regression_analysis
 * @todo    finished and tested so far
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "damping_constant.h"
#include "../Envelope/envelope.h"
#include "../Math Utilities/math_utilities.h"

// C Langunage Library headers
#include <cmath>
#include <cfloat>

// C++ language Library headers
#include <iostream>

//------------------------------------------------------------------------------------------------

/// the number of signal avg regression points
#define REGRESSION_POINTS 100
/// the epsilon value for the min signal amplitude for the y value in the point cloud
#define EPSILON 0.0001

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 
void DampingConstant::proposeDampingConstant(const double* signal, const int length, const int sampleRate,  double& dampingConstant, double& amplitude, double& peakPosition)
{
  // extract the envelope of the given signal with the fir version of the envelope extractor
  double* envelope = clauer::math::Envelope::calculateEnvelope(signal, length, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
  
  // now find the peak value and position
  double peakValue = -DBL_MAX;
  int    peakPos   = 0;
  for (int i=0; i<length; i++)
  {
    if(envelope[i] > peakValue)
    {
      peakValue = envelope[i];
      peakPos   = i;
    }
  }
  // give the peak point back
  peakPosition = static_cast<double>(peakPos) / static_cast<double>(sampleRate);
  
  // the point cloud
  double* pointCloudX = new double[REGRESSION_POINTS];
  double* pointCloudY = new double[REGRESSION_POINTS];
  int lengthAfterPeak = length - peakPos;
  
  // make sure that there are enough points available
  if (lengthAfterPeak < REGRESSION_POINTS)
  { 
    dampingConstant = -1.0;
    amplitude       = -1.0;
    peakPosition    = -1.0;
    return;
  }
  
  // collect hte peak points
  for (int i=0; i<REGRESSION_POINTS; i++)
  {
    // collect the array interval boundaries
    int startIndex =          peakPos + (i+0)*lengthAfterPeak/REGRESSION_POINTS;
    int endIndex   = std::min(peakPos + (i+1)*lengthAfterPeak/REGRESSION_POINTS, length);
    // look for the peak in the interval
    pointCloudY[i] = -DBL_MAX;
    for (int j=startIndex; j<endIndex; j++)
      if (pointCloudY[i] < envelope[j]) 
      {
        pointCloudX[i] = (j - peakPos) / static_cast<double>(sampleRate);
        // prevent to small y values
        if (envelope[j] < EPSILON*peakValue)
          pointCloudY[i] = EPSILON*peakValue;
        else
          pointCloudY[i] = envelope[j];
      }
      //std::cout << pointCloudX[i] << " --> " << pointCloudY[i] <<  " " << sampleRate << std::endl;
  }
  // normalize the damping konstant to the physical unit 1/s
  dampingConstant *= sampleRate;
  
  // call the exponetial regression
  exponentialRegression(pointCloudX, pointCloudY, REGRESSION_POINTS, amplitude, dampingConstant);
  
  // revove the influence of the sample rate and scale to the physical unit 1/s
  
  // waste the grabage
  delete[] pointCloudX;
  delete[] pointCloudY;
}

//------------------------------------------------------------------------------------------------
 
void DampingConstant::exponentialRegression(const double* pointsX, double* pointsY, const int length, double& amplitude, double& dampingConstant)
{
  // intitalize some values
  double a = 0.0; // the slope factor for the y=a+bx linear part
  double b = 0.0; // the intersection for the y=a+bx lienar part
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  //// first step is to transformate the point cloud to the logarithmic domain with the base e
  for (int i=0; i<length; i++)
    pointsY[i] = std::log(pointsY[i]+DBL_MIN);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  //// next step is solve the linear regression with the transformated points, start whith the slope calcualtion

  // first collect the arithmetical medean values
  double meanX = 0.0;
  double meanY = 0.0;
  for (int i=0; i<length; i++)
  {
    meanX += pointsX[i];
    meanY += pointsY[i];
  }
  meanX /= static_cast<double>(length);
  meanY /= static_cast<double>(length);
  
  // calculate the slope and intersection of the linear regressiom
  double numerator = 0.0;
  double denominator = 0.0;
  for (int i=0; i<length; i++)
  {
    numerator   += (pointsX[i]-meanX)*(pointsY[i]-meanY);
    denominator += (pointsX[i]-meanX)*(pointsX[i]-meanX);
  }
  numerator   /= static_cast<double>(length);
  denominator /= static_cast<double>(length);
  
  // calculate the slope and the intersection
  b = numerator / denominator;
  a = meanY - b*meanX;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  // transformate the linear result back to the eponential coefficients
  // NOTE: y = amplitude *1 exp ( - dampingConstant * x )
  amplitude       = std::exp(a);
  dampingConstant = -b;
}

//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
