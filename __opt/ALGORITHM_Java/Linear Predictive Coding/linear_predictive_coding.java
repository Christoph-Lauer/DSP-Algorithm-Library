package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    linear_predictive_coding.cpp
// * @class   LinearPredictiveCoding
// * @version 0.5
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Calculates the linear predictive coefficients and computes the resulting values.
// * @see     http://en.wikipedia.org/wiki/Linear_predictive_coding
// * @todo    the lpc spectrum should still be calclulated. In the LPC spectrum calculation 
// *          the logarithmization uses an additional factor which beware an logarithmization 
// *          of 0.
// *
// * The object of linear prediction is to form a model of a linear time-invariant 
// * digital system trought observation of input and output sequences. The implementation
// * of the prediction coefficient extraction here serves after the Levinson Durbin implementation
// * to solve the matrix system.
// 

//-------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    linear_predictive_coding.h
// * @class   LinearPredictiveCoding
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Calculates the linear predictive coefficients and computes the resulting values.
// * @see     http://en.wikipedia.org/wiki/Linear_predictive_coding
// * @todo    the lpc spectrum should still be calclulated.
// *
// * The object of linear prediction is to form a model of a linear time-invariant 
// * digital system trought observation of input and output sequences. The implementation
// * of the prediction coefficient extraction here serves after the Levinson Durbin implementation
// * to solve the matrix system.
// *
// * Please Note:  The number of LPC Coefficients used to predict the future vector must be shorter than 
// *               the number of previous samples used to extract the LP Coefficients !!! An Assertation 
// *               in the code will check this. In release versions the usage of more LP-Coefficients
// *               as the previous samples will cause an access violation !!!
// 

//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_LINEAR_PREDICTIVE_CODING
//#define CLAUER_MATH_LINEAR_PREDICTIVE_CODING

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------

public class LinearPredictiveCoding
{


//-------------------------------------------------------------------------------------------------  

// *
//  * This function is the master function for the prediction error extraction which generates multiple 
//  * prediction error values and collects them into a vector. In this way an stationary/continuary test 
//  * for signal can be done where the resulting cureve represents the changes in the signal in case of
//  * the drivation from the predicted to the original signal with the linear prediction.
//  * This function operates in the continious scale (real seconds) time domain instead ot the sample 
//  * time unit domain.
//  *
//  * @param   data        The pointer to the input time domain based signal.
//  * @param   nData       The number of elements from the data to use.
//  * @param   nCoeff      The number of LP coefficents to use for the prediction.
//  * @param   sampleRate  The sample rate of the input signal is needed to make the time to samples conversion
//  * @param   previous    The time of samples to be used for the prediction.
//  *                      Note the sale of this value is MILLISCONDS !!!
//  * @param   future      The time of samples to be predict in the future and to measure the error 
//  *                      between the prediction and the real world samples.
//  *                      Note the sale of this value is MILLISCONDS !!!
//  * @param   shift       The shift step time of the vector calculator.
//  *                      Note the sale of this value is MILLISCONDS !!!
//  * @param   nGenerated  A pointer to the number of error points which are generated for the output array.
//  *                      This paramter is a kind of return value.
//  * @return              This function returns the internally allocated arrray of the error vector.
//  *                      The scale of the error curve is percent.
//  

  //-------------------------------------------------------------------------------------------------	
  
