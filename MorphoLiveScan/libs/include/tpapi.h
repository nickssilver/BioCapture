/// @file tpapi.h
/// @brief TouchPrint API Definition File
/// @author MorphoLiveScan
/// @copyright Copyright Morpho 2013 - all rights reserved.


#ifndef TPAPI_H
#define TPAPI_H
#ifdef WIN32
   #define STDCALL __stdcall
   #ifdef TPAPI_EXPORTS
      #define TP_API __declspec(dllexport)
   #else
      #define TP_API __declspec(dllimport)
   #endif
#else
   #define MAX_PATH 260
   #define TCHAR char 
   #define STDCALL
   #define WAIT_OBJECT_0 0
   //#ifdef TPAPI_EXPORTS
      #define TP_API extern "C" __attribute__ ((visibility("default")))
  // #else
   //   #define TP_API __attribute__ ((visibility("default")))
   //#endif
   #define SetEvent(event) pthread_cond_signal(event)
#endif

/**
 * 8 bits unsigned char
 */
typedef unsigned char   U8;
/**
 * bits unsigned short
 */
typedef unsigned short  U16;
#ifndef U32
/**
 * 32 bits unsigned int
 */
   typedef unsigned int U32;
#endif
#ifndef S32
 /**
 * 32 bits signed int
 */
   typedef signed int   S32;
#endif

/**
 * Application-provided callback function that will be called when either 
 * a frame is received or the capture state has changed.
 * <c>\b long __stdcall \b *TP_ImgCallbk(U32 \b hDev, U32 \b capState, U8 \b *imgBuf, U16 \b imgWdth, U16 \b imgHght, void \b *pCtxt)</c> \n\n
 *
 * \c hDev is the handle to the device initiating the callback. The \c capState 
 * will indicate the capture process state code, and possibly the type
 * image in \c imgBuf (see Images). \c pCtxt is the application-defined value 
 * passed into \ref TP_StartPreview. Applications should avoid blocking in a 
 * callback function since it must complete before any subsequent callbacks 
 * can occur. Also, no TPAPI functions should be called from \ref CSTATE_PREVIEW, 
 * \ref CSTATE_STARTED, and non-RTQA \ref CSTATE_CAPTURED callbacks. \n
 *
 * \par Valid capStates are:
 *		- \ref CSTATE_PREVIEW	 Preview image available
 *		- \ref CSTATE_STARTED	 Operator has pressed SCAN button, image capture has begun
 *		- \ref CSTATE_CAPTURED	 Capture complete, a decorated image may be available. In case of MorphoTopSlim device the buffer image and the image length is equal to NULL
 *		- \ref CSTATE_SCANPRESS  Scan button on scanner was pressed – can only occur when 
 *								 \ref CMODE_REPORT_KEYS is enabled.
 *		- \ref CSTATE_SAVEPRESS  Save button on scanner was pressed - can only occur when 
 *								 \ref CMODE_REPORT_KEYS is enabled.
 *		- \ref CSTATE_UI_CHANGED State of the user interface LEDs has changed - can only 
 *								 occur when \ref CMODE_REPORT_UI is enabled. \n
 *
 * \par Final capture process states:
 *		- \ref CSTATE_ABORTED	SAVE button pressed during Preview stage
 *		- \ref CSTATE_ABORTED_FINGER	Finger were detected in early frames of capture
 *		- \ref CSTATE_FINISHED	Finished image available, capture process complete
 *		- \ref CSTATE_REJECTED	SCAN button pressed during Review stage
 *		- \ref CSTATE_TOOFAST	Hand scanner drum rolled too fast or too slow, capture failed.
 *		- \ref CSTATE_CANCELED	Capture stopped by the host PC
 *		- \ref CSTATE_FINISHED_WARN_ELEC_ISSUE Electrical issue: please verify electrical environment and the ground connection of devices
 *		- \ref CSTATE_FAILED	Failed due to hardware error (ex : unplug of the device) or system error (ex : no memory). The recommended recovery mechanism is to
 *									- Check the sensor connexion state (with \ref TP_ConnectedDevice Function)
 *										-  If the device is not plugged, plugged it and wait for its reconnection
 *										-  If the device is plugged, just wait for its reconnection
 *									- Retry the capture
 *									- If the retry capture fails, then call Reset Device, 
 *										 and retry the capture once more.  
 *									- If the capture still fails, re-boot the entire system. \n
 * \par Image information
 * \c imgWdth, \c imgHght, and \c imgSz indicate the size of the image in \c imgBuf and will be 
 * valid only for the duration of the callback.  See 2.2 for image sizes.  A return code of zero 
 * will indicate acceptance of the image, -1 rejection, and is only valid on \ref CSTATE_CAPTURED 
 * (decorated image) callbacks (see Get Image Quality Data).
 *
 * \par Other
 * \c pCtxt is an application-defined value passed back in \ref TP_ImgCallbk.
 *
 */
typedef long (STDCALL *TP_ImgCallbk)(U32, U32, U8*, U16, U16, void *);

/** @name Error Codes
 */
//@{
#define TPAPI_NOERROR              0								/**< Success */
#define TPAPI_ERR                  8000								/**< An error occured*/

