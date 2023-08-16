package clauer.math;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    resampler.cpp
// * @class   Resampler
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Definition of public resampling interface
// * @see     http://en.wikipedia.org/wiki/resampling
// * @todo    finished so far, but there could be a better version for the vector cating 
// *          from the double to float.
// 

 //------------------------------------------------------------------------------------------------

 // Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    resampler.h
// * @class   Resampler
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Definition of public resampling interface
// * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
// * @todo    finished so far.
// *
// * @note    !!! PLEASE NOTE THAT THE THREE SINC-TYPE SAMPLE RATE CONVERTERS DOES NOTE WORK !!! 
// *          !!! WITH A RESAMPLING FACTOR FROM ABOUT 0.1 AND DEEPER !!! THIS EQUIVALENT TO  !!!
// *          !!! DOWNSAMPLING RATE FROM ABOUT 10 AND ABOVE. THIS IS NOT A BUG BUT MORE AN   !!!
// *          !!! RATHER UNCONVENTIONAL DOWNSAMPLING RATE WHICH IS NOT SUPPORTED BY THE SINC !!!
// *          !!! CONVERTERS.                                                                !!!
// *
// * @note    Here simple Benchmark result which unfold the performance of the different converters.
// *          Point of reference is the SINC_BEST converter which will be threated as factor one.
// *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000 
// *          LINEAR              :           0.0066                      0.0061
// *          ZERO_ORDER_HOLD     :           0.0064                      0.0064
// *          SINC_FASTEST        :           0.12                        0.12
// *          SINC_MEDIUM_QUALITY :           0.28                        0.29
// *          SINC_BEST_QUALITY   :           1.0                         1.0
// *
// * This class is a simple c++ wrapper to the sample rate converter c function collection
// * defined into the sample_rate_converter files. The functions offer a sample rate conversion with
// * three different methods. The SRC_LINEAR and the SRC_ZERO_ORDER_HOLD methods are really fast but
// * so bad in the results that the usage of the SRC_SINC_FASTEST should be the best solution in the 
// * most cases...
// *
// 

 //------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
 //#if ! CLAUER_MATH_RESAMPLER
 //#define CLAUER_MATH_RESAMPLER

 //------------------------------------------------------------------------------------------------

 // Local headers

 //------------------------------------------------------------------------------------------------


 //------------------------------------------------------------------------------------------------

