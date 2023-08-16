public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the envelope algorithmus.
	// * NOTE: We did no take care on any memory leaks here !!!
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define TEST_SIGNAL_LENGTH 500
	//C++ TO JAVA CONVERTER NOTE: The following #define macro was replaced in-line:
	//#define PRINT_RESULT true

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // first generate a test signals
	  double[] twoPeakSignal = new double[DefineConstantsTest.TEST_SIGNAL_LENGTH];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(twoPeakSignal, 0, DefineConstantsTest.TEST_SIGNAL_LENGTH * sizeof(double));
	  twoPeakSignal[DefineConstantsTest.TEST_SIGNAL_LENGTH * 1 / 4] = 1.0;
	  twoPeakSignal[DefineConstantsTest.TEST_SIGNAL_LENGTH * 2 / 4] = 1.0;
	  twoPeakSignal[DefineConstantsTest.TEST_SIGNAL_LENGTH * 3 / 4] = 1.0;
	  double[] sinPeriodSignal = new double[DefineConstantsTest.TEST_SIGNAL_LENGTH];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(sinPeriodSignal, 0, DefineConstantsTest.TEST_SIGNAL_LENGTH * sizeof(double));
	  for (int i =DefineConstantsTest.TEST_SIGNAL_LENGTH/4; i<DefineConstantsTest.TEST_SIGNAL_LENGTH/2; i++)
		sinPeriodSignal[i] = Math.sin((double)(i));
	  double[] sinSignal = new double[DefineConstantsTest.TEST_SIGNAL_LENGTH];
	  double[] sincSignal = new double[DefineConstantsTest.TEST_SIGNAL_LENGTH];
	  for (int i =0; i<DefineConstantsTest.TEST_SIGNAL_LENGTH; i++)
	  {
		sinSignal[i] = Math.sin((double)(i)/10);
		if (i == DefineConstantsTest.TEST_SIGNAL_LENGTH/4)
		  sincSignal[i] = 1.0;
		else
		  sincSignal[i] = Math.sin((double)(i-DefineConstantsTest.TEST_SIGNAL_LENGTH/4))/(double)(i-DefineConstantsTest.TEST_SIGNAL_LENGTH/4);
	  }

	  // splash screen 
	  System.out.print("\n******************************************");
	  System.out.print("\n");
	  System.out.print("*** This is the ENVELOPE test function ***");
	  System.out.print("\n");
	  System.out.print("***  (c) Christoph Lauer Engineering   ***");
	  System.out.print("\n");
	  System.out.print("******************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // check if there was any file given as argument
	  if (argc > 1)
	  {
		  boolean error = false;
		  int sampleLength = 0;
		  int sampleRate = 0;
		  double fileSamples = clauer.io.WaveFileHandler.autoReadWaveFile(argv.argvalue[1], sampleLength, sampleRate, error);
		  double envelopeMAX = clauer.math.Envelope.calculateEnvelope(fileSamples, sampleLength, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
		  double envelopeFIR = clauer.math.Envelope.calculateEnvelope(fileSamples, sampleLength, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
		  clauer.io.DataPlotter.simple2DPlot(fileSamples, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Time Signal", argv.argvalue[1]);
		  clauer.io.DataPlotter.simple2DPlot(envelopeMAX, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX-Method", argv.argvalue[1]);
		  clauer.io.DataPlotter.simple2DPlot(envelopeFIR, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR-Method", argv.argvalue[1]);
	  }

	  // if no file was given as argument do the stadart test's
	  else
	  {
		  // first test the hilbert transformation
		  double hilbert = clauer.math.Envelope.fastHilbertTransform(sincSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH);
		  clauer.io.DataPlotter.simple2DPlot(sincSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","SINC Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(hilbert, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation");
		  double hilbertBack = clauer.math.Envelope.fastHilbertTransform(hilbert, DefineConstantsTest.TEST_SIGNAL_LENGTH);
		  clauer.io.DataPlotter.simple2DPlot(hilbertBack, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Back Transformation");
		  hilbert = clauer.math.Envelope.fastHilbertTransform(sinSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH);
		  clauer.io.DataPlotter.simple2DPlot(sinSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","SIN Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(hilbert, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation");
		  hilbertBack = clauer.math.Envelope.fastHilbertTransform(hilbert, DefineConstantsTest.TEST_SIGNAL_LENGTH);
		  clauer.io.DataPlotter.simple2DPlot(hilbertBack, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Back Transformation");

		  // next test with the two peaks test signal
		  System.out.print("FIRST THE TWO PEAK SIGNAL\n");
		  double envelopeFIR = clauer.math.Envelope.calculateEnvelope(twoPeakSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
		  double envelopeMAX = clauer.math.Envelope.calculateEnvelope(twoPeakSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
		  hilbert = clauer.math.Envelope.fastHilbertTransform(twoPeakSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH);
		  clauer.io.DataPlotter.simple2DPlot(twoPeakSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(hilbert, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation of the Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(envelopeMAX, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX");
		  clauer.io.DataPlotter.simple2DPlot(envelopeFIR, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR");

		  // nest test is the sin period signal
		  System.out.print("\nNEXT IS THE SINE PERIOD SIGNAL\n");
		  envelopeFIR = clauer.math.Envelope.calculateEnvelope(sinPeriodSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
		  envelopeMAX = clauer.math.Envelope.calculateEnvelope(sinPeriodSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
		  hilbert = clauer.math.Envelope.fastHilbertTransform(sinPeriodSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH);
		  // plot the resullt
		  clauer.io.DataPlotter.simple2DPlot(sinPeriodSignal, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(hilbert, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation of the Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(envelopeMAX, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX");
		  clauer.io.DataPlotter.simple2DPlot(envelopeFIR, DefineConstantsTest.TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR");

		  // read the samples from the wave file
		  clauer.io.WaveFileHandler wfh = new clauer.io.WaveFileHandler();
		  boolean error = false;
		  int sampleLength = 0;
		  int sampleRate = 0;
		  double fileSamples = wfh.autoReadWaveFile("../TEST_SIGNALS/resonance_analysis_test.wav", sampleLength, sampleRate, error);
		  envelopeFIR = clauer.math.Envelope.calculateEnvelope(fileSamples, sampleLength, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
		  envelopeMAX = clauer.math.Envelope.calculateEnvelope(fileSamples, sampleLength, clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
		  // // plot the resullt
		  clauer.io.DataPlotter.simple2DPlot(fileSamples, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Input Signal");
		  clauer.io.DataPlotter.simple2DPlot(envelopeMAX, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX");
		  clauer.io.DataPlotter.simple2DPlot(envelopeFIR, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR");
	  }
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