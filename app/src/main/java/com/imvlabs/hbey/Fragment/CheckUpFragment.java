package com.imvlabs.hbey.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.imvlabs.hbey.Adapter.RecentAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.imvlabs.hbey.CalculatingActivity;
import com.imvlabs.hbey.Entities.Recent;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


import fr.ganfra.materialspinner.MaterialSpinner;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class CheckUpFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    static String TAG = "CheckUpFragment";
    View parent;
    EditText username;
    TextView userage;
    MaterialSpinner gender;
    RecyclerView zRecyclerView;
    Button checkButton, submitButton;
    Recent recent;
    PieChart pieChart;
    double latitude, longitude;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;

    Boolean status;
    PieChart statusChart;
    ArrayList<String> statusValue = new ArrayList();
    ArrayList<Double> hbValue = new ArrayList();
    ArrayList<Boolean> genderValue = new ArrayList();
    ArrayList<Integer> ageValue = new ArrayList();
    Intent methodIntent;
    int allCounterStatus, allCounterKelamin, allCounterUmur;
    int mediumPurple= Color.parseColor("#bf55ec");
    int caribbeanGreen=Color.parseColor("#03c9a9");




    int counterAnemia =0, counterNormal=0, counterMale=0, counterFemale=0,
            counterYoung = 0, counterMiddle = 0, counterOld = 0;

    public CheckUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_check_up);
        Log.d(TAG,"onCreate: starting to create chart");
        //Setting up GPS Connection for Mapping
        if (!Utilities.isUserAllowSharing(getContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.agreement));
            builder.setPositiveButton("Agree", positive);
            builder.setNegativeButton("Later", negative);
            builder.show();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // ok
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parent = inflater.inflate(R.layout.fragment_check_up, container, false);
//        parent.findViewById(R.id.checkButton).setOnClickListener();

//        username = (EditText)parent.findViewById(R.id.username);
//        userage = (TextView)parent.findViewById(R.id.userage);
//
//        gender = (MaterialSpinner)parent.findViewById(R.id.usergender);

        zRecyclerView = (RecyclerView) parent.findViewById(R.id.recent_recycler_view);

        return parent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("realm","on resume");
        // Data
        // specify an adapter and data set
        try{
            Realm.setDefaultConfiguration(Utilities.getRealmConfig(getActivity().getApplicationContext()));
            Realm realme = Realm.getDefaultInstance();
            Number currentIdNum = realme.where(Recent.class).max("id");
            int num = currentIdNum.intValue();
//            String kata = Integer.toString(num);
//            Toast.makeText(getContext(),kata,Toast.LENGTH_SHORT).show();
            RealmResults<Recent> realmResults = realme.where(Recent.class).findAllSorted("id",Sort.DESCENDING);
            Log.d("realm",realmResults.size()+">>>>>>>.");



            Log.i(TAG, "sebelum fetching");
            for (int i=0; i<realmResults.size(); i++){
                statusValue.add(realmResults.get(i).getStatus());
                hbValue.add(realmResults.get(i).getHb());
                genderValue.add(realmResults.get(i).isKelamin());
                ageValue.add(realmResults.get(i).getUmur());
            }

            Log.i(TAG, "onResume: "+statusValue.size());
            Log.i(TAG, "sesudah fetching");
            for (int i = 0; i<statusValue.size();i++){
                String cekStatus = statusValue.get(i).toString();
//                Toast.makeText(getContext(),cekStatus,Toast.LENGTH_SHORT).show();
                //Log.i(TAG, "onResume: status",cekStatus );
                if(statusValue.get(i).toString().equals("Anemia")){
                    counterAnemia=counterAnemia+1;
                }
                else if(statusValue.get(i).toString().equals("Normal")){
                    counterNormal=counterNormal+1;
                }

                Log.i(TAG, "cA: "+ counterAnemia+"cN"+counterNormal);
            }
            allCounterStatus= counterAnemia+counterNormal;
                    Log.e(TAG, "onResume: mau ngitung kelamin nih");
            for (int i = 0; i<genderValue.size();i++){
                if(genderValue.get(i)){
                    counterMale=counterMale+1;
                }
                else {
                    counterFemale=counterFemale+1;
                }
                Log.i(TAG, "cM: "+ counterMale+"cF: "+counterFemale);
            }

            allCounterKelamin=counterMale+counterFemale;

            for (int i=0 ; i< ageValue.size(); i++){
                if(ageValue.get(i)>20 && ageValue.get(i)<30) {
                    counterYoung = counterYoung+1;
                }
                else if (ageValue.get(i)>=30 && ageValue.get(i)<40){
                    counterMiddle = counterMiddle+1;
                }
                else {
                    counterOld=counterOld+1;
                }
                Log.i(TAG, "cY: "+counterYoung+"cMd: "+counterMiddle+"cO: "+counterOld);
            }
            allCounterUmur=counterOld+counterMiddle+counterYoung;

            RecentAdapter zAdapter = new RecentAdapter(getContext(), realmResults, true, true);
            zRecyclerView.setAdapter(zAdapter);
            zRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            Log.i("realm","error ga");}

        catch (Exception e){

            Log.i(TAG, "onResume: Error showing data");
            e.printStackTrace();
        }
        Log.d(TAG,"onCreate: starting to create chart");

        // ok

        checkButton=(Button)parent.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View mView = getActivity().getLayoutInflater().inflate(R.layout.pop_up_check_up, null);
                username = (EditText)mView.findViewById(R.id.username);
                userage = (TextView)mView.findViewById(R.id.userage);
                submitButton=(Button)mView.findViewById(R.id.submitButton);
                gender = (MaterialSpinner)mView.findViewById(R.id.usergender);
                Log.d(TAG, " sudah sampe sini");
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(getContext());
                mbuilder.setView(mView);
                AlertDialog dialog=mbuilder.create();
                status=false;
                dialog.show();
                submitButton.setOnClickListener(listener);
