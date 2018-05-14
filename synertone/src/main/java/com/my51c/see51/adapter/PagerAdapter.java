package com.my51c.see51.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.my51c.see51.app.fragment.Call_All_Fragment;
import com.my51c.see51.app.fragment.No_Call_Fragment;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list_fragments;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        list_fragments = new ArrayList<Fragment>();
        list_fragments.add(new Call_All_Fragment());
        list_fragments.add(new No_Call_Fragment());
    }

    @Override
    public Fragment getItem(int arg0) {
        return list_fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return list_fragments != null ? list_fragments.size() : 0;
    }

}
