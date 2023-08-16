package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    WaveletTransformation.cpp
// * @class   WaveletTransformation
// * @version 1.0
// * @date    june 1999
// * @author  Christoph Lauer
// * @brief   Definition of the Wavelet Transformation algorithmus
// *
// * @see     http://de.wikipedia.org/wiki/Wavelet-Transformation
// * @todo    finished so far
// *
// * This class file defines an wavelet transformation implemented with the 
// * pyramid multiresolution algorithmus. 
// 

//--------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    WaveletTransformation.h
// * @class   WaveletTransformation
// * @version 1.0
// * @date    june 1999
// * @author  Christoph Lauer
// * @brief   Definition of the Wavelet Transformation algorithmus
// *
// * @see     http://de.wikipedia.org/wiki/Wavelet-Transformation
// * @todo    finished so far
// *
// * This class file defines an wavelet transformation implemented with the 
// * pyramid multiresolution algorithmus. As most of the mathicatical classes 
// * the functions here are all defined as static functions so no instantiation
// * is necessary.
// 

//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_WAVELETTRANSFORMATION
//#define CLAUER_MATH_WAVELETTRANSFORMATION

//--------------------------------------------------------------------------------------------------

// local headers

// stl headers

//--------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------

//*
// * This class defines the multiresolution pyramid wavelet algorithmus.
// 

//--------------------------------------------------------------------------------------------------

public class WaveletTransformation
{
// *
//  * The core of the transformation is defined as static class. As folow the list of possible wavelets
//  * where the filer pairs are hard coded inti the header file.
//  * b18, c06, c12, c18, c24, c30, d01, d04, d06, d08, d10, d12, d14, d16, d18, d20, v24.
//  *
//  * @param   in     the time domain based input vector
//  * @param   art    either 'b' 'c' 'd' 'v'
//  * @param   range  the wavelet order
//  * @param   N      the length of the vector should be a power of two
//  * @return         the wavelet domain vector will be allocated into the algorithm
//  *                 and has the same size as the input vector, the size N. This array
//	* 								is no inplace implementation, this means the vector newly allocated
//	*									into this function and the original input vector is not overwritten.
//  

  //--------------------------------------------------------------------------------------------------
  
  public static double doWaveletTransformation(double in, String art, int range, int N)
  {
	  double[] sums = new double[N];
	  double[] out = new double[N]; // wird auﬂerhalb gebraucht
	  int i;
	  int kind;
	  final pqf h; //Filter
	  final pqf g;
  
	  kind =DefineConstantsWavelet_transformation.LOW_PASS_QF;
	  h =qf(art, range, kind);
	  kind =DefineConstantsWavelet_transformation.HIGH_PASS_QF;
	  g =qf(art, range, kind);
	  for(i =0;i<N;i++)
		  {
			  out [i] = 0.0;
			  sums[i] = 0.0;
		  }
	  RefObject<Double> TempRefObject = new RefObject<Double>(out);
	  RefObject<Double> TempRefObject2 = new RefObject<Double>(sums);
	  dwtpd0(TempRefObject, TempRefObject2, in, N, h, g); //TRANSFORMATION
	  out = TempRefObject.argvalue;
	  sums = TempRefObject2.argvalue;
	// waste the grabage
	sums = null;
	// give the result back
	  return out;
  }

//--------------------------------------------------------------------------------------------------


// *
//  * All the static functions in thes group are helper functions for the wavelet transformation.
//  

  //@{ // BEGIN OF THE WAVLETET TRANSFORM HELPER FUNCTION GROUP

// *
//  * Prepares the high and low pass filters.
//  

  //--------------------------------------------------------------------------------------------------
  
