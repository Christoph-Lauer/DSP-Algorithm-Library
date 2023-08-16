/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    damping_constant.h
 * @class   DampingConstant
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Try to propose the Damping Constant of an given input inpulse signal
 * @see     http://de.wikipedia.org/wiki/Dämpfungskonstante
 * @see     http://en.wikipedia.org/wiki/Regression_analysis
 * @todo    finished and tested so far
 */


//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_DAMPING_CONSTANT
#define CLAUER_MATH_DAMPING_CONSTANT
 
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class collects functions to find a suitable exponential expression for an exponential function
 * which matches best the envelope of a given impulse resonse from a resonance analysis. The class 
 * implements only two functions, the main function which steers everythig, and the exponential 
 * regression function which try to find a suitable exponential function. This function makes heavily 
 * use of the envelope function class-collection whish was defined outside of this class file. 
 * The main function is implemented to find the damping constant autonomously and try to match the 
 * best values. The higher the damping constant the faster signal convergates to the zero line => 
 * Higher Damping. The fucntion try to match the best physical units and the result shound return a
 * value with unit 1/s which can be guarated by the sampleing rate. Another core function of this 
 * class-collection is the exponential regression whis here implemented with a fallback to a linear
 * regression where the solution can found in every statistical book. THe exponential regression 
 * returns the amplitude and damping factor of the function f(t)=A*exp(-bt), where b is thedamping 
 * factor and the amplitude A can be forgotten.
 */
class DampingConstant
{
 
//------------------------------------------------------------------------------------------------

public:
 
//------------------------------------------------------------------------------------------------

 /**
  * This function does the following steps to extract the damping constatnt of the given signal. 
  * 1.) It trys to find the envelope of the given signal with the envelope function.
  * 2.) Second step is to find the peak value and some significant values to build a suifficient
  *     point cloud form the following exponential regression. The envelope signal is splitted into
  *     intervalls (typically 100, which is defined in macro in the implementation file) and the peak 
  *     of each intervall is taken for a point in the point cloud.
  * 3.) In the third step this function try to find via an exponential regression the exponetial 
  *     curve of the given data cloud, which is given back by this function.
  * This function steers all the other functions like the envelope function of the exponential 
  * regression function and try to do all the things autonomously to find the correct values for the 
  * peak and all the other values for the damping constant extraction. The sample rate is needed to 
  * make the result independent from any A/D wanler values. The output has the unit 1/s which will 
  * outperform from the sample rate. If the function can not find any suitable result for the calculation
  * the three result values are set to 1.0 !
  *
  * @param    signal           A pointer to the input signal.
  * @param    length           The length of the given input signal.
  * @param    sampleRate       The sample rate of the given input signal.
  * @param    dampingConstant  This function returns the found damping constant in 1/s.
  * @param    amplitude        Another return result value form the exponential regression apart 
  *                            the damping constant value is the signal peak amplitude at the beginning
  *                            of the regression exponential function. This return value can be polled
  *                            optionally because a default value of 0.0 implemented.
  * @param    peakPosition     This return value gves the found peak point back. Like all other mathemeatical
  *                            functions the return value is in SI units so the result will be in secounds.
  */
  static void proposeDampingConstant(const double* signal, const int length, const int sampleRate, double& dampingConstant, double& amplitude, double& peakPoint);
 
//------------------------------------------------------------------------------------------------

private:
 
//------------------------------------------------------------------------------------------------

/**
 * This function calculates the exponential regression for the given point-cloud and try to find the 
 * exponential function which matches best the point cloud. The resulting exponential function can 
 * be described with the formular: f(t) = amplitude * exp ( - dampingConstant * t). The exponential 
 * regression can be break down to a linear regression and that's the way it is implemented into this 
 * function. We first transformate the values from the exponential space into the linear domain, 
 * solve the simple linear regression values for the slope and the intersection and transformate 
 * the result back into the exponential domain. Note that our impelmentation works only for positive
 * numbers. For the envelope calculation this case is not relevant, because the all the numbers from
 * the envelope are positive.
 * 
 * @param  pointsX          A pointer to the given point cloud X-values.
 * @param  pointsY          A pointer to the given point cloud Y-values. Note taht htis function has a 
 *                          side effect which modifyes the y points vector.
 * @param  length           The number of points in the point cloud.
 * @param  amplitude        The resulting amplitude of the exponential function.
 * @param  dampingConstant  The resulting damping constant of the exponential function.
 */
 static void exponentialRegression(const double* pointsX, double* pointsY, const int length, double& amplitude, double& dampingConstant);
 
//------------------------------------------------------------------------------------------------

}; // class DampingConstant
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_DAMPING_CONSTANT
