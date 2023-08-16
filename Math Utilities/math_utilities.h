/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    math_utilities.h
 * @class   Utilities
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   Class template file
 * @see     http://en.wikipedia.org/wiki/Logarithm
 * @todo    the implementation here is only a very simple implementation which works only
 *          in the interval [0..+inf]... A more detailed is very hard to implement with an
 *          reference point for the two domains.
 */

//------------------------------------------------------------------------------------------------

#ifndef CLAUER_MATH_MATH_UTILS
#define CLAUER_MATH_MATH_UTILS
 
//------------------------------------------------------------------------------------------------
 
// C Library headers
#include <cmath>
#include <cassert>
#include <cstring>

// Input/Output Stream Library headers (is needed for the memcopy function)
#include <iostream> 
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

//------------------------------------------------------------------------------------------------

#define PI              (3.14159265358979323846)
#define TWO_PI          (2.0*PI)
#define HALF_PI         (PI/2.0)
#define SQRT2           (1.414213562373095)

/**
 * NOTE:  This macro value is used for the default logarithmization base.
 *        For the magnitude spectrum this value should be 10.0, and for the operations
 *        in the power spectrum domain this value should be set to 20.0.
 */
#define DEFAULT_ROOT_POWER_CORRECTION (20.0) // amplitude, voltage, current, pressure and other field sizes 20, energy and power 10

//------------------------------------------------------------------------------------------------
 
/**
 * This class implemets in the first version only a simple logarithmize/delogarithmize 
 * function pair with the reference point arround 1. Number smaller than zero can not be
 * transformed.
 */
class Utilities
{

//------------------------------------------------------------------------------------------------
 
public:

 /**
  * This all in one function takes an array and allocates a new bigger one with a size
  * from the next power of two. The old one will not be destoyed into this function.
  * The data arrays will be copyed from the old one to the new, and the rest of the array will 
  * be initialized with zeros. Note that the caller must take about the destorsion of the input 
  * array.
  * 
  * @param    in    The input array.
  *                 into the this function.
  * @param    len   The length of the input array.
  * @return         The new allocated array with a size form the next power of two from the 
  *                 old size. The old data will be copyed and the rest will be filled with zeros.
  */
  static double* autoZeroPadding(const double* in, const int len, int* newLen)
  {
    // first find the next bigger power of two value
    *newLen = getNextPowerOfTwo(len);
    // next allocate the new array
    double* out = new double[*newLen];
    // the copy the elements from the old to the new array
    memcpy(out, in, sizeof(double) * len);
    // then fill the rest with zeros
    memset(out+len, 0, sizeof(double) * (*newLen - len));
    // and finally give the result back
    return out;
  };

//------------------------------------------------------------------------------------------------

 /**
  * This function simple finds the next bigger power of two value from the given and gives them back.
  *
  * @param  n   The input value.
  * @return     Returns the next bigger powerof two value.
  */
  static int getNextPowerOfTwo(const int value)
  {
    // make sure that 
    assert(value > 0);
    // calc the next power of two
    int n = 1;
    while (n < value)
    {
      n = n << 1; // the short form for the power of two
    }
    // give them baack
    return n;
  };

//------------------------------------------------------------------------------------------------

 /**
  * This function simple checks if the given value is a power of two.
  *
  * @param  x     The value to be evaluated.
  * @return       True is the given value is a power of two.
  */
  static inline bool isPowerOfTwo(const int x)
  {
    if (x < 2)
      return false;
    if (x & (x - 1))             // this is a little bit tricky !!!
      return false;
    return true;
  };

//------------------------------------------------------------------------------------------------

 /**
  * This function calculates the of two for the given number. (2^n)
  *
  * @param  n  The exponent for the base 2.0
  * @return    Returns the power of two.
  */
  static double powerOfTwo(double n)
  {
    return pow(2.0,n);
  }
  static int powerOfTwo(int n)
  {
    return 1 << n;
  }

//------------------------------------------------------------------------------------------------

 /**
  * This function returns the logarithmus dualis for the given value.
  *
  * @param n  The input value.
  * @return   The logarithmus dualis for the given input value.
  */
  static double logarithmusDualis(double n)
  {
    return std::log(n) / std::log(2.0);
  }
  static int logarithmusDualis(int n)
  {
    return static_cast<int>(round(std::log(static_cast<double>(n)) / std::log(2.0)));
  }

//------------------------------------------------------------------------------------------------

