




package com.my51c.see51.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.listener.OnSetRFInfoListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.protocal.RFPackage;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RFDeviceListFragment extends SherlockListFragment implements OnItemLongClickListener {
    static public RemoteInteractionStreamer mediastream;
    private static Device mDevice;
    private static RFPackage rfpack;
    static private ArrayList<Map<String, Object>> mRFList;
    private static String TAG = "RFDeviceListFragment";
    private static String curid = null;
    private int itemnum;
    private MyAdapter adapter = null;
    private AppData appData;
    private int[] rfimagelog = {R.drawable.grid_controller, R.drawable.grid_doorsensor, R.drawable.grid_pirsensor,
            R.drawable.grid_smokesensor, R.drawable.grid_smartplug, R.drawable.grid_soundlight,
            R.drawable.grid_doorcamera, R.drawable.grid_iosensor, R.drawable.grid_blp};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.rfdevicelist, container, false);
        Bundle bundle = getArguments();
        itemnum = bundle.getInt("itemnum");

        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediastream != null) {
            mediastream = null;
        }
    }

    public void onRefreshRFDevice()//ˢ��RF�豸
    {
        mDevice = ((RFDeviceInfoActivity) getActivity()).getParseDevice();

        rfpack = mDevice.getRFInfo();

        appData = (AppData) getActivity().getApplication();

        mediastream = appData.getRemoteInteractionStreamer();

        ParseDevice(itemnum);
        if (adapter == null) {
            adapter = new MyAdapter(getActivity());
            setListAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {//��ʼ��rfdev
        // TODO Auto-generated method stub
        super.onResume();

        mDevice = ((RFDeviceInfoActivity) getActivity()).getParseDevice();//��ȡdevice
        rfpack = mDevice.getRFInfo();                                    //RFPackage

        appData = (AppData) getActivity().getApplication();
        mediastream = appData.getRemoteInteractionStreamer();

        ParseDevice(itemnum);                                        //��ʼ�� mRFList
        adapter = new MyAdapter(getActivity());
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(this);
    }

    private void ParseDevice(int nItem) {
        if (mRFList == null) {
            mRFList = new ArrayList<Map<String, Object>>();
        }

        mRFList.clear();

        if (rfpack == null) {
            return;
        }


        List<Map<String, Object>> mRFTotalList = rfpack.getRFDevList();//��ȡ��������rf�豸��Ϣ��list
        if (mRFTotalList == null) {
            return;
        }

        for (int i = 0; i < mRFTotalList.size(); i++)//����rfinfoList������gridview--positionȡ����Ӧmap
        {
            HashMap<String, Object> map = (HashMap<String, Object>) mRFTotalList.get(i);
            String strID = (String) map.get("MY51CRFID");
            String strType = strID.substring(0, 2);
            if (nItem == 0) {
                if (strType.equals("01")) {
                    mRFList.add(map);
                }
            } else if (nItem == 1) {
                if (strType.equals("02")) {
                    mRFList.add(map);
                }
            } else if (nItem == 2) {
                if (strType.equals("03")) {
                    mRFList.add(map);
                }
            } else if (nItem == 3) {
                if (strType.equals("04")) {
                    mRFList.add(map);
                }
            } else if (nItem == 4) {
                if (strType.equals("10")) {
                    mRFList.add(map);
                }
            } else if (nItem == 5) {
                if (strType.equals("21")) {
                    mRFList.add(map);
                }
            } else if (nItem == 6) {
                if (strType.equals("22")) {
                    mRFList.add(map);
                }
            } else if (nItem == 7) {
                if (strType.equals("23")) {
                    mRFList.add(map);
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   final int position, long id) {
        // TODO Auto-generated method stub

        new AlertDialog.Builder(getActivity()).setTitle(R.string.sure).setMessage(R.string.isdeleterfdevice)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        RFPackage rpackcloneother = new RFPackage();
                        rpackcloneother.parseArrayList(rfpack.getRFDevList());

                        Map<String, Object> mTempInfo = RFDeviceListFragment.mRFList.get(position);

                        rpackcloneother.RemoveId((String) mTempInfo.get("MY51CRFID"));

                        WaitingDialogFragment.newInstance(position, rpackcloneother).show(getFragmentManager(), "waiting");


                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked cancel so do some stuff */
                    }
                })
                .create()
                .show();
        return false;
    }

    public static class RFInfoEditDialog extends SherlockDialogFragment {
        private static RFPackage rpackclone;
        Map<String, Object> mRFDevceInfo;
        private EditText rfnameedittext;
        private CompoundButton rfstatus;
        private TextView rfnumtextview;
        private int nIndex;

        public static RFInfoEditDialog newInstance(int nWhich) {
            RFInfoEditDialog rfInfoFragment = new RFInfoEditDialog();
            rfInfoFragment.nIndex = nWhich;
            return rfInfoFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            getDialog().setTitle(getString(R.string.general));
            View view = inflater.inflate(R.layout.rfinfoeditdialog, container, false);
            rfnameedittext = (EditText) view.findViewById(R.id.rfnameEditText);
            rfstatus = (CompoundButton) view.findViewById(R.id.rfstatus);
            rfnumtextview = (TextView) view.findViewById(R.id.textViewRFidVidew);

            rpackclone = new RFPackage();
            rpackclone.parseArrayList(rfpack.getRFDevList());
            //mRFDevceInfo = RFDeviceListFragment.mRFList.get(nIndex);
            mRFDevceInfo = RFDeviceListFragment.mRFList.get(nIndex);
            rfnumtextview.setText((String) mRFDevceInfo.get("MY51CRFID"));
            rfnameedittext.setText((String) mRFDevceInfo.get("name"));

            rfnameedittext.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    try {
                        rpackclone.setValue((String) mRFDevceInfo.get("MY51CRFID"), "name",
                                rfnameedittext.getText().toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });

            String status = (String) mRFDevceInfo.get("status");

            if (status.equals("on")) {
                rfstatus.setChecked(true);
            } else {
                rfstatus.setChecked(false);
            }

            rfstatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        rpackclone.setValue((String) mRFDevceInfo.get("MY51CRFID"), "status", "on");
                    } else {
                        rpackclone.setValue((String) mRFDevceInfo.get("MY51CRFID"), "status", "off");
                    }
                }
            });


            Button yesButton = (Button) view.findViewById(R.id.buttonOK);
            yesButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    curid = (String) mRFDevceInfo.get("MY51CRFID");
                    WaitingDialogFragment.newInstance(nIndex, rpackclone).show(getFragmentManager(), "waiting");
                    getDialog().dismiss();
                }
            });


            Button noButton = (Button) view.findViewById(R.id.buttonCancel);
            noButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    getDialog().dismiss();

                }
            });

            return view;
        }

        @Override
        public void onActivityCreated(Bundle arg0) {
            // TODO Auto-generated method stub
            super.onActivityCreated(arg0);

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
        int which;
        Activity mActivity;
        Handler handler;
        /**
         * ����RF�豸ʱ��ʾProgressDialog
         */
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

        public static WaitingDialogFragment newInstance(int which, RFPackage inPack) {
            WaitingDialogFragment frag = new WaitingDialogFragment(inPack);
            Bundle args = new Bundle();
            args.putInt("which", which);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            which = getArguments().getInt("which");

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

            asyncTask = new TimeOutAsyncTask(this);//������������
            asyncTask.execute(0);
            return pd;
        }

        @Override
        public void onStop() {
            super.onStop();

            handler.removeCallbacksAndMessages(null);
            if (!asyncTask.isCancelled()) {
                asyncTask.cancel(true);
                //mediastream.setOnSetRFInfoListener(null);
            }
        }

        public void handleMessage(Message msg) {
            if (msg.what == MSG_SET_SUCESS) {
                mDevice.setRFInfo(mRFPack);//��������ͳɹ���reset--RFpackage
                showToast(getString(R.string.rfsettingsuccess));
                ((RFDeviceInfoActivity) mActivity).onRefreshRFInfo();
                asyncTask.cancel(true);
                pd.cancel();
            } else if (msg.what == MSG_SET_FAILED) {

                showToast(getString(R.string.rfsettingfail));
                asyncTask.cancel(true);
                pd.cancel();
            } else if (msg.what == MSG_SET_TIMEOUT) {
//				if (which != 2) {
//
//				}
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
                mRef = new WeakReference<RFDeviceListFragment.WaitingDialogFragment>(mFragment);
            }

            @Override
            protected void onPreExecute() {
                //��һ��ִ�з���
                super.onPreExecute();
                mediastream.sendRFDevInfo(mRFPack, curid);//��������
                curid = null;
                //Log.d(TAG, " onPreExecute " );
            }

            @Override
            protected String doInBackground(Integer... params) {
                // TODO Auto-generated method stub
                try {
                    //Log.d(TAG, " sleep " );

                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    //Log.d(TAG, " InterruptedException " );
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

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

    public final class ViewHolder {
        public ImageView img;
        public ImageView camimage;
        public TextView rfname;
        public TextView rfid;
        public ImageButton editBtn;
    }

    public class MyAdapter extends BaseAdapter {//rfinfolist---adapter---listFragment

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mRFList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.rfdevice_list_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.rfdevicelogo);
                holder.camimage = (ImageView) convertView.findViewById(R.id.cam_image);
                holder.rfname = (TextView) convertView.findViewById(R.id.rfdevicename);
                holder.rfid = (TextView) convertView.findViewById(R.id.rfdeviceid);
                holder.editBtn = (ImageButton) convertView.findViewById(R.id.rfdevice_edit);
                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.img.setBackgroundResource(R.drawable.dev_online);
            holder.camimage.setBackgroundResource(rfimagelog[itemnum]);
            holder.rfid.setText((String) mRFList.get(position).get("MY51CRFID"));
            holder.rfname.setText((String) mRFList.get(position).get("name"));
            holder.editBtn.setBackgroundResource(R.drawable.setting_style);
            holder.editBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    RFInfoEditDialog ft = RFInfoEditDialog.newInstance(position);
                    if (ft != null) {
                        ft.show(getFragmentManager(), "");
                    }

                }
            });

            return convertView;
        }
    }

}
