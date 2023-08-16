DISLIN_LINK_FLAGS = ../Data\ Plotter/lib/dislin-11.5-rpi.a -lXm  -lXt  -lX11  -lGL

rebuildAndRun: rebuild run

rebuild: clean TestLinearPredictiveCoding spectrum

TestLinearPredictiveCoding:
	g++ -Wall linear_predictive_coding.cpp test.cpp ../Fourier\ Transformation/fourier_transformation.cpp -o TestLinearPredictiveCoding ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp;

spectrum:
	g++ -Wall linear_predictive_coding.cpp spectrum.cpp ../Fourier\ Transformation/fourier_transformation.cpp -o spectrum ../Data\ Plotter/data_plotter.o $(DISLIN_LINK_FLAGS) ../Wave\ File\ Handler/wave_file_handler.cpp;

clean:  
	rm -f TestLinearPredictiveCoding spectrum

run:
	./TestLinearPredictiveCoding
