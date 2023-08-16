/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    WaveletTransformation.h
 * @class   WaveletTransformation
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Definition of the Wavelet Transformation algorithmus
 *
 * @see     http://de.wikipedia.org/wiki/Wavelet-Transformation
 * @todo    finished so far
 *
 * This class file defines an wavelet transformation implemented with the 
 * pyramid multiresolution algorithmus. As most of the mathicatical classes 
 * the functions here are all defined as static functions so no instantiation
 * is necessary.
 */

//--------------------------------------------------------------------------------------------------
 
#ifndef CLAUER_MATH_WAVELETTRANSFORMATION
#define CLAUER_MATH_WAVELETTRANSFORMATION

//--------------------------------------------------------------------------------------------------

// local headers
#include "wavelet_definitions.h"

// stl headers
#include <assert.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

//--------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

//--------------------------------------------------------------------------------------------------

/**
 * This class defines the multiresolution pyramid wavelet algorithmus.
 */

//--------------------------------------------------------------------------------------------------
 
class WaveletTransformation
{
public:
 /**
  * The core of the transformation is defined as static class. As folow the list of possible wavelets
  * where the filer pairs are hard coded inti the header file.
  * b18, c06, c12, c18, c24, c30, d01, d04, d06, d08, d10, d12, d14, d16, d18, d20, v24.
  *
  * @param   in     the time domain based input vector
  * @param   art    either 'b' 'c' 'd' 'v'
  * @param   range  the wavelet order
  * @param   N      the length of the vector should be a power of two
  * @return         the wavelet domain vector will be allocated into the algorithm
  *                 and has the same size as the input vector, the size N. This array
  *                 is no inplace implementation, this means the vector newly allocated
  *	            into this function and the original input vector is not overwritten.
  */
  static double* doWaveletTransformation(const real* in, const char* art, const int range, const int N);

//--------------------------------------------------------------------------------------------------

private:

 /**
  * All the static functions in thes group are helper functions for the wavelet transformation.
  */

  //@{ // BEGIN OF THE WAVLETET TRANSFORM HELPER FUNCTION GROUP
 
 /**
  * Prepares the high and low pass filters.
  */
  static void cdpi(real *out,int step,const real *in,int q,const pqf *F );

 /**
  * [D]iscrete [W]avelet [T]ransform on [P]eriodic
  * [D]isjoint arrays, down to level [0]; Recursive.
  * Calling sequence and basic algorithm:
  *  dwtpd0( DIFS, SUMS, IN, N, H, G ):
  *     If N > 1 then
  *        cdpi( DIFS+N/2, 1, IN, N, G )
  *        cdpi( SUMS+N/2, 1, IN, N, H )
  *        dwtpd0( DIFS, SUMS, SUMS+N/2, N/2, H, G )
  *     Else
  *        Let DIFS[0] += IN[0]
  * Assumptions:
  *	1. `difs[]' and `sums[]' are disjoint and disjoint from `in[]'.
  *	2. `N' is a nonnegative integer power of 2.
  *	3. `sums[0]...sums[N-1]' are 0 at the outset.
  *
  * @param  difs		This preallocated array must have
  *				          at least `N' elements.
  *	@param  sums		This preallocated array must have
  *				          at least `N' zeroes.
  *	@param  in	    This input array must have at least
  *				          `N' elements.  It is not changed.
  *	@param  N			  This must be 2^L for some L>=0.
  *	@param  H		    These are QF structs used for low-pass and
  *	@param  G	 	    high-pass convolution-decimation.
  *	@param  difs		This is the pointer to the retuen array
  *                 This array is filled by reference with
  *				          wavelet coefficients.
  *	@param  sums		This is the pointer to the return array
  *                 This array is filled by reference with
  *				          scaling function coefficients.
  */
  static void dwtpd0(real* difs,real* sums,const real* in,int N,const pqf* H,const pqf* G);

