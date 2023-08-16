CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer 
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuild: clean test

test: cepstrum.o
	$(CC) $(CFLAGS) ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp ../Fourier\ Transformation/fourier_transformation.cpp cepstrum.o test.cpp -o test

cepstrum.o:
	$(CC) $(CFLAGS) -c cepstrum.cpp 

clean:
	rm -f *.o
	rm -f *.exe
	rm -f test
	rm -f *~
	rm -f dislin*
  
run:
	./test

edit:
	vi Makefile

