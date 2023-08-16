/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    data_plotter.h
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

#ifndef CLAUER_IO_DATA_PLOTTER
#define CLAUER_IO_DATA_PLOTTER

//------------------------------------------------------------------------------------------------

#include <cstdio>

//------------------------------------------------------------------------------------------------

namespace clauer
{
namespace io
{
 
//------------------------------------------------------------------------------------------------
 
/**
 * The class collects all the useful function for the plotting of datat structures into image files,
 * on the screen and onto the printer. Therefor we use the dislin programming library from the Max 
 * Planck Institute for Solar System Research in Lindau/Karlsruhe and would linke tosay thanks to the 
 * author Helmut Michels. This class prvides the caller with functions for the plotting of one and 
 * more dimensional functions and plots onto images, the screen and the printer. 
 */
class DataPlotter
{

//------------------------------------------------------------------------------------------------

public:

//------------------------------------------------------------------------------------------------

 /**
  * This fucntion provides the caller with a simple plotting fucntion. If no further values are given 
  * apart from the double array and the length of the array the function will autoscale the plot and 
  * draw an image. This function can simple be used with only the data and the length where the whole
  * plot is generated with the default values and the y axis will be autoscaled. If the min/max settings
  * for the axis values will be set to 0.0, the whole plot will be autoscaled. The png parameter will
  * save the file into a file named dislin.png where additional files will be saved in files named 
  * dislin_1.png, dislin_2.png and so on. If the png flag is not set the file will be displayed on 
  * the screen. The default resolution for the file is 1200x675. Th whole plot will be plotted with the
  * default colors white/grey/green for the title/grid/graph.
  *
  * @param data         The array of values to be plotted
  * @param length       The length of the array to be plotted
  * 
  * NOTE THAT ALL THE FOLLOWING PARAMETERS ARE OPTIONAL
  * @param png          If this flag is set, the image will be stored into an PNG file named "dislin.png".
  *                     If the file exist the name will be become and postfix with corresponding number
  * @param crossCoord   Switches between the outer box coord system and the midlle placed cross system.
  * @param noGrid       Disables the plot inner a[Bxis grid
  * @param xMin         The minimum of the X-Axis.
  * @param yxax         The maximum of the X-Axis
  * @param yMin         The minimum of the Y-Axis
  * @param yMax         The maximum of the Y-Axis
  * @param xAxisName    The name label of the X-Axis
  * @param yAxisName    The name label of the Y-Axis
  * @param title1       The first line of the title
  * @param title2       The second line of the title
  * @param title3       The third line of the title
  * @param title4       The fourth line of the title
  * @param fileName     The File Name, if no is given the files are stored with the name dislin, dislin_1, dislin_2....
  */
  static void simple2DPlot(
                            const double* data,
                            const int length,
                            double xMin = 0.0,
                            double xMax = 0.0,
                            double yMin = 0.0,
                            double yMax = 0.0,
                            const bool png = false,
                            const bool crossCoord = false,
                            const bool noGrid = false,
                            const char* xAxisName = NULL,
                            const char* yAxisName = NULL,
                            const char* title1 = NULL,
                            const char* title2 = NULL,
                            const char* title3 = NULL,
                            const char* title4 = NULL,
                            const char* fileName = NULL
                          );

//------------------------------------------------------------------------------------------------