  private static void cdpi(RefObject<Double> out, int step, double in, int q, pqf F)
  {
	int i;
	int j;
	int q2;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	final double *filt;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	double *outp;
  
	assert (q &1)==0; // Test that `q' is even
	q2 = q/2; // Number of outputs
	if(q > F.omega-F.alpha) // Long input case
	  {
		int ja2;
		int jo2;
  
		for(j =0; j<q; j++)
	  {
		ja2 = ((j+F.alpha)&1?((j+F.alpha)+1)/2:(j+F.alpha)/2);
		jo2 = ((j+F.omega)&1?((j+F.omega)-1)/2:(j+F.omega)/2);
		i =0;
		outp = out.argvalue;
		filt = F.f + q-j;
		while(i<=jo2-q2)
		  {
			*outp += (*filt) * ( in);
			++i;
			outp += step;
			filt += 2;
		  }
  
		i = ((0)>(ja2)?(0):(ja2));
		outp = out.argvalue+i *step;
		filt = F.f + 2 *i-j;
		while(i<=((q2-1)<(jo2)?(q2-1):(jo2)))
		  {
			*outp += (*filt) * ( in);
			++i;
			outp += step;
			filt += 2;
		  }
		i = ja2+q2;
		outp = out.argvalue+i *step;
		filt = F.f + 2 *i-j-q;
		while(i<q2)
		  {
			*outp +=(*filt) * ( in);
			++i;
			outp += step;
			filt += 2;
		  }
		++in;
	  }
	  }
	else // Short input case
	  {
		for(j =0; j<q; j++)
	  {
		filt = F.fp + (2*(q2)*(q2)) - j;
		outp = out.argvalue;
		for(i =0; i<q2; i++)
		  {
			*outp += (*filt) * ( in);
			filt += 2;
			outp += step;
		  }
		++in;
	  }
	  }
	return;
  }

// *
//  * [D]iscrete [W]avelet [T]ransform on [P]eriodic
//  * [D]isjoint arrays, down to level [0]; Recursive.
//  * Calling sequence and basic algorithm:
//  *  dwtpd0( DIFS, SUMS, IN, N, H, G ):
//  *     If N > 1 then
//  *        cdpi( DIFS+N/2, 1, IN, N, G )
//  *        cdpi( SUMS+N/2, 1, IN, N, H )
//  *        dwtpd0( DIFS, SUMS, SUMS+N/2, N/2, H, G )
//  *     Else
//  *        Let DIFS[0] += IN[0]
//  * Assumptions:
//  *	1. `difs[]' and `sums[]' are disjoint and disjoint from `in[]'.
//  *	2. `N' is a nonnegative integer power of 2.
//  *	3. `sums[0]...sums[N-1]' are 0 at the outset.
//  *
//  * @param  difs		This preallocated array must have
//  *				          at least `N' elements.
//  *	@param  sums		This preallocated array must have
//  *				          at least `N' zeroes.
//  *	@param  in	    This input array must have at least
//  *				          `N' elements.  It is not changed.
//  *	@param  N			  This must be 2^L for some L>=0.
//  *	@param  H		    These are QF structs used for low-pass and
//  *	@param  G	 	    high-pass convolution-decimation.
//  *	@param  difs		This is the pointer to the retuen array
//  *                 This array is filled by reference with
//  *				          wavelet coefficients.
//  *	@param  sums		This is the pointer to the return array
//  *                 This array is filled by reference with
//  *				          scaling function coefficients.
//  
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//  static void dwtpd0(RefObject<double> difs, RefObject<double> sums, double in, int N, pqf H, pqf G);

// *
//  * This function computes the periodized filter coefficients
//  * associated to the input array of quadrature mirror filter
//  * coefficients, for lengths 2, 4, 6,...  It then fills an array
//  * with these periodized coefficients, duplicating some of them
//  * to facilitate circular convolution.  If the input array is
//  *          f[alpha], f[alpha+1], ..., f[omega],
//  * and M is the largest integer satisfying `2M<=omega-alpha',
//  * then the output array is the concatenation of:
//  *
//  * Start     Contents                                         End
//  * -----  -------------------------------------------------- -----
//  *   0            f2[0],f2[1],f2[0],f2[1];                     3
//  *   4    f4[0],f4[1],f4[2],f4[3],f4[0],f4[1],f4[2],f4[3];     11
//  *  12    f6[0],f6[1],f6[2],...,f6[5],f6[0],f6[1],...,f6[5];   23
//  *  24    f8[0],f8[1],f8[2],...,f8[7],f8[0],f8[1],...,f8[7];   39
//  *   .          ...         ...                   ...      ;    .
//  * S(m-1) f{2m}[0],...,f{2m}[2m-1],f{2m}[0],...,f{2m}[2m-1]; S(m)-1
//  *   .          ...         ...             ...            ;    .
//  * S(M-1) f{2M}[0],...,f{2M}[2M-1],f{2M}[0],...,f{2M}[2M-1]; S(M)-1
//  *
//  * where S(m)= 4+8+16+...+ 4(m-1) = 2m(m-1) gives the
//  * starting index of the m-th segment.
//  *
//  * The "midpoint" or "origin" of the m-th periodic subarray is:
//  *		PQFO(m) = S(m-1)+2m = 2m*m.
//  * The total length of concatenated periodic subarrays `1,...,M' is
//  *		PQFL(M) = S(M-1)+4M = 2M*(M+1).
//  *  Calling sequence and basic algorithm:
//  * qfcirc(FP, F, ALPHA, OMEGA)
//  *   Let M = IFH(omega-alpha)
//  *   For N = 1 to M
//  *      Let Q = 2*N
//  *      Let FQ = FP + PQFO(N)
//  *      periodize( FQ, Q, F, ALPHA, OMEGA )
//  *      For K = 0 to Q-1
//  *         Let FQ[K-Q] = FQ[K]
//  *
//  * Assumptions: Conventional indexing: `alpha <= 0 <= omega'.
//  * @aram    fp	      This must point to a preallocated array
//  *           			  with at least `PQFL(M)' memory
//  *           			  locations.  This function call fills
//  *		            	  those locations, beginning with 0.
//  *	@param   f	      This is an array of quadrature mirror
//  *			              filter coefficients.
//  * @param   alpha    These are to be the first and last valid
//  * @param   omega    indices of the array `f[]'.
//  *
//  * @return  fp	      This array is filled with periodized,
//  *			              partially duplicated arrays with
//  *			              lengths 4, 8, ..., 4M.
//  
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//  static void qfcirc(RefObject<double> fp, double f, int alpha, int omega);

// *
//  * Periodize an array into a shorter-length array.
//  * Assumptions:
//  *	(1) `omega-alpha >= q > 0',
//  *	(2) `alpha <= 0 <= omega'
//  *
//  * periodize(FQ, Q, F, ALPHA, OMEGA)
//  *   For K = 0 to Q-1
//  *      Let FQ[K] = 0
//  *      Let J = (ALPHA-K)/Q
//  *      While K+J*Q <= OMEGA
//  *         FQ[K] += F[K+J*Q]
//  *         J += 1
//  *
//  * @param   fq       Preallocated array of length `q'.
//  * @param   q        This is the period of `fq[]'.
//  * @param   f        This array holds the original function.
//  * @param   alpha    These are to be the first and last valid
//  * @param   omega    indices of the array `f[]'.
//  * @param   fq       This is the pointer to the return array
//  *                   The array `fq[]' is assigned as follows:
//  *                   fq[k] = f[k+j0*q]+...+f[k+j1*q],
//  *	                	k = 0, 1, ..., q-1;
//  *				            j0 = ceil[(alpha-k)/q];
//  *				            j1 = floor[(omega-k)/q]
//  
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//  static void periodize(RefObject<double> fq, int q, double f, int alpha, int omega);

// *
//  * Computes the maximum deviation from linear phase of the
//  * convolution-decimation operator associated to a sequence.
//  * Basic algorithm:
//  *                    Sum_{x>0} (-1)^x Sum_k k*in[k-x]*in[k+x]
//  *   deviation =  2 * ----------------------------------------
//  *                               Sum_k in[k]^2
//  * @param   in       The sequence is `in[alpha],...,in[omega]'
//  * @param   alpha    These are to be the first and last valid
//  * @param   omega    indices of the `pqf->f[]' struct member.
//  *	@return	 lphdev   This is the absolute value of the maximum.
// 

