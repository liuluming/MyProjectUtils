package com.my51c.see51.app.http;

import com.lidroid.xutils.http.RequestParams;

/**
 * Created by snt1231 on 2018/1/20.
 */

public class MyRequestParams extends RequestParams{
    public MyRequestParams() {
        this.setContentType("application/json");
    }

    public MyRequestParams(String charset) {
        super(charset);
        this.setContentType("application/json");
    }
}
