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

//*
// * This function implements the algorithm for the calculation of a "Discrete Smoothed Pseudo Wigner-
// * Ville-Distribution" for the given double vector input signal. The function calculates the PWVT 
// * corresponding to the equation:
// *
// *             /        /                                  -j2pi f tau       
// * SPWV(t,f) = | h(tau) | g(tau-t) x(mu+tau/2)x*(mu-tau/2)e          dmu dtau 
// *             /        /                                                     
// 
public class SmoothedPseudoWignerVilleDistribution
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
public enum SPWVD_CALCULATION_METHOD
{
  /// The standard real transformation has a bandlimit of SR/4. The image will be mirrored in the middle.
  /// with the aliasing frequency SR/4.
  SPWVD_CALCULATION_METHOD_REAL,
  /// With help of the hilbert tranformation the complex analytic signal can be transformated and the
  /// resulting image no mirroring in the middle. This method will further depress the interferences
  /// from the local autocorrelation functions into the spwvd calculation function.The max. frequency
  /// is the nyquist frequency SR/2.
  SPWVD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION,
}

//------------------------------------------------------------------------------------------------

// *
//  * This is the public visible wrapper function for the "Discrete Smoothed Pseudo Discrete Wigner-Ville
//  * -Distribution" which the user of the function can call. The function takes the samples, the length
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
//  *             /        /                                  -j2pi f tau       
//  * SPWV(t,f) = | h(tau) | g(tau-t) x(mu+tau/2)x*(mu-tau/2)e          dmu dtau 
//  *             /        /  
//  *
//  * DEALLOCATION example:
//  * for (int i=0; i<timeLength; i++)
//  *	  delete[] spwvd[i];
//  * delete spwvd;
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
  
  public static double[][] calculateSmoothedPseudoWignerVilleDistribution(double[] signal, int length, int resolution)
  {
	  return calculateSmoothedPseudoWignerVilleDistribution(signal, length, resolution, SPWVD_CALCULATION_METHOD.SPWVD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double** calculateSmoothedPseudoWignerVilleDistribution (const double* signal, const int length, const int resolution, const SPWVD_CALCULATION_METHOD method = SPWVD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
  public static double[][] calculateSmoothedPseudoWignerVilleDistribution (double[] signal, int length, int resolution, SPWVD_CALCULATION_METHOD method)
  {
	// first initialize the time domain input structure
	signalRepresentation input = new signalRepresentation();
	input.real_part = signal;
	input.length = length;
	input.is_complex = false;
  
	  // in case the analytic response is reqired
	if (method == SPWVD_CALCULATION_METHOD.SPWVD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION)
	{
	  input.is_complex = true;
	  {
		// for input sizes which have not a length from a power of two
		if (isPowerOfTwo(length) == false)
		{
		  int newLength = getNextPowerOfTwo(length);
		  double[] zeroPaddedSignal = autoZeroPadding(signal, length);
		  if (true == true)
		  {
			  System.out.print("Calculate the analytic hilbert function with an extended length of ");
			  System.out.print(newLength);
			  System.out.print(" instead of ");
			  System.out.print(length);
			  System.out.print("\n");
		  }
		  input.imag_part = fastHilbertTransform(zeroPaddedSignal, newLength);
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
		  input.imag_part = fastHilbertTransform(signal, length);
		}
	  }
	}
  
	// print the debug messages
	if (true == true)
	{
	  System.out.print("Allocate ");
	  System.out.print((double)(length *resolution)*8/1024.0/1024.0);
	  System.out.print("MB of memory for the matrix");
	  System.out.print("\n");
	  System.out.print("Calculate ");
	  System.out.print(length);
	  System.out.print(" time bins");
	  System.out.print("\n");
	  if (isPowerOfTwo(resolution) == false)
	  {
	  	System.out.print("For faster computation, the frequency-resolution should be a power of two !");
	  	System.out.print("\n");
	  }
	}
  
	// next the output time-frequency-representation structure
	tfr = new timeFrequencyRepresentation();
	tfr.N_time = length;
	tfr.N_freq = resolution;
	tfr.time_instants = new double[length];
	tfr.spwvd = new double[length][resolution];
  
	// normally we have a linear aligned time domain input with equally spaced time points
	for (int i =0; i<length; i++)
	  tfr.time_instants[i] = (double)(i);
  
	// prepare the time window function
	int WindowT_Length = resolution/20;
	if (WindowT_Length % 2 == 0)
	  WindowT_Length++;
	double[] WindowT = new double[WindowT_Length];
	for (int i =0; i<WindowT_Length; i++)
	  WindowT[i] = 1.0;
	WindowT = applyHammingWindow(WindowT,WindowT_Length);
	WindowT = normalizeAvg(WindowT, WindowT_Length);
	// prepare the frequency window function
	int WindowF_Length = resolution/4;
	if (WindowF_Length % 2 == 0)
	  WindowF_Length++;
	double[] WindowF = new double[WindowF_Length];
	for (int i =0; i<WindowF_Length; i++)
	  WindowF[i] = 1.0;
	WindowF = applyHammingWindow(WindowF,WindowF_Length);
	WindowF = normalizeAvg(WindowF, WindowF_Length);
	if (true == true)
	{
		System.out.print("Filter Time Window Length = ");
		System.out.print(WindowT_Length);
		System.out.print(" Filter Frequency Window Length");
		System.out.print(WindowF_Length);
		System.out.print("\n");
	}


	// call the core algorithm
	algorithmSmoothedPseudoWignerVilleDistribution(input, WindowT, WindowT_Length, WindowF, WindowF_Length);
	// end measure the time for the computation
  
   
	// finally give the result back
	return tfr.spwvd;
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
  private static class signalRepresentation
  {
	public int length; // Length of the signal in points
	public double[] real_part; // real part of the signal
	public double[] imag_part; // imaginary part of the signal
	public boolean is_complex; // true for complex signals
  }

// *
//  * This stuct hold the resulting time frequency representation and all the metadata. The resulting 
//  * value of the wigner ville transformation is stored into the real part row by row. The struct is 
//  * only internally used.
//  
  private static class timeFrequencyRepresentation
  {
	public int N_freq; // number of freq bins in the TFR matrix
	public int N_time; // number of time_bins in the TFR matrix
	public double[] time_instants; // instant for each column of the TFR
	public double[][] spwvd; // the resulting TFR
  }

//@}

//------------------------------------------------------------------------------------------------

// *
//  * This function implements the algorithmus for the Smoothed Pseudo-Wigner-Ville Distribution (SPWVD).
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
  
  public static timeFrequencyRepresentation tfr;
  
  private static void algorithmSmoothedPseudoWignerVilleDistribution (signalRepresentation Signal, double[] WindowT, int WindowT_Length, double[] WindowF, int WindowF_Length)
  {
  
	// init the local values
	int column;
	int row;
	int time;
	int half_WindowT_Length;
	int half_WindowF_Length;
	int taumax;
	int tau;
	int mumin;
	int mumax;
	int mu;
	double[] lacf_real; //the local autocorrelation function
	double[] lacf_imag;
	double normT;
	double normF;
	double R0_real;
	double R0_imag;
	double R1_real;
	double R1_imag;
	double R2_real;
	double R2_imag;
    
	// determines some internal constants
	half_WindowT_Length = (WindowT_Length - 1) / 2;
	half_WindowF_Length = (WindowF_Length - 1) / 2;
	normF =WindowF[half_WindowF_Length];
	for(row = 0; row < WindowF_Length; row++)
	  WindowF[row] = WindowF[row]/normF;
  
	// memory allocation and init. of the local autocorrelation fuction
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
	  }
  
	  // time instants of interest to compute the tfr
	  time = ((int) tfr.time_instants[column]);
	  taumax = Math.min((time+half_WindowT_Length),(Signal.length-time-1+half_WindowT_Length));
	  taumax = Math.min(taumax,(tfr.N_freq / 2 - 1));
	  taumax = Math.min(taumax, half_WindowF_Length);
  
	  // determination of the begin and end of mu
	  mumin =Math.min(half_WindowT_Length,(Signal.length-time-1));
	  mumax =Math.min(half_WindowT_Length,time);
  
	  // normalization of the time-smoothing window
  
	  // window norm
	  normT =0;
	  for(row = -mumin ; row <= mumax ; row++)
		normT =normT+ WindowT[half_WindowT_Length+row];
  
	  R0_real =0.0;
	  R0_imag =0.0;
	  for(mu =-mumin;mu<=mumax;mu++)
		{
		if(Signal.is_complex == true)
			R0_real =R0_real + (Signal.real_part[time-mu] * Signal.real_part[time-mu] + Signal.imag_part[time-mu] * Signal.imag_part[time-mu]) * WindowT[half_WindowT_Length+mu]/normT;
		else
		  R0_real =R0_real + Signal.real_part[time-mu] * Signal.real_part[time-mu] * WindowT[half_WindowT_Length+mu]/normT;
	  }
	  lacf_real[0]=R0_real;
	  lacf_imag[0]=R0_imag;
  
	  // The signal is windowed around the current time
	  for (tau = 1; tau <= taumax; tau++)
	  {
		  R1_real =0;
		  R2_real =0;
		  R1_imag =0;
		  R2_imag =0;
		  mumin =Math.min(half_WindowT_Length,(Signal.length-time-1-tau));
		  mumax =Math.min(half_WindowT_Length,time-tau);
  
		  // window norm
		  normT =0;
		  for(row = -mumin ; row <= mumax ; row++)
			  normT = normT + WindowT[half_WindowT_Length+row];
		for(mu =-mumin;mu<=mumax;mu++)
		  {
		  if (Signal.is_complex == true)
		  {
			R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu] + Signal.imag_part[time+tau-mu]* Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
				 R1_imag = R1_imag + (Signal.imag_part[time+tau-mu] * Signal.real_part[time-tau-mu] - Signal.real_part[time+tau-mu]* Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
				R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu] + Signal.imag_part[time-tau-mu]* Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
				R2_imag = R2_imag + (Signal.imag_part[time-tau-mu] * Signal.real_part[time+tau-mu] - Signal.real_part[time-tau-mu]* Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
			  }
		  else
		  {
			R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
			R1_imag = 0.0;
			R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
			R2_imag = 0.0;
		  }
		  }
		  lacf_real[tau] = R1_real * WindowF[half_WindowF_Length+tau];
		  lacf_imag[tau] = R1_imag * WindowF[half_WindowF_Length+tau];
		  lacf_real[tfr.N_freq-tau] = R2_real * WindowF[half_WindowF_Length-tau];
		  lacf_imag[tfr.N_freq-tau] = R2_imag * WindowF[half_WindowF_Length-tau];
	  }
  
	  tau =(int)(Math.floor((double)(tfr.N_freq) / 2.0));
	  if ((time<=Signal.length-tau-1)&(time>=tau)&(tau<=half_WindowF_Length))
	  {
		mumin =Math.min(half_WindowT_Length,(Signal.length-time-1-tau));
		  mumax =Math.min(half_WindowT_Length,time-tau);
  
		normT =0;
		  for(row = -mumin ; row <= mumax ; row++)
			normT = normT + WindowT[half_WindowT_Length+row];
  
		R1_real =0;
		R2_real =0;
		  R1_imag =0;
		  R2_imag =0;
		for(mu =-mumin;mu<=mumax;mu++)
		{
		  if (Signal.is_complex == true)
		  {
			R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu] + Signal.imag_part[time+tau-mu] * Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
				 R1_imag = R1_imag + (Signal.imag_part[time+tau-mu] * Signal.real_part[time-tau-mu] - Signal.real_part[time+tau-mu] * Signal.imag_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
				R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu] + Signal.imag_part[time-tau-mu] * Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
				R2_imag = R2_imag + (Signal.imag_part[time-tau-mu] * Signal.real_part[time+tau-mu]- Signal.real_part[time-tau-mu] * Signal.imag_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
			  }
		  else
		  {
			R1_real = R1_real + (Signal.real_part[time+tau-mu] * Signal.real_part[time-tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
			R1_imag = 0.0;
			R2_real = R2_real + (Signal.real_part[time-tau-mu] * Signal.real_part[time+tau-mu]) * WindowT[half_WindowT_Length+mu]/normT;
			R2_imag = 0.0;
		  }
		}
		lacf_real[tau] = 0.5*(R1_real * WindowF[half_WindowF_Length+tau] + R2_real * WindowF[half_WindowF_Length-tau]);
		lacf_imag[tau] = 0.5*(R1_imag * WindowF[half_WindowF_Length+tau] + R2_imag * WindowF[half_WindowF_Length-tau]);
		}
  
	  // calling here the fourier transformation of the local autocorrelation function
	  double[] dftRe = new double[tfr.N_freq];
	  double[] dftIm = new double[tfr.N_freq];

  
	  // call the Fourier Transformation
	  FastFourierTransform fft = new FastFourierTransform();
	  fft.xre = new double[tfr.N_freq];
	  fft.xim = new double[tfr.N_freq];
	  fft.n   = tfr.N_freq;
		for (int i=0; i<fft.n; i++)
		{
		  fft.xre[i] = lacf_real[i];
		  fft.xim[i] = lacf_imag[i];
		}
	  fft.doFFT();
		for (int i=0; i<fft.n; i++)
		{
	  	dftRe[i] = fft.xre[i];
	  	dftIm[i] = fft.xim[i];
		}



  
	  // the fft is put into the tfr matrix
	  for (row = 0; row < tfr.N_freq; row++)
	  {
		  tfr.spwvd[column][row] = Math.sqrt(dftRe[row]*dftRe[row]+dftIm[row]*dftIm[row]);
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

private static boolean isPowerOfTwo(int number)
{  
	boolean isPowerOfTwo = true;  
	int reminder = 0;  
	while(number > 1)
	{  
		reminder = number % 2;  
		if(reminder != 0)
		{  
			isPowerOfTwo = false;  
			break;  
		}
		else
		{  
			number = number / 2;  
		}  
	}  
	return isPowerOfTwo;  
}

  private static int getNextPowerOfTwo(int value)
  {
    // calc the next power of two
    int n = 1;
    while (n < value)
    {
      n = n << 1; // the short form for the power of two
    }
    return n;
  }


  private static double[] autoZeroPadding(double[] in, int len)
  {
    // first find the next bigger power of two value
    int newLen = getNextPowerOfTwo(len);
    // next allocate the new array
    double[] out = new double[newLen];
		for (int i=0; i<len; i++)
		{
			out[i] = in[i];
		}
    return out;
  }
  
	private static double[] fastHilbertTransform(double[] timeDomain, int length)
  {
	// forst prepare some values for the transformation
	double[] reTd = new double[length];
	double[] imTd = new double[length];
	double[] reFd = new double[length];
	double[] imFd = new double[length];
	for (int i=0; i<length; i++)
	{
		reTd[i] = timeDomain[i];
		imTd[i] = 0;
	}
  
	// FASTEN YOUR SEAT BELLS, we now transform the signal into the frequency domain
		FastFourierTransform fft = new FastFourierTransform();
  fft.xre = new double[length];
  fft.xim = new double[length];
  fft.n   = length;
	for (int i=0; i<fft.n; i++)
	{
	  fft.xre[i] = reTd[i];
	  fft.xim[i] = imTd[i];
	}
  fft.doFFT();
	for (int i=0; i<fft.n; i++)
	{
  	reFd[i] = fft.xre[i];
  	imFd[i] = fft.xim[i];
	}
	
	// now apply the hilbert transformation into the frequency domain cooresponding the
	// formular "h(x(f)) = x(f) * (-j) * sign(f)" in the complex frequency domain.
	// note that in our special case, we zero out the DC and Nyquist components.
	for (int i =0; i<length; i++)
	{
	  // first calculate the signum of the frequency for the hilbert transformation
	  double sign = 0.0;
	  if (i < length/2) // for positive frequencies multiply the positive harmonics by j
		  sign = 1.0;
	  else // for negative freuqencyies multiply the negative harmonics by -j
		  sign = -1.0;
	  if (i == 0) // zero out the zeroth component
		  sign = 0.0;
	  if (i == length/2) // zero out the Nyquist component
		  sign = 0.0;
  
	  // now apply the "sign(f) * (-j)" formular for the hibert transformation in the complex freuqncy domain
	  double newRe = sign * imFd[i];
	  double newIm = -sign * reFd[i];
  
	  // copy the result values
	  reFd[i] = newRe;
	  imFd[i] = newIm;
	}
  
	// BACK TO EARTH, we transform the value back to the time domain
	fft.xre = new double[length];
  fft.xim = new double[length];
  fft.n   = length;
	for (int i=0; i<fft.n; i++)
	{
	  fft.xre[i] = reFd[i];
	  fft.xim[i] = imFd[i];
	}
  fft.doFFT();
	for (int i=0; i<fft.n; i++)
	{
  	reTd[i] = fft.xre[i];
  	imTd[i] = -fft.xim[i];
	}
  
	// get the result back
	return reTd;
  }
  
  private static double[] applyHammingWindow(double[] samples, int N)
  {
  	double[] out = new double[N];
    for (int n=0; n<N; n++) 
      out[n] *= (25.0/46.0 - ( 21.0/46.0 * Math.cos( 2.0*Math.PI*(double)(n+0.5) / (double)N )));
    return out;
  }


  private static  double[] normalizeAvg(double[] samples, int length)
  {
		// first calcluate the average value
		double sum = 0.0;
		for (int i =0; i<length; i++)
		{
		  sum += samples[i];
		}
		double avg = sum / (double)(length);
		double[] out = new double[length];
		for (int i=0; i<length; i++)
			out[i] = samples[i]/avg;	
		return out;
  }
  
  
  
private static class FastFourierTransform {


    public static double[] xre;
    public static double[] xim;
    public static int n;

		private static int nu;

    public static void doFFT() {
        // assume n is a power of 2
        double ld = (Math.log(n)/Math.log(2.0));

        if ((double)((int)ld)-ld != 0) {
            System.out.println("Klasse FastFourierTransformation:");
            System.out.println("Der uebergebene Vektor hat keine laenge von 2 hoch n.");
            System.out.println("Die Laenge ist:" + n + " Der Logarithmus Dualis ist:"+ ld);
        }
        nu = (int) ld;
        int n2 = n/2;
        int nu1 = nu - 1;


        double tr, ti, p, arg, c, s;
        int k = 0;

        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitrev (k >> nu1);
                    arg = 2 * (double) Math.PI * p / n;
                    c = (double) Math.cos (arg);
                    s = (double) Math.sin (arg);
                    tr = xre[k+n2]*c + xim[k+n2]*s;
                    ti = xim[k+n2]*c - xre[k+n2]*s;
                    xre[k+n2] = xre[k] - tr;
                    xim[k+n2] = xim[k] - ti;
                    xre[k] += tr;
                    xim[k] += ti;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 = n2/2;
        }
        k = 0;
        int r;
        while (k < n) {
            r = bitrev (k);
            if (r > k) {
                tr = xre[k];
                ti = xim[k];
                xre[k] = xre[r];
                xim[k] = xim[r];
                xre[r] = tr;
                xim[r] = ti;
            }
            k++;
        }

    }
    private static int bitrev(int j) {

        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1/2;
            k  = 2*k + j1 - 2*j2;
            j1 = j2;
        }
        return k;
    }
}

} // class SmoothedPseudoWignerVilleDistribution

//------------------------------------------------------------------------------------------------

 

//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_SMOOTHED_PSEUDO_WIGNER_VILLE_TRANSFORMATION
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


