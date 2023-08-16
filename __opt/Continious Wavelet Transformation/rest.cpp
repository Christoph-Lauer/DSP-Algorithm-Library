////////////////////////////////////////////////////////////////////////////////////
//calculate wavelet transform
void calc_wt(double scale, double dt, dcmplx *signal, dcmplx *coeff, int datasize, int waveletsize)
{

	int i, j;
	static dcmplx *phi;
	phi = new dcmplx[waveletsize];
	dcmplx cSum = 0;

	//calc wavelet for scale-parameter scale (store in phi)
	calc_wavelet(phi, waveletsize, dt, scale, ((waveletsize+1)/2)*dt);

	for(i=0;i<datasize;i++) //datasize*scale (time-step = 1E-7)
	{
		for(j=0;j<waveletsize;j++) //waveletsize*scale*datasize
		{				
			if(j-((waveletsize-1)/2)+i >= 0)
			{
				cSum += signal[j-((waveletsize-1)/2)+i]*conj(phi[j]);
			}
		}	
		coeff[i] = cSum*dt;
		cSum = 0;	
	}
	delete [] phi;
}

////////////////////////////////////////////////////////////////////////////////////
//calculate inverse wavelet transform
void calc_wtinv(double scale, double dt, dcmplx *coeffmatrix, double *signal, int datasize, int waveletsize, int NoOfStep)

{

	int j;
	int i;
	static dcmplx *phi;
	static dcmplx *invwaveform;
	double c = 1;

	phi = new dcmplx[waveletsize];
	invwaveform = new dcmplx[MAXDATASIZE];

	//calc wavelet for scale-parameter scale (store in phi)
	calc_wavelet(phi, waveletsize, dt, scale, ((waveletsize+1)/2)*dt);
	
	c = pow(scale, -(2.0) );
			for(i=0;i<datasize;i++)
			{
				for(j=0;j<waveletsize;j++)
				{
					
					if(j-((waveletsize-1)/2)+i >= 0)
					{
						invwaveform[j-((waveletsize-1)/2)+i] += coeffmatrix[((NoOfStep-1)*MAXDATASIZE)+i] * phi[j];
						
					}
				}
				//invwaveform[i] += coeffmatrix[((NoOfStep-1)*MAXDATASIZE)+i] * phi[(waveletsize-1)/2];
				signal[i] += c * real(invwaveform[i]);
			}
}

////////////////////////////////////////////////////////////////////////////////////
//A general wavelet function
void calc_wavelet(dcmplx *buf, int waveletsize, double dt, double A, double B)
{

	int n;
	double t, wp;
	dcmplx c (1.0/sqrt(A), 0.0);
	const double gamma = 5.336446257;
	wp = 2*(sqrt(2.0) * M_PI) / (dt); //omega_p from time resolution
	
	for(n=0;n<waveletsize;++n) //waveletsize*scale
	{
		t = n*dt;
		buf[n] = c * calc_gabor((t-B)/A,wp);
	}
}

////////////////////////////////////////////////////////////////////////////////////
//Mother Wavelet type Gabor
dcmplx calc_gabor(double t, double wp)
{
	double gamma = 5.336446257;
	dcmplx c;

	dcmplx a (pow(M_PI, -1.0/4.0)*sqrt(wp/gamma), 0.0);
	dcmplx b (-pow((t*wp/gamma), 2.0)/2.0,wp*t);
	
	c = a * exp(b);
	return c;
}