
/**
 * This simple test function illustrates the suage of the sample rate converter.
 * USAGE OF THE RESAMPLER IS VERY EASY:
 * first get an instance of the resampler with:
 * clauer::math::Resampler resampler;
 * then start the convertion routine with:
 * resampler.doResampling(in, out1, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_LINEAR);
 * thats all...
 */

//------------------------------------------------------------------------------------------------
 
// Stl headers
#include <iostream>
#include <cmath>

// Loacal haders
#include "resampler.h"
#include "../Wave File Handler/wave_file_handler.cpp"

// length of the SR convertion
#define LENGTH 1000000

//------------------------------------------------------------------------------------------------

int main (int argc, char **argv)
{
  std::cout << "(c) clauer Resampling test Routine..." << std::endl;
  std::cout << "First read the test sample wave file." << std::endl;

  ///////////////////////////////////////////////////////////////////////////
  // first read the samples from test sweep file
  clauer::io::WaveFileHandler wfh;
  int length;
  int sampleRate;
  bool error = false;
  short int* samples = wfh.readMonoPcm16WaveFile(&length, &sampleRate, "../TEST_SIGNALS/log_sweep_2s.wav", &error);
  if (error == true)
  {
    std::cout << "ERROR while read the wave file !!!" << std::endl;
    exit(0);
  }
  else
    std::cout << "Read the wave file for the test conversion with length " << length << " and sample-rate " << sampleRate << std::endl;
  // konvert the samples to an float array 
  float* fin = new float[length];
  float* fout = new float[100000000];
  // cast the samples to an floating point array
  std::cout << "LÄNGE:" << length << std::endl;
  for (int i=0; i<length; i++)
    fin[i] = static_cast<float>(samples[i]);
    
  ////////////////////////////////////////////////////////////////////////////
  // (1) Convert throught the whole spectrum of sample rates and store the wave files
  std::cout << std::endl << "(1) For the first go throught the whole spectrum of possible sample rates in the converter can convert" << std::endl;
  for (int i=250; i<=512000; i*=2)
  {
    // first call the resampler
    clauer::math::Resampler resampler;
    int newLength = (int) ( (double)length * (double)i / (double)sampleRate );
    std::cout << "NewLength=" << newLength << " Length=" << length << " OldSampleRate=" << sampleRate << " NewSampleRate=" << i << " I=" << i << std::endl;
    resampler.doResampling(fin, fout, length, newLength, clauer::math::Resampler::SRC_SINC_BEST_QUALITY);
    // then build the file name
    std::string  fileName =  "results/SRC_";
    char buf[8];
    sprintf(buf, "%d", i);
    fileName += buf;
    fileName += ".wav";
    // cast the result and save the wave file
    short int samples[newLength];
    for (int j=0; j<newLength; j++)
      samples[j] = (short int)fout[j];
    wfh.writeMonoPcm16WaveFile(samples, newLength, i, fileName.c_str(), &error);
  }
  
  ////////////////////////////////////////////////////////////////////////////
  // (2) Second testDOWNSAMPLES with the five different methods and stores the result into wave files
  std::cout << std::endl << "(2) The second test is DOWNSAMPLING test where the results are stored into wave files" << std::endl;
  std::cout << "Pointer to the array: " << samples << std::endl;
  clauer::math::Resampler resampler;
  // then resample the vector to the half size
  resampler.doResampling(fin, fout, length, length/2, clauer::math::Resampler::SRC_LINEAR);
  // cast the result back 
  for (int i=0; i<length/2; i++)
    samples[i] = (short int)fout[i];
  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_LINEAR.wav", &error);
  resampler.doResampling(fin, fout, length, length/2, clauer::math::Resampler::SRC_ZERO_ORDER_HOLD);
  // cast the result back 
  for (int i=0; i<length/2; i++)
    samples[i] = (short int)fout[i];
  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_ZERO_ORDER_HOLD.wav", &error);
  resampler.doResampling(fin, fout, length, length/2, clauer::math::Resampler::SRC_SINC_FASTEST);
  // cast the result back 
  for (int i=0; i<length/2; i++)
    samples[i] = (short int)fout[i];
  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_SINC_FASTEST.wav", &error);
  resampler.doResampling(fin, fout, length, length/2, clauer::math::Resampler::SRC_SINC_MEDIUM_QUALITY);
  // cast the result back 
  for (int i=0; i<length/2; i++)
    samples[i] = (short int)fout[i];
  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_SINC_MEDIUM_QUALITY.wav", &error);
  resampler.doResampling(fin, fout, length, length/2, clauer::math::Resampler::SRC_SINC_BEST_QUALITY);
  // cast the result back 
  for (int i=0; i<length/2; i++)
    samples[i] = (short int)fout[i];
  wfh.writeMonoPcm16WaveFile(samples, length/2, sampleRate, "results/LogSweep_SRC_SINC_BEST_QUALITY.wav", &error);
  //exit(0);

  //////////////////////////////////////////////////////////////////////////////////////////////////  
  // (3) The last test DOWN and UPSAMPLES and measures the difference between the converter methods
  std::cout << std::endl << "(3) The third and last test measures the differences between the different methods" << std::endl;
  double* in = new double[LENGTH];
  double* out1 = new double[2*LENGTH];
  double* out2 = new double[2*LENGTH];
  double* out3 = new double[2*LENGTH];
  double* out4 = new double[2*LENGTH];
  double* out5 = new double[2*LENGTH];
  for (int i=0; i<LENGTH; i++)
    in[i] = sin(static_cast<double>(i));
  std::cout << "Now some speed test's with the different interpolation methods:" << std::endl;
  std::cout << "SRC_LINEAR...";
  resampler.doResampling(in, out1, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_LINEAR);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_ZERO_ORDER_HOLD...";
  resampler.doResampling(in, out2, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_ZERO_ORDER_HOLD);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_SINC_FASTEST...";
  resampler.doResampling(in, out3, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_SINC_FASTEST);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_SINC_MEDIUM_QUALITY...";
  resampler.doResampling(in, out4, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_SINC_MEDIUM_QUALITY);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_SINC_BEST_QUALITY...";
  resampler.doResampling(in, out5, LENGTH, LENGTH*2, clauer::math::Resampler::SRC_SINC_BEST_QUALITY);
  std::cout << "FINISHED" << std::endl << std::endl;
  // then measure the deltas between the results
  long double delta = 0.0;
  for (int i=0; i<LENGTH*2; i++)
    delta += fabs(out1[i] - out5[i]);
  std::cout << "The overall delta between SRC_LINEAR and SRC_SINC_BEST_QUALITY: " << delta << std::endl; 
  delta = 0.0;
  for (int i=0; i<LENGTH*2; i++)
    delta += fabs(out3[i] - out5[i]);
  std::cout << "The overall delta between SRC_SINC_LOW_QUALITY and SRC_SINC_BEST_QUALITY: " << delta << std::endl; 
  delta = 0.0;
  for (int i=0; i<LENGTH*2; i++)
    delta += fabs(out3[i] - out2[i]);
  std::cout << "The overall delta between SRC_SINC_LOW_QUALITY and SRC_ZERO_ORDER_HOLD: " << delta << std::endl;   for (int i=0; i<LENGTH*2; i++)
  delta = 0.0;
  for (int i=0; i<LENGTH*2; i++)
    delta += fabs(out1[i] - out2[i]);
  std::cout << "The overall delta between SRC_LINEAR and SRC_ZERO_ORDER_HOLD: " << delta << std::endl; 
  // then using different resampling methods while downsampling
  std::cout << "Starting DOWN-sampling to the double sampling rate with " << LENGTH << " Samples" << std::endl;
  std::cout << "SRC_LINEAR...";
  //std::cout << std::cout.flush();
  resampler.doResampling(in, out1, LENGTH, LENGTH/2, clauer::math::Resampler::SRC_LINEAR);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_ZERO_ORDER_HOLD...";
  //std::cout << std::cout.flush();
  resampler.doResampling(in, out2, LENGTH, LENGTH/2, clauer::math::Resampler::SRC_ZERO_ORDER_HOLD);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_SINC_FASTEST...";
  //std::cout << std::cout.flush();
  resampler.doResampling(in, out3, LENGTH, LENGTH/2, clauer::math::Resampler::SRC_SINC_FASTEST);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_SINC_MEDIUM_QUALITY...";
  //std::cout << std::cout.flush();
  resampler.doResampling(in, out4, LENGTH, LENGTH/2, clauer::math::Resampler::SRC_SINC_MEDIUM_QUALITY);
  std::cout << "FINISHED" << std::endl;
  std::cout << "SRC_SINC_BEST_QUALITY...";
  //std::cout << std::cout.flush();
  resampler.doResampling(in, out5, LENGTH, LENGTH/2, clauer::math::Resampler::SRC_SINC_BEST_QUALITY);
  std::cout << "FINISHED" << std::endl << std::endl;
  // then measure the deltas between the results
  delta = 0.0;
  for (int i=0; i<LENGTH/2; i++)
    delta += fabs(out1[i] - out5[i]);
  std::cout << "The overall delta between SRC_LINEAR and SRC_SINC_BEST_QUALITY: " << delta << std::endl; 
  delta = 0.0;
  for (int i=0; i<LENGTH/2; i++)
    delta += fabs(out3[i] - out5[i]);
  std::cout << "The overall delta between SRC_SINC_LOW_QUALITY and SRC_SINC_BEST_QUALITY: " << delta << std::endl; 
  delta = 0.0;
  for (int i=0; i<LENGTH/2; i++)
    delta += fabs(out3[i] - out2[i]);
  std::cout << "The overall delta between SRC_SINC_LOW_QUALITY and SRC_ZERO_ORDER_HOLD: " << delta << std::endl;   for (int i=0; i<LENGTH*2; i++)
  delta = 0.0;
  for (int i=0; i<LENGTH/2; i++)
    delta += fabs(out1[i] - out2[i]);
  std::cout << "The overall delta between SRC_LINEAR and SRC_ZERO_ORDER_HOLD: " << delta << std::endl << std::endl; 
  
  return (0);
}
