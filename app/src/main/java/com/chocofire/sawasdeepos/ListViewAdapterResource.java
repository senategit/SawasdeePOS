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
 * Created by ChoCoFire on 9/5/2017.
 */

public class ListViewAdapterResource extends BaseAdapter {
    Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<String> strName1 = new ArrayList<String>();
    Bitmap[] resId;
    Bitmap[] resId2;
    Bitmap[] resId3;

    public ListViewAdapterResource(Context context, ArrayList<String> strName, ArrayList<String> strName1
            , Bitmap[] resId, Bitmap[] resId2, Bitmap[] resId3) {
        this.mContext = context;
        this.strName = strName;
        this.strName1 = strName1;
        this.resId = resId;
        this.resId2 = resId2;
        this.resId3 = resId3;
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
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = mInflater.inflate(R.layout.listview_custom_resource, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.textView_member);
        textView.setText(strName.get(position));

        TextView textView1 = (TextView) view.findViewById(R.id.textView_member_detail);
        textView1.setText(strName1.get(position));

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_member);
        imageView.setImageBitmap(resId[position]);

        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView_glass);
        imageView2.setImageBitmap(resId2[position]);

        ImageView imageView3 = (ImageView) view.findViewById(R.id.imageView_women);
        imageView3.setImageBitmap(resId3[position]);

        return view;
    }
}