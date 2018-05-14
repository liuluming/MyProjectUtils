package com.my51c.see51.map;

import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.synertone.netAssistant.R;

//�ۺ�items
public class MyClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private String name;
    private String id;


    public MyClusterItem(LatLng latLng, String name, String id) {
        mPosition = latLng;
        this.name = name;
        this.id = id;
    }

    public MyClusterItem(LatLng latLng) {
        mPosition = latLng;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getName() {
        return name;
    }

    public String getid() {
        return id;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.drawable.map_dev_img);
    }
}