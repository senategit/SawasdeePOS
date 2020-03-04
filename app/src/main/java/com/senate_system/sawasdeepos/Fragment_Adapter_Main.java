package com.senate_system.sawasdeepos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class Fragment_Adapter_Main extends Fragment {

    private View myViewMain;
    private ViewPager pager;
    private TabLayout tabLayout;
    private SharedParam shareData;

    public Fragment_Adapter_Main() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getContext().getApplicationContext(), "resume main", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewMain = (View) inflater.inflate(R.layout.tab_layout_workspace, container, false);

        pager = (ViewPager) myViewMain.findViewById(R.id.pagerWoke);

        shareData = (SharedParam) getActivity().getApplication();
        shareData.setCheckSearchPage("Frag");

        FragmentManager manager = getChildFragmentManager();
        PagerAdapterWorkspace adapter = new PagerAdapterWorkspace(manager);
        pager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(pager);
        // mTabLayout.setupWithViewPager(mPager1);
        //pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.setTabsFromPagerAdapter(adapter);

        return myViewMain;
    }
}