package com.senate_system.sawasdeepos;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class ListViewAdapterBill extends BaseAdapter {
    Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<String> strName1 = new ArrayList<String>();
    private ArrayList<String> strName2 = new ArrayList<String>();
    Bitmap[] resId;

    public ListViewAdapterBill(Context context, ArrayList<String> strName, ArrayList<String> strName1
            , ArrayList<String> strName2) {
        this.mContext= context;
        this.strName = strName;
        this.strName1 = strName1;
        this.strName2 = strName2;
    }

    public int getCount() {
        return strName.size();
    }

    public Object getItem(int position) {
        return resId;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.listview_custom_bill, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textview_Decor);
        textView.setText(strName.get(position));

        TextView textView1 = (TextView)view.findViewById(R.id.textView_Guest_Chk);
        textView1.setText(strName1.get(position));

        TextView textView2 = (TextView)view.findViewById(R.id.textView_Balance);
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        String value = strName2.get(position);
        if((value != null) && (value.trim().length() > 0)) {
            String yourFormattedString = formatter.format(Double.valueOf(value));
            textView2.setText(yourFormattedString);
        } else {
            textView2.setText("");
        }

        return view;
    }
}