package com.my51c.see51.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.synertone.netAssistant.R;

import java.util.ArrayList;

public class SmartIdAdapter extends BaseAdapter {

    ArrayList<String> ssidList;
    Context context;

    public SmartIdAdapter(ArrayList<String> ssidList, Context context) {
        this.ssidList = ssidList;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ssidList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.corner_list_item, null);
            holder.ssidTx = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ssidTx.setText(ssidList.get(position));
        return convertView;
    }

    public class ViewHolder {
        public TextView ssidTx;
    }

}