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
// * @file    zaho_atlas_marks_distribution.h
// * @class   ZahoAtlasMarksDistribution
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implements a Zaho-Atlas-Marks-Distribution (ZAMD)
// * @see     http://en.wikipedia.org/wiki/Cohen's_class_distribution_function
// * @todo    finished and tested so far.
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_ZAHO_ATLAS_MARKS_DISTRIBUTION
//#define CLAUER_MATH_ZAHO_ATLAS_MARKS_DISTRIBUTION

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: Alternate #define macros with the same name cannot be converted to Java:
//#define ISODD(x) ((x/2.0)== ((int)(x/2)) ? 0 : 1)

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This function implements the algorithm for the calculation of a "Zaho-Atlas-Marks-Distribution" 
// * for the given double vector input signal. The function calculates the PWVT  corresponding to the 
// * equation:
// *
// *            /         / t+|tau|/2                                           *
// *            |        |                                 -j2pi f tau          *
// * ZAM(t,f) = | h(tau) |         x(mu+tau/2)x*(mu-tau/2)e            dmu dtau *
// *            |        |                                                      *
// *           /        / t-|tau|/2  
// *                                                    
// 
public class ZahoAtlasMarksDistribution
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
public enum ZAMD_CALCULATION_METHOD
{
  /// The standard real transformation has a bandlimit of SR/4. The image will be mirrored in the middle.
  /// with the aliasing frequency SR/4.
  ZAMD_CALCULATION_METHOD_REAL,
  /// With help of the hilbert tranformation the complex analytic signal can be transformated and the
  /// resulting image no mirroring in the middle. This method will further depress the interferences
  /// from the local autocorrelation functions into the spwvd calculation function.The max. frequency
  /// is the nyquist frequency SR/2.
  ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION,
}

//------------------------------------------------------------------------------------------------

