package com.my51c.see51.app.utils;

import android.content.Context;

import com.my51c.see51.yzxvoip.StringUtils;
import com.synertone.netAssistant.R;

/**
 * Created by snt1231 on 2018/1/24.
 */

public class TransformUtils {
    public static String getConnectionState(String linkStatus, Context mContext) {
        // 0 断线; 1 连接
        if(StringUtils.isEmpty(linkStatus)){
            return mContext.getString(R.string.unknown_type);
        }
        switch (linkStatus){
            case "0":
                return mContext.getString(R.string.off_line);
            case "1":
                return mContext.getString(R.string.on_line);
        }
        return mContext.getString(R.string.unknown_type);
    }

    public static String getSimState(String sim, Context mContext) {
        // 0 无卡 ; 1 有卡
        if(StringUtils.isEmpty(sim)){
            return mContext.getString(R.string.unknown_type);
        }
        switch (sim){
            case"1":
                return mContext.getString(R.string.sim_ok);
            case"0":
                return mContext.getString(R.string.sim_no);
        }
        return mContext.getString(R.string.unknown_type);
    }

    public static String getMobileOperator(String mno, Context mContext) {
        // 0 移动; 1 电信; 2 联通
        if(StringUtils.isEmpty(mno)){
            return mContext.getString(R.string.unknown_type);
        }
        switch (mno){
            case"0":
                return mContext.getString(R.string.yidong);
            case"1":
                return mContext.getString(R.string.dainxin);
            case"2":
                return mContext.getString(R.string.liantong);
        }
        return mContext.getString(R.string.unknown_type);
    }

    public static String getMobileStandard(String standard, Context mContext) {
        // 网络制式 0 GPRS; 1 Edge ;2 3G; 3 4G
        if(StringUtils.isEmpty(standard)){
            return mContext.getString(R.string.unknown_type);
        }
        switch (standard){
            case"0":
                return mContext.getString(R.string.gprs);
            case"1":
                return mContext.getString(R.string.edge);
            case"2":
                return mContext.getString(R.string.san_g);
            case"3":
                return mContext.getString(R.string.si_g);
        }
        return mContext.getString(R.string.unknown_type);
    }
    public static String getAggregatedMode(String mode,Context mContext) {
        //0 广播模式;1 叠加模式;2 主备模式
        if(StringUtils.isEmpty(mode)){
            return mContext.getString(R.string.unknown_type);
        }
        switch (mode){
            case "0":
                return mContext.getString(R.string.broadcasting_mode);
            case "1":
                return mContext.getString(R.string.superposition_mode);
            case "2":
                return mContext.getString(R.string.master_mode);
        }
        return mContext.getString(R.string.unknown_type);
    }
    public static  String getProtocol(String protocol,Context mContext) {
        //1 DHCP ;2 静态IP;3 PPPOE
        if(StringUtils.isEmpty(protocol)){
            return mContext.getString(R.string.unknown_type);
        }
        switch (protocol){
            case "1":
                return mContext.getString(R.string.dhcp_netadapter);
            case "2":
                return mContext.getString(R.string.static_ip);
            case "3":
                return mContext.getString(R.string.PPPOE);
        }
        return mContext.getString(R.string.unknown_type);
    }
}
