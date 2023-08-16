/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    choi-williams-distribution.h
 * @class   ChoiWilliamsDistribution
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This class implements a choi-williams-distribution (CWD)
 * @see     http://en.wikipedia.org/wiki/Choi-Williams_distribution_function
 * @see     http://en.wikipedia.org/wiki/Cohen's_class_distribution_function
 * @todo    finished and tested so far.
 */
  
//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_CHOI_WILLIAMS_DISTRIBUTION
#define CLAUER_MATH_CHOI_WILLIAMS_DISTRIBUTION

//------------------------------------------------------------------------------------------------

#define ISODD(x)        ((x/2.0)== ((int)(x/2)) ? 0 : 1)

//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This function implements the algorithm for the calculation of Choi Williams Distribution. The
 * Choi-Williams distribution function is one of the members of Cohen's class distribution function. 
 * It was first proposed by Hyung-Ill Choi and William J. Williams in 1989. This distribution function
 * adopts exponential kernel to suppress the cross-term. However, the kernel gain dose not decrease 
 * along the ?,t axes in the ambiguity domain. Consequently, the kernel function of Choi-Williams 
 * distribution function can only filter out the cross-terms result form the components differ in 
 * both time and frequency center. 
 *
 *                 //
 *                 ||     sqrt(sigma)     -mu^2 *sigma / (16 tau^2)
 *      CW(t,f) =  || -----------------  e                           .../...
 *                 ||  2 sqrt(pi)|tau |
 *                //
 *
 *                                                 -j2pi f tau
 *                     x(t+mu+tau/2)x*(t+mu-tau/2)e            dmu dtau
 *
 */
class ChoiWilliamsDistribution
{
 
//------------------------------------------------------------------------------------------------

public:
//------------------------------------------------------------------------------------------------
 
/** 
 * To prevent aliasing the real input signal must be band limited with the nyquist frequency SR/2. 
 * The discrete choi williams distribution has a band limitation of SR/4, so normally for complex 
 * signals the input time domain vector must be up samples to the double length. In our case we have a
 * real input signal, so we use the complex transformation with the analytic signal as input which 
 * prevents aliasing at the frequency SR/4 and the mirroring of the image. For this case we can switch
 * between the transformation with the analytic signal ans the real input signal. The upsampling method
 * only required for complex input signals which is not part of out implementation.
 */
enum CWD_CALCULATION_METHOD
{
  /// The standard real transformation has a bandlimit of SR/4. The image will be mirrored in the middle.
  /// with the aliasing frequency SR/4.
  CWD_CALCULATION_METHOD_REAL,
  /// With help of the hilbert tranformation the complex analytic signal can be transformated and the
  /// resulting image no mirroring in the middle. This method will further depress the interferences
  /// from the local autocorrelation functions into the spwvd calculation function.The max. frequency
  /// is the nyquist frequency SR/2.
  CWD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION,
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
  *                 //
  *                 ||     sqrt(sigma)     -mu^2 *sigma / (16 tau^2)
  *      CW(t,f) =  || -----------------  e                           .../...
  *                 ||  2 sqrt(pi)|tau |
  *                //
  *
  *                                                 -j2pi f tau
  *                     x(t+mu+tau/2)x*(t+mu-tau/2)e            dmu dtau
  *
  * DEALLOCATION example:
  * for (int i=0; i<timeLength; i++)
  *	  delete[] cwd[i];
  * delete cvd;
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
  static double** calculateChoiWilliamsDistribution
  (
    const double* signal,
    const int length,
    const int resolution,    
    const CWD_CALCULATION_METHOD method = CWD_CALCULATION_METHOD_COMPLEX_ANALYTIC_FUNCTION
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
    double*        cwd;    	    // the resulting TFR
  }
  timeFrequencyRepresentation;
  
//@}
  
//------------------------------------------------------------------------------------------------

 /**
  * This function implements the algorithmus for the Choi-Williams Distribution (CWD).
  *                                                                           
  *              /        /                                  -j2pi f tau       
  * SPWVD(t,f) = | h(tau) | g(tau-t) x(mu+tau/2)x*(mu-tau/2)e          dmu dtau 
  *             /         /                                                     
  *
  * This function is real valued. Its computation requires a real or complex signal, a vector 
  * containing time instants, the number of frequency bins, a time smoothing window and a frequency
  * smoothing window and the kernel width sigma.
  *
  * @param  Signal               This struct holds the signal input data. This is the signal to analyse.
  * @param  WindowT              Vector containing the points of the time moothing window.
  * @param  windowT_Length       Number of points of the time window (ODD number !).
  * @param  WindowF              Vector containing the points of the frequency window.
  * @param  WindowF_length       Number of points of the window (ODD number !).
  * @param  tfr                  Matrix containing the resulting TFR (real).
  * @param  tfr.time_instants    positions of the smoothing window.
  * @param  tfr.N_time           length of '.time_instants' = number of cols. in the tfr matrix.
  * @param  tfr.N_freq           Number of frequency bins = number of rows in the tfr matrix.
  * @param  tfr.is_complex       True is the input signal is complex with the analytic representation.
  * @param  sigma                The kernel width.
  * @return tfr.wvd              The output tfr matrix.
  * 
  * The following variables are used intenally into the function.
  * @remarks column, row         variables of displacement in the matrices.
  * @remarks time                local time-instant variable to compute the tfr.
  * @remarks half_WindowT_Length half-length of the time smoothing window.
  * @remarks normT               normalization factor for the time smoothing window.
  * @remarks half_WindowF_Length half-length of the frequency smoothing window.
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
  static void algorithmChoiWilliamsDistribution
  (
    const signalRepresentation Signal,
    const double* WindowT,
    const int     WindowT_Length,
    double*       WindowF,
    const int     WindowF_Length,
    const double  sigma,
    timeFrequencyRepresentation tfr
  );

//------------------------------------------------------------------------------------------------

 /**
  * The matrices are stored as vectors, column by columm. This function computes the vector index 
  * corresponding to the specified line number (line), the column number (col) and the number of lines
  * (nb_line)
  * @param	  i_row		The line number.
  * @param	  j_row   The column number.
  * @param	  nb_row  The number of lines.
  * @return           Returns the vector index.
  */
  static int idx(int i_row, int j_col, int nb_row);

//------------------------------------------------------------------------------------------------

}; // class ChoiWilliamsDistribution
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_CHOI_WILLIAMS_DISTRIBUTION
