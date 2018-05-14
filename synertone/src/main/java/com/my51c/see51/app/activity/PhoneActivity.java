package com.my51c.see51.app.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.domian.Contact;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.List;

public class PhoneActivity extends BaseActivity implements OnClickListener {
    private static final int DTMF_DURATION_MS = 120; // 声音的播放时间
    private static String TAG = "MainActivity";
    private static boolean mDTMFToneEnabled; // 系统参数“按键操作音”标志位
    TextView tv;
    ImageButton but1;
    ImageButton but2;
    ImageButton but3;
    ImageButton but4;
    ImageButton but5;
    ImageButton but6;
    ImageButton but7;
    ImageButton but8;
    ImageButton but9;
    ImageButton but10;
    ImageButton but11;
    ImageButton but12;
    ImageButton but13;
    // ImageButton but14;
    ImageButton but15;
    ImageButton but16;
    List<Contact> list = new ArrayList<Contact>();
    ListView lv;
    private Object mToneGeneratorLock = new Object(); // 监视器对象锁
    private ToneGenerator mToneGenerator; // 声音产生器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_activity);
        // 按键声音播放设置及初始化
        try {
            // 获取系统参数“按键操作音”是否开启
            mDTMFToneEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
            synchronized (mToneGeneratorLock) {
                if (mDTMFToneEnabled && mToneGenerator == null) {
                    mToneGenerator = new ToneGenerator(
                            AudioManager.STREAM_DTMF, 80); // 设置声音的大小
                    setVolumeControlStream(AudioManager.STREAM_DTMF);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            mDTMFToneEnabled = false;
            mToneGenerator = null;
        }
        // lv = (ListView) findViewById(R.id.lv);
        tv = (TextView) findViewById(R.id.tv);

        but1 = (ImageButton) findViewById(R.id.but1);
        but2 = (ImageButton) findViewById(R.id.but2);
        but3 = (ImageButton) findViewById(R.id.but3);
        but4 = (ImageButton) findViewById(R.id.but4);
        but5 = (ImageButton) findViewById(R.id.but5);
        but6 = (ImageButton) findViewById(R.id.but6);
        but7 = (ImageButton) findViewById(R.id.but7);
        but8 = (ImageButton) findViewById(R.id.but8);
        but9 = (ImageButton) findViewById(R.id.but9);
        but10 = (ImageButton) findViewById(R.id.but10);
        but11 = (ImageButton) findViewById(R.id.but11);
        but12 = (ImageButton) findViewById(R.id.but12);
        but13 = (ImageButton) findViewById(R.id.but13);
        // but14 = (ImageButton)findViewById(R.id.but14);
        but15 = (ImageButton) findViewById(R.id.but15);
        but16 = (ImageButton) findViewById(R.id.stop_finish);

        but1.setOnClickListener(this);
        but2.setOnClickListener(this);
        but3.setOnClickListener(this);
        but4.setOnClickListener(this);
        but5.setOnClickListener(this);
        but6.setOnClickListener(this);
        but7.setOnClickListener(this);
        but8.setOnClickListener(this);
        but9.setOnClickListener(this);
        but10.setOnClickListener(this);
        but11.setOnClickListener(this);
        but12.setOnClickListener(this);
        but13.setOnClickListener(this);
        // but14.setOnClickListener(this);
        but15.setOnClickListener(this);
        but16.setOnClickListener(this);
        // 设置长按删除键，触发删除全部
        but15.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                tv.setText("");
                return false;
            }
        });
