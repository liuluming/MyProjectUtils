package com.my51c.see51.app.activity.aggregation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.my51c.see51.app.bean.LinkListState;
import com.my51c.see51.app.http.MyRequestCallBack;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.utils.GsonUtils;
import com.my51c.see51.app.utils.TransformUtils;
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
import java.util.Iterator;
import java.util.List;

public class AggregatedLinksSettingsActivity extends BaseActivity {
    private static final int AMEND = 0;
    private PullToRefreshListView plv_content;
    private List<LinkListState.LinkListBean> linkListBeans;
    private CommonAdapter<LinkListState.LinkListBean> aggregateLinkAdapter;
    private ImageView iv_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregated_links_settings);
        initView();
        initData();
        initEvent();
        doAggregatedLinkList(true);
    }

    private void doAggregatedLinkList(boolean isShow) {
        http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.AggregatedLinkList, new MyRequestCallBack(mContext,isShow) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                plv_content.onRefreshComplete();
                if(!isVisible()){
                    return;
                }
               if(!StringUtils.isEmpty(responseInfo.result.toString())){
                   LinkListState linkListState = GsonUtils.fromJson(responseInfo.result.toString(), LinkListState.class);
                   if(linkListState!=null){
                       if("0".equals(linkListState.getCode())){
                           List<LinkListState.LinkListBean> linkList = linkListState.getLinkList();
                           if(linkList!=null){
                               linkListBeans.clear();
                               linkListBeans.addAll(linkList);
                               aggregateLinkAdapter.notifyDataSetChanged();
                           }
                       }
                   }
               }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                super.onFailure(e, s);
                plv_content.onRefreshComplete();
            }
        });
    }

    private void initEvent() {
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,AggregateLinksAmendActivity.class);
                startActivityForResult(intent,AMEND);
            }
        });
        plv_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doAggregatedLinkList(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void initData() {
        linkListBeans=new ArrayList<>();
        aggregateLinkAdapter=new CommonAdapter<LinkListState.LinkListBean>(mContext,R.layout.item_aggregated_link,linkListBeans) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, final LinkListState.LinkListBean item) {
                viewHolder.setTextForTextView(R.id.tv_aggregated_name,item.getName());
                viewHolder.setTextForTextView(R.id.tv_aggregated_mode,TransformUtils.getAggregatedMode(item.getMode(),mContext));
                viewHolder.setTextForTextView(R.id.tv_receive_byte,item.getRecv()+getString(R.string.byte1));
                viewHolder.setTextForTextView(R.id.tv_send_byte,item.getTran()+getString(R.string.byte1));
                viewHolder.setAdapterForListView(R.id.lv_channels,new CommonAdapter<LinkListState.LinkListBean.ChannelListBean>(mContext,R.layout.item_channel_state,item.getChannelList()){

                    @Override
                    protected void fillItemData(CommonViewHolder viewHolder, int position, LinkListState.LinkListBean.ChannelListBean itemChannel) {
                        viewHolder.setTextForTextView(R.id.tv_channel_name,itemChannel.getChannel());
                        viewHolder.setTextForTextView(R.id.tv_channel_state, TransformUtils.getConnectionState(itemChannel.getLinkStatus(),mContext));
                        LinkListState.LinkListBean.ChannelListBean.SimInfoBean simInfo = itemChannel.getSimInfo();
                        if(simInfo!=null){
                            viewHolder.setTextForTextView(R.id.tv_sim_state,TransformUtils.getSimState(simInfo.getSim(),mContext));
                            viewHolder.setTextForTextView(R.id.tv_mobile_operator,TransformUtils.getMobileOperator(simInfo.getMno(),mContext));
                            viewHolder.setTextForTextView(R.id.tv_mobile_standard,  TransformUtils.getMobileStandard(simInfo.getStandard(),mContext));
                            viewHolder.setTextForTextView(R.id.tv_mobile_signal,simInfo.getSignal());
                        }
                    }
                });
                viewHolder.setHeightForListView(R.id.lv_channels);
                viewHolder.setOnClickListener(R.id.iv_amend, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,AggregateLinksAmendActivity.class);
                        intent.putExtra("oId",item.getOId());
                        startActivityForResult(intent,AMEND);
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
                                                    doAggregatedLinkDelete(item.getOId());
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
        plv_content.setAdapter(aggregateLinkAdapter);
        plv_content.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    private void doAggregatedLinkDelete(final String oId) throws Exception {
        RequestParams params=new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oId", oId);
        params.setBodyEntity(new StringEntity(jsonObject.toString()));
        http.send(HttpRequest.HttpMethod.POST, XTHttpUtil.AggregatedLinkDelete, params, new MyRequestCallBack(mContext,true,getString(R.string.sending_request)) {
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
                            removeAggregatedLinkById(oId);
                        }
                    }
                }
            }
        });
    }

    private void removeAggregatedLinkById(String oId) {
        if(linkListBeans!=null){
            Iterator<LinkListState.LinkListBean> iterator = linkListBeans.iterator();
            while(iterator.hasNext()){
                LinkListState.LinkListBean next = iterator.next();
                if(next.getOId().equals(oId)){
                    iterator.remove();
                    break;
                }
            }
            aggregateLinkAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AMEND:
                if(resultCode==RESULT_OK){
                    doAggregatedLinkList(false);
                }
                break;
        }
    }

    private void initView() {
        TextView tv_middle_title= (TextView) findViewById(R.id.tv_middle_title);
        tv_middle_title.setText(R.string.trunk_link_configuration);
        plv_content= (PullToRefreshListView) findViewById(R.id.plv_content);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setVisibility(View.VISIBLE);
    }

    public void onPersonFinish(View view) {
        finish();
    }
}
