/**
 * This simple test Program illustrates the usage of the digital filter algorithm.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// C headers
#include <cstring>

// local headers  
#include "digital_filter.h"
#include "../Wave File Handler/wave_file_handler.h"

//--------------------------------------------------------------------------------------

#define WAVE_FILE_NAME "../TEST_SIGNALS/white_noise.wav"

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n**************************************************" << std::endl;
  std::cout << "***  This is the DIGITAL FILTER test function  ***" << std::endl;
  std::cout << "***      (c) Christoph Lauer Engineering       ***" << std::endl;
  std::cout << "**************************************************" << std::endl << std::endl;

  // open the first IO file
  clauer::io::WaveFileHandler wfh;
  bool error           = false;
  int sampleLength     = 0;
  int sampleRate       = 0;
  short int* si_samples = wfh.readMonoPcm16WaveFile(&sampleLength, &sampleRate, WAVE_FILE_NAME, &error);
  double* samples = new double[sampleLength];
  for (int i=0; i<sampleLength; i++)
    samples[i] = static_cast<double>(si_samples[i]);
  double* samplesCopy = new double[sampleLength];

  std::cout << "Write the five filters results to file..." << std::endl;
  
  // apply the filter function
  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
  clauer::math::DigitalFilter::applyFilter(samplesCopy, sampleLength, sampleRate, clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 8, false);
  // write the result back to another wave file
  for (int i=0; i<sampleLength; i++)
    si_samples[i] = static_cast<short int>(samplesCopy[i]);
  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_low_pass_6000.wav", &error);

  // apply the filter function
  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
  clauer::math::DigitalFilter::applyFilter(samplesCopy, sampleLength, sampleRate, clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 8, true);
  // write the result back to another wave file
  for (int i=0; i<sampleLength; i++)
    si_samples[i] = static_cast<short int>(samplesCopy[i]);
  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_high_pass_6000.wav", &error);

  // apply the filter function
  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
  clauer::math::DigitalFilter::bandPassFilter(samplesCopy, sampleLength, sampleRate, clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 18000.0, 8);
  // write the result back to another wave file
  for (int i=0; i<sampleLength; i++)
    si_samples[i] = static_cast<short int>(samplesCopy[i]);
  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_band_pass_6000_18000.wav", &error);

  // apply the filter function
  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
  clauer::math::DigitalFilter::bandPassFilter(samplesCopy, sampleLength, sampleRate, clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 800.0, 1200.0, 8);
  // write the result back to another wave file
  for (int i=0; i<sampleLength; i++)
    si_samples[i] = static_cast<short int>(samplesCopy[i]);
  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_band_pass_800_1200.wav", &error);

  // apply the filter function
  memcpy(samplesCopy, samples, sampleLength * sizeof(double));
  clauer::math::DigitalFilter::bandStopFilter(samplesCopy, sampleLength, sampleRate, clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF, 6000.0, 18000.0, 8);
  // write the result back to another wave file
  for (int i=0; i<sampleLength; i++)
    si_samples[i] = static_cast<short int>(samplesCopy[i]);
  wfh.writeMonoPcm16WaveFile(si_samples, sampleLength, sampleRate, "../TEST_SIGNALS/white_noise_band_stop_6000_18000.wav", &error);

  // give no error back
  return(0);
}

//--------------------------------------------------------------------------------------
