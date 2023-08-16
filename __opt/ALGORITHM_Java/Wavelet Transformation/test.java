public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the wavelet algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	// transformation length
	//#define TRANSFORMATION_LENGTH 128

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo Wavelet");
	  // first generate the input array
	  double[] a = new double[DefineConstantsTest.TRANSFORMATION_LENGTH];
	  for (int i =0; i<DefineConstantsTest.TRANSFORMATION_LENGTH;i++)
	  {
		a[(int)i] = Math.sin((double)i)*1000.0;
	  }

	  // call the wavelet transformation
	  byte wlType = (byte)'C';
	  int wlOrder = 30;
	  double[] b = clauer.math.WaveletTransformation.doWaveletTransformation(a, wlType, wlOrder, DefineConstantsTest.TRANSFORMATION_LENGTH);

	  // output the input and result
	  for (int i =0; i<DefineConstantsTest.TRANSFORMATION_LENGTH; i++)
	  {
		System.out.print(i);
		System.out.print(":\t");
		System.out.print(a[i]);
		System.out.print("--> \t");
		System.out.print(b[i]);
		System.out.print("\n");
	  }

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