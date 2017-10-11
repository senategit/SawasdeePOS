package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.view.View;
import android.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/28/2017.
 */

public class Activity_ResourceMG extends AppCompatActivity {

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
    private String strAllDetail;
    private TextView txtResourceName;
    private TextView txtResourceCode;
    private TextView txtType;
    private TextView txtRate;
    private TextView txtStartTime;
    private TextView txtEndTime;
    private TextView txtTotalTime;
    private TextView txtSysCountAble;
    private TextView txtTimeAdjust;
    private TextView txtStatedBy;
    private JSONObject jAllDetail;
    private EditText edtCharge;
    private TextView txtSummit;
    private TextView txtCharge;
    private TextView txtChangeRe;
    private ImageView imgPageRe;
    private Bitmap bmpRED;
    private Bitmap bmpYELLOW;
    private TextView txt34;

    @Override
    protected void onResume() {
        super.onResume();
        getServerDateTimeLoader getLoad = new getServerDateTimeLoader();
        getLoad.execute();
        settingDetail();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resourcemg);

        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        showDialogSpinner();

        configureToolbar();

        setTextView();

        shareData = (SharedParam) getApplication();
        //configureToolbar();
        settingDetail();
        // ยังไม่มีservice ,ารองรับ ต้องรอจากพี่แนส
        txtSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop
                clickedStop();

