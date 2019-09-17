package com.cypress.academy.ble101_robot;


//import androidx.core.app.Fragment;
//import androidx.core.app.FragmentManager;
//import androidx.core.app.FragmentPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public ViewPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new FragmentContents();
        }
       /* else if (position == 1)
        {
            fragment = new FragmentB();
        }
        else if (position == 2)
        {
            fragment = new FragmentC();
        }*/
        return new FragmentContents();
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    /*@Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Tab-1";
        }
        else if (position == 1)
        {
            title = "Tab-2";
        }
        else if (position == 2)
        {
            title = "Tab-3";
        }
        return title;
    }*/
}
