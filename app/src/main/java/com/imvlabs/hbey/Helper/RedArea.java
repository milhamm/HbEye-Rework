package com.imvlabs.hbey.Helper;

public class RedArea {
    private boolean[][] pixelArea;
    private int imageArea;
    private int minX, maxX;
    private int minY, maxY;

    public RedArea() {
    }

    public RedArea(boolean[][] visitedArea, Coordinate start) {
        int width = visitedArea.length;
        int height = visitedArea[0].length;
        this.pixelArea = new boolean[width][height];
        this.imageArea = 0;
        startBounding(start);
    }

    public int[] getMin(){
        return new int[]{
                minX, minY
        };
    }

    public int[] getMax(){
        return new int[]{
                maxX, maxY
        };
    }

    private void startBounding(Coordinate coordinate){
        this.minX = coordinate.getX();
        this.maxX = coordinate.getX();
        this.minY = coordinate.getY();
        this.maxY = coordinate.getY();
    }

    private void setBounding(Coordinate coordinate){
        if (minX>coordinate.getX())
            this.minX = coordinate.getX();
        if (maxX<coordinate.getX())
            this.maxX = coordinate.getX();
        if (minY>coordinate.getY())
            this.minY = coordinate.getY();
        if (maxY<coordinate.getY())
            this.maxY = coordinate.getY();
    }

    public void putPixel(Coordinate coordinate){
        this.pixelArea[coordinate.getX()][coordinate.getY()] = true;
        setBounding(coordinate);
        this.imageArea ++;
    }

    public boolean[][] getPixelArea(){
        return pixelArea;
    }

    // return pixel size, image area
    public int getArea(){
        return imageArea;
    }
}
