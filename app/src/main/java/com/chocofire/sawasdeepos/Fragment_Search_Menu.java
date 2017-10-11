package com.chocofire.sawasdeepos;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class Fragment_Search_Menu extends Fragment {

    private View myViewMainSearch;
    private ViewPager pager;
    private TabLayout tabLayout;
    private Button btnSearch;
    private int Select;
    private SharedParam shareData;
    private PagerAdapterSearchMenu adapter;
    private EditText edtSearch;
    private ImageView imgBack;
    private ViewPager activityPager;

    public Fragment_Search_Menu() {
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
        myViewMainSearch = (View) inflater.inflate(R.layout.tab_layout_search_menu, container, false);

        shareData = (SharedParam) getActivity().getApplication();

        edtSearch = (EditText) myViewMainSearch.findViewById(R.id.editTextlist_search);
        pager = (ViewPager) myViewMainSearch.findViewById(R.id.pager);
        tabLayout = (TabLayout) myViewMainSearch.findViewById(R.id.tab_layout_takeorder);
        activityPager = (ViewPager) getActivity().findViewById(R.id.pagerWoke);

        imgBack = (ImageView) myViewMainSearch.findViewById(R.id.imageView_backButton_1);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ย้อนกลับไปหน้า fragment Adapter
                activityPager.setCurrentItem(0);
            }
        });

        btnSearch = (Button) myViewMainSearch.findViewById(R.id.button_SearchSummit);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Select==0){
                    Select=5;
                }
                shareData.setTabSelected(String.valueOf(Select));
                updateSecondFragment();
            }
        });

        FragmentManager manager = getChildFragmentManager();
        adapter=new PagerAdapterSearchMenu(manager);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // mTabLayout.setupWithViewPager(mPager1);
        //pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //tabLayout.setTabsFromPagerAdapter(adapter);
        setupTabIcons();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                shareData.setCheckSearchPage("Frag");
                Select = position;
                shareData.setTabSelected(String.valueOf(position));
                switch (position){
                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.bill_info_orange);
                        tabLayout.getTabAt(1).setIcon(R.drawable.member_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.resource_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.product_white);
                        break;
                    case 1 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.bill_info_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.member_orange);
                        tabLayout.getTabAt(2).setIcon(R.drawable.resource_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.product_white);
                        break;
                    case 2 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.bill_info_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.member_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.resource_orange);
                        tabLayout.getTabAt(3).setIcon(R.drawable.product_white);
                        break;
                    case 3 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.bill_info_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.member_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.resource_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.product_orange);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return myViewMainSearch;
    }

    private void updateSecondFragment(){

        //Way to get TagName which generated by FragmentPagerAdapter
        //String tagName = "android:switcher:"+R.id.pagerWoke+":1"; // Your pager name & tab no of Second Fragment
        //String a = edtSearch.getText().toString();

        //Get SecondFragment object from FirstFragment
        /*.Fragment_Search_Member f2 = (Fragment_Search_Member)getActivity().getSupportFragmentManager().findFragmentByTag(tagName);
        System.out.println(f2);*/
        //Then call your wish method from SecondFragment to update appropriate list

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private void setupTabIcons() {// set icon in tab
        tabLayout.getTabAt(0).setIcon(R.drawable.bill_info_orange);
        tabLayout.getTabAt(1).setIcon(R.drawable.member_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.resource_white);
        tabLayout.getTabAt(3).setIcon(R.drawable.product_white);
    }

}
