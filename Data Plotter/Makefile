CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer -Wall 
DISLIN_COMPILE_FLAGS = -Ilib -lgdi32
DISLIN_LINK_FLAGS = lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuild: clean test

test: data_plotter.o
	$(CC) $(CFLAGS) data_plotter.o $(DISLIN_LINK_FLAGS) test.cpp -o test

data_plotter.o: 
	$(CC) $(CFLAGS) $(DISLIN_COMPILE_FLAGS) -c data_plotter.cpp

clean:
	rm -f *.o
	rm -f *.exe
	rm -f test
	rm -f testOSX.exe
	rm -f *~
	rm -f dislin*
  
run:
	./test

edit:
	vi Makefile
