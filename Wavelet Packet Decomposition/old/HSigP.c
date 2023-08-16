/* ----------------------------------------------------------- */
/*                                                             */
/*                          ___                                */
/*                       |_| | |_/   SPEECH                    */
/*                       | | | | \   RECOGNITION               */
/*                       =========   SOFTWARE                  */ 
/*                                                             */
/*                                                             */
/* ----------------------------------------------------------- */
/* developed at:                                               */
/*                                                             */
/*      Speech Vision and Robotics group                       */
/*      Cambridge University Engineering Department            */
/*      http://svr-www.eng.cam.ac.uk/                          */
/*                                                             */
/*      Entropic Cambridge Research Laboratory                 */
/*      (now part of Microsoft)                                */
/*                                                             */
/* ----------------------------------------------------------- */
/*         Copyright: Microsoft Corporation                    */
/*          1995-2000 Redmond, Washington USA                  */
/*                    http://www.microsoft.com                 */
/*                                                             */
/*              2001  Cambridge University                     */
/*                    Engineering Department                   */
/*                                                             */
/*   Use of this software is governed by a License Agreement   */
/*    ** See the file License for the Conditions of Use  **    */
/*    **     This banner notice must not be removed      **    */
/*                                                             */
/* ----------------------------------------------------------- */
/*      File: HSigP.c:   Signal Processing Routines            */
/* ----------------------------------------------------------- */

char *hsigp_version = "!HVER!HSigP:   3.2.1 [CUED 15/10/03]";
char *hsigp_vc_id = "$Id: HSigP.c,v 1.6 2005/06/30 16:21:17 christoph Exp $";

#include "HShell.h"        /* HTK Libraries */
#include "wavelet.h"       /* Wavelet filters */
#include "HMem.h"
#include "HMath.h"
#include "HSigP.h"

/*
   This module provides a set of basic speech signal processing
   routines and feature level transformations.
*/

/* ------------------------ Trace Flags ------------------------- */

static int trace = 0;
#define T_MEL  0002     /* Mel filterbank */

/* -------------------- Config and Memory ----------------------- */

static MemHeap sigpHeap;
static ConfParam *cParm[MAXGLOBS];       /* config parameters */
static int numParm = 0;

/* ---------------------- Initialisation -------------------------*/

/* EXPORT->InitSigP: initialise the SigP module */
void InitSigP(void)
{
   int i;

   Register(hsigp_version,hsigp_vc_id);
   numParm = GetConfig("HSIGP", TRUE, cParm, MAXGLOBS);
   if (numParm>0){
      if (GetConfInt(cParm,numParm,"TRACE",&i)) trace = i;
   }
   CreateHeap(&sigpHeap,"sigpHeap",MSTAK,1,0.0,5000,5000);
}

/* --------------- Windowing and PreEmphasis ---------------------*/

/* ZeroMean: zero mean a complete speech waveform */
void ZeroMean(short *data, long nSamples)
{
   long i,hiClip=0,loClip=0;
   short *x;
   double sum=0.0,y,mean;

   x = data;
   for (i=0;i<nSamples;i++,x++)
      sum += *x;
   mean = sum / (float)nSamples;
   x = data;
   for (i=0;i<nSamples;i++,x++){
      y = (double)(*x) - mean;
      if (y<-32767.0){
         y = -32767.0; ++loClip;
      }
      if (y>32767.0){
         y = 32767.0; ++hiClip;
      }
      *x = (short) ((y>0.0) ? y+0.5 : y-0.5);
   }
   if (loClip>0)
      HError(-5322,"ZeroMean: %d samples too -ve\n",loClip);
   if (hiClip>0)
      HError(-5322,"ZeroMean: %d samples too +ve\n",hiClip);
}

static int asymExpoWinSize = 0;          /* Size of current AsynchromExponential  window */
static Vector asymExpoWin = NULL;        /* Current Asynchron Exponential  window */
/* Generate Ansynchron Exponential Window */
static void GenAsymExpoWindow (int frameSize,float factor)
{
int   i;
    float a;
    float max=0;
    const float alpha = log(factor);
    if (asymExpoWin==NULL || VectorSize(asymExpoWin) < frameSize)
      asymExpoWin = CreateVector(&sigpHeap,frameSize);
    a= TPI / (frameSize -1);
    for(i=1; i<=frameSize; i++)
      {
	asymExpoWin[i] = 0.5 * (1 - cos(a * (i-1))) * (i-1)*  exp(alpha *(i-1)) ;
	if(asymExpoWin[i] > max) max = asymExpoWin[i];
      }
    for(i=1; i<=frameSize; i++) asymExpoWin[i]/=max;
    asymExpoWinSize = frameSize;
    printf("AsymExpo-Factor = %g\n",factor);
}
void AsymExpo (Vector s,float factor)
{
  int i,frameSize;
  frameSize=VectorSize(s);
  if (asymExpoWinSize != frameSize)
    GenAsymExpoWindow(frameSize,factor);
  for (i=1;i<=frameSize;i++)
    s[i] *= asymExpoWin[i];
}

static int hamWinSize = 0;          /* Size of current Hamming window */
static Vector hamWin = NULL;        /* Current Hamming window */
/* GenHamWindow: generate precomputed Hamming window function */
static void GenHamWindow (int frameSize)
{
   int i;
   float a;
   
   if (hamWin==NULL || VectorSize(hamWin) < frameSize)
      hamWin = CreateVector(&sigpHeap,frameSize);
   a = TPI / (frameSize - 1);
   for (i=1;i<=frameSize;i++)
      hamWin[i] = 0.54 - 0.46 * cos(a*(i-1));
   hamWinSize = frameSize;
}

/* EXPORT->Ham: Apply Hamming Window to Speech frame s */
void Ham (Vector s)
{
   int i,frameSize;
   
   frameSize=VectorSize(s);
   if (hamWinSize != frameSize)
      GenHamWindow(frameSize);
   for (i=1;i<=frameSize;i++)
      s[i] *= hamWin[i];
}

/* EXPORT->PreEmphasise: pre-emphasise signal in s */
void PreEmphasise (Vector s, float k)
{
   int i;
   float preE;
   
   preE = k;
   for (i=VectorSize(s);i>=2;i--)
      s[i] -= s[i-1]*preE;
   s[1] *= 1.0-preE;
}

/* --------------- Linear Prediction Coding Operations ------------- */

/* AutoCorrelate: store auto coef 1 to p in r and return energy r[0] */
static float AutoCorrelate(Vector s, Vector r, int p, int frameSize)
{
   float sum,energy;
   int   i,j;

   for (i=0;i<=p;i++) {
      sum = 0.0;
      for (j=1;j<=frameSize-i;j++)
         sum += s[j]*s[j+i];
      if (i==0) 
         energy = sum;
      else
         r[i] = sum;
   }
   return energy;
}

/* Durbins recursion to get LP coeffs for auto values */
static float Durbin(Vector k, Vector thisA, Vector r, float E, int order)
{
   Vector newA;
   float ki;         /* Current Reflection Coefficient */
   int i,j;
 
   newA  = CreateVector(&gstack,order);
   for (i=1;i<=order;i++) {
      ki = r[i];              /* Calc next reflection coef */
      for (j=1;j<i;j++)
         ki = ki + thisA[j] * r[i - j];
      ki = ki / E;   
      if (k!=NULL) k[i] = ki;
      E *= 1 - ki*ki;         /* Update Error */
      newA[i] = -ki;          /* Calc new filter coef */
      for (j=1;j<i;j++)
         newA[j] = thisA[j] - ki * thisA[i - j];
      for (j=1;j<=i;j++)   
         thisA[j] = newA[j];
   }
   FreeVector(&gstack,newA);
   return (E);
}

/* EXPORT->Wave2LPC: Calculate LPCoef in a & RefC in k */
void Wave2LPC (Vector s, Vector a, Vector k, float *re, float *te)
{
   Vector thisA;     /* Current LP filter coefficients */
   Vector r;         /* AutoCorrelation Sequence */
   float E;          /* Prediction Error */
   int   p,frameSize;

   if (a==NULL && k==NULL)
      HError(5320,"Wave2LPC: Null a and k vectors in WaveToLPC");  
   if (a!=NULL) 
      p=VectorSize(a); 
   else
      p=VectorSize(k);
   r = CreateVector(&gstack,p);
   thisA = (a!=NULL)?a:CreateVector(&gstack,p);
   frameSize=VectorSize(s);
   E = AutoCorrelate(s,r,p,frameSize);
   *te = E;
   *re = Durbin(k,thisA,r,E,p);
   FreeVector(&gstack,r);
}