#define TPAPI_UNKNOWN              (TPAPI_ERR + 0)					/**< Unknown error */
#define TPAPI_UNINITIALIZED        (TPAPI_ERR + 1)					/**< Library has not been successfully initialized */
#define TPAPI_OUTOFRESOURCES       (TPAPI_ERR + 2)					/**< Memory or other OS resource allocation error */
#define TPAPI_DEVICEUNAVAILABLE    (TPAPI_ERR + 3)					/**< Device not found on the bus */
#define TPAPI_TOOMANYDEVICES       (TPAPI_ERR + 4)					/**< Too many devices on the bus */
#define TPAPI_TIMEOUT              (TPAPI_ERR + 5)					/**< Timeout attempting to communicate with the device */
#define TPAPI_PARAMETEROUTOFRANGE  (TPAPI_ERR + 6)					/**< Invalid value of one or several parameters */
#define TPAPI_INVALIDSTATE         (TPAPI_ERR + 7)					/**< Command cannot be completed in the current capture state */
#define TPAPI_ACCESSVIOLATION      (TPAPI_ERR + 8)					/**< Access violation */
#define TPAPI_BADRESPONSE          (TPAPI_ERR + 9)					/**< Unexpected or invalid response to device command */
#define TPAPI_BADFILE              (TPAPI_ERR + 10)					/**< Error writing equalization reference image */
#define TPAPI_DFC_TOOMANYFINGERS   (TPAPI_ERR + 11)					/**< Too many fingers selected in dfcMap */
#define TPAPI_DFC_NONPERMISSABLE_FINGER_COMBINATION (TPAPI_ERR + 12)/**< The thumb may not be selected in dfcMap if fingers from that same hand are selected */
#define TPAPI_DIRTY_DEVICE         (TPAPI_ERR + 13)					/**< Device is too dirty */
#define TPAPI_WARN_DIRTY_DEVICE	   (TPAPI_ERR + 14)					/**< Device is dirty ! Please clean the device and re-calibrate the device */
#define TPAPI_ERR_DIRTY_DEVICE	   (TPAPI_ERR + 15)
#define TPAPI_RTQA_NOTSUPPORTED    (TPAPI_ERR + 16)					/**< RTQA's Libraries not supported */
#define TPAPI_LISA_ERROR		   (TPAPI_ERR + 17)					/**< Processing error */
#define TPAPI_NOTSUPPORTED		   (TPAPI_ERR + 18)					/**< In case of not supported API */
#define TPAPI_DEVICE_BUSY          (TPAPI_ERR + 19)					/**< Device already open */
#define TPAPI_NOT_ENOUGH_BANDWIDTH (TPAPI_ERR + 20)					/**< Device plugged on Full speed port */
#define TPAPI_DAMAGED_DEVICE	   (TPAPI_ERR + 21)					/**< Device is damaged, contact the support */
#define TPAPI_FFD_ERROR			   (TPAPI_ERR + 22)					/**< Fake finger detection error */
#define TPAPI_TERMINATEINPROGRESS  (TPAPI_ERR + 23)				/**< Terminate in progress, command cannot be completed*/
#define TPAPI_MAXERR               (TPAPI_ERR + 24)					/**< Maximum errors reached */



#define MOD_TPAPI                  "TPAPI  "
#define MOD_SALI                   "SALI   "
#define MOD_IDXTIFF                "IDXTIFF"
#define MOD_PTHREAD                "PTHREAD"
#define MOD_OS                     "OS     "
//@}
 
/** Maximum Hand Sizes
 */
enum eHandSz
{
    TP_HAND_8_INCH               = 800,	/**< 8 inches - FBI palm print card size (default) */
    TP_HAND_10_INCH              = 1000 /**< 10 inches									   */
};
 
/** Scanner Bulk Data Types
 */
enum eScnDataType
{
    SCN_DTYPE_DSP                = 20,	/**< Update DSP firmware. This should be followed by a Reset Device to activate the update. */
    SCN_DTYPE_SET_PARM           = 90	/**<  */
};
 
/** Image Types
 */
enum eCaptureType
{
   CTYPE_ROLL               = 0x00,		/**< Roll type					*/
   CTYPE_SLAP_ONE           = 0x01,		/**< One finger slap capture	*/
   CTYPE_SLAP_FOUR          = 0x02,		/**< Four fingers slap capture	*/
   CTYPE_HAND               = 0x03,		/**< Hand-print					*/
   CTYPE_SLAP_LFOUR         = 0x04,		/**< Left four-finger slap		*/
   CTYPE_SLAP_RFOUR         = 0x05,		/**< Right four-finger slap		*/
   CTYPE_SLAP_THUMBS        = 0x06,		/**< Both thumbs slap			*/

   CTYPE_SLAP_LTHUMB        = 0x07,		/**< Left Thumb slap (plain)	*/
   CTYPE_SLAP_RTHUMB        = 0x08,		/**< Right Thumb slap (plain)	*/

