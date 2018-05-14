package com.my51c.see51.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.my51c.see51.adapter.SetListAdapter;
import com.my51c.see51.widget.NewSwitch;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashMap;
//snt-add

public class SetFragment extends SherlockFragment {
    private final String TAG = "SetActivity";
    private SetListAdapter adapter = null;
    private ArrayList<HashMap<String, String>> map_SetList = null;
    private ListView setListView;

    private Button btnProgramExit;

    //private TextView txtUser ; //snt-del
    //private ImageView imgUser; //snt-del
    private Button btnLogin;

    private LinearLayout btnNewCamera;
    private LinearLayout btnFourViews;
    private LinearLayout btnMyCameraList;
    private LinearLayout btnLocalList;
    //private LinearLayout btnExit; //snt-del
    private ImageView btnExit;      //snt-add
    private TextView txLocalList;
    private LinearLayout showWarn, warnMsg, about, localVideo;
    //private LinearLayout loginLayout; //snt-del
    //private TextView loginLayoutTx;   //snt-del
    //private ImageView loginLayoutImg; //snt-del
    private NewSwitch warnSwitch;


    public void setIsLoginBtnName(boolean isLogin) {
        if (isAdded()) {
            //snt-del begain
//			if (isLogin) {
//				loginLayoutTx.setText(getResources().getString(R.string.logout));
//				loginLayoutImg.setBackgroundResource(R.drawable.exit_img);
//				btnLogin.setText(R.string.logout);
//			}else {
//				loginLayoutTx.setText(getString(R.string._clicktologin));
//				//txtUser.setTextColor(getResources().getColor(color.white));
//				loginLayoutImg.setBackgroundResource(R.drawable.gointo_img);
//				btnLogin.setText(R.string.login);
//			}
            //snt-del end
        }
    }

    @Override
    public void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        //Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.set_frame, container,
                false);
        map_SetList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapAlarm = new HashMap<String, String>();
        String alarmTitle = getResources().getString(R.string.alarmNotification);
        mapAlarm.put("item", alarmTitle);
        map_SetList.add(mapAlarm);
        HashMap<String, String> mapHistory = new HashMap<String, String>();
        String historyTitle = getResources().getString(R.string.alarmNotification);
        mapHistory.put("item", historyTitle);
        map_SetList.add(mapHistory);

        setListView = (ListView) v.findViewById(R.id.setListView);
        btnNewCamera = (LinearLayout) v.findViewById(R.id.btnNewCamera);
        btnProgramExit = (Button) v.findViewById(R.id.btnProgramExit);
        btnLocalList = (LinearLayout) v.findViewById(R.id.btnLocalCamera);
        btnMyCameraList = (LinearLayout) v.findViewById(R.id.btnMyCamera);
        btnFourViews = (LinearLayout) v.findViewById(R.id.btnFourViews);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        txLocalList = (TextView) v.findViewById(R.id.txLocalList);
        txLocalList.setText(R.string.landev);
        btnExit = (ImageView) v.findViewById(R.id.progamexit);//snt-modi

        //txtUser = (TextView) v.findViewById(R.id.txtUser);   //snt-del
        //imgUser = (ImageView) v.findViewById(R.id.imgUser);  //snt-del
        showWarn = (LinearLayout) v.findViewById(R.id.showWarn);
        warnMsg = (LinearLayout) v.findViewById(R.id.warnMsg);
        //changePsw = (LinearLayout)v.findViewById(R.id.changePsw); //snt-del
        about = (LinearLayout) v.findViewById(R.id.about);
        localVideo = (LinearLayout) v.findViewById(R.id.local_video);
//		loginLayout = (LinearLayout)v.findViewById(R.id.loginLayout); //snt-del
//		loginLayoutTx = (TextView)v.findViewById(R.id.loginLayoutTx); //snt-del
//		loginLayoutImg = (ImageView)v.findViewById(R.id.loginLayoutImg); //snt-del
        warnSwitch = (NewSwitch) v.findViewById(R.id.showWarnSwitch);
        warnSwitch.setChecked(false);


        showWarn.setOnClickListener((MainActivity) getActivity());
        warnMsg.setOnClickListener((MainActivity) getActivity());
        //changePsw.setOnClickListener((MainActivity)getActivity()); //snt-del
        about.setOnClickListener((MainActivity) getActivity());
        localVideo.setOnClickListener((MainActivity) getActivity());
        //loginLayout.setOnClickListener((MainActivity)getActivity());  //snt-del
        warnSwitch.setOnCheckedChangeListener((MainActivity) getActivity());
        btnExit.setOnClickListener((MainActivity) getActivity());


        btnNewCamera.setOnClickListener((MainActivity) getActivity());
        btnProgramExit.setOnClickListener((MainActivity) getActivity());
        btnLocalList.setOnClickListener((MainActivity) getActivity());
        btnMyCameraList.setOnClickListener((MainActivity) getActivity());
        btnFourViews.setOnClickListener((MainActivity) getActivity());
        btnLogin.setOnClickListener((MainActivity) getActivity());

        setIsLoginBtnName(MainActivity.isLogin);
        return v;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //Log.d(TAG, "onResume");

        releaseadapter();
        adapter = new SetListAdapter(getActivity(), (MainActivity) getActivity());
        MainActivity mainactivity = (MainActivity) getActivity();
        setListView.setOnItemClickListener(mainactivity);

        setIsLoginBtnName(MainActivity.isLogin);
        setListView.setAdapter(adapter);
    }

    private void releaseadapter() {
        if (adapter != null) {
            adapter.release();
            adapter = null;
        }
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

    }

    public void setLoginState(String user) {
        //snt-del-begain
//		txtUser.setText(user);
//		txtUser.setTextColor(color.orange);
//		if(user.equals(getString(R.string.notLogedIn)))
//			imgUser.setBackgroundResource(R.drawable.vcard);
//		else
//			imgUser.setBackgroundResource(R.drawable.vcard_active);
        //snt-del-end
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();

        showWarn.setOnClickListener(null);
        warnMsg.setOnClickListener(null);
        //changePsw.setOnClickListener(null);  //snt-del
        about.setOnClickListener(null);
        localVideo.setOnClickListener(null);
        //	loginLayout.setOnClickListener(null);  //snt-del
        warnSwitch.setOnCheckedChangeListener(null);
        btnExit.setOnClickListener(null);

        btnNewCamera.setOnClickListener(null);
        btnProgramExit.setOnClickListener(null);
        btnLocalList.setOnClickListener(null);
        btnMyCameraList.setOnClickListener(null);
        btnFourViews.setOnClickListener(null);
        btnLogin.setOnClickListener(null);
        setListView.setOnItemClickListener(null);
        releaseadapter();
    }
}
