CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer 
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuild: clean test

test: convolution.o
	$(CC) $(CFLAGS) ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Fourier\ Transformation/fourier_transformation.cpp convolution.o test.cpp -o test

convolution.o:
	$(CC) $(CFLAGS) -c convolution.cpp 

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

