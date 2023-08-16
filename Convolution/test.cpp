/**
 * This simple test Program illustrates the usage of the convolution algorithmus.
 */

//--------------------------------------------------------------------------------------------------

// C Language Library heades
#include <cmath>
#include <cstring>

// C++ Language Library headers
#include <iostream>

// local headers  
#include "convolution.h"
#include "../Data Plotter/data_plotter.h"


//--------------------------------------------------------------------------------------------------

#define LENGTH     1000
#define RECT_BEGIN 100
#define RECT_END   200

//--------------------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n  *********************************************" << std::endl;
  std::cout << "  *** This is the CONVOLUTION test function ***" << std::endl;
  std::cout << "  ***    (c) Christoph Lauer Engineering    ***" << std::endl;
  std::cout << "  *********************************************" << std::endl << std::endl;
  
  // generate the test signals
  double* rect_signal_1 = new double[LENGTH];
  double* rect_signal_2 = new double[LENGTH];
  double* sin_signal_1 = new double[LENGTH];
  double* sin_signal_2 = new double[LENGTH];
  memset(rect_signal_1, 0, LENGTH*sizeof(double));
  memset(rect_signal_2, 0, LENGTH*sizeof(double));
  memset(sin_signal_1, 0, LENGTH*sizeof(double));
  memset(sin_signal_2, 0, LENGTH*sizeof(double));
  for (int i=RECT_BEGIN; i<RECT_END; i++)
  {
    rect_signal_1[i] = 1.0;
    rect_signal_2[i] = 1.0;
    sin_signal_1[i] = std::sin(static_cast<double>(i-RECT_BEGIN)/10.0);
    sin_signal_2[i] = std::cos(static_cast<double>(i-RECT_BEGIN)/10.0);
  }

  
  /////////////////////////////////////////////////////////////
  // CONVOLVE THE RECTANGE SIGNALS
  
  // call the core algorithm
  double* convolution_f = clauer::math::Convolution::calculateFastConvolution(rect_signal_1, rect_signal_2, LENGTH);
  double* convolution_s = clauer::math::Convolution::calculateStandardConvolution(rect_signal_1, rect_signal_2, LENGTH);
  // print the result
  clauer::io::DataPlotter::simple2DPlot(rect_signal_1, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 1");
  clauer::io::DataPlotter::simple2DPlot(rect_signal_2, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 2");
  clauer::io::DataPlotter::simple2DPlot(convolution_f, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Fast Convolution Result");
  clauer::io::DataPlotter::simple2DPlot(convolution_s, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Standard Convolution Result");
  // call the core algorithm
  convolution_f = clauer::math::Convolution::calculateFastConvolution(rect_signal_2, rect_signal_1, LENGTH);
  convolution_s = clauer::math::Convolution::calculateStandardConvolution(rect_signal_2, rect_signal_1, LENGTH);
  // print the result
  clauer::io::DataPlotter::simple2DPlot(rect_signal_1, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 1");
  clauer::io::DataPlotter::simple2DPlot(rect_signal_2, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 2");
  clauer::io::DataPlotter::simple2DPlot(convolution_f, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Fast Convolution Result");
  clauer::io::DataPlotter::simple2DPlot(convolution_s, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Standard Convolution Result");

  
  /////////////////////////////////////////////////////////////
  // CONVOLVE THE SIN SIGNALS
  
  // call the core algorithm
  convolution_f = clauer::math::Convolution::calculateFastConvolution(sin_signal_1, sin_signal_2, LENGTH);
  convolution_s = clauer::math::Convolution::calculateStandardConvolution(sin_signal_1, sin_signal_2, LENGTH);
  // print the result
  clauer::io::DataPlotter::simple2DPlot(sin_signal_1, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 1");
  clauer::io::DataPlotter::simple2DPlot(sin_signal_2, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 2");
  clauer::io::DataPlotter::simple2DPlot(convolution_f, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Fast Convolution Result");
  clauer::io::DataPlotter::simple2DPlot(convolution_s, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Standard Convolution Result");
  // call the core algorithm
  convolution_f = clauer::math::Convolution::calculateFastConvolution(sin_signal_2, sin_signal_1, LENGTH);
  convolution_s = clauer::math::Convolution::calculateStandardConvolution(sin_signal_2, sin_signal_1, LENGTH);
  // print the result
  clauer::io::DataPlotter::simple2DPlot(sin_signal_1, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 1");
  clauer::io::DataPlotter::simple2DPlot(sin_signal_2, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Input Signal 2");
  clauer::io::DataPlotter::simple2DPlot(convolution_f, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Fast Convolution Result");
  clauer::io::DataPlotter::simple2DPlot(convolution_s, LENGTH, false, false, false, 0,0,0,0,  "Samples", "Aplitude", "Standard Convolution Result");

  // report no error to the outside
  return(0);
}

//--------------------------------------------------------------------------------------------------

