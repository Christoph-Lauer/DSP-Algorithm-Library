public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the zero crossing rate algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define INPUT_LENGTH 512

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // print the greeting string
	  System.out.print("Hallo Zero Crossing Rate");
	  System.out.print("\n");

	  // first generate the input array
	  double[] a = new double[DefineConstantsTest.INPUT_LENGTH];
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH;i++)
	  {
		a[i] = Math.sin((double)(i)*.1);
		System.out.print("Signal(");
		System.out.print(i);
		System.out.print(") = ");
		System.out.print(a[i]);
		System.out.print("\n");
	  }

	  // call the core algorithm
	  int zcr = clauer.math.ZeroCrossingRate<Double>.calcZeroCrossingRate(a, DefineConstantsTest.INPUT_LENGTH);

	  // print the result to the std::out
	  System.out.print("Zero Crossing Rate: ");
	  System.out.print(zcr);
	  System.out.print("\n");
	  System.out.print("Normalized Zero Crossing Rate: ");
	  System.out.print((double)(zcr)/(double)(DefineConstantsTest.INPUT_LENGTH));
	  System.out.print(" per sample");
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