   CTYPE_ROLL_LTHUMB        = 0x09,		/**< Left Thumb roll			*/
   CTYPE_ROLL_LINDEX        = 0x0A,		/**< Left Index finger roll		*/
   CTYPE_ROLL_LMIDDLE       = 0x0B,		/**< Left Middle finger roll	*/
   CTYPE_ROLL_LRING         = 0x0C,		/**< Left Ring finger roll		*/
   CTYPE_ROLL_LLITTLE       = 0x0D,		/**< Left Little finger roll	*/	
   CTYPE_ROLL_RTHUMB        = 0x0E,		/**< Right Thumb roll			*/
   CTYPE_ROLL_RINDEX        = 0x0F,		/**< Right Index finger roll	*/
   CTYPE_ROLL_RMIDDLE       = 0x10,		/**< Right Middle finger roll	*/
   CTYPE_ROLL_RRING         = 0x11,		/**< Right Ring finger roll		*/
   CTYPE_ROLL_RLITTLE       = 0x12,		/**< Right Little finger roll	*/

   CTYPE_SLAP_PALM          = 0x13,		/**< Palm-print					*/
   CTYPE_SLAP_LPALM         = 0x13,		/**< Left palm-print			*/
   CTYPE_SLAP_RPALM         = 0x14,		/**< Right palm-print			*/
   CTYPE_SLAP_LWRITERS_PALM = 0x15,		/**< Left writer’s palm-print	*/
   CTYPE_SLAP_RWRITERS_PALM = 0x16,		/**< Right writer’s palm-print  */

   CTYPE_ID1CARD_GENERIC	= 0x17,		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
   CTYPE_ID1CARD_OCR		= 0x18,		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
   CTYPE_ID1CARD_MRZ		= 0x19,		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
   CTYPE_ID1CARD_BARCODE	= 0x1A		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
};
 
/** @name DFC Finger Bits
 */
//@{
#define BIT_RTHUMB      0x0001			/**< Right Thumb	*/
#define BIT_RINDEX      0x0002			/**< Right Index	*/
#define BIT_RMIDDLE     0x0004			/**< Right Middle	*/
#define BIT_RRING       0x0008			/**< Right Ring		*/
#define BIT_RLITTLE     0x0010			/**< Right Little	*/
#define BIT_LTHUMB      0x0020			/**< Left Thumb		*/
#define BIT_LINDEX      0x0040			/**< Left Index		*/
#define BIT_LMIDDLE     0x0080			/**< Left Middle	*/
#define BIT_LRING       0x0100			/**< Left Ring		*/
#define BIT_LLITTLE     0x0200			/**< Left Little	*/	
//@}
 
/** @name Video Mode - CSTATE_PREVIEW TP_ImgCallbk imgBuf info
 */
//@{
#define UNBANDED                   0     /**< Full image - imgHght indicates # of lines returned		*/
#define BANDED_LINES               4     /**< Partial image - This label defines the # lines
                                          *   returned. Ysize indicates vertical position
                                          *   of returned buffer in the preview image.					*/
//@}
 
/** Capture Modes
 */
