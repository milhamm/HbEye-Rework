package com.imvlabs.hbey;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.imvlabs.hbey.CameraHandler.Camera2;
import com.imvlabs.hbey.CameraHandler.OnPictureReadyListener;

import java.io.ByteArrayOutputStream;

//import com.imvlabs.hbey.CameraHandler.CameraBloodEy;

public class CameraActivity extends AppCompatActivity implements OnPictureReadyListener{
    // image size = 131 * 126
    final static String TAG = "CameraActivity";
    final static String CameraState = "isCameraFacingBack";

    View.OnClickListener done_capture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "OK selected");
            Intent data = new Intent();
//            data.putExtra(CameraState, ((Camera2) cameraHandler).cameraId == Camera.CameraInfo.CAMERA_FACING_BACK);
            data.putExtra(CameraState, false);
            setResult(RESULT_OK, data);
            data.putExtra("IMAGE", pictureData);
            data.putExtra("DIRECTION", direction);
            Log.e(TAG, "mau finish " + pictureData.length);
            Log.e(TAG, "mau finish");
            finish();
        }
    };
    private ImageButton switchCam;
    private ProgressDialog progressDialog;

    private View capturing_bar;
    private View confirmation_bar;

    private int status;

    private ImageView eyeImage;


    int direction;

    boolean isCameraWork = true;

    boolean isCameraAvailable(){
        Log.i(TAG, "isPermitted Checks");
        PackageManager pm = getPackageManager();
        int hasCameraPermission = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
        return (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                hasCameraPermission == PackageManager.PERMISSION_GRANTED);
    }

    void showUnAvailableDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setTitle("No camera is available");
        builder.setMessage("Please allow the app to use camera!!");
        builder.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    private Camera2 cameraHandler = null;

    View.OnClickListener capture_photo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isCameraWork = false;
            cameraHandler.takePicture();
            progressDialog = new ProgressDialog(CameraActivity.this);
            progressDialog.setMessage("Capturing..");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            Log.i(TAG, "beres gak sih ini?");
        }
    };

    View.OnClickListener recapture_photo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isCameraWork = true;
            cameraHandler.openCamera();
            Animation disappear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
            confirmation_bar.startAnimation(disappear);
            confirmation_bar.setVisibility(View.GONE);
            capturing_bar.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Align your eye into lines");
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar!=null)
            bar.setDisplayHomeAsUpEnabled(true);

        if (!isCameraAvailable())
            showUnAvailableDialog();
        else {
            TextureView mTextureView = findViewById(R.id.cameraView);
            Log.d(TAG, "Lolipop OS or higher detected");
            cameraHandler = new Camera2(this, mTextureView, this);
//            status = 1;
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            } else {
//            Log.d(TAG, "Older OS selected");
//            cameraHandler = new CameraOld(this, mTextureView, this);
//                status = 0;
//            }

            switchCam = findViewById(R.id.switch_camera);
            if (cameraHandler.isFrontCameraAvailable()) {
                switchCam.setOnClickListener(switchClick);
            } else {
                switchCam.setVisibility(View.GONE);
            }

            findViewById(R.id.take_picture).setOnClickListener(capture_photo);
            findViewById(R.id.button_cancel).setOnClickListener(recapture_photo);
            findViewById(R.id.button_done).setOnClickListener(done_capture);

            capturing_bar = findViewById(R.id.capturing_bar);
            confirmation_bar = findViewById(R.id.confirmation_bar);
        }

        eyeImage = findViewById(R.id.overlay_image);

        SensorManager senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final Sensor senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Sensor mySensor = sensorEvent.sensor;
                if (isCameraWork){
                    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        float x = sensorEvent.values[0];
                        float y = sensorEvent.values[1];
                        float z = sensorEvent.values[2];
                        if (y > 5){
                            direction = 0;
                            eyeImage.setImageDrawable(ContextCompat.getDrawable(CameraActivity.this,R.drawable.eye_frame));
                        } else if (y < -5){
                            direction = 1;
                            eyeImage.setImageDrawable(ContextCompat.getDrawable(CameraActivity.this,R.drawable.eye_frame180));
                        } else if (x > 5){
                            direction = 2;
                            eyeImage.setImageDrawable(ContextCompat.getDrawable(CameraActivity.this,R.drawable.eye_frame270));
                        } else if (x < -5){
                            direction = 3;
                            eyeImage.setImageDrawable(ContextCompat.getDrawable(CameraActivity.this,R.drawable.eye_frame90));
                        }
                        //Log.e("acc","x:"+String.valueOf(x)+";  y:"+String.valueOf(y));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    View.OnClickListener switchClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraHandler.switchCam();
            switchCam.setColorFilter(Color.argb(255, 231, 223, 223)); // White Tint
        }
    };

    byte[] pictureData;
    @Override
    public void onPictureAvailable(byte[] data) {
        Log.i(TAG, "Picture available");
        //ImageHolder.SetImage(data);
        //pictureData = data;
        progressDialog.dismiss();
        Animation disappear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
        capturing_bar.startAnimation(disappear);
        capturing_bar.setVisibility(View.GONE);
        confirmation_bar.setVisibility(View.VISIBLE);
        Bitmap original = BitmapFactory.decodeByteArray(data,0,data.length);
        data = null;
        if (original.getWidth() > 512){
            final int width = 512;
            final int height = original.getHeight() / original.getHeight() * width;
            Bitmap dest = Bitmap.createScaledBitmap(original, width, height, false);
            original = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            dest.compress(Bitmap.CompressFormat.PNG, 100, stream);
            pictureData = stream.toByteArray();
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 100, stream);
            pictureData = stream.toByteArray();
        }
        eyeImage.setImageBitmap(original);


// Write 'bitmap' to file using JPEG and 80% quality hint for JPEG:


//        MaterialDialog builder = new MaterialDialog.Builder(this).customView(R.layout.image_full,false).build();
//        builder.show();
//        Bitmap bmp= BitmapFactory.decodeByteArray(data,0,data.length);
//        ((ImageView)builder.findViewById(R.id.image_full)).setImageBitmap(bmp);
    }

    @Override
    public void onPictureAvailable() {
//        capture.setVisibility(View.GONE);
//        done.setVisibility(View.VISIBLE);
//        cancel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraHandler != null)
        {
            cameraHandler.closeCamera();
        }
    }
}
