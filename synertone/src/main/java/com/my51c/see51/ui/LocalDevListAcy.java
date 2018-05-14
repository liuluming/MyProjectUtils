package com.my51c.see51.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LocalDevListAcy extends BaseActivity implements OnItemClickListener {

    public boolean isResume = false;
    public MyHandler mHandler = new MyHandler(this);
    private ListView devList;
    private LinearLayout backLayout;
    private ArrayList<File> folderList;
    private ArrayList<File> fileList = null;
    private ArrayList<File> nullList = null;
    private DevAdapter adapter;

    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {//��ɾ�����ļ���
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sd_devlist_acy);
        devList = (ListView) findViewById(R.id.dev_list);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        setDevList();
    }

    public void setDevList() {
        devList.setOnItemClickListener(this);
        folderList = new ArrayList<File>();
        fileList = new ArrayList<File>();
        nullList = new ArrayList<File>();
        String path = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/SDRecord";
        LocalPicVideoAcy.isFolderExists(path);
        File[] files = new File(path).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                folderList.add(files[i]);
            }
        }
        adapter = new DevAdapter();
        devList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {

//		�ж��Ƿ�ȫɾ
        System.out.println("---------onResume---------");
        if (isResume) {
            nullList.clear();
            adapter.notifyDataSetChanged();
            mHandler.sendEmptyMessageDelayed(0, 1500);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(LocalDevListAcy.this, LocalPicVideoAcy.class);
        intent.putExtra("isSD", true);
        intent.putExtra("devName", folderList.get(position).getName());
        intent.putExtra("devPath", folderList.get(position).getAbsolutePath());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    private static class MyHandler extends Handler {
        private WeakReference<LocalDevListAcy> mRef;

        public MyHandler(LocalDevListAcy mAct) {
            mRef = new WeakReference<LocalDevListAcy>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRef == null)
                return;

            LocalDevListAcy activity = mRef.get();
            if (activity != null) {
                try {
                    for (int i = 0; i < activity.devList.getCount(); i++) {
                        if (((TextView) activity.devList.getChildAt(i).findViewById(R.id.local_folder_count)).
                                getText().toString().trim().equals("0")) {
                            activity.nullList.add(activity.folderList.get(i));
                        }
                    }

                    for (int i = 0; i < activity.nullList.size(); i++) {
                        File f = activity.nullList.get(i);
                        activity.folderList.remove(f);
                        RecursionDeleteFile(f);
                    }
                    activity.adapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    System.out.println("----------" + e);
                    e.printStackTrace();
                }
            }
            super.handleMessage(msg);
        }
    }

    private class DevAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return folderList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.local_folder_item, null);
            TextView name = (TextView) convertView.findViewById(R.id.local_folder_filename);
            TextView count = (TextView) convertView.findViewById(R.id.local_folder_count);
            name.setText(folderList.get(position).getName());

            String path = folderList.get(position).getAbsolutePath();
            fileList.clear();
            GetFiles(getApplicationContext(), path, ".jpg", true);
            GetFiles(getApplicationContext(), path, ".mp4", true);
            count.setText(fileList.size() + "");
            isResume = true;
            return convertView;
        }

        public void GetFiles(Context context, String Path, String Extension, boolean IsIterative) //����Ŀ¼����չ�����Ƿ�������ļ���
        {
            File[] files = new File(Path).listFiles();
            files = new File(Path).listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isFile()) {
                    if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //�ж���չ��
                    {

                        fileList.add(f);
                    }
                    if (!IsIterative)
                        break;
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //���Ե��ļ��������ļ�/�ļ��У�
                    GetFiles(context, f.getPath(), Extension, IsIterative);
            }
        }

    }


}
