public class GlobalMembersTest
{

	//*
	// * This simple test function illustrates the suage of the sample rate converter.
	// * USAGE OF THE RESAMPLER IS VERY EASY:
	// * first get an instance of the resampler with:
	// * clauer::math::Resampler resampler;
	// * then start the convertion routine with:
	// * resampler.doResampling(in, out1, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_LINEAR);
	// * thats all...
	// 

	//------------------------------------------------------------------------------------------------

	// Stl headers

	// Loacal haders

	// length of the SR convertion
	//#define LENGTH 1000000

	//------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("(c) clauer Resampling test Routine...");
	  System.out.print("\n");
	  System.out.print("First read the test sample wave file.");
	  System.out.print("\n");

	  ///////////////////////////////////////////////////////////////////////////
	  // first read the samples from test sweep file
	  clauer.io.WaveFileHandler wfh = new clauer.io.WaveFileHandler();
	  int length;
	  int sampleRate;
	  boolean error = false;
	  short samples = wfh.readMonoPcm16WaveFile(length, sampleRate, "../TEST_SIGNALS/log_sweep_2s.wav", error);
	  if (error == true)
	  {
		System.out.print("ERROR while read the wave file !!!");
		System.out.print("\n");
		exit(0);
	  }
	  else
		{
		System.out.print("Read the wave file for the test conversion with length ");
		System.out.print(length);
		System.out.print(" and sample-rate ");
		System.out.print(sampleRate);
		System.out.print("\n");
		}
	  // konvert the samples to an float array 
	  _float[] fin = new _float[length];
	  _float[] fout = new _float[100000000];
	  // cast the samples to an floating point array
	  System.out.print("LÄNGE:");
	  System.out.print(length);
	  System.out.print("\n");
	  for (int i =0; i<length; i++)
		fin[i] = (_float)(samples[i]);

	  ////////////////////////////////////////////////////////////////////////////
	  // (1) Convert throught the whole spectrum of sample rates and store the wave files
	  System.out.print("\n");
	  System.out.print("(1) For the first go throught the whole spectrum of possible sample rates in the converter can convert");
	  System.out.print("\n");
	  for (int i =250; i<=512000; i*=2)
	  {
		// first call the resampler
		clauer.math.Resampler resampler = new clauer.math.Resampler();
		int newLength = (int)((double)length * (double)i / (double)sampleRate);
		System.out.print("NewLength=");
		System.out.print(newLength);
		System.out.print(" Length=");
		System.out.print(length);
		System.out.print(" OldSampleRate=");
		System.out.print(sampleRate);
		System.out.print(" NewSampleRate=");
		System.out.print(i);
		System.out.print(" I=");
		System.out.print(i);
		System.out.print("\n");
		resampler.doResampling(fin, TempRefObject, length, newLength, clauer.math.Resampler.SRC_SINC_BEST_QUALITY);
		fout = TempRefObject.argvalue;
		// then build the file name
		String fileName = "results/SRC_";
		String buf = new String(new char[8]);
		String.format(buf, "%d", i);
		fileName += buf;
		fileName += ".wav";
		// cast the result and save the wave file
		short[] samples = new short[newLength];
		for (int j =0; j<newLength; j++)
		  samples[j] = (short)fout[j];
		wfh.writeMonoPcm16WaveFile(samples, newLength, i, fileName.c_str(), error);
	  }

