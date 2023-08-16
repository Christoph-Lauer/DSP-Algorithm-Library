/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    wavelet_package_decomposition.cpp
 * @class   WaveletpackageDecomposition
 * @version 0.5
 * @date    2014
 * @author  Christoph Lauer
 * @brief   C-Plus-Plus implemenation file
 * @see     http://en.wikipedia.org/wiki/Wavelet_packet_decomposition
 * @todo    Finished so far but not sure if the transformation works right... An evaluation should be done...
 */
 
//-------------------------------------------------------------------------------------------------

// Local headers
#include"wavelet_packet_decomposition.h"

// Standart C Library headers
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <cstring>
 
//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
 
//-------------------------------------------------------------------------------------------------

// initialize the static class members here
double WaveletPacketDecomposition::sampleRate = 2.0;
 
//-------------------------------------------------------------------------------------------------

int WaveletPacketDecomposition::SetVectors(int depth, int dimFilter, int dimSignal, double **transformed, int *dimTransformed, segmentDescription **description)
{
   int i;

   (*dimTransformed) = dimSignal;
   for(i=0; i<depth; i++)
      (*dimTransformed) = ((*dimTransformed)+dimFilter ) >> 1;
   (*dimTransformed) = (*dimTransformed)  * (1<<depth);
   (*description) = (segmentDescription*) malloc((1<<depth) * sizeof(segmentDescription));  
   (*transformed) = (double*) malloc((*dimTransformed) * sizeof(double));
   if( (*description) == NULL || (*transformed) == NULL)
   {
      printf("Failed to allocate the memory \n");
      return -1;
   }

   return 0;
}

//-------------------------------------------------------------------------------------------------

// calculates the high pass filter coefficients
void WaveletPacketDecomposition::permute(double* low, double* high, int dim)
{
   int i, sign = 1;

   for (i=0; i<dim; i++)
   {
      high[i] = low[dim-1-i] * sign;
      sign = -sign;
   }
}

//-------------------------------------------------------------------------------------------------

