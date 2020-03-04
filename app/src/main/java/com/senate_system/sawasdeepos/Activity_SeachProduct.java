package com.senate_system.sawasdeepos;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by ChoCoFire on 10/2/2017.
 */

public class Activity_SeachProduct extends AppCompatActivity {

    private SharedParam shareData;
    private ActionBar actionbar;
    private LinearLayout linearLayout;
    private LinearLayout linearSupbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchproduct);

        View header = (View) findViewById(R.id.include_header_Search_Product);
        header.setVisibility(View.VISIBLE);
        linearSupbar = (LinearLayout) findViewById(R.id.linear_supbar);
        linearSupbar.setVisibility(View.GONE);

        shareData = (SharedParam) getApplication();
        shareData.setCheckSearchPage("Act");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_member, new Fragment_Search_Product());
        transaction.commit();

        configureToolbar();
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Search Product");
        actionbar.setHomeAsUpIndicator(R.drawable.back_copy);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        /*Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_drawer);
        toolbar.setOverflowIcon(drawable);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // ย้อนกลับ
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
