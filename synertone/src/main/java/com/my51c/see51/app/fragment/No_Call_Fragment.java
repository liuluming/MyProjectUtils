package com.my51c.see51.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synertone.netAssistant.R;

public class No_Call_Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = null;
        if (mView == null && inflater != null) {
            mView = inflater.inflate(R.layout.fragment_content222, null);
            findView(mView);
        }
        return mView;
    }

    private void findView(View v) {
        TextView tv = (TextView) v.findViewById(R.id.tv);
        tv.setText("选中：未接电话");
    }
}
