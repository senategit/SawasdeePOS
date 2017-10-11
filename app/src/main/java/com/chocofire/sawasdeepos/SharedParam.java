package com.chocofire.sawasdeepos;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ChoCoFire on 7/27/2017.
 */

public class SharedParam extends Application {

    private String URL = "http://27.254.138.120/swps/1/"; //"http://192.168.0.211/swps/1/";
    private String Auth_key = "6F50BE86-A8B5-4155-AC16-ABDBDAC8692F";
    private String DataFormLogin;
    private String OrderInfo;
    private String OutLetID ;
    private String ObjSelectItem;
    private String ObjSelectHotkey;
    private String ObjSelectResource;
    private String ObjMemberInfo ;
    private String PackageNo;
    private String TabSelected = "-1";
    private String SEQDeleteItem ;
    private String LastAccess ;
    private String OrderID;
    private String CheckSearchPage;
    private String UserName;
    private JSONObject jObjWorkDateBean ;
    private String ResourceId;
    private String ResourceWay;
    private String ChangeWay = "-1";
    private String FloorID;
    private String ZoneID;
    public  boolean FreeDrink;
    //=========================================p'wut==============================================//
    private JSONObject jObjUserInfo;
    private JSONObject jObjPermInfo;
    private JSONObject jObjFloorInfo;
    private JSONObject jObjOutletInfo;
    private JSONObject jObjZoneInfo;
    private JSONObject jObjDecorInfo;
    private JSONObject jObjWorkDateInfo;
    private JSONObject jObjWorkShiftInfo;
    private Boolean IsMemberInOrder = false;
    public String HWURL;
    public String HWPORT;
    private Boolean NeedReloadConfig = true;
    //=========================================p'wut==============================================//

    public String getURL() {return  URL;}
    public String getAuth_key() {return  Auth_key;}
    public String getDataFormLogin() {return DataFormLogin;}
    public String getOrderInfo() {return OrderInfo;}
    public String getOutLetID(){return OutLetID;}
    public String getObjSelectItem(){return ObjSelectItem;}
    public String getObjSelectHotkey(){return ObjSelectHotkey;}
    public String getObjSelectResource(){return ObjSelectResource;}
    public String getObjMemberInfo(){return ObjMemberInfo;}
    public String getPackageNo(){return  PackageNo;}
    public String getTabSelected(){return TabSelected;}
    public String getSEQDeleteItem() {return SEQDeleteItem;}
    public String getLastAccess() {return LastAccess;}
    public String getOrderID(){return OrderID;}
    public String getCheckSearchPage(){return CheckSearchPage;}
    public String getUserName(){return UserName;}
    public JSONObject getjObjWorkDateBean(){return jObjWorkDateBean;}
    public String getResourceId(){return  ResourceId;}
    public String getResourceWay(){return  ResourceWay;}
    public String getChangeWay(){return  ChangeWay;}
    //public String getFloorID(){return FloorID;}
    //public String getZoneID(){return ZoneID;}
    public boolean getFreeDrink(){return FreeDrink;}

    public String getHWURL(){
        return HWURL;
    }
    public String getHWPORT(){
        return HWPORT;
    }
    //=========================================p'wut==============================================//
    public JSONObject getUserInfo(){return jObjUserInfo;}
    public JSONObject getPermInfo(){return jObjPermInfo;}
    public JSONObject getFloorInfo(){return jObjFloorInfo;}
    public JSONObject getOutletInfo(){return jObjOutletInfo;}
    public JSONObject getZoneInfo(){return jObjZoneInfo;}
    public JSONObject getDecorInfo(){return jObjDecorInfo;}
    public JSONObject getWorkDateInfo(){return jObjWorkDateInfo;}
    public JSONObject getWorkShiftInfo(){return jObjWorkShiftInfo;}
    //public JSONObject getOrderInfo(){return jObjOrderInfo;}
    public Boolean getIsMemberInOrder() {return IsMemberInOrder;}
    public Boolean getNeedReloadConfig() {return NeedReloadConfig;}
    //=========================================p'wut==============================================//

