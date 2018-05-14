package com.my51c.see51.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.guide.DimensionActivity;
import com.my51c.see51.listener.OnSetRFInfoListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.protocal.RFPackage;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;

public class RfDeviceAddActivity extends SherlockFragmentActivity implements OnClickListener {

    static public RemoteInteractionStreamer mediastream;
    private static Device mDevice;
    private String TAG = "RfDeviceAddActivity";
    private EditText edtRFID;
    private ImageButton imgBtnDimension;
    private Button btnAddRFDevice;
    private AppData appData;
    private String deviceId;
    private boolean islocal;
    private RFPackage rfpack;
    private RFPackage rfclone;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.rfdevice_guide);

//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(true);
//		Drawable titleDrawable = getResources().getDrawable(R.drawable.title_bg);
//		actionBar.setBackgroundDrawable(titleDrawable);

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.add_back_layout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backMainActivity();
            }
        });

        appData = (AppData) getApplication();
        //appData.addUIActivity(new WeakReference<Activity>(this));
        Bundle bundle = getIntent().getExtras();
        deviceId = bundle.getString("id");
        islocal = bundle.getBoolean("isLocal");

        findview();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        imgBtnDimension.setOnClickListener(this);
        btnAddRFDevice.setOnClickListener(this);


        if (islocal) {
            mDevice = appData.getLocalList().getDevice(deviceId);
        } else {
            mDevice = appData.getAccountInfo().getCurrentList().getDevice(deviceId);
        }

        if (mDevice != null) {
            rfpack = mDevice.getRFInfo();
        }

        String rfdeviceId = DimensionActivity.deviceId;

        if (rfdeviceId != null) {
            if (rfdeviceId.length() == 8) {
                edtRFID.setText(rfdeviceId);
                edtRFID.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                btnAddRFDevice.setVisibility(View.VISIBLE);
            } else {
                rfdeviceId = "";
                Toast toastQRcode = Toast.makeText(getApplicationContext(), getString(R.string.guideQRCodeError), Toast.LENGTH_SHORT);
                toastQRcode.setGravity(Gravity.CENTER, 0, 0);
                toastQRcode.show();
            }
        }

        edtTextchangeListener();
        mediastream = appData.getRemoteInteractionStreamer();


    }

    public void backMainActivity() {
        RfDeviceAddActivity.this.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {

            case android.R.id.home:
                // Do whatever you want, e.g. finish()
                backMainActivity();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void edtTextchangeListener() {
        edtRFID.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                selectionStart = edtRFID.getSelectionStart();
                selectionEnd = edtRFID.getSelectionEnd();
                //Log.i("gongbiao1", "" + selectionStart);
                if (temp.length() == 8) {
                    edtRFID.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    btnAddRFDevice.setVisibility(View.VISIBLE);
                } else if (temp.length() > 8) {
                    edtRFID.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    btnAddRFDevice.setVisibility(View.VISIBLE);
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    edtRFID.setText(s);
                    edtRFID.setSelection(tempSelection);

                } else if (temp.length() < 8) {
                    Drawable drawable = getResources().getDrawable(R.drawable.error);
                    edtRFID.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    btnAddRFDevice.setVisibility(View.GONE);
                }
            }
        });
    }

    private void findview() {
        edtRFID = (EditText) findViewById(R.id.edtRFId);
        imgBtnDimension = (ImageButton) findViewById(R.id.imgBtnDimension);
        btnAddRFDevice = (Button) findViewById(R.id.btnAddRFDevice);
    }

    public void addNewRFInfo() {
        rfclone = new RFPackage();
        if (rfpack != null) {
            rfclone.parseArrayList(rfpack.getRFDevList());
        }
        String rfid = edtRFID.getText().toString();
        if (rfid.equals("")) {
            return;
        }
        rfclone.addId(rfid);

        String strType = rfid.substring(0, 2);
        if (strType.equals("01")) {
            rfclone.setValue(rfid, "name", "controller");
        } else if (strType.equals("02")) {
            rfclone.setValue(rfid, "name", "door");
        } else if (strType.equals("03")) {
            rfclone.setValue(rfid, "name", "pir");
        } else if (strType.equals("04")) {
            rfclone.setValue(rfid, "name", "smoking");
        } else if (strType.equals("10")) {
            rfclone.setValue(rfid, "name", "smart socket");
        } else if (strType.equals("21")) {
            rfclone.setValue(rfid, "name", "alarmer");
        } else if (strType.equals("22")) {
            rfclone.setValue(rfid, "name", "bell");
        } else if (strType.equals("23")) {
            rfclone.setValue(rfid, "name", "out_io");
        }


        WaitingDialogFragment.newInstance(rfclone).show(getSupportFragmentManager(), "waiting");

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.imgBtnDimension: {
                Intent intent = new Intent();
                intent.setClass(RfDeviceAddActivity.this, DimensionActivity.class);
                intent.putExtra("isguide", false);
                RfDeviceAddActivity.this.startActivity(intent);
            }
            break;

            case R.id.btnAddRFDevice: {
                addNewRFInfo();
            }
            break;

            default:
                break;

        }
    }

    static public class WaitingDialogFragment extends SherlockDialogFragment {

        static final int MSG_SET_SUCESS = 0;
        static final int MSG_SET_FAILED = 1;
        static final int MSG_SET_TIMEOUT = 2;
        private static RFPackage mRFPack;
        ProgressDialog pd;
        TimeOutAsyncTask asyncTask;
        Toast toast;
        Activity mActivity;
        Handler handler;
        OnSetRFInfoListener mOnSetRFInfoListener = new OnSetRFInfoListener() {

            @Override
            public void onSetRFInfoFailed() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(MSG_SET_FAILED);
            }

            @Override
            public void onSetRFInfoSuccess() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(MSG_SET_SUCESS);

            }

        };

        public WaitingDialogFragment(RFPackage inPack) {
            // TODO Auto-generated constructor stub
            mRFPack = inPack;
        }

        public static WaitingDialogFragment newInstance(RFPackage inPack) {
            WaitingDialogFragment frag = new WaitingDialogFragment(inPack);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            mediastream.setOnSetRFInfoListener(mOnSetRFInfoListener);

            pd = new ProgressDialog(getActivity());
            pd.setTitle(R.string.sure);
            pd.setMessage(getString(R.string.rfdevicetitle));
            pd.setCancelable(true);
            pd.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    //mediastream.setOnSetRFInfoListener(null);
                }
            });
            pd.show();

            asyncTask = new TimeOutAsyncTask(this);
            asyncTask.execute(0);
            return pd;
        }

        @Override
        public void onStop() {
            super.onStop();
            if (!asyncTask.isCancelled()) {
                asyncTask.cancel(true);
                //mediastream.setOnSetRFInfoListener(null);
            }
        }

        public void handleMessage(Message msg) {
            if (msg.what == MSG_SET_SUCESS) {
                mDevice.setRFInfo(mRFPack);
                showToast(getString(R.string.rfsettingsuccess));
                asyncTask.cancel(true);
                pd.cancel();

            } else if (msg.what == MSG_SET_FAILED) {

                showToast(getString(R.string.rfsettingfail));
                asyncTask.cancel(true);
                pd.cancel();
            } else if (msg.what == MSG_SET_TIMEOUT) {
                showToast(getString(R.string.rfsettingfail));
                pd.cancel();
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
            handler = new MyHandler(this);
        }

        public void showToast(String resTip) {
            Toast toast = Toast.makeText(getActivity(), resTip, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        static class MyHandler extends Handler {
            WeakReference<WaitingDialogFragment> frg;

            MyHandler(WaitingDialogFragment wf) {
                frg = new WeakReference<WaitingDialogFragment>(wf);
            }

            @Override
            public void handleMessage(Message msg) {
                WaitingDialogFragment wdf = frg.get();
                wdf.handleMessage(msg);
            }
        }

        private static class TimeOutAsyncTask extends AsyncTask<Integer, Integer, String> {
            private WeakReference<WaitingDialogFragment> mRef;

            public TimeOutAsyncTask(WaitingDialogFragment mFragment) {
                mRef = new WeakReference<WaitingDialogFragment>(mFragment);
            }

            @Override
            protected void onPreExecute() {
                //��һ��ִ�з���
                super.onPreExecute();
                mediastream.sendRFDevInfo(mRFPack, "");
            }

            @Override
            protected String doInBackground(Integer... params) {
                // TODO Auto-generated method stub
                try {

                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                //��һ��ִ�з���
                super.onPreExecute();
                if (mRef == null)
                    return;
                WaitingDialogFragment mFrag = mRef.get();
                if (mFrag != null) {
                    mFrag.handler.sendEmptyMessage(MSG_SET_TIMEOUT);
                }
            }
        }
    }
}
