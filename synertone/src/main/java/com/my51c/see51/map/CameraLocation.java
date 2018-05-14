package com.my51c.see51.map;

import android.R.color;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.clusterutil.clustering.view.DefaultClusterRenderer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.DeviceListAdapter;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.ui.PlayerActivity;
import com.my51c.see51.widget.DevImgItem;
import com.my51c.see51.widget.ToastCommom;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CameraLocation extends BaseActivity implements BaiduMap.OnMapLoadedCallback {

    private static final String TAG = "CameraLocation";
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true;// 是否首次定位
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    //private Location location;
    private BitmapDescriptor mCurrentMaker;
    private BitmapDescriptor mCurrentMakerA;
    private BMapManager mBMapManager;
    private LocationClient mLocClient;
    //我的位置层
    private Overlay mOverlay;
    //模拟坐标标记
    private Marker mMarkerT;
    //实时坐标标记
    private Marker mMarkerA;
    private MapStatus ms;
    private ClusterManager<MyClusterItem> mClusterManager;
    private StringBuffer sb;
    private AppData appData;
    private ToastCommom toast;
    private PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        appData = (AppData) getApplication();
        toast = new ToastCommom();
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_cameralocation);
        mMapView = (MapView) findViewById(R.id.bmapView);
        initMap();

        LinearLayout back = (LinearLayout) findViewById(R.id.back_layout);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


    }

    private void initMap() {

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);
        mClusterManager = new ClusterManager<MyClusterItem>(this, mBaiduMap);
//        addMarkers();
        mClusterManager.addItems(DeviceListAdapter.devLocationList);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);// 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mCurrentMaker = BitmapDescriptorFactory.fromResource(R.drawable.map_dev_img);
        mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
        //定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd0911");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        if (mLocClient != null || mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try {
                    String name = (String) marker.getExtraInfo().get("name");
                    final String id = (String) marker.getExtraInfo().get("id");
                    //获得marker中的数据
                    InfoWindow infoWindow;
                    TextView infoTx = new TextView(getApplicationContext());
                    infoTx.setBackgroundResource(R.drawable.infowindow_bac);
                    infoTx.setText(name + " " + id);
                    infoTx.setTextColor(Color.parseColor("#000000"));
                    infoTx.setPadding(10, 20, 10, 20);
                    final LatLng tll = marker.getPosition();
                    Point p = mBaiduMap.getProjection().toScreenLocation(tll);
                    p.y = p.y - 110;
                    LatLng llinfo = mBaiduMap.getProjection().fromScreenLocation(p);
                    BitmapDescriptor mclickMaker;
                    mclickMaker = BitmapDescriptorFactory.fromView(infoTx);
                    infoWindow = new InfoWindow(mclickMaker, llinfo, 47, new InfoWindow.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick() {

                            mBaiduMap.hideInfoWindow();
                            Device dev = appData.getAccountInfo().getCurrentList().getDevice(id);
                            System.out.println("当前账户设备数量：" + appData.getAccountInfo().getCurrentList().getDeviceCount());
                            if (dev != null && dev.getPlayURL() != null) {
                                Intent intent = new Intent(CameraLocation.this, PlayerActivity.class);
                                intent.putExtra("id", dev.getID());
                                intent.putExtra("url", dev.getPlayURL());
                                intent.putExtra("title", dev.getSee51Info().getDeviceName());
                                intent.putExtra("version", dev.getSee51Info().getHwVersion() + " / " + dev.getSee51Info().getSwVersion());
                                intent.putExtra("name", dev.getSee51Info().getDeviceName());
                                intent.putExtra("isLocal", false);
                                startActivity(intent);
                            } else {
                                boolean b1 = dev == null;
                                boolean b2 = dev.getPlayURL() == null;
                                int status = dev.getSee51Info().getStatus();
                                toast.ToastShow(getApplicationContext(), getString(R.string.msgdeviceoffline), 1000);
                            }
                        }
                    });
                    mBaiduMap.showInfoWindow(infoWindow);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mBaiduMap.hideInfoWindow();
                        }
                    }, 3000);
                } catch (Exception e) {
                    Log.i(TAG, e + "" + marker.getPosition());

                    String key = marker.getPosition().latitude + "," + marker.getPosition().longitude;
                    ArrayList<String> value = new ArrayList<String>();
                    value = DefaultClusterRenderer.totallMap.get(key);
                    for (String str : value) {
                        Log.i(TAG, "id：" + str);
                    }
                    initPopupWindow(value);
                }
                return true;
            }
        });

    }

    private void initPopupWindow(ArrayList<String> ids) {

        View popupView = getLayoutInflater().inflate(R.layout.popupwindow_layout, null);
        LinearLayout contentL = (LinearLayout) popupView.findViewById(R.id.contentL);
        popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, 180, true);
        popupWindow.setAnimationStyle(R.anim.left_in);
        popupWindow.setBackgroundDrawable(new ColorDrawable(color.white));//响应back需添加背景
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });

        for (String id : ids) {
            final Device dev = appData.getAccountInfo().getCurrentList().getDevice(id);
            if (dev != null) {
                final DevImgItem devImg = new DevImgItem(getApplicationContext());
                devImg.setDevImg(dev.getSnapImage());
                devImg.setStatusImg(dev.getSee51Info().getStatus());
                devImg.setDevInfoTx(dev.getSee51Info().getDeviceName() + "," + dev.getID());
                devImg.setLayoutParams(new LayoutParams(200, 160));
                contentL.addView(devImg);
                devImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (devImg.getStatus()) {
                            Intent intent = new Intent(CameraLocation.this, PlayerActivity.class);
                            intent.putExtra("id", dev.getID());
                            intent.putExtra("url", dev.getPlayURL());
                            intent.putExtra("title", dev.getSee51Info().getDeviceName());
                            intent.putExtra("version", dev.getSee51Info().getHwVersion() + " / " + dev.getSee51Info().getSwVersion());
                            intent.putExtra("name", dev.getSee51Info().getDeviceName());
                            intent.putExtra("isLocal", false);
                            startActivity(intent);
                        } else {
                            toast.ToastShow(getApplicationContext(), getString(R.string.msgdeviceoffline), 1000);
                        }
                    }
                });
            }
        }
        popupWindow.showAtLocation(
                CameraLocation.this.findViewById(R.id.location_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        mLocClient.stop();
        DefaultClusterRenderer.totallMap.clear();
        mBaiduMap.setMyLocationEnabled(false);
        mCurrentMaker.recycle();
        mMapView = null;
        mLocClient = null;
        mBaiduMap = null;
        mCurrentMaker = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // mBaiduMap.setMyLocationEnabled(false);
        mMapView.onPause();
    }

    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().zoom(12).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    private class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            sb = new StringBuffer();
            sb.append("纬度：" + location.getLatitude() + "" + "经度：" + location.getLongitude());
            if (location == null || mMapView == null) {
                return;
            }
            //构造定位函数
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            //设置定位数据
            mBaiduMap.setMyLocationData(locationData);
            if (isFirstLoc) {
                isFirstLoc = false;
                ms = new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }


}
