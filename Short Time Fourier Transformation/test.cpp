/**
 * This simple test Program illustrate the usage of the Spectrogram algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>

// C headers
#include <cstring>

// local headers  
#include "short_time_fourier_transformation.h"
#include "../Wave File Handler/wave_file_handler.h"
#include "../Data Plotter/data_plotter.h"

//--------------------------------------------------------------------------------------

using namespace std;
using namespace clauer::io;
using namespace clauer::math;

//--------------------------------------------------------------------------------------

#define TEST_FILE_NAME "../TEST_SIGNALS/asparagus.wav"
#define DOWNSCALING_FACTOR 1

//--------------------------------------------------------------------------------------------------

void generateWaveFile(const char* fileName, bool save = false)
{
 // open the test signal
  bool error           = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, length, sampleRate, error);
  
	// restrict the time points
  if (length > 20000) length = 20000;

  // call the core algorithm

  std::cout << "Read " << length << " samples from the wave file." << std::endl;
  int timePoints = 0;
  int freqPoints = 0;
  double** stft = ShortTimeFourierTransformation::generateShortTimeFourierTransformation(signal, length, sampleRate, timePoints, freqPoints);

  // downscaling the time
  timePoints /= DOWNSCALING_FACTOR;
  freqPoints /= DOWNSCALING_FACTOR;
    
  // collect the information string
  char infoString1[128];
  sprintf(infoString1, "Time x Frequency Resolution: %d x %d = %d points", timePoints, freqPoints, timePoints * freqPoints );
  char infoString2[128];
  sprintf(infoString2, "Duration=%gsec. Sample Rate=%d Samples=%d", static_cast<double>(length/DOWNSCALING_FACTOR) / static_cast<double>(sampleRate), sampleRate, length/DOWNSCALING_FACTOR);
	cout << infoString1 << endl << infoString2 << endl;

  // plot the result
  clauer::io::DataPlotter::simple3DPlot  (stft, timePoints, freqPoints, 0.0, static_cast<double>(length)/static_cast<double>(sampleRate), 0.0, sampleRate/2/DOWNSCALING_FACTOR, save, false, false, "Short-Time-Fourier-Transformation",fileName, infoString1, infoString2,"Contour-STFT.png");
  //clauer::io::DataPlotter::extended3DPlot(stft, timePoints, freqPoints, 0.0, static_cast<double>(length)/static_cast<double>(sampleRate), 0.0, sampleRate/2/DOWNSCALING_FACTOR, save, false, false, "Short-Time-Fourier-Transformation",fileName, infoString1, infoString2,"Surface-STFT.png");

  // waste the grabage
  for (int i=0; i<timePoints; i++)
  	delete[] stft[i];
  delete stft;
  delete[] signal;
}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  *******************************************************************" << std::endl;
  std::cout << "  *** This is the SHORT-TIME-FOURIER-TRANSFORMATION test function ***" << std::endl;
  std::cout << "  ***                (c) Christoph Lauer Engineering              ***" << std::endl;
  std::cout << "  *******************************************************************" << std::endl << std::endl;
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
