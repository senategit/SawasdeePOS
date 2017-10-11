package com.chocofire.sawasdeepos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ChoCoFire on 10/1/2017.
 */

public class Activity_CheckService extends AppCompatActivity {

    final String P_NAME = "App_Config";

    private Button btnSummit;
    private EditText edt;
    private SharedParam shareData;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private EditText edtURL;
    private EditText edtPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        shareData = (SharedParam) getApplication();
        sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        String strURL = sp.getString("URL",shareData.getURL()); //sp.getString("URL","-1");
        String strHWurl = sp.getString("HWURL",shareData.getHWURL());
        String strHWport = sp.getString("HWPORT",shareData.getHWPORT());
        edtURL = (EditText) findViewById(R.id.editText_url_hw);
        edtPort = (EditText) findViewById(R.id.editText_port_hw);
        edt = (EditText) findViewById(R.id.editText_getURL);
        edt.setText(strURL);
        edtURL.setText(strHWurl);
        edtPort.setText(strHWport);




        btnSummit = (Button) findViewById(R.id.button_summitURL);
        btnSummit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogError("โปรแกรมจะทำการ Restart");
            }
        });
    }

    public void showDialogError(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener(){
                    public String strHWport;
                    public String strHWurl;
                    public String str;

                    public void onClick(DialogInterface dialog, int id){
                        str = edt.getText().toString();
                        strHWport = edtPort.getText().toString().trim();
                        strHWurl = edtURL.getText().toString().trim();
                        editor.putString("URL", str);
                        editor.putString("HWURL", strHWurl);
                        editor.putString("HWPORT", strHWport);
                        editor.apply();
                        shareData.setURL(str);
                        shareData.setHWURL(strHWurl);
                        shareData.setHWPORT(strHWport);
                        Intent intent = new Intent(getApplication(), SplashScreen.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
