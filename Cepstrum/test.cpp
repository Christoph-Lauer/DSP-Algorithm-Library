
/**
 * This simple test Program illustrates the usage of the cepstrum algorithmus.
 */

//--------------------------------------------------------------------------------------------------

// C Language Library heades
#include <cmath>

// C++ Language Library headers
#include <iostream>

// local headers  
#include "cepstrum.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "../Math Utilities/math_utilities.h"
#include "../Data Plotter/data_plotter.h"

//--------------------------------------------------------------------------------------------------

#define TEST_FILE "../TEST_SIGNALS/dail_tone.wav"

//--------------------------------------------------------------------------------------------------

void analyseWaveFile(const char* filePath, bool save = false)
{
 // open the test signal
  bool error = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(filePath, length, sampleRate, error);
  // call the algorithm
  double* cepstrum = clauer::math::Cepstrum::calculateRealCepstrum(signal, length);
  // give the result out
  char* fileName = clauer::math::Utilities::extractFileNameFromPath(const_cast<char*>(filePath));
  clauer::io::DataPlotter::simple2DPlot(cepstrum, length, false, false, false, 0,0,0,0, "Time Domain Samples", "Cepstrum Amplitude", "Power Cepstrum", fileName);
  // waste the grabage
  delete[] signal;
}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  **************************************************" << std::endl;
  std::cout << "  ***     This is the CEPSTRUM test function     ***" << std::endl;
  std::cout << "  ***        (c) Christoph Lauer Engineering     ***" << std::endl;
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
