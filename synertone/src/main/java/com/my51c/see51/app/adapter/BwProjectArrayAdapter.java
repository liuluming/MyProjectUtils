/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.app.domian.BwProjectNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.BwProjectActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.List;

//规则的适配器
public class BwProjectArrayAdapter extends BaseAdapter {
    protected static final String TAG = "BwProjectArrayAdapter";
    private String url;
    private List<BwProjectNum> mBwProjectNums;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int page;
    private RequestQueue mRequestQueue;
    private OnListRemovedListener mListener;
    // 下拉列表的适配器
    private ArrayAdapter<String> arrayAdapter;// 策略的下拉列表
    private List<String> mSprinner;
    private String bwProNameStr, bwProSadStr, bwProPortStr, bwProTarStr,
            bwProTagPortStr, bwProTocolStr, bwProPlanStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    public BwProjectArrayAdapter(List<BwProjectNum> mBwProjectNums,
                                 Context mContext, int page, RequestQueue mRequestQueue,
                                 List<String> mSprinner) {
        this.mBwProjectNums = mBwProjectNums;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.page = page;
        this.mRequestQueue = mRequestQueue;
        this.mSprinner = mSprinner;

        arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mSprinner);
        arrayAdapter.add("unreachable");
        arrayAdapter.add("default");
        arrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    public void setOnListRemovedListener(OnListRemovedListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return null == mBwProjectNums ? 0 : mBwProjectNums.size();
    }

    @Override
    public BwProjectNum getItem(int position) {
        return mBwProjectNums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final projectViewHolder vh;
        if (convertView == null) {
            vh = new projectViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.project_listview_item, null);
            //
            vh.pro_mingcheng = (TextView) convertView
                    .findViewById(R.id.pro_mingcheng);
            vh.pro_mingcheng.setTypeface(AppData.fontXiti);
            vh.pro_yuanadress = (TextView) convertView
                    .findViewById(R.id.pro_yuanadress);
            vh.pro_yuanadress.setTypeface(AppData.fontXiti);
            vh.pro_yuanduankou = (TextView) convertView
                    .findViewById(R.id.pro_yuanduankou);
            vh.pro_yuanduankou.setTypeface(AppData.fontXiti);
            vh.pro_mubiaoadress = (TextView) convertView
                    .findViewById(R.id.pro_mubiaoadress);
            vh.pro_mubiaoadress.setTypeface(AppData.fontXiti);
            vh.pro_mubiaoduankou = (TextView) convertView
                    .findViewById(R.id.pro_mubiaoduankou);
            vh.pro_mubiaoduankou.setTypeface(AppData.fontXiti);
            vh.pro_celue = (TextView) convertView
                    .findViewById(R.id.pro_celue);
            vh.pro_celue.setTypeface(AppData.fontXiti);
            vh.pro_xieyi = (TextView) convertView
                    .findViewById(R.id.pro_xieyi);
            vh.pro_xieyi.setTypeface(AppData.fontXiti);

            vh.textView = (TextView) convertView
                    .findViewById(R.id.bw_project_text);
            vh.bw_project_delbtn = (Button) convertView
                    .findViewById(R.id.bw_project_delebtn);


            // 删除按钮
            vh.bw_project_delbtn.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();

                    String nameBw = vh.bwProName.getText().toString();

                    url = XTHttpUtil.POST_BWSET_AVD_RULE_DEL;
                    BwProjectNum n = mBwProjectNums.get(position);
                    getTextStr(vh);
                    if (!n.isBwProjectTag()) {
                        try {
                            bwProject(url,
                                    new JSONObject().put("name", bwProNameStr)
                                            .toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mBwProjectNums.remove(position);
                    mListener.onRemoved(); // 通知主线程更新Adapter

                    if (BwProjectActivity.nameList.contains(nameBw)) {
                        BwProjectActivity.nameList.remove(nameBw);
                        Log.e(TAG, "规则删除----在里面-------》》》》》》。");
                    }
                    Log.e(TAG, "规则删除----不在里面-------》》》》》》。");
                }
            });

            //修改按钮
            vh.bw_project_refer = (Button) convertView
                    .findViewById(R.id.bw_project_referbtn);// 提交
            vh.bw_project_refer.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    url = XTHttpUtil.POST_BWSET_AVD_RULE_MODI;
                    getTextStr(vh);
                    try {
                        bwProject(url, getJsonStr().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            //添加按钮
            vh.bw_project_add = (Button) convertView
                    .findViewById(R.id.bw_project_addbtn);
            vh.bw_project_add.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);

                    String nameBw = vh.bwProName.getText().toString();

                    url = XTHttpUtil.POST_BWSET_AVD_RULE_ADD;
                    getTextStr(vh);
                    try {
                        bwProject(url, getJsonStr().toString());

                        BwProjectActivity.nameList.add(nameBw);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    n.setBwProjectTag(false);
                }
            });

