package com.imvlabs.hbey;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.imvlabs.hbey.Adapter.DetailAdapter;
import com.imvlabs.hbey.Entities.Person;
import com.imvlabs.hbey.Entities.ResultData;
import com.imvlabs.hbey.Fragment.CheckUpFragment;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.ImageReader.ImageHolder;
import com.imvlabs.hbey.ImageReader.PictureUtilities;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DetailActivity extends AppCompatActivity {
//    private final String TAG = "DetailActivity";
    Realm realm;
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String usernameId = getIntent().getStringExtra(CheckUpFragment.usernameExtra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle(usernameId+"'s");
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar!=null)
            bar.setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.detailed_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        try {
            // specify an adapter and data set
            Realm.setDefaultConfiguration(Utilities.getRealmConfig(getApplicationContext()));
            realm = Realm.getDefaultInstance();
            person = realm.where(Person.class).equalTo("user_name", usernameId).findFirst();
            RealmResults<ResultData> results = person.getResults().where()
                    .findAllSorted("taken_date", Sort.DESCENDING);
            DetailAdapter mAdapter = new DetailAdapter(DetailActivity.this, results, true, false, person.isMale());
            mRecyclerView.setAdapter(mAdapter);

            boolean fromCalculation = getIntent().getBooleanExtra(CalculatingActivity.calculatingIdExtra, false);
            setUpLatestResultView(fromCalculation);

        }catch (NullPointerException e){
            String TAG = "DetailActivity";
            Log.e(TAG, "No person found in database");
        }
    }

    String status;
    void setUpLatestResultView(boolean fromCalculationActivity){
        if (fromCalculationActivity) {
            boolean isMale = getIntent().getBooleanExtra(CheckUpFragment.genderExtra, true);
            double hbLevel = getIntent().getDoubleExtra(CalculatingActivity.hbLevelExtra, 0);

            ((TextView) findViewById(R.id.usergender)).setText(isMale ? "Male" : "Female");
            ((TextView) findViewById(R.id.userhb)).setText(Utilities.formatRawHbLevel(hbLevel));
            status = Utilities.getNormalStatus(isMale, hbLevel);
            ((TextView) findViewById(R.id.userstatus))
                    .setText(status);
            saveUserToDB(hbLevel);
        }else {
            ((TextView) findViewById(R.id.usergender)).setText(person.isMale() ? "Male" : "Female");
            ResultData result = person.getResults().where()
                    .findAllSorted("taken_date", Sort.DESCENDING).first();
            ((TextView) findViewById(R.id.userhb)).setText(Utilities.formatRawHbLevel(result.getHbLevel()));
            status = Utilities.getNormalStatus(person.isMale(), result.getHbLevel());
            ((TextView) findViewById(R.id.userstatus))
                    .setText(status);
        }
        if (status.equals("Anemia")){
            ((TextView) findViewById(R.id.txt_saran)).setText(R.string.saran_anemia);
        } else if (status.equals("NaN")) {
            ((TextView) findViewById(R.id.txt_saran)).setText(R.string.saran_nan);
        } else {
            ((TextView) findViewById(R.id.txt_saran)).setText(R.string.saran_normal);
        }
    }

    void saveUserToDB (final double hbLevel){
        Bitmap temp = PictureUtilities.getBitmapFromByteArray(ImageHolder.getImage());
        final Bitmap resizedEye = Bitmap.createScaledBitmap(temp, 120, 120, false);
        // save user
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                person.getResults().add(new ResultData(PictureUtilities.getByteArrayFromBitmap(resizedEye), hbLevel));
            }
        });
    }
}
