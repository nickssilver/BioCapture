package com.idemia.morpholivescan;

public final class CallbackMessage {
    public enum eMsgType {
        MSG_INVALID_CAPTURE_STATE,
        MSG_DEVICE_INIT_FAILED,
        MSG_DEVICE_TERM_FAILED,
        MSG_DEVICE_CONNECTED,
        MSG_DEVICE_DISCONNECTED,
        MSG_DEVICE_RECONNECTING,
        MSG_DEVICE_NOT_FOUND,
        MSG_DEVICE_PERMISSION_DENIED,
        MSG_DEVICE_OPEN_FAILED,
        MSG_DEVICE_CLOSE_FAILED,
        MSG_RESULT_UPDATE,
        MSG_UPDATE_IMAGE,
        MSG_DEFAULT,
    }

    private eMsgType messageType;
    private Object message;

    public CallbackMessage() {
        messageType = eMsgType.MSG_DEFAULT;
        message = null;
    }

    public eMsgType getMessageType() {
        return this.messageType;
    }

    public void setMessageType(eMsgType messageType) {
        this.messageType = messageType;
    }

    public Object getMessage() {
        return this.message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
