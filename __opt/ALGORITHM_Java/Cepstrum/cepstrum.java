package clauer.math;

//*
// * (c)      
// *
// * @file    cepstrum.h
// * @class   Cepstrum
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implements cepstral analyse
// * @see     http://en.wikipedia.org/wiki/Cepstrum
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    cepstrum.h
// * @class   Cepstrum
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implements cepstral analyse
// * @see     http://en.wikipedia.org/wiki/Cepstrum
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! RTE_MATH_CEPSTRUM
//#define RTE_MATH_CEPSTRUM

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * Like most of the mathematical classes this class also has only static members so no explizit
// * instanciation is necessary. The cepstrum class implement like the name says cepstral analysis methods.
// 
public class Cepstrum
{


//------------------------------------------------------------------------------------------------

// *
//  * This function calcualtes the cepstrum for the given time domain signal. The real cepstrum will be
//  * calculated with the folowing formular: Cepstrum = ABS ( IFFT ( LOG ( ABS ( FFT ( x) ) ) ). Note 
//  * that the resulting cepstrum will be calculated via an zeropadded fast fourier transformation which
//  * has in the back transformation a symetie. Therefore the resulting cepstrum will be have at the 
//  * end an zero zone.
//  *
//  * @param    samples                 The time domain input spectrum signal.
//  * @param    length                  The length of the spectrum input signal.
//  * @return                           The resulting cepstrum vectou will be pre allocated into this 
//  *                                   function and has the same langth than the input signal.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double calculateRealCepstrum(RefObject<Double> samples, int length)
  {
	// build the complex time domain input specreum
	int zeroPaddedLength = 0;
	double[] realTd = GlobalMembersCepstrum.clauer.math.Utilities.autoZeroPadding(samples.argvalue, length, zeroPaddedLength);
	double[] imagTd = new double[zeroPaddedLength];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(imagTd, 0, sizeof(double)*zeroPaddedLength);
	// tarnsform the time domain signal into the freqeuncy domain
	double[] realFd = new double[zeroPaddedLength];
	double[] imagFd = new double[zeroPaddedLength];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(realFd, 0, sizeof(double)*zeroPaddedLength);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(imagFd, 0, sizeof(double)*zeroPaddedLength);
	GlobalMembersCepstrum.clauer.math.FourierTransformation.FFT(zeroPaddedLength, false, realTd, imagTd, realFd, imagFd);
	// build the abs and logarithmize the spectrum
	for (int i =0; i<zeroPaddedLength/2; i++)
	  realFd[i] = Math.log(Math.sqrt(realFd[i]*realFd[i]+imagFd[i]*imagFd[i]));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(imagFd, 0, sizeof(double)*zeroPaddedLength);
	// transform the signal back to the frequency domain
	GlobalMembersCepstrum.clauer.math.FourierTransformation.FFT(zeroPaddedLength, true, realFd, imagFd, realTd, imagTd);
	// extract the cepstrum
	double[] cepstrum = new double[length];
	for (int i =0; i<length; i++)
	{
	  if (i < zeroPaddedLength/2)
		cepstrum[i] = Math.sqrt(realTd[i]*realTd[i]+imagTd[i]*imagTd[i]);
	  else
		cepstrum[i] = 0.0;
	}
	// waste the grabage
	realFd = null;
	imagFd = null;
	realTd = null;
	imagTd = null;
	// give the result back
	return cepstrum;
  }

//------------------------------------------------------------------------------------------------

} // class Cepstrum

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_CEPSTRUM
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C language Library headers

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------



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