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

#ifndef CLAUER_MATH_PSEUDO_MARGENAU_HILL_DISTRIBUTION
#define CLAUER_MATH_PSEUDO_MARGENAU_HILL_DISTRIBUTION

//------------------------------------------------------------------------------------------------

#define ISODD(x)        ((x/2.0)== ((int)(x/2)) ? 0 : 1)

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This function implements the algorithm for the calculation of a "Discrete Smoothed Pseudo Wigner-
 * Ville-Distribution" for the given double vector input signal. The function calculates the PWVT 
 * corresponding to the equation:
 *
 *              /                                                             
 *              | h(tau)                                  -j2pi f tau         
 * PMH(t,f) =   | ------ ( x(t+tau)x*(t) + x(t)x*(t-tau) )e            dtau   
 *              |   2                                                         
 *             /                                                      
 */
class PseudoMargenauHillDistribution
{
 
//------------------------------------------------------------------------------------------------

public:

//------------------------------------------------------------------------------------------------
 
/** 
 * To prevent aliasing the real input signal must be band limited with the nyquist frequency SR/2. 
 * The discrtete wigner ville distribution has a band limitation of SR/4, so normally for complex 
 * signals the input time domain vector must be up samples to the double length. In our case we have a
 * real input signal, so we use the complex transformation with the analytic signal as input which 
 * prevents aliasing at the frequency SR/4 and the mirroring of the image. For this case we can switch
 * between the transformation with the analytic signal ans the real input signal. The upsampling method
 * only required for complex input signals which is not part of out implementation.
 */
enum PMHD_CALCULATION_METHOD
{
  /// The standard real transformation has a bandlimit of SR/4. The image will be mirrored in the middle.
  /// with the aliasing frequency SR/4.
  PMHD_CALCULATION_METHOD_REAL,
  /// With help of the hilbert tranformation the complex analytic signal can be transformated and the
  /// resulting image no mirroring in the middle. This method will further depress the interferences
  /// from the local autocorrelation functions into the spwvd calculation function.The max. frequency
  /// is the nyquist frequency SR/2.
  PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION,
};

//------------------------------------------------------------------------------------------------

 /**
  * This is the public visible wrapper function for the "Discrete Smoothed Pseudo Discrete Wigner-Ville
  * -Transformation" which the user of the function can call. The function takes the samples, the length
  * of the samples and the frequency resolution which should be from a power of two. The frequency 
  * resolution can be a value which is not a power of two, but then the DFT is used instead of the much
  * more faster FFT which has a dramatically influence on the calculation time. Two main methods are 
  * implemented which have different frequency resolution characteristics in relation to the nyquist
  * frequency. The standard normal wigner-ville-distribution has a frequency resolution of SR/4 which
  * means for the calculated image a aliasing effect in the middle of the image because the image is
  * calculated over the frequency range from 0...SR/2. As alternative to the standard real SPWVD the 
  * complex analytic function with can be transformated. The complex analytic function can be calculated
  * via the hilbert transformation the the resulting spwvd will not have the restriction to SR/4 and
  * the aliasing mirror effect in the middle of the screent. The standard real transformation should
  * only be used if it is known that the sample data input vector has a band limitation of SR/4. Otherwise
  * the extended calculation method via the complex hilbert analyti function should be used to get the 
  * best frequency resolution and no antialiasing and mirroring effects in the middle of the image.
  *
  *              /                                                             
  *              | h(tau)                                  -j2pi f tau         
  * PMH(t,f) =   | ------ ( x(t+tau)x*(t) + x(t)x*(t-tau) )e            dtau   
  *              |   2                                                         
  *             /  
  *
  * DEALLOCATION example:
  * for (int i=0; i<timeLength; i++)
  *	  delete[] pmh[i];
  * delete pmh;
  * 
  * @param signal       The pointer to the signal time domain input vector.
  * @param length       The length of the signal time domain input vector.
  * @param resolution   For performance reasons the required resolution shoule be, but must not have
  *                     a power of two. A resolution from a power of two dramatically influences the
  *                     calculation speed.
  * @param method       The standard complex transformation transformates with the help of an hilbert 
  *                     transformator the real input signal into its analytic representation and bypasses
  *                     so the WVD-aliasing frequency from SR/4 which efects the aliasing mirroring 
  *                     on the image in the middle of the image. With the help of the complex implemented
  *                     SPWVD, which will be called normally with the analystic representation the 
  *                     distribution is generated with the aliasing frequency SR/2. The real method 
  *                     can be taken if it's known that the signal is bandlimited with SR/4.
  *                     middle of the image with SR/4. The complex transformation with the analytic function 
  *                     prevents aliasing and has no mirror effect in the middle of the image. 
  * @return             The resulting time-frequency representation is a two dimensional array from 
  *                     the size length*resolution and can be accessed via spevd[time][frequency]. 
  *                     Note that the array will be allocated into this function so no pre allocation 
  *                     is necessary. Note that the deallocation of this structure every single time
  *                     bin and the time bin pointer vector itselv must be deallocated.
  */
  static double** calculatePseudoMargenauHillDistribution
  (
    const double* signal,
    const int length,
    const int resolution,
    const PMHD_CALCULATION_METHOD method = PMHD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION
  );
 
//------------------------------------------------------------------------------------------------

private:

//------------------------------------------------------------------------------------------------

/**
 * This group of structures hold the input and out structures for the transformation
 */
//@{

/**
 * This struct represents the time signal including the metadata for used internally for the transformation.
 */
  typedef struct
  {
    int            length;	      // Length of the signal in points
    double*        real_part;	    // real part of the signal
    double*        imag_part;	    // imaginary part of the signal
    bool           is_complex;    // true for complex signals
  }
  signalRepresentation;
  
