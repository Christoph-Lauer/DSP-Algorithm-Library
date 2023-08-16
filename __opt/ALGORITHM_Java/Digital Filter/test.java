public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the digital filter algorithm.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define WAVE_FILE_NAME "../TEST_SIGNALS/white_noise.wav"

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n**************************************************");
	  System.out.print("\n");
	  System.out.print("***  This is the DIGITAL FILTER test function  ***");
	  System.out.print("\n");
	  System.out.print("***      (c) Christoph Lauer Engineering       ***");
	  System.out.print("\n");
	  System.out.print("**************************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // open the first IO file
	  clauer.io.WaveFileHandler wfh = new clauer.io.WaveFileHandler();
	  boolean error = false;
	  int sampleLength = 0;
	  int sampleRate = 0;
	  short[] si_samples = wfh.readMonoPcm16WaveFile(sampleLength, sampleRate, DefineConstantsTest.WAVE_FILE_NAME, error);
	  double[] samples = new double[sampleLength];
	  for (int i =0; i<sampleLength; i++)
		samples[i] = (double)(si_samples[i]);
	  double[] samplesCopy = new double[sampleLength];

	  System.out.print("Write the five filters results to file...");
	  System.out.print("\n");

	  // apply the filter function
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
	  clauer.math.DigitalFilter.applyFilter(samplesCopy, sampleLength, sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE.DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 8, false);
	  // write the result back to another wave file
	  for (int i =0; i<sampleLength; i++)
		si_samples[i] = (short)(samplesCopy[i]);
	  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_low_pass_6000.wav", error);

	  // apply the filter function
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
	  clauer.math.DigitalFilter.applyFilter(samplesCopy, sampleLength, sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE.DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 8, true);
	  // write the result back to another wave file
	  for (int i =0; i<sampleLength; i++)
		si_samples[i] = (short)(samplesCopy[i]);
	  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_high_pass_6000.wav", error);

	  // apply the filter function
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
	  clauer.math.DigitalFilter.bandPassFilter(samplesCopy, sampleLength, sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE.DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 18000.0, 8);
	  // write the result back to another wave file
	  for (int i =0; i<sampleLength; i++)
		si_samples[i] = (short)(samplesCopy[i]);
	  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_band_pass_6000_18000.wav", error);

	  // apply the filter function
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
	  clauer.math.DigitalFilter.bandPassFilter(samplesCopy, sampleLength, sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE.DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 800.0, 1200.0, 8);
	  // write the result back to another wave file
	  for (int i =0; i<sampleLength; i++)
		si_samples[i] = (short)(samplesCopy[i]);
	  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_band_pass_800_1200.wav", error);

	  // apply the filter function
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
	  clauer.math.DigitalFilter.bandStopFilter(samplesCopy, sampleLength, sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE.DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 18000.0, 8);
	  // write the result back to another wave file
	  for (int i =0; i<sampleLength; i++)
		si_samples[i] = (short)(samplesCopy[i]);
	  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_band_stop_6000_18000.wav", error);

	  // give no error back
	  return(0);
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