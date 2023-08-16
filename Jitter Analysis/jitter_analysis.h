/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    jitter_analysis.h
 * @class   JitterAnalysis
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Class template file
 * @see     http://en.wikipedia.org/...
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_JITTER_ANALYSIS
#define CLAUER_MATH_JITTER_ANALYSIS
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * The definition of the Jitter in this case is not like the Jitter definition in digital audio.
 * We understood Jitter as a micro oscillation of the frequency peak in a band which can be used
 * to detect variation in some transmision gears or other static signals with fluctations in the long
 * time anysis.
 */
class JitterAnalysis
{

public:

 /**
  * This static function extracts the frequency peak Jitter for a given frequency band from a time
  * domain based signal. The result is the frequency peak Jitter in Hz. This implementation here uses
  * no kind of whichcraft ;-) it an simple ane easy implementation which uses the Linear Predictive Coding
  * to calculate the spectrum. By default this function returns the Jitter in Hz from window to window.
  * Insted to see the difference it is possible to track the peak value.
  *
  * @param    samples         This is the input data vector.
  * @param    nSamples        The length of the input data vector.
  * @param    lpcPrevious     This is the number of samples used to generate the LPC coefficients.
  * @param    lpcSpecSize     This is the number of future samples calculated from the LPC prediction and 
  *                           coevally the spectrum size.
  *                           NOTE that this values influences the resolution of the jitter analysis.
  *                           NOTE this number must be a power of two because it is the input for 
  *                           final fast fourier transformation.
  * @param    lpcWinShift     The step size of the analysis in samples.
  *                           NOTE that this value influences the number of anaysis points.
  * @param    lpcCoeffs       The number of LPC coefficients.
  * @param    sampleRate      The sample rate of the input signal.
  * @param    lowerBandBorder The lower frequency band limit in Hz.
  * @param    upperBandBorder The upper frequency band limit in Hz.
  * @param    nPoints         The number of points generated while the analysis.
  * @param    peak            Normaly the algorithm gives back the peak frequency difference from window to 
  *                           window. If this flag is the algorithm gives back the raw peak value.
  *                           NOTE: This flag is more/less useless with the following weightResult flag.
  * @param    weightResult    This flag weights the difference from the previous peak frequency point with 
  *                           amplitude of the current peak point which results a more noise independent analysis.
  * @return                   This function returns the frequency jitter in Hz.
  *                           NOTE that the memory for the analysis output vector will be allocated
  *                           by this function.
  */
  static double* jitterAnalysis(const double* samples, const int nSamples, const int lpcPrevoius, const int lpcSpecSize, const int lpcWinShift,
                         const int lpcCoeffs, const int sampleRate, const int lowerBandBorder, const int upperBandBorder, int* nPoints,
                         bool peakReturn = false, bool weightResultWithAmplitude = false);

private:
 
//------------------------------------------------------------------------------------------------

}; // class JitterAnalysis
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_JITTER_ANALYSIS
