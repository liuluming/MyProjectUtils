package com.my51c.see51.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.synertone.netAssistant.R;
import com.xqe.method.DelEditText;

public class ChangeUserPasswdActivity extends Fragment implements OnClickListener {
    static Activity mActivity;
    private Button btnChangeOK;
    private DelEditText edtOldPasswd;
    private DelEditText edtNewPasswd;
    private DelEditText edtComfirePasswd;
    private OnChangePasswdListener mOnChangePasswdListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.local_setting_change_pwd, container, false);
        btnChangeOK = (Button) v.findViewById(R.id.btncomfirmpasswd);
        btnChangeOK.setVisibility(View.VISIBLE);

        edtOldPasswd = (DelEditText) v.findViewById(R.id.password_edit);
        edtNewPasswd = (DelEditText) v.findViewById(R.id.newpassword_edit);
        edtComfirePasswd = (DelEditText) v.findViewById(R.id.newpasswordagain_edit);

        btnChangeOK.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            mActivity = activity;
            mOnChangePasswdListener = (OnChangePasswdListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "ChangeUserPasswd attach failed!");
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        String psw = edtOldPasswd.getText().toString();
        String new_psw = edtNewPasswd.getText().toString();
        String psw_again = edtComfirePasswd.getText().toString();

        mOnChangePasswdListener.onChangePasswdClicked(psw, new_psw, psw_again);
    }

    //bm-add 1101
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mActivity = null;
    }

    public interface OnChangePasswdListener {
        void onChangePasswdClicked(String strOldPasswd, String strNewPasswd, String strNewPasswdAgain);
    }
}
