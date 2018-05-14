package com.my51c.see51.yzxvoip.model;

import android.content.Context;
import android.util.Log;

import com.my51c.see51.yzxvoip.IMChatActivity;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

/**
 * δ��¼״̬
 *
 * @author zhuqian
 */

public class LoginOutState implements State, ILoginListener {
    private IMChatActivity imChatActivity;

    @Override
    public void action(Context context, String token) {
        //δ��¼�����õ�¼�ӿ�
        Log.i(IMChatActivity.TAG, "LoginOutState����������...");
        UCSManager.connect(token, this);

        if (context instanceof IMChatActivity) {
            imChatActivity = (IMChatActivity) context;
        }
    }

    @Override
    public void onLogin(UcsReason reason) {
        if (reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK) {
            Log.i(IMChatActivity.TAG, "LoginOutState ��¼�ɹ�...");
            //imChatActivity.setState(new State());
        }
    }
}