//                if (!status) {
//                    dialog.hide();
//                }

//                submitButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!username.getText().toString().isEmpty() && !userage.getText().toString().isEmpty()){
//                            usernameExtra=username.getText().toString();
//                            userageExtra=userage.getText().toString();
//
//
//                        }
//                        else Toast.makeText(getContext(), R.string.haha,Toast.LENGTH_SHORT).show();
//                    }
//                });



            }
        });
        int a = statusValue.size();
        Log.i(TAG, "onResume: allCounterStatus "+allCounterStatus);
        float b = (((float)counterAnemia)/((float)allCounterStatus))*100;
        float c = (((float)counterNormal)/((float)allCounterStatus))*100;
        Log.i(TAG, "onResume: hasil ngitung normal anemia nih "+b+" "+c+" "+a);
        final float[] yData={b,c};
        final String[] xData= {"Anemia", "Normal"};
        Log.i(TAG, "onResume: cek status value size"+a);
//        methodIntent = new Intent(getContext(),CheckUpFragment.class);
//        methodIntent.putExtra("parseYData",yData);


        Log.d(TAG,"starting the status Chart");
        statusChart = (PieChart)parent.findViewById(R.id.statusChart);
        statusChart.setRotationEnabled(true);
        statusChart.setHoleRadius(25f);
        statusChart.setTransparentCircleAlpha(0);
        statusChart.setCenterText("Status");
        statusChart.setDrawEntryLabels(true);

        //addDataStatusChart();

            Log.d(TAG, "addDataStatusChart: Udah mau nambahin data ke chart " );


            ArrayList<PieEntry> yEntrys=new ArrayList<>();
            ArrayList<String> xEntrys=new ArrayList<>();
//            for (int i=1; i<statusValue.size();i++){
//                Log.i(TAG, "onResume: yang masuk ni bang: "+yData[i]);
//            }


            for (int i=0; i< xData.length; i++){
                yEntrys.add(new PieEntry(yData[i],i));
            }
            for (int i=0; i< xData.length;i++){
                xEntrys.add(xData[i]);
            }
            //creating DataSet
            PieDataSet pieDataSet=new PieDataSet(yEntrys,"");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);

            //adding colors to Dataset
            ArrayList<Integer> colors=new ArrayList<>();
            colors.add(mediumPurple);
            colors.add(caribbeanGreen);

            pieDataSet.setColors(colors);

            //creating pie data object
            PieData pieData=new PieData(pieDataSet);
            statusChart.setData(pieData);
            statusChart.invalidate();

        statusChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                int pos1 = e.toString().indexOf("y: ");
                String statusValueOnChart = e.toString().substring(pos1+1  );

                for(int i = 0 ; i < yData.length;i++){
                    if(yData[i]==Float.parseFloat(statusValueOnChart)){
                        pos1=i;
                    }
                }
                String statusOnChart = xData[pos1+1];
