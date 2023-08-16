
/**
 * This simple test Program illustrates the usage of the median frequency algorithmus.
 */

//--------------------------------------------------------------------------------------------------

// C Language Library heades
#include <cmath>

// C++ Language Library headers
#include <iostream>

// local headers  
#include "median_frequency.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "../Math Utilities/math_utilities.h"

//--------------------------------------------------------------------------------------------------

#define TEST_FILE "../TEST_SIGNALS/white_noise_band_pass_800_1200.wav"

//--------------------------------------------------------------------------------------------------

void analyseWaveFile(const char* filePath, bool save = false)
{
 // open the test signal
  bool error = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(filePath, length, sampleRate, error);
  // call the algorithm
  double medianFrequency = clauer::math::MedianFrequency::calcualteMedianFrequencyFromTimeDomainSignal(signal, length, sampleRate);
  // give the result out
  char* fileName = clauer::math::Utilities::extractFileNameFromPath(const_cast<char*>(filePath));
  std::cout << "The MEDIAN_FREQUENCY for the file " << fileName << " is " << medianFrequency << " Hz" << std::endl;
  // waste the grabage
  delete[] signal;
}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  **************************************************" << std::endl;
  std::cout << "  *** This is the MEDIAN-FERQUENCY test function ***" << std::endl;
  std::cout << "  ***      (c) Christoph Lauer Engineering        ***" << std::endl;
  std::cout << "  **************************************************" << std::endl << std::endl;
  // check if there was any file given as argument
  if (argc >= 2)
    for (int i=1; i<argc; i++)    
    	analyseWaveFile(argv[i]);
  else 
    analyseWaveFile(TEST_FILE);
  // report no error to the outside
  return(0);
}

//--------------------------------------------------------------------------------------------------
