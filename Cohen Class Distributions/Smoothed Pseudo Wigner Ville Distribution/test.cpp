
/**
 * This simple test Program illustrate the usage of the wigner-ville-distribution algorithmus.
 */

//--------------------------------------------------------------------------------------------------

// C Language Library heades
#include <cmath>

// C++ Language Library headers
#include <iostream>

// local headers  
#include "smoothed_pseudo_wigner_ville_distribution.h"
#include "../../Wave File Handler/wave_file_handler.h"
#include "../../Data Plotter/data_plotter.h"
#include "../../Math Utilities/math_utilities.h"

//--------------------------------------------------------------------------------------------------

#define TEST_FILE_NAME "../../TEST_SIGNALS/asparagus.wav"
#define DOWNNSCALING_FACTOR 1

//--------------------------------------------------------------------------------------------------

void generateWaveFile(const char* fileName, bool save = false)
{
 // open the test signal
  bool error           = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, length, sampleRate, error);
	// restrict the time points
  if (length > 20000) length = 20000;
  // call the core transformation
  int resolution = 2048;//clauer::math::Utilities::getNextPowerOfTwo(sampleLength);
  // downscaling the time
  length /= DOWNNSCALING_FACTOR;
  // call the core algorithm
  std::cout << "Read " << length << " samples from the wave file and generate " << resolution << " frequency points" << std::endl;
  double** spwvd = clauer::math::SmoothedPseudoWignerVilleDistribution::calculateSmoothedPseudoWignerVilleDistribution(signal, length, resolution);
  // downscaling the frequency resolution
  resolution /= DOWNNSCALING_FACTOR;
  // collect the information string
  char infoString1[128];
  sprintf(infoString1, "Time x Frequency Resolution: %d x %d = %d points", length, resolution/DOWNNSCALING_FACTOR, length *resolution/DOWNNSCALING_FACTOR);
  char infoString2[128];
  sprintf(infoString2, "Duration=%gsec. Sample Rate=%d Samples=%d", static_cast<double>(length) / static_cast<double>(sampleRate), sampleRate, length);
  // plot the result
  clauer::io::DataPlotter::simple3DPlot  (spwvd, length, resolution, 0.0, static_cast<double>(length)/static_cast<double>(sampleRate), 0.0, sampleRate/2/DOWNNSCALING_FACTOR, save, false, false, "Smoothed-Pseudo-Wigner-Ville-Distribution",fileName, infoString1, infoString2,"Countour-SPWVD.png");
  //clauer::io::DataPlotter::extended3DPlot(spwvd, length, resolution, 0.0, static_cast<double>(length)/static_cast<double>(sampleRate), 0.0, sampleRate/2/DOWNNSCALING_FACTOR, save, true , false, "Smoothed-Pseudo-Wigner-Ville-Distribution",fileName, infoString1, infoString2,"Surface-SPWVD.png");
  // waste the grabage
  for (int i=0; i<length; i++)
  	delete[] spwvd[i];
  delete spwvd;
  delete[] signal;
}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  ***************************************************************************" << std::endl;
  std::cout << "  *** This is the SMOOTHED-PSEUDO-WIGNER-VILLE-DISTRIBUTION test function ***" << std::endl;
  std::cout << "  ***                 (c) Christoph Lauer @ENGINEERING                    ***" << std::endl;
  std::cout << "  ***************************************************************************" << std::endl << std::endl;
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

//--------------------------------------------------------------------------------------------------