 /**
  * A very easy implementation of an delogarithmization with scalable function parameters.
  *
  * @param    log   The imput value in the logarithmic domain.
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param     Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  inline static double log2lin(const double log, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  { 
    return Cref * std::pow(10.0, log / Cpow);
  };
  
//------------------------------------------------------------------------------------------------
 
 /**
  * A very easy implementation of an logarithmization with scalable function parameters.
  *
  * @param    lin   The input value in the linear domain. This value must be greather zero !
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param     Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  * @return         The output value in the logarithmic domain-
  */
  inline static double lin2log(const double lin, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    assert(lin > 0.0);
    return Cpow * std::log10 ( lin / Cref );
  };
 
//------------------------------------------------------------------------------------------------
 
 /**
  * This function has exactely the same functionallity as the one defined above except the fact 
  * that this function operates Inplace which means that the value at the given pointer will be
  * changed. There fore this function has side effects.
  *
  * @param    log   The pointer to the value to be delogarithmized.
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param    Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  static void log2linInPlace(double* log, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    (*log) = Cref * std::pow(10.0, (*log) / Cpow);
  };
  
//------------------------------------------------------------------------------------------------

 /**
  * This function has exactely the same functionallity as the one defined above except the fact 
  * that this function operates Inplace which means that the value at the given pointer will be
  * changed. There fore this function has side effects.
  *
  * @param    lin   The pointer to the value to be logarithmized.
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param    Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  static void lin2logInPlace(double* lin, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    assert((*lin) > 0.0);
    (*lin) = Cpow * std::log10( (*lin) / Cref );
  };
  
//------------------------------------------------------------------------------------------------

 /**
  * This function has exactely the same functionallity as the functions defined above except they
  * work on arrays. Note that this function operates inplace.
  *
  * @param    log     The pointer to the array to be delogarithmized.
  * @param    length  The length of the array
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param    Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  static void log2linArrayInPlace(double* log, int length, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    for (int i=0; i<length; i++)
    {
      log2linInPlace(log + i, Cpow, Cref);
    }
  };

//------------------------------------------------------------------------------------------------

 /**
  * This function has exactely the same functionallity as the functions defined above except they
  * work on arrays. Note that this function operates inplace.
  *
  * @param    lin     The pointer to the array to be logarithmized.
  * @param    length  The length of the array
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param    Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  static void lin2logArrayInPlace(double* lin, int length, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    for (int i=0; i<length; i++)
    {
      lin2logInPlace( lin + i, Cpow, Cref);
    }
  };

//------------------------------------------------------------------------------------------------

 /**
  * This function has exactely the same functionallity as the functions defined above except they
  * work on arrays. Note that this function allocates the newly transformed array for himself.
  *
  * @param    log     The pointer to the array to be delogarithmized.
  * @param    length  The length of the array
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param    Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  static double* log2linArray(double* log, int length, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    double* lin = new double[length];
    for (int i=0; i<length; i++)
      lin[i] = log2lin(log[i], Cpow, Cref);
    return lin;
  }

//------------------------------------------------------------------------------------------------

 /**
  * This function has exactely the same functionallity as the functions defined above except they
  * work on arrays. Note that this function allocates the newly transformed array for himself.
  *
  * @param    lin     The pointer to the array to be logarithmized.
  * @param    length  The length of the array
  * @param    Cpow  The power correction factor Cpow is either 10 or 20 depending of the
  *                 type of the input values. For the mapping of amplitude values the power
  *                 correction factor is 20, and for the mapping of root power quantity values
  *                 this constant has to be 10 (e.g. amplitude, voltage, current, pressure and
  *                 other field sizes 20, energy and power 10).
  * @param    Cref  The second constant defines the reference point for the logarithmization Cref. This
  *                 point is the one element of the mapping, where the zero point of the amplification
  *                 is defined 0dB. This factor defined in the linear scale and is usual set to 1.0.
  *                 The logarithm base is always set to 10.
  */
  static double* lin2logArray(double* lin, int length, const double Cpow = DEFAULT_ROOT_POWER_CORRECTION, const double Cref = 1.0)
  {
    double* log = new double[length];
    for (int i=0; i<length; i++)
      log[i] = lin2log(lin[i], Cpow, Cref);
    return log;
  }

//------------------------------------------------------------------------------------------------
  