/* EXPORT->LPC2RefC: transfer from filter to ref coef */
void LPC2RefC(Vector a, Vector k)
{
   Vector thisA; /* Current LP filter coefficients */
   Vector newA;  /* New LP filter coefficients */
   int i,j,p;
   float ki,x;
   
   p=VectorSize(a);
   thisA = CreateVector(&gstack,p);
   newA  = CreateVector(&gstack,p);
   CopyVector(a,thisA);
   for (i=p;i>=1;i--)  { 
      ki = -thisA[i];
      k[i] = ki;
      x = 1 - ki*ki;
      for (j=1;j<i;j++) 
         newA[j] = (thisA[j] + ki * thisA[i - j]) / x;
      for (j=1;j<i;j++) 
         thisA[j] = newA[j];
   }
   FreeVector(&gstack,thisA);
}

/* EXPORT->RefC2LPC: transfer from ref coef to filter */
void RefC2LPC (Vector k, Vector a)
{
   Vector thisA; /* Current LP filter coefficients */
   Vector newA;  /* New LP filter coefficients */
   int i,j,p;
   float ki;
   
   p=VectorSize(k);
   thisA = CreateVector(&gstack,p);
   newA  = CreateVector(&gstack,p);
   for (i=1;i<=p;i++) { 
      ki = k[i];
      newA[i] = -ki;
      for (j=1;j<i;j++) 
         newA[j] = thisA[j] - ki * thisA[i - j];
      for (j=1;j<=i;j++)   
         thisA[j] = newA[j];
   }
   for (i=1;i<=p;i++)  a[i]=thisA[i];
   FreeVector(&gstack,thisA);
}

/* EXPORT->LPC2Cepstrum: transfer from lpc to cepstral coef */
void LPC2Cepstrum (Vector a, Vector c)
{
   int i,n,p;
   float sum;
   
   p=VectorSize(c);
   for (n=1;n<=p;n++)  { 
      sum = 0.0;
      for (i=1;i<n;i++) 
         sum = sum + (n - i) * a[i] * c[n - i];
      c[n] = -(a[n] + sum / n);
   }
}

/* EXPORT->Cepstrum2LPC: transfer from cepstral coef to lpc */
void Cepstrum2LPC (Vector c, Vector a)
{
   int i,n,p;
   float sum;
   
   p=VectorSize(a);
   for (n=1;n<=p;n++)  { 
      sum = 0.0;
      for (i=1;i<n;i++) 
         sum = sum + (n - i) * a[i] * c[n - i];
      a[n] = -(c[n] + sum / n);
   }
}

/* -------------------- FFT Based Operations ----------------------- */

/* EXPORT-> FVec2Spectrum: cvt feature vector f to a spectrum, fzero
   is the value of the 0'th feature vector coefficient which
   is typically omitted by HSigP routines eg a0 = 1.0 for LPC
*/
void FVec2Spectrum (float fzero, Vector f, Vector s)
{
   int i,p,n;
   
   p=VectorSize(f); n=VectorSize(s);
   s[1] = fzero;
   for (i=1;i<=p;i++) 
      s[i+1] = f[i];
   for (i=p+2;i<=n;i++) 
      s[i] = 0.0;
   Realft(s);
}

/* EXPORT-> FFT: apply fft/invfft to complex s */
void FFT(Vector s, int invert)
{
   int ii,jj,n,nn,limit,m,j,inc,i;
   double wx,wr,wpr,wpi,wi,theta;
   double xre,xri,x;
   
   n=VectorSize(s);
   nn=n / 2; j = 1;
   for (ii=1;ii<=nn;ii++) {
      i = 2 * ii - 1;
      if (j>i) {
         xre = s[j]; xri = s[j + 1];
         s[j] = s[i];  s[j + 1] = s[i + 1];
         s[i] = xre; s[i + 1] = xri;
      }
      m = n / 2;
      while (m >= 2  && j > m) {
         j -= m; m /= 2;
      }
      j += m;
   };
   limit = 2;


   while (limit < n) {
      inc = 2 * limit; theta = TPI / limit;
      if (invert) theta = -theta;
      wpr = cos(theta);
      wpi = sin(theta);
      wr = 1.0; wi = 0.0;
      for (ii=1; ii<=limit/2; ii++)
      {
         m = 2 * ii - 1;
         for (jj = 0; jj<=(n - m) / inc;jj++) {
            i = m + jj * inc;
            j = i + limit;
            xre = wr * s[j] - wi * s[j + 1];
            xri = wr * s[j + 1] + wi * s[j];
            s[j] = s[i] - xre; s[j + 1] = s[i + 1] - xri;
            s[i] = s[i] + xre; s[i + 1] = s[i + 1] + xri;
         }
         wx = wr;
         wr = wr * wpr - wi * wpi;
         wi = wi * wpr + wx * wpi;

      }
      limit = inc;
   }
   if (invert)
      for (i = 1;i<=n;i++) 
         s[i] = s[i] / nn;
   
}

/* EXPORT-> Realft: apply fft to real s */
void Realft (Vector s)
{
   int n, n2, i, i1, i2, i3, i4;
   double xr1, xi1, xr2, xi2, a, b;
   double yr, yi, yr2, yi2, yr0, theta;

   n=VectorSize(s) / 2;
   theta = PI / n;
   FFT(s, FALSE);
   yr2 = cos(theta);
   yi2 = sin(theta); yr = yr2; yi = yi2;
   for (i=4; i<=n; i+=2)
   {
      i1 = i - 1;      i2 = i;
      i4 = ((n + 2) << 1) - i;
      i3 = i4 - 1;
     
      xr1 = (s[i1] + s[i3])/2.0; xi1 = (s[i2] - s[i4])/2.0;
      xr2 = (s[i2] + s[i4])/2.0; xi2 = (s[i3] - s[i1])/2.0;
      a = yr * xr2 - yi * xi2;
      b = yr * xi2 + yi * xr2;
      s[i1] =  xr1 + a;
      s[i2] =  xi1 + b;
      s[i3] =  xr1 - a;
      s[i4] = -xi1 + b;
      yr0 = yr;
      yr = yr * yr2 - yi  * yi2;
      yi = yi * yr2 + yr0 * yi2;
   }
   xr1 = s[1];
   s[1] = xr1 + s[2];
   s[2] = 0.0;
}
   
/* EXPORT-> SpecModulus: store modulus of s in m */
void SpecModulus(Vector s, Vector m)
{
   int i,j;
   float x,y;
   
   for (i=1;i<=VectorSize(s)/2;i++) {
      j=i+i; x=s[j-1]; y=s[j];
      m[i]=sqrt(x*x + y*y);
   }
}

/* EXPORT-> SpecLogModulus: store log modulus of s in m */
void SpecLogModulus(Vector s, Vector m, Boolean invert)
{
   int i,j;
   float x,y;
   
   for (i=1;i<=VectorSize(s)/2;i++) {
      j=i+i; x=s[j-1]; y=s[j];
      x=0.5*log(x*x + y*y);
      m[i] = invert ? -x : x;
   }
}

/* EXPORT-> SpecPhase: store phase of s in m */
void SpecPhase(Vector s, Vector m)
{
   int i,j;
   float ph,re,im;
   
   for (i=1;i<=VectorSize(s)/2;i++) {
      j=i+i;
      re=s[j-1]; im=s[j];
      if (re==0.0) 
         ph = (im>=0.0) ? PI/2.0 : -PI/2.0;
      else {
         ph=atan(im/re);
         if (ph<0.0 && re<0.0)
            ph += PI;
         else if (ph>0.0 && im<0.0)
            ph -= PI;
      }
      m[i]=ph;
   }
}

/* -------------------- MFCC Related Operations -------------------- */

/* EXPORT->Mel: return mel-frequency corresponding to given FFT index */
float Mel(int k,float fres)
{
   return 1127 * log(1 + (k-1)*fres);
}

