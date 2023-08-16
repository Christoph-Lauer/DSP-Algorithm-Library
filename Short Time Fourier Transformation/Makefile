CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer -Wall
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuildDataPlotter:
	cd ../Data\ Plotter/; make clean; make data_plotter.o
	cd ../Short\ Time\ Fourier\ Transformation/

rebuild: clean test

test: short_time_fourier_transformation.o
	$(CC) $(CFLAGS) ../Data\ Plotter/data_plotter.o short_time_fourier_transformation.o $(DISLIN_LINK_FLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp ../Fourier\ Transformation/fourier_transformation.cpp test.cpp -o test

short_time_fourier_transformation.o:
	$(CC) $(CFLAGS) -c short_time_fourier_transformation.cpp

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

