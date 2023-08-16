/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    digital_filter.h
 * @class   DigitalFilter
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This class implemts digital filters (filter with critical damping, Bessel, Butterworth, Tschebyscheff)
 * @see     http://en.wikipedia.org/wiki/Digital_filter
 * @see     http://en.wikipedia.org/wiki/Digital_biquad_filter
 * @todo    finished so far
 */

//------------------------------------------------------------------------------------------------
 
// Local headers
#include "digital_filter.h"
#include "../Math Utilities/math_utilities.h"

// C Langunage Library headers
#include <cmath>

// C++ Langunage Library headers
#include <iostream>
#include <assert.h>
 
//------------------------------------------------------------------------------------------------
 
namespace clauer
{
namespace math
{

  const double clauer::math::DigitalFilter::biquadLowPassFilters[5][4][4] = 
  {
    { // The Critical Damping Filter
      {1.2872, 0.0   , 0.0    , 0.0   }, 
      {0.8700, 0.8700, 0.0    , 0.0   },
      {0.6999, 0.6999, 0.6999 , 0.0   },
      {0.6017, 0.6017, 0.6017 , 0.6017}
    },
    { // The Bessel-Filter
      {1.3617, 0.0   , 0.0    , 0.0   }, 
      {1.3397, 0.7743, 0.0    , 0.0   },
      {1.2217, 0.9686, 0.5131 , 0.0   },
      {1.1112, 0.9754, 0.7202 , 0.3728}
    },
    { // The Butterworth-Filter
      {1.4142, 0.0f   , 0.0   , 0.0   }, 
      {1.8478, 0.7654f, 0.0   , 0.0   },
      {1.9319, 1.4142f, 0.5176, 0.0   },
      {1.9616, 1.6629f, 1.1111, 0.3902}
    },
    { // Tschebyscheff-Filter, 1dB
      {1.3022, 0.0   , 0.0    , 0.0   }, 
      {2.5904, 0.3039, 0.0    , 0.0   },
      {3.8437, 0.6292, 0.1296 , 0.0   },
      {5.1019, 0.8916, 0.2806 , 0.0717}
    },

  };
    
