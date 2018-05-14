package com.my51c.see51.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my51c.see51.widget.MyComment;
import com.synertone.netAssistant.R;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    private ArrayList<MyComment> commentList;
    private Context context;

    public CommentAdapter(Context context, ArrayList<MyComment> commentList) {
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
            holder = new ViewHolder();
            holder.userHead = (ImageView) convertView.findViewById(R.id.userHead);
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.positionTx = (TextView) convertView.findViewById(R.id.positionTx);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//		holder.userHead.
        holder.userName.setText(commentList.get(position).getUsername());
        holder.publish_time.setText(commentList.get(position).getPublish_time());
        holder.content.setText(commentList.get(position).getContent());
        holder.positionTx.setText(position + 1 + "");
        return convertView;
    }
}

class ViewHolder {
    ImageView userHead;
    TextView userName;
    TextView publish_time;
    TextView content;
    TextView positionTx;
}


