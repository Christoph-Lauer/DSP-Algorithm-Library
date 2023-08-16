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

//-------------------------------------------------------------------------------------------------

// Local headers
#include "fourier_transformation.h"
#include "../Math Utilities/math_utilities.h"

// C language Library headers
#include <cstdlib>
#include <cstdio>
#include <cmath>
#include <cfloat>
#include <cassert>
#include <cstring>

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

int** FourierTransformation::gFFTBitTable = NULL;
const int FourierTransformation::MaxFastBits = 16;

//-------------------------------------------------------------------------------------------------

void FourierTransformation::PowerSpectrum(const int N, const double* In, double* Out)
{
  int Half = N / 2;
  int i;
  double theta = M_PI / Half;
  double *tmpReal = new double[Half];
  double *tmpImag = new double[Half];
  double *RealOut = new double[Half];
  double *ImagOut = new double[Half];
  for (i = 0; i < Half; i++) 
  {
    tmpReal[i] = In[2 * i];
    tmpImag[i] = In[2 * i + 1];
  }
  FFT(Half, true, tmpReal, tmpImag, RealOut, ImagOut);
  double wtemp = double (sin(0.5 * theta));
  double wpr = -2.0 * wtemp * wtemp;
  double wpi = double (sin(theta));
  double wr = 1.0 + wpr;
  double wi = wpi;
  int i3;
  double h1r, h1i, h2r, h2i, rt, it;
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
  delete[]tmpReal;
  delete[]tmpImag;
  delete[]RealOut;
  delete[]ImagOut;
}

//-------------------------------------------------------------------------------------------------

void FourierTransformation::FFT(const int N, const bool InverseTransform, const double* RealIn, const double* ImagIn, double* RealOut, double* ImagOut)
{
  // initialize the function variables
  int NumBits;                 // Number of bits needed to store indices
  int i, j, k, n;
  int BlockSize, BlockEnd;
  double angle_numerator = 2.0 * M_PI;
  double tr, ti;               // temp real, temp imaginary
  memset(RealOut, 0, N*sizeof(double));
  memset(ImagOut, 0, N*sizeof(double));
  
  // check if the length is a power of two
  if (!IsPowerOfTwo(N)) 
  {
    fprintf(stderr, "%d is not a power of two\n", N);
    exit(1);
  }
  if (!gFFTBitTable)
    InitFFT();
  if (InverseTransform == false)
    angle_numerator = -angle_numerator;
  NumBits = NumberOfBitsNeeded(N);

  // do the simultaneous data copy and bit-reversal ordering into outputs...
  for (i=0; i<N; i++) 
  {
    j = FastReverseBits(i, NumBits);
    RealOut[j] = RealIn[i];
    ImagOut[j] = (ImagIn == NULL) ? 0.0 : ImagIn[i];
  }

  // do the FFT itself...
  BlockEnd = 1;
  for (BlockSize = 2; BlockSize <= N; BlockSize <<= 1) 
  {
    double delta_angle = angle_numerator / (double) BlockSize;
    double sm2 = sin(-2 * delta_angle);
    double sm1 = sin(-delta_angle);
    double cm2 = cos(-2 * delta_angle);
    double cm1 = cos(-delta_angle);
    double w = 2 * cm1;
    double ar0, ar1, ar2, ai0, ai1, ai2;
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
    for (i=0; i<N; i++) 
    {
       RealOut[i] /= denom;
       ImagOut[i] /= denom;
    }
  }
}

//-------------------------------------------------------------------------------------------------

void FourierTransformation::FFT(const int N, const bool InverseTransform, const std::complex<double>* in, std::complex<double>* out)
{
  // allocate the common arrays
  double* RealIn  = new double[N];
  double* ImagIn  = new double[N];
  double* RealOut = new double[N];
  double* ImagOut = new double[N];
  // cast the complex vectors to common arrays
  for(int i=0; i<N; i++)
  {
    RealIn[i] = in[i].real();
    ImagIn[i] = in[i].imag();
  }
  // call the ordinary FFT with the common data types
  FFT(N, InverseTransform, RealIn, ImagIn, RealOut, ImagOut);
  // copy the result back to the complex template structures
  for(int i=0; i<N; i++)
    out[i] = std::complex<double>(RealOut[i],ImagOut[i]);
  // waste the grabbage
  delete[] RealIn;
  delete[] ImagIn;
  delete[] RealOut;
  delete[] ImagOut;
}

//-------------------------------------------------------------------------------------------------

