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
import com.my51c.see51.app.domian.BlackList;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.BlackListActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.List;

public class BlackListAdapter extends BaseAdapter {
    protected static final String TAG = "BlackListAdapter";
    public static boolean mCurrBlackUserFlag = false;
    String statu;
    private List<BlackList> mBlackLists;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int mPage;
    private RequestQueue mRequestQueue;
    private List<String> mBackIpStr;
    private List<String> mBackMacStr;
    private List<String> mBackIpStrAdd;
    private List<String> mBackMacStrAdd;
    private ArrayAdapter<String> mBlackIpAD;
    private ArrayAdapter<String> mBlackMacAD;
    private ArrayAdapter<String> mBlackIpAdd;
    private ArrayAdapter<String> mBlackMacAdd;
    /*private SpinnerAdapter mBlackIpAD;
    private SpinnerAdapter mBlackMacAD;
    private SpinnerAdapter mBlackIpAdd;
    private SpinnerAdapter mBlackMacAdd;*/
    private boolean statuTag;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    public BlackListAdapter(List<BlackList> mBlackLists, Context mContext,
                            int mPage, RequestQueue mRequestQueue, List<String> mBackIpStr,
                            List<String> mBackMacStr, List<String> mBackIpStrAdd,
                            List<String> mBackMacStrAdd) {
        super();
        this.mBlackLists = mBlackLists;
        this.mContext = mContext;
        this.mPage = mPage;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mRequestQueue = mRequestQueue;
        this.mBackIpStr = mBackIpStr;
        this.mBackMacStr = mBackMacStr;
        this.mBackIpStrAdd = mBackIpStrAdd;
        this.mBackMacStrAdd = mBackMacStrAdd;
        mRequestQueue = Volley.newRequestQueue(mContext);
        /*mBlackIpAD = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mBackIpStr);
		mBlackIpAD
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBlackMacAD = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mBackMacStr);
		mBlackMacAD
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mBlackIpAdd = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mBackIpStrAdd);
		mBlackIpAdd
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mBlackMacAdd = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mBackMacStrAdd);
		mBlackMacAdd
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        mBlackIpAD = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mBackIpStr);
        mBlackIpAD
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBlackMacAD = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mBackMacStr);
        mBlackMacAD
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBlackIpAdd = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mBackIpStrAdd);
        mBlackIpAdd
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBlackMacAdd = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mBackMacStrAdd);
        mBlackMacAdd
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public int getCount() {
        return null == mBlackLists ? 0 : mBlackLists.size();
    }

    @Override
    public BlackList getItem(int position) {
        return mBlackLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BlackViewHolder vh;
        if (convertView == null) {
            vh = new BlackViewHolder();
            convertView = layoutInflater
                    .inflate(R.layout.black_list_item, null);

            //这是设置字体，勿删
            vh.black_username = (TextView) convertView
                    .findViewById(R.id.black_username);
            vh.black_username.setTypeface(AppData.fontXiti);
            vh.black_ip = (TextView) convertView
                    .findViewById(R.id.black_ip);
            vh.black_ip.setTypeface(AppData.fontXiti);
            vh.black_mac = (TextView) convertView
                    .findViewById(R.id.black_mac);
            vh.black_mac.setTypeface(AppData.fontXiti);


            vh.black_item_name = (TextView) convertView
                    .findViewById(R.id.black_user);
            vh.black_item_name.setTag(position);
            vh.black_item_name.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s, BlackViewHolder holder) {
                    int position = (Integer) vh.black_item_name.getTag();
                    BlackList n = mBlackLists.get(position);
                    n.setUser(s.toString());
                    mBlackLists.set(position, n);
                }
            });

            vh.ipSpinner = (Spinner) convertView
                    .findViewById(R.id.black_ip_select);
            vh.ipSpinner.setOnItemSelectedListener(new MySpinnerIpListener(vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, BlackViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.black_item_name.getTag();
                    BlackList n = mBlackLists.get(position);
                    n.setIp(tv.getText().toString());
                    mBlackLists.set(position, n);
                }
            });

            vh.macSpinner = (Spinner) convertView
                    .findViewById(R.id.black_list_mac);
            vh.macSpinner
                    .setOnItemSelectedListener(new MySpinnerIpListener(vh) {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3,
                                                   BlackViewHolder holder) {
                            TextView tv = (TextView) arg1;
                            int position = (Integer) vh.black_item_name
                                    .getTag();
                            BlackList n = mBlackLists.get(position);
                            n.setIp(tv.getText().toString());
                            mBlackLists.set(position, n);
                        }
                    });

            vh.ipSpinnerAdd = (Spinner) convertView
                    .findViewById(R.id.black_ip_select_add);
            vh.ipSpinnerAdd.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, BlackViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.black_item_name.getTag();
                    BlackList n = mBlackLists.get(position);
                    n.setIp(tv.getText().toString());
                    mBlackLists.set(position, n);
                }
            });

            vh.macSpinnerAdd = (Spinner) convertView
                    .findViewById(R.id.black_list_mac_add);
            vh.macSpinnerAdd.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, BlackViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.black_item_name.getTag();
                    BlackList n = mBlackLists.get(position);
                    n.setIp(tv.getText().toString());
                    mBlackLists.set(position, n);
                }
            });

            //删除
            vh.delButton = (Button) convertView
                    .findViewById(R.id.black_list_delete);
            vh.delButton.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, BlackViewHolder holder) {
                    int position = (Integer) vh.black_item_name.getTag();
                    BlackList n = mBlackLists.get(position);
                    mBlackLists.remove(position);
                    if (n.getBlackDelTag() == 0) {
                        balckListDel(vh, n);
                    }
                    notifyDataSetChanged();
                }
            });
            //设置
            vh.setButton = (Button) convertView
                    .findViewById(R.id.black_list_set);
            vh.setButton.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, BlackViewHolder holder) {
                    int position = (Integer) vh.black_item_name.getTag();
                    BlackList n = mBlackLists.get(position);


                    blackListAdd(vh, n);
                    n.setBlackDelTag(0);
                }
            });

            convertView.setTag(vh);
        } else {
            vh = (BlackViewHolder) convertView.getTag();
            vh.black_item_name.setTag(position);
        }
        BlackList blackList = getItem(position);
        vh.black_item_name.setText(blackList.getUser());
        if (blackList.isBlackListTag()) {
            vh.ipSpinner.setAdapter(mBlackIpAD);
            vh.macSpinner.setAdapter(mBlackMacAD);
            vh.ipSpinner.setSelection(position);
            vh.macSpinner.setSelection(position);
            vh.ipSpinner.setVisibility(View.VISIBLE);
            vh.macSpinner.setVisibility(View.VISIBLE);
            vh.ipSpinnerAdd.setVisibility(View.GONE);
            vh.macSpinnerAdd.setVisibility(View.GONE);
        } else {
            vh.ipSpinner.setVisibility(View.GONE);
            vh.macSpinner.setVisibility(View.GONE);
            vh.ipSpinnerAdd.setVisibility(View.VISIBLE);
            vh.macSpinnerAdd.setVisibility(View.VISIBLE);
            vh.ipSpinnerAdd.setAdapter(mBlackIpAdd);
            vh.macSpinnerAdd.setAdapter(mBlackMacAdd);
        }
        return convertView;
    }

    private JSONObject getTextVh(BlackViewHolder vh, BlackList bList) {
        if (statuTag) {
            statu = "0";// 开启
        } else {
            statu = "1";// 表示重启生效
        }
        String ip, mac;
        if (bList.isBlackListTag()) {
            ip = vh.ipSpinner.getSelectedItem().toString();
            mac = vh.macSpinner.getSelectedItem().toString();
        } else {
            ip = vh.ipSpinnerAdd.getSelectedItem().toString();
            mac = vh.macSpinnerAdd.getSelectedItem().toString();
        }
        JSONObject object = XTHttpJSON.postBlcakSet(statu, ip, mac);
        return object;
    }

    // 添加指点黑名单
    private void blackListAdd(BlackViewHolder vh, final BlackList bList) {


        mCurrBlackUserFlag = BlackListActivity.mCurrBlackUsers.contains(bList.user);

        progresshow = true;
        showDia();
        JsonObjectRequest request = null;
        if (mCurrBlackUserFlag) {//修改
            Log.i(TAG, "在默认里面   修改黑名单，，，，===》");

            request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_BLACK_MODIFY, getTextVh(vh, bList),
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "修改黑名单，，，，接收回来的数据===》" + response.toString());
                            //Toast.makeText(mContext, response.toString(), 0).show();
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
        } else if (!mCurrBlackUserFlag) {//新增

            Log.i(TAG, "不在里面  新增   黑名单，，，，接收回来的数据===》");


            request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_BLACK_ADD, getTextVh(vh, bList),
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "新增黑名单 。。。。接收回来的数据===》" + response.toString());
                            //Toast.makeText(mContext, response.toString(), 0).show();

                            BlackListActivity.mCurrBlackUsers.add(bList.user);

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

        mCurrBlackUserFlag = false;
        mRequestQueue.add(request);
    }

    // 删除指点黑名单
    private void balckListDel(BlackViewHolder vh, final BlackList bList) {

        mCurrBlackUserFlag = BlackListActivity.mCurrBlackUsers.contains(bList.user);

        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_BALCK_DEL, getTextVh(vh, bList),
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "删除 黑名单  接收回来的数据===》" + response.toString());
                        //Toast.makeText(mContext, response.toString(), 0).show();

                        if (mCurrBlackUserFlag) {
                            BlackListActivity.mCurrBlackUsers.remove(bList.user);
                            Log.i(TAG, "在默认限制里面的白名单，，，，接收回来的数据===》");
                        }

                        loadData(response);// 加载数据
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
        mCurrBlackUserFlag = false;
        mRequestQueue.add(request);
    }

    private void loadData(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
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

    class BlackViewHolder {
        Spinner ipSpinner, macSpinner, ipSpinnerAdd, macSpinnerAdd;
        Button delButton, setButton;
        TextView black_item_name;
        TextView black_username, black_ip, black_mac;
    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private BlackViewHolder holder;

        public MySpinnerIpListener(BlackViewHolder holder) {
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
                                            int arg2, long arg3, BlackViewHolder holder);
    }

    private abstract class MyOnClickListener implements OnClickListener {

        private BlackViewHolder mHolder;

        public MyOnClickListener(BlackViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, BlackViewHolder holder);

    }

    private abstract class MyTextIpWatcher implements TextWatcher {
        private BlackViewHolder mHolder;

        public MyTextIpWatcher(BlackViewHolder holder) {
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

        public abstract void afterTextChanged(Editable s, BlackViewHolder holder);
    }
}
