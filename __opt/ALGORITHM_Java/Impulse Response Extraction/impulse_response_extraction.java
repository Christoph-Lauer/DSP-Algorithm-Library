package clauer.math;

import java.util.*;

//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    impulse_response_extractor.cpp
// * @class   ImpulseResponseExtractor
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   THis class supports basic functions for the  extract of the impulse response with the sine sweep technique
// * @see     http://en.wikipedia.org/wiki/Transfer_function
// * @see     http://en.wikipedia.org/wiki/Impulse_response
// * @see     http://en.wikipedia.org/wiki/Digital_room_correction
// * @see     http://en.wikipedia.org/wiki/Deconvolution
// * @see     http://de.wikipedia.org/wiki/Wobbelgenerator
// * @todo    finished so farso far
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    impulse_response_extractor.h
// * @class   ImpulseResponseExtractor
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   THis class supports basic functions for the  extract of the impulse response with the sine sweep technique
// * @see     http://en.wikipedia.org/wiki/Transfer_function
// * @see     http://en.wikipedia.org/wiki/Impulse_response
// * @see     http://en.wikipedia.org/wiki/Digital_room_correction
// * @see     http://en.wikipedia.org/wiki/Deconvolution
// * @see     http://de.wikipedia.org/wiki/Wobbelgenerator
// * @todo    finished so farso far
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_MATH_IMPULSE_RESPONSE_EXTRACTOR
//#define CLAUER_MATH_IMPULSE_RESPONSE_EXTRACTOR

//------------------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * This class supports with a collection of usefull fucntion for the impulse response extraction with
// * the sine sweep techique. Like all the other mathematical functions this class has only static 
// * function members. If the sweep response is recorded a deconvolution function be called for extraction
// * of either the transfer function of the impulse response of the system.
// 
public class ImpulseResponseExtraction
{

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

// *
//  * This function generates an exponential sweep sine signal with the desired max amplitude. A speacial
//  * flag can be used to extract a linear sweeped signal. For a core sine signal without the sweep set 
//  * the start and end frequency to the same value.
//  *
//  * @param  duration       The sin sweep signal duration in seconds.
//  * @param  sampleRate     The signal sample rate.
//  * @param  startFrequency The sweep start frequency (in Hz) must be >= 1.0. Any value smaler than 1Hz 
//  *                        The start frequency will be set to 1Hz.
//  * @param  endFrequency   the sweep end frequency (in Hz). Same values for the start and the end signal 
//  *                        a unsweeped sine signal will be genrated.
//  * @param  amplitude      The amplitude of the generated sinus sweep.
//  * @param  legnth         The reference value will be set to the generated number of samples.
//  * @param  linear         Per default this function returns an exponential sweep signal. Is this 
//  *                        flag set to true a linear sweep signal will be generated.
//  * @param  writeSweepFile This flag is per default set to false. If it is set to true, a file named
//  *                        "sweep.wav" will be saved to the local file system. The file format is 
//  *                        16 Bit with the desired sample rate.
//  * @param  inverse        If this flag is set to true, the inverse signal for the time domain deconvolution
//  *                        will be generated, which means that the sine signal will be generated using a
//  *                        cosine signal with an post processed differentation. The inverse of the sin
//  *                        sweep signal is then the time reversal with an 6db per octave damping.
//  * @return                The function returns the generated samples. Note that the sample vector is 
//  *                        allocated into the function so no pre allocation is needed. If the
//  *                        writeSweepFile is set the allocated sample vector will be deleted and a
//  *                        void pointer is geven back.
//  

  //------------------------------------------------------------------------------------------------
  
