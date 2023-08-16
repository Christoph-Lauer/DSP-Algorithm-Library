/**
 * This simple test Program illustrate the usage of the LPC algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>

// C header
#include <cstring>

// local headers  
#include "linear_predictive_coding.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "../Data Plotter/data_plotter.h"

//--------------------------------------------------------------------------------------

using namespace std;
using namespace clauer::io;
using namespace clauer::math;

//--------------------------------------------------------------------------------------

// lpc spectrum values
#define   LPC_SPEC_COEFFS   16
#define   SPEC_SIZE         2048

//--------------------------------------------------------------------------------------

#define TEST_FILE_NAME "../TEST_SIGNALS/demo.wav"

//--------------------------------------------------------------------------------------------------

void generateWaveFile(const char* fileName, bool save = false)
{
    
  // open the wave file
  int     length      = 0;
  int     sampleRate  = 0;
  bool    fileError   = false;
  double* fileSamples = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, length, sampleRate, fileError);

  // call the core algorithm
  double* lpcSpec = clauer::math::LinearPredictiveCoding::calcualteLpcPowerSpectrumEnvelope(fileSamples, length, LPC_SPEC_COEFFS, SPEC_SIZE, true, false);

  // collect the information string
  char infoString1[128];
  sprintf(infoString1, "Signal-Length = %d, LPC-Length = %d, ,LPC-Coefficients = %d, SampleRate = %d", length, SPEC_SIZE, LPC_SPEC_COEFFS, sampleRate);
  cout << infoString1 << endl;

  // plot the result
  clauer::io::DataPlotter::simple2DPlot(lpcSpec, SPEC_SIZE, 0,sampleRate/2,0,0,false,false,false,"Frequency in Hz", "Amplitude in dB" ,"LPC-POWER-SPECTRUM", infoString1, fileName);

}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  ********************************************************" << std::endl;
  std::cout << "  *** This is the FOURIER-TRANSFORMATION test function ***" << std::endl;
  std::cout << "  ***       (c) Christoph Lauer Engineering            ***" << std::endl;
  std::cout << "  ********************************************************" << std::endl << std::endl;
  // check if there was any file given as argument
  if (argc >= 2)
    if(argc == 3 && strcmp("save", argv[2])==0)
    	generateWaveFile(argv[1], true);
    else 
      for (int i=1; i<argc; i++)    
      	generateWaveFile(argv[i]);
  else 
    generateWaveFile(TEST_FILE_NAME);
  // report no error outside
  return(0);
}

//--------------------------------------------------------------------------------------
