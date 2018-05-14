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
import com.my51c.see51.app.domian.WhileList;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.WhileListActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.List;

public class WhileListAdapter extends BaseAdapter {
    protected static final String TAG = "WhileListAdapter";
    public static boolean mCurrWhiteUserFlag = false;
    String statu;
    private List<WhileList> mWhile;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int mPage;
    private RequestQueue mRequestQueue;
    private List<String> mIpStrings;
    private List<String> mMacStrings;
    private ArrayAdapter<String> mAdapterIp;
    private ArrayAdapter<String> mAdapterMac;
    private ArrayAdapter<String> mAdapterIpAdd;
    private ArrayAdapter<String> mAdapterMacAdd;
    /*private SpinnerAdapter mAdapterIp;
    private SpinnerAdapter mAdapterMac;
    private SpinnerAdapter mAdapterIpAdd;
    private SpinnerAdapter mAdapterMacAdd;*/
    private List<String> mIpStringAdd;
    private List<String> mMacStringAdd;
    private boolean statuTag;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    public WhileListAdapter(List<WhileList> mWhile, Context mContext,
                            int mPage, RequestQueue mRequestQueue, List<String> mSprinner,
                            List<String> mMacStrings, List<String> mIpStringAdd,
                            List<String> mMacStringAdd) {
        super();
        this.mWhile = mWhile;
        this.mContext = mContext;
        this.mPage = mPage;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mRequestQueue = mRequestQueue;
        this.mIpStrings = mSprinner;
        this.mMacStrings = mMacStrings;
        this.mIpStringAdd = mIpStringAdd;
        this.mMacStringAdd = mMacStringAdd;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mAdapterIp = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mIpStrings);
        mAdapterIp
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAdapterMac = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mMacStrings);
        mAdapterMac
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAdapterIpAdd = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mIpStringAdd);
        mAdapterIpAdd
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAdapterMacAdd = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mMacStringAdd);
        mAdapterMacAdd
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*mAdapterIp = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mIpStrings);
		mAdapterIp
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mAdapterMac = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mMacStrings);
		mAdapterMac
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mAdapterIpAdd = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mIpStringAdd);
		mAdapterIpAdd
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mAdapterMacAdd = new SpinnerAdapter(mContext,
				android.R.layout.simple_spinner_item, mMacStringAdd);
		mAdapterMacAdd
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
    }

    @Override
    public int getCount() {
        // return mWhile.size();
        return null == mWhile ? 0 : mWhile.size();
    }

    @Override
    public WhileList getItem(int position) {
        return mWhile.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final WhileViewHolder vh;
        if (convertView == null) {
            vh = new WhileViewHolder();
            convertView = layoutInflater
                    .inflate(R.layout.while_list_item, null);

            //这是设置字体，勿删
            vh.white_username = (TextView) convertView
                    .findViewById(R.id.white_username);
            vh.white_username.setTypeface(AppData.fontXiti);
            vh.white_ip = (TextView) convertView
                    .findViewById(R.id.white_ip);
            vh.white_ip.setTypeface(AppData.fontXiti);
            vh.black_mac = (TextView) convertView
                    .findViewById(R.id.black_mac);
            vh.black_mac.setTypeface(AppData.fontXiti);

            vh.whileName = (TextView) convertView
                    .findViewById(R.id.while_list_name);
            vh.whileName.setTag(position);
            vh.whileName.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s, WhileViewHolder holder) {
                    int position = (Integer) vh.whileName.getTag();
                    WhileList n = mWhile.get(position);
                    n.setUser(s.toString());
                    mWhile.set(position, n);
                }
            });

            vh.ipSpinner = (Spinner) convertView
                    .findViewById(R.id.while_ip_select);
            vh.ipSpinner.setOnItemSelectedListener(new MySpinnerIpListener(vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, WhileViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.whileName.getTag();
                    WhileList n = mWhile.get(position);
                    n.setIp(tv.getText().toString());
                    mWhile.set(position, n);
                }
            });

            vh.macSpinner = (Spinner) convertView
                    .findViewById(R.id.while_list_mac);
            vh.macSpinner
                    .setOnItemSelectedListener(new MySpinnerIpListener(vh) {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3,
                                                   WhileViewHolder holder) {
                            TextView tv = (TextView) arg1;
                            int position = (Integer) vh.whileName.getTag();
                            WhileList n = mWhile.get(position);
                            n.setMac(tv.getText().toString());
                            mWhile.set(position, n);
                        }
                    });

            //增加ip
            vh.ipSpinnerAdd = (Spinner) convertView
                    .findViewById(R.id.while_ip_select_add);
            vh.ipSpinnerAdd.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, WhileViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.whileName.getTag();
                    WhileList n = mWhile.get(position);
                    n.setIp(tv.getText().toString());
                    mWhile.set(position, n);
                }
            });
            //增加 mac
            vh.macSpinnerAdd = (Spinner) convertView
                    .findViewById(R.id.while_list_mac_add);
            vh.macSpinnerAdd.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, WhileViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.whileName.getTag();
                    WhileList n = mWhile.get(position);
                    n.setMac(tv.getText().toString());
                    mWhile.set(position, n);
                }
            });
            //删除
            vh.delButton = (Button) convertView
                    .findViewById(R.id.while_list_delete);
            vh.delButton.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, WhileViewHolder holder) {
                    int position = (Integer) vh.whileName.getTag();
                    WhileList n = mWhile.get(position);

                    mWhile.remove(position);
                    if (n.getWhileTag() == 0) {
                        whileListDel(vh, n);
                    }
                    notifyDataSetChanged();
                }
            });

            //设置
            vh.setButton = (Button) convertView
                    .findViewById(R.id.while_list_set);
            vh.setButton.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, WhileViewHolder holder) {
                    int position = (Integer) vh.whileName.getTag();
                    WhileList n = mWhile.get(position);
                    n.setWhileTag(0);
                    whileListAdd(vh, n);
                }
            });

            convertView.setTag(vh);
        } else {
            vh = (WhileViewHolder) convertView.getTag();
            vh.whileName.setTag(position);
        }
        WhileList whileList = getItem(position);
        vh.whileName.setText(whileList.getUser());

        if (whileList.isWhileListTag()) {
            vh.ipSpinner.setSelection(position);
            vh.macSpinner.setSelection(position);
            vh.ipSpinner.setVisibility(View.VISIBLE);
            vh.macSpinner.setVisibility(View.VISIBLE);
            vh.ipSpinnerAdd.setVisibility(View.GONE);
            vh.macSpinnerAdd.setVisibility(View.GONE);
            vh.ipSpinner.setAdapter(mAdapterIp);
            vh.macSpinner.setAdapter(mAdapterMac);
        } else {
            vh.ipSpinner.setVisibility(View.GONE);
            vh.macSpinner.setVisibility(View.GONE);
            vh.ipSpinnerAdd.setVisibility(View.VISIBLE);
            vh.macSpinnerAdd.setVisibility(View.VISIBLE);
            vh.ipSpinnerAdd.setAdapter(mAdapterIpAdd);
            vh.macSpinnerAdd.setAdapter(mAdapterMacAdd);
        }
        return convertView;
    }

    private JSONObject getTextVh(WhileViewHolder vh, WhileList n) {
        if (statuTag) {
            statu = "0";// 开启
        } else {
            statu = "1";// 表示重启生效
        }
        String ip = null;
        String mac = null;
        if (n.isWhileListTag()) {
            ip = vh.ipSpinner.getSelectedItem().toString();
            mac = vh.macSpinner.getSelectedItem().toString();
        } else {
            ip = vh.ipSpinnerAdd.getSelectedItem().toString();
            mac = vh.macSpinnerAdd.getSelectedItem().toString();
        }
        String userName = vh.whileName.getText().toString();
        JSONObject object = XTHttpJSON.postWhileSet(statu, ip, mac, userName);
        return object;
    }

    // 添加指点白名单
    private void whileListAdd(WhileViewHolder vh, final WhileList n) {

        mCurrWhiteUserFlag = WhileListActivity.mCurrWhiteUsers.contains(n.user);

        progresshow = true;
        showDia();
        JsonObjectRequest request = null;
        if (mCurrWhiteUserFlag) {//修改

            Log.i(TAG, "zai默认里面   修改。。。。接收回来的数据===》");

            request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_WHILE_LIST_MODIFY, getTextVh(vh, n),
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "修改。。。。接收回来的数据===》" + response.toString());
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

        } else if (!mCurrWhiteUserFlag) {//新增

            Log.i(TAG, "不在   默认里面   新增。。。。接收回来的数据===》");

            request = new JsonObjectRequest(Method.POST,
                    XTHttpUtil.POST_WHILE_LIST, getTextVh(vh, n),
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pdDismiss(response);
                            Log.i(TAG, "新增。。。。接收回来的数据===》" + response.toString());
                            //Toast.makeText(mContext, response.toString(), 0).show();

                            WhileListActivity.mCurrWhiteUsers.add(n.user);

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
        mCurrWhiteUserFlag = false;
        mRequestQueue.add(request);
    }

    // 删除指点白名单
    private void whileListDel(WhileViewHolder vh, final WhileList n) {

        if (WhileListActivity.mCurrWhiteUsers.contains(n.user)) {
            mCurrWhiteUserFlag = true;
        }

        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                XTHttpUtil.POST_WHILE_DEL, getTextVh(vh, n),
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "删除 白名单。。。。接收回来的数据===》" + response.toString());

                        if (mCurrWhiteUserFlag) {
                            WhileListActivity.mCurrWhiteUsers.remove(n.user);
                            Log.i(TAG, "删除白名单。。在默认限制里面。。。。。===》");
                            mCurrWhiteUserFlag = false;
                        } else {
                            Log.i(TAG, "删除白名单。。不在默认限制里面。。。。。===》");
                        }
                        //Toast.makeText(mContext, response.toString(), 0).show();
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
        mCurrWhiteUserFlag = false;
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

    class WhileViewHolder {
        TextView white_username, white_ip, black_mac;
        TextView whileName;
        Spinner ipSpinner, macSpinner, ipSpinnerAdd, macSpinnerAdd;
        Button delButton, setButton;
    }

    private abstract class MyTextIpWatcher implements TextWatcher {
        private WhileViewHolder mHolder;

        public MyTextIpWatcher(WhileViewHolder holder) {
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

        public abstract void afterTextChanged(Editable s, WhileViewHolder holder);
    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private WhileViewHolder holder;

        public MySpinnerIpListener(WhileViewHolder holder) {
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
                                            int arg2, long arg3, WhileViewHolder holder);
    }

    private abstract class MyOnClickListener implements OnClickListener {

        private WhileViewHolder mHolder;

        public MyOnClickListener(WhileViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, WhileViewHolder holder);


    }
}
