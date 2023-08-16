/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    wave_file_handler.h
 * @class   GenericWaveFileHandler
 * @version 0.9
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This is a file interface to wave files.
 * @see     http://de.wikipedia.org/wiki/RIFF_WAVE
 * @todo    There should be any kind of support for the INT24 file format where no generic data type is available...
 *
 * Note that all reading vectors are generated inside the reading function and must be deleted outside
 * the function explicitely. Note that the error handling is accululative, which means that the error
 * flag can bi given to more than one function and it will only set to true if an error occures.
 */
//-------------------------------------------------------------------------------------------------

#ifndef CLAUER_IO_WAVE_FILE_HANDLER
#define CLAUER_IO_WAVE_FILE_HANDLER

//-------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace io
{

//-------------------------------------------------------------------------------------------------

/**
 * This supports the caller with a basic interface to extract and write samples from MS wave format files.
 * The easyest way to extract samples is the autoReadWaveFile function.
 */
class WaveFileHandler
{

//-------------------------------------------------------------------------------------------------

public:

 /**
  * This function automatically extracts the sampels from the wave file and gives back the double samples
  * back. All format specific settings will be performed autonomous while the extraction of the wave file
  * and no format specific setting must be supported like the bitsPerSample, the file format (PCM/Float).
  * Note that all functions provide only mono files. If a stereo file was opened the samples will ne be
  * extracted This function will be the easyest way to get on the sampels of a wave file.
  *
  * @param  path         The path to the wave file.
  * @param  sampleRate   The reference to sample rate will be set while the extraction of the wave file.
  * @param  length       The reference to the length wil be set while the extraction of the wave file.
  * @param  error        This value will be set to true if there was any error with the opening of the file.
  * @param  print        This falg is defaut set to false, if the falg is set to true all the file 
  *                      specific information will be print to the console.
  * @return              The return value of the wave file extractor is the extarcted sample vector. 
  *                      Note that the sample vector array will be allocated into this function so no
  *                      pre allocation of the vector is needed.
  */
  static double* autoReadWaveFile(const char* path,  int& length, int& sampleRate, bool& error);
 
//-------------------------------------------------------------------------------------------------

 /**
  * This fucntion is only for testing purposes of the wave files. It extracts the meta data from 
  * a file and gives them bach via call by reference.
  *
  * @param  path          The path to the wave file.
  * @param  sampleRate    The samplerate of the wave file.
  * @param  length        The length of the wave file in samples.
  * @param  channels      The number of channels.
  * @param  bitsPerSample The number of bits per sample.
  * @param  pcm           True if the signal is stored in raw pcm point format and
  *                       false for floating point fomats.
  */
  static void getWaveFileMetaData(const char* path, int &sampleRate, int &length, int &channels, int &bitsPerSample, bool &pcm, bool* error, bool print = false);

//-------------------------------------------------------------------------------------------------

 /**
  * This group of functions implements the floating point versions of the read and write functions to the
  * wave files. This functions use the generic wave file class template to access the wave file data.
  *
  * The following function signature for the READ functions.
  * @param  length     A pointer to an integer where the length of the read data will be stored.
  * @param  sampleRate A pointer to the sample rate. Will be be set while writing the file.
  * @param  path       A string to the file path.
  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
  * @return            The pointer to the double data read from file.
  *
  * the following functions signature for the WRITE functions.
  * @param  samples    The pointer to data to be saved.
  * @param  length     The length of the samples vector.
  * @param  sampleRate The sampleRate integer value.
  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
  * @param  path       The path to the wave file.
  */
//@{
  static double* readMonoFloat64WaveFile   (int* length, int* sampleRate, const char* path, bool* error);
  static float*  readMonoFloat32WaveFile   (int* length, int* sampleRate, const char* path, bool* error);
  
  static void    writeMonoFloat64WaveFile  (const double* data, const int length, const int sampleRate, const char* path, bool* error);
  static void    writeMonoFloat32WaveFile  (const float* data, const int length, const int sampleRate, const char* path, bool* error);
//@}
  
//-------------------------------------------------------------------------------------------------

 /**
  * THis group of functions implements the PCM versions of the read and write functions to the
  * wave files This function group use the generic wave file class template to access the wave file data.
  *
  * The following signature for the READ functions.
  * @param  length     A pointer to an integer where the length of the read data will be stored.
  * @param  sampleRate A pointer to the sample rate. Will be be set while writing the file.
  * @param  path       A string to the file path.
  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
  * @return            The pointer to the double data read from file.
  *
  * the following functions signature for the WRITE functions.
  * @param  samples    The pointer to data to be saved.
  * @param  length     The length of the samples vector.
  * @param  sampleRate The sampleRate integer value.
  * @param  error      This bool will only be changed to true if an error occured, otherwise the flas is not touched.
  * @param  path       The path to the wave file.
  */
//@{
  static char*       readMonoPcm8WaveFile    (int* length, int* sampleRate, const char* path, bool* error);
  static short int*  readMonoPcm16WaveFile   (int* length, int* sampleRate, const char* path, bool* error);
  static int*        readMonoPcm32WaveFile   (int* length, int* sampleRate, const char* path, bool* error);

  static void        writeMonoPcm8WaveFile   (const char* data, const int length, const int sampleRate, const char* path, bool* error);
  static void        writeMonoPcm16WaveFile  (const short int* data, const int length, const int sampleRate, const char* path, bool* error);
  static void        writeMonoPcm32WaveFile  (const int* data, const int length, const int sampleRate, const char* path, bool* error);
//@}

/**
 * This special function converts the given ascii file to a standart 16 bit mono wave file. The wave
 * file will be normalized to the full amplitude. The ascii floating point syntax is more less arbitarry 
 * and can be or not in the exponential format.
 *
 * @param asciiFile     The path to the ascii file.
 * @param waveFile      The path to the wave file.
 * @param sampleRate    TThe sample rate of the wave file.
 */
 static void asciiToWave(const char* asciiFile, const char* waveFile, const int sampleRate);

//-------------------------------------------------------------------------------------------------

}; // class WaveFileReader

//-------------------------------------------------------------------------------------------------

} // namespace io

} // namespace clauer

//-------------------------------------------------------------------------------------------------

#endif // CLAUER_IO_WAVE_FILE_HANDLER

