CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer -Wall

rebuildAndRun: rebuild run

rebuild: clean test

test: harmonic_analyse.o
	$(CC) $(CFLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp ../Fourier\ Transformation/fourier_transformation.cpp harmonic_analyse.o test.cpp -o test

harmonic_analyse.o:
	$(CC) $(CFLAGS) -c harmonic_analyse.cpp

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

