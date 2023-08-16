public class test 
{
	public static void main(String [ ] args)
	{
		// splash screen 
		System.out.print("  *******************************************************************\n");
		System.out.print("  *** This is the PSEUDO-MARGENAU-HILL-DISTRIBUTION test function ***\n");
		System.out.print("  ***                (c) Christoph Lauer Engineering              ***\n");
		System.out.print("  *******************************************************************\n");
		
		// Create the time domain signal
		int length = 1024;
		int resolution = 256;
		double[] signal = new double[length];
		for (int i=0; i<length; i++)
		{
			signal[i] = Math.sin(signal[i]*i/10);
		}
		double[][] spwv = SmoothedPseudoWignerVilleDistribution.calculateSmoothedPseudoWignerVilleDistribution(signal, length, resolution);
		
		
		for (int i=0; i<1024; i++)
		  for (int j=0; j<256; j++)
			 System.out.print(spwv[i][j]);
	}
}
