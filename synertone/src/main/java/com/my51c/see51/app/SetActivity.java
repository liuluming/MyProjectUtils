package com.my51c.see51.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.activity.OneStartActivity;
import com.synertone.netAssistant.R;

public class SetActivity extends BaseActivity {

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_activity);
    }

    //��ť�ĵ���¼�
    public void setOnClick(View v) {
        switch (v.getId()) {
            case R.id.set_link_hand:
        /*	mIntent=new Intent(SetActivity.this, RouteSelect.class);
			startActivity(mIntent);
			//Toast.makeText(SetActivity.this, "�������·�ֶ��л�", 0).show();
			*/
                break;

            case R.id.set_bandwidth_add:
                //	Toast.makeText(SetActivity.this, "����˴������", 0).show();

                break;

            case R.id.set_satellite:
                mIntent = new Intent(SetActivity.this, OneStartActivity.class);
                startActivity(mIntent);
                //	Toast.makeText(SetActivity.this, "�������������", 0).show();

                break;

            case R.id.set_android_server:
                //	Toast.makeText(SetActivity.this, "����˰�׿������", 0).show();

                break;

            case R.id.set_linux_server:
                //	Toast.makeText(SetActivity.this, "�����Linux������", 0).show();

                break;

            case R.id.set_app_update:
                //	Toast.makeText(SetActivity.this, "�����APP����", 0).show();

                break;

            case R.id.set_device_update:
                //	Toast.makeText(SetActivity.this, "������豸����", 0).show();

                break;

            default:
                break;
        }

    }

    // ����˳�����
    public void onFinish(View v) {
        finish();
    }

}
