public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the autocorrelation algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define INPUT_LENGTH 256
	//#define SMOOTH_RANGE 6

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // the greeting string
	  System.out.print("Hallo Smooth");
	  System.out.print("\n");
	  System.out.print("Smooth Range: ");
	  System.out.print(DefineConstantsTest.SMOOTH_RANGE);
	  System.out.print("\n");
	  System.out.print("\n");

	  // first generate the input array
	  double[] a = new double[DefineConstantsTest.INPUT_LENGTH];
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH;i++)
	  {
		// generate a pseudo random sequence with a very large sinus frequency
		a[i] = Math.sin((double)i *66666.66666);
	  }

	  // call the moving average smooth algorithm with the pre defined smoothing parameters
	  double[] b = clauer.math.Smooth<Double>.doMovingAverageSmooth(a, DefineConstantsTest.INPUT_LENGTH, DefineConstantsTest.SMOOTH_RANGE);

	  // call the recusively moving averang smooth algorithm with the pre defined smoothing parameters
	  double[] c = clauer.math.Smooth<Double>.doMovingAverageSmoothRecursively(a, DefineConstantsTest.INPUT_LENGTH, DefineConstantsTest.SMOOTH_RANGE);

	  // call the standart derivation algorithm with the same pre defined smoothing parameters
	  double[] d = clauer.math.Smooth<Double>.doStandardDerivationToSmooth(a, DefineConstantsTest.INPUT_LENGTH, DefineConstantsTest.SMOOTH_RANGE);

		// output the input and result
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH; i++)
	  {
		System.out.print(i);
		System.out.print(":\t");
		System.out.print(a[i]);
		System.out.print("--> \tAvg:");
		System.out.print(b[i]);
		System.out.print("\tAvgRecursive:");
		System.out.print(c[i]);
		System.out.print("\tStandartDerivation:");
		System.out.print(d[i]);
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