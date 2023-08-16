CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer 

rebuildAndRun: rebuild run

rebuild: clean test

test: median_frequency.o
	$(CC) $(CFLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp ../Fourier\ Transformation/fourier_transformation.cpp median_frequency.o test.cpp -o test

median_frequency.o:
	$(CC) $(CFLAGS) -c median_frequency.cpp 

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

