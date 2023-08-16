package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    damping_constant.h
// * @class   DampingConstant
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Try to propose the Damping Constant of an given input inpulse signal
// * @see     http://de.wikipedia.org/wiki/Dämpfungskonstante
// * @see     http://en.wikipedia.org/wiki/Regression_analysis
// * @todo    finished and tested so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    damping_constant.h
// * @class   DampingConstant
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Try to propose the Damping Constant of an given input inpulse signal
// * @see     http://de.wikipedia.org/wiki/Dämpfungskonstante
// * @see     http://en.wikipedia.org/wiki/Regression_analysis
// * @todo    finished and tested so far
// 


//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_DAMPING_CONSTANT
//#define CLAUER_MATH_DAMPING_CONSTANT


//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This class collects functions to find a suitable exponential expression for an exponential function
// * which matches best the envelope of a given impulse resonse from a resonance analysis. The class 
// * implements only two functions, the main function which steers everythig, and the exponential 
// * regression function which try to find a suitable exponential function. This function makes heavily 
// * use of the envelope function class-collection whish was defined outside of this class file. 
// * The main function is implemented to find the damping constant autonomously and try to match the 
// * best values. The higher the damping constant the faster signal convergates to the zero line => 
// * Higher Damping. The fucntion try to match the best physical units and the result shound return a
// * value with unit 1/s which can be guarated by the sampleing rate. Another core function of this 
// * class-collection is the exponential regression whis here implemented with a fallback to a linear
// * regression where the solution can found in every statistical book. THe exponential regression 
// * returns the amplitude and damping factor of the function f(t)=A*exp(-bt), where b is thedamping 
// * factor and the amplitude A can be forgotten.
// 
public class DampingConstant
{

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

// *
//  * This function does the following steps to extract the damping constatnt of the given signal. 
//  * 1.) It trys to find the envelope of the given signal with the envelope function.
//  * 2.) Second step is to find the peak value and some significant values to build a suifficient
//  *     point cloud form the following exponential regression. The envelope signal is splitted into
//  *     intervalls (typically 100, which is defined in macro in the implementation file) and the peak 
//  *     of each intervall is taken for a point in the point cloud.
//  * 3.) In the third step this function try to find via an exponential regression the exponetial 
//  *     curve of the given data cloud, which is given back by this function.
//  * This function steers all the other functions like the envelope function of the exponential 
//  * regression function and try to do all the things autonomously to find the correct values for the 
//  * peak and all the other values for the damping constant extraction. The sample rate is needed to 
//  * make the result independent from any A/D wanler values. The output has the unit 1/s which will 
//  * outperform from the sample rate. If the function can not find any suitable result for the calculation
//  * the three result values are set to 1.0 !
//  *
//  * @param    signal           A pointer to the input signal.
//  * @param    length           The length of the given input signal.
//  * @param    sampleRate       The sample rate of the given input signal.
//  * @param    dampingConstant  This function returns the found damping constant in 1/s.
//  * @param    amplitude        Another return result value form the exponential regression apart 
//  *                            the damping constant value is the signal peak amplitude at the beginning
//  *                            of the regression exponential function. This return value can be polled
//  *                            optionally because a default value of 0.0 implemented.
//  * @param    peakPosition     This return value gves the found peak point back. Like all other mathemeatical
//  *                            functions the return value is in SI units so the result will be in secounds.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void proposeDampingConstant(double signal, int length, int sampleRate, RefObject<Double> dampingConstant, RefObject<Double> amplitude, RefObject<Double> peakPosition)
  {
	// extract the envelope of the given signal with the fir version of the envelope extractor
	double[] envelope = GlobalMembersDamping_constant.clauer.math.Envelope.calculateEnvelope(signal, length, GlobalMembersDamping_constant.clauer.math.Envelope.ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
  
	// now find the peak value and position
	double peakValue = -DBL_MAX;
	int peakPos = 0;
	for (int i =0; i<length; i++)
	{
	  if(envelope[i] > peakValue)
	  {
		peakValue = envelope[i];
		peakPos = i;
	  }
	}
	// give the peak point back
	peakPosition.argvalue = (double)(peakPos) / (double)(sampleRate);
  
	// the point cloud
	double[] pointCloudX = new double[DefineConstantsDamping_constant.REGRESSION_POINTS];
	double[] pointCloudY = new double[DefineConstantsDamping_constant.REGRESSION_POINTS];
	int lengthAfterPeak = length - peakPos;
  
	// make sure that there are enough points available
	if (lengthAfterPeak < DefineConstantsDamping_constant.REGRESSION_POINTS)
	{
	  dampingConstant.argvalue = -1.0;
	  amplitude.argvalue = -1.0;
	  peakPosition.argvalue = -1.0;
	  return;
	}
  
	// collect hte peak points
	for (int i =0; i<DefineConstantsDamping_constant.REGRESSION_POINTS; i++)
	{
	  // collect the array interval boundaries
	  int startIndex = peakPos + (i+0)*lengthAfterPeak/DefineConstantsDamping_constant.REGRESSION_POINTS;
	  int endIndex = Math.min(peakPos + (i+1)*lengthAfterPeak/DefineConstantsDamping_constant.REGRESSION_POINTS, length);
	  // look for the peak in the interval
	  pointCloudY[i] = -DBL_MAX;
	  for (int j =startIndex; j<endIndex; j++)
		if (pointCloudY[i] < envelope[j])
		{
		  pointCloudX[i] = (j - peakPos) / (double)(sampleRate);
		  // prevent to small y values
		  if (envelope[j] < DefineConstantsDamping_constant.EPSILON *peakValue)
			pointCloudY[i] = DefineConstantsDamping_constant.EPSILON *peakValue;
		  else
			pointCloudY[i] = envelope[j];
		}
		//std::cout << pointCloudX[i] << " --> " << pointCloudY[i] <<  " " << sampleRate << std::endl;
	}
	// normalize the damping konstant to the physical unit 1/s
	dampingConstant.argvalue *= sampleRate;
  
	// call the exponetial regression
	RefObject<Double> TempRefObject = new RefObject<Double>(pointCloudY);
	exponentialRegression(pointCloudX, TempRefObject, DefineConstantsDamping_constant.REGRESSION_POINTS, amplitude, dampingConstant);
	pointCloudY = TempRefObject.argvalue;
  
	// revove the influence of the sample rate and scale to the physical unit 1/s
  
	// waste the grabage
	pointCloudX = null;
	pointCloudY = null;
  }

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This function calculates the exponential regression for the given point-cloud and try to find the 
// * exponential function which matches best the point cloud. The resulting exponential function can 
// * be described with the formular: f(t) = amplitude * exp ( - dampingConstant * t). The exponential 
// * regression can be break down to a linear regression and that's the way it is implemented into this 
// * function. We first transformate the values from the exponential space into the linear domain, 
// * solve the simple linear regression values for the slope and the intersection and transformate 
// * the result back into the exponential domain. Note that our impelmentation works only for positive
// * numbers. For the envelope calculation this case is not relevant, because the all the numbers from
// * the envelope are positive.
// * 
// * @param  pointsX          A pointer to the given point cloud X-values.
// * @param  pointsY          A pointer to the given point cloud Y-values. Note taht htis function has a 
// *                          side effect which modifyes the y points vector.
// * @param  length           The number of points in the point cloud.
// * @param  amplitude        The resulting amplitude of the exponential function.
// * @param  dampingConstant  The resulting damping constant of the exponential function.
// 

 //------------------------------------------------------------------------------------------------
 
 private static void exponentialRegression(double[] pointsX, double[] pointsY, int length, RefObject<Double> amplitude, RefObject<Double> dampingConstant)
 {
   // intitalize some values
   double a = 0.0; // the slope factor for the y=a+bx linear part
   double b = 0.0; // the intersection for the y=a+bx lienar part
 
   //////////////////////////////////////////////////////////////////////////////////////////////////
   //// first step is to transformate the point cloud to the logarithmic domain with the base e
   for (int i =0; i<length; i++)
	 pointsY[i] = Math.log(pointsY[i]+DBL_MIN);
 
   //////////////////////////////////////////////////////////////////////////////////////////////////
   //// next step is solve the linear regression with the transformated points, start whith the slope calcualtion
 
   // first collect the arithmetical medean values
   double meanX = 0.0;
   double meanY = 0.0;
   for (int i =0; i<length; i++)
   {
	 meanX += pointsX[i];
	 meanY += pointsY[i];
   }
   meanX /= (double)(length);
   meanY /= (double)(length);
 
   // calculate the slope and intersection of the linear regressiom
   double numerator = 0.0;
   double denominator = 0.0;
   for (int i =0; i<length; i++)
   {
	 numerator += (pointsX[i]-meanX)*(pointsY[i]-meanY);
	 denominator += (pointsX[i]-meanX)*(pointsX[i]-meanX);
   }
   numerator /= (double)(length);
   denominator /= (double)(length);
 
   // calculate the slope and the intersection
   b = numerator / denominator;
   a = meanY - b *meanX;
 
   //////////////////////////////////////////////////////////////////////////////////////////////////
   // transformate the linear result back to the eponential coefficients
   // NOTE: y = amplitude *1 exp ( - dampingConstant * x )
   amplitude.argvalue = Math.exp(a);
   dampingConstant.argvalue = -b;
 }

//------------------------------------------------------------------------------------------------

} // class DampingConstant

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_DAMPING_CONSTANT
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Langunage Library headers

// C++ language Library headers

//------------------------------------------------------------------------------------------------

/// the number of signal avg regression points
//#define REGRESSION_POINTS 100
/// the epsilon value for the min signal amplitude for the y value in the point cloud
//#define EPSILON 0.0001

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------



//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2009 Tangible Software Solutions Inc.
//
//	This class is used to simulate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
final class RefObject<T>
{
	T argvalue;
	RefObject(T refarg)
	{
		argvalue = refarg;
	}
}