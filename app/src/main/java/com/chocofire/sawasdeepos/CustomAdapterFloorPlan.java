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
 * Created by ChoCoFire on 9/8/2017.
 */

public class CustomAdapterFloorPlan extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> web = new ArrayList<String>();
    private ArrayList<Bitmap> Imageid =new ArrayList<Bitmap>();

    public CustomAdapterFloorPlan(Context c,ArrayList<String> web,ArrayList<Bitmap> Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        try {
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.gridview_adapter, null);
            } else {
                v = convertView;
            }
            TextView tv = (TextView) v.findViewById(R.id.resource_textView_Gridview);
            tv.setText(web.get(position));
            ImageView iv = (ImageView) v.findViewById(R.id.imageView_gridview);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            iv.setImageBitmap(Imageid.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }
}