 /**
  * This stuct hold the resulting time frequency representation and all the metadata. The resulting 
  * value of the wigner ville transformation is stored into the real part row by row. The struct is 
  * only internally used.
  */
  typedef struct
  {
    int            N_freq;	      // number of freq bins in the TFR matrix
    int            N_time;	      // number of time_bins in the TFR matrix
    double*        time_instants; // instant for each column of the TFR
    double*        pmhd;    	    // the resulting TFR
  }
  timeFrequencyRepresentation;
  
//@}
  
//------------------------------------------------------------------------------------------------

 /**
  * This function implements the algorithmus for the Pseudo Margenau Hill Distribution (PMHD).
  *                                                                           
  *              /        /                                  -j2pi f tau       
  * SPWVD(t,f) = | h(tau) | g(tau-t) x(mu+tau/2)x*(mu-tau/2)e          dmu dtau 
  *             /         /                                                     
  *
  * This function is real valued. Its computation requires a real or complex signal, a vector 
  * containing time instants, the number of frequency bins, a time smoothing window and a frequency
  * smoothing window.
  *
  * @param  Signal               This struct holds the signal input data. This is the signal to analyse.
  * @param  Window               Vector containing the points of the frequency window.
  * @param  Window_length        Number of points of the window (ODD number !).
  * @param  tfr                  Matrix containing the resulting TFR (real).
  * @param  tfr.time_instants    positions of the smoothing window.
  * @param  tfr.N_time           length of '.time_instants' = number of cols. in the tfr matrix.
  * @param  tfr.N_freq           Number of frequency bins = number of rows in the tfr matrix.
  * @return tfr.real_part        The output tfr matrix  (real_part).
  * 
  * The following variables are used intenally into the function.
  * @remarks column, row         variables of displacement in the matrices.
  * @remarks half_Window_Length half-length of the frequency smoothing window.
  * @remarks normF               normalization factor for the frequency window.
  * @remarks tau                 time-lag variable.
  * @remarks taumax              accound the beginning and the end of the signal, where the window is cut.
  * @remarks mu                  time-smoothing variable.
  * @remarks mumin               local time-smoothing variable bounds. Used to take.
  * @remarks mumax               into accound the beginning and the end of time smoothing procedure.
  * @remarks lacf_real           Real and imaginary parts of the local autocorrelation.
  * @remarks lacf_imag           Function of the signal.
  * @remarks R1_real R1_imag     Used to compute real and imaginary parts of the time
  * @remarks R1_real R1_imag     Smoothed-windowed local autocorrelation function.
  * @remarks R2_real R2_imag
  */
  static void algorithmPseudoMargenauHillDistribution
  (
    const signalRepresentation Signal,
    double* Window,
    const int     Window_Length,
    timeFrequencyRepresentation tfr
  );
  
//------------------------------------------------------------------------------------------------
  
 /**
  * This function prevents a division by zero and checks if the quotient is zero. If the diviso is
  * zero the return value will be zero and not nan.
  *
  * @param x     The divisor
  * @param y     The quotient
  * @return      Zero if the quotient was zero, and the result of the division if the quotient was not zero.
  */
  static int irem( double x, double y);
  
//------------------------------------------------------------------------------------------------

}; // class PseudoMargenauHillDistribution
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_PSEUDO_MARGENAU_HILL_DISTRIBUTION
