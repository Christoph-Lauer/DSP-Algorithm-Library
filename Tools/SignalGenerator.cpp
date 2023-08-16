
/**
 * This simple program generates test singals.
 */

//--------------------------------------------------------------------------------------------------

// C Language Library heades
#include <cmath>

// C++ Language Library headers
#include <iostream>

// local headers  
#include "../Wave File Handler/wave_file_handler.h"
#include "../Math Utilities/math_utilities.h"
#include "../Data Plotter/data_plotter.h"

//--------------------------------------------------------------------------------------------------

// define the basic signal parameters in these macros
#define LENGTH            48000
#define SAMPLERATE        48000
#define EVENT_POINT       24000  
#define DAMPING           10.0

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // the splash screen 
  std::cout << "\n  ********************************************" << std::endl;
  std::cout << "  ***   This is the TEST-SIGNAL-GENERATOR  ***" << std::endl;
  std::cout << "  ***   (c) Christoph Lauer Engineering    ***" << std::endl;
  std::cout << "  ********************************************" << std::endl << std::endl;
  
  // first allocate the test signals
  double* signalConst     = new double[LENGTH];
  double* signalFall      = new double[LENGTH];
  double* signalRise      = new double[LENGTH];
  double* signalRiseZero  = new double[LENGTH];
  double* signalRiseConst = new double[LENGTH];
  double* signal100HzDamped = new double[LENGTH];
  double* signal1000HzDamped = new double[LENGTH];
  
  // next generate the signal data
  for (int i=0; i<LENGTH; i++)
  {
    // for the const singal
    signalConst[i] = 1.0;
    // for the fall signal
    signalFall[i] = exp(static_cast<double>(-i)/static_cast<double>(LENGTH)*DAMPING);
    // for the rise signal  
    signalRise[i] = exp(static_cast<double>(i-LENGTH)/static_cast<double>(LENGTH)*DAMPING);
    // for the rise-zero signal
    signalRiseZero[i]  = exp(static_cast<double>(i-EVENT_POINT)/static_cast<double>(EVENT_POINT)*DAMPING);
    // for the rise-const signal
    signalRiseConst[i] = exp(static_cast<double>(i-EVENT_POINT)/static_cast<double>(EVENT_POINT)*DAMPING);
    if (i>EVENT_POINT)
    {
      // for the rise-zero signal
      signalRiseZero[i]  = 0.0;
      // for the rise-const signal
      signalRiseConst[i] = 1.0;
    }
    signal100HzDamped[i]   = sin(static_cast<double>(i) * 100.0 / static_cast<double>(LENGTH) * TWO_PI) * exp(static_cast<double>(-i)/static_cast<double>(LENGTH)*DAMPING);
    signal1000HzDamped[i] = sin(static_cast<double>(i) * 1000.0 / static_cast<double>(LENGTH) * TWO_PI) * exp(static_cast<double>(-i)/static_cast<double>(LENGTH)*DAMPING);
  }  
  
  // print the generated signals in the data plotter
  clauer::io::DataPlotter::simple2DPlot(signalConst,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","CONST SIGNAL");
  clauer::io::DataPlotter::simple2DPlot(signalFall,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","FALL SIGNAL");
  clauer::io::DataPlotter::simple2DPlot(signalRise,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","RISE SIGNAL");
  clauer::io::DataPlotter::simple2DPlot(signalRiseZero,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","RISE ZERO SIGNAL");
  clauer::io::DataPlotter::simple2DPlot(signalRiseConst,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","RISE CONST SIGNAL");
  clauer::io::DataPlotter::simple2DPlot(signal100HzDamped,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","100Hz DAMPED CONST SIGNAL");
  clauer::io::DataPlotter::simple2DPlot(signal1000HzDamped,LENGTH,0,LENGTH,-1,1,false,false,false,"Samples","Amplitude","1000HZ DAMPED CONST SIGNAL");
  
  // save the signal to files
  bool error = false;
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signalConst,LENGTH,SAMPLERATE,"signalConst.wav",&error);
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signalFall,LENGTH,SAMPLERATE,"signalFall.wav",&error);
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signalRise,LENGTH,SAMPLERATE,"signalRise.wav",&error);
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signalRiseZero,LENGTH,SAMPLERATE,"signalRiseZero.wav",&error);
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signalRiseConst,LENGTH,SAMPLERATE,"signalRiseConst.wav",&error);
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signal100HzDamped,LENGTH,SAMPLERATE,"signal100HzDamped.wav",&error);
  clauer::io::WaveFileHandler::writeMonoFloat64WaveFile(signal1000HzDamped,LENGTH,SAMPLERATE,"signal1000HzDamped.wav",&error);
  
  // clean the grabage
  delete[] signalConst;
  delete[] signalFall;
  delete[] signalRise;
  delete[] signalRiseZero;
  delete[] signalRiseConst;
  
  // report no error to the outside
  return(0);
}

//--------------------------------------------------------------------------------------------------
