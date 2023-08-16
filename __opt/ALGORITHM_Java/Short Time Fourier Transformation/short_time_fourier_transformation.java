package clauer.math;

// C Langunage Library headers

// C++ Language Library headers

//------------------------------------------------------------------------------------------------

import clauer.math.*;
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    short_time_fourier_transformation.cpp
// * @class   ShortTimeFourierTransformation
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   A Spectrogram Implementation
// * @see     http://en.wikipedia.org/wiki/Spectrogram
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    short_time_fourier_transformation.cpp
// * @class   ShortTimeFourierTransformation
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   A Spectrogram Implementation
// * @see     http://en.wikipedia.org/wiki/Spectrogram
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_SHORT_TIME_FOURIER_TRANSFORMATION
//#define CLAUER_MATH_SHORT_TIME_FOURIER_TRANSFORMATION

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------


//*
// * This class implemets only one static function for the extraction of the so called spectrogram 
// * aka the sonogram or power spectrum of the short time fourier transformation. The function allocates
// * a two dimensional array for itself and try to work as autonomous as possible. The resulting array
//  * can be accessed with spectrogram[time][frequency].
// 
public class ShortTimeFourierTransformation
{

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

// this enum declares the possible window functions for the short-time-FFT
  public enum windowFunction
  {
	WINDOW_HAMMING,
	WINDOW_HANN,
	WINDOW_BLACKMAN,
	WINDOW_TRIANGLE,
	WINDOW_WELCH,
	WINDOW_GAUSS,
	WINDOW_COSINE,
	WINDOW_ASYMEXPO
  }

//------------------------------------------------------------------------------------------------

// *
//  * The spectrum will be generated with the fast fourier transformation. If no parameter for the fft
//  * Length is given, the function try to find the best frequency resolution corresponding a screen 
//  * size of 16/9. If no value for the shift is given the default value of 1/6 of the fftLength is 
//  * taken. The reference values for the generated timePoints and the freqPoints will be set into this
//  * function. The freqPoints value has half the length of the fftLength. 
//  * NOTE: This function can be used to work fully autonomous with only the three first parameters 
//  * (data, length and sampleRate) the generated freqPoints and timePoints values will be set from this
//  * function. This function allocates the spectrum matrix internally for itself so not pre allocation
//  * is necessary. 
//  *
//  * @param  data        This value represents the time domain input signal.
//  * @param  length      The legnt of the input signal time domain vector.
//  * @param  sampleRate  The sample rate of the time donain input signal.
//  * @param  freqPoints  This return variable will be set to the number of generated frequency points
//  *                     from this function. 
//  * @param  timePoints  This return variable will be set to the number of generated time points from
//  *                     this function.  
//  * @param  fftLength   If the default value of 0 is given here this function ty to find the best 
//  *                     fftLength autonomously for the best resolution from a image size of 16/9. A
//  *                     given value must be have a power of two and influences the number of generated
//  *                     time and frequency points.
//  * @param  shift       The window shift value influences the overlaping and has a default vale of 1/6.
//  * @param  windowFunct The window function used to smooth the single time domain fft input windows.
//  * @return             This function returns a the allocated one dimensional specturm matrix with has
//  *                     the dimension spectrogram[timePoints][freqPoints]. Note that NO pre allocation is 
//  *                     necessary because the number of time domain points is calculated into this function.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double** generateShortTimeFourierTransformation(double[] data, int length, int sampleRate, RefObject<Integer> timePoints, RefObject<Integer> freqPoints, int fftLength, double shift)
  {
	  return generateShortTimeFourierTransformation(data, length, sampleRate, timePoints, freqPoints, fftLength, shift, windowFunction.WINDOW_HAMMING);
  }
  public static double** generateShortTimeFourierTransformation(double[] data, int length, int sampleRate, RefObject<Integer> timePoints, RefObject<Integer> freqPoints, int fftLength)
  {
	  return generateShortTimeFourierTransformation(data, length, sampleRate, timePoints, freqPoints, fftLength, 0.1666666666666666, windowFunction.WINDOW_HAMMING);
  }
  public static double** generateShortTimeFourierTransformation(double[] data, int length, int sampleRate, RefObject<Integer> timePoints, RefObject<Integer> freqPoints)
  {
	  return generateShortTimeFourierTransformation(data, length, sampleRate, timePoints, freqPoints, 0, 0.1666666666666666, windowFunction.WINDOW_HAMMING);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double** generateShortTimeFourierTransformation(const double* data, const int length, const int sampleRate, int& timePoints, int& freqPoints, int fftLength = 0, const double shift = 0.1666666666666666, const windowFunction timeWindow = WINDOW_HAMMING)
  public static double** generateShortTimeFourierTransformation(double[] data, int length, int sampleRate, RefObject<Integer> timePoints, RefObject<Integer> freqPoints, int fftLength, double shift, windowFunction timeWindow)
  {
	// first check if there was any value given for the window sizes
	if (fftLength == 0)
	{
	  // tyr to find the window sizes corresponding the asuptation that the ws should have the
	  // approximately the double size as the fteqPoints length. The 16/9 factor is for the adaption
	  // of the screen and the image output.
	  fftLength = GlobalMembersShort_time_fourier_transformation.clauer.math.Utilities.getNextPowerOfTwo((int)(Math.sqrt(length/shift)));
	}
	// check if the given value has a power of two
	else
	  if (GlobalMembersShort_time_fourier_transformation.clauer.math.Utilities.isPowerOfTwo(fftLength) == false)
	  {
		if (true == true)
		{
			System.out.print("The given fftLength has not a power of two. Change the fftLength from ");
			System.out.print(fftLength);
		}
		fftLength = GlobalMembersShort_time_fourier_transformation.clauer.math.Utilities.getNextPowerOfTwo(fftLength);
		if (true == true)
		{
			System.out.print(" to a length of ");
			System.out.print(fftLength);
			System.out.print("\n");
		}
	  }
	// first calcualte the dimensions of the spectrogram matrix
	freqPoints.argvalue = fftLength/2;
	int winShift = (int)((double)(fftLength) * shift);
	timePoints.argvalue = (int)(Math.ceil((double)(length) / (double)(winShift)));
	if (true == true)
  {
	  System.out.print("FFTLength=");
	  System.out.print(fftLength);
	  System.out.print(" Shift=");
	  System.out.print(shift);
	  System.out.print(" WinShift=");
	  System.out.print(winShift);
	  System.out.print(" TimePoints=");
	  System.out.print(timePoints.argvalue);
	  System.out.print(" Frequencypoints=");
	  System.out.print(freqPoints.argvalue);
	  System.out.print(" Memory=");
	  System.out.print(timePoints.argvalue *freqPoints.argvalue *8.0/1024.0/1024.0);
	  System.out.print("MB");
	  System.out.print("\n");
  }
  
	// allocation the two dimensional array
	double[] spectrogram = new double[timePoints.argvalue];
	for(int i =0; i<timePoints.argvalue; i++)
	  spectrogram[i] = new double[freqPoints.argvalue];
  
	// the temporary time window
	double[] tmpTimeDomain = new double[fftLength];
  
	// loop over the time windows
	for (int i =0, j=0; i<timePoints.argvalue; i++, j+=winShift)
	{
	  // copy the time domain window
	  for (int k =0; k<fftLength; k++)
	  {
		int index = j+k;
		if (index < length)
		  tmpTimeDomain[k] = data[index];
		else
		  tmpTimeDomain[k] = 0.0;
	  }
	  // apply the selected window function
	  switch (timeWindow)
	  {
		case windowFunction.WINDOW_HAMMING:
			Utilities.applyHammingWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_HANN:
			Utilities.applyHannWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_BLACKMAN:
			Utilities.applyBlackmanWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_TRIANGLE:
			Utilities.applyTriangleWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_WELCH:
			Utilities.applyWelchWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_GAUSS:
			Utilities.applyGaussWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_COSINE:
			Utilities.applyCosineWindow(tmpTimeDomain, fftLength);
		case windowFunction.WINDOW_ASYMEXPO:
			Utilities.applyAsymetricalExponentialWindow(tmpTimeDomain, fftLength);
	  }
	  // calculate the FFT
	  GlobalMembersShort_time_fourier_transformation.FourierTransformation.PowerSpectrum(fftLength, tmpTimeDomain, spectrogram[i]);
	}
  
	// waste the grabage
	tmpTimeDomain = null;
  
	// return the spectrogram
	return spectrogram;
  }

//------------------------------------------------------------------------------------------------

} // class ShortTimeFourierTransformation

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_SHORT_TIME_FOURIER_TRANSFORMATION
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER NOTE: The following #define macro was replaced in-line:
//#define DEBUG true

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