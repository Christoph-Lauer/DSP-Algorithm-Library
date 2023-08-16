/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    WaveletTransformation.cpp
 * @class   WaveletTransformation
 * @version 1.0
 * @date    june 1999
 * @author  Christoph Lauer
 * @brief   Definition of the Wavelet Transformation algorithmus
 *
 * @see     http://de.wikipedia.org/wiki/Wavelet-Transformation
 * @todo    finished so far
 *
 * This class file defines an wavelet transformation implemented with the 
 * pyramid multiresolution algorithmus. 
 */
 
//--------------------------------------------------------------------------------------------------

// Local headers
#include "wavelet_transformation.h"

// macro definitions
#define min(a,b) (((a)<(b))?(a):(b))
#define max(a,b) (((a)>(b))?(a):(b))

//--------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{

//--------------------------------------------------------------------------------------------------

double* WaveletTransformation::doWaveletTransformation(const real *in, const char *art, const int range, const int N)
{
	real* sums = new real[N];
	real* out  = new real[N];   	// wird auﬂerhalb gebraucht
	int i,kind;
	const pqf* h, *g; 		//Filter

	kind=LOW_PASS_QF;
	h=qf(art,range,kind);
	kind=HIGH_PASS_QF;
	g=qf(art,range,kind);
	for(i=0;i<N;i++)
		{
			out [i] = 0.0;
			sums[i] = 0.0;
		}
	dwtpd0(out,sums,in,N,h,g); //TRANSFORMATION
  // waste the grabage
  delete[] sums;
  // give the result back
	return out;
}	


//--------------------------------------------------------------------------------------------------

void WaveletTransformation::dwtpd0(real *difs,real *sums,const real *in,int N,
	 						 const pqf *H,const pqf *G)
{
  int N2;
  if(N>1)
    {
      N2 = N/2;
      cdpi( difs+N2, 1, in, N, G );
      cdpi( sums+N2, 1, in, N, H );
      dwtpd0( difs, sums, sums+N2, N2, H, G );
    }
  else
    difs[0] += in[0];
  return;
}

//--------------------------------------------------------------------------------------------------

void WaveletTransformation::cdpi(real *out,int step,const real *in,int q,const pqf *F )
{
  int i, j, q2;
  const real *filt;
  real *outp;

  assert( (q&1)==0 );	         // Test that `q' is even
  q2 = q/2;                    // Number of outputs
  if( q > F->omega-F->alpha )  // Long input case
    {
      int  ja2, jo2;

      for( j=0; j<q; j++ )
	{
	  ja2 = ICH(j+F->alpha);
	  jo2 = IFH(j+F->omega);
	  i=0;    outp = out;    filt = F->f + q-j;
	  while( i<=jo2-q2 )
	    {
	      *outp += (*filt) * (*in);
	      ++i;  outp += step; filt += 2;
	    }

	  i = max(0, ja2); outp = out+i*step; filt = F->f + 2*i-j;
	  while(  i<=min(q2-1,jo2) )
	    {
	      *outp += (*filt) * (*in);
	      ++i; outp += step; filt += 2;
	    }
	  i = ja2+q2; outp = out+i*step; filt = F->f + 2*i-j-q;
	  while( i<q2 )
	    {
	      *outp +=(*filt) * (*in);
	      ++i; outp += step; filt += 2;
	    }
	  ++in;
	}
    }
  else                         // Short input case
    {
      for( j=0; j<q; j++ )
	{
	  filt = F->fp + PQFO(q2) - j;
	  outp = out;
	  for( i=0; i<q2; i++ )
	    {
	      *outp += (*filt) * (*in);
	      filt += 2;   outp += step;
	    }
	  ++in;
	}
    }
  return;
}

//--------------------------------------------------------------------------------------------------

