package clauer.math;

public class GlobalMembersDigital_filter
{


	// C Langunage Library headers

	// C++ Langunage Library headers

	//------------------------------------------------------------------------------------------------


	  public static final double[][][] clauer.math.DigitalFilter.biquadLowPassFilters = { { {1.2872, 0.0, 0.0, 0.0 }, {0.8700, 0.8700, 0.0, 0.0 }, {0.6999, 0.6999, 0.6999, 0.0 }, {0.6017, 0.6017, 0.6017, 0.6017} }, { {1.3617, 0.0, 0.0, 0.0 }, {1.3397, 0.7743, 0.0, 0.0 }, {1.2217, 0.9686, 0.5131, 0.0 }, {1.1112, 0.9754, 0.7202, 0.3728} }, { {1.4142, 0.0f, 0.0, 0.0 }, {1.8478, 0.7654f, 0.0, 0.0 }, {1.9319, 1.4142f, 0.5176, 0.0 }, {1.9616, 1.6629f, 1.1111, 0.3902} }, { {1.3022, 0.0, 0.0, 0.0 }, {2.5904, 0.3039, 0.0, 0.0 }, {3.8437, 0.6292, 0.1296, 0.0 }, {5.1019, 0.8916, 0.2806, 0.0717} }};

	  public static final double[][][] clauer.math.DigitalFilter.biquadHighPassFilters = { { {0.4142, 0.0, 0.0, 0.0 }, {0.1892, 0.1892, 0.0, 0.0 }, {0.1225, 0.1225, 0.1225, 0.0 }, {0.0905, 0.0905, 0.0905, 0.0905 } }, { {0.6180, 0.0, 0.0, 0.0 }, {0.4889, 0.3890, 0.0, 0.0 }, {0.3887, 0.3505, 0.2756, 0.0 }, {0.3162, 0.2979, 0.2621, 0.2087 } }, { {1.0, 0.0, 0.0, 0.0 }, {1.0, 1.0, 0.0, 0.0 }, {1.0, 1.0, 1.0, 0.0 }, {1.0, 1.0, 1.0, 1.0 } }, { {1.5515, 0.0, 0.0, 0.0 }, {4.1301, 1.1697, 0.0, 0.0 }, {8.5529, 1.9124, 1.0766, 0.0 }, {14.7608, 3.0426, 1.4334, 1.0432} } };

	//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER WARNING: The declaration of the following method implementation was not found:
//ORIGINAL LINE: void clauer::math::DigitalFilter::bandPassFilter(double* inputSamples, int length, int sampleRate, const clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, const int filterOrder)

	//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER WARNING: The declaration of the following method implementation was not found:
//ORIGINAL LINE: void clauer::math::DigitalFilter::bandStopFilter(double* inputSamples, int length, int sampleRate, const clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, const int filterOrder)
}
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    digital_filter.h
// * @class   DigitalFilter
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implemts digital filters (filter with critical damping, Bessel, Butterworth, Tschebyscheff)
// * @see     http://en.wikipedia.org/wiki/Digital_filter
// * @see     http://en.wikipedia.org/wiki/Digital_biquad_filter
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    digital_filter.h
// * @class   DigitalFilter
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   This class implemts digital filters (filter with critical damping, Bessel, Butterworth, Tschebyscheff)
// * @see     http://en.wikipedia.org/wiki/Digital_filter
// * @see     http://en.wikipedia.org/wiki/Digital_biquad_filter
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// C Languange Library headers

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_DIGITAL_FILTER
//#define CLAUER_MATH_DIGITAL_FILTER

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This class collects a set of usefull digital filter functions which are available for the four base
// * types of digital filters (CriticalDamping, Bessel, Butterwirth and Tschebyscheff) implemnted with a low
// * pass, a high pass, a band-pass and a bandstop filter. The filter coefficients defined into this
// * class are predefined for the IIR filter. Higher filter orders are realized with multiple biquad filters 
// * in concatenation. A digital biquad filter is a second-order recursive linear filter, containing two
// * poles and two zeros. "Biquad" is an abbreviation of "biquadratic", which refers to the fact that 
// * in the Z domain, its transfer function is the ratio of two quadratic functions. 
// 

//------------------------------------------------------------------------------------------------

