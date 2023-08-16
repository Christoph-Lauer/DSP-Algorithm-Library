public class GlobalMembersAsciiToWave
{

	// local headers

	// C++ Language Library headers

	static int Main(int argc, RefObject<String[]> argv)
	{
	  // the splash screen 
	  System.out.print("\n  ***************************************");
	  System.out.print("\n");
	  System.out.print("  ***   ASCII to WAVE FILE CONVERTER  ***");
	  System.out.print("\n");
	  System.out.print("  *** (c) Christoph Lauer Engineering ***");
	  System.out.print("\n");
	  System.out.print("  ***************************************");
	  System.out.print("\n");
	  System.out.print("\n");

	  // test and extract the arguments
	  if (argc != 4)
	  {
		System.out.print("USAGE: asciiToWave [ascii path] [wave path] [sample rate]");
		exit(0);
	  }
	  System.out.print("ASCCI FILE PATH = ");
	  System.out.print(argv.argvalue[1]);
	  System.out.print("\n");
	  System.out.print("WAVE  FILE PATH = ");
	  System.out.print(argv.argvalue[2]);
	  System.out.print("\n");
	  // get the sample rate
	  int sampleRate = Integer.parseInt(argv.argvalue[3]);
	  System.out.print("SAMPLERATE      = ");
	  System.out.print(sampleRate);
	  System.out.print("\n");

	  // call the convert function
	  clauer.io.WaveFileHandler.asciiToWave(argv.argvalue[1], argv.argvalue[2], sampleRate);

	  // give no error back
	  return(0);
	}
}

//----------------------------------------------------------------------------------------
//	Copyright © 2006 - 2009 Tangible Software Solutions Inc.
//
//	This class is used to simulate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
final class RefObject<T>
{
	T argvalue;
	RefObject(T refarg)
	{
		argvalue = refarg;
	}
}