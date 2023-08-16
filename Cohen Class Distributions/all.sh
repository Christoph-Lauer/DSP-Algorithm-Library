#! /bin/sh

# first remove all old image files
/usr/bin/find . -name "*.png" -exec rm '{}' ';';


# do for all given files
for arg do
  # generate ZAMD
  ../Short\ Time\ Fourier\ Transformation/test $arg save;
  # generate the SPWVD
  ./Smoothed\ Pseudo\ Wigner\ Ville\ Distribution/test $arg save;
  # generate the CHD
  ./Choi\ Williams\ Distribution/test $arg save;
  # generate ZAMD
  ./Zaho\ Atlas\ Marks\ Distribution/test $arg save;
  # generate PMHD
  ./Pseudo\ Margenau\ Hill\ Distribution/test $arg save;
done
