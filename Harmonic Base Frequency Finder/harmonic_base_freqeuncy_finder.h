/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    hatrminic_analyse.h
 * @class   Harmonic Analyse
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Collects functions for the harmonic analyse
 * @see     http://en.wikipedia.org/wiki/Fundamental_frequency
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_HARMONIC_FUNDAMENTAL_FREQUENCY_FINDER
#define CLAUER_MATH_HARMONIC_FUNDAMENTAL_FREQUENCY_FINDER
 
//------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 *
 */
class HarmonicFundamentalFreqeuncy
{

public:
 
 /**
  * 
  */
static double harmonicFundamentalFrequency(double* signal, int length, int sampleRate);

 /**
  * 
  */
static double harmonicFundamentalFrequencyArray(double* signal, int length, int sampleRate);
 
//------------------------------------------------------------------------------------------------

}; // class HarmonicFundamentalFreqeuncy
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_HARMONIC_FUNDAMENTAL_FREQUENCY_FINDER
