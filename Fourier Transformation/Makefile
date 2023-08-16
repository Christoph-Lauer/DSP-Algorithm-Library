CC     = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer -g
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL 

rebuild: clean TestFourierTransformation run

rebuildSpectrum: clean spectrum runSpectrum

spectrum: 
	$(CC) $(CFLAGS) ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp fourier_transformation.cpp spectrum.cpp -o spectrum

TestFourierTransformation:
	$(CC) $(CFLAGS) fourier_transformation.cpp test.cpp -o TestFourierTransformation

clean:  
	rm -f TestFourierTransformation
	rm -f spectrum

run:
	./TestFourierTransformation

runSpectrum:
	./spectrum