typedef enum
{
   CMODE_AUTO_CAP_WITH_MOTION		= 0x00000002,     /**< Enable user motion initiated Auto-capture with Preview mode. 
														This mode is valid only on roll capTypes and cannot be set 
														with CMODE_AUTO_CAP or CMODE_AUTO_CAP_WITH_PREVIEW. If 
														enabled, after centering of the finger the user can proceed 
														to roll without waiting for the device. The motion of the 
														finger will trigger the capture See Auto-capture with 
														Preview (User Initiated) for a description of a capture 
														operation utilizing this mode of operation.					*/
   CMODE_PREVIEW_INFO				= 0x00000400,     /**< Generate info on preview									*/
   CMODE_REMOVE_FINGER				= 0x00000800,	  /**< Check if finger is present in the first frames and abort if so*/	
   CMODE_RTQA						= 0x00002000,     /**< Enable RTQA and create a decorated image in the device. 
														RTQA information will be available only during the 
														CSTATE_CAPTURED via the Get Image Quality Data function. 
														See Images for information on displaying decorated images.	*/
			
   CMODE_REVIEW						= 0x00008000,     /**< Suspends the capture process following retrieval of the 
														image quality information and decorated image (if enabled), 
														allowing an operator to decide whether to accept or reject 
														the captured image. The capture process will wait in this 
														decision stage until an operator presses one of the scanner 
														buttons. The SCAN button rejects the image and the SAVE 
														button accepts the image.  If the image has been rejected 
														based on image quality criteria 
														(see <c>Get Image Quality Data</c>), only the SCAN button 
														will be enabled.											*/
   CMODE_REPORT_UI					= 0x01000000,     /**< The device will report state changes of the user interface 
														LED’s to the application-provided callback function during 
														the capture process. This will occur even on devices that do 
														not have a physical UI in which case simulated values will 
														be available. The state of the UI LEDs must be retrieved by 
														the Get Device UI function. Not available for Directed Finger 
														Capture (DFC).        */
   CMODE_AUTO_CAP					= 0x00800000,     /**< Enable Ink-like Auto-capture mode. If enabled, the device is 
														responsible for determining when to trigger a capture based 
														on the degree of finger contact detected. See Ink-like 
														Auto-capture for a description of a capture operation 
														utilizing this mode of operation.							*/
   CMODE_BEEP_MODE					= 0x02000000,     /**< Specific device beep sequence and tone configuration for 
														customer : keep only one beep after Captured status for 
														Slap, Palm and One Finger slap captures and keep one beep 
														after Started status and one dual beep after Captured status 
														for Finger Roll capture.									*/
   CMODE_AUTO_CAP_WITH_PREVIEW		= 0x04000000,     /**< Enable device initiated Auto-capture with Preview mode. 
														This mode is valid only on roll capTypes and cannot be set 
														with CMODE_AUTO_CAP or CMODE_AUTO_CAP_WITH_MOTION. If enabled, 
														the device is responsible for determining when to trigger a 
														capture. See Auto-capture with Preview (Device Initiated) for 
														a description of a capture operation utilizing this mode of 
														operation. <b>NOT AVAILABLE FOR MORPHOTOP DEVICES</b>		*/
   CMODE_DISABLE_KEYS				= 0x08000000,     /**< Disable the device’s SCAN/SAVE buttons (if available) 
														completely during the capture process. Key presses will not 
														be detected or reported to the application. The capture 
														process should be fully controlled manually via the 3
														TP_CaptureControl function.									*/
   CMODE_REPORT_KEYS				= 0x20000000,     /**< Prevent the device’s SCAN/SAVE buttons (if available) from 
														advancing the capture process. Key presses are still detected 
														and will be reported to the application-provided callback 
														function which must then take action via the TP_CaptureControl 
														function.													*/
   CMODE_CLEAN_BACKGROUND			= 0x40000000,     /**< An extra image processing step will be performed on the 
														finished image. This will attempt to clean the image 
														background of any minor contaminants (such as dust and 
														moisture drops) that were introduced onto the platen after 
														the capture was started.
														<b>NOT IMPLEMENTED IN THIS RELEASE – FOR FUTURE USE</b>		*/
   CMODE_DISABLE_BEEP				= 0x80000000,      /**< Disable device-controlled beeps during a capture operation. 
														The application can manually initiate scanner sounds via the 
														Query Switches function.									*/

	CMODE_FAST_ENROLL				= 0x00010000,		/**< Fast mode detection. Recommended for authentication.
														<b>IMPLEMENTED ONLY FOR MORPHOTOPSLIM DEVICE</b>		*/
    CMODE_MEDIUM_ENROLL 			= 0x00020000,		/**< Medium mode detection (default one). Recommended for enroll.
														<b>IMPLEMENTED ONLY FOR MORPHOTOPSLIM DEVICE</b>		*/
	CMODE_SLOW_ENROLL				= 0x00030000		/**< Slow mode detection. Recommended for enroll person with dry fingers.
														<b>IMPLEMENTED ONLY FOR MORPHOTOPSLIM DEVICE</b>		*/
} eCaptureMode;          
 
/** Capture States
 */
typedef enum
{
    CSTATE_IDLE        = 0x00000000,				/**< Idle State														*/
    CSTATE_PREVIEW     = 0x00000001,				/**< Preview image is available										*/
    CSTATE_STARTED     = 0x00000002,				/**< SCAN button pressed, capture begun								*/
    CSTATE_ABORTED     = 0x00000003,				/**< SAVE button pressed during Preview								*/
    CSTATE_CAPTURED    = 0x00000004,				/**< Image captured, decorated image may be available				*/
    CSTATE_FINISHED    = 0x00000005,				/**< Capture complete, finished image available						*/
    CSTATE_REJECTED    = 0x00000006,				/**< SCAN button pressed during Review								*/
    CSTATE_CANCELED    = 0x00000007,				/**< Capture stopped by host										*/
    CSTATE_FAILED      = 0x00000008,				/**< Capture failed due to hardware or system errors				*/
    CSTATE_TOOFAST     = 0x00000009,				/**< Handprint rolled too fast - capture refused					*/
    CSTATE_SCANPRESS   = 0x00000010,				/**< SCAN button pressed during capture with \ref CMODE_REPORT_KEYS	*/
    CSTATE_SAVEPRESS   = 0x00000011,				/**< SAVE button pressed during capture with \ref CMODE_REPORT_KEYS	*/
    CSTATE_UI_CHANGED  = 0x00000012,				/**< Device UI has changed during capture with \ref CMODE_REPORT_UI	*/
	CSTATE_FINISHED_WARN_ELEC_ISSUE	= 0x00000013,	/**< Electrical issue: please verify electrical environment and the ground connection of devices*/
	CSTATE_ABORTED_FINGER = 0x00000014				/**< Finger detected in the first frame of the capture, fingers must be removed \ref CMODE_REMOVE_FINGER */
} CaptureState;


/** ffd security levels
 */
enum eFFD_Security_Level
{
	FFD_NONE = 0,
	FFD_VERY_LOW,
	FFD_LOW,
	FFD_MEDIUM,
	FFD_HIGH,
	FFD_VERY_HIGH, 
	FFD_CRITICAL
} ;

/** ffd status
 */
typedef enum
{
  FFD_LIVE = 0, /**< Finger is Live */
  FFD_FAKE,		/**< Finger is Fake */
  FFD_MOIST,    /**< Finger is moist, we recommend to relaunch the acquisition */
}eFFD_Status;