            vh.bwProName = (EditText) convertView
                    .findViewById(R.id.bw_project_name);// 名称
            vh.bwProName.setTag(position);
            vh.bwProName.addTextChangedListener(new MyTextIpWatcher(vh) {

                @Override
                public void afterTextChanged(Editable s,
                                             projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);
                    n.setName(s.toString());
                    mBwProjectNums.set(position, n);
                }
            });

            vh.bwProSad = (EditText) convertView
                    .findViewById(R.id.bw_project_saddress);// 源码地址
            vh.bwProSad.setTag(position);
            vh.bwProSad.addTextChangedListener(new MyTextIpWatcher(vh) {

                @Override
                public void afterTextChanged(Editable s,
                                             projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);
                    n.setSrcip(s.toString());
                    mBwProjectNums.set(position, n);
                }
            });

            vh.bwProPort = (EditText) convertView
                    .findViewById(R.id.bw_project_port);// 源码端口
            vh.bwProPort.setTag(position);
            vh.bwProPort.addTextChangedListener(new MyTextIpWatcher(vh) {

                @Override
                public void afterTextChanged(Editable s,
                                             projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);
                    n.setSrcport(s.toString());
                    mBwProjectNums.set(position, n);
                }
            });

            vh.bwProTar = (EditText) convertView
                    .findViewById(R.id.bw_project_target);// 目标地址
            vh.bwProTar.setTag(position);
            vh.bwProTar.addTextChangedListener(new MyTextIpWatcher(vh) {

                @Override
                public void afterTextChanged(Editable s,
                                             projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);
                    n.setDesip(s.toString());
                    mBwProjectNums.set(position, n);
                }
            });

            vh.bwProTagPort = (EditText) convertView
                    .findViewById(R.id.bw_project_tagport);// 目标端口
            vh.bwProTagPort.setTag(position);
            vh.bwProTagPort.addTextChangedListener(new MyTextIpWatcher(vh) {

                @Override
                public void afterTextChanged(Editable s,
                                             projectViewHolder holder) {
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);
                    n.setDesport(s.toString());
                    mBwProjectNums.set(position, n);
                }
            });

            vh.bwProTocol = (Spinner) convertView
                    .findViewById(R.id.bw_project_protocol);// 协议
            vh.bwProTocol
                    .setOnItemSelectedListener(new MySpinnerIpListener(vh) {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3,
                                                   projectViewHolder holder) {
                            TextView tv = (TextView) arg1;
                            int position = (Integer) vh.bwProName.getTag();
                            BwProjectNum n = mBwProjectNums.get(position);
                            // n.setPro(Integer.valueOf(tv.getText().toString()));
                            mBwProjectNums.set(position, n);
                        }
                    });
            //
            // @Override
            // public void afterTextChanged(Editable s, projectViewHolder
            // holder) {
            // int position = (Integer) vh.bwProName.getTag();
            // BwProjectNum n=mBwProjectNums.get(position);
            // n.setPro(s.toString());
            // mBwProjectNums.set(position, n);
            // }
            // });

            vh.bwProPlan = (Spinner) convertView
                    .findViewById(R.id.bw_project_plan);// 策略
            vh.bwProPlan.setTag(position);
            vh.bwProPlan.setOnItemSelectedListener(new MySpinnerIpListener(vh) {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, projectViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.bwProName.getTag();
                    BwProjectNum n = mBwProjectNums.get(position);
                    n.setPolicy(tv.getText().toString());
                    mBwProjectNums.set(position, n);
                }
            });
            convertView.setTag(vh);
        } else {
            vh = (projectViewHolder) convertView.getTag();
            vh.bwProName.setTag(position);
        }

        if (mBwProjectNums != null) {
            BwProjectNum bwProjectNum = mBwProjectNums.get(position);
            vh.bwProName.setText(bwProjectNum.getName());
            vh.bwProSad.setText(bwProjectNum.getSrcip());
            vh.bwProPort.setText(bwProjectNum.getSrcport());
            vh.bwProTar.setText(bwProjectNum.getDesip());
            vh.bwProTagPort.setText(bwProjectNum.getDesport());
            vh.bwProTocol.setSelection(bwProjectNum.getPro());
            // vh.bwProPlan.setText(bwProjectNum.getPolicy());
            vh.bwProPlan.setAdapter(arrayAdapter);
            if (bwProjectNum.isBwProjectTag()) {
                vh.bw_project_refer.setVisibility(View.GONE);
                vh.bw_project_add.setVisibility(View.VISIBLE);
            } else {
                vh.bw_project_refer.setVisibility(View.VISIBLE);
                vh.bw_project_add.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private void getTextStr(projectViewHolder vh) {
        bwProNameStr = vh.bwProName.getText().toString();
        bwProSadStr = vh.bwProSad.getText().toString();
        bwProPortStr = vh.bwProPort.getText().toString();
        bwProTarStr = vh.bwProTar.getText().toString();
        bwProTagPortStr = vh.bwProTagPort.getText().toString();
        if (vh.bwProTocol.getSelectedItem().toString().equals("all")) {
            bwProTocolStr = "0";
        } else if (vh.bwProTocol.getSelectedItem().toString().equals("tcp")) {
            bwProTocolStr = "1";
        } else if (vh.bwProTocol.getSelectedItem().toString().equals("udp")) {
            bwProTocolStr = "2";
        } else if (vh.bwProTocol.getSelectedItem().toString().equals("icmp")) {
            bwProTocolStr = "3";
        }
        bwProPlanStr = vh.bwProPlan.getSelectedItem().toString();
    }

    private JSONObject getJsonStr() {
        JSONObject object = new JSONObject();
        try {
            object.put("name", bwProNameStr);
            object.put("srcip", bwProSadStr);
            object.put("srcport", bwProPortStr);
            object.put("desip", bwProTarStr);
            object.put("desport", bwProTagPortStr);
            object.put("pro", bwProTocolStr);
            object.put("policyname", bwProPlanStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    // 删除某个策略
    private void bwProject(String url, String json) throws Exception {
        RequestParams params = new RequestParams("UTF-8");
        params.setBodyEntity(new StringEntity(json, "UTF-8"));
        params.setContentType("applicatin/json");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        progresshow = true;
                        showDia();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> response) {
                        Log.i(TAG, "接收到数据-->" + response.result);
                        if (response != null) {
                            pd.dismiss();
                            loginDataQuery(response.result);

                        }
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });
    }

    protected void loginDataQuery(String string) {
        if (XTHttpJSON.getJSONString(string).equals("0")) {
            Toast.makeText(mContext, "设置成功", 0).show();
        } else if (XTHttpJSON.getJSONString(string).equals("-1")) {
            Toast.makeText(mContext, "设置失败", 0).show();
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

    // 动态添加List里面数据
    public void addItem(BwProjectNum n) {
        mBwProjectNums.add(n);
    }

    // 删除操作回调
    public interface OnListRemovedListener {
        void onRemoved();
    }

    class projectViewHolder {
        TextView pro_mingcheng, pro_yuanadress, pro_yuanduankou, pro_mubiaoadress, pro_mubiaoduankou, pro_xieyi, pro_celue;
        ImageView imgView;
        TextView textView;
        EditText bwProName, bwProSad, bwProPort, bwProTar, bwProTagPort;
        Button bw_project_delbtn, bw_project_refer, bw_project_add;
        Spinner bwplan_end, bwProTocol, bwProPlan;
    }

    // EditText的绑定事件
    private abstract class MyTextIpWatcher implements TextWatcher {
        private projectViewHolder mHolder;

        public MyTextIpWatcher(projectViewHolder vh) {
            this.mHolder = vh;
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
                                              projectViewHolder holder);
    }

    // 按钮的点击事件
    private abstract class MyOnClickListener implements OnClickListener {

        private projectViewHolder mHolder;

        public MyOnClickListener(projectViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, projectViewHolder holder);

    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private projectViewHolder holder;

        public MySpinnerIpListener(projectViewHolder holder) {
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
                                            int arg2, long arg3, projectViewHolder holder);
    }

}