package com.example.biocapture;

import static com.morpho.android.usb.USBManager.context;

import com.morpho.morphosmart.sdk.MorphoImage;

public class MorphoSmartFingerprintCapture {

    private com.idemia.morpholivescan.MorphoLiveScan morphoLiveScan;

    public MorphoSmartFingerprintCapture() {
        morphoLiveScan = new com.idemia.morpholivescan.MorphoLiveScan(context);
        morphoLiveScan.start();
    }

    public MorphoImage[] captureTwoFingerprints() {
        // Start capturing two fingerprints
        morphoLiveScan.startCapture(eCaptureType.CTYPE_TWO_FINGERS, 0, false);

        // Wait for the capture process to finish
        while (!morphoLiveScan.isCaptureDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Handle the interruption
                e.printStackTrace();
                // Close the Morpho device and throw an eMlsError exception
                closeDevice();
                throw new com.idemia.morpholivescan.eMlsError();
            }
        }

        // Get the captured fingerprint images
        MorphoImage fingerprint1 = morphoLiveScan.getFingerprintImage(0);
        MorphoImage fingerprint2 = morphoLiveScan.getFingerprintImage(1);

        return new MorphoImage[]{fingerprint1, fingerprint2};
    }

    public void closeDevice() {
        morphoLiveScan.destroy();
    }
}
