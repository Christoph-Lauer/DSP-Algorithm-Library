package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    normalizer.cpp
// * @class   Normalizer
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Class template file
// * @see     http://en.wikipedia.org/wiki/Audio_normalization
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    normalizer.h
// * @class   Normalizer
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Class template file
// * @see     http://en.wikipedia.org/wiki/Audio_normalization
// * @todo    finished so far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_NORMALIZER
//#define CLAUER_MATH_NORMALIZER

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This is only a class template
// 
public class Normalizer
{


//------------------------------------------------------------------------------------------------

// *
//  * This static function normalizes all amplitudes of the given vector to the 
//  * peak given as funtion parameter. The values are trimmed in a way that the
//  * no value reaches the highest point an all other values are scalled corresponding 
//  * the peak value.
//  *
//  * @param  samples   A pointer to the sample vector.
//  * @param  length    The length of the sample vector.
//  * @param  peakValue The normalization value.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void normalizePeak(double[] samples, int length, double peakValue)
  {
	// first search the peak
	double peak = 0.0;
	for (int i =0; i<length; i++)
	{
	  if (peak < Math.abs(samples[i]))
		peak = Math.abs(samples[i]);
	}
	// for debugging purposes
	System.out.print("Prevoius Peak: ");
	System.out.print(peak);
	System.out.print(" new Peak: ");
	System.out.print(peakValue);
	System.out.print("\n");
	// then multiplicate the vector with the new value
	RefObject<Double> TempRefObject = new RefObject<Double>(samples);
	multiplicateVectorByValue(TempRefObject, length, peakValue/peak);
	samples = TempRefObject.argvalue;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This static function normalizes all amplitudes of the given vector to the 
//  * values given as funtion parameter. The min and the max peak are scaled in 
//  * a way that the the reaches the given interval borders.
//  *
//  * @param  samples   A pointer to the sample vector.
//  * @param  length    The length of the sample vector.
//  * @param  minValue  The bottom border normalization value.
//  * @param  maxValue  The upper border normalization value.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void normalizeInterval(double[] samples, int length, double minValue, double maxValue)
  {
	// make sure that
	assert maxValue > minValue;
	assert samples != null;
	// first search the min and max values
	double min = DBL_MAX;
	double max = -DBL_MAX;
	for (int i =0; i<length; i++)
	{
	  if (min > samples[i])
		  min = samples[i];
	  if (max < samples[i])
		  max = samples[i];
	}
	double oldDelta = max - min;
	double newDelta = maxValue - minValue;
	// only for debugging purposes
	System.out.print("Prevoius [min...max] = [");
	System.out.print(min);
	System.out.print("...");
	System.out.print(max);
	System.out.print("] --> new [Min...max] = [");
	System.out.print(minValue);
	System.out.print("...");
	System.out.print(maxValue);
	System.out.print("]");
	System.out.print("\n");
	// now normalize the peaks to the given interval
	for (int i =0; i<length; i++)
	  samples[i] = (samples[i]-min) / oldDelta * newDelta + minValue;
  }

//------------------------------------------------------------------------------------------------

//*
//  * This static function normalizes all amplitudes of the given vector to the 
//  * avg given as funtion parameter. 
//  *
//  * @param  samples   A pointer to the sample vector.
//  * @param  length    The length of the sample vector.
//  * @param  avgValue  The normalization value.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void normalizeAvg(double[] samples, int length)
  {
	  normalizeAvg(samples, length, 1.0);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void normalizeAvg(double* samples, int length, double avgValue = 1.0)
  public static void normalizeAvg(double[] samples, int length, double avgValue)
  {
	// make sure that
	assert samples != null;
	// first calcluate the average value
	double sum = 0.0;
	for (int i =0; i<length; i++)
	{
	  sum += samples[i];
	}
	double avg = sum / (double)(length);
	// only for debugging purposes
	System.out.print("Prevoius AVG: ");
	System.out.print(avg);
	System.out.print(" new AVG: ");
	System.out.print(avgValue);
	System.out.print("\n");
	// then multiplicate the vector with the new value
	RefObject<Double> TempRefObject = new RefObject<Double>(samples);
	multiplicateVectorByValue(TempRefObject, length, avgValue/avg);
	samples = TempRefObject.argvalue;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This static function normalizes all amplitudes of the given vector to the 
//  * rms given as funtion parameter.
//  *
//  * @param  samples   A pointer to the sample vector.
//  * @param  length    The length of the sample vector.
//  * @param  rmsValue  The normalization value.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void normalizeRms(double[] samples, int length)
  {
	  normalizeRms(samples, length, 1.0);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void normalizeRms(double* samples, int length, double rmsValue = 1.0)
  public static void normalizeRms(double[] samples, int length, double rmsValue)
  {
	// make sure that
	assert samples != null;
	// first calcualte the the rms value
	double sqsum = 0.0;
	for (int i =0; i<length; i++)
	{
	  sqsum += samples[i]*samples[i];
	}
	double rms = Math.sqrt(sqsum/(double)(length));
	// only for debugging purposes
	System.out.print("Previous RMS: ");
	System.out.print(rms);
	System.out.print(" new RMS: ");
	System.out.print(rmsValue);
	System.out.print("\n");
	// then multiplicate the vector with the new value
	RefObject<Double> TempRefObject = new RefObject<Double>(samples);
	multiplicateVectorByValue(TempRefObject, length, rmsValue/rms);
	samples = TempRefObject.argvalue;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function multiplicates the given vector with the given factor
//  *
//  * @param    samples   A pointer to the sampel vector
//  * @param    length    The length of the samples vector
//  * @return   factor    The multiplication factor.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void multiplicateVectorByValue(double[] samples, int length, double factor)
  {
	// make sure that
	assert samples != null;
	// then multiplicate
	for (int i =0; i<length; i++)
	  samples[i] *= factor;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function multiplicates the given vector with the given factor
//  *
//  * @param    samples   A pointer to the sampel vector
//  * @param    length    The length of the samples vector
//  * @return   summand   The summand.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void addValueToVector(double[] samples, int length, double summand)
  {
	// make sure that
	assert samples != null;
	// then multiplicate
	for (int i =0; i<length; i++)
	  samples[i] *= summand;
  }

//------------------------------------------------------------------------------------------------

} // class Normalizer

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_NORMALIZER
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// Standard C Library headers

// Input/Output Stream Library headers

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------
