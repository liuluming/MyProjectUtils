package com.my51c.see51.guide;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.synertone.netAssistant.R;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author Chenchi wifi��صĲ�����ʹ�õ���ģʽ
 * @version 1.0
 */
public class WiFiHelper {

    public static final String WPA2 = "WPA2";
    public static final String WPA = "WPA";
    public static final String WEP = "WEP";
    public static final String OPEN = "Open";
    // For EAP Enterprise fields
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";
    static final int op_timeout = 10;
    static final String[] SECURITY_MODES =
            {WEP, WPA, WPA2, WPA_EAP, IEEE8021X};
    private final static String TAG = "WiFiHelper";
    private static final int MAX_PRIORITY = 99999;
    private static WiFiHelper mInstance = null;
    private static String regMatch;
    boolean mConnectOK;
    boolean mScanOver;
    private Context mContex;
    private Context alertDialogContext;
    private WifiManager mWifiManager;
    private boolean isStarted;
    private boolean isScanning;
    private WifiStatusEventListener eventLister;
    private WifiReceiver mWifiScanReceiver;
    private WifiStatusReceiver connectStatusReceiver;
    private List<WifiScanResult> results;
    private String previousWifiSSID = null;
    private boolean needReenable = false;
    private boolean enableingAll = false;

    private WiFiHelper(Context context) {
        // TODO Auto-generated constructor stub
        mContex = context;
        mWifiManager = (WifiManager) mContex
                .getSystemService(Context.WIFI_SERVICE);
        results = new ArrayList<WifiScanResult>();
    }

