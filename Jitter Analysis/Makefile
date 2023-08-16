rebuildAndRun:	rebuild run

rebuild: clean TestJitterAnalysis

TestJitterAnalysis:
	g++ -O3 *.cpp ../Linear\ Predictive\ Coding/linear_predictive_coding.cpp ../Fourier\ Transformation/fourier_transformation.cpp ../Wave\ File\ Handler/wave_file_handler.cpp -o TestJitterAnalysis

clean:  
	rm -f TestJitterAnalysis

run:
	./TestJitterAnalysis