int WaveletPacketDecomposition::SetFilter(char *type, int order, double** low, double** high, int* dimFilter)
{
  // DAUBECHIES
  if ( strncmp(type, "daubechies", 11) == 0)
  {
    switch (order)
    {
      case 2:  permute(d2l,d2h,2);(*low)=d2l;(*high)=d2h;(*dimFilter)=2;break;
      case 4:  permute(d4l,d4h,4);(*low)=d4l;(*high)=d4h;(*dimFilter)=4;break;
      case 6:  permute(d6l,d6h,6);(*low)=d6l;(*high)=d6h;(*dimFilter)=6;break;
      case 8:  permute(d8l,d8h,8);(*low)=d8l;(*high)=d8h;(*dimFilter)=8;break;
      case 10: permute(d10l,d10h,10);(*low)=d10l;(*high)=d10h;(*dimFilter)=10;break;
      case 12: permute(d12l,d12h,12);(*low)=d12l;(*high)=d12h;(*dimFilter)=12;break;
      case 14: permute(d14l,d14h,14);(*low)=d14l;(*high)=d14h;(*dimFilter)=14;break;
      case 16: permute(d16l,d16h,16);(*low)=d16l;(*high)=d16h;(*dimFilter)=16;break;
      case 18: permute(d18l,d18h,18);(*low)=d18l;(*high)=d18h;(*dimFilter)=18;break;
      case 20: permute(d20l,d20h,20);(*low)=d20l;(*high)=d20h;(*dimFilter)=20;break;
      case 22: permute(d22l,d22h,22);(*low)=d22l;(*high)=d22h;(*dimFilter)=22;break;
      case 24: permute(d24l,d24h,24);(*low)=d24l;(*high)=d24h;(*dimFilter)=24;break;
      case 26: permute(d26l,d26h,26);(*low)=d26l;(*high)=d26h;(*dimFilter)=26;break;
      case 28: permute(d28l,d28h,28);(*low)=d28l;(*high)=d28h;(*dimFilter)=28;break;
      case 30: permute(d30l,d30h,30);(*low)=d30l;(*high)=d30h;(*dimFilter)=30;break;
      case 32: permute(d32l,d32h,32);(*low)=d32l;(*high)=d32h;(*dimFilter)=32;break;
      case 34: permute(d34l,d34h,34);(*low)=d34l;(*high)=d34h;(*dimFilter)=34;break;
      case 36: permute(d36l,d36h,36);(*low)=d36l;(*high)=d36h;(*dimFilter)=36;break;
      case 38: permute(d38l,d38h,38);(*low)=d38l;(*high)=d38h;(*dimFilter)=38;break;
      case 40: permute(d40l,d40h,40);(*low)=d40l;(*high)=d40h;(*dimFilter)=40;break;
      case 42: permute(d40l,d42h,42);(*low)=d42l;(*high)=d42h;(*dimFilter)=42;break;
      case 44: permute(d44l,d44h,44);(*low)=d44l;(*high)=d44h;(*dimFilter)=44;break;
      case 46: permute(d46l,d46h,46);(*low)=d46l;(*high)=d46h;(*dimFilter)=46;break;
      case 48: permute(d48l,d48h,48);(*low)=d48l;(*high)=d48h;(*dimFilter)=48;break;
      case 50: permute(d50l,d50h,50);(*low)=d50l;(*high)=d50h;(*dimFilter)=50;break;
      case 52: permute(d52l,d52h,52);(*low)=d52l;(*high)=d52h;(*dimFilter)=52;break;
      case 54: permute(d54l,d54h,54);(*low)=d54l;(*high)=d54h;(*dimFilter)=54;break;
      case 56: permute(d56l,d56h,56);(*low)=d56l;(*high)=d56h;(*dimFilter)=56;break;
      case 58: permute(d58l,d58h,58);(*low)=d58l;(*high)=d58h;(*dimFilter)=58;break;
      case 60: permute(d60l,d60h,60);(*low)=d60l;(*high)=d60h;(*dimFilter)=60;break;
      case 62: permute(d62l,d62h,62);(*low)=d62l;(*high)=d62h;(*dimFilter)=62;break;
      case 64: permute(d64l,d64h,64);(*low)=d64l;(*high)=d64h;(*dimFilter)=64;break;
      case 66: permute(d66l,d66h,66);(*low)=d66l;(*high)=d66h;(*dimFilter)=66;break;
      case 68: permute(d68l,d68h,68);(*low)=d68l;(*high)=d68h;(*dimFilter)=68;break;
      case 70: permute(d70l,d70h,70);(*low)=d70l;(*high)=d70h;(*dimFilter)=70;break;
      case 72: permute(d72l,d72h,72);(*low)=d72l;(*high)=d72h;(*dimFilter)=72;break;
      case 74: permute(d74l,d74h,74);(*low)=d74l;(*high)=d74h;(*dimFilter)=74;break;
      case 76: permute(d76l,d76h,76);(*low)=d76l;(*high)=d76h;(*dimFilter)=76;break;
      case 78: permute(d78l,d78h,78);(*low)=d78l;(*high)=d78h;(*dimFilter)=78;break;
      case 80: permute(d80l,d80h,80);(*low)=d80l;(*high)=d80h;(*dimFilter)=80;break;
      case 82: permute(d82l,d82h,82);(*low)=d82l;(*high)=d82h;(*dimFilter)=82;break;
      case 84: permute(d84l,d84h,84);(*low)=d84l;(*high)=d84h;(*dimFilter)=84;break;
      case 86: permute(d86l,d86h,86);(*low)=d86l;(*high)=d86h;(*dimFilter)=86;break;
      case 88: permute(d88l,d88h,88);(*low)=d88l;(*high)=d88h;(*dimFilter)=88;break;
      case 90: permute(d90l,d90h,90);(*low)=d90l;(*high)=d90h;(*dimFilter)=90;break;
      case 92: permute(d92l,d92h,92);(*low)=d92l;(*high)=d92h;(*dimFilter)=92;break;
      case 94: permute(d94l,d94h,94);(*low)=d94l;(*high)=d94h;(*dimFilter)=94;break;
      case 96: permute(d96l,d96h,96);(*low)=d96l;(*high)=d96h;(*dimFilter)=96;break;
      case 98: permute(d98l,d98h,98);(*low)=d98l;(*high)=d98h;(*dimFilter)=98;break;
      case 100:permute(d100l,d100h,100);(*low)=d100l;(*high)=d100h;(*dimFilter)=100;break;
      case 102:permute(d102l,d102h,102);(*low)=d102l;(*high)=d102h;(*dimFilter)=102;break;
      default:
        printf("order %i for type %s not implemented \n", order, type);
        return -1;
    }
  }
  // SYMLETS
  else if ( strncmp(type, "symmlet", 8) == 0)
  {
    switch(order)
    {
      case 2:  permute(d2l,d2h,2);(*low)=d2l;(*high)=d2h;(*dimFilter)=2;break;
      case 4:  permute(d4l,d4h,4);(*low)=d4l;(*high)=d4h;(*dimFilter)=4;break;
      case 6:  permute(d6l,d6h,6);(*low)=d6l;(*high)=d6h;(*dimFilter)=6;break;
      case 8:  permute(s8l,s8h,8);(*low)=s8l;(*high)=s8h;(*dimFilter)=8;break;
      case 10: permute(s10l,s10h,10);(*low)=s10l;(*high)=s10h;(*dimFilter)=10;break;
      case 12: permute(s12l,s12h,12);(*low)=s12l;(*high)=s12h;(*dimFilter)=12;break;
      case 14: permute(s14l,s14h,14);(*low)=s14l;(*high)=s14h;(*dimFilter)=14;break;
      case 16: permute(s16l,s16h,16);(*low)=s16l;(*high)=s16h;(*dimFilter)=16;break;
      case 18: permute(s18l,s18h,18);(*low)=s18l;(*high)=s18h;(*dimFilter)=18;break;
      case 20: permute(s20l,s20h,20);(*low)=s20l;(*high)=s20h;(*dimFilter)=20;break;
      case 22: permute(s22l,s22h,22);(*low)=s22l;(*high)=s22h;(*dimFilter)=22;break;
      case 24: permute(s24l,s24h,24);(*low)=s24l;(*high)=s24h;(*dimFilter)=24;break;
      case 26: permute(s26l,s26h,26);(*low)=s26l;(*high)=s26h;(*dimFilter)=26;break;
      case 28: permute(s28l,s28h,28);(*low)=s28l;(*high)=s28h;(*dimFilter)=28;break;
      case 30: permute(s30l,s30h,30);(*low)=s30l;(*high)=s30h;(*dimFilter)=30;break;
      default:
        printf("order %i for type %s not implemented \n", order, type);
        return -1;
    }
  }
  // COIFLETS
  else if ( strncmp(type, "coiflet", 8) == 0)
  {
    switch(order)
    {
      case 6:  permute(c6l,c6h,6);(*low)=d6l;(*high)=d6h;(*dimFilter)=6;break;
      case 12: permute(c12l,c12h,12);(*low)=c12l;(*high)=c12h;(*dimFilter)=12;break;
      case 18: permute(c18l,c18h,18);(*low)=c18l;(*high)=c18h;(*dimFilter)=18;break;
      case 24: permute(c24l,c24h,24);(*low)=c24l;(*high)=c24h;(*dimFilter)=24;break;
      case 30: permute(c30l,c30h,30);(*low)=c30l;(*high)=c30h;(*dimFilter)=30;break;
      default:
        printf("order %i for type %s not implemented \n", order, type);
        return -1;
    }
  }
  // BEYLKIN
  else if ( strncmp(type, "beylkin", 8) == 0)
  {
    switch(order)
    {
      case 18: permute(b18l,b18h,18);(*low)=b18l;(*high)=b18h;(*dimFilter)=18;break;
      default:
        printf("order %i for type %s not implemented \n", order, type);
        return -1;
    }
  }
  // POLLEN
  else if ( strncmp(type, "pollen", 7) == 0)
  {
    switch(order)
    {
      case 4: permute(p4l,p4h,4);(*low)=p4l;(*high)=p4h;(*dimFilter)=4;break;
      default:
        printf("order %i for type %s not implemented \n", order, type);
        return -1;
    }
  }
  // VAIDYANATHAN
  else if ( strncmp(type, "vaidyanathan", 7) == 0)
  {
    switch(order)
    {
      case 24: permute(v24l,v24h,24);(*low)=v24l;(*high)=v24h;(*dimFilter)=4;break;
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

//-------------------------------------------------------------------------------------------------

void WaveletPacketDecomposition::printDescriptionFull(segmentDescription* description, int dimDescription, int depth)
{
  int i,j;
  for (i=0; i<dimDescription; i++)
  {	
    printf("dim=%2i",      description[i].dim);
    printf(" energy=%10.10e", description[i].energy);
    printf(" path=%d", description[i].path);
    printf(" level=%d", description[i].level);
    printf(" tree-pos=");
    for (j = depth-1; j >= depth - description[i].level; j--)
    {
      if((1<<j) & description[i].path) 
        printf("R");
      else
        printf("L");
    }
    printf(" center-frequency=%g\n", description[i].frequency);
  }
}

//-------------------------------------------------------------------------------------------------

void WaveletPacketDecomposition::printDescriptionEnergies(segmentDescription* description, int dimDescription, int depth)
{
  printf("\n");
  for (int i=0; i<dimDescription; i++)
  {	
    printf(" WPT[%gHz]\t= %30f\n", description[i].frequency, description[i].energy);
  }
}

//-------------------------------------------------------------------------------------------------

void WaveletPacketDecomposition::Convolution(const double* signal, int dimSignal, const double* filter, int dimFilter, double* output)
{
  int i, j;
  
  for (i=0 ; i<dimFilter-1; i+=2)
  {
    output[i>>1] = 0;
    for (j=0; j<=i; j++)
    {
      output[i>>1] += signal[i-j] * filter[j]; 
    } 	
  }

  for( ; i<dimSignal; i+=2)
  {
    output[i>>1] = 0;
    for(j=0; j<dimFilter; j++)
    {
      output[i>>1] += signal[i-j] * filter[j]; 	
    }
  }

  for( ; i<(dimSignal+dimFilter-1); i+=2)
  { 
    output[i>>1] = 0;
    for(j=(i-dimSignal+1); j<dimFilter; j++)
    {
      output[i>>1] += signal[i-j] * filter[j];
    }	
  } 
}

//---------------------------------------------------------------------------------------

double WaveletPacketDecomposition::ComputeEnergy(double* Signal, int dimSignal)
{
   double energy = 0.0;
   
   for (int i=0; i<dimSignal; i++)
      energy += Signal[i] * Signal[i];
   energy /= dimSignal;
   return energy;
}

//-------------------------------------------------------------------------------------------------

int WaveletPacketDecomposition::CheckTree(const int* tree, int depth, int* numChannels)
{
	int i=0,j=0;
	int parts=1;
	(*numChannels)=0;
  
	for (i=0; i<depth-1; i++)
	{
		for(j=0; j<parts; j++)
		{
			if (tree[(1<<i)+j-1]==0)
			{
				if (tree[(2<<i) + (j<<1) - 1]==1 || tree[(2<<i) + (j<<1)]==1)
				{
					printf("CheckTree (wavelet_packet_decomposition.cpp): not a correct wavelet paket tree definition \n");
					return -1;
				}
			}
			else
			{
			  if (tree[(2<<i) + (j<<1) - 1] == 0)
          (*numChannels)++; 
			  if (tree[(2<<i) + (j<<1)]     == 0)
          (*numChannels)++;
			}
		}
		parts = parts<<1;
	}

	//i = depth - 1;
	for (j=0; j<parts; j++)
	{
	  if(tree[(1<<i)+j-1]==1)
      (*numChannels) += 2;
	}  
	return 0;
}

//-------------------------------------------------------------------------------------------------

void WaveletPacketDecomposition::doWaveletPacketDecomposition(double* signal , int dimSignal, const double* lowPass, const double* highPass, int dimFilter, const int* tree, int depth, double* transformed, int* dimTransformed, segmentDescription* description, int* dimDescription)
{
  int                 parts = 1;
  int                 dimParts;
  double*             temp;
  segmentDescription  segment;
  int                 changeFlag;
  int                 address = 0;

  temp        = (double*) malloc((*dimTransformed) * sizeof(double));
  dimParts    = (*dimTransformed);
  (*dimDescription)  = 0;

  // first copy the input signal to the output signal
  memcpy(transformed, signal, dimSignal * sizeof(double));

  for (int i=0; i<(depth-1); i++)
  {
    for (int j=parts-1; j>=0; j--)
    {
      if (tree[(1<<i)+j-1]==1)
      {
        Convolution(transformed+j*dimParts, dimSignal, highPass, dimFilter, temp + j * dimParts+(dimParts>>1));	
        Convolution(transformed+j*dimParts, dimSignal, lowPass,  dimFilter, temp + j * dimParts);
        if (tree[(2<<i)+(j<<1)]==0)
        {
           description[(*dimDescription)].dim    = (dimSignal+dimFilter)>>1;
           description[(*dimDescription)].energy = ComputeEnergy(temp+j*dimParts+(dimParts>>1), (dimSignal+dimFilter)>>1);  
           description[(*dimDescription)].path   = (1<<(depth-i)) * j+(1<<(depth-i-1));
           description[(*dimDescription)].level  = i+1;
           (*dimDescription)++;
        }
          
        if (tree[(2<<i)+(j<<1)-1]==0)
        {
           description[(*dimDescription)].dim    = (dimSignal+dimFilter)>>1;
           description[(*dimDescription)].energy = ComputeEnergy(temp+j*dimParts, (dimSignal+dimFilter)>>1);
           description[(*dimDescription)].path   = (1<<(depth-i))*j; 
           description[(*dimDescription)].level  = i+1;
           (*dimDescription)++;
        }
      }
    } 
    memcpy(transformed, temp, (*dimTransformed) * sizeof(double)); 
    dimSignal = (dimSignal+dimFilter)>>1 ;
    parts     = parts<<1;
    dimParts  = dimParts>>1;
  }
	
  int i=depth-1; 
  for (int j=parts-1; j>=0; j--)
  {
    if (tree[(1<<i)+j-1]==1)
    {
      Convolution(transformed+j*dimParts, dimSignal, highPass, dimFilter, temp+j*dimParts+(dimParts>>1));	
      Convolution(transformed+j*dimParts, dimSignal, lowPass,  dimFilter, temp+j*dimParts);	
      description[(*dimDescription)].dim    = (dimSignal+dimFilter)>>1;
      description[(*dimDescription)].energy = ComputeEnergy(temp+j*dimParts+(dimParts>>1), (dimSignal+dimFilter)>>1);  
      description[(*dimDescription)].path   = (j<<1)+1;
      description[(*dimDescription)].level  = i+1;
      (*dimDescription)++;
      description[(*dimDescription)].dim    = (dimSignal+dimFilter)>>1;
      description[(*dimDescription)].energy = ComputeEnergy(temp+j*dimParts, (dimSignal+dimFilter)>>1) ;
      description[(*dimDescription)].path   = (j<<1); 
      description[(*dimDescription)].level  = i+1;
      (*dimDescription)++;
    }
  } 

  dimSignal = (dimSignal+dimFilter)>>1;

  for (int i=0; i<(*dimDescription)-1; i++)
  {
    changeFlag = 0;
    for (int j=0; j<(*dimDescription)-1; j++)
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

  for (int i=0; i<(*dimDescription); i++)
  {
    memcpy(transformed + address, temp + (description[i].path * dimSignal), description[i].dim * sizeof(double));
    address += description[i].dim;
  }
  (*dimTransformed) = address;
  
  // fianlly set the center frequencies for the leaf nodes of the wavelet packet tree in the segementDescriptions
  for (int i=0; i<(*dimDescription); i++) // go through the leaf nodes
  {	
    double frequencyDiff   = sampleRate / 8.0;
    double frequencyCenter = sampleRate / 4.0;
    for (int j = depth-1; j >= depth - description[i].level; j--) // go trough the tree
    {
      //printf("CF=%g DF=%g SR=%g\n", frequencyCenter, frequencyDiff, sampleRate);
      if((1<<j) & description[i].path) 
        // for the right/up tree branch
        frequencyCenter += frequencyDiff;
      else
        // for the left/down tree branch
        frequencyCenter -= frequencyDiff;
      frequencyDiff /= 2.0;
    }
    description[i].frequency = frequencyCenter;
  }

  // empty the waste
  free(temp);
}

//-------------------------------------------------------------------------------------------------

int WaveletPacketDecomposition::doWavelet(double* signal, int dimSignal, const double* lowPass, const double* highPass, int dimFilter, int depth, double* transformed)
{
  if(((dimSignal>>depth)<<depth) != dimSignal )
  {
    printf("circular wavelet transformation of depth %i with signal length %i not possible\n", depth, dimSignal);
    return -1;
  }

  if((dimSignal>>(depth-1)) < dimFilter)
  {
    printf("signal length(%i) to short for circular wavelet transformation of depth %i with filter length %i\n", dimSignal, depth, dimFilter); 
  }

  for (int i=0; i<depth; i++)
  {
    /* lowPass */
    for(int j=0; j<dimSignal; j+=2)
    {
      transformed[j>>1] = 0;
      for(int k=0; k<dimFilter; k++)
      {
        transformed[j>>1] += signal[(j + k) % dimSignal] * lowPass[k];
      }
    }
    /* highPass */
    for(int j=0; j<dimSignal; j+=2)
    {
      transformed[ (j>>1) + (dimSignal>>1) ] = 0;
      for(int k=0; k<dimFilter; k++)
      {
        transformed[(j>>1) + (dimSignal>>1)] += signal[(j+k) % dimSignal] * highPass[k];
      }
    }
    dimSignal = dimSignal>>1;
    memcpy(signal, transformed, dimSignal * sizeof(double));
  }
  return 0;
} 

//-------------------------------------------------------------------------------------------------
 
} // namespace math

} // namepsace clauer
