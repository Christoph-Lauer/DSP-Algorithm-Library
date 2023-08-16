/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    generic_wave_file_handler.h
 * @class   GenericWaveFileHandler
 * @version 0.9
 * @date    2022
 * @author  Christoph Lauer
 * @brief   This is a generic type file interface to wave files.
 * @see     http://de.wikipedia.org/wiki/RIFF_WAVE
 * @todo    finished so far for the simplest implementation. There should be accessor functions
 *          for metadata and the data variables. There should be any kind of support for the 
 *          INT24 file format where no generic data type is available...
 *
 * This class template file implements the generic wave file handling for the header and the core 
 * data which will be handled through an generic data type template class. The header will be written 
 * after the wav file specification and this template should be an good base for the handling
 * of the generic file types. More problematic will be the implementation of non generic data type
 * exotic file formats like PCM-INTEGER-24 or 12 bit. For this special cases a data casting class should
 * be written for the INT-8 type.
 *
 * Here a brief abstract from the WAV File Specification FROM http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
 * The canonical WAVE format starts with the RIFF header:
 * POS LENGTH NAME        DESCRIPTION
 * 0  4  ChunkID          Contains the letters "RIFF" in ASCII form (0x52494646 big-endian form).
 * 4  4  ChunkSize        36 + SubChunk2Size, or more precisely:
 *                        4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
 *                        This is the size of the rest of the chunk 
 *                        following this number.  This is the size of the 
 *                        entire file in bytes minus 8 bytes for the
 *                        two fields not included in this count:
 *                        ChunkID and ChunkSize.
 * 8  4  Format           Contains the letters "WAVE",(0x57415645 big-endian form).
 *
 * The "WAVE" format consists of two subchunks: "fmt " and "data":
 * The "fmt " subchunk describes the sound data's format:
 * 12  4  Subchunk1ID     Contains the letters "fmt "
 *                        (0x666d7420 big-endian form).
 * 16  4  Subchunk1Size   16 for PCM.  This is the size of the
 *                        rest of the Subchunk which follows this number.
 * 20  2  AudioFormat     PCM = 1 (i.e. Linear quantization)
 *                        Values other than 1 indicate some 
 *                        form of compression.
 * 22  2  NumChannels     Mono = 1, Stereo = 2, etc.
 * 24  4  SampleRate      8000, 44100, etc.
 * 28  4  ByteRate        == SampleRate * NumChannels * BitsPerSample/8
 * 32  2  BlockAlign      == NumChannels * BitsPerSample/8
 *                        The number of bytes for one sample including
 *                        all channels. I wonder what happens when
 *                        this number isn't an integer?
 * 34  2  BitsPerSample   8 bits = 8, 16 bits = 16, etc.
 *
 * The "data" subchunk contains the size of the data and the actual sound:
 * 36  4  Subchunk2ID     Contains the letters "data"
 *                        (0x64617461 big-endian form).
 * 40  4  Subchunk2Size   == NumSamples * NumChannels * BitsPerSample/8
 *                        This is the number of bytes in the data.
 *                        You can also think of this as the size
 *                        of the read of the subchunk following this number.
 * 44  *  Data            The actual sound data.
 *
 * Please note that this class is a generic class template and it is stored in an c++
 * HPP file instead of an H file.
 */

//-------------------------------------------------------------------------------------------------

#ifndef CLAUER_IO_GENERIC_WAVE_FILE_HANDLER
#define CLAUER_IO_GENERIC_WAVE_FILE_HANDLER
 
//-------------------------------------------------------------------------------------------------

// The PCM data type definitions
#define     FLOAT_32    float
#define     FLOAT_64    double
#define     INT_8       char
#define     INT_16      short int
#define     INT_32      int

//-------------------------------------------------------------------------------------------------

// Stl headers
#include <fstream>
#include <iostream>
#include <string>

// C headers
#include <cstring>

