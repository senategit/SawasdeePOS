package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 10/3/2017.
 */

public class Activity_ResourceInfo extends AppCompatActivity implements OnDateSetListener {

    final String P_NAME = "App_Config";
    private TextView txtTimeShow;
    private TextView btnStart;
    private SharedParam shareData;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    postJSON post = new postJSON();
    private Date currentTime;
    private Button btnCurrent;
    private Button btnAdjust;
    private TextView txtName;
    private TextView txtCode;
    private TextView txtType;
    private TextView txtRate;
    private JSONObject jObjResource;
    private LinearLayout linearAdjust;
    private LinearLayout linearShowTIme;
    private Bitmap bmpRED;
    private Bitmap bmpYELLOW;
    private Bitmap bmpBlack;
    private Bitmap bmpGreen;
    private ImageView imgView;
    private ActionBar actionbar;
    private String strAllDetail;
    private JSONObject jAllDetail;
    com.jzxiang.pickerview.TimePickerDialog mDialogAll;
    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    protected void onResume() {
        super.onResume();
        getServerDateTimeLoader getTimeLoad = new getServerDateTimeLoader();
        getTimeLoad.execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resourceinfo);

        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        configureToolbar();

        imgView = (ImageView) findViewById(R.id.imageView_resourcePageimg);

        bmpRED = BitmapFactory.decodeResource(getResources(), R.drawable.drink_red);
        bmpYELLOW = BitmapFactory.decodeResource(getResources(), R.drawable.drink_yellow);
        bmpBlack = BitmapFactory.decodeResource(getResources(), R.drawable.drink_black);
        bmpGreen = BitmapFactory.decodeResource(getResources(), R.drawable.drink_green);

