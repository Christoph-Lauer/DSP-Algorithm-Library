/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    resampler.cpp
 * @class   Resampler
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Definition of public resampling interface
 * @see     http://en.wikipedia.org/wiki/resampling
 * @todo    finished so far, but there could be a better version for the vector cating 
 *          from the double to float.
 */

 //------------------------------------------------------------------------------------------------
 
 // Local headers
 #include "resampler.h"
 
 // Stl headers
 #include <iostream>
 
 //------------------------------------------------------------------------------------------------
 
 namespace clauer
 {
 namespace math
 {
 
 //------------------------------------------------------------------------------------------------

void Resampler::doResampling(const float* in, float* out, const long samplesIn, const long samplesOut, int converter)
{
  // first initialise the needed values
  clauer::math::Resample::SRC_STATE*  src_state;     
	clauer::math::Resample::SRC_DATA	   src_data;      
	int			                         error = false;
  
  // check if the downsampling factor is grater than 14. If yes fall back to the LINEAR converter.
  if ( converter==SRC_SINC_BEST_QUALITY || converter==SRC_SINC_MEDIUM_QUALITY || converter==SRC_SINC_FASTEST )
  {
    double downSamplingRate = static_cast<double>(samplesIn) / static_cast<double>(samplesOut);
    if(downSamplingRate > 14.0)
    {
      converter = SRC_LINEAR;
      std::cout << "Downsampling Rate = " << downSamplingRate << " --> fall back to the SRC_LINEAR converter" << std::endl;
    }
  }

	// Initialize the sample rate converter
	if ((src_state = clauer::math::Resample::src_new (converter, 1, &error)) == 0)
	{
    std::cerr << "error while initialization of the sample rate converter";
  }
  
  // set the sample rate converter data structure
	src_data.data_in        = const_cast<float*>(in);
	src_data.data_out       = out;
  src_data.input_frames   = samplesIn;
	src_data.output_frames  = samplesOut;
  src_data.src_ratio      = static_cast<double>(samplesOut) / static_cast<double>(samplesIn);
	src_data.end_of_input   = 1;

  // finally do the sample convertion
  if ((error = clauer::math::Resample::src_process (src_state, &src_data)))
  {	
    std::cerr << "error while execution of the sample rate converter";
  }
  
  // finally check if algorithm has computed enought samples
  long generated = src_data.output_frames_gen;
  // if not, fullfill the rest of the samples with the last number
  if (generated < samplesOut)
  {
    for (int i = (generated); i < samplesOut; i++)
    {
      out[i] = out[generated-1];
    }
  }
}

//-------------------------------------------------------------------------------------------------

void Resampler::doResampling(const double* in, double* out, const long samplesIn, const long samplesOut, int converter)
{
  // first cast the double vector to an float vector
  float* fin =  new float[samplesIn];
  for (int i=0; i<samplesIn; i++)
    fin[i] = static_cast<float>(in[i]);
  float* fout = new float[samplesOut];
  
  // then call the float wrapper class
  doResampling(fin, fout, samplesIn, samplesOut, converter);
  
  // Then convert the result back
  for (int i=0; i<samplesOut; i++)
    out[i] = static_cast<double>(fout[i]);
    
  // delete the temporary allocated float arrays
  delete[] fin;
  delete[] fout;
}

//-------------------------------------------------------------------------------------------------

} // namespace  math

} // namespace clauer
