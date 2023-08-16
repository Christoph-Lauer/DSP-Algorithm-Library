public class GlobalMembersTest
{
public static void generateWaveFile(RefObject<String> fileName)
{
	generateWaveFile(fileName, false);
}

	//*
	// * This simple test Program illustrate the usage of the pseudo-margenau-hill-distribution algorithmus.
	// 

	//--------------------------------------------------------------------------------------------------

	// C Language Library heades

	// C++ Language Library headers

	// local headers  

	//--------------------------------------------------------------------------------------------------

	//#define TEST_FILE_NAME "../../TEST_SIGNALS/asparagus.wav"
	//#define DOWNNSCALING_FACTOR 1

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
	  // call the core transformation
	  int resolution = 2048; //clauer::math::Utilities::getNextPowerOfTwo(sampleLength);
	  // downscaling the time
	  length /= DefineConstantsTest.DOWNNSCALING_FACTOR;
	  // call the core algorithm
	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from the wave file and generate ");
	  System.out.print(resolution);
	  System.out.print(" frequency points");
	  System.out.print("\n");
	  double[][] pmhd = clauer.math.PseudoMargenauHillDistribution.calculatePseudoMargenauHillDistribution(signal, length, resolution);
	  // downscaling the frequency resolution
	  resolution /= DefineConstantsTest.DOWNNSCALING_FACTOR;
	  // collect the information string
	  String infoString1 = new String(new char[128]);
	  String.format(infoString1, "Time x Frequency Resolution: %d x %d = %d points", length, resolution/DefineConstantsTest.DOWNNSCALING_FACTOR, length *resolution/DefineConstantsTest.DOWNNSCALING_FACTOR);
	  String infoString2 = new String(new char[128]);
	  String.format(infoString2, "Duration=%gsec. Sample Rate=%d Samples=%d", (double)(length) / (double)(sampleRate), sampleRate, length);
	  // plot the result
	  clauer.io.DataPlotter.simple3DPlot (pmhd, length, resolution, 0.0, (double)(length)/(double)(sampleRate), 0.0, sampleRate/2/DefineConstantsTest.DOWNNSCALING_FACTOR, save, false, false, "Pseudo-Margenau-Hill-Distribution",fileName.argvalue, infoString1, infoString2,"Contour-PMHD.png");
	  //clauer::io::DataPlotter::extended3DPlot(pmhd, length, resolution, 0.0, static_cast<double>(length)/static_cast<double>(sampleRate ), 0.0, sampleRate/2/DOWNNSCALING_FACTOR, save, true , false, "Pseudo-Margenau-Hill-Distribution",fileName, infoString1, infoString2,"Surface-PMHD.png");
	  // waste the grabage
	  for (int i =0; i<length; i++)
		  pmhd[i] = null;
	  pmhd = null;
	  signal = null;
	}

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  *******************************************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the PSEUDO-MARGENAU-HILL-DISTRIBUTION test function ***");
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

//--------------------------------------------------------------------------------------------------

//----------------------------------------------------------------------------------------
//	Copyright � 2006 - 2009 Tangible Software Solutions Inc.
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