public class GlobalMembersTest
{
	//*
	// * This simple test Program illustrates the usage of the convolution algorithmus.
	// 

	//--------------------------------------------------------------------------------------------------

	// C Language Library heades

	// C++ Language Library headers

	// local headers  


	//--------------------------------------------------------------------------------------------------

	//#define LENGTH 1000
	//#define RECT_BEGIN 100
	//#define RECT_END 200

	//--------------------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n  *********************************************");
	  System.out.print("\n");
	  System.out.print("  *** This is the CONVOLUTION test function ***");
	  System.out.print("\n");
	  System.out.print("  ***    (c) Christoph Lauer Engineering    ***");
	  System.out.print("\n");
	  System.out.print("  *********************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // generate the test signals
	  double[] rect_signal_1 = new double[DefineConstantsTest.LENGTH];
	  double[] rect_signal_2 = new double[DefineConstantsTest.LENGTH];
	  double[] sin_signal_1 = new double[DefineConstantsTest.LENGTH];
	  double[] sin_signal_2 = new double[DefineConstantsTest.LENGTH];
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(rect_signal_1, 0, DefineConstantsTest.LENGTH *sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(rect_signal_2, 0, DefineConstantsTest.LENGTH *sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(sin_signal_1, 0, DefineConstantsTest.LENGTH *sizeof(double));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memset' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	  memset(sin_signal_2, 0, DefineConstantsTest.LENGTH *sizeof(double));
	  for (int i =DefineConstantsTest.RECT_BEGIN; i<DefineConstantsTest.RECT_END; i++)
	  {
		rect_signal_1[i] = 1.0;
		rect_signal_2[i] = 1.0;
		sin_signal_1[i] = Math.sin((double)(i-DefineConstantsTest.RECT_BEGIN)/10.0);
		sin_signal_2[i] = Math.cos((double)(i-DefineConstantsTest.RECT_BEGIN)/10.0);
	  }


	  /////////////////////////////////////////////////////////////
	  // CONVOLVE THE RECTANGE SIGNALS

	  // call the core algorithm
	  double convolution_f = clauer.math.Convolution.calculateFastConvolution(rect_signal_1, rect_signal_2, DefineConstantsTest.LENGTH);
	  double convolution_s = clauer.math.Convolution.calculateStandardConvolution(rect_signal_1, rect_signal_2, DefineConstantsTest.LENGTH);
	  // print the result
	  clauer.io.DataPlotter.simple2DPlot(rect_signal_1, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 1");
	  clauer.io.DataPlotter.simple2DPlot(rect_signal_2, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 2");
	  clauer.io.DataPlotter.simple2DPlot(convolution_f, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Fast Convolution Result");
	  clauer.io.DataPlotter.simple2DPlot(convolution_s, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Standard Convolution Result");
	  // call the core algorithm
	  convolution_f = clauer.math.Convolution.calculateFastConvolution(rect_signal_2, rect_signal_1, DefineConstantsTest.LENGTH);
	  convolution_s = clauer.math.Convolution.calculateStandardConvolution(rect_signal_2, rect_signal_1, DefineConstantsTest.LENGTH);
	  // print the result
	  clauer.io.DataPlotter.simple2DPlot(rect_signal_1, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 1");
	  clauer.io.DataPlotter.simple2DPlot(rect_signal_2, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 2");
	  clauer.io.DataPlotter.simple2DPlot(convolution_f, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Fast Convolution Result");
	  clauer.io.DataPlotter.simple2DPlot(convolution_s, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Standard Convolution Result");


	  /////////////////////////////////////////////////////////////
	  // CONVOLVE THE SIN SIGNALS

	  // call the core algorithm
	  convolution_f = clauer.math.Convolution.calculateFastConvolution(sin_signal_1, sin_signal_2, DefineConstantsTest.LENGTH);
	  convolution_s = clauer.math.Convolution.calculateStandardConvolution(sin_signal_1, sin_signal_2, DefineConstantsTest.LENGTH);
	  // print the result
	  clauer.io.DataPlotter.simple2DPlot(sin_signal_1, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 1");
	  clauer.io.DataPlotter.simple2DPlot(sin_signal_2, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 2");
	  clauer.io.DataPlotter.simple2DPlot(convolution_f, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Fast Convolution Result");
	  clauer.io.DataPlotter.simple2DPlot(convolution_s, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Standard Convolution Result");
	  // call the core algorithm
	  convolution_f = clauer.math.Convolution.calculateFastConvolution(sin_signal_2, sin_signal_1, DefineConstantsTest.LENGTH);
	  convolution_s = clauer.math.Convolution.calculateStandardConvolution(sin_signal_2, sin_signal_1, DefineConstantsTest.LENGTH);
	  // print the result
	  clauer.io.DataPlotter.simple2DPlot(sin_signal_1, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 1");
	  clauer.io.DataPlotter.simple2DPlot(sin_signal_2, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Input Signal 2");
	  clauer.io.DataPlotter.simple2DPlot(convolution_f, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Fast Convolution Result");
	  clauer.io.DataPlotter.simple2DPlot(convolution_s, DefineConstantsTest.LENGTH, false, false, false, 0,0,0,0, "Samples", "Aplitude", "Standard Convolution Result");

	  // report no error to the outside
	  return(0);
	}
}

//--------------------------------------------------------------------------------------------------


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