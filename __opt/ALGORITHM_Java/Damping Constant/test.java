public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrate the usage of the damping constant algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define WAVE_FILE_NAME_1 "../TEST_SIGNALS/envelope_IO_48000.wav"
	//#define WAVE_FILE_NAME_2 "../TEST_SIGNALS/envelope_IO_8000.wav"
	//#define WAVE_FILE_NAME_3 "../TEST_SIGNALS/envelope_NIO_48000.wav"
	//#define WAVE_FILE_NAME_4 "../TEST_SIGNALS/envelope_NIO_8000.wav"

	//--------------------------------------------------------------------------------------

	public static void analyseWaveFile(RefObject<String> fileName)
	{
	  // open the first IO file
	  clauer.io.WaveFileHandler wfh = new clauer.io.WaveFileHandler();
	  boolean error = false;
	  int sampleLength = 0;
	  int sampleRate = 0;
	  double samples = clauer.io.WaveFileHandler.autoReadWaveFile(fileName.argvalue, sampleLength, sampleRate, error);

	  // now call the damping factor function
	  double dampingConstant;
	  double amplitude;
	  double peakPosition;
	  clauer.math.DampingConstant.proposeDampingConstant(samples, sampleLength, sampleRate, dampingConstant, amplitude, peakPosition);

	  // call the envelope function for the plot
	  double envelope = clauer.math.Envelope.calculateEnvelope(samples, sampleLength, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);

	  // print the result string
	  String infoString = new String(new char[128]);
	  String.format(infoString,"DampingConstant=%g 1/s, Amplitude=%g, PeakPosition=%gs", dampingConstant, amplitude, peakPosition);
	  System.out.print(fileName.argvalue);
	  System.out.print(" --> ");
	  System.out.print(infoString);
	  System.out.print("\n");

	  // draw the plots
	  clauer.io.DataPlotter.simple2DPlot(samples, sampleLength,0,0,0,0,false,false,false,"Samples","Amplitude","INPUT SIGNAL", fileName.argvalue);
	  clauer.io.DataPlotter.simple2DPlot(envelope, sampleLength,0,0,0,0,false,false,false,"Samples","Amplitude","ENVELOPE",fileName.argvalue, infoString);

	  // waste the grabage
	  envelope = null;
	}

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n**************************************************");
	  System.out.print("\n");
	  System.out.print("*** This is the DAMPING CONSTANT test function ***");
	  System.out.print("\n");
	  System.out.print("***    (c) Christoph Lauer ENGINEERING         ***");
	  System.out.print("\n");
	  System.out.print("**************************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // check if there was any argument given to open and analysze
	  if (argc > 1)
	  {
		for (int i =2; i<=argc; i++)
		  analyseWaveFile(argv.argvalue[i-1]);
		return(0);
	  }
	  // doing the standart test here if no argument was given
	  else
	  {
		analyseWaveFile(TempRefObject);
		DefineConstantsTest.WAVE_FILE_NAME_1 = TempRefObject.argvalue;
		analyseWaveFile(TempRefObject2);
		DefineConstantsTest.WAVE_FILE_NAME_2 = TempRefObject2.argvalue;
		analyseWaveFile(TempRefObject3);
		DefineConstantsTest.WAVE_FILE_NAME_3 = TempRefObject3.argvalue;
		analyseWaveFile(TempRefObject4);
		DefineConstantsTest.WAVE_FILE_NAME_4 = TempRefObject4.argvalue;
	  }
	  return(0);
	}
}
	RefObject<String> TempRefObject = new RefObject<String>(DefineConstantsTest.WAVE_FILE_NAME_1);
	RefObject<String> TempRefObject2 = new RefObject<String>(DefineConstantsTest.WAVE_FILE_NAME_2);
	RefObject<String> TempRefObject3 = new RefObject<String>(DefineConstantsTest.WAVE_FILE_NAME_3);
	RefObject<String> TempRefObject4 = new RefObject<String>(DefineConstantsTest.WAVE_FILE_NAME_4);

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