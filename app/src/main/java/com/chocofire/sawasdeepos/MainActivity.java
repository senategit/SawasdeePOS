package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final String P_NAME = "App_Config";

    static SharedParam shareData;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    postTaskSyncLoader loginLoad;
    postJSON post = new postJSON();
    private String jUserID;
    private ImageView imgSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareData = (SharedParam) getApplication();
        showDialogSpinner();

        imgSetting = (ImageView) findViewById(R.id.imageView_Setting);
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ให้ไป new bill ก่อนแล้วไปหน้า take order
                settingPage();
            }
        });

        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        sp.getString("WSName","-1");
        sp.getString("WSType","-1");

        if(shareData.getNeedReloadConfig()) {
            String strURL = sp.getString("URL", "");
            String strHWurl = sp.getString("HWURL", "");
            String strHWport = sp.getString("HWPORT", "");

            shareData.setURL(strURL);
            shareData.setHWURL(strHWurl);
            shareData.setHWPORT(strHWport);
            System.out.println("Reload configuration complete");
        }


        Button btnLogin = (Button) findViewById(R.id.button_login_main);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLoad = new postTaskSyncLoader();
                loginLoad.execute();
            }
        });
    }

    public void settingPage(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SETTING");
        dialog.setIcon(R.drawable.ic_drawer);
        dialog.setCancelable(true);
        dialog.setMessage("ต้องการแก้ไขค่าในหน้า config ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplication(), Activity_CheckService.class);
                startActivity(intent);
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

    private class postTaskSyncLoader extends AsyncTask<Integer, Integer, String> {
        private EditText edtUsername;
        private EditText edtPassword;
        private JSONObject jsonObject;
        private String respone;
        private String jError;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            try {
                respone = post.postForm(shareData.getURL()+"userAuthen",jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jRes = new JSONObject(respone);
                jError = jRes.getString("ErrorCode");
                jMes = jRes.getString("ErrorMesg");
                shareData.setDataFormLogin(jRes.getString("Data"));
                jUserID= jRes.getJSONObject("Data").getJSONArray("UserInfo").getJSONObject(0).getString("UserID");
                shareData.setUserName(jRes.getJSONObject("Data").getJSONArray("UserInfo").getJSONObject(0).getString("FullName"));
                System.out.println("jUserID : "+jUserID);
                editor.putString("UserID",jUserID);
                editor.putString("CashierID",jUserID);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            dialog.dismiss();
            if(jError.equals("1")) {

                Intent a = new Intent(getApplicationContext(), Activity_Navigation.class);//Activity_Takeorder_menu
                startActivity(a);
            }else{
                showDialogError(jMes);
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            edtUsername = (EditText)findViewById(R.id.editText_Username_Main);
            edtPassword = (EditText)findViewById(R.id.editText_Password_Main);

            if(edtUsername.getText().toString().trim().equals("")) {
                edtUsername.setText("8432");
                edtPassword.setText("75391");
            }
            jsonObject = new JSONObject();
            try {
                jsonObject.put("UserName",edtUsername.getText().toString().trim());
                jsonObject.put("Password",edtPassword.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);

            }
        }
        return super.dispatchTouchEvent(ev);
    } // click anywhere to close keyboard

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

}
