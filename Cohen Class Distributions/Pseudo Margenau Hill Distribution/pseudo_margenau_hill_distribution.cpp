/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    smoothed_speudo_wigner_ville_distribution.h
 * @class   SmoothedPseudoWignerVilleDistribution
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This class implements a smoothed-pseudo-wigner-ville-distribution (SPWVD)
 * @see     http://en.wikipedia.org/wiki/Wigner_quasi-probability_distribution
 * @see     http://en.wikipedia.org/wiki/Cohen's_class_distribution_function
 * @todo    finished and tested so far.
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "pseudo_margenau_hill_distribution.h"
#include "../../Fourier Transformation/fourier_transformation.h"
#include "../../Math Utilities/math_utilities.h"
#include "../../Envelope/envelope.h"
#include "../../Normalization/normalizer.h"

// C Langunage Standard Library headers
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <cstdio>

// C++ language Standard Library headers
#include <iostream>

//------------------------------------------------------------------------------------------------

// enabled the debug console output
#define DEBUG true

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------
 
double** PseudoMargenauHillDistribution::calculatePseudoMargenauHillDistribution
    (
      const double* signal,
      const int length,
      const int resolution,
      const PMHD_CALCULATION_METHOD method 
    )
{
  // first initialize the time domain input structure
  signalRepresentation input;
  input.real_part  = const_cast<double*>(signal);
  input.length     = length;
  input.is_complex = false;
  
    // in case the analytic response is reqired
  if (method == PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
  {
    input.is_complex = true;
    {
      // for input sizes which have not a length from a power of two
      if (clauer::math::Utilities::isPowerOfTwo(length) == false)
      {
        int newLength;
        double* zeroPaddedSignal = clauer::math::Utilities::autoZeroPadding(signal, length, &newLength);
        if (DEBUG == true) std::cout << "Calculate the analytic hilbert function with an extended length of " << newLength << " instead of " << length << std::endl;
        input.imag_part = clauer::math::Envelope::fastHilbertTransform(zeroPaddedSignal, newLength);
        delete[] zeroPaddedSignal;
      }
      // if the length is from a power of two
      else
      {
        if (DEBUG == true) std::cout << "Calculate the Hilbert-Transformation for " << length << " samples" << std::endl;
        input.imag_part = clauer::math::Envelope::fastHilbertTransform(signal, length);
      }
    }
  }

  // print the debug messages
  if (DEBUG == true)
  { 
    std::cout << "Allocate " << static_cast<double>(length*resolution)*sizeof(double)/1024.0/1024.0 << "MB of memory for the matrix" << std::endl;
    std::cout << "Calculate " << length << " time bins" << std::endl;
    if (Utilities::isPowerOfTwo(resolution) == false)
      std::cout << "For faster computation, the frequency-resolution should be a power of two !" << std::endl;
    std::cout.flush();
  }
  
  // next the output time-frequency-representation structure
  timeFrequencyRepresentation output;
  output.N_time        = length;
  output.N_freq        = resolution*2;
  output.time_instants = new double[length];
  output.pmhd          = new double[length*resolution*2];
  
  // normally we have a linear aligned time domain input with equally spaced time points
  for (int i=0; i<length; i++)
    output.time_instants[i] = static_cast<double>(i);
  
   // prepare the frequency window function
  int WindowF_Length = resolution/4;
  if (WindowF_Length % 2 == 0)
    WindowF_Length++;
  double* WindowF = new double[WindowF_Length];
  for (int i=0; i<WindowF_Length; i++) 
    WindowF[i] = 1.0;
  clauer::math::Utilities::applyHammingWindow(WindowF,WindowF_Length);
  clauer::math::Normalizer::normalizeAvg(WindowF, WindowF_Length);
  if (DEBUG == true) std::cout << " Filter Frequency Window Length" << WindowF_Length << std::endl;
  
  // measure the time for the computation
  clock_t start,end;
  start = clock();
  // call the core algorithm
  algorithmPseudoMargenauHillDistribution(input, WindowF, WindowF_Length, output);
  // end measure the time for the computation
  end = clock();
  double time = (static_cast<double>(end)-static_cast<double>(start))/static_cast<double>(CLOCKS_PER_SEC);
  if (DEBUG == true)
    std::cout << std::endl << "Calculation Time = " << time << " Seconds." << std::endl;

  // allocate the result array
  double** result = new double*[length];
  for (int i=0; i<length; i++)
    result[i] = new double[resolution];
  
  // copy the one dimensional result to the two dimensional result representation
  for (int i=0; i<length; i++)
    for (int j=0; j<resolution; j++)
      result[i][j] = output.pmhd[ j + i * output.N_freq ];
      
  // waste the grabage
  delete[] output.pmhd;
  if (method == PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
    delete[] input.imag_part;
  delete[] output.time_instants;
  delete[] WindowF;
  
  // finally give the result back
  return result;
}

//------------------------------------------------------------------------------------------------

void PseudoMargenauHillDistribution::algorithmPseudoMargenauHillDistribution
      (
        const signalRepresentation Signal, 
        double* Window, 
        const int Window_Length, 
        timeFrequencyRepresentation tfr
      )
{
  int            column, row, time;
  int            half_Window_Length;
  int            taumin, taumax, tau;
  double         *lacf_real, *lacf_imag;  // local autocorrelation function
  double         norm;

  // Test the input variables
  if (tfr.N_freq <= 0)
    { std::cout << "pmh.c : The field tfr.N_freq is not correctly set\n" << std::endl; exit(0); }
  if (tfr.N_time <= 0)
    { std::cout << "pmh.c : The field tfr.N_time is not correctly set\n" << std::endl; exit(0); }
  if (ISODD(Window_Length) == 0)
    { std::cout << "pmh.c : The window Length must be an ODD number\n" << std::endl; exit(0); }
  // Determines some internal constants
  half_Window_Length = (Window_Length - 1) / 2;
  norm = Window[half_Window_Length];
  // normalization of the window
  for(row = 0 ; row < Window_Length ; row++)
      Window[row]=Window[row]/norm;
  // memory allocation for the windowed signal
  lacf_real = new double[tfr.N_freq];
  lacf_imag = new double[tfr.N_freq];
  // initialization of the intermediary vectors
  for (row = 0; row < tfr.N_freq ; row++)
  {
    lacf_real[row] = 0.0;
    lacf_imag[row] = 0.0;
  }
  // computation of the fft for the current windowed signal
  for (column = 0; column < tfr.N_time; column++)
  {
  	// give out a short debug message
    if (DEBUG == true) { std::cout << column << " "; std::cout.flush();}
    
		// time instants of interest to compute the tfr
		time = ((int) tfr.time_instants[column]) - 1;
		// taumin and taumax limit the range of tau near the edges
		taumin = ( (tfr.N_freq / 2 - 1), half_Window_Length );
		taumin = std::min( taumin, (Signal.length - time - 1) );
		taumax = std::min( (tfr.N_freq / 2 - 1), half_Window_Length );
		taumax = std::min (taumax, time);
		// The signal is windowed around the current time
		for (tau = -taumin; tau <= taumax; tau++)
    {
	    row = irem( (tfr.N_freq+tau), tfr.N_freq );
	    if (Signal.is_complex == true)
	    // case of complex-valued signal
	    {
	      lacf_real[row] = ( Signal.real_part[time] * Signal.real_part[time - tau] + Signal.imag_part[time] * Signal.imag_part[time - tau]) * Window[tau+half_Window_Length];
	      lacf_imag[row] = ( Signal.imag_part[time] * Signal.real_part[time - tau] - Signal.real_part[time] * Signal.imag_part[time - tau]) * Window[tau+half_Window_Length];
	    }
	    else
	    // case of real_valued signal
	    {
	      lacf_real[row]= ( Signal.real_part[time] * Signal.real_part[time - tau]) * Window[tau+half_Window_Length];
	      lacf_imag[row]=0.0;
	    }
    }
    // calling here the fourier transformation of the local autocorrelation function
    double* dftRe = new double[tfr.N_freq];
    double* dftIm = new double[tfr.N_freq];
    memset(dftRe, 0, sizeof(double) * tfr.N_freq);
    memset(dftIm, 0, sizeof(double) * tfr.N_freq);
    // call Fourier Transformation
    clauer::math::FourierTransformation::AutoFT(tfr.N_freq, false, lacf_real, lacf_imag, dftRe, dftIm);
    // the fft is put into the tfr matrix
    for (row = 0; row < tfr.N_freq/2; row++)
    {
      tfr.pmhd[row+column*tfr.N_freq] = std::sqrt(dftRe[row]*dftRe[row]+dftIm[row]*dftIm[row]);
      lacf_real[row] = 0.0;
      lacf_imag[row] = 0.0;
    }
    // and the dft is deleted
    delete[] dftRe;
    delete[] dftIm;
  }
  
  // waste the grabage
  delete[] lacf_real;
  delete[] lacf_imag;
}

//------------------------------------------------------------------------------------------------

int PseudoMargenauHillDistribution::irem( double x, double y)
{
 int result;
 if (y != 0.0)
   result =  static_cast<int>(x-y* (int)(x/y));
 else
 {
   result = 0.0;
   std::cout << "attempt to divide by 0 !!!" << std::endl;
 }
 return result;
}

//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
