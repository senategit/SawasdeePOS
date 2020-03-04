package com.senate_system.sawasdeepos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ChoCoFire on 9/20/2017.
 */

public class ListViewAdapterFragmentTakeOrder extends BaseAdapter {
    Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<String> strName2 = new ArrayList<String>();
    private ArrayList<String> strName3 = new ArrayList<String>();
    private ArrayList<String> strName4 = new ArrayList<String>();

    public ListViewAdapterFragmentTakeOrder(Context context, ArrayList<String> strName , ArrayList<String> strName2
            , ArrayList<String> strName3 , ArrayList<String> strName4) {
        this.mContext = context;
        this.strName = strName;
        this.strName2 = strName2;
        this.strName3 = strName3;
        this.strName4 = strName4;
    }

    public int getCount() {
        return strName.size();
    }

    public Object getItem(int position) {
        return strName.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        try {
            LayoutInflater mInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (view == null)
                view = mInflater.inflate(R.layout.listview_custom_fragment_takeorder, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.textview_DisplayName);
            if ((strName != null) && (strName.size() > 0)) {
                textView.setText(strName.get(position));
            } else {
                textView.setText("");
            }

            TextView textView2 = (TextView) view.findViewById(R.id.textView_PriceShow);
            //DecimalFormat formatter = new DecimalFormat("#,###,###.##");
            //String yourFormattedString = formatter.format(Double.valueOf(strName2.get(position)));
            //textView2.setText(yourFormattedString);
            if ((strName2 != null) && (strName2.size() > 0)) {
                String value = strName2.get(position);
                if ((value != null) && (value.trim().length() > 0)) {
                    DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                    String yourFormattedString = formatter.format(Double.valueOf(value));
                    textView2.setText(yourFormattedString);
                } else {
                    textView2.setText("");
                }
            } else {
                textView2.setText("");
            }

            TextView textView3 = (TextView) view.findViewById(R.id.textview_DescQTY);
            if ((strName3 != null) && (strName3.size() > 0)) {
                textView3.setText(strName3.get(position));
            } else {
                textView3.setText("");
            }
            /*if(strName3.get(position).equals("null")){//qty
                textView3.setVisibility(View.INVISIBLE);
            }else{
                textView3.setVisibility(View.VISIBLE);
                textView3.setText(strName3.get(position));
            }*/

            TextView textView4 = (TextView) view.findViewById(R.id.textView_status);
            if ((strName4 != null) && (strName4.size() > 0)) {
                textView4.setText(strName4.get(position));
            } else {
                textView4.setText("");
            }
            /*if(strName4.get(position).equals("null")){ //gprint kprint
                textView4.setVisibility(View.INVISIBLE);
            }else{
                textView4.setVisibility(View.VISIBLE);
                textView4.setText(strName4.get(position));
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}