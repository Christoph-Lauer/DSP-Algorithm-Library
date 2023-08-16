/**
 * This simple test Program illustrate the usage of the Harmonic Analyser algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>

// C headers
#include <cstring>

// local headers  
#include "../Fourier Transformation/fourier_transformation.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "harmonic_analyse.h"

//--------------------------------------------------------------------------------------

using namespace std;
using namespace clauer::io;
using namespace clauer::math;

//--------------------------------------------------------------------------------------

#define TEST_FILE_NAME_1 "../TEST_SIGNALS/harmonicOdd.wav"
#define TEST_FILE_NAME_2 "../TEST_SIGNALS/harmonicEven.wav"

#define LOWER_SEARCH_BAND_LIMIT   100.0
#define UPPER_SEARCH_BAND_LIMIT   2000.0

//--------------------------------------------------------------------------------------------------

void generateWaveFile(const char* fileName)
{
 // open the test signal
  bool error           = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, length, sampleRate, error);

  // call the core algorithm
  double  firstHarmonic;
  double  firstHarmonicLevel;
  double* harmonicsEXA = HarmonicAnalyse::harmonicAnalyse(signal, length, sampleRate, LOWER_SEARCH_BAND_LIMIT, UPPER_SEARCH_BAND_LIMIT, &firstHarmonic, &firstHarmonicLevel);
  double* harmonicsBOR = HarmonicAnalyse::harmonicAnalyse(signal, length, sampleRate, LOWER_SEARCH_BAND_LIMIT, UPPER_SEARCH_BAND_LIMIT, &firstHarmonic, &firstHarmonicLevel, clauer::math::HarmonicAnalyse::SEARCH_AROUND_RADIUS);
  
  // print the result
  cout << "Search Band = [" << LOWER_SEARCH_BAND_LIMIT << "," << UPPER_SEARCH_BAND_LIMIT << "]" << endl;
  cout << fileName << " --> FundamentalFrequency= " << firstHarmonic << "FundamentalFrequencylevel= " << firstHarmonicLevel << "dB" << endl;
  for (int i=0; i<10; i++)
  {
    double frequency = firstHarmonic * (i+1);
    std::cout << i+1 << "-th Harmonic = " << frequency << "Hz\t --> EXACT_POSITION:" << harmonicsEXA[i] << "dB \tSEARCH_AROUND_RADIUS:" << harmonicsBOR[i] << "dB" << std::endl;
  }
  // waste the grabage
  // delete[] harmonicsEXA;
  // delete[] harmonicsBOR;
}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  ***************************************************" << std::endl;
  std::cout << "  *** This is the HARMONIC-ANALYSER test function ***" << std::endl;
  std::cout << "  ***     (c) Christoph Lauer Engineering         ***" << std::endl;
  std::cout << "  ***************************************************" << std::endl << std::endl;
  // check if there was any file given as argument
  if (argc >= 2)
    if(argc == 3 && strcmp("save", argv[2])==0)
    	generateWaveFile(argv[1]);
    else 
      for (int i=1; i<argc; i++)    
      	generateWaveFile(argv[i]);
  else 
  {
    cout << "ODD (UNGEADE)" << endl;
    generateWaveFile(TEST_FILE_NAME_1);
    cout << endl << "EVEN (GERADE)" << endl;
    generateWaveFile(TEST_FILE_NAME_2);
  }
    
  // report no error outside
  return(0);
}

//--------------------------------------------------------------------------------------
