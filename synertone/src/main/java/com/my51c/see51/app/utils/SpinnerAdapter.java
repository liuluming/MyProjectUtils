package com.my51c.see51.app.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    String[] items = new String[]{};
    List<String> ListItems = new ArrayList<>();

    public SpinnerAdapter(final Context context,
                          final int textViewResourceId, final List<String> ListObjects) {
        super(context, textViewResourceId, ListObjects);
        this.ListItems = ListObjects;
        this.context = context;
    }

    public SpinnerAdapter(final Context context,
                          final int textViewResourceId, final String[] objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }


    @SuppressLint("RtlHardcoded")
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        if(items.length>0){
            tv.setText(items[position]);
        }else if(ListItems.size()>0){
            tv.setText(ListItems.get(position));
        }
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(15f);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        if(items.length>0){
            tv.setText(items[position]);
        }else if(ListItems.size()>0){
            tv.setText(ListItems.get(position));
        }
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(13f);
        return convertView;
    }

}