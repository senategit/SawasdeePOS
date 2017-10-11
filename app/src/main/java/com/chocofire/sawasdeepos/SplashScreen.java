package com.chocofire.sawasdeepos;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/8/2017.
 */

public class SplashScreen extends AppCompatActivity {

    final String P_NAME = "App_Config";

    postJSON post = new postJSON();
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 3000L;
    SharedParam shareData;
    private JSONObject jObject;
    private String strRes;
    private String strErr;
    private String strMes;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String strWSName;
    private String strWSType;
    private String jWSID;
    private int strNetCheck;
    private String strURL;
    public String strHWurl;
    public String strHWport;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        shareData = (SharedParam) getApplication();
        System.out.println(getMacAddr());

        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        strURL = sp.getString("URL","");
        strHWurl = sp.getString("HWURL","");
        strHWport = sp.getString("HWPORT","");
        if(strURL.equals("")){
            Intent intent = new Intent(SplashScreen.this, Activity_CheckService.class);
            startActivity(intent);
            finish();
        }else{
            shareData.setURL(strURL);
            shareData.setHWURL(strHWurl);
            shareData.setHWPORT(strHWport);

            shareData.setNeedReloadConfig(false);
        }
        strWSName = sp.getString("WSName","-1");
        strWSType = sp.getString("WSTpye","-1");
        System.out.println("############################"+strWSName);
        try {
            jObject = new JSONObject();
            jObject.put("WSName", strWSName);
            jObject.put("WSType", strWSType); //fix WSType = 5 mobile
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            strRes = post.postForm(shareData.getURL() + "getWorkstation", jObject.toString());
            strNetCheck=-1;
        } catch (IOException e) {
            e.printStackTrace();
            strNetCheck=1;
        }
        System.out.println("############################ : strres :"+strRes);
        if(strNetCheck==-1) { // ต่อเน็ตได้
            if (strWSName.equals("-1")) {
                strErr = "-1";
                //System.out.println("hello");
            } else {
                try {
                    JSONObject jRes = new JSONObject(strRes);
                    strErr = jRes.getString("ErrorCode");
                    strMes = jRes.getString("ErrorMesg");
                    jWSID = jRes.getJSONObject("Data").getJSONObject("WSInfo").getString("WSID");
                    editor.putString("WSID", jWSID);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{//ไม่สามารถติดต่อเน็ตได้
            strErr = "2";
        }


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                /*if(executeCommand()){
                    Intent intent = new Intent(SplashScreen.this, Activity_CheckService.class);
                    startActivity(intent);
                    finish();
                }*/
                if(strErr.equals("1")) {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(strErr.equals("2")) {
                    Intent intent = new Intent(SplashScreen.this, Activity_CheckService.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashScreen.this, Activity_Workstation.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
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

    public void showDialogError(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){
                        Intent intent = new Intent(SplashScreen.this, Activity_Workstation.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class postJSON { //posthttp class
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

    private boolean executeCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            java.lang.Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+shareData.getURL());
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
}