package clauer.io;

// DISLIN Plotting Library header from the Max Planck Institute for Solar System Research

// C Langunage Library headers

// C++ Language Libraray headers

//------------------------------------------------------------------------------------------------

import clauer.math.*;
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    data_plotter.cpp
// * @class   DataPlotter
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   The class gives a basic interface for the visualisation of two and three dimensional curves
// * @see     http://en.wikipedia.org/wiki/Scientific_visualization
// * @see     http://en.wikipedia.org/wiki/Input/output
// * @see     http://www.mps.mpg.de/dislin/
// * @todo    finished so far.
// 

//------------------------------------------------------------------------------------------------

// Local headers
//*
// * (c)      Christoph Lauer Engineering
// *
// * @file    data_plotter.h
// * @class   DataPlotter
// * @version 1.0
// * @date    2014
// * @author  Christoph Lauer
// * @brief   The class gives a basic interface for the visualisation of two and three dimensional curves
// * @see     http://en.wikipedia.org/wiki/Scientific_visualization
// * @see     http://en.wikipedia.org/wiki/Input/output
// * @see     http://www.mps.mpg.de/dislin/
// * @todo    finished so far.
// 

//------------------------------------------------------------------------------------------------

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! CLAUER_IO_DATA_PLOTTER
//#define CLAUER_IO_DATA_PLOTTER

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

//*
// * The class collects all the useful function for the plotting of datat structures into image files,
// * on the screen and onto the printer. Therefor we use the dislin programming library from the Max 
// * Planck Institute for Solar System Research in Lindau/Karlsruhe and would linke tosay thanks to the 
// * author Helmut Michels. This class prvides the caller with functiosn for the plotting of one and 
// * more dimensional functions and plots onto images, the screen and the printer. 
// 
public class DataPlotter
{

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

// *
//  * This fucntion provides the caller with a simple plotting fucntion. If no further values are given 
//  * apart from the double array and the length of the array the function will autoscale the plot and 
//  * draw an image. This function can simple be used with only the data and the length where the whole
//  * plot is generated with the default values and the y axis will be autoscaled. If the min/max settings
//  * for the axis values will be set to 0.0, the whole plot will be autoscaled. The png parameter will
//  * save the file into a file named dislin.png where additional files will be saved in files named 
//  * dislin_1.png, dislin_2.png and so on. If the png flag is not set the file will be displayed on 
//  * the screen. The default resolution for the file is 1200x675. Th whole plot will be plotted with the
//  * default colors white/grey/green for the title/grid/graph.
//  *
//  * @param data         The array of values to be plotted
//  * @param length       The length of the array to be plotted
//  * 
//  * NOTE THAT ALL THE FOLLOWING PARAMETERS ARE OPTIONAL
//  * @param png          If this flag is set, the image will be stored into an PNG file named "dislin.png".
//  *                     If the file exist the name will be become and postfix with corresponding number
//  * @param crossCoord   Switches between the outer box coord system and the midlle placed cross system.
//  * @param noGrid       Disables the plot inner a[Bxis grid
//  * @param xMin         The minimum of the X-Axis.
//  * @param yxax         The maximum of the X-Axis
//  * @param yMin         The minimum of the Y-Axis
//  * @param yMax         The maximum of the Y-Axis
//  * @param xAxisName    The name label of the X-Axis
//  * @param yAxisName    The name label of the Y-Axis
//  * @param title1       The first line of the title
//  * @param title2       The second line of the title
//  * @param title3       The third line of the title
//  * @param title4       The fourth line of the title
//  

