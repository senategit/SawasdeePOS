package com.senate_system.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/29/2017.
 */

public class Activity_MemberInfo extends AppCompatActivity {

    final String P_NAME = "App_Config";
    private ProgressDialog dialog;
    SharedParam shareData;
    private ListView list;
    private ListViewAdapterMemberInfo adapter;
    private ArrayList<String> strRightName = new ArrayList<String>();
    private ArrayList<String> strPackNo = new ArrayList<String>();
    private ArrayList<String> strRight = new ArrayList<String>();
    private ArrayList<String> strUsed = new ArrayList<String>();
    private ArrayList<String> strRemain = new ArrayList<String>();
    private ArrayList<Bitmap> bmpImg = new ArrayList<Bitmap>();
    private TextView txtMemberNameINfo;
    private TextView txtMemberCodeInfo;
    private TextView txtMobile;
    private TextView txtRegis;
    private TextView txtExp;
    private TextView txtSale;
    private String strDetailMember;
    private JSONObject jDetailMember;
    private String respone;
    postJSON post = new postJSON();
    private String MemberID;
    private JSONArray jArrgetMemberInfo;
    private ActionBar actionbar;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String strObjBill;
    private JSONObject jObjBill;
    private String strMember;
    private Toolbar toolbar;

    @Override
    protected void onResume() {
        super.onResume();
        getMemberInfoLoader memonfoLoad = new getMemberInfoLoader();
        memonfoLoad.execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);

        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        configureToolbar();

        final View headerView = (View) findViewById(R.id.expandAndcollap);

        txtMemberNameINfo = (TextView) findViewById(R.id.textView_memberNameINFO);
        txtMemberCodeInfo = (TextView) findViewById(R.id.textView_memberCodeINFO);
        txtMobile = (TextView) findViewById(R.id.textView_mobileINFO);
        txtRegis = (TextView) findViewById(R.id.textView_regisDateINFO);
        txtExp = (TextView) findViewById(R.id.textView_expDateINFO);
        txtSale = (TextView) findViewById(R.id.textView_SaleINFO);