 /**
  * This function computes the periodized filter coefficients
  * associated to the input array of quadrature mirror filter
  * coefficients, for lengths 2, 4, 6,...  It then fills an array
  * with these periodized coefficients, duplicating some of them
  * to facilitate circular convolution.  If the input array is
  *          f[alpha], f[alpha+1], ..., f[omega],
  * and M is the largest integer satisfying `2M<=omega-alpha',
  * then the output array is the concatenation of:
  *
  * Start     Contents                                         End
  * -----  -------------------------------------------------- -----
  *   0            f2[0],f2[1],f2[0],f2[1];                     3
  *   4    f4[0],f4[1],f4[2],f4[3],f4[0],f4[1],f4[2],f4[3];     11
  *  12    f6[0],f6[1],f6[2],...,f6[5],f6[0],f6[1],...,f6[5];   23
  *  24    f8[0],f8[1],f8[2],...,f8[7],f8[0],f8[1],...,f8[7];   39
  *   .          ...         ...                   ...      ;    .
  * S(m-1) f{2m}[0],...,f{2m}[2m-1],f{2m}[0],...,f{2m}[2m-1]; S(m)-1
  *   .          ...         ...             ...            ;    .
  * S(M-1) f{2M}[0],...,f{2M}[2M-1],f{2M}[0],...,f{2M}[2M-1]; S(M)-1
  *
  * where S(m)= 4+8+16+...+ 4(m-1) = 2m(m-1) gives the
  * starting index of the m-th segment.
  *
  * The "midpoint" or "origin" of the m-th periodic subarray is:
  *		PQFO(m) = S(m-1)+2m = 2m*m.
  * The total length of concatenated periodic subarrays `1,...,M' is
  *		PQFL(M) = S(M-1)+4M = 2M*(M+1).
  *  Calling sequence and basic algorithm:
  * qfcirc(FP, F, ALPHA, OMEGA)
  *   Let M = IFH(omega-alpha)
  *   For N = 1 to M
  *      Let Q = 2*N
  *      Let FQ = FP + PQFO(N)
  *      periodize( FQ, Q, F, ALPHA, OMEGA )
  *      For K = 0 to Q-1
  *         Let FQ[K-Q] = FQ[K]
  *
  * Assumptions: Conventional indexing: `alpha <= 0 <= omega'.
  * @aram    fp	      This must point to a preallocated array
  *           			  with at least `PQFL(M)' memory
  *           			  locations.  This function call fills
  *		            	  those locations, beginning with 0.
  *	@param   f	      This is an array of quadrature mirror
  *			              filter coefficients.
  * @param   alpha    These are to be the first and last valid
  * @param   omega    indices of the array `f[]'.
  *
  * @return  fp	      This array is filled with periodized,
  *			              partially duplicated arrays with
  *			              lengths 4, 8, ..., 4M.
  */
  static void qfcirc(real *fp,const real *f,int alpha,int omega);

 /**
  * Periodize an array into a shorter-length array.
  * Assumptions:
  *	(1) `omega-alpha >= q > 0',
  *	(2) `alpha <= 0 <= omega'
  *
  * periodize(FQ, Q, F, ALPHA, OMEGA)
  *   For K = 0 to Q-1
  *      Let FQ[K] = 0
  *      Let J = (ALPHA-K)/Q
  *      While K+J*Q <= OMEGA
  *         FQ[K] += F[K+J*Q]
  *         J += 1
  *
  * @param   fq       Preallocated array of length `q'.
  * @param   q        This is the period of `fq[]'.
  * @param   f        This array holds the original function.
  * @param   alpha    These are to be the first and last valid
  * @param   omega    indices of the array `f[]'.
  * @param   fq       This is the pointer to the return array
  *                   The array `fq[]' is assigned as follows:
  *                   fq[k] = f[k+j0*q]+...+f[k+j1*q],
  *	                	k = 0, 1, ..., q-1;
  *				            j0 = ceil[(alpha-k)/q];
  *				            j1 = floor[(omega-k)/q]
  */
  static void periodize(real *fq,int q,const real *f,int alpha,int omega);

