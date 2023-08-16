/**
 * This simple test Program illustrates the usage of the envelope algorithmus.
 * NOTE: We did no take care on any memory leaks here !!!
 */

//--------------------------------------------------------------------------------------

// stl headers
#include <iostream>

// C headers
#include <cmath>
#include <cstring>

// local headers  
#include "envelope.h"
#include "../Data Plotter/data_plotter.h"
#include "../Wave File Handler/wave_file_handler.h"

//--------------------------------------------------------------------------------------

#define TEST_SIGNAL_LENGTH  500
#define PRINT_RESULT        true

//--------------------------------------------------------------------------------------

int main (int argc, char *argv[])
{
  // first generate a test signals
  double twoPeakSignal[TEST_SIGNAL_LENGTH];
  memset(twoPeakSignal, 0, TEST_SIGNAL_LENGTH * sizeof(double));
  twoPeakSignal[TEST_SIGNAL_LENGTH * 1 / 4] = 1.0;
  twoPeakSignal[TEST_SIGNAL_LENGTH * 2 / 4] = 1.0;
  twoPeakSignal[TEST_SIGNAL_LENGTH * 3 / 4] = 1.0;
  double sinPeriodSignal[TEST_SIGNAL_LENGTH];
  memset(sinPeriodSignal, 0, TEST_SIGNAL_LENGTH * sizeof(double));
  for (int i=TEST_SIGNAL_LENGTH/4; i<TEST_SIGNAL_LENGTH/2; i++)
    sinPeriodSignal[i] = sin(static_cast<double>(i));
  double sinSignal[TEST_SIGNAL_LENGTH];
  double sincSignal[TEST_SIGNAL_LENGTH];
  for (int i=0; i<TEST_SIGNAL_LENGTH; i++)
  {
    sinSignal[i] = std::sin(static_cast<double>(i)/10);
    if (i == TEST_SIGNAL_LENGTH/4)
      sincSignal[i] = 1.0;
    else
      sincSignal[i] = std::sin(static_cast<double>(i-TEST_SIGNAL_LENGTH/4))/static_cast<double>(i-TEST_SIGNAL_LENGTH/4);
  }
  
  // splash screen 
  std::cout << "\n******************************************" << std::endl;
  std::cout << "*** This is the ENVELOPE test function ***" << std::endl;
  std::cout << "***  (c) Christoph Lauer Engineering   ***" << std::endl;
  std::cout << "******************************************" << std::endl << std::endl;
  
  // check if there was any file given as argument
  if (argc > 1)
  {
  	bool error = false;
	  int sampleLength = 0; 
	  int sampleRate = 0;
	  double* fileSamples = clauer::io::WaveFileHandler::autoReadWaveFile(argv[1], sampleLength, sampleRate, error);
	  double* envelopeMAX = clauer::math::Envelope::calculateEnvelope(fileSamples, sampleLength, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
	  double* envelopeFIR = clauer::math::Envelope::calculateEnvelope(fileSamples, sampleLength, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
	  clauer::io::DataPlotter::simple2DPlot(fileSamples, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Time Signal", argv[1]);
	  clauer::io::DataPlotter::simple2DPlot(envelopeMAX, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX-Method", argv[1]);
	  clauer::io::DataPlotter::simple2DPlot(envelopeFIR, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR-Method", argv[1]);
  } 
  
  // if no file was given as argument do the stadart test's
  else
  {
	  // first test the hilbert transformation
	  double* hilbert = clauer::math::Envelope::fastHilbertTransform(sincSignal, TEST_SIGNAL_LENGTH);
	  clauer::io::DataPlotter::simple2DPlot(sincSignal, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","SINC Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(hilbert, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation");
	  double* hilbertBack = clauer::math::Envelope::fastHilbertTransform(hilbert, TEST_SIGNAL_LENGTH);
	  clauer::io::DataPlotter::simple2DPlot(hilbertBack, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Back Transformation");
	  hilbert = clauer::math::Envelope::fastHilbertTransform(sinSignal, TEST_SIGNAL_LENGTH);
	  clauer::io::DataPlotter::simple2DPlot(sinSignal, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","SIN Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(hilbert, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation");
	  hilbertBack = clauer::math::Envelope::fastHilbertTransform(hilbert, TEST_SIGNAL_LENGTH);
	  clauer::io::DataPlotter::simple2DPlot(hilbertBack, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Back Transformation");
	  
	  // next test with the two peaks test signal
	  std::cout << "FIRST THE TWO PEAK SIGNAL\n";  
	  double* envelopeFIR = clauer::math::Envelope::calculateEnvelope(twoPeakSignal, TEST_SIGNAL_LENGTH, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
	  double* envelopeMAX = clauer::math::Envelope::calculateEnvelope(twoPeakSignal, TEST_SIGNAL_LENGTH, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
	  hilbert = clauer::math::Envelope::fastHilbertTransform(twoPeakSignal, TEST_SIGNAL_LENGTH);
	  clauer::io::DataPlotter::simple2DPlot(twoPeakSignal, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(hilbert, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation of the Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(envelopeMAX, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX");
	  clauer::io::DataPlotter::simple2DPlot(envelopeFIR, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR");
	    
	  // nest test is the sin period signal
	  std::cout << "\nNEXT IS THE SINE PERIOD SIGNAL\n";
	  envelopeFIR = clauer::math::Envelope::calculateEnvelope(sinPeriodSignal, TEST_SIGNAL_LENGTH, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
	  envelopeMAX = clauer::math::Envelope::calculateEnvelope(sinPeriodSignal, TEST_SIGNAL_LENGTH, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
	  hilbert = clauer::math::Envelope::fastHilbertTransform(sinPeriodSignal, TEST_SIGNAL_LENGTH);
	  // plot the resullt
	  clauer::io::DataPlotter::simple2DPlot(sinPeriodSignal, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(hilbert, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Hilbert Transformation of the Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(envelopeMAX, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX");
	  clauer::io::DataPlotter::simple2DPlot(envelopeFIR, TEST_SIGNAL_LENGTH,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR");
	
	  // read the samples from the wave file
	  clauer::io::WaveFileHandler wfh;
	  bool error = false;
	  int sampleLength = 0; 
	  int sampleRate = 0;
	  double* fileSamples = wfh.autoReadWaveFile("../TEST_SIGNALS/resonance_analysis_test.wav", sampleLength, sampleRate, error);
	  envelopeFIR = clauer::math::Envelope::calculateEnvelope(fileSamples, sampleLength, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_FIR_LOW_PASS);
	  envelopeMAX = clauer::math::Envelope::calculateEnvelope(fileSamples, sampleLength, clauer::math::Envelope::ENVELOPE_CALCULATION_METHOD_ABSOLUTE_VALUE);
	  // // plot the resullt
	  clauer::io::DataPlotter::simple2DPlot(fileSamples, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Input Signal");
	  clauer::io::DataPlotter::simple2DPlot(envelopeMAX, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope MAX");
	  clauer::io::DataPlotter::simple2DPlot(envelopeFIR, sampleLength,0,0,0,0,false,false,false,"Time","Amplitude","Envelope FIR");
  }
}

//--------------------------------------------------------------------------------------
