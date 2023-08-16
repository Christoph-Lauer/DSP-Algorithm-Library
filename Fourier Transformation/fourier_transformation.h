/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    fourier_transformation.h
 * @class   FourierTransformation
 * @version 1.0
 * @date    april 2022
 * @author  Christoph Lauer
 * @brief   This class implements a collection of fourier transformations and some helper functions.
 *
 * @see     http://en.wikipedia.org/wiki/Fast_Fourier_transform/
 * @see     http://en.wikipedia.org/wiki/Discrete_Fourier_transform
 * @todo    finished and tested so far.
 
 *
 * This file contains the "KING OF THE ALGORITHMS", a few FFT routines, including a real-FFT routine
 * that is almost twice as fast as a normal complex FFT, and a power spectrum routine when you know
 * you don't care about phase information. The basic algorithm for his code was based on Numerical
 * Recipes in Fortran. I optimized his code further by reducing array accesses, caching the bit
 * reversal table, and eliminating float-to-double conversions, and I added the routines to calculate
 *  a real FFT and a real power spectrum. Note that all this functions need a output data array pre 
 * allocation ! This implementation can massively make sense of the compiler optimization so the 
 * compiler should be called with the "-O3" option to increase the speed performance. We implemnt 
 * here mostly the the standart algorithm wich goes abck to the work of L.W. Cooley and J.W. Turkey
 * in the mid-1960s. For performace reasons we put the algorithm in each own of the tree implementations
 * (Comple, Real, and Power) and do  not make use of some outsourced function. This should give us 
 * the fastest implementation. Please note that all the algorithms here work ony with a vectro length 
 * from a power of two. The class collection was extended by a discrtete fourier transformation (DFT) 
 * also which has the same signature as the FFT and can be used to compute transformations with a length
 * which is not a power of two.
 */

//--------------------------------------------------------------------------------------------------
 
#ifndef CLAUER_MATH_FOURIER_TRANSFORMATION
#define CLAUER_MATH_FOURIER_TRANSFORMATION

//-------------------------------------------------------------------------------------------------

#include <complex>

//-------------------------------------------------------------------------------------------------

#ifndef M_PI
#define	M_PI		3.14159265358979323846
#endif

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

//-------------------------------------------------------------------------------------------------

class FourierTransformation
{

//-------------------------------------------------------------------------------------------------

public:

 /**
  * This is the function you will use the most often. Given an array of floats, this will compute the
  * power spectrum by doing a Real FFT and then computing the sum of the squares of the real and 
  * imaginary parts. Note that the output array is half the length of the input array, and that 
  * N must be a power of two. For speed, it does not call RealFFT, but duplicates some of
  * its code. This function is not in place and the corresponding output array is allocated into this 
  * function.
  *
  * @param  N           The lengtht of the input vector. Should be have a power of two.
  * @param  In          The input vector.    
  * @param  Out         The output vector has the half size of the input vector 
  *                     and must be pre allocated outside this function !!!
  *                     It should have the half size than the input vector.
  */
  static void PowerSpectrum(const int N, const double* In, double* Out);

//-------------------------------------------------------------------------------------------------

 /**
  * Computes a FFT of complex input and returns complex output. Currently this is the only function 
  * here that supports the inverse transform as well. Note that all the data vectors must be allocated
  * outside this function. The length of the data vectors should all have the same size of N.
  *
  * The algorithm brings the following frequency domain output.
  * re(0) re(1) re(2) re(3) re(4) re(-3) re(-2) re(-1) 
  * im(0) im(1) im(2) im(3) im(4) im(-3) im(-2) im(-1) 
  * 
  * or cleaner for the real and imaginary parts are idential with:  INDEXES <--> FREQUENCIES
  * 0  1  2  3  4  5  6  7  8 -7 -6 -5 -4 -3 -2 -1 
  *
  * It can be seen that the the 0-th coefficient (DC-offset) and the N-th coefficient (Nyquist-frequency)
  * occur only once, and the other occur for the positiove and the negative frequency part. Following 
  * the parameter description.
  * 
  * @param  N        The number of samples should be a power of two.
  * @param  InverseTransform  As the name says, a true here does an IFFT.
  * @param  RealIn            The RE input vector.
  * @param  ImagIn            The IM input vector.
  * @param  RealOut           The RE output vector - must be pre allocated !!!
  * @param  ImagOut           The IM output vector - must be pre allocated !!!
  */
  static void FFT(const int N, const bool InverseTransform, const double* RealIn, const double* ImagIn, double* RealOut, double* ImagOut);
  
//-------------------------------------------------------------------------------------------------
  
