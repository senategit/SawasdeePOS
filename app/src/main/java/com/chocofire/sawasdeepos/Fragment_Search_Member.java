package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class Fragment_Search_Member extends Fragment {

    private View myViewMember;
    private ListView listView;
    private ListViewAdapterMember adapterS;
    ArrayList<String> list1;
    private ArrayList<Bitmap> bmp = new ArrayList<Bitmap>();
    private ArrayList<String> strDisplay = new ArrayList<String>();
    private ArrayList<Bitmap> bmpImg = new ArrayList<Bitmap>();
    postJSON post = new postJSON();
    private SharedParam shareData;
    private ProgressDialog dialog;
    private String respone;
    private ImageView ImgBackBTN;
    private EditText edtSearchtxt;
    private Button btnSearchMember;
    private JSONArray jArrMember;
    private TabLayout tabLayout;
    private String strCheck = "Summit";
    private Bitmap bmpBlank;

    public Fragment_Search_Member() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("################################3" + shareData.getOrderInfo());
        System.out.println("################################3" + shareData.getOrderID());
        if(shareData.getTabSelected().equals("1")) {
            getMemberSearchLoader memberLoad = new getMemberSearchLoader();
            memberLoad.execute();
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
        myViewMember = (View) inflater.inflate(R.layout.fragment_search_member, container, false);

        shareData = (SharedParam) getActivity().getApplication();
        strCheck = shareData.getTabSelected();

        showDialogSpinner();
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout_takeorder);
        edtSearchtxt = (EditText) getActivity().findViewById(R.id.editTextlist_search);


        /*btnSearch = (Button) getActivity().findViewById(R.id.button_SearchSummit);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        //setOnClickMember();


        adapterS = new ListViewAdapterMember(getContext(), strDisplay ,bmpImg);
        listView = (ListView) myViewMember.findViewById(R.id.listView_Member); // define list view
        listView.setAdapter(adapterS); //set listview from json
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //ไปหน้า member info ได้
                //getmember info
                try {
                    shareData.setObjMemberInfo(jArrMember.getString(arg2));
                    shareData.setIsMemberInOrder(false);
                    Intent intent = new Intent(getActivity(), Activity_MemberInfo.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    showDialogError(e.toString());
                    e.printStackTrace();
                }
            }
        }); //set adapter list


        return myViewMember;
    }

    private class getMemberSearchLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;
        private String strGetText;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjGetOrder = new JSONObject();
            try {
                jObjGetOrder.put("OutletID",shareData.getOutLetID());
                jObjGetOrder.put("SearchText",strGetText);
                System.out.println("jObjmemberSearch : " + jObjGetOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"memberSearch",String.valueOf(jObjGetOrder));
                System.out.println("respone jObjmemberSearch: " + respone);
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
                try {
                    jArrMember = jResBill.getJSONObject("Data").getJSONArray("MemberList");
                    strDisplay.clear();
                    for(int count=0;count<jArrMember.length();count++){
                        strDisplay.add(jArrMember.getJSONObject(count).getString("MemberName")
                                +" "+jArrMember.getJSONObject(count).getString("MemberCode"));
                        bmpImg.add(bmpBlank);
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
            bmpImg.clear();
            strDisplay.clear();
            strGetText = edtSearchtxt.getText().toString();
            bmpBlank = BitmapFactory.decodeResource(getResources(), R.drawable.image_blank);
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