package com.example.morpholivescan;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import androidx.annotation.Keep;

import com.example.morpholivescan.CallbackMessage.eMsgType;
import static com.example.morpholivescan.CallbackMessage.eMsgType.*;
import static com.example.morpholivescan.eCaptureMode.*;
import static com.example.morpholivescan.eCaptureState.*;
import static com.example.morpholivescan.eMlsError.*;
import static com.example.morpholivescan.eMlsError.MLS_TPAPI_DAMAGED_DEVICE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.locks.ReentrantLock;

public class MorphoLiveScan extends Observable {
    private String TAG = "MorphoLiveScan";
    private static final String ACTION_USB_PERMISSION = "com.example.morpholivescan.USB_PERMISSION";

    private boolean g_initialized = false;
    private boolean g_wait_device_open = false;
    private boolean g_capturing = false;
    private boolean g_monitor_running = false;
    private boolean g_finger_box = false;

    // Sensor connection Status (0 = Disconnected, 1 = Connecting, 2 = Connected)
    private int g_connection_status = 0;

    private tpDevList[] mDevList = null;
    private tpDevList mWorkingDevice = null;
    private eCaptureState mCapState = CSTATE_IDLE;
    private Bitmap mDisplay = null;
    private Bitmap mSave = null;
    private Thread initSensorThread = null;
    private WakeLock wakeLock;
    private ReentrantLock initMutex = new ReentrantLock();
    private ReentrantLock captureMutex = new ReentrantLock();
    private Context mContext;
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mTopSlimSensorFound = false;
    private int example_VID = 0x225D;
    private int example_PID = 0xF019;

    static {
        System.loadLibrary("mls-jni");
    }

    private void getDeviceStatus(UsbDevice device) {
        mTopSlimSensorFound = false;

        if ((device.getVendorId() == example_VID) && (device.getProductId() == example_PID)) {
            try {
                UsbDeviceConnection connection = mUsbManager.openDevice(device);
                byte[] buffer = new byte[50];
                int indexOfDesc = 13; /* FirmwareVersion */
                int descLength = connection.controlTransfer(UsbConstants.USB_DIR_IN, 0x06,
                        (0x03 << 8) | indexOfDesc, 0x00, buffer, 50, 2000);

                if (descLength > 2) {
                    int pos = -1;
                    String fwVersionPrefix = "FW version - ";
                    String fw_version_str = new String(buffer, 2, descLength - 2, StandardCharsets.UTF_16LE);
                    Log.v(TAG, "Device SN: " + device.getSerialNumber() + ", FwVer: " + fw_version_str + "\n");

                    pos = fw_version_str.indexOf(fwVersionPrefix);
                    if (pos < 0) {
                        Log.e(TAG, "Unsupported firmware version");
                    } else {
                        String fwVersion = fw_version_str.substring(pos + fwVersionPrefix.length());
                        String fw[] = fwVersion.split("\\.");

                        if (fw.length == 3) {
                            try {
                                int fwMajor = Integer.valueOf(fw[0]);
                                int fwMinor = Integer.valueOf(fw[1]);

                                if (((fwMajor * 100) + fwMinor) >= 102) {
                                    mTopSlimSensorFound = true;
                                    Log.i(TAG, "Firmware version: " + fwVersion);
                                } else {
                                    Log.e(TAG, "Old firmware version: " + fwVersion + "(Should be > 1.02)");
                                }
                            } catch (NumberFormatException ex) {
                                Log.e(TAG, "Invalid firmware version format: " + fwVersion);
                            }
                        } else {
                            Log.e(TAG, "Invalid firmware version: " + fwVersion);
                        }
                    }
                }
                else {
                    Log.e(TAG, "Unsupported firmware version");
                }

                connection.close();

                if (mTopSlimSensorFound) {
                    if (g_initialized) {
                        connectSensor();
                    }
                    else {
                        eMlsError error = initSensor(false);

                        if (error != MLS_NOERROR) {
                            sendMessage(MSG_DEVICE_INIT_FAILED, error);
                            Log.e(TAG, "Device init failed (error = " + error + ") !");
                        }
                    }
                }
                else {
                    sendMessage(MSG_DEVICE_NOT_FOUND, null);
                }
            }
            catch (Exception e) {
                sendMessage(MSG_DEVICE_NOT_FOUND, null);
                Log.w(TAG, "Fingerprint sensor openDevice exception: " +  e.getMessage());
            }
        }
        else {
            sendMessage(MSG_DEVICE_NOT_FOUND, null);
            Log.w(TAG, "Found Unsupported Device: Vendor = " + device.getVendorId() +
                    ", ProductId = " + device.getProductId());
        }
    }

