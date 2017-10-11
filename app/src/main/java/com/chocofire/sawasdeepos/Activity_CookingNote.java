package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
 * Created by ChoCoFire on 9/25/2017.
 */

public class Activity_CookingNote extends AppCompatActivity {

    final String P_NAME = "App_Config";
    private ActionBar actionbar;
    private SharedParam shareData;
    private ListView list;
    private ListViewAdapterOrderCommand adapter;
    private ArrayList<String> strName = new ArrayList<String>();
    private ProgressDialog dialog;
    postJSON post = new postJSON();
    private String respone;
    private EditText edtCook;
    private Button btnSummitCook;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String strObjBill;
    private JSONObject jObjItemBill;
    private String strLastAccess;
    private JSONObject jObjLastAccess;
    private String strOrderID;

    @Override
    protected void onResume() {
        super.onResume();
        getCookingNoteLoader getlist = new getCookingNoteLoader();
        getlist.execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookingnote);

        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        strLastAccess = shareData.getOrderInfo();
        strObjBill = shareData.getObjSelectItem();/*{
                "SaleTypeID": 1,
                "QTY": 1,
                "ItemType": 10,
                "UnitPrice": 300,
                "OutletItem": 0,
                "ItemName1": "ถั่วรวม",
                "ItemName": "ถั่วรวม",
                "Seq": 2,
                "SaleType": "Member",
                "ItemDisplayName": "ถั่วรวม",
                "ITTypeID": 5,
                "ItemID": 383,
                "KPrintNo": 729955,
                "LMMR": 0,
                "OrderID": 282238,
                "PriceShow": 300,
                "ItemCode": "63034",
                "CountRow": 1
            }*/
        try {
            jObjLastAccess = new JSONObject(strLastAccess);
            strLastAccess = jObjLastAccess.getJSONObject("Data").getJSONObject("Order").getString("LastAccess");
            strOrderID = jObjLastAccess.getJSONObject("Data").getJSONObject("Order").getString("OrderID");
            jObjItemBill = new JSONObject(strObjBill);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setListView();

        configureToolbar();

        showDialogSpinner();

        edtCook = (EditText) findViewById(R.id.editText_cookingnote);
        btnSummitCook = (Button) findViewById(R.id.button_summit_cookingnote);
        btnSummitCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //summit cokking note
                addCookingNoteLoader addCook = new addCookingNoteLoader();
                addCook.execute();
            }
        });
    }

    public void setListView(){
        list = (ListView) findViewById(R.id.listView_cookingnote);
        adapter = new ListViewAdapterOrderCommand(getApplicationContext() , strName);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //
                edtCook.setText(edtCook.getText().toString()+" " + strName.get(position));
            }
        });
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Cooking Note");
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

    private class getCookingNoteLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResCookingNote;
        private String jErr;
        private String jMes;
        private JSONArray jArrCookingNote;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjCookingNote = new JSONObject();
            try {
                jObjCookingNote.put("OutletID",null);
                System.out.println("jObjCookingNote : " + jObjCookingNote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getCookingNote",String.valueOf(jObjCookingNote));
                System.out.println("respone jObjCookingNote: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResCookingNote = new JSONObject(respone);
                jErr = jResCookingNote.getString("ErrorCode");
                jMes = jResCookingNote.getString("ErrorMesg");
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
                    jArrCookingNote = jResCookingNote.getJSONObject("Data").getJSONArray("CookingMessage");
                    for(int countlist =0;countlist<jArrCookingNote.length();countlist++){
                        strName.add(jArrCookingNote.getJSONObject(countlist).getString("MSGName"));
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

    private class addCookingNoteLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResAddCookingNote;
        private String jErr;
        private String jMes;
        private JSONArray jArrAddCookingNote;
        private String strCookingNote;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjCookingNote = new JSONObject();
            try {
                jObjCookingNote.put("OutletID",shareData.getOutLetID());
                jObjCookingNote.put("OrderID",strOrderID);
                jObjCookingNote.put("CashierID",sp.getString("CashierID","-1"));
                jObjCookingNote.put("ItemSeq",jObjItemBill.getString("Seq"));
                jObjCookingNote.put("WSID",sp.getString("WSID","-1"));
                jObjCookingNote.put("CookingNote",strCookingNote);
                jObjCookingNote.put("LastAccess",strLastAccess);
                System.out.println("jObjCookingNote : " + jObjCookingNote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"addCookingNoteItem",String.valueOf(jObjCookingNote));
                System.out.println("respone jObjCookingNote: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResAddCookingNote = new JSONObject(respone);
                jErr = jResAddCookingNote.getString("ErrorCode");
                jMes = jResAddCookingNote.getString("ErrorMesg");
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
            strCookingNote = edtCook.getText().toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
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

}
