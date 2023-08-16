/**
 * This simple test Program illustrates the usage of the zero crossing rate algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "zero_crossing_rate.hpp"

//--------------------------------------------------------------------------------------

#define INPUT_LENGTH 512

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // print the greeting string
  std::cout << "Hallo Zero Crossing Rate" << std::endl;
  
  // first generate the input array
  double* a = new double[INPUT_LENGTH];
  for (int i=0; i<INPUT_LENGTH;i++)
  {
    a[i] = std::sin(static_cast<double>(i)*.1);
    std::cout << "Signal(" << i << ") = " << a[i] << std::endl;
  }
  
  // call the core algorithm
  int zcr = clauer::math::ZeroCrossingRate<double>::calcZeroCrossingRate(a, INPUT_LENGTH);
  
  // print the result to the std::out
  std::cout << "Zero Crossing Rate: " << zcr << std::endl;
  std::cout << "Normalized Zero Crossing Rate: " << static_cast<double>(zcr)/static_cast<double>(INPUT_LENGTH) << " per sample" << std::endl;
  
}

//--------------------------------------------------------------------------------------