    /*
     * Receiver to catch user permission responses, which are required in order to actual
     * interact with a connected device.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            if (device != null) {
                if (device.getVendorId() == example_VID && device.getProductId() == example_PID) {
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Log.i(TAG, "permission granted for device: " + device.getDeviceName());
                            getDeviceStatus(device);
                        } else {
                            mTopSlimSensorFound = false;
                            Log.w(TAG, "permission denied for device " + device);
                            sendMessage(MSG_DEVICE_PERMISSION_DENIED, null);
                        }
                    } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                        mTopSlimSensorFound = false;
                        Log.i(TAG, "device " + device.getDeviceName() + " attached");
                        mUsbManager.requestPermission(device, mPermissionIntent);
                    } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                        mTopSlimSensorFound = false;
                        g_capturing = false;
                        updateConnectedStatus(0);
                        Log.i(TAG, "device " + device.getDeviceName() + " detached");
                    } else {
                        Log.w(TAG, "device " + device.getDeviceName() + " action '" + action + "' not handled !");
                    }
                }
                else {
                    Log.d(TAG, "USB event detected, but different VID.");
                }
            }
            else {
                Log.w(TAG, "Invalid device for action '" + action + "' !");
            }
        }
    };

    public MorphoLiveScan(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

        mContext = context;
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION),0);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        if (sensorMonitorThread.getState() == Thread.State.NEW) {
            try {
                g_monitor_running = true;
                sensorMonitorThread.start();
            }
            catch(IllegalThreadStateException e) {
                Log.e(TAG, "Device monitor thread start failed (State: " + sensorMonitorThread.getState() +  ") !");
            }
        }
        else {
            Log.e(TAG, "Device monitor thread bad state: " + initSensorThread.getState() +  " !");
        }
    }

    private void sendMessage(eMsgType type, Object data) {
        CallbackMessage cb = new CallbackMessage();
        cb.setMessageType(type);

        if (data != null) {
            cb.setMessage(data);
        }

        setChanged();
        notifyObservers(cb);
    }


    public void updateConnectedStatus(int status) {
        if (g_connection_status != status) {
            g_connection_status = status;
        }
        else {
            return;
        }

        if (status == 2) { /* Connected */
            devInfo info = new devInfo();

