package clauer.io;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    wave_file_handler.cpp
// * @class   GenericWaveFileHandler
// * @version 0.9
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This is a file interface to wave files.
// * @see     http://de.wikipedia.org/wiki/RIFF_WAVE
// * @todo    There should be any kind of support for the INT24 file format where no generic data type is available...
// 

//-------------------------------------------------------------------------------------------------

// C Language Libraray headers

// C++ Language Libraray headers

// Standart-Template-Library header files

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    wave_file_handler.h
// * @class   GenericWaveFileHandler
// * @version 0.9
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This is a file interface to wave files.
// * @see     http://de.wikipedia.org/wiki/RIFF_WAVE
// * @todo    There should be any kind of support for the INT24 file format where no generic data type is available...
// *
// * Note that all reading vectors are generated inside the reading function and must be deleted outside
// * the function explicitely. Note that the error handling is accululative, which means that the error
// * flag can bi given to more than one function and it will only set to true if an error occures.
// 
//-------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_IO_WAVE_FILE_HANDLER
//#define CLAUER_IO_WAVE_FILE_HANDLER

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------

//*
// * This supports the caller with a basic interface to extract and write samples from MS wave format files.
// * The easyest way to extract samples is the autoReadWaveFile function.
// 
public class WaveFileHandler
{

//-------------------------------------------------------------------------------------------------


// *
//  * This function automatically extracts the sampels from the wave file and gives back the double samples
//  * back. All format specific settings will be performed autonomous while the extraction of the wave file
//  * and no format specific setting must be supported like the bitsPerSample, the file format (PCM/Float).
//  * Note that all functions provide only mono files. If a stereo file was opened the samples will ne be
//  * extracted This function will be the easyest way to get on the sampels of a wave file.
//  *
//  * @param  path         The path to the wave file.
//  * @param  sampleRate   The reference to sample rate will be set while the extraction of the wave file.
//  * @param  length       The reference to the length wil be set while the extraction of the wave file.
//  * @param  error        This value will be set to true if there was any error with the opening of the file.
//  * @param  print        This falg is defaut set to false, if the falg is set to true all the file 
//  *                      specific information will be print to the console.
//  * @return              The return value of the wave file extractor is the extarcted sample vector. 
//  *                      Note that the sample vector array will be allocated into this function so no
//  *                      pre allocation of the vector is needed.
//  

  //-------------------------------------------------------------------------------------------------
  
