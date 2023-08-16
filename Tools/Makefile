CC = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer 
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuild: clean signalGenerator

signalGenerator: 
	$(CC) $(CFLAGS) ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp SignalGenerator.cpp -o signalGenerator

runSignalGenerator:
	./signalGenerator

clean:
	rm -f *.o
	rm -f *.exe
	rm -f test
	rm -f *~
	rm -f dislin*

edit:
	vi Makefile

