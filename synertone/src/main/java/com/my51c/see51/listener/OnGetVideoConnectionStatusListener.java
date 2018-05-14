package com.my51c.see51.listener;

public interface OnGetVideoConnectionStatusListener {
    void onVideoConnection(String deviceid, int nIndex);

    void onVideoDisconnection(String deviceid, int nIndex);
}
