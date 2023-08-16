
CC     = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer

rebuildAndRun: rebuild run

rebuild: clean all 

all: wavelet_packet_decomposition.o
	$(CC)  $(CFLAGS) -o test wavelet_packet_decomposition.o ../Wave\ File\ Handler/wave_file_handler.cpp  test.cpp

wavelet_paket_decomposition.o: 
	$(CC) -c $(CFLAGS) wavelet_packet_decomposition.cpp

clean:
	rm -f *.o
	rm -f *.exe
	rm -f test
	rm -f *~
  
run:
	./test

compileAndRun: all run

edit:
	vi Makefile

