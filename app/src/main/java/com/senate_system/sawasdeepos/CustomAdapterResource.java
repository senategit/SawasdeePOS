package com.senate_system.sawasdeepos;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by ChoCoFire on 9/22/2017.
 */

public class CustomAdapterResource extends BaseAdapter {
    private Context mContext;
    //private ArrayList<String> web = new ArrayList<String>();
    private ArrayList<Bitmap> Imageid =new ArrayList<Bitmap>();

    public CustomAdapterResource(Context c ,ArrayList<Bitmap> Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        //this.web = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Imageid.size();
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
        View v;
        if(convertView==null)
        {
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.gridview_resource, null);
        }else{
            v = convertView;
        }
        /*TextView tv = (TextView)v.findViewById(R.id.resource_textView_Gridview);
        tv.setText(web.get(position));*/
        //LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.background_Gridview);
        ImageView iv = (ImageView)v.findViewById(R.id.imageView_gridview2);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setImageBitmap(Imageid.get(position));
        /*BitmapDrawable ob = new BitmapDrawable(v.getResources(), Imageid.get(position));
        linearLayout.setScaleY();
        linearLayout.setBackground(ob);*/

        return v;
    }
}