package com.imvlabs.hbey.CameraHandler;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

/**
 * Created by maaakbar on 10/22/15.
 * Updated by Kautsar Fadly Firdaus on 09/16/2017
 */
public class CameraNew implements CameraBloodEy{
    private final static String TAG = "CameraNew";
    private CameraDevice mCameraDevice = null;
    private Context context;
    private TextureView mTextureView = null;
    private Surface mSurface = null;
    private Size mPreviewSize = null;
    private CameraCaptureSession mCameraPreviewSession;
    private CameraCaptureSession mCameraCaptureSession;
    private CaptureRequest.Builder mSessionBuilder = null;
    private OnPictureReadyListener onPictureReadyListener;
    public static final int CAMERA_FRONT = 1;
    public static final int CAMERA_BACK = 0;
    public int currentCameraId  = CAMERA_BACK;


    public CameraNew(Context context, TextureView cameraView, OnPictureReadyListener listener) {
        this.context = context;
        this.mTextureView = cameraView;
        this.onPictureReadyListener = listener;
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    @Override
    public void openCamera() {
        CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try{
            String cameraId = mCameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            mCameraManager.openCamera(cameraId, mStateCallback, null);
        }
        catch(CameraAccessException e)
        {
            e.printStackTrace();
            //asdas
        }
        catch(SecurityException e){
            Log.e(TAG, "No Permission");

        }
    }

    @Override
    public void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    @Override
    public void takePicture() {
        try {
            mSessionBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        mSessionBuilder.addTarget(mSurface);
        try {
            mCameraDevice.createCaptureSession(Arrays.asList(mSurface), mCaptureStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFrontCameraAvailable() {
        return false;
    }

    @Override
    public void switchCam() {
    }

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
            closeCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                              int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onSurfaceTextureAvailable()");
            openCamera();
        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onOpened");
            mCameraDevice = camera;

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                Log.e(TAG, "texture is null");
                return;
            }

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mSurface = new Surface(texture);

            try {
                mSessionBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            } catch (CameraAccessException e){
                e.printStackTrace();
            }


            mSessionBuilder.addTarget(mSurface);

            try {
                mCameraDevice.createCaptureSession(Arrays.asList(mSurface), mPreviewStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            // TODO Auto-generated method stub
            mCameraDevice.close();
            mCameraDevice = null;
            Log.e(TAG, "onError");

        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            // TODO Auto-generated method stub
            mCameraDevice.close();
            Log.e(TAG, "onDisconnected");
        }
    };

    private CameraCaptureSession.StateCallback mPreviewStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onConfigured");

            mCameraPreviewSession = session;
            mSessionBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            HandlerThread backgroundThread = new HandlerThread("CameraPreview");
            backgroundThread.start();
            Handler backgroundHandler = new Handler(backgroundThread.getLooper());

            try {
                mCameraPreviewSession.setRepeatingRequest(mSessionBuilder.build(), null, backgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.e(TAG, "CameraCaptureSession Configure failed");
        }
    };

    private CameraCaptureSession.StateCallback mCaptureStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onConfigured CameraCaptureSession.StateCallback");

            mCameraCaptureSession = session;

            mSessionBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            HandlerThread backgroundThread = new HandlerThread("CameraCapture");
            backgroundThread.start();
            Handler backgroundHandler = new Handler(backgroundThread.getLooper());

            try {
                mCameraCaptureSession.capture(mSessionBuilder.build(), null, backgroundHandler);
                Log.i(TAG, "onConfigured mCameraCaptureSession.capture");
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "CameraCaptureSession.StateCallback done");
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.e(TAG, "CameraCaptureSession Configure failed");
        }
    };
}
