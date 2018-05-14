package com.my51c.see51.listener;

public interface OnGetRFInfoListener {
    void onGetRFInfoFailed();

    void onGetRFInfoSuccess(byte[] devbuf);
}
