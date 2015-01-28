package com.diandian.coolco.emilie.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonFragmentAdapter extends FragmentPagerAdapter{
    private List<Class<? extends Fragment>> fragmentClasses;
    private List<Fragment> fragments;

    public CommonFragmentAdapter(FragmentManager fm, List<Class<? extends Fragment>> fragmentClasses) {
        super(fm);
        this.fragmentClasses = fragmentClasses;
        this.fragments = new ArrayList<Fragment>(Collections.<Fragment>nCopies(fragmentClasses.size(), null));

    }

    @Override
    public Fragment getItem(int position) {
        if (fragments.get(position) == null){
            Fragment newlyCreateFragment = null;
            try {
                newlyCreateFragment = fragmentClasses.get(position).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            fragments.set(position, newlyCreateFragment);
        }
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragmentClasses.size();
    }
}