/* EXPORT->WarpFreq: return warped frequency */
float WarpFreq (float fcl, float fcu, float freq, float minFreq, float maxFreq , float alpha)
{
   if (alpha == 1.0)
      return freq;
   else {
      float scale = 1.0 / alpha;
      float cu = fcu * 2 / (1 + scale);
      float cl = fcl * 2 / (1 + scale);

      float au = (maxFreq - cu * scale) / (maxFreq - cu);
      float al = (cl * scale - minFreq) / (cl - minFreq);
      
      if (freq > cu)
         return  au * (freq - cu) + scale * cu ;
      else if (freq < cl)
         return al * (freq - minFreq) + minFreq ;
      else
         return scale * freq ;
   }
}

/* EXPORT->InitFBank: Initialise an FBankInfo record */
FBankInfo InitFBank(MemHeap *x, int frameSize, long sampPeriod, int numChans,
                    float lopass, float hipass, Boolean usePower, Boolean takeLogs,
                    Boolean doubleFFT,
                    float alpha, float warpLowCut, float warpUpCut)
{
   FBankInfo fb;
   float mlo,mhi,ms,melk;
   int k,chan,maxChan,Nby2;

   /* Save sizes to cross-check subsequent usage */
   fb.frameSize = frameSize; fb.numChans = numChans;
   fb.sampPeriod = sampPeriod; 
   fb.usePower = usePower; fb.takeLogs = takeLogs;
   /* Calculate required FFT size */
   fb.fftN = 2;   
   while (frameSize>fb.fftN) fb.fftN *= 2;
   if (doubleFFT) 
      fb.fftN *= 2;
   Nby2 = fb.fftN / 2;
   fb.fres = 1.0E7/(sampPeriod * fb.fftN * 700.0);
   maxChan = numChans+1;
   /* set lo and hi pass cut offs if any */
   fb.klo = 2; fb.khi = Nby2;       /* apply lo/hi pass filtering */
   mlo = 0; mhi = Mel(Nby2+1,fb.fres);
   if (lopass>=0.0) {
      mlo = 
      27*log(1+lopass/700.0);
      fb.klo = (int) ((lopass * sampPeriod * 1.0e-7 * fb.fftN) + 2.5);
      if (fb.klo<2) fb.klo = 2;
   }
   if (hipass>=0.0) {
      mhi = 1127*log(1+hipass/700.0);
      fb.khi = (int) ((hipass * sampPeriod * 1.0e-7 * fb.fftN) + 0.5);
      if (fb.khi>Nby2) fb.khi = Nby2;
   }
   if (trace&T_MEL){
      printf("FFT passband %d to %d out of 1 to %d\n",fb.klo,fb.khi,Nby2);
      printf("Mel passband %f to %f\n",mlo,mhi);
   }
   /* Create vector of fbank centre frequencies */
   fb.cf = CreateVector(x,maxChan);
   ms = mhi - mlo;
   for (chan=1; chan <= maxChan; chan++) {
      if (alpha == 1.0) {
         fb.cf[chan] = ((float)chan/(float)maxChan)*ms + mlo;
      }
      else {
         /* scale assuming scaling starts at lopass */
         float minFreq = 700.0 * (exp (mlo / 1127.0) - 1.0 );
         float maxFreq = 700.0 * (exp (mhi / 1127.0) - 1.0 );
         float cf = ((float)chan / (float) maxChan) * ms + mlo;
         
         cf = 700 * (exp (cf / 1127.0) - 1.0);
         
         fb.cf[chan] = 1127.0 * log (1.0 + WarpFreq (warpLowCut, warpUpCut, cf, minFreq, maxFreq, alpha) / 700.0);
      }
   }
   
   /* Create loChan map, loChan[fftindex] -> lower channel index */
   fb.loChan = CreateShortVec(x,Nby2);
   for (k=1,chan=1; k<=Nby2; k++){
      melk = Mel(k,fb.fres);
      if (k<fb.klo || k>fb.khi) fb.loChan[k]=-1;
      else {
         while (fb.cf[chan] < melk  && chan<=maxChan) ++chan;
         fb.loChan[k] = chan-1;
      }
   }

   /* Create vector of lower channel weights */   
   fb.loWt = CreateVector(x,Nby2);
   for (k=1; k<=Nby2; k++) {
      chan = fb.loChan[k];
      if (k<fb.klo || k>fb.khi) fb.loWt[k]=0.0;
      else {
         if (chan>0) 
            fb.loWt[k] = ((fb.cf[chan+1] - Mel(k,fb.fres)) / 
                          (fb.cf[chan+1] - fb.cf[chan]));
         else
            fb.loWt[k] = (fb.cf[1]-Mel(k,fb.fres))/(fb.cf[1] - mlo);
      }
   }
   /* Create workspace for fft */
   fb.x = CreateVector(x,fb.fftN);
   return fb;
}

/* EXPORT->Wave2FBank:  Perform filterbank analysis on speech s */
void Wave2FBank(Vector s, Vector fbank, float *te, FBankInfo info)
{
   const float melfloor = 1.0;
   int k, bin;
   float t1,t2;   /* real and imag parts */
   float ek;      /* energy of k'th fft channel */
   
   /* Check that info record is compatible */
   if (info.frameSize != VectorSize(s))
      HError(5321,"Wave2FBank: frame size mismatch");
   if (info.numChans != VectorSize(fbank))
      HError(5321,"Wave2FBank: num channels mismatch");
   /* Compute frame energy if needed */
   if (te != NULL){
      *te = 0.0;  
      for (k=1; k<=info.frameSize; k++) 
         *te += (s[k]*s[k]);
   }
   /* Apply FFT */
   for (k=1; k<=info.frameSize; k++) 
      info.x[k] = s[k];    /* copy to workspace */
   for (k=info.frameSize+1; k<=info.fftN; k++) 
      info.x[k] = 0.0;   /* pad with zeroes */
   Realft(info.x);                            /* take fft */

   /* Fill filterbank channels */
   ZeroVector(fbank); 
   for (k = info.klo; k <= info.khi; k++) {             /* fill bins */
      t1 = info.x[2*k-1]; t2 = info.x[2*k];
      if (info.usePower)
         ek = t1*t1 + t2*t2;
      else
         ek = sqrt(t1*t1 + t2*t2);
      bin = info.loChan[k];
      t1 = info.loWt[k]*ek;
      if (bin>0) fbank[bin] += t1;
      if (bin<info.numChans) fbank[bin+1] += ek - t1;
   }

   /* Take logs */
   if (info.takeLogs)
      for (bin=1; bin<=info.numChans; bin++) { 
         t1 = fbank[bin];
         if (t1<melfloor) t1 = melfloor;
         fbank[bin] = log(t1);
      }
}

