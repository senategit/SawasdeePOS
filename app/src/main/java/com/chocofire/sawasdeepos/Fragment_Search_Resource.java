package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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

public class Fragment_Search_Resource extends Fragment {

    private View myViewResource;
    private CustomAdapterResource adapterResource;
    private GridView grid;
    private ArrayList<Bitmap> imageId = new ArrayList<Bitmap>();
    private ArrayList<String> strOnBitmap = new ArrayList<String>();
    private Bitmap bitmaptest;
    private ProgressDialog dialog;
    private String respone;
    private SharedParam shareData;
    postJSON post = new postJSON();
    private String strSearchText = "";
    private EditText edtSearchtxt;
    private Button btnSearchResource;
    private String strCheck = "";
    private JSONArray jArrResourceList;
    private Button btnActivitySearch;

    public Fragment_Search_Resource() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if(shareData.getTabSelected().equals("2")){
            SearchResourceLoader resourceLoader = new SearchResourceLoader();
            resourceLoader.execute();
        }
        if(shareData.getCheckSearchPage().equals("Act")){
            shareData.setResourceWay("2");
            SearchResourceLoader resourceLoader = new SearchResourceLoader();
            resourceLoader.execute();
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
        myViewResource = (View) inflater.inflate(R.layout.fragment_search_resource, container, false);

        shareData = (SharedParam) getActivity().getApplication();
        strCheck = shareData.getTabSelected();
        showDialogSpinner();

        setGridView();

        edtSearchtxt = (EditText) getActivity().findViewById(R.id.editTextlist_search);
        shareData.setResourceWay("1");
        if(shareData.getCheckSearchPage().equals("Act")) {
            edtSearchtxt = (EditText) getActivity().findViewById(R.id.editText_SearchProduct2);
            btnActivitySearch = (Button) getActivity().findViewById(R.id.button_summit_SearchAct);
            btnActivitySearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareData.setResourceWay("2");
                    SearchResourceLoader resourceLoader = new SearchResourceLoader();
                    resourceLoader.execute();
                }
            });
        }

        return myViewResource;
    }

    private class SearchResourceLoader extends AsyncTask<Integer, Integer, String> {
        private String jErr;
        private String jMes;
        private JSONObject jResresourceSearch;
        private String str1;
        private String str2;
        private String idColor;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjresourceSearch = new JSONObject();
            try {
                jObjresourceSearch.put("OutletID",shareData.getOutLetID());
                jObjresourceSearch.put("SearchText",strSearchText);
                jObjresourceSearch.put("ResourceType","null");
                System.out.println("jObjResource : " + jObjresourceSearch);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"resourceSearch",String.valueOf(jObjresourceSearch));
                System.out.println("respone resourceSearch: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResresourceSearch = new JSONObject(respone);
                jErr = jResresourceSearch.getString("ErrorCode");
                jMes = jResresourceSearch.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }

        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                try {
                    jArrResourceList = jResresourceSearch.getJSONObject("Data").getJSONArray("ResourceList");
                    imageId.clear();
                    for(int countList=0;countList<jArrResourceList.length();countList++){
                        str1 = jArrResourceList.getJSONObject(countList).getString("ResourceName");
                        str2 = jArrResourceList.getJSONObject(countList).getString("ResourceCode");
                        idColor = jArrResourceList.getJSONObject(countList).getString("StatusColorCode");
                        imageId.add(drawMultilineTextToBitmap(getContext(),idColor,str1+"\n"+str2));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapterResource.notifyDataSetChanged();
            }else{
                showDialogError(jMes);
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            imageId.clear();
            dialog.show();
            strSearchText = edtSearchtxt.getText().toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public Bitmap drawMultilineTextToBitmap(Context gContext, String gResId, String gText) {
        // prepare canvas
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        /*Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);*/

        Bitmap bitmap=Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        bitmap.eraseColor(Color.parseColor(gResId));
        //Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(255, 255, 255));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth)/2;
        float y = (bitmap.getHeight() - textHeight)/2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    public void setGridView(){
        grid=(GridView) myViewResource.findViewById(R.id.gridview_Resource);
        adapterResource = new CustomAdapterResource(getContext().getApplicationContext(), imageId);
        grid.setNumColumns(4); // ตั้งค่าจำนวนแนวตั้ง
        grid.setAdapter(adapterResource);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    shareData.setResourceId(jArrResourceList.getString(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(shareData.getFreeDrink()){
                    try {
                        shareData.setResourceId(jArrResourceList.getJSONObject(position).getString("ResourceID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().finish();
                }else {
                    if (shareData.getChangeWay().equals("99")) {
                        getActivity().finish();
                    }
                    Intent intent = new Intent(getActivity(), Activity_ResourceInfo.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }

            }
        });
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
