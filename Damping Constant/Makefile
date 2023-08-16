CC     = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer
DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuild: clean all 

all: damping_constant.o
	$(CC) $(CFLAGS) -o damping damping_constant.o test.cpp ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Fourier\ Transformation/fourier_transformation.cpp ../Envelope/envelope.cpp ../Wave\ File\ Handler/wave_file_handler.cpp

damping_constant.o: 
	$(CC) -c $(CFLAGS) damping_constant.cpp

clean:
	rm -f *.o
	rm -f damping
	rm -f *~
	rm -f .stackdump
  
run:
	./damping
	./damping ../Tools/*.wav

compileAndRun: all run

edit:
	vi Makefile

