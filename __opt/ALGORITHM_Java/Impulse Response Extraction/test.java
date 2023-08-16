public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the impulse response extractor algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------


	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n************************************************************");
	  System.out.print("\n");
	  System.out.print("*** This is the IMPULSE RESPONSE EXTRACTOR test function ***");
	  System.out.print("\n");
	  System.out.print("***            (c) Christoph Lauer Engineering           ***");
	  System.out.print("\n");
	  System.out.print("************************************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // first write a sweep file
	  int length;
	  clauer.math.ImpulseResponseExtraction.generateSineSweep(3.0, 48000, 0.0, 24000.0, 1.0, length, 0.5, false, true);

	  // next write a inverse sweep file
	  clauer.math.ImpulseResponseExtraction.generateSineSweep(3.0, 48000, 0.0, 24000.0, 1.0, length, 0.5, false, true, true);

	  // next write a white noise file
	  clauer.math.ImpulseResponseExtraction.generateWhiteNoise(3.0, 48000, 1.0, length, 0.5, true);

	  // next test the impulse response deconvolution
	  int samplerate;
	  int l1;
	  int l2;
	  boolean error;
	  clauer.io.WaveFileHandler wfh = new clauer.io.WaveFileHandler();
	  double input = wfh.autoReadWaveFile("answer.wav", l1, samplerate, error);
	  double output = wfh.autoReadWaveFile("inverse.wav", l2, samplerate, error);
	  System.out.print("Open the sweep (");
	  System.out.print(l1);
	  System.out.print("samples) and the recorded answer (");
	  System.out.print(l2);
	  System.out.print("samples) file. Start with IR extraction...");
	  System.out.print("\n");
	  double impulseResponse = clauer.math.ImpulseResponseExtraction.deconvolveImpulseResponse(input, output, l2);
	  // finally print the impulse response
	  clauer.io.DataPlotter.simple2DPlot(impulseResponse, 1024);
	  // transform the signal into the frequency domain
	  double[] powerSpectrum = new double[512];
	  clauer.math.FourierTransformation.PowerSpectrum(1024, impulseResponse, powerSpectrum);
	  // plot the spectrum
	  clauer.io.DataPlotter.simple2DPlot(powerSpectrum, 512, 0, samplerate/2);

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