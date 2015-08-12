package com.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;
    private int Type;
    private final int OPTION1 = 1;
    private final int OPTION2 = 2;

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, int type) {
        super(fm);
        this.fragmentsList = fragments;
        Type = type;
    }



    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (Type){
            case OPTION1:
                if(position == 0){
                    return "任务列表";
                }else if(position == 1){
                    return "项目列表";
                }else {
                    return "统计";
                }

            case OPTION2:
                if(position == 0){
                    return "番茄工作法";
                }else {
                    return "习惯养成计划";
                }

        }
        return "Page " + (position + 1);
    }
}
