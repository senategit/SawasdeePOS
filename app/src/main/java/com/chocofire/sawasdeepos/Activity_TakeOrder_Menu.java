package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by ChoCoFire on 9/6/2017.
 */

public class Activity_TakeOrder_Menu extends AppCompatActivity {

    final String P_NAME = "App_Config";
    private ViewPager pager;
    private TabLayout tabLayout;
    private ActionBar actionbar;
    private SharedParam shareData;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private OkHttpClient client;
    private int countSend=1;
    private Button send;
    private String strUUID;
    private JSONObject jObjDataInfo;
    private JSONObject jObjParam;
    private JSONObject jObjData;
    private JSONObject jObjSend;
    private JSONObject jObj;
    private String strGlobal;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeorder_menu);

        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        showDialogSpinner();

        setWebSocket();

        configureToolbar();

        pager= (ViewPager) findViewById(R.id.pagerTakeOrder);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout_takeorder);

        FragmentManager manager= getSupportFragmentManager();
        PagerAdapterTakeOrder adapter=new PagerAdapterTakeOrder(manager);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
        // mTabLayout.setupWithViewPager(mPager1);
        //pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //tabLayout.setTabsFromPagerAdapter(adapter);
        setupTabIcons();

        //setupTablayout();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View header = (View) findViewById(R.id.include_header);
                View header2 = (View) findViewById(R.id.include_header2);
                System.out.println(position);
                if(getSupportFragmentManager().findFragmentById(R.id.frame_newFrag) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.frame_newFrag)).commit();
                }

                LinearLayout linear = (LinearLayout) findViewById(R.id.linear_supbar);

                switch (position){
                    case 0 :
                        actionbar.setTitle("Take Order");
                        header.setVisibility(View.VISIBLE);
                        header2.setVisibility(View.GONE);
                        tabLayout.getTabAt(0).setIcon(R.drawable.take_order_orange);
                        tabLayout.getTabAt(1).setIcon(R.drawable.hotkey_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.numpad_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.order_command_white);
                        tabLayout.getTabAt(4).setIcon(R.drawable.resource_white);
                        break;
                    case 1 :
                        actionbar.setTitle("Hotkey");
                        header.setVisibility(View.GONE);
                        header2.setVisibility(View.VISIBLE);
                        linear.setVisibility(View.VISIBLE);
                        tabLayout.getTabAt(0).setIcon(R.drawable.take_order_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.hotkey_orange);
                        tabLayout.getTabAt(2).setIcon(R.drawable.numpad_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.order_command_white);
                        tabLayout.getTabAt(4).setIcon(R.drawable.resource_white);
                        break;
                    case 2 :
                        actionbar.setTitle("Numpad");
                        header.setVisibility(View.GONE);
                        header2.setVisibility(View.VISIBLE);
                        linear.setVisibility(View.INVISIBLE);
                        tabLayout.getTabAt(0).setIcon(R.drawable.take_order_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.hotkey_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.numpad_orange);
                        tabLayout.getTabAt(3).setIcon(R.drawable.order_command_white);
                        tabLayout.getTabAt(4).setIcon(R.drawable.resource_white);
                        break;
                    case 3 :
                        actionbar.setTitle("Order Command");
                        header.setVisibility(View.GONE);
                        header2.setVisibility(View.GONE);
                        tabLayout.getTabAt(0).setIcon(R.drawable.take_order_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.hotkey_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.numpad_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.order_command_orange);
                        tabLayout.getTabAt(4).setIcon(R.drawable.resource_white);
                        break;
                    case 4 :
                        actionbar.setTitle("Resource Management");
                        header.setVisibility(View.GONE);
                        header2.setVisibility(View.GONE);
                        tabLayout.getTabAt(0).setIcon(R.drawable.take_order_white);
                        tabLayout.getTabAt(1).setIcon(R.drawable.hotkey_white);
                        tabLayout.getTabAt(2).setIcon(R.drawable.numpad_white);
                        tabLayout.getTabAt(3).setIcon(R.drawable.order_command_white);
                        tabLayout.getTabAt(4).setIcon(R.drawable.resource_orange);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupTabIcons() {// set icon in tab
        tabLayout.getTabAt(0).setIcon(R.drawable.take_order_orange);
        tabLayout.getTabAt(1).setIcon(R.drawable.hotkey_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.numpad_white);
        tabLayout.getTabAt(3).setIcon(R.drawable.order_command_white);
        tabLayout.getTabAt(4).setIcon(R.drawable.resource_white);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_takeOrder);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Take Order");
        actionbar.setHomeAsUpIndicator(R.drawable.printer_small_white);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        /*Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_drawer);
        toolbar.setOverflowIcon(drawable);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                // print ใบส่งห้องครัว
                if(executeCommand()) {
                    startWebSocket();
                }else{
                    // ให้ไป new bill ก่อนแล้วไปหน้า take order
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("ไม่สามารถเชื่อมต่อ :" + shareData.getHWURL());
                    dialog.setIcon(R.drawable.ic_drawer);
                    dialog.setCancelable(true);
                    dialog.setMessage("ต้องการกลับไปหน้า floorplan ใช่หรือไม่");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }

                shareData.setOrderInfo(null);
                shareData.setOrderID(null);
            } else if (id == R.id.bill_information) {
                Fragment f = new Fragment_new_BillInf();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.frame_newFrag, f);
                transaction.commit();
                actionbar.setTitle("Bill Information");
            } else if (id == R.id.search_resouce) {
                shareData.setTabSelected("99");
                shareData.setResourceWay("2"); // มาจาก Takeorder
                shareData.setFreeDrink(false);
                Intent intent = new Intent(this, Activity_SearchResource.class);
                startActivity(intent);
                this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }

        } catch (Exception e) {
            String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace();
            showDialogError(errmsg);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override//print & billinfo
    public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
        return true;
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            /*{"DataInfo":{"UUID":null,"OperationName":null,"OperationMethod":0,"Parameters":null,
            "SessionID":"7d700325-9ccf-4cb3-bb90-237f2849da65","MessageType":-1,
            "SRC":"Server(92bc47af-1ec6-4cdb-b760-360ac945ca15)","DST":null},"Data":"Connection established."}*/
                try {
                    jObjDataInfo.put("UUID", strUUID);
                    jObjDataInfo.put("OperationName", "prn/kitchen");
                    jObjDataInfo.put("OperationMethod", 1); //0:get
                    jObjDataInfo.put("Parameters", null);
                    jObjDataInfo.put("SessionID", jObj.getJSONObject("DataInfo").getString("SessionID"));
                    jObjDataInfo.put("MessageType", -1);
                    jObjDataInfo.put("SRC", jObj.getJSONObject("DataInfo").getString("SRC"));
                    jObjDataInfo.put("DST", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jObjSend.put("DataInfo", jObjDataInfo);
                    jObjSend.put("Data", jObjParam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try{
                    System.out.println(jObjSend);
                    webSocket.send(jObjSend.toString());
                    webSocket.close(NORMAL_CLOSURE_STATUS,"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("Receiving : " + text);
            if (countSend ==1){
                strGlobal = text;
                try {
                    jObj = new JSONObject(strGlobal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                countSend++;
            }
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            System.out.println("Receiving bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            System.out.println("Closing : " + code + " / " + reason);
            finish();
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            System.out.println("Error : " + t.getMessage());
            //showDialogError("Error : " + t.getMessage());
            output(t.getMessage());
        }
    }

    private void startWebSocket() {
        try {
            if(shareData.getHWURL() == null) {
                if (sp == null) sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                String strHWurl = sp.getString("HWURL","");
                shareData.setHWURL(strHWurl);
            }
            if(shareData.getHWURL().trim().length() == 0) return;

            if(shareData.getHWPORT() == null) {
                if (sp == null) sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                String strHWport = sp.getString("HWPORT","");
                shareData.setHWPORT(strHWport);
                return;
            }
            if(shareData.getHWPORT().trim().length() == 0) return;

            String url = "ws://" + shareData.getHWURL() + ":" + shareData.getHWPORT();
            Request request = new Request.Builder().url(url).build();
            EchoWebSocketListener listener = new EchoWebSocketListener();
            WebSocket ws = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
            System.out.println("ws :"+ws);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWebSocket(){
        client = new OkHttpClient();

        strUUID = UUID.randomUUID().toString();

        jObjSend = new JSONObject();
        jObjParam = new JSONObject();
        try {
            jObjParam.put("OutletID",shareData.getOutLetID());
            jObjParam.put("WSID",sp.getString("WSID","1"));
            jObjParam.put("UserID",sp.getString("UserID","1"));
            jObjParam.put("OrderID",shareData.getOrderID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jObjData = new JSONObject();
        try {
            jObjData.put("Data",jObjParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jObj = new JSONObject();
        jObjDataInfo = new JSONObject();
    }

    public void showDialogError(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean executeCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            java.lang.Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+shareData.getHWURL());
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

    private void showDialogSpinner(){
        dialog = new ProgressDialog(this);
        dialog.setTitle("กำลังดาวน์โหลดข้อมูล ...");
        dialog.setMessage("โปรดรอสักครู่ ...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER); //dialog spinning
        dialog.setCancelable(false);
    } // dialog setting

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                settingErrorUiThread(txt);
            }
        });
    }

    public void settingErrorUiThread(String s){
        // ให้ไป new bill ก่อนแล้วไปหน้า take order
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("กรุณาตรวจเช็ค Hardware Interface หรือ firewall");
        dialog.setIcon(R.drawable.ic_drawer);
        dialog.setCancelable(true);
        dialog.setMessage("ต้องการกลับไปหน้า floorplan ใช่หรือไม่");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
