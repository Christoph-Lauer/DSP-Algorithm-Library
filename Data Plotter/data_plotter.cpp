/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    data_plotter.cpp
 * @class   DataPlotter
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   The class gives a basic interface for the visualisation of two and three dimensional curves
 * @see     http://en.wikipedia.org/wiki/Scientific_visualization
 * @see     http://en.wikipedia.org/wiki/Input/output
 * @see     http://www.mps.mpg.de/dislin/
 * @todo    finished so far.
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "data_plotter.h"
#include "../Math Utilities/math_utilities.h"

// DISLIN Plotting Library header from the Max Planck Institute for Solar System Research
#include "lib/dislin.h"

// C Langunage Library headers
#include <cmath>
#include <cfloat>

// C++ Language Libraray headers
#include <iostream>

//------------------------------------------------------------------------------------------------

using namespace std; 
using namespace clauer::math; 

//------------------------------------------------------------------------------------------------

namespace clauer
{
namespace io
{

//------------------------------------------------------------------------------------------------

// the screen resolution must be define for the placement of the window on the screen
#define SCREEN_WIDTH  1920
#define SCREEN_HEIGHT 1080
// the dimension of the image and window on the screen (should be 16/9)
#define IMAGE_WIDTH 1200
#define IMAGE_HEIGHT 675

//------------------------------------------------------------------------------------------------
 
void DataPlotter::simple2DPlot(
                                const double* yRay,
                                const int length,
                                double xMin,
                                double xMax,
                                double yMin,
                                double yMax,
                                const bool png,
                                const bool crossCoord,
                                const bool noGrid,
                                const char* xAxisName,
                                const char* yAxisName,
                                const char* title1,
                                const char* title2, 
                                const char* title3,
                                const char* title4,
                                const char* fileName
                              )
{
  // prepare the x axis 
  float* xRay = new float[length];
  if (xMax == 0.0 && xMin == 0.0)
    xMax = static_cast<double>(length);
  for (int i=0; i<length; i++)
    xRay[i] = xMin + static_cast<double>(i) / static_cast<double>(length) * (xMax - xMin);
    
  // cast to float (due changes in dislin)
  float* yRay_f = new float[length];
  for (int i=0; i<length; i++)
    yRay_f[i] = static_cast<float>(yRay[i]);
  // prepare the y axis
  if (yMax == 0.0 && yMin == 0.0)
  {
    yMax = -DBL_MAX;
    yMin = DBL_MAX;
    for (int i=0; i<length; i++)
    {
      if (yMax < yRay[i]) yMax = yRay[i];
      if (yMin > yRay[i]) yMin = yRay[i];
    }
  }
  // the curve is plotted a little bit under the borders
  yMin -= fabs(yMin/100.0);
  yMax += fabs(yMax/100.0);
  
  // DISLIN - level 0
  if (png == false)
    metafl ("CONS");
  else
    metafl ("PNG");
  // set the file name is provided
	if (fileName != NULL)
    setfil(fileName);
  page(4000, 2250);
  window((SCREEN_WIDTH-IMAGE_WIDTH)/2,(SCREEN_HEIGHT-IMAGE_HEIGHT)/2+100,IMAGE_WIDTH,IMAGE_HEIGHT);
  sclmod ("full");
  disini ();
  
  // DISLIN Level 1
  autoScaleAxesNumberOfDecimalPoints(xMin, xMax, yMin, yMax,0,0);
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
  if (xAxisName == NULL) 
    xAxisName = "X-Axis";
  name(xAxisName,"X");
  if (yAxisName == NULL)
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
  if (title1 == NULL)
    title1 = "Christoph Lauer Engineering(c) Data-Visualization-Toolbox";
  titlin(title1,1);
  if (title2 == NULL)
    title2 = "2D function plotter";
  titlin(title2,2);
  if (title3 != NULL)
    titlin(title3,3);
  if (title4 != NULL)
    titlin(title4,4);
  title();
  //linwid(10); // The line thickness...
  setrgb(0.7, 1.0, 0.0);
  curve(xRay, yRay_f, length);

  // DISLIN end
  disfin ();

  // waste the grabage
  delete[] xRay;
  delete[] yRay_f;
}
 
//------------------------------------------------------------------------------------------------
 
void DataPlotter::simple3DPlot(
                                double** data,
                                const int lengthX,
                                const int lengthY,
                                double xMin,
                                double xMax,
                                double yMin,
                                double yMax,
                                const bool png,
                                const bool autoInterpolate,
                                const bool logPlot,
                                const char* title1,
                                const char* title2,
                                const char* title3,
                                const char* title4,
                                const char* fileName
                              )
{
  // init the plot parameter values
  int PLOT_WIDTH  = 3100;
  int PLOT_HEIGHT = 1750;

  // prepare the x axis
  if (xMax==0.0 && xMin==0.0)
    xMax = static_cast<double>(lengthX);
  // prepare the y axis
  if (yMax==0.0 && yMin==0.0)
    yMax = static_cast<double>(lengthY);

  // prepare the z axis borders
  double zMin = +DBL_MAX;
  double zMax = -DBL_MAX;
  
  // first find the peaks
  for (int x=0; x<lengthX; x++)
    for (int y=0; y<lengthY; y++)
    {
      if(zMin > data[x][y]) zMin = data[x][y];
      if(zMax < data[x][y]) zMax = data[x][y];
    }
    
  // check for correct border conditions
  if (zMax == zMin) 
    {zMax += zMax / 100.0;zMin -= zMin / 100.0;}
  if (zMax == 0.0 && zMin == 0.0) 
    {zMax += 1.0; zMin -= 1.0;}
    
  // now rearrange the elements and logarithmize them in required
  float* resortedData = new float[lengthX*lengthY];
  for (int x=0; x<lengthX; x++)
    for (int y=0; y<lengthY; y++)
    {
      // resort the elements
      if (logPlot == true)
        resortedData[y+x*lengthY] = Utilities::lin2log(data[x][y]);
      else
        resortedData[y+x*lengthY] = data[x][y];
    }

  // normalize the peak values 
  if (logPlot == true)
  {
    zMin = Utilities::lin2log(zMin);
    zMax = Utilities::lin2log(zMax);
  }
  cout << "Automatic Scaling: min=" << zMin << "max=" << zMax << endl;
      
  // DISLIN level 0
  if (png == false)
    metafl ("CONS");
  else
    metafl ("PNG");
  // set the file name is provided
	if (fileName != NULL)
    setfil(fileName);
  page(4000, 2250);
  window((SCREEN_WIDTH-IMAGE_WIDTH)/2,(SCREEN_HEIGHT-IMAGE_HEIGHT)/2+100,IMAGE_WIDTH,IMAGE_HEIGHT);
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
  if (title1 == NULL)
    title1 = "Christoph Lauer Engineering (c) Data-Visualization-Toolbox";
  titlin(title1,1);
  if (title2 == NULL)
    title2 = "3D countour plotter";
  titlin(title2,2);
  if (title3 != NULL)
    titlin(title3,3);
  if (title4 != NULL)
    titlin(title4,4);
  title();
  // the number of interpolation points used for the plot
  int ixPts = 1;
  int iyPts = 1;
  // if the automatic interpolation is enabled calcualte the number of interpoation points autonomously
  if (autoInterpolate == true)
  {
    cout << "----> AUTOINTERPOLATION" << endl;
    if (lengthX < PLOT_WIDTH)
      ixPts = PLOT_WIDTH / lengthX;
    if (lengthY < PLOT_HEIGHT)
      iyPts = PLOT_HEIGHT / lengthY;
    cout << "Automatic Interpolation: ix=" << ixPts << " iyPts=" << iyPts << endl;
  }
  /*
  for (int i=0; i<256;i++)
  {
    double r,g,b;
    getind(i,&r,&g,&b);
    int ir = (int) (r * 255.5);
    int ig = (int) (g * 255.5);
    int ib = (int) (b * 255.5);
    cout << "new Color(" << ir << "," << ig << "," << ib << ")"<< std::endl;
  }
  */
  // draw the contour plot
  crvmat(resortedData, lengthX, lengthY, ixPts, iyPts);
  
  // DISLIN end
  disfin();
  
  // waste the grabage
  delete[] resortedData;
}

//------------------------------------------------------------------------------------------------

void DataPlotter::extended3DPlot(
                                  double** data,
                                  const int lengthX,
                                  const int lengthY,
                                  double xMin,
                                  double xMax,
                                  double yMin,
                                  double yMax,
                                  const bool  png,
                                  const bool  smooth,
                                  const bool  isoLines,
                                  const char* title1,
                                  const char* title2,
                                  const char* title3,
                                  const char* title4,
                                  const char* fileName
                                )
{
	// init some basic data structures
	float* zmat  = new float[lengthX*lengthY];
	float*  xray = new float[lengthX];
	float*  yray = new float[lengthY];
	int nlev      = 100;
	float zlev[100];

	// resort the array and determine the peaks
	double zMin = DBL_MAX;
	double zMax = -DBL_MAX;
  for (int i=0; i < lengthX; i++)
    for (int j=0; j < lengthY; j++)
    {
      // resort the data
      zmat[i*lengthY+j] = data[i][j];
      // find the peaks
      if (zMin > data[i][j]) zMin = data[i][j]; 
      if (zMax < data[i][j]) zMax = data[i][j];
    }

  // fill the X ray array
  for (int i=0; i<lengthX; i++)
    if (xMin == 0.0 && xMax == 0.0)
      xray[i]=static_cast<double>(i);
    else
      xray[i]= (xMax-xMin)*static_cast<double>(i)/static_cast<double>(lengthX);
  // fill the Y ray array
  for (int i=0; i<lengthY; i++)
    if (yMin == 0.0 && yMax == 0.0)
      yray[i]=static_cast<double>(i);
    else
      yray[i]= (yMax-yMin)*static_cast<double>(i)/static_cast<double>(lengthY);
  // adapt the plot borders
  if (xMin == 0.0 && xMax == 0.0)
    xMax = static_cast<double>(lengthX-1);
  if (yMin == 0.0 && yMax == 0.0)
    yMax = static_cast<double>(lengthY-1);
  
  // build the level color array
  double step = (zMax-zMin) / nlev;
  for (int i = 0; i < nlev; i++)
    zlev[i] = (float) (zMin + i * step); 
 
	// DISLIN level 0
  if (png == false)
    metafl ("CONS");
  else
    metafl ("PNG");
  // set the file name is provided
	if (fileName != NULL)
    setfil(fileName);
  // the size of the
  page(4000, 2250);
  window((SCREEN_WIDTH-IMAGE_WIDTH)/2,(SCREEN_HEIGHT-IMAGE_HEIGHT)/2+100,IMAGE_WIDTH,IMAGE_HEIGHT);
  imgfmt("RGB");
  disini();
 
  // DISLIN level 1
  winkey("RETURN");
	// set the length of the coordinate system
  axslen(4000*2/3,1625);
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
  if (title1 == NULL)
    title1 = "Christoph Lauer Engineering (c) Data-Visualization-Toolbox";
  titlin(title1,1);
  if (title2 == NULL)
    title2 = "3D surface/countour plotter";
  titlin(title2,2);
  if (title3 != NULL)
    titlin(title3,3);
  if (title4 != NULL)
    titlin(title4,4);
  // enalble the 3d light
  light("ON");
  // set the font height
  height(25);
  // init the cube axis system
  graf3d(xMin, xMax, xMin, (xMax-xMin)/8.0, yMin, yMax, yMin, (yMax-yMin)/8.0, zMin ,zMax ,zMin,(zMax-zMin)/5.0);

  // DISLIN level 3
  height(36);
  title();
  height(25);
  nograf();
  // define the position of the projection in the 3-Dimensional space
  grfini (-2.25, -1.5, -1.0,    2.25, -1.5, -1.0,    2.25, 1.5, -1.0);  
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
  delete[] zmat;
  delete[] xray;
  delete[] yray;
}

//------------------------------------------------------------------------------------------------

void DataPlotter::autoScaleAxesNumberOfDecimalPoints(const double xMin,const double xMax, const double yMin, const double yMax, const double zMin, const double zMax)
{
  labdig(0,"x");
  if ( (xMax-xMin) < 100.0) labdig(1,"x");
  if ( (xMax-xMin) < 10.0) labdig(2,"x");
  if ( (xMax-xMin) < 1.0) labdig(3,"x");
  if ( (xMax-xMin) < 0.1) labdig(4,"x");
  if ( (xMax-xMin) < 0.01) labdig(5,"x");
  if ( (xMax-xMin) < 0.001) labdig(6,"x");
  if ( (xMax-xMin) < 0.0001) labdig(7,"x");
  if ( (xMax-xMin) < 0.00001) labdig(8,"x");
  if ( (xMax-xMin) < 0.000001) labdig(9,"x");
  labdig(0,"y");
  if ( (yMax-yMin) < 100.0) labdig(1,"y");
  if ( (yMax-yMin) < 10.0) labdig(2,"y");
  if ( (yMax-yMin) < 1.0) labdig(3,"y");
  if ( (yMax-yMin) < 0.1) labdig(4,"y");
  if ( (yMax-yMin) < 0.01) labdig(5,"y");
  if ( (yMax-yMin) < 0.001) labdig(6,"y");
  if ( (yMax-yMin) < 0.0001) labdig(7,"y");
  if ( (yMax-yMin) < 0.00001) labdig(8,"y");
  if ( (yMax-yMin) < 0.000001) labdig(9,"y");
  labdig(0,"z");
  if ( (zMax-zMin) < 100.0) labdig(1,"z");
  if ( (zMax-zMin) < 10.0) labdig(2,"z");
  if ( (zMax-zMin) < 1.0) labdig(3,"z");
  if ( (zMax-zMin) < 0.1) labdig(4,"z");
  if ( (zMax-zMin) < 0.01) labdig(5,"z");
  if ( (zMax-zMin) < 0.001) labdig(6,"z");
  if ( (zMax-zMin) < 0.0001) labdig(7,"z");
  if ( (zMax-zMin) < 0.00001) labdig(8,"z");
  if ( (zMax-zMin) < 0.000001) labdig(9,"z");
}

//------------------------------------------------------------------------------------------------

} // namespace io

} // namepsace clauer
