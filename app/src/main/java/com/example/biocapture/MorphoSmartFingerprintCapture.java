package com.example.biocapture;

import com.morpho.smart.api.MorphoDevice;
import com.morpho.smart.api.eCaptureType;
import com.morpho.smart.api.eMlsError;
import com.morpho.smart.api.MorphoDevice;
import com.morpho.smart.api.MSO_Secu;

public class MorphoSmartFingerprintCapture {

    private MorphoDevice morphoDevice;

    public MorphoSmartFingerprintCapture() {
        morphoDevice = MorphoDevice.getInstance();
        morphoDevice.open();
    }

    public String[] captureTwoFingerprints() {
        // Start capturing two fingerprints
        morphoDevice.startCapture(eCaptureType.CTYPE_TWO_FINGERS, 0, false);

        // Wait for the capture process to finish
        while (!morphoDevice.isCaptureDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
                // Close the Morpho device and throw an eMlsError exception
                closeDevice();
                throw new eMlsError();
            }
        }

        // Get the captured fingerprint images
        String fingerprint1 = morphoDevice.getFingerprintImage(0);
        String fingerprint2 = morphoDevice.getFingerprintImage(1);

        return new String[]{fingerprint1, fingerprint2};
    }

    public void closeDevice() {
        morphoDevice.close();
    }
}
