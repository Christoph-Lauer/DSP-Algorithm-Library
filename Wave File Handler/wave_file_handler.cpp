/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    wave_file_handler.cpp
 * @class   GenericWaveFileHandler
 * @version 0.9
 * @date    2014
 * @author  Christoph Lauer
 * @brief   This is a file interface to wave files.
 * @see     http://de.wikipedia.org/wiki/RIFF_WAVE
 * @todo    There should be any kind of support for the INT24 file format where no generic data type is available...
 */
 
//-------------------------------------------------------------------------------------------------

// C Language Libraray headers
#include <cfloat>
#include <cmath>
#include <climits>
#include <cstdlib>

// C++ Language Libraray headers
#include <iostream>

// Standart-Template-Library header files
#include <string>
#include <vector>

// Local headers
#include "generic_wave_file_handler.hpp"
#include "wave_file_handler.h"

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace io
{

//-------------------------------------------------------------------------------------------------

double* WaveFileHandler::autoReadWaveFile(const char* path, int& length, int& sampleRate, bool& error)
{
  // the result vector
  double* samples = NULL;
  double peak = 0.0; 
  
  ////////////////////////////////////////////////////////////////////////////
  // first get the metadata of the wave file to which format we will open
  
  // intt needed values
  int  channels      = 0;
  int  bitsPerSample = 0;
  bool pcm           = false;
  WaveFileHandler::getWaveFileMetaData(path, sampleRate, length, channels, bitsPerSample, pcm, &error);
  // check if there was any error while open the wave file
  if(error==true || channels != 1)
    return NULL;

  ////////////////////////////////////////////////////////////////////////////
  // go throught the supported formats and open the wave file
  
  // 32 bit FLOAT wave file
  if (bitsPerSample==32 && pcm == false)
  {
    float* tmpSamples = WaveFileHandler::readMonoFloat32WaveFile(&length, &sampleRate, path, &error);
    // cast the sampels to double values
    samples = new double[length];
    for (int i=0; i<length; i++)
      samples[i] = static_cast<double>(tmpSamples[i]);
    delete[] tmpSamples;
    peak = 1.0;
  }
  // 64 bit FLOAT wave file
  else if (bitsPerSample==64 && pcm == false)
  {
    samples = WaveFileHandler::readMonoFloat64WaveFile(&length, &sampleRate, path, &error);
    peak = 1.0;
  }
  // 8 bit INTEGER wave file
  else if (bitsPerSample==8 && pcm == true)
  {
    char* tmpSamples = WaveFileHandler::readMonoPcm8WaveFile(&length, &sampleRate, path, &error);
    // cast the sampels to double values
    samples = new double[length];
    for (int i=0; i<length; i++)
      samples[i] = static_cast<double>(tmpSamples[i]);
    delete[] tmpSamples;
    peak = static_cast<double>(SCHAR_MAX);
  }
  // 16 bit INTEGER wave file
  else if (bitsPerSample==16 && pcm == true)
  {
    short int* tmpSamples = WaveFileHandler::readMonoPcm16WaveFile(&length, &sampleRate, path, &error);
    // cast the sampels to double values
    samples = new double[length];
    for (int i=0; i<length; i++)
      samples[i] = static_cast<double>(tmpSamples[i]);
    delete[] tmpSamples;
    peak = static_cast<double>(SHRT_MAX);
  }
  // 32 bit INTEGER wave file
  else if (bitsPerSample==32 && pcm == true)
  {
    int* tmpSamples = WaveFileHandler::readMonoPcm32WaveFile(&length, &sampleRate, path, &error);
    // cast the sampels to double values
    samples = new double[length];
    for (int i=0; i<length; i++)
      samples[i] = static_cast<double>(tmpSamples[i]);
    delete[] tmpSamples;
    peak = static_cast<double>(INT_MAX);
  }
  // give back an error if there was no supported format found
  else 
    return NULL;

  ////////////////////////////////////////////////////////////////////////////
  // do the final processing of the extracted samples

  
  // normalize the signal to the interval [-1.0...1.0]
  if (peak != 1.0)
    for (int i=0; i<length; i++)
      samples[i] /= peak;
  
  // give the result back
  return samples;
}

//-------------------------------------------------------------------------------------------------

void WaveFileHandler::getWaveFileMetaData(const char* path, int &sampleRate, int &length, int &channels, int &bitsPerSample, bool &pcm, bool* error, bool print)
{
  // first get an instance of the reader. Note that here the smallest default type is used.
  clauer::io::GenericWaveFileHandler<INT_8> gwfh(path);
  // then read out the meta data
  if (gwfh.read(false) == false) // true means in this case on touch the meta data and do not read out the core data block
    *error = true;
  else 
    *error = false;
  // refer the values
  sampleRate    = gwfh.mySampleRate;
  channels      = gwfh.myChannels;
  bitsPerSample = gwfh.myBitsPerSample;
  length        = gwfh.myDataSize / gwfh.myChannels / (bitsPerSample / 8);
  if (gwfh.myFormat == 0x0001)
    pcm = true;
  else 
    pcm = false;
  if (print == true)
    cout << gwfh.getFormatString();
}

//-------------------------------------------------------------------------------------------------

double* WaveFileHandler::readMonoFloat64WaveFile(int* length, int* sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<FLOAT_64> gwfh(path);
  // then read out the data
  if (gwfh.read() == false)
    *error = true;
  *length = gwfh.myDataSize / gwfh.myChannels / sizeof(double);
  *sampleRate = gwfh.mySampleRate;
  return gwfh.myData;
}

void WaveFileHandler::writeMonoFloat64WaveFile(const double* data, const int length, const int sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<FLOAT_64> gwfh(path);
  // prepare the metadata values
  gwfh.myChannels       = 1;
  gwfh.myFormat         = 0x0003;         // for floating point data
  gwfh.mySampleRate     = sampleRate;
  gwfh.myBitsPerSample  = sizeof(FLOAT_64)*8;
  gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(FLOAT_64);
  gwfh.myData           = const_cast<FLOAT_64*>(data);
  // then write the file 
  if (gwfh.write() == false)
    *error = true;
}

//-------------------------------------------------------------------------------------------------

float* WaveFileHandler::readMonoFloat32WaveFile(int* length, int* sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<FLOAT_32> gwfh(path);
  // then read out the data
  if (gwfh.read() == false)
    *error = true;
  *length = gwfh.myDataSize / gwfh.myChannels / sizeof(float);
  *sampleRate = gwfh.mySampleRate;
  return gwfh.myData;
}

void WaveFileHandler::writeMonoFloat32WaveFile(const float* data, const int length, const int sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<FLOAT_32> gwfh(path);
  // prepare the metadata values
  gwfh.myChannels       = 1;
  gwfh.myFormat         = 0x0003;         // for floating point data
  gwfh.mySampleRate     = sampleRate;
  gwfh.myBitsPerSample  = sizeof(FLOAT_32)*8;
  gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(FLOAT_32);
  gwfh.myData           = const_cast<FLOAT_32*>(data);
  // then write the file 
  if (gwfh.write() == false)
    *error = true;
}

//-------------------------------------------------------------------------------------------------

INT_8* WaveFileHandler::readMonoPcm8WaveFile(int* length, int* sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<INT_8> gwfh(path);
  // then read out the data
  if (gwfh.read() == false)
    *error = true;
  *length = gwfh.myDataSize / gwfh.myChannels / sizeof(INT_8);
  *sampleRate = gwfh.mySampleRate;
  return gwfh.myData;
}

void WaveFileHandler::writeMonoPcm8WaveFile(const char* data, const int length, const int sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<INT_8> gwfh(path);
  // prepare the metadata values
  gwfh.myChannels       = 1;
  gwfh.myFormat         = 0x0001;         // for floating point data
  gwfh.mySampleRate     = sampleRate;
  gwfh.myBitsPerSample  = sizeof(INT_8)*8;
  gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(INT_8);
  gwfh.myData           = const_cast<INT_8*>(data);
  // then write the file 
  if (gwfh.write() == false)
    *error = true;
}

//-------------------------------------------------------------------------------------------------

INT_16* WaveFileHandler::readMonoPcm16WaveFile(int* length, int* sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<INT_16> gwfh(path);
  // then read out the data
  if (gwfh.read() == false)
    *error = true;
  *length = gwfh.myDataSize / gwfh.myChannels / sizeof(INT_16);
  *sampleRate = gwfh.mySampleRate;
  return gwfh.myData;
}

void WaveFileHandler::writeMonoPcm16WaveFile(const short int* data, const int length, const int sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<INT_16> gwfh(path);
  // prepare the metadata values
  gwfh.myChannels       = 1;
  gwfh.myFormat         = 0x0001;         // for floating point data
  gwfh.mySampleRate     = sampleRate;
  gwfh.myBitsPerSample  = sizeof(INT_16)*8;
  gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(INT_16);
  gwfh.myData           = const_cast<INT_16*>(data);
  // then write the file 
  if (gwfh.write() == false)
    *error = true;
}

//-------------------------------------------------------------------------------------------------int* WaveFileHandler::readMonoPcm32WaveFile(int* length, const char* path)

int* WaveFileHandler::readMonoPcm32WaveFile(int* length, int* sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<INT_32> gwfh(path);
  // then read out the data
  if (gwfh.read() == false)
    *error = true;
  *length = gwfh.myDataSize / gwfh.myChannels / sizeof(INT_32);
  return gwfh.myData;
}

void WaveFileHandler::writeMonoPcm32WaveFile(const int* data, const int length, const int sampleRate, const char* path, bool* error)
{
  // first get an instance of the file handler template class
  clauer::io::GenericWaveFileHandler<INT_32> gwfh(path);
  // prepare the metadata values
  gwfh.myChannels       = 1;
  gwfh.myFormat         = 0x0001;         // for floating point data
  gwfh.mySampleRate     = sampleRate;
  gwfh.myBitsPerSample  = sizeof(INT_32)*8;
  gwfh.myDataSize       = length * 1 /*cannels*/ * sizeof(INT_32);
  gwfh.myData           = const_cast<INT_32*>(data);
  // then write the file 
  if (gwfh.write() == false)
    *error = true;
}

//-------------------------------------------------------------------------------------------------

 void WaveFileHandler::asciiToWave(const char* asciiFile, const char* waveFile, const int sampleRate)
 {
    // first read out the ascii file
    vector<string> asciiStringVector;
    ifstream ifs(asciiFile);
    string temp;
    while( getline( ifs, temp ) )
     asciiStringVector.push_back( temp );
    
    // determine the length of the vector and allocate the sample array
    int length = asciiStringVector.size();
    double* d_samples = new double[length];
    
    // convert the line strings to double values and extract the peak
    double peak = DBL_MIN;
    for (int i=0; i<length; i++)
    {
      d_samples[i] = atof(asciiStringVector[i].c_str());
      if (peak < std::abs(d_samples[i]))
        peak = std::abs(d_samples[i]);
    }
    
    // normalize and cast the samples to the full amplitude
    short int* i_samples = new short int[length];
    for (int i=0; i<length; i++)
      i_samples[i] = static_cast<short int>(d_samples[i] / peak * 32767.0);
    
    // save the samples a the wave file
    bool error;
    writeMonoPcm16WaveFile(i_samples, length, sampleRate, waveFile, &error);
 }

//-------------------------------------------------------------------------------------------------

} // namespace io

} // namespace clauer
