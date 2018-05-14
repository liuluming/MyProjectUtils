package com.my51c.see51.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.my51c.see51.data.Device;
import com.my51c.see51.protocal.RFPackage;
import com.my51c.see51.widget.BadgeView;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RFDeviceTypeGridFragment extends SherlockFragment {
    private int typeDeviceName[] = {R.string.rfcontrol, R.string.rfdoorsensor, R.string.rfpir, R.string.rfsmoke,
            R.string.rfsmartplug, R.string.rfsoundlight, R.string.rfdoorcamera, R.string.rfioalarm, R.string.blpSTR
            , R.string.rf_curtain, R.string.rf_lock};
    private GridView gridview;
    private int[] rfdevnum = new int[11];
    private int[] rfimagelog = {R.drawable.grid_controller, R.drawable.grid_doorsensor, R.drawable.grid_pirsensor,
            R.drawable.grid_smokesensor, R.drawable.grid_smartplug, R.drawable.grid_soundlight,
            R.drawable.grid_doorcamera, R.drawable.grid_iosensor, R.drawable.grid_blp,
            R.drawable.grid_curtain, R.drawable.grid_lock};
    private Device mDevice;
    private RFPackage rfpack;
    private OnGridItemClick mOnGridItemClick;
    private ImageAdapter imgadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    public void onRefreshRFDevice() {
        mDevice = ((RFDeviceInfoActivity) getActivity()).getParseDevice();
        rfpack = mDevice.getRFInfo();
        computerfnum(rfpack);

        if (imgadapter != null) {
            imgadapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.rfdevicetypefragment, container, false);
        gridview = (GridView) view.findViewById(R.id.gridviewType);
        return view;
    }

    /**
     * ����RFPackage����ȡ�����豸����
     */
    private void computerfnum(RFPackage inpack) {

        for (int i = 0; i < 10; i++) {
            rfdevnum[i] = 0;
        }

//		if(isBLPexist){
//			rfdevnum[8] = blpList.size();
//		}else{
//			rfdevnum[8] = 0;
//		}
        rfdevnum[8] = ((RFDeviceInfoActivity) getActivity()).getBlpNum();

        if (inpack != null) {
            ArrayList<Map<String, Object>> mData = inpack.getRFDevList();
            int nCount = mData.size();

            for (int i = 0; i < nCount; i++) {
                HashMap<String, Object> map = (HashMap<String, Object>) mData.get(i);
                String strID = (String) map.get("MY51CRFID");
                String strType = strID.substring(0, 2);
                if (strType.equals("01")) {
                    rfdevnum[0]++;
                } else if (strType.equals("02")) {
                    rfdevnum[1]++;
                } else if (strType.equals("03")) {
                    rfdevnum[2]++;
                } else if (strType.equals("04")) {
                    rfdevnum[3]++;
                } else if (strType.equals("10")) {
                    rfdevnum[4]++;
                } else if (strType.equals("21")) {
                    rfdevnum[5]++;
                } else if (strType.equals("22")) {
                    rfdevnum[6]++;
                } else if (strType.equals("23")) {
                    rfdevnum[7]++;
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        try {
            mOnGridItemClick = (OnGridItemClick) activity;
        } catch (Exception e) {
            // TODO: handle exception
            throw new ClassCastException(activity.toString() + "must implement OnGridItemClick");
        }

        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mDevice = ((RFDeviceInfoActivity) getActivity()).getParseDevice();//get device

        if (mDevice != null) {
            rfpack = mDevice.getRFInfo();//get RFPackage
        }

        computerfnum(rfpack);

        imgadapter = new ImageAdapter(getActivity());
        gridview.setAdapter(imgadapter);
        gridview.setOnItemClickListener(new ItemClickListener());

    }

    public interface OnGridItemClick {
        void OnItemClick(int position);
    }

    public class ItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            mOnGridItemClick.OnItemClick(position);
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater inflater;

        public ImageAdapter(Context c) {
            mContext = c;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return typeDeviceName.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.rfdevice_nine_item, null);
                holder = new GridHolder();
                holder.rfitemImage = (ImageView) convertView.findViewById(R.id.rfitemImage);
                holder.rfitemText = (TextView) convertView.findViewById(R.id.rfitemText);
                holder.subView = (RelativeLayout) convertView.findViewById(R.id.subcontent);
                convertView.setTag(holder);
            } else {
                holder = (GridHolder) convertView.getTag();
            }

            BadgeView badge = new BadgeView(getActivity(), holder.subView);
            int num = 0;
            num += rfdevnum[position];
            badge.setText("" + num);
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badge.show();

            holder.rfitemImage.setImageResource(rfimagelog[position]);
            holder.rfitemText.setText(getString(typeDeviceName[position]));
            return convertView;
        }

        private class GridHolder {
            ImageView rfitemImage;
            TextView rfitemText;
            RelativeLayout subView;
        }

    }

}