  public static double calcPredictionErrorVectorInPercent(double data, int nData, int nCoeff, int sampleRate, double previous, double future, double shift, RefObject<Integer> nGenerated)
  {
	// first pre-calculate and pre-allocate some values
	int nPrevious = (int)(previous * (double)(sampleRate) / 1000.0);
	int nFuture = (int)(future * (double)(sampleRate) / 1000.0);
	int nShift = (int)(shift * (double)(sampleRate) / 1000.0);
  
	// Make sure the basic boundary conditions
	assert nData > nShift;
	assert nData > (nPrevious + nFuture);
  
	// estimate the maximal number of error points to generate
	( nGenerated.argvalue) = ((nData - nPrevious - nFuture) / nShift)+1;
	// then allocate the enough elements (max one more as needed)
	double[] out = new double[( nGenerated.argvalue)];
  
	// calc the prediction error curve and correct the estimated real number of points
	( nGenerated.argvalue) = 0;
	for (int i =0, j=0; i < (nData - nPrevious - nFuture); i+=nShift, j++, ( nGenerated.argvalue)++)
	{
	  // call the prediction error function for one point
	  out[j] = calcPredictionErrorInPercent(data+i, nPrevious, nCoeff, nFuture);
	  }
  
	//   finally give the result back
	return out;
  }

//-------------------------------------------------------------------------------------------------  

// *
//  * This function calculates the prediction error from the predicted feature vector for the 
//  * given settings to the real vector in percent. Note that the given input vector must have 
//  * at least the length of nData + nFuture !!!
//  *
//  * @param  data      The pointer to the input vector.
//  * @param  nData     The number of elements from data to use.
//  * @param  nCoeff    The number of LP coefficients to use.
//  * @param  nFuture   The number of future elements to compute. 
//  *                   Note that the user must make sure from the out side that the array has
//  *                   a valid longer size than the nFuture + nData sizes.
//  * @return           This function returns the prediction error from the predicted vector
//  *                   generated corresponding the settings given to the real vector.
//  

  //-------------------------------------------------------------------------------------------------	
  
