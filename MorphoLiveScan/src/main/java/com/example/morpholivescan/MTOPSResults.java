package com.idemia.morpholivescan;

public class MTOPSResults {

    private int statusglobaldtpr;
    private int nbFingDetected;
    private MTOPSResultsBox Box;
    private int imgStride;
    private int imgWidth;
    private int imgHeight;

    public MTOPSResults()
    {
        this.statusglobaldtpr = 0;
        this.nbFingDetected = 0;
        this.Box = null;
        this.imgStride = 0;
        this.imgWidth = 0;
        this.imgHeight = 0;
    }

    public MTOPSResults(int statusglobaldtpr, int nbFingDetected, MTOPSResultsBox Box, int imgStride, int imgWidth, int imgHeight)
    {
        this.statusglobaldtpr = statusglobaldtpr;
        this.nbFingDetected = nbFingDetected;
        this.Box = Box;
        this.imgStride = imgStride;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public int getStatusglobaldtpr() { return statusglobaldtpr; }

    public int getNbFingDetected() { return nbFingDetected; }

    public MTOPSResultsBox getBox() { return Box; }

    public int getImgStride() { return imgStride; }

    public int getImgWidth() { return imgWidth; }

    public int getImgHeight() { return imgHeight; }

    public void setStatusglobaldtpr(int statusglobaldtpr) { this.statusglobaldtpr = statusglobaldtpr; }

    public void setNbFingDetected(int nbFingDetected) { this.nbFingDetected = nbFingDetected; }

    public void setBox(MTOPSResultsBox box) { Box = box; }

    public void setImgStride(int imgStride) { this.imgStride = imgStride; }

    public void setImgWidth(int imgWidth) { this.imgWidth = imgWidth; }

    public void setImgHeight(int imgHeight) { this.imgHeight = imgHeight; }
}
