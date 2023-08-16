/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    envelope.h
 * @class   Envelope
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This function implements a basic envelope calculation based on a hilbert transformer.
 * @see     http://www.numerix-dsp.com/envelope.html
 * @todo    finished and tested so far.
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_ENVELOPE
#define CLAUER_MATH_ENVELOPE
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * This class implements a envelope calculation based on the hilbert transformation. More details about 
 * the implementation can be found into the function descriptions. The Hilbert transform works
 * best with AC coupled, band-limited signals. To perform an inverste Hilbert Transformation can be 
 * computed with the aid of the Hilbert Transformation and post negation. Beacause our implemenation
 * is based on the FFT we can say thast the performance of the Hilbert Transformation is blindingly 
 * fast. The speed should be a little bit more than the 2*FFT time --> approximately 2*N*log(N)+rest
 * for lengths from a power of two. This implementation takes every length of input vectors and does 
 * not depend on lengts from a power of two. Because we implemented this algorithms by  hand this 
 * collection of the hilbert and the envelope calculation benifits heavyly from the copmpiler
 * optimizations so we advise for perfomance purposes to compile this code with the compiler optimization 
 * "-O3", because we are not sure to make the best perfomant implementation here.
 */
class Envelope
{

public:

//-------------------------------------------------------------------------------------------------

/// this enummeration specifies both calculation methods for spectrum envelope
enum ENVELOPE_CALCULATION_METHOD
{
  /// For this method the signal will be decomutated and the max taken from the hilbert and signal
  ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE,
  /// For this method the absolute value from both will be shifted trough a FIR filter
  ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS
};

//-------------------------------------------------------------------------------------------------
  
 /**
  * This function calculates the envelope of a given time domain input signal with two 
  * different methos derived from the hilbert transfromation. Both methods build first the
  * DC absolute value of the so called analytic function from the given input signal. The calcualtion
  * of the analytic function has a need for the hilbert transformation phase shifter which is 
  * also implemented into this class. After the analytic function is calcualted two methods 
  * are implemented for the envelope calculation:
  * 1.) The second method calcualtes simple the absolute value of the analytic signal sqrt(h^1+x^2)
  * 2.) The first method build the max of them and allpies an simple FIR low pass.
  * The calculation method can be switched with the "method" flag.
  * 
  * @param    inputSignal      This is a pointer to the input signal.
  * @param    length           This value specifies the length of the input vector.
  * @param    method           This enummeration flag specifies the mwthod used for the calculation 
  *                            of the nevelope.
  * @param    firLowPassAlpha  For the FIR filtered method this value specifies the filter constant 
  *                            used to smooth the signal. A higher value results a higher low pass 
  *                            filtering. The value should be in the intervall from 0...1. If the
  *                            default vale from -1.0 is given the algorithm try to fid the best alpha
  *                            value depending on the length of the input vector which wrks ver good.
  * @return                    The return value of the calculation is the signal envelope. Note that
  *                            the result vector of this function will be allocated into this function
  *                            so no pre allocation is needed.
  */
  static double* calculateEnvelope(const double* inputSignal, const int length, const ENVELOPE_CALCULATION_METHOD method, double firLowPassAlpha = -1.0);
  
//------------------------------------------------------------------------------------------------

/**
 * This method applies a standart FIR low pass Filter corresponding the formular
 * y(n) = ( x(n) + alpha*y(n-1) ), where alpha is in the intervall ]0..1] and has a typical
 * value of 0.97. The low pass filtered signal will be allocated into this fucntion
 * so no pre allocation of the output signal is needed. The legnth of the output signal
 * is the same as the length of the input signal. A higher alpha value corresponds to more filter effect.
 *
 * @param   input   This value points to the input signal.
 * @param   length  Ths is simpel the length if the input signal.
 * @param   alpha   This value prepresents the FIR filter coefficcient. The value should be in the 
 *                  interval from 0...1. An alpha from 0 does not filter anything an a value of 1
 *                  fully enables the low pass filter. The default value of 0.97 cames from the speech
 *                  recognition.
 * @return          The result of the calculation is the low pass filtered vector of the input signal.
 */
 static double* applyFirLowPass(const double* input, const int length, const double alpha = 0.97);

//------------------------------------------------------------------------------------------------

 /**
  * This function calculates the hilbert transformation for the given input sample vector. 
  * We decide to use the FFT hilbert transformation where the given input signal will be first 
  * transformed in the complex frequency domain. We apply the hilbert transformation by  shift the 
  * signal in the frequency doamin by 90 degrees while applying the hilbert transformation formular:
  * H(X(jw)) = -j * sign (w) * X(jw). The corresponding result will be back transformed into 
  * the time domain and given back as result. Note that we implement a FFT and a DFT version 
  * of the Hilbert Transformation, so the algorithm works blindinly fast with verctor lengts from
  * a power of two and also with other lengths. For a length which is not a power of two the 
  * algorithm get slow because the use of the DFT instead of the FFT.
  * 
  * @param    timeDomain     The time domain input Signal.
  * @param    length         The length of the input signal. The length should be have a length
  *                          from a power of two.
  * @return   hilbertDomain  The hilbert domain output signal. Note that the output vector
  *                          will be allocated into this function and must not explicitely 
  *                          preallocated. The length of the vector is the same as the 
  *                          length of the input signal.
  *                   
  */
  static double* fastHilbertTransform(const double* timeDomain, const int length);

//------------------------------------------------------------------------------------------------

}; // class Envelope
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_ENVELOPE
