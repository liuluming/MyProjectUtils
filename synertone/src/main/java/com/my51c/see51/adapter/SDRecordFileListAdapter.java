package com.my51c.see51.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my51c.see51.data.SDFileInfo;
import com.my51c.see51.widget.NumberProgressBar;
import com.synertone.netAssistant.R;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class SDRecordFileListAdapter extends BaseAdapter {
    private ArrayList<SDFileInfo> sdlist;
    private Context context;
    private OnClickListener mOnClickListener;

    public SDRecordFileListAdapter(Context context, ArrayList<SDFileInfo> list) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.sdlist = list;
    }

    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    public void setFileList(ArrayList<SDFileInfo> list) {
        this.sdlist = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sdlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return sdlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        try {
            ViewHolder holder = null;
//		if(convertView == null)
//		{
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
//		}
//		else
//		{
//			holder = (ViewHolder) convertView.getTag();
//		}
            SDFileInfo tmp = null;
            try {
                synchronized (sdlist) {
                    tmp = sdlist.get(position);
                }
            } catch (Exception e) {
                // TODO: handle exception
                return convertView;
            }


            if (tmp.getSzFileName().length() == 10) {
                Log.i("SDRecorderFileListAdapter", "--�ļ���item");
                holder.tvfilename.setText(tmp.getSzFileName().toString());
                holder.ivdownload.setImageResource(R.drawable.arrow);

                holder.tvfiletime.setVisibility(View.GONE);
                holder.tvfilesize.setVisibility(View.GONE);
                holder.numbar.setVisibility(View.GONE);

            } else {

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

                SDFileInfo sditem = sdlist.get(position);
                holder.tvfilename.setText(sditem.getSzFileName());
                long nFileSize = sditem.getnFileSize() / 1024;
                holder.tvfilesize.setText("" + nFileSize + "kb");

                if (sditem.getFileTag().equals("jpg")) {
                    holder.ifileicon.setImageResource(R.drawable.ft_jpg);
                }

                if (sditem.getFileTag().equals("mp4")) {
                    holder.ifileicon.setImageResource(R.drawable.ft_mp4);
                }


                String strfile = sditem.getSzFileName();
                String date = strfile.substring(0, 8);
                date += " ";
                date += strfile.substring(8, 10);
                date += ":";
                date += strfile.substring(10, 12);
                date += ":";
                date += strfile.substring(12, 14);
                holder.tvfiletime.setText(date);

                if (sditem.checkitcomplete()) {
                    showbuttonType(holder, 3);
                } else {
                    showbuttonType(holder, 1);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // TODO Auto-generated catch block
            Log.i("SDRecordFileListAdapter", "--catch Exception:" + e);
            e.printStackTrace();
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
