package com.senate_system.sawasdeepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
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

public class Fragment_TakeOrder_Hotkey extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewTakeOrderHotkey;
    private ProgressDialog dialog;
    private SharedParam shareData;
    postJSON post = new postJSON();
    private String respone;
    getHotkeyLoader getLoader;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ArrayList<String> HKname = new ArrayList<String>();
    private ArrayList<String> KIDhotkey = new ArrayList<String>();
    private ArrayList<String> HotKeyID = new ArrayList<String>();
    private ArrayList<Bitmap> ImageArr = new ArrayList<Bitmap>();
    private ListView list;
    private ListViewAdapterMember adapterHotkey;
    private ArrayList<String> HKType = new ArrayList<String>();
    private String strKID;
    private String strHotkeyID;
    private String strHKCond;
    private getHotKeyListLoader getList;
    private int SelectValue;
    private JSONArray AllHotkey;
    private String strObjOrder;
    private JSONObject jObjAll;
    private JSONObject jObjOrder;
    private Dialog dialogAdditem;
    private TextView tHead;
    private Button buttonAddCancel;
    private Button buttonAddOk;
    private EditText OpenText;
    private EditText OpenValue;
    private String TextDialog;
    private String ValueDialog;
    private int caseValue=0;
    private EditText edtQty;
    private TextView txtSupname;
    private TextView txtHeadName;
    private Button btnCnote;
    private Button btnDelete;
    private String strName;
    private int ValueCase4;
    private boolean strAllHotkey;
    private Integer noOrderT;
    private String strSEQ;
    private int countListClick;
    private ArrayList<String> saveBackstrHotkeyID = new ArrayList<String>();
    private ArrayList<String> saveBackstrKID = new ArrayList<String>();
    private ArrayList<String> saveBackstrHKCond = new ArrayList<String>();
    private ArrayList<String> saveBackvalueCase = new ArrayList<String>();
    private ImageView imgHome;
    private ImageView imgBack;

    public Fragment_TakeOrder_Hotkey() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // restart listlabel
        strKID="null";
        strHotkeyID="null";
        ValueCase4=-1;
        countListClick=0;
        saveBackvalueCase.clear();
        saveBackstrHotkeyID.clear();
        saveBackstrKID.clear();
        saveBackstrHKCond.clear();
        imgBack.setVisibility(View.INVISIBLE);
        list=(ListView) myViewTakeOrderHotkey.findViewById(R.id.listView_Hotkey);
        setListView();

        getLoader = new getHotkeyLoader();
        getLoader.execute();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    // handle back button
                    settingBackButton();
                    return true;

                }

                return false;
            }
        });
    }

    public void settingBackButton() {
        //countListClick
        // กด 1 ครั้ง item จะไปอยู่ตำแหน่งที่ 0 แต่ countlistclick เป็น 1
        System.out.println("countListClick :"+countListClick);
        countListClick--;
        if (countListClick<1){
            onResume();
        }else {
            countListClick--;
            String caseCheck = saveBackvalueCase.get(countListClick);
            System.out.println("caseCheck :" + caseCheck);
            strHotkeyID = saveBackstrHotkeyID.get(countListClick);
            strKID = saveBackstrKID.get(countListClick);
            strHKCond = saveBackstrHKCond.get(countListClick);
            saveBackvalueCase.remove(countListClick);
            saveBackstrHotkeyID.remove(countListClick);
            saveBackstrKID.remove(countListClick);
            saveBackstrHKCond.remove(countListClick);
            if (caseCheck.equals("1") || caseCheck.equals("2")) {//gethotkey
                getLoader = new getHotkeyLoader();
                getLoader.execute();
            } else if (caseCheck.equals("4")) {//gethotkeylist
                getList = new getHotKeyListLoader();
                getList.execute();
            }
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
        myViewTakeOrderHotkey = (View) inflater.inflate(R.layout.fragment_takeorder_hotkey, container, false);

        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        showDialogSpinner();

        setHeader();

        btnCnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(getActivity(), Activity_CookingNote.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
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

        shareData = (SharedParam) getActivity().getApplication();
        strObjOrder = shareData.getOrderInfo();
        System.out.println("OrderInfo : " +strObjOrder);
        try {
            jObjAll = new JSONObject(strObjOrder);
            jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
            shareData.setLastAccess(jObjOrder.getString("LastAccess"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setDialog();

        //fragment list hotkey

        return myViewTakeOrderHotkey;
    }

    public  void setHeader(){
        // set header
        edtQty = (EditText) getActivity().findViewById(R.id.editText_include_header_QTY);
        edtQty.setText("1");
        txtSupname = (TextView) getActivity().findViewById(R.id.textView_include_helpbar_sup);
        txtSupname.setVisibility(View.INVISIBLE);
        txtHeadName = (TextView) getActivity().findViewById(R.id.textView_include_helpbar);
        txtHeadName.setVisibility(View.INVISIBLE);
        btnCnote = (Button) getActivity().findViewById(R.id.button_Cnote_healp2);
        btnCnote.setVisibility(View.INVISIBLE);
        btnDelete = (Button) getActivity().findViewById(R.id.button_Delete_item_help2);
        btnDelete.setVisibility(View.INVISIBLE);
        imgHome = (ImageView) getActivity().findViewById(R.id.imageView_Home_Refresh);
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
        imgBack = (ImageView) getActivity().findViewById(R.id.imageView_Back_Button);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("countListClick :"+countListClick);
                countListClick--;
                if (countListClick<1){
                    onResume();
                }else {
                    countListClick--;
                    String caseCheck = saveBackvalueCase.get(countListClick);
                    System.out.println("caseCheck :" + caseCheck);
                    strHotkeyID = saveBackstrHotkeyID.get(countListClick);
                    strKID = saveBackstrKID.get(countListClick);
                    strHKCond = saveBackstrHKCond.get(countListClick);
                    saveBackvalueCase.remove(countListClick);
                    saveBackstrHotkeyID.remove(countListClick);
                    saveBackstrKID.remove(countListClick);
                    saveBackstrHKCond.remove(countListClick);
                    if (caseCheck.equals("1") || caseCheck.equals("2")) {//gethotkey
                        getLoader = new getHotkeyLoader();
                        getLoader.execute();
                    } else if (caseCheck.equals("4")) {//gethotkeylist
                        getList = new getHotKeyListLoader();
                        getList.execute();
                    }
                }
            }
        });
        //
    }

    public void setDialog(){
        //===============================Dialog Set==================================
        dialogAdditem = new Dialog(this.getContext());
        dialogAdditem.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAdditem.setContentView(R.layout.dialog_additem);
        tHead = (TextView) dialogAdditem.findViewById(R.id.textView_Header);
        OpenText = (EditText) dialogAdditem.findViewById(R.id.edt_weight);
        OpenValue = (EditText) dialogAdditem.findViewById(R.id.edt_postcode);
        buttonAddCancel = (Button) dialogAdditem.findViewById(R.id.button_cancel);
        buttonAddOk = (Button) dialogAdditem.findViewById(R.id.button_ok);
        tHead.setText("Input Item");
        OpenText.setHint("Item Description");
        OpenValue.setHint("Item Price");

        buttonAddOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextDialog = OpenText.getText().toString();
                ValueDialog = OpenValue.getText().toString();
                if(TextDialog.equals("")||ValueDialog.equals("")){
                    Toast.makeText(getContext().getApplicationContext(),"กรุณาใส่ข้อมูลให้ครบถ้วน !", Toast.LENGTH_SHORT).show();
                } else if(caseValue==1){
                    addItemOpenTextLoader openLoad = new addItemOpenTextLoader();
                    openLoad.execute();
                } else if(caseValue==2){
                    addItemOpenValueLoader valueLoader = new addItemOpenValueLoader();
                    valueLoader.execute();
                } else if(caseValue==3){
                    addItemOpenBothLoader bothLoader = new addItemOpenBothLoader();
                    bothLoader.execute();
                }
                dialogAdditem.dismiss();
            }
        });
        buttonAddCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAdditem.dismiss();
            }
        });
        //===============================Dialog Set==================================
    }

    public void setListView(){
        adapterHotkey = new ListViewAdapterMember(getContext().getApplicationContext(), HKname , ImageArr );
        list.setAdapter(adapterHotkey);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            JSONArray jHKcond;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                imgBack.setVisibility(View.VISIBLE);
                //getOrder
                if(ValueCase4<1) {
                    strHotkeyID = HotKeyID.get(position);
                    strKID = KIDhotkey.get(position);
                }
                strName = HKname.get(position);
                SelectValue = position;



                switch (Integer.valueOf(HKType.get(position))){
                    case 1 : //call gethotkey
                        ValueCase4 = -1;
                        //
                        countListClick++;
                        saveBackstrHotkeyID.add(strHotkeyID);
                        saveBackstrKID.add(strKID);
                        saveBackstrHKCond.add("");
                        saveBackvalueCase.add(HKType.get(position));
                        //
                        getLoader = new getHotkeyLoader();
                        getLoader.execute();
                        break;
                    case 2 : //call gethotkey
                        ValueCase4 = -1;
                        //
                        countListClick++;
                        saveBackstrHotkeyID.add(strHotkeyID);
                        saveBackstrKID.add(strKID);
                        saveBackstrHKCond.add("");
                        saveBackvalueCase.add(HKType.get(position));
                        //
                        getLoader = new getHotkeyLoader();
                        getLoader.execute();
                        break;
                    case 3 : //add item
                        try {
                            checkHKtype3();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4 : // call gethotkeylistlabel
                        ValueCase4 = 2;
                        try {
                            jHKcond = new JSONArray(shareData.getObjSelectHotkey());
                            strHKCond = jHKcond.getJSONObject(position).getString("HKCondition");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //
                        countListClick++;
                        saveBackstrHotkeyID.add(strHotkeyID);
                        saveBackstrKID.add(strKID);
                        saveBackstrHKCond.add(strHKCond);
                        saveBackvalueCase.add(HKType.get(position));
                        //
                        getList = new getHotKeyListLoader();
                        getList.execute();
                        break;
                    case 5 : //
                        showDialogError("ยังไม่พร้อมใช้งาน");
                        break;
                    case 6 : //discount
                        showDialogError("ยังไม่พร้อมใช้งาน");
                        break;
                }

            }
        });
    }

    private class addItemFixLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemFix;
        private JSONArray jArraddItemFix;
        EditText edtQty = (EditText) getActivity().findViewById(R.id.editText_include_header_QTY);
        String QTY;
        private JSONObject jObjBill;
        private JSONArray arrListOrder;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTY);
                jObjtakeOrder.put("WSID",sp.getString("WSID","-1"));
                jObjtakeOrder.put("DecorID",jObjOrder.getString("DecorId"));
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("MMRSeq","null");
                jObjtakeOrder.put("SaleMode",jObjOrder.getString("SaleMode"));
                jObjtakeOrder.put("ResourceID",shareData.getResourceId());
                jObjtakeOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjaddItemFix : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"addItemFix",String.valueOf(jObjtakeOrder));
                System.out.println("respone addItemFix: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResaddItemFix = new JSONObject(respone);
                jErr = jResaddItemFix.getString("ErrorCode");
                jMes = jResaddItemFix.getString("ErrorMesg");
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
                    //send item order to add cooking note
                    jObjBill = new JSONObject(strObjOrder);
                    arrListOrder = jObjBill.getJSONObject("Data").getJSONArray("OrderItemList");
                    shareData.setLastAccess(jObjBill.getJSONObject("Data").getJSONObject("Order").getString("LastAccess"));
                    strSEQ = jObjBill.getJSONObject("Data").getJSONObject("ItemDataLast").getString("Seq");
                    shareData.setSEQDeleteItem(strSEQ);
                    shareData.setObjSelectItem(jObjBill.getJSONObject("Data").getString("ItemDataLast"));
                    System.out.println("setSEQDeleteItem : "+ shareData.getSEQDeleteItem());
                    System.out.println("setObjSelectItem : "+ shareData.getObjSelectItem());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jObjAll = new JSONObject(strObjOrder);
                    jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtHeadName.setVisibility(View.VISIBLE);
                txtSupname.setVisibility(View.VISIBLE);
                txtHeadName.setText(QTY + " x " +strName);
                txtSupname.setText("เพิ่มรายการแล้ว");
                btnCnote.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }else{
                showDialogError(jMes);
            }

            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            QTY = edtQty.getText().toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class getHotkeyLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResgetHotkey;
        private JSONArray jArrgetHotkey;
        private Bitmap bmpBLANK;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("HotkeyID",strHotkeyID);
                jObjtakeOrder.put("KID",strKID);
                System.out.println("jObjResource : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getHotkey",String.valueOf(jObjtakeOrder));
                System.out.println("respone getHotkey: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResgetHotkey = new JSONObject(respone);
                shareData.setObjSelectHotkey(jResgetHotkey.getJSONObject("Data").getString("HotkeyList"));
                jErr = jResgetHotkey.getString("ErrorCode");
                jMes = jResgetHotkey.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                try {
                    jArrgetHotkey = jResgetHotkey.getJSONObject("Data").getJSONArray("HotkeyList");
                    for(int countList=0;countList<jArrgetHotkey.length();countList++){
                        try {
                            HKType.add(jArrgetHotkey.getJSONObject(countList).getString("HKType"));
                            HKname.add(jArrgetHotkey.getJSONObject(countList).getString("HKName"));
                            KIDhotkey.add(jArrgetHotkey.getJSONObject(countList).getString("KID"));
                            HotKeyID.add(jArrgetHotkey.getJSONObject(countList).getString("HotKeyID"));
                            ImageArr.add(bmpBLANK);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showDialogError(jMes);
            }
            adapterHotkey.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            bmpBLANK = BitmapFactory.decodeResource(getResources(), R.drawable.image_blank);
            HKname.clear();
            KIDhotkey.clear();
            HotKeyID.clear();
            ImageArr.clear();
            HKType.clear();
            adapterHotkey.notifyDataSetChanged();
            dialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class getHotKeyListLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResgetListlebel;
        private JSONArray jArrgetListLebel;
        private Bitmap bmpBLANK;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjListlabel = new JSONObject();
            try {
                jObjListlabel.put("OutletID",shareData.getOutLetID());
                jObjListlabel.put("CashierID",sp.getString("CashierID","-1"));
                jObjListlabel.put("HKCond",strHKCond);
                System.out.println("jObjgetHotkeyListLabel : " + jObjListlabel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getHotkeyListLabel",String.valueOf(jObjListlabel));
                System.out.println("respone getlistlabel: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResgetListlebel = new JSONObject(respone);
                shareData.setObjSelectHotkey(jResgetListlebel.getJSONObject("Data").getString("HotkeyList"));
                jErr = jResgetListlebel.getString("ErrorCode");
                jMes = jResgetListlebel.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                try {
                    jArrgetListLebel = jResgetListlebel.getJSONObject("Data").getJSONArray("HotkeyList");
                    for(int countList=0;countList<jArrgetListLebel.length();countList++){
                        HKType.add(jArrgetListLebel.getJSONObject(countList).getString("HKType"));
                        HKname.add(jArrgetListLebel.getJSONObject(countList).getString("ItemDisplayName"));
                        //KIDhotkey.add(jArrgetListLebel.getJSONObject(countList).getString("KID"));
                        //HotKeyID.add(jArrgetListLebel.getJSONObject(countList).getString("HotKeyID"));
                        ImageArr.add(bmpBLANK);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showDialogError(jMes);
            }
            adapterHotkey.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            bmpBLANK = BitmapFactory.decodeResource(getResources(), R.drawable.image_blank);
            HKname.clear();
            KIDhotkey.clear();
            HotKeyID.clear();
            ImageArr.clear();
            HKType.clear();
            adapterHotkey.notifyDataSetChanged();
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

    public void checkHKtype3() throws JSONException {/*{
                "HKType": 3,
                "ITTypeID": 7,
                "HKName": "ออร์เดิฟพิเศษ",
                "HotKeyID": 27,
                "FuncID": 0,
                "KID": 3803,
                "NeedSerial": false,
                "ITGroupID": 1,
                "IsItemCondiment": false,
                "Seq": 3261,
                "FuncValue": 0,
                "ItemID": 415,
                "IsItemChoice": true,
                "ISGID": 54,
                "ItemCode": "63066",
                "IsItemResource": false,
                "OrderType": 0
            }*/
        try {
            AllHotkey = new JSONArray(shareData.getObjSelectHotkey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            strAllHotkey = AllHotkey.getJSONObject(SelectValue).getString("IsItemResource").equals("true");
        }catch (JSONException e){
            strAllHotkey = false;
        }
        try{
            noOrderT = Integer.valueOf(AllHotkey.getJSONObject(SelectValue).getString("OrderType"));
        }catch (JSONException e){
            noOrderT = 99;
        }
        if(AllHotkey.getJSONObject(SelectValue).getString("IsItemChoice").equals("true")) {//check item choice
            //call addItemFixWithChoice
            showDialogError("addItemFixWithChoice on Developer");
        }else{
            if(strAllHotkey){// choose Resouce page
                //showDialogError("Resource Page on Developer");
                //ดื่มลอย
                showDialogErrorState("กรุณาเลือก Resource");
                shareData.setFreeDrink(true);
                Intent intentMem = new Intent(getActivity(), Activity_SearchResource.class);
                startActivity(intentMem);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }else{// check ordertype
                shareData.setResourceId(null);
                switch (noOrderT){
                    case 0 ://call additemfix
                        addItemFixLoader additemfix = new addItemFixLoader();
                        additemfix.execute();
                        break;
                    case 1 ://call additemopenText
                        caseValue=1;
                        OpenText.setVisibility(View.VISIBLE);
                        OpenText.setText("");
                        OpenValue.setVisibility(View.GONE);
                        OpenValue.setText("-1");
                        dialogAdditem.show();
                        break;
                    case 2 ://call additemopenvalue
                        caseValue=2;
                        OpenText.setVisibility(View.GONE);
                        OpenText.setText("-1");
                        OpenValue.setVisibility(View.VISIBLE);
                        OpenValue.setText("");
                        dialogAdditem.show();
                        break;
                    case 3 ://call additemopenboth
                        caseValue=3;
                        OpenText.setVisibility(View.VISIBLE);
                        OpenText.setText("");
                        OpenValue.setVisibility(View.VISIBLE);
                        OpenValue.setText("");
                        dialogAdditem.show();
                        break;
                    default:
                        showDialogError("ตรวจไม่พบ OrderType เบอร์ : "+Integer.valueOf(AllHotkey.getJSONObject(SelectValue).getString("OrderType")));
                        break;
                }
            }
        }
    }

    private class addItemOpenTextLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemOpenText;
        private JSONArray jArraddItemOpenText;
        EditText edtQty = (EditText) getActivity().findViewById(R.id.editText_include_header_QTY);
        String QTY;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTY);
                jObjtakeOrder.put("WSID",sp.getString("WSID","-1"));
                jObjtakeOrder.put("DecorID",jObjOrder.getString("DecorId"));
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("MMRSeq","null");
                jObjtakeOrder.put("SaleMode",jObjOrder.getString("SaleMode"));
                jObjtakeOrder.put("ResourceID",shareData.getResourceId());
                jObjtakeOrder.put("OpenText",TextDialog);
                jObjtakeOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjaddItemOpenText : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"addItemOpenText",String.valueOf(jObjtakeOrder));
                System.out.println("respone addItemOpenText: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResaddItemOpenText = new JSONObject(respone);
                jErr = jResaddItemOpenText.getString("ErrorCode");
                jMes = jResaddItemOpenText.getString("ErrorMesg");
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
            }else{
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            QTY = edtQty.getText().toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class addItemOpenValueLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemOpenValue;
        private JSONArray jArraddItemOpenValue;
        EditText edtQty = (EditText) getActivity().findViewById(R.id.editText_include_header_QTY);
        String QTY;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTY);
                jObjtakeOrder.put("WSID",sp.getString("WSID","-1"));
                jObjtakeOrder.put("DecorID",jObjOrder.getString("DecorId"));
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("MMRSeq","null");
                jObjtakeOrder.put("SaleMode",jObjOrder.getString("SaleMode"));
                jObjtakeOrder.put("ResourceID",shareData.getResourceId());
                jObjtakeOrder.put("OpenValue",ValueDialog);
                jObjtakeOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjaddItemOpenValue : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"addItemOpenValue",String.valueOf(jObjtakeOrder));
                System.out.println("respone addItemOpenValue: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResaddItemOpenValue = new JSONObject(respone);
                jErr = jResaddItemOpenValue.getString("ErrorCode");
                jMes = jResaddItemOpenValue.getString("ErrorMesg");
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
            }else{
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            QTY = edtQty.getText().toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class addItemOpenBothLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemOpenBoth;
        private JSONArray jArraddItemOpenBoth;
        EditText edtQty = (EditText) getActivity().findViewById(R.id.editText_include_header_QTY);
        String QTY;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTY);
                jObjtakeOrder.put("WSID",sp.getString("WSID","-1"));
                jObjtakeOrder.put("DecorID",jObjOrder.getString("DecorId"));
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("MMRSeq","null");
                jObjtakeOrder.put("SaleMode",jObjOrder.getString("SaleMode"));
                jObjtakeOrder.put("ResourceID",shareData.getResourceId());
                jObjtakeOrder.put("OpenText",TextDialog);
                jObjtakeOrder.put("OpenValue",ValueDialog);
                jObjtakeOrder.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjaddItemOpenBoth : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"addItemOpenBoth",String.valueOf(jObjtakeOrder));
                System.out.println("respone addItemOpenBoth: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResaddItemOpenBoth = new JSONObject(respone);
                jErr = jResaddItemOpenBoth.getString("ErrorCode");
                jMes = jResaddItemOpenBoth.getString("ErrorMesg");
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
            }else{
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            QTY = edtQty.getText().toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class deleteItemLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResdeleteItem;
        private JSONArray jArrdeleteItem;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("Seq",shareData.getSEQDeleteItem());
                jObjtakeOrder.put("WSID",sp.getString("WSID","-1"));
                jObjtakeOrder.put("CashierID",sp.getString("CashierID","-1"));
                jObjtakeOrder.put("LastAccess",shareData.getLastAccess());
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

    public void showDialogErrorState(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){
                        switch (noOrderT){
                            case 0 ://call additemfix
                                addItemFixLoader additemfix = new addItemFixLoader();
                                additemfix.execute();
                                break;
                            case 1 ://call additemopenText
                                caseValue=1;
                                OpenText.setVisibility(View.VISIBLE);
                                OpenValue.setVisibility(View.GONE);
                                OpenValue.setText("-1");
                                dialogAdditem.show();
                                break;
                            case 2 ://call additemopenvalue
                                caseValue=2;
                                OpenText.setVisibility(View.GONE);
                                OpenText.setText("-1");
                                OpenValue.setVisibility(View.VISIBLE);
                                dialogAdditem.show();
                                break;
                            case 3 ://call additemopenboth
                                caseValue=3;
                                OpenText.setVisibility(View.VISIBLE);
                                OpenValue.setVisibility(View.VISIBLE);
                                dialogAdditem.show();
                                break;
                            default:
                                try {
                                    showDialogError("ตรวจไม่พบ OrderType เบอร์ : "+Integer.valueOf(AllHotkey.getJSONObject(SelectValue).getString("OrderType")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}