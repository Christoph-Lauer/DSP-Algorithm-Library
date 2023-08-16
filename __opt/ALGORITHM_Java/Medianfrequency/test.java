public class GlobalMembersTest
{
public static void analyseWaveFile(RefObject<String> filePath)
{
	analyseWaveFile(filePath, false);
}

	//*
	// * This simple test Program illustrates the usage of the median frequency algorithmus.
	// 

	//--------------------------------------------------------------------------------------------------

	// C Language Library heades

	// C++ Language Library headers

	// local headers  

	//--------------------------------------------------------------------------------------------------

	//#define TEST_FILE "../TEST_SIGNALS/white_noise_band_pass_800_1200.wav"

	//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: void analyseWaveFile(byte* filePath, boolean save = false)
	public static void analyseWaveFile(RefObject<String> filePath, boolean save)
	{
	 // open the test signal
	  boolean error = false;
	  int length =0;
	  int sampleRate =0;
	  double signal = clauer.io.WaveFileHandler.autoReadWaveFile(filePath.argvalue, length, sampleRate, error);
	  // call the algorithm
	  double medianFrequency = clauer.math.MedianFrequency.calcualteMedianFrequencyFromTimeDomainSignal(signal, length, sampleRate);
	  // give the result out
	  byte fileName = clauer.math.Utilities.extractFileNameFromPath(filePath.argvalue);
	  System.out.print("The MEDIAN_FREQUENCY for the file ");
	  System.out.print(fileName);
	  System.out.print(" is ");
	  System.out.print(medianFrequency);
	  System.out.print(" Hz");
	  System.out.print("\n");
	  // waste the grabage
	  signal = null;
	}

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  **************************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the MEDIAN-FERQUENCY test function ***");
	  System.out.print("\n");
	  System.out.print("  ***      (c) Christoph Lauer Engineering        ***");
	  System.out.print("\n");
	  System.out.print("  **************************************************");
	  System.out.print("\n");
	  System.out.print("\n");
	  // check if there was any file given as argument
	  if (argc >= 2)
		for (int i =1; i<argc; i++)
			analyseWaveFile(argv.argvalue[i]);
	  else
		analyseWaveFile(TempRefObject);
		DefineConstantsTest.TEST_FILE = TempRefObject.argvalue;
	  }
	  // report no error to the outside
	  return(0);
	}
}
  {
	RefObject<String> TempRefObject = new RefObject<String>(DefineConstantsTest.TEST_FILE);

//--------------------------------------------------------------------------------------------------

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