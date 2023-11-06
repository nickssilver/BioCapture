package com.example.morpholivescan;

public enum eCaptureState {
    CSTATE_UNKNOWN                  (-1),

    CSTATE_IDLE                     (0x00000000), /**< Idle State */
    CSTATE_PREVIEW                  (0x00000001), /**< Preview image is available */
    CSTATE_STARTED                  (0x00000002), /**< SCAN button pressed, capture begun */
    CSTATE_ABORTED                  (0x00000003), /**< SAVE button pressed during Preview */
    CSTATE_CAPTURED                 (0x00000004), /**< Image captured, decorated image may be available */
    CSTATE_FINISHED                 (0x00000005), /**< Capture complete, finished image available */
    CSTATE_REJECTED                 (0x00000006), /**< SCAN button pressed during Review */
    CSTATE_CANCELED                 (0x00000007), /**< Capture stopped by host */
    CSTATE_FAILED                   (0x00000008), /**< Capture failed due to hardware or system errors */
    CSTATE_TOOFAST                  (0x00000009), /**< Handprint rolled too fast - capture refused */
    CSTATE_SCANPRESS                (0x00000010), /**< SCAN button pressed during capture using CMODE_REPORT_KEYS */
    CSTATE_SAVEPRESS                (0x00000011), /**< SAVE button pressed during capture using CMODE_REPORT_KEYS */
    CSTATE_UI_CHANGED               (0x00000012), /**< Device UI has changed during capture using CMODE_REPORT_UI */
    CSTATE_FINISHED_WARN_ELEC_ISSUE (0x00000013), /**< Electrical issue: please verify electrical environment and the ground connection of devices*/
    CSTATE_ABORTED_FINGER           (0x00000014); /**< Finger detected in the first frame of the capture, fingers must be removed \ref CMODE_REMOVE_FINGER */

    private final int value;

    eCaptureState(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static eCaptureState fromInt(int value) {
        for (eCaptureState state : eCaptureState.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }

        return CSTATE_UNKNOWN;
    }
}
