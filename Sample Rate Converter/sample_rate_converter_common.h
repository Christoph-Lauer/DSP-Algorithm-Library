/**
 * (c)      Christoph Lauer Engineering
 *
 * @file    sample_rate_converter_common.h
 * @class   clauer::math::Reasmple
 * @version 1.0
 * @date    2022
 * @author  Christoph Lauer
 * @brief   A header file for multiple source files
 * @see     http://en.wikipedia.org/wiki/Sample_rate_conversion
 *
 * This header file collectes the macros, enums, typedefs and 
 * compiler settings for the sample rate converter function collection
 * in the sample_rate_converter files. At the end of the file all the compiler
 * linker and build settings can be chenged/trimmed in any case of errors in 
 * the build process.
 */

//-------------------------------------------------------------------------------------------------

#ifndef SAMPLE_RATE_CONVERTER_COMMON
#define SAMPLE_RATE_CONVERTER_COMMON

//-------------------------------------------------------------------------------------------------

// Local header
#include "sample_rate_converter.h"

// Stl headers
#include <math.h> // needed only for the definition "lrint" function

//-------------------------------------------------------------------------------------------------

namespace clauer
{
namespace math
{
namespace Resample
{

//-------------------------------------------------------------------------------------------------

/**
 * Some common MACRO definition conventions doe numbers
 */
enum
{	
  SRC_FALSE	= 0,
	SRC_TRUE	= 1,
	SRC_MODE_PROCESS	= 555,
	SRC_MODE_CALLBACK	= 556
};

//-------------------------------------------------------------------------------------------------

/**
 * Error code conventions
 */
enum
{	
  SRC_ERR_NO_ERROR = 0,
	SRC_ERR_MALLOC_FAILED,
	SRC_ERR_BAD_STATE,
	SRC_ERR_BAD_DATA,
	SRC_ERR_BAD_DATA_PTR,
	SRC_ERR_NO_PRIVATE,
	SRC_ERR_BAD_SRC_RATIO,
	SRC_ERR_BAD_PROC_PTR,
	SRC_ERR_SHIFT_BITS,
	SRC_ERR_FILTER_LEN,
	SRC_ERR_BAD_CONVERTER,
	SRC_ERR_BAD_CHANNEL_COUNT,
	SRC_ERR_SINC_BAD_BUFFER_LEN,
	SRC_ERR_SIZE_INCOMPATIBILITY,
	SRC_ERR_BAD_PRIV_PTR,
	SRC_ERR_BAD_SINC_STATE,
	SRC_ERR_DATA_OVERLAP,
	SRC_ERR_BAD_CALLBACK,
	SRC_ERR_BAD_MODE,
	SRC_ERR_NULL_CALLBACK,
	SRC_ERR_NO_VARIABLE_RATIO,
	SRC_ERR_MAX_ERROR           // This must be the last error number
};

//-------------------------------------------------------------------------------------------------

/**
 * This struct defines the common input structure for the converters
 */
typedef struct SRC_PRIVATE_tag
{	
  double	last_ratio, last_position;
	int		error;
	int		channels;
	int		mode;                                                             // SRC_MODE_PROCESS or SRC_MODE_CALLBACK
	void	*private_data;	                                                  // Pointer to data to converter specific data
	int		(*vari_process) (struct SRC_PRIVATE_tag *psrc, SRC_DATA *data); 	// Varispeed process function.
	int		(*const_process) (struct SRC_PRIVATE_tag *psrc, SRC_DATA *data);	// Constant speed process function.
	void	(*reset) (struct SRC_PRIVATE_tag *psrc); 	                        // State reset.
} SRC_PRIVATE;

//-------------------------------------------------------------------------------------------------

/**
 * This group of prototype definitions is for the resampling method definitions in the 
 * three method files. This prototypes define the three core resampling algorithm functions.
 */
//@{

/// In sample_rate_converter_method_sinc.cpp
const char* sinc_get_name (int src_enum);
const char* sinc_get_description (int src_enum);
int sinc_set_converter (SRC_PRIVATE *psrc, int src_enum);

/// In sample_rate_converter_method_linear.c
const char* linear_get_name (int src_enum);
const char* linear_get_description (int src_enum);
int linear_set_converter (SRC_PRIVATE *psrc, int src_enum);

/// In sample_rate_converter_method_zoh.c
const char* zoh_get_name (int src_enum);
const char* zoh_get_description (int src_enum);
int zoh_set_converter (SRC_PRIVATE *psrc, int src_enum);

//@}

//----------------------------------------------------------------------

/**
 *	Common static inline casting function used in all three resampling method files
 */
static inline double fmod_one (double x)
{	
  double res;
	res = x - lrint (x);
	if (res < 0.0)
		return res + 1.0;
	return res;
}

//----------------------------------------------------------------------

} // namespace Resample

} // namespace math

} // namespace rte

//----------------------------------------------------------------------

// some common macro definitions outside the namespace
#define	SRC_MAX_RATIO			    256
#define	SRC_MIN_RATIO_DIFF		(1e-20)

