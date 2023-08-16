CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer -Wall
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuild: clean test

test:
	$(CC) $(CFLAGS) ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) test.cpp -o test

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