//*
// * This class implemets a basic wrapper class to the sample rate converter function collection defined 
// * in the sample_rate_converter files. This wrapper is not like the most other functions in the clauer::math
// * namespace implemented in a static contex so an instance of the class must be instanciated before
// * one of the two converter functions can be used.
// 
public class Resampler
{

// *
//  * The following enums can be used to set the interpolator type.
//  *
//  * SRC_SINC_BEST_QUALITY    This is a bandlimited interpolator derived from the mathematical sinc
//  *                          function and this is the highest quality sinc based converter, providing
//  *                          a worst case Signal-to-Noise Ratio (SNR) of 97 decibels (dB) at a bandwidth
//  *                          of 97%. All three SRC_SINC_* converters are based on the techniques of
//  *                          Julius O. Smith although this code was developed independantly.
//  * SRC_SINC_MEDIUM_QUALITY  This is another bandlimited interpolator much like the previous one.
//  *                          It has an SNR of 97dB and a bandwidth of 90%. The speed of the conversion
//  *                          is much faster than the previous one.
//  * SRC_SINC_FASTEST         This is the fastest bandlimited interpolator and has an SNR of 97dB and a bandwidth of 80%.
//  * SRC_ZERO_ORDER_HOLD      A Zero Order Hold converter (interpolated value is equal to the last value).
//  *                          The quality is poor but the conversion speed is blindlingly fast.
//  * SRC_LINEAR               A linear converter. Again the quality is poor, but the conversion speed is
//  *                          blindingly fast.
//  *
//  * @note    Here simple Benchmark result which unfold the performance of the different converters.
//  *          Point of reference is the SINC_BEST converter which will be threated as factor one.
//  *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000
//  *          LINEAR              :           0.0066                      0.0061
//  *          ZERO_ORDER_HOLD     :           0.0064                      0.0064
//  *          SINC_FASTEST        :           0.12                        0.12
//  *          SINC_MEDIUM_QUALITY :           0.28                        0.29
//  *          SINC_BEST_QUALITY   :           1.0                         1.0
//  *
//  
  public enum converter
  {
	  SRC_SINC_BEST_QUALITY,
	  SRC_SINC_MEDIUM_QUALITY,
	  SRC_SINC_FASTEST,
	  SRC_ZERO_ORDER_HOLD,
	  SRC_LINEAR
  }

//------------------------------------------------------------------------------------------------

//   *
//    * This is basic convertion wreapper function for the float data type. This function is not in place and 
//    * the memory vector for the conversion must be allocated outside this function.
//    * 
//    * @note     !!! PLEASE NOTE THAT THE THREE SINC-TYPE SAMPLE RATE CONVERTERS DOES NOTE WORK !!! 
//    *           !!! WITH A RESAMPLING FACTOR FROM ABOUT 0.1 AND DEEPER !!! THIS EQUIVALENT TO  !!!
//    *           !!! ABOUT AND DOWNSAMPLING FACTOR FROM 10 AND ABOVE. THIS IS NOT A BUG BUT     !!!
//    *           !!! MORE AN UNCONVENTIONAL DOWNSAMPLING RATE WHICH IS NOT SUPPORTED BY THE     !!!
//    *           !!! SINC CONVERTERS.                                                           !!!
//    *
//    * @note     Here simple Benchmark result which unfold the performance of the different converterrs.
//    *           Point of reference is the SINC_BEST converter which will be threated as factor one.
//    *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000
//    *           LINEAR              :           0.0066                      0.0061
//    *           ZERO_ORDER_HOLD     :           0.0064                      0.0064
//    *           SINC_FASTEST        :           0.12                        0.12
//    *           SINC_MEDIUM_QUALITY :           0.28                        0.29
//    *           SINC_BEST_QUALITY   :           1.0                         1.0
//    * 
//    * @param    in          A ponter to the input vector.
//    * @param    out         A ponter to the input vector.
//    * @param    samplesIn   The count of the Input samples.
//    * @param    samplesOut  The count of the Output samples. Please not that the up/down sampling factor
//    *                       will be calculated from the rate og the samplesIn / samplesOut. Note that the 
//    *                       output smaples array must be pre allocated outside this function !!!
//    * @param    converter   Here the used converter corresponding to the enum declaration above 
//    *                       is declared.
//    *                       !!! The resampler uses automatically the LINEAR converter for downsampling rates !!!
//    *                       !!! lesser than 14 which corresponds to an resampling factor of 0.0714 if one of !!!
//    *                       !!! the SINC converters was selected.                                            !!!
//    

	 //------------------------------------------------------------------------------------------------
	
