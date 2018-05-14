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

import com.synertone.netAssistant.R;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class SDRecordFolderListAdapter extends BaseAdapter {
    private ArrayList<String> folderList;
    private Context context;
    private OnClickListener mOnClickListener;

    public SDRecordFolderListAdapter(Context context, ArrayList<String> list) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.folderList = list;
    }

    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    public void setFileList(ArrayList<String> list) {
        this.folderList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return folderList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return folderList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.sdrecord_folder_item, null);
            holder.folderName = (TextView) convertView.findViewById(R.id.sd_folderName);
            holder.fileCount = (ImageView) convertView.findViewById(R.id.sd_edit);
            holder.fileCount.setVisibility(View.GONE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.folderName.setText(folderList.get(position));

        return convertView;
    }

    public class ViewHolder {
        public TextView folderName;
        //		public ImageView folderImg;
        public ImageView fileCount;
    }

}
