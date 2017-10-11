package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by ChoCoFire on 9/6/2017.
 */

public class Fragment_TakeOrder_Main extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewTakeOrderMain;
    private TextView text_Order;
    private TextView text_Decor;
    private TextView text_Guest;
    private TextView text_Mode;
    private TextView text_Cover;
    private TextView text_Member;
    private TextView text_Balance;
    SharedParam shareData;
    private String strObjBill;
    private JSONObject jObjBill;
    private String strOrder;
    private String strDecor;
    private String strGuest;
    private String strMode;
    private String strCover;
    private String strMember;
    private String strBalance;
    private ProgressDialog dialog;
    private JSONArray arrListOrder;
    private ArrayList<String> strItemDisplay = new ArrayList<String>();
    private ArrayList<String> strPriceShow = new ArrayList<String>();
    private ArrayList<String> strShow = new ArrayList<String>();
    private ArrayList<String> strPrinted = new ArrayList<String>();
    private ListView listView;
    private ListViewAdapterFragmentTakeOrder adapter;
    private ActionBar actionbar;
    private String respone;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    postJSON post = new postJSON();
    private String strObjOrder;
    private JSONObject jObjAll;
    private JSONObject jObjOrder;
    private SwipeRefreshLayout mSwipeRefresh;
    private String FirstSt;
    private String SecondSt;
    private String strStatus;

    public Fragment_TakeOrder_Main() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        getOrderLoader orderLoader = new getOrderLoader();
        orderLoader.execute();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    // handle back button
                    if(getFragmentManager().findFragmentById(R.id.frame_newFrag) != null) {
                        getFragmentManager()
                                .beginTransaction().
                                remove(getFragmentManager().findFragmentById(R.id.frame_newFrag)).commit();
                    }else{
                        //refresh page
                        onResume();
                    }

                    return true;

                }

                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewTakeOrderMain = (View) inflater.inflate(R.layout.fragment_takeorder_main, container, false);

        mSwipeRefresh = (SwipeRefreshLayout) myViewTakeOrderMain.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                mSwipeRefresh.setRefreshing(false);
            }
        });

        shareData = (SharedParam) getActivity().getApplication();
        strObjBill = shareData.getOrderInfo();
        setListView();
        setHeader();

        strObjOrder = shareData.getOrderInfo();
        try {
            jObjAll = new JSONObject(strObjOrder);
            System.out.println("jObjAll : "+jObjAll);
            jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
            shareData.setLastAccess(jObjOrder.getString("LastAccess"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        shareData = (SharedParam) getActivity().getApplication();

        showDialogSpinner();


        return myViewTakeOrderMain;
    }

    public void setListView(){
        listView =(ListView) myViewTakeOrderMain.findViewById(R.id.listView_takeOrder);
        adapter = new ListViewAdapterFragmentTakeOrder(getContext().getApplicationContext(), strItemDisplay , strPriceShow , strShow , strPrinted);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // new bill information
                if(!strItemDisplay.get(position).equals("Service")) {
                    try {
                        shareData.setObjSelectItem(arrListOrder.getJSONObject(position).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Fragment newFragment = new Fragment_Item_Command();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    ft.replace(R.id.frame_newFrag, newFragment).commit();
                    System.out.println("Errorrorororoororororororororr");
                    actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    actionbar.setTitle("Item Command");
                }
            }
        });
    }

    public void setHeader(){
        try {
            strObjBill = shareData.getOrderInfo();
            jObjBill = new JSONObject(strObjBill);
            strOrder = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("OrderDateText");
            strDecor = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("DecorName");
            strGuest = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("GuestChkNo");
            strMode = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("SaleModeName");

            if(jObjBill.getJSONObject("Data").getJSONObject("Order").has("Cover")) strCover = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("Cover");
            else strCover = "0";

            if(jObjBill.getJSONObject("Data").getJSONObject("Order").has("MemberDispName")) strMember = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("MemberDispName");
            else strMember = "";

            strBalance = jObjBill.getJSONObject("Data").getJSONObject("Order").getString("Balance");
            if((strBalance != null) && (strBalance.trim().length() > 0)) {
                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                strBalance = formatter.format(Double.valueOf(strBalance));
            }

            //set list
            arrListOrder = jObjBill.getJSONObject("Data").getJSONArray("OrderItemList");
            for(int countOrder =0 ; countOrder < arrListOrder.length() ; countOrder++){
                strItemDisplay.add(arrListOrder.getJSONObject(countOrder).getString("ItemDisplayName"));
                strPriceShow.add(arrListOrder.getJSONObject(countOrder).getString("PriceShow"));
                if(!arrListOrder.getJSONObject(countOrder).getString("ItemDisplayName").equals("Service")) {
                    try {
                        strShow.add(arrListOrder.getJSONObject(countOrder).getString("QTY"));
                    } catch (JSONException e) {
                        strShow.add("");
                        //e.printStackTrace();
                    }
                    try {
                        FirstSt = arrListOrder.getJSONObject(countOrder).getString("KPrintNo");
                    } catch (JSONException e) {
                        FirstSt = "";
                        //e.printStackTrace();
                    }
                    try {
                        SecondSt = arrListOrder.getJSONObject(countOrder).getString("GPrintNo");
                    } catch (JSONException e) {
                        SecondSt = "";
                        //e.printStackTrace();
                    }

                    //assign printed status
                    if(FirstSt.equals("")&&SecondSt.equals("")){
                        strPrinted.add("");
                    }else {
                        String strStatus = "";
                        if(!FirstSt.equals("")) strStatus = "K:" + FirstSt;
                        if(!SecondSt.equals("")) {
                            if(!strStatus.equals("")) strStatus += ", ";
                            strStatus += "G:" + SecondSt;
                        }
                        strPrinted.add(strStatus);
                    }
                }else{
                    strShow.add("");
                    strPrinted.add("");
                }
            }
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        text_Order = (TextView) getActivity().findViewById(R.id.textView_Order_page_Takeorder);
        text_Decor = (TextView) getActivity().findViewById(R.id.textView_Decor_page_Takeorder);
        text_Guest = (TextView) getActivity().findViewById(R.id.textView_Guest_page_Takeorder);
        text_Mode = (TextView) getActivity().findViewById(R.id.textView_Mode_page_Takeorder);
        text_Cover = (TextView) getActivity().findViewById(R.id.textView_cover_page_Takeorder);
        text_Member = (TextView) getActivity().findViewById(R.id.textView_member_page_Takeorder);
        text_Balance = (TextView) getActivity().findViewById(R.id.textView_balance_page_Takeorder12);

        // get OrderInfo

        text_Order.setText(strOrder);
        text_Decor.setText(strDecor);
        text_Guest.setText(strGuest);
        text_Mode.setText(strMode);
        text_Cover.setText(strCover);
        text_Member.setText(strMember);
        text_Balance.setText(strBalance);
    }

    private class getOrderLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1")); // ได้มาจาก user id
                jObjGetOrder.put("OrderID",jObjOrder.getString("OrderID"));
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
            if(jErr.equals("1")){
                //
            }else{
                showDialogError(jMes);
            }
            setHeader();
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            strItemDisplay.clear();
            strPriceShow.clear();
            strShow.clear();
            strPrinted.clear();
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
}