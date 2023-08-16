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
#include "zaho_atlas_marks_distribution.h"
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
 
double** ZahoAtlasMarksDistribution::calculateZahoAtlasMarksDistribution
    (
      const double* signal,
      const int length,
      const int resolution,
      const ZAMD_CALCULATION_METHOD method 
    )
{
  // first initialize the time domain input structure
  signalRepresentation input;
  input.real_part  = const_cast<double*>(signal);
  input.length     = length;
  input.is_complex = false;
  
    // in case the analytic response is reqired
  if (method == ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
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
  output.N_freq        = resolution;
  output.time_instants = new double[length];
  output.zamd          = new double[length*resolution];
  
  // normally we have a linear aligned time domain input with equally spaced time points
  for (int i=0; i<length; i++)
    output.time_instants[i] = static_cast<double>(i);
  
  // prepare the time window function
  int WindowT_Length = resolution/20;
  if (WindowT_Length % 2 == 0)
    WindowT_Length++;
  double* WindowT = new double[WindowT_Length];
  for (int i=0; i<WindowT_Length; i++)
    WindowT[i] = 1.0;
  clauer::math::Utilities::applyHammingWindow(WindowT,WindowT_Length);
  clauer::math::Normalizer::normalizeAvg(WindowT, WindowT_Length);
  // prepare the frequency window function
  int WindowF_Length = resolution/4;
  if (WindowF_Length % 2 == 0)
    WindowF_Length++;
  double* WindowF = new double[WindowF_Length];
  for (int i=0; i<WindowF_Length; i++) 
    WindowF[i] = 1.0;
  clauer::math::Utilities::applyHammingWindow(WindowF,WindowF_Length);
  clauer::math::Normalizer::normalizeAvg(WindowF, WindowF_Length);
  if (DEBUG == true) std::cout << "Filter Time Window Length = " << WindowT_Length << " Filter Frequency Window Length" << WindowF_Length << std::endl;
  
  // measure the time for the computation
  clock_t start,end;
  start = clock();
  // call the core algorithm
  algorithmZahoAtlasMarksDistribution(input, WindowT, WindowT_Length , WindowF, WindowF_Length, output);
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
      result[i][j] = output.zamd[ j + i * output.N_freq ];
      
  // waste the grabage
  delete[] output.zamd;
  if (method == ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
    delete[] input.imag_part;
  delete[] output.time_instants;
  delete[] WindowF;
  delete[] WindowT;
  
  // finally give the result back
  return result;
}

//------------------------------------------------------------------------------------------------

void ZahoAtlasMarksDistribution::algorithmZahoAtlasMarksDistribution
      (
        const signalRepresentation Signal, 
        double* WindowT, 
        const int WindowT_Length, 
        double* WindowF, 
        const int WindowF_Length, 
        timeFrequencyRepresentation tfr
      )
{
	int            column, row, time;
  int            half_WindowT_Length, half_WindowF_Length;
  int            taumin, taumax, tau;
  int            mumin, mumax, mu;
  double        *lacf_real, *lacf_imag;/* local autocorrelation function */
  double         normT, normF;
  double         R1_real, R1_imag, R2_real, R2_imag;

 // Test the input variables
  if (tfr.N_freq <= 0)
    { std::cout << "zam.c : The field tfr.N_freq is not correctly set\n" << std::endl; exit(0);}
  if (tfr.N_time <= 0)
    { std::cout << "zam.c : The field tfr.N_time is not correctly set\n" << std::endl; exit(0);}
  if (ISODD(WindowT_Length) == 0)
    { std::cout <<"zam.c : The time-window Length must be an ODD number\n" << std::endl; exit(0); }
  if (ISODD(WindowF_Length) == 0)
    { std::cout << "zam.c : The frequency-window Length must be an ODD number\n" << std::endl; exit(0); }

  // Determines some internal constants
  half_WindowT_Length = (WindowT_Length - 1) / 2;
  normT=WindowT[half_WindowT_Length];
  
  // normalization of the time smoothing window
  for(row = 0; row < WindowT_Length; row++)
    WindowT[row]=WindowT[row]/normT;
  half_WindowF_Length = (WindowF_Length - 1) / 2;
  normF=WindowF[half_WindowF_Length];

  // normalization of the frequency smoothing window
  for(row = 0 ; row < WindowF_Length ; row++)
    WindowF[row]=WindowF[row]/normF;
    
  // memory allocation for the windowed signal
  lacf_real = new double [tfr.N_freq];
  lacf_imag = new double [tfr.N_freq];

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
    taumax = std::min((time+half_WindowT_Length),(Signal.length-time-1+half_WindowT_Length));
    taumax = std::min(taumax,(tfr.N_freq / 2 - 1));
    taumax = std::min(taumax, half_WindowF_Length);

    // initialization of the first local autocorrelation function
    if (Signal.is_complex == true)
    {
   	  lacf_real[0] =   Signal.real_part[time] * Signal.real_part[time] + Signal.imag_part[time] * Signal.imag_part[time];
	    // the imag part is always zero because the imag part of any complex 'x * conjugate(x)' is zero */
	    lacf_imag[0] =  0.0 ;
	  }
    else /* the signal is real-valued */
	  {
	    lacf_real[0] =   Signal.real_part[time] * Signal.real_part[time];
	    lacf_imag[0] =   0.0;
	  }

    // The signal is windowed around the current time
    for (tau = 1; tau <= taumax; tau++)
    {
		  R1_real = 0.0;
		  R2_real = 0.0;
		  R1_imag = 0.0;
		  R2_imag = 0.0;

	    // bound of mu in order to take into account the edges
	    mumin=std::min(tau,half_WindowT_Length);
	    mumin=std::min(mumin,(Signal.length-time-1-tau));
	    mumax=std::min(tau,half_WindowT_Length);
	    mumax=std::min(mumax,time-tau);
      for(mu=-mumin;mu<=mumax;mu++)
	    {
	      if (Signal.is_complex == true)
	      {
		      R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu] + Signal.imag_part[time+tau-mu] * Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R1_imag = R1_imag + (Signal.imag_part[time+tau-mu] * Signal.real_part[time-tau-mu] - Signal.real_part[time+tau-mu] * Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu] + Signal.imag_part[time-tau-mu] * Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R2_imag = R2_imag + (Signal.imag_part[time-tau-mu] * Signal.real_part[time+tau-mu] - Signal.real_part[time-tau-mu] * Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu];
	      }
	      else
	      {
		      R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R1_imag = 0.0;
		      R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R2_imag = 0.0;
	      }
      }
      lacf_real[tau] = R1_real * WindowF[half_WindowF_Length+tau];
      lacf_imag[tau] = R1_imag * WindowF[half_WindowF_Length+tau];
      lacf_real[tfr.N_freq-tau] = R2_real * WindowF[half_WindowF_Length-tau];
      lacf_imag[tfr.N_freq-tau] = R2_imag * WindowF[half_WindowF_Length-tau];
    }
    tau=floor(tfr.N_freq/2);
    if ((time<=Signal.length-tau-1)&(time>=tau)&(tau<=half_WindowF_Length))
    {
      R1_real = 0.0;
	    R2_real = 0.0;
	    R1_imag = 0.0;
	    R2_imag = 0.0;
	    
	    // bound of mu in order to take into account the edges
	    mumin=std::min(tau,half_WindowT_Length);
	    mumin=std::min(mumin,(Signal.length-time-1-tau));
	    mumax=std::min(tau,half_WindowT_Length);
	    mumax=std::min(mumax,time-tau);
      for(mu=-mumin;mu<=mumax;mu++)
	    {
	      if (Signal.is_complex == true)
	      {
		      R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu] + Signal.imag_part[time+tau-mu] * Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R1_imag = R1_imag + (Signal.imag_part[time+tau-mu] * Signal.real_part[time-tau-mu] - Signal.real_part[time+tau-mu] * Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu] + Signal.imag_part[time-tau-mu] * Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R2_imag = R2_imag + (Signal.imag_part[time-tau-mu] * Signal.real_part[time+tau-mu] - Signal.real_part[time-tau-mu] * Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu];
	      }
	      else
	      {
		      R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R1_imag = 0.0;
		      R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu];
		      R2_imag = 0.0;
	      }
      }
      lacf_real[tau] = 0.5*(R1_real * WindowF[half_WindowF_Length+tau] + R2_real * WindowF[half_WindowF_Length-tau]);
      lacf_imag[tau] = 0.5*(R1_imag * WindowF[half_WindowF_Length+tau] + R2_imag * WindowF[half_WindowF_Length-tau]);
    }
    
    // calling here the fourier transformation of the local autocorrelation function
    double* dftRe = new double[tfr.N_freq];
    double* dftIm = new double[tfr.N_freq];
    memset(dftRe, 0, sizeof(double) * tfr.N_freq);
    memset(dftIm, 0, sizeof(double) * tfr.N_freq);

    // call Fourier Transformation
    clauer::math::FourierTransformation::AutoFT(tfr.N_freq, false, lacf_real, lacf_imag, dftRe, dftIm);
    
    // the fft is put into the tfr matrix
    for (row = 0; row < tfr.N_freq; row++)
    {
      tfr.zamd[row+column*tfr.N_freq] = std::sqrt(dftRe[row]*dftRe[row]+dftIm[row]*dftIm[row]);
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
 
} // namespace math

} // namepsace clauer
