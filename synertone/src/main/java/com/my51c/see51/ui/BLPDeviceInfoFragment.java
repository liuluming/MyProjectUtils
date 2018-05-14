package com.my51c.see51.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class BLPDeviceInfoFragment extends Fragment {

    ArrayList<String> devNames = new ArrayList<String>();
    private ArrayList<String> blpList = null;
    private View view;
    private ListView blpDevList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//		70000004,20150829101802,01000000,5a3a5701,
//		�豸ID��    �������ڣ�  	������Աid,	��ѹ��ѹ��������
        Bundle bundle = getArguments();
        blpList = bundle.getStringArrayList("blpList");
        view = inflater.inflate(R.layout.blpdeviceinfofragment, container, false);
        setView();
        return view;
    }

    public void setView() {
        TextView testTx = (TextView) view.findViewById(R.id.testTx);
        blpDevList = (ListView) view.findViewById(R.id.blpDevList);
        if (blpList == null) {
            testTx.setText(getString(R.string.sphygomaontertip));
        } else {
            getDevNames();
            blpDevList.setAdapter(new BLPAdapter(devNames));
            blpDevList.setOnItemClickListener(new MyItemClickListener());
        }
    }

    private void getDevNames() {
        HashSet<String> devNameSet = new HashSet<String>();
        for (int i = 0; i < blpList.size(); i++) {
            String[] infos = blpList.get(i).split(",");
            devNameSet.add(infos[0]);
        }
        Iterator<String> it = devNameSet.iterator();
        while (it.hasNext()) {
            devNames.add(it.next());
        }
    }

    private class MyItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            String devName = devNames.get(position);
            System.out.println(devNames.get(position));
            ArrayList<String> infoList = new ArrayList<String>();
            for (int i = 0; i < blpList.size(); i++) {
                if (devName.equals(blpList.get(i).split(",")[0])) {
                    infoList.add(blpList.get(i));
                    System.out.println(blpList.get(i));
                }
            }
            Intent intent = new Intent(getActivity(), BLPDetailActivity.class);
            intent.putExtra("infoList", infoList);
            startActivity(intent);
        }
    }

    private class BLPAdapter extends BaseAdapter {

        private ArrayList<String> devNames;

        public BLPAdapter(ArrayList<String> devNames) {
            this.devNames = devNames;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return devNames.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.blpdevlist_item, null);
            TextView blpDevName = (TextView) convertView.findViewById(R.id.blpDevName);
            blpDevName.setText(devNames.get(position));
            return convertView;
        }

    }
}
