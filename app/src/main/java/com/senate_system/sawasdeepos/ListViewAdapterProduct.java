package com.senate_system.sawasdeepos;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ChoCoFire on 9/5/2017.
 */

public class ListViewAdapterProduct extends BaseAdapter {
    Context mContext;
    private ArrayList<String> strName = new ArrayList<String>();
    private ArrayList<String> strName1 = new ArrayList<String>();
    private ArrayList<Bitmap> resId = new ArrayList<Bitmap>();

    public ListViewAdapterProduct(Context context, ArrayList<String> strName, ArrayList<String> strName1
            , ArrayList<Bitmap> resId ) {
        this.mContext= context;
        this.strName = strName;
        this.strName1 = strName1;
        this.resId = resId;
    }

    public int getCount() {
        return strName.size();
    }

    public Object getItem(int position) {
        return resId.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.listview_custom_product, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textView_orderList);
        textView.setText(strName.get(position));

        TextView textView1 = (TextView)view.findViewById(R.id.textView_payment);
        //DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        //String yourFormattedString = formatter.format(Double.valueOf(strName1.get(position)));
        //textView1.setText(yourFormattedString);

        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        String value = strName1.get(position);
        if((value != null) && (value.trim().length() > 0)) {
            String yourFormattedString = formatter.format(Double.valueOf(value));
            textView1.setText(yourFormattedString);
        } else {
            textView1.setText("");
        }

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView_order);
        imageView.setImageBitmap(resId.get(position));

        return view;
    }
}