	public final void doResampling(float in, float[] out, int samplesIn, int samplesOut, int converter)
	{
	  // first initialise the needed values
	  GlobalMembersResampler.clauer.math.Resample.SRC_STATE src_state;
		GlobalMembersResampler.clauer.math.Resample.SRC_DATA src_data = new GlobalMembersResampler.clauer.math.Resample.SRC_DATA();
		int error = false;
	
	  // check if the downsampling factor is grater than 14. If yes fall back to the LINEAR converter.
	  if (converter ==SRC_SINC_BEST_QUALITY || converter ==SRC_SINC_MEDIUM_QUALITY || converter ==SRC_SINC_FASTEST)
	  {
		double downSamplingRate = (double)(samplesIn) / (double)(samplesOut);
		if(downSamplingRate > 14.0)
		{
		  converter = SRC_LINEAR;
		  System.out.print("Downsampling Rate = ");
		  System.out.print(downSamplingRate);
		  System.out.print(" --> fall back to the SRC_LINEAR converter");
		  System.out.print("\n");
		}
	  }
	
		// Initialize the sample rate converter
		if ((src_state = GlobalMembersResampler.clauer.math.Resample.src_new (converter, 1, error)) == 0)
		{
		std.cerr << "error while initialization of the sample rate converter";
	  }
	
	  // set the sample rate converter data structure
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to 'const_cast' in Java:
		src_data.data_in = const_cast<GlobalMembersResampler.float*>(in);
		src_data.data_out = out;
	  src_data.input_frames = samplesIn;
		src_data.output_frames = samplesOut;
	  src_data.src_ratio = (double)(samplesOut) / (double)(samplesIn);
		src_data.end_of_input = 1;
	
	  // finally do the sample convertion
	  if ((error = GlobalMembersResampler.clauer.math.Resample.src_process (src_state, src_data)))
	  {
		std.cerr << "error while execution of the sample rate converter";
	  }
	
	  // finally check if algorithm has computed enought samples
	  int generated = src_data.output_frames_gen;
	  // if not, fullfill the rest of the samples with the last number
	  if (generated < samplesOut)
	  {
		for (int i = (generated); i < samplesOut; i++)
		{
		  out[i] = out[generated-1];
		}
	  }
	}

//------------------------------------------------------------------------------------------------

//   *
//    * This function has exactely the same functionality as the float function above but it does 
//    * internaly a casting from the double type to float vice versa because the sample rate convertion
//    * is only defined for the float type. Simple casting tests shown that casting is very fast done on
//    * moder processors so using the double type should ne be a problem with this function signature.
//    *
//    * @note     !!! PLEASE NOTE THAT THE THREE SINC-TYPE SAMPLE RATE CONVERTERS DOES NOTE WORK !!! 
//    *           !!! WITH A RESAMPLING FACTOR FROM ABOUT 0.1 AND DEEPER !!! THIS EQUIVALENT TO  !!!
//    *           !!! ABOUT AND DOWNSAMPLING FACTOR FROM 10 AND ABOVE. THIS IS NOT A BUG BUT     !!!
//    *           !!! MORE AN UNCONVENTIONAL DOWNSAMPLING RATE WHICH IS NOT SUPPORTED BY THE     !!!
//    *           !!! SINC CONVERTERS.                                                           !!!
//    *
//    * @note     Here simple Benchmark result which unfold the performance of the different converters.
//    *           Point of reference is the SINC_BEST converter which will be threated as factor one.
//    *                                UPSAMPLING 48000->64000       DOWNSAMPLING 48000->8000
//    *           LINEAR              :           0.0066                      0.0061
//    *           ZERO_ORDER_HOLD     :           0.0064                      0.0064
//    *           SINC_FASTEST        :           0.12                        0.12
//    *           SINC_MEDIUM_QUALITY :           0.28                        0.29
//    *           SINC_BEST_QUALITY   :           1.0                         1.0
//    *
//    * @param    in          A ponter to the input vector.
//    * @param    out         A ponter to the input vector.
//    * @param    samplesIn   The count of the Input samples.
//    * @param    samplesOut  The count of the Output samples. Please not that the up/down sampling factor
//    *                       will be calculated from the rate og the samplesIn / samplesOut. Note that the 
//    *                       output smaples array must be pre allocated outside this function !!!
//    * @param    converter   Here the used converter corresponding to the enum declaration above 
//    *                       is declared.
//    *                       !!! The resampler uses automatically the LINEAR converter for downsampling rates !!!
//    *                       !!! lesser than 14 which corresponds to an resampling factor of 0.0714 if one of !!!
//    *                       !!! the SINC converters was selected.                                            !!!
//    

	//-------------------------------------------------------------------------------------------------
	
	public final void doResampling(double[] in, double[] out, int samplesIn, int samplesOut, int converter)
	{
	  // first cast the double vector to an float vector
	  GlobalMembersResampler.float[] fin = new GlobalMembersResampler.float[samplesIn];
	  for (int i =0; i<samplesIn; i++)
		fin[i] = (GlobalMembersResampler.float)(in[i]);
	  GlobalMembersResampler.float[] fout = new GlobalMembersResampler.float[samplesOut];
	
	  // then call the float wrapper class
	  RefObject<Float> TempRefObject = new RefObject<Float>(fout);
	  doResampling(fin, TempRefObject, samplesIn, samplesOut, converter);
	  fout = TempRefObject.argvalue;
	
	  // Then convert the result back
	  for (int i =0; i<samplesOut; i++)
		out[i] = (double)(fout[i]);
	
	  // delete the temporary allocated float arrays
	  fin = null;
	  fout = null;
	}
} // class Resampler

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_RESAMPLER
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


 // Stl headers

 //------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------


