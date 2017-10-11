package com.chocofire.sawasdeepos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class Fragment_Search_Bill extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewBill;
    private SharedParam shareData;
    ListViewAdapterBill adapterS;
    ArrayList<String> list1 = new ArrayList<String>();
    ArrayList<String> list2 = new ArrayList<String>();
    ArrayList<String> list3 = new ArrayList<String>();
    ArrayList<String> list4 = new ArrayList<String>();
    ArrayList<String> list5 = new ArrayList<String>();
    ArrayList<String> list6 = new ArrayList<String>();
    private ListView listView;
    private ProgressDialog dialog;
    postJSON post = new postJSON();
    private EditText edtSearchtxt;
    private Button btnSearch;
    private ImageView ImgBackBTN;
    private String strCheck = "";
    private JSONArray jArrbillSearch;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public Fragment_Search_Bill() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (strCheck.equals("5")) {
                shareData.setTabSelected("-1");

                if (shareData.getOutLetID() == null) {
                    showDialogError("กรุณาเลือก outlet ก่อน");
                    return;
                }

                if (shareData.getjObjWorkDateBean() == null) {
                    showDialogError("ระบบตรวจไม่พบข้อมูลการเปิดวัน กรุณาเปิดวันการขายสำหรับ outlet ก่อน");
                    return;
                }

                billSearchLoader billLoad = new billSearchLoader();
                billLoad.execute();

            /*
            if(!shareData.getjObjWorkDateBean().equals(null)) {
                    billSearchLoader billLoad = new billSearchLoader();
                    billLoad.execute();
            }else{
                showDialogError("กรุณาเลือก outlet ก่อน");
            }
            */
            }
        } catch (Exception e) {
            String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace().toString();
            showDialogError(errmsg);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewBill = (View) inflater.inflate(R.layout.fragment_search_bill, container, false);

        shareData = (SharedParam) getActivity().getApplication();
        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        strCheck = shareData.getTabSelected();
        showDialogSpinner();
        edtSearchtxt = (EditText) getActivity().findViewById(R.id.editTextlist_search);


        adapterS = new ListViewAdapterBill(getContext(), list1 , list2 , list3);
        listView = (ListView) myViewBill.findViewById(R.id.listView_Bill); // define list view
        listView.setAdapter(adapterS); //set listview from json
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //go to take order page
                try {
                    shareData.setOrderID(jArrbillSearch.getJSONObject(arg2).getString("OrderID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getOrderLoader orderLoader = new getOrderLoader();
                orderLoader.execute();
                /*Intent intent = new Intent(getActivity(), Activity_TakeOrder_Menu.class);
                startActivity(intent);*/
            }
        }); //set adapter list

        return myViewBill;
    }

    private class billSearchLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResbillSearch;
        private String respone;
        private String strSearch;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjbillSearch = new JSONObject();
            try {
                jObjbillSearch.put("OutletID",shareData.getOutLetID());
                jObjbillSearch.put("SearchText",strSearch);
                jObjbillSearch.put("WorkDateBean",shareData.getjObjWorkDateBean());
                System.out.println("jObjbillSearch : " +jObjbillSearch);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"billSearch",String.valueOf(jObjbillSearch));
                System.out.println("respone billSearch: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResbillSearch = new JSONObject(respone);
                jErr = jResbillSearch.getString("ErrorCode");
                jMes = jResbillSearch.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                // success
                try {
                    jArrbillSearch = jResbillSearch.getJSONObject("Data").getJSONArray("BillList");
                    list1.clear();
                    for(int count=0 ; count < jArrbillSearch.length();count++){
                        list1.add(jArrbillSearch.getJSONObject(count).getString("DecorName"));
                        list2.add(jArrbillSearch.getJSONObject(count).getString("GuestChkNo"));
                        list3.add(jArrbillSearch.getJSONObject(count).getString("Balance"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showDialogError(jMes);
            }
            adapterS.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            strSearch = edtSearchtxt.getText().toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class getOrderLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;
        private String respone;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1")); // ได้มาจาก user id
                jObjGetOrder.put("OrderID",shareData.getOrderID());
                System.out.println("jObjGetOrder : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getOrder",String.valueOf(jObjGetOrder));
                shareData.setOrderInfo(respone);
                System.out.println("respone jObjGetOrder: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResBill = new JSONObject(respone);
                jErr = jResBill.getString("ErrorCode");
                jMes = jResBill.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if(jErr.equals("1")){
                try {
                    shareData.setOrderID(jResBill.getJSONObject("Data").getJSONObject("Order").getString("OrderID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), Activity_TakeOrder_Menu.class);
                startActivity(intent);
            }else{
                showDialogError(jMes);
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void showDialogSpinner(){
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
            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
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
