package com.example.biocapture;

import static com.morpho.morphosmart.sdk.DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.morpholivescan.MorphoLiveScan;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.example.biocapture.CbmProcessObserver;
import com.morpho.morphosmart.sdk.CustomInteger;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MatchingStrategy;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

public class MorphoSmartFingerprintCapture {

    private MorphoDevice morphoDevice;
    private CbmProcessObserver observer;
    private boolean capturing = false;
    private int fingerprintCount = 0;

    public MorphoSmartFingerprintCapture(Context context) {
        // Initialize USB device
        initializeMorphoDevice(context);
    }

    public void registerObserver(CbmProcessObserver observer) {
        this.observer = observer;
    }

    public void captureFingerprints() {
        if (!capturing) {
            capturing = true;
            observer.onCaptureStart();

            // Start capturing the first fingerprint
            fingerprintCount = 0;
            performCapture();

        } else {
            capturing = false;
            observer.onCaptureFailure(new Exception("Capture process is already in progress."));
        }
    }

    private void initializeMorphoDevice(Context context) {
        try {
            // Initialize MorphoDevice using the provided context
            USBManager.getInstance().initialize(context, "com.morpho.morphosample.USB_ACTION", true);
            morphoDevice = new MorphoDevice();
            CustomInteger nbUsbDevice = new CustomInteger();

            int ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);
            if (ret == MorphoDevice.MORPHO_OK && nbUsbDevice.getValueOf() > 0) {
                String sensorName = morphoDevice.getUsbDeviceName(0);
                ret = morphoDevice.openUsbDevice(sensorName, 0);

                if (ret != MorphoDevice.MORPHO_OK) {
                    showToast(context, "Error opening USB device");
                }
            } else {
                showToast(context, "No USB devices found");
            }
        } catch (Exception e) {
            showToast(context, "Error initializing MorphoDevice: " + e.getMessage());
        }
    }

    private void performCapture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Define capture parameters
                    int timeout = 30;
                    int acquisitionThreshold = 0;
                    int advancedSecurityLevelsRequired = 0;
                    int fingerNumber = fingerprintCount + 1;
                    TemplateType templateType = TemplateType.MORPHO_PK_ISO_FMR;
                    TemplateFVPType templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;
                    int maxSizeTemplate = 512;
                    EnrollmentType enrollType = EnrollmentType.ONE_ACQUISITIONS;
                    LatentDetection latentDetection = LatentDetection.LATENT_DETECT_ENABLE;
                    DetectionMode detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE
                            | MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE;
                    TemplateList templateList = new TemplateList();

                    // Define the messages sent through the callback
                    int callbackCmd = CallbackMask.MORPHO_CALLBACK_COMMAND_CMD
                            | CallbackMask.MORPHO_CALLBACK_IMAGE_CMD
                            | CallbackMask.MORPHO_CALLBACK_CODEQUALITY
                            | CallbackMask.MORPHO_CALLBACK_DETECTQUALITY;

                    // Perform capture
                    int ret = morphoDevice.capture(timeout, acquisitionThreshold, advancedSecurityLevelsRequired,
                            fingerNumber, templateType, templateFVPType, maxSizeTemplate, enrollType,
                            latentDetection, detectModeChoice.getValue(), callbackCmd, templateList,
                            observer);

                    if (ret == MorphoDevice.MORPHO_OK) {
                        // Capture successful
                        observer.onCaptureComplete(templateList.getTemplates());

                        // Increment the fingerprint count and capture the next fingerprint
                        fingerprintCount++;
                        if (fingerprintCount < 2) {
                            performCapture();
                        } else {
                            // Capturing both fingerprints is complete
                            capturing = false;
                        }
                    } else {
                        // Capture failure
                        observer.onCaptureFailure(new Exception("Capture failed with error code: " + ret));
                        capturing = false;
                    }
                } catch (Exception e) {
                    observer.onCaptureFailure(e);
                    capturing = false;
                }
            }
        }).start();
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
