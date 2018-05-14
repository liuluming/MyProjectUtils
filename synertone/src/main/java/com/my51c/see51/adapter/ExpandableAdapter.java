package com.my51c.see51.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.List;

public class ExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList = null;
    private ArrayList<List<String>> childList = null;
    private Context context;

    public ExpandableAdapter(Context context, ArrayList<String> groupList, ArrayList<List<String>> childList) {
        this.context = context;
        this.groupList = groupList;
        this.childList = childList;
    }

    //-----------------Child----------------//
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return childList.get(groupPosition).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        // TODO Auto-generated method stub

        if (groupPosition == 0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.local_video_item, null);
            TextView name = (TextView) convertView.findViewById(R.id.local_filename);
            name.setText(childList.get(groupPosition).get(childPosition).toString());
            System.out.println("0");
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.local_folder_item, null);
            TextView name = (TextView) convertView.findViewById(R.id.local_folder_filename);
            name.setText(childList.get(groupPosition).get(childPosition).toString());
            System.out.println("1");
        }

        return convertView;
    }

    //-----------------Group----------------//
    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = LayoutInflater.from(context).inflate(R.layout.sdrecord_folder_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.sd_folderName);
        ImageView img = (ImageView) convertView.findViewById(R.id.sd_previewImg);
        ImageView editImg = (ImageView) convertView.findViewById(R.id.sd_edit);

        name.setText(groupList.get(groupPosition).toString());
        if (isExpanded) {
            img.setBackgroundResource(R.drawable.arrow_close);
        } else {
            img.setBackgroundResource(R.drawable.arrow_open);
        }
        editImg.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                System.out.println("-----------------");
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

}