package com.my51c.see51.common;

import android.content.Context;
import android.content.SharedPreferences;


public class SystemConfigSp {
    private static SystemConfigSp systemConfigSp = null;
    private final String fileName = "systemconfig.ini";
    SharedPreferences sharedPreferences;
    private Context ctx;

    private SystemConfigSp() {

    }

    public static SystemConfigSp instance() {
        if (systemConfigSp == null) {
            synchronized (SystemConfigSp.class) {
                systemConfigSp = new SystemConfigSp();
            }
        }
        return systemConfigSp;
    }

    public void init(Context ctx) {
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences
                (fileName, Context.MODE_PRIVATE);
    }

    public String getStrConfig(SysCfgDimension dimension) {
        String strValue = sharedPreferences.getString(dimension.name(), "");
        return strValue;
    }

    public void setStrConfig(SysCfgDimension dimension, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dimension.name(), value);
        //�ύ��ǰ����
        editor.commit();
    }

    public int getIntConfig(SysCfgDimension dimension) {
        int strValue = sharedPreferences.getInt(dimension.name(), 0);
        return strValue;
    }

    public void setIntConfig(SysCfgDimension dimension, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(dimension.name(), value);
        //�ύ��ǰ����
        editor.commit();
    }

    public int getIntConfig(String key) {
        int strValue = sharedPreferences.getInt(key, 0);
        return strValue;
    }

    public void setIntConfig(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        //�ύ��ǰ����
        editor.commit();
    }

    public enum SysCfgDimension {
        LOGINSERVERIP,
        LOGINSERVERPORT,
        REGISTERSERVERIP,
        REGISTERSERVERPORT
    }
}
