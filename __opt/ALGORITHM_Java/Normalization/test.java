public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the normalization routines
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define INPUT_LENGTH 1000000

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo Normalization");
	  System.out.print("\n");
	  // first generate the input array
	  double[] a = new double[DefineConstantsTest.INPUT_LENGTH];
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH;i++)
	  {
		a[(int)i] = Math.sin((double)(i));
	  }
	  clauer.math.Normalizer.normalizePeak(a, DefineConstantsTest.INPUT_LENGTH, 1.0);
	  clauer.math.Normalizer.normalizePeak(a, DefineConstantsTest.INPUT_LENGTH, 1.0);
	  clauer.math.Normalizer.normalizeAvg(a, DefineConstantsTest.INPUT_LENGTH, 1.0);
	  clauer.math.Normalizer.normalizeAvg(a, DefineConstantsTest.INPUT_LENGTH, 1.0);
	  clauer.math.Normalizer.normalizeRms(a, DefineConstantsTest.INPUT_LENGTH, 1.0);
	  clauer.math.Normalizer.normalizeRms(a, DefineConstantsTest.INPUT_LENGTH, 1.0);
	  clauer.math.Normalizer.normalizeInterval(a, DefineConstantsTest.INPUT_LENGTH, 11.0, 21.0);
	  clauer.math.Normalizer.normalizeInterval(a, DefineConstantsTest.INPUT_LENGTH, 11.0, 21.0);
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