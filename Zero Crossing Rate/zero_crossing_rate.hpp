/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    zero_crossing_rate.h
 * @class   ZeroCrossingRate
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Class template file
 * @see     http://en.wikipedia.org/wiki/Zero_crossing
 * @todo    this very easy template besed implementation could for shure be better and faster implemented
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_ZERO_CROSSING_RATE
#define CLAUER_MATH_ZERO_CROSSING_RATE
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class implements a staic function for the calculation of the zero crossing rate.
 */
template <class T>
class ZeroCrossingRate
{

public:

 /**
  * This static function calculates the zero crossing rate of the given array.
  * This is an quick and easy implementaion whichcould be make faster for shure.
  *
  * @param samples    A pointer to the samples array.
  * @param length     The length od the samples array.
  * @return           Returns the zero crossing rate.
  */
  static int calcZeroCrossingRate(T* samples, int length)
  { 
    // first the initial signum
    int zc = 0;
    bool sign;
    bool psign;
    if (samples[0] < 0)   psign = false;
    else                  psign = true;
    
    // then go throught the loop
    for (int i=1; i<length; i++)
    {
      if (samples[i] < 0) sign = false;
      else                sign = true;
      if (psign != sign)
        zc ++;
      psign = sign;
    }
    
    // finally give the result back
    return zc;
  };
 
//------------------------------------------------------------------------------------------------

}; // class ZeroCrossingRate
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_ZERO_CROSSING_RATE
