package com.imvlabs.hbey.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.imvlabs.hbey.Entities.ResultData;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.ImageReader.PictureUtilities;
import com.imvlabs.hbey.R;
import com.pkmmte.view.CircularImageView;

import java.util.Date;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Define detailed history adapters
 */
public class DetailAdapter extends RealmRecyclerViewAdapter<ResultData, DetailAdapter.ViewHolder> {
    String TAG = "DetailAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private CircularImageView eyeView;
        private TextView hbLevel, resultDate,status;

        public ViewHolder(View itemView) {
            super(itemView);
            eyeView = (CircularImageView)itemView.findViewById(R.id.eye_view);
            hbLevel = (TextView)itemView.findViewById(R.id.result_hb);
            resultDate = (TextView)itemView.findViewById(R.id.result_date);
            status = (TextView)itemView.findViewById(R.id.result_status);
        }

        public void setImage(Bitmap image){
            eyeView.setImageBitmap(image);
            eyeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        public void setHbLevel(double hb){
            hbLevel.setText(Utilities.formatRawHbLevel(hb));
        }
        public void setStatus(double hb, boolean isMale){
            status.setText(Utilities.getNormalStatus(isMale, hb));
        }
        public void setDate(Date date){
            resultDate.setText(Utilities.convertDateToString(date));
        }
    }

    boolean isMale;
    public DetailAdapter(Context context, RealmResults<ResultData> realmResults,
            boolean automaticUpdate,
            boolean animateResult, boolean isMale) {
        super(realmResults, automaticUpdate);
        this.isMale = isMale;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == 0){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.detailed_item, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dummy_item, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (position<5){
            final ResultData resultData = getData().get(position);
            viewHolder.setImage(PictureUtilities.getBitmapFromByteArray(resultData.getPicture()));
            viewHolder.setHbLevel(resultData.getHbLevel());
            viewHolder.setStatus(resultData.getHbLevel(), isMale);
            viewHolder.setDate(resultData.getTaken_date());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, Utilities.formatRawHbLevel(resultData.getHbLevel()));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position<5)
        return 0;
        else return 1;
    }
}
