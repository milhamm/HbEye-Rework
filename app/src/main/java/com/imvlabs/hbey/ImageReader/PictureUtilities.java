package com.imvlabs.hbey.ImageReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import java.io.ByteArrayOutputStream;


/**
 * Utilities Class to do basic operation to image
 */
public class PictureUtilities {
    private final static String TAG = "PictureUtilities";

    public static void cropEyeRegion(Context context, byte[] mDataImage, boolean isBackCam,
                                     OnImageCroppedListener listener, int direction){
        new CroppingTask(context, mDataImage, isBackCam, listener,direction).execute();
    }

    public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, stream); // no compression
        return stream.toByteArray();
    }

    public static Bitmap getBitmapFromByteArray(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap getBitmapFromDrawable(Resources res, int resID){
        return BitmapFactory.decodeResource(res, resID);
    }

    public static byte[] getCompressedByte(Bitmap bitmap){
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        //Compress it before sending it to minimize the size and quality of bitmap.
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        return bStream.toByteArray();
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                //return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, int direction,boolean isBackCam) {
        Log.e("direction",String.valueOf(direction));
        Matrix matrix = new Matrix();
        switch (direction){
            case 1:
                matrix.setRotate(180);
                break;
            case 2:
                if (isBackCam){
                    matrix.setRotate(270);
                } else {
                    matrix.setRotate(90);
                }
                break;
            case 3:
                if (isBackCam){
                    matrix.setRotate(90);
                } else {
                    matrix.setRotate(270);
                }
                break;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    static class CroppingTask extends AsyncTask<Void, Void, Bitmap>{
        private byte[] image;
        private OnImageCroppedListener listener;
        private boolean isBackCam;
        private Context context;
        private int direction;

        public CroppingTask(Context context, byte[] image, boolean isFacingBack, OnImageCroppedListener listener,int direction) {
            this.context = context;
            this.image = image;
            this.isBackCam = isFacingBack;
            this.listener = listener;
            this.direction = direction;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap originalBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            // normalize image rotation
            if (isBackCam){
                Log.e("camera","back");
                originalBmp = rotateBitmap(originalBmp, ExifInterface.ORIENTATION_ROTATE_90);
                originalBmp = rotateImage(originalBmp,direction,isBackCam);
            } else {
                Log.e("camera","front");
                originalBmp = rotateBitmap(originalBmp, ExifInterface.ORIENTATION_ROTATE_270);
                originalBmp = rotateImage(originalBmp,direction,isBackCam);
            }
            Log.i(TAG, "cropped: widht="+originalBmp.getWidth()+"height="+originalBmp.getHeight());

            int aThirdHeight = originalBmp.getHeight()/3;
            int aThirdWidth = originalBmp.getWidth()/3;

            int newHeight = originalBmp.getHeight() - (2*aThirdHeight);
            int newWidth= originalBmp.getWidth() - (2*aThirdWidth);
//            Bitmap newBitmap = Bitmap.createBitmap(originalBmp, aSixWidth, aThirdHeight, newWidth, newHeight);
//            return newBitmap;
            return Bitmap.createBitmap(originalBmp, aThirdWidth, aThirdHeight, newWidth, newHeight);
        }

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            dialog.setMessage("Capturing..");
            dialog.setIndeterminate(true);
//            dialog.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            dialog.dismiss();
            listener.onImageCropped(getCompressedByte(bitmap));
        }
    }
    public interface OnImageCroppedListener{
        void onImageCropped(byte[] image);
    }
}
