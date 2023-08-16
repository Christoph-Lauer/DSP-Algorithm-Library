package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    convolution.cpp
// * @class   Convolution
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class collects static convolution operations.
// * @see     http://en.wikipedia.org/wiki/Convolution
// * @see     http://en.wikipedia.org/wiki/FFT
// * @todo    finished so far
// 


//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    convolution.h
// * @class   Convolution
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class collects static convolution operations.
// * @see     http://en.wikipedia.org/wiki/Convolution
// * @see     http://en.wikipedia.org/wiki/FFT
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_CONVOLUTION
//#define CLAUER_MATH_CONVOLUTION


//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This class collects functions for the calculation of the convolution. One static function is for 
// * the "normal" convolution calculation corresponding the well known convolution equation in the time
// * domain. Another function calculates the so called fast convolution in the frequency domain with help
// * of the FFT. Like all other mathematic functions and classes both functions are implemented as 
// * static functions so no instanciation of the class is necessary. 
// 
public class Convolution
{


//------------------------------------------------------------------------------------------------

// *
//  * This static function calculates the standard convolution corresponding the well known convolution
//  * equation. The calculation is performed into the time domain. The resulting vector is allocated inside
//  * this function so no pre allocation is necessary. The return vector will have the same length than 
//  * the length of both input vectors.
//  * 
//  * @param signal1    The first time domain input signal.
//  * @param signal2    The secod time domain input signal.
//  * @param legnth     The length of both signals.
//  * @return           This function returns the convolution of signal1 and signal2 in the time domain.
//  *                   NOTE: the result return vector will be allocated into this function so ne pre
//  *                   allocation is necessary.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double calculateStandardConvolution(double[] signal1, double[] signal2, int length)
  {
	// first allocate the result vector
	double[] convolution = new double[length];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(convolution, 0, sizeof(double)*length);
  
	// calculate the concolution in the time doamin
	for (int n =0; n<length; n++)
	  for (int m =1; m<length; m++)
		if ((n-m) > 0)
		  convolution[n] += signal1[m]*signal2[n-m];
  
	// return the result
	return convolution;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function performs a fast convolution in with the help of the FFT. The length of the input vector
//  * can be have an arbitary length and must not have a length from a power of two. The concolution operation 
//  * is done in the frequency domain. THe resulting vector will have a length from the next power of two.
//  * @note  The this function allocates the signal vector for itself so no pre allocation is necessary.
//  * @note  Because the circular nature of the FFT the result will be mirrored on the middle point !
//  * 
//  * @param signal1    The first time domain input signal.
//  * @param signal2    The secod time domain input signal.
//  * @param legnth     The length of both signals.
//  * @return           This function returns the convolution of signal1 and signal2 in the time domain.
//  *                   NOTE: the result return vector will be allocated into this function so ne pre
//  *                   allocation is necessary.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double calculateFastConvolution(double signal1, double signal2, int length)
  {
	// first zeropadd both signals to a length from a power of two and prepare the data for the FFT
	int newLength = 0;
	double tdr1 = GlobalMembersConvolution.clauer.math.Utilities.autoZeroPadding(signal1, length, newLength);
	double tdr2 = GlobalMembersConvolution.clauer.math.Utilities.autoZeroPadding(signal2, length, newLength);
	double[] tdi1 = new double[newLength];
	double[] tdi2 = new double[newLength];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(tdi1, 0, sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(tdi2, 0, sizeof(double));
  
	// perform the FFT
	double[] fdr1 = new double[newLength];
	double[] fdi1 = new double[newLength];
	double[] fdr2 = new double[newLength];
	double[] fdi2 = new double[newLength];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(fdr1, 0, sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(fdi1, 0, sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(fdr2, 0, sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(fdi2, 0, sizeof(double));
	GlobalMembersConvolution.clauer.math.FourierTransformation.FFT(newLength, false, tdr1, tdi1, fdr1, fdi1);
	GlobalMembersConvolution.clauer.math.FourierTransformation.FFT(newLength, false, tdr2, tdi2, fdr2, fdi2);
  
	// do the convoltion in the frequency domain
	for (int i =0; i<newLength; i++)
	{
	  fdr1[i] = fdr1[i]*fdr2[i] - fdi1[i]*fdi2[i];
	  fdi1[i] = fdr1[i]*fdi2[i] + fdi1[i]*fdr2[i];
	}
  
	// transform the result back from the frequency domain into the time domain
	GlobalMembersConvolution.clauer.math.FourierTransformation.FFT(newLength, true, fdr1, fdi1, tdr1, fdi1);
  
	// wast the grabage
	//delete[] tdr1;
	tdi1 = null;
	tdr2 = null;
	tdi2 = null;
	fdr1 = null;
	fdi1 = null;
	fdr2 = null;
	fdi2 = null;
  
	// give the result back
	return tdr1;
  }

//------------------------------------------------------------------------------------------------

} // class Convolution

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_CONVOLUTION
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Langunage Library headers

// C++ Language Library headers

// Stl headers

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------


