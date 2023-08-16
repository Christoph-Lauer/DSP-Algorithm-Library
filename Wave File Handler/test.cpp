/**
 * This simple test Program illustrates the Wave File Handler.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "wave_file_handler.h"

#define LENGTH 480000

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  std::cout << "Hallo Wave File Reader";
  
  // alloc the error value
  bool error = false;
  
  // first lets generate an test vector
  std::cout << "First generate a sample vector." << std::endl;
  double* samples = new double[LENGTH];
  for (int i=0; i<LENGTH; i++)
  {
    samples[i] = sin((double)i);
    if (i<4)
      std::cout << samples[i] << " ";
  }
  std::cout << std::endl;
  
  // now lets write the double file
  clauer::io::WaveFileHandler wfr1;
  wfr1.writeMonoFloat64WaveFile(samples, LENGTH, 48000,"../TEST_SIGNALS/wave_file_handler_test_f_64.wav", &error);
  
  // lets read an file out
  clauer::io::WaveFileHandler wfr2;
  int length;
  int sampleRate;
  double* read = wfr2.readMonoFloat64WaveFile(&length, &sampleRate, "../TEST_SIGNALS/wave_file_handler_test_f_64.wav", &error);
  std::cout << "Read " <<  length << " samples from the file again, here the first 4 samples:" << std::endl;
  for (int i=0; i<4; i++)
    std::cout << read[i] << " ";
  std::cout << std::endl;
  
  // lets read an files metadata
  clauer::io::WaveFileHandler wfr3;
  int channels, bitsPerSample;
  bool pcm;
  wfr3.getWaveFileMetaData("../TEST_SIGNALS/wave_file_handler_test_f_64.wav", sampleRate, length, channels, bitsPerSample, pcm, &error);

  // and now lets write an 8 bit PCM file
  char* pcmVec2 = new char[LENGTH];
  for (int i=0; i<LENGTH; i++)
    pcmVec2[i] = (i%30);
  clauer::io::WaveFileHandler wfh4;
  wfh4.writeMonoPcm8WaveFile(pcmVec2,LENGTH, 48000, "../TEST_SIGNALS/wave_file_handler_test_pcm_8.wav", &error);
  
  // and now lets write an 16 bit PCM file
  short int* pcmVec = new short int[LENGTH];
  for (int i=0; i<LENGTH; i++)
    pcmVec[i] = (i%30) * 100;
  clauer::io::WaveFileHandler wfh5;
  wfh5.writeMonoPcm16WaveFile(pcmVec,LENGTH, 48000, "../TEST_SIGNALS/wave_file_handler_test_pcm_16.wav", &error);

  // test the easy file open interface
  clauer::io::WaveFileHandler wfh6;
  read = wfh6.autoReadWaveFile("../TEST_SIGNALS/wave_file_handler_test_f_64.wav", length, sampleRate, error);
  std::cout << "Read " <<  length << " samples from the 64bit FLOAT file with the Auto fucntion." << std::endl;
  for (int i=0; i<4; i++)
    std::cout << read[i] << " ";
  std::cout << std::endl;
  read = wfh6.autoReadWaveFile("../TEST_SIGNALS/wave_file_handler_test_pcm_16.wav", length, sampleRate, error);
  std::cout << "Read " <<  length << " samples from the 16bit INTEGER file with the Auto fucntion." << std::endl;
  for (int i=0; i<4; i++)
    std::cout << read[i] << " ";
  std::cout << std::endl;
  read = wfh6.autoReadWaveFile("../TEST_SIGNALS/wave_file_handler_test_pcm_8.wav",  length, sampleRate, error);
  std::cout << "Read " <<  length << " samples from the 16bit INTEGER file with the Auto fucntion." << std::endl;
  for (int i=0; i<4; i++)
    std::cout << read[i] << " ";
  std::cout << std::endl;
  
  
  // and finally the error flag handling
  if (error == true)
  {
    std::cout << "There was any kind of ERROR while the file operations !!!" << std::endl;
    return (1);
  }
  if (error == false)
  {
    std::cout << "No Errors...." << std::endl;
    return (0);
  }
  
  
  
}

//--------------------------------------------------------------------------------------