  public static double autoReadWaveFile(String path, RefObject<Integer> length, RefObject<Integer> sampleRate, RefObject<Boolean> error)
  {
	// the result vector
	double[] samples = null;
	double peak = 0.0;
  
	////////////////////////////////////////////////////////////////////////////
	// first get the metadata of the wave file to which format we will open
  
	// intt needed values
	int channels = 0;
	int bitsPerSample = 0;
	boolean pcm = false;
	WaveFileHandler.getWaveFileMetaData(path, sampleRate.argvalue, length.argvalue, channels, bitsPerSample, pcm, error.argvalue);
  // check if there was any error while open the wave file
	if(error.argvalue ==true || channels != 1)
	  return null;
	boolean opened = false;
  
	////////////////////////////////////////////////////////////////////////////
	// go throught the supported formats and open the wave file
  
	// 32 bit FLOAT wave file
	if (bitsPerSample ==32 && pcm == false)
	{
	  RefObject<Integer> TempRefObject = new RefObject<Integer>(length);
	  RefObject<Integer> TempRefObject2 = new RefObject<Integer>(sampleRate);
	  RefObject<Boolean> TempRefObject3 = new RefObject<Boolean>(error);
	  GlobalMembersWave_file_handler.float[] tmpSamples = WaveFileHandler.readMonoFloat32WaveFile(TempRefObject, TempRefObject2, path, TempRefObject3);
	  length.argvalue = TempRefObject.argvalue;
	  sampleRate.argvalue = TempRefObject2.argvalue;
	  error.argvalue = TempRefObject3.argvalue;
	  // cast the sampels to double values
	  samples = new double[length.argvalue];
	  for (int i =0; i<length.argvalue; i++)
		samples[i] = (double)(tmpSamples[i]);
	  tmpSamples = null;
	  opened = true;
	  peak = 1.0;
	}
	// 64 bit FLOAT wave file
	else if (bitsPerSample ==64 && pcm == false)
	{
	  RefObject<Integer> TempRefObject4 = new RefObject<Integer>(length);
	  RefObject<Integer> TempRefObject5 = new RefObject<Integer>(sampleRate);
	  RefObject<Boolean> TempRefObject6 = new RefObject<Boolean>(error);
	  samples = WaveFileHandler.readMonoFloat64WaveFile(TempRefObject4, TempRefObject5, path, TempRefObject6);
	  length.argvalue = TempRefObject4.argvalue;
	  sampleRate.argvalue = TempRefObject5.argvalue;
	  error.argvalue = TempRefObject6.argvalue;
	  opened = true;
	  peak = 1.0;
	}
	// 8 bit INTEGER wave file
	else if (bitsPerSample ==8 && pcm == true)
	{
	  RefObject<Integer> TempRefObject7 = new RefObject<Integer>(length);
	  RefObject<Integer> TempRefObject8 = new RefObject<Integer>(sampleRate);
	  RefObject<Boolean> TempRefObject9 = new RefObject<Boolean>(error);
	  byte[] tmpSamples = WaveFileHandler.readMonoPcm8WaveFile(TempRefObject7, TempRefObject8, path, TempRefObject9);
	  length.argvalue = TempRefObject7.argvalue;
	  sampleRate.argvalue = TempRefObject8.argvalue;
	  error.argvalue = TempRefObject9.argvalue;
	  // cast the sampels to double values
	  samples = new double[length.argvalue];
	  for (int i =0; i<length.argvalue; i++)
		samples[i] = (double)(tmpSamples[i]);
	  tmpSamples = null;
	  opened = true;
	  peak = (double)(SCHAR_MAX);
	}
	// 16 bit INTEGER wave file
	else if (bitsPerSample ==16 && pcm == true)
	{
	  RefObject<Integer> TempRefObject10 = new RefObject<Integer>(length);
	  RefObject<Integer> TempRefObject11 = new RefObject<Integer>(sampleRate);
	  RefObject<Boolean> TempRefObject12 = new RefObject<Boolean>(error);
	  short[] tmpSamples = WaveFileHandler.readMonoPcm16WaveFile(TempRefObject10, TempRefObject11, path, TempRefObject12);
	  length.argvalue = TempRefObject10.argvalue;
	  sampleRate.argvalue = TempRefObject11.argvalue;
	  error.argvalue = TempRefObject12.argvalue;
	  // cast the sampels to double values
	  samples = new double[length.argvalue];
	  for (int i =0; i<length.argvalue; i++)
		samples[i] = (double)(tmpSamples[i]);
	  tmpSamples = null;
	  opened = true;
	  peak = (double)(SHRT_MAX);
	}
	// 32 bit INTEGER wave file
	else if (bitsPerSample ==32 && pcm == true)
	{
	  RefObject<Integer> TempRefObject13 = new RefObject<Integer>(length);
	  RefObject<Integer> TempRefObject14 = new RefObject<Integer>(sampleRate);
	  RefObject<Boolean> TempRefObject15 = new RefObject<Boolean>(error);
	  int[] tmpSamples = WaveFileHandler.readMonoPcm32WaveFile(TempRefObject13, TempRefObject14, path, TempRefObject15);
	  length.argvalue = TempRefObject13.argvalue;
	  sampleRate.argvalue = TempRefObject14.argvalue;
	  error.argvalue = TempRefObject15.argvalue;
	  // cast the sampels to double values
	  samples = new double[length.argvalue];
	  for (int i =0; i<length.argvalue; i++)
		samples[i] = (double)(tmpSamples[i]);
	  tmpSamples = null;
	  opened = true;
	  peak = (double)(INT_MAX);
	}
	// give back an error if there was no supported format found
	else
	  error.argvalue = true;
  
	////////////////////////////////////////////////////////////////////////////
	// do the final processing of the extracted samples
  
	// check if there was any error while open the wave file
	if(error.argvalue == true)
	  return null;
  
	// normalize the signal to the interval [-1.0...1.0]
	if (peak != 1.0)
	  for (int i =0; i<length.argvalue; i++)
		samples[i] /= peak;
  
	// give the result back
	return samples;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This fucntion is only for testing purposes of the wave files. It extracts the meta data from 
//  * a file and gives them bach via call by reference.
//  *
//  * @param  path          The path to the wave file.
//  * @param  sampleRate    The samplerate of the wave file.
//  * @param  length        The length of the wave file in samples.
//  * @param  channels      The number of channels.
//  * @param  bitsPerSample The number of bits per sample.
//  * @param  pcm           True if the signal is stored in raw pcm point format and
//  *                       false for floating point fomats.
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void getWaveFileMetaData(String path, RefObject<Integer> sampleRate, RefObject<Integer> length, RefObject<Integer> channels, RefObject<Integer> bitsPerSample, RefObject<Boolean> pcm, RefObject<Boolean> error)
  {
	  getWaveFileMetaData(path, sampleRate, length, channels, bitsPerSample, pcm, error, false);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void getWaveFileMetaData(String path, int &sampleRate, int &length, int &channels, int &bitsPerSample, boolean &pcm, boolean* error, boolean print = false)
  public static void getWaveFileMetaData(String path, RefObject<Integer> sampleRate, RefObject<Integer> length, RefObject<Integer> channels, RefObject<Integer> bitsPerSample, RefObject<Boolean> pcm, RefObject<Boolean> error, boolean print)
  {
	// first get an instance of the reader. Note that here the smallest default type is used.
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Byte> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Byte>(path);
	// then read out the meta data
	if (gwfh.read(false) == false) // true means in this case on touch the meta data and do not read out the core data block
	  error.argvalue = true;
	else
	  error.argvalue = false;
	// refer the values
	sampleRate.argvalue = gwfh.mySampleRate;
	channels.argvalue = gwfh.myChannels;
	bitsPerSample.argvalue = gwfh.myBitsPerSample;
	length.argvalue = gwfh.myDataSize / gwfh.myChannels / (bitsPerSample.argvalue / 8);
	if (gwfh.myFormat == 0x0001)
	  pcm.argvalue = true;
	else
	  pcm.argvalue = false;
	if (print == true)
  {
	  System.out.print(gwfh.getFormatString());
  }
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This group of functions implements the floating point versions of the read and write functions to the
//  * wave files. This functions use the generic wave file class template to access the wave file data.
//  *
//  * The following function signature for the READ functions.
//  * @param  length     A pointer to an integer where the length of the read data will be stored.
//  * @param  sampleRate A pointer to the sample rate. Will be be set while writing the file.
//  * @param  path       A string to the file path.
//  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
//  * @return            The pointer to the double data read from file.
//  *
//  * the following functions signature for the WRITE functions.
//  * @param  samples    The pointer to data to be saved.
//  * @param  length     The length of the samples vector.
//  * @param  sampleRate The sampleRate integer value.
//  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
//  * @param  path       The path to the wave file.
//  
//@{

  //-------------------------------------------------------------------------------------------------
  
  public static double readMonoFloat64WaveFile(RefObject<Integer> length, RefObject<Integer> sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Double> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Double>(path);
	// then read out the data
	if (gwfh.read() == false)
	  error.argvalue = true;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	length.argvalue = gwfh.myDataSize / gwfh.myChannels / sizeof(double);
	sampleRate.argvalue = gwfh.mySampleRate;
	return gwfh.myData;
  }

  //-------------------------------------------------------------------------------------------------
  
  public static float readMonoFloat32WaveFile(RefObject<Integer> length, RefObject<Integer> sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<GlobalMembersWave_file_handler.float> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<GlobalMembersWave_file_handler.float>(path);
	// then read out the data
	if (gwfh.read() == false)
	  error.argvalue = true;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	length.argvalue = gwfh.myDataSize / gwfh.myChannels / sizeof(GlobalMembersWave_file_handler.float);
	sampleRate.argvalue = gwfh.mySampleRate;
	return gwfh.myData;
  }

  public static void writeMonoFloat64WaveFile(double data, int length, int sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Double> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Double>(path);
	// prepare the metadata values
	gwfh.myChannels = 1;
	gwfh.myFormat = 0x0003; // for floating point data
	gwfh.mySampleRate = sampleRate;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myBitsPerSample = sizeof(double)*8;
  //C++ TO JAVA CONVERTER NOTE: Embedded comments are not maintained by C++ to Java Converter
  //ORIGINAL LINE: gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(FLOAT_64);
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myDataSize = length * 1 * sizeof(double);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	gwfh.myData = const_cast<Double*>(data);
	// then write the file
	if (gwfh.write() == false)
	  error.argvalue = true;
  }
  public static void writeMonoFloat32WaveFile(float data, int length, int sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<GlobalMembersWave_file_handler.float> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<GlobalMembersWave_file_handler.float>(path);
	// prepare the metadata values
	gwfh.myChannels = 1;
	gwfh.myFormat = 0x0003; // for floating point data
	gwfh.mySampleRate = sampleRate;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myBitsPerSample = sizeof(GlobalMembersWave_file_handler.float)*8;
  //C++ TO JAVA CONVERTER NOTE: Embedded comments are not maintained by C++ to Java Converter
  //ORIGINAL LINE: gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(FLOAT_32);
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myDataSize = length * 1 * sizeof(GlobalMembersWave_file_handler.float);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	gwfh.myData = const_cast<GlobalMembersWave_file_handler.float*>(data);
	// then write the file
	if (gwfh.write() == false)
	  error.argvalue = true;
  }
//@}

//-------------------------------------------------------------------------------------------------

// *
//  * THis group of functions implements the PCM versions of the read and write functions to the
//  * wave files This function group use the generic wave file class template to access the wave file data.
//  *
//  * The following signature for the READ functions.
//  * @param  length     A pointer to an integer where the length of the read data will be stored.
//  * @param  sampleRate A pointer to the sample rate. Will be be set while writing the file.
//  * @param  path       A string to the file path.
//  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
//  * @return            The pointer to the double data read from file.
//  *
//  * the following functions signature for the WRITE functions.
//  * @param  samples    The pointer to data to be saved.
//  * @param  length     The length of the samples vector.
//  * @param  sampleRate The sampleRate integer value.
//  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
//  * @param  path       The path to the wave file.
//  
//@{

  //-------------------------------------------------------------------------------------------------
  
  public static String readMonoPcm8WaveFile(RefObject<Integer> length, RefObject<Integer> sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Byte> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Byte>(path);
	// then read out the data
	if (gwfh.read() == false)
	  error.argvalue = true;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	length.argvalue = gwfh.myDataSize / gwfh.myChannels / sizeof(byte);
	sampleRate.argvalue = gwfh.mySampleRate;
	return gwfh.myData;
  }

  //-------------------------------------------------------------------------------------------------
  
  public static short readMonoPcm16WaveFile(RefObject<Integer> length, RefObject<Integer> sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Short> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Short>(path);
	// then read out the data
	if (gwfh.read() == false)
	  error.argvalue = true;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	length.argvalue = gwfh.myDataSize / gwfh.myChannels / sizeof(short);
	sampleRate.argvalue = gwfh.mySampleRate;
	return gwfh.myData;
  }

  //-------------------------------------------------------------------------------------------------int* WaveFileHandler::readMonoPcm32WaveFile(int* length, const char* path)
  
  public static int readMonoPcm32WaveFile(RefObject<Integer> length, RefObject<Integer> sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Integer> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Integer>(path);
	// then read out the data
	if (gwfh.read() == false)
	  error.argvalue = true;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	length.argvalue = gwfh.myDataSize / gwfh.myChannels / sizeof(int);
	return gwfh.myData;
  }

  public static void writeMonoPcm8WaveFile(String data, int length, int sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Byte> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Byte>(path);
	// prepare the metadata values
	gwfh.myChannels = 1;
	gwfh.myFormat = 0x0001; // for floating point data
	gwfh.mySampleRate = sampleRate;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myBitsPerSample = sizeof(byte)*8;
  //C++ TO JAVA CONVERTER NOTE: Embedded comments are not maintained by C++ to Java Converter
  //ORIGINAL LINE: gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(INT_8);
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myDataSize = length * 1 * sizeof(byte);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	gwfh.myData = const_cast<Byte*>(data);
	// then write the file
	if (gwfh.write() == false)
	  error.argvalue = true;
  }
  public static void writeMonoPcm16WaveFile(short data, int length, int sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Short> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Short>(path);
	// prepare the metadata values
	gwfh.myChannels = 1;
	gwfh.myFormat = 0x0001; // for floating point data
	gwfh.mySampleRate = sampleRate;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myBitsPerSample = sizeof(short)*8;
  //C++ TO JAVA CONVERTER NOTE: Embedded comments are not maintained by C++ to Java Converter
  //ORIGINAL LINE: gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(INT_16);
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myDataSize = length * 1 * sizeof(short);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	gwfh.myData = const_cast<Short*>(data);
	// then write the file
	if (gwfh.write() == false)
	  error.argvalue = true;
  }
  public static void writeMonoPcm32WaveFile(int data, int length, int sampleRate, String path, RefObject<Boolean> error)
  {
	// first get an instance of the file handler template class
	GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Integer> gwfh = new GlobalMembersWave_file_handler.clauer.io.GenericWaveFileHandler<Integer>(path);
	// prepare the metadata values
	gwfh.myChannels = 1;
	gwfh.myFormat = 0x0001; // for floating point data
	gwfh.mySampleRate = sampleRate;
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myBitsPerSample = sizeof(int)*8;
  //C++ TO JAVA CONVERTER NOTE: Embedded comments are not maintained by C++ to Java Converter
  //ORIGINAL LINE: gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(INT_32);
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	gwfh.myDataSize = length * 1 * sizeof(int);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	gwfh.myData = const_cast<Integer*>(data);
	// then write the file
	if (gwfh.write() == false)
	  error.argvalue = true;
  }
//@}

//*
// * This special function converts the given ascii file to a standart 16 bit mono wave file. The wave
// * file will be normalized to the full amplitude. The ascii floating point syntax is more less arbitarry 
// * and can be or not in the exponential format.
// *
// * @param asciiFile     The path to the ascii file.
// * @param waveFile      The path to the wave file.
// * @param sampleRate    TThe sample rate of the wave file.
// 

//-------------------------------------------------------------------------------------------------

 public static void asciiToWave(String asciiFile, String waveFile, int sampleRate)
 {
	// first read out the ascii file
	java.util.ArrayList<String> asciiStringVector = new java.util.ArrayList<String>();
	ifstream ifs = new ifstream(asciiFile);
	String temp;
	while(getline(ifs, temp))
	 asciiStringVector.add(temp);

	// determine the length of the vector and allocate the sample array
	int length = asciiStringVector.size();
	double[] d_samples = new double[length];

	// convert the line strings to double values and extract the peak
	double peak = DBL_MIN;
	for (int i =0; i<length; i++)
	{
	  d_samples[i] = Double.parseDouble(asciiStringVector.get(i).c_str());
	  if (peak < Math.abs(d_samples[i]))
		peak = Math.abs(d_samples[i]);
	}

	// normalize and cast the samples to the full amplitude
	short[] i_samples = new short[length];
	for (int i =0; i<length; i++)
	  i_samples[i] = (short)(d_samples[i] / peak * 32767.0);

	// save the samples a the wave file
	boolean error;
	RefObject<Boolean> TempRefObject = new RefObject<Boolean>(error);
	writeMonoPcm16WaveFile(i_samples, length, sampleRate, waveFile, TempRefObject);
	error = TempRefObject.argvalue;
 }

//-------------------------------------------------------------------------------------------------

} // class WaveFileReader

//-------------------------------------------------------------------------------------------------



//-------------------------------------------------------------------------------------------------

//#endif // CLAUER_IO_WAVE_FILE_HANDLER
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace io



//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------



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