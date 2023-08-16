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

	//#define DEFAULT_FILE "../TEST_SIGNALS/harmonicOdd.wav"

	//--------------------------------------------------------------------------------------------------

	// this is the tree definition corresponds to the MEL tree
	public static final int depth = 6;
	public static final int[] tree = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	//--------------------------------------------------------------------------------------------------

	public static void generateWaveFile(RefObject<String> fileName)
	{
	  // open the test signal
	  boolean error = false;
	  int length =0;
	  int sampleRate =0;
	  double signal = clauer.io.WaveFileHandler.autoReadWaveFile(fileName.argvalue, length, sampleRate, error);
	  System.out.print("Load ");
	  System.out.print(length);
	  System.out.print(" samples from File ");
	  System.out.print(fileName.argvalue);
	  System.out.print(" with the Samplerate ");
	  System.out.print(sampleRate);
	  System.out.print("\n");

	  // init the values for the WPT
		int dimFilter;
	  int numChannels;
		int dimTransformed;
	  int dimDescription;
		double transformed;
		double low;
		double high;
		WaveletPacketDecomposition.segmentDescription description;

	  // init the WPT here
	  WaveletPacketDecomposition.SetFilter("daubechies", 2, low, high, TempRefObject);
	  dimFilter = TempRefObject.argvalue;
	  WaveletPacketDecomposition.SetVectors(depth, dimFilter, length, transformed, TempRefObject2, description);
	  dimTransformed = TempRefObject2.argvalue;
	  WaveletPacketDecomposition.setSampleRate(sampleRate);
	  // check for a correct tree
	  if (clauer.math.WaveletPacketDecomposition.CheckTree(tree, depth, numChannels) == 0)
		// call the core algorithm here
		WaveletPacketDecomposition.doWaveletPacketDecomposition(TempRefObject3, length, low, high, dimFilter, tree, depth, TempRefObject4, TempRefObject5, description, TempRefObject6);
		signal = TempRefObject3.argvalue;
		transformed = TempRefObject4.argvalue;
		dimTransformed = TempRefObject5.argvalue;
		dimDescription = TempRefObject6.argvalue;
	  }
	  else
	  {
		System.out.print("ERROR WHILE CHECK THE WAVELET PACKET TREE --> ABORT !!!");
		exit(0);
	  }

	  // because of display reasons multiplicate the result with one million
	  for (int i =0; i<dimDescription; i++)
		description[i].energy *= 1000000.0;

	  // evaluate the result
	  WaveletPacketDecomposition.printDescriptionEnergies(description, dimDescription, depth);

	  // waste the garbage
	  transformed = null;
	  description = null;
	}

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  **************************************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the WAVELET-PACKET-DECOMPOSITION test function ***");
	  System.out.print("\n");
	  System.out.print("  ***             (c) Christoph Lauer Engineering            ***");
	  System.out.print("\n");
	  System.out.print("  **************************************************************");
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
		generateWaveFile(TempRefObject);
		DefineConstantsTest.DEFAULT_FILE = TempRefObject.argvalue;
	  }

	  // report no error outside
	  return(0);
	}
}
  RefObject<Integer> TempRefObject = new RefObject<Integer>(dimFilter);
  RefObject<Integer> TempRefObject2 = new RefObject<Integer>(dimTransformed);
  {
	RefObject<Double> TempRefObject3 = new RefObject<Double>(signal);
	RefObject<Double> TempRefObject4 = new RefObject<Double>(transformed);
	RefObject<Integer> TempRefObject5 = new RefObject<Integer>(dimTransformed);
	RefObject<Integer> TempRefObject6 = new RefObject<Integer>(dimDescription);
  {
	RefObject<String> TempRefObject = new RefObject<String>(DefineConstantsTest.DEFAULT_FILE);

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