/** Image Size
 */
struct s_imgSize
{
   unsigned int x;	/**< Image width */
   unsigned int y;	/**< Image height */
};

/** RTQA Image Information
 */
struct s_imgQA
{
    unsigned short cover_factor;	/**< Roll coverage factor (times 100)	*/
    unsigned short roll_time;		/**< number of milliseconds to roll		*/
    unsigned short contact_area;	/**< % area of contact					*/
    unsigned short good_area;		/**< % area of good contrast			*/
    unsigned short dark_area;		/**< % area too dark					*/
    unsigned short light_area;		/**< % area too light					*/
    unsigned short smear_area;		/**< % area too smeared					*/	
};

/** Soft reconstruction logging type
 */
enum e_MR_imageLoggingType
{
   E_IMG_LOGTYPE_NO = 0,			/**< No log						*/
   E_IMG_LOGTYPE_INPUT,				/**< Input log type				*/
   E_IMG_LOGTYPE_PREVIEW,			/**< Preview log type			*/
   E_IMG_LOGTYPE_INPUT_AND_PREVIEW	/**< Input and preview log type */
};

/** Soft reconstruction logging format
 */
enum e_MR_imageLoggingFormat
{
   E_IMG_LOGFORMAT_RAW = 0,	/**< RAW format used for log */
   E_IMG_LOGFORMAT_TIF		/**< TIF format used for log */
};


/** Roll location information
 */
struct s_RolledLocation
{
    unsigned short      startingColumn;		/**< 0-based position of first column	*/
    unsigned short      startingLine;		/**< 0-based position of first line	*/
    unsigned short      width;				/**< Number of columns					*/
    unsigned short      height;				/**< Number of lines					*/
} ;

/** MorphoRolled results information
 */
struct s_softReconstructionResults
{
    float               shiftScore;        /**< Shift score						*/
#ifndef WIN32
	unsigned int        nbFramesUsed;      /**< Number of frames used for the reconstruction*/
    unsigned int        qualityMark;       /**< Reconstruction quality mark		*/
	s_RolledLocation rolledLocation;	   /**< Rolled location				    */
#endif
};

/** Rolled results information
 */
struct s_rollReconstructionResult
{
    float coverage_factor;					/**< Cover score						*/
	float shiftScore;						/**< Shift score						*/
	unsigned int qualityMark;				/**< Quality mark not yet implemented	*/
	unsigned int roll_time;					/**< roll time							*/
	s_RolledLocation RolledLocation;		/**< Rolled location				    */
};

/** Box results information
 */
struct s_MTOPS_ResultsBox{
	int  statusfinger;					/**< 0: No Box; 1:OK; 2: Box acquisition in progress ; 3: appearance of the box	*/
	int  boxWidth;						/**< Box Width					*/
	int  boxHeight;						/**< Box Height					*/
	int  boxStartingLine;				/**< Box Starting Line			*/
	int  boxStartingColumn;				/**< Box Starting Column		*/
	float boxOrientation;				/**< Box Starting Orientation	*/
};

struct s_MTOPS_Results{
	int  statusglobaldtpr;			 /**< 0: No finger detected in the serquecence; 1: FingerPrints OK; 2: FingerPrints detected but not yet sufficient; 3: fingerprints disappearing */
	int  nbFingDetected;			 /**< Number of fingers detected	*/
	s_MTOPS_ResultsBox *BoxResults;  /**< Box results information		*/
	int  imgStride;					 /**< Image Stride					*/
	unsigned int imgWidth;			 /**< Image Width					*/
	unsigned int imgHeight;			 /**< Image Height					*/
};


/** @brief Structure for fake finger detection results. */
struct s_FFD_Results	
{
	eFFD_Status statusglobalffd;
	int nbFingDetected;			
	eFFD_Status *statusffdfinger;
};

/** @name Device UI Information
 */
//@{
enum eUIType
{
   UI_NONE              = 0x00000001,		/**< No UI information		*/
   UI_4_4_2_TRI_LED							/**< 4,4,2 tri-colored LED	*/
};

enum eLEDState
{
   UI_LED_OFF           = 0x00000000,	/**< Tri-colored LED off	*/
   UI_LED_GREEN,						/**< Tri-colored LED green	*/
   UI_LED_RED,							/**< Tri-colored LED red	*/
   UI_LED_YELLOW						/**< Tri-colored LED yellow */
};

struct s_442TriLED						   // UIType est UI_4_4_2_TRI_LED
{
    enum eLEDState left_four_progress;  /**< Left four finger progress LED		*/
    enum eLEDState right_four_progress; /**< Left four finger progress LED		*/
    enum eLEDState thumb_progress;		/**< Thumb finger progress LED			*/
    enum eLEDState left_index;			/**< Left index finger progress LED		*/
    enum eLEDState left_middle;			/**< Left middlefinger progress LED		*/
    enum eLEDState left_ring;			/**< Left ring finger progress LED		*/
    enum eLEDState left_little;			/**< Left little finger progress LED	*/
    enum eLEDState right_index;			/**< Right index finger progress LED	*/
    enum eLEDState right_middle;		/**< Right middle finger progress LED	*/
    enum eLEDState right_ring;			/**< Right ring finger progress LED		*/
    enum eLEDState right_little;		/**< Right little finger progress LED	*/
    enum eLEDState left_thumb;			/**< Left thumb finger progress LED		*/
    enum eLEDState right_thumb;			/**< Right thumb finger progress LED	*/
};