/* EXPORT->FBank2MFCC: compute first n cepstral coeff */
/* cosine transformation */
void FBank2MFCC(Vector fbank, Vector c, int n, float* cosMatrix)
{
   int i, j, numChan;
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

/* EXPORT->FBank2MelSpec: convert log fbank to linear */
void FBank2MelSpec(Vector fbank)
{
   int i;
   
   for (i=1; i<=VectorSize(fbank); i++)
      fbank[i] = exp(fbank[i]);
}
   

/* EXPORT->MelSpec2FBank: convert lin mel spectrum to log fbank */
void MelSpec2FBank(Vector melspec)
{
   int i;
   float x;
   
   for (i=1; i<=VectorSize(melspec); i++){
      x = melspec[i];
      if (x<1.0) x = 1.0;
      melspec[i] = log(x);
   }
}

/* EXPORT->FBank2C0: return zero'th cepstral coefficient */
float FBank2C0(Vector fbank)
{
   int k,numChan;
   float mfnorm,sum;
   
   numChan = VectorSize(fbank);
   mfnorm = sqrt(2.0/(float)numChan);
   sum = 0.0; 
   for (k=1; k<=numChan; k++)
      sum += fbank[k];
   return sum * mfnorm;
}

/* --------------------- PLP Related Operations -------------------- */

/* EXPORT->InitPLP: Initialise equal-loudness curve & IDT cosine matrix */
void InitPLP (FBankInfo info, int lpcOrder, Vector eql, DMatrix cm)
{
   int i,j;
   double baseAngle;
   float f_hz_mid, fsub, fsq;
   int  nAuto, nFreq;

   /* Create the equal-loudness curve */
   for (i=1; i<=info.numChans; i++) {
      f_hz_mid = 700*(exp(info.cf[i]/1127)-1); /* Mel to Hz conversion */
      fsq = (f_hz_mid * f_hz_mid);
      fsub = fsq / (fsq + 1.6e5);
      eql[i] = fsub * fsub * ((fsq + 1.44e6)  /(fsq + 9.61e6));
   }

   /* Builds up matrix of cosines for IDFT */
   nAuto = lpcOrder+1; 
   nFreq = info.numChans+2;
   baseAngle =  PI / (double)(nFreq - 1);
   for (i=0; i<nAuto; i++) {
      cm[i+1][1] = 1.0;
      for (j=1; j<(nFreq-1); j++)
         cm[i+1][j+1] = 2.0 * cos(baseAngle * (double)i * (double)j);

      cm[i+1][nFreq] = cos(baseAngle * (double)i * (double)(nFreq-1));
   }
}

/* EXPORT->FBank2ASpec: Pre-emphasise filter bank output with the simulated 
           equal-loudness curve and perform amplitude compression */
void FBank2ASpec (Vector fbank, Vector as, Vector eql, float compressFact, 
                  FBankInfo info)
{
   const float melfloor = 1.0;
   int i;

   for (i=1; i<=info.numChans; i++) {
      if (fbank[i] < melfloor) fbank[i] = melfloor;
      as[i+1] = fbank[i] * eql[i]; /* Apply equal-loudness curve */
      as[i+1] = pow((double) as[i+1], (double) compressFact);
   }
   as[1] = as[2];  /* Duplicate values at either end */
   as[info.numChans+2] = as[info.numChans+1];
}


/* Matrix IDFT converts from auditory spectrum into autocorrelation values */
float MatrixIDFT(Vector as, Vector ac, DMatrix cm)
{
   double acc;
   float E;
   int nAuto, nFreq;
   int i, j;

   nFreq = VectorSize(as);
   nAuto = VectorSize(ac);

   for (i=0; i<nAuto; i++) {
      acc = cm[i+1][1] * (double)as[1];
      for (j=1; j<nFreq; j++)
         acc += cm[i+1][j+1] * (double)as[j+1];

      if (i>0) 
         ac[i] = (float)(acc / (double)(2.0 * (nFreq-1)));
      else  
         E = (float)(acc / (double)(2.0 * (nFreq-1)));
   }     
   return E; /* Return zero'th auto value separately */
}

/* EXPORT->ASpec2LPCep: Perform IDFT to get autocorrelation values then 
           produce autoregressive coeffs. and cepstral transform them */
void ASpec2LPCep (Vector as, Vector ac, Vector lp, Vector c, DMatrix cm)
{
   float lpcGain, E;

   /* Do IDFT to get autocorrelation values */
   E = MatrixIDFT(as, ac, cm);
   lp[VectorSize(lp)] = 0.0;    /* init to make Purify et al. happy */
   /* do Durbin recursion to get predictor coefficients */
   lpcGain = Durbin(NULL,lp,ac,E,VectorSize(ac)-1);
   if (lpcGain<=0) 
      HError(-5323,"ASpec2LPCep: Negative lpcgain");
   LPC2Cepstrum(lp,c);
   c[VectorSize(c)] = (float) -log((double) 1.0/lpcGain); /* value forms C0 */
}

/* ------------------- Feature Level Operations -------------------- */

static int cepWinSize=0;            /* Size of current cepstral weight window */
static int cepWinL=0;               /* Current liftering coeff */
static Vector cepWin = NULL;        /* Current cepstral weight window */

/* GenCepWin: generate a new cep liftering vector */
static void GenCepWin (int cepLiftering, int count)
{
   int i;
   float a, Lby2;
   
   if (cepWin==NULL || VectorSize(cepWin) < count)
      cepWin = CreateVector(&sigpHeap,count);
   a = PI/cepLiftering;
   Lby2 = cepLiftering/2.0;
   for (i=1;i<=count;i++)
      cepWin[i] = 1.0 + Lby2*sin(i * a);
   cepWinL = cepLiftering;
   cepWinSize = count;
}  

/* EXPORT->WeightCepstrum: Apply cepstral weighting to c */
void WeightCepstrum (Vector c, int start, int count, int cepLiftering)
{
   int i,j;
   
   if (cepWinL != cepLiftering || count > cepWinSize)
      GenCepWin(cepLiftering,count);
   j = start;
   for (i=1;i<=count;i++)
      c[j++] *= cepWin[i];
}

/* EXPORT->UnWeightCepstrum: Undo cepstral weighting of c */
void UnWeightCepstrum(Vector c, int start, int count, int cepLiftering)
{
   int i,j;
   
   if (cepWinL != cepLiftering || count > cepWinSize)
      GenCepWin(cepLiftering,count);
   j = start;
   for (i=1;i<=count;i++)
      c[j++] /= cepWin[i];
}

/* The following operations apply to a sequence of n vectors step apart.
   They are used to operate on the 'columns' of data files 
   containing a sequence of feature vectors packed together to form a
   continguous block of floats.  The logical size of each vector is
   vSize (<=step) */

/* EXPORT->FZeroMean: Zero mean the given data sequence */
void FZeroMean(float *data, int vSize, int n, int step)
{
   double sum;
   float *fp,mean;
   int i,j;

   for (i=0; i<vSize; i++){
      /* find mean over i'th component */
      sum = 0.0;
      fp = data+i;
      for (j=0;j<n;j++){
         sum += *fp; fp += step;
      }
      mean = sum / (double)n;
      /* subtract mean from i'th components */
      fp = data+i;
      for (j=0;j<n;j++){
         *fp -= mean; fp += step;
      }
   }
}

/* Regression: add regression vector at +offset from source vector.  If head
   or tail is less than delwin then duplicate first/last vector to compensate */
static void Regress(float *data, int vSize, int n, int step, int offset,
                    int delwin, int head, int tail, Boolean simpleDiffs)
{
   float *fp,*fp1,*fp2, *back, *forw;
   float sum, sigmaT2;
   int i,t,j;
   
   sigmaT2 = 0.0;
   for (t=1;t<=delwin;t++)
      sigmaT2 += t*t;
   sigmaT2 *= 2.0;
   fp = data;
   for (i=1;i<=n;i++){
      fp1 = fp; fp2 = fp+offset;
      for (j=1;j<=vSize;j++){
         back = forw = fp1; sum = 0.0;
         for (t=1;t<=delwin;t++) {
            if (head+i-t > 0)     back -= step;
            if (tail+n-i+1-t > 0) forw += step;
            if (!simpleDiffs) sum += t * (*forw - *back);
         }
         if (simpleDiffs)
            *fp2 = (*forw - *back) / (2*delwin);
         else
            *fp2 = sum / sigmaT2;
         ++fp1; ++fp2;
      }
      fp += step;
   }
}


/* EXPORT->AddRegression: add regression vector at +offset from source vector */
void AddRegression(float *data, int vSize, int n, int step, int offset,
                   int delwin, int head, int tail, Boolean simpleDiffs)
{
   Regress(data,vSize,n,step,offset,delwin,head,tail,simpleDiffs);
}

/* EXPORT->AddHeadRegress: add regression at start of data */
void AddHeadRegress(float *data, int vSize, int n, int step, int offset,
                    int delwin, Boolean simpleDiffs)
{
   float *fp,*fp1,*fp2;
   int i,j;

   fp = data;
   if (delwin==0){
      for (i=1;i<=n;i++){
         fp1 = fp; fp2 = fp+offset;
         for (j=1;j<=vSize;j++){
            *fp2 = *(fp1+step) - *fp1;
            ++fp1; ++fp2;
         }
         fp += step;
      }
   }else{
      Regress(data,vSize,n,step,offset,delwin,0,delwin,simpleDiffs);
   }
}

/* EXPORT->AddTailRegress: add regression at end of data */
void AddTailRegress(float *data, int vSize, int n, int step, int offset,
                    int delwin, Boolean simpleDiffs)
{
   float *fp,*fp1,*fp2;
   int i,j;

   fp = data;
   if (delwin==0){
      for (i=1;i<=n;i++){
         fp1 = fp; fp2 = fp+offset;
         for (j=1;j<=vSize;j++){
            *fp2 = *fp1 - *(fp1-step);
            ++fp1; ++fp2;
         }
         fp += step;
      }
   }else{
      Regress(data,vSize,n,step,offset,delwin,delwin,0,simpleDiffs);      
   }
}

/* EXPORT->NormaliseLogEnergy: normalise log energy to range -X .. 1.0 */
void NormaliseLogEnergy(float *data,int n,int step,float silFloor,float escale)
{
   float *p,max,min;
   int i;

   /* find max log energy */
   p = data; max = *p;
   for (i=1;i<n;i++){
      p += step;                   /* step p to next e val */
      if (*p > max) max = *p;
   }
   min = max - (silFloor*log(10.0))/10.0;  /* set the silence floor */
   /* normalise */
   p = data;
   for (i=0;i<n;i++){
      if (*p < min) *p = min;          /* clamp to silence floor */
      *p = 1.0 - (max - *p) * escale;  /* normalise */
      p += step; 
   }
}

/* ------------------- Wavelet Related Operations -------------------- */

/* THIS CREATES THE SUBBAND ENERGIES */
void Wave2Wavelet(Vector s, Vector *fbank, float *te, wlTree wpt,int wde, wlType wty, Boolean useWaveletThreshold, float waveletThreshold, float waveletThresholdAlpha)
{
  const float melfloor = 0.1;
  int i;
  int     depth = wpt.depth;  /* baumtiefe */
  float   *low;               /* filter */
  float   *high;              /* filter */
  int     dimFilter;          /* dimension of the filter coeeficients */
  float   *transformed;       /* ergebnisswerte */
  int     dimTransformed;     /* anzahl aller wavelet koefizienten */
  int     dimSignal = VectorSize(s);
  int     dimDescription;

  char *waveletType;
  switch  (wty) 
    {
    case DAUBECHIES: 
      waveletType="daubechies";
      break;
    case SYMMLET:    
      waveletType="symmlet";
      break;
    case COIFLET: 
      waveletType="coiflet";
      break;
    case BEYLKIN: 
      waveletType="beylkin";
      break;
    case POLLEN: 
      waveletType="pollen";
      break;
    case VAIDYANATHAN:
      waveletType="vaidyanathan";
      break;
    }
  
  segmentDescription *description; /* beschreibung der ergebnisswerte */
  SetFilter(waveletType,wde, &low, &high, &dimFilter);  /* returns -1 if something goes wrong */
  SetVectors(depth, dimFilter, dimSignal, &transformed, &dimTransformed, &description);
/*   printf("WAVELETTYPE:%s ",waveletType); */
/*   printf("WAVELETORDER:%d ",wde); */
/*   printf("DEPTH:%d ",depth); */
/*   printf("DIMFILTER:%d ",dimFilter); */
/*   printf("DIMSIGNAL:%d ",dimSignal); */
  WaveletPacket(s+1,dimSignal,low,high,dimFilter,wpt.tree,depth,transformed,&dimTransformed,description,&dimDescription,useWaveletThreshold,waveletThreshold,waveletThresholdAlpha);
  if ((*fbank)==NULL) 
    (*fbank)  = CreateVector(&gstack,dimDescription);
  else 
    if (VectorSize((*fbank)) != dimDescription)
      {
	/*FreeVector(&gstack,fbank)*/
	(*fbank) = CreateVector(&gstack,dimDescription);
      }
  /* copy the result and logarithmise the filter energys */
  for(i = 1; i <= dimDescription; i++)
  {
     if (description[i - 1].energy > melfloor)
     {
	(*fbank)[i] = log(description[i - 1].energy);
     }
     else
     {
	(*fbank)[i] = log(melfloor);
     }
  }
  free(transformed);
  free(description);
}
/******************************************************************************************************/
void Wavelet2WPP(Vector fbank, Vector *c)
{
  float   *low;
  float   *high;
  int     dimFilter;
  int     depth = 0;
  int     order = 0;
  int     i;
  int     n = VectorSize(fbank);

  /* allocate the vectors */
  if ((*c)==NULL) 
    (*c) = CreateVector(&gstack,n);
  else 
    if (VectorSize((*c)) != n)
      (*c) = CreateVector(&gstack,n);

  if (VectorSize(fbank) == 12) 
    {depth=2;order=4;}
  if (VectorSize(fbank) == 14) 
    {depth=1;order=8;}
  if (VectorSize(fbank) == 16) 
    {depth=3;order=4;}
  if (VectorSize(fbank) == 18) 
    {depth=1;order=10;}
  if (VectorSize(fbank) == 20) 
    {depth=2;order=6;}
  if (VectorSize(fbank) == 22) 
    {depth=1;order=12;}
  if (VectorSize(fbank) == 24) 
    {depth=3;order=4;}
  if (VectorSize(fbank) == 26) 
    {depth=1;order=14;}
  if (VectorSize(fbank) == 28) 
    {depth=2;order=8;}
  if (VectorSize(fbank) == 30) 
    {depth=1;order=16;}
  if (VectorSize(fbank) == 32) 
    {depth=4;order=4;}
  if (VectorSize(fbank) == 36) 
    {depth=2;order=10;}
  if (VectorSize(fbank) == 48) 
    {depth=4;order=4;}
  
  if (depth==0 && order==0)
    { 
      printf("ERROR: WPP features generation at present only configured for input vectors with an size of 24 !!!"); 
      return; 
    } 
  SetFilter("daubechies",order,&low,&high,&dimFilter); 
  /*   for(i = 1; i < 24; i++) printf(" %i \n", fbank[i]);  */
  /*   printf("Size: %i",VectorSize(fbank)); */
  Wavelet(fbank+1,VectorSize(fbank),low,high,dimFilter,depth,(*c)+1);   
}
/******************************************************************************************************/
void Wavelet2SBC(Vector fbank, Vector *c,int n,Boolean useC0, float* cosMatrix)
{
  if (useC0 == TRUE) 
    {
      if (VectorSize(fbank) < n)
	{
	  printf("ERROR (UseC0ForSBC TRUE): Number of cepstral coefficients larger than the number of filter channels.");
	  exit(-1);
	}
    }
  else
    {
      if (VectorSize(fbank) <= n)
	{
	  printf("ERROR (UseC0ForSBC FALSE): Number of cepstral coefficients larger than the number of filter channels.");
	  exit(-1);
	}
    }
  /* allocate the vectors */
  if ((*c)==NULL) 
    (*c) = CreateVector(&gstack,n);
  else 
    if (VectorSize((*c)) != n)
      (*c) = CreateVector(&gstack,n);
  /* decide here to use the first cosine transform coefficient */
  if (useC0 == TRUE)  
    { 
      /* use the first value of the cosine transformation (sum of the logarithmized energy) */
      int i,j,k,numChan;
      float mfnorm,pi_factor,x;
      numChan = VectorSize(fbank);
/*       mfnorm = sqrt(2.0/(float)numChan); */
/*       pi_factor = PI/(float)numChan; */
/*       for (j=0; j<n; j++) */
/* 	{ */
/* 	  (*c)[j+1] = 0.0; x = (float)j * pi_factor; */
/* 	  for (k=1; k<=numChan; k++) */
/* 	    { */
/* 	      (*c)[j+1] += fbank[k] * cos(x*(k-0.5)); */
/* 	    } */
/* 	  (*c)[j+1] *= mfnorm; */
/* 	} */


      for(i = 0; i < n; i++)
      {
	 (*c)[i + 1] = 0.0;
	 for(j = 0; j < numChan; j++)
	 {
	    (*c)[i+1] += fbank[j + 1] * cosMatrix[i * numChan + j];
	 }
      }

	    
	 
    }
  else 
    {
      FBank2MFCC(fbank,(*c),n, cosMatrix); 
    }
}
/******************************************************************************************************/

/**************************************/
/** the wavelet package installation **/
/**************************************/

int SetVectors(int depth,int dimFilter,int dimSignal,float **transformed,int *dimTransformed,segmentDescription **description)
{
   int i;

   (*dimTransformed) = dimSignal;
   for(i = 0; i < depth; i++)
      (*dimTransformed) = ((*dimTransformed) + dimFilter )  >> 1 ;
   (*dimTransformed) = (*dimTransformed)  * (1<<depth);

   (*description) = (segmentDescription*) malloc((1<<depth) * sizeof(segmentDescription));  
   (*transformed) = (float*) malloc((*dimTransformed) * sizeof(float));

   if( (*description) == NULL || (*transformed) == NULL)
   {
      printf(" failed to allocate memory \n");
      return -1;
   }
   return 0;
}

/* calc the high pass filter coefficients */
void permute(float *low, float *high, int dim) 
{
   int i;
   int sign = 1;

   for (i = 0 ; i < dim; i++)
   {
      high[i] = low[dim -1 - i] * sign;
      sign = -sign;
   }
}

int SetFilter(char *type, int order, float **low, float **high, int *dimFilter)
{
   permute(d2l,d2h,2);
   permute(d4l,d4h,4);
   permute(d6l,d6h,6);
   permute(d8l,d8h,8);
   permute(d10l,d10h,10);
   permute(d12l,d12h,12);
   permute(d14l,d14h,14);
   permute(d16l,d16h,16);
   permute(d18l,d18h,18);
   permute(d20l,d20h,20);
   permute(d22l,d22h,22);
   permute(d24l,d24h,24);
   permute(d26l,d26h,26);
   permute(d28l,d28h,28);
   permute(d30l,d30h,30);
   permute(d32l,d32h,32);
   permute(d34l,d34h,34);
   permute(d36l,d36h,36);
   permute(d38l,d38h,38);
   permute(d30l,d30h,30);
   permute(d40l,d40h,40);
   permute(d42l,d42h,42);
   permute(d44l,d44h,44);
   permute(d46l,d46h,46);
   permute(d48l,d48h,48);
   permute(d50l,d50h,50);
   permute(d52l,d52h,52);
   permute(d54l,d54h,54);
   permute(d56l,d56h,56);
   permute(d58l,d58h,58);
   permute(d60l,d60h,60);
   permute(d62l,d62h,62);
   permute(d64l,d64h,64);
   permute(d66l,d66h,66);
   permute(d68l,d68h,68);
   permute(d70l,d70h,70);
   permute(d72l,d72h,72);
   permute(d74l,d74h,74);
   permute(d76l,d76h,76);
   permute(d78l,d78h,78);
   permute(d80l,d80h,80);
   permute(d82l,d82h,82);
   permute(d84l,d84h,84);
   permute(d86l,d86h,86);
   permute(d88l,d88h,88);
   permute(d90l,d90h,90);
   permute(d92l,d92h,92);
   permute(d94l,d94h,94);
   permute(d96l,d96h,96);
   permute(d98l,d98h,98);
   permute(d100l,d100h,100);
   permute(d102l,d102h,102);
   permute(d508l,d508h,508);

   permute(s8l,s8h,8);
   permute(s10l,s10h,10);
   permute(s12l,s12h,12);
   permute(s14l,s14h,14);
   permute(s16l,s16h,16);
   permute(s18l,s18h,18);
   permute(s20l,s20h,20);
   permute(s22l,s22h,22);
   permute(s24l,s24h,24);
   permute(s26l,s26h,26);
   permute(s28l,s28h,28);
   permute(s30l,s30h,30);
   permute(s102l,s102h,102);

   permute(c6l,c6h,6);
   permute(c12l,c12h,12);
   permute(c18l,c18h,18);
   permute(c24l,c24h,24);
   permute(c30l,c30h,30);

   permute(b18l,b18h,18);

   permute(p4l,p4h,4);

   permute(v24l,v24h,24);

   if ( strncmp(type, "daubechies", 11) == 0)
   {
      switch (order)
      {
	 case 2:(*low)=d2l;(*high)=d2h;(*dimFilter)=2;break;
	 case 4:(*low)=d4l;(*high)=d4h;(*dimFilter)=4;break;
	 case 6:(*low)=d6l;(*high)=d6h;(*dimFilter)=6;break;
	 case 8:(*low)=d8l;(*high)=d8h;(*dimFilter)=8;break;
	 case 10:(*low)=d10l;(*high)=d10h;(*dimFilter)=10;break;
	 case 12:(*low)=d12l;(*high)=d12h;(*dimFilter)=12;break;
	 case 14:(*low)=d14l;(*high)=d14h;(*dimFilter)=14;break;
	 case 16:(*low)=d16l;(*high)=d16h;(*dimFilter)=16;break;
	 case 18:(*low)=d18l;(*high)=d18h;(*dimFilter)=18;break;
	 case 20:(*low)=d20l;(*high)=d20h;(*dimFilter)=20;break;
	 case 22:(*low)=d22l;(*high)=d22h;(*dimFilter)=22;break;
	 case 24:(*low)=d24l;(*high)=d24h;(*dimFilter)=24;break;
	 case 26:(*low)=d26l;(*high)=d26h;(*dimFilter)=26;break;
	 case 28:(*low)=d28l;(*high)=d28h;(*dimFilter)=28;break;
	 case 30:(*low)=d30l;(*high)=d30h;(*dimFilter)=30;break;
	 case 32:(*low)=d32l;(*high)=d32h;(*dimFilter)=32;break;
	 case 34:(*low)=d34l;(*high)=d34h;(*dimFilter)=34;break;
	 case 36:(*low)=d36l;(*high)=d36h;(*dimFilter)=36;break;
	 case 38:(*low)=d38l;(*high)=d38h;(*dimFilter)=38;break;
	 case 40:(*low)=d40l;(*high)=d40h;(*dimFilter)=40;break;
	 case 42:(*low)=d42l;(*high)=d42h;(*dimFilter)=42;break;
	 case 44:(*low)=d44l;(*high)=d44h;(*dimFilter)=44;break;
	 case 46:(*low)=d46l;(*high)=d46h;(*dimFilter)=46;break;
	 case 48:(*low)=d48l;(*high)=d48h;(*dimFilter)=48;break;
	 case 50:(*low)=d50l;(*high)=d50h;(*dimFilter)=50;break;
	 case 52:(*low)=d52l;(*high)=d52h;(*dimFilter)=52;break;
	 case 54:(*low)=d54l;(*high)=d54h;(*dimFilter)=54;break;
	 case 56:(*low)=d56l;(*high)=d56h;(*dimFilter)=56;break;
	 case 58:(*low)=d58l;(*high)=d58h;(*dimFilter)=58;break;
	 case 60:(*low)=d60l;(*high)=d60h;(*dimFilter)=60;break;
	 case 62:(*low)=d62l;(*high)=d62h;(*dimFilter)=62;break;
	 case 64:(*low)=d64l;(*high)=d64h;(*dimFilter)=64;break;
	 case 66:(*low)=d66l;(*high)=d66h;(*dimFilter)=66;break;
	 case 68:(*low)=d68l;(*high)=d68h;(*dimFilter)=68;break;
	 case 70:(*low)=d70l;(*high)=d70h;(*dimFilter)=70;break;
	 case 72:(*low)=d72l;(*high)=d72h;(*dimFilter)=72;break;
	 case 74:(*low)=d74l;(*high)=d74h;(*dimFilter)=74;break;
	 case 76:(*low)=d76l;(*high)=d76h;(*dimFilter)=76;break;
	 case 78:(*low)=d78l;(*high)=d78h;(*dimFilter)=78;break;
	 case 80:(*low)=d80l;(*high)=d80h;(*dimFilter)=80;break;
	 case 82:(*low)=d82l;(*high)=d82h;(*dimFilter)=82;break;
	 case 84:(*low)=d84l;(*high)=d84h;(*dimFilter)=84;break;
	 case 86:(*low)=d86l;(*high)=d86h;(*dimFilter)=86;break;
	 case 88:(*low)=d88l;(*high)=d88h;(*dimFilter)=88;break;
	 case 90:(*low)=d90l;(*high)=d90h;(*dimFilter)=90;break;
	 case 92:(*low)=d92l;(*high)=d92h;(*dimFilter)=92;break;
	 case 94:(*low)=d94l;(*high)=d94h;(*dimFilter)=94;break;
	 case 96:(*low)=d96l;(*high)=d96h;(*dimFilter)=96;break;
	 case 98:(*low)=d98l;(*high)=d98h;(*dimFilter)=98;break;
	 case 100:(*low)=d100l;(*high)=d100h;(*dimFilter)=100;break;
	 case 102:(*low)=d102l;(*high)=d102h;(*dimFilter)=102;break;
	 case 508:(*low)=d508l;(*high)=d508h;(*dimFilter)=508;break;
         default:
	    printf("HSIGP.c: Order %i for type %s not implemented \n", order, type);
	    return -1;
      }
   }
   else if ( strncmp(type, "symmlet", 8) == 0)
   {
      switch(order)
      {
	 case 2:(*low)=d2l;(*high)=d2h;(*dimFilter)=2;break;
	 case 4:(*low)=d4l;(*high)=d4h;(*dimFilter)=4;break;
	 case 6:(*low)=d6l;(*high)=d6h;(*dimFilter)=6;break;
	 case 8:(*low)=s8l;(*high)=s8h;(*dimFilter)=8;break;
	 case 10:(*low)=s10l;(*high)=s10h;(*dimFilter)=10;break;
	 case 12:(*low)=s12l;(*high)=s12h;(*dimFilter)=12;break;
	 case 14:(*low)=s14l;(*high)=s14h;(*dimFilter)=14;break;
	 case 16:(*low)=s16l;(*high)=s16h;(*dimFilter)=16;break;
	 case 18:(*low)=s18l;(*high)=s18h;(*dimFilter)=18;break;
	 case 20:(*low)=s20l;(*high)=s20h;(*dimFilter)=20;break;
	 case 22:(*low)=s22l;(*high)=s22h;(*dimFilter)=22;break;
	 case 24:(*low)=s24l;(*high)=s24h;(*dimFilter)=24;break;
	 case 26:(*low)=s26l;(*high)=s26h;(*dimFilter)=26;break;
	 case 28:(*low)=s28l;(*high)=s28h;(*dimFilter)=28;break;
	 case 30:(*low)=s30l;(*high)=s30h;(*dimFilter)=30;break;
	 case 102:(*low)=s102l;(*high)=s102h;(*dimFilter)=102;break;
	 default:
	    printf("order %i for type %s not implemented \n", order, type);
	    return -1;
      }
   }
   else if ( strncmp(type, "coiflet", 8) == 0)
   {
      switch(order)
      {
	 case 6:(*low)=d6l;(*high)=d6h;(*dimFilter)=6;break;
	 case 12:(*low)=c12l;(*high)=c12h;(*dimFilter)=12;break;
	 case 18:(*low)=c18l;(*high)=c18h;(*dimFilter)=18;break;
	 case 24:(*low)=c24l;(*high)=c24h;(*dimFilter)=24;break;
	 case 30:(*low)=c30l;(*high)=c30h;(*dimFilter)=30;break;
	 default:
	    printf("order %i for type %s not implemented \n", order, type);
	    return -1;
      }
   }
   else if ( strncmp(type, "beylkin", 8) == 0)
   {
      switch(order)
      {
	 case 18:(*low)=b18l;(*high)=b18h;(*dimFilter)=18;break;
	 default:
	    printf("order %i for type %s not implemented \n", order, type);
	    return -1;
      }
   }
   else if ( strncmp(type, "pollen", 7) == 0)
   {
      switch(order)
      {
	 case 4:(*low)=p4l;(*high)=p4h;(*dimFilter)=4;break;
	 default:
	    printf("order %i for type %s not implemented \n", order, type);
	    return -1;
      }
   }
   else if ( strncmp(type, "vaidyanathan", 7) == 0)
     {
       switch(order)
	 {
	 case 24:(*low)=v24l;(*high)=v24h;(*dimFilter)=4;break;
	 default:
	   printf("order %i for type %s not implemented \n", order, type);
	   return -1;
      }
     }
   else
     {
       printf("filter type %s unknown \n", type);
       return -1;
     }
   return 0;
}


void PrintDescription( segmentDescription *description, int dimDescription, int depth)
{
   int i,j;

   for (i = 0; i < dimDescription; i++)
   {	
      printf("dim =  %2i    ",      description[i].dim);
      printf("energy =  %8.5f    ", description[i].energy);
      printf("offset =  %i    ",    description[i].offset);
      printf("path =  ");
      for (j = depth - 1; j >= depth - description[i].level ; j--)
      {
	 if((1<<j) & description[i].path) printf("r");
	 else printf("l");
      }
      printf("\n");
   }
}

/* void Convolution(const float *signal, int dimSignal, const float *filter, int dimFilter, */
/*                  float *output) */
/* { */
/*    int i, j; */

/*    for ( i = 0 ; i < dimFilter - 1; i+=2) */
/*    { */
/*       output[i >> 1] = 0; */
/*       for (j = 0; j <=  i; j++) */
/*       { */
/*   	 output[i >> 1] += signal[i - j] * filter[j];   */
/*       } 	 */
/*    } */

/*    for( ; i < dimSignal; i+=2) */
/*    { */
/*       output[i >> 1] = 0; */
/*       for( j = 0; j < dimFilter; j++) */
/*       { */
/*  	 output[i >> 1] += signal[i -j] * filter[j]; 	 */
/*       } */
/*    } */

/*    for( ; i < (dimSignal + dimFilter - 1); i+=2) */
/*    {  */
/*       output[i >> 1] = 0; */
/*       for(j = (i - dimSignal +1) ; j < dimFilter; j++) */
/*       { */
/* 	 output[i >> 1] += signal[i-j] * filter[j];  */
/*       }	 */
/*    }  */
/* } */

void   Convolution(const float *signal, int dimSignal, int offsetSignal,
		   const float *filter, int dimFilter, int offsetFilter,
		         float *output, int *dimOutput,int  *offsetOutput)
{
   int i, j, position;
   
   position = offsetSignal - (dimFilter + offsetFilter);
 
   if(position%2 == 0)
   {
      (*offsetOutput) = (position + 2) / 2;
      (*dimOutput)    = (dimFilter + dimSignal-1) >> 1;

      
      for ( i = 1 ; i < dimFilter - 1; i+=2)
      {
	 output[i >> 1] = 0;
	 for (j = 0 ; j <= i  ; j++)
	 {
	    output[i >> 1] += signal[j] * filter[dimFilter - 1  - i + j]; 
	 } 	
      }

      for( ; i < dimSignal; i+=2)
      {
	 output[i >> 1] = 0;
	 for( j = 0; j < dimFilter; j++)
	 {
	    output[i >> 1] += signal[i -j] * filter[dimFilter - 1 - j]; 	
	 }
      }

      for( ; i < (dimSignal + dimFilter - 1); i+=2)
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
      (*offsetOutput) = (position + 1) / 2;
      (*dimOutput)    = (dimFilter + dimSignal) >> 1;
      
      
      for ( i = 0 ; i < dimFilter - 1; i+=2)
      {
	 output[i >> 1] = 0;
	 for (j = 0 ; j <= i  ; j++)
	 {
	    output[i >> 1] += signal[j] * filter[dimFilter - 1  - i + j]; 
	 } 	
      }

      for( ; i < dimSignal; i+=2)
      {
	 output[i >> 1] = 0;
	 for( j = 0; j < dimFilter; j++)
	 {
	    output[i >> 1] += signal[i -j] * filter[dimFilter - 1 - j]; 	
	 }
      }

      for( ; i < (dimSignal + dimFilter - 1); i+=2)
      { 
	 output[i >> 1] = 0;
	 for(j = (i - dimSignal +1) ; j < dimFilter; j++)
	 {
	    output[i >> 1] += signal[i-j] * filter[dimFilter - 1 - j];
	 }	
      }
      
   } 
}



float ComputeEnergy(float *Signal, int dimSignal,Boolean useWaveletThreshold, float waveletThreshold, float waveletThresholdAlpha)
{
   /* this  is the version wiht thresholding*/
  float alpha;
  float threshold;
  if (useWaveletThreshold == TRUE) {
    alpha    = waveletThresholdAlpha;
    threshold = waveletThreshold;
  }
  else {
    alpha = 1.0f;
    threshold = 1.0f;
  }
  float temp;
  int i;
  float energy = 0;
  
  /* for (i = 0; i < dimSignal; i++)
     energy += Signal[i] * Signal[i];*/
  for (i = 0; i < dimSignal; i++)
    {
      if(fabs(Signal[i]) > threshold)
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
/*
  Checks the tree for correctness and returns 0 if anything gore right, otherwise -1
  PARAM: const int *tree   --  the wavelet packet tree
  PARAM: int depth         --  the deepth of the three
  PARAM: int* numChannels  --  (call by reference) number of output coefficients
  RETURN: int 0 or -1(false)
 */
int CheckTree(const int *tree, int depth, int* numChannels)
{
	int i, j;
	int parts = 1;

	(*numChannels) = 0;

	for ( i = 0; i < depth - 1; i++)
	{
		for( j = 0; j < parts; j++)
		{
			if (tree[(1 << i) + j -1] == 0)
			{
				if (tree[(2<<i) + (j<<1) - 1] == 1 ||
				    tree[(2<<i) + (j<<1)]     == 1   )
				{
					printf("CheckTree (Wavelet.c): not a correct wpp tree definition \n");
					return -1;
				}
			}
			else
			{
			  if (tree[(2<<i) + (j<<1) - 1] == 0) (*numChannels)++; 
			  if (tree[(2<<i) + (j<<1)]     == 0) (*numChannels)++;
			}
		}
		parts =  parts<<1;
	}

	/* i = depth - 1 */
	for (j = 0; j < parts; j++)
	{
	  if(tree[(1 << i) + j -1] == 1) (*numChannels) += 2;
	}  

	return 0;
}
    
void WaveletPacket( float *signal , int dimSignal, const float *lowPass,
		    const float *highPass, int dimFilter, const int  *tree,  int depth,
		    float *transformed, int *dimTransformed, segmentDescription *description, int *dimDescription,
		    Boolean useWaveletThreshold, float waveletThreshold, float waveletThresholdAlpha
		    )
{
  
   int   i, j; 
   int   parts = 1;
   int   dimParts;
   float *temp;
   segmentDescription  segment;
   int   changeFlag;
   int   address = 0;
   int   dimOutput;
   int   offsetFilter = 0;
   int   offsetSignal = 0;
   int   offsetOutput;


   temp        = (float*) malloc((*dimTransformed) * sizeof(float));

   dimParts    = (*dimTransformed);
   (*dimDescription)  = 0;
   memcpy(transformed, signal, dimSignal * sizeof(float));

  for (i = 0; i < (depth - 1); i++)
  {
    for (j = parts - 1; j >= 0; j--)
    {
      if (tree[(1 << i) + j -1] == 1)
      {
        Convolution(transformed + j * dimParts, dimSignal, offsetSignal,
        highPass, dimFilter, offsetFilter,
        temp +  j * dimParts +  (dimParts >> 1), &dimOutput, &offsetOutput );	
        Convolution(transformed + j * dimParts, dimSignal, offsetSignal,
        lowPass, dimFilter, offsetFilter,
        temp +  j * dimParts, &dimOutput, &offsetOutput);
          
        if (tree[(2<<i) + (j<<1)] == 0)
        {
           description[(*dimDescription)].offset = offsetOutput;
           description[(*dimDescription)].dim    = dimOutput;
           description[(*dimDescription)].energy = ComputeEnergy(temp +  j * dimParts +  (dimParts >> 1),
          dimOutput,useWaveletThreshold,waveletThreshold,waveletThresholdAlpha);  
           description[(*dimDescription)].path   = (1<<(depth  - i)) * j + (1<<(depth - i - 1));
           description[(*dimDescription)].level  = i + 1;

           (*dimDescription)++;
        }
          
        if (tree[(2<<i) + (j<<1) - 1] == 0)
        {
           description[(*dimDescription)].offset = offsetOutput;
           description[(*dimDescription)].dim    = dimOutput;
           description[(*dimDescription)].energy = ComputeEnergy(temp +  j * dimParts,
           dimOutput,useWaveletThreshold,waveletThreshold,waveletThresholdAlpha);
           description[(*dimDescription)].path   = (1<<(depth - i)) * j; 
           description[(*dimDescription)].level  = i + 1;

           (*dimDescription)++;
        }
      }
    } 
    memcpy(transformed, temp, (*dimTransformed) * sizeof(float)); 
    dimSignal    = dimOutput;
    offsetSignal = offsetOutput;
    parts        = parts << 1;
    dimParts     = dimParts >> 1;
   }
	
	
   i = depth -1; 
   for (j = parts - 1; j >= 0; j--)
   {
      if (tree[(1 << i) + j -1] == 1)
      {
        Convolution(transformed + j * dimParts, dimSignal, offsetSignal,
             highPass, dimFilter, offsetFilter,
             temp +  j * dimParts +  (dimParts >> 1), &dimOutput, &offsetOutput);	
        Convolution(transformed + j * dimParts, dimSignal, offsetSignal,
             lowPass, dimFilter, offsetFilter,
             temp +  j * dimParts, &dimOutput, &offsetOutput);	

        description[(*dimDescription)].offset = offsetOutput;
        description[(*dimDescription)].dim    = dimOutput;
        description[(*dimDescription)].energy = ComputeEnergy(temp +  j * dimParts +  (dimParts >> 1),
                         dimOutput,useWaveletThreshold,waveletThreshold,waveletThresholdAlpha);  
        description[(*dimDescription)].path   = (j<<1) + 1 ;
        description[(*dimDescription)].level  = i + 1;
          
        (*dimDescription)++;
        description[(*dimDescription)].offset = offsetOutput;
        description[(*dimDescription)].dim    = dimOutput;
        description[(*dimDescription)].energy = ComputeEnergy(temp +  j * dimParts,
                         dimOutput,useWaveletThreshold,waveletThreshold,waveletThresholdAlpha);
        description[(*dimDescription)].path   =  (j<<1); 
        description[(*dimDescription)].level  = i + 1;
        (*dimDescription)++;
      }
   } 


   parts        = parts << 1;
   dimParts     = dimParts >> 1;
   
   	
   for (i = 0; i < (*dimDescription) - 1; i++)
   {
      changeFlag = 0;
				
      for (j = 0; j < (*dimDescription) - 1; j++)
      {
        if (description[j+1].path > description[j].path)
        {
          changeFlag     = 1;
            
          segment = description[j];
          description[j] = description[j+1];
          description[j+1] = segment;	
        }
      }

      if (changeFlag == 0) break;
   }

   for (i = 0; i < (*dimDescription); i++)
   {
    memcpy(transformed + address, temp + (description[i].path * dimParts), description[i].dim * sizeof(float));
    address += description[i].dim;
   }

   (*dimTransformed) = address;

	
   free(temp);
}


/**********************************************************************************************************************************************/
int Wavelet(float* signal, int dimSignal, const float* lowPass, const float* highPass, int dimFilter, int depth, float* transformed )
{
   int i,j,k;
  
   if(((dimSignal>>depth)<<depth)  != dimSignal )
   {
      printf("circular wavelet transformation of depth %i with signal length %i not possible\n", depth, dimSignal);
      return -1;
   }
   
   if((dimSignal>>(depth - 1)) < dimFilter)
   {
      printf("signal length(%i) to short for circular wavelet transformation of depth %i with filter length %i\n", dimSignal, depth, dimFilter);  
   }


   for (i = 0; i < depth; i++)
   {

          /*lowPass
          for(j = 0; j < dimFilter; j += 2)
          {
    	 transformed[j>>1] = lowPass[0] * signal[j];
    	 for (k = 1; k <=j; k++)
    	 {
    	    transformed[j>>1] += lowPass[k] * signal[j - k];
    	 }
    	 for (k = j + 1; k < dimFilter; k++)
    	 {
    	    transformed[j>>1] += lowPass[k] * signal[j - k + dimSignal];
    	 }
          }
          for(; j < dimSignal; j += 2)
          {
    	 transformed[j>>1] = lowPass[0] * signal[j];
    	 for (k = 1; k < dimFilter; k++)
    	 {
    	    transformed[j>>1] += lowPass[k] * signal[j - k];
    	 }
          }
          */

      for(j = 0; j < dimSignal; j+=2)
      {
        transformed[j>>1] = 0;
        for(k = 0; k < dimFilter; k++)
        {
          transformed[j>>1] += signal[(j + k) % dimSignal] * lowPass[k];
        }
      }


          /*highPass
          for(j = 0; j < dimFilter; j += 2)
          {
    	 transformed[(j>>1) + (dimSignal>>1)] = highPass[0] * signal[j];
    	 for (k = 1; k <=j; k++)
    	 {
    	    transformed[(j>>1) + (dimSignal>>1)] += highPass[k] * signal[j - k];
    	 }
    	 for (k = j + 1; k < dimFilter; k++)
    	 {
    	    transformed[(j>>1) + (dimSignal>>1)] += highPass[k] * signal[j - k + dimSignal];
    	 }
          }
          for(; j < dimSignal; j += 2)
          {
    	 transformed[(j>>1) + (dimSignal>>1)] = highPass[0] * signal[j];
    	 for (k = 1; k < dimFilter; k++)
    	 {
    	    transformed[(j>>1) + (dimSignal>>1)] += highPass[k] * signal[j - k];
    	 }
          }
          */

      for(j = 0; j < dimSignal; j+=2)
      {
        transformed[(j>>1) + (dimSignal>>1) ] = 0;
        for(k = 0; k < dimFilter; k++)
        {
          transformed[(j>>1) + (dimSignal>>1)] += signal[(j + k) % dimSignal] * highPass[k];
        }
      }
      dimSignal = dimSignal>>1;
      memcpy(signal, transformed, dimSignal * sizeof(float));
   }
   return 0;
} 
 
