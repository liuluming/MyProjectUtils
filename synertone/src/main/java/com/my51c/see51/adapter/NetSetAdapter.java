package com.my51c.see51.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my51c.see51.app.domian.ResWifi;
import com.synertone.netAssistant.R;

import java.util.List;

public class NetSetAdapter extends BaseAdapter {
    private List<ResWifi> mResWifis;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public NetSetAdapter(List<ResWifi> mResWifis, Context mContext) {
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
        NetSetViewHolder vh;
        if (convertView == null) {
            vh = new NetSetViewHolder();
            convertView = layoutInflater.inflate(R.layout.netset_item_lv,
                    null);
            vh.tvName = (TextView) convertView.findViewById(R.id.ns_wifiname);
//			vh.tvContent = (TextView) convertView
//					.findViewById(R.id.reswifi_content);
            vh.tvImage = (ImageView) convertView.findViewById(R.id.ns_wifiimage);
            vh.tvEncryption = (TextView) convertView.findViewById(R.id.ns_wifijiamiway);
            convertView.setTag(vh);
        } else {
            vh = (NetSetViewHolder) convertView.getTag();
        }
        ResWifi resWifi = mResWifis.get(position);

        vh.tvName.setText(resWifi.getName());
        vh.tvEncryption.setText(resWifi.getEncryption());
        String strength = resWifi.getStrength().toString();
        //Bitmap bitmap=null;
        //bitmap=BitmapDrawable.createFromResourceStream(res, value, is, srcName)
        //这里更换wifi信号强度图片
        if (strength.equals("1") || strength.equals("2")) {

            vh.tvImage.setBackgroundResource(R.drawable.netset_wifi1);
        }
        if (strength.equals("3")) {
            vh.tvImage.setBackgroundResource(R.drawable.netset_wifi2);
        }
        if (strength.equals("4")) {
            vh.tvImage.setBackgroundResource(R.drawable.netset_wifi3);
        }
        if (strength.equals("5")) {
            vh.tvImage.setBackgroundResource(R.drawable.netset_wifi4);
        }

        return convertView;
    }

    class NetSetViewHolder {
        TextView tvName, tvEncryption, tvStrength, tvContent;
        ImageView tvImage;
    }

}
