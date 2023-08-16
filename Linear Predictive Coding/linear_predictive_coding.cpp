/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    linear_predictive_coding.cpp
 * @class   LinearPredictiveCoding
 * @version 0.5
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Calculates the linear predictive coefficients and computes the resulting values.
 * @see     http://en.wikipedia.org/wiki/Linear_predictive_coding
 * @todo    the lpc spectrum should still be calclulated. In the LPC spectrum calculation 
 *          the logarithmization uses an additional factor which beware an logarithmization 
 *          of 0.
 *
 * The object of linear prediction is to form a model of a linear time-invariant 
 * digital system trought observation of input and output sequences. The implementation
 * of the prediction coefficient extraction here serves after the Levinson Durbin implementation
 * to solve the matrix system.
 */

//-------------------------------------------------------------------------------------------------

// Local headers
#include "linear_predictive_coding.h"
#include "../Fourier Transformation/fourier_transformation.h"
#include "../Math Utilities/math_utilities.h"

// C Language Libary headers
#include <cmath>
#include <cassert>

// IOstream header
#include <iostream>

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

//-------------------------------------------------------------------------------------------------	

double* LinearPredictiveCoding::calcPredictionErrorVectorInPercent(const double* data, const int nData, const int nCoeff,
                                                  const int sampleRate, const double previous, const double future,
                                                  const double shift, int* nGenerated)
{
  // first pre-calculate and pre-allocate some values
  int nPrevious = static_cast<int>(previous * static_cast<double>(sampleRate) / 1000.0);
  int nFuture   = static_cast<int>(future   * static_cast<double>(sampleRate) / 1000.0);
  int nShift    = static_cast<int>(shift    * static_cast<double>(sampleRate) / 1000.0);
  
  // Make sure the basic boundary conditions
  assert(nData > nShift);
  assert(nData > (nPrevious + nFuture));

  // estimate the maximal number of error points to generate
  (*nGenerated) = ((nData - nPrevious - nFuture) / nShift)+1;
  // then allocate the enough elements (max one more as needed)
  double* out = new double[(*nGenerated)];
  
  // calc the prediction error curve and correct the estimated real number of points
  (*nGenerated) = 0;
  for (int i=0, j=0; i < (nData - nPrevious - nFuture); i+=nShift, j++, (*nGenerated)++)
  {
    // call the prediction error function for one point
    out[j] = calcPredictionErrorInPercent(data+i, nPrevious, nCoeff, nFuture);
	}
  
  //   finally give the result back
  return out;
}

//-------------------------------------------------------------------------------------------------	

double LinearPredictiveCoding::calcPredictionErrorInPercent(const double* data, const int nData, const int nCoeff, const int nFuture)
{
  // first calculate the future vector
  double* future = calcFutureVector(data, nData, nCoeff, nFuture);
  
  // now calculate the prediction error in percent
  double error = 0.0;
  double avg   = 0.0;
  for (int i=0; i<nFuture; i++)
  {
    error += fabs( future[i] - data[nData+i]);  // upsum the error
    avg   += fabs(data[nData+i]);               // upsum the core signal
  }
  error = error / avg * 100.0;
  
  // remove temporary memory
  delete[] future;
  
  // finally give the result back
  return error;
}

//-------------------------------------------------------------------------------------------------	

double* LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(const double* data, const int nData, const int nCoeff, const int nSpecSize, bool logarithmize, bool applyHammingWindow)
{
  // first calclualte the furture vector
  double* future = calcFutureVector(data, nData, nCoeff, nSpecSize*2);
  
  // now calculate the fast fourier transformation from the predicted future array
  double* lpcSpec = new double[nSpecSize];
  // window the time domain signal if required
  if (applyHammingWindow == true)
    clauer::math::Utilities::applyHammingWindow(future, nSpecSize*2);
  // then do the FFT transformation
  clauer::math::FourierTransformation::PowerSpectrum(nSpecSize*2, future, lpcSpec);  
  
  // logarithmize if postulated
  if (logarithmize == true)
    clauer::math::Utilities::lin2logArrayInPlace(lpcSpec, nSpecSize);
 
  // delete the grabage
  delete[] future;
  
  // finally return the lpc spectrum
  return lpcSpec;
}

//-------------------------------------------------------------------------------------------------	