enum eCapControl
{
   CC_CANCEL_CAPTURE		= 0x0000000,	/**< Abort the capture process		 */
   CC_CAPTURE_IMAGE			= 0x0000001,	/**< Trigger an image capture (SCAN) */
   CC_FINISH_IMAGE			= 0x0000002,	/**< Create a finished image (SAVE)	 */
   CC_END_ROLL			    = 0x0000003		/**< End the roll capture			 */
};
//@}

/** @name Switch Information
 */
//@{
#define QRYSW_CANCEL         -1          /**< Cancel previous query (wait)			*/
#define QRYSW_NOCLEAR         0          /**< Prompt and return when a switch closure is detected  */
#define QRYSW_CLEAR           1          /**< Clear prior switch states, prompt and wait for a switch closure */
#define QRYSW_NOWAIT          2          /**< Return switch states immediately			*/
#define QRYSW_BEEP_ONLY       3			 /**< Audible prompt only */

#define SW_BSCAN_LATCHED      0x01       /**< Scan button pressed since last query		*/
#define SW_BSAVE_LATCHED      0x02       /**< Save button pressed since last query		*/
#define SW_FSAVE_LATCHED      0x04       /**< Save foot-switch pressed since last query */
#define SW_FSCAN_LATCHED      0x08       /**< Scan foot-switch pressed since last query */
#define SW_NOT_SCAN_NOW       0x10       /**< Scan button currently NOT pressed			*/
#define SW_NOT_BSAVE_NOW      0x20       /**< Save button currently NOT pressed			*/
#define SW_NOT_FSAVE_NOW      0x40       /**< Save foot-switch currently NOT pressed	*/
#define SW_NOT_FSCAN_NOW      0x80       /**< Scan foot-switch currently NOT pressed	*/
#define SW_SWITCH_TIMEOUT     0xF0       /**< WaitMsec timeout expired					*/
//@}

/** @name Device UI Information
 */
//@{
struct s_devInfo                        // Toutes les tables finissent par 0
{
   char assy_make[8];                   /**< Assembly Make (ex: "IDX")					*/
   char assy_model[16];                 /**< Assembly Model (ex: "TP-4100")			*/
   char assy_sn[16];                    /**< Assembly Serial Number (ex: "123456")		*/
   char sw_version[12];                 /**< Embedded Firmware Application Version		*/
};

/**
 * Finger quality thresholds levels
 */
struct s_fngrThlds
{
   unsigned long minimum;	/**< Minimum contact level */
   unsigned long okay;		/**< Okay contact level	   */
   unsigned long good;		/**< Good contact level	   */
};

#define TPAPI_MAX_DEVICES     2	/**< Maximum number of connected devices supported */
//@}

/** @name TP-LSMULTI-supported link library types
 */
//@{
#define TPAPI_LNK_USB					0	/**< USB library			*/
#define TPAPI_LNK_1394_OHCI				1	/**< 1394 OHCI library		*/
#define TPAPI_LNK_1394_UB				2	/**< 1394 UB library		*/
#define TPAPI_LNK_USB_MORPHO			3	/**<  USB MorphoTop library	*/
#define TPAPI_LNK_USB_MORPHOTOPSLIM		4	/**<  USB MorphoTops library	*/
#define TPAPI_LNK_USB_MORPHOTOPLC		5	/**<  USB MTOP2020 library	*/
//@}

struct s_tpDevList
{
   TCHAR devName[255];	/**< Device name								*/
   char assy_model[16];	/**< Assembly Model (ex: “TP-4100”)			*/	
   char assy_sn[16];	/**< Assembly Serial Number (ex: “123456”)		*/
   U32 libType;			/**< Link library type							*/
};

/** Type of slap detected
 */
enum eSlapDetect
{
   SLAP_DETECT_NOT_IMPLEMENTED  = -1,	/**< Slap detection not implemented	*/
   SLAP_DETECT_UNKNOWN          =  0,	/**< Slap detected unknown			*/
   SLAP_DETECT_LEFT_HAND        =  1,	/**< Left-hand slap detected		*/
   SLAP_DETECT_RIGHT_HAND       =  2,	/**< Right-hand slap detected		*/
   SLAP_DETECT_TWO_THUMBS       =  3	/**< Two-thumbs slap detected		*/
};



/** @brief Sampling modes for raw image acquisition For MorphoTop device */
enum eMorphoTopSamplingModes{	
	SM_FULL = 1,					/**< Full image */	
	SM_HALF = 2,					/**< Half image */	
	SM_THIRD = 3,					/**< Third image */	
	SM_QUARTER = 4,					/**< Quarter image */	
	SM_TINY = 8,					/**< Tiny image */	
	SM_LILLIPUTIAN = 16			/**< Very tiny image */	
};

/** Measurement of the contact using density
 */
