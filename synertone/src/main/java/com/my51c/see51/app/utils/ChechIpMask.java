package com.my51c.see51.app.utils;

import android.content.Context;
import android.widget.Toast;

import com.my51c.see51.yzxvoip.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import io.vov.vitamio.utils.Log;

public class ChechIpMask {
    private static final String TAG = "CheckIpMask---------->>>>>";
    static boolean isView = false;//来判断是否关闭吐司，

    /* 判断IP地址是否合法 */
    /* IP地址转换为二进制字符串 */
	/* 例如：172.16.4.235 --> 10101100000100000000010011101011 */
    public static boolean judgeIpIsLegal(String ipAddr) {

        String regIps = "^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$";
        Pattern pattern = Pattern.compile(regIps);
        if (!pattern.matcher(ipAddr).find()) {
            Log.e(TAG, "输入的IP有误");
            return false;
        } else {
            Log.e(TAG, "输入的IP正确！！");
            return true;
        }
    }

    /* 判断子网掩码是否合法 */
	/* 子网掩码必须是 1 和 0组成的连续的一段 如 11110000 */
    public static boolean judgeSubnetMask(String ipAddress) {
        if (judgeIpIsLegal(ipAddress)) {
            String binaryIpString = ipAddress;
            int subIndex = binaryIpString.lastIndexOf("1") + 1;
            String frontHalf = binaryIpString.substring(0, subIndex);
            String backHalf = binaryIpString.substring(subIndex);
            if (frontHalf.indexOf("0") != -1 || backHalf.indexOf("1") != -1) {
                Log.e(TAG, "子网掩码有误！！");
                return false;
            } else {
                Log.e(TAG, "子网掩码正确！！");
                return true;
            }
        } else {
            Log.e(TAG, "子网掩码有误,不是有效IP！！");
            return false;
        }
    }

    /* 判断网关地址是否合法 */
    public static boolean judgeGatewayResult(String ipAddr, String subnetMask, String gateway) {
        String andResult1 = getIPsAndResult(ipAddr, subnetMask);
        String andResult2 = getIPsAndResult(gateway, subnetMask);
        if (andResult1.equals(andResult2)) {
            Log.e(TAG, "网关正确！！");
            return true;
        } else {
            Log.e(TAG, "网关有误！！");
            return false;
        }
    }

    /*//int 转为  binary*/
    public static long praseIpToBinary(String ipAddress) {
        String[] numArray = ipAddress.split(".");
        long result = 0;
        if (numArray.length != 4) {
            Log.e(TAG, "IP转二进制有误！！");
            return result = -1;
        } else {
            for (int i = 0; i < numArray.length; i++) {
                int power = 3 - i;
                int ip = Integer.parseInt(numArray[i]);
                result += ip * Math.pow(256, power);
            }
            Log.e(TAG, "IP转二进制正确！！");
        }
        return result;
    }
    //看字符串是否能转化为数字,并判断取值范围，从a到b,使用于各种有效数字，

    /* 两个IP地址做 与 操作 返回结果 */
	/* 该功能主要用来实现 IP地址和子网掩码 相与，获取当前IP地址的IP地址段 */
	/* 以此来验证输入的网关地址是否合法 */
    public static String getIPsAndResult(String ipAddr1, String ipAddr2) {
        String[] ipArray1 = ipAddr1.split(".", -1);
		/*for (int i = 0; i < ipArray1.length; i++) {
			if(ipArray1[i]!=null){
			}
		}*/
        String[] ipArray2 = ipAddr2.split(".", -1);
        String returnResult = "";
        if (ipArray1.length != 4 || ipArray2.length != 4) {
            //Toast.makeText(this, "输入IP有误！", Toast.LENGTH_LONG);
            Log.e(TAG, "输入至少一个IP有误！网关不合法-->！");
            return "网关不合法-->！";//
        }
        for (int i = 0; i < 4; i++) {
            int num1 = Integer.parseInt(ipArray1[i]);
            int num2 = Integer.parseInt(ipArray2[i]);
            returnResult += num1 & num2;
            if (i < 3) {
                returnResult += ".";
            }
        }
        return returnResult;
    }

    /**
     * @param data_re
     * @param a       开始
     * @param b       结束
     * @param c       几位有效数字
     * @return
     */
    public static boolean isDigite(String data_re, int a, int b, int c) {
        int d = 1;
        int aa;
        for (int i = 0; i < c; i++) {
            d = d * 10;
        }
        try {  //判断一个数字的正负
            /// double num=Math.abs(Double.valueOf(data_re));//把字符串强制转换为数字
            double num = Double.valueOf(data_re);//把字符串强制转换为数字
            //是数字了，下面判断是否为给定位数的小数
            return (int) (num * d) >= a * d && (int) (num * d) <= b * d;
        } catch (Exception e) {
            return false;//如果抛出异常，返回False
        }
    }
    //

    /**
     * 前提是已经是数字了，现在是从数字里面截取几位小数
     * 截取几位小数
     *
     * @return
     */