  public static double clauer.generateSineSweep(double duration, int sampleRate, double startFrequency, double endFrequency, double amplitude, RefObject<Integer> length, double silence, boolean linear, boolean writeSweepFile)
  {
	  return generateSineSweep(duration, sampleRate, startFrequency, endFrequency, amplitude, length, silence, linear, writeSweepFile, false);
  }
  public static double clauer.generateSineSweep(double duration, int sampleRate, double startFrequency, double endFrequency, double amplitude, RefObject<Integer> length, double silence, boolean linear)
  {
	  return generateSineSweep(duration, sampleRate, startFrequency, endFrequency, amplitude, length, silence, linear, false, false);
  }
  public static double clauer.generateSineSweep(double duration, int sampleRate, double startFrequency, double endFrequency, double amplitude, RefObject<Integer> length, double silence)
  {
	  return generateSineSweep(duration, sampleRate, startFrequency, endFrequency, amplitude, length, silence, false, false, false);
  }
  public static double clauer.generateSineSweep(double duration, int sampleRate, double startFrequency, double endFrequency, double amplitude, RefObject<Integer> length)
  {
	  return generateSineSweep(duration, sampleRate, startFrequency, endFrequency, amplitude, length, 0.0, false, false, false);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double* clauer::generateSineSweep(const double duration, const int sampleRate, double startFrequency, const double endFrequency, const double amplitude, int& length, const double silence = 0.0, const boolean linear = false, const boolean writeSweepFile = false, boolean inverse = false)
  public static double clauer.generateSineSweep(double duration, int sampleRate, double startFrequency, double endFrequency, double amplitude, RefObject<Integer> length, double silence, boolean linear, boolean writeSweepFile, boolean inverse)
  {
	// make sure that the input values are in a correct condition
	assert endFrequency >= startFrequency;
	assert startFrequency >= 0.0;
	if (startFrequency < 1.0)
		startFrequency = 1.0;
	assert duration > 0;
	assert sampleRate > 0;
  
	// first initialize the signal allocate array
	length.argvalue = (int)((double)(sampleRate) * duration);
	int silenceLength = (int)((double)(sampleRate) * silence);
	double[] sweep = new double[length.argvalue+silenceLength];
  
  
	// then generate the signal array
	for (int n =0; n<length.argvalue; n++)
	{
	  // for a core sin signal without sweep
	  if (startFrequency == endFrequency)
	  {
		sweep[n] = Math.sin(startFrequency *2.0 *DefineConstantsImpulse_response_extraction.PI *(double)(n)/(double)(sampleRate));
		continue;
	  }
	  // for the exponential rise sweep
	  if (linear == false)
	  {
		if (inverse == false)
		  sweep[n] = Math.sin(startFrequency * (double)(length.argvalue)/(Math.log(endFrequency/startFrequency))*(Math.exp((double)(n)/(double)(length.argvalue)*Math.log(endFrequency/startFrequency))-1.0)/(double)(sampleRate)*DefineConstantsImpulse_response_extraction.PI *2.0);
		else
		  sweep[(length.argvalue+silenceLength)-n] = -Math.cos(startFrequency * (double)(length.argvalue)/(Math.log(endFrequency/startFrequency))*(Math.exp((double)(n)/(double)(length.argvalue)*Math.log(endFrequency/startFrequency))-1.0)/(double)(sampleRate)*DefineConstantsImpulse_response_extraction.PI *2.0);
		continue;
	  }
	  // for the linear rise sweep
	  if (linear == true)
	  {
		double frequency = startFrequency+((double)(n)/(double)(length.argvalue)*(endFrequency - startFrequency)/2.0);
		if (inverse == false)
		  sweep[n] = Math.sin(frequency *2.0 *DefineConstantsImpulse_response_extraction.PI *(double)(n)/(double)(sampleRate));
		else
		  sweep[(length.argvalue+silenceLength)-n] = -Math.cos(frequency *2.0 *DefineConstantsImpulse_response_extraction.PI *(double)(n)/(double)(sampleRate));
	  }
	}
  
	// fade out the sine sweep signal for the last 2 percent of the siganl to prevent hard wideband peaks at the of the singal in the spectrum
	for (int n =length.argvalue / 1000 * 995; n<length.argvalue; n++)
	{
	  // first calcualte the damping factor
	  double begin = (double)(length.argvalue) / 1000.0 * 995.0;
	  double size = (double)(length.argvalue) - begin;
	  double pos = (double)(n) - begin;
	  double damping = 1.0 - pos / size;
	   // apply the damping factor
	  if (inverse == false)
		sweep[n] *= damping;
	  else
		sweep[(length.argvalue+silenceLength)-n] *= damping;
	}
  
	// write the silence part
	if (silenceLength != 0)
	  for (int i =length.argvalue; i<length.argvalue+silenceLength; i++)
	  {
		if (inverse == false)
		  sweep[i] = 0.0;
		else
		  sweep[(length.argvalue+silenceLength)-i] = 0.0;
	  }
  
	// perform the 6db per octave damping for the inverse signal with a differentation
	if (inverse == true)
	{
	  double[] damped = new double[length.argvalue+silenceLength];
	  for (int i =0; i<((length.argvalue+silenceLength)-1); i++)
		damped[i] = (sweep[i+1] - sweep[i]) / 2.0;
	  damped[(length.argvalue+silenceLength)-1] = 0;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memcpy(sweep, damped, sizeof(double)*(length.argvalue+silenceLength));
	  damped = null;
	}
  
	// write the wave file if required
	if (writeSweepFile == true)
	{
	  // first cast the result back to 16_bit
	  short[] shortIntSweep = new short[length.argvalue+silenceLength];
	  for (int i =0; i<length.argvalue+silenceLength; i++)
		shortIntSweep[i] = (short)((double)(sweep[i]/amplitude)*(double)(SHRT_MAX));
	  // then save wave file
	  GlobalMembersImpulse_response_extraction.clauer.io.WaveFileHandler wfh = new GlobalMembersImpulse_response_extraction.clauer.io.WaveFileHandler();
	  boolean error;
	  if (inverse == false)
		wfh.writeMonoPcm16WaveFile(shortIntSweep, length.argvalue+silenceLength, sampleRate, "sweep.wav", error);
	  else
		wfh.writeMonoPcm16WaveFile(shortIntSweep, length.argvalue+silenceLength, sampleRate, "inverse.wav", error);
	  shortIntSweep = null;
	  sweep = null;
	  return null;
	}
	// give the generated sweep signal back
	else
	  return sweep;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function simple generates a white noise signal for the given length and sample rate. A speacial
//  * flag can be used to extract a linear sweeped signal. For a core sine signal without the sweep set 
//  * the start and end frequency to the same value.
//  *
//  * @param  duration       The white noise signal duration in seconds.
//  * @param  sampleRate     The noise signal sample rate.
//  * @param  amplitude      The amplitude of the generated sinus sweep.
//  * @param  legnth         The reference value will be set to the generated number of samples.
//  * @param  writeSweepFile This flag is per default set to false. If it is set to true, a file named
//  *                        "noise.wav" will be saved to the local file system. The file format is 
//  *                        16 Bit with the desired sample rate.
//  * @param  silence        The length of the additional silence part at the end of the file in seconde.
//  *                        The default value is 0.0 seconds.
//  * @return                The function returns the generated samples. Note that the sample vector is 
//  *                        allocated into the function so no pre allocation is needed. If the
//  *                        writeSweepFile is set the allocated sample vector will be deleted and a
//  *                        void pointer is geven back.
//  

//------------------------------------------------------------------------------------------------

  public static double clauer.generateWhiteNoise(double duration, int sampleRate, double amplitude, RefObject<Integer> length, double silence)
  {
	  return generateWhiteNoise(duration, sampleRate, amplitude, length, silence, false);
  }
  public static double clauer.generateWhiteNoise(double duration, int sampleRate, double amplitude, RefObject<Integer> length)
  {
	  return generateWhiteNoise(duration, sampleRate, amplitude, length, 0.0, false);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double* clauer::generateWhiteNoise(const double duration, const int sampleRate, const double amplitude, int& length, const double silence = 0.0, const boolean writeNoiseFile = false)
  public static double clauer.generateWhiteNoise(double duration, int sampleRate, double amplitude, RefObject<Integer> length, double silence, boolean writeNoiseFile)
{
  // make sure that the input values are in a correct condition
  assert duration > 0;
  assert sampleRate > 0;

  // first initialize the signal allocate array
  length.argvalue = (int)((double)(sampleRate) * duration);
  int silenceLength = (int)((double)(sampleRate) * silence);
  double[] noise = new double[length.argvalue+silenceLength];

  // initialize the random generator
  RandomNumbers.seed(time(null));

  // then generate the signal array
  for (int n =0; n<length.argvalue; n++)
	noise[n] = (double)(RandomNumbers.nextNumber()) / (double)(RAND_MAX) * 2.0 + 1.0;

  // write the silence part
  if (silenceLength != 0)
	for (int i =length.argvalue; i<length.argvalue+silenceLength; i++)
	  noise[i] = 0.0;

  // write the wave file if required
  if (writeNoiseFile == true)
  {
	// first cast the result back to 16_bit
	short[] shortIntNoise = new short[length.argvalue+silenceLength];
	for (int i =0; i<length.argvalue+silenceLength; i++)
	  shortIntNoise[i] = (short)((double)(noise[i]/amplitude)*(double)(SHRT_MAX));
	// then save wave file
	GlobalMembersImpulse_response_extraction.clauer.io.WaveFileHandler wfh = new GlobalMembersImpulse_response_extraction.clauer.io.WaveFileHandler();
	boolean error;
	wfh.writeMonoPcm16WaveFile(shortIntNoise, length.argvalue+silenceLength, sampleRate, "noise.wav", error);
	shortIntNoise = null;
	noise = null;
	return null;
  }

  // give the generated sweep signal back
  else
	return noise;
}

//------------------------------------------------------------------------------------------------



 //------------------------------------------------------------------------------------------------
 
 public static double clauer.deconvolveImpulseResponse(double stimulusTimeReversal, double answer, int length)
 {
	 return deconvolveImpulseResponse(stimulusTimeReversal, answer, length, 1024);
 }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static double* clauer::deconvolveImpulseResponse(const double* stimulusTimeReversal, const double* answer, const int length, const int irSize = 1024)
 public static double clauer.deconvolveImpulseResponse(double stimulusTimeReversal, double answer, int length, int irSize)
 {
   // make sure that
   assert irSize < length;
 
   /////////////////////////////////////
   // convolve both signals
   double[] ir = GlobalMembersImpulse_response_extraction.clauer.math.Convolution.calculateFastConvolution(stimulusTimeReversal, answer, length);
 
   /////////////////////////////////////
   // search for the peak
   double peakLevel = -DBL_MAX;
   int peakPos = -1;
   for (int i =0; i<length; i++)
	 if (peakLevel < Math.abs(ir[i]))
	 {
	   peakPos = i;
	   peakLevel = Math.abs(ir[i]);
	 }
 
   /////////////////////////////////////
   // cut the impulse response arround the peak
   double[] cutIr = new double[irSize];
   int beginIndex = peakPos - irSize/2;
   if (beginIndex > (length - irSize))
	 beginIndex = (length - irSize);
   for (int i =0; i<irSize; i++)
	 cutIr[i] = ir[i+beginIndex];
 
   // waste the grabage
   ir = null;
 
   // give the response back
   return cutIr;
 }

//------------------------------------------------------------------------------------------------

} // class ImpulseResponseExtractor

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_MATH_IMPULSE_RESPONSE_EXTRACTOR
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace math


// C Langunage Library headers

// C++ Langunage Library headers

// Input/Output Stream Library headers

// Stl headers

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------



//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2009 Tangible Software Solutions Inc.
//
//	This class provides the ability to simulate the behavior of the C/C++ functions for 
//	generating random numbers.
//	'rand' converts to the parameterless overload of NextNumber
//	'random' converts to the single-parameter overload of NextNumber
//	'randomize' converts to the parameterless overload of Seed
//	'srand' converts to the single-parameter overload of Seed
//----------------------------------------------------------------------------------------
final class RandomNumbers
{
	private static Random r;

	static int nextNumber()
	{
		if (r == null)
			Seed();

		return r.nextInt();
	}

	static int nextNumber(int ceiling)
	{
		if (r == null)
			Seed();

		return r.nextInt(ceiling);
	}

	static void seed()
	{
		r = new Random();
	}

	static void seed(int seed)
	{
		r = new Random(seed);
	}
}
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