package com.my51c.see51.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my51c.see51.data.CloudFileInfo;
import com.my51c.see51.widget.NumberProgressBar;
import com.synertone.netAssistant.R;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class CloudRecordFileListAdapter extends BaseAdapter {

    private ArrayList<CloudFileInfo> cloudfilelist;
    private Context context;
    private OnClickListener mOnClickListener;

    public CloudRecordFileListAdapter(Context context, ArrayList<CloudFileInfo> list) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.cloudfilelist = list;
    }

    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }


    public void setFileList(ArrayList<CloudFileInfo> list) {
        this.cloudfilelist = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return cloudfilelist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return cloudfilelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.sdrecord_download_item, null);
            holder.tvfiletime = (TextView) convertView.findViewById(R.id.tv_sdrecord_time);
            holder.ifileicon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
            holder.tvfilename = (TextView) convertView.findViewById(R.id.tv_sdrecord_filename);
            holder.tvfilesize = (TextView) convertView.findViewById(R.id.tv_sdrecord_filesize);
            holder.ivdownload = (ImageView) convertView.findViewById(R.id.iv_download);
            holder.ivcanceldownload = (ImageView) convertView.findViewById(R.id.iv_cancel);
            holder.ivplay = (ImageView) convertView.findViewById(R.id.iv_play);
            holder.numbar = (NumberProgressBar) convertView.findViewById(R.id.progress_download);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CloudFileInfo tmp = cloudfilelist.get(position);

        if (tmp.isbDown()) {
            holder.numbar.setVisibility(View.VISIBLE);
        } else {
            holder.numbar.setVisibility(View.GONE);
        }
        holder.ivdownload.setOnClickListener(mOnClickListener);
        holder.ivcanceldownload.setOnClickListener(mOnClickListener);
        holder.ivplay.setOnClickListener(mOnClickListener);

        holder.ivdownload.setTag(holder);
        holder.ivcanceldownload.setTag(holder);
        holder.ivplay.setTag(holder);
        holder.tvfilename.setTag(tmp);


        CloudFileInfo sditem = cloudfilelist.get(position);
        holder.tvfilename.setText(sditem.getRealFileName());
        long nFileSize = sditem.getnFileSize() / 1024;
        holder.tvfilesize.setText("" + nFileSize + "kb");

        String strfile = sditem.getSzFileName();
        String date = strfile.substring(0, 8);
        date += " ";
        date += strfile.substring(8, 10);
        date += ":";
        date += strfile.substring(10, 12);
        date += ":";
        date += strfile.substring(12, 14);
        holder.tvfiletime.setText(date);

        switch (sditem.getlType()) {

            case 3: {
                holder.ifileicon.setImageResource(R.drawable.ft_jpg);
            }
            break;
            case 4: {
                holder.ifileicon.setImageResource(R.drawable.ft_mp4);
            }
            break;

            default:
                break;
        }

        if (sditem.checkitcomplete()) {
            showbuttonType(holder, 3);
        } else {
            showbuttonType(holder, 1);
        }

        return convertView;
    }

    public void showbuttonType(ViewHolder holder, int nItem) {
        if (holder == null) {
            return;
        }

        switch (nItem) {
            case 1: {
                holder.ivdownload.setVisibility(View.VISIBLE);
                holder.ivcanceldownload.setVisibility(View.GONE);
                holder.ivplay.setVisibility(View.GONE);
            }
            break;
            case 2: {
                holder.ivdownload.setVisibility(View.GONE);
                holder.ivcanceldownload.setVisibility(View.VISIBLE);
                holder.ivplay.setVisibility(View.GONE);
            }
            break;
            case 3: {
                holder.ivdownload.setVisibility(View.GONE);
                holder.ivcanceldownload.setVisibility(View.GONE);
                holder.ivplay.setVisibility(View.VISIBLE);
            }
            break;
            default:
                break;

        }
    }

    public class ViewHolder {
        public TextView tvfiletime;
        public ImageView ifileicon;
        public TextView tvfilename;
        public TextView tvfilesize;
        public ImageView ivdownload;
        public ImageView ivcanceldownload;
        public ImageView ivplay;
        public NumberProgressBar numbar;
    }

}
