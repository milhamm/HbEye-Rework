package com.imvlabs.hbey.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.imvlabs.hbey.Fragment.CheckUpFragment;
import com.imvlabs.hbey.Fragment.HistoryFragment;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public CheckUpFragment checkUpFragment;

    public HomePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        checkUpFragment = new CheckUpFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Fragment for ID Tab
                return checkUpFragment;
            case 1:
                //Fragement for Camera Tab
                return new HistoryFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mNumOfTabs; //No of Tabs
    }
}