using namespace std;

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace io
{

//-------------------------------------------------------------------------------------------------

template <class T> class GenericWaveFileHandler
{

//-----------------------------------------------------------------------------------------------

public:

 /**
  * Note that the whole wave file data is public accessible. This is not good c++
  * style but in this case it makes it much more easy to access the metadata and the 
  * data block. See the above description ofr a more detailed description.
  */
  //@{
  /// The core data
	T* 	    myData;
  /// The file path
    char* 	myPath;
  /// The chunk size (the size of the wav file after this block)
	int 	myChunkSize;
  /// As the word says, see the above description for details
	int	    mySubChunk1Size;
  /// Should be set to 0x0001 for PCM data, for floating point fata set to 0x0003
    short 	myFormat;
  /// The number of channels
	short 	myChannels;
  /// The sample rate  
	int   	mySampleRate;
  /// Bytes per second
	int   	myByteRate;
  /// Bytes per sample
	short 	myBlockAlign;
  /// 8, 16, 32 usw...
	short 	myBitsPerSample;
  /// size of the data block in bytes
	int	    myDataSize;
  //@}

//-----------------------------------------------------------------------------------------------

 /**
  * This simple construktor takes only a path string.
  *
  * @path   filePath    The path to the wave file.
  */
	GenericWaveFileHandler(const char* filePath)
  {
    myPath = new char[256];
    strcpy(myPath, filePath);
  }

//-----------------------------------------------------------------------------------------------

 /**
  * The destruktor simple waste the grabage.
  */
	~GenericWaveFileHandler()
	{
		delete myPath;
	}

//-----------------------------------------------------------------------------------------------

 /**
  * This public read function reads out the wave meta information 
  * and the data. The data is stored into the myData variable and 
  * the metadata informatrion is stored in the format shrink variables.
  * This function can be used to read only the metadata if the readData
  * with the readData flag set to false
  *
  * @param    readData  If this flag is set the core sample data will be read out of the 
  *                     file, otherwise only the metadata will be read, deafult is read the data.
  * @return             Returns true if the data could be successfully read from the file.
  */
  bool read(bool readData=true)
  {
    ifstream inFile( myPath, ios::in | ios::binary);
    //printf("Reading wav file...\n");                                      // only for debugging purposes
    inFile.seekg(4, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myChunkSize), 4 );                 // read the ChunkSize
    inFile.seekg(16, ios::beg);
    inFile.read(reinterpret_cast<char*>(&mySubChunk1Size), 4 );             // read the SubChunk1Size
    //inFile.seekg(20, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myFormat), sizeof(short) );        // read the file format.  This should be 1 for PCM
    //inFile.seekg(22, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myChannels), sizeof(short) );      // read the # of channels (1 or 2)
    //inFile.seekg(24, ios::beg);
    inFile.read(reinterpret_cast<char*>(&mySampleRate), sizeof(int) );      // read the samplerate
    //inFile.seekg(28, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myByteRate), sizeof(int) );        // read the byterate
    //inFile.seekg(32, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myBlockAlign), sizeof(short) );    // read the blockalign
    //inFile.seekg(34, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myBitsPerSample), sizeof(short) ); // read the bitspersample
    inFile.seekg(40, ios::beg);
    inFile.read(reinterpret_cast<char*>(&myDataSize), sizeof(int) );        // read the size of the data
    // read the data chunk
    if (readData == true)
    {
      myData = new T[myDataSize / sizeof(T)];
      inFile.seekg(44, ios::beg);
      // this casting and the multiplication is a special hack
      inFile.read(reinterpret_cast<char*>(myData), myDataSize); 
    }
    // close the input file
    inFile.close(); 
    if (inFile.fail() == true)
    {
      cerr << endl << "*** Error while read the wave file " << myPath << " ***" << endl << endl; 
      return false;
    }
		return true; // this should probably be something more descriptive
  }

//-----------------------------------------------------------------------------------------------

 /**
  * This public save function saves the wave file data stored into the myData variable with
  * the metadata stored into the metadata shrink of the class.
  *
  * @return   returns true if the file was successfully saved to the file.
  */
  bool write()
  {
    // first lets prepare the the rest of the metadata
    mySubChunk1Size = 16;
    myChunkSize = 4 + (8 + mySubChunk1Size) + (8 + myDataSize);
    // myFormat     must be set outside this funcion
    // myCHannels   must be set outside this function
    // myChannels   must be set outside this function
    // mySampleRate must be set outside this function
    myByteRate   = mySampleRate * myChannels * myBitsPerSample / 8;
    myBlockAlign = myChannels * myBitsPerSample;
    
    // now access the file
    fstream myFile (myPath, ios::out | ios::binary);
    // write the wav file per the wav file format
    myFile.seekp (0, ios::beg); 
    myFile.write ("RIFF", 4);
    myFile.write (reinterpret_cast<char*>(&myChunkSize), 4);
    myFile.write ("WAVE", 4);
    myFile.write ("fmt ", 4);
    myFile.write (reinterpret_cast<char*>(&mySubChunk1Size), 4);
    myFile.write (reinterpret_cast<char*>(&myFormat), 2);
    myFile.write (reinterpret_cast<char*>(&myChannels), 2);
    myFile.write (reinterpret_cast<char*>(&mySampleRate), 4);
    myFile.write (reinterpret_cast<char*>(&myByteRate), 4);
    myFile.write (reinterpret_cast<char*>(&myBlockAlign), 2);
    myFile.write (reinterpret_cast<char*>(&myBitsPerSample), 2);
    myFile.write ("data", 4);
    myFile.write (reinterpret_cast<char*>(&myDataSize), 4);
    myFile.write (reinterpret_cast<char*>(myData), myDataSize);
    if (myFile.fail() == true)
    {
      cerr << endl << "*** Error while write the wave file " << myPath << " ***" << endl << endl; 
      return false;
    }
		return true;
	}

//-----------------------------------------------------------------------------------------------
  
 /**
  * This small function returns simple a format string presenting the 
  * current read or set data.
  *
  * @param  returns a String with the current format information.
  */
	char *getFormatString()
	{
		char *summary = new char[256];
		sprintf(summary, " Format: %d\n Channels: %d\n SampleRate: %d\n ByteRate: %d\n BlockAlign: %d\n BitsPerSample: %d\n DataSize: %d\n", myFormat, myChannels, mySampleRate, myByteRate, myBlockAlign, myBitsPerSample, myDataSize);
		return summary;
	}

//-----------------------------------------------------------------------------------------------
  
}; // class template GenericWaveFileHandler

} // namespace io

} // namespace clauer

//-----------------------------------------------------------------------------------------------

#endif // CLAUER_IO_GENERIC_WAVE_FILE_HANDLER
