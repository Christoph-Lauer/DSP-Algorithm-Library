//*
// * This simple test Program illustrates the usage of the data plotter io functions.
// 

//--------------------------------------------------------------------------------------

// stl headers

// local headers  

//--------------------------------------------------------------------------------------


import clauer.io.*;
public class GlobalMembersTest
{

	//--------------------------------------------------------------------------------------

	//#define DATA_LENGTH 1024
	//#define MESH_LENGTH 50

	//--------------------------------------------------------------------------------------

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // splash screen 
	  System.out.print("\n**********************************************");
	  System.out.print("\n");
	  System.out.print("*** This is the DATA VISLUALISER function ***");
	  System.out.print("\n");
	  System.out.print("***   (c) Christoph Lauer Engineering     ***");
	  System.out.print("\n");
	  System.out.print("*********************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // instanciate the 2D plot data
	  double[] data2d = new double[DefineConstantsTest.DATA_LENGTH];
	  for (int i =0; i<DefineConstantsTest.DATA_LENGTH; i++)
		data2d[i] = Math.sin((double)(i)/20.0);

	  // instanciate the first 3D plot data
	  double[][] data3d = new double[DefineConstantsTest.MESH_LENGTH];
	  for (int i =0; i<DefineConstantsTest.MESH_LENGTH; i++)
	  {
		data3d[i] = new double[DefineConstantsTest.MESH_LENGTH];
		for (int j =0; j<DefineConstantsTest.MESH_LENGTH; j++)
		  data3d[i][j] = (double)(i *j);
	  }

	  // instanciate the second 3D plot data
	  double[][] data3d_2 = new double[DefineConstantsTest.MESH_LENGTH];
	  for (int i =0; i<DefineConstantsTest.MESH_LENGTH; i++)
	  {
		data3d_2[i] = new double[DefineConstantsTest.MESH_LENGTH];
		for (int j =0; j<DefineConstantsTest.MESH_LENGTH; j++)
		  data3d_2[i][j] = Math.sin((double)(i)/5) + Math.sin((double)(j)/5);
	  }

	  // draw the 2D fucntion plot
	  DataPlotter.simple2DPlot(data2d, DefineConstantsTest.DATA_LENGTH);
	  DataPlotter.simple2DPlot(data2d, DefineConstantsTest.DATA_LENGTH,0.0,DefineConstantsTest.DATA_LENGTH/20.0,0.0,0.0,false,true,true,"Samples","Amplitude","Sinus Function", "f(x) = sin(x)", "Cross Axis Coord System", "Without the inner Grid");
	  System.out.print("Save the last image to file...");
	  System.out.print("\n");
	  DataPlotter.simple2DPlot(data2d, DefineConstantsTest.DATA_LENGTH,0.0,0.0,0.0,0.0,true);
	  DataPlotter.simple2DPlot(data2d, DefineConstantsTest.DATA_LENGTH,0.0,0.0,0.0,0.0,true,true,true,"Samples","Amplitude","Sinus Function", "f(x) = sin(x)");

	  // draw the 3d contour plot
	  DataPlotter.simple3DPlot(data3d, DefineConstantsTest.MESH_LENGTH, DefineConstantsTest.MESH_LENGTH,0,0,0,0,false,false,false,"Simple Hill Distribution Function of the CLAUER Contour-Ploter","25x25 Sampels Matrix","No Interpolation", "In the Interval from [0...1]");
		System.out.print("\n");
		System.out.print("!!! Next Plaot Function Test take time --> don't break !!!");
		System.out.print("\n");
		System.out.print("\n");
	  DataPlotter.simple3DPlot(data3d, DefineConstantsTest.MESH_LENGTH, DefineConstantsTest.MESH_LENGTH,0,0,0,0,false,true,false,"Simple Hill Distribution Function of the CLAUER Contour-Ploter","25x25 Sampels Matrix","automatic Interpolation", "In the Interval from [0...1]");

	  // draw the 3d surfact/contour plot
		DataPlotter.extended3DPlot(data3d,DefineConstantsTest.MESH_LENGTH, DefineConstantsTest.MESH_LENGTH);

	  // draw the 3d surfact/contour plot
		DataPlotter.extended3DPlot(data3d_2,DefineConstantsTest.MESH_LENGTH, DefineConstantsTest.MESH_LENGTH);
		DataPlotter.extended3DPlot(data3d_2,DefineConstantsTest.MESH_LENGTH, DefineConstantsTest.MESH_LENGTH,0,0,0,0,false,true);

	  // waste the grabage
	  data2d = null;
	  data3d = null;

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