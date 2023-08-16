//*
// * This simple test Program illustrate the usage of the Harmonic Analyser algorithmus.
// 

//--------------------------------------------------------------------------------------

// stl headers

// local headers  

//--------------------------------------------------------------------------------------

import clauer.io.*;
import clauer.math.*;
public class GlobalMembersTest
{

	//--------------------------------------------------------------------------------------

	//#define TEST_FILE_NAME_1 "../TEST_SIGNALS/harmonicOdd.wav"
	//#define TEST_FILE_NAME_2 "../TEST_SIGNALS/harmonicEven.wav"

	//#define LOWER_SEARCH_BAND_LIMIT 100.0
	//#define UPPER_SEARCH_BAND_LIMIT 2000.0

	//--------------------------------------------------------------------------------------------------

	public static void generateWaveFile(RefObject<String> fileName)
	{
	 // open the test signal
	  boolean error = false;
	  int length =0;
	  int sampleRate =0;
	  double signal = clauer.io.WaveFileHandler.autoReadWaveFile(fileName.argvalue, length, sampleRate, error);

	  // call the core algorithm
	  double firstHarmonic;
	  double firstHarmonicLevel;
	  double[] harmonicsEXA = HarmonicAnalyse.harmonicAnalyse(signal, length, sampleRate, DefineConstantsTest.LOWER_SEARCH_BAND_LIMIT, DefineConstantsTest.UPPER_SEARCH_BAND_LIMIT, firstHarmonic, firstHarmonicLevel);
	  double[] harmonicsBOR = HarmonicAnalyse.harmonicAnalyse(signal, length, sampleRate, DefineConstantsTest.LOWER_SEARCH_BAND_LIMIT, DefineConstantsTest.UPPER_SEARCH_BAND_LIMIT, firstHarmonic, firstHarmonicLevel, clauer.math.HarmonicAnalyse.extraction_method.SEARCH_AROUND_RADIUS);

	  // print the result
	  System.out.print("Search Band = [");
	  System.out.print(DefineConstantsTest.LOWER_SEARCH_BAND_LIMIT);
	  System.out.print(",");
	  System.out.print(DefineConstantsTest.UPPER_SEARCH_BAND_LIMIT);
	  System.out.print("]");
	  System.out.print("\n");
	  System.out.print(fileName.argvalue);
	  System.out.print(" --> FundamentalFrequency= ");
	  System.out.print(firstHarmonic);
	  System.out.print("FundamentalFrequencylevel= ");
	  System.out.print(firstHarmonicLevel);
	  System.out.print("dB");
	  System.out.print("\n");
	  for (int i =0; i<10; i++)
	  {
		double frequency = firstHarmonic * (i+1);
		System.out.print(i+1);
		System.out.print("-th Harmonic = ");
		System.out.print(frequency);
		System.out.print("Hz\t --> EXACT_POSITION:");
		System.out.print(harmonicsEXA[i]);
		System.out.print("dB \tSEARCH_AROUND_RADIUS:");
		System.out.print(harmonicsBOR[i]);
		System.out.print("dB");
		System.out.print("\n");
	  }
	  // waste the grabage
	  // delete[] harmonicsEXA;
	  // delete[] harmonicsBOR;
	}

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  ***************************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the HARMONIC-ANALYSER test function ***");
	  System.out.print("\n");
	  System.out.print("  ***     (c) Christoph Lauer Engineering         ***");
	  System.out.print("\n");
	  System.out.print("  ***************************************************");
	  System.out.print("\n");
	  System.out.print("\n");
	  // check if there was any file given as argument
	  if (argc >= 2)
		if(argc == 3 && strcmp("save", argv.argvalue[2])==0)
			generateWaveFile(argv.argvalue[1]);
		else
		  for (int i =1; i<argc; i++)
			  generateWaveFile(argv.argvalue[i]);
	  else
	  {
		System.out.print("ODD (UNGEADE)");
		System.out.print("\n");
		generateWaveFile(TempRefObject);
		DefineConstantsTest.TEST_FILE_NAME_1 = TempRefObject.argvalue;
		System.out.print("\n");
		System.out.print("EVEN (GERADE)");
		System.out.print("\n");
		generateWaveFile(TempRefObject2);
		DefineConstantsTest.TEST_FILE_NAME_2 = TempRefObject2.argvalue;
	  }

	  // report no error outside
	  return(0);
	}
}
	RefObject<String> TempRefObject = new RefObject<String>(DefineConstantsTest.TEST_FILE_NAME_1);
	RefObject<String> TempRefObject2 = new RefObject<String>(DefineConstantsTest.TEST_FILE_NAME_2);

//--------------------------------------------------------------------------------------

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