                //start or stop
                //stopTimerLoader stopLoad = new stopTimerLoader();
                //stopLoad.execute();
            }
        });

        txtCharge.setText("Sent to bill");
        txtCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sent to bill
                clickedSentToBill();
            }
        });

        txtChangeRe.setText("Change Resource");
        txtChangeRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change resource
                shareData.setChangeWay("99");
                Intent intent = new Intent(getApplication(), Activity_SearchResource.class);
                startActivity(intent);
            }
        });

    }

    public void clickedStop() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("คำถาม");
            //dialog.setIcon(R.drawable.ic_drawer);
            dialog.setCancelable(true);
            dialog.setMessage("ยืนยันการ Stop?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    stopTimerLoader stopLoad = new stopTimerLoader();
                    stopLoad.execute();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickedSentToBill() {
        try {
            if (jAllDetail != null) {
                if (jAllDetail.getString("Status").equals("1")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("แจ้งเตือน");
                    dialog.setCancelable(true);
                    dialog.setMessage("กรุณาสั่ง Stop ก่อน");
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("คำถาม");
        //dialog.setIcon(R.drawable.ic_drawer);
        dialog.setCancelable(true);
        dialog.setMessage("ยืนยันการ Sent to Bill?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sentToBillLoader sentBill = new sentToBillLoader();
                sentBill.execute();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void settingDetail() {
        strAllDetail = shareData.getObjSelectResource();/*{
                "PolicyID": 1,
                "STime": 636414476664800000,
                "TimerUnit": 1,
                "TimerRate": 40,
                "ResourceCode1": "7014",
                "ResourceName": "น้ำเหนือ/N",
                "CalQty": 2,
                "ItemID": 756,
                "WSID": 24,
                "OrderQty": 2,
                "Status": 2,
                "ETimeText": "19-09-2017 19:55",
                "PolicySeq": 11,
                "ResourceType": 16,
                "ETime": 636414477161870000,
                "StatusName": "Stopped",
                "OrderTimeText": "19-09-2017 19:54",
                "WaiterId": 1,
                "Seq": 1,
                "STimeText": "19-09-2017 19:54",
                "TimerRateName": "40 Minutes",
                "OrderID": 282238,
                "OrderTime": 636414476674500000,
                "ResourceTypeName": "ทีมเนม",
                "ResourceID": 1780,
                "ResourceCode": "7014"
            }*/
        try {
            jAllDetail = new JSONObject(strAllDetail);
            edtCharge.setText(jAllDetail.getString("CalQty"));
            txtResourceName.setText(jAllDetail.getString("ResourceName"));
            txtResourceCode.setText(jAllDetail.getString("ResourceCode"));
            txtType.setText(jAllDetail.getString("ResourceTypeName"));
            txtRate.setText(jAllDetail.getString("TimerRateName"));
            txtStartTime.setText(jAllDetail.getString("STimeText"));
            if (!jAllDetail.getString("Status").equals("1")) {
                txtEndTime.setText(jAllDetail.getString("ETimeText"));
                txtSummit.setVisibility(View.VISIBLE);
                txtSummit.setText("Stop");
                imgPageRe.setImageBitmap(bmpYELLOW);
                edtCharge.setVisibility(View.VISIBLE);
                txt34.setVisibility(View.VISIBLE);
            } else {
                txtEndTime.setText("-");
                txtSummit.setText("Stop");
                //txtSummit.setVisibility(View.INVISIBLE);
                imgPageRe.setImageBitmap(bmpRED);
                edtCharge.setVisibility(View.INVISIBLE);
                txt34.setVisibility(View.INVISIBLE);
            }
            try {
                txtTotalTime.setText(jAllDetail.getString("TotalTimeText"));
            } catch (JSONException e) {
                txtTotalTime.setText("-");
            }
            try {
                txtTimeAdjust.setText(jAllDetail.getString("TimeAdjustText"));
            } catch (JSONException e) {
                txtTimeAdjust.setText("-");
            }
            //txtSysCountAble.setText(jAllDetail.getString(""));
            try {
                txtStatedBy.setText(jAllDetail.getString("StartedByName"));
            } catch (JSONException e) {
                txtStatedBy.setText("-");
            }
            txtSysCountAble.setText(jAllDetail.getString("CalQty"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTextView() {
        txt34 = (TextView) findViewById(R.id.textView34_title);
        edtCharge = (EditText) findViewById(R.id.editText_chargeToBillMG);
        txtResourceName = (TextView) findViewById(R.id.textView_resourceName);
        txtResourceCode = (TextView) findViewById(R.id.textView_resourceCode);
        txtType = (TextView) findViewById(R.id.textView_type);
        txtRate = (TextView) findViewById(R.id.textView_rate);
        txtStartTime = (TextView) findViewById(R.id.textView_StartTime);
        txtEndTime = (TextView) findViewById(R.id.textView_EndTime);
        txtTotalTime = (TextView) findViewById(R.id.textView_totalTime);
        txtSysCountAble = (TextView) findViewById(R.id.textView_SysCountAble);
        txtTimeAdjust = (TextView) findViewById(R.id.textView_timeAdjust);
        txtStatedBy = (TextView) findViewById(R.id.textView_StartedBy);
        txtSummit = (TextView) findViewById(R.id.textView_summit_resource);
        txtCharge = (TextView) findViewById(R.id.textView_ChargeToBill);
        txtChangeRe = (TextView) findViewById(R.id.textView_ChangeResource);
        imgPageRe = (ImageView) findViewById(R.id.imageView_resourcePageimg);
        bmpRED = BitmapFactory.decodeResource(getResources(), R.drawable.drink_red);
        bmpYELLOW = BitmapFactory.decodeResource(getResources(), R.drawable.drink_yellow);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Resource Command");
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class stopTimerLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResstopTimer;
        private String jErr;
        private String jMes;
        private JSONArray jArrstopTimer;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjstopTimer = new JSONObject();
            try {
                jObjstopTimer.put("OutletID", shareData.getOutLetID());
                jObjstopTimer.put("OrderID", shareData.getOrderID());
                jObjstopTimer.put("TimerSeq", jAllDetail.getString("Seq"));
                jObjstopTimer.put("EndTime", shareData.getjObjWorkDateBean());
                jObjstopTimer.put("CashierID", sp.getString("CashierID", "-1"));
                System.out.println("jObjstopTimer : " + jObjstopTimer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL() + "stopTimer", String.valueOf(jObjstopTimer));
                System.out.println("respone jObjstopTimer: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResstopTimer = new JSONObject(respone);
                jErr = jResstopTimer.getString("ErrorCode");
                jMes = jResstopTimer.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if (jErr.equals("1")) {
                try {
                    shareData.setObjSelectResource(jResstopTimer.getJSONObject("Data").getString("ResourceInfo"));
                    onResume();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //finish();
            } else {
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

    private class sentToBillLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jRessentToBill;
        private String jErr;
        private String jMes;
        private JSONArray jArrsentToBill;
        private String QTY;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjchargeTimer = new JSONObject();
            try {
                jObjchargeTimer.put("OutletID", shareData.getOutLetID());
                jObjchargeTimer.put("OrderID", shareData.getOrderID());
                jObjchargeTimer.put("TimerSeq", jAllDetail.getString("Seq"));
                jObjchargeTimer.put("WSID", sp.getString("WSID", "-1"));
                jObjchargeTimer.put("CashierID", sp.getString("CashierID", "-1"));
                jObjchargeTimer.put("OrderQty", QTY);
                System.out.println("jObjsentToBill : " + jObjchargeTimer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL() + "sentToBill", String.valueOf(jObjchargeTimer));
                System.out.println("respone jObjsentToBill: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jRessentToBill = new JSONObject(respone);
                jErr = jRessentToBill.getString("ErrorCode");
                jMes = jRessentToBill.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if (jErr.equals("1")) {
                finish();
            } else {
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            QTY = edtCharge.getText().toString();
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
                jObjSend.put("OutletID", shareData.getOutLetID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL() + "getServerDateTime", String.valueOf(jObjSend));
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
            if (jErr.equals("1")) {
                //set text
                /*try {
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            } else {
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
                    .addHeader("auth_key", shareData.getAuth_key())
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    } // post from JSONobject

    public void showDialogSpinner() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("กำลังดาวน์โหลดข้อมูล ...");
        dialog.setMessage("โปรดรอสักครู่ ...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER); //dialog spinning
        dialog.setCancelable(false);
    } // dialog setting

    public void showDialogError(String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}