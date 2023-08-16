package clauer.math;

public class GlobalMembersFourier_transformation
{


	// C language Library headers

	//-------------------------------------------------------------------------------------------------


	public static final int FourierTransformation.MaxFastBits = 16;
}
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    fourier_transformation.h
// * @class   FourierTransformation
// * @version 1.0
// * @date    april 2008
// * @author  Christoph Lauer
// * @brief   This class implements a collection of fourier transformations and some helper functions.
// *
// * @see     http://en.wikipedia.org/wiki/Fast_Fourier_transform/
// * @see     http://en.wikipedia.org/wiki/Discrete_Fourier_transform
// * @todo    finished and tested so far.
// *
// * This file contains the "KING OF THE ALGORITHMS", a few FFT routines, including a real-FFT routine
// * that is almost twice as fast as a normal complex FFT, and a power spectrum routine when you know
// * you don't care about phase information. The basic algorithm for his code was based on Numerical
// * Recipes in Fortran. I optimized his code further by reducing array accesses, caching the bit
// * reversal table, and eliminating float-to-double conversions, and I added the routines to calculate
// *  a real FFT and a real power spectrum. Note that all this functions need a output data array pre 
// * allocation ! This implementation can massively make sense of the compiler optimization so the 
// * compiler should be called with the "-O3" option to increase the speed performance. We implemnt 
// * here mostly the the standart algorithm wich goes abck to the work of L.W. Cooley and J.W. Turkey
// * in the mid-1960s. For performace reasons we put the algorithm in each own of the tree implementations
// * (Comple, Real, and Power) and do  not make use of some outsourced function. This should give us 
// * the fastest implementation. Please note that all the algorithms here work ony with a vectro length 
// * from a power of two. The class collection was extended by a discrtete fourier transformation (DFT) 
// * also which has the same signature as the FFT and can be used to compute transformations with a length
// * which is not a power of two.
// 

//-------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    fourier_transformation.h
// * @class   FourierTransformation
// * @version 1.0
// * @date    april 2008
// * @author  Christoph Lauer
// * @brief   This class implements a collection of fourier transformations and some helper functions.
// *
// * @see     http://en.wikipedia.org/wiki/Fast_Fourier_transform/
// * @see     http://en.wikipedia.org/wiki/Discrete_Fourier_transform
// * @todo    finished and tested so far.
// 
// *
// * This file contains the "KING OF THE ALGORITHMS", a few FFT routines, including a real-FFT routine
// * that is almost twice as fast as a normal complex FFT, and a power spectrum routine when you know
// * you don't care about phase information. The basic algorithm for his code was based on Numerical
// * Recipes in Fortran. I optimized his code further by reducing array accesses, caching the bit
// * reversal table, and eliminating float-to-double conversions, and I added the routines to calculate
// *  a real FFT and a real power spectrum. Note that all this functions need a output data array pre 
// * allocation ! This implementation can massively make sense of the compiler optimization so the 
// * compiler should be called with the "-O3" option to increase the speed performance. We implemnt 
// * here mostly the the standart algorithm wich goes abck to the work of L.W. Cooley and J.W. Turkey
// * in the mid-1960s. For performace reasons we put the algorithm in each own of the tree implementations
// * (Comple, Real, and Power) and do  not make use of some outsourced function. This should give us 
// * the fastest implementation. Please note that all the algorithms here work ony with a vectro length 
// * from a power of two. The class collection was extended by a discrtete fourier transformation (DFT) 
// * also which has the same signature as the FFT and can be used to compute transformations with a length
// * which is not a power of two.
// 

//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_FOURIER_TRANSFORMATION
//#define CLAUER_MATH_FOURIER_TRANSFORMATION

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! M_PI
//#define M_PI 3.14159265358979323846
//#endif

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------

