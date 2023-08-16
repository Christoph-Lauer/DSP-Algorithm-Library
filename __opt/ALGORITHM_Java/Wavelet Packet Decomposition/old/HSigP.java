public class GlobalMembersHSigP
{
	// ----------------------------------------------------------- 
	//                                                             
	//                          ___                                
	//                       |_| | |_/   SPEECH                    
	//                       | | | | \   RECOGNITION               
	//                       =========   SOFTWARE                   
	//                                                             
	//                                                             
	// ----------------------------------------------------------- 
	// developed at:                                               
	//                                                             
	//      Speech Vision and Robotics group                       
	//      Cambridge University Engineering Department            
	//      http://svr-www.eng.cam.ac.uk/                          
	//                                                             
	//      Entropic Cambridge Research Laboratory                 
	//      (now part of Microsoft)                                
	//                                                             
	// ----------------------------------------------------------- 
	//         Copyright: Microsoft Corporation                    
	//          1995-2000 Redmond, Washington USA                  
	//                    http://www.microsoft.com                 
	//                                                             
	//              2001  Cambridge University                     
	//                    Engineering Department                   
	//                                                             
	//   Use of this software is governed by a License Agreement   
	//    ** See the file License for the Conditions of Use  **    
	//    **     This banner notice must not be removed      **    
	//                                                             
	// ----------------------------------------------------------- 
	//      File: HSigP.c:   Signal Processing Routines            
	// ----------------------------------------------------------- 

	public static String hsigp_version = "!HVER!HSigP:   3.2.1 [CUED 15/10/03]";
	public static String hsigp_vc_id = "$Id: HSigP.c,v 1.6 2005/06/30 16:21:17 christoph Exp $";

// ---------------------- Initialisation -------------------------

// EXPORT->InitSigP: initialise the SigP module 

	public static void InitSigP()
	{
	   int i;

	   Register(hsigp_version,hsigp_vc_id);
	   numParm = GetConfig("HSIGP", DefineConstantsHSigP.TRUE, cParm, MAXGLOBS);
	   if (numParm>0)
	   {
		  if (GetConfInt(cParm, numParm, "TRACE", i))
			  trace = i;
	   }
	   CreateHeap(sigpHeap, "sigpHeap", MSTAK, 1, 0.0, 5000, 5000);
	}

// --------------- Windowing and PreEmphasis ---------------------

// ZeroMean: zero mean a complete speech waveform 
	//
	//   Initialise the signal processing module.  This must be called
	//   before any other operation
	//

	// --------------- Speech Signal Processing Operations ------------- 

	public static void ZeroMean(RefObject<Short> data, int nSamples)
	{
	   int i;
	   int hiClip =0;
	   int loClip =0;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   short *x;
	   double sum =0.0;
	   double y;
	   double mean;

	   x = data.argvalue;
	   for (i =0;i<nSamples;i++,x++)
		  sum += *x;
	   mean = sum / (_float)nSamples;
	   x = data.argvalue;
	   for (i =0;i<nSamples;i++,x++)
	   {
		  y = (double)(*x) - mean;
		  if (y<-32767.0)
		  {
			 y = -32767.0;
			 ++loClip;
		  }
		  if (y>32767.0)
		  {
			 y = 32767.0;
			 ++hiClip;
		  }
		  *x = (short)((y>0.0) ? y+0.5 : y-0.5);
	   }
	   if (loClip>0)
		  HError(-5322,"ZeroMean: %d samples too -ve\n",loClip);
	   if (hiClip>0)
		  HError(-5322,"ZeroMean: %d samples too +ve\n",hiClip);
	}
	// 
	//   zero mean a complete speech waveform nSamples long
	//

	public static void AsymExpo(Vector s, _float factor)
	{
	  int i;
	  int frameSize;
	  frameSize =VectorSize(s);
	  if (asymExpoWinSize != frameSize)
		GenAsymExpoWindow(frameSize, factor);
	  for (i =1;i<=frameSize;i++)
		s[i] *= asymExpoWin[i];
	}

// EXPORT->Ham: Apply Hamming Window to Speech frame s 
	//
	//   Apply Asynchron Exponential Window to Speech frame s
	//

	public static void Ham(Vector s)
	{
	   int i;
	   int frameSize;

	   frameSize =VectorSize(s);
	   if (hamWinSize != frameSize)
		  GenHamWindow(frameSize);
	   for (i =1;i<=frameSize;i++)
		  s[i] *= hamWin[i];
	}

// EXPORT->PreEmphasise: pre-emphasise signal in s 
	//
	//   Apply Hamming Window to Speech frame s
	//

	public static void PreEmphasise(Vector s, _float k)
	{
	   int i;
	   _float preE;

	   preE = k;
	   for (i =VectorSize(s);i>=2;i--)
		  s[i] -= s[i-1]*preE;
	   s[1] *= 1.0-preE;
	}

// EXPORT->Wave2LPC: Calculate LPCoef in a & RefC in k 
	//
	//   Apply first order preemphasis filter y[n] = x[n] - K*x[n-1] to s
	//

	// --------------- Linear Prediction Coding Operations ------------- 

	public static void Wave2LPC(Vector s, Vector a, Vector k, RefObject<_float> re, RefObject<_float> te)
	{
	   Vector thisA = new Vector(); // Current LP filter coefficients
	   Vector r = new Vector(); // AutoCorrelation Sequence
	   _float E; // Prediction Error
	   int p;
	   int frameSize;

	   if (a ==null && k ==null)
		  HError(5320,"Wave2LPC: Null a and k vectors in WaveToLPC");
	   if (a!=null)
		  p =VectorSize(a);
	   else
		  p =VectorSize(k);
	   r = CreateVector(gstack, p);
	   thisA = (a!=null)?a:CreateVector(gstack, p);
	   frameSize =VectorSize(s);
	   E = AutoCorrelate(s, r, p, frameSize);
	   te.argvalue = E;
	   re.argvalue = Durbin(k, thisA, r, E, p);
	   FreeVector(gstack, r);
	}

// EXPORT->LPC2RefC: transfer from filter to ref coef 
	//
	//   Calculate LP Filter Coef in a and LP Refl Coef in k from speech s
	//   Either a and k can be NULL.  Residual Energy is returned in re and
	//   total energy in te.
	//

	public static void LPC2RefC(Vector a, Vector k)
	{
	   Vector thisA = new Vector(); // Current LP filter coefficients
	   Vector newA = new Vector(); // New LP filter coefficients
	   int i;
	   int j;
	   int p;
	   _float ki;
	   _float x;

	   p =VectorSize(a);
	   thisA = CreateVector(gstack, p);
	   newA = CreateVector(gstack, p);
	   CopyVector(a,thisA);
	   for (i =p;i>=1;i--)
	   {
		  ki = -thisA[i];
		  k[i] = ki;
		  x = 1 - ki *ki;
		  for (j =1;j<i;j++)
			 newA[j] = (thisA[j] + ki * thisA[i - j]) / x;
		  for (j =1;j<i;j++)
			 thisA[j] = newA[j];
	   }
	   FreeVector(gstack, thisA);
	}

// EXPORT->RefC2LPC: transfer from ref coef to filter 
	public static void RefC2LPC(Vector k, Vector a)
	{
	   Vector thisA = new Vector(); // Current LP filter coefficients
	   Vector newA = new Vector(); // New LP filter coefficients
	   int i;
	   int j;
	   int p;
	   _float ki;

	   p =VectorSize(k);
	   thisA = CreateVector(gstack, p);
	   newA = CreateVector(gstack, p);
	   for (i =1;i<=p;i++)
	   {
		  ki = k[i];
		  newA[i] = -ki;
		  for (j =1;j<i;j++)
			 newA[j] = thisA[j] - ki * thisA[i - j];
		  for (j =1;j<=i;j++)
			 thisA[j] = newA[j];
	   }
	   for (i =1;i<=p;i++)
		   a[i]=thisA[i];
	   FreeVector(gstack, thisA);
	}

// EXPORT->LPC2Cepstrum: transfer from lpc to cepstral coef 
	//
	//   Convert between filter and reflection coefs 
	//

	public static void LPC2Cepstrum(Vector a, Vector c)
	{
	   int i;
	   int n;
	   int p;
	   _float sum;

	   p =VectorSize(c);
	   for (n =1;n<=p;n++)
	   {
		  sum = 0.0;
		  for (i =1;i<n;i++)
			 sum = sum + (n - i) * a[i] * c[n - i];
		  c[n] = -(a[n] + sum / n);
	   }
	}

// EXPORT->Cepstrum2LPC: transfer from cepstral coef to lpc 
	public static void Cepstrum2LPC(Vector c, Vector a)
	{
	   int i;
	   int n;
	   int p;
	   _float sum;

	   p =VectorSize(a);
	   for (n =1;n<=p;n++)
	   {
		  sum = 0.0;
		  for (i =1;i<n;i++)
			 sum = sum + (n - i) * a[i] * c[n - i];
		  a[n] = -(c[n] + sum / n);
	   }
	}

// -------------------- FFT Based Operations ----------------------- 

// EXPORT-> FVec2Spectrum: cvt feature vector f to a spectrum, fzero
//   is the value of the 0'th feature vector coefficient which
//   is typically omitted by HSigP routines eg a0 = 1.0 for LPC
//
	//
	//   Convert between LP Cepstral Coef in c and LP Coef in a
	//

	// -------------------- FFT Based Operations ----------------------- 

	public static void FVec2Spectrum(_float fzero, Vector f, Vector s)
	{
	   int i;
	   int p;
	   int n;

	   p =VectorSize(f);
	   n =VectorSize(s);
	   s[1] = fzero;
	   for (i =1;i<=p;i++)
		  s[i+1] = f[i];
	   for (i =p+2;i<=n;i++)
		  s[i] = 0.0;
	   Realft(s);
	}

// EXPORT-> FFT: apply fft/invfft to complex s 
	//
	//   Pads f with zeroes and applies FFT to give spectrum s
	//   Only the useful half of the spectrum is returned eg 
	//   if VectorSize(s)=128 then a 128 point FFT will be used
	//   but only the first 64 complex points are returned in s.
	//   fzero is the value of the 0'th feature vector coefficient 
	//   which is typically omitted by HSigP routines eg a0 = 1.0 
	//   for LPC
	//

	public static void FFT(Vector s, int invert)
	{
	   int ii;
	   int jj;
	   int n;
	   int nn;
	   int limit;
	   int m;
	   int j;
	   int inc;
	   int i;
	   double wx;
	   double wr;
	   double wpr;
	   double wpi;
	   double wi;
	   double theta;
	   double xre;
	   double xri;
	   double x;

	   n =VectorSize(s);
	   nn =n / 2;
	   j = 1;
	   for (ii =1;ii<=nn;ii++)
	   {
		  i = 2 * ii - 1;
		  if (j>i)
		  {
			 xre = s[j];
			 xri = s[j + 1];
			 s[j] = s[i];
			 s[j + 1] = s[i + 1];
			 s[i] = xre;
			 s[i + 1] = xri;
		  }
		  m = n / 2;
		  while (m >= 2 && j > m)
		  {
			 j -= m;
			 m /= 2;
		  }
		  j += m;
	   }
	   limit = 2;


	   while (limit < n)
	   {
		  inc = 2 * limit;
		  theta = TPI / limit;
		  if (invert != 0)
			  theta = -theta;
		  wpr = Math.cos(theta);
		  wpi = Math.sin(theta);
		  wr = 1.0;
		  wi = 0.0;
		  for (ii =1; ii<=limit/2; ii++)
		  {
			 m = 2 * ii - 1;
			 for (jj = 0; jj<=(n - m) / inc;jj++)
			 {
				i = m + jj * inc;
				j = i + limit;
				xre = wr * s[j] - wi * s[j + 1];
				xri = wr * s[j + 1] + wi * s[j];
				s[j] = s[i] - xre;
				s[j + 1] = s[i + 1] - xri;
				s[i] = s[i] + xre;
				s[i + 1] = s[i + 1] + xri;
			 }
			 wx = wr;
			 wr = wr * wpr - wi * wpi;
			 wi = wi * wpr + wx * wpi;

		  }
		  limit = inc;
	   }
	   if (invert != 0)
		  for (i = 1;i<=n;i++)
			 s[i] = s[i] / nn;

	}

// EXPORT-> Realft: apply fft to real s 
	//
	//   When called s holds nn complex values stored in the
	//   sequence   [ r1 , i1 , r2 , i2 , .. .. , rn , in ] where
	//   n = VectorSize(s) DIV 2, n must be a power of 2. On exit s
	//   holds the fft (or the inverse fft if invert == 1) 
	//

	public static void Realft(Vector s)
	{
	   int n;
	   int n2;
	   int i;
	   int i1;
	   int i2;
	   int i3;
	   int i4;
	   double xr1;
	   double xi1;
	   double xr2;
	   double xi2;
	   double a;
	   double b;
	   double yr;
	   double yi;
	   double yr2;
	   double yi2;
	   double yr0;
	   double theta;

	   n =VectorSize(s) / 2;
	   theta = DefineConstantsHSigP.PI / n;
	   FFT(s, DefineConstantsHSigP.FALSE);
	   yr2 = Math.cos(theta);
	   yi2 = Math.sin(theta);
	   yr = yr2;
	   yi = yi2;
	   for (i =4; i<=n; i+=2)
	   {
		  i1 = i - 1;
		  i2 = i;
		  i4 = ((n + 2) << 1) - i;
		  i3 = i4 - 1;

		  xr1 = (s[i1] + s[i3])/2.0;
		  xi1 = (s[i2] - s[i4])/2.0;
		  xr2 = (s[i2] + s[i4])/2.0;
		  xi2 = (s[i3] - s[i1])/2.0;
		  a = yr * xr2 - yi * xi2;
		  b = yr * xi2 + yi * xr2;
		  s[i1] = xr1 + a;
		  s[i2] = xi1 + b;
		  s[i3] = xr1 - a;
		  s[i4] = -xi1 + b;
		  yr0 = yr;
		  yr = yr * yr2 - yi * yi2;
		  yi = yi * yr2 + yr0 * yi2;
	   }
	   xr1 = s[1];
	   s[1] = xr1 + s[2];
	   s[2] = 0.0;
	}

// EXPORT-> SpecModulus: store modulus of s in m 
	//
	//   When called s holds 2*n real values, on exit s holds the
	//   first  n complex points of the spectrum stored in
	//   the same format as for fft
	//

	public static void SpecModulus(Vector s, Vector m)
	{
	   int i;
	   int j;
	   _float x;
	   _float y;

	   for (i =1;i<=VectorSize(s)/2;i++)
	   {
		  j =i+i;
		  x =s[j-1];
		  y =s[j];
		  m[i]=Math.sqrt(x *x + y *y);
	   }
	}

// EXPORT-> SpecLogModulus: store log modulus of s in m 
	public static void SpecLogModulus(Vector s, Vector m, boolean invert)
	{
	   int i;
	   int j;
	   _float x;
	   _float y;

	   for (i =1;i<=VectorSize(s)/2;i++)
	   {
		  j =i+i;
		  x =s[j-1];
		  y =s[j];
		  x =0.5 *Math.log(x *x + y *y);
		  m[i] = invert ? -x : x;
	   }
	}

// EXPORT-> SpecPhase: store phase of s in m 
	public static void SpecPhase(Vector s, Vector m)
	{
	   int i;
	   int j;
	   _float ph;
	   _float re;
	   _float im;

	   for (i =1;i<=VectorSize(s)/2;i++)
	   {
		  j =i+i;
		  re =s[j-1];
		  im =s[j];
		  if (re ==0.0)
			 ph = (im>=0.0) ? DefineConstantsHSigP.PI/2.0 : -DefineConstantsHSigP.PI/2.0;
		  else
		  {
			 ph =Math.atan(im/re);
			 if (ph<0.0 && re<0.0)
				ph += DefineConstantsHSigP.PI;
			 else if (ph>0.0 && im<0.0)
				ph -= DefineConstantsHSigP.PI;
		  }
		  m[i]=ph;
	   }
	}

// -------------------- MFCC Related Operations -------------------- 

// EXPORT->Mel: return mel-frequency corresponding to given FFT index 

	public static _float Mel(int k, _float fres)
	{
	   return 1127 * Math.log(1 + (k-1)*fres);
	}

// EXPORT->InitFBank: Initialise an FBankInfo record 
	// 
	//   return mel-frequency corresponding to given FFT index k.  
	//   Resolution is normally determined by fres field of FBankInfo
	//   record.
	//

	public static FBankInfo InitFBank(RefObject<MemHeap> x, int frameSize, int sampPeriod, int numChans, _float lopass, _float hipass, boolean usePower, boolean takeLogs, boolean doubleFFT, _float alpha, _float warpLowCut, _float warpUpCut)
	{
	   FBankInfo fb = new FBankInfo();
	   _float mlo;
	   _float mhi;
	   _float ms;
	   _float melk;
	   int k;
	   int chan;
	   int maxChan;
	   int Nby2;

	   // Save sizes to cross-check subsequent usage
	   fb.frameSize = frameSize;
	   fb.numChans = numChans;
	   fb.sampPeriod = sampPeriod;
	   fb.usePower = usePower;
	   fb.takeLogs = takeLogs;
	   // Calculate required FFT size
	   fb.fftN = 2;
	   while (frameSize>fb.fftN)
		   fb.fftN *= 2;
	   if (doubleFFT)
		  fb.fftN *= 2;
	   Nby2 = fb.fftN / 2;
	   fb.fres = 1.0E7/(sampPeriod * fb.fftN * 700.0);
	   maxChan = numChans+1;
	   // set lo and hi pass cut offs if any
	   fb.klo = 2; // apply lo/hi pass filtering
	   fb.khi = Nby2;
	   mlo = 0;
	   mhi = Mel(Nby2+1, fb.fres);
	   if (lopass>=0.0)
	   {
		  mlo = 27 *Math.log(1+lopass/700.0);
		  fb.klo = (int)((lopass * sampPeriod * 1.0e-7 * fb.fftN) + 2.5);
		  if (fb.klo<2)
			  fb.klo = 2;
	   }
	   if (hipass>=0.0)
	   {
		  mhi = 1127 *Math.log(1+hipass/700.0);
		  fb.khi = (int)((hipass * sampPeriod * 1.0e-7 * fb.fftN) + 0.5);
		  if (fb.khi>Nby2)
			  fb.khi = Nby2;
	   }
	   if (trace &DefineConstantsHSigP.T_MEL != 0)
	   {
		  System.out.printf("FFT passband %d to %d out of 1 to %d\n",fb.klo,fb.khi,Nby2);
		  System.out.printf("Mel passband %f to %f\n",mlo,mhi);
	   }
	   // Create vector of fbank centre frequencies
	   fb.cf = CreateVector(x.argvalue,maxChan);
	   ms = mhi - mlo;
	   for (chan =1; chan <= maxChan; chan++)
	   {
		  if (alpha == 1.0)
		  {
			 fb.cf[chan] = ((_float)chan/(_float)maxChan)*ms + mlo;
		  }
		  else
		  {
			 // scale assuming scaling starts at lopass
			 _float minFreq = 700.0 * (Math.exp (mlo / 1127.0) - 1.0);
			 _float maxFreq = 700.0 * (Math.exp (mhi / 1127.0) - 1.0);
			 _float cf = ((_float)chan / (_float) maxChan) * ms + mlo;

			 cf = 700 * (Math.exp (cf / 1127.0) - 1.0);

			 fb.cf[chan] = 1127.0 * Math.log (1.0 + WarpFreq (warpLowCut, warpUpCut, cf, minFreq, maxFreq, alpha) / 700.0);
		  }
	   }

	   // Create loChan map, loChan[fftindex] -> lower channel index
	   fb.loChan = CreateShortVec(x.argvalue,Nby2);
	   for (k =1,chan =1; k<=Nby2; k++)
	   {
		  melk = Mel(k, fb.fres);
		  if (k<fb.klo || k>fb.khi)
			  fb.loChan[k]=-1;
		  else
		  {
			 while (fb.cf[chan] < melk && chan<=maxChan)
				 ++chan;
			 fb.loChan[k] = chan-1;
		  }
	   }

	   // Create vector of lower channel weights
	   fb.loWt = CreateVector(x.argvalue,Nby2);
	   for (k =1; k<=Nby2; k++)
	   {
		  chan = fb.loChan[k];
		  if (k<fb.klo || k>fb.khi)
			  fb.loWt[k]=0.0;
		  else
		  {
			 if (chan>0)
				fb.loWt[k] = ((fb.cf[chan+1] - Mel(k, fb.fres)) / (fb.cf[chan+1] - fb.cf[chan]));
			 else
				fb.loWt[k] = (fb.cf[1]-Mel(k, fb.fres))/(fb.cf[1] - mlo);
		  }
	   }
	   // Create workspace for fft
	   fb.x = CreateVector(x.argvalue,fb.fftN);
	   return fb;
	}

// EXPORT->Wave2FBank:  Perform filterbank analysis on speech s 
	//
	//   Initialise an FBankInfo record prior to calling Wave2FBank.
	//


	public static void Wave2FBank(Vector s, Vector fbank, RefObject<_float> te, FBankInfo info)
	{
	   final _float melfloor = 1.0;
	   int k;
	   int bin;
	   _float t1; // real and imag parts
	   _float t2;
	   _float ek; // energy of k'th fft channel

	   // Check that info record is compatible
	   if (info.frameSize != VectorSize(s))
		  HError(5321,"Wave2FBank: frame size mismatch");
	   if (info.numChans != VectorSize(fbank))
		  HError(5321,"Wave2FBank: num channels mismatch");
	   // Compute frame energy if needed
	   if (te.argvalue != null)
	   {
		  te.argvalue = 0.0;
		  for (k =1; k<=info.frameSize; k++)
			 te.argvalue += (s[k]*s[k]);
	   }
	   // Apply FFT
	   for (k =1; k<=info.frameSize; k++)
		  info.x[k] = s[k]; // copy to workspace
	   for (k =info.frameSize+1; k<=info.fftN; k++)
		  info.x[k] = 0.0; // pad with zeroes
	   Realft(info.x); // take fft

	   // Fill filterbank channels
	   ZeroVector(fbank);
	   for (k = info.klo; k <= info.khi; k++) // fill bins
	   {
		  t1 = info.x[2 *k-1];
		  t2 = info.x[2 *k];
		  if (info.usePower)
			 ek = t1 *t1 + t2 *t2;
		  else
			 ek = Math.sqrt(t1 *t1 + t2 *t2);
		  bin = info.loChan[k];
		  t1 = info.loWt[k]*ek;
		  if (bin>0)
			  fbank[bin] += t1;
		  if (bin<info.numChans)
			  fbank[bin+1] += ek - t1;
	   }

	   // Take logs
	   if (info.takeLogs)
		  for (bin =1; bin<=info.numChans; bin++)
		  {
			 t1 = fbank[bin];
			 if (t1<melfloor)
				 t1 = melfloor;
			 fbank[bin] = Math.log(t1);
		  }
	}

// EXPORT->FBank2MFCC: compute first n cepstral coeff 
// cosine transformation 
	//
	//   Convert given speech frame in s into mel-frequency filterbank
	//   coefficients.  The total frame energy is stored in te.  The
	//   info record contains precomputed filter weights and should be set
	//   prior to using Wave2FBank by calling InitFBank.
	//

	public static void FBank2MFCC(Vector fbank, Vector c, int n, _float[] cosMatrix)
	{
	   int i;
	   int j;
	   int numChan;
	   numChan = VectorSize(fbank);
	   for(i = 1; i <= n; i++)
	   {
		  c[i] = 0.0;
		  for(j = 0; j < numChan; j++)
		  {
		 c[i] += fbank[j + 1] * cosMatrix[i * numChan + j];
		  }
	   }
	}

// EXPORT->FBank2MelSpec: convert log fbank to linear 
	//
	//   Apply the DCT to fbank and store first n cepstral coeff in c.
	//   Note that the resulting coef are normalised by sqrt(2/numChans)
	// 

	public static void FBank2MelSpec(Vector fbank)
	{
	   int i;

	   for (i =1; i<=VectorSize(fbank); i++)
		  fbank[i] = Math.exp(fbank[i]);
	}

// EXPORT->MelSpec2FBank: convert lin mel spectrum to log fbank 
	//
	//   Convert the given log filterbank coef, in place, to linear
	// 

	public static void MelSpec2FBank(Vector melspec)
	{
	   int i;
	   _float x;

	   for (i =1; i<=VectorSize(melspec); i++)
	   {
		  x = melspec[i];
		  if (x<1.0)
			  x = 1.0;
		  melspec[i] = Math.log(x);
	   }
	}

// EXPORT->FBank2C0: return zero'th cepstral coefficient 
	//
	//   Convert the given linear filterbank coef, in place, to log
	// 

	public static _float FBank2C0(Vector fbank)
	{
	   int k;
	   int numChan;
	   _float mfnorm;
	   _float sum;

	   numChan = VectorSize(fbank);
	   mfnorm = Math.sqrt(2.0/(_float)numChan);
	   sum = 0.0;
	   for (k =1; k<=numChan; k++)
		  sum += fbank[k];
	   return sum * mfnorm;
	}

// --------------------- PLP Related Operations -------------------- 

// EXPORT->InitPLP: Initialise equal-loudness curve & IDT cosine matrix 
	//
	//   return zero'th cepstral coefficient for given filter bank, i.e.
	//   compute sum of fbank channels and do standard normalisation
	//


	// ------------------- PLP Related Operations ---------------------- 

	public static void InitPLP(FBankInfo info, int lpcOrder, Vector eql, DMatrix cm)
	{
	   int i;
	   int j;
	   double baseAngle;
	   _float f_hz_mid;
	   _float fsub;
	   _float fsq;
	   int nAuto;
	   int nFreq;

	   // Create the equal-loudness curve
	   for (i =1; i<=info.numChans; i++)
	   {
		  f_hz_mid = 700*(Math.exp(info.cf[i]/1127)-1); // Mel to Hz conversion
		  fsq = (f_hz_mid * f_hz_mid);
		  fsub = fsq / (fsq + 1.6e5);
		  eql[i] = fsub * fsub * ((fsq + 1.44e6) /(fsq + 9.61e6));
	   }

	   // Builds up matrix of cosines for IDFT
	   nAuto = lpcOrder+1;
	   nFreq = info.numChans+2;
	   baseAngle = DefineConstantsHSigP.PI / (double)(nFreq - 1);
	   for (i =0; i<nAuto; i++)
	   {
		  cm[i+1][1] = 1.0;
		  for (j =1; j<(nFreq-1); j++)
			 cm[i+1][j+1] = 2.0 * Math.cos(baseAngle * (double)i * (double)j);

		  cm[i+1][nFreq] = Math.cos(baseAngle * (double)i * (double)(nFreq-1));
	   }
	}

// EXPORT->FBank2ASpec: Pre-emphasise filter bank output with the simulated 
//           equal-loudness curve and perform amplitude compression 
	//
	//   Initialise equal-loudness curve and cosine matrix for IDFT
	//
	public static void FBank2ASpec(Vector fbank, Vector as, Vector eql, _float compressFact, FBankInfo info)
	{
	   final _float melfloor = 1.0;
	   int i;

	   for (i =1; i<=info.numChans; i++)
	   {
		  if (fbank[i] < melfloor)
			  fbank[i] = melfloor;
		  as[i+1] = fbank[i] * eql[i]; // Apply equal-loudness curve
		  as[i+1] = Math.pow((double) as[i+1], (double) compressFact);
	   }
	   as[1] = as[2]; // Duplicate values at either end
	   as[info.numChans+2] = as[info.numChans+1];
	}

// EXPORT->ASpec2LPCep: Perform IDFT to get autocorrelation values then 
//           produce autoregressive coeffs. and cepstral transform them 
	//
	//   Pre-emphasise with simulated equal-loudness curve and perform
	//   cubic root amplitude compression.
	//
	public static void ASpec2LPCep(Vector as, Vector ac, Vector lp, Vector c, DMatrix cm)
	{
	   _float lpcGain;
	   _float E;

	   // Do IDFT to get autocorrelation values
	   E = MatrixIDFT(as, ac, cm);
	   lp[VectorSize(lp)] = 0.0; // init to make Purify et al. happy
	   // do Durbin recursion to get predictor coefficients
	   lpcGain = Durbin(null, lp, ac, E, VectorSize(ac)-1);
	   if (lpcGain<=0)
		  HError(-5323,"ASpec2LPCep: Negative lpcgain");
	   LPC2Cepstrum(lp, c);
	   c[VectorSize(c)] = (_float) -Math.log((double) 1.0/lpcGain); // value forms C0
	}

// EXPORT->WeightCepstrum: Apply cepstral weighting to c 
	//
	//   Do IDFT giving autocorrelation values then do linear prediction
	//   and finally, transform into cepstral coefficients
	//

	// ------------------- Feature Level Operations -------------------- 

	public static void WeightCepstrum(Vector c, int start, int count, int cepLiftering)
	{
	   int i;
	   int j;

	   if (cepWinL != cepLiftering || count > cepWinSize)
		  GenCepWin(cepLiftering, count);
	   j = start;
	   for (i =1;i<=count;i++)
		  c[j++] *= cepWin[i];
	}

// EXPORT->UnWeightCepstrum: Undo cepstral weighting of c 
	public static void UnWeightCepstrum(Vector c, int start, int count, int cepLiftering)
	{
	   int i;
	   int j;

	   if (cepWinL != cepLiftering || count > cepWinSize)
		  GenCepWin(cepLiftering, count);
	   j = start;
	   for (i =1;i<=count;i++)
		  c[j++] /= cepWin[i];
	}

// The following operations apply to a sequence of n vectors step apart.
//   They are used to operate on the 'columns' of data files 
//   containing a sequence of feature vectors packed together to form a
//   continguous block of floats.  The logical size of each vector is
//   vSize (<=step) 

// EXPORT->FZeroMean: Zero mean the given data sequence 
	//
	//   Apply weights w[1]..w[count] to c[start] to c[start+count-1] 
	//   where w[i] = 1.0 + (L/2.0)*sin(i*pi/L),  L=cepLiftering
	//

	// The following apply to a sequence of 'n' vectors 'step' floats apart  

	public static void FZeroMean(RefObject<_float> data, int vSize, int n, int step)
	{
	   double sum;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp;
	   _float mean;
	   int i;
	   int j;

	   for (i =0; i<vSize; i++)
	   {
		  // find mean over i'th component
		  sum = 0.0;
		  fp = data.argvalue+i;
		  for (j =0;j<n;j++)
		  {
			 sum += *fp;
			 fp += step;
		  }
		  mean = sum / (double)n;
		  // subtract mean from i'th components
		  fp = data.argvalue+i;
		  for (j =0;j<n;j++)
		  {
			 *fp -= mean;
			 fp += step;
		  }
	   }
	}

// EXPORT->AddRegression: add regression vector at +offset from source vector 
	// 
	//   Zero mean the given data sequence
	//

	public static void AddRegression(RefObject<_float> data, int vSize, int n, int step, int offset, int delwin, int head, int tail, boolean simpleDiffs)
	{
	   Regress(data, vSize, n, step, offset, delwin, head, tail, simpleDiffs);
	}

// EXPORT->AddHeadRegress: add regression at start of data 
	//
	//   Add regression vector at +offset from source vector.  
	//
	//   Each regression component is given by Sum( t*(v[+t] - v[-t])) / 2*Sum(t*t) 
	//   where the sum ranges over 1 to delwin and v[+t/-t] is the corresponding 
	//   component t steps ahead/back assuming that this vector is in the valid
	//   range -head...n+tail.  If simple diffs is true, then slope is 
	//   calculated from (v[delwin] - v[-delwin])/(2*delwin).  
	//

	public static void AddHeadRegress(RefObject<_float> data, int vSize, int n, int step, int offset, int delwin, boolean simpleDiffs)
	{
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp1;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp2;
	   int i;
	   int j;

	   fp = data.argvalue;
	   if (delwin ==0)
	   {
		  for (i =1;i<=n;i++)
		  {
			 fp1 = fp;
			 fp2 = fp+offset;
			 for (j =1;j<=vSize;j++)
			 {
				*fp2 = *(fp1+step) - *fp1;
				++fp1;
				++fp2;
			 }
			 fp += step;
		  }
	   }
	   else
	   {
		  Regress(data, vSize, n, step, offset, delwin, 0, delwin, simpleDiffs);
	   }
	}

// EXPORT->AddTailRegress: add regression at end of data 
	// 
	//   As for AddRegression, but deals with start case where there are no
	//   previous frames to regress over (assumes that there are at least
	//   min(delwin,1) valid following frames).  If delwin==0, then a simple
	//   forward difference given by v[0] - v[-1] is used.  Otherwise, the first
	//   available frame in the window is replicated back in time to fill the
	//   window.
	//

	public static void AddTailRegress(RefObject<_float> data, int vSize, int n, int step, int offset, int delwin, boolean simpleDiffs)
	{
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp1;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp2;
	   int i;
	   int j;

	   fp = data.argvalue;
	   if (delwin ==0)
	   {
		  for (i =1;i<=n;i++)
		  {
			 fp1 = fp;
			 fp2 = fp+offset;
			 for (j =1;j<=vSize;j++)
			 {
				*fp2 = *fp1 - *(fp1-step);
				++fp1;
				++fp2;
			 }
			 fp += step;
		  }
	   }
	   else
	   {
		  Regress(data, vSize, n, step, offset, delwin, delwin, 0, simpleDiffs);
	   }
	}

// EXPORT->NormaliseLogEnergy: normalise log energy to range -X .. 1.0 
	// 
	//   As for AddRegression, but deals with start case where there are no
	//   previous frames to regress over (assumes that there are at least
	//   min(delwin,1) valid preceding frames).  If delwin==0, then a simple
	//   forward difference given by v[0] - v[-1] is used.  Otherwise, the first
	//   available frame in the window is replicated back in time to fill the
	//   window.  
	//

	public static void NormaliseLogEnergy(RefObject<_float> data, int n, int step, _float silFloor, _float escale)
	{
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *p;
	   _float max;
	   _float min;
	   int i;

	   // find max log energy
	   p = data.argvalue;
	   max = *p;
	   for (i =1;i<n;i++)
	   {
		  p += step; // step p to next e val
		  if (*p > max)
			  max = *p;
	   }
	   min = max - (silFloor *Math.log(10.0))/10.0; // set the silence floor
	   // normalise
	   p = data.argvalue;
	   for (i =0;i<n;i++)
	   {
		  if (*p < min) // clamp to silence floor
			  *p = min;
		  *p = 1.0 - (max - *p) * escale; // normalise
		  p += step;
	   }
	}

// ------------------- Wavelet Related Operations -------------------- 

// THIS CREATES THE SUBBAND ENERGIES 

	public static void Wave2Wavelet(Vector s, RefObject<Vector> fbank, RefObject<_float> te, wlTree wpt, int wde, wlType wty, boolean useWaveletThreshold, _float waveletThreshold, _float waveletThresholdAlpha)
	{
	  final _float melfloor = 0.1;
	  int i;
	  int depth = wpt.depth; // baumtiefe
	  _float low; // filter
	  _float high; // filter
	  int dimFilter; // dimension of the filter coeeficients
	  _float transformed; // ergebnisswerte
	  int dimTransformed; // anzahl aller wavelet koefizienten
	  int dimSignal = VectorSize(s);
	  int dimDescription;

	  String waveletType;
	  switch (wty)
		{
		case AnonymousEnum.DAUBECHIES:
		  waveletType ="daubechies";
		  break;
		case AnonymousEnum.SYMMLET:
		  waveletType ="symmlet";
		  break;
		case AnonymousEnum.COIFLET:
		  waveletType ="coiflet";
		  break;
		case AnonymousEnum.BEYLKIN:
		  waveletType ="beylkin";
		  break;
		case AnonymousEnum.POLLEN:
		  waveletType ="pollen";
		  break;
		case AnonymousEnum.VAIDYANATHAN:
		  waveletType ="vaidyanathan";
		  break;
		}

	  segmentDescription description; // beschreibung der ergebnisswerte
	  SetFilter(TempRefObject, wde, low, high, TempRefObject2); // returns -1 if something goes wrong
	  waveletType = TempRefObject.argvalue;
	  dimFilter = TempRefObject2.argvalue;
	  SetVectors(depth, dimFilter, dimSignal, transformed, TempRefObject3, description);
	  dimTransformed = TempRefObject3.argvalue;
	//   printf("WAVELETTYPE:%s ",waveletType);
	//   printf("WAVELETORDER:%d ",wde);
	//   printf("DEPTH:%d ",depth);
	//   printf("DIMFILTER:%d ",dimFilter);
	//   printf("DIMSIGNAL:%d ",dimSignal);
	  WaveletPacket(s+1, dimSignal, low, high, dimFilter, wpt.tree, depth, TempRefObject4, TempRefObject5, description, TempRefObject6, useWaveletThreshold, waveletThreshold, waveletThresholdAlpha);
	  transformed = TempRefObject4.argvalue;
	  dimTransformed = TempRefObject5.argvalue;
	  dimDescription = TempRefObject6.argvalue;
	  if (( fbank.argvalue)==null)
		( fbank.argvalue) = CreateVector(gstack, dimDescription);
	  else
		if (VectorSize(( fbank.argvalue)) != dimDescription)
		  {
		//FreeVector(&gstack,fbank)
		( fbank.argvalue) = CreateVector(gstack, dimDescription);
		  }
	  // copy the result and logarithmise the filter energys
	  for(i = 1; i <= dimDescription; i++)
	  {
		 if (description[i - 1].energy > melfloor)
		 {
		( fbank.argvalue)[i] = Math.log(description[i - 1].energy);
		 }
		 else
		 {
		( fbank.argvalue)[i] = Math.log(melfloor);
		 }
	  }
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
	  free(transformed);
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
	  free(description);
	}
//****************************************************************************************************
	public static void Wavelet2WPP(Vector fbank, RefObject<Vector> c)
	{
	  _float low;
	  _float high;
	  int dimFilter;
	  int depth = 0;
	  int order = 0;
	  int i;
	  int n = VectorSize(fbank);

	  // allocate the vectors
	  if (( c.argvalue)==null)
		( c.argvalue) = CreateVector(gstack, n);
	  else
		if (VectorSize(( c.argvalue)) != n)
		  ( c.argvalue) = CreateVector(gstack, n);

	  if (VectorSize(fbank) == 12)
		{
			depth =2;
			order =4;
		}
	  if (VectorSize(fbank) == 14)
		{
			depth =1;
			order =8;
		}
	  if (VectorSize(fbank) == 16)
		{
			depth =3;
			order =4;
		}
	  if (VectorSize(fbank) == 18)
		{
			depth =1;
			order =10;
		}
	  if (VectorSize(fbank) == 20)
		{
			depth =2;
			order =6;
		}
	  if (VectorSize(fbank) == 22)
		{
			depth =1;
			order =12;
		}
	  if (VectorSize(fbank) == 24)
		{
			depth =3;
			order =4;
		}
	  if (VectorSize(fbank) == 26)
		{
			depth =1;
			order =14;
		}
	  if (VectorSize(fbank) == 28)
		{
			depth =2;
			order =8;
		}
	  if (VectorSize(fbank) == 30)
		{
			depth =1;
			order =16;
		}
	  if (VectorSize(fbank) == 32)
		{
			depth =4;
			order =4;
		}
	  if (VectorSize(fbank) == 36)
		{
			depth =2;
			order =10;
		}
	  if (VectorSize(fbank) == 48)
		{
			depth =4;
			order =4;
		}

	  if (depth ==0 && order ==0)
		{
		  System.out.print("ERROR: WPP features generation at present only configured for input vectors with an size of 24 !!!");
		  return;
		}
	  SetFilter("daubechies", order, low, high, TempRefObject);
	  dimFilter = TempRefObject.argvalue;
	  //   for(i = 1; i < 24; i++) printf(" %i \n", fbank[i]);
	  //   printf("Size: %i",VectorSize(fbank));
	  Wavelet(fbank+1, VectorSize(fbank), low, high, dimFilter, depth, ( c.argvalue)+1);
	}
//****************************************************************************************************
	public static void Wavelet2SBC(Vector fbank, RefObject<Vector> c, int n, boolean useC0, _float[] cosMatrix)
	{
	  if (useC0 == DefineConstantsHSigP.TRUE)
		{
		  if (VectorSize(fbank) < n)
		{
		  System.out.print("ERROR (UseC0ForSBC TRUE): Number of cepstral coefficients larger than the number of filter channels.");
		  exit(-1);
		}
		}
	  else
		{
		  if (VectorSize(fbank) <= n)
		{
		  System.out.print("ERROR (UseC0ForSBC FALSE): Number of cepstral coefficients larger than the number of filter channels.");
		  exit(-1);
		}
		}
	  // allocate the vectors
	  if (( c.argvalue)==null)
		( c.argvalue) = CreateVector(gstack, n);
	  else
		if (VectorSize(( c.argvalue)) != n)
		  ( c.argvalue) = CreateVector(gstack, n);
	  // decide here to use the first cosine transform coefficient
	  if (useC0 == DefineConstantsHSigP.TRUE)
		{
		  // use the first value of the cosine transformation (sum of the logarithmized energy)
		  int i;
		  int j;
		  int k;
		  int numChan;
		  _float mfnorm;
		  _float pi_factor;
		  _float x;
		  numChan = VectorSize(fbank);
	//       mfnorm = sqrt(2.0/(float)numChan);
	//       pi_factor = PI/(float)numChan;
	//       for (j=0; j<n; j++)
	// 	{
	// 	  (*c)[j+1] = 0.0; x = (float)j * pi_factor;
	// 	  for (k=1; k<=numChan; k++)
	// 	    {
	// 	      (*c)[j+1] += fbank[k] * cos(x*(k-0.5));
	// 	    }
	// 	  (*c)[j+1] *= mfnorm;
	// 	}


		  for(i = 0; i < n; i++)
		  {
		 ( c.argvalue)[i + 1] = 0.0;
		 for(j = 0; j < numChan; j++)
		 {
			( c.argvalue)[i+1] += fbank[j + 1] * cosMatrix[i * numChan + j];
		 }
		  }



		}
	  else
		{
		  FBank2MFCC(fbank, ( c.argvalue), n, TempRefObject);
		  cosMatrix = TempRefObject.argvalue;
		}
	}


	//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
	//#if __cplusplus
	}
	//#endif

	//#endif // _HSIGP_H_ 

	// ------------------------ End of HSigP.h ------------------------- 


	//
	//   This module provides a set of basic speech signal processing
	//   routines and feature level transformations.
	//

	// ------------------------ Trace Flags ------------------------- 

	static int trace = 0;
	//#define T_MEL 0002

	// -------------------- Config and Memory ----------------------- 

	static MemHeap sigpHeap = new MemHeap();
	static ConfParam[] cParm = new ConfParam[MAXGLOBS]; // config parameters 
	static int numParm = 0;

	static int asymExpoWinSize = 0; // Size of current AsynchromExponential window 
	static Vector asymExpoWin = null; // Current Asynchron Exponential window 
	// Generate Ansynchron Exponential Window 
	static void GenAsymExpoWindow(int frameSize,_float factor)
	{
	int i;
		_float a;
		_float max =0;
		final _float alpha = Math.log(factor);
		if (asymExpoWin ==null || VectorSize(asymExpoWin) < frameSize)
		  asymExpoWin = CreateVector(sigpHeap, frameSize);
		a = TPI / (frameSize -1);
		for(i =1; i<=frameSize; i++)
		  {
		asymExpoWin[i] = 0.5 * (1 - Math.cos(a * (i-1))) * (i-1)* Math.exp(alpha *(i-1));
		if(asymExpoWin[i] > max)
			max = asymExpoWin[i];
		  }
		for(i =1; i<=frameSize; i++)
			asymExpoWin[i]/=max;
		asymExpoWinSize = frameSize;
		System.out.printf("AsymExpo-Factor = %g\n",factor);
	}

	static int hamWinSize = 0; // Size of current Hamming window 
	static Vector hamWin = null; // Current Hamming window 
	// GenHamWindow: generate precomputed Hamming window function 
	static void GenHamWindow(int frameSize)
	{
	   int i;
	   _float a;

	   if (hamWin ==null || VectorSize(hamWin) < frameSize)
		  hamWin = CreateVector(sigpHeap, frameSize);
	   a = TPI / (frameSize - 1);
	   for (i =1;i<=frameSize;i++)
		  hamWin[i] = 0.54 - 0.46 * Math.cos(a*(i-1));
	   hamWinSize = frameSize;
	}

	// --------------- Linear Prediction Coding Operations ------------- 

	// AutoCorrelate: store auto coef 1 to p in r and return energy r[0] 
	static _float AutoCorrelate(Vector s, Vector r, int p, int frameSize)
	{
	   _float sum;
	   _float energy;
	   int i;
	   int j;

	   for (i =0;i<=p;i++)
	   {
		  sum = 0.0;
		  for (j =1;j<=frameSize-i;j++)
			 sum += s[j]*s[j+i];
		  if (i ==0)
			 energy = sum;
		  else
			 r[i] = sum;
	   }
	   return energy;
	}

	// Durbins recursion to get LP coeffs for auto values 
	static _float Durbin(Vector k, Vector thisA, Vector r, _float E, int order)
	{
	   Vector newA = new Vector();
	   _float ki; // Current Reflection Coefficient 
	   int i;
	   int j;

	   newA = CreateVector(gstack, order);
	   for (i =1;i<=order;i++)
	   {
		  ki = r[i]; // Calc next reflection coef 
		  for (j =1;j<i;j++)
			 ki = ki + thisA[j] * r[i - j];
		  ki = ki / E;
		  if (k!=null)
			  k[i] = ki;
		  E *= 1 - ki *ki; // Update Error 
		  newA[i] = -ki; // Calc new filter coef 
		  for (j =1;j<i;j++)
			 newA[j] = thisA[j] - ki * thisA[i - j];
		  for (j =1;j<=i;j++)
			 thisA[j] = newA[j];
	   }
	   FreeVector(gstack, newA);
	   return (E);
	}

	// EXPORT->WarpFreq: return warped frequency 
	public static _float WarpFreq(_float fcl, _float fcu, _float freq, _float minFreq, _float maxFreq, _float alpha)
	{
	   if (alpha == 1.0)
		  return freq;
	   else
	   {
		  _float scale = 1.0 / alpha;
		  _float cu = fcu * 2 / (1 + scale);
		  _float cl = fcl * 2 / (1 + scale);

		  _float au = (maxFreq - cu * scale) / (maxFreq - cu);
		  _float al = (cl * scale - minFreq) / (cl - minFreq);

		  if (freq > cu)
			 return au * (freq - cu) + scale * cu;
		  else if (freq < cl)
			 return al * (freq - minFreq) + minFreq;
		  else
			 return scale * freq;
	   }
	}


	// Matrix IDFT converts from auditory spectrum into autocorrelation values 
	public static _float MatrixIDFT(Vector as, Vector ac, DMatrix cm)
	{
	   double acc;
	   _float E;
	   int nAuto;
	   int nFreq;
	   int i;
	   int j;

	   nFreq = VectorSize(as);
	   nAuto = VectorSize(ac);

	   for (i =0; i<nAuto; i++)
	   {
		  acc = cm[i+1][1] * (double)as[1];
		  for (j =1; j<nFreq; j++)
			 acc += cm[i+1][j+1] * (double)as[j+1];

		  if (i>0)
			 ac[i] = (_float)(acc / (double)(2.0 * (nFreq-1)));
		  else
			 E = (_float)(acc / (double)(2.0 * (nFreq-1)));
	   }
	   return E; // Return zero'th auto value separately 
	}

	// ------------------- Feature Level Operations -------------------- 

	static int cepWinSize =0; // Size of current cepstral weight window 
	static int cepWinL =0; // Current liftering coeff 
	static Vector cepWin = null; // Current cepstral weight window 

	// GenCepWin: generate a new cep liftering vector 
	static void GenCepWin(int cepLiftering, int count)
	{
	   int i;
	   _float a;
	   _float Lby2;

	   if (cepWin ==null || VectorSize(cepWin) < count)
		  cepWin = CreateVector(sigpHeap, count);
	   a = DefineConstantsHSigP.PI/cepLiftering;
	   Lby2 = cepLiftering/2.0;
	   for (i =1;i<=count;i++)
		  cepWin[i] = 1.0 + Lby2 *Math.sin(i * a);
	   cepWinL = cepLiftering;
	   cepWinSize = count;
	}

	// Regression: add regression vector at +offset from source vector.  If head
	//   or tail is less than delwin then duplicate first/last vector to compensate 
	static void Regress(RefObject<_float> data, int vSize, int n, int step, int offset, int delwin, int head, int tail, boolean simpleDiffs)
	{
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp1;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *fp2;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *back;
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged.
	   _float *forw;
	   _float sum;
	   _float sigmaT2;
	   int i;
	   int t;
	   int j;

	   sigmaT2 = 0.0;
	   for (t =1;t<=delwin;t++)
		  sigmaT2 += t *t;
	   sigmaT2 *= 2.0;
	   fp = data.argvalue;
	   for (i =1;i<=n;i++)
	   {
		  fp1 = fp;
		  fp2 = fp+offset;
		  for (j =1;j<=vSize;j++)
		  {
			 back = forw = fp1;
			 sum = 0.0;
			 for (t =1;t<=delwin;t++)
			 {
				if (head+i-t > 0)
					back -= step;
				if (tail+n-i+1-t > 0)
					forw += step;
				if (!simpleDiffs)
					sum += t * (*forw - *back);
			 }
			 if (simpleDiffs)
				*fp2 = (*forw - *back) / (2 *delwin);
			 else
				*fp2 = sum / sigmaT2;
			 ++fp1;
			 ++fp2;
		  }
		  fp += step;
	   }
	}
	//****************************************************************************************************

	//************************************
	//* the wavelet package installation *
	//************************************

	public static int SetVectors(int depth, int dimFilter, int dimSignal, _float[] transformed, RefObject<Integer> dimTransformed, segmentDescription[] description)
	{
	   int i;

	   ( dimTransformed.argvalue) = dimSignal;
	   for(i = 0; i < depth; i++)
		  ( dimTransformed.argvalue) = (( dimTransformed.argvalue) + dimFilter) >> 1;
	   ( dimTransformed.argvalue) = ( dimTransformed.argvalue) * (1<<depth);

//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	   (*description) = (segmentDescription) malloc((1<<depth) * sizeof(segmentDescription));
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	   (*transformed) = (_float) malloc(( dimTransformed.argvalue) * sizeof(_float));

	   if((*description) == null || (*transformed) == null)
	   {
		  System.out.print(" failed to allocate memory \n");
		  return -1;
	   }
	   return 0;
	}

	// calc the high pass filter coefficients 
	public static void permute(_float[] low, _float[] high, int dim)
	{
	   int i;
	   int sign = 1;

	   for (i = 0 ; i < dim; i++)
	   {
		  high[i] = low[dim -1 - i] * sign;
		  sign = -sign;
	   }
	}

	public static int SetFilter(RefObject<String> type, int order, _float[] low, _float[] high, RefObject<Integer> dimFilter)
	{
	   permute(d2l, d2h, 2);
	   permute(d4l, d4h, 4);
	   permute(d6l, d6h, 6);
	   permute(d8l, d8h, 8);
	   permute(d10l, d10h, 10);
	   permute(d12l, d12h, 12);
	   permute(d14l, d14h, 14);
	   permute(d16l, d16h, 16);
	   permute(d18l, d18h, 18);
	   permute(d20l, d20h, 20);
	   permute(d22l, d22h, 22);
	   permute(d24l, d24h, 24);
	   permute(d26l, d26h, 26);
	   permute(d28l, d28h, 28);
	   permute(d30l, d30h, 30);
	   permute(d32l, d32h, 32);
	   permute(d34l, d34h, 34);
	   permute(d36l, d36h, 36);
	   permute(d38l, d38h, 38);
	   permute(d30l, d30h, 30);
	   permute(d40l, d40h, 40);
	   permute(d42l, d42h, 42);
	   permute(d44l, d44h, 44);
	   permute(d46l, d46h, 46);
	   permute(d48l, d48h, 48);
	   permute(d50l, d50h, 50);
	   permute(d52l, d52h, 52);
	   permute(d54l, d54h, 54);
	   permute(d56l, d56h, 56);
	   permute(d58l, d58h, 58);
	   permute(d60l, d60h, 60);
	   permute(d62l, d62h, 62);
	   permute(d64l, d64h, 64);
	   permute(d66l, d66h, 66);
	   permute(d68l, d68h, 68);
	   permute(d70l, d70h, 70);
	   permute(d72l, d72h, 72);
	   permute(d74l, d74h, 74);
	   permute(d76l, d76h, 76);
	   permute(d78l, d78h, 78);
	   permute(d80l, d80h, 80);
	   permute(d82l, d82h, 82);
	   permute(d84l, d84h, 84);
	   permute(d86l, d86h, 86);
	   permute(d88l, d88h, 88);
	   permute(d90l, d90h, 90);
	   permute(d92l, d92h, 92);
	   permute(d94l, d94h, 94);
	   permute(d96l, d96h, 96);
	   permute(d98l, d98h, 98);
	   permute(d100l, d100h, 100);
	   permute(d102l, d102h, 102);
	   permute(d508l, d508h, 508);

	   permute(s8l, s8h, 8);
	   permute(s10l, s10h, 10);
	   permute(s12l, s12h, 12);
	   permute(s14l, s14h, 14);
	   permute(s16l, s16h, 16);
	   permute(s18l, s18h, 18);
	   permute(s20l, s20h, 20);
	   permute(s22l, s22h, 22);
	   permute(s24l, s24h, 24);
	   permute(s26l, s26h, 26);
	   permute(s28l, s28h, 28);
	   permute(s30l, s30h, 30);
	   permute(s102l, s102h, 102);

	   permute(c6l, c6h, 6);
	   permute(c12l, c12h, 12);
	   permute(c18l, c18h, 18);
	   permute(c24l, c24h, 24);
	   permute(c30l, c30h, 30);

	   permute(b18l, b18h, 18);

	   permute(p4l, p4h, 4);

	   permute(v24l, v24h, 24);

	   if (strncmp(type.argvalue, "daubechies", 11) == 0)
	   {
		  switch (order)
		  {
		 case 2:
			 (*low)=d2l;
			 (*high)=d2h;
			 ( dimFilter.argvalue)=2;
			 break;
		 case 4:
			 (*low)=d4l;
			 (*high)=d4h;
			 ( dimFilter.argvalue)=4;
			 break;
		 case 6:
			 (*low)=d6l;
			 (*high)=d6h;
			 ( dimFilter.argvalue)=6;
			 break;
		 case 8:
			 (*low)=d8l;
			 (*high)=d8h;
			 ( dimFilter.argvalue)=8;
			 break;
		 case 10:
			 (*low)=d10l;
			 (*high)=d10h;
			 ( dimFilter.argvalue)=10;
			 break;
		 case 12:
			 (*low)=d12l;
			 (*high)=d12h;
			 ( dimFilter.argvalue)=12;
			 break;
		 case 14:
			 (*low)=d14l;
			 (*high)=d14h;
			 ( dimFilter.argvalue)=14;
			 break;
		 case 16:
			 (*low)=d16l;
			 (*high)=d16h;
			 ( dimFilter.argvalue)=16;
			 break;
		 case 18:
			 (*low)=d18l;
			 (*high)=d18h;
			 ( dimFilter.argvalue)=18;
			 break;
		 case 20:
			 (*low)=d20l;
			 (*high)=d20h;
			 ( dimFilter.argvalue)=20;
			 break;
		 case 22:
			 (*low)=d22l;
			 (*high)=d22h;
			 ( dimFilter.argvalue)=22;
			 break;
		 case 24:
			 (*low)=d24l;
			 (*high)=d24h;
			 ( dimFilter.argvalue)=24;
			 break;
		 case 26:
			 (*low)=d26l;
			 (*high)=d26h;
			 ( dimFilter.argvalue)=26;
			 break;
		 case 28:
			 (*low)=d28l;
			 (*high)=d28h;
			 ( dimFilter.argvalue)=28;
			 break;
		 case 30:
			 (*low)=d30l;
			 (*high)=d30h;
			 ( dimFilter.argvalue)=30;
			 break;
		 case 32:
			 (*low)=d32l;
			 (*high)=d32h;
			 ( dimFilter.argvalue)=32;
			 break;
		 case 34:
			 (*low)=d34l;
			 (*high)=d34h;
			 ( dimFilter.argvalue)=34;
			 break;
		 case 36:
			 (*low)=d36l;
			 (*high)=d36h;
			 ( dimFilter.argvalue)=36;
			 break;
		 case 38:
			 (*low)=d38l;
			 (*high)=d38h;
			 ( dimFilter.argvalue)=38;
			 break;
		 case 40:
			 (*low)=d40l;
			 (*high)=d40h;
			 ( dimFilter.argvalue)=40;
			 break;
		 case 42:
			 (*low)=d42l;
			 (*high)=d42h;
			 ( dimFilter.argvalue)=42;
			 break;
		 case 44:
			 (*low)=d44l;
			 (*high)=d44h;
			 ( dimFilter.argvalue)=44;
			 break;
		 case 46:
			 (*low)=d46l;
			 (*high)=d46h;
			 ( dimFilter.argvalue)=46;
			 break;
		 case 48:
			 (*low)=d48l;
			 (*high)=d48h;
			 ( dimFilter.argvalue)=48;
			 break;
		 case 50:
			 (*low)=d50l;
			 (*high)=d50h;
			 ( dimFilter.argvalue)=50;
			 break;
		 case 52:
			 (*low)=d52l;
			 (*high)=d52h;
			 ( dimFilter.argvalue)=52;
			 break;
		 case 54:
			 (*low)=d54l;
			 (*high)=d54h;
			 ( dimFilter.argvalue)=54;
			 break;
		 case 56:
			 (*low)=d56l;
			 (*high)=d56h;
			 ( dimFilter.argvalue)=56;
			 break;
		 case 58:
			 (*low)=d58l;
			 (*high)=d58h;
			 ( dimFilter.argvalue)=58;
			 break;
		 case 60:
			 (*low)=d60l;
			 (*high)=d60h;
			 ( dimFilter.argvalue)=60;
			 break;
		 case 62:
			 (*low)=d62l;
			 (*high)=d62h;
			 ( dimFilter.argvalue)=62;
			 break;
		 case 64:
			 (*low)=d64l;
			 (*high)=d64h;
			 ( dimFilter.argvalue)=64;
			 break;
		 case 66:
			 (*low)=d66l;
			 (*high)=d66h;
			 ( dimFilter.argvalue)=66;
			 break;
		 case 68:
			 (*low)=d68l;
			 (*high)=d68h;
			 ( dimFilter.argvalue)=68;
			 break;
		 case 70:
			 (*low)=d70l;
			 (*high)=d70h;
			 ( dimFilter.argvalue)=70;
			 break;
		 case 72:
			 (*low)=d72l;
			 (*high)=d72h;
			 ( dimFilter.argvalue)=72;
			 break;
		 case 74:
			 (*low)=d74l;
			 (*high)=d74h;
			 ( dimFilter.argvalue)=74;
			 break;
		 case 76:
			 (*low)=d76l;
			 (*high)=d76h;
			 ( dimFilter.argvalue)=76;
			 break;
		 case 78:
			 (*low)=d78l;
			 (*high)=d78h;
			 ( dimFilter.argvalue)=78;
			 break;
		 case 80:
			 (*low)=d80l;
			 (*high)=d80h;
			 ( dimFilter.argvalue)=80;
			 break;
		 case 82:
			 (*low)=d82l;
			 (*high)=d82h;
			 ( dimFilter.argvalue)=82;
			 break;
		 case 84:
			 (*low)=d84l;
			 (*high)=d84h;
			 ( dimFilter.argvalue)=84;
			 break;
		 case 86:
			 (*low)=d86l;
			 (*high)=d86h;
			 ( dimFilter.argvalue)=86;
			 break;
		 case 88:
			 (*low)=d88l;
			 (*high)=d88h;
			 ( dimFilter.argvalue)=88;
			 break;
		 case 90:
			 (*low)=d90l;
			 (*high)=d90h;
			 ( dimFilter.argvalue)=90;
			 break;
		 case 92:
			 (*low)=d92l;
			 (*high)=d92h;
			 ( dimFilter.argvalue)=92;
			 break;
		 case 94:
			 (*low)=d94l;
			 (*high)=d94h;
			 ( dimFilter.argvalue)=94;
			 break;
		 case 96:
			 (*low)=d96l;
			 (*high)=d96h;
			 ( dimFilter.argvalue)=96;
			 break;
		 case 98:
			 (*low)=d98l;
			 (*high)=d98h;
			 ( dimFilter.argvalue)=98;
			 break;
		 case 100:
			 (*low)=d100l;
			 (*high)=d100h;
			 ( dimFilter.argvalue)=100;
			 break;
		 case 102:
			 (*low)=d102l;
			 (*high)=d102h;
			 ( dimFilter.argvalue)=102;
			 break;
		 case 508:
			 (*low)=d508l;
			 (*high)=d508h;
			 ( dimFilter.argvalue)=508;
			 break;
			 default:
			System.out.printf("HSIGP.c: Order %i for type %s not implemented \n", order, type.argvalue);
			return -1;
		  }
	   }
	   else if (strncmp(type.argvalue, "symmlet", 8) == 0)
	   {
		  switch(order)
		  {
		 case 2:
			 (*low)=d2l;
			 (*high)=d2h;
			 ( dimFilter.argvalue)=2;
			 break;
		 case 4:
			 (*low)=d4l;
			 (*high)=d4h;
			 ( dimFilter.argvalue)=4;
			 break;
		 case 6:
			 (*low)=d6l;
			 (*high)=d6h;
			 ( dimFilter.argvalue)=6;
			 break;
		 case 8:
			 (*low)=s8l;
			 (*high)=s8h;
			 ( dimFilter.argvalue)=8;
			 break;
		 case 10:
			 (*low)=s10l;
			 (*high)=s10h;
			 ( dimFilter.argvalue)=10;
			 break;
		 case 12:
			 (*low)=s12l;
			 (*high)=s12h;
			 ( dimFilter.argvalue)=12;
			 break;
		 case 14:
			 (*low)=s14l;
			 (*high)=s14h;
			 ( dimFilter.argvalue)=14;
			 break;
		 case 16:
			 (*low)=s16l;
			 (*high)=s16h;
			 ( dimFilter.argvalue)=16;
			 break;
		 case 18:
			 (*low)=s18l;
			 (*high)=s18h;
			 ( dimFilter.argvalue)=18;
			 break;
		 case 20:
			 (*low)=s20l;
			 (*high)=s20h;
			 ( dimFilter.argvalue)=20;
			 break;
		 case 22:
			 (*low)=s22l;
			 (*high)=s22h;
			 ( dimFilter.argvalue)=22;
			 break;
		 case 24:
			 (*low)=s24l;
			 (*high)=s24h;
			 ( dimFilter.argvalue)=24;
			 break;
		 case 26:
			 (*low)=s26l;
			 (*high)=s26h;
			 ( dimFilter.argvalue)=26;
			 break;
		 case 28:
			 (*low)=s28l;
			 (*high)=s28h;
			 ( dimFilter.argvalue)=28;
			 break;
		 case 30:
			 (*low)=s30l;
			 (*high)=s30h;
			 ( dimFilter.argvalue)=30;
			 break;
		 case 102:
			 (*low)=s102l;
			 (*high)=s102h;
			 ( dimFilter.argvalue)=102;
			 break;
		 default:
			System.out.printf("order %i for type %s not implemented \n", order, type.argvalue);
			return -1;
		  }
	   }
	   else if (strncmp(type.argvalue, "coiflet", 8) == 0)
	   {
		  switch(order)
		  {
		 case 6:
			 (*low)=d6l;
			 (*high)=d6h;
			 ( dimFilter.argvalue)=6;
			 break;
		 case 12:
			 (*low)=c12l;
			 (*high)=c12h;
			 ( dimFilter.argvalue)=12;
			 break;
		 case 18:
			 (*low)=c18l;
			 (*high)=c18h;
			 ( dimFilter.argvalue)=18;
			 break;
		 case 24:
			 (*low)=c24l;
			 (*high)=c24h;
			 ( dimFilter.argvalue)=24;
			 break;
		 case 30:
			 (*low)=c30l;
			 (*high)=c30h;
			 ( dimFilter.argvalue)=30;
			 break;
		 default:
			System.out.printf("order %i for type %s not implemented \n", order, type.argvalue);
			return -1;
		  }
	   }
	   else if (strncmp(type.argvalue, "beylkin", 8) == 0)
	   {
		  switch(order)
		  {
		 case 18:
			 (*low)=b18l;
			 (*high)=b18h;
			 ( dimFilter.argvalue)=18;
			 break;
		 default:
			System.out.printf("order %i for type %s not implemented \n", order, type.argvalue);
			return -1;
		  }
	   }
	   else if (strncmp(type.argvalue, "pollen", 7) == 0)
	   {
		  switch(order)
		  {
		 case 4:
			 (*low)=p4l;
			 (*high)=p4h;
			 ( dimFilter.argvalue)=4;
			 break;
		 default:
			System.out.printf("order %i for type %s not implemented \n", order, type.argvalue);
			return -1;
		  }
	   }
	   else if (strncmp(type.argvalue, "vaidyanathan", 7) == 0)
		 {
		   switch(order)
		 {
		 case 24:
			 (*low)=v24l;
			 (*high)=v24h;
			 ( dimFilter.argvalue)=4;
			 break;
		 default:
		   System.out.printf("order %i for type %s not implemented \n", order, type.argvalue);
		   return -1;
		  }
		 }
	   else
		 {
		   System.out.printf("filter type %s unknown \n", type.argvalue);
		   return -1;
		 }
	   return 0;
	}


	public static void PrintDescription(segmentDescription[] description, int dimDescription, int depth)
	{
	   int i;
	   int j;

	   for (i = 0; i < dimDescription; i++)
	   {
		  System.out.printf("dim =  %2i    ", description[i].dim);
		  System.out.printf("energy =  %8.5f    ", description[i].energy);
		  System.out.printf("offset =  %i    ", description[i].offset);
		  System.out.print("path =  ");
		  for (j = depth - 1; j >= depth - description[i].level ; j--)
		  {
		 if((1<<j) & description[i].path)
			 System.out.print("r");
		 else
			 System.out.print("l");
		  }
		  System.out.print("\n");
	   }
	}

	// void Convolution(const float *signal, int dimSignal, const float *filter, int dimFilter, 
	//                  float *output) 
	// { 
	//    int i, j; 

	//    for ( i = 0 ; i < dimFilter - 1; i+=2) 
	//    { 
	//       output[i >> 1] = 0; 
	//       for (j = 0; j <=  i; j++) 
	//       { 
	//   	 output[i >> 1] += signal[i - j] * filter[j];   
	//       } 	 
	//    } 

	//    for( ; i < dimSignal; i+=2) 
	//    { 
	//       output[i >> 1] = 0; 
	//       for( j = 0; j < dimFilter; j++) 
	//       { 
	//  	 output[i >> 1] += signal[i -j] * filter[j]; 	 
	//       } 
	//    } 

	//    for( ; i < (dimSignal + dimFilter - 1); i+=2) 
	//    {  
	//       output[i >> 1] = 0; 
	//       for(j = (i - dimSignal +1) ; j < dimFilter; j++) 
	//       { 
	// 	 output[i >> 1] += signal[i-j] * filter[j];  
	//       }	 
	//    }  
	// } 

	public static void Convolution(_float[] signal, int dimSignal, int offsetSignal, _float[] filter, int dimFilter, int offsetFilter, _float[] output, RefObject<Integer> dimOutput, RefObject<Integer> offsetOutput)
	{
	   int i;
	   int j;
	   int position;

	   position = offsetSignal - (dimFilter + offsetFilter);

	   if(position%2 == 0)
	   {
		  ( offsetOutput.argvalue) = (position + 2) / 2;
		  ( dimOutput.argvalue) = (dimFilter + dimSignal-1) >> 1;


		  for (i = 1 ; i < dimFilter - 1; i+=2)
		  {
		 output[i >> 1] = 0;
		 for (j = 0 ; j <= i ; j++)
		 {
			output[i >> 1] += signal[j] * filter[dimFilter - 1 - i + j];
		 }
		  }

		  for(; i < dimSignal; i+=2)
		  {
		 output[i >> 1] = 0;
		 for(j = 0; j < dimFilter; j++)
		 {
			output[i >> 1] += signal[i -j] * filter[dimFilter - 1 - j];
		 }
		  }

		  for(; i < (dimSignal + dimFilter - 1); i+=2)
		  {
		 output[i >> 1] = 0;
		 for(j = (i - dimSignal +1) ; j < dimFilter; j++)
		 {
			output[i >> 1] += signal[i-j] * filter[dimFilter - 1 - j];
		 }
		  }

	   }
	   else
	   {
		  ( offsetOutput.argvalue) = (position + 1) / 2;
		  ( dimOutput.argvalue) = (dimFilter + dimSignal) >> 1;


		  for (i = 0 ; i < dimFilter - 1; i+=2)
		  {
		 output[i >> 1] = 0;
		 for (j = 0 ; j <= i ; j++)
		 {
			output[i >> 1] += signal[j] * filter[dimFilter - 1 - i + j];
		 }
		  }

		  for(; i < dimSignal; i+=2)
		  {
		 output[i >> 1] = 0;
		 for(j = 0; j < dimFilter; j++)
		 {
			output[i >> 1] += signal[i -j] * filter[dimFilter - 1 - j];
		 }
		  }

		  for(; i < (dimSignal + dimFilter - 1); i+=2)
		  {
		 output[i >> 1] = 0;
		 for(j = (i - dimSignal +1) ; j < dimFilter; j++)
		 {
			output[i >> 1] += signal[i-j] * filter[dimFilter - 1 - j];
		 }
		  }

	   }
	}



	public static _float ComputeEnergy(_float[] Signal, int dimSignal, boolean useWaveletThreshold, _float waveletThreshold, _float waveletThresholdAlpha)
	{
	   // this  is the version wiht thresholding
	  _float alpha;
	  _float threshold;
	  if (useWaveletThreshold == DefineConstantsHSigP.TRUE)
	  {
		alpha = waveletThresholdAlpha;
		threshold = waveletThreshold;
	  }
	  else
	  {
		alpha = 1.0f;
		threshold = 1.0f;
	  }
	  _float temp;
	  int i;
	  _float energy = 0;

	//   for (i = 0; i < dimSignal; i++)
	//     energy += Signal[i] * Signal[i];
	  for (i = 0; i < dimSignal; i++)
		{
		  if(Math.abs(Signal[i]) > threshold)
		{
		  if(Signal[i] > 0.0)
			{
			  temp = Signal[i] - (1 - alpha) * threshold;
			}
		  else
			{
			  temp = Signal[i] + (1 - alpha) * threshold;
			}
		}
		  else
		  {
		 temp = alpha * Signal[i];
		  }
		  energy += temp * temp;
	   }

	   energy /= dimSignal;

	   return energy;
	}
	//
	//  Checks the tree for correctness and returns 0 if anything gore right, otherwise -1
	//  PARAM: const int *tree   --  the wavelet packet tree
	//  PARAM: int depth         --  the deepth of the three
	//  PARAM: int* numChannels  --  (call by reference) number of output coefficients
	//  RETURN: int 0 or -1(false)
	// 
	public static int CheckTree(int[] tree, int depth, RefObject<Integer> numChannels)
	{
		int i;
		int j;
		int parts = 1;

		( numChannels.argvalue) = 0;

		for (i = 0; i < depth - 1; i++)
		{
			for(j = 0; j < parts; j++)
			{
				if (tree[(1 << i) + j -1] == 0)
				{
					if (tree[(2<<i) + (j<<1) - 1] == 1 || tree[(2<<i) + (j<<1)] == 1)
					{
						System.out.print("CheckTree (Wavelet.c): not a correct wpp tree definition \n");
						return -1;
					}
				}
				else
				{
				  if (tree[(2<<i) + (j<<1) - 1] == 0)
					  ( numChannels.argvalue)++;
				  if (tree[(2<<i) + (j<<1)] == 0)
					  ( numChannels.argvalue)++;
				}
			}
			parts = parts<<1;
		}

		// i = depth - 1 
		for (j = 0; j < parts; j++)
		{
		  if(tree[(1 << i) + j -1] == 1)
			  ( numChannels.argvalue) += 2;
		}

		return 0;
	}

	public static void WaveletPacket(RefObject<_float> signal, int dimSignal, _float lowPass, _float highPass, int dimFilter, int[] tree, int depth, RefObject<_float> transformed, RefObject<Integer> dimTransformed, segmentDescription[] description, RefObject<Integer> dimDescription, boolean useWaveletThreshold, _float waveletThreshold, _float waveletThresholdAlpha)
	{

	   int i;
	   int j;
	   int parts = 1;
	   int dimParts;
	   _float temp;
	   segmentDescription segment = new segmentDescription();
	   int changeFlag;
	   int address = 0;
	   int dimOutput;
	   int offsetFilter = 0;
	   int offsetSignal = 0;
	   int offsetOutput;


//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'malloc' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	   temp = (_float) malloc(( dimTransformed.argvalue) * sizeof(_float));

	   dimParts = ( dimTransformed.argvalue);
	   ( dimDescription.argvalue) = 0;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
	   memcpy(transformed.argvalue, signal.argvalue, dimSignal * sizeof(_float));

	  for (i = 0; i < (depth - 1); i++)
	  {
		for (j = parts - 1; j >= 0; j--)
		{
		  if (tree[(1 << i) + j -1] == 1)
		  {
			Convolution(transformed.argvalue + j * dimParts, dimSignal, offsetSignal, highPass, dimFilter, offsetFilter, temp + j * dimParts + (dimParts >> 1), TempRefObject, TempRefObject2);
			dimOutput = TempRefObject.argvalue;
			offsetOutput = TempRefObject2.argvalue;
			Convolution(transformed.argvalue + j * dimParts, dimSignal, offsetSignal, lowPass, dimFilter, offsetFilter, temp + j * dimParts, TempRefObject3, TempRefObject4);
			dimOutput = TempRefObject3.argvalue;
			offsetOutput = TempRefObject4.argvalue;

			if (tree[(2<<i) + (j<<1)] == 0)
			{
			   description[( dimDescription.argvalue)].offset = offsetOutput;
			   description[( dimDescription.argvalue)].dim = dimOutput;
			   description[( dimDescription.argvalue)].energy = ComputeEnergy(temp + j * dimParts + (dimParts >> 1), dimOutput, useWaveletThreshold, waveletThreshold, waveletThresholdAlpha);
			   description[( dimDescription.argvalue)].path = (1<<(depth - i)) * j + (1<<(depth - i - 1));
			   description[( dimDescription.argvalue)].level = i + 1;

			   ( dimDescription.argvalue)++;
			}

			if (tree[(2<<i) + (j<<1) - 1] == 0)
			{
			   description[( dimDescription.argvalue)].offset = offsetOutput;
			   description[( dimDescription.argvalue)].dim = dimOutput;
			   description[( dimDescription.argvalue)].energy = ComputeEnergy(temp + j * dimParts, dimOutput, useWaveletThreshold, waveletThreshold, waveletThresholdAlpha);
			   description[( dimDescription.argvalue)].path = (1<<(depth - i)) * j;
			   description[( dimDescription.argvalue)].level = i + 1;

			   ( dimDescription.argvalue)++;
			}
		  }
		}
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		memcpy(transformed.argvalue, temp, ( dimTransformed.argvalue) * sizeof(_float));
		dimSignal = dimOutput;
		offsetSignal = offsetOutput;
		parts = parts << 1;
		dimParts = dimParts >> 1;
	   }


	   i = depth -1;
	   for (j = parts - 1; j >= 0; j--)
	   {
		  if (tree[(1 << i) + j -1] == 1)
		  {
			Convolution(transformed.argvalue + j * dimParts, dimSignal, offsetSignal, highPass, dimFilter, offsetFilter, temp + j * dimParts + (dimParts >> 1), TempRefObject5, TempRefObject6);
			dimOutput = TempRefObject5.argvalue;
			offsetOutput = TempRefObject6.argvalue;
			Convolution(transformed.argvalue + j * dimParts, dimSignal, offsetSignal, lowPass, dimFilter, offsetFilter, temp + j * dimParts, TempRefObject7, TempRefObject8);
			dimOutput = TempRefObject7.argvalue;
			offsetOutput = TempRefObject8.argvalue;

			description[( dimDescription.argvalue)].offset = offsetOutput;
			description[( dimDescription.argvalue)].dim = dimOutput;
			description[( dimDescription.argvalue)].energy = ComputeEnergy(temp + j * dimParts + (dimParts >> 1), dimOutput, useWaveletThreshold, waveletThreshold, waveletThresholdAlpha);
			description[( dimDescription.argvalue)].path = (j<<1) + 1;
			description[( dimDescription.argvalue)].level = i + 1;

			( dimDescription.argvalue)++;
			description[( dimDescription.argvalue)].offset = offsetOutput;
			description[( dimDescription.argvalue)].dim = dimOutput;
			description[( dimDescription.argvalue)].energy = ComputeEnergy(temp + j * dimParts, dimOutput, useWaveletThreshold, waveletThreshold, waveletThresholdAlpha);
			description[( dimDescription.argvalue)].path = (j<<1);
			description[( dimDescription.argvalue)].level = i + 1;
			( dimDescription.argvalue)++;
		  }
	   }


	   parts = parts << 1;
	   dimParts = dimParts >> 1;


	   for (i = 0; i < ( dimDescription.argvalue) - 1; i++)
	   {
		  changeFlag = 0;

		  for (j = 0; j < ( dimDescription.argvalue) - 1; j++)
		  {
			if (description[j+1].path > description[j].path)
			{
			  changeFlag = 1;

			  segment = description[j];
			  description[j] = description[j+1];
			  description[j+1] = segment;
			}
		  }

		  if (changeFlag == 0)
			  break;
	   }

	   for (i = 0; i < ( dimDescription.argvalue); i++)
	   {
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		memcpy(transformed.argvalue + address, temp + (description[i].path * dimParts), description[i].dim * sizeof(_float));
		address += description[i].dim;
	   }

	   ( dimTransformed.argvalue) = address;


//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'free' has no equivalent in Java:
	   free(temp);
	}


	//********************************************************************************************************************************************
	public static int Wavelet(_float[] signal, int dimSignal, _float[] lowPass, _float[] highPass, int dimFilter, int depth, _float[] transformed)
	{
	   int i;
	   int j;
	   int k;

	   if(((dimSignal>>depth)<<depth) != dimSignal)
	   {
		  System.out.printf("circular wavelet transformation of depth %i with signal length %i not possible\n", depth, dimSignal);
		  return -1;
	   }

	   if((dimSignal>>(depth - 1)) < dimFilter)
	   {
		  System.out.printf("signal length(%i) to short for circular wavelet transformation of depth %i with filter length %i\n", dimSignal, depth, dimFilter);
	   }


	   for (i = 0; i < depth; i++)
	   {

	//          lowPass
	//          for(j = 0; j < dimFilter; j += 2)
	//          {
	//    	 transformed[j>>1] = lowPass[0] * signal[j];
	//    	 for (k = 1; k <=j; k++)
	//    	 {
	//    	    transformed[j>>1] += lowPass[k] * signal[j - k];
	//    	 }
	//    	 for (k = j + 1; k < dimFilter; k++)
	//    	 {
	//    	    transformed[j>>1] += lowPass[k] * signal[j - k + dimSignal];
	//    	 }
	//          }
	//          for(; j < dimSignal; j += 2)
	//          {
	//    	 transformed[j>>1] = lowPass[0] * signal[j];
	//    	 for (k = 1; k < dimFilter; k++)
	//    	 {
	//    	    transformed[j>>1] += lowPass[k] * signal[j - k];
	//    	 }
	//          }
	//          

		  for(j = 0; j < dimSignal; j+=2)
		  {
			transformed[j>>1] = 0;
			for(k = 0; k < dimFilter; k++)
			{
			  transformed[j>>1] += signal[(j + k) % dimSignal] * lowPass[k];
			}
		  }


	//          highPass
	//          for(j = 0; j < dimFilter; j += 2)
	//          {
	//    	 transformed[(j>>1) + (dimSignal>>1)] = highPass[0] * signal[j];
	//    	 for (k = 1; k <=j; k++)
	//    	 {
	//    	    transformed[(j>>1) + (dimSignal>>1)] += highPass[k] * signal[j - k];
	//    	 }
	//    	 for (k = j + 1; k < dimFilter; k++)
	//    	 {
	//    	    transformed[(j>>1) + (dimSignal>>1)] += highPass[k] * signal[j - k + dimSignal];
	//    	 }
	//          }
	//          for(; j < dimSignal; j += 2)
	//          {
	//    	 transformed[(j>>1) + (dimSignal>>1)] = highPass[0] * signal[j];
	//    	 for (k = 1; k < dimFilter; k++)
	//    	 {
	//    	    transformed[(j>>1) + (dimSignal>>1)] += highPass[k] * signal[j - k];
	//    	 }
	//          }
	//          

		  for(j = 0; j < dimSignal; j+=2)
		  {
			transformed[(j>>1) + (dimSignal>>1)] = 0;
			for(k = 0; k < dimFilter; k++)
			{
			  transformed[(j>>1) + (dimSignal>>1)] += signal[(j + k) % dimSignal] * highPass[k];
			}
		  }
		  dimSignal = dimSignal>>1;
//C++ TO JAVA CONVERTER TODO TASK: The memory management function 'memcpy' has no equivalent in Java:
//C++ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		  memcpy(signal, transformed, dimSignal * sizeof(_float));
	   }
	   return 0;
	}
}

