package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/25/2017.
 */

public class Fragment_new_BillInf extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewBillinf;
    private ProgressDialog dialog;
    private SharedParam shareData;
    postJSON post = new postJSON();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String respone;
    private String strOrderID;
    private ListView listView;
    private ListViewAdapterFragmentTakeOrder adapter;
    private ActionBar actionbar;
    private ArrayList<String> strItemDisplay = new ArrayList<String>();
    private ArrayList<String> strPriceShow = new ArrayList<String>();
    private ArrayList<String> strShow = new ArrayList<String>();
    private ArrayList<String> strPrinted = new ArrayList<String>();
    private String strOrderTable;
    private TextView txtDiscount;
    private TextView txtService;
    private TextView txtTax;
    private TextView txtTotal;
    private TextView txtPayment;
    private TextView txtBalance;
    private String strDiscount;
    private String strService;
    private String strTax;
    private String strTotal;
    private String strPayment;
    private String strBalance;
    private String strObjOrder;
    private JSONObject jObjAll;
    private JSONObject jObjOrder;

    public Fragment_new_BillInf() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getBillInfoLoader getbill = new getBillInfoLoader();
        getbill.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewBillinf = (View) inflater.inflate(R.layout.fragment_new_billinf, container, false);

        setTextView();

        shareData = (SharedParam) getActivity().getApplication();
        strObjOrder = shareData.getOrderInfo();
        try {
            jObjAll = new JSONObject(strObjOrder);
            jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        showDialogSpinner();

        setListView();
        setHeader();


        return myViewBillinf;
    }

    public void setTextView(){
        txtDiscount = (TextView) myViewBillinf.findViewById(R.id.textView_Discount_BillInfo);
        txtService = (TextView) myViewBillinf.findViewById(R.id.textView_service_BillInfo);
        txtTax = (TextView) myViewBillinf.findViewById(R.id.textViewtax_billInfo);
        txtTotal = (TextView) myViewBillinf.findViewById(R.id.textView_total_BillInfo);
        txtPayment = (TextView) myViewBillinf.findViewById(R.id.textView_payment_BillInfo);
        txtBalance = (TextView) myViewBillinf.findViewById(R.id.textView_balance_BillInfo);
    }

    public void setHeader() {
        try {
            if(jObjOrder != null) {
                if (jObjOrder.has("Discount")) {
                    strDiscount = jObjOrder.getString("Discount");
                    if((strDiscount != null) && (strDiscount.trim().length() > 0)) {
                        DecimalFormat formatter = new DecimalFormat("#,##0.00");
                        strDiscount = formatter.format(Double.valueOf(strDiscount));
                    }
                }
                else strDiscount = "";

                if (jObjOrder.has("Service")) {
                    strService = jObjOrder.getString("Service");
                    if((strService != null) && (strService.trim().length() > 0)) {
                        DecimalFormat formatter = new DecimalFormat("#,##0.00");
                        strService = formatter.format(Double.valueOf(strService));
                    }
                }
                else strService = "";

                if (jObjOrder.has("Tax")) {
                    strTax = jObjOrder.getString("Tax");
                    if((strTax != null) && (strTax.trim().length() > 0)) {
                        DecimalFormat formatter = new DecimalFormat("#,##0.00");
                        strTax = formatter.format(Double.valueOf(strTax));
                    }
                }
                else strTax = "";

                if (jObjOrder.has("Total")) {
                    strTotal = jObjOrder.getString("Total");
                    if((strTotal != null) && (strTotal.trim().length() > 0)) {
                        DecimalFormat formatter = new DecimalFormat("#,##0.00");
                        strTotal = formatter.format(Double.valueOf(strTotal));
                    }
                }
                else strTotal = "";

                if (jObjOrder.has("Payment")){
                    strPayment = jObjOrder.getString("Payment");
                    if((strPayment != null) && (strPayment.trim().length() > 0)) {
                        DecimalFormat formatter = new DecimalFormat("#,##0.00");
                        strPayment = formatter.format(Double.valueOf(strPayment));
                    }
                }
                else strPayment = "";

                if (jObjOrder.has("Balance")){
                    strBalance = jObjOrder.getString("Balance");
                    if((strBalance != null) && (strBalance.trim().length() > 0)) {
                        DecimalFormat formatter = new DecimalFormat("#,##0.00");
                        strBalance = formatter.format(Double.valueOf(strBalance));
                    }
                }
                else strBalance = "";

                if (txtDiscount != null) txtDiscount.setText(strDiscount);
                if (txtService != null) txtService.setText(strService);
                if (txtTax != null) txtTax.setText(strTax);
                if (txtTotal != null) txtTotal.setText(strTotal);
                if (txtPayment != null) txtPayment.setText(strPayment);
                if (txtBalance != null) txtBalance.setText(strBalance);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class getBillInfoLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResAddCookingNote;
        private String jErr;
        private String jMes;
        private JSONArray jArrgetBillInfo;
        private String strgetBillInfo;
        private String SecondSt;
        private String FirstSt;
        private String GCheck;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjCookingNote = new JSONObject();
            try {
                jObjCookingNote.put("OutletID",shareData.getOutLetID());
                jObjCookingNote.put("OrderID",jObjOrder.getString("OrderID"));
                System.out.println("jObjgetBillInfo : " + jObjCookingNote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getOrderInfoItemList",String.valueOf(jObjCookingNote));
                System.out.println("respone jObjgetBillInfo: " + respone);
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
                try {
                    jArrgetBillInfo = jResAddCookingNote.getJSONObject("Data").getJSONArray("OrderItemList");
                    for(int count=0;count<jArrgetBillInfo.length();count++){
                        strItemDisplay.add(jArrgetBillInfo.getJSONObject(count).getString("ItemDisplayName"));
                        try {
                            GCheck = jArrgetBillInfo.getJSONObject(count).getString("GSeq"); // มีG เป็น CK
                        } catch (JSONException e) { //ไม่มี Gseq
                            GCheck = "";
                            //e.printStackTrace();
                        }
                        if(!GCheck.equals("")) { // เพิ่มเติม
                            strPriceShow.add("");
                            strShow.add("");
                        }else{
                            strPriceShow.add(jArrgetBillInfo.getJSONObject(count).getString("PriceShow"));
                            try {
                                strShow.add(jArrgetBillInfo.getJSONObject(count).getString("QTY"));
                            } catch (JSONException e) {
                                strShow.add("");
                                //e.printStackTrace();
                            }
                        }
                        if(!jArrgetBillInfo.getJSONObject(count).getString("ItemDisplayName").equals("Service")) {
                            try {
                                FirstSt = jArrgetBillInfo.getJSONObject(count).getString("KPrintNo");
                            } catch (JSONException e) {
                                FirstSt = "";
                                //e.printStackTrace();
                            }
                            try {
                                SecondSt = jArrgetBillInfo.getJSONObject(count).getString("GPrintNo");
                            } catch (JSONException e) {
                                SecondSt = "";
                                //e.printStackTrace();
                            }
                            if(FirstSt.equals("")&&SecondSt.equals("")){
                                strPrinted.add("");
                            }else {
                                String strStatus = "K:" + FirstSt + " G:" + SecondSt;
                                strPrinted.add(strStatus);
                            }
                        }else{
                            strShow.add("");
                            strPrinted.add("");
                        }
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

    public void setListView(){
        listView =(ListView) myViewBillinf.findViewById(R.id.listView_BIllInfo);
        adapter = new ListViewAdapterFragmentTakeOrder(getContext().getApplicationContext(), strItemDisplay , strPriceShow , strShow , strPrinted
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //nothing to do in page billinfo
            }
        });
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
}
