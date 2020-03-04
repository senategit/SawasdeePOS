package com.senate_system.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/8/2017.
 */

public class Fragment_Floorplan extends Fragment {

    final String P_NAME = "App_Config";
    private ProgressDialog dialog;
    private View myViewFloorplan;
    GridView grid;
    postJSON post = new postJSON();
    ArrayList<String> web = new ArrayList<String>();
    ArrayList<Bitmap> imageId = new ArrayList<Bitmap>();
    ArrayList<String> strGuest = new ArrayList<String>();
    ArrayList<String> strCovor = new ArrayList<String>();
    ArrayList<String> strBalance = new ArrayList<String>();
    ArrayList<String> stringObj = new ArrayList<String>();
    ArrayList<String> strOrderID = new ArrayList<String>();
    ArrayList<String> lastID = new ArrayList<String>();
    ArrayList<String> lastLN = new ArrayList<String>();
    private CustomAdapterFloorPlan adapter;
    private int countClick=0;
    private int resumeLevel;
    private String respone;
    private JSONObject jsonRespone;
    private Integer countTable;
    private JSONArray jArr;
    private String layoutLevel;
    private String layoutID;
    private ArrayList<String> stringLID = new ArrayList<String>();
    private String lidClick;
    String lastPost;
    private String stringBitmap;
    private SharedParam shareData;
    private postFloorLoader postLoad;
    private ActionBar actionbar;
    private String LName;
    private ListView list;
    private ListViewAdapterTakeOrder adapter2;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int posSelect;
    private String sOutletID;
    private String sDecorID;
    private JSONObject sWorkDateBean;
    countBillLoader countLoad;
    private LinearLayout li;
    private String Orderselect;
    private JSONObject jObjBill;
    private int SelectPosition;

    public Fragment_Floorplan() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        list=(ListView) myViewFloorplan.findViewById(R.id.listView_takeFloorplan);
        grid=(GridView) myViewFloorplan.findViewById(R.id.gridView1);
        li = (LinearLayout) myViewFloorplan.findViewById(R.id.headerListview);

        setListView();
        setGridView();

        li.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        grid.setVisibility(View.VISIBLE);

        lastID.clear();
        lastLN.clear();
        countClick=0;
        layoutLevel = "1";
        lidClick = null;
        postLoad = new postFloorLoader();
        postLoad.execute();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    // handle back button
                    setBackbutton();

