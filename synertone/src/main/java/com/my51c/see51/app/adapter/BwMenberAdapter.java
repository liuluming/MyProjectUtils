package com.my51c.see51.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.Editable;
import android.text.TextUtils;
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
import com.my51c.see51.app.domian.BwMenberNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.BwMenberActivity;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.afinal.simplecache.ACache;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BwMenberAdapter extends BaseAdapter {
    protected static final String TAG = "IpSpeedAdapter";
    private static int numLen;
    private List<BwMenberNum> menberNums;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int page;
    private RequestQueue mRequestQueue;
    private List<String> mSprinner;
    private boolean statuTag;
    private OnListRemovedListener mListener;
    // 下拉列表的适配器
    private ArrayAdapter<String> arrayAdapter;
    // 下拉列表的选项
    private String memberStr;
    private ACache mBwMemberCache;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    public BwMenberAdapter(List<BwMenberNum> menberNums, Context mContext,
                           int page, RequestQueue mRequestQueue, int numLen,
                           List<String> mSprinner, boolean statuTag) {
        super();
        this.menberNums = menberNums;
        this.mContext = mContext;
        this.page = page;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mRequestQueue = mRequestQueue;
        BwMenberAdapter.numLen = numLen;
        this.mSprinner = mSprinner;
        this.statuTag = statuTag;
        mRequestQueue = Volley.newRequestQueue(mContext);
        arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mSprinner);
        arrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBwMemberCache = ACache.get(mContext);
        try {
            mBwMemberCache.put("oldname");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnListRemovedListener(OnListRemovedListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return null == menberNums ? 0 : menberNums.size();
    }

    @Override
    public BwMenberNum getItem(int position) {
        return menberNums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.member_listview_item,
                    null);
            //member_yuedianshu,member_quanzhong
            vh.member_jiekou = (TextView) convertView
                    .findViewById(R.id.member_jiekou);
            vh.member_jiekou.setTypeface(AppData.fontXiti);
            vh.member_yuedianshu = (TextView) convertView
                    .findViewById(R.id.member_yuedianshu);
            vh.member_yuedianshu.setTypeface(AppData.fontXiti);
            vh.member_quanzhong = (TextView) convertView
                    .findViewById(R.id.member_quanzhong);
            vh.member_quanzhong.setTypeface(AppData.fontXiti);

            vh.textName = (TextView) convertView
                    .findViewById(R.id.bwmember_text_view);
            vh.textOldName = (TextView) convertView.findViewById(R.id.bwmember_text_oldname);

            vh.interSpinner = (Spinner) convertView
                    .findViewById(R.id.bwmember_interfer);
            vh.interSpinner.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, ViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    memberStr = arrayAdapter.getItem(arg2);
                    int position = (Integer) vh.bwmemHopnum.getTag();
                    BwMenberNum n = menberNums.get(position);
                    n.setIntfer(tv.getText().toString());
                    menberNums.set(position, n);
                    vh.textName.setText(memberStr + "_m"
                            + vh.bwmemHopnum.getText().toString() + "_w"
                            + vh.bwmemWeigth.getText().toString());
                }
            });

            vh.bwmemHopnum = (EditText) convertView
                    .findViewById(R.id.bwmember_hopnum);
            vh.bwmemHopnum.setTag(position);
            vh.bwmemHopnum.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s, ViewHolder holder) {
                    int position = (Integer) vh.bwmemHopnum.getTag();
                    BwMenberNum n = menberNums.get(position);
                    n.setMetric(s.toString());
                    menberNums.set(position, n);
                }
            });

            vh.bwmemWeigth = (EditText) convertView
                    .findViewById(R.id.bwmember_weigth);
            vh.bwmemWeigth.setTag(position);
            vh.bwmemWeigth.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s, ViewHolder holder) {
                    int position = (Integer) vh.bwmemHopnum.getTag();
                    BwMenberNum n = menberNums.get(position);
                    n.setWeight(s.toString());
                    menberNums.set(position, n);
                }
            });
            //点击删除
            vh.delButton = (Button) convertView
                    .findViewById(R.id.bwmember_delebtn);
            vh.delButton.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, ViewHolder holder) {
                    Log.i(TAG, "点击了删除=======》");
                    if (mListener != null) {
                        int position = (Integer) vh.bwmemHopnum.getTag();
                        BwMenberNum n = menberNums.get(position);

                        if (BwMenberActivity.nameList.contains(n.getName())) {

                            BwMenberActivity.nameList.remove(n.getName());
                            Log.i(TAG, "点击了删除====在里面===》");
                        }

                        if (!n.isMemberTag()) {
                            String url = XTHttpUtil.POST_BWSET_AVD_MEMBER_DEL;
                            String memberName = vh.textName.getText().toString();
                            try {
                                bwMemberSave(vh, url,
                                        new JSONObject().put("name", memberName));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        menberNums.remove(position);
                        mListener.onRemoved(); // 通知主线程更新Adapter
                    }
                }
            });

            //点击添加
            vh.addButton = (Button) convertView
                    .findViewById(R.id.bwmember_savebtn);
            vh.addButton.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, ViewHolder holder) {
                    Log.i(TAG, "点击了添加========》");
                    int position = (Integer) vh.bwmemHopnum.getTag();
                    BwMenberNum n = menberNums.get(position);

                    String url = XTHttpUtil.POST_BWSET_AVD_MEMBER_ADD;

                    BwMenberActivity.nameList.add(n.getName());

                    bwMemberSave(vh, url, getTextRetString(vh));
                    n.setMemberTag(false);
                    //vh.textOldName.setText(vh.textName.getText().toString());
                    mBwMemberCache.put("oldname", vh.textName.getText().toString());
                }
            });
            //点击修改
            vh.modiButton = (Button) convertView
                    .findViewById(R.id.bwmember_revise_btn);
            vh.modiButton.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, ViewHolder holder) {

                    Log.i(TAG, "点击了修改============》");
                    int position = (Integer) vh.bwmemHopnum.getTag();
                    String url = XTHttpUtil.POST_BWSET_AVD_MEMBER_MODI;
                    bwMemberSave(vh, url, getTextString(vh));
                    mBwMemberCache.put("oldname", vh.textName.getText().toString());
                }
            });
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
            vh.bwmemHopnum.setTag(position);
        }
        final BwMenberNum mNum = menberNums.get(position);
        //vh.textOldName.setText(mNum.getName());
        vh.interSpinner.setAdapter(arrayAdapter);
        vh.bwmemHopnum.setText(mNum.getMetric());
        vh.bwmemWeigth.setText(mNum.getWeight());

        if (mNum.isMemberTag()) {
            vh.modiButton.setVisibility(View.GONE);
            vh.addButton.setVisibility(View.VISIBLE);
        } else {
            vh.modiButton.setVisibility(View.VISIBLE);
            vh.addButton.setVisibility(View.GONE);
        }

        if (mNum.getIntfer().equals("wan0")) {
            vh.interSpinner.setSelection(0);
        } else if (mNum.getIntfer().equals("wan1")) {
            vh.interSpinner.setSelection(1);
        } else if (mNum.getIntfer().equals("wan2")) {
            vh.interSpinner.setSelection(2);
        } else if (mNum.getIntfer().equals("wan3")) {
            vh.interSpinner.setSelection(3);
        } else if (mNum.getIntfer().equals("wan4")) {
            vh.interSpinner.setSelection(4);
        } else if (mNum.getIntfer().equals("wan5")) {
            vh.interSpinner.setSelection(5);
        } else {
            return null;
        }
        if (!TextUtils.isEmpty(vh.interSpinner.getSelectedItem().toString())) {
            vh.textName.setText(vh.interSpinner.getSelectedItem().toString() + "_m"
                    + vh.bwmemHopnum.getText().toString() + "_w"
                    + vh.bwmemWeigth.getText().toString());
        }
        return convertView;
    }

    private JSONObject getTextString(ViewHolder vh) {
        String memberOldName = null;
        if (mBwMemberCache.getAsString("oldname") != null) {
            memberOldName = mBwMemberCache.getAsString("oldname");
        }
        String memberName = vh.textName.getText().toString();
        String memberInterfer = vh.interSpinner.getSelectedItem().toString();
        String memberHopnum = vh.bwmemHopnum.getText().toString();
        String memberWeigth = vh.bwmemWeigth.getText().toString();
        JSONObject object = new JSONObject();
        try {
            object.put("newname", memberName);
            object.put("oldname", memberOldName);
            object.put("intfer", memberInterfer);
            object.put("metric", memberHopnum);
            object.put("weight", memberWeigth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

	/*
     * String statu; private JSONObject getTextVh(ViewHolder vh) { if (statuTag)
	 * { statu = "0";// 开启 } else { statu = "1";// 表示重启生效 } String ip =
	 * vh.ipSpinner.getSelectedItem().toString(); String downBw =
	 * vh.downBw.getText().toString(); String upBw =
	 * vh.upBw.getText().toString(); String optFir =
	 * vh.ipSpinnerFir.getSelectedItem().toString(); String userName = "";
	 * 
	 * JSONObject object = XTHttpJSON.postIpSpeedSet(statu, ip, downBw, upBw,
	 * optFir, userName); return object; }
	 */

    private JSONObject getTextRetString(ViewHolder vh) {
        String memberName = vh.textName.getText().toString();
        String memberInterfer = vh.interSpinner.getSelectedItem().toString();
        String memberHopnum = vh.bwmemHopnum.getText().toString();
        String memberWeigth = vh.bwmemWeigth.getText().toString();
        JSONObject object = new JSONObject();
        try {
            object.put("name", memberName);
            object.put("intfer", memberInterfer);
            object.put("metric", memberHopnum);
            object.put("weight", memberWeigth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private void bwMemberSave(ViewHolder vh, String url, JSONObject object) {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST, url,
                object, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
                Log.i(TAG, "接收回来的数据===》" + response.toString());
                loadData(response);
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
        mRequestQueue.add(request);
    }

    private void loadData(JSONObject response) {

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
    public void addItem(BwMenberNum n) {
        menberNums.add(n);
    }

    // 删除操作回调
    public interface OnListRemovedListener {
        void onRemoved();
    }

    class ViewHolder {
        TextView textName, textOldName;
        Spinner interSpinner;
        EditText bwmemHopnum;
        EditText bwmemWeigth;
        Button delButton,
                addButton,
                modiButton;
        TextView member_jiekou, member_yuedianshu, member_quanzhong;
    }

    private abstract class MyTextIpWatcher implements TextWatcher {
        private ViewHolder mHolder;

        public MyTextIpWatcher(ViewHolder holder) {
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

        public abstract void afterTextChanged(Editable s, ViewHolder holder);
    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private ViewHolder holder;

        public MySpinnerIpListener(ViewHolder holder) {
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
                                            int arg2, long arg3, ViewHolder holder);
    }

    private abstract class MyOnClickListener implements OnClickListener {

        private ViewHolder mHolder;

        public MyOnClickListener(ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, ViewHolder holder);

    }

}