 /**
  * Computes the maximum deviation from linear phase of the
  * convolution-decimation operator associated to a sequence.
  * Basic algorithm:
  *                    Sum_{x>0} (-1)^x Sum_k k*in[k-x]*in[k+x]
  *   deviation =  2 * ----------------------------------------
  *                               Sum_k in[k]^2
  * @param   in       The sequence is `in[alpha],...,in[omega]'
  * @param   alpha    These are to be the first and last valid
  * @param   omega    indices of the `pqf->f[]' struct member.
  *	@return	 lphdev   This is the absolute value of the maximum.
 */
  static real lphdev(const real *in,int alpha,int omega);

 /**
  * Compute the center-of-energy for a sequence.
  * Assumptions: Conventional indexing: `alpha <= 0 <= omega'
  * Basic algorithm: center =  ( Sum_k k*in[x]^2 ) / (Sum_k in[k]^2 )
  *	coe( in, alpha, omega )
  *
  * @param   in	      The sequence is `in[alpha],...,in[omega]'
  * @param   alpha    These are to be the first and last
  * @param   omega		valid indices of the array `in[]'.
  *	@return  coe		  This is in the interval `[alpha, omega]'.
  */
  static real coe(const real *in,int alpha,int omega);
  
 /**
  * The `qf()' function returns a pointer to a `pqf' data structure
  *  containing the coefficients, name, length and kind of the filter
  *  named by the input parameters.
  *
  * @param   name     This is the name of the filter, specified as
  *				            at least the first letter of "Beylkin",
  *			            	"Coifman", "Vaidyanathan", or "Daubechies",
  *                   written as a string (enclosed in " marks).
  *  			          	Case is ignored, only the first letter
  *                   matters, and "Standard" is an alias for "D".
  * @param   range    This is the length of the requested filter.  Allowed
  *				            values depend on what `name' is.
  * @param   kind	    If kind==LOW_PASS_QF, qf() returns the summing or
  *		        	      low-pass filter `pqf' structure.
  *		              	If kind==HIGH_PASS_QF, qf() returns the differencing
  *	            		  or high-pass filter `pqf' structure.
  * @return  qf       If a `name'd filter of the requested `range' and
  *			              `kind' is listed, then the return value is
  *			              a pointer to a newly-allocated pqf struct
  *		      	        specifying a  conventionally-indexed filter
  *			              to be used for convolution-decimation.
  * 			            Otherwise, the returned pointer is NULL.
  */
  static pqf* qf(const char *name,int range,int kind);

 /**
  * The `mkpqf()' function returns a pointer to a `pqf' data structure
  * containing the coefficients, length and the pre-periodized filter
  * named by the input parameters.
  * Assumptions: Conventional indexing: `alpha <= 0 <= omega'.
  *
  * @param  coefs     These are the filter coefficients,
  *				            assumed to be given in the array
  *				            `coefs[0],...,coefs[omega-alpha]'
  *				            are the only nonzero values.
  * @param  alpha     These are to be the first and last valid
  * @param  omega     indices of the pqf->f struct member.
  *	@param  flags	    This is reserved for later uses, such as
  *			              indicating when to generate a full QF
  *			              sequence from just one symmetric half.
  * @return mkpqf     The return value is a pointer to a newly
  *			              allocated pqf struct containing `coefs[]',
  *			              `alpha', `omega', and the preperiodized
  *			              version of  `coefs[]'.
  */
  static pqf* mkpqf(const real *coefs,int alpha,int omega,int flags);
  
//@} // END OF THE WAVLETET TRANSFORM HELPER FUNCTION GROUP

};

//--------------------------------------------------------------------------------------------------

} // namespace clauer
} // namespace math

//--------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_WAVELETTRANSFORMATION
