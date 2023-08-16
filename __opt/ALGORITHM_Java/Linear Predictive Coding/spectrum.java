//*
// * This simple test Program illustrate the usage of the LPC algorithmus.
// 

//--------------------------------------------------------------------------------------

// stl headers

// local headers  

//--------------------------------------------------------------------------------------

import clauer.io.*;
import clauer.math.*;
public class GlobalMembersSpectrum
{
public static void generateWaveFile(RefObject<String> fileName)
{
	generateWaveFile(fileName, false);
}

	//--------------------------------------------------------------------------------------

	// lpc spectrum values
	//#define LPC_SPEC_COEFFS 16
	//#define SPEC_SIZE 2048

	//--------------------------------------------------------------------------------------

	//#define TEST_FILE_NAME "../TEST_SIGNALS/demo.wav"

	//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: void generateWaveFile(byte* fileName, boolean save = false)
	public static void generateWaveFile(RefObject<String> fileName, boolean save)
	{

	  // open the wave file
	  int length = 0;
	  int sampleRate = 0;
	  boolean fileError = false;
	  double fileSamples = clauer.io.WaveFileHandler.autoReadWaveFile(fileName.argvalue, length, sampleRate, fileError);

	  // call the core algorithm
	  double lpcSpec = clauer.math.LinearPredictiveCoding.calcualteLpcPowerSpectrumEnvelope(fileSamples, length, DefineConstantsSpectrum.LPC_SPEC_COEFFS, DefineConstantsSpectrum.SPEC_SIZE, true, false);

	  // collect the information string
	  String infoString1 = new String(new char[128]);
	  String.format(infoString1, "Signal-Length = %d, LPC-Length = %d, ,LPC-Coefficients = %d, SampleRate = %d", length, DefineConstantsSpectrum.SPEC_SIZE, DefineConstantsSpectrum.LPC_SPEC_COEFFS, sampleRate);
	  System.out.print(infoString1);
	  System.out.print("\n");

	  // plot the result
	  clauer.io.DataPlotter.simple2DPlot(lpcSpec, DefineConstantsSpectrum.SPEC_SIZE, 0,sampleRate/2,0,0,false,false,false,"Frequency in Hz", "Amplitude in dB","LPC-POWER-SPECTRUM", infoString1, fileName.argvalue);

	}

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  ********************************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the FOURIER-TRANSFORMATION test function ***");
	  System.out.print("\n");
	  System.out.print("  ***       (c) Christoph Lauer Engineering            ***");
	  System.out.print("\n");
	  System.out.print("  ********************************************************");
	  System.out.print("\n");
	  System.out.print("\n");
	  // check if there was any file given as argument
	  if (argc >= 2)
		if(argc == 3 && strcmp("save", argv.argvalue[2])==0)
			generateWaveFile(argv.argvalue[1], true);
		else
		  for (int i =1; i<argc; i++)
			  generateWaveFile(argv.argvalue[i]);
	  else
		generateWaveFile(TempRefObject);
		DefineConstantsSpectrum.TEST_FILE_NAME = TempRefObject.argvalue;
	  }
	  // report no error outside
	  return(0);
	}
}
  {
	RefObject<String> TempRefObject = new RefObject<String>(DefineConstantsSpectrum.TEST_FILE_NAME);

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