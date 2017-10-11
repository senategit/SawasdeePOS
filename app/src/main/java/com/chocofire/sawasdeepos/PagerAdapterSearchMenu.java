package com.chocofire.sawasdeepos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class PagerAdapterSearchMenu extends FragmentPagerAdapter {
    public PagerAdapterSearchMenu(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new Fragment_Search_Bill();
                break;
            case 1:
                frag=new Fragment_Search_Member();
                break;
            case 2:
                frag=new Fragment_Search_Resource();
                break;
            case 3:
                frag=new Fragment_Search_Product();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="Bill";
                break;
            case 1:
                title="Member";
                break;
            case 2:
                title="Resource";
                break;
            case 3:
                title="Product";
                break;
        }

        return title;
    }
}