        try {
            jObjResource = new JSONObject(shareData.getResourceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setUp();

        showDialogSpinner();

        txtTimeShow = (TextView) findViewById(R.id.textView_TimeShow);

        btnCurrent = (Button) findViewById(R.id.button_ResourceInfo_Current);
        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServerDateTimeLoader getLoad = new getServerDateTimeLoader();
                getLoad.execute();
            }
        });

        btnStart = (TextView) findViewById(R.id.textView_Start_onReourceInfo);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareData.getChangeWay().equals("99")){
                    shareData.setChangeWay("-1");
                    changeTimerLoader changeLoad = new changeTimerLoader();
                    changeLoad.execute();
                }else {
                    startTimerLoader startLoader = new startTimerLoader();
                    startLoader.execute();
                }
            }
        });

        btnAdjust = (Button) findViewById(R.id.button_Adjust);
        btnAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });





        linearAdjust = (LinearLayout) findViewById(R.id.linearAdjust);
        linearShowTIme = (LinearLayout) findViewById(R.id.linearShowTIme);
        txtName = (TextView) findViewById(R.id.textView_resourceName);
        txtCode = (TextView) findViewById(R.id.textView_resourceCode);
        txtType = (TextView) findViewById(R.id.textView_type);
        txtRate = (TextView) findViewById(R.id.textView_rate);
        System.out.println("logCHeck    ############ : " + shareData.getChangeWay() + " ###### " + shareData.getResourceWay());
        /*if(shareData.getFreeDrink()){//ดื่มลอย
            ResourceID
        }*/
        if(shareData.getChangeWay().equals("99")){ // ต้องการ change resource
            strAllDetail = shareData.getObjSelectResource();
            try {
                jAllDetail = new JSONObject(strAllDetail);
                txtName.setText(jObjResource.getString("ResourceName"));
                txtCode.setText(jObjResource.getString("ResourceCode"));
                txtType.setText(jObjResource.getString("ResourceTypeName"));
                txtRate.setText(jObjResource.getString("TimerRate")+" minutes");
                switch (jObjResource.getString("ResourceStatus")) {
                    case "0": // ไม่มาทำงาน สีดำ
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpBlack);
                        break;
                    case "1": // ไม่ว่าง สีแดง
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpRED);
                        break;
                    case "2": // กำลังจะว่าง สีเหลือง
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpYELLOW);
                        break;
                    case "3": // ว่าง สีเขียว
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        btnStart.setText("Change");
                        imgView.setImageBitmap(bmpGreen);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(shareData.getResourceWay().equals("1")) { // มาจาก fragment search // แค่ดูอย่างเดียว
            try {
                txtName.setText(jObjResource.getString("ResourceName"));
                txtCode.setText(jObjResource.getString("ResourceCode"));
                txtType.setText(jObjResource.getString("ResourceTypeName"));
                txtRate.setText(jObjResource.getString("TimerRate")+" minutes");
                switch (jObjResource.getString("ResourceStatus")) {
                    case "0": // ไม่มาทำงาน สีดำ
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpBlack);
                        break;
                    case "1": // ไม่ว่าง สีแดง
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpRED);
                        break;
                    case "2": // กำลังจะว่าง สีเหลือง
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpYELLOW);
                        break;
                    case "3": // ว่าง สีเขียว
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpGreen);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(shareData.getResourceWay().equals("2")){ // มาจากหน้า takeorder ต้องการแอด
            try {
                txtName.setText(jObjResource.getString("ResourceName"));
                txtCode.setText(jObjResource.getString("ResourceCode"));
                txtType.setText(jObjResource.getString("ResourceTypeName"));
                try {
                    txtRate.setText(jObjResource.getString("TimerRate" + " minutes"));
                }catch (JSONException e){
                    txtRate.setText("-");
                }
                switch (jObjResource.getString("ResourceStatus")) {
                    case "0": // ไม่มาทำงาน สีดำ
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpBlack);
                        break;
                    case "1": // ไม่ว่าง สีแดง
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpRED);
                        break;
                    case "2": // กำลังจะว่าง สีเหลือง
                        linearAdjust.setVisibility(View.GONE);
                        linearShowTIme.setVisibility(View.GONE);
                        btnStart.setVisibility(View.INVISIBLE);
                        imgView.setImageBitmap(bmpYELLOW);
                        break;
                    case "3": // ว่าง สีเขียว
                        linearAdjust.setVisibility(View.VISIBLE);
                        linearShowTIme.setVisibility(View.VISIBLE);
                        btnStart.setVisibility(View.VISIBLE);
                        imgView.setImageBitmap(bmpGreen);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void setUp(){
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("Cancel")
                .setSureStringId("Ok")
                .setTitleStringId("TimePicker")
                .setYearText("Year")
                .setMonthText("Month")
                .setDayText("Day")
                .setHourText("Hour")
                .setMinuteText("Minute")
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis() - tenYears)
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(30)
                .build();
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Resource Information");
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

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String text = getDateToString(millseconds);
        txtTimeShow.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private class startTimerLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResstartTimer;
        private String jErr;
        private String jMes;
        private JSONArray jArrstartTimer;
        private String respone;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjstartTimer= new JSONObject();
            try {
                jObjstartTimer.put("OutletID",shareData.getOutLetID());
                jObjstartTimer.put("OrderID",shareData.getOrderID());
                jObjstartTimer.put("ResourceID",jObjResource.getString("ResourceID"));
                jObjstartTimer.put("StartTime",shareData.getjObjWorkDateBean());
                jObjstartTimer.put("WSID",sp.getString("WSID","-1"));
                jObjstartTimer.put("CashierID",sp.getString("CashierID","-1"));
                System.out.println("jObjstartTimer : " + jObjstartTimer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"startTimer",String.valueOf(jObjstartTimer));
                System.out.println("respone jObjstartTimer: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResstartTimer = new JSONObject(respone);
                jErr = jResstartTimer.getString("ErrorCode");
                jMes = jResstartTimer.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                finish();
            }else{
                showDialogError(jMes);
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

    private class getServerDateTimeLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResgetServerDateTime;
        private String jErr;
        private String jMes;
        private JSONArray jArrgetServerDateTime;
        private String respone;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjSend = new JSONObject();
            try {
                jObjSend.put("OutletID",shareData.getOutLetID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getServerDateTime",String.valueOf(jObjSend));
                System.out.println("respone jObjgetServerDateTime: " + jObjSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResgetServerDateTime = new JSONObject(respone);
                jErr = jResgetServerDateTime.getString("ErrorCode");
                jMes = jResgetServerDateTime.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                //set text
                try {
                    txtTimeShow.setText(jResgetServerDateTime.getJSONObject("Data").getString("DateTimeText"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showDialogError(jMes);
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

    private class changeTimerLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jReschangeTimer;
        private String jErr;
        private String jMes;
        private JSONArray jArrchangeTimer;
        private String respone;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjstopTimer= new JSONObject();
            try {
                jObjstopTimer.put("OutletID",shareData.getOutLetID());
                jObjstopTimer.put("OrderID",shareData.getOrderID());
                jObjstopTimer.put("TimerSeq",jAllDetail.getString("Seq"));
                jObjstopTimer.put("ResourceID",jObjResource.getString("ResourceID"));
                jObjstopTimer.put("WSID",sp.getString("WSID","-1"));
                jObjstopTimer.put("CashierID",sp.getString("CashierID","-1"));
                System.out.println("jObjchangeTimer : " + jObjstopTimer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"changeTimer",String.valueOf(jObjstopTimer));
                System.out.println("respone jObjchangeTimer: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jReschangeTimer = new JSONObject(respone);
                jErr = jReschangeTimer.getString("ErrorCode");
                jMes = jReschangeTimer.getString("ErrorMesg");
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
                    shareData.setObjSelectResource(jReschangeTimer.getJSONObject("Data").getString("ResourceInfo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                shareData.setCheckSearchPage("Act");
                finish();
            }else{
                showDialogError(jMes);
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
