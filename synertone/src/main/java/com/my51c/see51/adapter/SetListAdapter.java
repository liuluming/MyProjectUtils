package com.my51c.see51.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.my51c.see51.ui.MainActivity;
import com.my51c.see51.widget.MySwitch;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("InflateParams")
public class SetListAdapter extends BaseAdapter {
    public final static int ALRAMHISTORY = 0;
    public final static int SWITCHACCOUNT = 1;
    public final static int DEFUALT = 3;
    public final static int EDITPASSWD = 4;
    public final static int ABOUT = 5;
    private final String TAG = "SetListAdapter";
    private LayoutInflater mInflater;
    private List<Map<String, Object>> mData;
    private Context mContext;
    private OnCheckedChangeListener mCheckedChangeListener;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public SetListAdapter(Context context, OnCheckedChangeListener mCheckedChangeListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mCheckedChangeListener = mCheckedChangeListener;
        this.mContext = context;
        setListItem();
        settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        editor = settings.edit();
        // TODO Test code here
        ////Log.d(TAG, "constructor");
    }

    public void release() {
        this.settings = null;
        this.editor = null;
        this.mContext = null;
        this.mCheckedChangeListener = null;
    }

    protected void setListItem() {
        //Log.d(TAG, "setListItem");
        ArrayList<String> setTitleList = new ArrayList<String>();
        setTitleList.add(mContext.getResources().getString(R.string.alarmNotification));
        setTitleList.add(mContext.getResources().getString(R.string.history));
        //setTitleList.add(mContext.getResources().getString(R.string.switchAccount));
        setTitleList.add(mContext.getResources().getString(R.string.changepassword));
        setTitleList.add(mContext.getResources().getString(R.string.about));
        if (mData == null) {
            mData = new ArrayList<Map<String, Object>>();
        }
        mData.clear();
        for (int i = 0; i < setTitleList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item", setTitleList.get(i));
            mData.add(map);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ////Log.d(TAG,"getView interge");
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.corner_list_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.mySwitch = (MySwitch) convertView.findViewById(R.id.mySwitch);
            holder.img = (ImageView) convertView.findViewById(R.id.img_arrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText((String) mData.get(position).get("item"));
        holder.mySwitch.setOnCheckedChangeListener(mCheckedChangeListener);
        if (settings.getBoolean(MainActivity.FIRST_RUN, true)) {
            ////Log.d(TAG, "first true");
            holder.mySwitch.setChecked(false);
            editor.putBoolean(MainActivity.ALARM_NOTIFICATION, false); // �رձ���
            editor.commit();
        } else if (settings.getBoolean(MainActivity.ALARM_NOTIFICATION, false)) {
            holder.mySwitch.setChecked(true);
        }
        ////Log.d(TAG,"SetListAdapter's ALARM_NOTIFICATION"+settings.getBoolean(MainActivity.ALARM_NOTIFICATION, false));
        if (position == 0) {
            holder.mySwitch.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            convertView.setId(DEFUALT);
        }
        if (position == 1) {
//			convertView.setId(id)
            convertView.setId(ALRAMHISTORY);
        }

        if (position == 2) {
            convertView.setId(EDITPASSWD);
        }

        if (position == 3) {
            convertView.setId(ABOUT);
        }

        return convertView;

    }

    public final class ViewHolder {
        public TextView title;
        public MySwitch mySwitch;
        public ImageView img;
    }

}
