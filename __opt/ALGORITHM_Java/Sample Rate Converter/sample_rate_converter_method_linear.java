package clauer.math.Resample;

public class GlobalMembersSample_rate_converter_method_linear
{

//-------------------------------------------------------------------------------------------------

//*
// * The main processing loop for the conversion algorithmus
// 
	//*
	// * (c)      Christoph Lauer Engineering
	// *
	// * @file    sample_rate_converter_method_linear.cpp
	// * @class   clauer::math::Reasmple
	// * @version 1.0
	// * @date    2014
	// * @author  Christoph Lauer
	// * @brief   A linear inpolation based sample rate converter.
	// * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
	// *
	// * This function collection implements a simple linear interpolation based sample rate converter
	// 

	//-------------------------------------------------------------------------------------------------

	// stl headers

	// local headers

	//-------------------------------------------------------------------------------------------------


	//-------------------------------------------------------------------------------------------------

	// prototype declarations
	static int linear_vari_process(SRC_PRIVATE_tag psrc, SRC_DATA data)
	{
	  LINEAR_DATA linear;
		double src_ratio;
		double input_index;
		double rem;
		int ch;
		if (psrc.private_data == null)
			return AnonymousEnum.SRC_ERR_NO_PRIVATE;
		linear = (LINEAR_DATA) psrc.private_data;
	  if (linear.reset != 0)
		{ // If we have just been reset, set the last_value data.
			for (ch = 0; ch < linear.channels; ch++)
				linear.last_value [ch] = data.data_in [ch];
			linear.reset = 0;
		}
		linear.in_count = data.input_frames linear.channels;
		linear.out_count = data.output_frames linear.channels;
		linear.in_used = linear.out_gen = 0;
		src_ratio = psrc.last_ratio;
		input_index = psrc.last_position;
		// Calculate samples before first sample in input array.
		while (input_index < 1.0 && linear.out_gen < linear.out_count)
		{
			if (linear.in_used + linear.channels * input_index > linear.in_count)
				break;
			if (linear.out_count > 0 && Math.abs (psrc.last_ratio - data.src_ratio) > 1e-20)
				src_ratio = psrc.last_ratio + linear.out_gen * (data.src_ratio - psrc.last_ratio) / linear.out_count;
			for (ch = 0; ch < linear.channels; ch++)
			{
		  data.data_out [linear.out_gen] = (_float)(linear.last_value [ch] + input_index * (data.data_in [ch] - linear.last_value [ch]));
				linear.out_gen ++;
			}
			// Figure out the next index.
			input_index += 1.0 / src_ratio;
		}
		rem = fmod_one (input_index);
		linear.in_used += linear.channels * lrint (input_index - rem);
		input_index = rem;
		// Main processing loop.
		while (linear.out_gen < linear.out_count && linear.in_used + linear.channels * input_index <= linear.in_count)
		{
			if (linear.out_count > 0 && Math.abs (psrc.last_ratio - data.src_ratio) > 1e-20)
				src_ratio = psrc.last_ratio + linear.out_gen * (data.src_ratio - psrc.last_ratio) / linear.out_count;
			if (DefineConstantsSample_rate_converter_method_linear.SRC_DEBUG && linear.in_used < linear.channels && input_index < 1.0)
			{
		  System.out.printf ("Whoops!!!!   in_used : %ld     channels : %d     input_index : %f\n", linear.in_used, linear.channels, input_index);
				exit (1);
			}
			for (ch = 0; ch < linear.channels; ch++)
			{
		  data.data_out [linear.out_gen] = (_float)(data.data_in [linear.in_used - linear.channels + ch] + input_index * (data.data_in [linear.in_used + ch] - data.data_in [linear.in_used - linear.channels + ch]));
				linear.out_gen ++;
			}
			// Figure out the next index.
			input_index += 1.0 / src_ratio;
			rem = fmod_one (input_index);
			linear.in_used += linear.channels * lrint (input_index - rem);
			input_index = rem;
			}
		if (linear.in_used > linear.in_count)
		{
			input_index += (linear.in_used - linear.in_count) / linear.channels;
			linear.in_used = linear.in_count;
			}
		psrc.last_position = input_index;
		if (linear.in_used > 0)
			for (ch = 0; ch < linear.channels; ch++)
				linear.last_value [ch] = data.data_in [linear.in_used - linear.channels + ch];
		// Save current ratio rather then target ratio.
		psrc.last_ratio = src_ratio;
		data.input_frames_used = linear.in_used / linear.channels;
		data.output_frames_gen = linear.out_gen / linear.channels;
		return AnonymousEnum.SRC_ERR_NO_ERROR;
	}
//-------------------------------------------------------------------------------------------------

//*
// * preparation for re using the data structures
// 
	static void linear_reset(SRC_PRIVATE_tag psrc)
	{
	  LINEAR_DATA linear = null;
		linear = (LINEAR_DATA) psrc.private_data;
		if (linear == null)
			return;
		linear.channels = psrc.channels;
		linear.reset = 1;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		memset (linear.last_value, 0, sizeof (linear.last_value [0]) linear.channels);
	}

	//-------------------------------------------------------------------------------------------------

	//*
	// * Converter initialization.
	// 
	public static int linear_set_converter(SRC_PRIVATE_tag psrc, int src_enum)
	{
	  LINEAR_DATA linear = null;
		if (src_enum != SRC_LINEAR)
			return AnonymousEnum.SRC_ERR_BAD_CONVERTER;
		if (psrc.private_data != null)
		{
			linear = (LINEAR_DATA) psrc.private_data;
			if (linear.linear_magic_marker != ('l') + (('i') << 4) + (('n') << 8) + (('e') << 12) + (('a') << 16) + (('r') << 20))
			{
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
				free (psrc.private_data);
				psrc.private_data = null;
				}
			}
		if (psrc.private_data == null)
		{
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'calloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
			linear = (LINEAR_DATA) calloc (1, sizeof ( linear) + psrc.channels * sizeof (_float));
			if (linear == null)
				return AnonymousEnum.SRC_ERR_MALLOC_FAILED;
			psrc.private_data = linear;
			}
		linear.linear_magic_marker = ('l') + (('i') << 4) + (('n') << 8) + (('e') << 12) + (('a') << 16) + (('r') << 20);
		linear.channels = psrc.channels;
		psrc.const_process = linear_vari_process;
		psrc.vari_process = linear_vari_process;
		psrc.reset = linear_reset;
		linear_reset (psrc);
		return AnonymousEnum.SRC_ERR_NO_ERROR;
	}
}

//-------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER NOTE: The following #define macro was replaced in-line:
//#define LINEAR_MAGIC_MARKER (('l') + (('i') << 4) + (('n') << 8) + (('e') << 12) + (('a') << 16) + (('r') << 20))
//#define SRC_DEBUG 0

//-------------------------------------------------------------------------------------------------

//*
// * The linear interpolation data structure used for the conversion-
// 
public class LINEAR_DATA
{
  public int linear_magic_marker;
	public int channels;
	public int reset;
	public int in_count;
	public int in_used;
	public int out_count;
	public int out_gen;
	public GlobalMembersSample_rate_converter_method_linear.float[] last_value = new GlobalMembersSample_rate_converter_method_linear.float[1];
}

//-------------------------------------------------------------------------------------------------




//-------------------------------------------------------------------------------------------------
