package com.imvlabs.hbey.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.imvlabs.hbey.R;

/**
 * Created by maaakbar on 12/31/15.
 */
public class TutorialAdapter extends PagerAdapter {
    Context context;

    public TutorialAdapter(Context context) {
        this.context = context;
    }

    private int[] imageRes = {
            R.mipmap.tutor1,
            R.mipmap.tutor2,
            R.mipmap.tutor3,
            R.mipmap.tutor4,
    };

    private int[] stringRes = {
            R.string.first_tutorial,
            R.string.second_tutorial,
            R.string.third_tutorial,
            R.string.last_tutorial,
    };

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_tutorial, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.image_tutorial);
        imageView.setImageResource(imageRes[position]);

        TextView textView = (TextView) itemView.findViewById(R.id.text_tutorial);
        textView.setText(context.getString(stringRes[position]));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageRes.length; //No of Tabs
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
}
