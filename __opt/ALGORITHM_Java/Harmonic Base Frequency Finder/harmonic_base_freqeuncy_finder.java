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

// C Langunage Library headers

//------------------------------------------------------------------------------------------------

/// THis value defines the number of harmonics and the length of the result vector
//#define NUMBER_OF_HARMONICS 10

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER WARNING: The declaration of the following method implementation was not found:
//ORIGINAL LINE: double* HarmonicAnalyse::harmonicAnalyse(double* signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, double* fundamentalFrequency, double* fundamentalFrequencyLevel, int extraction_method, double searchRadius)

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

public class HarmonicAnalyse
{
	public double harmonicAnalyse(RefObject<Double> signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, RefObject<Double> fundamentalFrequency, RefObject<Double> fundamentalFrequencyLevel, int extraction_method, double searchRadius)
	{
	
	  /////////////////////////////////////////////////////////
	  // extract the spectrum and the fundamental frequency
	
	  // first transform the spectrum into the frequency domain
	  int zpLength;
	  double zpSignal = GlobalMembersHarmonic_base_freqeuncy_finder.clauer.math.Utilities.autoZeroPadding(signal.argvalue, length, zpLength);
	  double[] spectrum = new double[zpLength/2];
	  GlobalMembersHarmonic_base_freqeuncy_finder.clauer.math.FourierTransformation.PowerSpectrum(zpLength, zpSignal, spectrum);
	  // logarithmize the powerspectrum
	  GlobalMembersHarmonic_base_freqeuncy_finder.clauer.math.Utilities.lin2logArrayInPlace(spectrum, zpLength/2);
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
	  double[] harmonics = new double[DefineConstantsHarmonic_base_freqeuncy_finder.NUMBER_OF_HARMONICS];
	
	
	  /////////////////////////////////////////////////////////
	  // EXACT_POSITION harmonic extraction method
	
	  if (extraction_method == (int)extraction_method.EXACT_POSITION)
	  {
		for (int i =1; i<=DefineConstantsHarmonic_base_freqeuncy_finder.NUMBER_OF_HARMONICS; i++)
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
		for (int i =1; i<=DefineConstantsHarmonic_base_freqeuncy_finder.NUMBER_OF_HARMONICS; i++)
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
}