  //--------------------------------------------------------------------------------------------------
  
  private static double lphdev(double[] in, int alpha, int omega)
  {
	double energy;
	double fx;
	double deviation;
	int k;
	int x;
	int sgn;
	assert alpha <= 0; // Conventional indexing
	assert 0 <= omega; // Conventional indexing
	// First compute the sum of the squares of the sequence elements:
	energy = 0;
	for(k =alpha; k<omega; k++)
		energy += in[k]*in[k];
	// If the sum of the squares in nonzero, compute the deviation: */
	deviation = 0;
	if(energy>0)
	  {
		sgn = -1;
		for(x =1; x <= (omega-alpha)/2; x++)
	  {
		fx = 0;
		for(k =x+alpha; k<=omega-x; k++)
		  {
			fx += k *in[k-x]*in[k+x];
		  }
		deviation += sgn *fx;
		sgn = -sgn;
	  }
		deviation = ((deviation)<0?-(deviation):(deviation));
		deviation /= energy; // Normalize
		deviation *= 2; // Final factor from the formula
	  }
	// If `energy==0' then `deviation' is trivially 0
	return(deviation);
  }

// *
//  * Compute the center-of-energy for a sequence.
//  * Assumptions: Conventional indexing: `alpha <= 0 <= omega'
//  * Basic algorithm: center =  ( Sum_k k*in[x]^2 ) / (Sum_k in[k]^2 )
//  *	coe( in, alpha, omega )
//  *
//  * @param   in	      The sequence is `in[alpha],...,in[omega]'
//  * @param   alpha    These are to be the first and last
//  * @param   omega		valid indices of the array `in[]'.
//  *	@return  coe		  This is in the interval `[alpha, omega]'.
//  

