package com.my51c.see51.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.my51c.see51.adapter.DeviceListAdapter;
import com.my51c.see51.adapter.DeviceListAdapter.ViewHolder;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.Group;
import com.my51c.see51.data.SelectionDevice;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.service.GVAPService;
import com.my51c.see51.service.GvapEvent;
import com.my51c.see51.service.GvapEvent.GvapEventListener;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FavoriteFragment extends ListFragment implements DeviceListListener,
        OnClickListener, OnRefreshListener {

    static final int MSG_UPDATE = 0;
    static final int MSG_ClEAR_PROGRESSBAR = 1;
    public static View progressView;
    public static View waitTextView;
    public static View emptyView;
    public static ImageView refreshImg;
    static DeviceListAdapter adapter;
    private static String TAG = "FavoriteFragment";
    private static MainActivity mainActivity;
    private static AlertDialog nonetworkAlertDialog;
    public TextView emptyDevice;
    private DeviceListView listView;
    private AppData appData;
    private DeviceList myList;
    private Timer timer;
    private TimerTask timerTask;
    private GVAPService gvapService = null;
    private Group parent_group = null;
    private MyHandler mHandler = new MyHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_device_list, container, false);
        listView = (DeviceListView) v.findViewById(android.R.id.list);
        listView.setItemsCanFocus(true);
        listView.setonRefreshListener(this);

        progressView = v.findViewById(R.id.progress_get_devices_image);
        waitTextView = v.findViewById(R.id.loading);
        emptyView = v.findViewById(R.id.emptyView);
        emptyDevice = (TextView) v.findViewById(R.id.emptydevice);
        emptyDevice.setVisibility(View.INVISIBLE);

        refreshImg = (ImageView) v.findViewById(R.id.refreshImg);
        refreshImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                refreshDevice();
            }
        });

        gvapService = new GVAPService();
        gvapService.bNetStatus = true;


        if (MainActivity.isLoginClicked) {
            mHandler.sendEmptyMessageDelayed(7, 5000);
            MainActivity.isLoginClicked = false;
        }

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivity = (MainActivity) activity;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appData = (AppData) getActivity().getApplication();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mHandler.sendEmptyMessage(MSG_ClEAR_PROGRESSBAR);
            }
        };


        myList = appData.getAccountInfo().getCurrentList();
        synchronized (myList) {
            if (myList.getDeviceCount() == 0) {
                progressView.setVisibility(View.VISIBLE);
                waitTextView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                timer.schedule(timerTask, 10000);
            } else {
                progressView.setVisibility(View.INVISIBLE);
                waitTextView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }

        }

        emptyDevice.setVisibility(View.INVISIBLE);

        try {
            adapter = new DeviceListAdapter(getActivity(), myList, this, false);
        } catch (OutOfMemoryError e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "-" + e);
        }

        nonetworkAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sure)
                .setMessage(R.string.nonetwork)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).create();
        myList.addListListener(this);
        setListAdapter(adapter);
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        //adapter.release();
        adapter = null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (nonetworkAlertDialog != null)
            nonetworkAlertDialog = null;

        if (adapter != null)
            adapter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerTask.cancel();
        timer.cancel();
        myList.removeListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.onRefresh();
        } else {

        }
    }

    @Override
    public void onRefresh() {
        emptyDevice.setVisibility(View.INVISIBLE);
        if (MainActivity.canRefresh) {
            emptyView.setVisibility(View.GONE);

            refreshDevice();
            MainActivity.canRefresh = false;
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    MainActivity.canRefresh = true;
                    Log.i(TAG, "refreshTimer:" + MainActivity.canRefresh);
                }
            }, 2000);
        } else {

        }
    }

    public void refreshDevice() {
        emptyDevice.setVisibility(View.INVISIBLE);
        MainActivity.fromFav = true;

        if (!appData.getAccountInfo().isLogined()
                || appData.getAccountInfo().isGuest() || !appData.getNetStatus()) {
            nonetworkAlertDialog.show();
            Log.i(TAG, "--hasn't login");
            return;
        }
//		emptyView.setVisibility(View.VISIBLE);
//		progressView.setVisibility(View.VISIBLE);
//		waitTextView.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(MSG_ClEAR_PROGRESSBAR, 15000);

		/*update start*/
        DeviceList currentList = appData.getAccountInfo().getCurrentList();
        parent_group = currentList.getParent_group();
        if (parent_group != null)                            //��ȡ������Ϣ
        {
            String groupId = parent_group.getGroupID();
//				appData.getAccountInfo().getDevList(groupId).clear();
            appData.getGVAPService().getDeviceList(groupId);
            Log.i(TAG, "get parent_group:" + groupId);
        } else                                                //��ȡ��Ŀ¼��Ϣ
        {
//				appData.getAccountInfo().getDevList().clear();
            appData.getGVAPService().getDeviceList();            //���ͻ�ȡ�豸�б�ָ��
        }

    }

    @Override
    public void onListUpdate() {
        // TODO Auto-generated method stub
        mHandler.sendEmptyMessage(MSG_UPDATE);
        synchronized (myList) {
            if (myList.getDeviceCount() > 0) {
                if (timer != null)
                    timer.cancel();
            } else {
                if (timerTask != null)
                    timerTask.cancel();

                if (timer != null)
                    timer.cancel();

                timer = new Timer();
                timerTask = new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mHandler.sendEmptyMessage(1);
                    }
                };
                timer.schedule(timerTask, 1000);
            }
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Log.i("FragmentList", "Item clicked: id " + id);
        try {
            String Gro_Dev = ((ViewHolder) v.getTag()).Gro_Dev;
            if (Gro_Dev.equals("device")) {
                Device dev = (Device) ((ViewHolder) v.getTag()).info.getTag();

                Pattern pattern = Pattern.compile("^d[a-fA-F0-9]{11}$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(dev.getID());
                if (matcher.matches()) {
                    Toast.makeText(getActivity(), getString(R.string.canseenvr), Toast.LENGTH_LONG).show();
                    return;
                }

                if (dev != null) {
                    /*
					Intent intent = new Intent(this.getActivity(),FourPlayerActivity.class);
					intent.putExtra("id", dev.getID());
					intent.putExtra("url", dev.getPlayURL());
					intent.putExtra("title",((ViewHolder) v.getTag()).title.getText());
					intent.putExtra("version", dev.getSee51Info().getHwVersion() + " / " + dev.getSee51Info().getSwVersion());
					intent.putExtra("name", dev.getSee51Info().getDeviceName());
					intent.putExtra("isLocal", false);
					startActivity(intent);
					*/

                    SelectionDevice selDev = new SelectionDevice();
                    selDev.setDeviceid(dev.getID());
                    selDev.setDevicename(dev.getSee51Info().getDeviceName());
                    selDev.setUrl(dev.getPlayURL());
                    selDev.setLocal(false);

                    if (dev.isbRemoteSelected()) {
                        dev.setbRemoteSelected(false);
                        appData.removeSelectDev(selDev);
                        adapter.notifyDataSetChanged();
                    } else if (!dev.isbRemoteSelected() && dev.getPlayURL() != null) {
                        if (!appData.addSelectionDev(selDev)) {
                            Toast.makeText(getActivity(), getString(R.string.enoughselectiondevice), Toast.LENGTH_LONG).show();
                        } else {
                            dev.setbRemoteSelected(true);
                            adapter.notifyDataSetChanged();
                        }
                    }


                }
            } else if (Gro_Dev.equals("group")) {
                showSubList(v);
            }
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // ��Ӧinfo����
        String Gro_Dev = ((ViewHolder) v.getTag()).Gro_Dev;
        if (Gro_Dev.equals("device")) {
            Device dev = (Device) ((ViewHolder) v.getTag()).info.getTag();
            Intent intent = new Intent(this.getActivity(), DeviceInfoActivity.class);
            intent.putExtra("id", dev.getID());
            intent.putExtra("version", dev.getSee51Info().getHwVersion() + " / " + dev.getSee51Info().getSwVersion());
            intent.putExtra("name", dev.getSee51Info().getDeviceName());
            intent.putExtra("isLocal", false);
            startActivity(intent);
            this.getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);

        } else if (Gro_Dev.equals("group")) {
            showSubList(v);
        }
    }

    public void showSubList(View v) {
        mainActivity.menuBtn.setVisibility(View.GONE);
        mainActivity.menuBack.setVisibility(View.VISIBLE);

        Group parent_group = ((ViewHolder) v.getTag()).group;
        String groupId = parent_group.getGroupID();
        String grandParent_group = ((ViewHolder) v.getTag()).grandParent_group;
        if (appData.getAccountInfo().getDevList(groupId) == null) {
            appData.getAccountInfo().addList(parent_group,
                    grandParent_group);
            appData.getGVAPService().getDeviceList(groupId);
        }
        DeviceList devList = appData.getAccountInfo().getDevList(groupId);
        appData.getAccountInfo().setCurrentList(devList);
        myList = appData.getAccountInfo().getCurrentList();
        myList.addListListener(this);
        adapter.setDeviceList(myList);
        myList.listUpdated();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.displayHomeButton(true);
    }

    private static class MyHandler extends Handler {
        private WeakReference<FavoriteFragment> mRef;

        public MyHandler(FavoriteFragment mAct) {
            mRef = new WeakReference<FavoriteFragment>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRef == null)
                return;

            FavoriteFragment mFragment = mRef.get();
            if (mFragment != null) {
                switch (msg.what) {
                    case MSG_UPDATE:
                        if (adapter != null) {
                            adapter.updateDeviceData();
                            adapter.notifyDataSetChanged();
                        }
                        progressView.setVisibility(View.INVISIBLE);
                        waitTextView.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.INVISIBLE);
                        synchronized (mFragment.myList) {
                            if (mFragment.myList.getDeviceCount() == 0) {
                                mFragment.emptyDevice.setVisibility(View.VISIBLE);
                            } else {
                                mFragment.emptyDevice.setVisibility(View.INVISIBLE);
                            }
                        }


                        break;
                    case MSG_ClEAR_PROGRESSBAR:
                        progressView.setVisibility(View.INVISIBLE);
                        waitTextView.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.INVISIBLE);
                        synchronized (mFragment.myList) {
                            if (mFragment.myList.getDeviceCount() == 0) {
                                mFragment.emptyDevice.setVisibility(View.VISIBLE);
                            } else {
                                mFragment.emptyDevice.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;
                    case 7:
                        try {
                            if (mFragment.appData != null &&
                                    mFragment.appData.getAccountInfo() != null &&
                                    mFragment.appData.getAccountInfo().getCurrentList() != null &&
                                    mFragment.appData.getAccountInfo().getCurrentList().getDeviceCount() == 0) {
                                mFragment.refreshDevice();
                            }
                        } catch (NullPointerException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    static public class GvapDeviceInfoDialogFragment extends
            SherlockDialogFragment {
        static Device device;
        Handler mHandler;
        private EditText name;
        private EditText remark;
        private TextView id;
        private TextView softversion;
        private TextView hardversion;
        private Button btnUnbind;
        private AppData appData;
        private GvapEventListener gl;
        private ProgressDialog pd;
        private String mID;

		/*
		 * static class GvapHandler extends Handler{
		 * GvapHandler(GvapDeviceInfoDialogFragment fm){ new
		 * WeakReference<GvapDeviceInfoDialogFragment>(fm); //�����������ڻ����ڴ� } }
		 */

        public static GvapDeviceInfoDialogFragment newInstance(
                Device devResponse) {
            GvapDeviceInfoDialogFragment fm = new GvapDeviceInfoDialogFragment();
            Bundle args = new Bundle();
            device = devResponse;
            fm.setArguments(args);
            return fm;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getDialog().setTitle(R.string.deviceinfomation);
            View v = inflater.inflate(R.layout.gvap_device_info, container,
                    false);
            name = (EditText) v.findViewById(R.id.GvapDeviceName);
            remark = (EditText) v.findViewById(R.id.GvapDeviceRemark);
            id = (TextView) v.findViewById(R.id.serialNumberGvap);
            softversion = (TextView) v.findViewById(R.id.softwareversion);
            hardversion = (TextView) v.findViewById(R.id.hardwareversion);
            btnUnbind = (Button) v.findViewById(R.id.btnunBind);

            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mID = device.getID();

            name.setText(device.getSee51Info().getDeviceName());
            id.setText(device.getID());
            softversion.setText(device.getSee51Info().getSwVersion());
            hardversion.setText(device.getSee51Info().getHwVersion());

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    GvapCommand cmd = (GvapCommand) msg.obj;
                    switch (msg.what) {
                        case 0:
                            if (cmd.equals(GvapCommand.CMD_UNBIND)) {
                                pd.cancel();
                                Toast.makeText(getActivity(),
                                        getString(R.string.unbindSuccess),
                                        Toast.LENGTH_LONG).show();
                                appData.getAccountInfo().getDevList().clear();
                                appData.getGVAPService().getDeviceList();
                            }
                            break;
                        case 1:
                            if (cmd.equals(GvapCommand.CMD_UNBIND)) {
                                pd.cancel();
                                Toast.makeText(getActivity(),
                                        getString(R.string.unbindFailed),
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        default:
                            break;
                    }
                    try {
                        GvapDeviceInfoDialogFragment.this.getDialog().dismiss();
                    } catch (NullPointerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };

            btnUnbind.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    gl = new GvapEventListener() {

                        @Override
                        public void onGvapEvent(GvapEvent event) {
                            // TODO Auto-generated method stub
                            switch (event) {
                                case OPERATION_SUCCESS: {
                                    if (event.getCommandID() == GvapCommand.CMD_UNBIND) {
                                        Message msg = mHandler.obtainMessage(0,
                                                event.getCommandID());
                                        mHandler.sendMessage(msg);
                                    }
                                    break;
                                }
                                case OPERATION_TIMEOUT:
                                case OPERATION_FAILED:
                                case CONNECTION_RESET:
                                case CONNECT_TIMEOUT:
                                case CONNECT_FAILED:
                                case NETWORK_ERROR: {
                                    if (event.getCommandID() == GvapCommand.CMD_UNBIND) {
                                        Message msg = mHandler.obtainMessage(1,
                                                event.getCommandID());
                                        mHandler.sendMessage(msg);
                                    }
                                }
                                break;
                            }
                        }
                    };

                    appData.getGVAPService().removeGvapEventListener(gl);
                    appData.getGVAPService().restartRegServer(); // ��������ע���������socket
                    appData.getGVAPService().addGvapEventListener(gl);
                    appData.getGVAPService().unbind(
                            appData.getAccountInfo(),
                            appData.getAccountInfo().getDevList()
                                    .getDevice(mID));

                    pd = ProgressDialog.show(getActivity(), "",
                            getString(R.string.unbinding));
                    pd.setCancelable(true);
                }
            });

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            appData = (AppData) activity.getApplication();
        }

        @Override
        public void onStop() {
            super.onStop();

            if (gl != null) {
                appData.getGVAPService().removeGvapEventListener(gl);
            }
        }
    }
}