                    return true;

                }

                return false;
            }
        });
        /*Toast.makeText(getContext().getApplicationContext(), "resume main",
                Toast.LENGTH_SHORT).show();*/
    }

    public void setBackbutton(){
        System.out.println("countTable :"+countTable);
        if(countTable==2) { // ออกไปหน้า workspace
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Alert Dialog");
            dialog.setIcon(R.drawable.ic_drawer);
            dialog.setCancelable(true);
            dialog.setMessage("ต้องการกลับไปหน้า Workspace ?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity_Navigation) getActivity()).switchToFragment1();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }else if(countTable==4){ //กด 2 ครั้ง
            countClick--;
            grid.setNumColumns(2); // ตั้งค่าจำนวนแนวตั้ง
            lidClick = lastID.get(countClick);
            layoutLevel = lastLN.get(countClick);
            lastID.remove(countClick);
            lastLN.remove(countClick);
            //postFloorplan(layoutLevel,lidClick);
            postLoad = new postFloorLoader();
            postLoad.execute();
        }else if(countTable==5){ //ได้หน้าโต๊ะ
            countClick--;
            li.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
            grid.setNumColumns(5); // ตั้งค่าจำนวนแนวตั้ง
            lidClick = lastID.get(countClick);
            layoutLevel = lastLN.get(countClick);
            lastID.remove(countClick);
            lastLN.remove(countClick);
            //postFloorplan(layoutLevel,lidClick);
            postLoad = new postFloorLoader();
            postLoad.execute();
        }else if(countTable==3){ //กลับไปหน้าแรก
            onResume();
        }
    }

    public void setListView(){
        adapter2 = new ListViewAdapterTakeOrder(getContext().getApplicationContext(), strGuest , strCovor , strBalance);
        list.setAdapter(adapter2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //getOrder
                Orderselect = strOrderID.get(position);
                getOrderLoader getLoad = new getOrderLoader();
                getLoad.execute();
            }
        });
    }

    public void setGridView(){
        adapter = new CustomAdapterFloorPlan(getContext().getApplicationContext(), web, imageId);
        grid.setNumColumns(2); // ตั้งค่าจำนวนแนวตั้ง
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {/*
                LName = web.get(SelectPosition);
                lastLN = layoutLevel;
                layoutLevel = String.valueOf(Integer.valueOf(countTable)+1);
                if(layoutLevel.equals("3")){//เลือกห้อง floor
                    sOutletID = stringObj.get(position);
                    shareData.setOutLetID(sOutletID); // ไม่ 1 ก็ 2
                    System.out.println("######################################################## ========================= "+shareData.getOutLetID());
                }else if(layoutLevel.equals("5")){ //เลือกโต๊ะ
                    shareData.setZoneID(lidClick); //เอาใว้ก่อน
                    sDecorID = stringObj.get(position);
                    editor.putString("sDecorID", sDecorID);
                    editor.apply();
                }else if(layoutLevel.equals("4")){
                    shareData.setFloorID(lidClick); //เอาใว้ก่อน
                }
                lastID=lidClick;
                lidClick = stringLID.get(position);*/// old code by mos

                //==============================================p'wut=============================//
                countClick++;
                System.out.println("countClick : "+countClick);
                try {
                    if (countTable == 1) {
                        //Keep FloorInfo
                        JSONObject jsobjFloorInfo = jArr.getJSONObject(position);
                        if (jsobjFloorInfo != null) shareData.setFloorInfo(jsobjFloorInfo);
                    } else if (countTable == 2) {
                        //Keep OutletInfo
                        JSONObject jsobjOutletInfo = jArr.getJSONObject(position);
                        if(jsobjOutletInfo != null) shareData.setOutletInfo(jsobjOutletInfo);
                    } else if (countTable == 3) {
                        //Keep ZoneInfo
                        JSONObject jsobjZoneInfo = jArr.getJSONObject(position);
                        if(jsobjZoneInfo != null) shareData.setZoneInfo(jsobjZoneInfo);
                    } else if (countTable == 4) {
                        //Keep DecorInfo
                        JSONObject jsobjDecorInfo = jArr.getJSONObject(position);
                        if(jsobjDecorInfo != null) shareData.setDecorInfo(jsobjDecorInfo);
                    }
                }
                catch(JSONException e) {
                    //System.out.println("error : " + e.getMessage());
                    //System.out.println("cause : " + e.getCause());
                    //System.out.println("stack trace : " + e.getStackTrace());
                    String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace().toString();
                    Toast.makeText(getContext().getApplicationContext(), errmsg, Toast.LENGTH_SHORT).show();
                }

                lastLN.add(layoutLevel);
                layoutLevel = String.valueOf(Integer.valueOf(countTable)+1);
                lastID.add(lidClick);
                lidClick = stringLID.get(position);

                if(layoutLevel.equals("3")){
                    sOutletID = stringObj.get(position);
                    shareData.setOutLetID(sOutletID);
                }else if(layoutLevel.equals("5")){
                    sDecorID = stringObj.get(position);
                    editor.putString("sDecorID", sDecorID);
                    editor.apply();
                }

                //==============================================p'wut=============================//
                //Toast.makeText(getContext().getApplicationContext(), "You Clicked at " +layoutLevel + lidClick, Toast.LENGTH_SHORT).show();
                postLoad = new postFloorLoader();
                postLoad.execute();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewFloorplan = (View) inflater.inflate(R.layout.fragment_floorplan, container, false);

        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        configureToolbar();

        shareData = (SharedParam) getActivity().getApplication();

        showDialogSpinner();

        return myViewFloorplan;
    }

    public void postFloorplan(String layoutLevel1,String layoutID1){
        /*String("{\n" +
                "  \"LayoutLevel\": 1,\n" +
                "  \"LayoutID\": 1,\n" +
                "  \"WSID\": 1\n" +
                "}");*/
        String json = new String("{    \"LayoutLevel\": "+layoutLevel1+",    \"LayoutID\": "+layoutID1+"}");
        System.out.println(json);
        try {
            respone = post.postForm(shareData.getURL()+"getLayout?langid=1",json);
            System.out.println("respone : " + respone);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonRespone = new JSONObject(respone);
            countTable = Integer.valueOf(jsonRespone.getJSONObject("Data").getString("LayoutLevel"));
            /*if(countTable==2) { //เก็บ outletid ด้วย
                LName = jsonRespone.getJSONObject("Data").getJSONObject("FloorInfo").getString("LName");
                jArr = jsonRespone.getJSONObject("Data").getJSONArray("LayoutList");
                for(int i=0;i<jArr.length();i++){
                    web.add(jArr.getJSONObject(i).getString("LName"));
                    stringBitmap = jArr.getJSONObject(i).getString("Img");
                    byte[] decodedString = Base64.decode(stringBitmap, Base64.DEFAULT);
                    imageId.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    stringLID.add(jArr.getJSONObject(i).getString("LID"));
                    stringObj.add(jArr.getJSONObject(i).getString("ObjID"));
                }
            }else if(countTable==3){ //เก็บ floorid ด้วย
                LName = jsonRespone.getJSONObject("Data").getJSONObject("OutletInfo").getString("LName");
                jArr = jsonRespone.getJSONObject("Data").getJSONArray("LayoutList");
                for(int i=0;i<jArr.length();i++){
                    web.add(jArr.getJSONObject(i).getString("LName"));
                    stringBitmap = jArr.getJSONObject(i).getString("Img");
                    byte[] decodedString = Base64.decode(stringBitmap, Base64.DEFAULT);
                    imageId.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    stringLID.add(jArr.getJSONObject(i).getString("LID"));
                    stringObj.add(jArr.getJSONObject(i).getString("ObjID"));
                }
            }
            else if(countTable==4){ //เก็บ zone id ด้วย
                grid.setNumColumns(3); // ตั้งค่าจำนวนแนวตั้ง
                jArr = jsonRespone.getJSONObject("Data").getJSONArray("LayoutList");
                for(int i=0;i<jArr.length();i++){
                    web.add(jArr.getJSONObject(i).getString("LName"));
                    stringBitmap = jArr.getJSONObject(i).getString("Img");
                    byte[] decodedString = Base64.decode(stringBitmap, Base64.DEFAULT);
                    imageId.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    stringLID.add(jArr.getJSONObject(i).getString("LID"));
                    stringObj.add(jArr.getJSONObject(i).getString("ObjID"));
                }
            }*/ //old code by mos
            //====================================p'wut===========================================//
            if(countTable==2) {

                //Keep FloorInfo
                if(jsonRespone.getJSONObject("Data").has("FloorInfo")) {
                    JSONObject jsobjFloorInfo = jsonRespone.getJSONObject("Data").getJSONObject("FloorInfo");
                    if (jsobjFloorInfo != null) shareData.setFloorInfo(jsobjFloorInfo);
                }

                if(shareData.getFloorInfo() != null) LName = shareData.getFloorInfo().getString("FloorName");
                else LName = "Outlet List";

                LName = jsonRespone.getJSONObject("Data").getJSONObject("FloorInfo").getString("LName");
                jArr = jsonRespone.getJSONObject("Data").getJSONArray("LayoutList");
                for(int i=0;i<jArr.length();i++){
                    web.add(jArr.getJSONObject(i).getString("LName"));
                    stringBitmap = jArr.getJSONObject(i).getString("Img");
                    byte[] decodedString = Base64.decode(stringBitmap, Base64.DEFAULT);
                    imageId.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    stringLID.add(jArr.getJSONObject(i).getString("LID"));
                    stringObj.add(jArr.getJSONObject(i).getString("ObjID"));
                }
            }else if(countTable==3){

                //Keep OutletInfo
                if(jsonRespone.getJSONObject("Data").has("OutletInfo")) {
                    JSONObject jsobjOutletInfo = jsonRespone.getJSONObject("Data").getJSONObject("OutletInfo");
                    if (jsobjOutletInfo != null) shareData.setOutletInfo(jsobjOutletInfo);
                }

                //Keep WorkDateInfo
                if(jsonRespone.getJSONObject("Data").has("WorkDateInfo")) {
                    JSONObject jsobjWorkDateInfo = jsonRespone.getJSONObject("Data").getJSONObject("WorkDateInfo");
                    if (jsobjWorkDateInfo != null) {
                        shareData.setWorkDateInfo(jsobjWorkDateInfo);

                        sWorkDateBean = jsobjWorkDateInfo.getJSONObject("WorkDateBean");
                        shareData.setjObjWorkDateBean(sWorkDateBean);
                    }
                }

                //Keep WorkShiftInfo
                if(jsonRespone.getJSONObject("Data").has("WorkShiftInfo")) {
                    JSONObject jsobjWorkShiftInfo = jsonRespone.getJSONObject("Data").getJSONObject("WorkShiftInfo");
                    if (jsobjWorkShiftInfo != null) shareData.setWorkShiftInfo(jsobjWorkShiftInfo);
                }

                if(shareData.getOutletInfo() != null) LName = shareData.getOutletInfo().getString("OutletName");
                else LName = "Zone List";

                LName = jsonRespone.getJSONObject("Data").getJSONObject("OutletInfo").getString("LName");
                jArr = jsonRespone.getJSONObject("Data").getJSONArray("LayoutList");
                for(int i=0;i<jArr.length();i++){
                    web.add(jArr.getJSONObject(i).getString("LName"));
                    stringBitmap = jArr.getJSONObject(i).getString("Img");
                    byte[] decodedString = Base64.decode(stringBitmap, Base64.DEFAULT);
                    imageId.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    stringLID.add(jArr.getJSONObject(i).getString("LID"));
                    stringObj.add(jArr.getJSONObject(i).getString("ObjID"));
                }
            }
            else if(countTable==4){

                //Keep OutletInfo
                if(jsonRespone.getJSONObject("Data").has("OutletInfo")) {
                    JSONObject jsobjOutletInfo = jsonRespone.getJSONObject("Data").getJSONObject("OutletInfo");
                    if (jsobjOutletInfo != null) shareData.setOutletInfo(jsobjOutletInfo);
                }

                //Keep WorkDateInfo
                if(jsonRespone.getJSONObject("Data").has("WorkDateInfo")) {
                    JSONObject jsobjWorkDateInfo = jsonRespone.getJSONObject("Data").getJSONObject("WorkDateInfo");
                    if (jsobjWorkDateInfo != null) {
                        shareData.setWorkDateInfo(jsobjWorkDateInfo);

                        sWorkDateBean = jsobjWorkDateInfo.getJSONObject("WorkDateBean");
                        shareData.setjObjWorkDateBean(sWorkDateBean);
                    }
                }

                //Keep WorkShiftInfo
                if(jsonRespone.getJSONObject("Data").has("WorkShiftInfo")) {
                    JSONObject jsobjWorkShiftInfo = jsonRespone.getJSONObject("Data").getJSONObject("WorkShiftInfo");
                    if (jsobjWorkShiftInfo != null) shareData.setWorkShiftInfo(jsobjWorkShiftInfo);
                }

                //Keep ZoneInfo
                if(jsonRespone.getJSONObject("Data").has("ZoneInfo")) {
                    JSONObject jsobjZoneInfo = jsonRespone.getJSONObject("Data").getJSONObject("ZoneInfo");
                    if (jsobjZoneInfo != null) shareData.setZoneInfo(jsobjZoneInfo);
                }

                if(shareData.getZoneInfo() != null) LName = shareData.getZoneInfo().getString("ZoneName");
                else LName = "Decor List";

                grid.setNumColumns(5); // ตั้งค่าจำนวนแนวตั้ง
                //LName = "Main Bar";
                jArr = jsonRespone.getJSONObject("Data").getJSONArray("LayoutList");
                for(int i=0;i<jArr.length();i++){
                    web.add(jArr.getJSONObject(i).getString("LName"));
                    stringBitmap = jArr.getJSONObject(i).getString("Img");
                    byte[] decodedString = Base64.decode(stringBitmap, Base64.DEFAULT);
                    imageId.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    stringLID.add(jArr.getJSONObject(i).getString("LID"));
                    stringObj.add(jArr.getJSONObject(i).getString("ObjID"));
                }
            }
            //====================================p'wut===========================================//
        } catch (JSONException e) {
            e.printStackTrace();
            String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace().toString();
            Toast.makeText(getContext().getApplicationContext(), errmsg, Toast.LENGTH_SHORT).show();
        }

        /*
        try {
            sWorkDateBean = jsonRespone.getJSONObject("Data").getJSONObject("WorkDateInfo").getJSONObject("WorkDateBean");
            shareData.setjObjWorkDateBean(jsonRespone.getJSONObject("Data").getJSONObject("WorkDateInfo").getJSONObject("WorkDateBean"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    private class postFloorLoader extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            postFloorplan(layoutLevel,lidClick);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            actionbar.setTitle(LName);
            adapter.notifyDataSetChanged();
            if(countTable==5){
                countLoad = new countBillLoader();
                countLoad.execute();
            }
            /*
            grid.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            adapter2 = new ListViewAdapterTakeOrder(getContext().getApplicationContext(), strGuest , strCovor , strBalance);
            list.setAdapter(adapter2);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                }
            });*/
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            web.clear();
            imageId.clear();
            stringLID.clear();
            stringObj.clear();
            adapter.notifyDataSetChanged();
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class countBillLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResBill;
        private JSONArray jArrBill;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjBill = new JSONObject();
            try {
                jObjBill.put("OutletID",sOutletID);
                jObjBill.put("DecorID",sDecorID);
                jObjBill.put("WorkDateBean",sWorkDateBean);
                System.out.println("jObjBill : " + jObjBill);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getBillList",String.valueOf(jObjBill));
                System.out.println("respone countBill: " + respone);
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
                    jArrBill = jResBill.getJSONObject("Data").getJSONArray("BillList");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jArrBill.length()==0){
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
                            layoutLevel= lastLN.get(countClick-1);
                            lidClick = lastID.get(countClick-1);
                            postLoad = new postFloorLoader();
                            postLoad.execute();
                        }
                    });
                    dialog.show();
                }else if(jArrBill.length()==1){
                    // ให้ เข้าหน้า takeorder ไปเลย
                    try {
                        Orderselect = jArrBill.getJSONObject(0).getString("OrderID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getOrderLoader getLoad = new getOrderLoader();
                    getLoad.execute();
                }
                else{
                    try {
                        // ถ้ามีbill อยู่แล้วให้โชว์ bill ขึ้นมา
                        for (int countBill = 0; countBill < jArrBill.length(); countBill++) {
                            strGuest.add(jArrBill.getJSONObject(countBill).getString("GuestChkNo"));

                            //strCovor.add(jArrBill.getJSONObject(countBill).getString("0"));
                            strCovor.add("0");

                            String strBalanceFmt = jArrBill.getJSONObject(countBill).getString("Balance");
                            if((strBalanceFmt != null) && (strBalanceFmt.trim().length() > 0)) {
                                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                                strBalanceFmt = formatter.format(Double.valueOf(strBalanceFmt));
                                strBalance.add(strBalanceFmt);
                            }
                            else {
                                strBalance.add("");
                            }

                            strOrderID.add(jArrBill.getJSONObject(countBill).getString("OrderID"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    grid.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    li.setVisibility(View.VISIBLE);
                    adapter2.notifyDataSetChanged();
                }
            }else{
                showDialogError(jMes);
            }
        }

        @Override
        protected void onPreExecute() {
            strGuest.clear();
            strCovor.clear();
            strBalance.clear();
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class newBillLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjnewBill = new JSONObject();
            try {
                jObjnewBill.put("OutletID",sOutletID);
                jObjnewBill.put("CashierID",sp.getString("CashierID","-1")); // ได้มาจาก user id
                jObjnewBill.put("WSID",sp.getString("WSID","-1"));
                jObjnewBill.put("SaleMode",1); // fix เอาใว้แล้ว
                jObjnewBill.put("DecorID",sDecorID);
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

    private class getOrderLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",sOutletID);
                jObjGetOrder.put("CashierID",sp.getString("CashierID","-1")); // ได้มาจาก user id
                jObjGetOrder.put("OrderID",Orderselect);
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

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) myViewFloorplan.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setTitle("Floor Plan");
        actionbar.setHomeAsUpIndicator(R.drawable.back_copy);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        /*Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_drawer);
        toolbar.setOverflowIcon(drawable);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        System.out.println("click");
        switch (itemId) {
            // Android home
            case android.R.id.home:
                setBackbutton();
                /*Activity_Navigation.drawerLayout.openDrawer(GravityCompat.START);
                System.out.println("click");*/
                return true;
            // manage other entries if you have it ...
        }
        return true;
    }

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
