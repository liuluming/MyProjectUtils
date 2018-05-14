package com.my51c.see51.yzxvoip.model;

import com.yzx.api.UCSCall;

import java.util.HashMap;

public class UIDfineAction {

    public static final HashMap<Integer, String> dialState = new HashMap<Integer, String>();
    public static final String FROM_NUM_KEY = "fromsernum";
    public static final String TO_NUM_KEY = "tosernum";
    public static final String CALL_UID = "call_uid";
    public static final String CALL_PHONE = "call_phone";
    public static final String RESULT_KEY = "result";
    public static final String REASON_KEY = "reason";
    public static final int LOCATION = 10;        //�Զ�����Ϣ����  λ��
    public static final int FILE = 20;        //�Զ�����Ϣ����  ����
    public static String ACTION_LOGIN = "com.yzx.login";
    //	public static String ACTION_LOGOUT = "com.yzx.logout";
    public static String ACTION_TCP_LOGIN_RESPONSE = "com.yzx.tcp_login_response";
    //	public static String ACTION_TCP_LOGIN_CLIENT_RESPONSE = "com.yzx.tcp_login_client_response";
    public static String ACTION_SEND_FILE_PROGRESS = "com.yzx.send_file";
    public static String ACTION_DIAL = "com.yzx.dial";
    public static String ACTION_DIAL_STATE = "com.yzx.dial.state";
    public static String ACTION_DIAL_HANGUP = "com.yzx.dial.hangup";
    public static String ACTION_NETWORK_STATE = "com.yzx.network.state";
    public static String ACTION_ANSWER = "com.yzx.answer";
    public static String ACTION_CALL_TIME = "com.yzx.call_time";
    //	public static String ACTION_START_TIME = "com.yzx.start_time";
    //public static final String ACTION_STOP_TIME = "com.yzx.stop_time";
    public static String ACTION = "com.yzxproject.resetList";
    public static String ACTION_MSG = "com.yzxproject.end_failed";
    public static String ACTION_STATUS = "com.yzxproject.status";
    public static String ACTION_TEL_USER_INFO_DATA_UPDATE = "tel_user_info_data_update";
    public static String ACTION_TEL_LIST_DATA_UPDATE = "tel_list_data_update";
    public static String ACTION_NET_ERROR_KICKOUT = "net_error_kickout";

    static {
        //dialState.put(UCSCall.CALL_VOIP_ANSWER, "�����绰");
        dialState.put(UCSCall.CALL_VOIP_NOT_ENOUGH_BALANCE, "����");
        dialState.put(UCSCall.CALL_VOIP_BUSY, "�Է���æ");
        dialState.put(UCSCall.CALL_VOIP_REFUSAL, "�Է��ܾ�");
        dialState.put(UCSCall.CALL_VOIP_NUMBER_OFFLINE, "���к��벻����");
        dialState.put(UCSCall.CALL_VOIP_CALLID_NOT_EXIST, "����ID������");
        dialState.put(UCSCall.CALL_VOIP_NUMBER_WRONG, "���к������");
        dialState.put(UCSCall.CALL_VOIP_REJECT_ACCOUNT_FROZEN, "�����˻�������");
        dialState.put(UCSCall.CALL_VOIP_ACCOUNT_FROZEN, "�����˻�������");
        dialState.put(UCSCall.CALL_VOIP_ACCOUNT_EXPIRED, "�����˻�����");
        dialState.put(UCSCall.CALL_VOIP_CALLYOURSELF, "���ܲ����Լ��󶨺���");
        dialState.put(UCSCall.CALL_VOIP_NETWORK_TIMEOUT, "��������ʱ");
        dialState.put(UCSCall.CALL_VOIP_NOT_ANSWER, "�Է�����Ӧ��");
        dialState.put(UCSCall.CALL_VOIP_TRYING_183, "���в�����,תֱ��");
        dialState.put(UCSCall.CALL_VOIP_RINGING_180, "�Է���������");
        dialState.put(UCSCall.CALL_VOIP_SESSION_EXPIRATION, "��Ȩʧ��(TCPδ��֤)");
        dialState.put(UCSCall.CALL_VOIP_ERROR, "����������");
        dialState.put(UCSCall.HUNGUP_NOT_ANSWER, "���з�û��Ӧ��");
        dialState.put(UCSCall.HUNGUP_MYSELF, "�Լ��Ҷϵ绰");
        dialState.put(UCSCall.HUNGUP_OTHER, "�Է��ѹһ�");
        dialState.put(UCSCall.HUNGUP_WHILE_2G, "2G���ܲ�����ѡ�ֱ������Ƶ������ͨ��");
        dialState.put(UCSCall.HUNGUP_REFUSAL, "�Է��ܾ�����");
        dialState.put(UCSCall.HUNGUP_NOT_ENOUGH_BALANCE, "����");
        dialState.put(UCSCall.HUNGUP_MYSELF_REFUSAL, "�Լ��ܾ�����");
        dialState.put(UCSCall.CALL_VIDEO_DOES_NOT_SUPPORT, "���豸��֧����Ƶͨ��");
        dialState.put(UCSCall.CALL_FAIL_BLACKLIST, "Ƶ�������ѱ����������");
        dialState.put(UCSCall.NOT_NETWORK, "��ǰ������");
        dialState.put(UCSCall.HUNGUP_RTP_TIMEOUT, "RTP��ʱ�绰���Ҷ�");
        dialState.put(UCSCall.HUNGUP_OTHER_REASON, "����ԭ��绰���Ҷ�");
        dialState.put(UCSCall.CALL_NUMBER_IS_EMPTY, "���к���Ϊ��");
    }

    public static void initAction(String packName) {
        if (!ACTION_LOGIN.startsWith(packName)) {
            ACTION_LOGIN = packName + "_" + ACTION_LOGIN;
//			ACTION_LOGOUT = packName + "_" + ACTION_LOGOUT;
            ACTION_TCP_LOGIN_RESPONSE = packName + "_" + ACTION_TCP_LOGIN_RESPONSE;
//			ACTION_TCP_LOGIN_CLIENT_RESPONSE = packName + "_" + ACTION_TCP_LOGIN_CLIENT_RESPONSE; 
            ACTION_SEND_FILE_PROGRESS = packName + "_" + ACTION_SEND_FILE_PROGRESS;
            ACTION_DIAL = packName + "_" + ACTION_DIAL;
            ACTION_DIAL_STATE = packName + "_" + ACTION_DIAL_STATE;
            ACTION_DIAL_HANGUP = packName + "_" + ACTION_DIAL_HANGUP;
            ACTION_NETWORK_STATE = packName + "_" + ACTION_NETWORK_STATE;
            ACTION_ANSWER = packName + "_" + ACTION_ANSWER;
            ACTION_CALL_TIME = packName + "_" + ACTION_CALL_TIME;
//			ACTION_START_TIME = packName + "_" + ACTION_START_TIME;
            ACTION = packName + "_" + ACTION;
            ACTION_MSG = packName + "_" + ACTION_MSG;
            ACTION_STATUS = packName + "_" + ACTION_STATUS;
            ACTION_TEL_LIST_DATA_UPDATE = packName + "_" + ACTION_TEL_LIST_DATA_UPDATE;
            ACTION_TEL_USER_INFO_DATA_UPDATE = packName + "_" + ACTION_TEL_USER_INFO_DATA_UPDATE;
            ACTION_NET_ERROR_KICKOUT = packName + "_" + ACTION_NET_ERROR_KICKOUT;
        }
    }

}