public class DigitalFilter
{

//------------------------------------------------------------------------------------------------


// * 
//  * This enumeration specifyes the type of the used filter coefficients. As desribed above four
//  * digital filters are implenmeted, the Critical-Damping filter, the Bessel the Butterwoth and
//  * Tschebyscheff filters.
//  
  public enum DIGITAL_FILTER_TYPE
  {
	DIGITAL_FILTER_TYPE_CRITICAL_DAMPING(0),
	DIGITAL_FILTER_TYPE_BESSEL,
	DIGITAL_FILTER_TYPE_BUTTERWORTH,
	DIGITAL_FILTER_TYPE_TSCHEBYSCHEFF,
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function defines the base functions for the other two functions, the band-pass and the 
//  * band-stop filters. The low and high pass filters are defiend here in one fucntion because most 
//  * of the code is identical for both. The implementation uses a standart IIR routine where the 
//  * specification can be found in any literature or in the web. It can be switched between the low 
//  * and the high pass filter with an simple flag. As follows the paramter description:
//  *                              
//  * @param inputSamples          The time domain input sample vector.
//  * @param length                The length of the time domain input sample vector.
//  * @param sampleRate            The sample rate of the time domain input sample vector.
//  * @param type                  One of the four enumerations for the filter type.
//  * @param cutOffFrequency       The cut off frequency for the low or the high pass.
//  * @param filterOrder           The filter order must be an even value in the interval [2,4,6,8]
//  * @param highPass              An LowPass is applayed to the inputSample values per default, if this
//  *                              optional flag is set to true an high pass filter is applyed.
//  * @return                      This implementation works inplace.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void clauer.applyFilter(double[] inputSamples, int length, int sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE type, double cutOffFrequency, int filterOrder)
  {
	  applyFilter(inputSamples, length, sampleRate, type, cutOffFrequency, filterOrder, false);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void clauer::applyFilter(double* inputSamples, const int length, const int sampleRate, const clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE type, const double cutOffFrequency, const int filterOrder, boolean highPass = false)
  public static void clauer.applyFilter(double[] inputSamples, int length, int sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE type, double cutOffFrequency, int filterOrder, boolean highPass)
  {
	// assume that the correct values are given
	  assert length > 3;
	  assert filterOrder ==2 || filterOrder ==4 || filterOrder ==6 || filterOrder ==8;
	  assert cutOffFrequency > 1.0E-10;
	  assert cutOffFrequency < 1.0E10;
	  assert 2.0 *cutOffFrequency < sampleRate;
	  assert Math.cos(DefineConstantsDigital_filter.PI *cutOffFrequency/sampleRate) > 1.0E-10;
	  assert Math.sin(DefineConstantsDigital_filter.PI *cutOffFrequency/sampleRate) / Math.cos (DefineConstantsDigital_filter.PI *cutOffFrequency/sampleRate) > 1E-10;
  
	// declare and initialize some local variables
	  double wa;
	  double ai;
	  double bi;
	  double c;
	  double d;
	  double e;
	double[][] lastInputValues = new double[8][3];
	double[][] lastOutputValues = new double[8][3];
	for (int k =0; k<8; k++)
	  for (int l =0; l<3; l++)
	  {
		lastInputValues [k][l] = 0.0;
		lastOutputValues[k][l] = 0.0;
	  }
	wa = Math.sin(DefineConstantsDigital_filter.PI *cutOffFrequency/sampleRate) / Math.cos (DefineConstantsDigital_filter.PI *cutOffFrequency/sampleRate);
	int iFilOrd = (filterOrder / 2) - 1; // for indexing reasons
  
	// for all two orders
	for (int j =0; j<=iFilOrd; j++)
	{
	  // init some values
	  if (highPass == false)
	  {
		ai = biquadLowPassFilters[type][iFilOrd][j] / wa;
		bi = biquadHighPassFilters[type][iFilOrd][j] / (wa *wa);
	  }
	  else
	  {
		ai = biquadLowPassFilters[type][iFilOrd][j] * wa;
		bi = biquadHighPassFilters[type][iFilOrd][j] * (wa *wa);
	  }
	  c = 1.0 / (1.0 + ai + bi);
	  if(highPass == false)
		d = 2.0 * (1.0 - bi) * c;
	  else
		d = 2.0 * (bi - 1.0) * c;
	  e = (1.0 - ai + bi) * c;
  
	  // this special flag here initializes the filter coefficients
  
	  // write the old input values
	  lastInputValues[j][2] = lastInputValues[j][1];
	  lastInputValues[j][1] = lastInputValues[j][0];
	  lastInputValues[j][0] = inputSamples[0];
	  // shift the old output values
	  lastOutputValues[j][2] = lastOutputValues[j][1];
	  lastOutputValues[j][1] = lastOutputValues[j][0];
	  if (highPass == false)
		inputSamples[0] = c * (lastInputValues[j][0] + 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e * lastOutputValues[j][2];
	  else
		inputSamples[0] = c * (lastInputValues[j][0] - 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e * lastOutputValues[j][2];
	  lastOutputValues[j][0] = inputSamples[0];
	  // shift the old input values
	  lastInputValues[j][2] = lastInputValues[j][1];
	  lastInputValues[j][1] = lastInputValues[j][0];
	  lastInputValues[j][0] = inputSamples[1];
	  // shift the old output values
	  lastOutputValues[j][2] = lastOutputValues[j][1];
	  lastOutputValues[j][1] = lastOutputValues[j][0];
	  if (highPass == false)
		inputSamples[1] = c * (lastInputValues[j][0] + 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e * lastOutputValues[j][2];
	  else
		inputSamples[1] = c * (lastInputValues[j][0] - 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e * lastOutputValues[j][2];
	  // apply here the core filter
	  for (int i =2; i<length; i++)
	  {
		// write the input values
		lastInputValues[j][2] = lastInputValues[j][1];
		lastInputValues[j][1] = lastInputValues[j][0];
		lastInputValues[j][0] = inputSamples[i];
		// shifting of the input sampels and differ here between the low and high pass filter
		if (highPass == false)
		  // the low pass
		  inputSamples[i] = c * (lastInputValues[j][0] + 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * inputSamples[i-1] - e * inputSamples[i-2];
		else
		  // the high pass
		  inputSamples[i] = c * (lastInputValues[j][0] - 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * inputSamples[i-1] - e * inputSamples[i-2];
	  }
	}
  }


//------------------------------------------------------------------------------------------------

// *
//  * This second pair of functions uses the base low and high pass filters for the implemntation of the 
//  * band-pass and band-stop filters. Both functions hav the same fucntion signatures.
//  *
//  * @param inputSamples          The time domain input sample vector.
//  * @param length                The length of the time domain input sample vector.
//  * @param sampleRate            The sample rate of the time domain input sample vector.
//  * @param type                  One of the four enumerations for the filter type.
//  * @param cutOffFrequencyLow    The cut off frequency for the lower low pass side.
//  * @param cutOffFrequencyHigh   The cut off frequency for the upper high pass side.
//  * @param filterOrder           The filter order must be an even value 2,4,6,8...
//  * @return                      The time domain output filteres signal.
//  
//@{
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//  static void bandPassFilter(RefObject<double> inputSamples, int length, int sampleRate, DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, int filterOrder);

//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//  static void bandStopFilter(RefObject<double> inputSamples, int length, int sampleRate, DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, int filterOrder);
//@}

//------------------------------------------------------------------------------------------------

//*
// * This pair of IIR filter coeffciients defines the IIR filter structures for the Critical-Damping,
// * the Bessel, the Butterworth and the Tschebyscheff filters. This matrices are the heart of the IIR
// * digital filter.
// 
//@{
  private static final double[][][] biquadLowPassFilters = new double[5][4][4];
  private static final double[][][] biquadHighPassFilters = new double[5][4][4];
//}@

//------------------------------------------------------------------------------------------------

} // class DigitalFilter

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_DIGITAL_FILTER
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math

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

public class clauer.math.DigitalFilter
{
	public void bandPassFilter(RefObject<Double> inputSamples, int length, int sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, int filterOrder)
	{
	  // make sure that
	  assert cutOffFrequencyHigh > cutOffFrequencyLow;

	  // call first the low pass
	  applyFilter(inputSamples.argvalue, length, sampleRate, type, cutOffFrequencyHigh, filterOrder, false);
	  // then the high pass filter
	  applyFilter(inputSamples.argvalue, length, sampleRate, type, cutOffFrequencyLow, filterOrder, true);
	}
	public void bandStopFilter(double[] inputSamples, int length, int sampleRate, clauer.math.DigitalFilter.DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, int filterOrder)
	{
	  // make sure that
	  assert cutOffFrequencyHigh > cutOffFrequencyLow;

	  // first allocate the needed vectors
	  double[] low = new double[length];
	  double[] hight = new double[length];

	  // init the new vectors with the signal
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  std.memcpy(low, inputSamples, length *sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  std.memcpy(hight, inputSamples, length *sizeof(double));

	  // call first the low pass
	  applyFilter(low, length, sampleRate, type, cutOffFrequencyLow, filterOrder, false);
	  // then the high pass filter
	  applyFilter(hight, length, sampleRate, type, cutOffFrequencyHigh, filterOrder, true);

	  // copy the results together
	  for (int i =0; i<length; i++)
		inputSamples[i] = low[i] + hight[i];

	  // wast the grabage
	  low = null;
	  hight = null;
	}
}