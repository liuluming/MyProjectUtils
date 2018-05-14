package com.my51c.see51.app.http;

import java.io.File;

public class XTHttpUtil {

    //用于http通信，服务器无响应时的错误码宏定义：
    public final static String NORESPONES = "-100";
    // 总的
//	public final static String XTADDRESS = "http://172.28.1.2:8005/api";// http請求地址
    public final static String XTADDRESS = "http://192.168.80.1:8005/api";// http請求地址
    /* 登录 */
    /*public final static String POST_LOGIN_ADDRESS = XTADDRESS + File.separator
			+ "login";*/
    public final static String POST_LOGIN_ADDRESS = XTADDRESS + "/login";
    /*注册*/
    public final static String POST_RESIGER_ADDRESS = XTADDRESS + File.separator
            + "register";
    /* 忘记密码*/
    public final static String POST_FORGETPASS_ADDRESS = XTADDRESS + File.separator
            + "forgetpass";
    /* 修改密码*/
    public final static String POST_MODIFYPASS_ADDRESS = XTADDRESS + "/modpasswd";
    /*高级账号登录*/
    public final static String POST_GAOJIACCOUNT_ADDRESS = XTADDRESS + File.separator + "moreadv" + File.separator + "userm" + File.separator
            + "login";
    /* 网络设置 */
    public final static String GET_NETSET_STATUS = XTADDRESS + "/netset/status";
    // 3.2.2.1  使能卫星链路
    public final static String POST_NETSET_SATEENABLE = XTADDRESS + "/netset/sate/enable";
    // 3.2.2.2 由卫星编号捕获卫星
    public final static String POST_NETSET_SATECATCH = XTADDRESS + "/netset/sate/catch";
    //  3.2.2.3  查看对星状态
    public final static String GET_NETSET_SATESTU = XTADDRESS + "/netset/sate/status";
    // 3.2.3.1  使能移动网络链路
    public final static String POST_NETSET_SIMENABLE = XTADDRESS + "/netset/sim/enable";
    // 3.2.2.2   选择某运营商的
    public final static String POST_NETSET_SIMSEL = XTADDRESS + "/netset/sim/select";
    //  3.2.2.3  查看运营商状态
    public final static String GET_NETSET_SIMQUERY = XTADDRESS + "/netset/sim/query";
    public final static String GET_NETSET_SIM_QUERY = XTADDRESS + "/netset/sim/query";
    // 3.2.4.1  使能wifi桥接功能
    public final static String POST_NETSET_WIFIENABLE = XTADDRESS + "/netset/wifi/enable";
    // 3.2.4.2  选择要桥接到的wifi
    public final static String POST_NETSET_WIFICONNECT = XTADDRESS + "/netset/wifi/connect";
    //  3.2.4.3  搜索到的所有wifi信号
    public final static String GET_NETSET_WIFIQUERY = XTADDRESS + "/netset/wifi/query";
    // 带宽资费选择
    public final static String POST_NETSET_POLICYSLC = XTADDRESS
            + "/netset/policyslc";
    /* 网络优化 */
    // 带宽管理 查询
    public final static String GET_NETOPT_BWMANGER_QUERY = XTADDRESS
            + "/netopt/bwmanger/query";
    // 带宽管理 查询
    public final static String POST_NETOPT_BWMANGER_SET = XTADDRESS
            + "/netopt/bwmanger/set";
    // Qos基本信息查询
    public final static String GET_BWDIY_BASIC_QUERY = XTADDRESS
            + "/netopt/bwdiy/basic/query";
    // Qos基本信息设置
    public final static String POST_BWDIY_BASIC_SET = XTADDRESS
            + "/netopt/bwdiy/basic/set";
    //查找全部IP
    public final static String GET_AllIP_QUERY = XTADDRESS + "/netopt/bwdiy/allip/query";
    /* 设备状态 */
    // 查看卫星状态
    public final static String GET_DEVSTATU_SATESATUS = XTADDRESS
            + "/devstatu/satesatus";
    // 版本、内存、硬盘
    public final static String GET_DEVSTATU_CORESTATUS_ONE = XTADDRESS
            + "/devstatu/corestatus1";
    // 查看防火墙信息
    public final static String GET_DEVSTATU_CORESTATUS_FIRE = XTADDRESS
            + "/devstatu/corestatus3";
    // 查看系统日志
    public final static String GET_DEVSTATU_CORESTATUS_SYSDAILY = XTADDRESS
            + "/devstatu/corestatus2";
    // 路由列表
    public final static String GET_DEVSTATU_CORESTATUS_ROUTLIST = XTADDRESS
            + "/devstatu/corestatus4";
    //高级登陆
    public final static String MUST_LOGIN_ADDRESS = XTADDRESS + "/moreadv/userm/login";
    //保存总按钮
    public final static String BWSET_ADV_START = XTADDRESS + "/moreadv/linkstatus/bwset/adv/start";
    /*
     * 更多高级
     */
    // 卫星设置
    public final static String MOREADV = XTADDRESS + "/moreadv/sate";
    // 一键对星查看状态
    public final static String GET_ONESTAR_STATE_ADDRESS = XTADDRESS
            + "/moreadv/sate/onestar/query";
    // 一键对星切星
    public final static String POST_ONESTAR_CHANGE_ADDRESS = XTADDRESS
            + "/moreadv/sate/onestar/catch";
    // 设置为默认值
    public final static String POST_ONESTAR_DEFAULT_ADDRESS = XTADDRESS
            + "/moreadv/sate/onestar/default";
    // 卫星参数设置-通常
    public final static String POST_ARGSET_GENERAL = XTADDRESS
            + "/moreadv/sate/argset/general";
    // 查询天线类型
    public final static String GET_TIAN_STYLE = XTADDRESS
            + "/odu/type";
    // 查询鉴权状态
    public final static String QUERY_STATUS = XTADDRESS
            + "/token/query";
    // 卫星参数设置-高级
    public final static String POST_ARGSET_ADV = XTADDRESS
            + "/moreadv/sate/argset/adv";
    // 调试控制-查看仰角、水平角
    public final static String GET_DEBUG_QUERY = XTADDRESS
            + "/moreadv/sate/debug/query";
    // 调整水平角
    public final static String POST_DEBUG_ZAISTART = XTADDRESS
            + "/moreadv/sate/debug/azistart";
    // 调整仰角
    public final static String POST_DEBUG_ELEVSTART = XTADDRESS
            + "/moreadv/sate/debug/elevstart";
    //天线停止
    public final static String GET_STOP = XTADDRESS
            + "/moreadv/sate/debug/stop";
    //天线重启
    public final static String GET_RESTART = XTADDRESS
            + "/moreadv/odu/restart";
    //路由板重启
    public final static String GET_REBOOT = XTADDRESS
            + "/moreadv/route/reboot";
    //电子罗盘初始化
    public final static String GET_INIT_COMPASS = XTADDRESS
            + "/moreadv/compass/init";
    //电子罗盘初始化结果查询
    public final static String QUERY_INIT_COMPASS_RESULT = XTADDRESS
            + "/moreadv/compass/init/query";
    //一键校准电子罗盘
    public final static String GET_COMPASS = XTADDRESS
            + "/moreadv/compass/calibration";
    //校准电子罗盘结果查询
    public final static String QUERY_COMPASS_RESULT = XTADDRESS
            + "/moreadv/compass/calibration/query";
    //查看modem板web服务器ip
    public final static String GET_MODEM_IP = XTADDRESS
            + "/modem/webserver/ip";
    //根据卫星参数捕获卫星
   /* public final static String POST_SATE_CATCH = XTADDRESS
					+ "/moreadv/sate/allarg/catch";*/
    public final static String POST_SATE_CATCH = XTADDRESS
            + "/moreadv/sate/start";
    //查询横滚角
    public final static String QUERY_HENG_GUN = XTADDRESS
            + "/moreadv/odu/roll/query";
    //设置横滚角
    public final static String SET_HENG_GUN = XTADDRESS
            + "/moreadv/odu/roll/set";
    // 根据卫星编号，查看卫星参数
    public final static String GET_SATENUM_ARG = XTADDRESS + "/moreadv/sate/arg/query";
    // 查看卫星参数--高级
    public final static String GET_SATEADV_ARG = XTADDRESS + "/moreadv/sate/advarg/query";
    //停止对星
    public final static String GET_SATE_STOP = XTADDRESS + "/moreadv/sate/debug/stop";
    /* 链路状态-链路状态 */
    public final static String LINKSTATUS = XTADDRESS + "/moreadv/linkstatus";
    // 链路状态-查看链路状态
    public final static String GET_LINKSTATUS_STATE = XTADDRESS
            + "/moreadv/linkstatus/linkstatus";
    // 链路状态-路由设置 获取LAN口状态
    public final static String GET_ROUTESET_LAN_QUERT = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/lan/query";
    // 设置LAN
    public final static String POST_ROUTESET_LAN_SET = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/lan/set";
    // 查看WAN口状态
    public final static String PSOT_ROUTESET_WAN_QUERY = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/wan/query";
    // WAN设置
    public final static String POST_ROUTESET_WAN_SET = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/wan/set";
    // 网络设置-wifi查看
    public final static String GET_ROUTESET_WF_QUERY = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/wifi/query";
    // 网络设置-wifi设置
    public final static String POST_ROUTESET_WIFI_SET = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/wifi/set";
    // 网络设置-5g桥接查询
    public final static String GET_ROUTESET_Bridging_Select = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/wifi/5gap/query";
    // 网络设置-5g桥接连接
    public final static String POST_ROUTESET_Bridging_Connect = XTADDRESS
            + "/moreadv/linkstatus/routeset/netset/wifi/5gap/link";
    // 附 录 B路由设置-web认证 查看
    public final static String GET_ROUTESET_WEB_QUERY = XTADDRESS
            + "/moreadv/linkstatus/routeset/web/query";
    // 附 录 B路由设置-web认证 设置
    public final static String POST_ROUTESET_WEB_SET = XTADDRESS
            + "/moreadv/linkstatus/routeset/web/set";
    // 规则添加
    public final static String POST_BWSET_AVD_RULE_ADD = XTADDRESS
            + "/moreadvnkstatus/bwset/adv/rule/add";
    /* 3.5.2.3 链路状态-带宽叠加 */
    // 附 录 A 简易设置 查看
    public final static String GET_BWSET_EASY_QUERY = XTADDRESS
            + "/moreadv/linkstatus/bwset/easy/query";
    // 附 录 A 简易设置 设置
    public final static String POST_BWSET_EASY_SET = XTADDRESS
            + "/moreadv/linkstatus/bwset/easy/set";
    /* 附录B 高级设置 */
    // 接口查询
    public final static String GET_BWSET_ADV_INTFER_QUERY = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/intfer/query";
    // 接口修改
    public final static String POST_BWSET_AVD_INTFER_MODI = LINKSTATUS
            + "/bwset/adv/intfer/modi";
    // 添加接口
    public final static String POST_BWSET_AVD_INTFER_ADD = LINKSTATUS
            + "/bwset/adv/intfer/add";
    // 添加接口ip
    public final static String POST_BWSET_AVD_INTFER_ADDIP = LINKSTATUS
            + "/bwset/adv/intfer/addip";
    // 删除某个接口
    public final static String POST_BWSET_AVD_INTFER_DEL = LINKSTATUS
            + "/bwset/adv/intfer/del";
    // 删除某个接口中的某个ip
    public final static String POST_BWSET_AVD_INTFER_DELIP = LINKSTATUS
            + "/bwset/adv/intfer/delip";
    // 修改某个接口中的某个ip
    public final static String POST_BWSET_AVD_INTFER_MODIIP = LINKSTATUS
            + "/bwset/adv/intfer/modiip";
    // 成员
    public final static String GET_BWSET_AVD_MEMBER_QUERY = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/member/query";
    // 策略
    public final static String GET_BWSET_AVD_POLICY_QUERY = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/policy/query";
    //删除某个策略一个Item
    public final static String GET_BWSET_AVD_POLICY_DEL = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/policy/del";
    //删除某个成员
    public final static String POST_BWSET_AVD_POLICY_DELMEM = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/policy/delmem";
    //修改某个策略
    public final static String POST_BWSET_AVD_POLICY_MODI = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/policy/modi";
    //添加一个策略
    public final static String POST_BWSET_AVD_POLICY_ADD = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/policy/add";
    // 规则
    public final static String GET_BWSET_AVD_RULE_QUREY = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/rule/query";
    //规则删除
    public final static String POST_BWSET_AVD_RULE_DEL = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/rule/del";
    //规则修改
    public final static String POST_BWSET_AVD_RULE_MODI = XTADDRESS
            + "/moreadv/linkstatus/bwset/adv/rule/modi";
    // 配置下发
    public final static String GET_ONESTART_SETDOWN_ONE = "http://192.168.0.2:80"
            + "/ConfP.htm?ID=2?0?0?119.5?E?-90.0?124?1?15809?0.0?192.168.0.2?255.255.255.0?192.168.0.1?255?0?0?3?0?0?0?3?0?1?1?0?Shenzhen?105.5?E?0.0?3?952000?3290?3?1?0?1335000?26500?1061.0?22.0?3?3?0?3?160?3?55?0?0?1?27.3?12.0?5?0?0?0?45.41?N?75.61?W?30?0?0?0?0?1?1?90.0?0.7?2?0.0?0?0?8?0?3?990.0?1?1210.99?22.0?30000000?192.168.0.4?0?1?0?0?0?1?1?0?282.8?12.0?3?0? HTTP/1.1";
    public final static String GET_ONESTART_SETDOWN_THREE = "http://192.168.0.2:80"
            + "/ConfP.htm?ID=2?0?3?105.5?E?0.0?124?1?15809?0.0?192.168.0.2?255.255.255.0?192.168.0.1?255?0?2?2?0?0?0?3?0?1?1?0?Shenzhen?105.5?E?0.0?0?2035000?26500?3?3?0?2035000?26500?1061.0?22.0?3?3?1?3?160?3?55?0?0?1?28.1?12.0?5?0?0?0?45.41?N?75.61?W?30?0?0?0?0?1?1?90.0?0.7?2?0.0?0?0?4?0?16?990.0?1?1210.99?22.0?30000000?192.168.0.4?0?1?0?0?0?1?1?0?282.8?12.0?3?0?comtech HTTP/1.1";
    public final static String GET_ONESTART_SETDOWN_FIVE = "http://192.168.0.2:80"
            + "/ConfP.htm?ID=2?0?4?110.5?E?90.0?124?1?15809?0.0?192.168.0.2?255.255.255.0?192.168.0.1?255?0?2?2?1?1?0?3?0?1?1?0?Shenzhen?110.5?E?90.0?3?1900000?43200?0?1?3?1900000?43200?1061.0?22.0?0?1?1?3?160?3?55?0?0?0?28.1?12.0?5?0?0?0?45.41?N?75.61?W?30?0?0?0?0?1?1?90.0?0.7?2?0.0?0?0?4?0?16?990.0?1?1214.99?22.4?30000000?192.168.0.4?0?1?0?0?0?1?1?0?282.8?12.0?3?0?comtech HTTP/1.1";
    // 一键对星
    public final static String GET_ONESTART_ONE = "http://192.168.0.2:80"
            + "/CtrlP.htm?ID=I HTTP/1.1";
    public final static String GET_ONESTART_THREE = "http://192.168.0.2:80"
            + "/CtrlP.htm?ID=L HTTP/1.1";
    public final static String GET_ONESTART_FIVE = "http://192.168.0.2:80"
            + "/CtrlP.htm?ID=M HTTP/1.1";
    // 停止
    public final static String GET_ONESTART_STOP = "http://192.168.0.2:80"
            + "/CtrlP.htm?ID=C HTTP/1.1";
    // 收星
    public final static String GET_ONESTART_STOW = "http://192.168.0.2:80"
            + "/CtrlP.htm?ID=D HTTP/1.1";
    public final static String GET_XT = "http://192.168.0.2:80"
            + "/CtrlP.htm?ID=0 HTTP/1.1";
    /*指定ip限速-查看*/
    public final static String GET_IPLIMIT_QUERY = XTADDRESS + "/netopt/bwdiy/iplimit/query";
    //ip添加
    public final static String POST_IPLIMIT_ADD = XTADDRESS + "/netopt/bwdiy/iplimit/add";
    //ip修改
    public final static String POST_IPLIMIT_MODIFY = XTADDRESS + "/netopt/bwdiy/iplimit/modi";//新增   目前没有实现  20160714  by  hyw
    //ip删除
    public final static String POST_IPLIMIT_DEL = XTADDRESS + "/netopt/bwdiy/iplimit/del";
    //查看所有名单
    public final static String GET_WHILE_ALLLIST = XTADDRESS + "/netopt/bwdiy/allip/query";
    //查看白名单
    public final static String GET_WHILE_LIST = XTADDRESS + "/netopt/bwdiy/superip/query";
    //添加指定的白名单
    public final static String POST_WHILE_LIST = XTADDRESS + "/netopt/bwdiy/super/add";  ///  ADD
    //修改指定的白名单
    public final static String POST_WHILE_LIST_MODIFY = XTADDRESS + "/netopt/bwdiy/super/modi";//新增，目前还没有实现20160714
    //删除白名单
    public final static String POST_WHILE_DEL = XTADDRESS + "/netopt/bwdiy/super/del";
    //查看白名单
    public final static String GET_BLACK_LIST = XTADDRESS + "/netopt/bwdiy/blackip/query";
    //添加指定的白名单
    public final static String POST_BLACK_ADD = XTADDRESS + "/netopt/bwdiy/super/add";
    //修改指定的白名单
    public final static String POST_BLACK_MODIFY = XTADDRESS + "/netopt/bwdiy/super/modi";//新增   目前还没有实现  20160714
    //删除白名单
    public final static String POST_BALCK_DEL = XTADDRESS + "/netopt/bwdiy/super/del";
    //密码修改
    public final static String POST_MOREADV_MODIPASS = XTADDRESS + "/moreadv/userm/modipass";
    //个人中心修改密码
    public final static String POST_PERSON_REVICE_PASSWORD = XTADDRESS + "/people/modipass";
    //查看最新版本
    public final static String GET_PEOPLE_UPSYS_CHECK = XTADDRESS + "/people/upsys/check";
    //开始升级
    public final static String GET_PEOPLE_UPSYS_START = XTADDRESS + "/people/upsys/start";
    //高级设置查询升级情况
    public final static String GET_MOREAD_SYSUP_STATUS = XTADDRESS + "/moreadv/sysup/status";
    // 定位接口
    public final static String GET_POSITION = XTADDRESS + "/position";
    //成员修改
    public final static String POST_BWSET_AVD_MEMBER_MODI = XTADDRESS + "/moreadv/linkstatus/bwset/adv/member/modi";
    //成员添加
    public final static String POST_BWSET_AVD_MEMBER_ADD = XTADDRESS + "/moreadv/linkstatus/bwset/adv/member/add";
    //成员删除
    public final static String POST_BWSET_AVD_MEMBER_DEL = XTADDRESS + "/moreadv/linkstatus/bwset/adv/member/del";
    private final static String TAG = "XTHttpUtil";
    public static String devstatu = XTADDRESS + "/devstatu/odu/realtime";

