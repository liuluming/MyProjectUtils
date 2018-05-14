package com.my51c.see51.app.activity.aggregation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.my51c.see51.app.bean.ChannelModel;
import com.my51c.see51.app.bean.QueryServiceModel;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.yzxvoip.StringUtils;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.synertone.netAssistant.R;
import com.yzx.tools.DensityUtil;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.synertone.netAssistant.R.id.tv_middle_title;

/**
 * Created by snt1206 on 2018/1/12.
 */

public class TrunkServiceActivity extends BaseActivity {
    private TextView mTittle;
    private PullToRefreshListView plv_service_content;
    private ImageView iv_right;
    private List<QueryServiceModel.ServiceListBean> serviceModels=new ArrayList<>();
    private CommonAdapter<QueryServiceModel.ServiceListBean> serviceAdapter ;
    private static final int AMEND = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trunk_service_activity);
        initView();
        initData();
        queryServiceInfoList();
    }

    private void initView() {
        mTittle= (TextView)findViewById(tv_middle_title);
        mTittle.setText(R.string.trunk_service);
        iv_right=(ImageView)findViewById(R.id.iv_right);
        iv_right.setVisibility(View.VISIBLE);
        plv_service_content= (PullToRefreshListView) findViewById(R.id.plv_service_content);
        plv_service_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                queryServiceInfoList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void initData() {
        serviceAdapter = new CommonAdapter<QueryServiceModel.ServiceListBean>(mContext, R.layout.service_content, serviceModels) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, final QueryServiceModel.ServiceListBean item) {
                viewHolder.setTextForTextView(R.id.tv_service_name, item.getName());
                viewHolder.setTextForTextView(R.id.tv_service_priority, "优先级："+item.getPriority());
                viewHolder.setTextForTextView(R.id.tv_sour_ip, "源IP："+item.getSourIP());
                viewHolder.setTextForTextView(R.id.tv_sour_port, "源端口："+item.getSourPort());
                viewHolder.setTextForTextView(R.id.tv_dest_ip, "目标IP："+item.getDestIP());
                viewHolder.setTextForTextView(R.id.tv_dest_port, "目标端口："+item.getDestPort());
                if("0".equals(item.getProtocol())) {
                    viewHolder.setTextForTextView(R.id.tv_net_protocol, "网络协议：TCP");
                }else{
                    viewHolder.setTextForTextView(R.id.tv_net_protocol, "网络协议：UDP");
                }
                viewHolder.setOnClickListener(R.id.iv_service_amend, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,ConfigServiceActivity.class);
                        intent.putExtra("name",item.getName()+"配置");
                        intent.putExtra("id",item.getOId());
                        intent.putExtra("obj", item);
                        startActivityForResult(intent,AMEND);
                    }
                });
                viewHolder.setOnClickListener(R.id.iv_service_del, new View.OnClickListener() {
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
                                                    doDeleteService(item.getOId());
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
            }

        };
        plv_service_content.setAdapter(serviceAdapter);
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,AddServiceActivity.class);
                intent.putExtra("name","添加业务");
                startActivityForResult(intent,AMEND);
            }
        });
    }

    /**
     * 查询业务配置信息列表
     */
    private void queryServiceInfoList() {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.QueryServiceConfigList, new MyRequestCallBack(mContext, true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                plv_service_content.onRefreshComplete();
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                    QueryServiceModel queryServiceModel = GsonUtils.fromJson(responseInfo.result.toString(), QueryServiceModel.class);
                    if (queryServiceModel != null) {
                        if ("0".equals(queryServiceModel.getCode())) {
                            List<QueryServiceModel.ServiceListBean> serviceList = queryServiceModel.getServiceList();
                            if (serviceModels != null) {
                                serviceModels.clear();
                                serviceModels.addAll(serviceList);
                                serviceAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.oprator_fail,Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                super.onFailure(e, s);
                plv_service_content.onRefreshComplete();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)   {
            case AMEND:
                if(resultCode==RESULT_OK) {
                    queryServiceInfoList();
                }
                break;
        }
    }

    public void onPersonFinish(View view) {
        finish();
    }

    /**
     * 删除业务
     */
    private void doDeleteService(String oId) {
        try {
            RequestParams params = new RequestParams();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oId", oId);
            params.setBodyEntity(new StringEntity(jsonObject.toString()));
            http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.delService, params, new MyRequestCallBack(mContext, true) {
                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    super.onSuccess(responseInfo);
                    if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                        ChannelModel configChannelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                        if (configChannelModel != null) {
                            if ("0".equals(configChannelModel.getCode())) {
                                Toast.makeText(mContext, R.string.del_service_success, Toast.LENGTH_SHORT).show();
                                queryServiceInfoList();
                            } else {
                                Toast.makeText(mContext, R.string.oprator_fail,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
