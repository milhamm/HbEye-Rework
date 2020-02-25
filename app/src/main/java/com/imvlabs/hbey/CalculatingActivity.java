package com.imvlabs.hbey;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imvlabs.hbey.Entities.Person;
import com.imvlabs.hbey.Entities.Recent;
import com.imvlabs.hbey.Entities.FireModel;
import com.imvlabs.hbey.Firebase.FireHelp;
import com.imvlabs.hbey.Fragment.CheckUpFragment;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.ImageReader.CalculateTask;
import com.imvlabs.hbey.ImageReader.ImageHolder;
import com.imvlabs.hbey.ImageReader.PictureUtilities;
import com.pkmmte.view.CircularImageView;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class CalculatingActivity extends AppCompatActivity {
    public static final int requestImage = 1;
    private byte[] eyeImage;
    private CircularImageView eye_view;
    private View camera_tap;
    String username,longitude,latitude;
    boolean isMale;
    int userAge;
    double nilaiHb;
    Realm realm;
    int nextId;
    FireHelp helper;
    DatabaseReference db;
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    UploadTask uploadTask;

    String TAG = "CalculatingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Take your lower eye lid photo");
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar!=null)
            bar.setDisplayHomeAsUpEnabled(true);


        camera_tap = findViewById(R.id.camera_tap);
        camera_tap.setOnClickListener(camera_tap_listener);

        eye_view = (CircularImageView)findViewById(R.id.eye_view);
    }

    View.OnClickListener camera_tap_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                Intent moveToCamera = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(moveToCamera, requestImage);
        }
    };


    void selectEyeRegion(byte[] image, boolean isCameraFacingBack,int direction) {
        //Log.e("direction again",String.valueOf(direction));
        PictureUtilities.cropEyeRegion(CalculatingActivity.this, image, isCameraFacingBack,
                new PictureUtilities.OnImageCroppedListener() {
                    @Override
                    public void onImageCropped(byte[] image) {
                        Log.i(TAG, "image cropped");
                        eyeImage = image;
                        eye_view.setImageBitmap(PictureUtilities.getBitmapFromByteArray(eyeImage));
                        eye_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        eye_view.setVisibility(View.VISIBLE);
                        eye_view.setOnClickListener(camera_tap_listener);

                        camera_tap.setVisibility(View.GONE);

                        View calculate_button = findViewById(R.id.calculate_button);
                        calculate_button.setEnabled(true);
                        calculate_button.setOnClickListener(calculateClick);
                    }
                },direction);
    }

    View.OnClickListener calculateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // will use this later to upload data
            username = getIntent().getStringExtra(CheckUpFragment.usernameExtra);
            userAge = getIntent().getIntExtra(CheckUpFragment.userageExtra, 21);
            isMale = getIntent().getBooleanExtra(CheckUpFragment.genderExtra, true);
            longitude = getIntent().getStringExtra(CheckUpFragment.longitudeExtra);
            latitude = getIntent().getStringExtra(CheckUpFragment.latitudeExtra);
            Person person = new Person(username, isMale, userAge);
            saveUserToDB(person);
            int direction = getIntent().getIntExtra("DIRECTION",0);
            //Log.e("DIRECTION",String.valueOf(direction));
            CalculateTask task = new CalculateTask(CalculatingActivity.this, isMale, calculatedListener,direction);
            task.execute(PictureUtilities.getBitmapFromByteArray(eyeImage));
        }
    };

    void saveUserToDB (final Person person){
        // save user
        Realm.setDefaultConfiguration(Utilities.getRealmConfig(getApplicationContext()));
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person existedPerson = realm.where(Person.class)
                        .equalTo("user_name", person.getUser_name()).findFirst();
                if (existedPerson==null)
                    realm.copyToRealm(person);
                else {
                    existedPerson.setAge(person.getAge());
                    existedPerson.setIsMale(person.isMale());
                }
            }
        });
    }
    public int getMax(){
        Realm.setDefaultConfiguration(Utilities.getRealmConfig(getApplicationContext()));
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction((new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(Recent.class).max("id");

                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
            }
        }));
        return nextId;
    }
    void saveUserToRecent(final Recent recent){
        // Save recent user
        Realm.setDefaultConfiguration(Utilities.getRealmConfig(getApplicationContext()));
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction((new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(recent);
            }
        }));
    }

    public static String calculatingIdExtra = "calculatingActivity";
    public static String hbLevelExtra = "hbLevel";
    CalculateTask.OnImageCalculatedListener calculatedListener = new CalculateTask.OnImageCalculatedListener() {
        @Override
        public void onImageCalculated(CalculateTask.Result result) {
            Log.d(TAG, result.toString());
            Intent moveActivity = new Intent(CalculatingActivity.this, DetailActivity.class);
            moveActivity.putExtra(calculatingIdExtra, true);
            //moveActivity.putExtra("DIRECTION",direction);
            moveActivity.putExtra(CheckUpFragment.usernameExtra, getIntent().getStringExtra(CheckUpFragment.usernameExtra));
            moveActivity.putExtra(CheckUpFragment.userageExtra, getIntent().getIntExtra(CheckUpFragment.userageExtra, 21));
            moveActivity.putExtra(CheckUpFragment.genderExtra, getIntent().getBooleanExtra(CheckUpFragment.genderExtra, true));
            ImageHolder.SetImage(eyeImage);
            moveActivity.putExtra(hbLevelExtra, result.getHbLevel());
            Date date = new Date();
            String nama = username;
            String umur = Integer.toString(userAge);
            String kelamin = (isMale ? "Male" : "Female");
            String hbLevel = String.valueOf(result.getHbLevel());
            String waktu = Utilities.convertDateToString(date);
            String status = Utilities.getNormalStatus(isMale, result.getHbLevel());
            Recent recent = new Recent(getMax(),username,isMale,userAge,eyeImage,result.getHbLevel(),date, status);
            saveUserToRecent(recent);
            String random = UUID.randomUUID().toString();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            helper = new FireHelp(ref);
            //nama,umur,kelamin,hbLevel,waktu,status
            FireModel fireModel = new FireModel(nama,umur,kelamin,hbLevel,waktu,status,longitude,latitude);
            try {
                    if (helper.save(fireModel)) {
                        Log.i(TAG, "Upload Success");
                    } else {
                        Log.i(TAG, "Upload Fail");
                    }
                } catch(Exception e){
                e.printStackTrace();
                Log.i(TAG, "firebase fail");
            }
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            imageRef = storageRef.child("Images").child(helper.getFolder());
            try {
                uploadTask = imageRef.putBytes(eyeImage);
            } catch (Exception e){
                e.printStackTrace();
            }
            startActivity(moveActivity);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ON ACTIVITY RESULT", "YEE RESULT");
        if (requestCode == requestImage) {
            Log.e("ON ACTIVITY RESULT", "YEE RESULT");
            if (resultCode == RESULT_OK && data!=null) {
                Log.e("ON ACTIVITY RESULT", "YEE RESULT");
                boolean isCameraFacingBack = data.getBooleanExtra(CameraActivity.CameraState, true);
                int direction = data.getIntExtra("DIRECTION",0);
                Log.e("DIRECTION", String.valueOf(direction));
                //byte[] pictureData = data.getByteArrayExtra("IMAGE");
                selectEyeRegion(data.getByteArrayExtra("IMAGE"), isCameraFacingBack, direction);
            }
        }
    }

}
