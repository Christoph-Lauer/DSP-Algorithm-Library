CC = g++
CFLAGS_DEBUG = -pipe -g -Wall 
CFLAGS_RELEASE = -pipe -O3 -ansi -fomit-frame-pointer 
DISLIN_LINK_FLAGS = ../../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL


default: rebuildAndRunRelease


rebuildAndRun: rebuild run

rebuildAndRunRelease: rebuildRelease run


rebuild: clean test

rebuildRelease: clean testRelease


test: zaho_atlas_marks_distribution.o
	$(CC) $(CFLAGS_DEBUG) ../../Data\ Plotter/data_plotter.o zaho_atlas_marks_distribution.o $(DISLIN_LINK_FLAGS) ../../Wave\ File\ Handler/wave_file_handler.cpp ../../Fourier\ Transformation/fourier_transformation.cpp test.cpp -o test ../../Envelope/envelope.cpp ../../Normalization/normalizer.cpp

testRelease: zaho_atlas_marks_distributionRelease.o
	$(CC) $(CFLAGS_RELEASE) ../../Data\ Plotter/data_plotter.o zaho_atlas_marks_distribution.o $(DISLIN_LINK_FLAGS) ../../Wave\ File\ Handler/wave_file_handler.cpp ../../Fourier\ Transformation/fourier_transformation.cpp test.cpp -o test ../../Envelope/envelope.cpp ../../Normalization/normalizer.cpp


zaho_atlas_marks_distribution.o:
	$(CC) $(CFLAGS_DEBUG) -c zaho_atlas_marks_distribution.cpp

zaho_atlas_marks_distributionRelease.o:
	$(CC) $(CFLAGS_RELEASE) -c zaho_atlas_marks_distribution.cpp

rebuildDataPlotter:
	cd ../../Data\ Plotter/; make clean; make data_plotter.o
	cd ../../Spectrogram/


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

