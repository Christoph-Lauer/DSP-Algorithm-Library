/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    linear_predictive_coding.h
 * @class   LinearPredictiveCoding
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Calculates the linear predictive coefficients and computes the resulting values.
 * @see     http://en.wikipedia.org/wiki/Linear_predictive_coding
 * @todo    the lpc spectrum should still be calclulated.
 *
 * The object of linear prediction is to form a model of a linear time-invariant 
 * digital system trought observation of input and output sequences. The implementation
 * of the prediction coefficient extraction here serves after the Levinson Durbin implementation
 * to solve the matrix system.
 *
 * Please Note:  The number of LPC Coefficients used to predict the future vector must be shorter than 
 *               the number of previous samples used to extract the LP Coefficients !!! An Assertation 
 *               in the code will check this. In release versions the usage of more LP-Coefficients
 *               as the previous samples will cause an access violation !!!
 */

//--------------------------------------------------------------------------------------------------
 
#ifndef CLAUER_MATH_LINEAR_PREDICTIVE_CODING
#define CLAUER_MATH_LINEAR_PREDICTIVE_CODING
 
//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

//-------------------------------------------------------------------------------------------------

class LinearPredictiveCoding
{

public:

//-------------------------------------------------------------------------------------------------  

 /**
  * This function is the master function for the prediction error extraction which generates multiple 
  * prediction error values and collects them into a vector. In this way an stationary/continuary test 
  * for signal can be done where the resulting cureve represents the changes in the signal in case of
  * the drivation from the predicted to the original signal with the linear prediction.
  * This function operates in the continious scale (real seconds) time domain instead ot the sample 
  * time unit domain.
  *
  * @param   data        The pointer to the input time domain based signal.
  * @param   nData       The number of elements from the data to use.
  * @param   nCoeff      The number of LP coefficents to use for the prediction.
  * @param   sampleRate  The sample rate of the input signal is needed to make the time to samples conversion
  * @param   previous    The time of samples to be used for the prediction.
  *                      Note the sale of this value is MILLISCONDS !!!
  * @param   future      The time of samples to be predict in the future and to measure the error 
  *                      between the prediction and the real world samples.
  *                      Note the sale of this value is MILLISCONDS !!!
  * @param   shift       The shift step time of the vector calculator.
  *                      Note the sale of this value is MILLISCONDS !!!
  * @param   nGenerated  A pointer to the number of error points which are generated for the output array.
  *                      This paramter is a kind of return value.
  * @return              This function returns the internally allocated arrray of the error vector.
  *                      The scale of the error curve is percent.
  */
  static double* calcPredictionErrorVectorInPercent(const double* data, const int nData, const int nCoeff,
                                                    const int sampleRate, const double previous, const double future, 
                                                    const double shift, int* nGenerated);

//-------------------------------------------------------------------------------------------------  

 /**
  * This function calculates the prediction error from the predicted feature vector for the 
  * given settings to the real vector in percent. Note that the given input vector must have 
  * at least the length of nData + nFuture !!!
  *
  * @param  data      The pointer to the input vector.
  * @param  nData     The number of elements from data to use.
  * @param  nCoeff    The number of LP coefficients to use.
  * @param  nFuture   The number of future elements to compute. 
  *                   Note that the user must make sure from the out side that the array has
  *                   a valid longer size than the nFuture + nData sizes.
  * @return           This function returns the prediction error from the predicted vector
  *                   generated corresponding the settings given to the real vector.
  */
  static double calcPredictionErrorInPercent(const double* data, const int nData, const int nCoeff, const int nFuture);

//-------------------------------------------------------------------------------------------------
  
 /**
  * This function calculates the LPC power spectrum envelope with the given prediction coefficients and the 
  * given size for the sample vector. Internally this function calculates first the an linear prediction
  * for the double size and then calculates the power spectrum for the  predicted future vectore. Please note
  * that the size of the spectrum must have a power of two !!!
  *
  * @param  data                The pointer to the input vector.
  * @param  nData               The number of elements from data to use.
  * @param  nCoeff              The number of LP coefficients to use.
  * @param  nSpecSize           Is the size of the spectrum to calculate. Please ensure that the size of the 
  *                             Spectrum must have a power of two !!!
  * @param  logarithmize        If this special flag is set the spectrum will be logarithmized.
  * @param  applyHammingWindow  If this flag is set the predicted time doamin vector will be windowed
  *                             with an Hamming window before the final fast fourier transformation.
  * @return                     This function returns the LPC power spectrum with the size of nSpecSize.
  *                             The memory for the spectrum will be allocated into this function.
  */
  static double* calcualteLpcPowerSpectrumEnvelope(const double* data, const int nData, const int nCoeff, const int nSpecSize, bool logarithmize = false, bool applyHammingWindow = false);

//-------------------------------------------------------------------------------------------------
  
 /**
  * This function calculates future sample based on the linear predictive coding method implemented
  * into this class. 
  *
  * @param  data      The pointer to the input vector.
  * @param  nData     The number of elements from data to use.
  * @param  nCoeff    The number of LP coefficients to use.
  * @param  nFuture   The number of future elements to compute.
  * @return           The is function returns a pointer to the future elements. Note that 
  *                   All needed memory for the future vector is allocated into this function,
  *                   so no pre allocation for the future vector is needed. Do not forget to 
  *                   delete this memory !!!
  * Please Note:  The number of LPC Coefficients (nCoeff) used to predict the future vector must be shorter than 
  *               the number of previous samples (nData) used to extract the LP Coefficients !!! An Assertation 
  *               in the code will check this. In release versions the usage of more LP-Coefficients
  *               as the previous samples will cause an access violation !!!  */
  static double* calcFutureVector(const double* data, const int nData, const int nCoeff, const int nFuture);

//-------------------------------------------------------------------------------------------------

private:

 /**
  * Autocorrelation LPC coeff generation algorithm invented by N. Levinson in 1947,
  * modified by J. Durbin in 1959. This routine is stohlen from the Numerical Recipes in c.
  * Note that the prediction coefficient vector 'd' must be pre allocated outside this function !
  *
  * @param  data      The pointer to the input sample vector.
  * @param  n         The length of the sample input vector.
  * @param  m         The number of LP coefficients to compute.
  * @param  d         The pointer to the pre allocated linear predictive coefficents vector.
  * @return           This function returns the mean square discrepancy of the calculation.
  */
  static double lpcSolveLevinsonDurbin(const double* data, const int n, const int m, double* d);

//-------------------------------------------------------------------------------------------------
  
 /**
  * This function predictes the future of the given data input array corresponding to
  * the given and pre calculated linear predictive coefficients. Note that the future 
  * vector must be pre allocated. The algorithms works so that the first calculated 
  * futur element (means futur[0]) is the last element of in the input array (data[ndata])
  * so that future[1] = data[ndata+1].
  * 
  * @param  data      The pointer to the input sample vector.
  * @param  ndata     The length of the sample input vector.
  * @param  d         The pointer to the pre calculated linear predictive coefficients vector.
  * @param  m         The number of pre computed LP coefficients.
  * @param  future    The pre allocated array to the calculated future samples.
  * @param  nfut      The number of elements to compute. 
  */
  static void predict(const double* data, const int ndata, const double* d, const int m, double* future, const int nfut);
  
//-------------------------------------------------------------------------------------------------

}; // class LinearPredictiveCoding

//-------------------------------------------------------------------------------------------------

} // namespace math

} // namespace clauer

//-------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_LINEAR_PREDICTIVE_CODING
