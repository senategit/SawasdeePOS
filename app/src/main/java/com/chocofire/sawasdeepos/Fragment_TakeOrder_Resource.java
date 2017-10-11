package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class Fragment_TakeOrder_Resource extends Fragment {

    private View myViewTakeOrderResource;
    private ActionBar actionbar;
    private ProgressDialog dialog;
    private SharedParam shareData;
    postJSON post = new postJSON();
    private String respone;
    private String strObjOrder;
    private JSONObject jObjAll;
    private JSONObject jObjOrder;
    ArrayList<String> strResourceName = new ArrayList<String>();
    ArrayList<String> strResourceCode = new ArrayList<String>();
    ArrayList<String> strStartTime = new ArrayList<String>();
    ArrayList<String> strCountAble = new ArrayList<String>();
    ArrayList<String> strEndTime = new ArrayList<String>();
    ArrayList<Bitmap> bmpImage = new ArrayList<Bitmap>();
    private ListView listView;
    private ListViewAdapterPageResource adapter;
    private JSONArray jArrgetOrderResource;
    private SwipeRefreshLayout mSwipeRefresh;

    public Fragment_TakeOrder_Resource() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderResourceLoader reLoader = new getOrderResourceLoader();
        reLoader.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myViewTakeOrderResource = (View) inflater.inflate(R.layout.fragment_takeorder_resource, container, false);

        mSwipeRefresh = (SwipeRefreshLayout) myViewTakeOrderResource.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                mSwipeRefresh.setRefreshing(false);
            }
        });

        shareData = (SharedParam) getActivity().getApplication();
        strObjOrder = shareData.getOrderInfo();
        try {
            jObjAll = new JSONObject(strObjOrder);
            jObjOrder = jObjAll.getJSONObject("Data").getJSONObject("Order");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showDialogSpinner();
        //configureToolbar();
        setListView();




        return myViewTakeOrderResource;
    }

    private class getOrderResourceLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResgetOrderResource;
        private Bitmap bmpYELLOW;
        private Bitmap bmpRED;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjtakeOrder = new JSONObject();
            try {
                jObjtakeOrder.put("OutletID",shareData.getOutLetID());
                jObjtakeOrder.put("OrderID",jObjOrder.getString("OrderID"));
                System.out.println("jObjaddItemWithItemCode : " + jObjtakeOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getOrderResource",String.valueOf(jObjtakeOrder));
                System.out.println("respone getOrderResource: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResgetOrderResource = new JSONObject(respone);
                jErr = jResgetOrderResource.getString("ErrorCode");
                jMes = jResgetOrderResource.getString("ErrorMesg");
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
                    jArrgetOrderResource = jResgetOrderResource.getJSONObject("Data").getJSONArray("ResourceList");
                    for(int count=0;count<jArrgetOrderResource.length();count++){
                        strResourceName.add(jArrgetOrderResource.getJSONObject(count).getString("ResourceName"));
                        strResourceCode.add(jArrgetOrderResource.getJSONObject(count).getString("ResourceCode"));
                        strStartTime.add(jArrgetOrderResource.getJSONObject(count).getString("STimeText"));
                        strCountAble.add(jArrgetOrderResource.getJSONObject(count).getString("OrderQty"));
                        if(jArrgetOrderResource.getJSONObject(count).getString("Status").equals("2")) {
                            bmpImage.add(bmpYELLOW);
                            strEndTime.add(jArrgetOrderResource.getJSONObject(count).getString("ETimeText"));
                        }else{
                            bmpImage.add(bmpRED);
                            strEndTime.add("-");
                        }
                        // ยังไม่ได้เพิ่มรูป
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
            strResourceName.clear();
            strResourceCode.clear();
            strStartTime.clear();
            strEndTime.clear();
            strCountAble.clear();
            bmpImage.clear();
            bmpRED = BitmapFactory.decodeResource(getResources(), R.drawable.drink_red);
            bmpYELLOW = BitmapFactory.decodeResource(getResources(), R.drawable.drink_yellow);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public void setListView(){
        listView =(ListView) myViewTakeOrderResource.findViewById(R.id.listView_ResourceTakeorder);
        adapter = new ListViewAdapterPageResource(getContext().getApplicationContext()
                , strResourceName , strResourceCode, strStartTime, strCountAble , strEndTime , bmpImage );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    shareData.setObjSelectResource(jArrgetOrderResource.getString(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), Activity_ResourceMG.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });
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