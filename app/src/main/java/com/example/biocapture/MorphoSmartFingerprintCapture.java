package com.example.biocapture;

import com.example.morpholivescan.MorphoLiveScan;
import com.example.morpholivescan.eCaptureType;
import com.morpho.morphosmart.sdk.MorphoImage;

/**
 * A class for capturing two fingerprints one at a time using the MorphoLiveScan SDK.
 */
public class MorphoSmartFingerprintCapture {

    private MorphoLiveScan morphoLiveScan;
    private CaptureObserver observer;

    /**
     * Constructor for creating a MorphoSmartFingerprintCapture object.
     * @param morphoLiveScan The MorphoLiveScan object to use for capturing fingerprints.
     */
    public MorphoSmartFingerprintCapture(MorphoLiveScan morphoLiveScan) {
        this.morphoLiveScan = morphoLiveScan;
    }

    /**
     * Registers an observer to receive notifications about capture events.
     * @param observer The observer to register.
     */
    public void registerObserver(CaptureObserver observer) {
        this.observer = observer;
    }

    /**
     * Captures two fingerprints one at a time.
     */
    public void captureTwoFingerprints() {
        try {
            // Capture the first fingerprint
            morphoLiveScan.startCapture(eCaptureType.CTYPE_SLAP_ONE, 0, false);
            observer.onCaptureStart();

            // Wait for the capture process to finish
            while (!isCaptureDone()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Handle the interruption
                    e.printStackTrace();
                    // Close the Morpho device and call onCaptureFailure with the exception
                    morphoLiveScan.destroy();
                    observer.onCaptureFailure(e);
                    return;
                }
            }

            // Retrieve the captured fingerprint image
            MorphoImage fingerprint1 = getFingerprintImage(0);

            // Capture the second fingerprint
            morphoLiveScan.startCapture(eCaptureType.CTYPE_SLAP_ONE, 1, false);
            observer.onCaptureStart();

            // Wait for the capture process to finish
            while (!isCaptureDone()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Handle the interruption
                    e.printStackTrace();
                    // Close the Morpho device and call onCaptureFailure with the exception
                    morphoLiveScan.destroy();
                    observer.onCaptureFailure(e);
                    return;
                }
            }

            // Retrieve the captured fingerprint image
            MorphoImage fingerprint2 = getFingerprintImage(1);

            // Notify the observer that the capture is complete
            observer.onCaptureComplete(new MorphoImage[]{fingerprint1, fingerprint2});
        } catch (Throwable e) {
            // Call onCaptureFailure with the exception
            observer.onCaptureFailure(e);
        }
    }

    /**
     * Checks if the capture process is done.
     * @return True if the capture process is done, false otherwise.
     */
    private boolean isCaptureDone() {
        if (morphoLiveScan.isCaptureCompleted(0) && morphoLiveScan.isCaptureCompleted(1)) {
            return true;
        }
        return false;
    }

    private MorphoImage getFingerprintImage(int index) {
        if (index < 0 || index > 1) {
            throw new IllegalArgumentException("Invalid index for fingerprint image: " + index);
        }
        return morphoLiveScan.getFingerprintImage(index);
    }


    /**
     * Interface for observing capture events.
     */
    public interface CaptureObserver {
        void onCaptureStart();
        void onCaptureComplete(MorphoImage[] fingerprints);
        void onCaptureFailure(Throwable e);

        void onCaptureFailure(Exception e);
    }
}