	  ////////////////////////////////////////////////////////////////////////////
	  // (2) Second testDOWNSAMPLES with the five different methods and stores the result into wave files
	  System.out.print("\n");
	  System.out.print("(2) The second test is DOWNSAMPLING test where the results are stored into wave files");
	  System.out.print("\n");
	  System.out.print("Pointer to the array: ");
	  System.out.print(samples);
	  System.out.print("\n");
	  clauer.math.Resampler resampler = new clauer.math.Resampler();
	  // then resample the vector to the half size
	  resampler.doResampling(fin, TempRefObject2, length, length/2, clauer.math.Resampler.SRC_LINEAR);
	  fout = TempRefObject2.argvalue;
	  // cast the result back 
	  for (int i =0; i<length/2; i++)
		samples[i] = (short)fout[i];
	  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_LINEAR.wav", error);
	  resampler.doResampling(fin, TempRefObject3, length, length/2, clauer.math.Resampler.SRC_ZERO_ORDER_HOLD);
	  fout = TempRefObject3.argvalue;
	  // cast the result back 
	  for (int i =0; i<length/2; i++)
		samples[i] = (short)fout[i];
	  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_ZERO_ORDER_HOLD.wav", error);
	  resampler.doResampling(fin, TempRefObject4, length, length/2, clauer.math.Resampler.SRC_SINC_FASTEST);
	  fout = TempRefObject4.argvalue;
	  // cast the result back 
	  for (int i =0; i<length/2; i++)
		samples[i] = (short)fout[i];
	  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_SINC_FASTEST.wav", error);
	  resampler.doResampling(fin, TempRefObject5, length, length/2, clauer.math.Resampler.SRC_SINC_MEDIUM_QUALITY);
	  fout = TempRefObject5.argvalue;
	  // cast the result back 
	  for (int i =0; i<length/2; i++)
		samples[i] = (short)fout[i];
	  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_SINC_MEDIUM_QUALITY.wav", error);
	  resampler.doResampling(fin, TempRefObject6, length, length/2, clauer.math.Resampler.SRC_SINC_BEST_QUALITY);
	  fout = TempRefObject6.argvalue;
	  // cast the result back 
	  for (int i =0; i<length/2; i++)
		samples[i] = (short)fout[i];
	  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_SINC_BEST_QUALITY.wav", error);
	  //exit(0);

