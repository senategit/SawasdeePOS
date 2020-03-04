package com.senate_system.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/6/2017.
 */

public class Fragment_TakeOrder_Command extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewTakeOrderCommand;
    private ListView list;
    private ListViewAdapterFragmentHotkey adapter;
    private ArrayList<String> strDisplay = new ArrayList<String>();
    private SharedParam shareData;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    postJSON post = new postJSON();
    private String OrderMemberID = null;

    public Fragment_TakeOrder_Command() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            public ViewPager activityPager;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    // handle back button
                    activityPager = (ViewPager) getActivity().findViewById(R.id.pagerTakeOrder);
                    activityPager.setCurrentItem(0); // 0= main
                    System.out.println("switch");

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
        try {
            // Inflate the layout for this fragment
            myViewTakeOrderCommand = (View) inflater.inflate(R.layout.fragment_takeorder_command, container, false);

            sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
            editor = sp.edit();

            shareData = (SharedParam) getActivity().getApplication();

            //Keep MemberID from Order
            OrderMemberID = shareData.getOrderMemberID();
            if ((OrderMemberID != null) && (OrderMemberID.trim().length() > 0)) {
                shareData.setIsMemberInOrder(true);
            }

            showDialogSpinner();

            list = (ListView) myViewTakeOrderCommand.findViewById(R.id.listVIew_MemberSearch);
            adapter = new ListViewAdapterFragmentHotkey(getContext().getApplicationContext(), strDisplay);
            list.setAdapter(adapter);
            strDisplay.clear();
            strDisplay.add("New Bill");
            strDisplay.add("Search Product");
            strDisplay.add("Member");
            //strDisplay.add("Print Bill");
            //strDisplay.add("Apply Member");
            //strDisplay.add("Change Default Sale Type");
            //strDisplay.add("Change All Item Sale Type");

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0://new bill
                            // ให้ไป new bill ก่อนแล้วไปหน้า take order
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            dialog.setTitle("NEW BILL");
                            dialog.setIcon(R.drawable.ic_drawer);
                            dialog.setCancelable(true);
                            dialog.setMessage("ต้องการเปิดบิลใหม่ใช่หรือไม่?");
                            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    newBillLoader loadBill = new newBillLoader();
                                    loadBill.execute();
                                }
                            });
                            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            dialog.show();
                            break;
                        case 1://search product
                            Intent intentMem = new Intent(getActivity(), Activity_SeachProduct.class);
                            startActivity(intentMem);
                            getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            break;
                        case 2:// search member
                            //String memberid = shareData.getOrderMemberID();
                            if ((OrderMemberID == null) || (OrderMemberID.trim().length() == 0)) {
                                // send MemberID
                                Intent intent = new Intent(getActivity(), Activity_MemberSearch.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            } else {
                                Intent intent = new Intent(getActivity(), Activity_MemberInfo.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }
                            break;
                        /*
                        case 3://print bill
                            break;
                        case 4: //apply member
                                break;
                        case 5:
                            break;
                        case 6:
                            break;
                        */
                    }
                }
            });
        } catch (Exception e){
            String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace().toString();
            showDialogError(errmsg);
        }

        return myViewTakeOrderCommand;
    }

    private class newBillLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;
        private String respone;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjnewBill = new JSONObject();
            try {
                jObjnewBill.put("OutletID",shareData.getOutLetID());
                jObjnewBill.put("CashierID",sp.getString("CashierID","-1")); // ได้มาจาก user id
                jObjnewBill.put("WSID",sp.getString("WSID","-1"));
                jObjnewBill.put("SaleMode",1); // fix เอาใว้แล้ว
                jObjnewBill.put("DecorID",sp.getString("sDecorID","-1"));
                System.out.println("jObjBill : " + jObjnewBill);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"newBill",String.valueOf(jObjnewBill));
                shareData.setOrderInfo(respone);
                System.out.println("respone newBill: " + respone);
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
                getActivity().finish();
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