  //--------------------------------------------------------------------------------------------------
  
  private static double coe(double[] in, int alpha, int omega)
  {
	double center;
	double energy;
	int k;
  
	assert alpha <= 0; // Conventional indexing
	assert 0 <= omega; // Conventional indexing
  
	center = 0;
	energy = 0;
	for(k =alpha; k<=omega; k++)
	  {
		center += k *in[k]*in[k];
		energy += in[k]*in[k];
	  }
	if(energy>0)
		center /= energy;
	return(center);
  }

// *
//  * The `qf()' function returns a pointer to a `pqf' data structure
//  *  containing the coefficients, name, length and kind of the filter
//  *  named by the input parameters.
//  *
//  * @param   name     This is the name of the filter, specified as
//  *				            at least the first letter of "Beylkin",
//  *			            	"Coifman", "Vaidyanathan", or "Daubechies",
//  *                   written as a string (enclosed in " marks).
//  *  			          	Case is ignored, only the first letter
//  *                   matters, and "Standard" is an alias for "D".
//  * @param   range    This is the length of the requested filter.  Allowed
//  *				            values depend on what `name' is.
//  * @param   kind	    If kind==LOW_PASS_QF, qf() returns the summing or
//  *		        	      low-pass filter `pqf' structure.
//  *		              	If kind==HIGH_PASS_QF, qf() returns the differencing
//  *	            		  or high-pass filter `pqf' structure.
//  * @return  qf       If a `name'd filter of the requested `range' and
//  *			              `kind' is listed, then the return value is
//  *			              a pointer to a newly-allocated pqf struct
//  *		      	        specifying a  conventionally-indexed filter
//  *			              to be used for convolution-decimation.
//  * 			            Otherwise, the returned pointer is NULL.
//  

