package com.example.morpholivescan;

public enum eMlsError
{
    MLS_NOERROR                                     (0),    /**< Success */
    MLS_UNKNOWN_ERROR                               (-1),
    /** MorphoLiveScan Java library Error Codes */
    MLS_INIT_THREAD_START_FAILED                    (1000),
    MLS_INIT_THREAD_BAD_STATE                       (1001),
    MLS_INIT_FAILED                                 (1002),
    MLS_TERM_FAILED                                 (1003),
    MLS_OPEN_FAILED                                 (1004),
    MLS_CLOSE_FAILED                                (1005),
    MLS_START_CAPTURE_FAILED                        (1006),
    MLS_STOP_CAPTURE_FAILED                         (1007),
    MLS_SENSOR_NOT_FOUND                            (1008),
    MLS_RESETTING_SENSOR                            (1009),
    MLS_SENSOR_INVALID_STATE                        (1010),
    MLS_SENSOR_DISCONNECTED                         (1011),
    MLS_SENSOR_UNAVAILABLE                          (1012),
    MLS_INVALID_VIDEO_SIZE                          (1013),
    MLS_PERMISSION_DENIED                           (1014),
    /**< TPAPI Native MorphoLiveScan Error Codes */
    MLS_TPAPI_UNKNOWN                               (8000), /**< Unknown error */
    MLS_TPAPI_UNINITIALIZED                         (8001), /**< Library has not been successfully initialized */
    MLS_TPAPI_OUTOFRESOURCES                        (8002), /**< Memory or other OS resource allocation error */
    MLS_TPAPI_DEVICEUNAVAILABLE                     (8003), /**< Device not found on the bus */
    MLS_TPAPI_TOOMANYDEVICES                        (8004), /**< Too many devices on the bus */
    MLS_TPAPI_TIMEOUT                               (8005), /**< Timeout attempting to communicate with the device */
    MLS_TPAPI_PARAMETEROUTOFRANGE                   (8006), /**< Invalid value of one or several parameters */
    MLS_TPAPI_INVALIDSTATE                          (8007), /**< Command cannot be completed in the current capture state */
    MLS_TPAPI_ACCESSVIOLATION                       (8008), /**< Access violation */
    MLS_TPAPI_BADRESPONSE                           (8009), /**< Unexpected or invalid response to device command */
    MLS_TPAPI_BADFILE                               (8010), /**< Error writing equalization reference image */
    MLS_TPAPI_DFC_TOOMANYFINGERS                    (8011), /**< Too many fingers selected in dfcMap */
    MLS_TPAPI_DFC_NONPERMISSABLE_FINGER_COMBINATION (8012), /**< The thumb may not be selected in dfcMap if fingers from that same hand are selected */
    MLS_TPAPI_DIRTY_DEVICE                          (8013), /**< Device is too dirty */
    MLS_TPAPI_WARN_DIRTY_DEVICE	                    (8014), /**< Device is dirty ! Please clean the device and re-calibrate the device */
    MLS_TPAPI_ERR_DIRTY_DEVICE	                    (8015),
    MLS_TPAPI_RTQA_NOTSUPPORTED                     (8016), /**< RTQA's Libraries not supported */
    MLS_TPAPI_LISA_ERROR		                    (8017), /**< Processing error */
    MLS_TPAPI_NOTSUPPORTED		                    (8018), /**< In case of not supported API */
    MLS_TPAPI_DEVICE_BUSY                           (8019), /**< Device already open */
    MLS_TPAPI_NOT_ENOUGH_BANDWIDTH                  (8020), /**< Device plugged on Full speed port */
    MLS_TPAPI_DAMAGED_DEVICE	                    (8021), /**< Device is damaged, contact the support */
    MLS_TPAPI_FFD_ERROR			                    (8022), /**< Fake finger detection error */
    MLS_TPAPI_MAXERR                                (8023); /**< Maximum errors reached */

    private int value;

    private eMlsError(int value)
    {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }

    public static eMlsError fromInt(int value) {
        for (eMlsError error : eMlsError.values()) {
            if (error.getValue() == value) {
                return error;
            }
        }

        return MLS_UNKNOWN_ERROR;
    }
}
