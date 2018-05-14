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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.app.domian.BwPlanNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.BwPlanActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.List;

//策略的适配器
public class PlanArrayAdapter extends BaseAdapter {
    protected static final String TAG = "PlanArrayAdapter";
    Context mContext;
    int mCounter;
    int page;
    int mPag;
    String bwName, bwmenber01, bwmenber02, bwmenber03, bwmenber04, bwmenber05,
            bwEnd;
    private LayoutInflater layoutInflater;
    private RequestQueue mRequestQueue;
    private List<BwPlanNum> mBwPlanNums;
    private OnListRemovedListener mListener;
    private List<String> mMem;
    private String url;
    // 下拉列表的适配器
    private ArrayAdapter<String> arrayAdapter;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    public PlanArrayAdapter(List<BwPlanNum> mBwPlanNums, Context context,
                            int page, RequestQueue mRequestQueue, List<String> mMem, int mPag) {
        this.mBwPlanNums = mBwPlanNums;
        this.page = page;
        this.mContext = context;
        this.mMem = mMem;
        this.mPag = mPag;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mRequestQueue = mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(mContext);
        arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mMem);
        arrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void setOnListRemovedListener(OnListRemovedListener listener) {
        this.mListener = listener;
    }

    @Override
    public BwPlanNum getItem(int position) {
        return mBwPlanNums.get(position);
    }

