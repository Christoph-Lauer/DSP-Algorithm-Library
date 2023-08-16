/**
 * This simple test Program illustrate the usage of the damping constant algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "damping_constant.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "../Data Plotter/data_plotter.h"
#include "../Envelope/envelope.h"

//--------------------------------------------------------------------------------------

#define WAVE_FILE_NAME_1 "../TEST_SIGNALS/envelope_IO_48000.wav"
#define WAVE_FILE_NAME_2 "../TEST_SIGNALS/envelope_IO_8000.wav"
#define WAVE_FILE_NAME_3 "../TEST_SIGNALS/envelope_NIO_48000.wav"
#define WAVE_FILE_NAME_4 "../TEST_SIGNALS/envelope_NIO_8000.wav"

//--------------------------------------------------------------------------------------

void analyseWaveFile(const char* fileName)
{
  // open the first IO file
  clauer::io::WaveFileHandler wfh;
  bool error           = false;
  int sampleLength     = 0;
  int sampleRate       = 0;
  double* samples = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, sampleLength, sampleRate, error);
  
  // now call the damping factor function
  double dampingConstant;
  double amplitude;
  double peakPosition;
  clauer::math::DampingConstant::proposeDampingConstant(samples, sampleLength, sampleRate, dampingConstant, amplitude, peakPosition);
    
  // call the envelope function for the plot
  double* envelope = clauer::math::Envelope::calculateEnvelope(samples, sampleLength, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);

  // print the result string
  char infoString[128];
  sprintf(infoString,"DampingConstant=%g 1/s, Amplitude=%g, PeakPosition=%gs", dampingConstant, amplitude, peakPosition);
  std::cout << fileName << " --> " << infoString << std::endl;
  
  // draw the plots
  clauer::io::DataPlotter::simple2DPlot(samples, sampleLength,0,0,0,0,false,false,false,"Samples","Amplitude","INPUT SIGNAL", fileName);
  clauer::io::DataPlotter::simple2DPlot(envelope, sampleLength,0,0,0,0,false,false,false,"Samples","Amplitude","ENVELOPE",fileName, infoString);
  
  // waste the grabage
  delete[] envelope;
}

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n**************************************************" << std::endl;
  std::cout << "*** This is the DAMPING CONSTANT test function ***" << std::endl;
  std::cout << "***    (c) Christoph Lauer ENGINEERING         ***" << std::endl;
  std::cout << "**************************************************" << std::endl << std::endl;

  // check if there was any argument given to open and analysze
  if (argc > 1)
  {
    for (int i=2; i<=argc; i++)
      analyseWaveFile(argv[i-1]);
    return(0);
  }
  // doing the standart test here if no argument was given
  else
  {
    analyseWaveFile(WAVE_FILE_NAME_1);
    analyseWaveFile(WAVE_FILE_NAME_2);
    analyseWaveFile(WAVE_FILE_NAME_3);
    analyseWaveFile(WAVE_FILE_NAME_4);
  }
  return(0);
}

//--------------------------------------------------------------------------------------