 /**
  * This function provies a simple interface for the plotting of three dimensional data matrices like
  * time-frequency analysis representions for the specrogram and the wigner ville transformations. The
  * simple interface with no additional parameters can be used to plot the matrice autonomously. 
  * The extended interface with the scling functions and the interpolation/png/log and title information
  * can be used to plot a more detailed matrice. This function based on the DISLIN library and calls 
  * the painting functions of the DISLIN library. The default image and screen size for the images are
  * 1000x675 pixels. The whole plot will be painted with the default rainbow color spectrum.
  *
  * @param data            The data martice must be in the format double** and will be resorted internally 
  *                        into the time-frequency representation which will be plotted onto the screen.
  * @param lengthX         The length of the time domain input signal in samples.
  * @param lengthY         The length of the frequency representaion in samples.
  * @param xMin            The lower X-Achis scaling factor.
  * @param xMax            The upper X-Achis scaling factor.
  * @param yMin            The lower Y-Achis scaling factor.
  * @param yMax            The upper Y-Achis scaling factor.
  * @param png             If this flag is set, the image will be stored into an PNG file named "dislin.png".
  *                        If the file exist the name will be become a postfix with corresponding number
  * @param autiInterpolate If this flag is set to true, the image will be automaticall smoothed in both 
  *                        directions and the interpolation factors for both axes will be set automnomous.
  * @param logPlot         If this flag is set the z-axis will be automaticall transformed into the logarithmix
  *                        space. Not that here all values must be in the positive real interval. A value
  *                        lesser than 0 will be thro an exception.
  * @param title1          The first line of the title
  * @param title2          The second line of the title
  * @param title3          The third line of the title
  * @param title4          The fourth line of the title
  * @param fileName        The File Name, if no is given the files are stored with the name dislin, dislin_1, dislin_2....  */
  static void simple3DPlot( 
                            double** data,
                            const int lengthX,
                            const int lengthY,
                            double xMin = 0.0,
                            double xMax = 0.0,
                            double yMin = 0.0,
                            double yMax = 0.0,
                            const bool png     = false, 
                            const bool autoInterpolate = false,
                            const bool logPlot = false,
                            const char* title1 = NULL,
                            const char* title2 = NULL,
                            const char* title3 = NULL,
                            const char* title4 = NULL,
                            const char* fileName = NULL
                          );
 
//------------------------------------------------------------------------------------------------

 /**
  * This function implements a simple interface to a surface plot with a contour plot on the bottom 
  * of the surface. The function signature has the same structure like the simple2DPlot function 
  * defined above, so the programmer can be simple and fast switch between the surface/contour and 
  * the raw contour plot. In most cases the underlaying contour plot will not be seen because the 
  * surface lays in the most cases arround the zero point in the Z-Axis and covered the contour plot.
  *
  * @param data         The data martice must be in the format double** and will be resorted internally 
  *                     into the time-frequency representation which will be plotted onto the screen.
  * @param lengthX      The length of the time domain input signal in samples.
  * @param lengthY      The length of the frequency representaion in samples.
  * @param xMin         The lower X-Achis scaling factor.
  * @param xMax         The upper X-Achis scaling factor.
  * @param yMin         The lower Y-Achis scaling factor.
  * @param yMax         The upper Y-Achis scaling factor.
  * @param png          If this flag is set, the image will be stored into an PNG file named "dislin.png".
  *                     If the file exist the name will be become a postfix with corresponding number
  * @param smooth       This flag enabled the polygon smoothing if the resolution was low.
  * @param isoLines     This flag enables the painting of the Iso-Z lines.
  * @param title1       The first line of the title
  * @param title2       The second line of the title
  * @param title3       The third line of the title
  * @param title4       The fourth line of the title
  * @param fileName     The File Name, if no is given the files are stored with the name dislin, dislin_1, dislin_2....
  */
  static void extended3DPlot( 
                              double** data,
                              const int lengthX,
                              const int lengthY,
                              double xMin = 0.0,
                              double xMax = 0.0,
                              double yMin = 0.0,
                              double yMax = 0.0,
                              const bool  png      = false,
                              const bool  smooth   = false,
                              const bool  isoLines = false,
                              const char* title1   = NULL,
                              const char* title2   = NULL,
                              const char* title3   = NULL,
                              const char* title4   = NULL,
                              const char* fileName = NULL
                            );  

//------------------------------------------------------------------------------------------------

 /**
  * This function scales the three axes decimal point number resolution corresponding to the intervall
  * of the borders.
  *
  * @param xMin   The lower X-Axis border.
  * @param xMax   The Upper X-Axis border.
  * @param yMin   The lower Y-Axis border.
  * @param yMax   The Upper Y-Axis border.
  * @param zMin   The lower Z-Axis border.
  * @param zMax   The Upper Z-Axis border.
  */
  static void autoScaleAxesNumberOfDecimalPoints(const double xMin,const double xMax, const double yMin, const double yMax, const double zMin, const double zMax);

//------------------------------------------------------------------------------------------------

}; // class DataVisualiser
 
//------------------------------------------------------------------------------------------------
 
} // namespace io

} // namespace clauer

//------------------------------------------------------------------------------------------------

#endif // CLAUER_IO_DATA_PLOTTER
