/**
 * This simple test Program illustrates the usage of the Jitter Analysis algorithmus.
 */

//--------------------------------------------------------------------------------------

// C headers
#include <cstdlib>

// Local headers  
#include "jitter_analysis.h"
#include "../Wave File Handler/wave_file_handler.h"

// Input/Output Stream Library headers
#include <iostream>
using namespace std;

//--------------------------------------------------------------------------------------

// macro definitions for the jitter analysis
#define PATH            "../TEST_SIGNALS/small_gear_nio.wav"
#define LPC_PREVIOUS    1000
#define LPC_SPEC_SIZE   8192
#define LPC_WIN_SHIFT   500
#define LPC_COEFFS      24
#define LOWER_BAND      3000
#define UPPER_BAND      3600

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  std::cout << "Hallo Jitter Analysis" << std::endl;

  // first read an wave file and store the vector
  int length;
  int sampleRate;
  bool error = false;
  clauer::io::WaveFileHandler wfh;
  short int* read = wfh.readMonoPcm16WaveFile(&length, &sampleRate, PATH, &error);
  // check if the file could redulary opened
  if (error == true)
  {
    cout << "Could not open the wave file, break here..." << endl;
    exit(127);
  }
  // cast the array to an double array
  double* samples = new double[length];
  for (int i=0; i<length; i++)
    samples[i] = static_cast<double>(read[i]);
  cout << "Read " << length << " samples from file " << PATH << endl;
  delete read;
  
  // now call the jitter routine
  int n;
  cout << "Start two analysis..." << endl;
  double* jitterDiff = clauer::math::JitterAnalysis::jitterAnalysis(samples, length, LPC_PREVIOUS, LPC_SPEC_SIZE, LPC_WIN_SHIFT, LPC_COEFFS, sampleRate, LOWER_BAND, UPPER_BAND, &n, false);
  cout << "FIRST Analysis finished..." << endl;
  double* jitterPeak = clauer::math::JitterAnalysis::jitterAnalysis(samples, length, LPC_PREVIOUS, LPC_SPEC_SIZE, LPC_WIN_SHIFT, LPC_COEFFS, sampleRate, LOWER_BAND, UPPER_BAND, &n, true);
  cout << "SECOND Analysis finished..." << endl;
  
  // now give the result out
  for(int i=0; i<n; i++)
  {
    cout << "Jitter [" << i << "] --> DIFF: " << jitterDiff[i] << "        \tPEAK: " << jitterPeak[i] << endl;
  }
  
  
  // no error occured
  return (0);
}

//--------------------------------------------------------------------------------------