 /**
  * The following function group collects functions which apply a window function to the 
  * given array. The functions here implement not any weighted or scaled version of the 
  * window function but simple a single window function. A scaled version of the functions 
  * defined here are available via the Ni Math Library.
  *
  * @see  http://en.wikipedia.org/wiki/Window_function
  */
  
//@{
 /**
  * This inplace function applyes an Hamming window function to the gives array.
  * The Hamming window function is per definition delared as:
  * w(n) = 25/46 - 21/46*cos(2*Pi*n/(N-1))
  *
  * @param  sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyHammingWindow(double* samples, int N)
  {
    for (int n=0; n<N; n++) 
      samples[n] *= (25.0/46.0 - ( 21.0/46.0 * cos( 2.0*PI*(double)(n+0.5) / (double)N )));
  }
   
 /**
  * This inplace function applyes an Hann window function to the gives array.
  * The Hann window function is per definition delared as:
  * w(n) = 0.5*(1-cos(2*Pi*n/(N-1)))
  *
  * @param  sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyHannWindow(double* samples, int N)
  {
    for (int n=0; n<N; n++) 
      samples[n] *= (0.5 - 0.5 * cos( 2.0*PI*(double)(n+0.5) / (double)N ));
  } 

 /**
  * This inplace function applyes a Blackman window function to the gives array.
  *
  * @param  Sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyBlackmanWindow(double* samples, int N)
  {
    for (int n=0; n<N; n++) 
      samples[n] *= (0.42 - ( 0.5 * cos( 2.0*PI*(double)(n) / (double)N ) + 0.08 * cos( 2.0*PI*(double)(n+1) / (double)N) ));
  }
  
 /**
  * This inplace function applyes a Tringle window function to the gives array.
  *
  * @param  Sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyTriangleWindow(double* samples, int N)
  {
    for (int n=0; n<N; n++) 
    {
      if ( n < N/2)
        samples[n] *= ((double)n / (double)N * 2.0);
      else 
        samples[n] *= (2.0 - (double)(n+1) / (double)N * 2.0);
    }
  }

  /**
  * This inplace function applyes a Welch window function to the gives array.
  *
  * @param  Sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyWelchWindow(double* samples, int N)
  {
    for (int n=0; n<N; n++) 
      samples[n] *= (1.0 - pow( ((double)(n+0.5) - (double)N / 2.0) / ((double)N / 2.0), 2.0));
  }

 /**
  * This inplace function applyes an Gauss window function to the gives array.
  *
  * @param  sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyGaussWindow(double* samples, int N)
  {
    double rho = 1.0 / 3.0;
    for (int n=0; n<N; n++) 
      samples[n] *= (exp( -0.5 * pow( ((double)(n+0.5)-(double)N/2.0) / (rho*(double)N/2.0), 2.0)));
  }
  
 /**
  * This inplace function applyes an Cosine window function to the gives array.
  *
  * @param  sampels     A pointer to the input samples. Note that the window function is applyed
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input samples.
  */
  static void applyCosineWindow(double* samples, int N)
  {
    for (int n=0; n<N; n++) 
      samples[n] *= (cos( PI*(double)(n) / ((double)N-1.0) - PI/2));
  }
  
 /**
  * This inplace function apllyes an Asynchron Expoantion Window function to the given array.
  *  
  * @param  samples     A Pointer to the input samples. Note that the window function is applyed 
  *                     in place which means that no further vector is allocated or given back.
  * @param  nSamples    The number of input sampels.
  * @param  asymFact    This factor influences the unsymetrical side relation from one side to another.
  *                     The value value should be more/less in the intervall [0.8...1.5]. A typical value is 1.0
  */  
  static void applyAsymetricalExponentialWindow(double* samples, int nSamples, double asymFact = 1.0)
  {
    // first prepare som values
    double cosineFrequency = TWO_PI / nSamples;
    double alpha = log(asymFact);
    double max = 0.0;
    double* asymExpoWin = new double[nSamples];
    
    // DEFAULT_ROOT_POWER_CORRECTIONte the window function here
    for(int i=0; i<nSamples; i++)
      {
        double d_i = static_cast<double>(i);
      	asymExpoWin[i] = 0.5 * (1.0 - cos(cosineFrequency * (d_i))) * (d_i)*  exp(alpha *(d_i)) ;
      	if(asymExpoWin[i] > max)
          max = asymExpoWin[i];
      }
      
    // normalize and applay the window
    for(int i=0; i<nSamples; i++) 
      samples[i] *= asymExpoWin[i]/=max;
    
    // empty the waste
    delete[] asymExpoWin;
  }
//@}
  
//------------------------------------------------------------------------------------------------
  
 /**
  * Like the name says this function rounds the given value up or down. This function makes use of 
  * the standart C math library rounding functions.
  *  
  * @param  value  The input value which needs to be rounded.
  * @return        Returns the rounded value of the input value.
  */  
  static double round(double value)
  {
    if(value-std::floor(value) >= 0.5)
      return std::ceil(value);
    else
      return std::floor(value);
  } 
  
//------------------------------------------------------------------------------------------------

 /**
  * This function will extarct the file name from the the given file path string.
  * 
  * @param      The input string char path to look for the last file.
  * @return     This function returns the file path.
  */
  static char* extractFileNameFromPath(char* path)
  {
    if (strrchr(path,'/') != NULL)
      return strrchr(path,'/') + 1 ;

    if (strrchr(path,'\\') != NULL)
      return strrchr(path,'\\') + 1;
      
    return path;
  } 
 
}; // class Logarithmize
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_MATH_MATH_UTILS
