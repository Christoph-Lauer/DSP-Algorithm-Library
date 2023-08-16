/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    hatrminic_analyse.h
 * @class   Harmonic Analyse
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Collects functions for the harmonic analyse
 * @see     http://en.wikipedia.org/wiki/Harmonic
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_HARMONIC_ANALYSE
#define CLAUER_MATH_HARMONIC_ANALYSE
 
//------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class implements functions for the harmonic analyse. Like all other mathematical classes this
 * class collects static functions. THe harmonic analyse function trys to find a fundamental frequency 
 * in a given search intervall and extracts the levels of the harmonic followers. The resulting return
 * values are all in decibell (dB) level dimensions appart from the frequency return values.
 */
class HarmonicAnalyse
{

public:

/** 
 * This enummeration defines the harmonics search method. Two methods are implemented.
 * The first (EXACT_POSITION) method looks for the peak at the exact multipler possitions of the ground frequency.
 * The second method (SEARCH_AROUND_RADIUS) searches the harmonics based on the fundamental frequency arround an intervall
 * border. The border is typically determined by an percent value of the first fundamental frequency.
 * In this case the search border will be determined by percentage value of the fundamental frequency 
 * which is the applyed to search for the harmonics with a multipler of the harmoc frequencies. 
 * Example given: If the fundamental was found at 100Hz and the search border was predefined at 1%,
 * for the second order harmonics the search intervall was +/-1 percent, from 199...200Hz. For the
 * third oder harmonic the intervall was at +/-3Hz, from 297...306 and so on. 
  */
 enum extraction_method{EXACT_POSITION, SEARCH_AROUND_RADIUS};
 
 /**
  * This function performs an harmonic analyse. Given the Signal, the length, the sample rate and 
  * the search intervall for the first harmonic this function try to find the peak in the search
  * intervall and looks for the level of the other harmonics. The return value is a vector with the 
  * level of the harmonics beginning from the first harmonic found in the search intervall to the 
  * tenth harmonics. If the resulting N-th order harmonic frequency is outside the nyquist frequency
  * the level of the harmonic given back in the return value is set to zero.
  * 
  * @param  signal                    The time domain Signal.
  * @param  length                    The length of the time domain signal.
  * @param  sampleRate                The sample rate of the time domain signal.
  * @param  lowerSearchFrequency      The lower search frequency.
  * @param  UpperSearchFrequency      The Upper search frequency.
  * @return fundamentalFrequency      This is the fundamental base frequency found in the search intervall.
  * @return fundamentalFrequencyLevel This value is the level of the found fundamental frequency. This return 
  *                                   is in decibel.
  * @param  searchMethod              This value determines the search method for the harmonic extraction.
  *                                   The default valeu for the search method is the normal exact position method.
  *                                   For the secod method the the search radius in percent can be specifed.
  * @param  searchRadius              This value defines the search radius for the harmonics extraction for the 
  *                                   second method. The second method searches the ahrmoncis arround an predefined
  *                                   intervall which is a multiple of the percentage of the fundamental freqeuncy found.
  *                                   Look in the specification of the methods enummeration for a more detailed description.
  *                                   Note that this value must be smaler than approx. 5 percent. The defautl value is 0.5 %
  * @return                           This function Returns a vector with the first ten harmonics beginning
  *                                   from th one found in the search intervall to the tenth. Note that the 
  *                                   return vector will be allocated into this function so the caller hat to 
  *                                   be care for the deallocation of the arry.
  *                                   THE UNIT OF THE RESULT ARRAY IS DECIBELL (dB) !!!
  */
static double* harmonicAnalyse(double* signal, int length, int sampleRate, double lowerSearchFrequency, double upperSearchFrequency, double* fundamenalFrequency, double* fundamentalFrequencyLevel, int extraction_method = EXACT_POSITION, double searchRadius = .5);

private:
 
//------------------------------------------------------------------------------------------------

}; // class HarmonicAlanylse
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_HARMONIC_ANALYSE