    public static String devstatuAlarm = XTADDRESS + "/devstatu/odu/alarm";
    //聚合链路列表查询
    public static String AggregatedLinkList=XTADDRESS+"/netset/AggregatedLink/list";
    //聚合链路查询
    public static String AggregatedLinkInfo=XTADDRESS+"/netset/AggregatedLink/info";
    //网络适配器列表查询
    public static String NetworkAdapterList=XTADDRESS+"/netset/networkAdapter/list";
    //网络适配器查询
    public static String NetworkAdapterInfo=XTADDRESS+"/netset/networkAdapter/info";
    //查询网络通道信息
    public static String NetworkChannel=XTADDRESS+"/netset/networkChannel/info";
    //查询网络通道信息列表
    public static String NetworkChannelList=XTADDRESS+"/netset/networkChannel/list";
    //聚合链路设置
    public static String AggregatedLinkConfig=XTADDRESS+"/netset/AggregatedLink/config";
    //获取网络适配器状态信息
    public static String NetworkAdapterStatus=XTADDRESS+"/netset/networkAdapter/status";
    //业务配置
    public static String SetServiceConfig=XTADDRESS+"/netset/ServiceSet/config";
    //设置网络通道信息
    public static String SetChannelConfig=XTADDRESS+"/netset/networkChannel/config";
    //查询业务配置信息列表
    public static String QueryServiceConfigList=XTADDRESS+"/netset/ServiceSet/list";
    //查询业务配置信息
    public static String QueryServiceConfig=XTADDRESS+"/netset/ServiceSet/info";
    //应用配置
    public static String AppConfig=XTADDRESS+"/netset/Service/apply";
    //获取未被占用的interface
    public static String InterfaceList=XTADDRESS+"/netset/interface/list";
    //设置网络适配器
    public static String NetworkAdapterConfig=XTADDRESS+"/netset/networkAdapter/config";
    //网络适配器操作接口
    public static String NetworkAdapterOperation=XTADDRESS+"/netset/networkAdapter/operation";
    //删除网络适配器
    public static String NetworkAdapterDelete=XTADDRESS+"/netset/networkAdapter/delete";
    //删除链路聚合
    public static String AggregatedLinkDelete=XTADDRESS+"/netset/AggregatedLink/delete";
    //删除网络通道
    public static String delNetChannel=XTADDRESS+"/netset/networkChannel/delete";//删除网络//删除业务
    public static String delService=XTADDRESS+"/netset/ServiceSet/delete";


}
