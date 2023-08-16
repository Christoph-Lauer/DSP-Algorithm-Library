/**
 * This simple test Program illustrates the usage of the Wavelet Packet Decomposition.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>

// C headers
#include <cstdlib>
#include <cstring>
#include <cstdio>

// local headers  
#include "wavelet_packet_decomposition.h"
#include "../Wave File Handler/wave_file_handler.h"

//--------------------------------------------------------------------------------------

using namespace std;
using namespace clauer::io;
using namespace clauer::math;

//--------------------------------------------------------------------------------------

#define DEFAULT_FILE "../TEST_SIGNALS/harmonicOdd.wav"

//--------------------------------------------------------------------------------------------------

// this is the tree definition corresponds to the MEL tree
const int depth  = 6;
const int tree[] =
{   
	1, 
	1, 1,
	1, 1, 1, 1,
	1, 1, 1, 1, 1, 0, 0, 0,
 	1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 	1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
};

//--------------------------------------------------------------------------------------------------

void generateWaveFile(const char* fileName)
{
  // open the test signal
  bool error = false;
  int length=0, sampleRate=0;
  double* signal = clauer::io::WaveFileHandler::autoReadWaveFile(fileName, length, sampleRate, error);
  cout << "Load " << length << " samples from File " << fileName << " with the Samplerate " << sampleRate << endl;
 
  // init the values for the WPT
	int dimFilter; 
  int numChannels;
	int dimTransformed;
  int dimDescription;
	double* transformed;
	double* low;
	double* high;
	WaveletPacketDecomposition::segmentDescription *description;
  
  // init the WPT here
  WaveletPacketDecomposition::SetFilter("daubechies", 2, &low, &high, &dimFilter);
  WaveletPacketDecomposition::SetVectors(depth, dimFilter, length, &transformed, &dimTransformed, &description);
  WaveletPacketDecomposition::setSampleRate(sampleRate);
  // check for a correct tree
  if (clauer::math::WaveletPacketDecomposition::CheckTree(tree, depth, &numChannels) == 0)
    // call the core algorithm here
    WaveletPacketDecomposition::doWaveletPacketDecomposition(signal, length, low, high, dimFilter, tree, depth, transformed, &dimTransformed, description, &dimDescription);
  else
  {
    printf("ERROR WHILE CHECK THE WAVELET PACKET TREE --> ABORT !!!");
    exit(0);
  }

  // because of display reasons multiplicate the result with one million
  for (int i=0; i<dimDescription; i++)
    description[i].energy *= 1000000.0;
  
  // evaluate the result
  WaveletPacketDecomposition::printDescriptionEnergies(description, dimDescription, depth);
  
  // waste the garbage
  delete[] transformed;
  delete[] description;
}

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  **************************************************************" << std::endl;
  std::cout << "  *** This is the WAVELET-PACKET-DECOMPOSITION test function ***" << std::endl;
  std::cout << "  ***             (c) Christoph Lauer Engineering            ***" << std::endl;
  std::cout << "  **************************************************************" << std::endl << std::endl;
  // check if there was any file given as argument
  if (argc >= 2)
    if(argc == 3 && strcmp("save", argv[2])==0)
    	generateWaveFile(argv[1]);
    else 
      for (int i=1; i<argc; i++)    
      	generateWaveFile(argv[i]);
  else 
    generateWaveFile(DEFAULT_FILE);
    
  // report no error outside
  return(0);
}

//--------------------------------------------------------------------------------------------------
