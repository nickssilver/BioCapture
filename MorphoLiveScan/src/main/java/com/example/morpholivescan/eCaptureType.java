package com.idemia.morpholivescan;

public enum eCaptureType
{
    CTYPE_ROLL               (0x00),		/**< Roll type					*/
    CTYPE_SLAP_ONE           (0x01),		/**< One finger slap capture	*/
    CTYPE_SLAP_FOUR          (0x02),		/**< Four fingers slap capture	*/
    CTYPE_HAND               (0x03),		/**< Hand-print					*/
    CTYPE_SLAP_LFOUR         (0x04),		/**< Left four-finger slap		*/
    CTYPE_SLAP_RFOUR         (0x05),		/**< Right four-finger slap		*/
    CTYPE_SLAP_THUMBS        (0x06),		/**< Both thumbs slap			*/

    CTYPE_SLAP_LTHUMB        (0x07),		/**< Left Thumb slap (plain)	*/
    CTYPE_SLAP_RTHUMB        (0x08),		/**< Right Thumb slap (plain)	*/

    CTYPE_ROLL_LTHUMB        (0x09),		/**< Left Thumb roll			*/
    CTYPE_ROLL_LINDEX        (0x0A),		/**< Left Index finger roll		*/
    CTYPE_ROLL_LMIDDLE       (0x0B),		/**< Left Middle finger roll	*/
    CTYPE_ROLL_LRING         (0x0C),		/**< Left Ring finger roll		*/
    CTYPE_ROLL_LLITTLE       (0x0D),		/**< Left Little finger roll	*/
    CTYPE_ROLL_RTHUMB        (0x0E),		/**< Right Thumb roll			*/
    CTYPE_ROLL_RINDEX        (0x0F),		/**< Right Index finger roll	*/
    CTYPE_ROLL_RMIDDLE       (0x10),		/**< Right Middle finger roll	*/
    CTYPE_ROLL_RRING         (0x11),		/**< Right Ring finger roll		*/
    CTYPE_ROLL_RLITTLE       (0x12),		/**< Right Little finger roll	*/

    CTYPE_SLAP_PALM          (0x13),		/**< Palm-print					*/
    CTYPE_SLAP_LPALM         (0x13),		/**< Left palm-print			*/
    CTYPE_SLAP_RPALM         (0x14),		/**< Right palm-print			*/
    CTYPE_SLAP_LWRITERS_PALM (0x15),		/**< Left writer’s palm-print	*/
    CTYPE_SLAP_RWRITERS_PALM (0x16),		/**< Right writer’s palm-print  */

    CTYPE_ID1CARD_GENERIC	(0x17),		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
    CTYPE_ID1CARD_OCR		(0x18),		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
    CTYPE_ID1CARD_MRZ		(0x19),		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */
    CTYPE_ID1CARD_BARCODE	(0x1A);		/**< ID1 Card reader type. Only for MorphoTopSlim Device  */

    private int value;

    private eCaptureType(int value)
    {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