  const double clauer::math::DigitalFilter::biquadHighPassFilters[5][4][4] =
  {
    { // The Critical Damping Filter
      {0.4142, 0.0   , 0.0   , 0.0    }, 
      {0.1892, 0.1892, 0.0   , 0.0    },
      {0.1225, 0.1225, 0.1225, 0.0    },
      {0.0905, 0.0905, 0.0905, 0.0905 }
    },
    { // The Bessel-Filter
      {0.6180, 0.0   , 0.0   , 0.0    }, 
      {0.4889, 0.3890, 0.0   , 0.0    },
      {0.3887, 0.3505, 0.2756, 0.0    },
      {0.3162, 0.2979, 0.2621, 0.2087 }
    },
    { // The Butterworth-Filter
      {1.0   , 0.0   , 0.0   , 0.0    }, 
      {1.0   , 1.0   , 0.0   , 0.0    },
      {1.0   , 1.0   , 1.0   , 0.0    },
      {1.0   , 1.0   , 1.0   , 1.0    }
    },
    { // The Tschebyscheff-Filter, 1db
      {1.5515 , 0.0   , 0.0   , 0.0   }, 
      {4.1301 , 1.1697, 0.0   , 0.0   },
      {8.5529 , 1.9124, 1.0766, 0.0   },
      {14.7608, 3.0426, 1.4334, 1.0432}
    }
  };
  
//------------------------------------------------------------------------------------------------

void clauer::math::DigitalFilter::applyFilter(double* inputSamples, const int length, const int sampleRate, const clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE type, const double cutOffFrequency, const int filterOrder, bool highPass)
{
  // assume that the correct values are given
	assert(length > 3);
	assert(filterOrder==2 || filterOrder==4 || filterOrder==6 || filterOrder==8);
	assert(cutOffFrequency > 1.0E-10 );
	assert(cutOffFrequency < 1.0E10 );
	assert(2.0*cutOffFrequency < sampleRate );
	assert(cos(PI*cutOffFrequency/sampleRate) > 1.0E-10 );
	assert(sin(PI*cutOffFrequency/sampleRate) / cos (PI *cutOffFrequency/sampleRate) > 1E-10 );
  
  // declare and initialize some local variables
	double	wa, ai, bi;
	double	c, d, e;
  double lastInputValues[8][3];
  double lastOutputValues [8][3];
  for (int k=0; k<8; k++)
    for (int l=0; l<3; l++)
    {
      lastInputValues [k][l] = 0.0;
      lastOutputValues[k][l] = 0.0;
    }    
  wa = std::sin(PI*cutOffFrequency/sampleRate) / std::cos (PI*cutOffFrequency/sampleRate);  
  int iFilOrd = (filterOrder / 2) - 1; // for indexing reasons
  
  // for all two orders 
  for (int j=0; j<=iFilOrd; j++) 
  {
    // init some values
    if (highPass == false)
    {
      ai = biquadLowPassFilters[type][iFilOrd][j]  / wa;
      bi = biquadHighPassFilters[type][iFilOrd][j] / (wa*wa);
    }
    else
    {
      ai = biquadLowPassFilters[type][iFilOrd][j]  * wa;
      bi = biquadHighPassFilters[type][iFilOrd][j] * (wa*wa);
    }
    c = 1.0 / (1.0 + ai + bi);
    if(highPass == false)
      d = 2.0 * (1.0 - bi) * c;
    else
      d = 2.0 * (bi - 1.0) * c;
    e = (1.0 - ai + bi) * c;
    
    // this special flag here initializes the filter coefficients

    // write the old input values
    lastInputValues[j][2] = lastInputValues[j][1];
    lastInputValues[j][1] = lastInputValues[j][0];
    lastInputValues[j][0] = inputSamples[0];
    // shift the old output values
    lastOutputValues[j][2] = lastOutputValues[j][1];
    lastOutputValues[j][1] = lastOutputValues[j][0];
    if (highPass == false)
      inputSamples[0] =  c * (lastInputValues[j][0] + 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e *  lastOutputValues[j][2];
    else 
      inputSamples[0] =  c * (lastInputValues[j][0] - 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e *  lastOutputValues[j][2];
    lastOutputValues[j][0] = inputSamples[0];
    // shift the old input values
    lastInputValues[j][2] = lastInputValues[j][1];
    lastInputValues[j][1] = lastInputValues[j][0];
    lastInputValues[j][0] = inputSamples[1];
    // shift the old output values
    lastOutputValues[j][2] = lastOutputValues[j][1];
    lastOutputValues[j][1] = lastOutputValues[j][0];
    if (highPass == false)
      inputSamples[1] = c * (lastInputValues[j][0] + 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e * lastOutputValues[j][2];
    else
      inputSamples[1] = c * (lastInputValues[j][0] - 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * lastOutputValues[j][1] - e * lastOutputValues[j][2];
    // apply here the core filter
    for (int  i=2; i<length; i++)
    {
      // write the input values
      lastInputValues[j][2] = lastInputValues[j][1];
      lastInputValues[j][1] = lastInputValues[j][0];
      lastInputValues[j][0] = inputSamples[i];
      // shifting of the input sampels and differ here between the low and high pass filter
      if (highPass == false)
        // the low pass
        inputSamples[i] =  c * (lastInputValues[j][0] + 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * inputSamples[i-1] - e * inputSamples[i-2];
      else
        // the high pass
        inputSamples[i] =  c * (lastInputValues[j][0] - 2.0 * lastInputValues[j][1] + lastInputValues[j][2]) - d * inputSamples[i-1] - e * inputSamples[i-2];
    }
  }
}
  
//------------------------------------------------------------------------------------------------
 
void clauer::math::DigitalFilter::bandPassFilter(double* inputSamples, int length, int sampleRate, const clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, const int filterOrder)
{
  // make sure that
  assert(cutOffFrequencyHigh > cutOffFrequencyLow);

  // call first the low pass
  applyFilter(inputSamples, length, sampleRate, type, cutOffFrequencyHigh, filterOrder, false);
  // then the high pass filter
  applyFilter(inputSamples, length, sampleRate, type, cutOffFrequencyLow, filterOrder, true);
}
 
//------------------------------------------------------------------------------------------------

void clauer::math::DigitalFilter::bandStopFilter(double* inputSamples, int length, int sampleRate, const clauer::math::DigitalFilter::DIGITAL_FILTER_TYPE type, double cutOffFrequencyLow, double cutOffFrequencyHigh, const int filterOrder)
{
  // make sure that
  assert(cutOffFrequencyHigh > cutOffFrequencyLow);
  
  // first allocate the needed vectors
  double* low   = new double[length];
  double* hight = new double[length];
  
  // init the new vectors with the signal
  std::memcpy(low, inputSamples, length*sizeof(double));
  std::memcpy(hight, inputSamples, length*sizeof(double));
  
  // call first the low pass
  applyFilter(low, length, sampleRate, type, cutOffFrequencyLow, filterOrder, false);
  // then the high pass filter
  applyFilter(hight, length, sampleRate, type, cutOffFrequencyHigh, filterOrder, true);
  
  // copy the results together
  for (int i=0; i<length; i++)
    inputSamples[i] = low[i] + hight[i];
  
  // wast the grabage
  delete[] low;
  delete[] hight;
}
 
//------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
