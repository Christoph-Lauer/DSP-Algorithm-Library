public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the fast-forurier algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define INPUT_LENGTH 32

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo FourierTransformation\n");

	  // first generate the input array
	  double[] a = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] c = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] d = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] e = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] f = new double[DefineConstantsTest.INPUT_LENGTH];
	  // initialize the input values
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH;i++)
	  {
		a[i] = Math.sin((double)i);
		c[i] = 1.0;
		d[i] = 0.0;
		e[i] = Math.exp((double)i);
		if (i == DefineConstantsTest.INPUT_LENGTH/4)
		  f[i] = 1.0;
		else
		  f[i] = Math.sin((double)(i-DefineConstantsTest.INPUT_LENGTH/4))/(double)(i-DefineConstantsTest.INPUT_LENGTH/4);

	  }
	  d[DefineConstantsTest.INPUT_LENGTH/2] = 1.0;

	  ////////////////////////////////////////////
	  // The first test is the POWERSPECTRUM

	  // now extract the power spectrum
	  double[] b = new double[DefineConstantsTest.INPUT_LENGTH / 2];
	  System.out.print("Test the powerspectrum algorithm with a sine input signal\n");
	  clauer.math.FourierTransformation.PowerSpectrum(DefineConstantsTest.INPUT_LENGTH, a, b);
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH/2; i++)
		{
		System.out.print(i);
		System.out.print(":\t");
		System.out.print(a[i]);
		System.out.print("--> \t");
		System.out.print(b[i]);
		System.out.print("\n");
		}
	  System.out.print("Test the powerspectrum algorithm with a DC input signal\n");
	  clauer.math.FourierTransformation.PowerSpectrum(DefineConstantsTest.INPUT_LENGTH, c, b);
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH/2; i++)
		{
		System.out.print(i);
		System.out.print(":\t");
		System.out.print(c[i]);
		System.out.print("--> \t");
		System.out.print(b[i]);
		System.out.print("\n");
		}

	  ////////////////////////////////////////////
	  // The scond test is the COMPLEX SPECTRUM

	  // first set the numbers output format strings