  //--------------------------------------------------------------------------------------------------
  
  private static pqf qf(String name, int range, int kind)
  {
	pqf qm;
	qm = 0; // Return NULL pointer if unsuccessful
  
  //  *
  //   * Choose an orthogonal filter based on the first letter of
  //   * the filter name: `name' argument starts with  B, C, D, V
  //
	switch(name.charAt(0))
	{
  
	case 'b':
	case 'B':
  //    *
  //     * Beylkin filters.
  //     * Here are the coefficients for Gregory BEYLKIN'S wave packets.
  //
	  switch(range)
	  {
		case 18:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		default: // Fall out: the length is unavailable
			break;
	  }
	  break; // Beylkin type
  
	case 'c':
	case 'C':
  //    *
  //     * Coiflet filters.
  //     * Here are the coefficients for COIFLETS with respectively
  //     * 2, 4, 6, 8 and 10 moments vanishing for phi.  Filter Q has
  //     * range/3 moments vanishing.  Filter P has range/3 moments
  //     * vanishing with the appropriate shift.
  //
	  switch(range)
	  {
		case 6:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 12:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 18:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 24:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 30:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		default: // Fall out: the length is unavailable
			break;
	  }
	  break; // Coifman type
  
	case 's':
	case 'S': // STANDARD filters, in old terminology
	case 'd':
	case 'D':
  //    *
  //     * Daubechies filters.
  //     * Initialize quadrature mirror filters P,Q of length 'range' with
  //     * smooth limits and minimal phase, as in DAUBECHIES:
  //
	  switch(range)
	  {
		case 2:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 4:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 6:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 8:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 10:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 12:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 14:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 16:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 18:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		case 20:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		default: // Fall out: the length is unavailable
			break;
	  }
	  break; // Standard or Daubechies type
  
	case 'v':
	case 'V':
  //    *
  //     * Vaidyanathan filters
  //     * The following coefficients correspond to one of the filters
  //     * constructed by Vaidyanathan (Filter #24B from his paper
  //     * in IEEE Trans. ASSP Vol.36, pp 81-94 (1988). These coefficients
  //     * give an exact reconstruction scheme, but they don't satisfy ANY
  //     * moment condition (not even the normalization : the sum of the c_n
  //     * is close to 1, but not equal to 1). The function one obtains after
  //     * iterating the reconstruction procedure 9 times looks continuous,
  //     * but is of course not differentiable. It would be interesting to
  //     * see how such a filter performs. It has been optimized, for its
  //     * filter length, for the standard requirements that speech people
  //     * impose.
  //
	  switch(range)
	  {
		case 24:
			qm = (kind ==DefineConstantsWavelet_transformation.LOW_PASS_QF) ? mkpqf(nsoqf, nsalpha, nsomega, 0) : mkpqf(ndoqf, ndalpha, ndomega, 0);
			break;
		default: // Fall out: the length is unavailable
			break;
	  }
	  break; // Vaidyanathan type
	default: // Fall out: the filter is unavailable. */
		break;
	}
	return(qm);
  }

// *
//  * The `mkpqf()' function returns a pointer to a `pqf' data structure
//  * containing the coefficients, length and the pre-periodized filter
//  * named by the input parameters.
//  * Assumptions: Conventional indexing: `alpha <= 0 <= omega'.
//  *
//  * @param  coefs     These are the filter coefficients,
//  *				            assumed to be given in the array
//  *				            `coefs[0],...,coefs[omega-alpha]'
//  *				            are the only nonzero values.
//  * @param  alpha     These are to be the first and last valid
//  * @param  omega     indices of the pqf->f struct member.
//  *	@param  flags	    This is reserved for later uses, such as
//  *			              indicating when to generate a full QF
//  *			              sequence from just one symmetric half.
//  * @return mkpqf     The return value is a pointer to a newly
//  *			              allocated pqf struct containing `coefs[]',
//  *			              `alpha', `omega', and the preperiodized
//  *			              version of  `coefs[]'.
//  

