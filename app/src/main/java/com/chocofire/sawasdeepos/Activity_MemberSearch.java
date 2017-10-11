package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
 * Created by ChoCoFire on 9/28/2017.
 */

public class Activity_MemberSearch extends AppCompatActivity {

    private ListView listMember;
    private ArrayList<String> strDisplay = new ArrayList<String>();
    private ArrayList<Bitmap> bmpImg = new ArrayList<Bitmap>();
    SharedParam shareData;
    private ProgressDialog dialog;
    private String respone;
    postJSON post = new postJSON();
    private EditText searchText;
    private Button btnSearch;
    private ListViewAdapterMember adapterMem;
    private JSONArray jArrMember;
    private ActionBar actionbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membersearch);

        shareData = (SharedParam) getApplication();
        searchText = (EditText) findViewById(R.id.editText_searchMember);
        btnSearch = (Button) findViewById(R.id.button_summit_Member);

        listMember = (ListView) findViewById(R.id.listView_membersearchAct);
        adapterMem= new ListViewAdapterMember(getApplicationContext(), strDisplay , bmpImg);
        listMember.setAdapter(adapterMem);
        listMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getmember info
                try {
                    shareData.setObjMemberInfo(jArrMember.getString(position));
                    shareData.setIsMemberInOrder(false);
                    Intent intent = new Intent(getApplication(), Activity_MemberInfo.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    showDialogError(e.toString());
                    e.printStackTrace();
                }
            }
        });

        showDialogSpinner();

        configureToolbar();

        //
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load list member
                getMemberSearchLoader getMemload = new getMemberSearchLoader();
                getMemload.execute();
            }
        });

    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_cookingnote);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Member Search");
        actionbar.setHomeAsUpIndicator(R.drawable.back_copy);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        /*Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_drawer);
        toolbar.setOverflowIcon(drawable);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // ย้อนกลับ
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class getMemberSearchLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResBill;
        private String jErr;
        private String jMes;
        private String strGetText;
        private Bitmap bmp;

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
                    for(int count=0;count<jArrMember.length();count++){
                        strDisplay.add(jArrMember.getJSONObject(count).getString("MemberName")
                        +" "+jArrMember.getJSONObject(count).getString("MemberCode"));
                        bmpImg.add(bmp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showDialogError(jMes);
            }
            adapterMem.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            bmpImg.clear();
            strDisplay.clear();
            dialog.show();
            strGetText = searchText.getText().toString();
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.image_blank);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public void showDialogSpinner(){
        dialog = new ProgressDialog(this);
        dialog.setTitle("กำลังดาวน์โหลดข้อมูล ...");
        dialog.setMessage("โปรดรอสักครู่ ...");
        dialog.setProgressStyle(dialog.STYLE_SPINNER); //dialog spinning
        dialog.setCancelable(false);
    } // dialog setting

    public void showDialogError(String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
