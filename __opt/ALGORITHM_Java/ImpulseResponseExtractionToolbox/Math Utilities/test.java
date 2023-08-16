public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the lUtilities.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo Utilities");
	  System.out.print("\n");
	  // first generate the input array
	  for (double i = 0.00001; i < 100000.0; i *= Math.sqrt(Math.sqrt(10.0)))
	  {
		double log = clauer.math.Utilities.lin2log(i);
		double lin = clauer.math.Utilities.log2lin(log);
		System.out.print("lin: ");
		System.out.print(i);
		System.out.print("\t--> log:");
		System.out.print(log);
		System.out.print("db\t and back to lin  --> ");
		System.out.print(lin);
		System.out.print("\n");
	  }
	  System.out.print("\n");

	  // some numbers
	  double lin = 2;
	  double log = clauer.math.Utilities.lin2log(lin);
	  System.out.print(lin);
	  System.out.print(" --> ");
	  System.out.print(log);
	  System.out.print("db");
	  System.out.print("\n");

	  lin = 10;
	  log = clauer.math.Utilities.lin2log(lin);
	  System.out.print(lin);
	  System.out.print(" --> ");
	  System.out.print(log);
	  System.out.print("db");
	  System.out.print("\n");

	  log = 3;
	  lin = clauer.math.Utilities.log2lin(log);
	  System.out.print(log);
	  System.out.print("db --> ");
	  System.out.print(lin);
	  System.out.print("\n");

	  log = 1;
	  lin = clauer.math.Utilities.log2lin(log);
	  System.out.print(log);
	  System.out.print("db --> ");
	  System.out.print(lin);
	  System.out.print("\n");

	  // test the power of two extender
	  for (int i =13; i<18; i++)
	  {
		int next = clauer.math.Utilities.getNextPowerOfTwo(i);
		System.out.print(i);
		System.out.print(" --> ");
		System.out.print(next);
		System.out.print("\n");
	  }

	  // test the zero padder
	  System.out.print("\n");
	  System.out.print("Now test the zero padder, extend an array with 10 elements to the next bigger power of 2 size");
	  System.out.print("\n");
	  double[] in = {1.0, 2.0,3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
	  int newLen = 0;
	  double[] out = clauer.math.Utilities.autoZeroPadding(in, 10, newLen);
	  System.out.print("Array extended to a size of ");
	  System.out.print(newLen);
	  System.out.print("\n");
	  for (int i =0; i<newLen; i++)
	  {
		System.out.print("Array [");
		System.out.print(i);
		System.out.print("] --> ");
		System.out.print(out[i]);
		System.out.print("\n");
	  }

	  // test the inplace operation for the logarithmization
	  System.out.print("\n");
	  System.out.print("Test the InPlace operations");
	  System.out.print("\n");
	  double value = 2.0;
	  System.out.print("Value = ");
	  System.out.print(value);
	  System.out.print("\n");
	  clauer.math.Utilities.lin2logInPlace(value);
	  System.out.print("Logarithmize = ");
	  System.out.print(value);
	  System.out.print("\n");
	  clauer.math.Utilities.log2linInPlace(value);
	  System.out.print("back to Lin = ");
	  System.out.print(value);
	  System.out.print("\n");

	  // test the array logarithmization
	  System.out.print("\n");
	  System.out.print("Test the inplace array logarithmization");
	  System.out.print("\n");
	  double[] rgd_lin = {DBL_MIN, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
	  double[] rgd_log = {DBL_MIN, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
	  clauer.math.Utilities.lin2logArrayInPlace(rgd_lin, 9);
	  clauer.math.Utilities.log2linArrayInPlace(rgd_log, 9);
	  for (int i =0;i<9; i++)
		{
		System.out.print("LOG: ");
		System.out.print(rgd_log[i]);
		System.out.print("dB \tREAL");
		System.out.print(i);
		System.out.print("\tLIN: ");
		System.out.print(rgd_lin[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  System.out.print("Another test for the inplace array logarithmization");
	  System.out.print("\n");
	  double[] linlog = new double[100];
	  double[] loglin = new double[100];
	  for (int i =0; i < 100; i++)
	  {
		double real = (double)(i) / 10.0;
		if (real == 0.0)
			real = DBL_MIN;
		linlog[i] = real;
		loglin[i] = real;
	  }
	  clauer.math.Utilities.lin2logArrayInPlace(linlog, 100);
	  clauer.math.Utilities.log2linArrayInPlace(loglin, 100);
	  for (int i =0; i< 100; i++)
	  {
		double real = (double)(i)/10.0;
		if (real == 0.0)
			real = DBL_MIN;
		double log = real;
		double lin = real;
		clauer.math.Utilities.lin2logInPlace(log);
		clauer.math.Utilities.log2linInPlace(lin);
		System.out.print("LOG: ");
		System.out.print(linlog[i]);
		System.out.print("dB \tREAL: ");
		System.out.print(real);
		System.out.print(" \tLIN ");
		System.out.print(loglin[i]);
		System.out.print("\n");
		System.out.print("LOG: ");
		System.out.print(log);
		System.out.print("dB \tREAL: ");
		System.out.print(real);
		System.out.print(" \tLIN ");
		System.out.print(lin);
		System.out.print("\n");
	  }

	  // tests the window functions
	  System.out.print("\n");
	  double[] hamming_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyHammingWindow(hamming_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Hamming_even[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(hamming_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] hamming_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyHammingWindow(hamming_odd, 17);
	  for (int i =0; i<17; i++)
		{
		System.out.print("Hamming_odd[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(hamming_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] hann_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyHannWindow(hann_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Hann_even[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(hann_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] hann_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyHannWindow(hann_odd, 17);
	  for (int i =0; i<17; i++)
		{
		System.out.print("Hann_odd[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(hann_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] blackman_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyBlackmanWindow(blackman_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Blackman_even[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(blackman_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] blackman_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyBlackmanWindow(blackman_odd, 17);
	  for (int i =0; i<17; i++)
		{
		System.out.print("Blackman_odd[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(blackman_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] triangle_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyTriangleWindow(triangle_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Triangle_even[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(triangle_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] triangle_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyTriangleWindow(triangle_odd, 16);
	  for (int i =0; i<17; i++)
		{
		System.out.print("Triangle_odd[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(triangle_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] welch_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyWelchWindow(welch_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Welch_even[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(welch_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] welch_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyWelchWindow(welch_odd, 17);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Welch_odd[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(welch_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] gauss_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyGaussWindow(gauss_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Gauss_even[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(gauss_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] gauss_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyGaussWindow(gauss_odd, 17);
	  for (int i =0; i<17; i++)
		{
		System.out.print("Gauss_odd[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(gauss_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] cosine_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyCosineWindow(cosine_even, 16);
	  for (int i =0; i<16; i++)
		{
		System.out.print("Cosine[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(cosine_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] cosine_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyCosineWindow(cosine_odd, 17);
	  for (int i =0; i<17; i++)
		{
		System.out.print("Cosine[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(cosine_odd[i]);
		System.out.print("\n");
		}

	  System.out.print("\n");
	  double[] asym_even = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyAsymetricalExponentialWindow(asym_even, 16, 1.0);
	  for (int i =0; i<16; i++)
		{
		System.out.print("AsymExpo[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(asym_even[i]);
		System.out.print("\n");
		}
	  System.out.print("\n");
	  double[] asym_odd = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	  clauer.math.Utilities.applyAsymetricalExponentialWindow(asym_odd, 17, 1.0);
	  for (int i =0; i<17; i++)
		{
		System.out.print("AsymExpo[");
		System.out.print(i);
		System.out.print("] =");
		System.out.print(asym_odd[i]);
		System.out.print("\n");
		}

	  // testing the round function
	  System.out.print("round(6.499)=");
	  System.out.print(clauer.math.Utilities.round(6.499));
	  System.out.print("\n");
	  System.out.print("round(6.500)=");
	  System.out.print(clauer.math.Utilities.round(6.500));
	  System.out.print("\n");
	  System.out.print("round(6.501)=");
	  System.out.print(clauer.math.Utilities.round(6.501));
	  System.out.print("\n");
	  System.out.print("round(6.999)=");
	  System.out.print(clauer.math.Utilities.round(6.999));
	  System.out.print("\n");
	  System.out.print("round(7.000)=");
	  System.out.print(clauer.math.Utilities.round(7.000));
	  System.out.print("\n");
	  System.out.print("round(7.001)=");
	  System.out.print(clauer.math.Utilities.round(7.001));
	  System.out.print("\n");
	  System.out.print("round(7.499)=");
	  System.out.print(clauer.math.Utilities.round(7.499));
	  System.out.print("\n");
	  System.out.print("round(7.500)=");
	  System.out.print(clauer.math.Utilities.round(7.500));
	  System.out.print("\n");
	  System.out.print("round(7.501)=");
	  System.out.print(clauer.math.Utilities.round(7.501));
	  System.out.print("\n");

	  double d_n = 7.0;
	  double d_p2 = clauer.math.Utilities.powerOfTwo(d_n);
	  double d_ld = clauer.math.Utilities.logarithmusDualis(d_p2);
	  System.out.print("\n");
	  System.out.print("powerOfTwo(");
	  System.out.print(d_n);
	  System.out.print(")=");
	  System.out.print(d_p2);
	  System.out.print("\n");
	  System.out.print("logarithmusDualis(");
	  System.out.print(d_p2);
	  System.out.print(")=");
	  System.out.print(d_ld);
	  System.out.print("\n");

	  d_n = 7.4;
	  d_p2 = clauer.math.Utilities.powerOfTwo(d_n);
	  d_ld = clauer.math.Utilities.logarithmusDualis(d_p2);
	  System.out.print("\n");
	  System.out.print("powerOfTwo(");
	  System.out.print(d_n);
	  System.out.print(")=");
	  System.out.print(d_p2);
	  System.out.print("\n");
	  System.out.print("logarithmusDualis(");
	  System.out.print(d_p2);
	  System.out.print(")=");
	  System.out.print(d_ld);
	  System.out.print("\n");

	  int i_n = 0;
	  int i_p2 = clauer.math.Utilities.powerOfTwo(i_n);
	  int i_ld = clauer.math.Utilities.logarithmusDualis(i_p2);
	  System.out.print("\n");
	  System.out.print("powerOfTwo(");
	  System.out.print(i_n);
	  System.out.print(")=");
	  System.out.print(i_p2);
	  System.out.print("\n");
	  System.out.print("logarithmusDualis(");
	  System.out.print(i_p2);
	  System.out.print(")=");
	  System.out.print(i_ld);
	  System.out.print("\n");

	  i_n = 1;
	  i_p2 = clauer.math.Utilities.powerOfTwo(i_n);
	  i_ld = clauer.math.Utilities.logarithmusDualis(i_p2);
	  System.out.print("\n");
	  System.out.print("powerOfTwo(");
	  System.out.print(i_n);
	  System.out.print(")=");
	  System.out.print(i_p2);
	  System.out.print("\n");
	  System.out.print("logarithmusDualis(");
	  System.out.print(i_p2);
	  System.out.print(")=");
	  System.out.print(i_ld);
	  System.out.print("\n");

	  i_n = 7;
	  i_p2 = clauer.math.Utilities.powerOfTwo(i_n);
	  i_ld = clauer.math.Utilities.logarithmusDualis(i_p2);
	  System.out.print("\n");
	  System.out.print("powerOfTwo(");
	  System.out.print(i_n);
	  System.out.print(")=");
	  System.out.print(i_p2);
	  System.out.print("\n");
	  System.out.print("logarithmusDualis(");
	  System.out.print(i_p2);
	  System.out.print(")=");
	  System.out.print(i_ld);
	  System.out.print("\n");

	  i_n = 16;
	  i_p2 = clauer.math.Utilities.powerOfTwo(i_n);
	  i_ld = clauer.math.Utilities.logarithmusDualis(i_p2);
	  System.out.print("\n");
	  System.out.print("powerOfTwo(");
	  System.out.print(i_n);
	  System.out.print(")=");
	  System.out.print(i_p2);
	  System.out.print("\n");
	  System.out.print("logarithmusDualis(");
	  System.out.print(i_p2);
	  System.out.print(")=");
	  System.out.print(i_ld);
	  System.out.print("\n");

	  System.out.print("Next test is the power of Two test");
	  System.out.print("\n");
	  System.out.print("isPowerOfTwo(128)");
	  System.out.print(clauer.math.Utilities.isPowerOfTwo(128));
	  System.out.print("\n");
	  System.out.print("isPowerOfTwo(129)");
	  System.out.print(clauer.math.Utilities.isPowerOfTwo(129));
	  System.out.print("\n");

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