/**
 * This simple test Program illustrate the usage of the Spectrum algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>

// local headers  
#include "fourier_transformation.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "../Data Plotter/data_plotter.h"
#include "../Math Utilities/math_utilities.h"

//--------------------------------------------------------------------------------------

using namespace std;
using namespace clauer::io;
using namespace clauer::math;

//--------------------------------------------------------------------------------------

#define TEST_FILE_NAME "../TEST_SIGNALS/demo.wav"

//--------------------------------------------------------------------------------------------------

void generateWaveFile(const char* fileName, bool save = false)
{
 // open the test signal
  bool error           = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, length, sampleRate, error);
  
  // call the core algorithm
  int specLength; 
  double* spectrum = clauer::math::FourierTransformation::AutoPS(signal, length, &specLength);
      
  // transform using the complex spectrum
  double* imIn    = new double[length]();
  double* reOut   = new double[length];
  double* imOut   = new double[length];
  double* logSpec = new double[specLength];
  double* phase   = new double[length];
  clauer::math::FourierTransformation::AutoFT(length, false, signal, imIn, reOut, imOut);
  for (int i=0; i<length; i++)
    phase[i]=std::atan(imOut[i]/reOut[i]);

  // lin2log
  logSpec = clauer::math::Utilities::lin2logArray(spectrum, specLength);
  
  // collect the information string
  char infoString1[128];
  sprintf(infoString1, "TestSignal --> Length = %d, FFT-Length = %d, SampleRate = %d", length, specLength, sampleRate);
  char infoString2[128];
  sprintf(infoString2, "TestSignal --> Length = %d, SampleRate = %d", length, sampleRate);
  cout << infoString2 << endl;
  
  // plot the Signal
  clauer::io::DataPlotter::simple2DPlot(signal, length,0,length,0,0,false,false,false,"Time in Samples", "Amplitude" ,"Signal", infoString2, "");
  // plot the RE
  clauer::io::DataPlotter::simple2DPlot(reOut, length,0,sampleRate,0,0,false,false,false,"Sample", "Amplitude" ,"Real", infoString2, "");
  // plot the IM
  clauer::io::DataPlotter::simple2DPlot(imOut, length,0,sampleRate,0,0,false,false,false,"Sample", "Amplitude" ,"Imaginary", infoString2, "");
  // plot the Magnitude Spectrum
  clauer::io::DataPlotter::simple2DPlot(spectrum, specLength,0,sampleRate/2,0,0,false,false,false,"Frequency in Hz", "Amplitude in dB" ,"Lin Power Spectrum", infoString1, "");
  // plot the log Magnitude Spectrum
  clauer::io::DataPlotter::simple2DPlot(logSpec, specLength,0,sampleRate/2,0,0,false,false,false,"Frequency in Hz", "Amplitude in dB" ,"Log Power Spectrum", infoString1, "");
  // plot the Phase Spectrum
  clauer::io::DataPlotter::simple2DPlot(phase, length,0,length,0,0,false,false,true,"Sample", "Phase" ,"Phase Spectrum", infoString2, "");

  
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
