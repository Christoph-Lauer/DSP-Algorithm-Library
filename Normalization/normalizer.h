/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    normalizer.h
 * @class   Normalizer
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Class template file
 * @see     http://en.wikipedia.org/wiki/Audio_normalization
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_NORMALIZER
#define CLAUER_MATH_NORMALIZER
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This is only a class template
 */
class Normalizer
{

public:

//------------------------------------------------------------------------------------------------

 /**
  * This static function normalizes all amplitudes of the given vector to the 
  * peak given as funtion parameter. The values are trimmed in a way that the
  * no value reaches the highest point an all other values are scalled corresponding 
  * the peak value.
  *
  * @param  samples   A pointer to the sample vector.
  * @param  length    The length of the sample vector.
  * @param  peakValue The normalization value.
  */
  static void normalizePeak(double* samples, int length, double peakValue);
  
//------------------------------------------------------------------------------------------------

 /**
  * This static function normalizes all amplitudes of the given vector to the 
  * values given as funtion parameter. The min and the max peak are scaled in 
  * a way that the the reaches the given interval borders.
  *
  * @param  samples   A pointer to the sample vector.
  * @param  length    The length of the sample vector.
  * @param  minValue  The bottom border normalization value.
  * @param  maxValue  The upper border normalization value.
  */
  static void normalizeInterval(double* samples, int length, double minValue, double maxValue);
  
//------------------------------------------------------------------------------------------------

/**
  * This static function normalizes all amplitudes of the given vector to the 
  * avg given as funtion parameter. 
  *
  * @param  samples   A pointer to the sample vector.
  * @param  length    The length of the sample vector.
  * @param  avgValue  The normalization value.
  */
  static void normalizeAvg(double* samples, int length, double avgValue = 1.0);
  
//------------------------------------------------------------------------------------------------

 /**
  * This static function normalizes all amplitudes of the given vector to the 
  * rms given as funtion parameter.
  *
  * @param  samples   A pointer to the sample vector.
  * @param  length    The length of the sample vector.
  * @param  rmsValue  The normalization value.
  */
  static void normalizeRms(double* samples, int length, double rmsValue = 1.0);

//------------------------------------------------------------------------------------------------
  
 /**
  * This function multiplicates the given vector with the given factor
  *
  * @param    samples   A pointer to the sampel vector
  * @param    length    The length of the samples vector
  * @return   factor    The multiplication factor.
  */
  static void multiplicateVectorByValue(double* samples, int length, double factor);
 
//------------------------------------------------------------------------------------------------
  
 /**
  * This function add the given value to the given vecor
  *
  * @param    samples   A pointer to the sampel vector
  * @param    length    The length of the samples vector
  * @return   summand   The summand.
  */
  static void addValueToVector(double* samples, int length, double summand);
 
//------------------------------------------------------------------------------------------------

}; // class Normalizer
 
//------------------------------------------------------------------------------------------------
 
} // namespace template

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_NORMALIZER
