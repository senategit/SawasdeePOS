package com.senate_system.sawasdeepos;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by ChoCoFire on 9/6/2017.
 */

public class PagerAdapterTakeOrder extends FragmentPagerAdapter {
    public PagerAdapterTakeOrder(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new Fragment_TakeOrder_Main();
                break;
            case 1:
                frag=new Fragment_TakeOrder_Hotkey();
                break;
            case 2:
                frag=new Fragment_TakeOrder_Numpad();
                break;
            case 3:
                frag=new Fragment_TakeOrder_Command();
                break;
            case 4:
                frag=new Fragment_TakeOrder_Resource();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="Take Order";
                break;
            case 1:
                title="Hotkey";
                break;
            case 2:
                title="Numpad";
                break;
            case 3:
                title="Command";
                break;
            case 4:
                title="Resource";
                break;
        }

        return title;
    }
}