enum eFngrDensity
{
   FNGR_NO_CONTACT              =  0,	/**< No contact							*/
   FNGR_POOR_CONTACT,					/**< Poor contact density measurement	*/
   FNGR_GOOD_CONTACT,					/**< Good contact density measurement	*/
   FNGR_GREAT_CONTACT					/**< Great contact density measurement	*/
};

/** App RTQA numbers
 */
struct s_FngrRtqa
{
  unsigned long contactPct;			/**< percent of the platen area with contact		*/
  unsigned long goodPct;			/**< percent of the contact area with good cells	*/
  unsigned long darkPct;			/**< percent of the contact area with dark cells	*/
  unsigned long lightPct;			/**< percent of the contact area with light cells	*/
  unsigned long smearPct;			/**< percent of the contact area with smeared cells */
};

/** All of the information for a single finger
 */
struct s_FngrInfo
{
   struct s_FngrRtqa  fngrRtqa;		/**< App RTQA numbers for a finger */
   enum eFngrDensity  fngrContact;	/**< Measurement of the finger contact using density */
};

/** All of the information for a preview
 */
struct s_PreviewInfo
{
   struct s_FngrInfo fngrInfo[4];	/**< Information for four fingers (max) */
   enum eSlapDetect slapDetect;		/**< Type of slap detected */
};

/** @name Function Prototypes
 */
//@{
TP_API long STDCALL TP_InitializeAPI(JNIEnv *);
TP_API long STDCALL TP_TerminateAPI(void);
TP_API long STDCALL TP_GetAndroidDeviceList(U32 *, struct s_tpDevList *, JNIEnv *);
TP_API long STDCALL TP_OpenDeviceSN(char *, U32, U32*);
TP_API long STDCALL TP_OpenDevice(TCHAR *, U32, U32*);
TP_API long STDCALL TP_CloseDevice(U32);
TP_API bool STDCALL TP_DeviceConnected(U32);
TP_API long STDCALL TP_GetDeviceInfo(U32, struct s_devInfo *);
TP_API long STDCALL TP_ResetDevice(U32);
TP_API long STDCALL TP_SetResolution(U32, enum eCaptureType, U32);
TP_API long STDCALL TP_GetResolution(U32, enum eCaptureType, U32*);
TP_API long STDCALL TP_GetMaxVideoSize(U32, enum eCaptureType, U16*, U16*);
TP_API long STDCALL TP_GetFinishedImageSize(U32, enum eCaptureType, U32, struct s_imgSize*);
TP_API long STDCALL TP_SetDeviceUI(U32, enum eUIType, void *);
TP_API long STDCALL TP_GetDeviceUI(U32, enum eUIType, void *);
TP_API long STDCALL TP_IsDeviceUISupported(U32, enum eUIType, bool*);
TP_API long STDCALL TP_IsCaptureTypeSupported(U32, enum eCaptureType, bool*);
TP_API long STDCALL TP_IsAutoCaptureSupported(U32, enum eCaptureType, bool*);
TP_API long STDCALL TP_IsCalibrationSupported(U32, enum eCaptureType, bool*);
TP_API long STDCALL TP_IsCalibrated(U32, enum eCaptureType, bool*);
TP_API long STDCALL TP_CalibrateScanner(U32, enum eCaptureType);
TP_API long STDCALL TP_CalibrateScannerWithCheck(U32, enum eCaptureType);
TP_API long STDCALL TP_SendBulkData(U32, enum eScnDataType, U8*, U32);
TP_API long STDCALL TP_GetSlapFngrThresholds(U32, struct s_fngrThlds *);
TP_API long STDCALL TP_SetSlapFngrThresholds(U32, struct s_fngrThlds *);
TP_API long STDCALL TP_GetSlapFngrRTQA(U32, U32 thrlds[10]);
TP_API long STDCALL TP_SetSlapFngrRTQA(U32, U32 thrlds[10]);
TP_API long STDCALL TP_GetSlapAutoCapTimeouts(U32, U32*, U32*, U32*);
TP_API long STDCALL TP_SetSlapAutoCapTimeouts(U32, U32, U32, U32);
TP_API long STDCALL TP_GetSlapAutoCapPrimaryFngrs(U32, U32*);
TP_API long STDCALL TP_SetSlapAutoCapPrimaryFngrs(U32, U32);
TP_API long STDCALL TP_GetMaxHandSize(U32, enum eHandSz *);
TP_API long STDCALL TP_SetMaxHandSize(U32, enum eHandSz);
TP_API long STDCALL TP_StartPreview(U32, enum eCaptureType, U32, U16, U16, TP_ImgCallbk, void *);
TP_API long STDCALL TP_StartPreviewDFC(U32, enum eCaptureType, U32, U32, struct s_imgSize*, TP_ImgCallbk, void *);
TP_API long STDCALL TP_CaptureControl(U32, enum eCapControl);
TP_API long STDCALL TP_GetImageQA(U32, struct s_imgQA *);
TP_API long	STDCALL TP_QuerySwitches(U32, S32, U32, U32, U32 Freq[3], U32 Time[3], U16*);
TP_API long STDCALL TP_GetSoftQualityResults(U32, struct s_softReconstructionResults *);
TP_API long STDCALL TP_GetRollReconstructionResults(U32, struct s_rollReconstructionResult *);

