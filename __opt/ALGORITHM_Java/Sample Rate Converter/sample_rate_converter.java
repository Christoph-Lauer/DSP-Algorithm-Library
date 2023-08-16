package clauer.math.Resample;

public class GlobalMembersSample_rate_converter
{

//-------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: The typedef SRC_STATE was defined in alternate ways and cannot be replaced in-line:

	//-------------------------------------------------------------------------------------------------

	//*
	// *	This is the standard initialisation function : return an anonymous pointer to the
	// *	internal state of the converter. Choose a converter from the enums below.
	// *	Error returned in *error.
	// 
	public static SRC_STATE src_new(int converter_type, int channels, RefObject<Integer> error)
	{
	  SRC_PRIVATE_tag psrc;
		if (error.argvalue != 0)
			error.argvalue = (int)AnonymousEnum.SRC_ERR_NO_ERROR;
		if (channels < 1)
		{
		if (error.argvalue != 0)
				error.argvalue = (int)AnonymousEnum.SRC_ERR_BAD_CHANNEL_COUNT;
			return null;
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		if ((psrc = (SRC_PRIVATE_tag) calloc (1, sizeof ( psrc))) == null)
		{
		if (error.argvalue != 0)
				error.argvalue = (int)AnonymousEnum.SRC_ERR_MALLOC_FAILED;
			return null;
		}
		psrc.channels = channels;
		psrc.mode = (int)AnonymousEnum.SRC_MODE_PROCESS;
		if (psrc_set_converter (psrc, converter_type) != AnonymousEnum.SRC_ERR_NO_ERROR)
		{
		if (error.argvalue != 0)
				error.argvalue = (int)AnonymousEnum.SRC_ERR_BAD_CONVERTER;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
			free (psrc);
			psrc = null;
		}
	//C++ TO JAVA CONVERTER TODO TASK: The typedef SRC_STATE was defined in alternate ways and cannot be replaced in-line:
		src_reset ((SRC_STATE) psrc);
	//C++ TO JAVA CONVERTER TODO TASK: The typedef SRC_STATE was defined in alternate ways and cannot be replaced in-line:
		return (SRC_STATE) psrc;
	}

	//-------------------------------------------------------------------------------------------------

	//*
	// *	Cleanup all internal allocations.
	// *	Always returns NULL.
	// 
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
	//SRC_STATE_tag src_delete(RefObject<SRC_STATE_tag> state);

	//-------------------------------------------------------------------------------------------------

	//*
	// *	Standard processing function.
	// *	Returns non zero on error.
	// 
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
	//int src_process(RefObject<SRC_STATE_tag> state, SRC_DATA data);

	//-------------------------------------------------------------------------------------------------

	//*
	// *	Reset the internal SRC state. Does not modify the quality settings.
	// *	Does not free any memory allocations. Returns non zero on error.
	// 
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
	//int src_reset(RefObject<SRC_STATE_tag> state);

//-------------------------------------------------------------------------------------------------

//*
// * Private function definition needed only in this file.
// 


	//-------------------------------------------------------------------------------------------------


	//-------------------------------------------------------------------------------------------------

	// prototype declaration for the private functions in this file
	static int psrc_set_converter(SRC_PRIVATE_tag psrc, int converter_type)
	{
		if (sinc_set_converter (psrc, converter_type) == AnonymousEnum.SRC_ERR_NO_ERROR)
			return AnonymousEnum.SRC_ERR_NO_ERROR;
		if (zoh_set_converter (psrc, converter_type) == AnonymousEnum.SRC_ERR_NO_ERROR)
			return AnonymousEnum.SRC_ERR_NO_ERROR;
		if (linear_set_converter (psrc, converter_type) == AnonymousEnum.SRC_ERR_NO_ERROR)
			return AnonymousEnum.SRC_ERR_NO_ERROR;
		return AnonymousEnum.SRC_ERR_BAD_CONVERTER;
	}

	//-------------------------------------------------------------------------------------------------

	static __inline int is_bad_src_ratio(double ratio)
	{
	  return (ratio < (1.0 / DefineConstantsSample_rate_converter.SRC_MAX_RATIO) || ratio > (1.0 * DefineConstantsSample_rate_converter.SRC_MAX_RATIO));
	}

	//-------------------------------------------------------------------------------------------------

	//C++ TO JAVA CONVERTER TODO TASK: The typedef SRC_STATE was defined in alternate ways and cannot be replaced in-line:
	public static SRC_STATE src_delete(RefObject<SRC_STATE> state)
	{
	  SRC_PRIVATE_tag psrc;
		psrc = (SRC_PRIVATE_tag) state.argvalue;
		if (psrc != null)
		{
			if (psrc.private_data)
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
				free (psrc.private_data);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
			memset (psrc, 0, sizeof (SRC_PRIVATE_tag));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
			free (psrc);
			}
		return null;
	}

	//-------------------------------------------------------------------------------------------------

	//C++ TO JAVA CONVERTER TODO TASK: The typedef SRC_STATE was defined in alternate ways and cannot be replaced in-line:
	public static int src_process(RefObject<SRC_STATE> state, SRC_DATA data)
	{
	  SRC_PRIVATE_tag psrc;
		int error;
		psrc = (SRC_PRIVATE_tag) state.argvalue;
		if (psrc == null)
			return AnonymousEnum.SRC_ERR_BAD_STATE;
		if (psrc.vari_process == null || psrc.const_process == null)
			return AnonymousEnum.SRC_ERR_BAD_PROC_PTR;
		if (psrc.mode != (int)AnonymousEnum.SRC_MODE_PROCESS)
			return AnonymousEnum.SRC_ERR_BAD_MODE;
		// Check for valid SRC_DATA first. 
		if (data == null)
			return AnonymousEnum.SRC_ERR_BAD_DATA;
		// Check src_ratio is in range. 
		if (is_bad_src_ratio (data.src_ratio) != 0)
			return AnonymousEnum.SRC_ERR_BAD_SRC_RATIO;
		// And that data_in and data_out are valid. 
		if (data.data_in == null || data.data_out == null)
			return AnonymousEnum.SRC_ERR_BAD_DATA_PTR;
		if (data.data_in == null)
			data.input_frames = 0;
		if (data.input_frames < 0)
			data.input_frames = 0;
		if (data.output_frames < 0)
			data.output_frames = 0;
		if (data.data_in < data.data_out)
		{
			if (data.data_in + data.input_frames psrc.channels > data.data_out)
			{
	//      -printf ("\n\ndata_in: %p    data_out: %p\n",
	//			(void*) (data->data_in + data->input_frames * psrc->channels), (void*) data->data_out);-
				return AnonymousEnum.SRC_ERR_DATA_OVERLAP;
			}
		}
		else if (data.data_out + data.output_frames psrc.channels > data.data_in)
		{
	//    -printf ("\n\ndata_in : %p   ouput frames: %ld    data_out: %p\n", (void*) data->data_in, data->output_frames, (void*) data->data_out);
	//		printf ("data_out: %p (%p)    data_in: %p\n", (void*) data->data_out,
	//		(void*) (data->data_out + data->input_frames * psrc->channels), (void*) data->data_in);-
			return AnonymousEnum.SRC_ERR_DATA_OVERLAP;
		}
		// Set the input and output counts to zero. 
		data.input_frames_used = 0;
		data.output_frames_gen = 0;
		// Special case for when last_ratio has not been set. 
		if (psrc.last_ratio < (1.0 / DefineConstantsSample_rate_converter.SRC_MAX_RATIO))
			psrc.last_ratio = data.src_ratio;
		// Now process. 
		if (Math.abs (psrc.last_ratio - data.src_ratio) < 1e-15)
			error = psrc.const_process (psrc, data);
		else
			error = psrc.vari_process (psrc, data);
		return error;
	}

	//-------------------------------------------------------------------------------------------------

	//C++ TO JAVA CONVERTER TODO TASK: The typedef SRC_STATE was defined in alternate ways and cannot be replaced in-line:
	public static int src_reset(RefObject<SRC_STATE> state)
	{
	  SRC_PRIVATE_tag psrc;
		if ((psrc = (SRC_PRIVATE_tag) state.argvalue) == null)
			return AnonymousEnum.SRC_ERR_BAD_STATE;
		if (psrc.reset != null)
			psrc.reset (psrc);
		psrc.last_position = 0.0;
		psrc.last_ratio = 0.0;
		psrc.error = (int)AnonymousEnum.SRC_ERR_NO_ERROR;
		return AnonymousEnum.SRC_ERR_NO_ERROR;
	}
}
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    sample_rate_converter.cpp
// * @class   Autocorrelation
// * @version 0.5
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Function collection for the sample rate convertion.
// *
// * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
// * @todo    the whule function structure should be ordered in classes !
// *
// * This file collectes the the steering functions for the sample rate convertion function
// * collection which implements three kind of sample rate convertion methods. See the possible
// * conversion definition enumeration above for a more detailed description of the methds defined here.
// 

//-------------------------------------------------------------------------------------------------

// Stl headers

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    sample_rate_converter.h
// * @class   Reasmple
// * @version 0.5
// * @date    2014
// * @author  Christoph Lauer
// * @brief   Function collection for the sample rate convertion.
// *
// * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
// * @todo    the whule function structure should be ordered in classes !
// *
// * This file collectes the the steering functions for the sample rate convertion function
// * collection which implements three kind of sample rate convertion methods. See the possible
// * conversion definition enumeration above for a more detailed description of the methds defined here.
// * 
// * NOTE: the ZOH converter should bring the poorest results, followed from the 
// * linear converter. Best results bring the sinc converters.
// 

//-------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! SAMPLE_RATE_CONVERTER
//#define SAMPLE_RATE_CONVERTER

//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------

/// Opaque data type SRC_STATE
//C++ TO JAVA CONVERTER TODO TASK: Alternate typedef's with the same name cannot be converted to Java:
typedef struct SRC_STATE_tag SRC_STATE;

//-------------------------------------------------------------------------------------------------

//*
// * This struct holds the basic data structure for the resampling process.
// * @param   in                  A float pointer to the input vector.
// * @param   out                 A float vector to the preallocated output vector.
// * @param   imput_frames        The number of samples to convert.
// * @param   output_frames       The maximum number of output samples to generate.
// * @param   input_frames_used   This value is set while the transformation process
// *                              and up counts the used input samples while the resampling.
// * @param   output_frames_gen   This value is set while the transformation process
// *                              and up counts the generated output samples while the resampling process.
// * @param   end_of_input        This 1/0 value is set to 0 for streaming and set to 1
// *                              if if this was the last buffer which was resampled
// * @param   src_ratio           The rate of output_sample_rate / input_sample_rate.
// 
public class SRC_DATA
{
  public GlobalMembersSample_rate_converter.float data_in;
  public GlobalMembersSample_rate_converter.float data_out;
	public int input_frames;
  public int output_frames;
	public int input_frames_used; // will be set while the processing
  public int output_frames_gen; // will be set while the processing
	public int end_of_input;
  public double src_ratio;
}

//-------------------------------------------------------------------------------------------------

//*
// * The following enums can be used to set the interpolator type using the function src_set_converter().
// * SRC_SINC_BEST_QUALITY    This is a bandlimited interpolator derived from the mathematical sinc
// *                          function and this is the highest quality sinc based converter, providing
// *                          a worst case Signal-to-Noise Ratio (SNR) of 97 decibels (dB) at a bandwidth
// *                          of 97%. All three SRC_SINC_* converters are based on the techniques of
// *                          Julius O. Smith although this code was developed independantly.
// * SRC_SINC_MEDIUM_QUALITY  This is another bandlimited interpolator much like the previous one.
// *                          It has an SNR of 97dB and a bandwidth of 90%. The speed of the conversion
// *                          is much faster than the previous one.
// * SRC_SINC_FASTEST         This is the fastest bandlimited interpolator and has an SNR of 97dB and a bandwidth of 80%.
// * SRC_ZERO_ORDER_HOLD      A Zero Order Hold converter (interpolated value is equal to the last value).
// *                          The quality is poor but the conversion speed is blindlingly fast.
// * SRC_LINEAR               A linear converter. Again the quality is poor, but the conversion speed is
// *                          blindingly fast.
// *
// * NOTE: the ZOH converter should bring the poorest results, followed from the 
// * linear converter. Best results bring the sinc converters.
// 
//C++ TO JAVA CONVERTER NOTE: Enums must be named in Java, so the following enum has been named AnonymousEnum:
public enum AnonymousEnum
{
	SRC_SINC_BEST_QUALITY(0),
	SRC_SINC_MEDIUM_QUALITY(1),
	SRC_SINC_FASTEST(2),
	SRC_ZERO_ORDER_HOLD(3),
	SRC_LINEAR(4),
}

//-------------------------------------------------------------------------------------------------




//-------------------------------------------------------------------------------------------------

//#endif // SAMPLE_RATE_CONVERTER
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace Resample

//-------------------------------------------------------------------------------------------------





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