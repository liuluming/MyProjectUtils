package com.my51c.see51.ui;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.LocalFileAdapter;
import com.synertone.netAssistant.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public class LocalPicVideoAcy extends BaseActivity implements MultiChoiceModeListener, OnClickListener, OnItemClickListener {

    private final String TAG = "PicVideoAcy";
    public MyHandler mHandler = new MyHandler(this);
    private ListView listView;
    private LocalFileAdapter adapter;
    private ArrayList<File> fileList = null;
    private ArrayList<Bitmap> thumbNailList = null;
    private RelativeLayout titleLayout;
    private LinearLayout backLayout;
    private TextView countTx;
    private HashSet<String> delList = null;
    private TextView title;
    private boolean isRunning = false;
    private int runTime = 0;

    public static void isFolderExists(String strFolder) {
        File file = new File(strFolder);

        if (!file.exists()) {
            file.mkdirs();
            System.out.println("�½��ļ��гɹ�");
        } else {
            System.out.println("�ļ����Ѵ���");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_video_acy);
        findView();
        setListView();
    }

    private void findView() {
        listView = (ListView) findViewById(R.id.pic_list);
        titleLayout = (RelativeLayout) findViewById(R.id.local_titleLayout);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        title = (TextView) findViewById(R.id.local_title);
        backLayout.setOnClickListener(this);
    }

    public void setListView() {
        fileList = new ArrayList<File>();
        thumbNailList = new ArrayList<Bitmap>();
        final String videoPath, imgPath;

        boolean isSD = getIntent().getBooleanExtra("isSD", false);
        if (isSD) {
            title.setText(getIntent().getStringExtra("devName"));
            imgPath = videoPath = getIntent().getStringExtra("devPath");
            Log.i(TAG, "devPath:" + imgPath);
            isFolderExists(videoPath);
        } else {
            videoPath = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/video";
            isFolderExists(videoPath);
            imgPath = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/image";
            isFolderExists(imgPath);
        }


        GetFiles(this, videoPath, ".mp4", true);
        GetFiles(this, imgPath, ".jpg", true);
        listView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);
        listView.setOnItemClickListener(this);
        adapter = new LocalFileAdapter(getApplicationContext(), fileList, thumbNailList, false);
        listView.setAdapter(adapter);
    }

    public void GetFiles(Context context, String Path, String Extension, boolean IsIterative) //����Ŀ¼����չ�����Ƿ�������ļ���
    {
        File[] files = new File(Path).listFiles();
        files = new File(Path).listFiles();
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cam_default_icon);
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //�ж���չ��
                {

//                	if(thumbNailList!=null){
//                		if(Extension.equals(".jpg")){
//                    		tempBitmap = getImageThumbnail(f.getAbsolutePath(), 150, 120);
//                    	}else{
//                    		tempBitmap = getVideoThumbnail(f.getAbsolutePath(), 150, 120, MediaStore.Images.Thumbnails.MICRO_KIND);//bug
//                    	}
//                		thumbNailList.add(tempBitmap);//��ʱ
//                	}
//                	if(f.length()>100){
                    if (f.length() == 0) {
                        f.delete();
                        continue;
                    }

                    fileList.add(f);
//                	}
                }
                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //���Ե��ļ��������ļ�/�ļ��У�
                GetFiles(context, f.getPath(), Extension, IsIterative);
        }

        new ThumbnailThread(this).start();
    }

    public Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // ��Ϊ false
        // �������ű�
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // ��ȡ��Ƶ������ͼ
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        File f = fileList.get(position);
        String path = f.getAbsolutePath();
        boolean isjpg = f.getName().contains("jpg");
        Log.i(TAG, "onItemClick:" + path + "-" + isjpg);
        if (isjpg) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(f), "image/jpeg");
            startActivity(intent);
        } else {
            Intent intent = new Intent(LocalPicVideoAcy.this, PlayAcy.class);
            intent.putExtra("string", f.getAbsolutePath());
            intent.putExtra("name", f.getName());
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backMainActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        System.gc();
        runTime = 0;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.backLayout:
                backMainActivity();
                break;

            default:
                break;
        }
    }

    public void backMainActivity() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_select:
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, true);
                    delList.add(String.valueOf(i));
                }
                break;
            case R.id.menu_unselect:
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, false);
                }
                delList.clear();
                break;
            case R.id.menu_delete:
                final Dialog dialog = new Dialog(LocalPicVideoAcy.this, R.style.Erro_Dialog);
                dialog.setContentView(R.layout.del_dialog);
                Button sure = (Button) dialog.findViewById(R.id.del_ok);
                Button cancel = (Button) dialog.findViewById(R.id.del_cancel);
                TextView delTx = (TextView) dialog.findViewById(R.id.erroTx);
                delTx.setText(getResources().getString(R.string.delete_for_sure));
                cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });

                sure.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        HashSet<File> delFiles = new HashSet<File>();
                        for (String s : delList) {
                            int position = Integer.parseInt(s);
                            File f = fileList.get(position);
                            delFiles.add(f);
                        }
                        for (File f : delFiles) {
                            try {
                                thumbNailList.remove(fileList.indexOf(f));
                            } catch (IndexOutOfBoundsException e) {
                                // TODO Auto-generated catch block
                                thumbNailList.clear();
                                e.printStackTrace();
                            }
                            fileList.remove(f);
                            f.delete();
                        }
                        mHandler.sendEmptyMessage(1);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        titleLayout.setVisibility(View.GONE);
        delList = new HashSet<String>();
        listView.setAdapter(new LocalFileAdapter(getApplicationContext(), fileList, thumbNailList, true));
        View v = LayoutInflater.from(this).inflate(R.layout.bar_layout, null);
        countTx = (TextView) v.findViewById(R.id.action_text);
        countTx.setText(String.format(getString(R.string.select_num), listView.getCheckedItemCount()));
        mode.setCustomView(v);
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode arg0) {
        // TODO Auto-generated method stub
        listView.setAdapter(new LocalFileAdapter(getApplicationContext(), fileList, thumbNailList, false));
        titleLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode arg0, Menu menu) {
        // TODO Auto-generated method stub
        menu.getItem(1).setEnabled(listView.getCheckedItemCount() != listView.getCount());
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // TODO Auto-generated method stub
        countTx.setText(String.format(getString(R.string.select_num), listView.getCheckedItemCount()));
        if (checked) {
            delList.add(String.valueOf(position));
        } else {
            delList.remove(String.valueOf(position));
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<LocalPicVideoAcy> mRef;

        public MyHandler(LocalPicVideoAcy mAct) {
            mRef = new WeakReference<LocalPicVideoAcy>(mAct);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRef == null)
                return;

            LocalPicVideoAcy mActivity = mRef.get();

            if (mActivity != null) {
                switch (msg.what) {
                    case 0:
                        mActivity.adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        mActivity.listView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
                        mActivity.listView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
                        mActivity.adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        File f = (File) msg.obj;
                        Bitmap tempBitmap = null;
                        if (f.getName().contains("jpg")) {
                            tempBitmap = mActivity.getImageThumbnail(f.getAbsolutePath(), 150, 120);
                        } else {
                            tempBitmap = mActivity.getVideoThumbnail(f.getAbsolutePath(), 150, 120, MediaStore.Images.Thumbnails.MICRO_KIND);
                        }
                        mActivity.thumbNailList.add(tempBitmap);
                        mActivity.mHandler.sendEmptyMessage(0);
                        break;

                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    public static class ThumbnailThread extends Thread {
        Bitmap tempBitmap = null;
        private WeakReference<LocalPicVideoAcy> mRef;

        public ThumbnailThread(LocalPicVideoAcy mAct) {
            mRef = new WeakReference<LocalPicVideoAcy>(mAct);
        }

        @Override
        public void run() {
            if (mRef == null)
                return;

            LocalPicVideoAcy mActivity = mRef.get();

            if (mActivity != null) {
                if (mActivity.runTime < 3) {
                    mActivity.runTime++;
                    mActivity.thumbNailList.clear();
                    for (int i = 0; i < mActivity.fileList.size(); i++) {
                        File f = mActivity.fileList.get(i);
                        if (f.getName().contains("jpg")) {
                            tempBitmap = mActivity.getImageThumbnail(f.getAbsolutePath(), 60, 60);

                        } else {

                            tempBitmap = mActivity.getVideoThumbnail(f.getAbsolutePath(), 60, 60, MediaStore.Images.Thumbnails.MICRO_KIND);
                        }
                        mActivity.thumbNailList.add(tempBitmap);
                    }
                    mActivity.mHandler.sendEmptyMessage(0);
                }
            }
            super.run();
        }
    }


}
