package com.example.biocapture;

import android.app.Activity;
import android.util.Log;

import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.CustomInteger;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.File;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;



public class FingerprintSensor {
    private static final String TAG = "FingerprintSensor";
    private Activity activity;
    private CbmProcessObserver cbmProcessObserver;
    public FingerprintSensor(Activity activity, CbmProcessObserver cbmProcessObserver) {
        this.activity = activity;
        this.cbmProcessObserver = cbmProcessObserver;
    }

    public byte[][] captureFingerprints() {
        // Initialize the fingerprint sensor
        MorphoDevice morphoDevice = initMorphoDevice();

        // Initialize the template list
        TemplateList templateList = new TemplateList();

        // Capture first fingerprint
        int ret = captureFingerprint(morphoDevice, 1, templateList);

        // Capture second fingerprint
        ret = captureFingerprint(morphoDevice, 2, templateList);

        // Get the templates
        int nbTemplate = templateList.getNbTemplate();
        if (nbTemplate == 2) {
            // Save the templates to files
            return new byte[][]{templateList.getTemplate(0).getData(), templateList.getTemplate(1).getData()};
        }

        return null;
    }

    private int captureFingerprint(MorphoDevice morphoDevice, int fingerNumber, TemplateList templateList) {
        // Set the parameters for capture
        int timeout = 30;
        final int acquisitionThreshold = 0;
        int advancedSecurityLevelsRequired = 0;
        TemplateType templateType = TemplateType.MORPHO_PK_ISO_FMR;
        TemplateFVPType templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;
        int maxSizeTemplate = 512;
        EnrollmentType enrollType = EnrollmentType.ONE_ACQUISITIONS;
        LatentDetection latentDetection = LatentDetection.LATENT_DETECT_ENABLE;
        Coder coderChoice = Coder.MORPHO_DEFAULT_CODER;
        int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue()
                | DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
        // Define the messages sent through the callback
        int callbackCmd = CallbackMask.MORPHO_CALLBACK_COMMAND_CMD.getValue()
                | CallbackMask.MORPHO_CALLBACK_IMAGE_CMD.getValue()
                | CallbackMask.MORPHO_CALLBACK_CODEQUALITY.getValue()
                | CallbackMask.MORPHO_CALLBACK_DETECTQUALITY.getValue();
        // Capture fingerprint
        return morphoDevice.capture(timeout, acquisitionThreshold, advancedSecurityLevelsRequired, fingerNumber,
                templateType, templateFVPType, maxSizeTemplate, enrollType, latentDetection, coderChoice,
                detectModeChoice, CompressionAlgorithm.MORPHO_NO_COMPRESS, 0, templateList, callbackCmd, null);
    }


    private String[] saveTemplatesToFile(TemplateList templateList) {
        // Save the templates to files
        FileOutputStream fos1 = null, fos2 = null;
        try {
            File file1 = createTemplateFile(1, templateList.getTemplate(0).getTemplateType());
            File file2 = createTemplateFile(2, templateList.getTemplate(1).getTemplateType());
            fos1 = new FileOutputStream(file1);
            fos2 = new FileOutputStream(file2);
            fos1.write(templateList.getTemplate(0).getData());
            fos2.write(templateList.getTemplate(1).getData());
            fos1.close();
            fos2.close();

            // Return the file names
            return new String[]{file1.getName(), file2.getName()};
        } catch (Exception e) {
            Log.e(TAG, "FileOutputStream : " + e.getMessage());
        }

        return null;
    }

    private File createTemplateFile(int fingerNumber, TemplateType templateType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateAndTime = sdf.format(new Date());
        String fileName = "TemplateFP_" + currentDateAndTime + "_" + System.currentTimeMillis() +
                "_" + fingerNumber + templateType.getExtension();
        return new File(activity.getExternalFilesDir(null), fileName);
    }

    private MorphoDevice initMorphoDevice() {
        int ret = 0;
        Log.d(TAG, "initMorphoDevice");
        // On Morphotablet, 3rd parameter (enableWakeLock) must always be true
        USBManager.getInstance().initialize(activity, "com.morpho.morphosample.USB_ACTION", true);
        MorphoDevice md = new MorphoDevice();
        CustomInteger nbUsbDevice = new CustomInteger();
        ret = md.initUsbDevicesNameEnum(nbUsbDevice);
        if (ret == ErrorCodes.MORPHO_OK) {
            if (nbUsbDevice.getValueOf() != 1) {
                // Fingerprint sensor was not initialized properly
                // Check that it is enabled
                // USB mode should be set to "Host"
                // Or it should be set to "Auto" and PC shouldn't be connected to tablet
                // Also check that no other fingerprint sensor is connected to tablet
            } else {
                String sensorName = md.getUsbDeviceName(0); // We use the first CBM found
                ret = md.openUsbDevice(sensorName, 0);
                if (ret != ErrorCodes.MORPHO_OK) {
                    // Error opening USB device
                }
            }
        } else {
            // Error initializing USB device
        }
        return md;
    }

    public void setCbmProcessObserver(CbmProcessObserver cbmProcessObserver) {
        this.cbmProcessObserver = cbmProcessObserver;
    }
}
