package com.my51c.see51.app.activity.aggregation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.my51c.see51.BaseActivity;
import com.my51c.see51.adapter.CommonAdapter;
import com.my51c.see51.adapter.CommonViewHolder;
import com.my51c.see51.app.bean.BaseModel;
import com.my51c.see51.app.bean.NetAdapterModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.TransformUtils;
import com.my51c.see51.yzxvoip.StringUtils;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.suke.widget.SwitchButton;
import com.synertone.netAssistant.R;
import com.yzx.tools.DensityUtil;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

public class NetAdapterSettingActivity extends BaseActivity {
    private static final int AMEND = 0;
    private static final int ADD = 1;
    @BindView(R.id.more_finish)
    ImageButton moreFinish;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.plv_content)
    PullToRefreshListView plvContent;
    private CommonAdapter<NetAdapterModel.AdapterListBean> netAdapter;
    private ArrayList<NetAdapterModel.AdapterListBean> interfaceListBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_adapter_setting);
        initView();
        initData();
        initEvent();
        doNetworkAdapterList(true);
    }

    private void doNetworkAdapterList(boolean b) {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.NetworkAdapterList, new MyRequestCallBack(mContext, b) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                plvContent.onRefreshComplete();
                if (!isVisible()) {
                    return;
                }
                String result = responseInfo.result.toString();
                if (!StringUtils.isEmpty(result)) {
                    NetAdapterModel netAdapterModel = GsonUtils.fromJson(result, NetAdapterModel.class);
                    if (netAdapterModel != null) {
                        String code = netAdapterModel.getCode();
                        if ("0".equals(code)) {
                            List<NetAdapterModel.AdapterListBean> interfaceList = netAdapterModel.getAdapterList();
                            if (interfaceList != null && interfaceList.size() > 0) {
                                interfaceListBeans.clear();
                                interfaceListBeans.addAll(interfaceList);
                                netAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initEvent() {
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,NetAdapterAmendActivity.class);
                startActivityForResult(intent,ADD);
            }
        });
        plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetAdapterModel.AdapterListBean interfaceListBean = (NetAdapterModel.AdapterListBean) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, NetAdapterStateActivity.class);
                intent.putExtra("item", interfaceListBean);
                startActivity(intent);
            }
        });
        plvContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doNetworkAdapterList(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void initData() {
        interfaceListBeans = new ArrayList<>();
        netAdapter = new CommonAdapter<NetAdapterModel.AdapterListBean>(mContext, R.layout.item_net_adatper, interfaceListBeans) {

            @Override
            protected void fillItemData(final CommonViewHolder viewHolder, int position, final NetAdapterModel.AdapterListBean item) {
                viewHolder.setTextForTextView(R.id.tv_adapter_name, item.getAdapterName());
                String enable = item.getEnable();
                viewHolder.setCheckForSwitchButton(R.id.switch_button, enable.equals("1"));
                viewHolder.setTextForTextView(R.id.tv_adapter_state, TransformUtils.getConnectionState(item.getStatus(), mContext));
                viewHolder.setTextForTextView(R.id.tv_receive_byte, (item.getRecv().equals("")?"0":item.getRecv()) + getString(R.string.byte1));
                viewHolder.setTextForTextView(R.id.tv_send_byte, (item.getTran().equals("")?"0":item.getTran())+ getString(R.string.byte1));
                viewHolder.setOnClickListener(R.id.iv_adapter_restart, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NiceDialog.init()
                                .setLayoutId(R.layout.confirm_layout)
                                .setConvertListener(new ViewConvertListener() {
                                    @Override
                                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                        holder.setText(R.id.title, getString(R.string.tip));
                                        holder.setText(R.id.message, getString(R.string.is_restart));
                                        holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                try {
                                                    doNetworkAdapterRestart(item.getOId());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setOutCancel(false)
                                .setMargin(DensityUtil.dip2px(mContext,10))
                                .show(getSupportFragmentManager())
                        ;

                    }
                });
                MyOnCheckedChangeListener oncheckListener = new MyOnCheckedChangeListener(item.getOId(),viewHolder);
                viewHolder.getContentView().setTag(R.id.switch_button,oncheckListener);
                viewHolder.setOnSwitchButtonCheckedChangeListener(R.id.switch_button,oncheckListener);
                viewHolder.setOnClickListener(R.id.iv_amend, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NetAdapterAmendActivity.class);
                        intent.putExtra("oId", item.getOId());
                        startActivityForResult(intent, AMEND);
                    }
                });
                viewHolder.setOnClickListener(R.id.iv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NiceDialog.init()
                                .setLayoutId(R.layout.confirm_layout)
                                .setConvertListener(new ViewConvertListener() {
                                    @Override
                                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                        holder.setText(R.id.title, getString(R.string.tip));
                                        holder.setText(R.id.message, getString(R.string.is_del));
                                        holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                try {
                                                    doNetworkAdapterDelete(item.getOId());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setOutCancel(false)
                                .setMargin(DensityUtil.dip2px(mContext,10))
                                .show(getSupportFragmentManager())
                                ;
                    }
                });
                viewHolder.setOnClickListener(R.id.iv_adapter_details, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NetAdapterStateActivity.class);
                        intent.putExtra("item", item);
                        startActivity(intent);
                    }
                });
            }
        };
        plvContent.setAdapter(netAdapter);
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    private void doNetworkAdapterRestart(String oId) throws Exception {
        RequestParams params=new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oId", oId);
        jsonObject.put("operation", "2");
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.NetworkAdapterOperation, params, new MyRequestCallBack(mContext,true,getString(R.string.sending_request)) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if(!isVisible()){
                    return;
                }
                String result = responseInfo.result.toString();
                if(!StringUtils.isEmpty(result)){
                    BaseModel baseModel = GsonUtils.fromJson(result, BaseModel.class);
                    if(baseModel!=null){
                        String code = baseModel.getCode();
                        if("0".equals(code)){
                            Toast.makeText(mContext, R.string.restart_success,Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

            }
        });
    }

    private void doNetworkAdapterDelete(final String oId) throws Exception {
        RequestParams params=new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oId", oId);
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.NetworkAdapterDelete, params, new MyRequestCallBack(mContext,true,getString(R.string.sending_request)) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if(!isVisible()){
                    return;
                }
                String result = responseInfo.result.toString();
                if(!StringUtils.isEmpty(result)){
                    BaseModel baseModel = GsonUtils.fromJson(result, BaseModel.class);
                    if(baseModel!=null){
                        String code = baseModel.getCode();
                        if("0".equals(code)){
                            removeAdapterById(oId);
                        }
                    }
                }
            }
        });

    }

    private void removeAdapterById(String oId) {
        if(interfaceListBeans!=null){
            Iterator<NetAdapterModel.AdapterListBean> iterator = interfaceListBeans.iterator();
            while(iterator.hasNext()){
                NetAdapterModel.AdapterListBean next = iterator.next();
                if(next.getOId().equals(oId)){
                   iterator.remove();
                   break;
                }
            }
            netAdapter.notifyDataSetChanged();
        }
    }

    private void doNetworkAdapterOperation(String oId, final boolean isChecked, final CommonViewHolder viewHolder) throws UnsupportedEncodingException, JSONException {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oId", oId);
        jsonObject.put("operation", isChecked ? "1" : "0");
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.NetworkAdapterOperation, params, new MyRequestCallBack(mContext, true,getString(R.string.sending_request)) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                if (!isVisible()) {
                    return;
                }
                String result = responseInfo.result.toString();
                if (!StringUtils.isEmpty(result)) {
                    BaseModel baseModel = GsonUtils.fromJson(result, BaseModel.class);
                    if (baseModel != null) {
                        String code = baseModel.getCode();
                        if ("0".equals(code)) {
                         return;
                        }
                    }

                }
                revertSwitchButton(viewHolder, isChecked);

            }

            @Override
            public void onFailure(HttpException e, String s) {
                super.onFailure(e, s);
                revertSwitchButton(viewHolder, isChecked);
            }
        });
    }

    private void revertSwitchButton(final CommonViewHolder viewHolder, boolean isChecked) {
        viewHolder.setOnSwitchButtonCheckedChangeListener(R.id.switch_button,null);
        viewHolder.setCheckForSwitchButton(R.id.switch_button,!isChecked);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyOnCheckedChangeListener listener= (MyOnCheckedChangeListener) viewHolder.getContentView().getTag(R.id.switch_button);
                viewHolder.setOnSwitchButtonCheckedChangeListener(R.id.switch_button,listener);
            }
        }, 330);
    }
    private void initView() {
        plvContent = (PullToRefreshListView) findViewById(R.id.plv_content);
        tvMiddleTitle = (TextView) findViewById(R.id.tv_middle_title);
        tvMiddleTitle.setText(R.string.trunk_net);
        ivRight.setVisibility(View.VISIBLE);
    }

    public void onPersonFinish(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD:
                if (resultCode == RESULT_OK) {
                    doNetworkAdapterList(false);
                }
                break;
            case AMEND:
                if (resultCode == RESULT_OK) {
                    doNetworkAdapterList(false);
                }
                break;
            default:
                break;
        }
    }
    class MyOnCheckedChangeListener implements SwitchButton.OnCheckedChangeListener{
        private String oId;
        private CommonViewHolder viewHolder;
        public MyOnCheckedChangeListener(String oId, CommonViewHolder viewHolder) {
            this.oId=oId;
            this.viewHolder=viewHolder;
        }

        @Override
        public void onCheckedChanged(SwitchButton view, final boolean isChecked) {
            NiceDialog.init()
                    .setLayoutId(R.layout.confirm_layout)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            holder.setText(R.id.title, getString(R.string.tip));
                            holder.setText(R.id.message, "是否"+(isChecked?"启用":"禁用"));
                            holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    revertSwitchButton(viewHolder, isChecked);
                                }
                            });
                            holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    try {
                                        doNetworkAdapterOperation(oId, isChecked,viewHolder);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    })
                    .setOutCancel(false)
                    .setMargin(DensityUtil.dip2px(mContext,10))
                    .show(getSupportFragmentManager())
            ;


        }
    }
}
