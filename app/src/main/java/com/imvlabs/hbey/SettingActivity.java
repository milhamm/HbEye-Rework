package com.imvlabs.hbey;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.imvlabs.hbey.Entities.Person;
import com.imvlabs.hbey.Helper.Utilities;
import com.gc.materialdesign.views.Slider;
import com.gc.materialdesign.views.Switch;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar!=null)
            bar.setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.shareResultText).setOnClickListener(clickListener);

        // switch allow to share
        Switch aSwitch = (Switch)findViewById(R.id.shareResultSwitch);
        aSwitch.setChecked(Utilities.isUserAllowSharing(SettingActivity.this));
        aSwitch.setOncheckListener(checkListener);

        // switch allow to share
        Slider slider = (Slider)findViewById(R.id.historySlider);
        slider.setValue(Utilities.getHistoryNumber(SettingActivity.this));
        slider.setOnValueChangedListener(onValueChangedListener);
    }

    Slider.OnValueChangedListener onValueChangedListener = new Slider.OnValueChangedListener() {
        @Override
        public void onValueChanged(int i) {
            Utilities.setHistoryNumber(SettingActivity.this, i);
        }
    };

    Switch.OnCheckListener checkListener = new Switch.OnCheckListener() {
        @Override
        public void onCheck(Switch aSwitch, boolean b) {
            Utilities.setUserAllowSharing(SettingActivity.this, b);
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setMessage(getString(R.string.agreement));
            builder.setPositiveButton("Agree", allow);
            builder.setNegativeButton("Disagree", disallow);
            builder.show();
        }
    };
    DialogInterface.OnClickListener allow = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utilities.setUserAllowSharing(SettingActivity.this, true);
        }
    };
    DialogInterface.OnClickListener disallow = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utilities.setUserAllowSharing(SettingActivity.this, false);
        }
    };

    public void deleteAllHistory(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Are you sure?").setPositiveButton("Yes", positive)
                .setNegativeButton("No", negative).show();
    }

    DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // does something very interesting
            Realm realm = Realm.getInstance(Utilities.getRealmConfig(getApplicationContext()));
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Person> objects = realm.where(Person.class).findAll();
                    List<Person> personList = new ArrayList<>();
                    personList.addAll(objects);
                    for (Person person:personList){
                        person.getResults().where().findAll().clear();
                    }
                    objects.clear();
                }
            });
        }
    };
    DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
}
