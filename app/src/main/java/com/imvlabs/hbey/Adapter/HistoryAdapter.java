package com.imvlabs.hbey.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imvlabs.hbey.DetailActivity;
import com.imvlabs.hbey.Entities.Person;
import com.imvlabs.hbey.Entities.ResultData;
import com.imvlabs.hbey.Fragment.CheckUpFragment;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.R;

import java.util.Date;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Define persons history adapters
 */
public class HistoryAdapter extends RealmRecyclerViewAdapter<Person, HistoryAdapter.ViewHolder> {
    final static String TAG = "HistoryAdapter";
    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Person person = getData().get(position);

        viewHolder.setUsername(person.getUser_name());
        viewHolder.setUserAge(person.getAge());
        viewHolder.setUserGender(person.isMale());

        // latest Result
        final ResultData resultData = person.getResults().last();
        if (resultData!=null) {
            viewHolder.setDate(resultData.getTaken_date());
            viewHolder.setStatus(resultData.getHbLevel(), person.isMale());
            viewHolder.setHb(resultData.getHbLevel());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, Utilities.formatRawHbLevel(resultData.getHbLevel()));
                    moveToDetailActivity(person.getUser_name());
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private Button nameTag;
        private TextView username, userAge, userGender;  // user's info
        private TextView date, status, hb;  // result's info

        public ViewHolder(View itemView){
            super(itemView);
            nameTag = (Button)itemView.findViewById(R.id.usertag);
            username = (TextView)itemView.findViewById(R.id.username);
            userAge = (TextView)itemView.findViewById(R.id.userage);
            userGender = (TextView)itemView.findViewById(R.id.usergender);
            date = (TextView)itemView.findViewById(R.id.result_date);
            status = (TextView)itemView.findViewById(R.id.result_status);
            hb = (TextView)itemView.findViewById(R.id.result_hb);
        }

        public void setUsername (String name){
            username.setText(name);
            nameTag.setText(Character.toString(name.charAt(0)));
        }

        public void setUserAge(int age) {
            this.userAge.setText(Integer.toString(age));
        }

        public void setUserGender(boolean isMale) {
            this.userGender.setText( isMale ? "Male" : "Female");
        }

        public void setDate(Date date) {
            this.date.setText(Utilities.convertDateToString(date));
        }

        public void setStatus(double hb, boolean isMale) {
            this.status.setText(Utilities.getNormalStatus(isMale, hb));
        }

        public void setHb(double hb) {
            this.hb.setText(Utilities.formatRawHbLevel(hb));
        }
    }

    public HistoryAdapter(Context context, RealmResults<Person> realmResults,
                         boolean automaticUpdate,
                         boolean animateResult) {
        super(realmResults, automaticUpdate);
        this.context = context;
    }

    void moveToDetailActivity(String username){
        Intent moveActivity = new Intent(context, DetailActivity.class);
        moveActivity.putExtra(CheckUpFragment.usernameExtra, username);
        context.startActivity(moveActivity);
    }
}
