/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    correlation.hpp
 * @class   Correlation
 * @version 0.5
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Definition of the discerte autocorrelation function
 * @see     http://en.wikipedia.org/wiki/Autocorrelation
 * @see     http://en.wikipedia.org/wiki/Correlation
 * @see     http://en.wikipedia.org/wiki/Cross-correlation
 * @see     http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
 * @todo    finished so far for the simplest implementation. The algorithmus can be for sure
 *          implemented faster with alternative functions like powerspectrum with the FFT.
 *
 * This class file defines an discrete corss-correlation and autocorrelation functions. Like most of
 * the math classes this functions are is defined as staic class functions so no instantiation is 
 * needed. This ver short algorithmus is defined directely into the header file of the class definition.
 * as template implementations. As defined in the RTE coding styles this class template header file 
 * has the suffic HPP instead of H for class files.
 * NOTE: There are much more faster implementation vaialabe e.G. the FFT Autocorrelation.
 */

//--------------------------------------------------------------------------------------------------
 
#ifndef CLAUER_MATH_CORRELATION
#define CLAUER_MATH_CORRELATION


#include <iostream>


//--------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

//--------------------------------------------------------------------------------------------------

template<class T> class Correlation
{

public:

//--------------------------------------------------------------------------------------------------
 /**
  * Cross correlation is a standard method of estimating the degree to which two series are correlated.
  * This cross correlation implementation implements the standart way the cross correlation is done.
  * This function is also the base for the autocorrelation function which alls the cross correlation
  * with the same input signal. The length of the generated output signal is (lenF + lenG).
  * Note that the generated vetor is allocated insode this function so no pre allocation is required.
  * The generated output vector has usually the time zero point after the legnth of the first input
  * vector, e.g. length of vector 1= 32 and vector 2 = 16. Then the time middle point is located at 
  * index 31 (form 0...47). Note that this function is implemted as generic template so the function
  * can be called with any gerneric type.
  * 
  *                    // ∞
  *                    ||
  * cc(t) = (f*g)(τ) = || f(t)g(t+τ) dt
  *                    ||
  *                   // -∞
  *
  * @param  f                 The frist input signal in the time domain.
  * @param  lengthF           The length of the first signal.
  * @param  g                 The second input signal in the time domain.
  * @param  lengthG           The length of the second signal.
  * @return                   This function returns the standard cross correlation vector calculated
  *                           inside this function. Note that the generated vector in allocted inside
  *                           this function so no further pre allocation is 
  */
  static T* crossCorrelation(const T* f, const int lenF, const T* g, const int lenG)
  {
    // allocate and initialize the result array
    T* cc = new T[lenF+lenG];             // allocate the result array
    for(int i=0; i<lenF+lenG; i++)        // initalize the array to 0
        cc[i]=0.0;                        // assign 0
    // (pre/post) zero padding F to prevent indexing problems he the summarization loop  
    T* f_zp = new T[lenF+2*lenG];         // allocate the zero padded array of F
    for(int i=0; i<lenF+2*lenG; i++)      // initalize the array to 0
        f_zp[i]=0.0;                      // assign 0
    for(int i=0; i<lenF; i++)             // copy F into the zero padded F
        f_zp[i+lenG]=f[i];                // assign f
    // calculate the cross correlation
    for (int t=0; t<lenF+lenG; t++)       // the indexing loop
      for (int τ=0; τ<lenG; τ++)          // the summarization (integration) loop
        cc[t] += f_zp[t+τ] * g[τ];        // sumarize the cross correlation    
    delete[] f_zp;                        // waste the grabbage
    return cc;                            // return the cross correlation
  }  
  
//--------------------------------------------------------------------------------------------------

 /**
  * This function implements the autocorrelation while using the cross correlation. a further description
  * of the correlation function can be found in the description above. Note that the time domain middle 
  * point is located at index length-1. e.g. If a vector form a length of 16 is given the time domain
  * middle point is located at the point 15. The resulting vector will be allocted into the function 
  * has a length of 2+length-1.
  *
  * @param  x                 The input signal in the time domain.
  * @param  length            The length of input signal.
  * @param  correlationLength The length of the correlation, also called the order of the correlation.
  *                           Note that the length of the correlation must be shorter than the signal length.
  * @return                   This function returns the standart auto correlation vector calculated
  *                           inside this function. Note that the generated vector in allocted inside
  *                           this function so no further pre allocation is 
  */
  static T* autoCorrelation(const T*x, const int length)
  {
    T* Rxx = crossCorrelation(x, length, x, length);
    return Rxx;
  }

//--------------------------------------------------------------------------------------------------

 /**
  * This fucntion calculates the person correlation for the two given input signals with the given 
  * length. The person correlation has a good numerically stability. The pearson correlation is also
  * known as the Pearson product-moment correlation coefficient. The correlation between two variables
  * reflects the degree to which the variables are related. The most common measure of correlation is
  * the Pearson Product Moment Correlation (called Pearson's correlation for short). When measured in
  * a population the Pearson Product Moment correlation is designated by the Greek letter rho. When
  * computed in a sample, it is designated by the letter "r" and is sometimes called "Pearson's r." 
  * Pearson's correlation reflects the degree of linear relationship between two variables. It ranges
  * from +1 to -1. A correlation of +1 means that there is a perfect positive linear relationship 
  * between variables.
  * 
  * @param  x                 The frist input signal in the time domain.
  * @param  y                 The second input signal in the time domain.
  * @param  length            The length of both signals.
  * @return                   This fucntion returns the simple person correlation (rho).
  */
  static T pearsonCorrelation(const T* x, const T* y, const int length)
  {
    // inner summation products
    T sum_sq_x = 0.0;
    T sum_sq_y = 0.0;
    // the inner sum
    T sum_coproduct = 0.0;
    // the mean variables of the both values
    T mean_x = x[0];
    T mean_y = y[0];
    // the correlation loop
    for (int i=1; i<length; i++)
    {
      // calculate the inner sum products
      T sweep = ((double)i - 1.0) / (double)i;
      T delta_x = x[i] - mean_x;
      T delta_y = y[i] - mean_y;
      // calc the inner sumation products
      sum_sq_x += delta_x * delta_x * sweep;
      sum_sq_y += delta_y * delta_y * sweep;
      // calc the inner sum
      sum_coproduct += delta_x * delta_y * sweep;
      // update the mean
      mean_x += delta_x / (double)i;
      mean_y += delta_y / (double)i;
    }
    // the standard derivation
    T pop_sd_x = std::sqrt( sum_sq_x / length );
    T pop_sd_y = std::sqrt( sum_sq_y / length );
    // calc the covariance and the variance
    T cov_x_y = sum_coproduct / length;
    T correlation = cov_x_y / (pop_sd_x * pop_sd_y);
    
    // give the result back
    return correlation;
  }

//--------------------------------------------------------------------------------------------------

}; // CORRELATION

//--------------------------------------------------------------------------------------------------

} // namespace math
} // namespace clauer

#endif // CLAUER_MATH_AUTOCORRELATION
