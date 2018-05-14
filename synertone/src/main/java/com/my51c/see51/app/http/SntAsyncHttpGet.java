package com.my51c.see51.app.http;

import android.os.AsyncTask;

import com.my51c.see51.Logger.LoggerSave;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SntAsyncHttpGet extends AsyncTask<String, String, String> {


    HpOverListener finishlistener;

    public void setFinishListener(HpOverListener finishlistener) {
        this.finishlistener = finishlistener;
    }

    @Override
    protected String doInBackground(String... params) {
        String line = "", recv = "";
        // TODO Auto-generated method stub
        HttpGet hget = new HttpGet(params[0]);
        HttpClient hclient = new DefaultHttpClient();
        hclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);

        try {
            HttpResponse respone = hclient.execute(hget);

            recv = EntityUtils.toString(respone.getEntity());
            LoggerSave.responseLog(params[0],recv);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return recv;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        if (result.equals("")) {
            //Log.i("LYJ", "result='',服务器无响应"+result);
            JSONObject js = new JSONObject();
            try {
                js.put("code", "-100");
                finishlistener.HpRecvOk(js);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }

        try {
            JSONObject js = new JSONObject(result);
            //String code = js.getString("code");
            //Log.i("LYJ", "json code"+code);
            finishlistener.HpRecvOk(js);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public interface HpOverListener {
        void HpRecvOk(JSONObject data);

    }


}
