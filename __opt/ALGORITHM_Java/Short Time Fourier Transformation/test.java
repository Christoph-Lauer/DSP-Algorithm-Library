//*
// * This simple test Program illustrate the usage of the Spectrogram algorithmus.
// 

//--------------------------------------------------------------------------------------

// stl headers

// local headers  

//--------------------------------------------------------------------------------------

import clauer.io.*;
import clauer.math.*;
public class GlobalMembersTest
{
public static void generateWaveFile(RefObject<String> fileName)
{
	generateWaveFile(fileName, false);
}

	//--------------------------------------------------------------------------------------

	//#define TEST_FILE_NAME "../TEST_SIGNALS/asparagus.wav"
	//#define DOWNSCALING_FACTOR 1

	//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: void generateWaveFile(byte* fileName, boolean save = false)
	public static void generateWaveFile(RefObject<String> fileName, boolean save)
	{
	 // open the test signal
	  boolean error = false;
	  int length =0;
	  int sampleRate =0;
	  double signal = clauer.io.WaveFileHandler.autoReadWaveFile(fileName.argvalue, length, sampleRate, error);

		// restrict the time points
	  if (length > 20000)
		  length = 20000;

	  // call the core algorithm

	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from the wave file.");
	  System.out.print("\n");
	  int timePoints = 0;
	  int freqPoints = 0;
	  double[][] stft = ShortTimeFourierTransformation.generateShortTimeFourierTransformation(signal, length, sampleRate, timePoints, freqPoints);

	  // downscaling the time
	  timePoints /= DefineConstantsTest.DOWNSCALING_FACTOR;
	  freqPoints /= DefineConstantsTest.DOWNSCALING_FACTOR;

	  // collect the information string
	  String infoString1 = new String(new char[128]);
	  String.format(infoString1, "Time x Frequency Resolution: %d x %d = %d points", timePoints, freqPoints, timePoints * freqPoints);
	  String infoString2 = new String(new char[128]);
	  String.format(infoString2, "Duration=%gsec. Sample Rate=%d Samples=%d", (double)(length/DefineConstantsTest.DOWNSCALING_FACTOR) / (double)(sampleRate), sampleRate, length/DefineConstantsTest.DOWNSCALING_FACTOR);
		System.out.print(infoString1);
		System.out.print("\n");
		System.out.print(infoString2);
		System.out.print("\n");

	  // plot the result
	  clauer.io.DataPlotter.simple3DPlot (stft, timePoints, freqPoints, 0.0, (double)(length)/(double)(sampleRate), 0.0, sampleRate/2/DefineConstantsTest.DOWNSCALING_FACTOR, save, false, false, "Short-Time-Fourier-Transformation",fileName.argvalue, infoString1, infoString2,"Contour-STFT.png");
	  //clauer::io::DataPlotter::extended3DPlot(stft, timePoints, freqPoints, 0.0, static_cast<double>(length)/static_cast<double>(sampleRate), 0.0, sampleRate/2/DOWNSCALING_FACTOR, save, false, false, "Short-Time-Fourier-Transformation",fileName, infoString1, infoString2,"Surface-STFT.png");

	  // waste the grabage
	  for (int i =0; i<timePoints; i++)
		  stft[i] = null;
	  stft = null;
	  signal = null;
	}

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  *******************************************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the SHORT-TIME-FOURIER-TRANSFORMATION test function ***");
	  System.out.print("\n");
	  System.out.print("  ***                (c) Christoph Lauer Engineering              ***");
	  System.out.print("\n");
	  System.out.print("  *******************************************************************");
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
		DefineConstantsTest.TEST_FILE_NAME = TempRefObject.argvalue;
	  }
	  // report no error outside
	  return(0);
	}
}
  {
	RefObject<String> TempRefObject = new RefObject<String>(DefineConstantsTest.TEST_FILE_NAME);

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