    public static String numDigite(String _data, int scale) {
        if(StringUtils.isEmpty(_data)){
            return "";
        }
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        //第一，判断是否有小数点，
        if (_data.substring(0, 1).equals("-") || _data.substring(0, 1).equals("﹣") || _data.substring(0, 1).equals("－") || _data.substring(0, 1).equals("﹣")) {
            _data = "-" + _data.substring(1);
        }
        BigDecimal value = new BigDecimal(_data);
        double zemval = value.setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
			/*double d =40.0000;
			DecimalFormat df=new DecimalFormat("###.00");
			System.out.println(df.format(d));*/
        return String.valueOf(zemval);
    }
    /** */

    /**
     * 前提是已经是数字了，现在是从数字里面截取几位小数
     * 截取几位小数
     *
     * @return
     */

    public static String numDigite2(String _data, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        //第一，判断是否有小数点，
        if (_data == null || "".equals(_data)) {
            return null;
        } else {
            if (_data.substring(0, 1).equals("-") || _data.substring(0, 1).equals("﹣") || _data.substring(0, 1).equals("－") || _data.substring(0, 1).equals("﹣")) {
                _data = "-" + _data.substring(1);
            }
            BigDecimal value = new BigDecimal(_data);
            double zemval = value.setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
			/*double d =40.0000;
			DecimalFormat df=new DecimalFormat("###.00");
			System.out.println(df.format(d));*/
            return String.valueOf(zemval);
        }

    }

    /**
     * 提供精确的小数位
     *
     * @param v
     * @param scale 小数点后保留几位
     * @return
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    //判断取值范围为正负对称的数。
    public static boolean abs(String data_re, int max) {
        if (isNumeric(data_re)) {
            if (data_re.substring(0, 1).equals("-") || data_re.substring(0, 1).equals("﹣") || data_re.substring(0, 1).equals("－") || data_re.substring(0, 1).equals("﹣")) {
                data_re = data_re.substring(1);
            }
            double num = Math.abs(Double.valueOf(data_re));//把字符串强制转换为数字
            // double num=Double.valueOf(data_re);//把字符串强制转换为数字
            //是数字了，下面判断是否为给定位数的小数
            return num <= max;
        } else {
            return false;
        }
    }

    //判断正数的取值范围在一定范围的数。
    public static boolean a2b(String data_re, int min, int max) {
        if (isNumeric(data_re)) {
            if (data_re.substring(0, 1).equals("-") || data_re.substring(0, 1).equals("﹣") || data_re.substring(0, 1).equals("－") || data_re.substring(0, 1).equals("﹣")) {
                data_re = data_re.substring(1);
            }
            double num = Double.valueOf(data_re);
            return min <= num && num <= max;
        } else {
            return false;
        }
    }

    //判断任意数的取值范围在一定范围的数。
    public static boolean a2d(String data_re, int min, int max) {
        if (isNumeric(data_re)) {
            //if("-".equals(data_re.substring(0,1))||"﹣".equals(data_re.substring(0,1))||"－".equals(data_re.substring(0,1))||"﹣".equals(data_re.substring(0,1))){
            if (data_re.substring(0, 1).equals("-") || data_re.substring(0, 1).equals("﹣") || data_re.substring(0, 1).equals("－") || data_re.substring(0, 1).equals("﹣")) {
                data_re = data_re.substring(0);
            }
            double num = Double.valueOf(data_re);
            return min <= num && num <= max;
        } else {
            return false;
        }
    }

    public static boolean dotCount(String str) {
        char c = '.';
        int num = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (c == chars[i]) {
                num++;
            }
        }
        return num < 2;


    }

    //判断是否为一个数字。
    public static boolean isNumeric(String str) {

        if ("".equals(str)) {
            return false;
        }
        if (str.substring(0, 1).equals("-") || str.substring(0, 1).equals("﹣") || str.substring(0, 1).equals("－") || str.substring(0, 1).equals("﹣")) {
            str = str.substring(1);
        }
        if (str != null && !"".equals(str)) {
            if (!dotCount(str)) {
                return false;
            }
            if (str.contains(".")) {
                int dotLoc = str.indexOf(".");
                if ((str.length() - 1) == dotLoc) {
                    return false;
                }
                if (dotLoc != 0) {
                    str = str.substring(0, dotLoc);
                } else {
                    return false;
                }
            }

            for (int j = 0; j < str.length(); j++) {
                System.out.println(str.charAt(j));
                if (!Character.isDigit(str.charAt(j))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    public static void Toast_alert(Context context, String alert) {
        Toast.makeText(context, alert + "", 1000).show();
    }

    public static void Toast_alert(Context context, CharSequence alertKey, String value, int showTime) {
        if (isView) {
            Toast.makeText(context, alertKey + "" + value, showTime).show();
        }
    }

    public static void Toast_alert(Context context, CharSequence alertKey, int res, int showTime) {
        if (isView) {
            Toast.makeText(context, alertKey + "" + res, showTime).show();
        }
    }
}