  public static double calcPredictionErrorInPercent(double[] data, int nData, int nCoeff, int nFuture)
  {
	// first calculate the future vector
	double[] future = calcFutureVector(data, nData, nCoeff, nFuture);
  
	// now calculate the prediction error in percent
	double error = 0.0;
	double avg = 0.0;
	for (int i =0; i<nFuture; i++)
	{
	  error += Math.abs(future[i] - data[nData+i]); // upsum the error
	  avg += Math.abs(data[nData+i]); // upsum the core signal
	}
	error = error / avg * 100.0;
  
	// remove temporary memory
	future = null;
  
	// finally give the result back
	return error;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This function calculates the LPC power spectrum envelope with the given prediction coefficients and the 
//  * given size for the sample vector. Internally this function calculates first the an linear prediction
//  * for the double size and then calculates the power spectrum for the  predicted future vectore. Please note
//  * that the size of the spectrum must have a power of two !!!
//  *
//  * @param  data                The pointer to the input vector.
//  * @param  nData               The number of elements from data to use.
//  * @param  nCoeff              The number of LP coefficients to use.
//  * @param  nSpecSize           Is the size of the spectrum to calculate. Please ensure that the size of the 
//  *                             Spectrum must have a power of two !!!
//  * @param  logarithmize        If this special flag is set the spectrum will be logarithmized.
//  * @param  applyHammingWindow  If this flag is set the predicted time doamin vector will be windowed
//  *                             with an Hamming window before the final fast fourier transformation.
//  * @return                     This function returns the LPC power spectrum with the size of nSpecSize.
//  *                             The memory for the spectrum will be allocated into this function.
//  

  //-------------------------------------------------------------------------------------------------	
  
  public static double calcualteLpcPowerSpectrumEnvelope(double data, int nData, int nCoeff, int nSpecSize, boolean logarithmize)
  {
	  return calcualteLpcPowerSpectrumEnvelope(data, nData, nCoeff, nSpecSize, logarithmize, false);
  }
  public static double calcualteLpcPowerSpectrumEnvelope(double data, int nData, int nCoeff, int nSpecSize)
  {
	  return calcualteLpcPowerSpectrumEnvelope(data, nData, nCoeff, nSpecSize, false, false);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double* calcualteLpcPowerSpectrumEnvelope(const double* data, const int nData, const int nCoeff, const int nSpecSize, boolean logarithmize = false, boolean applyHammingWindow = false)
  public static double calcualteLpcPowerSpectrumEnvelope(double data, int nData, int nCoeff, int nSpecSize, boolean logarithmize, boolean applyHammingWindow)
  {
	// first calclualte the furture vector
	double future = calcFutureVector(data, nData, nCoeff, nSpecSize *2);
  
	// now calculate the fast fourier transformation from the predicted future array
	double[] lpcSpec = new double[nSpecSize];
	// window the time domain signal if required
	if (applyHammingWindow == true)
	  GlobalMembersLinear_predictive_coding.clauer.math.Utilities.applyHammingWindow(future, nSpecSize *2);
	// then do the FFT transformation
	GlobalMembersLinear_predictive_coding.clauer.math.FourierTransformation.PowerSpectrum(nSpecSize *2, future, lpcSpec);
  
	// logarithmize if postulated
	if (logarithmize == true)
	  GlobalMembersLinear_predictive_coding.clauer.math.Utilities.lin2logArrayInPlace(lpcSpec, nSpecSize);
  
	// delete the grabage
	future = null;
  
	// finally return the lpc spectrum
	return lpcSpec;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This function calculates future sample based on the linear predictive coding method implemented
//  * into this class. 
//  *
//  * @param  data      The pointer to the input vector.
//  * @param  nData     The number of elements from data to use.
//  * @param  nCoeff    The number of LP coefficients to use.
//  * @param  nFuture   The number of future elements to compute.
//  * @return           The is function returns a pointer to the future elements. Note that 
//  *                   All needed memory for the future vector is allocated into this function,
//  *                   so no pre allocation for the future vector is needed. Do not forget to 
//  *                   delete this memory !!!
//  * Please Note:  The number of LPC Coefficients (nCoeff) used to predict the future vector must be shorter than 
//  *               the number of previous samples (nData) used to extract the LP Coefficients !!! An Assertation 
//  *               in the code will check this. In release versions the usage of more LP-Coefficients
//  *               as the previous samples will cause an access violation !!!  

  //-------------------------------------------------------------------------------------------------	
  
  public static double calcFutureVector(double data, int nData, int nCoeff, int nFuture)
  {
	// some initial conditionals before beginning
	assert nData > 0;
	assert nCoeff > 0;
	assert nFuture > 0;
	assert data != null;
	// this an absolute must criterion !!!
	assert nCoeff < nData;
  
	// first allocate the needed memory
	double[] coeffs = new double[nCoeff];
	double[] future = new double[nFuture];
  
	// calculate the linear predictive coefficients
	RefObject<Double> TempRefObject = new RefObject<Double>(coeffs);
	lpcSolveLevinsonDurbin(data, nData, nCoeff, TempRefObject);
	coeffs = TempRefObject.argvalue;
  
	// then calculate the predicted future based of the coefficients
	RefObject<Double> TempRefObject2 = new RefObject<Double>(future);
	predict(data, nData, coeffs, nCoeff, TempRefObject2, nFuture);
	future = TempRefObject2.argvalue;
  
	// at the end delete the temporary needed memory
	coeffs = null;
  
	// and finally give the vector back
	return future;
  }

//-------------------------------------------------------------------------------------------------


// *
//  * Autocorrelation LPC coeff generation algorithm invented by N. Levinson in 1947,
//  * modified by J. Durbin in 1959. This routine is stohlen from the Numerical Recipes in c.
//  * Note that the prediction coefficient vector 'd' must be pre allocated outside this function !
//  *
//  * @param  data      The pointer to the input sample vector.
//  * @param  n         The length of the sample input vector.
//  * @param  m         The number of LP coefficients to compute.
//  * @param  d         The pointer to the pre allocated linear predictive coefficents vector.
//  * @return           This function returns the mean square discrepancy of the calculation.
//  

  //-------------------------------------------------------------------------------------------------	
  
  private static double lpcSolveLevinsonDurbin(double[] data, int n, int m, double[] d)
  {
	// adapt the pointers from the real c world to the numerical recipes world
	data--; // the input time domain sample data
	d--; // the calculated linear prediction coefficients
  
	// declare and initialize some temporary values here
	int k;
	int j;
	int i;
	double p =0.0;
	double xms;
	double[] wk1 = new double[n+1];
	double[] wk2 = new double[n+1];
	double[] wkm = new double[m+1];
  
	// inittalize the temporary arrys to 0 with memset
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(wk1,0,sizeof(wk1));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(wk2,0,sizeof(wk2));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(wkm,0,sizeof(wkm));
  
	for (j =1;j<=n;j++)
	  p += (data[j]*data[j]);
	xms =p/n;
	wk1[1] =data[1];
	wk2[n-1] =data[n];
	for (j =2;j<=n-1;j++)
	{
	  wk1[j] =data[j];
	  wk2[j-1] =data[j];
	}
	for (k =1;k<=m;k++)
	{
	  double num =0.0;
	  double denom =0.0;
	  for (j =1;j<=(n-k);j++)
	  {
		num += wk1[j]*wk2[j];
		denom += (wk1[j]*wk1[j])+(wk2[j]*wk2[j]);
	  }
	  d[k] =2.0 *num/denom;
	  xms *= (1.0-(d[k]*d[k]));
	  for (i =1; i<=(k-1); i++)
		d[i] =wkm[i]-d[k]*wkm[k-i];
  //   *
  //    * The algorithm is recursive, building up the answer for larger and larger values of m
  //    * until the desired value is reached. At this point in the algorithm, one could return
  //    * the vector d and scalar xms for a set of LP coeffcients with k (rather than m) terms.
  //
	  if (k == m)
	  {
		// clean the temporary arrays
		wk1 = null;
		wk2 = null;
		wkm = null;
		// and return from the recursive loop
		return xms;
	  }
	  for (i =1;i<=k;i++)
		  wkm[i] =d[i];
	  for (j =1;j<=(n-k-1);j++)
	  {
		wk1[j] -= wkm[k]*wk2[j];
		wk2[j] =wk2[j+1]-wkm[k]*wk1[j+1];
	  }
	}
  // *
  //  *  never get here in this function
  //
	assert true;
	return 0.0;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This function predictes the future of the given data input array corresponding to
//  * the given and pre calculated linear predictive coefficients. Note that the future 
//  * vector must be pre allocated. The algorithms works so that the first calculated 
//  * futur element (means futur[0]) is the last element of in the input array (data[ndata])
//  * so that future[1] = data[ndata+1].
//  * 
//  * @param  data      The pointer to the input sample vector.
//  * @param  ndata     The length of the sample input vector.
//  * @param  d         The pointer to the pre calculated linear predictive coefficients vector.
//  * @param  m         The number of pre computed LP coefficients.
//  * @param  future    The pre allocated array to the calculated future samples.
//  * @param  nfut      The number of elements to compute. 
//  

  //-------------------------------------------------------------------------------------------------
  
  private static void predict(double[] data, int ndata, double[] d, int m, double[] future, int nfut)
  {
	int k;
	int j;
	double sum;
	double discrp;
	double[] reg = new double[m+1];
  
	// adapt the pointers from the real c world to the numerical recipes world
	data--; // the input time doamin data
	d--; // the pre calculated linear prediction coefficients
	future--; // the predicted future vector
  
	for (j =1; j<=m; j++)
	  reg[j] = data[ndata + 1 -j];
	for (j =1; j<=nfut; j++)
	{
	  discrp =0.0;
  //
  //    * This is where you would put in a known discrepancy if you were reconstructing a
  //    * function by linear predictive coding rather than extrapolating a function by linear prediction.
  //
	  sum =discrp;
	  for (k =1; k<=m;k++)
		sum += d[k]*reg[k];
	  for (k =m; k>=2; k--)
		reg[k] = reg[k-1];
	  // If you want to implement circular arrays, you can avoid this shifting of coffcients.
	  future[j] = reg[1] = sum;
	}
	// clean the temporary arrays
	reg = null;
  }

//-------------------------------------------------------------------------------------------------

} // class LinearPredictiveCoding

//-------------------------------------------------------------------------------------------------



//-------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_LINEAR_PREDICTIVE_CODING
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Language Libary headers

// IOstream header

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------



//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2009 Tangible Software Solutions Inc.
//
//	This class is used to simulate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
final class RefObject<T>
{
	T argvalue;
	RefObject(T refarg)
	{
		argvalue = refarg;
	}
}