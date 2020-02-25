package com.imvlabs.hbey.CameraHandler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import org.jetbrains.annotations.Contract;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maaakbar on 10/22/15.
 */
public class CameraOld implements CameraBloodEy{
    private final static String TAG = "CameraOld";
    private Camera mCamera = null;
    private Context context;
    private TextureView mTextureView;
    private SurfaceTexture surfaceTexture = null;
    private OnPictureReadyListener onPictureReadyListener;
    public int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean isPreviewRunning = false;

    public CameraOld(Context context, TextureView mTextureView, OnPictureReadyListener listener){
        this.context = context;
        this.mTextureView = mTextureView;
        this.onPictureReadyListener = listener;
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        mTextureView.setOnTouchListener(mOnTouchListener);
    }

    @Override
    public void switchCam(){
        closeCamera();
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        openCamera();
    }

    @Override
    public boolean isFrontCameraAvailable(){
        return Camera.getNumberOfCameras()>1;
    }

    @Override
    public void openCamera() {
        try {
            if (mCamera == null)
                mCamera = getCameraInstance(currentCameraId);
            mCamera.setPreviewTexture(surfaceTexture);
            // set the image upright
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            isPreviewRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
            // Something bad happened
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("No camera is available");
            builder.setMessage("Please allow the app to use camera!!");
            builder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((Activity)context).finish();
                }
            });
            builder.show();
        }

    }

    @Override
    public void closeCamera() {
        if (mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void takePicture() {
        // enable to play other sound
        // mCamera.takePicture(shutterCallback, null, pictureCallback);
        mCamera.takePicture(null, null, pictureCallback);
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int cameraID) throws IOException{
        Camera c = null;
        try {
            c = Camera.open(cameraID); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            throw new IOException("No camera is available");
        }
        return c; // will not return null
    }

    private static  final int FOCUS_AREA_SIZE= 300;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i(TAG, "surface touched");

//            List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
//            boolean hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN && hasAutoFocus) {
                focusOnTouch(event);
            }
            return false;
        }
    };
    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null ) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                Rect rect = calculateFocusArea(event.getX(), event.getY());
                if (parameters.getMaxNumMeteringAreas() > 0) {
                    Log.i(TAG, "fancy !");

                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                    meteringAreas.add(new Camera.Area(rect, 800));
                    parameters.setFocusAreas(meteringAreas);

                    mCamera.setParameters(parameters);
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                } else {
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mTextureView.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mTextureView.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    @Contract (pure = true)
    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            //Log.i(TAG, "onSurfaceTextureUpdated()");

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                                int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureSizeChanged()");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureDestroyed()");
            return true;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
                throws RuntimeException{
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureAvailable()");
            surfaceTexture = surface;
            openCamera();
        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            onPictureReadyListener.onPictureAvailable(data);
        }
    };
}