//C++ TO JAVA CONVERTER WARNING: The following #include directive was ignored:
//#include "HShell.h" // HTK Libraries 
//C++ TO JAVA CONVERTER WARNING: The following #include directive was ignored:
//#include "wavelet.h" // Wavelet filters 
//C++ TO JAVA CONVERTER WARNING: The following #include directive was ignored:
//#include "HMem.h"
//C++ TO JAVA CONVERTER WARNING: The following #include directive was ignored:
//#include "HMath.h"
// ----------------------------------------------------------- 
//                                                             
//                          ___                                
//                       |_| | |_/   SPEECH                    
//                       | | | | \   RECOGNITION               
//                       =========   SOFTWARE                   
//                                                             
//                                                             
// ----------------------------------------------------------- 
// developed at:                                               
//                                                             
//      Speech Vision and Robotics group                       
//      Cambridge University Engineering Department            
//      http://svr-www.eng.cam.ac.uk/                          
//                                                             
//      Entropic Cambridge Research Laboratory                 
//      (now part of Microsoft)                                
//                                                             
// ----------------------------------------------------------- 
//         Copyright: Microsoft Corporation                    
//          1995-2000 Redmond, Washington USA                  
//                    http://www.microsoft.com                 
//                                                             
//              2001  Cambridge University                     
//                    Engineering Department                   
//                                                             
//   Use of this software is governed by a License Agreement   
//    ** See the file License for the Conditions of Use  **    
//    **     This banner notice must not be removed      **    
//                                                             
// ----------------------------------------------------------- 
//      File: HSigP.h:   Signal Processing Routines            
// ----------------------------------------------------------- 