void FourierTransformation::DFT(const int N, const bool InverseTransform, const double* RealIn, const double* ImagIn, double* RealOut, double* ImagOut)
{
  // into some values
  long int i, j;
  double arg;
  double cosarg,sinarg;
  memset(RealOut, 0, N*sizeof(double));
  memset(ImagOut, 0, N*sizeof(double));

  // the only change between the inverse and the forward tarnsformation is this sign flag
  double inverseFactor = -1.0;
  if (InverseTransform == true) 
    inverseFactor = 1.0;
  // calcualte the DFT
  for(i=0; i<N; i+=1) 
  {
    arg = inverseFactor * TWO_PI * static_cast<double>(i) / static_cast<double>(N);
    for(j=0; j<N; j++) 
    {
      cosarg = cos(j * arg);
      sinarg = sin(j * arg);
      RealOut[i] += ( RealIn[j] * cosarg - ImagIn[j] * sinarg );
      ImagOut[i] += ( RealIn[j] * sinarg + ImagIn[j] * cosarg );
    }
    if (InverseTransform == true)
    {
      RealOut[i] /= static_cast<double>(N);
      ImagOut[i] /= static_cast<double>(N);
    }
  }
}

//-------------------------------------------------------------------------------------------------

void FourierTransformation::AutoFT(const int N, const bool InverseTransform, const double* RealIn, const double* ImagIn, double* RealOut, double* ImagOut)
{
  if (clauer::math::Utilities::isPowerOfTwo(N) == true)
    FFT(N, InverseTransform, RealIn, ImagIn, RealOut, ImagOut);
  else  
    DFT(N, InverseTransform, RealIn, ImagIn, RealOut, ImagOut);
}
  
//-------------------------------------------------------------------------------------------------

double* FourierTransformation::AutoPS(const double* in, const int length, int* specLength)
{
  // first expand the length of the input vector to a power of two if necessary
  int newLen = 0;
  double* td = clauer::math::Utilities::autoZeroPadding(in, length, &newLen);
  
  // generate the spectrum
  (*specLength) = newLen/2;
  double* fd = new double[(*specLength)];
  PowerSpectrum(newLen, td, fd);

  // normalize the spectrum amplitude to the zer padded samples
  double normalizeFactor = static_cast<double>(newLen) / static_cast<double>(length);
  for (int i=0; i<(*specLength); i++)
  {
    if (fd[i] <= 0.0)
      fd[i] = DBL_MIN;
    fd[i] *= normalizeFactor;
  }

  // waste the grabage
  delete[] td;
    
  // give the result back
  return fd;
}

//-------------------------------------------------------------------------------------------------

void FourierTransformation::WindowFunc(const int whichFunction, const int N, double* in)
{
  int i;

  if (whichFunction == 1) {
    // Bartlett (triangular) window
    for (i = 0; i < N / 2; i++) {
      in[i] *= (i / (double) (N / 2));
      in[i + (N / 2)] *= (1.0 - (i / (double) (N / 2)));
    }
  }
  if (whichFunction == 2) {
    // Hamming
    for (i = 0; i < N; i++)
      in[i] *= 0.54 - 0.46 * cos(2 * M_PI * i / (N - 1));
  }
  if (whichFunction == 3) {
    // Hanning
    for (i = 0; i < N; i++)
      in[i] *= 0.50 - 0.50 * cos(2 * M_PI * i / (N - 1));
  }
}

//-------------------------------------------------------------------------------------------------

int FourierTransformation::IsPowerOfTwo(const int x)
{
   if (x < 2)
      return false;
   if (x & (x - 1))             // this is very very tricky !!!
      return false;
   return true;
}

//-------------------------------------------------------------------------------------------------

int FourierTransformation::NumberOfBitsNeeded(const int PowerOfTwo)
{
  assert(PowerOfTwo > 2);
  for (int i = 0;; i++)
    if (PowerOfTwo & (1 << i))
      return i;
}

//-------------------------------------------------------------------------------------------------

void FourierTransformation::InitFFT()
{
  gFFTBitTable = new int *[MaxFastBits];
  int len = 2;
  for (int b = 1; b <= MaxFastBits; b++) {
    gFFTBitTable[b - 1] = new int[len];
    for (int i = 0; i < len; i++)
      gFFTBitTable[b - 1][i] = ReverseBits(i, b);
    len <<= 1;
  }
}

//-------------------------------------------------------------------------------------------------

int FourierTransformation::ReverseBits(int index, const int NumBits)
{
   int rev;
   for (int i = rev = 0; i < NumBits; i++) {
      rev = (rev << 1) | (index & 1);
      index >>= 1;
   }
   return rev;
}

//-------------------------------------------------------------------------------------------------

int FourierTransformation::FastReverseBits(const int i, const int NumBits)
{
   if (NumBits <= MaxFastBits)
     return gFFTBitTable[NumBits - 1][i];
   else
     return ReverseBits(i, NumBits);
}

//-------------------------------------------------------------------------------------------------


} // namepsace clauer
} // namepsace math
