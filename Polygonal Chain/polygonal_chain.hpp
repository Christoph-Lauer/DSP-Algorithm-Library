/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    polygonal_chain.hpp
 * @class   PolygonalChain
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Class template file
 * @see     http://en.wikipedia.org/wiki/Polygonal_chain
 * @todo    this very easy implementation could for shure be better and faster implemented
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_POLYGONAL_CHAIN
#define CLAUER_MATH_POLYGONAL_CHAIN
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class implements a static function for the calculation of the polygonal chain for the given 
 * input signal. The function is implemented as a static template class. The polygonal chain is the 
 * integrated way of the signal. For a more detailed information the normalized polygonal chain for
 * the signal should be taken. The normalized polygonal chain is the polygonal chain of the signal 
 * divided to the length of the signal.
 */
template <class T>
class PolygonalChain
{

public:

 /**
  * This static function calculates the signal polygonal chain way of the given signal.
  * The polygonal chain way is simple the length of the way of the signal.
  *
  * @param samples    A pointer to the samples array.
  * @param length     The length od the samples array.
  * @return           Returns the zero crossing rate.
  */
  static T polygonalChain(T* samples, int length)
  { 
    // first the initial signum
    T pc = 0;
    // then go throught the loop
    for (int i=1; i<length; i++)
    {
      T diff = samples[i] - samples[i-1];
      if (diff > 0)
        pc += diff;
      if (diff < 0)
        pc -= diff;
    }
    // finally give the result back
    return pc;
  };
 
//------------------------------------------------------------------------------------------------

}; // class polygonalChain
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_POLYGONAL_CHAIN
