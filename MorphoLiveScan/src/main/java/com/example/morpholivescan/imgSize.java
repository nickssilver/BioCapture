package com.example.morpholivescan;

public class imgSize {

    private int x;  /**< Image width */
    private int y;  /**< Image height */

    public imgSize() {
        this.x = 0;
        this.y = 0;
    }

    public imgSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public imgSize(int x, int y, short Img[]) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "imgSize{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
