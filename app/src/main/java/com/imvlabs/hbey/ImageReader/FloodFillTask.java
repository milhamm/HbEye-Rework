package com.imvlabs.hbey.ImageReader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.imvlabs.hbey.Helper.Coordinate;
import com.imvlabs.hbey.Helper.RedArea;

import java.util.ArrayDeque;
import java.util.ArrayList;


/**
 * Created by maakbar on 11/18/15.
 */
public class FloodFillTask extends AsyncTask<Bitmap, Void, Bitmap>{
    // default red color acceptance
    private double acceptanceRatio = 0.298;

    public FloodFillTask() {
    }

    public FloodFillTask(double acceptanceRatio) {
        this.acceptanceRatio = acceptanceRatio;
    }

    private float[] getHSVPixel(int pixel){
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);

        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        return hsv;
    }

    private boolean isPixelRed(float[] pixel){
        double normalizedH = pixel[0]/360.0;
        if ((1-normalizedH)<acceptanceRatio)
            return true;
        return false;
    }

    private RedArea markRedArea(Bitmap image, boolean[][] visitedArr, Coordinate start){
        RedArea area = new RedArea(visitedArr, start);
        ArrayDeque<Coordinate> arrayDeque = new ArrayDeque<>();
        arrayDeque.push(start);
        while (!arrayDeque.isEmpty()){
            Coordinate current = arrayDeque.pop();
            if(!isVisited(visitedArr, current)) {
                area.putPixel(current);
                markVisited(visitedArr, current);
            }

            /* o is still available, x is not need to check
             * from an image:
             *  xxx
             *  o.o
             *  ooo
             */
            int[][] directions = {
                    {-1, 0}, // left
                    {-1, 1}, // botLeft
                    { 0, 1}, // bottom
                    { 1, 1}, // botRight
                    { 1, 0}, // botRight
            };
            for (int[] pair: directions) {
                int newX = current.getX()+pair[0];
                int newY = current.getY()+pair[1];
                try {
                    int pixel = image.getPixel(newX, newY);
                    if (isPixelRed(getHSVPixel(pixel))) {
                        arrayDeque.push(new Coordinate(newX, newY));
                    }
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }
        return area;
    }

    private void markVisited(boolean[][] visitedArr, Coordinate coor){
        visitedArr[coor.getX()][coor.getY()] = true;
    }

    private boolean isVisited(boolean[][] visitedArray, Coordinate coordinate){
        return visitedArray[coordinate.getX()][coordinate.getY()];
    }

    private Bitmap copyCroppedImage(Bitmap image, RedArea area){
        Bitmap copiedImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        for (int x = area.getMin()[0]; x <= area.getMax()[0]; x++){
            for (int y = area.getMin()[1]; x <= area.getMax()[1]; y++){
                boolean[][] isColored = area.getPixelArea();
                if (isColored[x][y]){
                    copiedImage.setPixel(x,y,image.getPixel(x,y));
                }
            }
        }
        return copiedImage;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap image = params[0];

        int height =  image.getHeight();
        int width =  image.getWidth();

        boolean[][] visitedArr = new boolean[width][height];

        Coordinate coor = new Coordinate();
        ArrayList<RedArea> areas = new ArrayList<>();
        while (coor.getX()<width){
            while (coor.getY()<height){
                if(!isVisited(visitedArr, coor)){
                    int pixel = image.getPixel(coor.getX(), coor.getY());
                    float[] hsvPixel = getHSVPixel(pixel);
                    if (isPixelRed(hsvPixel))
                        areas.add(markRedArea(image, visitedArr,
                                new Coordinate(coor.getX(), coor.getY())));
                    markVisited(visitedArr, coor);
                }
                coor.addX(1);
            }
            coor.addX(1);
        }

        RedArea selected = new RedArea();
        // iterate via "for loop"
        for (int i = 0; i < areas.size(); i++) {
            if (i==0 || selected.getArea()<areas.get(i).getArea())
                selected = areas.get(i);
        }

        width = selected.getMax()[0] - selected.getMin()[0];
        height = selected.getMax()[1] - selected.getMin()[1];

        Bitmap copied = copyCroppedImage(image, selected);
        Bitmap cropped = Bitmap.createBitmap(copied, selected.getMin()[0], selected.getMin()[1], width, height);

        return cropped;
    }
}
