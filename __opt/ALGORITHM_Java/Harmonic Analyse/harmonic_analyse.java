package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    hatrminic_analyse.cpp
// * @class   Harmonic Analyse
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Collects functions for the harmonic analyse
// * @see     http://en.wikipedia.org/wiki/Harmonic
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    hatrminic_analyse.h
// * @class   Harmonic Analyse
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Collects functions for the harmonic analyse
// * @see     http://en.wikipedia.org/wiki/Harmonic
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_HARMONIC_ANALYSE
//#define CLAUER_MATH_HARMONIC_ANALYSE

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This class implements functions for the harmonic analyse. Like all other mathematical classes this
// * class collects static functions. THe harmonic analyse function trys to find a fundamental frequency 
// * in a given search intervall and extracts the levels of the harmonic followers. The resulting return
// * values are all in decibell (dB) level dimensions appart from the frequency return values.
// 
public class HarmonicAnalyse
{


//* 
// * This enummeration defines the harmonics search method. Two methods are implemented.
// * The first (EXACT_POSITION) method looks for the peak at the exact multipler possitions of the ground frequency.
// * The second method (SEARCH_AROUND_RADIUS) searches the harmonics based on the fundamental frequency arround an intervall
// * border. The border is typically determined by an percent value of the first fundamental frequency.
// * In this case the search border will be determined by percentage value of the fundamental frequency 
// * which is the applyed to search for the harmonics with a multipler of the harmoc frequencies. 
// * Example given: If the fundamental was found at 100Hz and the search border was predefined at 1%,
// * for the second order harmonics the search intervall was +/-1 percent, from 199...200Hz. For the
// * third oder harmonic the intervall was at +/-3Hz, from 297...306 and so on. 
//  
 public enum extraction_method
 {
	 EXACT_POSITION,
	 SEARCH_AROUND_RADIUS
 }

// *
//  * This function performs an harmonic analyse. Given the Signal, the length, the sample rate and 
//  * the search intervall for the first harmonic this function try to find the peak in the search
//  * intervall and looks for the level of the other harmonics. The return value is a vector with the 
//  * level of the harmonics beginning from the first harmonic found in the search intervall to the 
//  * tenth harmonics. If the resulting N-th order harmonic frequency is outside the nyquist frequency
//  * the level of the harmonic given back in the return value is set to zero.
//  * 
//  * @param  signal                    The time domain Signal.
//  * @param  length                    The length of the time domain signal.
//  * @param  sampleRate                The sample rate of the time domain signal.
//  * @param  lowerSearchFrequency      The lower search frequency.
//  * @param  UpperSearchFrequency      The Upper search frequency.
//  * @return fundamentalFrequency      This is the fundamental base frequency found in the search intervall.
//  * @return fundamentalFrequencyLevel This value is the level of the found fundamental frequency. This return 
//  *                                   is in decibel.
//  * @param  searchMethod              This value determines the search method for the harmonic extraction.
//  *                                   The default valeu for the search method is the normal exact position method.
//  *                                   For the secod method the the search radius in percent can be specifed.
//  * @param  searchRadius              This value defines the search radius for the harmonics extraction for the 
//  *                                   second method. The second method searches the ahrmoncis arround an predefined
//  *                                   intervall which is a multiple of the percentage of the fundamental freqeuncy found.
//  *                                   Look in the specification of the methods enummeration for a more detailed description.
//  *                                   Note that this value must be smaler than approx. 5 percent. The defautl value is 0.5 %
//  * @return                           This function Returns a vector with the first ten harmonics beginning
//  *                                   from th one found in the search intervall to the tenth. Note that the 
//  *                                   return vector will be allocated into this function so the caller hat to 
//  *                                   be care for the deallocation of the arry.
//  *                                   THE UNIT OF THE RESULT ARRAY IS DECIBELL (dB) !!!
//  

//------------------------------------------------------------------------------------------------

public static double harmonicAnalyse(RefObject<Double> signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, RefObject<Double> fundamentalFrequency, RefObject<Double> fundamentalFrequencyLevel, int extraction_method)
{
	return harmonicAnalyse(signal, length, sampleRate, lowerSearchFrequency, upperSearchFrequency, fundamentalFrequency, fundamentalFrequencyLevel, extraction_method, .5);
}
public static double harmonicAnalyse(RefObject<Double> signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, RefObject<Double> fundamentalFrequency, RefObject<Double> fundamentalFrequencyLevel)
{
	return harmonicAnalyse(signal, length, sampleRate, lowerSearchFrequency, upperSearchFrequency, fundamentalFrequency, fundamentalFrequencyLevel, extraction_method.EXACT_POSITION, .5);
}
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double* harmonicAnalyse(double* signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, double* fundamentalFrequency, double* fundamentalFrequencyLevel, int extraction_method = EXACT_POSITION, double searchRadius = .5)
public static double harmonicAnalyse(RefObject<Double> signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, RefObject<Double> fundamentalFrequency, RefObject<Double> fundamentalFrequencyLevel, int extraction_method, double searchRadius)
{

  /////////////////////////////////////////////////////////
  // extract the spectrum and the fundamental frequency

  // first transform the spectrum into the frequency domain
  int zpLength;
  double zpSignal = GlobalMembersHarmonic_analyse.clauer.math.Utilities.autoZeroPadding(signal.argvalue, length, zpLength);
  double[] spectrum = new double[zpLength/2];
  GlobalMembersHarmonic_analyse.clauer.math.FourierTransformation.PowerSpectrum(zpLength, zpSignal, spectrum);
  // logarithmize the powerspectrum
  GlobalMembersHarmonic_analyse.clauer.math.Utilities.lin2logArrayInPlace(spectrum, zpLength/2);
  // look for the peak in the given intervall
  int lowerIndex = (int)(lowerSearchFrequency * (double)(zpLength/2) / (double)(sampleRate/2));
  int upperIndex = (int)(upperSearchFrequency * (double)(zpLength/2) / (double)(sampleRate/2));
  double peakLevel = -DBL_MAX;
  int peakIndex = -1;
  for (int i =lowerIndex; i<upperIndex; i++)
	if (spectrum[i] > peakLevel)
	{
	  peakLevel = spectrum[i];
	  peakIndex = i;
	}
  // fundamental frequency results
  ( fundamentalFrequency.argvalue) = (double)(peakIndex) / (double)(zpLength/2) * (double)(sampleRate/2);
  ( fundamentalFrequencyLevel.argvalue) = spectrum[peakIndex];
  // allocate the result vector
  double[] harmonics = new double[DefineConstantsHarmonic_analyse.NUMBER_OF_HARMONICS];


  /////////////////////////////////////////////////////////
  // EXACT_POSITION harmonic extraction method

  if (extraction_method == (int)extraction_method.EXACT_POSITION)
  {
	for (int i =1; i<=DefineConstantsHarmonic_analyse.NUMBER_OF_HARMONICS; i++)
	{
	  if (peakIndex *i < zpLength/2)
	  {
		harmonics[i-1] = spectrum[peakIndex *i];
		harmonics[i-1] -= ( fundamentalFrequencyLevel.argvalue);
	  }
	  else
		harmonics[i-1] = -DBL_MAX;
	}
  }


  /////////////////////////////////////////////////////////
  // SEARCH_IN_INTERVALL harmonic extraction method

  if (extraction_method == (int)extraction_method.SEARCH_AROUND_RADIUS)
  {
	harmonics[0] = 0.0;
	// next the loop over the harmonics
	for (int i =1; i<=DefineConstantsHarmonic_analyse.NUMBER_OF_HARMONICS; i++)
	{
	  // this value defines the search border for the harmonics
	  int borderIndex = i *(int)(searchRadius / 100.0 / 2.0 *(double)(zpLength/2));
	  // the harmonic peak index
	  int harmonicPeakIndex = peakIndex*(i+1);
	  // the local peak value for the border area arround the harmonic
	  double localPeak = -DBL_MAX;
	  // the loop over the harmonics search border, first look for the peak in the border
	  for (int index =(harmonicPeakIndex-borderIndex); index<(harmonicPeakIndex+borderIndex); index++)
	  {
		// make sure the index is not out of the border of the vector length
		if (index >= zpLength/2)
		  continue;
		// look for the peak
		if (localPeak < spectrum[index])
		  localPeak = spectrum[index];
	  }
	  // save the found harmonic
	  harmonics[i] = localPeak;
	  harmonics[i] -= ( fundamentalFrequencyLevel.argvalue);
	}
  }

  // clean the grabbage
  zpSignal = null;
  spectrum = null;

  // give the result back
  return harmonics;
}


//------------------------------------------------------------------------------------------------

} // class HarmonicAlanylse

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_HARMONIC_ANALYSE
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Langunage Library headers

//------------------------------------------------------------------------------------------------

/// THis value defines the number of harmonics and the length of the result vector
//#define NUMBER_OF_HARMONICS 10

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