  //------------------------------------------------------------------------------------------------
  
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName, String yAxisName, String title1, String title2, String title3, String title4)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, xAxisName, yAxisName, title1, title2, title3, title4, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName, String yAxisName, String title1, String title2, String title3)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, xAxisName, yAxisName, title1, title2, title3, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName, String yAxisName, String title1, String title2)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, xAxisName, yAxisName, title1, title2, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName, String yAxisName, String title1)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, xAxisName, yAxisName, title1, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName, String yAxisName)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, xAxisName, yAxisName, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, xAxisName, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, noGrid, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, crossCoord, false, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, png, false, false, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, yMax, false, false, false, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin)
  {
	  simple2DPlot(yRay, length, xMin, xMax, yMin, 0.0, false, false, false, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax)
  {
	  simple2DPlot(yRay, length, xMin, xMax, 0.0, 0.0, false, false, false, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length, double xMin)
  {
	  simple2DPlot(yRay, length, xMin, 0.0, 0.0, 0.0, false, false, false, null, null, null, null, null, null, null);
  }
  public static void simple2DPlot(double[] yRay, int length)
  {
	  simple2DPlot(yRay, length, 0.0, 0.0, 0.0, 0.0, false, false, false, null, null, null, null, null, null, null);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void simple2DPlot(const double* yRay, const int length, double xMin = 0.0, double xMax = 0.0, double yMin = 0.0, double yMax = 0.0, const boolean png = false, const boolean crossCoord = false, const boolean noGrid = false, String xAxisName = null, String yAxisName = null, String title1 = null, String title2 = null, String title3 = null, String title4 = null, String fileName = null)
  public static void simple2DPlot(double[] yRay, int length, double xMin, double xMax, double yMin, double yMax, boolean png, boolean crossCoord, boolean noGrid, String xAxisName, String yAxisName, String title1, String title2, String title3, String title4, String fileName)
  {
  
	// prepare the x axis
	double[] xRay = new double[length];
	if (xMax == 0.0 && xMin == 0.0)
	  xMax = (double)(length);
	for (int i =0; i<length; i++)
	  xRay[i] = xMin + (double)(i) / (double)(length) * (xMax - xMin);
  
	// prepare the y axis
	if (yMax == 0.0 && yMin == 0.0)
	{
	  yMax = -DBL_MAX;
	  yMin = DBL_MAX;
	  for (int i =0; i<length; i++)
	  {
		if (yMax < yRay[i])
			yMax = yRay[i];
		if (yMin > yRay[i])
			yMin = yRay[i];
	  }
	}
	// the courve is plotted a little bit under the borders
	yMin -= Math.abs(yMin/100.0);
	yMax += Math.abs(yMax/100.0);
  
	// DISLIN - level 0
	if (png == false)
	  metafl ("CONS");
	else
	  metafl ("PNG");
	// set the file name is provided
	  if (!fileName.equals(null))
	  setfil(fileName);
	page(4000, 2250);
	window((DefineConstantsData_plotter.SCREEN_WIDTH-DefineConstantsData_plotter.IMAGE_WIDTH)/2,(DefineConstantsData_plotter.SCREEN_HEIGHT-DefineConstantsData_plotter.IMAGE_HEIGHT)/2+100,DefineConstantsData_plotter.IMAGE_WIDTH,DefineConstantsData_plotter.IMAGE_HEIGHT);
	sclmod ("full");
	disini ();
  
	// DISLIN Level 1
	autoScaleAxesNumberOfDecimalPoints(xMin, xMax, yMin, yMax, 0, 0);
	winkey("RETURN");
	setind(150, 0.3, 0.3, 0.3);
	setind(151, 0.5, 0.5, 0.5);
	setind(152, 0.6, 0.6, 0.6);
	setind(153, 0.7, 0.7, 0.7);
	setind(154, 0.7, 1.0, 1.0);
	pagera();
	axspos(350,2030);
	axslen(3450,1700);
	if (crossCoord == true)
	  axstyp("CROSS");
	if (xAxisName.equals(null))
	  xAxisName = "X-Axis";
	name(xAxisName,"X");
	if (yAxisName.equals(null))
	  yAxisName = "Y-Axis";
	name(yAxisName,"Y");
	ticks(16,"x");
	ticks(8,"y");
	setrgb(0.7, 0.7, 0.7);
	graf (xMin, xMax, xMin, (xMax-xMin)/10, yMin, yMax, yMin, (yMax-yMin)/10);
  
	// DISLIN Level 2
	if (noGrid == false)
	{
	  setrgb(0.3, 0.3, 0.3);
	  grid(8,4);
	}
	setrgb(1.0, 1.0, 1.0);
	if (title1.equals(null))
	  title1 = "Christoph Lauer Engineering(c) Data-Visualization-Toolbox";
	titlin(title1,1);
	if (title2.equals(null))
	  title2 = "2D function plotter";
	titlin(title2,2);
	if (!title3.equals(null))
	  titlin(title3,3);
	if (!title4.equals(null))
	  titlin(title4,4);
	title();
	setrgb(0.7, 1.0, 0.0);
	curve(xRay, yRay, length);
  
	// DISLIN end
	disfin ();
  
	// waste the grabage
	xRay = null;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function provies a simple interface for the plotting of three dimensional data matrices like
//  * time-frequency analysis representions for the specrogram and the wigner ville transformations. The
//  * simple interface with no additional parameters can be used to plot the matrice autonomously. 
//  * The extended interface with the scling functions and the interpolation/png/log and title information
//  * can be used to plot a more detailed matrice. This function based on the DISLIN library and calls 
//  * the painting functions of the DISLIN library. The default image and screen size for the images are
//  * 1000x675 pixels. The whole plot will be painted with the default rainbow color spectrum.
//  *
//  * @param data            The data martice must be in the format double** and will be resorted internally 
//  *                        into the time-frequency representation which will be plotted onto the screen.
//  * @param lengthX         The length of the time domain input signal in samples.
//  * @param lengthY         The length of the frequency representaion in samples.
//  * @param xMin            The lower X-Achis scaling factor.
//  * @param xMax            The upper X-Achis scaling factor.
//  * @param yMin            The lower Y-Achis scaling factor.
//  * @param yMax            The upper Y-Achis scaling factor.
//  * @param png             If this flag is set, the image will be stored into an PNG file named "dislin.png".
//  *                        If the file exist the name will be become a postfix with corresponding number
//  * @param autiInterpolate If this flag is set to true, the image will be automaticall smoothed in both 
//  *                        directions and the interpolation factors for both axes will be set automnomous.
//  * @param logPlot         If this flag is set the z-axis will be automaticall transformed into the logarithmix
//  *                        space. Not that here all values must be in the positive real interval. A value
//  *                        lesser than 0 will be thro an exception.
//  * @param title1       The first line of the title
//  * @param title2       The second line of the title
//  * @param title3       The third line of the title
//  * @param title4       The fourth line of the title
//  

  //------------------------------------------------------------------------------------------------
  
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate, boolean logPlot, String title1, String title2, String title3, String title4)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, autoInterpolate, logPlot, title1, title2, title3, title4, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate, boolean logPlot, String title1, String title2, String title3)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, autoInterpolate, logPlot, title1, title2, title3, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate, boolean logPlot, String title1, String title2)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, autoInterpolate, logPlot, title1, title2, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate, boolean logPlot, String title1)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, autoInterpolate, logPlot, title1, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate, boolean logPlot)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, autoInterpolate, logPlot, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, autoInterpolate, false, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, false, false, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, false, false, false, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, 0.0, false, false, false, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, xMax, 0.0, 0.0, false, false, false, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin)
  {
	  simple3DPlot(data, lengthX, lengthY, xMin, 0.0, 0.0, 0.0, false, false, false, null, null, null, null, null);
  }
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY)
  {
	  simple3DPlot(data, lengthX, lengthY, 0.0, 0.0, 0.0, 0.0, false, false, false, null, null, null, null, null);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void simple3DPlot(double** data, const int lengthX, const int lengthY, double xMin = 0.0, double xMax = 0.0, double yMin = 0.0, double yMax = 0.0, const boolean png = false, const boolean autoInterpolate = false, const boolean logPlot = false, String title1 = null, String title2 = null, String title3 = null, String title4 = null, String fileName = null)
  public static void simple3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean autoInterpolate, boolean logPlot, String title1, String title2, String title3, String title4, String fileName)
  {
	// init the plot parameter values
	int PLOT_WIDTH = 3100;
	int PLOT_HEIGHT = 1750;
  
	// prepare the x axis
	if (xMax ==0.0 && xMin ==0.0)
	  xMax = (double)(lengthX);
	// prepare the y axis
	if (yMax ==0.0 && yMin ==0.0)
	  yMax = (double)(lengthY);
  
	// prepare the z axis borders
	double zMin = +DBL_MAX;
	double zMax = -DBL_MAX;
  
	// first find the peaks
	for (int x =0; x<lengthX; x++)
	  for (int y =0; y<lengthY; y++)
	  {
		if(zMin > data[x][y])
			zMin = data[x][y];
		if(zMax < data[x][y])
			zMax = data[x][y];
	  }
  
	// check for correct border conditions
	if (zMax == zMin)
	  {
		  zMax += zMax / 100.0;
		  zMin -= zMin / 100.0;
	  }
	if (zMax == 0.0 && zMin == 0.0)
	  {
		  zMax += 1.0;
		  zMin -= 1.0;
	  }
  
	// now rearrange the elements and logarithmize them in required
	double[] resortedData = new double[lengthX *lengthY];
	for (int x =0; x<lengthX; x++)
	  for (int y =0; y<lengthY; y++)
	  {
		// resort the elements
		if (logPlot == true)
		  resortedData[y+x *lengthY] = Utilities.lin2log(data[x][y]);
		else
		  resortedData[y+x *lengthY] = data[x][y];
	  }
  
	// normalize the peak values
	if (logPlot == true)
	{
	  zMin = Utilities.lin2log(zMin);
	  zMax = Utilities.lin2log(zMax);
	}
	System.out.print("Automatic Scaling: min=");
	System.out.print(zMin);
	System.out.print("max=");
	System.out.print(zMax);
	System.out.print("\n");
  
	// DISLIN level 0
	if (png == false)
	  metafl ("CONS");
	else
	  metafl ("PNG");
	// set the file name is provided
	  if (!fileName.equals(null))
	  setfil(fileName);
	page(4000, 2250);
	window((DefineConstantsData_plotter.SCREEN_WIDTH-DefineConstantsData_plotter.IMAGE_WIDTH)/2,(DefineConstantsData_plotter.SCREEN_HEIGHT-DefineConstantsData_plotter.IMAGE_HEIGHT)/2+100,DefineConstantsData_plotter.IMAGE_WIDTH,DefineConstantsData_plotter.IMAGE_HEIGHT);
	imgfmt("RGB");
	disini();
  
	// DISLIN level 1
	autoScaleAxesNumberOfDecimalPoints(xMin, xMax, yMin, yMax, zMin, zMax);
	winkey("RETURN");
	pagera();
	// name the axes
	name("Time Domain Axis","x");
	name("Frequency Domain Axis","y");
	name("Amplitude Gradient","z");
	autres(lengthX, lengthY);
	axspos(350, PLOT_HEIGHT + 320);
	ax3len(PLOT_WIDTH, PLOT_HEIGHT, PLOT_HEIGHT);
	graf3(xMin, xMax, xMin, (xMax-xMin)/10, yMin, yMax, yMin, (yMax-yMin)/10, zMin, zMax, zMin, (zMax-zMin)/10);
  
	// DISLIN level 2
	if (title1.equals(null))
	  title1 = "Christoph Lauer Engineering (c) Data-Visualization-Toolbox";
	titlin(title1,1);
	if (title2.equals(null))
	  title2 = "3D countour plotter";
	titlin(title2,2);
	if (!title3.equals(null))
	  titlin(title3,3);
	if (!title4.equals(null))
	  titlin(title4,4);
	title();
	// the number of interpolation points used for the plot
	int ixPts = 1;
	int iyPts = 1;
	// if the automatic interpolation is enabled calcualte the number of interpoation points autonomously
	if (autoInterpolate == true)
	{
	  System.out.print("----> AUTOINTERPOLATION");
	  System.out.print("\n");
	  if (lengthX < PLOT_WIDTH)
		ixPts = PLOT_WIDTH / lengthX;
	  if (lengthY < PLOT_HEIGHT)
		iyPts = PLOT_HEIGHT / lengthY;
	  System.out.print("Automatic Interpolation: ix=");
	  System.out.print(ixPts);
	  System.out.print(" iyPts=");
	  System.out.print(iyPts);
	  System.out.print("\n");
	}
  
	// draw the contour plot
	crvmat(resortedData, lengthX, lengthY, ixPts, iyPts);
  
	// DISLIN end
	disfin();
  
	// waste the grabage
	resortedData = null;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function implements a simple interface to a surface plot with a contour plot on the bottom 
//  * of the surface. The function signature has the same structure like the simple2DPlot function 
//  * defined above, so the programmer can be simple and fast switch between the surface/contour and 
//  * the raw contour plot. In most cases the underlaying contour plot will not be seen because the 
//  * surface lays in the most cases arround the zero point in the Z-Axis and covered the contour plot.
//  *
//  * @param data         The data martice must be in the format double** and will be resorted internally 
//  *                     into the time-frequency representation which will be plotted onto the screen.
//  * @param lengthX      The length of the time domain input signal in samples.
//  * @param lengthY      The length of the frequency representaion in samples.
//  * @param xMin         The lower X-Achis scaling factor.
//  * @param xMax         The upper X-Achis scaling factor.
//  * @param yMin         The lower Y-Achis scaling factor.
//  * @param yMax         The upper Y-Achis scaling factor.
//  * @param png          If this flag is set, the image will be stored into an PNG file named "dislin.png".
//  *                     If the file exist the name will be become a postfix with corresponding number
//  * @param smooth       This flag enabled the polygon smoothing if the resolution was low.
//  * @param isoLines     This flag enables the painting of the Iso-Z lines.
//  * @param title1       The first line of the title
//  * @param title2       The second line of the title
//  * @param title3       The third line of the title
//  * @param title4       The fourth line of the title
//  

  //------------------------------------------------------------------------------------------------
  
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth, boolean isoLines, String title1, String title2, String title3, String title4)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, smooth, isoLines, title1, title2, title3, title4, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth, boolean isoLines, String title1, String title2, String title3)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, smooth, isoLines, title1, title2, title3, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth, boolean isoLines, String title1, String title2)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, smooth, isoLines, title1, title2, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth, boolean isoLines, String title1)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, smooth, isoLines, title1, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth, boolean isoLines)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, smooth, isoLines, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, smooth, false, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, png, false, false, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, yMax, false, false, false, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, yMin, 0.0, false, false, false, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, xMax, 0.0, 0.0, false, false, false, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin)
  {
	  extended3DPlot(data, lengthX, lengthY, xMin, 0.0, 0.0, 0.0, false, false, false, null, null, null, null, null);
  }
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY)
  {
	  extended3DPlot(data, lengthX, lengthY, 0.0, 0.0, 0.0, 0.0, false, false, false, null, null, null, null, null);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: static void extended3DPlot(double** data, const int lengthX, const int lengthY, double xMin = 0.0, double xMax = 0.0, double yMin = 0.0, double yMax = 0.0, const boolean png = false, const boolean smooth = false, const boolean isoLines = false, String title1 = null, String title2 = null, String title3 = null, String title4 = null, String fileName = null)
  public static void extended3DPlot(double[][]data, int lengthX, int lengthY, double xMin, double xMax, double yMin, double yMax, boolean png, boolean smooth, boolean isoLines, String title1, String title2, String title3, String title4, String fileName)
  {
	  // init some basic data structures
	  double[] zmat = new double[lengthX *lengthY];
	  double[] xray = new double[lengthX];
	  double[] yray = new double[lengthY];
	  int nlev = 100;
	  double[] zlev = new double[100];
  
	  // resort the array and determine the peaks
	  double zMin = DBL_MAX;
	  double zMax = -DBL_MAX;
	for (int i =0; i < lengthX; i++)
	  for (int j =0; j < lengthY; j++)
	  {
		// resort the data
		zmat[i *lengthY+j] = data[i][j];
		// find the peaks
		if (zMin > data[i][j])
			zMin = data[i][j];
		if (zMax < data[i][j])
			zMax = data[i][j];
	  }
  
	// fill the X ray array
	for (int i =0; i<lengthX; i++)
	  if (xMin == 0.0 && xMax == 0.0)
		xray[i] =(double)(i);
	  else
		xray[i] = (xMax-xMin)*(double)(i)/(double)(lengthX);
	// fill the Y ray array
	for (int i =0; i<lengthY; i++)
	  if (yMin == 0.0 && yMax == 0.0)
		yray[i] =(double)(i);
	  else
		yray[i] = (yMax-yMin)*(double)(i)/(double)(lengthY);
	// adapt the plot borders
	if (xMin == 0.0 && xMax == 0.0)
	  xMax = (double)(lengthX-1);
	if (yMin == 0.0 && yMax == 0.0)
	  yMax = (double)(lengthY-1);
  
	// build the level color array
	double step = (zMax-zMin) / nlev;
	for (int i = 0; i < nlev; i++)
	  zlev[i] = (double)(zMin + i * step);
  
	  // DISLIN level 0
	if (png == false)
	  metafl ("CONS");
	else
	  metafl ("PNG");
	// set the file name is provided
	  if (!fileName.equals(null))
	  setfil(fileName);
	// the size of the
	page(4000, 2250);
	window((DefineConstantsData_plotter.SCREEN_WIDTH-DefineConstantsData_plotter.IMAGE_WIDTH)/2,(DefineConstantsData_plotter.SCREEN_HEIGHT-DefineConstantsData_plotter.IMAGE_HEIGHT)/2+100,DefineConstantsData_plotter.IMAGE_WIDTH,DefineConstantsData_plotter.IMAGE_HEIGHT);
	imgfmt("RGB");
	disini();
  
	// DISLIN level 1
	winkey("RETURN");
	  // set the length of the coordinate system
	axslen(4000 *2/3,1625);
	// automatical set the number of floating drawed points
	autoScaleAxesNumberOfDecimalPoints(xMin, xMax, yMin, yMax, zMin, zMax);
	pagera();
	// defien the size of the cube
	axis3d(4.5,3,2);
	// define the view position
	  view3d(-0.15, -10.0, 7.0, "ABS");
	// define the view point
	  vfoc3d(-0.15, -0.8, 0.0, "ABS");
	  // define the camera view angle
	  vang3d(11.5);
	// name the axes
	name("Time Domain Axis","x");
	name("Frequency Domain Axis","y");
	name("Amplitude Axis","z");
	// paint the titles
	if (title1.equals(null))
	  title1 = "Christoph Lauer Engineering (c) Data-Visualization-Toolbox";
	titlin(title1,1);
	if (title2.equals(null))
	  title2 = "3D surface/countour plotter";
	titlin(title2,2);
	if (!title3.equals(null))
	  titlin(title3,3);
	if (!title4.equals(null))
	  titlin(title4,4);
	// enalble the 3d light
	light("ON");
	// set the font height
	height(25);
	// init the cube axis system
	graf3d(xMin, xMax, xMin, (xMax-xMin)/8.0, yMin, yMax, yMin, (yMax-yMin)/8.0, zMin,zMax,zMin,(zMax-zMin)/5.0);
  
	// DISLIN level 3
	height(36);
	title();
	height(25);
	nograf();
	// define the position of the projection in the 3-Dimensional space
	grfini (-2.25, -1.5, -1.0, 2.25, -1.5, -1.0, 2.25, 1.5, -1.0);
	// the borders of the contour plot
	graf (xMin, xMax, xMin, (xMax-xMin)/8.0, yMin, yMax, yMin, (yMax-yMin)/8.0);
	// paint the contour plot
	conshd (xray, lengthX, yray, lengthY, zmat, zlev, nlev);
	// draw a border arround the contour plot
	frame(5);
	box2d ();
	reset ("nograf");
	grffin ();
	// draw the surface plott
	if (smooth == true)
	  shdmod("smooth","surface");
	surshd(xray, lengthX, yray, lengthY, zmat);
	// finite dislin
	disfin();
  
	// waste the grabage
	zmat = null;
	xray = null;
	yray = null;
  }

//------------------------------------------------------------------------------------------------

// *
//  * This function scales the three axes decimal point number resolution corresponding to the intervall
//  * of the borders.
//  *
//  * @param xMin   The lower X-Axis border.
//  * @param xMax   The Upper X-Axis border.
//  * @param yMin   The lower Y-Axis border.
//  * @param yMax   The Upper Y-Axis border.
//  * @param zMin   The lower Z-Axis border.
//  * @param zMax   The Upper Z-Axis border.
//  

  //------------------------------------------------------------------------------------------------
  
  public static void autoScaleAxesNumberOfDecimalPoints(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax)
  {
	labdig(0,"x");
	if ((xMax-xMin) < 100.0)
		labdig(1,"x");
	if ((xMax-xMin) < 10.0)
		labdig(2,"x");
	if ((xMax-xMin) < 1.0)
		labdig(3,"x");
	if ((xMax-xMin) < 0.1)
		labdig(4,"x");
	if ((xMax-xMin) < 0.01)
		labdig(5,"x");
	if ((xMax-xMin) < 0.001)
		labdig(6,"x");
	if ((xMax-xMin) < 0.0001)
		labdig(7,"x");
	if ((xMax-xMin) < 0.00001)
		labdig(8,"x");
	if ((xMax-xMin) < 0.000001)
		labdig(9,"x");
	labdig(0,"y");
	if ((yMax-yMin) < 100.0)
		labdig(1,"y");
	if ((yMax-yMin) < 10.0)
		labdig(2,"y");
	if ((yMax-yMin) < 1.0)
		labdig(3,"y");
	if ((yMax-yMin) < 0.1)
		labdig(4,"y");
	if ((yMax-yMin) < 0.01)
		labdig(5,"y");
	if ((yMax-yMin) < 0.001)
		labdig(6,"y");
	if ((yMax-yMin) < 0.0001)
		labdig(7,"y");
	if ((yMax-yMin) < 0.00001)
		labdig(8,"y");
	if ((yMax-yMin) < 0.000001)
		labdig(9,"y");
	labdig(0,"z");
	if ((zMax-zMin) < 100.0)
		labdig(1,"z");
	if ((zMax-zMin) < 10.0)
		labdig(2,"z");
	if ((zMax-zMin) < 1.0)
		labdig(3,"z");
	if ((zMax-zMin) < 0.1)
		labdig(4,"z");
	if ((zMax-zMin) < 0.01)
		labdig(5,"z");
	if ((zMax-zMin) < 0.001)
		labdig(6,"z");
	if ((zMax-zMin) < 0.0001)
		labdig(7,"z");
	if ((zMax-zMin) < 0.00001)
		labdig(8,"z");
	if ((zMax-zMin) < 0.000001)
		labdig(9,"z");
  }

//------------------------------------------------------------------------------------------------

} // class DataVisualiser

//------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------

//#endif // CLAUER_IO_DATA_PLOTTER
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace clauer
//C++ TO JAVA CONVERTER TODO TASK: Only the namespaces at the beginning of the file can be converted to the Java 'package' for this file:
//ORIGINAL LINE: namespace io

//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------

// the screen resolution must be define for the placement of the window on the screen
//#define SCREEN_WIDTH 1440
//#define SCREEN_HEIGHT 800
// the dimension of the image and window on the screen (should be 16/9)
//#define IMAGE_WIDTH 1200
//#define IMAGE_HEIGHT 675

//------------------------------------------------------------------------------------------------


