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
 * Created by ChoCoFire on 9/28/2017.
 */

public class ListViewAdapterPageResource extends BaseAdapter {
    Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<String> strName1 = new ArrayList<String>();
    private ArrayList<String> strName2 = new ArrayList<String>();
    private ArrayList<String> strName3 = new ArrayList<String>();
    private ArrayList<String> strName4 = new ArrayList<String>();
    private ArrayList<Bitmap> resId = new ArrayList<Bitmap>();

    public ListViewAdapterPageResource(Context context, ArrayList<String> strName, ArrayList<String> strName1
            , ArrayList<String> strName2, ArrayList<String> strName3
            , ArrayList<String> strName4, ArrayList<Bitmap> resId ){
        this.mContext= context;
        this.strName = strName;
        this.strName1 = strName1;
        this.strName2 = strName2;
        this.strName3 = strName3;
        this.strName4 = strName4;
        this.resId = resId;
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
            view = mInflater.inflate(R.layout.listview_custom_pageresource, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textView_resourceName);
        textView.setText(strName.get(position));

        TextView textView1 = (TextView)view.findViewById(R.id.textView_resourceCode);
        textView1.setText(strName1.get(position));

        TextView textView2 = (TextView)view.findViewById(R.id.textView_StartTime);
        textView2.setText(strName2.get(position));

        TextView textView3 = (TextView)view.findViewById(R.id.textView_countalbe);
        textView3.setText(strName3.get(position));

        TextView textView4 = (TextView)view.findViewById(R.id.textView_EndTime);
        textView4.setText(strName4.get(position));

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView_resourcePage);
        imageView.setImageBitmap(resId.get(position));

        return view;
    }
}