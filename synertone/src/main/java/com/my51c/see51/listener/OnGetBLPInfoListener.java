package com.my51c.see51.listener;

public interface OnGetBLPInfoListener {
    void onGetBLPInfoFailed();

    void onGetBLPInfoSuccess(byte[] devbuf);
}
