package com.chocofire.sawasdeepos;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChoCoFire on 9/25/2017.
 */

public class ListViewAdapterOrderCommand extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();

    ListViewAdapterOrderCommand(Context context, ArrayList<String> strName) {
        this.mContext= context;
        this.strName = strName;
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
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.listview_custom_fragment_hotkey, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textView_Hotkey);
        textView.setText(strName.get(position));

        return view;
    }
}