//		tv.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
//				// 文本变化中
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				// 文本变化前
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// 文本变化后
//				list = MyService.findContacts(PhoneActivity.this, s.toString());
//				// 方法1：自己定义Adapter 每次都new貌似效率不好
//				lv.setAdapter(new MyAdapter());
//			}
//		});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.but1:
                playTone(ToneGenerator.TONE_DTMF_1);
                change("1");
                break;
            case R.id.but2:
                playTone(ToneGenerator.TONE_DTMF_2);
                change("2");
                break;
            case R.id.but3:
                playTone(ToneGenerator.TONE_DTMF_3);
                change("3");
                break;
            case R.id.but4:
                playTone(ToneGenerator.TONE_DTMF_4);
                change("4");
                break;
            case R.id.but5:
                playTone(ToneGenerator.TONE_DTMF_5);
                change("5");
                break;
            case R.id.but6:
                playTone(ToneGenerator.TONE_DTMF_6);
                change("6");
                break;
            case R.id.but7:
                playTone(ToneGenerator.TONE_DTMF_7);
                change("7");
                break;
            case R.id.but8:
                playTone(ToneGenerator.TONE_DTMF_8);
                change("8");
                break;
            case R.id.but9:
                playTone(ToneGenerator.TONE_DTMF_9);
                change("9");
                break;
            case R.id.but10:
                playTone(ToneGenerator.TONE_DTMF_S);
                change("*");
                break;
            case R.id.but11:
                playTone(ToneGenerator.TONE_DTMF_0);
                change("0");
                break;
            case R.id.but12:
                playTone(ToneGenerator.TONE_DTMF_P);
                change("#");
                break;
            case R.id.but13:
                call();
                break;
            // case R.id.but14:

            // break;
            case R.id.but15:
                delete();
                break;
            case R.id.stop_finish:
                finish();
                break;
        }
    }

    /**
     * 播放按键声音
     */
    private void playTone(int tone) {
        if (!mDTMFToneEnabled) {
            return;
        }
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_SILENT
                || ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            // 静音或者震动时不发出声音
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }
            mToneGenerator.startTone(tone, DTMF_DURATION_MS); // 发出声音
        }
    }

    private void change(String number) {
        StringBuffer sb = new StringBuffer(tv.getText());
        tv.setText(sb.append(number));
    }

    /**
     * 点击删除按钮删除操作
     */
    private void delete() {
        if (tv.getText() != null && tv.getText().length() > 1) {
            StringBuffer sb = new StringBuffer(tv.getText());
            tv.setText(sb.substring(0, sb.length() - 1));
        } else if (tv.getText() != null && !"".equals(tv.getText())) {
            tv.setText("");
        }
    }

    public void call() {
        /**
         * 打电话需要获取系统权限，需要到AndroidManifest.xml里面配置权限 <uses-permission
         * android:name="android.permission.CALL_PHONE"/>
         */
        Intent intent = new Intent();
        // 设置意图要做的事，这里是打电话
        intent.setAction(Intent.ACTION_CALL);
        // 设置参数 Uri请求资源表示符
        intent.setData(Uri.parse("tel:" + tv.getText()));
        startActivity(intent);
    }

    /**
     * 自定义Adapter
     *
     * @author Lenovo
     */
    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // 需要遍历集合的size
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            // View.inflate对布局XML文件进行填充，获得View对象（相当于这个R.layout.activity_main）
            // 参数1：容器的Context 参数2：表示填充的哪个布局文件XML
            // 参数3：表示具体填充到哪个容器中，这里由Adapter指定，所以填null
            View v = View.inflate(PhoneActivity.this, R.layout.phoneitem, null);
            // tv.findViewById(R.id.id))获得填充后的布局文件中的具体哪个ID的对象，并赋值
            ((TextView) v.findViewById(R.id.id)).setText("id:"
                    + list.get(position).getId());
            ((TextView) v.findViewById(R.id.name)).setText("name:"
                    + list.get(position).getName());

            String str = "phone:" + list.get(position).getPhone() + "("
                    + list.get(position).getAddress() + ")";
            str = str.replaceFirst(tv.getText().toString(),
                    "<font color=#5db43b>" + tv.getText().toString()
                            + "</font>");
            // 测试一下简单的字体样式
            String html = "<html><head><title>TextView 使用HTML</title></head><body><p><strong>强 调</strong></p><p><em>斜体</em></p>"
                    + "<p><a href=\"http://www.dreamdu.com /xhtml/\">超链接HTML入门</a>学习HTML!< /p><p><font color=\"#aabb00\">颜色1"
                    + "</p><p><font color=\"#00bbaa \">颜色2</p><h1>标题1</h1><h3>标题2< /h3><h6>标题3</h6><p>大于>小于<</p><p>"
                    + "下面是网络图片</p><img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/></body></html>";
            ((TextView) v.findViewById(R.id.phone)).setText(Html.fromHtml(str));
            return v;
        }

    }
}