double* LinearPredictiveCoding::calcFutureVector(const double* data, const int nData, const int nCoeff, const int nFuture)
{
  // some initial conditionals before beginning
  assert(nData > 0);
  assert(nCoeff > 0);
  assert(nFuture > 0);
  assert(data != NULL);
  // this an absolute must criterion !!!
  assert(nCoeff < nData);

  // first allocate the needed memory
  double* coeffs = new double[nCoeff];
  double* future = new double[nFuture];
  
  // calculate the linear predictive coefficients
  lpcSolveLevinsonDurbin(data, nData, nCoeff, coeffs);
  
  // then calculate the predicted future based of the coefficients
  predict(data, nData, coeffs, nCoeff, future, nFuture);
  
  // at the end delete the temporary needed memory
  delete[] coeffs;
  
  // and finally give the vector back
  return future;
}

//-------------------------------------------------------------------------------------------------

void LinearPredictiveCoding::predict(const double* data, const int ndata, const double* d, const int m, double* future, const int nfut)
{
  int k,j;
  double sum,discrp;
  double* reg = new double[m+1];
  
  // adapt the pointers from the real c world to the numerical recipes world
  data--;     // the input time doamin data
  d--;        // the pre calculated linear prediction coefficients
  future--;   // the predicted future vector
  
  for ( j=1; j<=m; j++)
    reg[j] = data[ ndata + 1 -j ];
  for ( j=1; j<=nfut; j++)
  {
    discrp=0.0;
   /*
    * This is where you would put in a known discrepancy if you were reconstructing a
    * function by linear predictive coding rather than extrapolating a function by linear prediction.
    */
    sum=discrp;
    for ( k=1; k<=m;k++) 
      sum += d[k]*reg[k];
    for ( k=m; k>=2; k--) 
      reg[k] = reg[k-1]; 
    // If you want to implement circular arrays, you can avoid this shifting of coffcients.
    future[j] = reg[1] = sum;
  }
  // clean the temporary arrays
  delete[] reg;
}

//-------------------------------------------------------------------------------------------------	

double LinearPredictiveCoding::lpcSolveLevinsonDurbin(const double* data, const int n, const int m, double* d)
{
  // adapt the pointers from the real c world to the numerical recipes world
  data--;   // the input time domain sample data
  d--;      // the calculated linear prediction coefficients
  
  // declare and initialize some temporary values here
  int k,j,i;
  double p=0.0, xms;
  double* wk1 = new double[n+1];
  double* wk2 = new double[n+1];
  double* wkm = new double[m+1];
  
  // initialize the temporary arrays to 0 with memset
  memset(wk1,0,sizeof(*wk1));
  memset(wk2,0,sizeof(*wk2));
  memset(wkm,0,sizeof(*wkm));
  
  for (j=1;j<=n;j++) 
    p += (data[j]*data[j]);
  xms=p/n;
  wk1[1]=data[1];
  wk2[n-1]=data[n];
  for (j=2;j<=n-1;j++)
  {
    wk1[j]=data[j];
    wk2[j-1]=data[j];
  }
  for (k=1;k<=m;k++) 
  {
    double num=0.0,denom=0.0;
    for (j=1;j<=(n-k);j++) 
    {
      num += wk1[j]*wk2[j];
      denom += (wk1[j]*wk1[j])+(wk2[j]*wk2[j]);
    }
    d[k]=2.0*num/denom;
    xms *= (1.0-(d[k]*d[k]));
    for (i=1; i<=(k-1); i++)
      d[i]=wkm[i]-d[k]*wkm[k-i];
   /**
    * The algorithm is recursive, building up the answer for larger and larger values of m
    * until the desired value is reached. At this point in the algorithm, one could return
    * the vector d and scalar xms for a set of LP coeffcients with k (rather than m) terms.
    */
    if (k == m) 
    {
      // clean the temporary arrays
      delete[] wk1;
      delete[] wk2;
      delete[] wkm;
      // and return from the recursive loop
      return xms;
    }
    for (i=1;i<=k;i++) wkm[i]=d[i];
    for (j=1;j<=(n-k-1);j++)
    {
      wk1[j] -= wkm[k]*wk2[j];
      wk2[j]=wk2[j+1]-wkm[k]*wk1[j+1];
    }
  }
 /**
  *  never get here in this function
  */
  assert(true);
  return 0.0;
}

//-------------------------------------------------------------------------------------------------

} // namespace math

} // namespace clauer
