package com.example.morpholivescan;

public enum eCapControl {

    CC_CANCEL_CAPTURE		(0x0000000),	/**< Abort the capture process		 */
    CC_CAPTURE_IMAGE		(0x0000001),	/**< Trigger an image capture (SCAN) */
    CC_FINISH_IMAGE			(0x0000002),	/**< Create a finished image (SAVE)	 */
    CC_END_ROLL			    (0x0000003);	/**< End the roll capture			 */

    private int value;

    private eCapControl(int value)
    {
        this.value = value;
    }
    public int getValue() { return this.value; }

}
