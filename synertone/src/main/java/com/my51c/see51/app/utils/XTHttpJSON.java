package com.my51c.see51.app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.DecimalFormat;

public class XTHttpJSON {

    private final static String TAG = "XTHttpJSON";
    public static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    /* 用户登录 */
    public static JSONObject login(String userName, String passWork) {
        JSONObject param = new JSONObject();
        // List<Object> list = new ArrayList<Object>();
        try {
            param.put("user", userName);
            param.put("passwd", passWork);

            // list.add("TAG=");
            // list.add(param.toString());
            Log.i(TAG, "用户登录--->" + param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    /*忘记密码*/
    public static JSONObject forgetpassword(String userName, String phone) {
        JSONObject param = new JSONObject();
        // List<Object> list = new ArrayList<Object>();
        try {
            param.put("user", userName);
            param.put("phone", phone);

            // list.add("TAG=");
            // list.add(param.toString());
            Log.i(TAG, "忘记密码--->" + param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    /*高级用户登录*/
    public static JSONObject gaojiAccount(String password) {
        JSONObject param = new JSONObject();
        // List<Object> list = new ArrayList<Object>();
        try {

            param.put("password", password);

            // list.add("TAG=");
            // list.add(param.toString());
            Log.i(TAG, "高级账户--->" + param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

	

	/*
	 * 提交上去的数据 获取到json数据的结果码 判断连接是否成功 public static String
	 * getResultJSONString(String result) { String code = null; try { JSONObject
	 * object = new JSONObject(result); JSONObject jsonObject =
	 * object.getJSONObject("data"); code = jsonObject.getString("code"); }
	 * catch (Exception e) { e.printStackTrace(); } return code; }
	 */

    /* 高级登陆 */
    public static JSONObject loginMuch(String passWork) {
        JSONObject param = new JSONObject();
        // List<Object> list = new ArrayList<Object>();
        try {
            param.put("password", passWork);

            // list.add("TAG=");
            // list.add(param.toString());
            Log.i(TAG, "用户登录--->" + param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    /* 直接访问 获取到json数据的结果码 判断连接是否成功 */
    public static String getJSONString(String result) {
        String code = null;
        try {
            JSONObject object = new JSONObject(result);
            code = object.getString("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /* 高级 --》参数设置 -->通常 */
    public static JSONObject postRefGeneral(String satenum, String satelon,
                                            String mode, String freq, int bw, String type, String modem,
                                            String rssi, String recvpol, String sendpol) {
        JSONObject refGeneral = new JSONObject();
        try {
            refGeneral.put("satenum", satenum);
            refGeneral.put("satelon", satelon);
            refGeneral.put("mode", mode);
            refGeneral.put("freq", freq);
            refGeneral.put("bw", bw);
            refGeneral.put("type", type);
            refGeneral.put("modem", modem);
            refGeneral.put("rssi", rssi);
            refGeneral.put("recvpol", recvpol);
            refGeneral.put("sendpol", sendpol);
            Log.i(TAG, "高级里面的参数设置 普通====》" + refGeneral.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refGeneral;
    }

    /* 高级 --》参数设置 -->高级 */
    public static JSONObject postRefGeneralAdv(String locatype, String curlon,
                                               String currlat) {
        JSONObject refGeneralAdv = new JSONObject();
        try {
            refGeneralAdv.put("locatype", locatype);
            refGeneralAdv.put("curlon", curlon);
            refGeneralAdv.put("currlat", currlat);
            Log.i(TAG, "高级里面的参数设置  高级 ====》" + refGeneralAdv.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refGeneralAdv;
    }

    /* 调制解调 调整水平角 */
    public static JSONObject postAziStart(String zai, String desazai,
                                          Context context) {
        JSONObject aziStart = new JSONObject();
        try {
            aziStart.put("zai", zai);
            aziStart.put("desazi", desazai);
            Toast.makeText(context, aziStart.toString(), 0).show();
            Log.i(TAG, "高级设置====调制解调===水平角" + aziStart.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return aziStart;
    }

    /* 调制解调 调整仰角 */
    public static JSONObject postElevStart(String elev, String deselev,
                                           Context context) {
        JSONObject elevStart = new JSONObject();
        try {
            elevStart.put("elev", elev);
            elevStart.put("deselev", deselev);
            Toast.makeText(context, elevStart.toString(), 0).show();
            Log.i(TAG, "高级设置====调制解调===仰角" + elevStart.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return elevStart;
    }

    /* 一键对星切星 */
    public static JSONObject postOneStart(String satenum, String satelon,
                                          String mode, String freq, String bw, String type, String modem,
                                          String rssi, String recvpol, String sendpol, Context context) {
        JSONObject oneStart = new JSONObject();
        try {
            oneStart.put("satenum", satenum);
            oneStart.put("satelon", satelon);
            oneStart.put("mode", mode);
            oneStart.put("freq", freq);
            oneStart.put("bw", bw);
            oneStart.put("type", type);
            oneStart.put("modem", modem);
            oneStart.put("rssi", rssi);
            oneStart.put("recvpol", recvpol);
            oneStart.put("sendpol", sendpol);
            Log.i("=======", oneStart.toString() + "=================");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oneStart;
    }

    /* 设置LAN口 POST */
    public static JSONObject postLanSet(String enable, String type, String ip,
                                        String mask, String gw, String broadcast, String dhcp,
                                        String dhcpstart, String dhcpend, String renttime, Context context) {
        JSONObject lanSet = new JSONObject();
        try {
            lanSet.put("enable", enable);
            lanSet.put("type", type);
            lanSet.put("ip", ip);
            lanSet.put("mask", mask);
            lanSet.put("gw", gw);
            lanSet.put("broadcast", broadcast);
            lanSet.put("dhcp", dhcp);
            lanSet.put("dhcpstart", dhcpstart);
            lanSet.put("dhcpend", dhcpend);
            lanSet.put("renttime", renttime);
            Log.e(TAG, lanSet.toString());
            //	Toast.makeText(context, lanSet.toString(), 0).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lanSet;
    }

    // 宽带叠加 Post
    public static JSONObject postBwSet(String enable, int wansnum,
                                       String wannum, String wanenable, String wanssetNum,
                                       String wannsetEnable) {
        JSONObject bwSetJson = new JSONObject();
        JSONArray bwWanSetJson = new JSONArray();
        JSONObject bwSet = new JSONObject();
        try {
            for (int i = 0; i < wansnum; i++) {
                bwSetJson.put("wannum", wanssetNum);
                bwSetJson.put("enable", wannsetEnable);
                bwWanSetJson.put(bwSetJson);
            }
            // 总的
            bwSet.put("enable", enable);
            bwSet.put("wansnum", wansnum);
            bwSet.put("wansset", bwWanSetJson);
            bwSet.put("policy", wanenable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bwSetJson;
    }

    // webAutSet Post
    public static JSONObject postWebAut(String enBbleStr, int timeOutStr,
                                        int serNumStr, Context context) {
        JSONObject webAutoObject = new JSONObject();
        try {
            webAutoObject.put("enable", enBbleStr);
            webAutoObject.put("timeout", timeOutStr);
            webAutoObject.put("sernum", serNumStr);
            Toast.makeText(context, webAutoObject.toString(), 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return webAutoObject;
    }

    // 设置移动网post
    public static JSONObject postSimEnable(String enable, String sinnum) {
        JSONObject simEnableObject = new JSONObject();
        try {
            simEnableObject.put("enable", enable);
            simEnableObject.put("simnum", sinnum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return simEnableObject;
    }

    // 连接指定wifi
    public static JSONObject postWifiConnect(String ssid, String ssidpass) {
        JSONObject connectObject = new JSONObject();
        try {
            connectObject.put("ssid", ssid);
            connectObject.put("ssidpass", ssidpass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectObject;
    }

    // 带宽资费选择
    public static JSONObject postPolicyslc(String enable, String val) {
        JSONObject policyslcObject = new JSONObject();
        try {
            policyslcObject.put("enable", enable);
            policyslcObject.put("val", val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return policyslcObject;
    }

    // 网络优化宽带管理
    public static JSONObject postBwMangerSet(String bwSet) {
        JSONObject bwSetObject = new JSONObject();
        try {
            bwSetObject.put("bwset", bwSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bwSetObject;
    }

    // 设置qos基本信息
    public static JSONObject postQosBasicSet(String downtotal, String uptotal,
                                             String downprior, String upprior, String ipdownlimit,
                                             String ipuplimit, String pudownpri, String puuppri, String pudown,
                                             String puup, String btenable, String btdown, String btup) {
        JSONObject QosSetObject = new JSONObject();
        try {
            QosSetObject.put("downtotal", downtotal);
            QosSetObject.put("uptotal", uptotal);
            QosSetObject.put("downprior", downprior);
            QosSetObject.put("upprior", upprior);
            QosSetObject.put("ipdownlimit", ipdownlimit);
            QosSetObject.put("ipuplimit", ipuplimit);
            QosSetObject.put("pudownpri", pudownpri);
            QosSetObject.put("puuppri", puuppri);
            QosSetObject.put("pudown", pudown);
            QosSetObject.put("puup", puup);
            QosSetObject.put("btenable", btenable);
            QosSetObject.put("btdown", btdown);
            QosSetObject.put("btup", btup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return QosSetObject;
    }

    // 设置开启对星
    public static JSONObject postSateableSet(String enableStr,
                                             String satenumStr, String satelonStr, String modeStr,
                                             String frepStr, String bwStr, String typeStr, String modemStr,
                                             String rssiStr, String recvpolStr, String sendpolStr) {
        JSONObject psotSateObject = new JSONObject();
        try {
            psotSateObject.put("enable", enableStr);
            psotSateObject.put("satenum", satenumStr);
            psotSateObject.put("satelon", satelonStr);
            psotSateObject.put("mode", modeStr);
            psotSateObject.put("freq", frepStr);
            psotSateObject.put("bw", bwStr);
            psotSateObject.put("type", typeStr);
            psotSateObject.put("modem", modemStr);
            psotSateObject.put("rssi", rssiStr);
            psotSateObject.put("recvpol", recvpolStr);
            psotSateObject.put("sendpol", sendpolStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return psotSateObject;
    }

    public static JSONObject postIpSpeedSet(String enable, String ip,
                                            String downMax, String upMax, String prior, String user) {
        JSONObject iPSpeedobject = new JSONObject();
        try {
            iPSpeedobject.put("enable", enable);
            iPSpeedobject.put("ip", ip);
            iPSpeedobject.put("downmax", downMax);
            iPSpeedobject.put("upmax", upMax);
            iPSpeedobject.put("prior", prior);
            iPSpeedobject.put("user", user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iPSpeedobject;
    }

    // 白名单
    public static JSONObject postWhileSet(String enable, String ip, String mac,
                                          String user) {
        JSONObject iPSpeedobject = new JSONObject();
        try {
            iPSpeedobject.put("enable", enable);
            iPSpeedobject.put("ip", ip);
            iPSpeedobject.put("mac", mac);
            iPSpeedobject.put("user", user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iPSpeedobject;
    }

    // 黑名单
    public static JSONObject postBlcakSet(String enable, String ip, String mac) {
        JSONObject iPSpeedobject = new JSONObject();
        try {
            iPSpeedobject.put("enable", enable);
            iPSpeedobject.put("ip", ip);
            iPSpeedobject.put("mac", mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iPSpeedobject;
    }

    // 密码修改
    public static JSONObject postPassWork(String oldPass, String newPass) {
        JSONObject passObject = new JSONObject();
        try {
            passObject.put("oldpass", oldPass);
            passObject.put("newpass", newPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passObject;
    }

    // 个人中心密码修改
    public static JSONObject postPersonPassWork(String oldPass, String newPass) {
        JSONObject passObject = new JSONObject();
        try {
            passObject.put("oldpass", oldPass);
            passObject.put("newpass", newPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passObject;
    }

    public static JSONObject postWifiSet(String enable24, String tiem24,
                                         String type24, String ssid24, String passtype24, String passset24,
                                         String level24, String hide24, String channel24, String enable5,
                                         String tiem5, String type5, String ssid5, String passType5,
                                         String passset5, String level5, String hidel5, String channel5) {
        JSONObject wifiSetObject = new JSONObject();
        JSONObject wifiSet24G = new JSONObject();
        JSONObject wifiSet5G = new JSONObject();
        try {
            wifiSet24G.put("enable", enable24);
            wifiSet24G.put("time", tiem24);
            wifiSet24G.put("type", type24);
            wifiSet24G.put("ssid", ssid24);
            wifiSet24G.put("passtype", passtype24);
            wifiSet24G.put("passset", passset24);
            wifiSet24G.put("level", level24);
            wifiSet24G.put("hide", hide24);
            wifiSet24G.put("channel", channel24);

            wifiSet5G.put("enable", enable5);
            wifiSet5G.put("time", tiem5);
            wifiSet5G.put("type", type5);
            wifiSet5G.put("ssid", ssid5);
            wifiSet5G.put("passtype", passType5);
            wifiSet5G.put("passset", passset5);
            wifiSet5G.put("level", level5);
            wifiSet5G.put("hide", hidel5);
            wifiSet5G.put("channel", channel5);

            wifiSetObject.put("2g", wifiSet24G);
            wifiSetObject.put("5g", wifiSet5G);
            Log.i("245G的数据", wifiSetObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wifiSetObject;
    }

    // wan口设置里面的静态
    public static JSONObject postWanSetStatic(String wannum, String type,
                                              String enable, String staticIp, String staticMask, String staticGw,
                                              String staticDns1, String staticDns2) {
        JSONObject object = new JSONObject();
        JSONObject staticObject = new JSONObject();

        try {
            object.put("wannum", wannum);
            object.put("type", type);
            object.put("enable", enable);

            staticObject.put("ip", staticIp);
            staticObject.put("mask", staticMask);
            staticObject.put("gw", staticGw);
            staticObject.put("dns1", staticDns1);
            staticObject.put("dns2", staticDns2);

            object.put("static", staticObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    // wan设置里面Dhcp
    public static JSONObject postWanSetDhcp(String wanNum, String dhcpType,
                                            String dhcpEnable, String dhcpHostName) {
        JSONObject object = new JSONObject();
        try {
            object.put("wannum", wanNum);
            object.put("type", dhcpType);
            object.put("enable", dhcpEnable);
            object.put("hostname", dhcpHostName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    // wan口设置里面的PPPoe
    public static JSONObject postWanSetPPPoe(String wannum, String type,
                                             String enable, String papuser, String pappass, String access,
                                             String server) {
        JSONObject object = new JSONObject();
        JSONObject pppoeObject = new JSONObject();

        try {
            object.put("wannum", wannum);
            object.put("type", type);
            object.put("enable", enable);

            pppoeObject.put("papuser", papuser);
            pppoeObject.put("pappass", pappass);
            pppoeObject.put("access", access);
            pppoeObject.put("server", server);

            object.put("pppoe", pppoeObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    // wan口设置里面的umts
    public static JSONObject postWanSetUmts(String wannum, String type,
                                            String enable, String modp, String sertype, String apn, String pin,
                                            String papuser, String pappass) {
        JSONObject object = new JSONObject();
        JSONObject umtsObject = new JSONObject();

        try {
            object.put("wannum", wannum);
            object.put("type", type);
            object.put("enable", enable);

            umtsObject.put("modp", modp);
            umtsObject.put("sertype", sertype);
            umtsObject.put("apn", apn);
            umtsObject.put("pin", pin);
            umtsObject.put("papuser", papuser);
            umtsObject.put("pappass", pappass);
            object.put("umts", umtsObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    // 高级设置里面的接口
    public static JSONObject postInterfer(int enableStr, String nameStr,
                                          int ipssum, String ed01Str, String ed02Str, String ed03Str,
                                          String ed04Str, String ed05Str, String ipcountStr,
                                          String groupCountStr, String timeOutStr, String timeSpaceStr,
                                          String notNetNumStr, String netNumStr) {
        JSONObject object = new JSONObject();
        try {
            object.put("enable", enableStr);
            object.put("name", nameStr);
            object.put("ipssum", ipssum);
            if (!TextUtils.isEmpty(ed01Str)) {
                object.put("ip1", ed01Str);
                ipssum++;
            }
            if (!TextUtils.isEmpty(ed02Str)) {
                object.put("ip2", ed02Str);
                ipssum++;
            }
            if (!TextUtils.isEmpty(ed03Str)) {
                object.put("ip3", ed03Str);
                ipssum++;
            }
            if (!TextUtils.isEmpty(ed04Str)) {
                object.put("ip4", ed04Str);
                ipssum++;
            }
            if (!TextUtils.isEmpty(ed05Str)) {
                object.put("ip5", ed05Str);
                ipssum++;
            }
            object.put("ipsnum", ipcountStr);
            object.put("count", groupCountStr);
            object.put("timeout", timeOutStr);
            object.put("interval", timeSpaceStr);
            object.put("lost", notNetNumStr);
            object.put("connect", netNumStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String string2MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
