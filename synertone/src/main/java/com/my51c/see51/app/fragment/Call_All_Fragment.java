package com.my51c.see51.app.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.synertone.netAssistant.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Call_All_Fragment extends Fragment {
    private List<CallEtity> calldate;
    private ListView call_all_listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_content111, null);
        call_all_listView = (ListView) mView.findViewById(R.id.call_all_listView);
        findView(mView);
        getcalldate();
        CallAdapter adapter = new CallAdapter();
        call_all_listView.setAdapter(adapter);
        return mView;
    }

    private void findView(View v) {
        TextView tv = (TextView) v.findViewById(R.id.tv);
        tv.setText("选中：全部通话");
    }

    /**
     * 得到本地通话记录
     */
    public void getcalldate() {

        calldate = new ArrayList<Call_All_Fragment.CallEtity>();

        Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CallEtity etity = new CallEtity();
                CallLog calls = new CallLog();
                //号码
                String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
                etity.number = number;
                //呼叫类型
                String type;
                switch (Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE)))) {
                    case Calls.INCOMING_TYPE:
                        type = "呼入";
                        break;
                    case Calls.OUTGOING_TYPE:
                        type = "呼出";
                        break;
                    case Calls.MISSED_TYPE:
                        type = "未接";
                        break;
                    default:
                        type = "挂断";//应该是挂断.根据我手机类型判断出的
                        break;
                }
                etity.type = type;
                SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE))));
                //呼叫时间
                String time = sfd.format(date);
                etity.time = time;
                //联系人
                String name = cursor.getString(cursor.getColumnIndexOrThrow(Calls.CACHED_NAME));
                etity.name = name;
                //通话时间,单位:s
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(Calls.DURATION));
                etity.duration = duration;
                calldate.add(etity);

            } while (cursor.moveToNext());

        }
    }

    private class CallAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return calldate.size();
        }

        @Override
        public Object getItem(int arg0) {
            return calldate.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int point, View view, ViewGroup arg2) {
            ImageView weijie_img;
            TextView phone_number_tv;
            TextView call_time_tv;

            LayoutInflater localinflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = localinflater.inflate(R.layout.call_all_listview_item, null);
            weijie_img = (ImageView) view.findViewById(R.id.weijie_img);
            phone_number_tv = (TextView) view.findViewById(R.id.phone_number_tv);
            call_time_tv = (TextView) view.findViewById(R.id.call_time_tv);

            CallEtity etity = calldate.get(point);
            //显示姓名或电话号码
            if (!TextUtils.isEmpty(etity.name)) {
                phone_number_tv.setText(etity.name);
            } else {
                phone_number_tv.setText(etity.number);
            }
            //显示通话日期
            call_time_tv.setText(etity.time);
            //设置通话日期类型
//			if(!TextUtils.isEmpty(etity.type)){
//				 if(etity.type.equals("未接")) {
//					 weijie_img.setImageResource(R.drawable.call_all_17);
//
//			        } else if(){}
//			}


            return view;
        }


    }

    /**
     * Entity
     */
    public class CallEtity {
        private String number;
        private String type;
        private String time;
        private String name;
        private String duration;

        public CallEtity() {
        }
    }
}
