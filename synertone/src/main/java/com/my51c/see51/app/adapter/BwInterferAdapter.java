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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.app.domian.InteferNum;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.BwSuperInteferActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.my51c.see51.common.AppData;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.List;

public class BwInterferAdapter extends BaseAdapter {
    protected static final String TAG = "BwInterferAdapter";
    public static List<InteferNum> mInterferNums;
    public static List<String> mCurrName;
    Boolean tagOnClick = false;
    int mPag;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int page;
    private RequestQueue mRequestQueue;
    private OnListRemovedListener mListener;
    private boolean statuTag;
    private String nameStr, ed01Str, ed02Str, ed03Str, ed04Str, ed05Str,
            ed06Str, ipcountStr, groupCountStr, timeOutStr, timeSpaceStr,
            notNetNumStr, netNumStr;
    private int enableStr;
    // 加载数据的 ProgressDialog
    private ProgressDialog pd;
    private boolean progresshow;

    public BwInterferAdapter(List<InteferNum> mInterferNums, Context mContext,
                             int page, RequestQueue mRequestQueue) {
        super();
        BwInterferAdapter.mInterferNums = mInterferNums;
        this.mContext = mContext;
        this.page = page;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.mRequestQueue = mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public void setOnListRemovedListener(OnListRemovedListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        // return mInterferNums.size();
        return null == mInterferNums ? 0 : mInterferNums.size();
    }

    @Override
    public InteferNum getItem(int position) {
        return mInterferNums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final InterferViewHolder vh;
        if (convertView == null) {
            vh = new InterferViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.interfer_listview_item, null);
            //这是字体改变
            vh.inter_name = (TextView) convertView
                    .findViewById(R.id.inter_name);
            vh.inter_name.setTypeface(AppData.fontXiti);
            vh.inter_kekao = (TextView) convertView
                    .findViewById(R.id.inter_kekao);
            vh.inter_kekao.setTypeface(AppData.fontXiti);
            vh.inter_meizu = (TextView) convertView
                    .findViewById(R.id.inter_meizu);
            vh.inter_meizu.setTypeface(AppData.fontXiti);
            vh.inter_timeout = (TextView) convertView
                    .findViewById(R.id.inter_timeout);
            vh.inter_timeout.setTypeface(AppData.fontXiti);
            vh.inter_timeduring = (TextView) convertView
                    .findViewById(R.id.inter_timeduring);
            vh.inter_timeduring.setTypeface(AppData.fontXiti);
            vh.inter_linestop = (TextView) convertView
                    .findViewById(R.id.inter_linestop);
            vh.inter_linestop.setTypeface(AppData.fontXiti);
            vh.inter_connline = (TextView) convertView
                    .findViewById(R.id.inter_connline);
            vh.inter_connline.setTypeface(AppData.fontXiti);


            vh.textView = (TextView) convertView
                    .findViewById(R.id.bw_text_view);

            // 开关按钮
            vh.interferOnOff = (ToggleButton) convertView
                    .findViewById(R.id.interfer_bwonoff);
            vh.interferOnOff.setTag(position);
            vh.interferOnOff
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            int position = (Integer) vh.bwinterfer_name
                                    .getTag();
                            if (isChecked) {
                                enableStr = 0;
                            } else {
                                enableStr = 1;
                            }
                        }
                    });

            vh.bwinterfer_name = (EditText) convertView
                    .findViewById(R.id.bwinterfer_name);
            vh.bwinterfer_name.setTag(position);
            vh.bwinterfer_name.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setName(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_ed01 = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ed01);
            vh.bwinterfer_ed01.setTag(position);
            vh.bwinterfer_ed01.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setIp1(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_ed02 = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ed02);
            vh.bwinterfer_ed02.setTag(position);
            vh.bwinterfer_ed02.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setIp2(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_ed03 = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ed03);
            vh.bwinterfer_ed03.setTag(position);
            vh.bwinterfer_ed03.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setIp3(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_ed04 = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ed04);
            vh.bwinterfer_ed04.setTag(position);
            vh.bwinterfer_ed04.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setIp4(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_ed05 = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ed05);
            vh.bwinterfer_ed05.setTag(position);
            vh.bwinterfer_ed05.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setIp5(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_ed06 = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ed06);
            vh.bwinterfer_ed06.setTag(position);
            vh.bwinterfer_ed06.addTextChangedListener(new MyTextIpWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setIp6(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            // 点击添加成员（Ip）的按钮
            vh.bwinterfer_ipbtn = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipbtn);
            vh.bwinterfer_ipbtn.setOnClickListener(new MyOnClickListener(vh) {

                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    int Pag = n.getIpssum();
                    Log.e(TAG, "Pag的数量为------------》" + Pag);
                    if (Pag <= 6) {
                        Pag++;
                        if (Pag == 1) {
                            vh.bwinterfer_ip_add.setVisibility(View.VISIBLE);
                        } else if (Pag == 2) {
                            vh.bwinterfer_ip_add01.setVisibility(View.VISIBLE);
                        } else if (Pag == 3) {
                            vh.bwinterfer_ip_add02.setVisibility(View.VISIBLE);
                        } else if (Pag == 4) {
                            vh.bwinterfer_ip_add03.setVisibility(View.VISIBLE);
                        } else if (Pag == 5) {
                            vh.bwinterfer_ip_add04.setVisibility(View.VISIBLE);
                        } else if (Pag == 6) {
                            vh.bwinterfer_ip_add05.setVisibility(View.VISIBLE);
                        }
                    } else {
                        return;
                    }

                }
            });

            vh.bwinterfer_ipcount = (EditText) convertView
                    .findViewById(R.id.bwinterfer_ipcount);
            vh.bwinterfer_ipcount.setTag(position);
            vh.bwinterfer_ipcount
                    .addTextChangedListener(new MyTextIpWatcher(vh) {
                        @Override
                        public void afterTextChanged(Editable s,
                                                     InterferViewHolder holder) {
                            int position = (Integer) vh.bwinterfer_name
                                    .getTag();
                            InteferNum n = mInterferNums.get(position);
                            n.setIpsnum(s.toString());
                            mInterferNums.set(position, n);
                        }
                    });

            vh.bwinterfer_group_count = (EditText) convertView
                    .findViewById(R.id.bwinterfer_group_count);
            vh.bwinterfer_group_count.setTag(position);
            vh.bwinterfer_group_count
                    .addTextChangedListener(new MyTextIpWatcher(vh) {
                        @Override
                        public void afterTextChanged(Editable s,
                                                     InterferViewHolder holder) {
                            int position = (Integer) vh.bwinterfer_name
                                    .getTag();
                            InteferNum n = mInterferNums.get(position);
                            n.setCount(s.toString());
                            mInterferNums.set(position, n);
                        }
                    });

            vh.bwinterfer_time_out = (EditText) convertView
                    .findViewById(R.id.bwinterfer_time_out);
            vh.bwinterfer_time_out.setTag(position);
            vh.bwinterfer_time_out.addTextChangedListener(new MyTextIpWatcher(
                    vh) {
                @Override
                public void afterTextChanged(Editable s,
                                             InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    n.setTimeout(s.toString());
                    mInterferNums.set(position, n);
                }
            });

            vh.bwinterfer_time_space = (EditText) convertView
                    .findViewById(R.id.bwinterfer_time_space);
            vh.bwinterfer_time_space.setTag(position);
            vh.bwinterfer_time_space
                    .addTextChangedListener(new MyTextIpWatcher(vh) {
                        @Override
                        public void afterTextChanged(Editable s,
                                                     InterferViewHolder holder) {
                            int position = (Integer) vh.bwinterfer_name
                                    .getTag();
                            InteferNum n = mInterferNums.get(position);
                            n.setInterval(s.toString());
                            mInterferNums.set(position, n);
                        }
                    });

            vh.bwinterfer_notnet_num = (EditText) convertView
                    .findViewById(R.id.bwinterfer_notnet_num);
            vh.bwinterfer_notnet_num.setTag(position);
            vh.bwinterfer_notnet_num
                    .addTextChangedListener(new MyTextIpWatcher(vh) {
                        @Override
                        public void afterTextChanged(Editable s,
                                                     InterferViewHolder holder) {
                            int position = (Integer) vh.bwinterfer_name
                                    .getTag();
                            InteferNum n = mInterferNums.get(position);
                            n.setLost(s.toString());
                            mInterferNums.set(position, n);
                        }
                    });

            vh.bwinterfer_net_num = (EditText) convertView
                    .findViewById(R.id.bwinterfer_net_num);
            vh.bwinterfer_net_num.setTag(position);
            vh.bwinterfer_net_num
                    .addTextChangedListener(new MyTextIpWatcher(vh) {
                        @Override
                        public void afterTextChanged(Editable s,
                                                     InterferViewHolder holder) {
                            int position = (Integer) vh.bwinterfer_name
                                    .getTag();
                            InteferNum n = mInterferNums.get(position);
                            n.setConnect(s.toString());
                            mInterferNums.set(position, n);
                        }
                    });

            vh.bwinterfer_ip_add = (LinearLayout) convertView
                    .findViewById(R.id.bwinterfer_ip_add);
            vh.bwinterfer_ip_add01 = (LinearLayout) convertView
                    .findViewById(R.id.bwinterfer_ip_add01);
            vh.bwinterfer_ip_add02 = (LinearLayout) convertView
                    .findViewById(R.id.bwinterfer_ip_add02);
            vh.bwinterfer_ip_add03 = (LinearLayout) convertView
                    .findViewById(R.id.bwinterfer_ip_add03);
            vh.bwinterfer_ip_add04 = (LinearLayout) convertView
                    .findViewById(R.id.bwinterfer_ip_add04);
            vh.bwinterfer_ip_add05 = (LinearLayout) convertView
                    .findViewById(R.id.bwinterfer_ip_add05);

            vh.bwinterfer_ipbtn = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipdelbtn);
            vh.bwinterfer_ipbtn.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);

                    vh.bwinterfer_ip_add.setVisibility(View.GONE);
                    // if (ed01Str!=null) {
                    String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DELIP;
                    getTextStr(vh);
                    try {
                        postInterfer(url, new JSONObject().put("name", nameStr)
                                .put("ip", ed01Str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                    ed01Str = null;
                    mPag--;
                }
            });


            //删除ip的一系列按钮
            vh.bwinterfer_ipbtndele01 = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipbtndele01);
            vh.bwinterfer_ipbtndele01.setOnClickListener(new MyOnClickListener(
                    vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);

                    vh.bwinterfer_ip_add01.setVisibility(View.GONE);
                    // if (ed02Str!=null) {
                    String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DELIP;
                    getTextStr(vh);
                    try {
                        postInterfer(url, new JSONObject().put("name", nameStr)
                                .put("ip", ed02Str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                    ed02Str = null;
                    mPag--;
                }
            });

            vh.bwinterfer_ipbtndele02 = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipbtndele02);
            vh.bwinterfer_ipbtndele02.setOnClickListener(new MyOnClickListener(
                    vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    vh.bwinterfer_ip_add02.setVisibility(View.GONE);
                    // if (ed03Str!=null) {

                    String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DELIP;
                    getTextStr(vh);
                    try {
                        postInterfer(url, new JSONObject().put("name", nameStr)
                                .put("ip", ed03Str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                    ed03Str = null;
                    mPag--;
                }
            });

            vh.bwinterfer_ipbtndele03 = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipbtndele03);
            vh.bwinterfer_ipbtndele03.setOnClickListener(new MyOnClickListener(
                    vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    vh.bwinterfer_ip_add03.setVisibility(View.GONE);
                    // if (ed04Str != null) {
                    String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DELIP;
                    getTextStr(vh);
                    try {
                        postInterfer(url, new JSONObject().put("name", nameStr)
                                .put("ip", ed04Str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                    ed04Str = null;
                    mPag--;
                }
            });

            vh.bwinterfer_ipbtndele04 = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipbtndele04);
            vh.bwinterfer_ipbtndele04.setOnClickListener(new MyOnClickListener(
                    vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    vh.bwinterfer_ip_add04.setVisibility(View.GONE);

                    // if (ed05Str != null) {
                    String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DELIP;
                    getTextStr(vh);
                    try {
                        postInterfer(url, new JSONObject().put("name", nameStr)
                                .put("ip", ed05Str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                    ed05Str = null;
                    mPag--;
                }
            });

            vh.bwinterfer_ipbtndele05 = (Button) convertView
                    .findViewById(R.id.bwinterfer_ipbtndele05);
            vh.bwinterfer_ipbtndele05.setOnClickListener(new MyOnClickListener(
                    vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    vh.bwinterfer_ip_add05.setVisibility(View.GONE);
                    try {
                        // if (ed06Str != null) {
                        getTextStr(vh);
                        String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DELIP;
                        postInterfer(url, new JSONObject().put("name", nameStr)
                                .put("ip", ed06Str));
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ed06Str = null;
                    mPag--;
                }
            });

            // 删除某个接口（点击删除）
            vh.btDelete = (Button) convertView
                    .findViewById(R.id.bwinterfer_dianji_dele);
            vh.btDelete.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    if (mListener != null) {
                        int position = (Integer) vh.bwinterfer_name.getTag();
                        InteferNum n = mInterferNums.get(position);

                        if (BwSuperInteferActivity.nameList.contains(n.getName())) {//存在
                            BwSuperInteferActivity.nameList.remove(n.getName());
                            Log.e(TAG, "点击了删除----->在--------此时删除的已经在列表里面--------->" + n.name);
                        }
                        Log.e(TAG, "点击了删除----->不在--------此时删除的已经在列表里面--------->" + n.name);
                        if (n.isInterferTag()) {
                            String url = XTHttpUtil.POST_BWSET_AVD_INTFER_DEL;
                            getTextStr(vh);
                            try {
                                postInterfer(url,
                                        new JSONObject().put("name", nameStr));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mInterferNums.remove(position);
                        mListener.onRemoved(); //通知主线程更新Adapter
                    }
                }
            });
            // 添加一个接口(点击保存;修改或者是新增)
            vh.btSet = (Button) convertView
                    .findViewById(R.id.bwinterfer_dianji_set);
            vh.btSet.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, InterferViewHolder holder) {
                    //在这里  判断是新增还是修改
                    int position = (Integer) vh.bwinterfer_name.getTag();
                    InteferNum n = mInterferNums.get(position);
                    //String name =n.getName();
                    String urlAdd = XTHttpUtil.POST_BWSET_AVD_INTFER_ADD;
                    String urlModi = XTHttpUtil.POST_BWSET_AVD_INTFER_MODI;
                    getTextStr(vh);
                    Log.e(TAG, "n.getName()------->" + n.getName());
                    if (BwSuperInteferActivity.nameList.contains(n.getName())) {//修改
                        try {
                            JSONObject object = XTHttpJSON.postInterfer(enableStr,
                                    nameStr, 0, ed01Str, ed02Str, ed03Str, ed04Str,
                                    ed05Str, ipcountStr, groupCountStr, timeOutStr,
                                    timeSpaceStr, notNetNumStr, netNumStr);
                            postInterfer(urlAdd, object);
                            Log.e(TAG, "存在------此时是----修改----接口。。。。。。。。。。------->" + object.toString());
                            n.setInterferTag(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {//不存在   就是  新增
                        try {
                            JSONObject object = XTHttpJSON.postInterfer(enableStr,
                                    nameStr, 0, ed01Str, ed02Str, ed03Str, ed04Str,
                                    ed05Str, ipcountStr, groupCountStr, timeOutStr,
                                    timeSpaceStr, notNetNumStr, netNumStr);
                            BwSuperInteferActivity.nameList.add(n.getName());
                            postInterfer(urlModi, object);
                            Log.e(TAG, "不存在------此时是------新增------接口。。。。。。。。。。------->" + object.toString());
                            n.setInterferTag(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            convertView.setTag(vh);
        } else {
            vh = (InterferViewHolder) convertView.getTag();
            vh.bwinterfer_name.setTag(position);
        }
        InteferNum mInteferNum = mInterferNums.get(position);
        vh.textView.setText(mInteferNum.getName());
        vh.bwinterfer_name.setText(mInteferNum.getName());

        if (!TextUtils.isEmpty(mInteferNum.getIp1())) {
            vh.bwinterfer_ip_add.setVisibility(View.VISIBLE);
            vh.bwinterfer_ed01.setText(mInteferNum.getIp1());
        } else {
            ed01Str = null;
        }
        if (!TextUtils.isEmpty(mInteferNum.getIp2())) {
            vh.bwinterfer_ip_add01.setVisibility(View.VISIBLE);
            vh.bwinterfer_ed02.setText(mInteferNum.getIp2());
        } else {
            ed02Str = null;
        }
        if (!TextUtils.isEmpty(mInteferNum.getIp3())) {
            vh.bwinterfer_ip_add02.setVisibility(View.VISIBLE);
            vh.bwinterfer_ed03.setText(mInteferNum.getIp3());
        } else {
            ed03Str = null;
        }

        if (!TextUtils.isEmpty(mInteferNum.getIp4())) {
            vh.bwinterfer_ip_add03.setVisibility(View.VISIBLE);
            vh.bwinterfer_ed04.setText(mInteferNum.getIp4());
        } else {
            ed04Str = null;
        }

        if (!TextUtils.isEmpty(mInteferNum.getIp5())) {
            vh.bwinterfer_ip_add05.setVisibility(View.VISIBLE);
            vh.bwinterfer_ed05.setText(mInteferNum.getIp5());
        } else {
            ed05Str = null;
        }
        vh.bwinterfer_ipcount.setText(mInteferNum.getIpsnum());
        vh.bwinterfer_group_count.setText(mInteferNum.getCount());
        vh.bwinterfer_time_out.setText(mInteferNum.getTimeout());
        vh.bwinterfer_time_space.setText(mInteferNum.getInterval());
        vh.bwinterfer_notnet_num.setText(mInteferNum.getLost());
        vh.bwinterfer_net_num.setText(mInteferNum.getConnect());
        return convertView;
    }

    private void getTextStr(InterferViewHolder vh) {
        nameStr = vh.bwinterfer_name.getText().toString();
        if (vh.bwinterfer_ed01.getText() != null) {
            ed01Str = vh.bwinterfer_ed01.getText().toString();
        } else {
            ed01Str = null;
        }
        if (vh.bwinterfer_ed02.getText() != null) {
            ed02Str = vh.bwinterfer_ed02.getText().toString();
        } else {
            ed02Str = null;
        }
        if (vh.bwinterfer_ed03.getText() != null) {
            ed03Str = vh.bwinterfer_ed03.getText().toString();
        } else {
            ed03Str = null;
        }
        if (vh.bwinterfer_ed04.getText() != null) {
            ed04Str = vh.bwinterfer_ed04.getText().toString();
        } else {
            ed04Str = null;
        }
        if (vh.bwinterfer_ed05.getText() != null) {
            ed05Str = vh.bwinterfer_ed05.getText().toString();
        } else {
            ed05Str = null;
        }
        if (vh.bwinterfer_ed06.getText() != null) {
            ed06Str = vh.bwinterfer_ed06.getText().toString();
        } else {
            ed06Str = null;
        }
        ipcountStr = vh.bwinterfer_ipcount.getText().toString();
        groupCountStr = vh.bwinterfer_group_count.getText().toString();
        timeOutStr = vh.bwinterfer_time_out.getText().toString();
        timeSpaceStr = vh.bwinterfer_time_space.getText().toString();
        notNetNumStr = vh.bwinterfer_notnet_num.getText().toString();
        netNumStr = vh.bwinterfer_net_num.getText().toString();
    }

    // // 删除的请求
    // private void iplimitDel(InterferViewHolder vh) {
    // progresshow = true;
    // showDia();
    // JsonObjectRequest request = new JsonObjectRequest(Method.POST,
    // XTHttpUtil.POST_IPLIMIT_DEL, null, new Listener<JSONObject>() {
    // @Override
    // public void onResponse(JSONObject response) {
    // pdDismiss(response);
    // Log.i(TAG, "接收回来的数据===》" + response.toString());
    // loadData(response);// 加载数据
    // }
    // }, new ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // Log.i(TAG, error.toString());
    // Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT)
    // .show();
    // if (pd.isShowing()) {
    // pd.dismiss();
    // }
    // }
    // });
    // mRequestQueue.add(request);
    // }

    // 设置的请求
    private void postInterfer(String url, JSONObject object) {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.POST, url,
                object, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pdDismiss(response);
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
    public void addItem(InteferNum n) {
        mInterferNums.add(n);
    }

    // 删除操作回调
    public interface OnListRemovedListener {
        void onRemoved();
    }

    class InterferViewHolder {
        ImageView imgView;
        TextView textView;
        EditText bwinterfer_name, bwinterfer_ed01, bwinterfer_ed02,
                bwinterfer_ed03, bwinterfer_ed04, bwinterfer_ed05,
                bwinterfer_ed06, bwinterfer_ipcount, bwinterfer_group_count,
                bwinterfer_time_out, bwinterfer_time_space,
                bwinterfer_notnet_num, bwinterfer_net_num;
        LinearLayout bwinterfer_ip_add, bwinterfer_ip_add01,
                bwinterfer_ip_add02, bwinterfer_ip_add03, bwinterfer_ip_add04,
                bwinterfer_ip_add05;
        Button bwinterfer_ipbtndele01, bwinterfer_ipbtndele02,
                bwinterfer_ipbtndele03, bwinterfer_ipbtndele04,
                bwinterfer_ipbtndele05, bwinterfer_ipbtn, btDelete, btSet;
        ToggleButton interferOnOff;

        TextView inter_name, inter_kekao, inter_meizu, inter_timeout, inter_timeduring, inter_linestop, inter_connline;

    }

    private abstract class MyTextIpWatcher implements TextWatcher {
        private InterferViewHolder mHolder;

        public MyTextIpWatcher(InterferViewHolder holder) {
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
                                              InterferViewHolder holder);
    }

    private abstract class MySpinnerIpListener implements
            OnItemSelectedListener {
        private InterferViewHolder holder;

        public MySpinnerIpListener(InterferViewHolder holder) {
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
                                            int arg2, long arg3, InterferViewHolder holder);
    }

    private abstract class MyOnClickListener implements OnClickListener {
        private InterferViewHolder mHolder;

        public MyOnClickListener(InterferViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, InterferViewHolder holder);

    }

}
