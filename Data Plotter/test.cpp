/**
 * This simple test Program illustrates the usage of the data plotter io functions.
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>
#include <cmath>

// local headers  
#include "data_plotter.h"
#include "../Math Utilities/math_utilities.h"

//--------------------------------------------------------------------------------------

using namespace std;

using namespace clauer::io;

//--------------------------------------------------------------------------------------

#define DATA_LENGTH 1024
#define MESH_LENGTH 50

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // splash screen 
  std::cout << "\n**********************************************" << std::endl;
  std::cout << "*** This is the DATA VISLUALISER function ***" << std::endl;
  std::cout << "***   (c) Christoph Lauer Engineering     ***" << std::endl;
  std::cout << "*********************************************" << std::endl << std::endl;
  
  // instanciate the 2D plot data
  double* data2d = new double[DATA_LENGTH]; 
  for (int i=0; i<DATA_LENGTH; i++)
    data2d[i] = sin(static_cast<double>(i)/20.0);

  // instanciate the first 3D plot data
  double ** data3d = new double*[MESH_LENGTH];
  for (int i=0; i<MESH_LENGTH; i++)
  {
    data3d[i] = new double[MESH_LENGTH];
    for (int j=0; j<MESH_LENGTH; j++)
      data3d[i][j] = static_cast<double>(i*j);
  }

  // instanciate the second 3D plot data
  double ** data3d_2 = new double*[MESH_LENGTH];
  for (int i=0; i<MESH_LENGTH; i++)
  {
    data3d_2[i] = new double[MESH_LENGTH];
    for (int j=0; j<MESH_LENGTH; j++)
      data3d_2[i][j] = sin(static_cast<double>(i)/5) + sin(static_cast<double>(j)/5);
  }
  
  // draw the 2D fucntion plot
  DataPlotter::simple2DPlot(data2d, DATA_LENGTH);
  DataPlotter::simple2DPlot(data2d, DATA_LENGTH,0.0,DATA_LENGTH/20.0,0.0,0.0,false,true,true,"Samples","Amplitude","Sinus Function", "f(x) = sin(x)", "Cross Axis Coord System", "Without the inner Grid");
  cout << "Save the last image to file..." << endl;
  DataPlotter::simple2DPlot(data2d, DATA_LENGTH,0.0,0.0,0.0,0.0,true);
  DataPlotter::simple2DPlot(data2d, DATA_LENGTH,0.0,0.0,0.0,0.0,true,true,true,"Samples","Amplitude","Sinus Function", "f(x) = sin(x)");
  
  // draw the 3d contour plot
  DataPlotter::simple3DPlot(data3d, MESH_LENGTH, MESH_LENGTH,0,0,0,0,false,false,false,"Simple Hill Distribution Function of the CLAUER Contour-Ploter","25x25 Sampels Matrix","No Interpolation", "In the Interval from [0...1]");
	std::cout << std::endl << "!!! Next Plaot Function Test take time --> don't break !!!" << std::endl << std::endl;
  DataPlotter::simple3DPlot(data3d, MESH_LENGTH, MESH_LENGTH,0,0,0,0,false,true,false ,"Simple Hill Distribution Function of the CLAUER Contour-Ploter","25x25 Sampels Matrix","automatic Interpolation", "In the Interval from [0...1]");

  // draw the 3d surfact/contour plot
	DataPlotter::extended3DPlot(data3d,MESH_LENGTH, MESH_LENGTH);

  // draw the 3d surfact/contour plot
	DataPlotter::extended3DPlot(data3d_2,MESH_LENGTH, MESH_LENGTH);
	DataPlotter::extended3DPlot(data3d_2,MESH_LENGTH, MESH_LENGTH,0,0,0,0,false,true);
  
  // waste the grabage
  delete[] data2d;
  delete[] data3d;
  
}

//--------------------------------------------------------------------------------------
