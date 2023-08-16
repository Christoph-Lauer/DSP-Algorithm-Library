/**
 * This simple test Program illustrates the usage of the normalization routines
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "normalizer.h"

//--------------------------------------------------------------------------------------

#define INPUT_LENGTH 1000000

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  std::cout << "Hallo Normalization" << std::endl;
  // first generate the input array
  double* a = new double[INPUT_LENGTH];
  for (int i=0; i<INPUT_LENGTH;i++)
  {
    a[(int)i] = std::sin(static_cast<double>(i));
  }
  clauer::math::Normalizer::normalizePeak(a, INPUT_LENGTH, 1.0);
  clauer::math::Normalizer::normalizePeak(a, INPUT_LENGTH, 1.0);
  clauer::math::Normalizer::normalizeAvg(a, INPUT_LENGTH, 1.0);
  clauer::math::Normalizer::normalizeAvg(a, INPUT_LENGTH, 1.0);
  clauer::math::Normalizer::normalizeRms(a, INPUT_LENGTH, 1.0);
  clauer::math::Normalizer::normalizeRms(a, INPUT_LENGTH, 1.0);
  clauer::math::Normalizer::normalizeInterval(a, INPUT_LENGTH, 11.0, 21.0);
  clauer::math::Normalizer::normalizeInterval(a, INPUT_LENGTH, 11.0, 21.0);
}

//--------------------------------------------------------------------------------------