	  //////////////////////////////////////////////////////////////////////////////////////////////////  
	  // (3) The last test DOWN and UPSAMPLES and measures the difference between the converter methods
	  System.out.print("\n");
	  System.out.print("(3) The third and last test measures the differences between the different methods");
	  System.out.print("\n");
	  double[] in = new double[DefineConstantsTest.LENGTH];
	  double[] out1 = new double[2 *DefineConstantsTest.LENGTH];
	  double[] out2 = new double[2 *DefineConstantsTest.LENGTH];
	  double[] out3 = new double[2 *DefineConstantsTest.LENGTH];
	  double[] out4 = new double[2 *DefineConstantsTest.LENGTH];
	  double[] out5 = new double[2 *DefineConstantsTest.LENGTH];
	  for (int i =0; i<DefineConstantsTest.LENGTH; i++)
		in[i] = Math.sin((double)(i));
	  System.out.print("Now some speed test's with the different interpolation methods:");
	  System.out.print("\n");
	  System.out.print("SRC_LINEAR...");
	  resampler.doResampling(in, TempRefObject7, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH *2, clauer.math.Resampler.SRC_LINEAR);
	  out1 = TempRefObject7.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_ZERO_ORDER_HOLD...");
	  resampler.doResampling(in, TempRefObject8, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH *2, clauer.math.Resampler.SRC_ZERO_ORDER_HOLD);
	  out2 = TempRefObject8.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_SINC_FASTEST...");
	  resampler.doResampling(in, TempRefObject9, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH *2, clauer.math.Resampler.SRC_SINC_FASTEST);
	  out3 = TempRefObject9.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_SINC_MEDIUM_QUALITY...");
	  resampler.doResampling(in, TempRefObject10, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH *2, clauer.math.Resampler.SRC_SINC_MEDIUM_QUALITY);
	  out4 = TempRefObject10.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_SINC_BEST_QUALITY...");
	  resampler.doResampling(in, TempRefObject11, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH *2, clauer.math.Resampler.SRC_SINC_BEST_QUALITY);
	  out5 = TempRefObject11.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("\n");
	  // then measure the deltas between the results
	  double delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH *2; i++)
		delta += Math.abs(out1[i] - out5[i]);
	  System.out.print("The overall delta between SRC_LINEAR and SRC_SINC_BEST_QUALITY: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH *2; i++)
		delta += Math.abs(out3[i] - out5[i]);
	  System.out.print("The overall delta between SRC_SINC_LOW_QUALITY and SRC_SINC_BEST_QUALITY: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH *2; i++)
		delta += Math.abs(out3[i] - out2[i]);
	  System.out.print("The overall delta between SRC_SINC_LOW_QUALITY and SRC_ZERO_ORDER_HOLD: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  for (int i =0; i<DefineConstantsTest.LENGTH *2; i++)
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH *2; i++)
		delta += Math.abs(out1[i] - out2[i]);
	  System.out.print("The overall delta between SRC_LINEAR and SRC_ZERO_ORDER_HOLD: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  // then using different resampling methods while downsampling
	  System.out.print("Starting DOWN-sampling to the double sampling rate with ");
	  System.out.print(DefineConstantsTest.LENGTH);
	  System.out.print(" Samples");
	  System.out.print("\n");
	  System.out.print("SRC_LINEAR...");
	  System.out.print(std.cout.flush());
	  resampler.doResampling(in, TempRefObject12, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH/2, clauer.math.Resampler.SRC_LINEAR);
	  out1 = TempRefObject12.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_ZERO_ORDER_HOLD...");
	  System.out.print(std.cout.flush());
	  resampler.doResampling(in, TempRefObject13, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH/2, clauer.math.Resampler.SRC_ZERO_ORDER_HOLD);
	  out2 = TempRefObject13.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_SINC_FASTEST...");
	  System.out.print(std.cout.flush());
	  resampler.doResampling(in, TempRefObject14, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH/2, clauer.math.Resampler.SRC_SINC_FASTEST);
	  out3 = TempRefObject14.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_SINC_MEDIUM_QUALITY...");
	  System.out.print(std.cout.flush());
	  resampler.doResampling(in, TempRefObject15, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH/2, clauer.math.Resampler.SRC_SINC_MEDIUM_QUALITY);
	  out4 = TempRefObject15.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("SRC_SINC_BEST_QUALITY...");
	  System.out.print(std.cout.flush());
	  resampler.doResampling(in, TempRefObject16, DefineConstantsTest.LENGTH, DefineConstantsTest.LENGTH/2, clauer.math.Resampler.SRC_SINC_BEST_QUALITY);
	  out5 = TempRefObject16.argvalue;
	  System.out.print("FINISHED");
	  System.out.print("\n");
	  System.out.print("\n");
	  // then measure the deltas between the results
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH/2; i++)
		delta += Math.abs(out1[i] - out5[i]);
	  System.out.print("The overall delta between SRC_LINEAR and SRC_SINC_BEST_QUALITY: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH/2; i++)
		delta += Math.abs(out3[i] - out5[i]);
	  System.out.print("The overall delta between SRC_SINC_LOW_QUALITY and SRC_SINC_BEST_QUALITY: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH/2; i++)
		delta += Math.abs(out3[i] - out2[i]);
	  System.out.print("The overall delta between SRC_SINC_LOW_QUALITY and SRC_ZERO_ORDER_HOLD: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  for (int i =0; i<DefineConstantsTest.LENGTH *2; i++)
	  delta = 0.0;
	  for (int i =0; i<DefineConstantsTest.LENGTH/2; i++)
		delta += Math.abs(out1[i] - out2[i]);
	  System.out.print("The overall delta between SRC_LINEAR and SRC_ZERO_ORDER_HOLD: ");
	  System.out.print(delta);
	  System.out.print("\n");
	  System.out.print("\n");

	  return (0);
	}
}
	RefObject<Float> TempRefObject = new RefObject<Float>(fout);
  RefObject<Float> TempRefObject2 = new RefObject<Float>(fout);
  RefObject<Float> TempRefObject3 = new RefObject<Float>(fout);
  RefObject<Float> TempRefObject4 = new RefObject<Float>(fout);
  RefObject<Float> TempRefObject5 = new RefObject<Float>(fout);
  RefObject<Float> TempRefObject6 = new RefObject<Float>(fout);
  RefObject<Float> TempRefObject7 = new RefObject<Float>(out1);
  RefObject<Float> TempRefObject8 = new RefObject<Float>(out2);
  RefObject<Float> TempRefObject9 = new RefObject<Float>(out3);
  RefObject<Float> TempRefObject10 = new RefObject<Float>(out4);
  RefObject<Float> TempRefObject11 = new RefObject<Float>(out5);
  RefObject<Float> TempRefObject12 = new RefObject<Float>(out1);
  RefObject<Float> TempRefObject13 = new RefObject<Float>(out2);
  RefObject<Float> TempRefObject14 = new RefObject<Float>(out3);
  RefObject<Float> TempRefObject15 = new RefObject<Float>(out4);
  RefObject<Float> TempRefObject16 = new RefObject<Float>(out5);

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