//                Toast.makeText(getContext(), "Status: "+statusOnChart+"Nilai HB: "+statusValueOnChart,
//                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });


    }
//    private void addDataStatusChart() {
//        Log.d(TAG, "addDataStatusChart: Udah mau nambahin data ke chart " );
//
//        //methodIntent.getExtras();
//        final float[] yDataParsed= methodIntent.getExtras().getFloatArray("parseYData");
//        String[] xData= {"Anemia", "Normal"};
//        for(int i = 0; i<yDataParsed.length;i++){
//            Log.i(TAG, "addDataStatusChart: Dah masuk bang"+yDataParsed[i] );
//        }
//
//
//        ArrayList<PieEntry> yEntrys=new ArrayList<>();
//        ArrayList<String> xEntrys=new ArrayList<>();
//
//        for (int i=0; i< xData.length; i++){
//            yEntrys.add(new PieEntry(yDataParsed[i],i));
//        }
//        for (int i=0; i< xData.length;i++){
//            xEntrys.add(xData[i]);
//        }
//        //creating DataSet
//        PieDataSet pieDataSet=new PieDataSet(yEntrys,"Nilai Hemoglobin");
//        pieDataSet.setSliceSpace(2);
//        pieDataSet.setValueTextSize(12);
//
//        //adding colors to Dataset
//        ArrayList<Integer> colors=new ArrayList<>();
//        colors.add(Color.BLUE);
//        colors.add(Color.GREEN);
//
//        pieDataSet.setColors(colors);
//
//        //creating pie data object
//        PieData pieData=new PieData(pieDataSet);
//        statusChart.setData(pieData);
//        statusChart.invalidate();
//    }



    //    @Override
//    public void onResume() {
//        super.onResume();
//        Realm.setDefaultConfiguration(Utilities.getRealmConfig(getContext()));
//        Realm realm = Realm.getDefaultInstance();
//        RealmResults<Person> persons = realm.where(Person.class).findAll();
//        List<String> strings = new ArrayList<>();
//        for (Person person: persons){
//            strings.add(new String(person.getUser_name()));
//        }
//        names = strings.toArray(names);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, names);
//
//        username.setAdapter(adapter);
//    }

//    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Log.i(TAG, parent.getAdapter().getItem(position).toString());
//        }
//    };

    //Check if input data is empty
    boolean isDataEmpty (){
        return (username.getText().toString().isEmpty() ||
                userage.getText().toString().isEmpty() ||
                gender.getSelectedItem().equals("Gender")
        );
    }

    //Show Alert Dialog if Input Data is Empty
    void showEmptyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please input your data");
        builder.show();
    }

    //Listener for Submit Data
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Ripple completed");
            if (!isDataEmpty()) {
                if (!Utilities.isUserAllowSharing(getContext())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getString(R.string.agreement));
                    builder.setPositiveButton("Agree", positive);
                    builder.setNegativeButton("Later", negative);
                    builder.show();
                }else {
                    checkAndRequestPermissions();
                    if (isCameraEnabled && isInternetEnabled  && isLocationEnabled && isWriteEnabled){
                        //newest change
                        Intent moveActivity = new Intent(getContext(), CalculatingActivity.class);
                        moveActivity.putExtra(usernameExtra, username.getText().toString());
                        try {
                            moveActivity.putExtra(longitudeExtra, String.valueOf(longitude));
                            moveActivity.putExtra(latitudeExtra, String.valueOf(latitude));
                            moveActivity.putExtra(userageExtra, Integer.parseInt(userage.getText().toString()));
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                        moveActivity.putExtra(genderExtra, gender.getSelectedItem().toString().equals("Male"));
                        startActivity(moveActivity);
                        //change stop here
                        //startActivity(getMovingIntent());
                    } else if (!isCameraEnabled || !isLocationEnabled || !isLocationEnabled || !isWriteEnabled) {
                        Toast.makeText(getContext(), "We need your permission to use this application.", Toast.LENGTH_SHORT).show();
//                      requestPermissions(new String[]{Manifest.permission.CAMERA},
//                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//                      checkAndRequestPermissions();
                    }
                }
            } else
                showEmptyDialog();
        }
    };

    //Dialog Interface Starts Here
    DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // save tidak perlu tanya lagi
            // <code>save status agree</code>
            Utilities.setUserAllowSharing(getContext(), true);
            checkAndRequestPermissions();
            //commented after location update