public class FourierTransformation
{

//-------------------------------------------------------------------------------------------------


// *
//  * This is the function you will use the most often. Given an array of floats, this will compute the
//  * power spectrum by doing a Real FFT and then computing the sum of the squares of the real and 
//  * imaginary parts. Note that the output array is half the length of the input array, and that 
//  * N must be a power of two. For speed, it does not call RealFFT, but duplicates some of
//  * its code. This function is not in place and the corresponding output array is allocated into this 
//  * function.
//  *
//  * @param  N           The lengtht of the input vector. Should be have a power of two.
//  * @param  In          The input vector.    
//  * @param  Out         The output vector has the half size of the input vector 
//  *                     and must be pre allocated outside this function !!!
//  *                     It should have the half size than the input vector.
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void PowerSpectrum(int N, double[] In, double[] Out)
  {
	int Half = N / 2;
	int i;
	double theta = DefineConstantsFourier_transformation.M_PI / Half;
	double[] tmpReal = new double[Half];
	double[] tmpImag = new double[Half];
	double[] RealOut = new double[Half];
	double[] ImagOut = new double[Half];
	for (i = 0; i < Half; i++)
	{
	  tmpReal[i] = In[2 * i];
	  tmpImag[i] = In[2 * i + 1];
	}
	GlobalMembersFourier_transformation.FFT(Half, true, tmpReal, tmpImag, RealOut, ImagOut);
	double wtemp = Math.sin(0.5 * theta);
	double wpr = -2.0 * wtemp * wtemp;
	double wpi = Math.sin(theta);
	double wr = 1.0 + wpr;
	double wi = wpi;
	int i3;
	double h1r;
	double h1i;
	double h2r;
	double h2i;
	double rt;
	double it;
	for (i = 1; i < Half / 2; i++)
	{
	  i3 = Half - i;
	  h1r = 0.5 * (RealOut[i] + RealOut[i3]);
	  h1i = 0.5 * (ImagOut[i] - ImagOut[i3]);
	  h2r = 0.5 * (ImagOut[i] + ImagOut[i3]);
	  h2i = -0.5 * (RealOut[i] - RealOut[i3]);
	  rt = h1r + wr * h2r - wi * h2i;
	  it = h1i + wr * h2i + wi * h2r;
	  Out[i] = (rt * rt + it * it) / 4.0;
	  rt = h1r - wr * h2r + wi * h2i;
	  it = -h1i + wr * h2i + wi * h2r;
	  Out[i3] = (rt * rt + it * it) / 4.0;
	  wr = (wtemp = wr) * wpr - wi * wpi + wr;
	  wi = wi * wpr + wtemp * wpi + wi;
	}
	rt = (h1r = RealOut[0]) + ImagOut[0];
	it = h1r - ImagOut[0];
	Out[0] = (rt * rt + it * it) / 4.0;
	rt = RealOut[Half / 2];
	it = ImagOut[Half / 2];
	Out[Half / 2] = (rt * rt + it * it) / 4.0;
	// waste the grabbage
	tmpReal = null;
	tmpImag = null;
	RealOut = null;
	ImagOut = null;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * Computes a FFT of complex input and returns complex output. Currently this is the only function 
//  * here that supports the inverse transform as well. Note that all the data vectors must be allocated
//  * outside this function. The length of the data vectors should all have the same size of N.
//  *
//  * The algorithm brings the following frequency domain output.
//  * re(0) re(1) re(2) re(3) re(4) re(-3) re(-2) re(-1) 
//  * im(0) im(1) im(2) im(3) im(4) im(-3) im(-2) im(-1) 
//  * 
//  * or cleaner for the real and imaginary parts are idential with:  INDEXES <--> FREQUENCIES
//  * 0  1  2  3  4  5  6  7  8 -7 -6 -5 -4 -3 -2 -1 
//  *
//  * It can be seen that the the 0-th coefficient (DC-offset) and the N-th coefficient (Nyquist-frequency)
//  * occur only once, and the other occur for the positiove and the negative frequency part. Following 
//  * the parameter description.
//  * 
//  * @param  N        The number of samples should be a power of two.
//  * @param  InverseTransform  As the name says, a true here does an IFFT.
//  * @param  RealIn            The RE input vector.
//  * @param  ImagIn            The IM input vector.
//  * @param  RealOut           The RE output vector - must be pre allocated !!!
//  * @param  ImagOut           The IM output vector - must be pre allocated !!!
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void FFT(int N, boolean InverseTransform, double[] RealIn, double[] ImagIn, double[] RealOut, double[] ImagOut)
  {
	// initialize the function variables
	int NumBits; // Number of bits needed to store indices
	int i;
	int j;
	int k;
	int n;
	int BlockSize;
	int BlockEnd;
	double angle_numerator = 2.0 * DefineConstantsFourier_transformation.M_PI;
	double tr; // temp real, temp imaginary
	double ti;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(RealOut, 0, N *sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(ImagOut, 0, N *sizeof(double));
  
	// check if the length is a power of two
	if (IsPowerOfTwo(N) == 0)
	{
	  fprintf(stderr, "%d is not a power of two\n", N);
	  exit(1);
	}
	if (gFFTBitTable == 0)
	  InitFFT();
	if (InverseTransform == false)
	  angle_numerator = -angle_numerator;
	NumBits = NumberOfBitsNeeded(N);
  
	// do the simultaneous data copy and bit-reversal ordering into outputs...
	for (i =0; i<N; i++)
	{
	  j = FastReverseBits(i, NumBits);
	  RealOut[j] = RealIn[i];
	  ImagOut[j] = (ImagIn == null) ? 0.0 : ImagIn[i];
	}
  
	// do the FFT itself...
	BlockEnd = 1;
	for (BlockSize = 2; BlockSize <= N; BlockSize <<= 1)
	{
	  double delta_angle = angle_numerator / (double) BlockSize;
	  double sm2 = Math.sin(-2 * delta_angle);
	  double sm1 = Math.sin(-delta_angle);
	  double cm2 = Math.cos(-2 * delta_angle);
	  double cm1 = Math.cos(-delta_angle);
	  double w = 2 * cm1;
	  double ar0;
	  double ar1;
	  double ar2;
	  double ai0;
	  double ai1;
	  double ai2;
	  for (i = 0; i < N; i += BlockSize)
	  {
		ar2 = cm2;
		ar1 = cm1;
		ai2 = sm2;
		ai1 = sm1;
		for (j = i, n = 0; n < BlockEnd; j++, n++)
		{
		  ar0 = w * ar1 - ar2;
		  ar2 = ar1;
		  ar1 = ar0;
		  ai0 = w * ai1 - ai2;
		  ai2 = ai1;
		  ai1 = ai0;
		  k = j + BlockEnd;
		  tr = ar0 * RealOut[k] - ai0 * ImagOut[k];
		  ti = ar0 * ImagOut[k] + ai0 * RealOut[k];
		  RealOut[k] = RealOut[j] - tr;
		  ImagOut[k] = ImagOut[j] - ti;
		  RealOut[j] += tr;
		  ImagOut[j] += ti;
		}
	  }
	  BlockEnd = BlockSize;
	}
  
  
	// we need to normalize the result for the inverse transform...
	if (InverseTransform)
	{
	  double denom = (double) N;
	  for (i =0; i<N; i++)
	  {
		 RealOut[i] /= denom;
		 ImagOut[i] /= denom;
	  }
	}
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This implementation of the complex fast fourier transformation here has the same specifications 
//  * like the implemntation above exept that here the complex numbers are used instead of the common
//  * 64 bit double values. Note that the complex output vector must be pre allocted !!!
//  *
//  * @param  N        The number of samples.
//  * @param  InverseTransform  As the name says, a true here does an IFFT.
//  * @param  in                The complex input vector.
//  * @param  out               The complex output vector - must be pre allocated !!!
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void FFT(int N, boolean InverseTransform, std.complex<Double>[] in, std.complex<Double>[] out)
  {
	// allocate the common arrays
	double[] RealIn = new double[N];
	double[] ImagIn = new double[N];
	double[] RealOut = new double[N];
	double[] ImagOut = new double[N];
	// cast the complex vectors to common arrays
	for(int i =0; i<N; i++)
	{
	  RealIn[i] = in[i].real();
	  ImagIn[i] = in[i].imag();
	}
	// call the ordinary FFT with the common data types
	GlobalMembersFourier_transformation.FFT(N, InverseTransform, RealIn, ImagIn, RealOut, ImagOut);
	// copy the result back to the complex template structures
	for(int i =0; i<N; i++)
	  out[i] = std.<Double>complex(RealOut[i],ImagOut[i]);
	// waste the grabbage
	RealIn = null;
	ImagIn = null;
	RealOut = null;
	ImagOut = null;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This function simple performs an discrete Fourier Transformation where the number of elements 
//  * must not have a length from a power of two. We use here the standard implementation with the 
//  * discrete matice multiplication where which can found anywhere in the literature and in the 
//  * internet.
//  *
//  * The algorithm brings the following frequency domain output.
//  * re(0) re(1) re(2) re(3) re(4) re(-3) re(-2) re(-1) 
//  * im(0) im(1) im(2) im(3) im(4) im(-3) im(-2) im(-1) 
//  * 
//  * or cleaner for the real and imaginary parts are idential with:  INDEXES <--> FREQUENCIES
//  * 0  1  2  3  4  5  6  7  8 -7 -6 -5 -4 -3 -2 -1 
//  *
//  * It can be seen that the the 0-th coefficient (DC-offset) and the N-th coefficient (Nyquist-frequency)
//  * occur only once, and the other occur for the positiove and the negative frequency part. Following
//  * the parameter description.
//  *
//  * @param  N        The number of s amples should be a power of two.
//  * @param  InverseTransform  As the name says, a true here does an IFFT.
//  * @param  RealIn            The RE input vector.
//  * @param  ImagIn            The IM input vector.
//  * @param  RealOut           The RE output vector - must be pre allocated !!!
//  * @param  ImagOut           The IM output vector - must be pre allocated !!!
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void DFT(int N, boolean InverseTransform, double[] RealIn, double[] ImagIn, double[] RealOut, double[] ImagOut)
  {
	// into some values
	int i;
	int j;
	double arg;
	double cosarg;
	double sinarg;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(RealOut, 0, N *sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	memset(ImagOut, 0, N *sizeof(double));
  
	// the only change between the inverse and the forward tarnsformation is this sign flag
	double inverseFactor = -1.0;
	if (InverseTransform == true)
	  inverseFactor = 1.0;
	// calcualte the DFT
	for(i =0; i<N; i+=1)
	{
	  arg = inverseFactor * 2.0 *DefineConstantsFourier_transformation.PI * (double)(i) / (double)(N);
	  for(j =0; j<N; j++)
	  {
		cosarg = Math.cos(j * arg);
		sinarg = Math.sin(j * arg);
		RealOut[i] += (RealIn[j] * cosarg - ImagIn[j] * sinarg);
		ImagOut[i] += (RealIn[j] * sinarg + ImagIn[j] * cosarg);
	  }
	  if (InverseTransform == true)
	  {
		RealOut[i] /= (double)(N);
		ImagOut[i] /= (double)(N);
	  }
	}
  }

//-------------------------------------------------------------------------------------------------

// *
//  * Because the FFT works only for lengths with a power of two and the DFT for all lengths this function
//  * decides autonomously to call the faster FFT of the slow DFT depending on the length of the vector.
//  * The signature is the same than the signature of the FFT and DFT.
//  *
//  * @param  N                 The number of samples should be a power of two.
//  * @param  InverseTransform  As the name says, a true here does an IFFT.
//  * @param  RealIn            The RE input vector.
//  * @param  ImagIn            The IM input vector.
//  * @param  RealOut           The RE output vector - must be pre allocated !!!
//  * @param  ImagOut           The IM output vector - must be pre allocated !!!
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void AutoFT(int N, boolean InverseTransform, double RealIn, double ImagIn, RefObject<Double> RealOut, RefObject<Double> ImagOut)
  {
	if (GlobalMembersFourier_transformation.clauer.math.Utilities.isPowerOfTwo(N) == true)
	  GlobalMembersFourier_transformation.FFT(N, InverseTransform, RealIn, ImagIn, RealOut.argvalue, ImagOut.argvalue);
	else
	{
	  RefObject<Double> TempRefObject = new RefObject<Double>(RealOut);
	  RefObject<Double> TempRefObject2 = new RefObject<Double>(ImagOut);
	  DFT(N, InverseTransform, RealIn, ImagIn, TempRefObject, TempRefObject2);
	  RealOut.argvalue = TempRefObject.argvalue;
	  ImagOut.argvalue = TempRefObject2.argvalue;
	}
  }

//-------------------------------------------------------------------------------------------------

// *
//  * This function automatically generates the powerspectrum for the given time domain signal, independent
//  * from the size of the signal. The function internally expands the size of the spectrum to a power 
//  * of two, and results the corresponding powerspectrum. The size of the new generated spectrum will 
//  * be given back via the call by reference parameter specLength.
//  *
//  * @param  in          The time domain based input signal.
//  * @param  legnth      The length of the input signal.
//  * @param  specLength  The length of the resulting power spectrum.
//  

  //-------------------------------------------------------------------------------------------------
  
  public static double AutoPS(double in, int length, RefObject<Integer> specLength)
  {
	// first expand the length of the input vector to a power of two if necessary
	int newLen = 0;
	double td = GlobalMembersFourier_transformation.clauer.math.Utilities.autoZeroPadding(in, length, newLen);
  
	// generate the spectrum
	( specLength.argvalue) = newLen/2;
	double[] fd = new double[( specLength.argvalue)];
	RefObject<Double> TempRefObject = new RefObject<Double>(fd);
	PowerSpectrum(newLen, td, TempRefObject);
	fd = TempRefObject.argvalue;
  
	// normalize the spectrum amplitude to the zer padded samples
	double normalizeFactor = (double)(newLen) / (double)(length);
	for (int i =0; i<( specLength.argvalue); i++)
	{
	  if (fd[i] <= 0.0)
		fd[i] = DBL_MIN;
	  fd[i] *= normalizeFactor;
	}
  
	// waste the grabage
	td = null;
  
	// give the result back
	return fd;
  }

//-------------------------------------------------------------------------------------------------

// *
//  * Applies a windowing function to the data in place
//  * 0: Rectangular (no window)
//  * 1: Bartlett    (triangular)
//  * 2: Hamming
//  * 3: Hanning
//  *
//  * @param  whichFunction   See description above.
//  * @param  N      The length of the data vector.
//  * @param  data            The input vector array. (in place)
//  *
//  * @note   There is also a collection of window functions into the static clauer::math::Utilities class
//  *         in the ../math Utilities/math_utilities.h file.
//  

  //-------------------------------------------------------------------------------------------------
  
  public static void WindowFunc(int whichFunction, int N, double[] in)
  {
	int i;
  
	if (whichFunction == 1)
	{
	  // Bartlett (triangular) window
	  for (i = 0; i < N / 2; i++)
	  {
		in[i] *= (i / (double)(N / 2));
		in[i + (N / 2)] *= (1.0 - (i / (double)(N / 2)));
	  }
	}
	if (whichFunction == 2)
	{
	  // Hamming
	  for (i = 0; i < N; i++)
		in[i] *= 0.54 - 0.46 * Math.cos(2 * DefineConstantsFourier_transformation.M_PI * i / (N - 1));
	}
	if (whichFunction == 3)
	{
	  // Hanning
	  for (i = 0; i < N; i++)
		in[i] *= 0.50 - 0.50 * Math.cos(2 * DefineConstantsFourier_transformation.M_PI * i / (N - 1));
	}
  }

//-------------------------------------------------------------------------------------------------


/// the global FFT bit table two dimensional array
private static int[][] gFFTBitTable = null;

/// variable used to allocate the gFFTBitTable two dimensional array
private final int MaxFastBits;

// *
//  * This private function group are all helper functions for the fft functions.
//  * e.g. BitShift, Power of Two...
//  
  //@{

  //-------------------------------------------------------------------------------------------------
  
  private static int IsPowerOfTwo(int x)
  {
	 if (x < 2)
		return false;
	 if (x & (x - 1) != 0) // this is very very tricky !!!
		return false;
	 return true;
  }

  //-------------------------------------------------------------------------------------------------
  
  private static int NumberOfBitsNeeded(int PowerOfTwo)
  {
	assert PowerOfTwo > 2;
	for (int i = 0;; i++)
	  if (PowerOfTwo & (1 << i) != 0)
		return i;
  }

  //-------------------------------------------------------------------------------------------------
  
  private static void InitFFT()
  {
	gFFTBitTable = new int[MaxFastBits];
	int len = 2;
	for (int b = 1; b <= MaxFastBits; b++)
	{
	  gFFTBitTable[b - 1] = new int[len];
	  for (int i = 0; i < len; i++)
		gFFTBitTable[b - 1][i] = ReverseBits(i, b);
	  len <<= 1;
	}
  }

  //-------------------------------------------------------------------------------------------------
  
  private static int ReverseBits(int index, int NumBits)
  {
	 int rev;
	 for (int i = rev = 0; i < NumBits; i++)
	 {
		rev = (rev << 1) | (index & 1);
		index >>= 1;
	 }
	 return rev;
  }

  //-------------------------------------------------------------------------------------------------
  
  private static int FastReverseBits(int i, int NumBits)
  {
	 if (NumBits <= MaxFastBits)
	   return gFFTBitTable[NumBits - 1][i];
	 else
	   return ReverseBits(i, NumBits);
  }
  //@}

} // FourierTransformation

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_FOURIER_TRANSFORMATION
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math

//-------------------------------------------------------------------------------------------------



//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2009 Tangible Software Solutions Inc.
//
//	This class is used to simulate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
final class RefObject<T>
{
	T argvalue;
	RefObject(T refarg)
	{
		argvalue = refarg;
	}
}