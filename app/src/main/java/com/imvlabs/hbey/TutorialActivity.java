package com.imvlabs.hbey;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.imvlabs.hbey.Adapter.TutorialAdapter;

public class TutorialActivity extends AppCompatActivity {
//    String TAG = "TutorialActivity"; is define below

    private ImageButton btnNext, btnFinish;
    private ViewPager viewPager;
    private int dotsCount;
    private ImageView[] dots;
    private LinearLayout pager_indicator;
    private TutorialAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.tutorial_pager);

        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        btnNext = (ImageButton)findViewById(R.id.btn_next);
        btnFinish = (ImageButton)findViewById(R.id.btn_finish);

        mAdapter = new TutorialAdapter(TutorialActivity.this);

        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        btnNext.setOnClickListener(clickListener);
        btnFinish.setOnClickListener(clickListener);

        setUiPageViewController();
    }

    ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.nonselecteditem_dot));
            }

            dots[position].setImageDrawable(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.selecteditem_dot));

            if (position + 1 == dotsCount) {
                btnNext.setVisibility(View.GONE);
                btnFinish.setVisibility(View.VISIBLE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setUiPageViewController() {
        String TAG = "TutorialActivity";

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];
        Log.i(TAG, "before bulleting");
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.selecteditem_dot));
        Log.i(TAG, "after bulleting");
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_next:
                    viewPager.setCurrentItem((viewPager.getCurrentItem() < dotsCount)
                            ? viewPager.getCurrentItem() + 1 : 0);
                    break;
                case R.id.btn_finish:
                    finish();
                    break;
            }
        }
    };
}
