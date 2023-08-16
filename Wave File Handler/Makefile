rebuild: clean TestWaveFileReader run

TestWaveFileReader:
	g++ -O3 test.cpp wave_file_handler.cpp -Wall -o TestWaveFileReader

clean:  
	rm -f TestWaveFileReader

run:
	./TestWaveFileReader
asciiToWave: clean
	g++ -O3 asciiToWave.cpp wave_file_handler.cpp -Wall -o asciiToWave

