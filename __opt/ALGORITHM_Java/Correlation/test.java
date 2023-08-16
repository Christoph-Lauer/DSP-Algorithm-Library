//*
// * This simple test Program illustrates the usage of the autocorrelation algorithmus.
// 

//--------------------------------------------------------------------------------------

// C Langauge Library headers

// C++ Langauge Library headers

// local headers  

//--------------------------------------------------------------------------------------

//#define INPUT_SIGNAL_LENGTH 256
//#define COMPARISATION_SIGNAL_LENGTH 128

//--------------------------------------------------------------------------------------

import clauer.io.*;
public class GlobalMembersTest
{

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  System.out.print("Hallo Autocorrelation");
	  // first generate the input array
	  double[] a1 = new double[DefineConstantsTest.INPUT_SIGNAL_LENGTH];
	  double[] a2 = new double[DefineConstantsTest.INPUT_SIGNAL_LENGTH];
	  double[] a3 = new double[DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH];
	  for (int i =0; i<DefineConstantsTest.INPUT_SIGNAL_LENGTH; i++)
	  {
		double x1 = (double)(i-50);
		double x2 = (double)(i-100);
		if (x1 == 0.0)
			x1 = DBL_MIN;
		if (x2 == 0.0)
			x2 = DBL_MIN;
		a1[i] = Math.sin(x1)/x1;
		a2[i] = Math.sin(x2)/x2;
	  }
	  for (int i =0; i<DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH; i++)
	  {
		double x = (double)(i-100);
		if (x == 0.0)
			x = DBL_MIN;
		a3[i] = Math.sin(x)/x;
	  }

		// call the static ac function
	  double b = clauer.math.Correlation<Double>.autoCorrelation(a1, DefineConstantsTest.INPUT_SIGNAL_LENGTH);
	  double c = clauer.math.Correlation<Double>.crossCorrelation(a1, DefineConstantsTest.INPUT_SIGNAL_LENGTH, a2, DefineConstantsTest.INPUT_SIGNAL_LENGTH);
	  double d = clauer.math.Correlation<Double>.crossCorrelation(a1, DefineConstantsTest.INPUT_SIGNAL_LENGTH, a3, DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH);
	  double e = clauer.math.Correlation<Double>.crossCorrelation(a2, DefineConstantsTest.INPUT_SIGNAL_LENGTH, a3, DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH);

	  // output the input and result to the data plotter
	  DataPlotter plotter = new DataPlotter();
	  plotter.simple2DPlot(b, DefineConstantsTest.INPUT_SIGNAL_LENGTH *2-1,-DefineConstantsTest.INPUT_SIGNAL_LENGTH,DefineConstantsTest.INPUT_SIGNAL_LENGTH,0.0,0.0, false, false, false, "Samples", "Autocorrelation", "Autocorrelation Example", "f(x)=sin(x)/x");
	  plotter.simple2DPlot(c, DefineConstantsTest.INPUT_SIGNAL_LENGTH *2-1,-DefineConstantsTest.INPUT_SIGNAL_LENGTH,DefineConstantsTest.INPUT_SIGNAL_LENGTH,0.0,0.0, false, false, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "f1(x)= sin(x)/x, f2(x-50)=sin(x)/(x)");
	  plotter.simple2DPlot(d, (DefineConstantsTest.INPUT_SIGNAL_LENGTH+DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH)-1,-DefineConstantsTest.INPUT_SIGNAL_LENGTH, DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH,0.0,0.0, false, false, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "f1(x)= sin(x)/x, f2(x-50)=sin(x)/(x), different length");
	  plotter.simple2DPlot(e, (DefineConstantsTest.INPUT_SIGNAL_LENGTH+DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH)-1,-DefineConstantsTest.INPUT_SIGNAL_LENGTH, DefineConstantsTest.COMPARISATION_SIGNAL_LENGTH,0.0,0.0, false, true, false, "Samples", "Cross-Correlation", "Cross-Correlation Example", "f1(x-50)= sin(x)/x, f2(x-50)=sin(x)/(x), different length");

	  // now test the pearson correlation
	  double roh = clauer.math.Correlation<Double>.pearsonCorrelation(a1, a2, DefineConstantsTest.INPUT_SIGNAL_LENGTH);
	  System.out.print("The Pearsson Correlation between the two signal");
	  System.out.print(roh);
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