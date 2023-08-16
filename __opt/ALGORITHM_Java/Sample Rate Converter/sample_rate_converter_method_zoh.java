package clauer.math.Resample;

public class GlobalMembersSample_rate_converter_method_zoh
{

//-------------------------------------------------------------------------------------------------

	//*
	// * (c)      Christoph Lauer Engineering
	// *
	// * @file    sample_rate_converter_method_zoh.cpp
	// * @class   clauer::math::Reasmple
	// * @version 1.0
	// * @date    2014
	// * @author  Christoph Lauer
	// * @brief   A Zero Order Hold converter (interpolated value is equal to the last value).
	// *          The quality is poor but the conversion speed is blindlingly fast.
	// * @see     http://en.wikipedia.org/wiki/Zero-order_hold
	// *
	// * This function collection implements a simple zero hold converter which brings not so good
	// * signal reconstruction quality but the convertion algorithmus is incredible fast. This ZOH 
	// * convertion algorithmus 
	// 

	//-------------------------------------------------------------------------------------------------

	// stl headers

	//-------------------------------------------------------------------------------------------------

	// local headers

	//-------------------------------------------------------------------------------------------------


	//-------------------------------------------------------------------------------------------------

	//*
	// * Define the local private prototypes here
	// 
	 //@{
	static int zoh_vari_process(SRC_PRIVATE_tag psrc, SRC_DATA data)
	{
	  ZOH_DATA zoh;
		double src_ratio;
		double input_index;
		double rem;
		int ch;

		if (psrc.private_data == null)
			return AnonymousEnum.SRC_ERR_NO_PRIVATE;

		zoh = (ZOH_DATA) psrc.private_data;

		if (zoh.reset != 0)
		{
		// If we have just been reset, set the last_value data.
			for (ch = 0; ch < zoh.channels; ch++)
				zoh.last_value [ch] = data.data_in [ch];
			zoh.reset = 0;
			}

		zoh.in_count = data.input_frames zoh.channels;
		zoh.out_count = data.output_frames zoh.channels;
		zoh.in_used = zoh.out_gen = 0;

		src_ratio = psrc.last_ratio;
		input_index = psrc.last_position;

		// Calculate samples before first sample in input array.
		while (input_index < 1.0 && zoh.out_gen < zoh.out_count)
		{
			if (zoh.in_used + zoh.channels * input_index >= zoh.in_count)
				break;

			if (zoh.out_count > 0 && Math.abs (psrc.last_ratio - data.src_ratio) > 1e-20)
				src_ratio = psrc.last_ratio + zoh.out_gen * (data.src_ratio - psrc.last_ratio) / zoh.out_count;

			for (ch = 0; ch < zoh.channels; ch++)
			{
				data.data_out [zoh.out_gen] = zoh.last_value [ch];
				zoh.out_gen ++;
				}

			// Figure out the next index.
			input_index += 1.0 / src_ratio;
			}

		rem = fmod_one (input_index);
		zoh.in_used += zoh.channels * lrint (input_index - rem);
		input_index = rem;

		// Main processing loop.
		while (zoh.out_gen < zoh.out_count && zoh.in_used + zoh.channels * input_index <= zoh.in_count)
		{
			if (zoh.out_count > 0 && Math.abs (psrc.last_ratio - data.src_ratio) > 1e-20)
				src_ratio = psrc.last_ratio + zoh.out_gen * (data.src_ratio - psrc.last_ratio) / zoh.out_count;

			for (ch = 0; ch < zoh.channels; ch++)
			{
		  data.data_out [zoh.out_gen] = data.data_in [zoh.in_used - zoh.channels + ch];
				zoh.out_gen ++;
			}

			// Figure out the next index.
			input_index += 1.0 / src_ratio;
			rem = fmod_one (input_index);

			zoh.in_used += zoh.channels * lrint (input_index - rem);
			input_index = rem;
			}

		if (zoh.in_used > zoh.in_count)
		{
			input_index += (zoh.in_used - zoh.in_count) / zoh.channels;
			zoh.in_used = zoh.in_count;
			}

		psrc.last_position = input_index;

		if (zoh.in_used > 0)
			for (ch = 0; ch < zoh.channels; ch++)
				zoh.last_value [ch] = data.data_in [zoh.in_used - zoh.channels + ch];

		// Save current ratio rather then target ratio.
		psrc.last_ratio = src_ratio;

		data.input_frames_used = zoh.in_used / zoh.channels;
		data.output_frames_gen = zoh.out_gen / zoh.channels;

		return AnonymousEnum.SRC_ERR_NO_ERROR;
	}

//-------------------------------------------------------------------------------------------------

//*
// * preparation for re using the data structures
// 
	static void zoh_reset(SRC_PRIVATE_tag psrc)
	{
	  ZOH_DATA zoh;

		zoh = (ZOH_DATA) psrc.private_data;
		if (zoh == null)
			return;

		zoh.channels = psrc.channels;
		zoh.reset = 1;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		memset (zoh.last_value, 0, sizeof (zoh.last_value [0]) zoh.channels);

		return;
	}

	//-------------------------------------------------------------------------------------------------

	//*
	// * Converter initialization 
	// 
	public static int zoh_set_converter(SRC_PRIVATE_tag psrc, int src_enum)
	{
		ZOH_DATA zoh = null;

		if (src_enum != SRC_ZERO_ORDER_HOLD)
			return AnonymousEnum.SRC_ERR_BAD_CONVERTER;

		if (psrc.private_data != null)
		{
		zoh = (ZOH_DATA) psrc.private_data;
			if (zoh.zoh_magic_marker != ('s') + (('r') << 4) + (('c') << 8) + (('z') << 12) + (('o') << 16) + (('h') << 20))
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
		zoh = (ZOH_DATA) calloc (1, sizeof ( zoh) + psrc.channels * sizeof (_float));
			if (zoh == null)
				return AnonymousEnum.SRC_ERR_MALLOC_FAILED;
			psrc.private_data = zoh;
		}

		zoh.zoh_magic_marker = ('s') + (('r') << 4) + (('c') << 8) + (('z') << 12) + (('o') << 16) + (('h') << 20);
		zoh.channels = psrc.channels;

		psrc.const_process = zoh_vari_process;
		psrc.vari_process = zoh_vari_process;
		psrc.reset = zoh_reset;

		zoh_reset (psrc);

		return AnonymousEnum.SRC_ERR_NO_ERROR;
	}
}
//@}

//-------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER NOTE: The following #define macro was replaced in-line:
//#define ZOH_MAGIC_MARKER (('s') + (('r') << 4) + (('c') << 8) + (('z') << 12) + (('o') << 16) + (('h') << 20))

//-------------------------------------------------------------------------------------------------

public class ZOH_DATA
{
  public int zoh_magic_marker;
	public int channels;
	public int reset;
	public int in_count;
	public int in_used;
	public int out_count;
	public int out_gen;
	public GlobalMembersSample_rate_converter_method_zoh.float[] last_value = new GlobalMembersSample_rate_converter_method_zoh.float[1];
}

//-------------------------------------------------------------------------------------------------




//-------------------------------------------------------------------------------------------------