//            if (isCameraEnabled && isInternetEnabled && isLocationEnabled && isWriteEnabled){
//                startActivity(getMovingIntent());
//            } else {
////                requestPermissions(new String[]{Manifest.permission.CAMERA},
////                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//                Toast.makeText(getContext(), "We need your permission to use this application.", Toast.LENGTH_SHORT).show();
//            }
        }
    };

    DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utilities.setUserAllowSharing(getContext(), false);
            //Commented after location update
//            checkAndRequestPermissions();
//            if (isCameraEnabled && isInternetEnabled && isLocationEnabled && isWriteEnabled){
//                startActivity(getMovingIntent());
//            } else {
//                Toast.makeText(getContext(), "We need your permission to use this application.", Toast.LENGTH_SHORT).show();
//            }
            getActivity().finish();
        }
    };
    //Dialog Interface Ends Here

    public static String usernameExtra = "username";
    public static String userageExtra = "userage";
    public static String genderExtra = "gender";
    public static String longitudeExtra  = "longitude";
    public static String latitudeExtra = "latitude";

    //Commented after location update
//    Intent getMovingIntent(){
//        Intent moveActivity = new Intent(getContext(), CalculatingActivity.class);
//        moveActivity.putExtra(usernameExtra, username.getText().toString());
//        try {
//            moveActivity.putExtra(longitudeExtra, String.valueOf(longitude));
//            moveActivity.putExtra(latitudeExtra, String.valueOf(latitude));
//            moveActivity.putExtra(userageExtra, Integer.parseInt(userage.getText().toString()));
//        }catch (NumberFormatException e){
//            e.printStackTrace();
//        }
//        moveActivity.putExtra(genderExtra, gender.getSelectedItem().toString().equals("Male"));
//        return moveActivity;
//    }

    //Set up permission parameters
    public boolean isCameraEnabled = false;
    public boolean isInternetEnabled = false;
    public boolean isLocationEnabled = false;
    public boolean isWriteEnabled = false;

    //Set a Permission request
    final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    //Permission Check and Request start here
    private  boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int internetPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET);
        int writePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(cameraPermission == PackageManager.PERMISSION_GRANTED && internetPermission == PackageManager.PERMISSION_GRANTED
                && writePermission == PackageManager.PERMISSION_GRANTED && locationPermission == PackageManager.PERMISSION_GRANTED ){
            isCameraEnabled = true;
            isLocationEnabled = true;
            isInternetEnabled = true;
            isWriteEnabled = true;
        } else {
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (internetPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.INTERNET);
            }
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                //Commented after permission update
                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    isCameraEnabled = true;
//                    startActivity(getMovingIntent());
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//                    Toast.makeText(getContext(), "We need your permission to access camera to take picture of your lid.", Toast.LENGTH_LONG).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }

                //Change after permission update
                if(hasAllPermissionsGranted(grantResults)){
                    isCameraEnabled = true;
                    isLocationEnabled = true;
                    isInternetEnabled = true;
                    isWriteEnabled = true;
//                    startActivity(getMovingIntent());
                } else {
                    Toast.makeText(getContext(), "We need your permission to use this application.", Toast.LENGTH_LONG).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    //Permission Check and Request end here

    //Location update starts here
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(50)
                .setFastestInterval(10);
        // Request location updates
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        } else {
            // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
    //location update ends here
}
