public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the Wave File Handler.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//#define LENGTH 480000

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo Wave File Reader");

	  // alloc the error value
	  boolean error = false;

	  // first lets generate an test vector
	  System.out.print("First generate a sample vector.");
	  System.out.print("\n");
	  double[] samples = new double[DefineConstantsTest.LENGTH];
	  for (int i =0; i<DefineConstantsTest.LENGTH; i++)
	  {
		samples[i] = Math.sin((double)i);
		if (i<4)
	  {
		  System.out.print(samples[i]);
		  System.out.print(" ");
	  }
	  }
	  System.out.print("\n");

	  // now lets write the double file
	  clauer.io.WaveFileHandler wfr1 = new clauer.io.WaveFileHandler();
	  wfr1.writeMonoFloat64WaveFile(samples, DefineConstantsTest.LENGTH, 48000, "../TEST_SIGNALS/wave_file_handler_test_f_64.wav", error);

	  // lets read an file out
	  clauer.io.WaveFileHandler wfr2 = new clauer.io.WaveFileHandler();
	  int length;
	  int sampleRate;
	  double[] read = wfr2.readMonoFloat64WaveFile(length, sampleRate, "../TEST_SIGNALS/wave_file_handler_test_f_64.wav", error);
	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from the file again, here the first 4 samples:");
	  System.out.print("\n");
	  for (int i =0; i<4; i++)
		{
		System.out.print(read[i]);
		System.out.print(" ");
		}
	  System.out.print("\n");

	  // lets read an files metadata
	  clauer.io.WaveFileHandler wfr3 = new clauer.io.WaveFileHandler();
	  int channels;
	  int bitsPerSample;
	  boolean pcm;
	  wfr3.getWaveFileMetaData("../TEST_SIGNALS/wave_file_handler_test_f_64.wav", sampleRate, length, channels, bitsPerSample, pcm, error);

	  // and now lets write an 8 bit PCM file
	  String pcmVec2 = new byte[DefineConstantsTest.LENGTH];
	  for (int i =0; i<DefineConstantsTest.LENGTH; i++)
		pcmVec2 = StringHelper.changeCharacter(pcmVec2, i, (i%30));
	  clauer.io.WaveFileHandler wfh4 = new clauer.io.WaveFileHandler();
	  wfh4.writeMonoPcm8WaveFile(pcmVec2, DefineConstantsTest.LENGTH, 48000, "../TEST_SIGNALS/wave_file_handler_test_pcm_8.wav", error);

	  // and now lets write an 16 bit PCM file
	  short[] pcmVec = new short[DefineConstantsTest.LENGTH];
	  for (int i =0; i<DefineConstantsTest.LENGTH; i++)
		pcmVec[i] = (i%30) * 100;
	  clauer.io.WaveFileHandler wfh5 = new clauer.io.WaveFileHandler();
	  wfh5.writeMonoPcm16WaveFile(pcmVec, DefineConstantsTest.LENGTH, 48000, "../TEST_SIGNALS/wave_file_handler_test_pcm_16.wav", error);

	  // test the easy file open interface
	  clauer.io.WaveFileHandler wfh6 = new clauer.io.WaveFileHandler();
	  read = wfh6.autoReadWaveFile("../TEST_SIGNALS/wave_file_handler_test_f_64.wav", length, sampleRate, error);
	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from the 64bit FLOAT file with the Auto fucntion.");
	  System.out.print("\n");
	  for (int i =0; i<4; i++)
		{
		System.out.print(read[i]);
		System.out.print(" ");
		}
	  System.out.print("\n");
	  read = wfh6.autoReadWaveFile("../TEST_SIGNALS/wave_file_handler_test_pcm_16.wav", length, sampleRate, error);
	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from the 16bit INTEGER file with the Auto fucntion.");
	  System.out.print("\n");
	  for (int i =0; i<4; i++)
		{
		System.out.print(read[i]);
		System.out.print(" ");
		}
	  System.out.print("\n");
	  read = wfh6.autoReadWaveFile("../TEST_SIGNALS/wave_file_handler_test_pcm_8.wav", length, sampleRate, error);
	  System.out.print("Read ");
	  System.out.print(length);
	  System.out.print(" samples from the 16bit INTEGER file with the Auto fucntion.");
	  System.out.print("\n");
	  for (int i =0; i<4; i++)
		{
		System.out.print(read[i]);
		System.out.print(" ");
		}
	  System.out.print("\n");


	  // and finally the error flag handling
	  if (error == true)
	  {
		System.out.print("There was any kind of ERROR while the file operations !!!");
		System.out.print("\n");
		return (1);
	  }
	  if (error == false)
	  {
		System.out.print("No Errors....");
		System.out.print("\n");
		return (0);
	  }



	}
}

//--------------------------------------------------------------------------------------

//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2009 Tangible Software Solutions Inc.
//
//	This class provides miscellaneous helper methods for strings.
//----------------------------------------------------------------------------------------
final class StringHelper
{
	//------------------------------------------------------------------------------------
	//	This method allows replacing a single character in a string, to help convert
	//	C++ code where a single character in a character array is replaced.
	//------------------------------------------------------------------------------------
	static String changeCharacter(String sourcestring, int charindex, char changechar)
	{
		return (charindex > 0 ? sourcestring.substring(0, charindex) : "")
			+ Character.toString(changechar) + (charindex < sourcestring.length() - 1 ? sourcestring.substring(charindex + 1) : "");
	}
}
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