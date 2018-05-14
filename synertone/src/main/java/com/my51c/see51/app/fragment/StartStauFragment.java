package com.my51c.see51.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

//卫星状态
public class StartStauFragment extends Fragment {
    protected static final String TAG = "StartStauFragment";
    private TextView mDeviceAzi, mDevicElevcar, mDeviceRoll, mDeviceCurrent,
            mDeviceCurrentlat, mDeviceRssi, mDevicElev, mDeviceRecpol,
            mDeviceTemp, mDeviceLocatype, mDeviceHot, mDeviceVol,
            mDeviceAzimotor, mDevicElevmotor, mDeviceSendzero, mDeviceElevzero,
            mDeviceGyroscope, mDeviceRf, mDevicePosition, mDeviceCtrlver,
            mDeviceCtrlnum, mDeviceOudver, mDeviceOdenum,device_star_compass;
    private View view;// 缓存Fragment view

    private String statUrl;
    private JSONObject response;
    private LinearLayout ll_start_gyroscope,ll_star_compass,ll_start_elevcarrier;
    private TextView mTianxianStyle;
    private HashMap<String, Toast> toaHashMap=new HashMap<>();
    public StartStauFragment(String statUrl, JSONObject response) {
        this.statUrl = statUrl;
        this.response = response;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.device_start_statu_fragment,
                container, false);
		/*
		 * mRequestQueue = Volley.newRequestQueue(StartStauFragment.this
		 * .getActivity());
		 */
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		/*
		 * ViewGroup parent = (ViewGroup) view.getParent(); if (parent != null)
		 * { parent.removeView(view); }
		 */
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // devStatuQuery();
    }
    /*
     * @Override public void setUserVisibleHint(boolean isVisibleToUser) { if
     * (isVisibleToUser) {
     *
     * } else { return; } super.setUserVisibleHint(isVisibleToUser); }
     */
    private void initView(View view) {
        mDeviceAzi = (TextView) view.findViewById(R.id.device_start_azi);
        mDevicElevcar = (TextView) view
                .findViewById(R.id.device_start_elevcarrier);
        mDeviceRoll = (TextView) view.findViewById(R.id.device_start_roll);
        mDeviceCurrent = (TextView) view
                .findViewById(R.id.device_start_currentlon);
        mDeviceCurrentlat = (TextView) view
                .findViewById(R.id.device_start_currentlat);
        mDeviceRssi = (TextView) view.findViewById(R.id.device_start_rssi);

        mDevicElev = (TextView) view.findViewById(R.id.device_start_elev);
        mDeviceRecpol = (TextView) view.findViewById(R.id.device_start_recpol);
        mDeviceTemp = (TextView) view.findViewById(R.id.device_start_temp);
        mDeviceLocatype = (TextView) view
                .findViewById(R.id.device_start_locatype);
        mDeviceHot = (TextView) view.findViewById(R.id.device_start_hot);
        mDeviceVol = (TextView) view.findViewById(R.id.device_start_vol);

        mDeviceAzimotor = (TextView) view
                .findViewById(R.id.device_start_azimotor);
        mDevicElevmotor = (TextView) view
                .findViewById(R.id.device_start_elevmotor);
        mDeviceSendzero = (TextView) view
                .findViewById(R.id.device_start_sendzero);
        mDeviceElevzero = (TextView) view
                .findViewById(R.id.device_start_elevzero);
        mDeviceGyroscope = (TextView) view
                .findViewById(R.id.device_start_gyroscope);
        mDeviceRf = (TextView) view.findViewById(R.id.device_start_rf);
        mDevicePosition = (TextView) view
                .findViewById(R.id.device_start_position);
        mDeviceCtrlver = (TextView) view
                .findViewById(R.id.device_start_ctrlver);
        mDeviceCtrlnum = (TextView) view
                .findViewById(R.id.device_start_ctrlnum);
        mDeviceOudver = (TextView) view.findViewById(R.id.device_start_oudver);
        mDeviceOdenum = (TextView) view.findViewById(R.id.device_start_odenum);
        mTianxianStyle=(TextView) view.findViewById(R.id.device_star_type);
        ll_start_gyroscope=(LinearLayout) view.findViewById(R.id.ll_start_gyroscope);
        ll_star_compass=(LinearLayout) view.findViewById(R.id.ll_star_compass);
        device_star_compass=(TextView) view.findViewById(R.id.device_star_compass);
        ll_start_elevcarrier=(LinearLayout)view.findViewById(R.id.ll_start_elevcarrier);
        initToasts();
    }
    @SuppressLint("ShowToast")
    private void initToasts() {
        Toast toast=Toast.makeText(getActivity().getBaseContext(),"无法获取天线类型信息，请检查ODU模块是否正常！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toaHashMap.put("无法获取天线类型信息，请检查ODU模块是否正常！", toast);
    }
	/*
	 * private void devStatuQuery() { progresshow = true; showDia();
	 * JsonObjectRequest request = new JsonObjectRequest(Method.GET, statUrl,
	 * null, new Listener<JSONObject>() {
	 *
	 * @Override public void onResponse(JSONObject response) { //
	 * pdDismiss(response); Log.i(TAG, "接收回来的数据===》" + response.toString());
	 * Toast.makeText(getActivity(), response.toString(), 0) .show();
	 * loadData(response);// 加载数据 } }, new ErrorListener() {
	 *
	 * @Override public void onErrorResponse(VolleyError error) { Log.i(TAG,
	 * error.toString());
	 *
	 * if (pd.isShowing()) { pd.dismiss(); } } }); mRequestQueue.add(request); }
	 */

    private String aziStr, elevcarrStr, rollStr, curlonStr, currlatStr,
            elevStr, tempStr, rssiStr, ctrlverStr, ctrlnumStr, oduverStr,
            odunumStr, locatypeStr, hotStr, volStr, azimotorStr, elevmotorStr,
            sendzeroStr, elevzeroStr, rfStr, positionStr,mOduType,mSensor;

    public void loadData(JSONObject response) {
        // if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
        try {
            aziStr = response.getString("azi");
            elevcarrStr = response.getString("elevcarr");
            rollStr = response.getString("roll");
            curlonStr = response.getString("curlon");
            currlatStr = response.getString("currlat");
            elevStr = response.getString("elev");
            tempStr = response.getString("temp");
            rssiStr = response.getString("rssi");
            ctrlverStr = response.getString("ctrlver");
            ctrlnumStr = response.getString("ctrlnum");
            oduverStr = response.getString("oduver");
            odunumStr = response.getString("odunum");
            locatypeStr = response.getString("locatype");
            hotStr = response.getString("hot");
            volStr = response.getString("vol");
            azimotorStr = response.getString("azimotor");
            elevmotorStr = response.getString("elevmotor");
            sendzeroStr = response.getString("sendzero");
            elevzeroStr = response.getString("elevzero");
            //gyroscopeStr = response.getString("gyroscope");
            rfStr = response.getString("rf");
            positionStr = response.getString("position");
            mOduType = response.getString("odutype");
            mSensor = response.getString("sensor");
//			Toast.makeText(
//					getActivity(),
//					aziStr + elevmotorStr + sendzeroStr + elevzeroStr
//							+ gyroscopeStr + rfStr + positionStr, 0).show();

//			getActivity().runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
            mDeviceAzi.setText(aziStr);// 方位角
            mDevicElevcar.setText(elevcarrStr);// 仰角载体
            mDeviceRoll.setText(rollStr);// 横滚角
            mDeviceCurrent.setText(curlonStr);// 经度
            mDeviceCurrentlat.setText(currlatStr);// 纬度
            mDeviceRssi.setText(rssiStr);// RSSI数值
            mDevicElev.setText(elevStr);// 天线仰角
            // mDeviceRecpol.setText();
            mDeviceTemp.setText(tempStr);// 天线温度
            // 定位方式
            if (locatypeStr.equals("0")) {
                mDeviceLocatype.setText("手动");
            } else if (locatypeStr.equals("1")) {
                mDeviceLocatype.setText("GPS");
            } else if (locatypeStr.equals("2")) {
                mDeviceLocatype.setText("北斗");
            }

            if (hotStr.equals("0")) {
                mDeviceHot.setText("正常");// 过温
            } else if (hotStr.equals("-1")) {
                mDeviceHot.setText("故障");// 故障
            }

            // 电压故障
            if (volStr.equals("0")) {
                mDeviceVol.setText("正常");
            } else if (volStr.equals("-1")) {
                mDeviceVol.setText("故障");
            }

            // 方位点击故障
            if (azimotorStr.equals("0")) {
                mDeviceAzimotor.setText("正常");
            } else if (azimotorStr.equals("-1")) {
                mDeviceAzimotor.setText("故障");
            }

            // 俯仰电机故障
            if (elevmotorStr.equals("0")) {
                mDevicElevmotor.setText("正常");
            } else if (elevmotorStr.equals("-1")) {
                mDevicElevmotor.setText("故障");
            }

            // 发射归零故障
            if (sendzeroStr.equals("0")) {
                mDeviceSendzero.setText("正常");
            } else if (sendzeroStr.equals("-1")) {
                mDeviceSendzero.setText("故障");
            }
            // 俯仰归零故障
            if (elevzeroStr.equals("0")) {
                mDeviceElevzero.setText("正常");
            } else if (elevzeroStr.equals("-1")) {
                mDeviceElevzero.setText("故障");
            }
				/*	// 陀螺仪故障
					if (gyroscopeStr.equals("0")) {
						mDeviceGyroscope.setText("正常");
					} else if (gyroscopeStr.equals("-1")) {
						mDeviceGyroscope.setText("故障");
					}*/

            // 射频信号故障
            if (rfStr.equals("0")) {
                mDeviceRf.setText("正常");
            } else if (rfStr.equals("-1")) {
                mDeviceRf.setText("故障");
            }

            // 位置设置错误
            if (positionStr.equals("0")) {
                mDevicePosition.setText("正常");
            } else if (positionStr.equals("-1")) {
                mDevicePosition.setText("位置错误");
            }
            // 天线类型
            if("0".equals(mOduType)){
                mTianxianStyle.setText("--");
                if (toaHashMap.get("无法获取天线类型信息，请检查ODU模块是否正常！")!=null){
                    toaHashMap.get("无法获取天线类型信息，请检查ODU模块是否正常！").show();
                }
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("1".equals(mOduType)){
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                mTianxianStyle.setText("V4");
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("2".equals(mOduType)){
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                mTianxianStyle.setText("V6");
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("3".equals(mOduType)){
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                mTianxianStyle.setText("S6");
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("4".equals(mOduType)){
                mTianxianStyle.setText("S6A");//三轴
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("5".equals(mOduType)){
                mTianxianStyle.setText("S8");
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("6".equals(mOduType)){
                mTianxianStyle.setText("S9");
                ll_start_gyroscope.setVisibility(View.VISIBLE);
                ll_star_compass.setVisibility(View.GONE);
                ll_start_elevcarrier.setVisibility(View.VISIBLE);
            }else if("7".equals(mOduType)){
                mTianxianStyle.setText("C6");
                ll_start_gyroscope.setVisibility(View.GONE);
                ll_star_compass.setVisibility(View.VISIBLE);
                ll_start_elevcarrier.setVisibility(View.GONE);
            }else if("8".equals(mOduType)){
                mTianxianStyle.setText("C9");
                ll_start_gyroscope.setVisibility(View.GONE);
                ll_star_compass.setVisibility(View.VISIBLE);
                ll_start_elevcarrier.setVisibility(View.GONE);
            }
            // 陀螺仪故障或电子罗盘故障
            if (mSensor.equals("0")) {
                device_star_compass.setText("正常");
                mDeviceGyroscope.setText("正常");
            } else if (mSensor.equals("-1")) {
                device_star_compass.setText("故障");
                mDeviceGyroscope.setText("故障");
            }
            mDeviceCtrlver.setText(ctrlverStr);// 控制器版本
            mDeviceCtrlnum.setText(ctrlnumStr);// 控制器编号
            mDeviceOudver.setText(oduverStr);// 天线版本
            mDeviceOdenum.setText(odunumStr);// 天线编号
            //}
            //	});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Iterator<Entry<String, Toast>> iter = toaHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Toast> entry = iter.next();
                Toast toast=entry.getValue();
                Field field = toast.getClass().getDeclaredField("mTN");
                field.setAccessible(true);
                Object obj = field.get(toast);
                java.lang.reflect.Method m=obj.getClass().getDeclaredMethod("hide");
                m.invoke(obj);
                iter.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
