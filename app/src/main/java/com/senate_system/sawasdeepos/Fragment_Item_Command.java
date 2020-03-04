package com.senate_system.sawasdeepos;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
 * Created by ChoCoFire on 9/25/2017.
 */

public class Fragment_Item_Command extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewItemCommand;
    private ListViewAdapterOrderCommand adapter;
    private ArrayList<String> strItemDisplay = new ArrayList<String>();
    private SharedParam shareData;
    private String strObjBill;
    private JSONObject jObjItemBill;
    private TextView txtProductName;
    private TextView txtProductCode;
    private TextView txtQTY;
    private TextView txtSaletype;
    private TextView txtSaleMem;
    private TextView txtPrice;
    private TextView txtPriceQty;
    private String strLastAccess;
    private JSONObject jObjLastAccess;
    private String saveValue;
    private Dialog dialogVoid;
    private Button buttonVoidOk;
    private Button buttonVoidCancel;
    private EditText edtReason;
    private ProgressDialog dialog;
    postJSON post = new postJSON();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public Fragment_Item_Command() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewItemCommand = (View) inflater.inflate(R.layout.fragment_item_command, container, false);

        shareData = (SharedParam) getActivity().getApplication();
        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        setTextView();
        showDialogSpinner();
        setVoidItemDialog();

        shareData = (SharedParam) getActivity().getApplication();
        strObjBill = shareData.getObjSelectItem();/*{
                "SaleTypeID": 1,
                "QTY": 1,
                "ItemType": 10,
                "UnitPrice": 300,
                "OutletItem": 0,
                "ItemName1": "ถั่วรวม",
                "ItemName": "ถั่วรวม",
                "Seq": 2,
                "SaleType": "Member",
                "ItemDisplayName": "ถั่วรวม",
                "ITTypeID": 5,
                "ItemID": 383,
                "KPrintNo": 729955,
                "LMMR": 0,
                "OrderID": 282238,
                "PriceShow": 300,
                "ItemCode": "63034",
                "CountRow": 1
            }*/
        try {
            jObjItemBill = new JSONObject(strObjBill);
            txtProductName.setText(jObjItemBill.getString("ItemDisplayName"));
            txtProductCode.setText(jObjItemBill.getString("ItemCode"));
            txtQTY.setText(jObjItemBill.getString("QTY"));

            String priceshow = jObjItemBill.getString("PriceShow");
            if((priceshow != null) && (priceshow.trim().length() > 0)) {
                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                priceshow = formatter.format(Double.valueOf(priceshow));
            }
            else priceshow = "";
            txtPrice.setText(priceshow);

            String unitprice = jObjItemBill.getString("UnitPrice");
            if((unitprice != null) && (unitprice.trim().length() > 0)) {
                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                unitprice = formatter.format(Double.valueOf(unitprice));
            }
            else unitprice = "";
            txtPriceQty.setText(unitprice);

            txtSaletype.setText(jObjItemBill.getString("SaleType"));
            txtSaleMem.setText("-");
            //set fix sale "-"
        } catch (JSONException e) {
            e.printStackTrace();
        }

        strItemDisplay.clear();

        strItemDisplay.add("Cooking Note");
        strItemDisplay.add("Void Item");
        //strItemDisplay.add("Change Sale Type"); ยังไม่ได้ใช้

        //listview
        ListView listViewItem = (ListView) myViewItemCommand.findViewById(R.id.listView_ItemCOmmand);
        adapter = new ListViewAdapterOrderCommand(getContext().getApplicationContext(), strItemDisplay );
        listViewItem.setAdapter(adapter);
        listViewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //
                if(strItemDisplay.get(position).equals("Cooking Note")){
                    getFragmentManager().beginTransaction()
                            .remove(Fragment_Item_Command.this).commit();
                    Intent intent = new Intent(getActivity(), Activity_CookingNote.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }else if(strItemDisplay.get(position).equals("Void Item")){ // void item show dialog
                    //service void item
                    dialogVoid.show();
                }
            }
        });

        return myViewItemCommand;
    }

    public void setVoidItemDialog(){
        //===============================Dialog Set==================================
        dialogVoid = new Dialog(getContext());
        dialogVoid.setContentView(R.layout.dialog_voiditem);
        edtReason = (EditText) dialogVoid.findViewById(R.id.editText_reason);
        buttonVoidCancel = (Button) dialogVoid.findViewById(R.id.button_cancel);
        buttonVoidOk = (Button) dialogVoid.findViewById(R.id.button_ok);
        //===============================Dialog Set==================================

        buttonVoidCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogVoid.dismiss();
            }
        });
        buttonVoidOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtReason.getText().toString().equals("")) {
                    /*postVoid = new postVoidSyncLoader();
                    postVoid.execute();*/
                    voidItemLoader voidLoad = new voidItemLoader();
                    voidLoad.execute();
                    dialogVoid.dismiss();
                }else{
                    dialogVoid.dismiss();
                    showDialogError("กรุณาใส่เหตุผล");
                }
            }
        });
    }

    public void setTextView(){
        txtProductName = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_Productname);
        txtProductCode = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_Productcode);
        txtQTY = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_qty);
        txtSaletype = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_Saletype);
        txtSaleMem = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_attention);
        txtPrice = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_Price);
        txtPriceQty = (TextView) myViewItemCommand.findViewById(R.id.textView_itemCommand_priceqty);
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

    private class voidItemLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResvoidItem;
        private JSONArray jArrvoidItem;
        private String respone;
        private String strReason;
        private String strObjOrder;
        private JSONObject jObjBill;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjvoidItem = new JSONObject();
            try {
                jObjvoidItem.put("OutletID",shareData.getOutLetID());
                jObjvoidItem.put("OrderID",jObjItemBill.getString("OrderID"));
                jObjvoidItem.put("Seq",jObjItemBill.getString("Seq"));
                jObjvoidItem.put("WSID",sp.getString("WSID","-1"));
                jObjvoidItem.put("CashierID",sp.getString("CashierID","-1"));
                jObjvoidItem.put("VoidReason",strReason);
                jObjvoidItem.put("LastAccess",shareData.getLastAccess());
                System.out.println("jObjvoidItem : " + jObjvoidItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"voidItem",String.valueOf(jObjvoidItem));
                System.out.println("respone voidItem: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResvoidItem = new JSONObject(respone);
                jErr = jResvoidItem.getString("ErrorCode");
                jMes = jResvoidItem.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                shareData.setOrderInfo(respone);
                strObjOrder = shareData.getOrderInfo();
                try {
                    //send item order to add cooking note
                    jObjBill = new JSONObject(strObjOrder);
                    shareData.setLastAccess(jObjBill.getJSONObject("Data").getJSONObject("Order").getString("LastAccess"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getFragmentManager().beginTransaction()
                        .remove(Fragment_Item_Command.this).commit();
            }else{
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
            strReason = edtReason.getText().toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}
