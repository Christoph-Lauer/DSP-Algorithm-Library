CC     = g++
CFLAGS = -pipe -O3 -ansi -fomit-frame-pointer -Wall

rebuildAndRun: rebuild run

rebuild: clean all 

all: digital_filter.o
	$(CC)  $(CFLAGS) -o filter digital_filter.o test.cpp ../Wave\ File\ Handler/wave_file_handler.cpp


digital_filter.o: 
	$(CC) -c $(CFLAGS) digital_filter.cpp

clean:
	rm -f *.o
	rm -f filter
	rm -f *~
  
run:
	./filter

compileAndRun: all run

edit:
	vi Makefile