            if ((mWorkingDevice!= null) && (GetDeviceInfo(mWorkingDevice.getHandel(), info) == 0)) {
                sendMessage(MSG_DEVICE_CONNECTED, info.getAssy_model());
                Log.i(TAG, "== Fingerprint sensor (" + info.toString() + ") Connected ==");
            }
            else {
                sendMessage(MSG_DEVICE_CONNECTED, null);
                Log.i(TAG, "== Fingerprint sensor Connected ==");
            }
        }
        else if (status == 1) { /* Connecting */
            sendMessage(MSG_DEVICE_RECONNECTING, null);
        }
        else { /* Disconnected */
            sendMessage(MSG_DEVICE_DISCONNECTED, null);
            Log.i(TAG, "== Fingerprint sensor is Disconnected ==");
        }
    }

    private void updateResult(eCaptureState capState) {
        sendMessage(MSG_RESULT_UPDATE, capState);
    }

    private void updateImage(eCaptureState state) {
        if (mDisplay != null) {
            //Log.v(TAG, "==== updateImage (state = " + state + ") ====");
            sendMessage(MSG_UPDATE_IMAGE, mDisplay);
        }
        else {
            Log.w(TAG, "==== updateImage: Invalid Image bitmap ! ====");
        }
    }

    private Thread sensorMonitorThread = new Thread(new Runnable() {
        public void run() {
            while(g_monitor_running) {
                /*Log.v(TAG, "Sensor Monitor: Initialized = " + g_initialized + ", Open: " +
                        (mWorkingDevice != null) + ", Found = " + mTopSlimSensorFound);*/

                if (mTopSlimSensorFound && (mWorkingDevice != null) &&
                        !g_capturing && (g_connection_status != 2)) {
                    if (DeviceConnected(mWorkingDevice.getHandel())) {
                        updateConnectedStatus(2);
                    }
                    else {
                        updateConnectedStatus(1);
                    }
                }

                if (g_monitor_running) {
                    try {
                        sensorMonitorThread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        Log.w(TAG, "Sensor Monitor sleep interrupted !");
                    }
                }
            }
        }
    });

    public void start() {
        boolean deviceNotFound = true;
        boolean waitUsbPermission = false;
        HashMap<String, UsbDevice> devicelist = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devicelist.values().iterator();

        while (deviceIterator.hasNext()) {
            final UsbDevice device = deviceIterator.next();
            if ((device.getVendorId() == example_VID) && (device.getProductId() == example_PID)) {
                if (mUsbManager.hasPermission(device)) {
                    String productName = device.getProductName();
                    String serialNumber = device.getSerialNumber();

                    Log.i(TAG, "Found usb device with VID: 0x" + Integer.toHexString(device.getVendorId())
                            + " PID: 0x" + Integer.toHexString(device.getProductId()));

                    String sn[] = serialNumber.split("-");

                    if (sn.length == 2) {
                        Log.i(TAG, "Model: " + productName + " - " + sn[0]);
                        Log.i(TAG, "Serial Number: " + sn[1]);

                    } else {
                        Log.i(TAG, "Model: " + productName);
                        Log.i(TAG, "Serial Number: " + serialNumber);
                    }

                    deviceNotFound = false;
                    getDeviceStatus(device);
                    break;
                }
                else {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    waitUsbPermission = true;
                    Log.d(TAG, "Waiting for USB permission for fingerprint sensor ...");
                }
            }
        }

        if (deviceNotFound) {
            sendMessage(MSG_DEVICE_NOT_FOUND, null);

            if (!waitUsbPermission) {
                Log.w(TAG, "Sensor not detected: please check that PC is disconnected and that sensor is enabled.");
            }
        }
    }

    private eMlsError openSensor() {
        mWorkingDevice = null;
        mDevList = GetAndroidDeviceList();

        if((mDevList != null) && (mDevList.length > 0)) {
            long ret = -1;

            for(int i=0; i < mDevList.length; i++) {
                Log.v(TAG, "Opening Device: " + mDevList[i] + " ...");

                try {
                    ret = OpenDeviceSN(mDevList[i]);
                    if (ret == 0) {
                        mWorkingDevice = mDevList[i];
                        return MLS_NOERROR;
                    }
                    else if (g_initialized && ((ret == MLS_TPAPI_TOOMANYDEVICES.getValue()) ||
                            (ret == MLS_TPAPI_INVALIDSTATE.getValue()))) {
                        Log.w(TAG, "ReInitializing Fingerprint sensor (" + ret + ") ...");
                        resetSensor();
                        return MLS_RESETTING_SENSOR;
                    }
                    else if (ret == MLS_TPAPI_DAMAGED_DEVICE.getValue()) {
                        Log.e(TAG, "Fingerprint sensor is damaged !");
                    }
                    else {
                        Log.e(TAG, "Unable to open Fingerprint sensor (ret = " + ret + ") !");
                    }
                }
                catch(Exception e) {
                    ret = MLS_SENSOR_UNAVAILABLE.getValue();
                    Log.w(TAG, "Fingerprint sensor [" + i + "] open execption (" + e.getMessage() + ") !");
                }
            }

            if (ret == -1) {
                Log.w(TAG, "Fingerprint sensor not found (" + mDevList.length + ") !");
                return MLS_SENSOR_NOT_FOUND;
            }
            else {
                eMlsError error = eMlsError.fromInt((int)ret);

                if (error != MLS_UNKNOWN_ERROR) {
                    return error;
                }
                else {
                    Log.e(TAG, "Invalid Fingerprint sensor (ret = " + (int)ret + ") !");
                    return MLS_OPEN_FAILED;
                }
            }
        }
        else {
            if (mDevList == null) {
                Log.w(TAG, "Fingerprint sensor not found ! (mDevList is null)");
            }
            else {
                Log.w(TAG, "Fingerprint sensor not found ! (mDevList.length = "+ mDevList.length +" )");
            }

            return MLS_SENSOR_NOT_FOUND;
        }
    }

    private void connectSensor() {
        if (mWorkingDevice == null) {
            eMlsError error = openSensor();
            if ((error != MLS_NOERROR) && (error != MLS_RESETTING_SENSOR)) {
                sendMessage(MSG_DEVICE_OPEN_FAILED, error);
                Log.e(TAG, "Device Opening failed after Init (error = " + error + ") !");
            }
        }
        else {
            Log.d(TAG, "Device is already open");
        }
    }

    private eMlsError initSensor(boolean reset) {
        wait_init_finished();

        if (mWorkingDevice != null) {
            closeSensor();
        }

        if (reset) {
            eMlsError error;

            if (g_initialized) {
                error = terminateDevice();
                if (error != MLS_NOERROR) {
                    Log.w(TAG, "Reset: termination failed (error = " + error + ") !");
                }
            }
            else {
                Log.w(TAG, "Reset: Sensor was not Initialized !");
            }
        }

        initSensorThread = new Thread(new Runnable() {
            public void run() {
                if (!g_initialized) {
                    long ret = -1;

                    try {
                        Log.v(TAG, "Initializing Sensor ...");
                        initMutex.lock();
                        ret = InitializeAPI();
                    }
                    finally {
                        initMutex.unlock();

                        if (ret == 0) {
                            g_initialized = true;
                            Log.i(TAG, "Sensor Initialized");
                        }
                        else {
                            eMlsError error = eMlsError.fromInt((int)ret);

                            if (error != MLS_UNKNOWN_ERROR) {
                                Log.e(TAG, "Unable to Initialize fingerprint sensor (error = " + error + ")");
                            }
                            else {
                                Log.e(TAG, "Invalid Fingerprint sensor init (ret = " + (int)ret + ") !");
                                error = MLS_INIT_FAILED;
                            }

                            sendMessage(MSG_DEVICE_INIT_FAILED, error);
                            return;
                        }
                    }
                }

                if (g_wait_device_open) {
                    connectSensor();
                }
            }
        });

        if (initSensorThread.getState() == Thread.State.NEW) {
            try {
                g_wait_device_open = true;
                initSensorThread.start();
            }
            catch(IllegalThreadStateException e) {
                Log.e(TAG, "Device init thread start failed (State: " + initSensorThread.getState() +  ") !");
                return MLS_INIT_THREAD_START_FAILED;
            }
        }
        else {
            Log.e(TAG, "Device init thread bad state: " + initSensorThread.getState() +  " !");
            return MLS_INIT_THREAD_BAD_STATE;
        }

        return MLS_NOERROR;
    }

    private eMlsError terminateDevice() {
        long ret = 0;

        try {
            ret = TerminateAPI();
        }
        finally {
            if (ret == 0) {
                g_initialized = false;
                return MLS_NOERROR;
            }
            else  {
                eMlsError error = eMlsError.fromInt((int)ret);

                if (error != MLS_UNKNOWN_ERROR) {
                    Log.e(TAG, "Fingerprint sensor termination (error = " + error + ")");
                    return error;
                }
                else {
                    Log.e(TAG, "Invalid Fingerprint sensor termination (ret = " + (int)ret + ") !");
                    return MLS_TERM_FAILED;
                }
            }
        }
    }

    private void resetSensor() {
        eMlsError error;

        Log.v(TAG, "Resetting Sensor ...");
        error = initSensor(true);

        if (error != MLS_NOERROR) {
            sendMessage(MSG_DEVICE_INIT_FAILED, error);
            Log.e(TAG, "Reset: init failed (error = " + error +  ") !");
        }
    }

    public boolean waitInit() {
        return (mTopSlimSensorFound && !g_initialized);
    }

    private void closeSensor() {
        if (mWorkingDevice != null) {
            long ret;
            Log.v(TAG, "Closing Device: " + mWorkingDevice + " ...");

            ret = CloseDevice(mWorkingDevice.getHandel());
            if (ret == 0) {
                Log.d(TAG, "Device closed");
            }
            else {
                eMlsError error = eMlsError.fromInt((int)ret);

                if (error != MLS_UNKNOWN_ERROR) {
                    Log.e(TAG, "Unable to close Fingerprint sensor (error = " + error + ")");
                }
                else {
                    Log.e(TAG, "closing Fingerprint sensor failed (ret = " + (int)ret + ") !");
                    error = MLS_CLOSE_FAILED;
                }

                sendMessage(MSG_DEVICE_CLOSE_FAILED, error);
            }

            mWorkingDevice = null;
        }
    }

    public eMlsError stopCapture() {
        long ret = 0;
        eMlsError error = MLS_NOERROR;

        if (mWorkingDevice == null) {
            Log.w(TAG, "Cannot stop a capture on unopened fingerprint sensor !");
            return MLS_SENSOR_INVALID_STATE;
        }

        try {
            Log.v(TAG, "Stopping Capture ...");
            captureMutex.lock();

            ret = CaptureControl(mWorkingDevice.getHandel(), eCapControl.CC_CANCEL_CAPTURE);
            if (ret == 0) {
                g_capturing = false;
                Log.d(TAG, "Capture stopped");
            }
            else {
                error = eMlsError.fromInt((int) ret);

                if (error != MLS_UNKNOWN_ERROR) {
                    Log.e(TAG, "Unable to Stop Capture : (error = " + error + ")");
                }
                else {
                    Log.e(TAG, "Invalid Capture stop (ret = " + (int) ret + ") !");
                    error = MLS_STOP_CAPTURE_FAILED;
                }
            }
        }
        finally {
            captureMutex.unlock();
        }

        return error;
    }

    public void acceptCapture() {
        Log.v(TAG, "Accepting captured Fingerprint (state = " + mCapState + ") ...");

        if (mCapState == CSTATE_PREVIEW) {
            CaptureControl(mWorkingDevice.getHandel(), eCapControl.CC_CAPTURE_IMAGE);
        }
        else {
            Log.d(TAG, "Warning: capture state = " + mCapState);
        }
    }

    public void saveCapture(File file) {
        if (mSave != null) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                Log.v(TAG, "Saving Captured image in: "+ file + " ...");
                mSave.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Log.d(TAG, "Captured image Saved in: "+ file + " ...");
            }
            catch (FileNotFoundException e) {
                Log.e(TAG, "Cannot write in " + file + " " + e.getMessage());
                Log.e(TAG, "Cannot save Captured Image !");
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
                Log.e(TAG, "Error saving Captured Image !");
            }
        }
        else {
            Log.e(TAG, "No Captured image to save !");
        }
    }

    public eMlsError startCapture(eCaptureType capType, int capMode, boolean useFingerBox)
    {
        eMlsError error = MLS_NOERROR;

        try {
            captureMutex.lock();

            if (!g_capturing) {
                if (mWorkingDevice == null) {
                    Log.e(TAG, "Can not Start Capture on unopened fingerprint sensor !");
                    error = MLS_SENSOR_INVALID_STATE;
                }
                else if (DeviceConnected(mWorkingDevice.getHandel())) {
                    long ret;
                    imgSize videoSize = new imgSize();

                    g_finger_box = useFingerBox;

                    ret = GetMaxVideoSize(mWorkingDevice.getHandel(), capType, videoSize);
                    if (ret != 0) {
                        error = MLS_INVALID_VIDEO_SIZE;
                        Log.e(TAG, "Unable to Get Video Size : (ret = " + ret + ")");
                    }
                    else {
                        SetFingersBox(useFingerBox);

                        try {
                            Log.v(TAG, "Start Capture (Type = " + capType + ") ...");

                            ret = StartPreview(mWorkingDevice.getHandel(), capType, capMode, (short) videoSize.getX(), (short) videoSize.getY(), "changeImg");
                        }
                        finally {
                            if (ret == 0) {
                                if ((capMode & CMODE_AUTO_CAP.getValue()) != 0) {
                                    Log.d(TAG, "Automatic Capture Mode started");
                                }
                                else {
                                    Log.d(TAG, "Manual Capture Mode started");
                                }
                                g_capturing = true;
                                Log.d(TAG, "Capture Started");
                            }
                            else {
                                error = eMlsError.fromInt((int) ret);

                                if (error != MLS_UNKNOWN_ERROR) {
                                    Log.e(TAG, "Unable to Stop Capture : (error = " + error + ")");
                                }
                                else {
                                    Log.e(TAG, "Invalid Capture start (ret = " + (int) ret + ") !");
                                    error = MLS_START_CAPTURE_FAILED;
                                }
                            }
                        }
                    }
                }
                else {
                    Log.w(TAG, "Cannot Capture on a disconnected Device !");
                    updateConnectedStatus(0);
                    error = MLS_SENSOR_DISCONNECTED;
                }
            }
            else {
                Log.w(TAG,"Capture is already started (state  = " + mCapState + ") !");
                error = MLS_SENSOR_INVALID_STATE;
            }
        }
        finally {
            captureMutex.unlock();
        }

        return error;
    }

    private void wait_init_finished() {
        if (initSensorThread != null) {
            g_wait_device_open = false;

            try {
                Log.v(TAG, "Waiting for Sensor Initializing Thread to finish ...");
                initSensorThread.join(5000);
                Log.v(TAG, "Sensor Initializing Thread finished");
            }
            catch (InterruptedException e) {
                Log.w(TAG, "Cannot join Sensor Initializing Thread");
            }
            finally {
                initSensorThread = null;
            }
        }
    }

    public void destroy() {
        g_monitor_running = false;

        Log.v(TAG, "Destroying Sensor ...");

        wait_init_finished();

        if (mWorkingDevice != null) {
            closeSensor();
        }

        if (g_initialized) {
            terminateDevice();
        }
        else {
            Log.d(TAG, "Sensor was not Initialized");
        }

        try {
            mContext.unregisterReceiver(mUsbReceiver);
        }
        catch (IllegalArgumentException e) {
            Log.w(TAG, "Usb Receiver already unregistered !");
        }

        try {
            Log.v(TAG, "Waiting for Sensor Monitoring Thread to finish ...");
            sensorMonitorThread.join(5000);
            Log.v(TAG, "Sensor Monitoring Thread finished");
        }
        catch (InterruptedException e) {
            Log.w(TAG, "Cannot join Sensor Monitoring Thread");
        }

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Keep
    void ImageCallBack(byte img[], int Xsize, int Ysize, int CapState, byte original_img[]) {
        eCaptureState state = eCaptureState.fromInt(CapState);

        if (state != CSTATE_UNKNOWN) {
            mCapState = state;

            //Log.v(TAG, "ImageCallBack: CState = " + mCapState + " [" +Xsize + "x" + Ysize + "]");

            if ((Xsize > 0) && (Ysize > 0) && ((mCapState == CSTATE_PREVIEW) ||
                (mCapState == CSTATE_CAPTURED) || (mCapState == CSTATE_FINISHED))) {
                mDisplay = Bitmap.createBitmap(Xsize, Ysize, Bitmap.Config.ALPHA_8);
                mDisplay.copyPixelsFromBuffer(ByteBuffer.wrap(img));
            }

            switch (mCapState) {
                case CSTATE_IDLE:
                case CSTATE_STARTED:
                    updateResult(mCapState);
                    break;
                case CSTATE_PREVIEW:
                    if ((Xsize > 0) && (Ysize > 0)) {
                        updateImage(mCapState);
                    }
                    updateResult(mCapState);
                    break;
                case CSTATE_ABORTED:
                case CSTATE_FAILED:
                case CSTATE_REJECTED:
                case CSTATE_CANCELED:
                    if (g_capturing) {
                        stopCapture();
                    }
                    updateResult(mCapState);
                    break;
                case CSTATE_CAPTURED: {
                    if ((Xsize > 0) && (Ysize > 0)) {
                        updateImage(mCapState);
                    }
                    updateResult(mCapState);

                    break;
                }
                case CSTATE_FINISHED: {
                    if ((Xsize > 0) && (Ysize > 0)) {
                        updateImage(mCapState);
                    }

                    if (g_finger_box && (original_img != null)) {
                        if ((Xsize > 0) && (Ysize > 0)) {
                            for(int i =0 ; i < (Xsize * Ysize) ; i++) {
                                original_img[i] = (byte) (255 - original_img[i]);
                            }

                            mSave = Bitmap.createBitmap(Xsize, Ysize, Bitmap.Config.ALPHA_8);
                            mSave.copyPixelsFromBuffer(ByteBuffer.wrap(original_img));
                        }
                    }
                    else {
                        mSave = Bitmap.createBitmap(Xsize, Ysize, Bitmap.Config.ALPHA_8);
                        mSave.copyPixelsFromBuffer(ByteBuffer.wrap(img));
                    }

                    if (g_capturing) {
                        stopCapture();
                    }

                    updateResult(mCapState);
                    break;
                }
                default:
                    Log.w(TAG, "Unhandled Capture State = " + CapState);
                    break;
            }
        }
        else {
            sendMessage(MSG_INVALID_CAPTURE_STATE, Integer.valueOf(CapState));
            Log.w(TAG, "Invalid Capture State =" + CapState);
        }
    }

    public native void SetFingersBox(boolean fingersBox);
    public native long InitializeAPI();
    public native long TerminateAPI();
    public native tpDevList[] GetAndroidDeviceList();
    public native long OpenDeviceSN(tpDevList tpDevice);
    public native long CloseDevice(int hdl);
    public native boolean DeviceConnected(int hdl);
    public native long GetDeviceInfo(int hdl, devInfo pInfo);
    public native long GetMaxVideoSize(int hdl, eCaptureType capType, imgSize pVideoSize);
    public native long CaptureControl(int hdl, eCapControl capControl);
    public native long StartPreview(int hdl, eCaptureType capType, int jcapMode, short jvideoW, short jvideoH, String imgCallbk);
}
