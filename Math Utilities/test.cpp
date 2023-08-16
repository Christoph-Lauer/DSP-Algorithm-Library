/**
 * This simple test Program illustrates the usage of the lUtilities.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>
#include <cfloat>

// local headers  
#include "math_utilities.h"

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  std::cout << "Hallo Utilities" << std::endl;
  // first generate the input array
  for (double i = 0.00001; i < 100000.0; i *= sqrt(sqrt(10.0)))
  {
    double log = clauer::math::Utilities::lin2log(i);
    double lin = clauer::math::Utilities::log2lin(log);
    std::cout << "lin: " << i << "\t--> log:" << log << "db\t and back to lin  --> " << lin << std::endl;
  }
  std::cout << std::endl;
  
  // some numbers
  double lin = 2;
  double log = clauer::math::Utilities::lin2log(lin);
  std::cout << lin << " --> " << log << "db" << std::endl;

  lin = 10;
  log = clauer::math::Utilities::lin2log(lin);
  std::cout << lin << " --> " << log << "db" << std::endl;

  log = 3;
  lin = clauer::math::Utilities::log2lin(log);
  std::cout << log << "db --> " << lin << std::endl;

  log = 1;
  lin = clauer::math::Utilities::log2lin(log);
  std::cout << log << "db --> " << lin << std::endl;

  // test the power of two extender
  for (int i=13; i<18; i++)
  {
    int next = clauer::math::Utilities::getNextPowerOfTwo(i);
    std::cout << i << " --> " << next << std::endl;
  }
  
  // test the zero padder
  std::cout << std::endl << "Now test the zero padder, extend an array with 10 elements to the next bigger power of 2 size" << std::endl;
  double in[] = {1.0, 2.0 ,3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
  int newLen = 0;
  double* out = clauer::math::Utilities::autoZeroPadding(in, 10, &newLen);
  std::cout << "Array extended to a size of " << newLen << std::endl;
  for (int i=0; i<newLen; i++)
  {
    std::cout << "Array [" << i << "] --> " << out[i] << std::endl;
  }
  
  // test the inplace operation for the logarithmization
  std::cout << std::endl << "Test the InPlace operations" << std::endl;
  double value = 2.0;
  std::cout << "Value = " << value << std::endl;
  clauer::math::Utilities::lin2logInPlace(&value);
  std::cout << "Logarithmize = " << value << std::endl;
  clauer::math::Utilities::log2linInPlace(&value);
  std::cout << "back to Lin = " << value << std::endl;
   
  // test the array logarithmization
  std::cout << std::endl << "Test the inplace array logarithmization" << std::endl;
  double rgd_lin[9] = {DBL_MIN, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
  double rgd_log[9] = {DBL_MIN, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
  clauer::math::Utilities::lin2logArrayInPlace(rgd_lin, 9);
  clauer::math::Utilities::log2linArrayInPlace(rgd_log, 9);
  for (int i=0;i<9; i++)
    std::cout << "LOG: " << rgd_log[i] << "dB \tREAL" << i << "\tLIN: " << rgd_lin[i] << std::endl;
  std::cout << std::endl << "Another test for the inplace array logarithmization" << std::endl;
  double* linlog = new double[100];
  double* loglin = new double[100];
  for (int i=0; i < 100; i++)
  {
    double real = static_cast<double>(i) / 10.0;
    if (real == 0.0) real = DBL_MIN;
    linlog[i] = real;
    loglin[i] = real;
  }
  clauer::math::Utilities::lin2logArrayInPlace(linlog, 100);
  clauer::math::Utilities::log2linArrayInPlace(loglin, 100);
  for (int i=0; i< 100; i++)
  {
    double real = static_cast<double>(i)/10.0;
    if (real == 0.0) real = DBL_MIN;
    double log = real;
    double lin = real;
    clauer::math::Utilities::lin2logInPlace(&log);
    clauer::math::Utilities::log2linInPlace(&lin);
    std::cout << "LOG: " << linlog[i] << "dB \tREAL: " << real << " \tLIN " << loglin[i] << std::endl;
    std::cout << "LOG: " << log << "dB \tREAL: " << real << " \tLIN " << lin << std::endl;
  }
  
  // tests the window functions
  std::cout << std::endl;
  double hamming_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyHammingWindow(hamming_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Hamming_even[" << i << "] =" << hamming_even[i] << std::endl;
  std::cout << std::endl;
  double hamming_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyHammingWindow(hamming_odd, 17);
  for (int i=0; i<17; i++)
    std::cout << "Hamming_odd[" << i << "] =" << hamming_odd[i] << std::endl;

  std::cout << std::endl;
  double hann_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyHannWindow(hann_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Hann_even[" << i << "] =" << hann_even[i] << std::endl;
  std::cout << std::endl;
  double hann_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyHannWindow(hann_odd, 17);
  for (int i=0; i<17; i++)
    std::cout << "Hann_odd[" << i << "] =" << hann_odd[i] << std::endl;

  std::cout << std::endl;
  double blackman_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyBlackmanWindow(blackman_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Blackman_even[" << i << "] =" << blackman_even[i] << std::endl;
  std::cout << std::endl;
  double blackman_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyBlackmanWindow(blackman_odd, 17);
  for (int i=0; i<17; i++)
    std::cout << "Blackman_odd[" << i << "] =" << blackman_odd[i] << std::endl;
  
  std::cout << std::endl;
  double triangle_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyTriangleWindow(triangle_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Triangle_even[" << i << "] =" << triangle_even[i] << std::endl;
  std::cout << std::endl;
  double triangle_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyTriangleWindow(triangle_odd, 16);
  for (int i=0; i<17; i++)
    std::cout << "Triangle_odd[" << i << "] =" << triangle_odd[i] << std::endl;

  std::cout << std::endl;
  double welch_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyWelchWindow(welch_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Welch_even[" << i << "] =" << welch_even[i] << std::endl;
  std::cout << std::endl;
  double welch_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyWelchWindow(welch_odd, 17);
  for (int i=0; i<16; i++)
    std::cout << "Welch_odd[" << i << "] =" << welch_odd[i] << std::endl;

  std::cout << std::endl;
  double gauss_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyGaussWindow(gauss_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Gauss_even[" << i << "] =" << gauss_even[i] << std::endl;
  std::cout << std::endl;
  double gauss_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyGaussWindow(gauss_odd, 17);
  for (int i=0; i<17; i++)
    std::cout << "Gauss_odd[" << i << "] =" << gauss_odd[i] << std::endl;
  
  std::cout << std::endl;
  double cosine_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyCosineWindow(cosine_even, 16);
  for (int i=0; i<16; i++)
    std::cout << "Cosine[" << i << "] =" << cosine_even[i] << std::endl;
  std::cout << std::endl;
  double cosine_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyCosineWindow(cosine_odd, 17);
  for (int i=0; i<17; i++)
    std::cout << "Cosine[" << i << "] =" << cosine_odd[i] << std::endl;
  
  std::cout << std::endl;
  double asym_even[16] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyAsymetricalExponentialWindow(asym_even, 16, 1.0);
  for (int i=0; i<16; i++)
    std::cout << "AsymExpo[" << i << "] =" << asym_even[i] << std::endl;
  std::cout << std::endl;
  double asym_odd[17] = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  clauer::math::Utilities::applyAsymetricalExponentialWindow(asym_odd, 17, 1.0);
  for (int i=0; i<17; i++)
    std::cout << "AsymExpo[" << i << "] =" << asym_odd[i] << std::endl;
  
  // testing the round function
  std::cout << "round(6.499)=" << clauer::math::Utilities::round(6.499) << std::endl; 
  std::cout << "round(6.500)=" << clauer::math::Utilities::round(6.500) << std::endl; 
  std::cout << "round(6.501)=" << clauer::math::Utilities::round(6.501) << std::endl; 
  std::cout << "round(6.999)=" << clauer::math::Utilities::round(6.999) << std::endl; 
  std::cout << "round(7.000)=" << clauer::math::Utilities::round(7.000) << std::endl; 
  std::cout << "round(7.001)=" << clauer::math::Utilities::round(7.001) << std::endl; 
  std::cout << "round(7.499)=" << clauer::math::Utilities::round(7.499) << std::endl; 
  std::cout << "round(7.500)=" << clauer::math::Utilities::round(7.500) << std::endl; 
  std::cout << "round(7.501)=" << clauer::math::Utilities::round(7.501) << std::endl; 
  
  double d_n  = 7.0;
  double d_p2 = clauer::math::Utilities::powerOfTwo(d_n);
  double d_ld = clauer::math::Utilities::logarithmusDualis(d_p2);
  std::cout << std::endl << "powerOfTwo(" << d_n << ")=" << d_p2 << std::endl;
  std::cout << "logarithmusDualis(" << d_p2 << ")=" << d_ld << std::endl;

  d_n  = 7.4;
  d_p2 = clauer::math::Utilities::powerOfTwo(d_n);
  d_ld = clauer::math::Utilities::logarithmusDualis(d_p2);
  std::cout << std::endl << "powerOfTwo(" << d_n << ")=" << d_p2 << std::endl;
  std::cout << "logarithmusDualis(" << d_p2 << ")=" << d_ld << std::endl;

  int i_n  = 0;
  int i_p2 = clauer::math::Utilities::powerOfTwo(i_n);
  int i_ld = clauer::math::Utilities::logarithmusDualis(i_p2);
  std::cout << std::endl << "powerOfTwo(" << i_n << ")=" << i_p2 << std::endl;
  std::cout << "logarithmusDualis(" << i_p2 << ")=" << i_ld << std::endl;

  i_n  = 1;
  i_p2 = clauer::math::Utilities::powerOfTwo(i_n);
  i_ld = clauer::math::Utilities::logarithmusDualis(i_p2);
  std::cout << std::endl << "powerOfTwo(" << i_n << ")=" << i_p2 << std::endl;
  std::cout << "logarithmusDualis(" << i_p2 << ")=" << i_ld << std::endl;

  i_n  = 7;
  i_p2 = clauer::math::Utilities::powerOfTwo(i_n);
  i_ld = clauer::math::Utilities::logarithmusDualis(i_p2);
  std::cout << std::endl << "powerOfTwo(" << i_n << ")=" << i_p2 << std::endl;
  std::cout << "logarithmusDualis(" << i_p2 << ")=" << i_ld << std::endl;

  i_n  = 16;
  i_p2 = clauer::math::Utilities::powerOfTwo(i_n);
  i_ld = clauer::math::Utilities::logarithmusDualis(i_p2);
  std::cout << std::endl << "powerOfTwo(" << i_n << ")=" << i_p2 << std::endl;
  std::cout << "logarithmusDualis(" << i_p2 << ")=" << i_ld << std::endl;

  std::cout << "Next test is the power of Two test" << std::endl;
  std::cout << "isPowerOfTwo(128)" << clauer::math::Utilities::isPowerOfTwo(128) << std::endl;
  std::cout << "isPowerOfTwo(129)" << clauer::math::Utilities::isPowerOfTwo(129) << std::endl;

}

//--------------------------------------------------------------------------------------
