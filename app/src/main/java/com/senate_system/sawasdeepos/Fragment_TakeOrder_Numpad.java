package com.senate_system.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/6/2017.
 */

public class Fragment_TakeOrder_Numpad extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewTakeOrderNumpad;
    private EditText edt_summary;
    private Button btn_9;
    private Button btn_8;
    private Button btn_4;
    private Button btn_7;
    private Button btn_6;
    private Button btn_5;
    private Button btn_3;
    private Button btn_2;
    private Button btn_1;
    private Button btn_0;
    private Button btn_power;
    private Button btn_dot;
    private Button btn_enter;
    private Button btn_bs;
    private Button btn_ce;
    private String FirstText = "";
    private String SecoundText = "";
    private int ClickPower = 0;
    SharedParam shareData;
    private ProgressDialog dialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    postJSON post = new postJSON();
    private String strObjOrder;
    private JSONObject jObjAll;
    private JSONObject jObjOrder;
    private TextView txtSupname;
    private TextView txtHeadName;
    private Button btnCnote;
    private Button btnDelete;

    public Fragment_TakeOrder_Numpad() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        FirstText="";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewTakeOrderNumpad = (View) inflater.inflate(R.layout.fragment_takeorder_numpad, container, false);

        showDialogSpinner();

        shareData = (SharedParam) getActivity().getApplication();
        strObjOrder = shareData.getOrderInfo();

        try {
            jObjAll = new JSONObject(strObjOrder);
            jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
            shareData.setLastAccess(jObjOrder.getString("LastAccess"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        txtSupname = (TextView) getActivity().findViewById(R.id.textView_include_helpbar_sup);
        txtSupname.setVisibility(View.INVISIBLE);
        txtHeadName = (TextView) getActivity().findViewById(R.id.textView_include_helpbar);
        txtHeadName.setVisibility(View.INVISIBLE);
        btnCnote = (Button) getActivity().findViewById(R.id.button_Cnote_healp2);
        btnCnote.setVisibility(View.INVISIBLE);
        btnCnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(getActivity(), Activity_CookingNote.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        btnDelete = (Button) getActivity().findViewById(R.id.button_Delete_item_help2);
        btnDelete.setVisibility(View.INVISIBLE);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //service delete item
                strObjOrder = shareData.getOrderInfo();
                try {
                    jObjAll = new JSONObject(strObjOrder);
                    jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
                    shareData.setLastAccess(jObjOrder.getString("LastAccess"));
                    System.out.println(shareData.getLastAccess());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("delete item");
                deleteItemLoader deleteLoader = new deleteItemLoader();
                deleteLoader.execute();
            }
        });
        //

        setButton();

        return myViewTakeOrderNumpad;
    }

    public void setButton(){
        edt_summary = (EditText) myViewTakeOrderNumpad.findViewById(R.id.editText_summary);
        btn_9 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_9);
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"9";
                }else{
                    SecoundText = SecoundText+"9";
                }
                edt_summary.setText(edt_summary.getText().toString()+"9");
            }
        });
        btn_8 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_8);
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"8";
                }else{
                    SecoundText = SecoundText+"8";
                }
                edt_summary.setText(edt_summary.getText().toString()+"8");
            }
        });
        btn_7 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_7);
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"7";
                }else{
                    SecoundText = SecoundText+"7";
                }
                edt_summary.setText(edt_summary.getText().toString()+"7");
            }
        });
        btn_6 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_6);
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"6";
                }else{
                    SecoundText = SecoundText+"6";
                }
                edt_summary.setText(edt_summary.getText().toString()+"6");
            }
        });
        btn_5 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_5);
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"5";
                }else{
                    SecoundText = SecoundText+"5";
                }
                edt_summary.setText(edt_summary.getText().toString()+"5");
            }
        });
        btn_4 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_4);
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"4";
                }else{
                    SecoundText = SecoundText+"4";
                }
                edt_summary.setText(edt_summary.getText().toString()+"4");
            }
        });
        btn_3 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_3);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"3";
                }else{
                    SecoundText = SecoundText+"3";
                }
                edt_summary.setText(edt_summary.getText().toString()+"3");
            }
        });
        btn_2 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_2);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"2";
                }else{
                    SecoundText = SecoundText+"2";
                }
                edt_summary.setText(edt_summary.getText().toString()+"2");
            }
        });
        btn_1 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_1);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"1";
                }else{
                    SecoundText = SecoundText+"1";
                }
                edt_summary.setText(edt_summary.getText().toString()+"1");
            }
        });
        btn_0 = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_0);
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+"0";
                }else{
                    SecoundText = SecoundText+"0";
                }
                edt_summary.setText(edt_summary.getText().toString()+"0");
            }
        });
        btn_power = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_power);
        btn_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_summary.getText().toString().contains("*")&&!edt_summary.getText().toString().equals("")) {
                    ClickPower = 1;
                    edt_summary.setText(edt_summary.getText().toString() + "*");
                }
            }
        });
        btn_dot = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_dot);
        btn_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickPower==0){
                    FirstText = FirstText+".";
                }else{
                    SecoundText = SecoundText+".";
                }
                edt_summary.setText(edt_summary.getText().toString()+".");
            }
        });
        btn_enter = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //additemwithcode
                if(!FirstText.equals("")) {
                    System.out.println("QTY111111232323232332 : " + FirstText +SecoundText);
                    if (SecoundText.equals("")) {
                        SecoundText = FirstText;
                        FirstText = "1";
                    }
                    ClickPower = 0;
                    System.out.println("QTY111111 : " + FirstText);
                    addItemWithItemCodeLoader addCode = new addItemWithItemCodeLoader();
                    addCode.execute();
                }
            }
        });
        btn_bs = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_bs);
        btn_bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String s = edt_summary.getText().toString();
                    if (s.length() > 0) {
                        if (s.substring(s.length() - 1).equals("*")) {
                            ClickPower = 0;
                        } else {
                            if (ClickPower == 0) {
                                FirstText = FirstText.substring(0, FirstText.length() - 1);
                            } else {
                                SecoundText = SecoundText.substring(0, SecoundText.length() - 1);
                            }
                        }
                        edt_summary.setText(s.substring(0, s.length() - 1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace();
                    //showDialogError(errmsg);
                }
            }
        });
        btn_ce = (Button) myViewTakeOrderNumpad.findViewById(R.id.button_numpad_ce);
        btn_ce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_summary.setText("");
                ClickPower=0;
                FirstText="";
                SecoundText="";
            }
        });
    }

    private class addItemWithItemCodeLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResaddItemWithItemCode;
        private String jErr;
        private String jMes;
        private String respone;
        private String strName;
        private String strSEQ;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("OrderID",jObjOrder.getString("OrderID")); // ได้มาจาก user id
                jObjGetOrder.put("ItemCode",SecoundText);
                jObjGetOrder.put("Qty",FirstText);
                jObjGetOrder.put("WSID",sp.getString("CashierID","-1"));
                jObjGetOrder.put("DecorID",jObjOrder.getString("DecorId"));
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjGetOrder.put("MMRSeq","null");
                jObjGetOrder.put("SaleMode",jObjOrder.getString("SaleMode"));
                jObjGetOrder.put("ResourceID","null");
                jObjGetOrder.put("LastAccess",jObjOrder.getString("LastAccess"));
                System.out.println("jObjGetOrder : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"addItemWithItemCode",String.valueOf(jObjGetOrder));
                shareData.setOrderInfo(respone);
                System.out.println("respone jObjaddItemWithItemCode: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResaddItemWithItemCode = new JSONObject(respone);
                jErr = jResaddItemWithItemCode.getString("ErrorCode");
                jMes = jResaddItemWithItemCode.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                //
                shareData.setOrderInfo(respone);
                strObjOrder = shareData.getOrderInfo();
                try {
                    strName = jResaddItemWithItemCode.getJSONObject("Data").getJSONObject("ItemDataLast") .getString("ItemDisplayName");
                    jObjAll = new JSONObject(strObjOrder);
                    jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
                    strSEQ = jObjAll.getJSONObject("Data").getJSONObject("ItemDataLast").getString("Seq");
                    shareData.setSEQDeleteItem(strSEQ);
                    shareData.setLastAccess(jObjOrder.getString("LastAccess"));
                    shareData.setObjSelectItem(jObjAll.getJSONObject("Data").getString("ItemDataLast"));
                    System.out.println("DataOrder :" + jObjOrder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtHeadName.setVisibility(View.VISIBLE);
                txtSupname.setVisibility(View.VISIBLE);
                btnCnote.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                txtHeadName.setText(FirstText + " x " +strName);
                txtSupname.setText("เพิ่มรายการแล้ว");

                FirstText="";
                SecoundText="";
                edt_summary.setText("");

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

    public void showDialogSpinner(){
        dialog = new ProgressDialog(this.getContext());
        dialog.setTitle("กำลังดาวน์โหลดข้อมูล ...");
        dialog.setMessage("โปรดรอสักครู่ ...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER); //dialog spinning
        dialog.setCancelable(false);
    } // dialog setting

    public void showDialogError(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
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

    private class deleteItemLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResdeleteItem;
        private JSONArray jArrdeleteItem;
        private String respone;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("Seq",shareData.getSEQDeleteItem());
                jObjtakeOrder.put("WSID",sp.getString("WSID","-1"));
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("LastAccess",jObjOrder.getString("LastAccess"));
                System.out.println("jObjdeleteItem : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"deleteItem",String.valueOf(jObjtakeOrder));
                System.out.println("respone deleteItem: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResdeleteItem = new JSONObject(respone);
                jErr = jResdeleteItem.getString("ErrorCode");
                jMes = jResdeleteItem.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                //
                shareData.setOrderInfo(respone);
                strObjOrder = shareData.getOrderInfo();
                try {
                    jObjAll = new JSONObject(strObjOrder);
                    jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
                    shareData.setLastAccess(jObjOrder.getString("LastAccess"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setHeader();
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

    public  void setHeader(){
        // set header
        txtSupname = (TextView) getActivity().findViewById(R.id.textView_include_helpbar_sup);
        txtSupname.setVisibility(View.INVISIBLE);
        txtHeadName = (TextView) getActivity().findViewById(R.id.textView_include_helpbar);
        txtHeadName.setVisibility(View.INVISIBLE);
        btnCnote = (Button) getActivity().findViewById(R.id.button_Cnote_healp2);
        btnCnote.setVisibility(View.INVISIBLE);
        btnDelete = (Button) getActivity().findViewById(R.id.button_Delete_item_help2);
        btnDelete.setVisibility(View.INVISIBLE);
        //
    }
}