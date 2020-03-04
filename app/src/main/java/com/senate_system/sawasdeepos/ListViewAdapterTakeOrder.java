package com.senate_system.sawasdeepos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChoCoFire on 9/18/2017.
 */

public class ListViewAdapterTakeOrder extends BaseAdapter {
    Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<String> strName1 = new ArrayList<String>();
    private ArrayList<String> strName2 = new ArrayList<String>();

    public ListViewAdapterTakeOrder(Context context, ArrayList<String> strName, ArrayList<String> strName1
            , ArrayList<String> strName2 ) {
        this.mContext= context;
        this.strName = strName;
        this.strName1 = strName1;
        this.strName2 = strName2;
    }

    public int getCount() {
        return strName.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.listview_custom_takeorder, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textview_GuestCheck);
        textView.setText(strName.get(position));

        TextView textView1 = (TextView)view.findViewById(R.id.textView_Cover);
        textView1.setText(strName1.get(position));

        TextView textView2 = (TextView)view.findViewById(R.id.textView_Balance_takeorder);
        textView2.setText(strName2.get(position));

        return view;
    }
}