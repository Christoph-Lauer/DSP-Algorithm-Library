CC     = g++
CFLAGS_DEBUG = -pipe -g
CFLAGS_RELEASE = -pipe -O3 -ansi -fomit-frame-pointer
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuildAndRunRelease: rebuildRelease run

rebuild: clean all 

rebuildRelease: clean allRelease 

all: envelope.o
	$(CC)  $(CFLAGS_DEBUG) -o test envelope.o ../Data\ Plotter/data_plotter.o test.cpp $(DISLIN_LINK_FLAGS) ../Fourier\ Transformation/fourier_transformation.cpp ../Wave\ File\ Handler/wave_file_handler.cpp

allRelease: envelopeRelease.o
	$(CC)  $(CFLAGS_RELEASE) -o test envelope.o ../Data\ Plotter/data_plotter.o test.cpp $(DISLIN_LINK_FLAGS) ../Fourier\ Transformation/fourier_transformation.cpp ../Wave\ File\ Handler/wave_file_handler.cpp

envelope.o: 
	$(CC) -c $(CFLAGS_DEBUG) envelope.cpp

envelopeRelease.o: 
	$(CC) -c $(CFLAGS_DEBUG) envelope.cpp

clean:
	rm -f *.o
	rm -f *.exe
	rm -f test
	rm -f *~
  
run:
	./test

edit:
	vi Makefile

