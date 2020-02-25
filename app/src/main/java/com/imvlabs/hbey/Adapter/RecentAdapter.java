package com.imvlabs.hbey.Adapter;

import android.graphics.Bitmap;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imvlabs.hbey.Entities.Recent;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.ImageReader.PictureUtilities;
import com.imvlabs.hbey.Fragment.CheckUpFragment;
import com.imvlabs.hbey.R;
import com.pkmmte.view.CircularImageView;

import java.util.Date;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Kautsar Fadly F on 11/09/2017.
 */

public class RecentAdapter extends RealmRecyclerViewAdapter<Recent, RecentAdapter.ViewHolder> {
    final static String TAG = "Recent Adapter";
    Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nama,umur,kelamin; //user's info
        private TextView status,waktu,hb;
        private CircularImageView eyeView;


        public ViewHolder(View itemView){
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.nama);
            umur = (TextView) itemView.findViewById(R.id.umur);
            kelamin = (TextView) itemView.findViewById(R.id.kelamin);
            status = (TextView) itemView.findViewById(R.id.status);
            waktu = (TextView) itemView.findViewById(R.id.waktu);
            hb = (TextView) itemView.findViewById(R.id.hb);
            eyeView = (CircularImageView)itemView.findViewById(R.id.circularImageView);
        }


        public void setUsername(String nama){
            this.nama.setText(nama);
        }
        public void setUserAge(int age) {
            this.umur.setText(Integer.toString(age));
        }
        public void setUserGender(boolean isMale) {
            this.kelamin.setText( isMale ? "Male" : "Female");
        }
        public void setDate(Date date) {
            this.waktu.setText(Utilities.convertDateToString(date));
        }
        public void setStatus(String status) {
            this.status.setText(status);
        }
        public void setHb(double hb) {
            this.hb.setText(Utilities.formatRawHbLevel(hb));
        }
        public void setImage(Bitmap image){
            eyeView.setImageBitmap(image);
            eyeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

//    public RecentAdapter(Context context, RealmResults<Recent> realmResults,
//                          boolean automaticUpdate,
//                          boolean animateResult) {
//        super(context, realmResults, automaticUpdate, animateResult);
//        this.context = context;
//    }
    public RecentAdapter(Context context, RealmResults<Recent> realmResults,
                         boolean automaticUpdate,
                         boolean animateResult){
        super(realmResults, automaticUpdate);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_item, parent, false);
        return new RecentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Recent recent = getData().get(position);

        viewHolder.setUsername(recent.getNama());
        viewHolder.setUserAge(recent.getUmur());
        viewHolder.setUserGender(recent.isKelamin());
        viewHolder.setDate(recent.getWaktu());
        viewHolder.setStatus(recent.getStatus());
        viewHolder.setHb(recent.getHb());
        viewHolder.setImage(PictureUtilities.getBitmapFromByteArray(recent.getGambar()));
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
