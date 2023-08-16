/**
 * This simple test Program illustrates the usage of the polygonal chain algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "polygonal_chain.hpp"

//--------------------------------------------------------------------------------------

#define INPUT_LENGTH 512

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // foirst the greeting screen
  std::cout << "Hallo Polygonal Chain" << std::endl;
  
  // first generate the input array
  double* a = new double[INPUT_LENGTH];
  for (int i=0; i<INPUT_LENGTH;i++)
  {
    a[(int)i] = std::sin((double)i*10.0);
  }
  
  // call the algorithm
  double pc = clauer::math::PolygonalChain<double>::polygonalChain(a, INPUT_LENGTH);
  
  // print the result to the std::out
  std::cout << "PolygonalChain = " << pc << std::endl;
  std::cout << "RelativePolygonalChain = " << pc/static_cast<double>(INPUT_LENGTH) << std::endl;
}

//--------------------------------------------------------------------------------------
