package com.senate_system.sawasdeepos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class Activity_Navigation extends AppCompatActivity {

    public static DrawerLayout drawerLayout;
    private View background;
    private ProgressDialog dialog;
    private boolean checkIn;
    private int success;
    private SharedParam shareData;
    postJSON post = new postJSON();
    private TextView txtHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        shareData = (SharedParam) getApplication();

        setUI();

        showDialogSpinner();

        //setImageBtn();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView imgvw = (ImageView)hView.findViewById(R.id.imageView);
        txtHeader = (TextView) hView.findViewById(R.id.textView_NameHeader);
        txtHeader.setText(shareData.getUserName());

    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment f = null;
                int itemId = menuItem.getItemId();

                if (itemId == R.id.work) {
                    f = new Fragment_Adapter_Main();
                    /*f = new NewFirstFragment();
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    ActionBar actionbar = getSupportActionBar();
                    actionbar.setTitle("รับฝาก");
                    System.out.println("Status :First Fragment");*/
                } else if (itemId == R.id.floor) {
                    f = new Fragment_Floorplan();/*
                    internetLoader checkIn = new internetLoader();
                    checkIn.execute();*/
                } else if (itemId == R.id.setting) {
                    Intent intent = new Intent(getApplication(), Activity_CheckService.class);
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.logout) {
                    finish();
                }

                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, f);
                    transaction.commit();
                    drawerLayout.closeDrawers();
                    System.out.println("Status : ! null");
                    return true;
                }
                return false;
            }
        });
    }

    public void setUI(){
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        Menu menuNav = navView.getMenu();
        //MenuItem nav_id = menuNav.findItem(R.id.item_username);
        //MenuItem nav_user = menuNav.findItem(R.id.item_email);
        //MenuItem nav_name = menuNav.findItem(R.id.item_name);
        //nav_id.setTitle("Menu");
        //nav_user.setTitle(textUSER);
        //nav_name.setTitle(textName);


        configureNavigationDrawer(); //set click on navigat

        //=============================set first fragment on layout page 2 =====================
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, new Fragment_Floorplan());
        transaction.commit();
        System.out.println("Status :First Fragment");
        //=============================set first fragment on layout page 2 =====================

        //configureToolbar();// set toolbar
    }

    private class internetLoader extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            checkIn = executeCommand();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if(checkIn){
                showDialogError("ติดต่อเซิฟเวอร์ 192.168.0.29");
                success=1;
            }else{
                showDialogError("ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้ \n กรุณาตรวจสอบสัญญาณ");
                success=0;
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void showDialogSpinner(){
        dialog = new ProgressDialog(this);
        dialog.setTitle("กำลังดาวน์โหลดข้อมูล ...");
        dialog.setMessage("โปรดรอสักครู่ ...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER); //dialog spinning
        dialog.setCancelable(false);
    } // dialog setting

    private boolean executeCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            java.lang.Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 2 192.168.0.252");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }

    public void showDialogError(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){
                        if(success==1) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame, new Fragment_Floorplan());
                            transaction.commit();
                            drawerLayout.closeDrawers();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void switchToFragment1() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new Fragment_Adapter_Main()).commit();
    }

    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Logout");
        dialog.setIcon(R.drawable.ic_drawer);
        dialog.setCancelable(true);
        dialog.setMessage("Do you want to logout?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // ยังไม่มี service logout
                finish(); // close fragment
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    } // check back press

    public class postJSON { //posthttp class
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String postForm(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .addHeader("auth_key",shareData.getAuth_key())
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    } // post from JSONobject
}
