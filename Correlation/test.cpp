/**
 * This simple test Program illustrates the usage of the auto/cross-correlation algorithms.
 */

//--------------------------------------------------------------------------------------

// C Langauge Library headers
#include <cmath>
#include <cfloat>

// C++ Langauge Library headers
#include <iostream>

// local headers  
#include "correlation.hpp"
#include "../Data Plotter/data_plotter.h"

//--------------------------------------------------------------------------------------

#define INPUT_SIGNAL_LENGTH 256
#define COMPARISATION_SIGNAL_LENGTH 128

//--------------------------------------------------------------------------------------

using namespace clauer::io;
using namespace std;

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  cout << "Hallo Autocorrelation";
  // first generate the input array
  double* a1 = new double[INPUT_SIGNAL_LENGTH];
  double* a2 = new double[INPUT_SIGNAL_LENGTH];
  double* a3 = new double[COMPARISATION_SIGNAL_LENGTH];
  double* a4 = new double[INPUT_SIGNAL_LENGTH];
  double* a5 = new double[INPUT_SIGNAL_LENGTH];
  for (int i=0; i<INPUT_SIGNAL_LENGTH; i++)
  {
    double x1 = static_cast<double>(i-50);
    double x2 = static_cast<double>(i-100);
    if (x1 == 0.0) x1 = DBL_MIN;
    if (x2 == 0.0) x2 = DBL_MIN;
    a1[i] = std::sin(x1)/x1;
    a2[i] = std::sin(x2)/x2;
    a4[i] = 0.0;
    a5[i] = 0.0;
  }
  for (int i=0; i<COMPARISATION_SIGNAL_LENGTH; i++)
  {
    double x = static_cast<double>(i-100);
    if (x == 0.0) x = DBL_MIN;
    a3[i] = std::sin(x)/x;
  }
  for (int i=10; i<20; i++)
    a4[i] = 1.0;
  for (int i=120; i<150; i++)
    a5[i] = 1.0;
 
  // call the static autocorrelation functions
  double* b = clauer::math::Correlation<double>::autoCorrelation(a1, INPUT_SIGNAL_LENGTH);
  double* c = clauer::math::Correlation<double>::crossCorrelation(a1, INPUT_SIGNAL_LENGTH, a2, INPUT_SIGNAL_LENGTH);
  double* d = clauer::math::Correlation<double>::crossCorrelation(a1, INPUT_SIGNAL_LENGTH, a3, COMPARISATION_SIGNAL_LENGTH);
  double* e = clauer::math::Correlation<double>::crossCorrelation(a2, INPUT_SIGNAL_LENGTH, a3, COMPARISATION_SIGNAL_LENGTH);
  double* f = clauer::math::Correlation<double>::crossCorrelation(a4, INPUT_SIGNAL_LENGTH, a5, INPUT_SIGNAL_LENGTH);
  
  // output the input and result to the data plotter
  DataPlotter plotter;
  plotter.simple2DPlot(a1, INPUT_SIGNAL_LENGTH);
  plotter.simple2DPlot(b, INPUT_SIGNAL_LENGTH*2,-INPUT_SIGNAL_LENGTH,INPUT_SIGNAL_LENGTH,0.0,0.0, false, false, false, "Samples", "Autocorrelation", "Autocorrelation Example", "f(x)=sin(x)/x");
  plotter.simple2DPlot(c, INPUT_SIGNAL_LENGTH*2,-INPUT_SIGNAL_LENGTH,INPUT_SIGNAL_LENGTH,0.0,0.0, false, false, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "f1(x)= sin(x)/x, f2(x-50)=sin(x)/(x)");
  plotter.simple2DPlot(d, (INPUT_SIGNAL_LENGTH+COMPARISATION_SIGNAL_LENGTH),-INPUT_SIGNAL_LENGTH, COMPARISATION_SIGNAL_LENGTH,0.0,0.0, false, false, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "f1(x)= sin(x)/x, f2(x-50)=sin(x)/(x), different length");
  plotter.simple2DPlot(e, (INPUT_SIGNAL_LENGTH+COMPARISATION_SIGNAL_LENGTH),-INPUT_SIGNAL_LENGTH, COMPARISATION_SIGNAL_LENGTH,0.0,0.0, false, true, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "f1(x-50)= sin(x)/x, f2(x-50)=sin(x)/(x), different length");
  plotter.simple2DPlot(f, (INPUT_SIGNAL_LENGTH+COMPARISATION_SIGNAL_LENGTH),-INPUT_SIGNAL_LENGTH, INPUT_SIGNAL_LENGTH,0.0,0.0, false, true, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "step(10,10), step(120,30)");
    
  // now test the pearson correlation
  double roh = clauer::math::Correlation<double>::pearsonCorrelation(a1, a2, INPUT_SIGNAL_LENGTH);
  std::cout << "The Pearsson Correlation between the two signal" << roh << std::endl;
}

//--------------------------------------------------------------------------------------
