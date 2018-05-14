package com.my51c.see51.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.my51c.see51.app.domian.ResWifi;
import com.synertone.netAssistant.R;

import java.util.List;


public class ResWifiAdapter extends BaseAdapter {
    private List<ResWifi> mResWifis;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public ResWifiAdapter(List<ResWifi> mResWifis, Context mContext) {
        super();
        this.mResWifis = mResWifis;
        this.mContext = mContext;
        this.layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mResWifis.size();
    }

    @Override
    public Object getItem(int position) {
        return mResWifis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.reswifi_list_item,
                    null);
            vh.tvName = (TextView) convertView.findViewById(R.id.reswifi_name);
            vh.tvContent = (TextView) convertView
                    .findViewById(R.id.reswifi_content);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ResWifi resWifi = mResWifis.get(position);

        vh.tvName.setText(resWifi.getName());
        return convertView;
    }

    class ViewHolder {
        TextView tvName, tvEncryption, tvStrength, tvContent;
    }

}