//C++ TO JAVA CONVERTER TODO TASK: The cout 'showpos' manipulator is not converted by C++ to Java Converter:
//ORIGINAL LINE: std::cout << std::showpos;

	  // first initialize the input values
	  double[] realIn = a;
	  double[] imagIn = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] realOut = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] imagOut = new double[DefineConstantsTest.INPUT_LENGTH];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  // call the complex fft here for the sin input signal
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  System.out.printf("%.20f", "\nThe next test is the Complex FFT with a sine input signal.\n");
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut[i]);
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\t\t|FFT(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ")| = ");
		System.out.printf("%.20f", Math.sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]));
		System.out.printf("%.20f", "\n");
		}

	  // next test the DC input fft
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		realIn[i] = 1.0;
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  System.out.printf("%.20f", "\nThe next test is the Complex FFT with a REAL-DC input signal.\n");
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut[i]);
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\t\t|FFT(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ")| = ");
		System.out.printf("%.20f", Math.sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]));
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		imagIn[i] = 1.0;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(realIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  System.out.printf("%.20f", "\nThe next test is the Complex FFT with a IMAGINARY-DC input signal.\n");
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut[i]);
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\t\t|FFT(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ")| = ");
		System.out.printf("%.20f", Math.sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]));
		System.out.printf("%.20f", "\n");
		}

	  // FFT and back transformation

	  // first init the input values
	  System.out.printf("%.20f", "\nNext look if the FFT back transformed signal doesn't have any changes.\n");
	  realIn = d;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  // do the FFT
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  // transform back
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, true, realOut, imagOut, realIn, imagIn);
	  // show the result in the console
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " IFFT(FFT)=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realIn[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagIn[i]);
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\t\t|FFT(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ")| = ");
		System.out.printf("%.20f", Math.sqrt(realIn[i]*realIn[i]+imagIn[i]*imagIn[i]));
		System.out.printf("%.20f", "\n");
		}

	  /////////////////////////////////////////////////////////////
	  // last test is the discrete fourier transformation

	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH;i++)
	  {
		a[i] = Math.sin((double)i);
		c[i] = 1.0;
		d[i] = 0.0;
	  }
	  d[DefineConstantsTest.INPUT_LENGTH/2] = 1.0;
	  realIn = a;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  System.out.printf("%.20f", "\nThe next test is the Complex DFT with a sine input signal.\n");
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut[i]);
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\t\t|DFT(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ")| = ");
		System.out.printf("%.20f", Math.sqrt(realOut[i]*realOut[i]+imagOut[i]*imagOut[i]));
		System.out.printf("%.20f", "\n");
		}
	  System.out.printf("%.20f", "\nNext look if the DFT back transformed signal doesn't have any changes.\n");
	  realIn = d;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  // do the DFT
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, false,realIn, imagIn, realOut, imagOut);
	  // transform back
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, true, realOut, imagOut, realIn, imagIn);
	  // show the result in the console
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " IDFT(DFT)=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realIn[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagIn[i]);
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\t\t|DFT(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ")| = ");
		System.out.printf("%.20f", Math.sqrt(realIn[i]*realIn[i]+imagIn[i]*imagIn[i]));
		System.out.printf("%.20f", "\n");
		}

	  // compare the result from the fft with the results from the dft
	  System.out.printf("%.20f", "\nCompare the FFT with the DFT signal with a exponential signal.\n");
	  realIn = e;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  double[] realOut1 = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] imagOut1 = new double[DefineConstantsTest.INPUT_LENGTH];
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut1, imagOut1);
	  // show the result in the console
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut[i]);
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", "DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut1[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut1[i]);
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT - DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", (realOut[i]-realOut1[i]));
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", (imagOut[i]-imagOut1[i]));
		System.out.printf("%.20f", "\n");
		}

	  // compare the result from the fft with the results from the dft
	  System.out.printf("%.20f", "\nCompare the FFT with the DFT signal with an unsymetrical sinc signal.\n");
	  realIn = f;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut1, imagOut1);
	  // show the result in the console
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut[i]);
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", "DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realOut1[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagOut1[i]);
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT - DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", (realOut[i]-realOut1[i]));
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", (imagOut[i]-imagOut1[i]));
		System.out.printf("%.20f", "\n");
		}

	  // compare the result from the fft with the results from the dft
	  System.out.printf("%.20f", "\nCompare the FFT with the DFT signal with the back transformation.\n");
	  double[] realIn1 = new double[DefineConstantsTest.INPUT_LENGTH];
	  double[] imagIn1 = new double[DefineConstantsTest.INPUT_LENGTH];
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
	  {
		realIn[i] = f[i];
		realIn1[i] = f[i];
	  }
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  imagIn[10] = 1.0;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(imagIn1, 0, DefineConstantsTest.INPUT_LENGTH * sizeof(double));
	  imagIn1[10] = 1.0;
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, false, realIn, imagIn, realOut, imagOut);
	  clauer.math.FourierTransformation.FFT(DefineConstantsTest.INPUT_LENGTH, true, realOut, imagOut, realIn, imagIn);
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, false, realIn1, imagIn1, realOut1, imagOut1);
	  clauer.math.FourierTransformation.DFT(DefineConstantsTest.INPUT_LENGTH, true, realOut1, imagOut1, realIn1, imagIn1);
	  // show the result in the console
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realIn[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagIn[i]);
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", "DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", realIn1[i]);
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", imagIn1[i]);
		System.out.printf("%.20f", "\n");
		}
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
		{
		System.out.printf("%.20f", " FFT - DFT=(");
		System.out.printf("%.20f", i);
		System.out.printf("%.20f", ") = [");
		System.out.printf("%.20f", (realIn[i]-realIn1[i]));
		System.out.printf("%.20f", ", ");
		System.out.printf("%.20f", (imagIn[i]-imagIn1[i]));
		System.out.printf("%.20f", "]");
		System.out.printf("%.20f", "\n");
		}

	  // waste the temporary arrays
	  a = null;
	  b = null;
	  c = null;
	  d = null;
	  e = null;
	  imagIn = null;
	  realOut = null;
	  imagOut = null;
	}
}

//--------------------------------------------------------------------------------------

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