package com.my51c.see51.listener;

public interface OnGet3GInfoListener {
    void onGet3GInfoFailed();

    void onGet3GInfoSuccess(byte[] devbuf);
}
