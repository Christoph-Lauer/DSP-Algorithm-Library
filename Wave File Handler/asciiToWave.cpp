
// local headers
#include "wave_file_handler.h"

// C++ Language Library headers
#include <iostream>

int main (int argc, char *argv[])
{
  // the splash screen 
  std::cout << "\n  ***************************************" << std::endl;
  std::cout << "  ***   ASCII to WAVE FILE CONVERTER  ***" << std::endl;
  std::cout << "  *** (c) Christoph Lauer Engineering ***" << std::endl;
  std::cout << "  ***************************************" << std::endl << std::endl;
  
  // test and extract the arguments
  if (argc != 4)
  {
    std::cout << "USAGE: asciiToWave [ascii path] [wave path] [sample rate]";
    exit(0);
  }   
  std::cout << "ASCCI FILE PATH = " << argv[1] << std::endl;
  std::cout << "WAVE  FILE PATH = " << argv[2] << std::endl;
  // get the sample rate
  int sampleRate = atoi(argv[3]);
  std::cout << "SAMPLERATE      = " << sampleRate << std::endl;
  
  // call the convert function
  clauer::io::WaveFileHandler::asciiToWave(argv[1], argv[2], sampleRate);

  // give no error back
  return(0);
}
