rebuild: clean build

build:	
	cd Math\ Utilities; make rebuild;
	cd Data\ Plotter; make;
	cd Cepstrum; make rebuild;
	cd Cohen\ Class\ Distributions; make;
	cd Correlation; make rebuild;
	cd Damping\ Constant; make rebuild;
	cd Digital\ Filter; make rebuild;
	cd Envelope; make rebuild;
	cd Fourier\ Transformation; make rebuild; make spectrum;
	cd Harmonic\ Analyse; make rebuild; 
	cd Impulse\ Response\ Extraction; make rebuild;
	cd Jitter\ Analysis; make rebuild;
	cd Linear\ Predictive\ Coding; make rebuild;
	cd Medianfrequency; make rebuild;
	cd Normalization; make rebuild;
	cd Polygonal\ Chain; make rebuild;
	cd Sample\ Rate\ Converter; make rebuild;
	cd Smooth; make rebuild;
	cd Wave\ File\ Handler; make rebuild;
	cd Wavelet\ Packet\ Decomposition; make rebuild;
	cd Wavelet\ Transformation; make rebuild;
	cd Zero\ Crossing\ Rate; make rebuild;
	cd Tools; make rebuild;

clean:
	find . -name "*.exe"       -exec rm '{}' ';';
	find . -name "*~"          -exec rm '{}' ';';
	find . -name "*.o"         -exec rm '{}' ';';
	find . -name "dislin*png"  -exec rm '{}' ';';
	find . -name "*.csv"       -exec rm '{}' ';';
	find . -name "*.pk"        -exec rm '{}' ';';
	find . -name "*.stackdump" -exec rm '{}' ';';
	find . -name "*.png"	     -exec rm '{}' ';';
	find . -name "Desktop.ini" -exec rm '{}' ';';
	find . -name "~*"          -exec rm '{}' ';';

packet: #clean
	cd ..; tar vcjf ALGORITHM.tbz ALGORITHM/*
