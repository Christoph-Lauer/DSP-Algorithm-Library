/**
 * This simple test Program illustrates the usage of the fast-forurier algorithmus.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <iomanip>
#include <cmath>
#include <cstring>

// local headers  
#include "fourier_transformation.h"

//--------------------------------------------------------------------------------------

#define INPUT_LENGTH 32

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  std::cout << "Hallo FourierTransformation\n";
  
  // first generate the input array
  double* a = new double[INPUT_LENGTH];
  double* c = new double[INPUT_LENGTH];
  double* d = new double[INPUT_LENGTH];
  double* e = new double[INPUT_LENGTH];
  double* f = new double[INPUT_LENGTH];
  // initialize the input values
  for (int i=0; i<INPUT_LENGTH;i++)
  {
    a[i] = std::sin((double)i);
    c[i] = 1.0;
    d[i] = 0.0;
    e[i] = std::exp((double)i);
    if (i == INPUT_LENGTH/4)
      f[i] = 1.0;
    else
      f[i] = std::sin(static_cast<double>(i-INPUT_LENGTH/4))/static_cast<double>(i-INPUT_LENGTH/4);

  }
  d[INPUT_LENGTH/2] = 1.0;
  
  ////////////////////////////////////////////
  // The first test is the POWERSPECTRUM
  
  // now extract the power spectrum
  double* b = new double[INPUT_LENGTH / 2];
  std::cout << "Test the powerspectrum algorithm with a sine input signal\n";
  clauer::math::FourierTransformation::PowerSpectrum(INPUT_LENGTH, a, b);
  for (int i=0; i<INPUT_LENGTH/2; i++)
    std::cout << i << ":\t" << a[i] << "--> \t" << b[i] << std::endl;
  std::cout << "Test the powerspectrum algorithm with a DC input signal\n";
  clauer::math::FourierTransformation::PowerSpectrum(INPUT_LENGTH, c, b);
  for (int i=0; i<INPUT_LENGTH/2; i++)
    std::cout << i << ":\t" << c[i] << "--> \t" << b[i] << std::endl;
    
  ////////////////////////////////////////////
  // The second test is the COMPLEX SPECTRUM
  
  // first set the numbers output format strings
  std::cout.precision(20);
  std::cout << std::fixed;
  std::cout << std::showpos;
  
  // first initialize the input values
  double* realIn  = a;
  double* imagIn  = new double[INPUT_LENGTH];
  double* realOut = new double[INPUT_LENGTH];
  double* imagOut = new double[INPUT_LENGTH];
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  // call the complex fft here for the sin input signal
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  std::cout << "\nThe next test is the Complex FFT with a sine input signal.\n";
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT=(" << i << ") = [" << realOut[i] << ", " << imagOut[i] << "]" << "\t\t|FFT("<< i << ")| = " << std::sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]) << std::endl;
    
  // next test the DC input fft
  for (int i=0; i<INPUT_LENGTH; i++)
    realIn[i] = 1.0;
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  std::cout << "\nThe next test is the Complex FFT with a REAL-DC input signal.\n";
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT=(" << i << ") = [" << realOut[i] << ", " << imagOut[i] << "]" << "\t\t|FFT("<< i << ")| = " << std::sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]) << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    imagIn[i] = 1.0;
  memset(realIn, 0, INPUT_LENGTH * sizeof(double));
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  std::cout << "\nThe next test is the Complex FFT with a IMAGINARY-DC input signal.\n";
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT=(" << i << ") = [" << realOut[i] << ", " << imagOut[i] << "]" << "\t\t|FFT("<< i << ")| = " << std::sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]) << std::endl;
    
  // FFT and back transformation
  
  // first init the input values
  std::cout << "\nNext look if the FFT back transformed signal doesn't have any changes.\n";
  realIn = d;
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  // do the FFT
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  // transform back
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, true, realOut, imagOut, realIn, imagIn);
  // show the result in the console
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " IFFT(FFT)=(" << i << ") = [" << realIn[i] << ", " << imagIn[i] << "]" << "\t\t|FFT("<< i << ")| = " << std::sqrt(realIn[i]*realIn[i]+imagIn[i]*imagIn[i]) << std::endl;
  
  /////////////////////////////////////////////////////////////
  // last test is the discrete fourier transformation

  for (int i=0; i<INPUT_LENGTH;i++)
  {
    a[i] = std::sin((double)i);
    c[i] = 1.0;
    d[i] = 0.0;
  }
  d[INPUT_LENGTH/2] = 1.0;
  realIn = a;
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  std::cout << "\nThe next test is the Complex DFT with a sine input signal.\n";
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " DFT=(" << i << ") = [" << realOut[i] << ", " << imagOut[i] << "]" << "\t\t|DFT("<< i << ")| = " << std::sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]) << std::endl;
  std::cout << "\nNext look if the DFT back transformed signal doesn't have any changes.\n";
  realIn = d;
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  // do the DFT
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, false ,realIn, imagIn, realOut, imagOut);
  // transform back
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, true, realOut, imagOut, realIn, imagIn);
  // show the result in the console
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " IDFT(DFT)=(" << i << ") = [" << realIn[i] << ", " << imagIn[i] << "]" << "\t\t|DFT("<< i << ")| = " << std::sqrt(realIn[i]*realIn[i]+imagIn[i]*imagIn[i]) << std::endl;
  
  // compare the result from the fft with the results from the dft
  std::cout << "\nCompare the FFT with the DFT signal with a exponential signal.\n";
  realIn = e;
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  double* realOut1 = new double[INPUT_LENGTH];
  double* imagOut1 = new double[INPUT_LENGTH];
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, false, realIn, imagIn, realOut1, imagOut1);
  // show the result in the console
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT=(" << i << ") = [" << realOut[i] << ", " << imagOut[i] << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << "DFT=(" << i << ") = [" << realOut1[i] << ", " << imagOut1[i] << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT - DFT=(" << i << ") = [" << (realOut[i]-realOut1[i]) << ", " << (imagOut[i]-imagOut1[i]) << std::endl;

  // compare the result from the fft with the results from the dft
  std::cout << "\nCompare the FFT with the DFT signal with an unsymetrical sinc signal.\n";
  realIn = f;
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, false, realIn, imagIn, realOut1, imagOut1);
  // show the result in the console
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT=(" << i << ") = [" << realOut[i] << ", " << imagOut[i] << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << "DFT=(" << i << ") = [" << realOut1[i] << ", " << imagOut1[i] << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT - DFT=(" << i << ") = [" << (realOut[i]-realOut1[i]) << ", " << (imagOut[i]-imagOut1[i]) << std::endl;

  // compare the result from the fft with the results from the dft
  std::cout << "\nCompare the FFT with the DFT signal with the back transformation.\n";
  double* realIn1 = new double[INPUT_LENGTH];
  double* imagIn1 = new double[INPUT_LENGTH];
  for (int i=0; i<INPUT_LENGTH; i++)
  {
    realIn[i] = f[i];
    realIn1[i] = f[i];
  } 
  memset(imagIn, 0, INPUT_LENGTH * sizeof(double));
  imagIn[10] = 1.0; 
  memset(imagIn1, 0, INPUT_LENGTH * sizeof(double));
  imagIn1[10] = 1.0; 
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
  clauer::math::FourierTransformation::FFT(INPUT_LENGTH, true, realOut, imagOut, realIn, imagIn);
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, false, realIn1, imagIn1, realOut1, imagOut1);
  clauer::math::FourierTransformation::DFT(INPUT_LENGTH, true, realOut1, imagOut1, realIn1, imagIn1);
  // show the result in the console
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT=(" << i << ") = [" << realIn[i] << ", " << imagIn[i] << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << "DFT=(" << i << ") = [" << realIn1[i] << ", " << imagIn1[i] << std::endl;
  for (int i=0; i<INPUT_LENGTH; i++)
    std::cout << " FFT - DFT=(" << i << ") = [" << (realIn[i]-realIn1[i]) << ", " << (imagIn[i]-imagIn1[i]) << "]" << std::endl;

  // waste the temporary arrays
  delete[] a;
  delete[] b; 
  delete[] c; 
  delete[] d; 
  delete[] e; 
  delete[] imagIn;
  delete[] realOut;
  delete[] imagOut;
}

//--------------------------------------------------------------------------------------
