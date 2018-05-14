package com.my51c.see51.app.http;

import android.os.AsyncTask;
import android.util.Log;

import com.my51c.see51.Logger.LoggerSave;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SntAsyncPost extends AsyncTask<String, String, String> {
    private static final String TAG = "SntAsyncPost--------->";
    PostOverHandle postoverhandle;

    public void SetListener(PostOverHandle postoverhandle) {
        this.postoverhandle = postoverhandle;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        String line = "", recv = "";
        try {
            StringEntity jsentity = new StringEntity(params[1], "UTF-8");
            HttpPost hpost = new HttpPost(params[0]);
            hpost.setEntity(jsentity);
            HttpClient hclient = new DefaultHttpClient();
            hclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            try {
                HttpResponse rsponse = hclient.execute(hpost);
                recv = EntityUtils.toString(rsponse.getEntity());
                LoggerSave.responseLog(params[0],recv);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return recv;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        Log.e(TAG, "onPostExecute" + result);
        if (result.equals("")) {
            JSONObject js = new JSONObject();
            try {
                js.put("code", "-100");
                postoverhandle.HandleData(js);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }

        try {
            JSONObject js = new JSONObject(result);
            postoverhandle.HandleData(js);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public interface PostOverHandle {
        void HandleData(JSONObject data);
    }


}
