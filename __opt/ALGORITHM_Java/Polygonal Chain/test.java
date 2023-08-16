public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the polygonal chain algorithmus.
	// 

	//--------------------------------------------------------------------------------------

	// stl headers

	// local headers  

	//--------------------------------------------------------------------------------------

	//#define INPUT_LENGTH 512

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // foirst the greeting screen
	  System.out.print("Hallo Polygonal Chain");
	  System.out.print("\n");

	  // first generate the input array
	  double[] a = new double[DefineConstantsTest.INPUT_LENGTH];
	  for (int i =0; i<DefineConstantsTest.INPUT_LENGTH;i++)
	  {
		a[(int)i] = Math.sin((double)i *10.0);
	  }

	  // call the algorithm
	  double pc = clauer.math.PolygonalChain<Double>.polygonalChain(a, DefineConstantsTest.INPUT_LENGTH);

	  // print the result to the std::out
	  System.out.print("PolygonalChain = ");
	  System.out.print(pc);
	  System.out.print("\n");
	  System.out.print("RelativePolygonalChain = ");
	  System.out.print(pc/(double)(DefineConstantsTest.INPUT_LENGTH));
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