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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class Fragment_Search_Product extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewProduct;
    ArrayList<String> list1 = new ArrayList<String>();
    ArrayList<String> list2 = new ArrayList<String>();
    ArrayList<Bitmap> bmp = new ArrayList<Bitmap>();
    ArrayList<String> strAdditem = new ArrayList<String>();
    private ListViewAdapterProduct adapterS;
    private ListView listView;
    private ProgressDialog dialog;
    private SharedParam shareData;
    private String respone;
    postJSON post = new postJSON();
    private JSONArray jArrMember;
    private EditText edtSearchtxtProduct;
    private Button btnActivitySearch;
    private EditText edtSearchtxtProduct2;
    private JSONArray AllHotkey;
    private boolean strAllHotkey;
    private Integer noOrderT;
    private TextView tHead;
    private EditText OpenText;
    private EditText OpenValue;
    private Button buttonAddCancel;
    private Button buttonAddOk;
    private String TextDialog;
    private String ValueDialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String strObjOrder;
    private JSONObject jObjAll;
    private JSONObject jObjOrder;
    private int caseValue=0;
    private String strShowOnDialog;
    private JSONObject jObjSelect;
    private int ValueCase4;
    private String strHotkeyID;
    private String strKID;
    private int SelectValue;
    private Dialog dialogProduct;
    private Dialog dialogAdditem;
    private String strSEQ;
    private String QTYinputFromDialog;
    private EditText edtQty;
    private TextView txtSupname;
    private TextView txtHeadName;
    private Button btnCnote;
    private Button btnDelete;

    public Fragment_Search_Product() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if (shareData.getOutLetID() == null) {
            showDialogError("กรุณาเลือก outlet ก่อน");
            return;
        }

        if (shareData.getjObjWorkDateBean() == null) {
            showDialogError("ระบบตรวจไม่พบข้อมูลการเปิดวัน กรุณาเปิดวันการขายสำหรับ outlet ก่อน");
            return;
        }

        if(shareData.getTabSelected().equals("3")) {
            itemSearchLoader itemLoader = new itemSearchLoader();
            itemLoader.execute();
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
        myViewProduct = (View) inflater.inflate(R.layout.fragment_search_product, container, false);

        shareData = (SharedParam) getActivity().getApplication();
        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        strObjOrder = shareData.getOrderInfo();
        System.out.println("OrderInfo : " +strObjOrder);
        try {
            jObjAll = new JSONObject(strObjOrder);
            jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
            shareData.setLastAccess(jObjOrder.getString("LastAccess"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HeaderView();

        showDialogSpinner();

        setDialog();

        edtSearchtxtProduct = (EditText) getActivity().findViewById(R.id.editTextlist_search);
        if(shareData.getCheckSearchPage().equals("Act")) {
            edtSearchtxtProduct = (EditText) getActivity().findViewById(R.id.editText_SearchProduct2);
            btnActivitySearch = (Button) getActivity().findViewById(R.id.button_summit_SearchAct);
            btnActivitySearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemSearchLoader itemLoader = new itemSearchLoader();
                    itemLoader.execute();
                }
            });
        }


        adapterS = new ListViewAdapterProduct(getContext(), list1 , list2 , bmp );
        listView = (ListView) myViewProduct.findViewById(R.id.listView_Product); // define list view
        listView.setAdapter(adapterS); //set listview from json
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // click product
                setDialogProduct();
                SelectValue = arg2;
                strShowOnDialog = list1.get(arg2);
                //shareData.setObjSelectHotkey(strAdditem.get(arg2));
                if(shareData.getCheckSearchPage().equals("Act")){ // เข้าจากการtake order
                    System.out.println(strShowOnDialog);
                    tHead.setText("ต้องการ add item :" + strShowOnDialog );
                    dialogProduct.show();
                }else{ // ไม่ได้เข้าจากการtakeorder

                }
            }
        }); //set adapter list

        return myViewProduct;
    }

    public void HeaderView(){
        txtSupname = (TextView) getActivity().findViewById(R.id.textView_include_helpbar_sup);
        txtSupname.setVisibility(View.INVISIBLE);
        txtHeadName = (TextView) getActivity().findViewById(R.id.textView_include_helpbar);
        txtHeadName.setVisibility(View.INVISIBLE);
        btnCnote = (Button) getActivity().findViewById(R.id.button_Cnote_healp2);
        btnCnote.setVisibility(View.INVISIBLE);
        btnDelete = (Button) getActivity().findViewById(R.id.button_Delete_item_help2);
        btnDelete.setVisibility(View.INVISIBLE);

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
    }

    private class itemSearchLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;
        private String strGetText;
        private Bitmap bmpA;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("SearchText",strGetText);
                jObjGetOrder.put("WHID","null");
                System.out.println("jObjitemSearch : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"itemSearch",String.valueOf(jObjGetOrder));
                System.out.println("respone jObjitemSearch: " + respone);
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
                // remark ram ไม่พอเนื่องจากค้นหาเยอะเกินไป
                try {
                    jArrMember = jResBill.getJSONObject("Data").getJSONArray("ItemList");
                    shareData.setObjSelectHotkey(jArrMember.toString());
                    for(int count=0;count<jArrMember.length();count++){
                        strAdditem.add(jArrMember.getString(count));
                        list1.add(jArrMember.getJSONObject(count).getString("ItemDisplayName"));
                        list2.add(jArrMember.getJSONObject(count).getString("Price"));
                        bmp.add(bmpA);// wait time
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
            list2.clear();
            list1.clear();
            bmp.clear();
            strGetText = edtSearchtxtProduct.getText().toString();
            bmpA = BitmapFactory.decodeResource(getResources(), R.drawable.image_blank);
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

    public void setDialogProduct(){
        //===============================Dialog Set==================================
        dialogProduct = new Dialog(this.getContext());
        dialogProduct.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogProduct.setContentView(R.layout.dialog_additem);
        tHead = (TextView) dialogProduct.findViewById(R.id.textView_Header);
        OpenText = (EditText) dialogProduct.findViewById(R.id.edt_weight);
        OpenValue = (EditText) dialogProduct.findViewById(R.id.edt_postcode);
        buttonAddCancel = (Button) dialogProduct.findViewById(R.id.button_cancel);
        buttonAddOk = (Button) dialogProduct.findViewById(R.id.button_ok);
        tHead.setText("ต้องการ add item :" + strShowOnDialog );
        OpenText.setHint("Item Description");
        OpenValue.setHint("Item Qty");
        OpenText.setVisibility(View.GONE);
        buttonAddOk.setOnClickListener(new View.OnClickListener() { //add item
            @Override
            public void onClick(View v) {
                QTYinputFromDialog = OpenValue.getText().toString(); //qty
                System.out.println("QTYinputFromDialog :"+QTYinputFromDialog);
                dialogProduct.dismiss();
                try {
                    checkHKtype3();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonAddCancel.setOnClickListener(new View.OnClickListener() { // close dialog
            @Override
            public void onClick(View v) {
                dialogProduct.dismiss();
            }
        });
        //===========================================================================
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

    public void showDialogErrorState(String Message){
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

    private class addItemFixLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemFix;
        private JSONArray jArraddItemFix;
        private JSONObject jObjBill;
        private JSONArray arrListOrder;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTYinputFromDialog);
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
                txtHeadName.setText(QTYinputFromDialog + " x " +strShowOnDialog);
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
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class addItemOpenTextLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemOpenText;
        private JSONArray jArraddItemOpenText;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTYinputFromDialog);
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
                showDialogError("Add item เรียบร้อย");
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

    private class addItemOpenValueLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemOpenValue;
        private JSONArray jArraddItemOpenValue;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTYinputFromDialog);
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
                showDialogError("Add item เรียบร้อย");
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

    private class addItemOpenBothLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResaddItemOpenBoth;
        private JSONArray jArraddItemOpenBoth;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                jObjtakeOrder.put("ItemID",AllHotkey.getJSONObject(SelectValue).getString("ItemID"));
                jObjtakeOrder.put("Qty",QTYinputFromDialog);
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
                showDialogError("Add item เรียบร้อย");
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
                HeaderView();
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

}