#define	MAX(a,b)	(((a) > (b)) ? (a) : (b))
#define	MIN(a,b)	(((a) < (b)) ? (a) : (b))

#define	ARRAY_LEN(x)			((int) (sizeof (x) / sizeof ((x) [0])))
#define OFFSETOF(type,member)	((int) (&((type*) 0)->member))

#define	MAKE_MAGIC(a,b,c,d,e,f)	((a) + ((b) << 4) + ((c) << 8) + ((d) << 12) + ((e) << 16) + ((f) << 20))

//----------------------------------------------------------------------

//!
//! BUILD SETTINGS BUILD SETTINGS BUILD SETTINGS BUILD SETTINGS BUILD SETTINGS 
//!
//! The folowing macro definitions are only for build, compiler and linker 
//! issues and should be manipulated/trimmed in case of any problems while
//! the build phase

/* Set to 1 if the compile is GNU GCC. */
#define COMPILER_IS_GCC 0

/* Target processor clips on negative float to int conversion. */
#define CPU_CLIPS_NEGATIVE 1

/* Target processor clips on positive float to int conversion. */
#define CPU_CLIPS_POSITIVE 0

/* Target processor is big endian. */
#define CPU_IS_BIG_ENDIAN 0

/* Target processor is little endian. */
#define CPU_IS_LITTLE_ENDIAN 1

/* Set to 1 to enable debugging. */
#define ENABLE_DEBUG 0

/* Major version of GCC or 3 otherwise. */
/* #undef GCC_MAJOR_VERSION */

/* Define to 1 if you have the `alarm' function. */
/* #undef HAVE_ALARM */

/* Define to 1 if you have the `calloc' function. */
#define HAVE_CALLOC 1

/* Define to 1 if you have the `ceil' function. */
#define HAVE_CEIL 1

/* Define to 1 if you have the <dlfcn.h> header file. */
/* #undef HAVE_DLFCN_H */

/* Define to 1 if you have the `floor' function. */
#define HAVE_FLOOR 1

/* Define to 1 if you have the `fmod' function. */
#define HAVE_FMOD 1

/* Define to 1 if you have the `free' function. */
#define HAVE_FREE 1

/* Define to 1 if you have the <inttypes.h> header file. */
/* #undef HAVE_INTTYPES_H */

/* Define to 1 if you have the `efence' library (-lefence). */
/* #undef HAVE_LIBEFENCE */

/* Define to 1 if you have the `fftw' library (-lfftw). */
/* #undef HAVE_LIBFFTW */

/* Define to 1 if you have the `m' library (-lm). */
/* #undef HAVE_LIBM */

/* Define to 1 if you have the `rfftw' library (-lrfftw). */
/* #undef HAVE_LIBRFFTW */

/* Define if you have C99's lrint function. */
/* #undef HAVE_LRINT */

/* Define if you have C99's lrintf function. */
/* #undef HAVE_LRINTF */

/* Define to 1 if you have the `malloc' function. */
#define HAVE_MALLOC 1

/* Define to 1 if you have the `memcpy' function. */
#define HAVE_MEMCPY 1

/* Define to 1 if you have the `memmove' function. */
#define HAVE_MEMMOVE 1

/* Define to 1 if you have the <memory.h> header file. */
#define HAVE_MEMORY_H 1

/* Define if you have signal SIGALRM. */
/* #undef HAVE_SIGALRM */

/* Define to 1 if you have the `signal' function. */
/* #undef HAVE_SIGNAL */

/* Set to 1 if you have libsndfile. */
#define HAVE_SNDFILE 1

/* Define to 1 if you have the <stdint.h> header file. */
/* #undef HAVE_STDINT_H */

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the <strings.h> header file. */
#define HAVE_STRINGS_H 1

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if you have the <sys/stat.h> header file. */
#define HAVE_SYS_STAT_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
#define HAVE_SYS_TYPES_H 1

/* Define to 1 if you have the <unistd.h> header file. */
#define HAVE_UNISTD_H 1

/* Set to 1 if compiling for Win32 */
#define OS_IS_WIN32 1

/* Name of package */
#define PACKAGE "libsamplerate"

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT ""

/* Define to the full name of this package. */
#define PACKAGE_NAME ""

/* Define to the full name and version of this package. */
#define PACKAGE_STRING ""

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME ""

/* Define to the version of this package. */
#define PACKAGE_VERSION ""

/* The size of a `double', as computed by sizeof. */
#define SIZEOF_DOUBLE 8

/* The size of a `float', as computed by sizeof. */
#define SIZEOF_FLOAT 4

/* The size of a `int', as computed by sizeof. */
#define SIZEOF_INT 4

/* The size of a `long', as computed by sizeof. */
#define SIZEOF_LONG 4

/* Define to 1 if you have the ANSI C header files. */
#define STDC_HEADERS 1

/* Version number of package */
#define VERSION "0.1.3"

/* Extra Win32 hacks. */

/*
 *	Microsoft's compiler still does not support the 1999 ISO C Standard 
 *	which includes 'inline'.
 */
#define inline __inline

//----------------------------------------------------------------------

#endif  // SAMPLE_RATE_CONVERTER_COMMON

