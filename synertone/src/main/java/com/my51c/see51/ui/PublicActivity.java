package com.my51c.see51.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.adapter.DeviceListAdapter;
import com.my51c.see51.adapter.DeviceListAdapter.ViewHolder;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.Group;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.synertone.netAssistant.R;

public class PublicActivity extends SherlockFragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            PublicDevicesFragment list = new PublicDevicesFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, list).commit();
        }
    }

    public static class PublicDevicesFragment extends SherlockListFragment
            implements DeviceListListener, OnClickListener, OnRefreshListener {
        static final int MSG_UPDATE = 0;
        static final int MSG_GVAP = 1;
        private DeviceListView listView;
        private DeviceListAdapter adapter;
        private AppData appData;
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE:
                        adapter.updateDeviceData();
                        adapter.notifyDataSetChanged();
                        break;
                /*
				 * case MSG_GVAP: switch ((GvapEvent)msg.obj) { case
				 * OPERATION_SUCCESS: break; default:
				 * Toast.makeText(getActivity(), getString(R.string.nonetwork),
				 * Toast.LENGTH_LONG ).show(); break; }
				 */
                    default:
                        break;
                }
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // menu.add("add").setIcon(R.drawable.refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add("refresh").setIcon(R.drawable.refresh)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            appData.getPublicList().clear();
            appData.getGVAPService().getPublicList();

            if (adapter != null) {
                adapter.updateDeviceData();
            }
            // mHandler.sendEmptyMessage(0);
            return false;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.layout_device_list, container,
                    false);
            listView = (DeviceListView) v.findViewById(android.R.id.list);
            listView.setItemsCanFocus(true);
            listView.setonRefreshListener(this);

			/*
			 * if(appData.getAccountInfo() == null ||
			 * !appData.getAccountInfo().isLogined()) { AccountInfo guest = new
			 * AccountInfo("guest", "guest");
			 * if(appData.getGVAPService()==null){ Toast.makeText(getActivity(),
			 * getString(R.string.wait), Toast.LENGTH_LONG ).show(); }else{
			 * appData.getGVAPService().addGvapEventListener(new
			 * GvapEventListener() {
			 * 
			 * @Override public void onGvapEvent(GvapEvent event) { // TODO
			 * Auto-generated method stub Message msg =
			 * mHandler.obtainMessage(MSG_GVAP, event);
			 * mHandler.sendMessage(msg); } });
			 * appData.getGVAPService().login(guest); }
			 * appData.setAccountInfo(guest); }
			 */
            return v;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            //Log.i("FragmentList", "Item clicked: " + id);
            String Gro_Dev = ((ViewHolder) v.getTag()).Gro_Dev;
            if (Gro_Dev.equals("device")) {
                Device dev = (Device) ((ViewHolder) v.getTag()).info.getTag();
                if (dev != null) {
                    Intent intent = new Intent(this.getActivity(),
                            PlayerActivity.class);
                    intent.putExtra("id", dev.getID());
                    intent.putExtra("url", dev.getPlayURL());
                    intent.putExtra("title",
                            ((ViewHolder) v.getTag()).title.getText());
                    startActivity(intent);
                }
            } else if (Gro_Dev.equals("group")) {
                Group parent_group = ((ViewHolder) v.getTag()).group;
                String groupId = parent_group.getGroupID();
                String grandParent_group = ((ViewHolder) v.getTag()).grandParent_group;
                if (appData.getAccountInfo().getDevList(groupId) == null) {
                    appData.getAccountInfo().addList(parent_group, grandParent_group);
                    appData.getGVAPService().getDeviceList(groupId);
                }
                DeviceList devList = appData.getAccountInfo().getDevList(groupId);
                appData.getAccountInfo().setCurrentList(devList);
                devList.addListListener(this);
                adapter.setDeviceList(devList);
                devList.listUpdated();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            //Log.d("PublicActivity", "onPause()");
        }

        @Override
        public void onStop() {
            super.onStop();
            //Log.d("PublicActivity", "onStop()");
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            //Log.d("PublicActivity", "onDestroyView()");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            //Log.d("PublicActivity", "onDestroy()");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            //Log.d("PublicActivity", "onDetach()");
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            appData = (AppData) activity.getApplication();

            DeviceList publicList = appData.getPublicList();
            publicList.addListListener(this);
            adapter = new DeviceListAdapter(this.getActivity(), publicList,
                    this, true);
            setListAdapter(adapter);
        }

        @Override
        public void onListUpdate() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(MSG_UPDATE);
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // ÏìÓ¦info°´¼ü
            //Log.d("LocalActivity", "info button clicked");
        }

        @Override
        public void onRefresh() {
            // TODO Auto-generated method stub
            appData.getGVAPService().getPublicList();
            if (adapter != null) {
                adapter.updateDeviceData();
            }
        }
    }
}