    /**
     * @param context   ʹ�� activity��������.
     * @param matchSSID ƥ���ض���ssid��������ʽ
     * @return ��ȡ����WiFihelper����
     */
    public static WiFiHelper getInstance(Context context, String matchSSID) {
        regMatch = matchSSID;
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new WiFiHelper(context);
            return mInstance;
        }
    }

    private static int shiftPriorityAndSave(final WifiManager wifiMgr) {
        final List<WifiConfiguration> configurations = wifiMgr
                .getConfiguredNetworks();
        sortByPriority(configurations);
        final int size = configurations.size();
        for (int i = 0; i < size; i++) {
            final WifiConfiguration config = configurations.get(i);
            config.priority = i;
            wifiMgr.updateNetwork(config);
        }
        wifiMgr.saveConfiguration();
        return size;
    }

    private static void sortByPriority(
            final List<WifiConfiguration> configurations) {
        java.util.Collections.sort(configurations,
                new Comparator<WifiConfiguration>() {

                    @Override
                    public int compare(WifiConfiguration object1,
                                       WifiConfiguration object2) {
                        return object1.priority - object2.priority;
                    }
                });
    }

    private static int getMaxPriority(final WifiManager wifiManager) {
        final List<WifiConfiguration> configurations = wifiManager
                .getConfiguredNetworks();
        int pri = 0;
        for (final WifiConfiguration config : configurations) {
            if (config.priority > pri) {
                pri = config.priority;
            }
        }
        return pri;
    }

    private static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        final int lastPos = string.length() - 1;
        if (lastPos < 0
                || (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string;
        }

        return "\"" + string + "\"";
    }

    public static WifiConfiguration getWifiConfiguration(
            final WifiManager wifiMgr, final ScanResult hotsopt,
            String hotspotSecurity) {
        final String ssid = convertToQuotedString(hotsopt.SSID);
        if (ssid.length() == 0) {
            return null;
        }

        final String bssid = hotsopt.BSSID;
        if (bssid == null) {
            return null;
        }

        if (hotspotSecurity == null) {
            hotspotSecurity = getScanResultSecurity(hotsopt);
        }

        final List<WifiConfiguration> configurations = wifiMgr
                .getConfiguredNetworks();

        for (final WifiConfiguration config : configurations) {
            if (config.SSID == null || !ssid.equals(config.SSID)) {
                continue;
            }
            if (config.BSSID == null || bssid.equals(config.BSSID)) {
                final String configSecurity = getWifiConfigurationSecurity(config);
                if (hotspotSecurity.equals(configSecurity)) {
                    return config;
                }
            }
        }
        return null;
    }

    public static WifiConfiguration getWifiConfiguration(
            final WifiManager wifiMgr, final WifiConfiguration configToFind,
            String security) {
        final String ssid = configToFind.SSID;
        if (ssid.length() == 0) {
            return null;
        }

        final String bssid = configToFind.BSSID;

        if (security == null) {
            security = getWifiConfigurationSecurity(configToFind);
        }

        final List<WifiConfiguration> configurations = wifiMgr
                .getConfiguredNetworks();

        for (final WifiConfiguration config : configurations) {
            if (config.SSID == null || !ssid.equals(config.SSID)) {
                continue;
            }
            if (config.BSSID == null || bssid == null
                    || bssid.equals(config.BSSID)) {
                final String configSecurity = getWifiConfigurationSecurity(config);
                if (security.equals(configSecurity)) {
                    return config;
                }
            }
        }
        return null;
    }

    public static String getScanResultSecurity(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
            if (cap.contains(SECURITY_MODES[i])) {
                return SECURITY_MODES[i];
            }
        }
        return OPEN;
    }

    static public String getWifiConfigurationSecurity(
            WifiConfiguration wifiConfig) {

        if (wifiConfig.allowedKeyManagement.get(KeyMgmt.NONE)) {
            // If we never set group ciphers, wpa_supplicant puts all of them.
            // For open, we don't set group ciphers.
            // For WEP, we specifically only set WEP40 and WEP104, so CCMP
            // and TKIP should not be there.
            if (!wifiConfig.allowedGroupCiphers.get(GroupCipher.CCMP)
                    && (wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP40) || wifiConfig.allowedGroupCiphers
                    .get(GroupCipher.WEP104))) {
                return WEP;
            } else {
                return OPEN;
            }
        } else if (wifiConfig.allowedProtocols.get(Protocol.RSN)) {
            return WPA2;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP)) {
            return WPA_EAP;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return IEEE8021X;
        } else if (wifiConfig.allowedProtocols.get(Protocol.WPA)) {
            return WPA;
        } else {
            Log.w(TAG,
                    "Unknown security type from WifiConfiguration, falling back on open.");
            return OPEN;
        }
    }

    public void setWifiStatusEventListener(WifiStatusEventListener l) {
        eventLister = l;
    }

    private boolean isMatch(String id) {
        if (id.startsWith("\"")) {
            id = id.substring(1, id.length() - 1);
        }
        return id.matches(regMatch);
    }

    public String getPreviousWifiSSID() {
        // TODO Auto-generated method stub
        return previousWifiSSID;
    }

    public void pushWIFI() {
        if (mWifiManager.isWifiEnabled()) {
            previousWifiSSID = getCurrentSSID();
        } else {
            previousWifiSSID = null;
        }
    }

    public void popWIFI() {
        //Log.d(TAG, "popWIFI()");

        if (previousWifiSSID != null && isConnected(previousWifiSSID)) {
            String password = null;
            connect(previousWifiSSID, password);
            //Log.d(TAG, "connect : " + previousWifiSSID);
        }
    }

    public List<WifiScanResult> getScanResults() {
        results.clear();
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        List<WifiConfiguration> allConfig = mWifiManager
                .getConfiguredNetworks();
        // ����ssid���Ϲ����wifi
        if (wifiList != null) {
            for (ScanResult wifi : wifiList) {
                //Log.d(TAG, "wifiSSID:" + wifi.SSID + "wifi.capabilities:"
                //		+ wifi.capabilities);
                if (isMatch(wifi.SSID)) {
                    WifiScanResult result = new WifiScanResult();
                    result.ssid = wifi.SSID;
                    result.index = -1;
                    result.capabilities = wifi.capabilities;
                    for (int i = 0; i < allConfig.size(); i++) {
                        if (allConfig.get(i).SSID.equals("\"" + wifi.SSID
                                + "\"")
                                || allConfig.get(i).SSID.equals(wifi.SSID)) {
                            result.index = i;
                            break;
                        }

                    }
                    results.add(result);
                }
            }
        }
        return results;
    }

    /**
     * ��ʼ����wifi״̬�ı��¼��Ĺ㲥
     */
    public void start() {
        if (!isStarted) {
            connectStatusReceiver = new WifiStatusReceiver();
            mContex.registerReceiver(connectStatusReceiver, new IntentFilter(
                    WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
            mContex.registerReceiver(connectStatusReceiver, new IntentFilter(
                    WifiManager.WIFI_STATE_CHANGED_ACTION));

            mContex.registerReceiver(connectStatusReceiver, new IntentFilter(
                    WifiManager.NETWORK_STATE_CHANGED_ACTION));
            isStarted = true;
        }
    }

    public void stop() {
        StopScan();
        if (isStarted) {
            if (connectStatusReceiver != null) {
                //Log.d(TAG, "unregisterReceiver(connectStatusReceiver)");
                mContex.unregisterReceiver(connectStatusReceiver);
                connectStatusReceiver = null;
            }
            isStarted = false;
        }
    }

    public void Scan() {
        if (mWifiManager.setWifiEnabled(true)) {
            mWifiScanReceiver = new WifiReceiver();
            mContex.registerReceiver(mWifiScanReceiver, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if (!isScanning) {
                mWifiManager.startScan();
                isScanning = true;
            }
        }
    }

    public void StopScan() {
        if (mWifiScanReceiver != null) {
            //Log.d(TAG, "unregisterReceiver(mWifiScanReceiver)");
            mContex.unregisterReceiver(mWifiScanReceiver);
            mWifiScanReceiver = null;
        }
    }

    /**
     * ��ȡָ��ssid��wifi�����Ƿ��Ѿ������,������򷵻��ڱ����б��е�index
     *
     * @param ssid �� ָ����ssid
     * @return -1 û�б��棬>= 0����������ö���
     */
    public int getSavedConfigrationIndex(String ssid) {
        List<WifiConfiguration> allConfig = mWifiManager
                .getConfiguredNetworks();
        for (int i = 0; i < allConfig.size(); i++) {
            if (ssid.equals(allConfig.get(i).SSID)) {
                return i;
            }
        }

        String ssidWithQuto = new StringBuilder().append("\"").append(ssid)
                .append("\"").toString();
        // ssid = new
        // StringBuilder().append("\"").append(ssid).append("\"").toString();
        for (int i = 0; i < allConfig.size(); i++) {
            if (ssidWithQuto.equals(allConfig.get(i).SSID)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * ��ȡ��ǰ���ӵ�ssid
     * wifiInfo.getSSID()�����õ���ssid��Android4.2��ǰ��˫���ţ���4.2����˫���ţ����ĵ�������һ�¡�
     *
     * @param
     * @return
     */
    public String getCurrentSSID() {
        String ssid = "";

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getSSID() != null
                && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED)
            ssid = wifiInfo.getSSID();
        return ssid;
    }

    /**
     * �ж�ָ��ssid��wifi�Ƿ��Ѿ�����
     *
     * @param ssid
     * @return
     */
    public boolean isConnected(String ssid) {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String curSSID = wifiInfo.getSSID();
        String tmpSSID = new StringBuilder().append("\"").append(ssid)
                .append("\"").toString();
        return curSSID != null
                && (curSSID.equals(ssid) || curSSID.equals(tmpSSID))
                && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED;
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public void connect(int index) {
        if (index != -1) {
            mWifiManager.enableNetwork(index, true);
        }
    }

    /**
     * You should set alertDialogContext first.
     *
     * @param ssid
     */
    public void connect(String ssid, String password) {
        if (isConnected(ssid)) {
            //Log.d(TAG, "isConnected(" + ssid + ") == true");
            if (eventLister != null)
                eventLister.OnWifiConnected(ssid);
            return;
        }
        for (WifiScanResult result : results) {
            if (result.ssid.equals(ssid)) {
                if (result.index != -1) {
                    WifiConfiguration config = mWifiManager
                            .getConfiguredNetworks().get(result.index);
                    connectToConfiguredNetwork(mContex, mWifiManager, config, false);
                    break;
                } else {
                    if (result.capabilities.equals("[ESS]") || password != null) {
                        configWifi(ssid, password);
                    } else {
                        ShowPasswordInput(ssid,
                                alertDialogContext
                                        .getString(R.string.enterPassword));
                    }
                    break;
                }
            }
        }
    }

    private void ShowPasswordInput(String ssid, String tips) {

        final EditText mPasswordInput = new EditText(mContex);
        final String finalSsid = ssid;

        new AlertDialog.Builder(alertDialogContext)
                .setTitle(ssid)
                .setItems(new String[]
                        {tips}, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(mPasswordInput)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String password = mPasswordInput.getText()
                                        .toString();
                                if (password.length() == 0) {
                                    ShowPasswordInput(finalSsid,
                                            "Password Error!");
                                    dialog.dismiss();
                                } else {
                                    connect(finalSsid, password);
                                    dialog.dismiss();
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * ����ָ��wifi
     *
     * @param ssid     -- ssid
     * @param password -- wifi����
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws InvalidKeyException
     */
    public void configWifi(String ssid, String password) {
        if (isConnected(ssid)) {
            //Log.d("wifi", "isConnected(" + ssid + ") == true");
            if (eventLister != null)
                eventLister.OnWifiConnected(ssid);
            return;
        }

        int index = -1;
        WifiConfiguration wc = new WifiConfiguration();
        if (password != null) {
            wc.SSID = "\"" + ssid + "\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.hiddenSSID = false;
            wc.preSharedKey = "\"" + password + "\""; // wifi����
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        } else {
            wc.SSID = "\"" + ssid + "\"";
            wc.wepKeys[0] = "";
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wc.wepTxKeyIndex = 0;
        }

        index = mWifiManager.addNetwork(wc);
        if (index == -1) {
            eventLister.OnWifiConnectFailed(ssid);
            return;
        }
        wc.networkId = index;
        if (!mWifiManager.saveConfiguration()) {
            eventLister.OnWifiConnectFailed(ssid);
            return;
        }

        WifiConfiguration config = getWifiConfiguration(mWifiManager, wc, null);
        if (config == null) {
            eventLister.OnWifiConnectFailed(ssid);
            return;
        }
        //Log.d(TAG, "1 index = " + index);
        connectToConfiguredNetwork(mContex, mWifiManager, config, false);
        return;

    }

    /**
     * @return the alertDialogContext
     */
    public Context getAlertDialogContext() {
        return alertDialogContext;
    }

    /**
     * @param alertDialogContext the alertDialogContext to set
     */
    public void setAlertDialogContext(Context alertDialogContext) {
        this.alertDialogContext = alertDialogContext;
    }

    boolean connectToConfiguredNetwork(final Context ctx,
                                       final WifiManager wifiMgr, WifiConfiguration config,
                                       boolean reassociate) {
        final String security = getWifiConfigurationSecurity(config);
        int oldPri = config.priority;
        // Make it the highest priority.
        int newPri = getMaxPriority(wifiMgr) + 1;
        if (newPri > MAX_PRIORITY) {
            newPri = shiftPriorityAndSave(wifiMgr);
            config = getWifiConfiguration(wifiMgr, config, security);
            if (config == null) {
                return false;
            }
        }

        // Set highest priority to this configured network
        config.priority = newPri;
        int networkId = wifiMgr.updateNetwork(config);
        if (networkId == -1) {
            return false;
        }

        // Do not disable others
        if (!wifiMgr.enableNetwork(networkId, false)) {
            config.priority = oldPri;
            return false;
        }

        if (!wifiMgr.saveConfiguration()) {
            config.priority = oldPri;
            return false;
        }

        // We have to retrieve the WifiConfiguration after save.
        config = getWifiConfiguration(wifiMgr, config, security);
        if (config == null) {
            return false;
        }

        reenableAllApsWhenNetworkStateChanged();

        // Disable others, but do not save.
        // Just to force the WifiManager to connect to it.
        if (!wifiMgr.enableNetwork(config.networkId, true)) {
            return false;
        }

        final boolean connect = reassociate ? wifiMgr.reassociate() : wifiMgr
                .reconnect();
        return connect;
    }

    private void reenableAllApsWhenNetworkStateChanged() {
        needReenable = true;
    }

    public interface WifiStatusEventListener {
        void OnWifiDisconnected(String ssid);

        void OnWifiConnected(String ssid);

        void OnWifiConnectFailed(String ssid);

        void OnWifiScanOver(List<WifiScanResult> result);
    }

    public class WifiScanResult {
        public String ssid;
        public int index;
        public String capabilities;
    }

    class WifiStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "WifiStatusReceiver : " + intent.getAction().toString());
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            String curSSID = wifiInfo.getSSID();

            if (needReenable
                    && WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
                    .getAction())) {

                final NetworkInfo networkInfo = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                final NetworkInfo.DetailedState detailed = networkInfo
                        .getDetailedState();
                switch (detailed) {
                    case DISCONNECTED:
                    case DISCONNECTING:
                    case SCANNING:
                        break;
                    default:
                        if (!enableingAll) {
                            enableingAll = true;
                            final List<WifiConfiguration> configurations = mWifiManager
                                    .getConfiguredNetworks();
                            if (configurations != null) {
                                for (final WifiConfiguration config : configurations) {
                                    //Log.d(TAG, "WifiStatusReceiver enableNetwork "
                                    //		+ config.SSID);
                                    mWifiManager.enableNetwork(config.networkId,
                                            false);
                                }
                            }
                            needReenable = false;
                            enableingAll = false;
                        }
                        break;
                }
            }
            if (intent.getAction().equals(
                    WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                SupplicantState newState = intent
                        .getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                //Log.d(TAG, wifiInfo.getSSID() + " " + newState.toString());
                if (curSSID != null && newState != null) {
                    if (isMatch(curSSID)) {
                        if (newState == SupplicantState.COMPLETED) {
                            if (eventLister != null) {
                                eventLister.OnWifiConnected(curSSID);
                            }
                        } else if (newState == SupplicantState.DISCONNECTED
                                || newState == SupplicantState.INACTIVE) {
                            if (eventLister != null) {
                                eventLister.OnWifiDisconnected(curSSID);
                            }
                        }
                    }
                }
            } else if (intent.getAction().equals(
                    WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = mWifiManager.getWifiState();

                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //Log.d(TAG, "state : WIFI_STATE_ENABLED");
                        if (eventLister != null) {
                            // eventLister.OnWifiScanOver(null); // ����wifi������ɨ��
                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //Log.d(TAG, "state : WIFI_STATE_DISABLED");
                        if (eventLister != null) {
                            eventLister.OnWifiDisconnected(curSSID);
                        }
                        break;

                }
            }
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isScanning = false;
            // ����ssid���Ϲ����wifi
            getScanResults();
            if (eventLister != null)
                eventLister.OnWifiScanOver(results);
        }
    }
}
