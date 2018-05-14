package com.my51c.see51.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.vov.vitamio.utils.Log;

public class LocationActivity extends BaseActivity {
    BaiduMap mBaiduMap = null;
    MapView mMapView = null;
    private RequestQueue requestQueue;
    private TextView locationmap;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;
    private String lonStr, latStr;

    /* @author sichard
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.locationmap);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        /*mBaiduMap = mMapView.getMap();
		 mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
*/
        locationmap = (TextView) findViewById(R.id.dingweimap);
        locationmap.setTypeface(AppData.fontXiti);
        StartLocation();
        // click();
    }

    public void click() {

        //Toast.makeText(LocationActivity.this, "in click()----------->",Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(latStr) && !TextUtils.isEmpty(lonStr)) {
            //	Toast.makeText(LocationActivity.this, "in 都不为空----------->",Toast.LENGTH_SHORT).show();
            LatLng cenpt = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
		/*if (!TextUtils.isEmpty("1") && !TextUtils.isEmpty("1")) {//写死是可以的。
			LatLng cenpt = new LatLng(Double.parseDouble("33.224"),Double.parseDouble("124.668"));*/
            mBaiduMap = mMapView.getMap();
            mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
            // 定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(15)
                    .build();
            // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                    .newMapStatus(mMapStatus);
            // 改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_marka);
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(cenpt)
                    .icon(bitmap);
            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);

            if (!isNetworkAvailable(LocationActivity.this)) {
                Toast.makeText(LocationActivity.this, "未连接路由！！", 0).show();
            } else {
                if (!ping()) {
                    Toast.makeText(LocationActivity.this, "连接路由但外网不通！！", 0).show();
                } else {
                    Toast.makeText(LocationActivity.this, "连接路由外网连通！！", 0).show();
                }
            }
        } else {
            Toast.makeText(LocationActivity.this, "未得到有效经纬度，请检查网络！", 0).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        pd.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public void onPersonFinish(View v) {
        finish();
    }

    private void StartLocation() {
        progresshow = true;
        showDia();
        JsonObjectRequest set03 = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_POSITION, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                //Toast.makeText(LocationActivity.this, "XTHttpJSON.getJSONString(response.toString())------------"+XTHttpJSON.getJSONString(response.toString().toString()),
                //Toast.LENGTH_SHORT).show();
                loadData(response);// 加载数据
                //Toast.makeText(LocationActivity.this,
                //response.toString() + "12313", 0).show();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.i(TAG, error.toString());
                Toast.makeText(LocationActivity.this, "网络错误!!!",
                        Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        //Toast.makeText(LocationActivity.this,"set03----------------=>"+ set03, 0).show();
        requestQueue.add(set03);
    }

    protected void loadData(JSONObject response) {
        //Toast.makeText(LocationActivity.this, "in loaddata()----------->",Toast.LENGTH_SHORT).show();

        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                latStr = response.getString("lat");
                lonStr = response.getString("lon");
                Log.e("lOCATION------->", "latStr----->" + latStr + "------lonStr------>" + lonStr);
                click();//得到坐标进行定位

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
					/*Toast.makeText(LocationActivity.this, "in response.toString()).为-1----------->",
							Toast.LENGTH_SHORT).show();	*/
            Toast.makeText(LocationActivity.this, "获取参数失败！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    private void showDia() {
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                progresshow = false;
            }
        });
        pd.setMessage("正在加载数据。。。。。");
        pd.show();
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    // if (isNetworkAvailable(ClassTestDemoActivity.this))
    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