        //Set MemberID
        if(shareData.getIsMemberInOrder()){
            MemberID = shareData.getOrderMemberID();
        } else {
            strDetailMember = shareData.getObjMemberInfo();
            /*{
                "SaleName": "ไวน์ ชยาทิพย์  เจริญธนานันท์",
                "ExpireDateBean": {
                    "Year": 2551,
                    "Month": 9,
                    "Day": 20,
                    "Hour": 0,
                    "Minute": 0,
                    "Second": 0,
                    "Millisecond": 0,
                    "Ticks": "804928320000000000"
                },
                "MemberGroup": 1,
                "MemberType": 1,
                "Memo": "20",
                "RegisDateBean": {
                    "Year": 2550,
                    "Month": 9,
                    "Day": 20,
                    "Hour": 0,
                    "Minute": 0,
                    "Second": 0,
                    "Millisecond": 0,
                    "Ticks": "804612960000000000"
                },
                "MemberID": 32,
                "SaleID": 48,
                "MemberName": "เชาว์",
                "FirstName": "เชาว์",
                "RegisDate": 804612960000000000,
                "MemberCode": "1029",
                "CreditLimit": 20000,
                "ExpireDate": 804928320000000000
            }*/

            try {
                jDetailMember = new JSONObject(strDetailMember);
                MemberID = jDetailMember.getString("MemberID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        showDialogSpinner();
        //definelist
        list =(ListView) findViewById(R.id.listView_MemberInfo);
        adapter = new ListViewAdapterMemberInfo(getApplicationContext(), strRightName , strPackNo
            , strRight , strUsed , strRemain );
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getmember info
                try {
                    //do not open member-rights page if is not take-order state
                    if(shareData.getOrderID() == null) return;
                    if(shareData.getOrderInfo() == null) return;

                    shareData.setPackageNo(jArrgetMemberInfo.getString(position));
                    Intent intent = new Intent(getApplication(), Activity_MemberRightS.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //expand(headerView);
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        SettingOptionMember();

    }

    public void SettingOptionMember(){
        strObjBill = shareData.getOrderInfo();
        try {
            jObjBill = new JSONObject(strObjBill);
            if(jObjBill.getJSONObject("Data").getJSONObject("Order").has("MemberDispName")) strMember = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("MemberDispName");
            else strMember = ""; // ไม่มี member ผูกกับ order
            /*if(strMember.equals("")) {
                toolbar.getMenu().findItem(R.id.action_choose_member).setVisible(false);
                toolbar.getMenu().findItem(R.id.action_Remove_member).setVisible(true);
                toolbar.getMenu().clear();
                toolbar.getMenu().add(R.id.action_choose_member);
                toolbar.getMenu().removeItem(R.id.action_Remove_member);
            }
            else {
                toolbar.getMenu().findItem(R.id.action_choose_member).setVisible(true);
                toolbar.getMenu().findItem(R.id.action_Remove_member).setVisible(false);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class getMemberInfoLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResgetMemberInfo;
        private String jErr;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("MemberID",MemberID);
                System.out.println("jObjgetMemberInfo : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getMemberInfo",String.valueOf(jObjGetOrder));
                System.out.println("respone jObjgetMemberInfo: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResgetMemberInfo = new JSONObject(respone);
                jErr = jResgetMemberInfo.getString("ErrorCode");
                jMes = jResgetMemberInfo.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {

            if(jErr.equals("1")){
                //
                try {
                    shareData.setObjMemberInfo(jResgetMemberInfo.getJSONObject("Data").getJSONArray("MemberInfo").getJSONObject(0).toString());

                    JSONObject mminfo = jResgetMemberInfo.getJSONObject("Data").getJSONArray("MemberInfo").getJSONObject(0);
                    if(mminfo.has("MemberName")) txtMemberNameINfo.setText(mminfo.getString("MemberName"));
                    else txtMemberNameINfo.setText("");
                    if(mminfo.has("MemberCode")) txtMemberCodeInfo.setText(mminfo.getString("MemberCode"));
                    else txtMemberCodeInfo.setText("");
                    if(mminfo.has("Mobile")) txtMobile.setText(mminfo.getString("Mobile"));
                    else txtMobile.setText("");
                    if(mminfo.has("SaleName")) txtSale.setText(mminfo.getString("SaleName"));
                    else txtSale.setText("");
                    if(mminfo.has("RegisDateText")) txtRegis.setText(mminfo.getString("RegisDateText"));
                    else txtRegis.setText("");
                    if(mminfo.has("ExpireDateText")) txtExp.setText(mminfo.getString("ExpireDateText"));
                    else txtExp.setText("");

                    strRightName.clear();
                    strPackNo.clear();
                    strRight.clear();
                    strUsed.clear();
                    strRemain.clear();
                    bmpImg.clear();

                    jArrgetMemberInfo = jResgetMemberInfo.getJSONObject("Data").getJSONArray("MemberRightsList");
                    for(int count=0;count<jArrgetMemberInfo.length();count++){
                        strRightName.add(jArrgetMemberInfo.getJSONObject(count).getString("RightName"));
                        strPackNo.add(jArrgetMemberInfo.getJSONObject(count).getString("PackageNo"));
                        strRight.add(jArrgetMemberInfo.getJSONObject(count).getString("Qty"));
                        strUsed.add(jArrgetMemberInfo.getJSONObject(count).getString("UseQty"));
                        strRemain.add(jArrgetMemberInfo.getJSONObject(count).getString("RemainQty"));
                        bmpImg.add(BitmapFactory.decodeResource(getResources(), R.drawable.raw));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showDialogError(jMes);
            }
            adapter.notifyDataSetChanged();
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

    public void showDialogSpinner(){
        dialog = new ProgressDialog(this);
        dialog.setTitle("กำลังดาวน์โหลดข้อมูล ...");
        dialog.setMessage("โปรดรอสักครู่ ...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER); //dialog spinning
        dialog.setCancelable(false);
    } // dialog setting

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

    public static void expand(final View v) {
        v.measure(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Member Information");
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
        }else if(id == R.id.action_choose_member){
            //choose member assign member to order
            assignMemberToOrderLoader assisgnLoad = new assignMemberToOrderLoader();
            assisgnLoad.execute();
        }else if(id == R.id.action_Remove_member){
            //remove
            removeMemberFromOrderLoader removeLoad = new removeMemberFromOrderLoader();
            removeLoad.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_member_information, menu);
        if(strMember.equals("")) {
            menu.removeItem(R.id.action_Remove_member);
        }
        else {
            menu.removeItem(R.id.action_choose_member);
        }
        return true;
    }

    private class assignMemberToOrderLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResassignMemberToOrder;
        private String jErr;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("OrderID",shareData.getOrderID());
                jObjGetOrder.put("MemberID",MemberID);
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjGetOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjassignMemberToOrder : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"assignMemberToOrder",String.valueOf(jObjGetOrder));
                shareData.setOrderInfo(respone);
                System.out.println("respone jObjassignMemberToOrder: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResassignMemberToOrder = new JSONObject(respone);
                jErr = jResassignMemberToOrder.getString("ErrorCode");
                jMes = jResassignMemberToOrder.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {

            if(jErr.equals("1")){
                // ปิดหน้าจอนี้
                finish();
            }else{
                showDialogError(jMes);
            }
            adapter.notifyDataSetChanged();
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

    private class removeMemberFromOrderLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResremoveMemberFromOrder;
        private String jErr;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("OrderID",shareData.getOrderID());
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjGetOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjremoveMemberFromOrder : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"removeMemberFromOrder",String.valueOf(jObjGetOrder));
                shareData.setOrderInfo(respone);
                System.out.println("respone jObjremoveMemberFromOrder: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResremoveMemberFromOrder = new JSONObject(respone);
                jErr = jResremoveMemberFromOrder.getString("ErrorCode");
                jMes = jResremoveMemberFromOrder.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {

            if(jErr.equals("1")){
                // ปิดหน้าจอนี้
                finish();
            }else{
                showDialogError(jMes);
            }
            adapter.notifyDataSetChanged();
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

}
