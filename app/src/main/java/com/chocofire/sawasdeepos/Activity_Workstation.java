package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.security.AccessController.getContext;

/**
 * Created by ChoCoFire on 9/11/2017.
 */

public class Activity_Workstation extends AppCompatActivity {

    final String P_NAME = "App_Config";
    private ProgressDialog dialog;
    postTaskSyncLoader postLoader;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SharedParam shareData;
    private Spinner spin;
    List<String> arrList = new ArrayList<String>();
    private int verifyCheck = 0;
    private EditText edtWSNAME;
    private EditText edtREMARK;
    postJSON post = new postJSON();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workstation);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        showDialogSpinner();
        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        /*editor.putBoolean("FirstRun", true);
        editor.apply();*/

        Button btnVerify = (Button)findViewById(R.id.btn_verify);
        Button btnRegis = (Button)findViewById(R.id.btn_Regis);
        edtWSNAME = (EditText)findViewById(R.id.editText_WSNAME);
        edtREMARK = (EditText)findViewById(R.id.editText_Remark);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtWSNAME.getText().toString().equals("")||edtREMARK.getText().toString().equals("")) {
                    showDialogError("กรุณากรอกข้อมูลให้ครบถ้วน");
                }else {
                    postLoader = new postTaskSyncLoader();
                    postLoader.execute();
                }
            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyCheck == 1){
                    postLoader = new postTaskSyncLoader();
                    postLoader.execute();
                }else{
                    showDialogError("กรุณาทำการ verify ชื่อเครื่องก่อน");
                }
            }
        });

        /*// spinner1
        spin = (Spinner) findViewById(R.id.spinner_wstype);
        arrList.add("1 = Desktop Cashier");
        arrList.add("2 = Desktop TakeOrder");
        arrList.add("3 = Desktop Total");
        arrList.add("4 = PDACashier");
        arrList.add("5 = PDATakeOrder");
        ArrayAdapter<String> arrAd = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrList);
        arrAd.setDropDownViewResource(R.layout.spinner_layout);
        spin.setAdapter(arrAd);
        spin.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext().getApplicationContext(),
                        String.valueOf("Your Selected : " + arrList.get(i)),
                        Toast.LENGTH_SHORT).show();
                shareData.setPS_Weight_Unit(String.valueOf(i+1)); //อย่าลืมเช็ค weight id ด้วยว่า 1 คืออะไร 0 คืออะไร
                System.out.println("PS_Weight_Unit"+shareData.getPS_Weight_Unit());
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getContext().getApplicationContext(),
                        String.valueOf("Your Selected Empty"),
                        Toast.LENGTH_SHORT).show();
            }
        }); // set gram or kilogram**/
    }

    private class postTaskSyncLoader extends AsyncTask<Integer, Integer, String> {

        private String respone;
        private JSONObject jsonObject;
        private String jError;
        private String jMes;
        private JSONObject jSuccess;
        private String jWSName;
        private String jWSType;

        @Override
        protected String doInBackground(Integer... params) {
            if(verifyCheck==0) { // กดปุ่ม verify
                try {
                    System.out.println(jsonObject);
                    respone = post.postForm(shareData.getURL() + "verifyWorkstation", jsonObject.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jRes = new JSONObject(respone);
                    jError = jRes.getString("ErrorCode");
                    jMes = jRes.getString("ErrorMesg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{ //กด ปุ่ม regis
                try {
                    System.out.println(jsonObject);
                    respone = post.postForm(shareData.getURL() + "regisWorkstation", jsonObject.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jRes = new JSONObject(respone);
                    jError = jRes.getString("ErrorCode");
                    jMes = jRes.getString("ErrorMesg");
                    jSuccess = jRes;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            dialog.dismiss();
            if(verifyCheck==0) { // กดปุ่ม verify
                if (jError.equals("1")) {
                    verifyCheck=1;
                    showDialogError("สามารถใช้ชื่อเครื่องนี้ได้");
                } else {
                    showDialogError(jMes);
                }
            }
            else if(verifyCheck==1){ //กดปุ่ม regis ผ่าน
                if(jError.equals("1")){
                    try {
                        jWSName = jSuccess.getJSONObject("Data").getJSONObject("WSInfo").getString("WSName");
                        jWSType = jSuccess.getJSONObject("Data").getJSONObject("WSInfo").getString("WSType");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString("WSName", jWSName);
                    editor.putString("WSType", jWSType);
                    editor.apply();
                    Intent intent = new Intent(getApplication(), SplashScreen.class);
                    startActivity(intent);
                    finish();
                }else{
                    showDialogError(jMes);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            jsonObject = new JSONObject();
            if(verifyCheck==0) {
                try {
                    jsonObject.put("WSName", edtWSNAME.getText().toString());
                    jsonObject.put("DeviceCode", getMacAddr());
                    System.out.println("==================== verify");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    jsonObject.put("WSName", edtWSNAME.getText().toString());
                    jsonObject.put("DeviceCode", getMacAddr());
                    jsonObject.put("Remark",edtREMARK.getText().toString());
                    jsonObject.put("WSType",5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
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
}
