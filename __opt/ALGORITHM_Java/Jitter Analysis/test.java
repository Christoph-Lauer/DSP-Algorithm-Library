public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the Jitter Analysis algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// Local headers  

	// Input/Output Stream Library headers

	//--------------------------------------------------------------------------------------

	// macro definitions for the jitter analysis
	//#define PATH "../TEST_SIGNALS/small_gear_nio.wav"
	//#define LPC_PREVIOUS 1000
	//#define LPC_SPEC_SIZE 8192
	//#define LPC_WIN_SHIFT 500
	//#define LPC_COEFFS 24
	//#define LOWER_BAND 3000
	//#define UPPER_BAND 3600

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo Jitter Analysis");
	  System.out.print("\n");

	  // first read an wave file and store the vector
	  int length;
	  int sampleRate;
	  boolean error = false;
	  clauer.io.WaveFileHandler wfh = new clauer.io.WaveFileHandler();
	  short read = wfh.readMonoPcm16WaveFile(length, sampleRate, DefineConstantsTest.PATH, error);
	  // check if the file could redulary opened
	  if (error == true)
	  {
		System.out.print("Could not open the wave file, break here...");
		System.out.print("\n");
		exit(127);
	  }
	  // cast the array to an double array
	  double[] samples = new double[length];
	  for (int i =0; i<length; i++)
		samples[i] = (double)(read[i]);
	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from file ");
	  System.out.print(DefineConstantsTest.PATH);
	  System.out.print("\n");
	  read = null;

	  // now call the jitter routine
	  int n;
	  System.out.print("Start two analysis...");
	  System.out.print("\n");
	  double[] jitterDiff = clauer.math.JitterAnalysis.jitterAnalysis(samples, length, DefineConstantsTest.LPC_PREVIOUS, DefineConstantsTest.LPC_SPEC_SIZE, DefineConstantsTest.LPC_WIN_SHIFT, DefineConstantsTest.LPC_COEFFS, sampleRate, DefineConstantsTest.LOWER_BAND, DefineConstantsTest.UPPER_BAND, n, false);
	  System.out.print("FIRST Analysis finished...");
	  System.out.print("\n");
	  double[] jitterPeak = clauer.math.JitterAnalysis.jitterAnalysis(samples, length, DefineConstantsTest.LPC_PREVIOUS, DefineConstantsTest.LPC_SPEC_SIZE, DefineConstantsTest.LPC_WIN_SHIFT, DefineConstantsTest.LPC_COEFFS, sampleRate, DefineConstantsTest.LOWER_BAND, DefineConstantsTest.UPPER_BAND, n, true);
	  System.out.print("SECOND Analysis finished...");
	  System.out.print("\n");

	  // now give the result out
	  for(int i =0; i<n; i++)
	  {
		System.out.print("Jitter [");
		System.out.print(i);
		System.out.print("] --> DIFF: ");
		System.out.print(jitterDiff[i]);
		System.out.print("        \tPEAK: ");
		System.out.print(jitterPeak[i]);
		System.out.print("\n");
	  }


	  // no error occured
	  return (0);
	}
}

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