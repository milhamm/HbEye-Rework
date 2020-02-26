package com.imvlabs.hbey;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.imvlabs.hbey.Adapter.HomePagerAdapter;

public class HomeActivity extends AppCompatActivity {
    static String TAG = "HomeActivity";
    HomePagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.history));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new HomePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        adapter.checkUpFragment.isCameraEnabled = isCameraAvailable();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent move = null;
        switch (id){
            case R.id.action_settings:
                Log.d(TAG, "Setting");
                move = new Intent(this, SettingActivity.class);
                break;
            case R.id.action_about:
                Log.d(TAG, "About");
                move = new Intent(this, AboutActivity.class);
                break;
            case R.id.action_tutorial:
                Log.d(TAG, "tutorial");
                move = new Intent(this, TutorialActivity.class);
                break;
        }
        if(move!=null)
            startActivity(move);
        return true;
    }

    boolean isCameraAvailable(){
        Log.i(TAG, "isPermitted Checks");
        PackageManager pm = getPackageManager();
        int hasCameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        Log.i(TAG, "isPermitted"+(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                hasCameraPermission == PackageManager.PERMISSION_GRANTED));
        return (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                hasCameraPermission == PackageManager.PERMISSION_GRANTED);
    }
}
