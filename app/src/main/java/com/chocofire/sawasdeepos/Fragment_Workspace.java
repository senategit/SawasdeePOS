package com.chocofire.sawasdeepos;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class Fragment_Workspace extends Fragment {

    final String P_NAME = "App_Config";
    private View myViewMainMenu;
    private ProgressDialog dialog;
    private SharedParam shareData;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String respone;
    postJSON post = new postJSON();
    private SwipeRefreshLayout mSwipeRefresh;
    private TextView txtFloorName;
    private TextView txtFloorDecor;
    private TextView txtOutletName;
    private TextView txtOutletDecor;
    private TextView txtZoneName;
    private TextView txtZoneDecor;
    private TextView txtBillOpen;
    private TextView txtBillClose;
    private TextView txtResrcOnWorkNow;
    private TextView txtResrcOnFloor;
    private TextView txtResrcTakeCare;
    private TextView txtResrcRunning;
    private TextView txtMemberTakeCare;
    private TextView txtMemberOnWork;
    private ViewPager activityPager;


    public Fragment_Workspace() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getWorkspaceInfoLoader workLoad = new getWorkspaceInfoLoader();
        workLoad.execute();
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
        myViewMainMenu = (View) inflater.inflate(R.layout.fragment_workspace, container, false);
        configureToolbar();

        activityPager = (ViewPager) getActivity().findViewById(R.id.pagerWoke);

        showDialogSpinner();

        setTextView();

        mSwipeRefresh = (SwipeRefreshLayout) myViewMainMenu.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                mSwipeRefresh.setRefreshing(false);
            }
        });



        shareData = (SharedParam) getActivity().getApplication();
        sp = this.getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();



        return myViewMainMenu;
    }

    public void setTextView(){
        txtFloorName = (TextView) myViewMainMenu.findViewById(R.id.txtFloorName);
        txtFloorDecor = (TextView) myViewMainMenu.findViewById(R.id.txtFloorDecor);
        txtOutletName = (TextView) myViewMainMenu.findViewById(R.id.txtOutletName);
        txtOutletDecor = (TextView) myViewMainMenu.findViewById(R.id.txtOutletDecor);
        txtZoneName = (TextView) myViewMainMenu.findViewById(R.id.txtZoneName);
        txtZoneDecor = (TextView) myViewMainMenu.findViewById(R.id.txtZoneDecor);

        txtBillOpen = (TextView) myViewMainMenu.findViewById(R.id.txtBillOpen);
        txtBillClose = (TextView) myViewMainMenu.findViewById(R.id.txtBillClose);

        txtResrcOnWorkNow = (TextView) myViewMainMenu.findViewById(R.id.txtResrcOnWorkNow);
        txtResrcOnFloor = (TextView) myViewMainMenu.findViewById(R.id.txtResrcOnFloor);
        txtResrcTakeCare = (TextView) myViewMainMenu.findViewById(R.id.txtResrcTakeCare);
        txtResrcRunning = (TextView) myViewMainMenu.findViewById(R.id.txtResrcRunning);

        txtMemberTakeCare = (TextView) myViewMainMenu.findViewById(R.id.txtMemberTakeCare);
        txtMemberOnWork = (TextView) myViewMainMenu.findViewById(R.id.txtMemberOnWork);
    }
    /*public void setTextView(){
        txtMain1 = (TextView) myViewMainMenu.findViewById(R.id.textView_Main_1);
        txtMain2 = (TextView) myViewMainMenu.findViewById(R.id.textView_Main_2);
        txtMain3 = (TextView) myViewMainMenu.findViewById(R.id.textView_Main_3);
        txtSherBet = (TextView) myViewMainMenu.findViewById(R.id.textView_Sherbet_Table);
        txtSHERBET = (TextView) myViewMainMenu.findViewById(R.id.textView_SHERBER_table);
        txtMiniBar = (TextView) myViewMainMenu.findViewById(R.id.textView_MiniBar_Table);
        txtBillOpen = (TextView) myViewMainMenu.findViewById(R.id.textView_BillOpen);
        txtBillClose = (TextView) myViewMainMenu.findViewById(R.id.textView_BillClose);
        txtOnWorkNow = (TextView) myViewMainMenu.findViewById(R.id.textView_OnWorkNow);
        txtOnFloor = (TextView) myViewMainMenu.findViewById(R.id.textView_OnFloor);
        txtTakeCareRe = (TextView) myViewMainMenu.findViewById(R.id.textView_takeCareRe);
        txtRunning = (TextView) myViewMainMenu.findViewById(R.id.textView_Running);
        txtOnTakeCare = (TextView) myViewMainMenu.findViewById(R.id.textView_OnTakeCare);
        txtOnWorkMem = (TextView) myViewMainMenu.findViewById(R.id.textView_OnWorkMem);
    }*/ // old code by mos


    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) myViewMainMenu.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setTitle("Workspace");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        /*Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_drawer);
        toolbar.setOverflowIcon(drawable);*/
    }

    private class getWorkspaceInfoLoader extends AsyncTask<Integer, Integer, String> {

        private JSONObject jResgetWorkspaceInfo;
        private String jErr;
        private String jMes;
        private JSONArray jArrgetWorkspaceInfo;

        @Override
        protected String doInBackground(Integer... params) {
            JSONObject jObjCookingNote = new JSONObject();
            try {
                String floorid = null;
                String outletid = null;
                String zoneid = null;

                if (shareData.getFloorInfo() != null) floorid = shareData.getFloorInfo().getString("FloorID");
                if (shareData.getOutletInfo() != null) outletid = shareData.getOutletInfo().getString("OutletID");
                if (shareData.getZoneInfo() != null) zoneid = shareData.getZoneInfo().getString("ZoneID");
                //outletid = shareData.getOutLetID();

                jObjCookingNote.put("CashierID",sp.getString("CashierID","-1"));
                jObjCookingNote.put("FloorID",floorid);
                jObjCookingNote.put("OutletID", outletid);
                jObjCookingNote.put("ZoneID",zoneid);
                System.out.println("jObjgetWorkspaceInfo : " + jObjCookingNote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                respone = post.postForm(shareData.getURL()+"getWorkspaceInfo",String.valueOf(jObjCookingNote));
                System.out.println("respone getWorkspaceInfo: " + respone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jResgetWorkspaceInfo = new JSONObject(respone);
                jErr = jResgetWorkspaceInfo.getString("ErrorCode");
                jMes = jResgetWorkspaceInfo.getString("ErrorMesg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respone;
        }
        @Override
        protected void onPostExecute(String result) {
            if(jErr.equals("1")){
                /*"Data": {
                "Resource": {
                    "OnFloorText": "0/0",
                            "OnFloor": 0,
                            "OnRunningText": "0/0",
                            "OnWorking": 0,
                            "OnRunning": 0,
                            "AllResource": 330,
                            "OwnResourceText": "0",
                            "OwnResource": 0,
                            "OnWorkingText": "0/330"
                },
                "Bill": {
                    "OpenedBillText": "0",
                            "ClosedBillText": "0"
                },
                "FloorPlan": {
                    "FloorName": "",
                            "OutletID": null,
                            "OutletName": "",
                            "FloorID": null,
                            "FloorDecorText": "",
                            "ZoneID": null,
                            "ZoneName": "",
                            "OutletDecorText": "",
                            "ZoneDecorText": ""
                },
                "Member": {
                    "OwnMember": 0,
                            "ActiveMember": 0,
                            "ActiveMemberText": "0",
                            "OwnMemberText": "0"
                }
            }*/
                try {
                    /*txtMain1.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("FloorName"));
                    txtMain2.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("OutletName"));
                    txtMain3.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("ZoneName"));
                    txtSherBet.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("FloorDecorText"));
                    txtSHERBET.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("OutletDecorText"));
                    txtMiniBar.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("ZoneDecorText"));
                    txtBillOpen.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Bill").getString("OpenedBillText"));
                    txtBillClose.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Bill").getString("ClosedBillText"));
                    txtOnWorkNow.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OnWorkingText"));
                    txtOnFloor.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OnFloorText"));
                    txtTakeCareRe.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OwnResourceText"));
                    txtRunning.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OnRunningText"));
                    txtOnTakeCare.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Member").getString("OwnMemberText"));
                    txtOnWorkMem.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Member").getString("ActiveMemberText"));*/ // old code by mos
                    txtFloorName.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("FloorName"));
                    txtFloorDecor.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("FloorDecorText"));
                    txtOutletName.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("OutletName"));
                    txtOutletDecor.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("OutletDecorText"));
                    txtZoneName.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("ZoneName"));
                    txtZoneDecor.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("FloorPlan").getString("ZoneDecorText"));

                    txtBillOpen.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Bill").getString("OpenedBillText"));
                    txtBillClose.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Bill").getString("ClosedBillText"));

                    txtResrcOnWorkNow.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OnWorkingText"));
                    txtResrcOnFloor.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OnFloorText"));
                    txtResrcTakeCare.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OwnResourceText"));
                    txtResrcRunning.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Resource").getString("OnRunningText"));

                    txtMemberTakeCare.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Member").getString("OwnMemberText"));
                    txtMemberOnWork.setText(jResgetWorkspaceInfo.getJSONObject("Data").getJSONObject("Member").getString("ActiveMemberText"));
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
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        System.out.println("click");
        switch (itemId) {
            // Android home
            case android.R.id.home:
                Activity_Navigation.drawerLayout.openDrawer(GravityCompat.START);
                System.out.println("click");
                return true;
            // manage other entries if you have it ...
            case R.id.action_search:
                activityPager.setCurrentItem(1); // 0= Workspace 1= FragmentSearchMenu
                System.out.println("switch");
        }
        return true;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        //MenuItemCompat.setActionView(item, searchView);
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Do search code here
                return true;
            }
        });*/ //query and search
        /*searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ย้อนกลับไปหน้า fragment Search
                activityPager.setCurrentItem(1);
            }
        });*/
    }
}