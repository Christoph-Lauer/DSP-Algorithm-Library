/**
 * This simple test Program illustrates the usage of the wavelet algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "wavelet_transformation.h"

// transformation length
#define TRANSFORMATION_LENGTH 128

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  std::cout << "Hallo Wavelet";
  // first generate the input array
  double* a = new double[TRANSFORMATION_LENGTH];
  for (int i=0; i<TRANSFORMATION_LENGTH;i++)
  {
    a[(int)i] = std::sin((double)i)*1000.0;
  }
  
  // call the wavelet transformation
  char wlType  ='C';
  int  wlOrder = 30;
  double* b = clauer::math::WaveletTransformation::doWaveletTransformation(a, &wlType, wlOrder, TRANSFORMATION_LENGTH);
  
  // output the input and result
  for (int i=0; i<TRANSFORMATION_LENGTH; i++)
  {
    std::cout << i << ":\t" << a[i] << "--> \t" << b[i] << std::endl;
  }

}

//--------------------------------------------------------------------------------------
