package com.my51c.see51.guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.synertone.netAssistant.R;

import java.util.ArrayList;

public class wifiListAdapter extends BaseAdapter {

    public static int choiceItem;
    public static boolean isSingleChoice = false;
    private ArrayList<String> ssidList;
    private Context context;
    private int id;

    public wifiListAdapter(ArrayList<String> ssidList, Context context, int id) {
        this.ssidList = ssidList;
        this.context = context;
        this.id = id;
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.wifilist_dialog_item, null);
            holder.ssidTx = (TextView) convertView.findViewById(R.id.ssidTx);
            holder.itemImg = (ImageView) convertView.findViewById(R.id.itemImg);
            holder.choiceImg = convertView.findViewById(R.id.choicedImg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.choiceImg.setVisibility(View.INVISIBLE);
        }

        holder.ssidTx.setText(ssidList.get(position) + "");
        holder.itemImg.setBackgroundResource(id);
        if (isSingleChoice) {
            if (position == choiceItem) {
                holder.choiceImg.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView ssidTx;
        ImageView itemImg;
        View choiceImg;
    }

}