 /**
  * This implementation of the complex fast fourier transformation here has the same specifications 
  * like the implemntation above exept that here the complex numbers are used instead of the common
  * 64 bit double values. Note that the complex output vector must be pre allocted !!!
  *
  * @param  N        The number of samples.
  * @param  InverseTransform  As the name says, a true here does an IFFT.
  * @param  in                The complex input vector.
  * @param  out               The complex output vector - must be pre allocated !!!
  */
  static void FFT(const int N, const bool InverseTransform, const std::complex<double>* in, std::complex<double>* out);

//-------------------------------------------------------------------------------------------------

 /**
  * This function simple performs an discrete Fourier Transformation where the number of elements 
  * must not have a length from a power of two. We use here the standard implementation with the 
  * discrete matice multiplication where which can found anywhere in the literature and in the 
  * internet.
  *
  * The algorithm brings the following frequency domain output.
  * re(0) re(1) re(2) re(3) re(4) re(-3) re(-2) re(-1) 
  * im(0) im(1) im(2) im(3) im(4) im(-3) im(-2) im(-1) 
  * 
  * or cleaner for the real and imaginary parts are idential with:  INDEXES <--> FREQUENCIES
  * 0  1  2  3  4  5  6  7  8 -7 -6 -5 -4 -3 -2 -1 
  *
  * It can be seen that the the 0-th coefficient (DC-offset) and the N-th coefficient (Nyquist-frequency)
  * occur only once, and the other occur for the positiove and the negative frequency part. Following
  * the parameter description.
  *
  * @param  N        The number of s amples should be a power of two.
  * @param  InverseTransform  As the name says, a true here does an IFFT.
  * @param  RealIn            The RE input vector.
  * @param  ImagIn            The IM input vector.
  * @param  RealOut           The RE output vector - must be pre allocated !!!
  * @param  ImagOut           The IM output vector - must be pre allocated !!!
  */
  static void DFT(const int N, const bool InverseTransform, const double* RealIn, const double* ImagIn, double* RealOut, double* ImagOut);

//-------------------------------------------------------------------------------------------------

 /**
  * Because the FFT works only for lengths with a power of two and the DFT for all lengths this function
  * decides autonomously to call the faster FFT of the slow DFT depending on the length of the vector.
  * The signature is the same than the signature of the FFT and DFT.
  *
  * @param  N                 The number of samples should be a power of two.
  * @param  InverseTransform  As the name says, a true here does an IFFT.
  * @param  RealIn            The RE input vector.
  * @param  ImagIn            The IM input vector.
  * @param  RealOut           The RE output vector - must be pre allocated !!!
  * @param  ImagOut           The IM output vector - must be pre allocated !!!
  */
  static void AutoFT(const int N, const bool InverseTransform, const double* RealIn, const double* ImagIn, double* RealOut, double* ImagOut);

//-------------------------------------------------------------------------------------------------

 /**
  * This function automatically generates the powerspectrum for the given time domain signal, independent
  * from the size of the signal. The function internally expands the size of the spectrum to a power 
  * of two, and results the corresponding powerspectrum. The size of the new generated spectrum will 
  * be given back via the call by reference parameter specLength.
  *
  * @param  in          The time domain based input signal.
  * @param  legnth      The length of the input signal.
  * @param  specLength  The length of the resulting power spectrum.
  */
  static double* AutoPS(const double* in, const int length, int* newLength);
  
//-------------------------------------------------------------------------------------------------

 /**
  * Applies a windowing function to the data in place
  * 0: Rectangular (no window)
  * 1: Bartlett    (triangular)
  * 2: Hamming
  * 3: Hanning
  *
  * @param  whichFunction   See description above.
  * @param  N      The length of the data vector.
  * @param  data            The input vector array. (in place)
  *
  * @note   There is also a collection of window functions into the static clauer::math::Utilities class
  *         in the ../math Utilities/math_utilities.h file.
  */
  static void WindowFunc(const int whichFunction, const int N, double* data);
 
//-------------------------------------------------------------------------------------------------

private:

/// the global FFT bit table two dimensional array
static int** gFFTBitTable;

/// variable used to allocate the gFFTBitTable two dimensional array
static const int MaxFastBits;

 /**
  * This private function group are all helper functions for the fft functions.
  * e.g. BitShift, Power of Two...
  */
  //@{
  static int  IsPowerOfTwo(const int x);
  static int  NumberOfBitsNeeded(const int PowerOfTwo);
  static void InitFFT();
  static int  ReverseBits(int index, const int NumBits);
  static int  FastReverseBits(const int i, const int NumBits);
  //@}

}; // FourierTransformation

//-------------------------------------------------------------------------------------------------

} // namespace math
} // namepsace clauer

//-------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_FOURIER_TRANSFORMATION
