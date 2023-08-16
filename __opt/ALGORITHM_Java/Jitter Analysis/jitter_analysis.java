package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    jitter_analysis.cpp
// * @class   JitterAnalysis
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   takes a jitter analysis of the given signal
// * @see     http://en.wikipedia.org/...
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    jitter_analysis.h
// * @class   JitterAnalysis
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Class template file
// * @see     http://en.wikipedia.org/...
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_JITTER_ANALYSIS
//#define CLAUER_MATH_JITTER_ANALYSIS

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * The definition of the Jitter in this case is not like the Jitter definition in digital audio.
// * We understood Jitter as a micro oscillation of the frequency peak in a band which can be used
// * to detect variation in some transmision gears or other static signals with fluctations in the long
// * time anysis.
// 
public class JitterAnalysis
{


// *
//  * This static function extracts the frequency peak Jitter for a given frequency band from a time
//  * domain based signal. The result is the frequency peak Jitter in Hz. This implementation here uses
//  * no kind of whichcraft ;-) it an simple ane easy implementation which uses the Linear Predictive Coding
//  * to calculate the spectrum. By default this function returns the Jitter in Hz from window to window.
//  * Insted to see the difference it is possible to track the peak value.
//  *
//  * @param    samples         This is the input data vector.
//  * @param    nSamples        The length of the input data vector.
//  * @param    lpcPrevious     This is the number of samples used to generate the LPC coefficients.
//  * @param    lpcSpecSize     This is the number of future samples calculated from the LPC prediction and 
//  *                           coevally the spectrum size.
//  *                           NOTE that this values influences the resolution of the jitter analysis.
//  *                           NOTE this number must be a power of two because it is the input for 
//  *                           final fast fourier transformation.
//  * @param    lpcWinShift     The step size of the analysis in samples.
//  *                           NOTE that this value influences the number of anaysis points.
//  * @param    lpcCoeffs       The number of LPC coefficients.
//  * @param    sampleRate      The sample rate of the input signal.
//  * @param    lowerBandBorder The lower frequency band limit in Hz.
//  * @param    upperBandBorder The upper frequency band limit in Hz.
//  * @param    nPoints         The number of points generated while the analysis.
//  * @param    peak            Normaly the algorithm gives back the peak frequency difference from window to 
//  *                           window. If this flag is the algorithm gives back the raw peak value.
//  *                           NOTE: This flag is more/less useless with the following weightResult flag.
//  * @param    weightResult    This flag weights the difference from the previous peak frequency point with 
//  *                           amplitude of the current peak point which results a more noise independent analysis.
//  * @return                   This function returns the frequency jitter in Hz.
//  *                           NOTE that the memory for the analysis output vector will be allocated
//  *                           by this function.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double jitterAnalysis(double samples, int nSamples, int lpcPrevoius, int lpcSpecSize, int lpcWinShift, int lpcCoeffs, int sampleRate, int lowerBandBorder, int upperBandBorder, RefObject<Integer> nPoints, boolean peakReturn)
  {
	  return jitterAnalysis(samples, nSamples, lpcPrevoius, lpcSpecSize, lpcWinShift, lpcCoeffs, sampleRate, lowerBandBorder, upperBandBorder, nPoints, peakReturn, false);
  }
  public static double jitterAnalysis(double samples, int nSamples, int lpcPrevoius, int lpcSpecSize, int lpcWinShift, int lpcCoeffs, int sampleRate, int lowerBandBorder, int upperBandBorder, RefObject<Integer> nPoints)
  {
	  return jitterAnalysis(samples, nSamples, lpcPrevoius, lpcSpecSize, lpcWinShift, lpcCoeffs, sampleRate, lowerBandBorder, upperBandBorder, nPoints, false, false);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double* jitterAnalysis(const double* samples, const int nSamples, const int lpcPrevoius, const int lpcSpecSize, const int lpcWinShift, const int lpcCoeffs, const int sampleRate, const int lowerBandBorder, const int upperBandBorder, int* nPoints, boolean peakReturn = false, boolean weightResult = false)
  public static double jitterAnalysis(double samples, int nSamples, int lpcPrevoius, int lpcSpecSize, int lpcWinShift, int lpcCoeffs, int sampleRate, int lowerBandBorder, int upperBandBorder, RefObject<Integer> nPoints, boolean peakReturn, boolean weightResult)
  {
	// first init some numbers
	int nyquistFrequency = sampleRate / 2;
	double frequencyResolution = (double)(nyquistFrequency) / (double)(lpcSpecSize);
	nPoints.argvalue = (nSamples - lpcPrevoius) / lpcWinShift + 1;
	double[] jitter = new double[ nPoints.argvalue];
	assert nPoints.argvalue != 0;
	assert lowerBandBorder < upperBandBorder;
  
	// then the main windowing loop
	int peakPos = 0;
	int previousPeakPos = 0;
	for (int i =0, j=0; i < (nSamples-lpcPrevoius); i += lpcWinShift, j++)
	{
	  // first calcluate the lpc spectrum
	  double[] lpcSpectrum = GlobalMembersJitter_analysis.clauer.math.LinearPredictiveCoding.calcualteLpcPowerSpectrumEnvelope(samples+i, lpcPrevoius, lpcCoeffs, lpcSpecSize);
	  // extract here the border values in the vector
	  int lowerBorder = (int)((double)(lpcSpecSize) / (double)(nyquistFrequency) * (double)(lowerBandBorder));
	  int upperBorder = (int)((double)(lpcSpecSize) / (double)(nyquistFrequency) * (double)(upperBandBorder));
	  double peak = 0.0;
	  // now search for the peak and store the frequency
	  for (int f = lowerBorder; f < upperBorder; f++) // loop over the band
	  {
		// search the peak and the peak-posintion
		if (lpcSpectrum[f] > peak)
		{
		  peak = lpcSpectrum[f];
		  peakPos = f;
		}
	  }
	  // for the first value there is no previous value stored
	  if (i == 0)
		previousPeakPos = peakPos;
	  // set the jitter value
	  if (peakReturn == true)
		jitter[j] = (double)(peakPos) * frequencyResolution;
	  else
		jitter[j] = (double)(previousPeakPos - peakPos) * frequencyResolution;
	  // weight the result if required
	  if (weightResult == true)
		jitter[j] *= lpcSpectrum[peakPos];
	  // store the old peak position
	  previousPeakPos = peakPos;
	}
  
	// finally give the whole result back
	return(jitter);
  }


//------------------------------------------------------------------------------------------------

} // class JitterAnalysis

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_JITTER_ANALYSIS
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Langunage Library headers

// Input/Output Stream Library headers

// Stl headers

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