package com.senate_system.sawasdeepos;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class PagerAdapterWorkspace extends FragmentPagerAdapter {
    public PagerAdapterWorkspace(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new Fragment_Workspace();
                break;
            case 1:
                frag=new Fragment_Search_Menu();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
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
        }

        return title;
    }
}