TP_API long STDCALL TP_GetSlapFngrBox(U32, struct s_MTOPS_Results *);
TP_API long STDCALL TP_FreeSlapFngrBox(struct s_MTOPS_Results *);
TP_API long STDCALL TP_SetParametersForLiveMode (U32, enum eMorphoTopSamplingModes);
TP_API long STDCALL TP_SetFFDSecurityLevel(U32, enum eFFD_Security_Level);
TP_API long STDCALL TP_GetSlapFFDResults(U32, struct s_FFD_Results *);
TP_API long STDCALL TP_FreeSlapFFDResults(U32, struct s_FFD_Results *);

TP_API int	STDCALL TP_GetLastError(void);

//Analyse IQS
TP_API long STDCALL TP_SetSlapEqualize(U32, U32);
TP_API long STDCALL TP_SetSlapFinalContrast(U32, U32);
TP_API long STDCALL TP_SetSlapLatent(U32, U32);
TP_API long STDCALL TP_WriteCameraEeprom(U32, U16, U8);
TP_API long STDCALL TP_ReadCameraEeprom(U32, U16, U8*);

#ifndef WIN32
TP_API void STDCALL  TP_GetIniFilename(char * filename);
// Mode Expert 
TP_API long   TP_GetValueFromIniFile(const char* i_filename, const char* i_section, const char* i_key, U32 i_defaultValue, U32 * i_value );
TP_API long   TP_SetValueFromIniFile(const char* i_filename, const char* i_section, const char* i_key, char* i_value );
TP_API void STDCALL TP_GetVersion(char * o_version);
#endif
//@}

/** @name Deprecated Function Prototypes
 */
//@{
TP_API long STDCALL StartPreview(enum eCaptureType capType, 
			unsigned long capMode, unsigned short videoW, unsigned short videoH,
         long (STDCALL *ImgCallbk)(int capState, 
			  unsigned char *ImageBuf, unsigned short Xsize, 
			  unsigned short Ysize));
TP_API long STDCALL StopPreview(void);
TP_API long STDCALL CaptureControl(enum eCapControl CapControl);
TP_API long STDCALL GetImageQA(struct s_imgQA *pParams);
TP_API long	STDCALL QuerySwitches(long Mode, 
         unsigned long WaitMsec, unsigned long Prompt, 
         unsigned long Freq[3], unsigned long Time[3], 
         unsigned short *SwitchState);
TP_API long STDCALL SetResolution(enum eCaptureType capType, unsigned int dpi);
TP_API long STDCALL GetResolution(enum eCaptureType capType, unsigned int *dpi);
TP_API long STDCALL GetMaxVideoSize(enum eCaptureType capType,
          unsigned short *videoW,unsigned short *videoH);
TP_API long STDCALL GetFinishedImageSize(enum eCaptureType capType, 
         unsigned int dpi,struct s_imgSize *imgSize);
TP_API long STDCALL SetDeviceUI(enum eUIType UIType , void *pUI);
TP_API long STDCALL GetDeviceUI(enum eUIType UIType , void *pUI);
TP_API long STDCALL IsDeviceUISupported(enum eUIType UIType, bool *result);
TP_API long STDCALL IsCaptureTypeSupported(enum eCaptureType capType, bool *result);
TP_API long STDCALL IsAutoCaptureSupported(enum eCaptureType capType, bool *result);
TP_API long STDCALL CalibrateScanner(enum eCaptureType capType);
TP_API long STDCALL CalibrateScannerWithCheck(enum eCaptureType capType);
TP_API long STDCALL IsCalibrated(enum eCaptureType capType, bool *result);
TP_API long STDCALL IsCalibrationSupported(enum eCaptureType capType, bool *result);
TP_API long STDCALL SendBulkData(enum eScnDataType dType, 
			unsigned char *buf, unsigned int len);
TP_API long STDCALL GetSlapFngrThresholds(struct s_fngrThlds *fngrThrld);
TP_API long STDCALL SetSlapFngrThresholds(struct s_fngrThlds *fngrThrld);
TP_API long STDCALL GetSlapAutoCapTimeouts(unsigned long *capDelay, 
         unsigned long *capTmOut, unsigned long *altCapTmOut);
TP_API long STDCALL SetSlapAutoCapTimeouts(unsigned long capDelay, 
         unsigned long capTmOut, unsigned long altCapTmOut);
TP_API long STDCALL GetSlapAutoCapPrimaryFngrs(unsigned long *numFngrs);
TP_API long STDCALL SetSlapAutoCapPrimaryFngrs(unsigned long numFngrs);
TP_API long STDCALL GetMaxHandSize(enum eHandSz *hndSz);
TP_API long STDCALL SetMaxHandSize(enum eHandSz hndSz);
TP_API long STDCALL GetDeviceInfo(struct s_devInfo *pInfo);
TP_API bool STDCALL DeviceConnected(void);
TP_API long STDCALL ResetDevice(void);
TP_API long STDCALL TerminateAPI(void);
TP_API long STDCALL InitializeAPI(void);
//@}

#endif // TPAPI_H