    @Override
    public int getCount() {
        return null == mBwPlanNums ? 0 : mBwPlanNums.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PlanViewHolder vh;
        if (convertView == null) {
            vh = new PlanViewHolder();
            convertView = layoutInflater.inflate(R.layout.plan_listview_item,
                    null);
            //plan_mingcheng,plan_youxianji
            vh.plan_mingcheng = (TextView) convertView
                    .findViewById(R.id.plan_mingcheng);
            vh.plan_mingcheng.setTypeface(AppData.fontXiti);
            vh.plan_youxianji = (TextView) convertView
                    .findViewById(R.id.plan_youxianji);
            vh.plan_youxianji.setTypeface(AppData.fontXiti);

            vh.bwplan_num01 = (EditText) convertView
                    .findViewById(R.id.bwplan_num01);// 名称
            vh.bwplan_num01.setTag(position);
            vh.bwplan_num01.addTextChangedListener(new MyTextWatcher(vh) {

                @Override
                public void afterTextChanged(Editable s, PlanViewHolder holder) {
                    int position = (Integer) vh.bwplan_num01.getTag();
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    bwPlanNum.setName(s.toString());
                    mBwPlanNums.set(position, bwPlanNum);
                }
            });

            vh.bpMenbeNum01 = (Spinner) convertView
                    .findViewById(R.id.bwplan_menber_num01);// 成员
            vh.bpMenbeNum01.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, PlanViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.bwplan_num01.getTag();
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    bwPlanNum.setMem1(tv.getText().toString());
                    mBwPlanNums.set(position, bwPlanNum);
                }
            });

            vh.bpMenbeNum02 = (Spinner) convertView
                    .findViewById(R.id.bwplan_menber_num02);// 成员
            vh.bpMenbeNum02.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, PlanViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.bwplan_num01.getTag();
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    bwPlanNum.setMem2(tv.getText().toString());
                    mBwPlanNums.set(position, bwPlanNum);
                }
            });
            vh.bpMenbeNum03 = (Spinner) convertView
                    .findViewById(R.id.bwplan_menber_num03);// 成员
            vh.bpMenbeNum03.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, PlanViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.bwplan_num01.getTag();
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    bwPlanNum.setMem3(tv.getText().toString());
                    mBwPlanNums.set(position, bwPlanNum);
                }
            });

            vh.bpMenbeNum04 = (Spinner) convertView
                    .findViewById(R.id.bwplan_menber_num04);// 成员
            vh.bpMenbeNum04.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, PlanViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.bwplan_num01.getTag();
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    bwPlanNum.setMem4(tv.getText().toString());
                    mBwPlanNums.set(position, bwPlanNum);
                }
            });

            vh.bpMenbeNum05 = (Spinner) convertView
                    .findViewById(R.id.bwplan_menber_num05);// 成员
            vh.bpMenbeNum05.setOnItemSelectedListener(new MySpinnerIpListener(
                    vh) {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3, PlanViewHolder holder) {
                    TextView tv = (TextView) arg1;
                    int position = (Integer) vh.bwplan_num01.getTag();
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    bwPlanNum.setMem5(tv.getText().toString());
                    mBwPlanNums.set(position, bwPlanNum);
                }
            });

            vh.bwplan_end = (Spinner) convertView.findViewById(R.id.bwplan_end);// 最终策略
            vh.bwplan_end
                    .setOnItemSelectedListener(new MySpinnerIpListener(vh) {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3,
                                                   PlanViewHolder holder) {
                            TextView tv = (TextView) arg1;
                            int position = (Integer) vh.bwplan_num01.getTag();
                            BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                            // bwPlanNum.setPolicysel(tv.getText().toString());
                            mBwPlanNums.set(position, bwPlanNum);
                        }
                    });

            //删除
            vh.bwplanDelbtn = (Button) convertView
                    .findViewById(R.id.bwplan_delebtn);// 删除一条Item
            vh.bwplanDelbtn.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    if (mListener != null) {
                        int position = (Integer) vh.bwplan_num01.getTag();
                        String url = XTHttpUtil.GET_BWSET_AVD_POLICY_DEL;
                        String memberName = vh.bwplan_num01.getText()
                                .toString();
                        try {
                            bwPlan(url, new JSONObject()
                                    .put("name", memberName).toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mBwPlanNums.remove(position);
                        mListener.onRemoved();
                        // 通知主线程更新Adapter

                        if (BwPlanActivity.nameList.contains(memberName)) {
                            BwPlanActivity.nameList.remove(memberName);
                            Log.e(TAG, "策略删除----在里面-------》》》》》》。");
                        }
                        Log.e(TAG, "策略删除----不在里面-------》》》》》》。");
                    }
                }
            });

            //设置（修改）
            vh.bwplanSetBtn = (Button) convertView
                    .findViewById(R.id.bwplan_setbtn);// 设置Item策略
            vh.bwplanSetBtn.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    int position = (Integer) vh.bwplan_num01.getTag();

                    String memberName = vh.bwplan_num01.getText()
                            .toString();

                    String url = XTHttpUtil.POST_BWSET_AVD_POLICY_MODI;
                    getTextString(vh);
                    try {
                        bwPlan(url, getStringModi().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (BwPlanActivity.nameList.contains(memberName)) {
                        BwPlanActivity.nameList.remove(memberName);
                        Log.e(TAG, "策略设置----一定在里面-------》》》》》》。");
                    }
                    Log.e(TAG, "策略设置----不在里面-------》》》》》》。");
                }
            });

            vh.bwplanIpbtn = (Button) convertView
                    .findViewById(R.id.bwplan_ipbtn);// 点击添加一条成员
            vh.bwplanIpbtn.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    mPag = bwPlanNum.getmPag();
                    if (mPag <= 4 || mPag >= 0) {
                        mPag++;
                        if (mPag == 1) {
                            vh.mLayoutPlan02.setVisibility(View.VISIBLE);
                        } else if (mPag == 0) {
                            vh.mLayoutPlan01.setVisibility(View.VISIBLE);
                        } else if (mPag == 2) {
                            vh.mLayoutPlan03.setVisibility(View.VISIBLE);
                        } else if (mPag == 3) {
                            vh.mLayoutPlan04.setVisibility(View.VISIBLE);
                        } else if (mPag == 4) {
                            vh.mLayoutPlan05.setVisibility(View.VISIBLE);
                        }
                        bwPlanNum.setmPag(mPag);
                    } else {
                        vh.bwplanIpbtn.setEnabled(false);
                    }
                }
            });


            //添加（新增）
            vh.bwplanAddbtn = (Button) convertView
                    .findViewById(R.id.bwplan_addbtn);// 点击添加一个策列
            vh.bwplanAddbtn.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {

                    int position = (Integer) vh.bwplan_num01.getTag();

                    String memberName = vh.bwplan_num01.getText()
                            .toString();

                    String url = XTHttpUtil.POST_BWSET_AVD_POLICY_ADD;
                    getTextString(vh);
                    try {
                        bwPlan(url, getAddPlan().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (BwPlanActivity.nameList.contains(memberName)) {
                        BwPlanActivity.nameList.remove(memberName);
                        Log.e(TAG, "策略添加----在里面-------》》》》》》。");
                    } else {
                        Log.e(TAG, "策略添加----不在里面-------》》》》》》。");
                    }
                }
            });

            vh.bwplanDelItem01 = (Button) convertView
                    .findViewById(R.id.bwplan_delitembtn);
            vh.bwplanDelItem01.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    vh.mLayoutPlan01.setVisibility(View.GONE);
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    try {
                        String url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
                        getTextString(vh);
                        bwPlan(url, getStringJson(bwmenber01).toString());
                        vh.mLayoutPlan01.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPag--;
                    bwPlanNum.setmPag(mPag);
                }
            });

            vh.bwplanDelItem02 = (Button) convertView
                    .findViewById(R.id.bwplan_delitembtn01);
            vh.bwplanDelItem02.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    vh.mLayoutPlan02.setVisibility(View.GONE);
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    try {
                        String url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
                        getTextString(vh);
                        bwPlan(url, getStringJson(bwmenber02).toString());
                        vh.mLayoutPlan02.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPag--;
                    bwPlanNum.setmPag(mPag);
                }
            });

            vh.bwplanDelItem03 = (Button) convertView
                    .findViewById(R.id.bwplan_delitembtn02);
            vh.bwplanDelItem03.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    vh.mLayoutPlan03.setVisibility(View.GONE);
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    try {
                        String url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
                        getTextString(vh);
                        bwPlan(url, getStringJson(bwmenber03).toString());
                        vh.mLayoutPlan04.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPag--;
                    bwPlanNum.setmPag(mPag);
                }
            });

            vh.bwplanDelItem04 = (Button) convertView
                    .findViewById(R.id.bwplan_delitembtn03);
            vh.bwplanDelItem04.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    vh.mLayoutPlan04.setVisibility(View.GONE);
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    try {
                        String url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
                        getTextString(vh);
                        bwPlan(url, getStringJson(bwmenber04).toString());
                        vh.mLayoutPlan04.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPag--;
                    bwPlanNum.setmPag(mPag);
                }
            });

            vh.bwplanDelItem05 = (Button) convertView
                    .findViewById(R.id.bwplan_delitembtn04);
            vh.bwplanDelItem05.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, PlanViewHolder holder) {
                    vh.mLayoutPlan05.setVisibility(View.GONE);
                    BwPlanNum bwPlanNum = mBwPlanNums.get(position);
                    String url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
                    getTextString(vh);
                    try {
                        bwPlan(url, getStringJson(bwmenber05).toString());
                        vh.mLayoutPlan05.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPag--;
                    bwPlanNum.setmPag(mPag);
                }
            });

            vh.mLayoutPlan01 = (LinearLayout) convertView
                    .findViewById(R.id.bwplan_layout_01);
            vh.mLayoutPlan02 = (LinearLayout) convertView
                    .findViewById(R.id.bwplan_layout_02);
            vh.mLayoutPlan03 = (LinearLayout) convertView
                    .findViewById(R.id.bwplan_layout_03);
            vh.mLayoutPlan04 = (LinearLayout) convertView
                    .findViewById(R.id.bwplan_layout_04);
            vh.mLayoutPlan05 = (LinearLayout) convertView
                    .findViewById(R.id.bwplan_layout_05);
            convertView.setTag(vh);
        } else {
            vh = (PlanViewHolder) convertView.getTag();
            vh.bwplan_num01.setTag(position);
        }
        BwPlanNum bwPlanNum = mBwPlanNums.get(position);
        vh.bwplan_num01.setText(bwPlanNum.getName());
        vh.bpMenbeNum01.setAdapter(arrayAdapter);
        vh.bpMenbeNum02.setAdapter(arrayAdapter);
        vh.bpMenbeNum03.setAdapter(arrayAdapter);
        vh.bpMenbeNum04.setAdapter(arrayAdapter);
        vh.bpMenbeNum05.setAdapter(arrayAdapter);
        vh.bwplan_end.setSelection(bwPlanNum.getPolicysel());

        if (bwPlanNum.bwPlanTag) {
            vh.bwplanSetBtn.setVisibility(View.GONE);
            vh.bwplanAddbtn.setVisibility(View.VISIBLE);
        } else {
            vh.bwplanSetBtn.setVisibility(View.VISIBLE);
            vh.bwplanAddbtn.setVisibility(View.GONE);
        }

        // if (mData == null) {
        // return null;
        // }
        // vh.bwplan_num01.setText(bwPlanNum.getName());

        // if (mMem != null) {
        // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
        // mContext, android.R.layout.simple_dropdown_item_1line, mMem);
        // vh.bpMenbeNum01.setAdapter(arrayAdapter);
        // }

        // // 修改某个策略
        // vh.bwplanSetBtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // url = XTHttpUtil.POST_BWSET_AVD_POLICY_MODI;
        // //getTextString(vh);
        // try {
        // bwPlan(url, getStringModi().toString());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });

        // // 添加一条Item
        // vh.bwplanIpbtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // if (mPag <= 4 || mPag >= 0) {
        //
        // }
        // }
        // });

        // // 增加一个策略
        // vh.bwplanAddbtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // url = XTHttpUtil.POST_BWSET_AVD_POLICY_ADD;
        // getTextString(vh);
        // try {
        // bwPlan(url, getAddPlan(bwmenber01).toString());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });

        // // 删除第一个
        // vh.bwplanDelItem01.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // try {
        // // url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
        // getTextString(vh);
        // bwPlan(url, getStringJson(bwmenber01).toString());
        // vh.mLayoutPlan01.setVisibility(View.GONE);
        // mPag--;
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });

		/*
         * vh.bwplanDelItem02.setOnClickListener(new OnClickListener() {
		 *
		 * @Override public void onClick(View v) { // url =
		 * XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM; getTextString(vh); try {
		 * bwPlan(url, getStringJson(bwmenber02).toString());
		 * vh.mLayoutPlan02.setVisibility(View.GONE); mPag--; } catch (Exception
		 * e) { e.printStackTrace(); }
		 *
		 * } });
		 */

        // vh.bwplanDelItem03.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
        // getTextString(vh);
        // try {
        // bwPlan(url, getStringJson(bwmenber03).toString());
        // vh.mLayoutPlan03.setVisibility(View.GONE);
        // mPag--;
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });

		/*
		 * vh.bwplanDelItem04.setOnClickListener(new OnClickListener() {
		 *
		 * @Override public void onClick(View v) { // url =
		 * XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM; getTextString(vh); try {
		 * bwPlan(url, getStringJson(bwmenber04).toString());
		 * vh.mLayoutPlan04.setVisibility(View.GONE); mPag--; } catch (Exception
		 * e) { e.printStackTrace(); } } });
		 */
        // vh.bwplanDelItem05.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // url = XTHttpUtil.POST_BWSET_AVD_POLICY_DELMEM;
        // getTextString(vh);
        // try {
        // bwPlan(url, getStringJson(bwmenber05).toString());
        // vh.mLayoutPlan05.setVisibility(View.GONE);
        // mPag--;
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });

        // // 删除一条Item
        // vh.bwplanDelbtn.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // mData.remove(position);
        // notifyDataSetChanged();
        // try {
        // url = XTHttpUtil.GET_BWSET_AVD_POLICY_DEL;
        // getTextString(vh);
        // bwPlan(url, new JSONObject().put("name", bwName).toString());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // });
        return convertView;
    }

    private void getTextString(PlanViewHolder vh) {
        bwName = vh.bwplan_num01.getText().toString();
        bwmenber01 = vh.bpMenbeNum01.getSelectedItem().toString();
        bwmenber02 = vh.bpMenbeNum02.getSelectedItem().toString();
        bwmenber03 = vh.bpMenbeNum03.getSelectedItem().toString();
        bwmenber04 = vh.bpMenbeNum04.getSelectedItem().toString();
        bwmenber05 = vh.bpMenbeNum05.getSelectedItem().toString();

        // bwEnd = vh.bwplan_end.getSelectedItem().toString();
        if (vh.bwplan_end.getSelectedItem().toString().equals("默认（允许）")) {
            bwEnd = "0";
        } else if (vh.bwplan_end.getSelectedItem().toString().equals("目标不可达")) {
            bwEnd = "1";
        } else if (vh.bwplan_end.getSelectedItem().toString().equals("丢弃")) {
            bwEnd = "2";
        }
    }

    // 删除成员
    private JSONObject getStringJson(String bwmenber) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", bwName);
            json.put("memname", bwmenber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    // 添加一个策略
    private JSONObject getAddPlan() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", bwName);
            if (bwmenber01 != null) {
                json.put("mem1", bwmenber01);
            }
            if (bwmenber02 != null) {
                json.put("mem2", bwmenber02);
            }
            if (bwmenber03 != null) {
                json.put("mem3", bwmenber03);
            }
            if (bwmenber04 != null) {
                json.put("mem4", bwmenber04);
            }
            if (bwmenber05 != null) {
                json.put("mem5", bwmenber05);
            }
            json.put("policysel", bwEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    // 修改某个策略
    private JSONObject getStringModi() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", bwName);
            json.put("memname", "");
            if (bwmenber01 != null) {
                json.put("mem1", bwmenber01);
            }
            if (bwmenber02 != null) {
                json.put("mem2", bwmenber02);
            }
            if (bwmenber03 != null) {
                json.put("mem3", bwmenber03);
            }
            if (bwmenber04 != null) {
                json.put("mem4", bwmenber04);
            }
            if (bwmenber05 != null) {
                json.put("mem5", bwmenber05);
            }
            json.put("policysel", bwEnd);
            //	json.put("mennum", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    // 删除某个策略
    private void bwPlan(String url, String json) throws Exception {
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
                        pdDismiss(response);
                        loginDataQuery(response.result);
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

    // 有数据的时候ProgressDialog消失
    private void pdDismiss(Object object) {
        if (object != null) {
            pd.dismiss();
        }
    }

    // 动态添加List里面数据
    public void addItem(BwPlanNum n) {
        mBwPlanNums.add(n);
    }
    // 删除操作回调
    public interface OnListRemovedListener {
        void onRemoved();
    }

    class PlanViewHolder {
        TextView plan_mingcheng, plan_youxianji;

        EditText bwplan_num01;
        Button bwplanAddbtn, bwplanDelbtn, bwplanSetBtn, bwplanIpbtn,
                bwplanDelItem01, bwplanDelItem02, bwplanDelItem03,
                bwplanDelItem04, bwplanDelItem05;
        Spinner bwplan_end, bpMenbeNum01, bpMenbeNum02, bpMenbeNum03,
                bpMenbeNum04, bpMenbeNum05;
        LinearLayout mLayoutPlan01, mLayoutPlan02, mLayoutPlan03,
                mLayoutPlan04, mLayoutPlan05;
    }

    private abstract class MyTextWatcher implements TextWatcher {
        private PlanViewHolder mHolder;

        public MyTextWatcher(PlanViewHolder holder) {
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

        public abstract void afterTextChanged(Editable s, PlanViewHolder holder);
    }

    private abstract class MyOnClickListener implements OnClickListener {

        private PlanViewHolder mHolder;

        public MyOnClickListener(PlanViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, PlanViewHolder holder);

    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private PlanViewHolder holder;

        public MySpinnerIpListener(PlanViewHolder holder) {
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
                                            int arg2, long arg3, PlanViewHolder holder);
    }

}