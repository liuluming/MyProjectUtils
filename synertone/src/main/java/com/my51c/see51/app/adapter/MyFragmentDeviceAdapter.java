package com.my51c.see51.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentDeviceAdapter extends FragmentPagerAdapter {

    // 管理 Fragment 的一个 列表
    private List<Fragment> fragments;

    public MyFragmentDeviceAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    // 返回当前位置，显示的Fragment
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    // 返回值，决定了页面的多少
    @Override
    public int getCount() {
        return fragments.size();
    }
}
