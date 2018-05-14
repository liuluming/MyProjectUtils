package com.my51c.see51.listener;

public interface OnGetDevInfoListener {
    void onGetDevInfoFailed();

    void onGetDevInfoSuccess(byte[] devbuf);
}
