package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/29/2017.
 */

public class Activity_MemberRightS  extends AppCompatActivity{

    final String P_NAME = "App_Config";
    private TextView txtRightsName;
    private TextView txtPackageNo;
    private TextView txtPackageName;
    private TextView txtRightsType;
    private TextView txtConditionPack;
    private TextView txtRightsQTY;
    private TextView txtUsedQTY;
    private TextView txtRemain;
    private SharedParam shareData;
    private String strDetailMember;
    private JSONObject jDetailMember;
    private ProgressDialog dialog;
    postJSON post = new postJSON();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String strObjMemberInfo;
    private JSONObject jObjMemberIno;
    private EditText edtQTY;
    private ActionBar actionbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_memberrights);
            setTextView();

            shareData = (SharedParam) getApplication();
            sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
            editor = sp.edit();

            configureToolbar();

            edtQTY = (EditText) findViewById(R.id.editText_countOfRght);

            showDialogSpinner();

            strObjMemberInfo = shareData.getObjMemberInfo();
            strDetailMember = shareData.getPackageNo();
            /*{
                "RightName": "Glenlivet Reserve",
                "Expire": 0,
                "RCPID": 450758,
                "RightType": "Limited Quantity",
                "DocID": 2342,
                "PackageName": "สมาชิกเก่า",
                "RemainQty": 2,
                "PackageNo": 2342,
                "Qty": 6,
                "AdjQty": 0,
                "ItemID": 1553,
                "Condition": "นับจำนวน",
                "UseQty": 4
            }*/
            try {
                jDetailMember = new JSONObject(strDetailMember);
                jObjMemberIno = new JSONObject(strObjMemberInfo);
                txtRightsName.setText(jDetailMember.getString("RightName"));
                txtPackageNo.setText(jDetailMember.getString("PackageNo"));
                txtPackageName.setText(jDetailMember.getString("PackageName"));
                txtRightsType.setText(jDetailMember.getString("RightType"));
                txtConditionPack.setText(jDetailMember.getString("Condition"));
                txtRightsQTY.setText(jDetailMember.getString("Qty"));
                txtUsedQTY.setText(jDetailMember.getString("UseQty"));
                txtRemain.setText(jDetailMember.getString("RemainQty"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Button btnSummit = (Button) findViewById(R.id.button_takememberRight_summit);
            btnSummit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*{
                      "OutletID": 1,
                      "OrderID": 1,
                      "ItemID": 2,
                      "Qty": 3.0,
                      "RCPID": 4,
                      "MemberID": 5,
                      "WSID": 6,
                      "CashierID": 7,
                      "LastAccess": "sample string 8"
                    }*/
                    getMemberInfoLoader getInfo = new getMemberInfoLoader();
                    getInfo.execute();
                }
            });
    }

    public void setTextView(){
        txtRightsName = (TextView) findViewById(R.id.textView_rightsName);
        txtPackageNo = (TextView) findViewById(R.id.textView_packNo);
        txtPackageName = (TextView) findViewById(R.id.textView_packageName);
        txtRightsType = (TextView) findViewById(R.id.textView_rightType);
        txtConditionPack = (TextView) findViewById(R.id.textView_conditionCount);
        txtRightsQTY = (TextView) findViewById(R.id.textView_rightsQty);
        txtUsedQTY = (TextView) findViewById(R.id.textView_usedQty);
        txtRemain = (TextView) findViewById(R.id.textView_remainQty);
    }

    private class getMemberInfoLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResgetMemberInfo;
        private String jErr;
        private String jMes;
        private String respone;
        private String strQTY;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("OrderID",shareData.getOrderID());
                jObjGetOrder.put("ItemID",jDetailMember.getString("ItemID"));
                jObjGetOrder.put("Qty",strQTY);
                jObjGetOrder.put("RCPID",jDetailMember.getString("RCPID"));
                jObjGetOrder.put("MemberID",jObjMemberIno.getString("MemberID"));
                jObjGetOrder.put("WSID",sp.getString("WSID","-1"));
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjGetOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjtakeMemberRights : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"takeMemberRights",String.valueOf(jObjGetOrder));
                System.out.println("respone jObjtakeMemberRights: " + respone);
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
                finish();
            }else{
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            strQTY = edtQTY.getText().toString();
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

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Member Rights");
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
