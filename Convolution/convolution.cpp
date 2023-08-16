/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    convolution.cpp
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
 
// Local headers
#include "convolution.h"
#include "../Math Utilities/math_utilities.h"
#include "../Fourier Transformation/fourier_transformation.h"

// C Langunage Library headers
#include <cmath>
#include <cstdio>
#include <cassert>

// C++ Language Library headers
#include <iostream>

// Stl headers
#include <string>
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------

double* Convolution::calculateStandardConvolution(const double* signal1, const double* signal2, const int length)
{
  // first allocate the result vector
  double* convolution = new double[length];
  memset(convolution, 0, sizeof(double)*length);
  
  // calculate the concolution in the time doamin
  for (int n=0; n<length; n++)
    for (int m=1; m<length; m++)
      if ( (n-m) > 0 )
        convolution[n] += signal1[m]*signal2[n-m];
  
  // return the result
  return convolution;
}
 
//------------------------------------------------------------------------------------------------
 
double* Convolution::calculateFastConvolution(const double* signal1, const double* signal2, const int length)
{
  // first zeropadd both signals to a length from a power of two and prepare the data for the FFT
  int newLength = 0;
  double* tdr1 = clauer::math::Utilities::autoZeroPadding(signal1, length, &newLength);
  double* tdr2 = clauer::math::Utilities::autoZeroPadding(signal2, length, &newLength);
  double* tdi1 = new double[newLength];
  double* tdi2 = new double[newLength];
  memset(tdi1, 0, sizeof(double));
  memset(tdi2, 0, sizeof(double));
  
  // perform the FFT
  double* fdr1 = new double[newLength];
  double* fdi1 = new double[newLength];
  double* fdr2 = new double[newLength];
  double* fdi2 = new double[newLength];
  memset(fdr1, 0, sizeof(double));
  memset(fdi1, 0, sizeof(double));
  memset(fdr2, 0, sizeof(double));
  memset(fdi2, 0, sizeof(double));
  clauer::math::FourierTransformation::FFT(newLength, false, tdr1, tdi1, fdr1, fdi1);
  clauer::math::FourierTransformation::FFT(newLength, false, tdr2, tdi2, fdr2, fdi2);
  
  // do the convoltion in the frequency domain
  for (int i=0; i<newLength; i++)
  {
    fdr1[i] = fdr1[i]*fdr2[i] - fdi1[i]*fdi2[i];
    fdi1[i] = fdr1[i]*fdi2[i] + fdi1[i]*fdr2[i];
  }
  
  // transform the result back from the frequency domain into the time domain
  clauer::math::FourierTransformation::FFT(newLength, true, fdr1, fdi1, tdr1, fdi1);
  
  // wast the grabage
  //delete[] tdr1;
  delete[] tdi1;
  delete[] tdr2;
  delete[] tdi2;
  delete[] fdr1;
  delete[] fdi1;
  delete[] fdr2;
  delete[] fdi2;
  
  // give the result back
  return tdr1;
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