pqf * WaveletTransformation::mkpqf(
	const real *coefs,	// Original filter coefficients
	int alpha,		      // Least valid index of `?->f[]'
	int omega,		      // Greatest valid index of `?->f[]'
	int flags)		      // Reserved for future use
{
  pqf *qm;
  int M;

  assert(alpha <= 0);		// Conventional indexing
  assert(0 <= omega);		// Conventional indexing

  qm = (pqf *)calloc(1,sizeof(pqf)); assert(qm);
  qm->alpha = alpha;
  qm->omega = omega;
  qm->f = coefs-alpha;
  M = IFH( omega-alpha );
  qm->fp = (real *)calloc( PQFL(M), sizeof(real)); assert(qm->fp);
  qfcirc( qm->fp, qm->f, alpha, omega );
  qm->center = coe( qm->f, alpha, omega );
  qm->deviation = lphdev( qm->f, alpha, omega );
  return(qm);
}

//--------------------------------------------------------------------------------------------------

pqf* WaveletTransformation::qf(				// Coefficient struct or NULL pointer
     const char *name,		            // String "B", "C", "D", or "V"
     int         range,		            // Length of coefficient array
     int         kind)		            // LOW_PASS_QF or HIGH_PASS_QF
{
  pqf* qm;
  qm = 0;			// Return NULL pointer if unsuccessful

  /**
   * Choose an orthogonal filter based on the first letter of
   * the filter name: `name' argument starts with  B, C, D, V
   */
  switch( name[0] ) {

  case 'b':
  case 'B':
    /**
     * Beylkin filters.
     * Here are the coefficients for Gregory BEYLKIN'S wave packets.
     */
    switch( range )
    {
      case 18: qm = SETSDPQF( b18, kind ); break;
      default: break;		                    // Fall out: the length is unavailable
    }
    break;			                            // Beylkin type

  case 'c':
  case 'C':
    /**
     * Coiflet filters.
     * Here are the coefficients for COIFLETS with respectively
     * 2, 4, 6, 8 and 10 moments vanishing for phi.  Filter Q has
     * range/3 moments vanishing.  Filter P has range/3 moments
     * vanishing with the appropriate shift.
     */
    switch( range )
    {
      case 6:  qm = SETSDPQF( c06, kind ); break;
      case 12: qm = SETSDPQF( c12, kind ); break;
      case 18: qm = SETSDPQF( c18, kind ); break;
      case 24: qm = SETSDPQF( c24, kind ); break;
      case 30: qm = SETSDPQF( c30, kind ); break;
      default: break;		                    // Fall out: the length is unavailable
    }
    break;			                            // Coifman type

  case 's':
  case 'S':			                            // STANDARD filters, in old terminology
  case 'd':
  case 'D':
    /**
     * Daubechies filters.
     * Initialize quadrature mirror filters P,Q of length 'range' with
     * smooth limits and minimal phase, as in DAUBECHIES:
     */
    switch( range )
    {
      case 2:  qm = SETSDPQF( d02, kind ); break;
      case 4:  qm = SETSDPQF( d04, kind ); break;
      case 6:  qm = SETSDPQF( d06, kind ); break;
      case 8:  qm = SETSDPQF( d08, kind ); break;
      case 10: qm = SETSDPQF( d10, kind ); break;
      case 12: qm = SETSDPQF( d12, kind ); break;
      case 14: qm = SETSDPQF( d14, kind ); break;
      case 16: qm = SETSDPQF( d16, kind ); break;
      case 18: qm = SETSDPQF( d18, kind ); break;
      case 20: qm = SETSDPQF( d20, kind ); break;
      default: break;		                  // Fall out: the length is unavailable
    }
    break;			                          // Standard or Daubechies type

  case 'v':
  case 'V':
    /**
     * Vaidyanathan filters
     * The following coefficients correspond to one of the filters
     * constructed by Vaidyanathan (Filter #24B from his paper
     * in IEEE Trans. ASSP Vol.36, pp 81-94 (1988). These coefficients
     * give an exact reconstruction scheme, but they don't satisfy ANY
     * moment condition (not even the normalization : the sum of the c_n
     * is close to 1, but not equal to 1). The function one obtains after
     * iterating the reconstruction procedure 9 times looks continuous,
     * but is of course not differentiable. It would be interesting to
     * see how such a filter performs. It has been optimized, for its
     * filter length, for the standard requirements that speech people
     * impose.
     */
    switch( range )
    {
      case 24:  qm = SETSDPQF( v24, kind ); break;
      default: break;		                // Fall out: the length is unavailable
    }
    break;			                        // Vaidyanathan type
  default: break;		                    // Fall out: the filter is unavailable. */
  }
  return(qm);
}