// *
//  * This is the public visible wrapper function for the "Discrete Zaho Atlas Marks Distribution" which
//  * the user of the function can call. The function takes the samples, the length
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
//  *            /         / t+|tau|/2                                           *
//  *            |        |                                 -j2pi f tau          *
//  * ZAM(t,f) = | h(tau) |         x(mu+tau/2)x*(mu-tau/2)e            dmu dtau *
//  *            |        |                                                      *
//  *           /        / t-|tau|/2
//  *  
//  * DEALLOCATION example:
//  * for (int i=0; i<timeLength; i++)
//  *	  delete[] zam[i];
//  * delete zam;
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
  
  public static double** calculateZahoAtlasMarksDistribution(double signal, int length, int resolution)
  {
	  return calculateZahoAtlasMarksDistribution(signal, length, resolution, ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double** calculateZahoAtlasMarksDistribution (const double* signal, const int length, const int resolution, const ZAMD_CALCULATION_METHOD method = ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
  public static double** calculateZahoAtlasMarksDistribution (double signal, int length, int resolution, ZAMD_CALCULATION_METHOD method)
  {
	// first initialize the time domain input structure
	signalRepresentation input = new signalRepresentation();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
	input.real_part = const_cast<Double*>(signal);
	input.length = length;
	input.is_complex = false;
  
	  // in case the analytic response is reqired
	if (method == ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
	{
	  input.is_complex = true;
	  {
		// for input sizes which have not a length from a power of two
		if (GlobalMembersZaho_atlas_marks_distribution.clauer.math.Utilities.isPowerOfTwo(length) == false)
		{
		  int newLength;
		  double zeroPaddedSignal = GlobalMembersZaho_atlas_marks_distribution.clauer.math.Utilities.autoZeroPadding(signal, length, newLength);
		  if (true == true)
		  {
			  System.out.print("Calculate the analytic hilbert function with an extended length of ");
			  System.out.print(newLength);
			  System.out.print(" instead of ");
			  System.out.print(length);
			  System.out.print("\n");
		  }
		  input.imag_part = GlobalMembersZaho_atlas_marks_distribution.clauer.math.Envelope.fastHilbertTransform(zeroPaddedSignal, newLength);
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
		  input.imag_part = GlobalMembersZaho_atlas_marks_distribution.clauer.math.Envelope.fastHilbertTransform(signal, length);
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
	output.N_freq = resolution;
	output.time_instants = new double[length];
	output.zamd = new double[length *resolution];
  
	// normally we have a linear aligned time domain input with equally spaced time points
	for (int i =0; i<length; i++)
	  output.time_instants[i] = (double)(i);
  
	// prepare the time window function
	int WindowT_Length = resolution/20;
	if (WindowT_Length % 2 == 0)
	  WindowT_Length++;
	double[] WindowT = new double[WindowT_Length];
	for (int i =0; i<WindowT_Length; i++)
	  WindowT[i] = 1.0;
	GlobalMembersZaho_atlas_marks_distribution.clauer.math.Utilities.applyHammingWindow(WindowT,WindowT_Length);
	GlobalMembersZaho_atlas_marks_distribution.clauer.math.Normalizer.normalizeAvg(WindowT, WindowT_Length);
	// prepare the frequency window function
	int WindowF_Length = resolution/4;
	if (WindowF_Length % 2 == 0)
	  WindowF_Length++;
	double[] WindowF = new double[WindowF_Length];
	for (int i =0; i<WindowF_Length; i++)
	  WindowF[i] = 1.0;
	GlobalMembersZaho_atlas_marks_distribution.clauer.math.Utilities.applyHammingWindow(WindowF,WindowF_Length);
	GlobalMembersZaho_atlas_marks_distribution.clauer.math.Normalizer.normalizeAvg(WindowF, WindowF_Length);
	if (true == true)
	{
		System.out.print("Filter Time Window Length = ");
		System.out.print(WindowT_Length);
		System.out.print(" Filter Frequency Window Length");
		System.out.print(WindowF_Length);
		System.out.print("\n");
	}
  
	// measure the time for the computation
	clock_t start = new clock_t();
	clock_t end = new clock_t();
	start = clock();
	// call the core algorithm
	RefObject<Double> TempRefObject = new RefObject<Double>(WindowT);
	RefObject<Double> TempRefObject2 = new RefObject<Double>(WindowF);
	algorithmZahoAtlasMarksDistribution(input, TempRefObject, WindowT_Length, TempRefObject2, WindowF_Length, output);
	WindowT = TempRefObject.argvalue;
	WindowF = TempRefObject2.argvalue;
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
		result[i][j] = output.zamd[j + i * output.N_freq];
  
	// waste the grabage
	output.zamd = null;
	if (method == ZAMD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
	  input.imag_part = null;
	output.time_instants = null;
	WindowF = null;
	WindowT = null;
  
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
	public double zamd; // the resulting TFR
  }

//@}

//------------------------------------------------------------------------------------------------

// *
//  * This function implements the algorithmus for the Zaho-Atlas-Marks Distribution (SPWVD).
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
//  * @param  WindowT              Vector containing the points of the time moothing window.
//  * @param  windowT_Length       Number of points of the time window (ODD number !).
//  * @param  WindowF              Vector containing the points of the frequency window.
//  * @param  WindowF_length       Number of points of the window (ODD number !).
//  * @param  tfr                  Matrix containing the resulting TFR (real).
//  * @param  tfr.time_instants    positions of the smoothing window.
//  * @param  tfr.N_time           length of '.time_instants' = number of cols. in the tfr matrix.
//  * @param  tfr.N_freq           Number of frequency bins = number of rows in the tfr matrix.
//  * @return tfr.real_part        The output tfr matrix  (real_part).
//  * 
//  * The following variables are used intenally into the function.
//  * @remarks column, row         variables of displacement in the matrices.
//  * @remarks time                local time-instant variable to compute the tfr.
//  * @remarks half_WindowT_Length half-length of the time smoothing window.
//  * @remarks normT               normalization factor for the time smoothing window.
//  * @remarks half_WindowF_Length half-length of the frequency smoothing window.
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
  
  private static void algorithmZahoAtlasMarksDistribution (signalRepresentation Signal, double[] WindowT, int WindowT_Length, double[] WindowF, int WindowF_Length, timeFrequencyRepresentation tfr)
  {
	  int column;
	  int row;
	  int time;
	int half_WindowT_Length;
	int half_WindowF_Length;
	int taumin;
	int taumax;
	int tau;
	int mumin;
	int mumax;
	int mu;
	double lacf_real; // local autocorrelation function
	double lacf_imag;
	double normT;
	double normF;
	double R1_real;
	double R1_imag;
	double R2_real;
	double R2_imag;
  
   // Test the input variables
	if (tfr.N_freq <= 0)
	  {
		  System.out.print("zam.c : The field tfr.N_freq is not correctly set\n");
		  System.out.print("\n");
		  exit(0);
	  }
	if (tfr.N_time <= 0)
	  {
		  System.out.print("zam.c : The field tfr.N_time is not correctly set\n");
		  System.out.print("\n");
		  exit(0);
	  }
  //C++ TO JAVA CONVERTER TODO TASK: The #define macro ISODD was defined in alternate ways and cannot be replaced in-line:
	if (ISODD(WindowT_Length) == 0)
	  {
		  System.out.print("zam.c : The time-window Length must be an ODD number\n");
		  System.out.print("\n");
		  exit(0);
	  }
  //C++ TO JAVA CONVERTER TODO TASK: The #define macro ISODD was defined in alternate ways and cannot be replaced in-line:
	if (ISODD(WindowF_Length) == 0)
	  {
		  System.out.print("zam.c : The frequency-window Length must be an ODD number\n");
		  System.out.print("\n");
		  exit(0);
	  }
  
	// Determines some internal constants
	half_WindowT_Length = (WindowT_Length - 1) / 2;
	normT =WindowT[half_WindowT_Length];
  
	// normalization of the time smoothing window
	for(row = 0; row < WindowT_Length; row++)
	  WindowT[row] =WindowT[row]/normT;
	half_WindowF_Length = (WindowF_Length - 1) / 2;
	normF =WindowF[half_WindowF_Length];
  
	// normalization of the frequency smoothing window
	for(row = 0 ; row < WindowF_Length ; row++)
	  WindowF[row] =WindowF[row]/normF;
  
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
	  if (true == true)
	  {
		  System.out.print(column);
		  System.out.print(" ");
		  std.cout.flush();
	  }
  
	  // time instants of interest to compute the tfr
	  time = ((int) tfr.time_instants[column]) - 1;
	  taumax = Math.min((time+half_WindowT_Length),(Signal.length-time-1+half_WindowT_Length));
	  taumax = Math.min(taumax,(tfr.N_freq / 2 - 1));
	  taumax = Math.min(taumax, half_WindowF_Length);
  
	  // initialization of the first local autocorrelation function
	  if (Signal.is_complex == true)
	  {
		   lacf_real[0] = Signal.real_part[time] * Signal.real_part[time] + Signal.imag_part[time] * Signal.imag_part[time];
		  // the imag part is always zero because the imag part of any complex 'x * conjugate(x)' is zero */
		  lacf_imag[0] = 0.0;
		}
	  else // the signal is real-valued
		{
		  lacf_real[0] = Signal.real_part[time] * Signal.real_part[time];
		  lacf_imag[0] = 0.0;
		}
  
	  // The signal is windowed around the current time
	  for (tau = 1; tau <= taumax; tau++)
	  {
			R1_real = 0.0;
			R2_real = 0.0;
			R1_imag = 0.0;
			R2_imag = 0.0;
  
		  // bound of mu in order to take into account the edges
		  mumin =Math.min(tau,half_WindowT_Length);
		  mumin =Math.min(mumin,(Signal.length-time-1-tau));
		  mumax =Math.min(tau,half_WindowT_Length);
		  mumax =Math.min(mumax,time-tau);
		for(mu =-mumin;mu<=mumax;mu++)
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
	  tau =Math.floor(tfr.N_freq/2);
	  if ((time<=Signal.length-tau-1)&(time>=tau)&(tau<=half_WindowF_Length))
	  {
		R1_real = 0.0;
		  R2_real = 0.0;
		  R1_imag = 0.0;
		  R2_imag = 0.0;
  
		  // bound of mu in order to take into account the edges
		  mumin =Math.min(tau,half_WindowT_Length);
		  mumin =Math.min(mumin,(Signal.length-time-1-tau));
		  mumax =Math.min(tau,half_WindowT_Length);
		  mumax =Math.min(mumax,time-tau);
		for(mu =-mumin;mu<=mumax;mu++)
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
	  double[] dftRe = new double[tfr.N_freq];
	  double[] dftIm = new double[tfr.N_freq];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(dftRe, 0, sizeof(double) * tfr.N_freq);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(dftIm, 0, sizeof(double) * tfr.N_freq);
  
	  // call Fourier Transformation
	  GlobalMembersZaho_atlas_marks_distribution.clauer.math.FourierTransformation.AutoFT(tfr.N_freq, false, lacf_real, lacf_imag, dftRe, dftIm);
  
	  // the fft is put into the tfr matrix
	  for (row = 0; row < tfr.N_freq; row++)
	  {
		tfr.zamd[row+column *tfr.N_freq] = Math.sqrt(dftRe[row]*dftRe[row]+dftIm[row]*dftIm[row]);
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

} // class ZahoAtlasMarksDistribution

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_ZAHO_ATLAS_MARKS_DISTRIBUTION
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


