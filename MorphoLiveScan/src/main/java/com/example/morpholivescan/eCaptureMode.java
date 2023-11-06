package com.example.morpholivescan;

/**
 *  Capture Modes
 *  See: "tpapi.h" header for full description of each Mode
 */

public enum eCaptureMode {
    CMODE_AUTO_CAP_WITH_MOTION		(0x00000002), /**< Enable user motion initiated Auto-capture with Preview mode. */
    CMODE_PREVIEW_INFO				(0x00000400), /**< Generate info on preview. */
    CMODE_REMOVE_FINGER				(0x00000800), /**< Remove Finger mode */
    CMODE_RTQA						(0x00002000), /**< Enable RTQA and create a decorated image in the device. */
    CMODE_REVIEW					(0x00008000), /**< Suspends the capture process following retrieval of the image quality */
    CMODE_REPORT_UI					(0x01000000), /**< The device will report state changes of the user interface LED’s */
    CMODE_AUTO_CAP					(0x00800000), /**< Enable Ink-like Auto-capture mode. */
    CMODE_BEEP_MODE					(0x02000000), /**< Specific device beep sequence and tone configuration for customer */
    CMODE_AUTO_CAP_WITH_PREVIEW		(0x04000000), /**< Enable device initiated Auto-capture with Preview mode. */
    CMODE_DISABLE_KEYS				(0x08000000), /**< Disable the device’s SCAN/SAVE buttons (if available) */
    CMODE_REPORT_KEYS				(0x20000000), /**< Prevent the device’s SCAN/SAVE buttons from advancing the capture process. */
    CMODE_CLEAN_BACKGROUND			(0x40000000), /**< An extra image processing step will be performed on the finished image. */
    CMODE_DISABLE_BEEP				(0x80000000), /**< Disable device-controlled beeps during a capture operation. */
    CMODE_FAST_ENROLL				(0x00010000), /**< Fast mode detection. Recommended for authentication. */
    CMODE_MEDIUM_ENROLL 			(0x00020000), /**< Medium mode detection (default one). Recommended for enroll. */
    CMODE_SLOW_ENROLL				(0x00030000); /**< Slow mode detection. Recommended for enroll person with dry fingers. */

    private int value;

    private eCaptureMode(int value)
    {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
