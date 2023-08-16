package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    median_frequency.h
// * @class   MedianFrequency
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This function calculates for the given spectrum the emdian frequency
// * @see     http://en.wikipedia.org/wiki/Median
// * @see     http://en.wikipedia.org/wiki/Frequency_spectrum
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    median_frequency.h
// * @class   MedianFrequency
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This function calculates for the given spectrum the emdian frequency
// * @see     http://en.wikipedia.org/wiki/Median
// * @see     http://en.wikipedia.org/wiki/Frequency_spectrum
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_MEDIAN_FREQUENCY
//#define CLAUER_MATH_MEDIAN_FREQUENCY

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This class implements two static member functions for the for the calculation of the median frequency
// * for wither a time domain or a frequency domain input signal. The median frequency is the frequency 
// * where the integral on the left side is equal to the integral of the Right side.
// 
public class MedianFrequency
{


//------------------------------------------------------------------------------------------------

// *
//  * This function calculates the median frequency of a spectrum. The median frequency is the frequency
//  * in the spectrum where the integral on the right side is equal to the integral on the left side. This 
//  * function can be used for example to make a decision for the hight of of a signal or try to differ 
//  * between a high and a low frequency distributed signal.
//  *
//  * @param    frequencyDomainSignal   The frequency domain input spectrum signal.
//  * @param    length                  The length of the spectrum input signal.
//  * @param    sampleRate              The sample rate or the double max-frequency of the input signal.
//  * @return                           The median frequency will be returned.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double calcualteMedianFrequencyFromFrequencyDomainSignal(double[] frequencyDomainSignal, int length, int sampleRate)
  {
	 // first calcualte the whole integral value from the normalized spectrum
	double sum = 0.0;
	for (int i =0; i<length; i++)
	  sum += Math.abs(frequencyDomainSignal[i]);
  
	// look for the median frequency on the point where the half integral sum value was reached
	double tmpSum = 0.0;
	int medianIndex = 0;
	for (int i =0; i<length; i++)
	{
	  tmpSum += Math.abs(frequencyDomainSignal[i]);
	  if (tmpSum > sum/2.0)
	  {
		medianIndex = i;
		break;
	  }
	}
  
	// calculate the median frequency
	double medianFrequency = (double)(sampleRate) /2.0 / (double)(length) * (double)(medianIndex);
  
	// return the result
	return medianFrequency;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This fucntion calcualtes the median frequency for a given time domain signal. The time domain signal 
//  * will be transfromed into the frequency domain and the above function will be called.
//  *
//  * @param    timeDomainSignal        The time domain input spectrum signal.
//  * @param    length                  The length of the spectrum input signal.
//  * @param    sampleRate              The sample rate or the double max-frequency of the input signal.
//  * @return                           The median frequency will be returned.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double calcualteMedianFrequencyFromTimeDomainSignal(RefObject<Double> timeDomainSignal, int length, int sampleRate)
  {
	// first zeropadd the signal to a length from a power of two
	int zeroPaddededLength = 0;
	double zeroPaddedTimeDomainSignal = GlobalMembersMedian_frequency.clauer.math.Utilities.autoZeroPadding(timeDomainSignal.argvalue, length, zeroPaddededLength);
	// transform the signal into the frequency domain
	double[] powerSpectrum = new double[zeroPaddededLength/2];
	//std::cout << "Length="<< length << ", SampeRate=" << sampleRate << ", ZeroPaddedLength=" << zeroPaddededLength << std::endl;
	GlobalMembersMedian_frequency.clauer.math.FourierTransformation.PowerSpectrum(zeroPaddededLength, zeroPaddedTimeDomainSignal, powerSpectrum);
	// call the frequency domain median function
	double medianFrequency = calcualteMedianFrequencyFromFrequencyDomainSignal(powerSpectrum, zeroPaddededLength/2, sampleRate);
	// delete the zero padded time domain signal and the spectrum
	zeroPaddedTimeDomainSignal = null;
	powerSpectrum = null;
	// return the result
	return medianFrequency;
  }

//------------------------------------------------------------------------------------------------

} // class MedianFrequency

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_MEDIAN_FREQUENCY
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C++ Langunage Library headers

// C Langunage Library headers

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