  //--------------------------------------------------------------------------------------------------
  
  private static pqf mkpqf(double coefs, int alpha, int omega, int flags)
  {
	pqf qm;
	int M;
  
	assert alpha <= 0; // Conventional indexing
	assert 0 <= omega; // Conventional indexing
  
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	qm = (pqf)calloc(1,sizeof(pqf));
	assert qm;
	qm.alpha = alpha;
	qm.omega = omega;
	qm.f = coefs-alpha;
	M = ((omega-alpha)&1?((omega-alpha)-1)/2:(omega-alpha)/2);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	qm.fp = (double)calloc((2*(M)*(M+1)), sizeof(double));
	assert qm.fp;
	RefObject<Double> TempRefObject = new RefObject<Double>(qm.fp);
	qfcirc(TempRefObject, qm.f, alpha, omega);
	qm.fp = TempRefObject.argvalue;
	qm.center = coe(qm.f, alpha, omega);
	qm.deviation = lphdev(qm.f, alpha, omega);
	return(qm);
  }

//@} // END OF THE WAVLETET TRANSFORM HELPER FUNCTION GROUP

}

//--------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_WAVELETTRANSFORMATION
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


//--------------------------------------------------------------------------------------------------



//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER WARNING: The declaration of the following method implementation was not found:
//ORIGINAL LINE: void WaveletTransformation::dwtpd0(double *difs,double *sums,const double *in,int N, const pqf *H,const pqf *G)

//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER WARNING: The declaration of the following method implementation was not found:
//ORIGINAL LINE: void WaveletTransformation::periodize(double *fq, int q, const double *f, int alpha, int omega)
 // Last valid `f[]' index
//--------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER WARNING: The declaration of the following method implementation was not found:
//ORIGINAL LINE: void WaveletTransformation::qfcirc(double *fp, const double *f, int alpha, int omega)
 // Last valid `f[]' index
//--------------------------------------------------------------------------------------------------


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

public class WaveletTransformation
{
	public void dwtpd0(double[] difs, RefObject<Double> sums, double[] in, int N, pqf H, pqf G)
	{
	  int N2;
	  if(N>1)
		{
		  N2 = N/2;
		  cdpi(difs+N2, 1, in, N, G);
		  cdpi(sums.argvalue+N2, 1, in, N, H);
		  dwtpd0(difs, sums.argvalue, sums.argvalue+N2, N2, H, G);
		}
	  else
		difs[0] += in[0];
	  return;
	}
	public void periodize(double[] fq, int q, double[] f, int alpha, int omega)
	{
	  int j;
	  int k;
	
	  assert q>0; // Nontrivial period
	  assert q<=omega-alpha; // Periodization needed
	  assert alpha <= 0; // Conventional indexing
	  assert 0 <= omega; // Conventional indexing
	
	  for(k =0; k<q; k++)
		{
		  fq[k] = 0;
		  j = (alpha-k)/q; // `j==ceil((alpha-k)/q)'
		  while((k+j *q) <= omega)
			{
			  fq[k] += f[k+j *q];
			  j++;
			}
		}
	  return;
	}
	public void qfcirc(RefObject<Double> fp, double f, int alpha, int omega)
	{
	  int k;
	  int m;
	  int M;
	  int q;
	  double[] fq;
	
	  assert alpha <= 0; // Conventional indexing
	  assert 0 <= omega; // Conventional indexing
	
	  M = ((omega-alpha)&1?((omega-alpha)-1)/2:(omega-alpha)/2);
	
	  for(m =1; m<=M; m++)
		{
		  q = 2 *m;
		  fq = fp.argvalue+(2*(m)*(m));
		  periodize(fq, q, f, alpha, omega);
		  for(k =0; k<q; k++)
			fq[k-q] = fq[k];
		}
	  return;
	}
}