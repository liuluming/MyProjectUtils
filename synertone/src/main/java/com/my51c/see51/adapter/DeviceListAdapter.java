package com.my51c.see51.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.DeviceSee51Info;
import com.my51c.see51.data.Group;
import com.my51c.see51.map.MyClusterItem;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeviceListAdapter extends BaseAdapter {

    private static final String TAG = "DeviceListAdapter";
    public static ArrayList<MyClusterItem> devLocationList = new ArrayList<MyClusterItem>();
    private LayoutInflater mInflater;
    private ArrayList<Map<String, Object>> mData;
    private DeviceList deviceList;
    private OnClickListener mListener;
    private boolean bLocal = true;

    public DeviceListAdapter(Context context, DeviceList devList, OnClickListener l, boolean bType) {
        this.mInflater = LayoutInflater.from(context);
        this.deviceList = devList;
        this.mListener = l;
        bLocal = bType;
        // devList.addListListener(this);
        updateDeviceData();
        // TODO Test code here
        //Log.d("DeviceListAdapter", "constructor");

    }

    public DeviceListAdapter(Context context, ArrayList<Map<String, Object>> mData) {
        this.mData = new ArrayList<Map<String, Object>>();
        this.mData = mData;
    }

    public void setDeviceList(DeviceList deviceList) {
        this.deviceList = deviceList;
    }

    public ArrayList<Map<String, Object>> getmData() {
        return mData;
    }

    public DeviceList getdeviceList() {
        return deviceList;
    }

    public void updateDeviceData() {
        if (deviceList == null) {
            return;
        }
        if (mData == null) {
            mData = new ArrayList<Map<String, Object>>();
        }

        mData.clear();
        devLocationList.clear();
        synchronized (deviceList) {
            for (Device dev : deviceList) {
                DeviceLocalInfo localInfo = dev.getLocalInfo();
                DeviceSee51Info see51Info = dev.getSee51Info();
                dev.set3GParam(null);
                dev.setRFInfo(null);

                if (see51Info != null)  // ������ʾԶ����Ϣ��˳���ܸ�
                {
//					DeviceLocalInfo info = dev.getLocalInfo();
//					Log.i(TAG, dev.getID()+":"+info.getSdTotal()+"--"+info.getSdFree());
                    String locationStr = see51Info.getLocation();
                    if (!locationStr.equals("") && locationStr.contains(",")) {
                        try {
                            String[] items = locationStr.split(",");
                            double latitude = Double.valueOf(items[0]);
                            double longitude = Double.valueOf(items[1]);
                            LatLng latLng = new LatLng(latitude, longitude);
                            MyClusterItem item = new MyClusterItem(latLng, see51Info.getDeviceName(), see51Info.getDiviceID());
                            if (!devLocationList.contains(item)) {
                                devLocationList.add(item);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    dev.setLocalInfo(null);
                    boolean online = false;
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("title", see51Info.getDeviceName());
                    map.put("info", see51Info.getDiviceID());
                    map.put("img", dev.getSnapImage());
                    map.put("url", dev.getPlayURL());
                    if (see51Info.getStatus() == 2) {
                        map.put("status", R.drawable.dev_online);
                        online = true;
                    } else {
                        map.put("status", R.drawable.dev_offline);
                    }
                    ////Log.d("DeviceAdapter", see51Info.getDiviceID() + " " + see51Info.getStatus() );
                    map.put("device", dev);
                    if (online)
                        mData.add(0, map);
                    else
                        mData.add(map);
                } else if (localInfo != null)  // ��ʾ������Ϣ
                {
                    Log.i(TAG, localInfo.getCamSerial() + ":" + localInfo.getSdTotal() + "--" + localInfo.getSdFree());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("title", localInfo.getDeviceName());
                    map.put("info", localInfo.getCamSerial());
                    map.put("img", dev.getSnapImage());
                    map.put("device", dev);
                    map.put("status", R.drawable.dev_online);
                    mData.add(map);
                }
            }

            Iterator<Group> groupIterator = deviceList.getIteratorGroup();
            Group grandGroup = deviceList.getParent_group();
            while (groupIterator.hasNext()) {
                Group group = groupIterator.next();
                if (group != null) // ��ʾsee51�����Ϣ
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("title", group.getGroupName());
//					Log.i("DeviceListAdapter", "�����ƣ�"+group.getGroupName());
                    if (grandGroup != null) {
                        String grandParent_group = grandGroup.getGroupID();
                        map.put("grandParent_group", grandParent_group);
                    }
                    map.put("img", R.drawable.folder);
                    map.put("group", group);
                    map.put("status", R.drawable.dev_offline);
                    mData.add(0, map);
                }
            }
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.device_list_item, null);
            holder.statusImg = (ImageView) convertView.findViewById(R.id.cam_status);
            holder.img = (ImageView) convertView.findViewById(R.id.cam_image);
            holder.title = (TextView) convertView.findViewById(R.id.cam_title);
            holder.info = (TextView) convertView.findViewById(R.id.cam_info);
            holder.viewBtn = (ImageButton) convertView.findViewById(R.id.cam_imagebtn);
            holder.SelectImg = (ImageView) convertView.findViewById(R.id.imageViewSelection);
            holder.viewBtn.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.statusImg.setBackgroundResource((Integer) mData.get(position).get("status"));
        Object img = mData.get(position).get("img");
        if (img instanceof Bitmap) {
            holder.img.setImageBitmap((Bitmap) img);
        } else {
            holder.img.setImageResource((Integer) mData.get(position).get("img"));
        }

        holder.title.setText((String) mData.get(position).get("title"));
        if (mData.get(position).get("info") != null) {
            holder.viewBtn.setOnClickListener(this.mListener); // ����info��������
            holder.viewBtn.setTag(holder);
            holder.viewBtn.setBackgroundResource(R.drawable.setting_style);
            holder.Gro_Dev = "device";
            // holder.title.setOnClickListener(onListClickLisenter); //
            // �����б����¼��������
            holder.title.setTag(mData.get(position).get("device"));
            holder.info.setText((String) mData.get(position).get("info"));
            // holder.info.setOnClickListener(onListClickLisenter);
            holder.info.setTag(mData.get(position).get("device"));
            holder.statusImg.setVisibility(View.VISIBLE);
            holder.SelectImg.setVisibility(View.VISIBLE);

            Device mCurrentDev = (Device) mData.get(position).get("device");
            boolean bSelectStatus = false;
            if (bLocal) {
                bSelectStatus = mCurrentDev.isbLocalSelected();
            } else {
                bSelectStatus = mCurrentDev.isbRemoteSelected();
            }

            if (bSelectStatus) {
                holder.SelectImg.setImageResource(R.drawable.list_selected_btn_sel);
            } else {
                holder.SelectImg.setImageResource(R.drawable.list_selected_btn);
            }
        } else {
            // �����б����¼��������
            holder.viewBtn.setOnClickListener(null);
            // holder.info.setOnClickListener(onListClickLisenter);
            holder.info.setText(null);
            holder.viewBtn.setClickable(false);
            holder.viewBtn.setBackgroundResource(R.drawable.arrow);
            Group group = (Group) mData.get(position).get("group");
            if (group.getDevCount() > 0)
                holder.info.setText(String.valueOf(group.getDevCount()));
            holder.Gro_Dev = "group";
            holder.group = (Group) mData.get(position).get("group");
            holder.grandParent_group = (String) mData.get(position).get("grandParent_group");
            holder.statusImg.setVisibility(View.GONE);
            holder.SelectImg.setVisibility(View.GONE);
        }
        return convertView;
    }

    public final class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
        public ImageButton viewBtn;
        public String Gro_Dev;
        public String grandParent_group;
        public Group group;
        public ImageView statusImg;
        public ImageView SelectImg;
    }

	/*public void release()
	{
		
	}*/

}
