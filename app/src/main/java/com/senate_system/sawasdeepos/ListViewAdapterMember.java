package com.senate_system.sawasdeepos;

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
 * Created by ChoCoFire on 9/5/2017.
 */

public class ListViewAdapterMember extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<Bitmap> resId =new ArrayList<Bitmap>();

    ListViewAdapterMember(Context context, ArrayList<String> strName, ArrayList<Bitmap> resId) {
        this.mContext= context;
        this.strName = strName;
        this.resId = resId;
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
            view = mInflater.inflate(R.layout.listview_custom_member, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textView_member);
        textView.setText(strName.get(position));

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView_member);
        imageView.setImageBitmap(resId.get(position));

        return view;
    }
}