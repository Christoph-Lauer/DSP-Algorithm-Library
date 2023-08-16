package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    smoothed_speudo_wigner_ville_distribution.h
// * @class   SmoothedPseudoWignerVilleDistribution
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implements a smoothed-pseudo-wigner-ville-distribution (SPWVD)
// * @see     http://en.wikipedia.org/wiki/Wigner_quasi-probability_distribution
// * @see     http://en.wikipedia.org/wiki/Cohen's_class_distribution_function
// * @todo    finished and tested so far.
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    smoothed_speudo_wigner_ville_distribution.h
// * @class   SmoothedPseudoWignerVilleDistribution
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implements a smoothed-pseudo-wigner-ville-distribution (SPWVD)
// * @see     http://en.wikipedia.org/wiki/Wigner_quasi-probability_distribution
// * @see     http://en.wikipedia.org/wiki/Cohen's_class_distribution_function
// * @todo    finished and tested so far.
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_PSEUDO_MARGENAU_HILL_DISTRIBUTION
//#define CLAUER_MATH_PSEUDO_MARGENAU_HILL_DISTRIBUTION

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: Alternate #define macros with the same name cannot be converted to Java:
//#define ISODD(x) ((x/2.0)== ((int)(x/2)) ? 0 : 1)

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This function implements the algorithm for the calculation of a "Discrete Smoothed Pseudo Wigner-
// * Ville-Distribution" for the given double vector input signal. The function calculates the PWVT 
// * corresponding to the equation:
// *
// *              /                                                             *
// *              | h(tau)                                  -j2pi f tau         *
// * PMH(t,f) =   | ------ ( x(t+tau)x*(t) + x(t)x*(t-tau) )e            dtau   *
// *              |   2                                                         *
// *             /                                                      
// 
public class PseudoMargenauHillDistribution
{

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//* 
// * To prevent aliasing the real input signal must be band limited with the nyquist frequency SR/2. 
// * The discrtete wigner ville distribution has a band limitation of SR/4, so normally for complex 
// * signals the input time domain vector must be up samples to the double length. In our case we have a
// * real input signal, so we use the complex transformation with the analytic signal as input which 
// * prevents aliasing at the frequency SR/4 and the mirroring of the image. For this case we can switch
// * between the transformation with the analytic signal ans the real input signal. The upsampling method
// * only required for complex input signals which is not part of out implementation.
// 
public enum PMHD_CALCULATION_METHOD
{
  /// The standard real transformation has a bandlimit of SR/4. The image will be mirrored in the middle.
  /// with the aliasing frequency SR/4.
  PMHD_CALCULATION_METHOD_REAL,
  /// With help of the hilbert tranformation the complex analytic signal can be transformated and the
  /// resulting image no mirroring in the middle. This method will further depress the interferences
  /// from the local autocorrelation functions into the spwvd calculation function.The max. frequency
  /// is the nyquist frequency SR/2.
  PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION,
}

//------------------------------------------------------------------------------------------------

// *
//  * This is the public visible wrapper function for the "Discrete Smoothed Pseudo Discrete Wigner-Ville
//  * -Transformation" which the user of the function can call. The function takes the samples, the length
//  * of the samples and the frequency resolution which should be from a power of two. The frequency 
//  * resolution can be a value which is not a power of two, but then the DFT is used instead of the much
//  * more faster FFT which has a dramatically influence on the calculation time. Two main methods are 
//  * implemented which have different frequency resolution characteristics in relation to the nyquist
//  * frequency. The standard normal wigner-ville-distribution has a frequency resolution of SR/4 which
//  * means for the calculated image a aliasing effect in the middle of the image because the image is
//  * calculated over the frequency range from 0...SR/2. As alternative to the standard real SPWVD the 
//  * complex analytic function with can be transformated. The complex analytic function can be calculated
//  * via the hilbert transformation the the resulting spwvd will not have the restriction to SR/4 and
//  * the aliasing mirror effect in the middle of the screent. The standard real transformation should
//  * only be used if it is known that the sample data input vector has a band limitation of SR/4. Otherwise
//  * the extended calculation method via the complex hilbert analyti function should be used to get the 
//  * best frequency resolution and no antialiasing and mirroring effects in the middle of the image.
//  *
//  *              /                                                             *
//  *              | h(tau)                                  -j2pi f tau         *
//  * PMH(t,f) =   | ------ ( x(t+tau)x*(t) + x(t)x*(t-tau) )e            dtau   *
//  *              |   2                                                         *
//  *             /  
//  *
//  * DEALLOCATION example:
//  * for (int i=0; i<timeLength; i++)
//  *	  delete[] pmh[i];
//  * delete pmh;
//  * 
//  * @param signal       The pointer to the signal time domain input vector.
//  * @param length       The length of the signal time domain input vector.
//  * @param resolution   For performance reasons the required resolution shoule be, but must not have
//  *                     a power of two. A resolution from a power of two dramatically influences the
//  *                     calculation speed.
//  * @param method       The standard complex transformation transformates with the help of an hilbert 
//  *                     transformator the real input signal into its analytic representation and bypasses
//  *                     so the WVD-aliasing frequency from SR/4 which efects the aliasing mirroring 
//  *                     on the image in the middle of the image. With the help of the complex implemented
//  *                     SPWVD, which will be called normally with the analystic representation the 
//  *                     distribution is generated with the aliasing frequency SR/2. The real method 
//  *                     can be taken if it's known that the signal is bandlimited with SR/4.
//  *                     middle of the image with SR/4. The complex transformation with the analytic function 
//  *                     prevents aliasing and has no mirror effect in the middle of the image. 
//  * @return             The resulting time-frequency representation is a two dimensional array from 
//  *                     the size length*resolution and can be accessed via spevd[time][frequency]. 
//  *                     Note that the array will be allocated into this function so no pre allocation 
//  *                     is necessary. Note that the deallocation of this structure every single time
//  *                     bin and the time bin pointer vector itselv must be deallocated.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double** calculatePseudoMargenauHillDistribution(double signal, int length, int resolution)
  {
	  return calculatePseudoMargenauHillDistribution(signal, length, resolution, PMHD_CALCULATION_METHOD.PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double** calculatePseudoMargenauHillDistribution (const double* signal, const int length, const int resolution, const PMHD_CALCULATION_METHOD method = PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
  public static double** calculatePseudoMargenauHillDistribution (double signal, int length, int resolution, PMHD_CALCULATION_METHOD method)
  {
	// first initialize the time domain input structure
	signalRepresentation input = new signalRepresentation();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	input.real_part = const_cast<Double*>(signal);
	input.length = length;
	input.is_complex = false;
  
	  // in case the analytic response is reqired
	if (method == PMHD_CALCULATION_METHOD.PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
	{
	  input.is_complex = true;
	  {
		// for input sizes which have not a length from a power of two
		if (GlobalMembersPseudo_margenau_hill_distribution.clauer.math.Utilities.isPowerOfTwo(length) == false)
		{
		  int newLength;
		  double zeroPaddedSignal = GlobalMembersPseudo_margenau_hill_distribution.clauer.math.Utilities.autoZeroPadding(signal, length, newLength);
		  if (true == true)
		  {
			  System.out.print("Calculate the analytic hilbert function with an extended length of ");
			  System.out.print(newLength);
			  System.out.print(" instead of ");
			  System.out.print(length);
			  System.out.print("\n");
		  }
		  input.imag_part = GlobalMembersPseudo_margenau_hill_distribution.clauer.math.Envelope.fastHilbertTransform(zeroPaddedSignal, newLength);
		  zeroPaddedSignal = null;
		}
		// if the length is from a power of two
		else
		{
		  if (true == true)
		  {
			  System.out.print("Calculate the Hilbert-Transformation for ");
			  System.out.print(length);
			  System.out.print(" samples");
			  System.out.print("\n");
		  }
		  input.imag_part = GlobalMembersPseudo_margenau_hill_distribution.clauer.math.Envelope.fastHilbertTransform(signal, length);
		}
	  }
	}
  
	// print the debug messages
	if (true == true)
	{
	  System.out.print("Allocate ");
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  System.out.print((double)(length *resolution)*sizeof(double)/1024.0/1024.0);
	  System.out.print("MB of memory for the matrix");
	  System.out.print("\n");
	  System.out.print("Calculate ");
	  System.out.print(length);
	  System.out.print(" time bins");
	  System.out.print("\n");
	  if (Utilities.isPowerOfTwo(resolution) == false)
	{
		System.out.print("For faster computation, the frequency-resolution should be a power of two !");
		System.out.print("\n");
	}
	  std.cout.flush();
	}
  
	// next the output time-frequency-representation structure
	timeFrequencyRepresentation output = new timeFrequencyRepresentation();
	output.N_time = length;
	output.N_freq = resolution *2;
	output.time_instants = new double[length];
	output.pmhd = new double[length *resolution *2];
  
	// normally we have a linear aligned time domain input with equally spaced time points
	for (int i =0; i<length; i++)
	  output.time_instants[i] = (double)(i);
  
	 // prepare the frequency window function
	int WindowF_Length = resolution/4;
	if (WindowF_Length % 2 == 0)
	  WindowF_Length++;
	double[] WindowF = new double[WindowF_Length];
	for (int i =0; i<WindowF_Length; i++)
	  WindowF[i] = 1.0;
	GlobalMembersPseudo_margenau_hill_distribution.clauer.math.Utilities.applyHammingWindow(WindowF,WindowF_Length);
	GlobalMembersPseudo_margenau_hill_distribution.clauer.math.Normalizer.normalizeAvg(WindowF, WindowF_Length);
	if (true == true)
	{
		System.out.print(" Filter Frequency Window Length");
		System.out.print(WindowF_Length);
		System.out.print("\n");
	}
  
	// measure the time for the computation
	clock_t start = new clock_t();
	clock_t end = new clock_t();
	start = clock();
	// call the core algorithm
	RefObject<Double> TempRefObject = new RefObject<Double>(WindowF);
	algorithmPseudoMargenauHillDistribution(input, TempRefObject, WindowF_Length, output);
	WindowF = TempRefObject.argvalue;
	// end measure the time for the computation
	end = clock();
	double time = ((double)(end)-(double)(start))/(double)(CLOCKS_PER_SEC);
	if (true == true)
  {
	  System.out.print("\n");
	  System.out.print("Calculation Time = ");
	  System.out.print(time);
	  System.out.print(" Seconds.");
	  System.out.print("\n");
  }
  
	// allocate the result array
	double[][] result = new double[length];
	for (int i =0; i<length; i++)
	  result[i] = new double[resolution];
  
	// copy the one dimensional result to the two dimensional result representation
	for (int i =0; i<length; i++)
	  for (int j =0; j<resolution; j++)
		result[i][j] = output.pmhd[j + i * output.N_freq];
  
	// waste the grabage
	output.pmhd = null;
	if (method == PMHD_CALCULATION_METHOD.PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
	  input.imag_part = null;
	output.time_instants = null;
	WindowF = null;
  
	// finally give the result back
	return result;
  }

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This group of structures hold the input and out structures for the transformation
// 
//@{

//*
// * This struct represents the time signal including the metadata for used internally for the transformation.
// 
  private class signalRepresentation
  {
	public int length; // Length of the signal in points
	public double real_part; // real part of the signal
	public double imag_part; // imaginary part of the signal
	public boolean is_complex; // true for complex signals
  }

// *
//  * This stuct hold the resulting time frequency representation and all the metadata. The resulting 
//  * value of the wigner ville transformation is stored into the real part row by row. The struct is 
//  * only internally used.
//  
  private class timeFrequencyRepresentation
  {
	public int N_freq; // number of freq bins in the TFR matrix
	public int N_time; // number of time_bins in the TFR matrix
	public double time_instants; // instant for each column of the TFR
	public double pmhd; // the resulting TFR
  }

//@}

//------------------------------------------------------------------------------------------------

// *
//  * This function implements the algorithmus for the Pseudo Margenau Hill Distribution (PMHD).
//  *                                                                           
//  *              /        /                                  -j2pi f tau       
//  * SPWVD(t,f) = | h(tau) | g(tau-t) x(mu+tau/2)x*(mu-tau/2)e          dmu dtau 
//  *             /         /                                                     
//  *
//  * This function is real valued. Its computation requires a real or complex signal, a vector 
//  * containing time instants, the number of frequency bins, a time smoothing window and a frequency
//  * smoothing window.
//  *
//  * @param  Signal               This struct holds the signal input data. This is the signal to analyse.
//  * @param  Window               Vector containing the points of the frequency window.
//  * @param  Window_length        Number of points of the window (ODD number !).
//  * @param  tfr                  Matrix containing the resulting TFR (real).
//  * @param  tfr.time_instants    positions of the smoothing window.
//  * @param  tfr.N_time           length of '.time_instants' = number of cols. in the tfr matrix.
//  * @param  tfr.N_freq           Number of frequency bins = number of rows in the tfr matrix.
//  * @return tfr.real_part        The output tfr matrix  (real_part).
//  * 
//  * The following variables are used intenally into the function.
//  * @remarks column, row         variables of displacement in the matrices.
//  * @remarks half_Window_Length half-length of the frequency smoothing window.
//  * @remarks normF               normalization factor for the frequency window.
//  * @remarks tau                 time-lag variable.
//  * @remarks taumax              accound the beginning and the end of the signal, where the window is cut.
//  * @remarks mu                  time-smoothing variable.
//  * @remarks mumin               local time-smoothing variable bounds. Used to take.
//  * @remarks mumax               into accound the beginning and the end of time smoothing procedure.
//  * @remarks lacf_real           Real and imaginary parts of the local autocorrelation.
//  * @remarks lacf_imag           Function of the signal.
//  * @remarks R1_real R1_imag     Used to compute real and imaginary parts of the time
//  * @remarks R1_real R1_imag     Smoothed-windowed local autocorrelation function.
//  * @remarks R2_real R2_imag
//  

  //------------------------------------------------------------------------------------------------
  
  private static void algorithmPseudoMargenauHillDistribution (signalRepresentation Signal, double[] Window, int Window_Length, timeFrequencyRepresentation tfr)
  {
	int column;
	int row;
	int time;
	int half_Window_Length;
	int taumin;
	int taumax;
	int tau;
	double lacf_real; // local autocorrelation function
	double lacf_imag;
	double norm;
  
	// Test the input variables
	if (tfr.N_freq <= 0)
	  {
		  System.out.print("pmh.c : The field tfr.N_freq is not correctly set\n");
		  System.out.print("\n");
		  exit(0);
	  }
	if (tfr.N_time <= 0)
	  {
		  System.out.print("pmh.c : The field tfr.N_time is not correctly set\n");
		  System.out.print("\n");
		  exit(0);
	  }
  //C++ TO JAVA CONVERTER TODO TASK: The #define macro ISODD was defined in alternate ways and cannot be replaced in-line:
	if (ISODD(Window_Length) == 0)
	  {
		  System.out.print("pmh.c : The window Length must be an ODD number\n");
		  System.out.print("\n");
		  exit(0);
	  }
	// Determines some internal constants
	half_Window_Length = (Window_Length - 1) / 2;
	norm = Window[half_Window_Length];
	// normalization of the window
	for(row = 0 ; row < Window_Length ; row++)
		Window[row] =Window[row]/norm;
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
	  if (true == true)
	  {
		  System.out.print(column);
		  System.out.print(" ");
		  std.cout.flush();
	  }
  
		  // time instants of interest to compute the tfr
		  time = ((int) tfr.time_instants[column]) - 1;
		  // taumin and taumax limit the range of tau near the edges
		  (tfr.N_freq / 2 - 1);
		  taumin = half_Window_Length;
		  taumin = Math.min(taumin, (Signal.length - time - 1));
		  taumax = Math.min((tfr.N_freq / 2 - 1), half_Window_Length);
		  taumax = Math.min (taumax, time);
		  // The signal is windowed around the current time
		  for (tau = -taumin; tau <= taumax; tau++)
	  {
		  row = irem((tfr.N_freq+tau), tfr.N_freq);
		  if (Signal.is_complex == true)
		  // case of complex-valued signal
		  {
			lacf_real[row] = (Signal.real_part[time] * Signal.real_part[time - tau] + Signal.imag_part[time] * Signal.imag_part[time - tau]) * Window[tau+half_Window_Length];
			lacf_imag[row] = (Signal.imag_part[time] * Signal.real_part[time - tau] - Signal.real_part[time] * Signal.imag_part[time - tau]) * Window[tau+half_Window_Length];
		  }
		  else
		  // case of real_valued signal
		  {
			lacf_real[row] = (Signal.real_part[time] * Signal.real_part[time - tau]) * Window[tau+half_Window_Length];
			lacf_imag[row]=0.0;
		  }
	  }
	  // calling here the fourier transformation of the local autocorrelation function
	  double[] dftRe = new double[tfr.N_freq];
	  double[] dftIm = new double[tfr.N_freq];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(dftRe, 0, sizeof(double) * tfr.N_freq);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(dftIm, 0, sizeof(double) * tfr.N_freq);
	  // call Fourier Transformation
	  GlobalMembersPseudo_margenau_hill_distribution.clauer.math.FourierTransformation.AutoFT(tfr.N_freq, false, lacf_real, lacf_imag, dftRe, dftIm);
	  // the fft is put into the tfr matrix
	  for (row = 0; row < tfr.N_freq/2; row++)
	  {
		tfr.pmhd[row+column *tfr.N_freq] = Math.sqrt(dftRe[row]*dftRe[row]+dftIm[row]*dftIm[row]);
		lacf_real[row] = 0.0;
		lacf_imag[row] = 0.0;
	  }
	  // and the dft is deleted
	  dftRe = null;
	  dftIm = null;
	}
  
	// waste the grabage
	lacf_real = null;
	lacf_imag = null;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function prevents a division by zero and checks if the quotient is zero. If the diviso is
//  * zero the return value will be zero and not nan.
//  *
//  * @param x     The divisor
//  * @param y     The quotient
//  * @return      Zero if the quotient was zero, and the result of the division if the quotient was not zero.
//  

  //------------------------------------------------------------------------------------------------
  
  private static int irem(double x, double y)
  {
   int result;
   if (y != 0.0)
	 result = (int)(x-y* (int)(x/y));
   else
   {
	 result = 0.0;
	 System.out.print("attempt to divide by 0 !!!");
	 System.out.print("\n");
   }
   return result;
  }

//------------------------------------------------------------------------------------------------

} // class PseudoMargenauHillDistribution

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_PSEUDO_MARGENAU_HILL_DISTRIBUTION
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Langunage Standard Library headers

// C++ language Standard Library headers

//------------------------------------------------------------------------------------------------

// enabled the debug console output
//C++ TO JAVA CONVERTER NOTE: The following #define macro was replaced in-line:
//#define DEBUG true

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------


