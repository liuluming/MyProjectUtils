package com.my51c.see51.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.app.activity.IPSpeedActivity;
import com.my51c.see51.app.domian.IpSpeed;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.List;

public class IpspeedAdapter extends BaseAdapter {
    protected static final String TAG = "IpSpeedAdapter";
    public static boolean mCurrIPsFlag = false;
    public static int currPosition;
    String statu;
    private List<IpSpeed> mIpSpeed;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int page;
    private RequestQueue mRequestQueue;
    private List<String> mSprinner;
    private List<String> mAddSprinner;
    //private SpinnerAdapter addAdapter;
    private boolean statuTag;
    private ArrayAdapter<String> arrayAdapter;
    //private SpinnerAdapter arrayAdapter;
    private ArrayAdapter<String> addAdapter;
    private OnListRemovedListener mListener;
    private String ipStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    /*
     * public  IpspeedAdapter(List<IpSpeed> mIpSpeed, Context mContext, int page,
			RequestQueue mRequestQueue, List<String> mSprinner,List<String> mAddSprinner)
     * */
    public IpspeedAdapter(List<IpSpeed> mIpSpeed, Context mContext, int page,
                          RequestQueue mRequestQueue, List<String> mSprinner, List<String> mAddSprinner) {
        super();
        this.mIpSpeed = mIpSpeed;
        //	Log.e(TAG, "IpspeedAdapter-----mIpSpeed============>"+mIpSpeed.get(currPosition).getIp());
        this.mContext = mContext;
        this.page = page;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mRequestQueue = mRequestQueue;
        this.mSprinner = mSprinner;
        this.mAddSprinner = mAddSprinner;
        mRequestQueue = Volley.newRequestQueue(mContext);
        arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mSprinner);
        arrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        addAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mAddSprinner);
        addAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*arrayAdapter = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mSprinner);
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		addAdapter = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mAddSprinner);
		addAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
    }

    public void setOnListRemovedListener(OnListRemovedListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        // return mIpSpeed.size();
        return null == mIpSpeed ? 0 : mIpSpeed.size();
    }

    @Override
    public IpSpeed getItem(int position) {
        return mIpSpeed.get(position);
    }

    @Override
    public long getItemId(int position) {

        currPosition = position;
        return position;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final IpSpeedViewHolder vh;
        if (convertView == null) {
            vh = new IpSpeedViewHolder();
            convertView = layoutInflater.inflate(R.layout.ipspeed_item, null);
            //这是设置字体，勿删
            vh.ip_choose = (TextView) convertView
                    .findViewById(R.id.ip_choose);
            vh.ip_choose.setTypeface(AppData.fontXiti);
            vh.ip_username = (TextView) convertView
                    .findViewById(R.id.ip_username);
            vh.ip_username.setTypeface(AppData.fontXiti);
            vh.ip_downloadmax = (TextView) convertView
                    .findViewById(R.id.ip_downloadmax);
            vh.ip_downloadmax.setTypeface(AppData.fontXiti);
            vh.ip_upmax = (TextView) convertView
                    .findViewById(R.id.ip_upmax);
            vh.ip_upmax.setTypeface(AppData.fontXiti);
            vh.ip_youxianji = (TextView) convertView
                    .findViewById(R.id.ip_youxianji);
            vh.ip_youxianji.setTypeface(AppData.fontXiti);

            vh.textBtIp = (TextView) convertView
                    .findViewById(R.id.ipspeed_used);
            // vh.textSelcet = (TextView) convertView
            // .findViewById(R.id.ipspeed_ip_select_text);
            // vh.textSelcet.setTag(position);
            vh.ipSpinner = (Spinner) convertView
                    .findViewById(R.id.ipspeed_select);

            vh.ipSpinnerAdd = (Spinner) convertView
                    .findViewById(R.id.ipspeed_select_add);

            vh.downBw = (EditText) convertView
                    .findViewById(R.id.ipspeed_download_bw);
            vh.downBw.setTag(position);

            vh.upBw = (EditText) convertView.findViewById(R.id.ipspeed_up_bw);
            vh.upBw.setTag(position);


            vh.ipSpinnerFir = (Spinner) convertView
                    .findViewById(R.id.ipspeed_first);

            // 点击删除
            vh.delButton = (Button) convertView
                    .findViewById(R.id.ipspeed_delete);

            //设置按钮
            vh.setButton = (Button) convertView.findViewById(R.id.ipspeed_set);

            convertView.setTag(vh);
        } else {

            vh = (IpSpeedViewHolder) convertView.getTag();
            vh.downBw.setTag(position);

        }

        vh.ipSpinner.setOnItemSelectedListener(new MySpinnerIpListener(vh) {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3, IpSpeedViewHolder holder) {
                TextView tv = (TextView) arg1;
                //	posit=arg2;
                vh.ipSpinner.setSelection(arg2);
                Log.e(TAG, "IpspeedAdapter-----arg2============>" + arg2);
                int position = (Integer) vh.downBw.getTag();
                IpSpeed n = mIpSpeed.get(position);
                Log.e(TAG, "IpspeedAdapter-----tv.getText().toString()============>" + tv.getText().toString());
                Log.e(TAG, "IpspeedAdapter-----n.getIp()============>" + n.getIp().toString());
                n.setIp(tv.getText().toString());
                mIpSpeed.set(position, n);
                Log.e(TAG, "IpspeedAdapter----mIpSpeed.get(position).getIp()============>" + mIpSpeed.get(position).getIp());
                arrayAdapter.notifyDataSetChanged();
            }
        });
        vh.ipSpinnerAdd.setOnItemSelectedListener(new MySpinnerIpListener(vh) {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3, IpSpeedViewHolder holder) {
                TextView tv = (TextView) arg1;
                vh.ipSpinnerAdd.setSelection(arg2);
                int position = (Integer) vh.downBw.getTag();
                IpSpeed n = mIpSpeed.get(position);
                n.setIp(tv.getText().toString());
                mIpSpeed.set(position, n);
                addAdapter.notifyDataSetChanged();
            }
        });

        vh.downBw.addTextChangedListener(new MyTextIpWatcher(vh) {
            @Override
            public void afterTextChanged(Editable s,
                                         IpSpeedViewHolder holder) {
                int position = (Integer) vh.downBw.getTag();
                IpSpeed n = mIpSpeed.get(position);
                n.setDownmax(s.toString());
                mIpSpeed.set(position, n);
            }
        });

        vh.upBw.addTextChangedListener(new MyTextIpWatcher(vh) {

            @Override
            public void afterTextChanged(Editable s,
                                         IpSpeedViewHolder holder) {
                int position = (Integer) vh.downBw.getTag();
                IpSpeed n = mIpSpeed.get(position);
                n.setUpmax(s.toString());
                mIpSpeed.set(position, n);
            }
        });
        vh.ipSpinnerFir.setOnItemSelectedListener(new MySpinnerIpListener(
                vh) {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3, IpSpeedViewHolder holder) {
                TextView tv = (TextView) arg1;
                int position = (Integer) vh.downBw.getTag();
                IpSpeed n = mIpSpeed.get(position);
                n.setIp(tv.getText().toString());
                mIpSpeed.set(position, n);
            }
        });
        vh.delButton.setOnClickListener(new MyOnClickListener(vh) {

            @Override
            public void onClick(View v, IpSpeedViewHolder holder) {
                if (mListener != null) {
                    int position = (Integer) vh.downBw.getTag();
                    IpSpeed n = mIpSpeed.get(position);
                    mIpSpeed.remove(position);
                    if (n.getIpSpeedDelTag() == 0) {
                        iplimitDel(vh, n);
                    }
                    mListener.onRemoved(); // 通知主线程更新Adapter
                }
            }
        });
        vh.setButton.setOnClickListener(new MyOnClickListener(vh) {

            @Override
            public void onClick(View v, IpSpeedViewHolder holder) {
                int position = (Integer) vh.downBw.getTag();
                IpSpeed n = mIpSpeed.get(position);
                iplimitSet(vh, n);
                n.setIpSpeedDelTag(0);
            }
        });
        IpSpeed ipSpeed = getItem(position);
        String user = ipSpeed.getUser();
        statuTag = true;
        vh.ipSpinner.setAdapter(arrayAdapter);
        vh.ipSpinnerAdd.setAdapter(addAdapter);
        // vh.textSelcet.setText(ipSpeed.getIp());
        vh.downBw.setText(ipSpeed.getDownmax());
        vh.upBw.setText(ipSpeed.getUpmax());
        vh.textBtIp.setText(user);
        vh.ipSpinnerFir.setSelection(ipSpeed.getPrior() - 1);
        vh.ipSpinner.setSelection(position);


        if (ipSpeed.ipSpeedTag) {
            vh.ipSpinner.setVisibility(View.VISIBLE);
            vh.ipSpinnerAdd.setVisibility(View.GONE);
        } else {
            vh.ipSpinner.setVisibility(View.GONE);
            vh.ipSpinnerAdd.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private JSONObject getTextVh(IpSpeedViewHolder vh, IpSpeed n) {
        String ip = null;
        if (n.isIpSpeedTag()) {
            ip = vh.ipSpinner.getSelectedItem().toString();
        } else {
            ip = vh.ipSpinnerAdd.getSelectedItem().toString();
        }
        String downBw = vh.downBw.getText().toString();
        String upBw = vh.upBw.getText().toString();
        String optFir = vh.ipSpinnerFir.getSelectedItem().toString();
        String userName = "";

        JSONObject object = XTHttpJSON.postIpSpeedSet(statu, ip, downBw, upBw,
                optFir, userName);
        return object;
    }

    // 设置的请求
    private void iplimitSet(IpSpeedViewHolder vh, final IpSpeed n) {
        progresshow = true;
        showDia();
        //判断是新增还是修改   20160714 by  hyw
        JsonObjectRequest request = null;
        //20160714  by hyw
        //修改
//新增
        mCurrIPsFlag = IPSpeedActivity.mCurrUsers.contains(n.user);


        if (!mCurrIPsFlag) {  //此时为新增的设置
            Log.i(TAG, "请求了新增设置的URL。。。。。===》");
            request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_IPLIMIT_ADD, getTextVh(vh, n),
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "新增的设置。。。。接收回来的数据===》" + response.toString());
                            IPSpeedActivity.mCurrUsers.add(n.user);
                            Log.i(TAG, "新增了一个名字===》" + IPSpeedActivity.mCurrUsers);

                            loadData(response);// 加载数据
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, error.toString());
                    Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT)
                            .show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
        } else if (mCurrIPsFlag) {          //此时为 修改的设置
            Log.i(TAG, "请求了修改设置URL。。。。。===》");
            request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_IPLIMIT_MODIFY, getTextVh(vh, n),
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, " 修改的设置。。。。接收回来的数据===》" + response.toString());
                            loadData(response);// 加载数据
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, error.toString());
                    Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT)
                            .show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
        }
        mCurrIPsFlag = false;
        mRequestQueue.add(request);
    }

    // 删除的请求
    private void iplimitDel(IpSpeedViewHolder vh, final IpSpeed n) {

        //20160714 by hyw
        //IPSpeedActivity.mCurrUsers.remove(n.user);
        mCurrIPsFlag = IPSpeedActivity.mCurrUsers.contains(n.user);
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_IPLIMIT_DEL, getTextVh(vh, n),

                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);

                        if (mCurrIPsFlag) {
                            IPSpeedActivity.mCurrUsers.remove(n.user);
                            Log.i(TAG, "在默认里面   已删除===》");
                        }

                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT)
                        .show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mCurrIPsFlag = false;
        mRequestQueue.add(request);
    }

    private void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(mContext, "访问成功", 0).show();

        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
            Toast.makeText(mContext, "访问失败", 0).show();
        }
    }

    private void showDia() {
        pd = new ProgressDialog(mContext);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progresshow = false;
            }
        });
        pd.setMessage("正在加载数据。。。。。");
        pd.show();
    }

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(JSONObject object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 动态添加List里面数据
    public void addItem(IpSpeed n) {
        mIpSpeed.add(n);
    }

    // 删除操作回调
    public interface OnListRemovedListener {
        void onRemoved();
    }

    class IpSpeedViewHolder {
        TextView ip_choose, ip_username, ip_downloadmax, ip_upmax, ip_youxianji;
        TextView textBtIp, textSelcet;
        Spinner ipSpinner, ipSpinnerFir, ipSpinnerAdd;
        EditText downBw;
        EditText upBw;
        Button delButton, setButton;
    }

    private abstract class MyTextIpWatcher implements TextWatcher {
        private IpSpeedViewHolder mHolder;

        public MyTextIpWatcher(IpSpeedViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(s, mHolder);
        }

        public abstract void afterTextChanged(Editable s,
                                              IpSpeedViewHolder holder);
    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private IpSpeedViewHolder holder;

        public MySpinnerIpListener(IpSpeedViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            onItemSelected(arg0, arg1, arg2, arg3, holder);
        }

        public abstract void onItemSelected(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3, IpSpeedViewHolder holder);
    }

    private abstract class MyOnClickListener implements OnClickListener {

        private IpSpeedViewHolder mHolder;

        public MyOnClickListener(IpSpeedViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, IpSpeedViewHolder holder);

    }

}
