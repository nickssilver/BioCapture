package com.idemia.morpholivescan;

public class MTOPSResultsBox {

    private int statusfinger;
    private int boxWidth;
    private int boxHeight;
    private int boxStartingLine;
    private int boxStartingColumn;
    private float boxOrientation;

    public MTOPSResultsBox()
    {
        this.statusfinger = 0;
        this.boxWidth = 0;
        this.boxHeight = 0;
        this.boxStartingLine = 0;
        this.boxStartingColumn = 0;
        this.boxOrientation = 0;
    }

    public MTOPSResultsBox(int statusfinger, int boxWidth, int boxHeight, int boxStartingLine, int boxStartingColumn, float boxOrientation)
    {
        this.statusfinger = statusfinger;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.boxStartingLine = boxStartingLine;
        this.boxStartingColumn = boxStartingColumn;
        this.boxOrientation = boxOrientation;
    }

    public int getStatusfinger() { return statusfinger; }

    public int getBoxWidth() { return boxWidth; }

    public int getBoxHeight() { return boxHeight; }

    public int getBoxStartingLine() { return boxStartingLine; }

    public int getBoxStartingColumn() { return boxStartingColumn; }

    public float getBoxOrientation() { return boxOrientation; }

    public void setStatusfinger(int statusfinger) { this.statusfinger = statusfinger; }

    public void setBoxWidth(int boxWidth) { this.boxWidth = boxWidth; }

    public void setBoxHeight(int boxHeight) { this.boxHeight = boxHeight; }

    public void setBoxStartingLine(int boxStartingLine) { this.boxStartingLine = boxStartingLine; }

    public void setBoxStartingColumn(int boxStartingColumn) { this.boxStartingColumn = boxStartingColumn; }

    public void setBoxOrientation(float boxOrientation) { this.boxOrientation = boxOrientation; }
}
