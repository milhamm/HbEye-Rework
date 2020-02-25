package com.imvlabs.hbey.ImageReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;


/**
 * Asynctask to do calculations
 */
public class CalculateTask extends AsyncTask<Bitmap, Void, CalculateTask.Result> {
    private Context context;
    private OnImageCalculatedListener listener;
    private ProgressDialog dialog;
    private boolean isMale;
    private int direction;

    public CalculateTask(Context context, boolean isMale, OnImageCalculatedListener listener, int direction) {
        this.context = context;
        this.isMale = isMale;
        this.listener = listener;
        this.direction = direction;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setMessage("Calculating");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected Result doInBackground(Bitmap... params) {
        Bitmap image = params[0];
        double threshold = 255;
        double normalizeThres = threshold/360;

        double totalR = 0;
        double totalG = 0;
        double totalB = 0;
        int totalArea = 0;
        /*double totalH = 0;
        double totalS = 0;
        int totalArea = 0;*/

//        int[] pixels = new int[image.getHeight()*image.getHeight()];
//        image.getPixels(pixels, 0, image.getWidth(), 0,0, image.getWidth(), image.getHeight());

        /*for (int x=0; x<image.getWidth(); x++){
            for (int y=0; y<image.getHeight(); y++){
                int pixel = image.getPixel(x,y);
                float[] hsvPixel = getHSVcolor(pixel);
                double normalizeH = hsvPixel[0]/360;
                if (hsvPixel[0]>normalizeThres){
                    totalH += normalizeH;
                    totalS += hsvPixel[1];
                    totalArea += 1;
                }
            }
        }*/
        /*for (int x=0; x<image.getWidth(); x++){
            for (int y=0; y<image.getHeight(); y++){
                int pixel = image.getPixel(x,y);
                float[] hsvPixel = getHSVcolor(pixel);
                if (hsvPixel[0]>normalizeThres){
                    int[] rgbPixel = getRGBcolor(pixel);
                    *//*double normalizeR = rgbPixel[0]/255;
                    double normalizeG = rgbPixel[1]/255;
                    double normalizeB = rgbPixel[2]/255;*//*
                    totalR += rgbPixel[0];
                    totalG += rgbPixel[1];
                    totalB += rgbPixel[2];
                    totalArea += 1;
                }
            }
        }*/
        for (int x=0; x<image.getWidth(); x++){
            for (int y=image.getHeight()/2; y<image.getHeight(); y++){
                int pixel = image.getPixel(x,y);
                int[] rgbPixel = getRGBcolor(pixel);
                if (rgbPixel[0]>70 && !(rgbPixel[0]<150 && rgbPixel[0]-rgbPixel[2] < 35)){
                    totalR += rgbPixel[0];
                    totalG += rgbPixel[1];
                    totalB += rgbPixel[2];
                    totalArea += 1;
                }
            }
        }

//        for(int i =0; i<image.getHeight()*image.getWidth();i++){
//            float[] hsvPixel = getHSVcolor(pixels[i]);
//            double normalizeH = hsvPixel[0]/360;
//            if (hsvPixel[0]>normalizeThres){
//                totalH += normalizeH;
//                totalS += hsvPixel[2];
//                totalArea += 1;
//            }
//        }

        /*double meanH = totalH/totalArea;
        double meanS = totalS/totalArea;*/
        double meanR = totalR/totalArea;
        double meanG = totalG/totalArea;
        double meanB = totalB/totalArea;

//            Result result = new Result(meanH, meanV, true);
//            return result;
        return new Result(meanR, meanG, meanB, isMale);
    }

    float[] getHSVcolor(int pixel){
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);

        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        return hsv;
    }
    int[] getRGBcolor(int pixel){
        int[] rgb = new int[3];
        rgb[0] = Color.red(pixel);
        rgb[1] = Color.green(pixel);
        rgb[2] = Color.blue(pixel);
        return rgb;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        dialog.dismiss();
        listener.onImageCalculated(result);
    }

    public interface OnImageCalculatedListener{
        void onImageCalculated(Result result);
    }

    public class Result {
        private double hue;
        private double saturation;
        private boolean isMale;
        private double red,green,blue;

        public Result(double hue, double saturation, boolean isMale) {
            this.hue = hue;
            this.saturation = saturation;
            this.isMale = isMale;
        }
        public Result(double red, double green, double blue, boolean isMale) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.isMale = isMale;
        }

        /*public double getHbLevel(){
            if (isMale)
                return 3.33 + (12.79*hue) + (1.68*saturation);
            return 1.06 + (14.02*hue) + (0.77*saturation);
        }*/

        public double getHbLevel(){
            if (isMale)
                return 6.8 + (0.051 * red) - (0.005 * blue);
//            return 8.15 + (0.036 * red) - (0.0015 * blue);
            return ((red-50.228)/14.486);
        }

        @Override
        public String toString() {
            //double value = Double.parseDouble(new DecimalFormat("##.###").format(getHbLevel()));
            String value = String.valueOf(getHbLevel());
            if (value.length() >5){
                value = value.substring(0,4);
            }
            return "HBlevel= "+ value ;
        }
    }
}