//--------------------------------------------------------------------------------------------------

real WaveletTransformation::coe(				// Center of energy
      const real *in,		                // Sequence of coefficients
      int alpha,		                    // First valid `in[]' index
      int omega)		                    // Last valid `in[]' index
{
  real center, energy;
  int k;

  assert(alpha <= 0);		                // Conventional indexing
  assert(0 <= omega);		                // Conventional indexing

  center = 0;  energy = 0;
  for( k=alpha; k<=omega; k++)
    {
      center += k*in[k]*in[k];
      energy += in[k]*in[k];
    }
  if( energy>0 ) center /= energy;
  return(center);
}

//--------------------------------------------------------------------------------------------------

real WaveletTransformation::lphdev(			// Center of energy
	 const real *in,	                    // Sequence of coefficients. */
	 int alpha,		                        // First valid `in[]' index
	 int omega)		                        // Last valid `in[]' index
{
  real energy, fx, deviation;
  int k, x, sgn;
  assert(alpha <= 0);		                // Conventional indexing
  assert(0 <= omega);		                // Conventional indexing
  // First compute the sum of the squares of the sequence elements: 
  energy = 0;
  for( k=alpha; k<omega; k++)    energy += in[k]*in[k];
  // If the sum of the squares in nonzero, compute the deviation: */
  deviation = 0;
  if( energy>0 )
    {
      sgn= -1;
      for( x=1;  x <= (omega-alpha)/2;  x++ )
	{
	  fx = 0;
	  for( k=x+alpha; k<=omega-x; k++ )
	    {
	      fx += k*in[k-x]*in[k+x];
	    }
	  deviation += sgn*fx;
	  sgn = -sgn;
	}
      deviation = absval(deviation);
      deviation /= energy;	// Normalize
      deviation *= 2;		    // Final factor from the formula
    }
  // If `energy==0' then `deviation' is trivially 0
  return(deviation);
}

//--------------------------------------------------------------------------------------------------

void WaveletTransformation::periodize(
			real *fq,        // Preallocated output array
	    int q,           // Length of `fq[]'
	    const real *f,   // Input array
	    int alpha,       // First valid `f[]' index
	    int omega)       // Last valid `f[]' index
{
  int j, k;

  assert(q>0);			   // Nontrivial period
  assert(q<=omega-alpha);	// Periodization needed
  assert(alpha <= 0);		  // Conventional indexing
  assert(0 <= omega);		  // Conventional indexing

  for(k=0; k<q; k++)
    {
      fq[k] = 0;
      j = (alpha-k)/q;		// `j==ceil((alpha-k)/q)' 
      while( (k+j*q) <= omega )
    	{
    	  fq[k] += f[k+j*q];
    	  j++;
    	}
    }
  return;
}

//--------------------------------------------------------------------------------------------------

void WaveletTransformation::qfcirc(
	 real *fp,	          // Preallocated output array
	 const real *f,	      // Filter coefficients 
	 int alpha,	          // First valid `f[]' index
	 int omega)	          // Last valid `f[]' index
{
  int k, m, M, q;
  real *fq;

  assert(alpha <= 0);   // Conventional indexing
  assert(0 <= omega);	  // Conventional indexing

  M = IFH(omega-alpha); // Max with `2*M <= omega-alpha'

  for( m=1; m<=M; m++ )
    {
      q  = 2*m;
      fq = fp+PQFO(m);
      periodize( fq, q, f, alpha, omega);
      for( k=0; k<q; k++ )
        fq[k-q] = fq[k];
    }
  return;
}

//--------------------------------------------------------------------------------------------------

} // namespace clauer
} // namespace math
