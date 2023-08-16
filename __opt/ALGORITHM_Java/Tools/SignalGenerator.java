public class GlobalMembersSignalGenerator
{

	//*
	// * This simple program generates test singals.
	// 

	//--------------------------------------------------------------------------------------------------

	// C Language Library heades

	// C++ Language Library headers

	// local headers  

	//--------------------------------------------------------------------------------------------------

	// define the basic signal parameters in these macros
	//#define LENGTH 48000
	//#define SAMPLERATE 48000
	//#define EVENT_POINT 24000
	//#define DAMPING 10.0

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // the splash screen 
	  System.out.print("\n  ********************************************");
	  System.out.print("\n");
	  System.out.print("  ***   This is the TEST-SIGNAL-GENERATOR  ***");
	  System.out.print("\n");
	  System.out.print("  ***   (c) Christoph Lauer Engineering    ***");
	  System.out.print("\n");
	  System.out.print("  ********************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // first allocate the test signals
	  double[] signalConst = new double[DefineConstantsSignalGenerator.LENGTH];
	  double[] signalFall = new double[DefineConstantsSignalGenerator.LENGTH];
	  double[] signalRise = new double[DefineConstantsSignalGenerator.LENGTH];
	  double[] signalRiseZero = new double[DefineConstantsSignalGenerator.LENGTH];
	  double[] signalRiseConst = new double[DefineConstantsSignalGenerator.LENGTH];
	  double[] signal100HzDamped = new double[DefineConstantsSignalGenerator.LENGTH];
	  double[] signal1000HzDamped = new double[DefineConstantsSignalGenerator.LENGTH];

	  // next generate the signal data
	  for (int i =0; i<DefineConstantsSignalGenerator.LENGTH; i++)
	  {
		// for the const singal
		signalConst[i] = 1.0;
		// for the fall signal
		signalFall[i] = Math.exp((double)(-i)/(double)(DefineConstantsSignalGenerator.LENGTH)*DefineConstantsSignalGenerator.DAMPING);
		// for the rise signal  
		signalRise[i] = Math.exp((double)(i-DefineConstantsSignalGenerator.LENGTH)/(double)(DefineConstantsSignalGenerator.LENGTH)*DefineConstantsSignalGenerator.DAMPING);
		// for the rise-zero signal
		signalRiseZero[i] = Math.exp((double)(i-DefineConstantsSignalGenerator.EVENT_POINT)/(double)(DefineConstantsSignalGenerator.EVENT_POINT)*DefineConstantsSignalGenerator.DAMPING);
		// for the rise-const signal
		signalRiseConst[i] = Math.exp((double)(i-DefineConstantsSignalGenerator.EVENT_POINT)/(double)(DefineConstantsSignalGenerator.EVENT_POINT)*DefineConstantsSignalGenerator.DAMPING);
		if (i>DefineConstantsSignalGenerator.EVENT_POINT)
		{
		  // for the rise-zero signal
		  signalRiseZero[i] = 0.0;
		  // for the rise-const signal
		  signalRiseConst[i] = 1.0;
		}
		signal100HzDamped[i] = Math.sin((double)(i) * 100.0 / (double)(DefineConstantsSignalGenerator.LENGTH) * 2.0 *DefineConstantsSignalGenerator.PI) * Math.exp((double)(-i)/(double)(DefineConstantsSignalGenerator.LENGTH)*DefineConstantsSignalGenerator.DAMPING);
		signal1000HzDamped[i] = Math.sin((double)(i) * 1000.0 / (double)(DefineConstantsSignalGenerator.LENGTH) * 2.0 *DefineConstantsSignalGenerator.PI) * Math.exp((double)(-i)/(double)(DefineConstantsSignalGenerator.LENGTH)*DefineConstantsSignalGenerator.DAMPING);
	  }

	  // print the generated signals in the data plotter
	  clauer.io.DataPlotter.simple2DPlot(signalConst,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","CONST SIGNAL");
	  clauer.io.DataPlotter.simple2DPlot(signalFall,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","FALL SIGNAL");
	  clauer.io.DataPlotter.simple2DPlot(signalRise,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","RISE SIGNAL");
	  clauer.io.DataPlotter.simple2DPlot(signalRiseZero,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","RISE ZERO SIGNAL");
	  clauer.io.DataPlotter.simple2DPlot(signalRiseConst,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","RISE CONST SIGNAL");
	  clauer.io.DataPlotter.simple2DPlot(signal100HzDamped,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","100Hz DAMPED CONST SIGNAL");
	  clauer.io.DataPlotter.simple2DPlot(signal1000HzDamped,DefineConstantsSignalGenerator.LENGTH,0,DefineConstantsSignalGenerator.LENGTH,-1,1,false,false,false,"Samples","Amplitude","1000HZ DAMPED CONST SIGNAL");

	  // save the signal to files
	  boolean error = false;
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signalConst, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signalConst.wav", error);
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signalFall, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signalFall.wav", error);
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signalRise, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signalRise.wav", error);
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signalRiseZero, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signalRiseZero.wav", error);
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signalRiseConst, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signalRiseConst.wav", error);
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signal100HzDamped, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signal100HzDamped.wav", error);
	  clauer.io.WaveFileHandler.writeMonoFloat64WaveFile(signal1000HzDamped, DefineConstantsSignalGenerator.LENGTH, DefineConstantsSignalGenerator.SAMPLERATE, "signal1000HzDamped.wav", error);

	  // clean the grabage
	  signalConst = null;
	  signalFall = null;
	  signalRise = null;
	  signalRiseZero = null;
	  signalRiseConst = null;

	  // report no error to the outside
	  return(0);
	}
}

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