package com.my51c.see51.yzxvoip;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;
import com.yzxtcp.tools.CustomLog;

/**
 * 配置服务器地址界面
 *
 * @author zhuqian
 */
public class AddressConfigActivity extends BaseActivity implements OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private YzxTopBar topBar;
    private EditText as_address;
    private EditText as_port;
    private EditText tcp_address;
    private EditText tcp_port;
    private EditText cps_address;
    private EditText cps_port;
    private Button address_ok;
    private Button address_reset;
    private CheckBox mChkDebug;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_config);

        initViews();
    }

    private void initViews() {
        topBar = (YzxTopBar) findViewById(R.id.actionBar_addressConfig);
        topBar.setTitle("服务器地址配置");
        topBar.setBackBtnOnclickListener(this);
        as_address = (EditText) findViewById(R.id.as_address);
        as_port = (EditText) findViewById(R.id.as_port);
        tcp_address = (EditText) findViewById(R.id.tcp_address);
        tcp_port = (EditText) findViewById(R.id.tcp_port);
        address_ok = (Button) findViewById(R.id.address_ok);
        address_reset = (Button) findViewById(R.id.address_reset);
        cps_address = (EditText) findViewById(R.id.cps_address);
        cps_port = (EditText) findViewById(R.id.cps_port);
        mChkDebug = (CheckBox) findViewById(R.id.id_checkbox_debug);

        String asAddressAndPort = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_ASADDRESS", "");
        String tcpAddressAndPort = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_CSADDRESS", "");
        String cpsAddressAndPort = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_CPSADDRESS", "");
        boolean avDebug = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getBoolean("YZX_AVDEBUG", false);
        mChkDebug.setChecked(avDebug);

        if (!TextUtils.isEmpty(asAddressAndPort)) {
            if (asAddressAndPort.split(":").length == 2) {
                as_address.setText(asAddressAndPort.split(":")[0]);
                as_port.setText(asAddressAndPort.split(":")[1]);
            }
        } else {
            as_port.setText("");
            as_address.setText("");
        }
        as_address.setSelection(as_address.getText().toString().length());
        if (!TextUtils.isEmpty(tcpAddressAndPort)) {
            if (tcpAddressAndPort.split(":").length == 2) {
                tcp_address.setText(tcpAddressAndPort.split(":")[0]);
                tcp_port.setText(tcpAddressAndPort.split(":")[1]);
            }
        } else {
            tcp_address.setText("");
            tcp_port.setText("");
        }
        if (!TextUtils.isEmpty(cpsAddressAndPort)) {
            if (cpsAddressAndPort.split(":").length == 2) {
                cps_address.setText(cpsAddressAndPort.split(":")[0]);
                cps_port.setText(cpsAddressAndPort.split(":")[1]);
            }
        } else {
            cps_address.setText("");
            cps_port.setText("");
        }
        address_ok.setOnClickListener(this);
        address_reset.setOnClickListener(this);
        mChkDebug.setOnCheckedChangeListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imbtn_back:
                //返回
                finish();
                break;
            case R.id.address_ok:
                //save
                String asAddress = as_address.getText().toString();
                String asPort = as_port.getText().toString();
                String tcpAddress = tcp_address.getText().toString();
                String tcpPort = tcp_port.getText().toString();
                String cpsAddress = cps_address.getText().toString();
                String cpsPort = cps_port.getText().toString();
                if (!checkIPSetIsValid(asAddress, asPort) ||
                        !checkIPSetIsValid(tcpAddress, tcpPort) ||
                        !checkIPSetIsValid(cpsAddress, cpsPort)
                        ) {
                    Toast.makeText(this, "地址或者端口不能为空或不合法", Toast.LENGTH_SHORT).show();
                    return;
                }
            /*if(!AddressTools.isPort(asPort) || !AddressTools.isPort(tcpPort) || !AddressTools.isPort(cpsPort)){
				Toast.makeText(this, "您输入的端口不合法", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!AddressTools.isNetAddress(asAddress) || !AddressTools.isNetAddress(tcpAddress)
					|| !AddressTools.isNetAddress(cpsAddress)){
				Toast.makeText(this, "您输入的地址不合法", Toast.LENGTH_SHORT).show();
				return;
			}*/
                if (!TextUtils.isEmpty(asAddress) && !TextUtils.isEmpty(asAddress)) {
                    getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().putString("YZX_ASADDRESS", asAddress + ":" + asPort).commit();
                }

                if (!TextUtils.isEmpty(tcpAddress) && !TextUtils.isEmpty(tcpPort)) {
                    getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().putString("YZX_CSADDRESS", tcpAddress + ":" + tcpPort).commit();
                }

                if (!TextUtils.isEmpty(cpsAddress) && !TextUtils.isEmpty(cpsPort)) {
                    getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().putString("YZX_CPSADDRESS", cpsAddress + ":" + cpsPort).commit();
                }

                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                RestTools.initUrl(AddressConfigActivity.this);
                break;
            case R.id.address_reset:
                //reset
                getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().clear().commit();
                Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
                as_address.setText("");
                as_port.setText("");
                tcp_port.setText("");
                tcp_address.setText("");
                cps_address.setText("");
                cps_port.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.id_checkbox_debug) {
            getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().putBoolean("YZX_AVDEBUG", isChecked).commit();
            CustomLog.d("debug isChecked:" + isChecked);
        }
    }

    /**
     * @param ip
     * @param port
     * @return
     * @author zhangbin
     * @2016-3-15
     * @descript:判断输入的IP是否合法 同时为NULL或都有值为TRUE
     */
    private boolean checkIPSetIsValid(String ip, String port) {
        if (TextUtils.isEmpty(ip) && TextUtils.isEmpty(port)) {
            System.out.println("zz 444444");
            return true;
        } else if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(ip)) {
            System.out.println("zz 111111");
            if (AddressTools.isNetAddress(ip) && AddressTools.isPort(port)) {
                System.out.println("zz 222222");
                return true;
            } else {
                System.out.println("zz 333333");
                return false;
            }
        } else {
            System.out.println("zz 555555");
            return false;
        }
    }
}