// !HVER!HSigP:   3.2.1 [CUED 15/10/03] 

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if ! _HSIGP_H_
//#define _HSIGP_H_

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if __cplusplus
//C++ TO JAVA CONVERTER TODO TASK: Extern blocks are not supported in Java.
extern "C"
{
//#endif
//
//   On entry, s should hold n complex points; VectorSize(s)=n*2
//   On return, m holds (log) modulus/phase of s in first n points
//

// -------------------- MFCC Related Operations -------------------- 

public class FBankInfo
{
   public int frameSize; // speech frameSize 
   public int numChans; // number of channels 
   public int sampPeriod; // sample period 
   public int fftN; // fft size 
   public int klo; // lopass to hipass cut-off fft indices 
   public int khi;
   public boolean usePower; // use power rather than magnitude 
   public boolean takeLogs; // log filterbank channels 
   public GlobalMembersHSigP.float fres; // scaled fft resolution 
   public Vector cf = new Vector(); // array[1..pOrder+1] of ce9ntre freqs 
   public ShortVec loChan = new ShortVec(); // array[1..fftN/2] of loChan index 
   public Vector loWt = new Vector(); // array[1..fftN/2] of loChan weighting 
   public Vector x = new Vector(); // array[1..fftN] of fftchans 

  public int waveletCorr; // Wavelet 1=SBC 2=DCT (chris) 
}
// 
//   normalise log energy to range -X .. 1.0 by subtracting the max log
//   energy and adding 1.0.  The lowest energy level is set by the value
//   of silFloor which gives the ratio between the max and min energies
//   in dB.  Escale is used to scale the normalised log energy.
//

// ------------------- Wavelet Package Operations -------------------- 

// This is the definition for the four supported wavelet types 
public enum wlType
{
	DAUBECHIES,
	SYMMLET,
	COIFLET,
	BEYLKIN,
	POLLEN,
	VAIDYANATHAN
}
// the wavelet package tree definition 
public class wlTree
{
  public int tree;
  public int depth;
  public int numcannels;
}
  RefObject<String> TempRefObject = new RefObject<String>(waveletType);
  RefObject<Integer> TempRefObject2 = new RefObject<Integer>(dimFilter);
  RefObject<Integer> TempRefObject3 = new RefObject<Integer>(dimTransformed);
  RefObject<Float> TempRefObject4 = new RefObject<Float>(transformed);
  RefObject<Integer> TempRefObject5 = new RefObject<Integer>(dimTransformed);
  RefObject<Integer> TempRefObject6 = new RefObject<Integer>(dimDescription);
  RefObject<Integer> TempRefObject = new RefObject<Integer>(dimFilter);
	  RefObject<Float> TempRefObject = new RefObject<Float>(cosMatrix);
		RefObject<Integer> TempRefObject = new RefObject<Integer>(dimOutput);
		RefObject<Integer> TempRefObject2 = new RefObject<Integer>(offsetOutput);
		RefObject<Integer> TempRefObject3 = new RefObject<Integer>(dimOutput);
		RefObject<Integer> TempRefObject4 = new RefObject<Integer>(offsetOutput);
		RefObject<Integer> TempRefObject5 = new RefObject<Integer>(dimOutput);
		RefObject<Integer> TempRefObject6 = new RefObject<Integer>(offsetOutput);
		RefObject<Integer> TempRefObject7 = new RefObject<Integer>(dimOutput);
		RefObject<Integer> TempRefObject8 = new RefObject<Integer>(offsetOutput);


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