    public String getOrderMemberID() {
        String memberid = null;
        try {
            if((this.OrderInfo != null) && (this.OrderInfo.trim().length() > 0)) {
                JSONObject jObjOrderInfo = new JSONObject(this.OrderInfo);
                JSONObject jObjOrder = jObjOrderInfo.getJSONObject("Data").getJSONObject("Order");
                if ((jObjOrder != null) && (jObjOrder.has("MemberID"))) {
                    memberid = jObjOrder.getString("MemberID");
                }
            }
        } catch (JSONException e) {
            memberid = null;
            String errmsg = "error: " + e.getMessage() + "\n" + "stack trace: " + e.getStackTrace().toString();
            Toast.makeText(this.getApplicationContext(), errmsg, Toast.LENGTH_SHORT).show();
        }
        return memberid;
    }


    public void setURL(String URL){this.URL=URL;}
    public void setAuth_key(String Auth_key){this.Auth_key=Auth_key;}
    public void setDataFormLogin(String DataFormLogin){this.DataFormLogin=DataFormLogin;}
    public void setOrderInfo(String OrderInfo){this.OrderInfo = OrderInfo;}
    public void setOutLetID(String OutletID){this.OutLetID=OutletID;}
    public void setObjSelectItem(String ObjSelectItem){this.ObjSelectItem=ObjSelectItem;}
    public void setObjSelectHotkey(String ObjSelectHotkey){this.ObjSelectHotkey=ObjSelectHotkey;}
    public void setObjSelectResource(String ObjSelectResource){this.ObjSelectResource=ObjSelectResource;}
    public void setObjMemberInfo(String ObjMemberInfo){this.ObjMemberInfo=ObjMemberInfo;}
    public void setPackageNo(String PackageNo){this.PackageNo=PackageNo;}
    public void setTabSelected(String TabSelected){this.TabSelected=TabSelected;}
    public void setSEQDeleteItem(String SEQDeleteItem) {this.SEQDeleteItem=SEQDeleteItem;}
    public void setLastAccess(String LastAccess) {this.LastAccess=LastAccess;}
    public void setOrderID(String OrderID){this.OrderID=OrderID;}
    public void setCheckSearchPage(String CheckSearchPage){this.CheckSearchPage=CheckSearchPage;}
    public void setUserName(String UserName){this.UserName=UserName;}
    public void setjObjWorkDateBean(JSONObject jObjWorkDateBean){this.jObjWorkDateBean=jObjWorkDateBean;}
    public void setResourceId(String ResourceId){this.ResourceId=ResourceId;}
    public void setResourceWay(String ResourceWay){this.ResourceWay=ResourceWay;}
    public void setChangeWay(String ChangeWay){this.ChangeWay=ChangeWay;}
    public void setFreeDrink(boolean FreeDrink){this.FreeDrink=FreeDrink;}
    public void setHWURL(String HWURL){this.HWURL=HWURL;}
    public void setHWPORT(String HWPORT){this.HWPORT=HWPORT;}
    //=========================================p'wut==============================================//
    public void setUserInfo(JSONObject jObjUserInfo){this.jObjUserInfo=jObjUserInfo;}
    public void setPermInfo(JSONObject jObjPermInfo){this.jObjPermInfo=jObjPermInfo;}
    public void setFloorInfo(JSONObject jObjFloorInfo){this.jObjFloorInfo=jObjFloorInfo;}
    public void setOutletInfo(JSONObject jObjOutletInfo){this.jObjOutletInfo=jObjOutletInfo;}
    public void setZoneInfo(JSONObject jObjZoneInfo){this.jObjZoneInfo=jObjZoneInfo;}
    public void setDecorInfo(JSONObject jObjDecorInfo){this.jObjDecorInfo=jObjDecorInfo;}
    public void setWorkDateInfo(JSONObject jObjWorkDateInfo){this.jObjWorkDateInfo=jObjWorkDateInfo;}
    public void setWorkShiftInfo(JSONObject jObjWorkShiftInfo){this.jObjWorkShiftInfo=jObjWorkShiftInfo;}
    public void setIsMemberInOrder(Boolean IsMemberInOrder) {this.IsMemberInOrder = IsMemberInOrder;}
    public void setNeedReloadConfig(Boolean NeedReloadConfig) {this.NeedReloadConfig = NeedReloadConfig;}
    //=========================================p'wut==============================================//

}