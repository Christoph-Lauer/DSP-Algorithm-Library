public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the Linear Predicitve Coding algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// Input/Output Stream Library headers

	// C Language Library headers

	// Local headers  

	//--------------------------------------------------------------------------------------

	// for the prediction error test
	//#define SIGNAL_LENGTH 48000
	//#define LPC_COEFFICIENTS 100
	//#define FUTURE_SAMPLES 10000

	// for the lpc spectrum test
	//#define LPC_SPEC_COEFFS 16
	//#define SPEC_SIZE 2048

	// for the prediction error vector test
	//#define LPC_ERROR_VECTOR_SIZE 48000
	//#define LPC_ERROR_VECTOR_SAMPLE_RATE 48000
	//#define LPC_ERROR_VECTOR_COEFFS 120
	//#define LPC_ERROR_VECTOR_PREVIOUS 20.0
	//#define LPC_ERROR_VECTOR_FUTURE 10.0
	//#define LPC_ERROR_VECTOR_WINSHIFT 1.0

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	//  
	//  /////////////////////////////////////////////////
	//  // the first test should be the prediction error test
	//  cout << endl << "(1) Test the LPC-Error..." << endl;
	//  // first generate some time domain based signal
	//  double* signal = new double[SIGNAL_LENGTH];
	//  // init the signal with some harmonic row
	//  for (int i=0; i<SIGNAL_LENGTH; i++)
	//  {
	//    signal[i] =   sin(static_cast<double>(i)) + sin(static_cast<double>(i/2))
	//                + sin(static_cast<double>(i/4)) + sin(static_cast<double>(i/6))
	//                + sin(static_cast<double>(i*4)) + sin(static_cast<double>(i*8));
	//  }
	//  // then get an instance of the predictor
	//  double error = clauer::math::LinearPredictiveCoding::calcPredictionErrorInPercent(signal, SIGNAL_LENGTH/2, LPC_COEFFICIENTS, FUTURE_SAMPLES);
	//  // give out the result
	//  cout << "LPC Prediction Error: " << error << "%" << endl << endl;
	//
	//  /////////////////////////////////////////////////
	//  // second test should be the lpc spectrum
	//  cout << "(2) Test the LPC-Spectrum" << endl;
	//  for (int i=0; i<SIGNAL_LENGTH; i++)
	//  {
	//    signal[i] = sin(static_cast<double>(i)*M_PI_2*0.25);
	//  }
	//  // do the core transformation
	//  double* lpcSpec_notWindowed = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(signal, SIGNAL_LENGTH, LPC_SPEC_COEFFS, SPEC_SIZE, true, false);
	//  double* lpcSpec_windowed = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(signal, SIGNAL_LENGTH, LPC_SPEC_COEFFS, SPEC_SIZE, true, true);
	//  // give out the result spectrum 
	//  for (int i=0; i<SPEC_SIZE; i++)
	//    cout << "LPC-Spectrum[" << i << "]" << " -->\t" << lpcSpec_notWindowed[i] << "dB(unwindowed)\t" << lpcSpec_windowed[i] << "dB(windowed)" << endl;
	//  cout << endl;
	//  // print the result
	//  clauer::io::DataPlotter::simple2DPlot(lpcSpec_notWindowed, SPEC_SIZE);
	//  clauer::io::DataPlotter::simple2DPlot(lpcSpec_windowed, SPEC_SIZE);
	//  
	//  // open the wave file
	//  int length = 0;
	//  int sampleRate = 0;
	//  bool fileError = false;
	//  double* fileSamples = clauer::io::WaveFileHandler::autoReadWaveFile("../TEST_SIGNALS/white_noise_band_pass_6000_18000.wav", length, sampleRate, fileError);
	//  // do the core transformation
	//  lpcSpec_notWindowed = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(fileSamples, length, LPC_SPEC_COEFFS, SPEC_SIZE, true, false);
	//  lpcSpec_windowed = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(fileSamples, length, LPC_SPEC_COEFFS, SPEC_SIZE, true, true);
	//  // print the result
	//  clauer::io::DataPlotter::simple2DPlot(lpcSpec_notWindowed, SPEC_SIZE, 0,0,0,0,false,false,false,"white_noise_band_pass_6000_18000.wav", "NOT WINDOWED");
	//  clauer::io::DataPlotter::simple2DPlot(lpcSpec_windowed, SPEC_SIZE,0,0,0,0,false,false,false,"white_noise_band_pass_6000_18000.wav","WINDOWED");
	//
	//  // open a wave file
	//  fileSamples = clauer::io::WaveFileHandler::autoReadWaveFile("../TEST_SIGNALS/lamy_pen.wav", length, sampleRate, fileError);
	//  // do the core transformation
	//  lpcSpec_notWindowed = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(fileSamples, length, LPC_SPEC_COEFFS, SPEC_SIZE, true, false);
	//  lpcSpec_windowed = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(fileSamples, length, LPC_SPEC_COEFFS*4, SPEC_SIZE, true, true);
	//  // print the result
	//  clauer::io::DataPlotter::simple2DPlot(lpcSpec_notWindowed, SPEC_SIZE, 0,0,0,0,false,false,false,"lamy_pen.wav", "NOT WINDOWED");
	//  clauer::io::DataPlotter::simple2DPlot(lpcSpec_windowed, SPEC_SIZE,0,0,0,0,false,false,false,"lamy_pen.wav","NOT WINDOWED, FOR TIMES COEFFICIENTS ");
	//  delete[] signal;
	//  

	  /////////////////////////////////////////////////////////
	  // finally test the LPC-Error vector generation
	  System.out.print("(3) Test the LPC-Error vector generation");
	  System.out.print("\n");

	  // open the wave file
	  int length = 0;
	  int sampleRate = 0;
	  boolean fileError = false;
	  double fileSamples = clauer.io.WaveFileHandler.autoReadWaveFile("../TEST_SIGNALS/scratch.wav", length, sampleRate, fileError);

	  // generate the prediction error 
	  int n;
	  double out = clauer.math.LinearPredictiveCoding.calcPredictionErrorVectorInPercent (fileSamples, length, DefineConstantsTest.LPC_ERROR_VECTOR_COEFFS, sampleRate, DefineConstantsTest.LPC_ERROR_VECTOR_PREVIOUS, DefineConstantsTest.LPC_ERROR_VECTOR_FUTURE, DefineConstantsTest.LPC_ERROR_VECTOR_WINSHIFT, n);
	  System.out.print("Generate ");
	  System.out.print(n);
	  System.out.print("error points");
	  System.out.print("\n");
	  // give out the result spectrum	
	  clauer.io.DataPlotter.simple2DPlot(out,n);


	  // give no error back
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