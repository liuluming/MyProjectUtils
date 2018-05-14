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


/**
 * Created by snt1206 on 2018/1/12.
 */
public class TrunkChannelActivity extends BaseActivity {
    private TextView mTittle;
    private PullToRefreshListView plv_channel_content;
    private ImageView iv_right;
    private List<ChannelModel.ChannelListBean> channlModels=new ArrayList<>() ;
    private CommonAdapter<ChannelModel.ChannelListBean> commonAdapter ;
    private static final int AMEND = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trunk_channel_activity);
        initView();
        initData();
        doNetworkChannelList();
    }

    private void initView() {
        mTittle= (TextView)findViewById(R.id.tv_middle_title);
        iv_right= (ImageView)findViewById(R.id.iv_right);
        mTittle.setText(R.string.trunk_channel);
        iv_right.setVisibility(View.VISIBLE);
        plv_channel_content= (PullToRefreshListView) findViewById(R.id.plv_channel_content);
        plv_channel_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doNetworkChannelList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void initData() {
       commonAdapter = new CommonAdapter<ChannelModel.ChannelListBean>(mContext, R.layout.channel_content, channlModels) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, final ChannelModel.ChannelListBean item) {
                viewHolder.setTextForTextView(R.id.tv_channel_name, item.getChannel());
                if("0".equals(item.getEncrypt())) {
                    viewHolder.setTextForTextView(R.id.tv_encryption, "加密关闭");
                }else{
                    viewHolder.setTextForTextView(R.id.tv_encryption, "加密开启");
                }
                if("0".equals(item.getCompress())) {
                    viewHolder.setTextForTextView(R.id.tv_compass, "压缩关闭");
                }else{
                    viewHolder.setTextForTextView(R.id.tv_compass, "压缩开启");
                }
                if("0".equals(item.getStatus())) {
                    viewHolder.setTextForTextView(R.id.tv_connect_status,getString(R.string.off_line));
                }else{
                    viewHolder.setTextForTextView(R.id.tv_connect_status,getString(R.string.on_line));
                }
                if(item.getRecv()!=null) {
                    viewHolder.setTextForTextView(R.id.food_fp_has_rest, " "+item.getRecv());
                } else{
                    viewHolder.setTextForTextView(R.id.food_fp_has_rest, " "+"--");
                }
                if(item.getTran()!=null) {
                    viewHolder.setTextForTextView(R.id.send_byte, " "+item.getTran());
                } else{
                    viewHolder.setTextForTextView(R.id.send_byte, " "+"--");
                }
                viewHolder.setTextForTextView(R.id.tv_adapter_net, item.getAdapterName());
                viewHolder.setOnClickListener(R.id.iv_channel_amend, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,ConfigChannelActivity.class);
                        intent.putExtra("name",item.getChannel()+"配置");
                        intent.putExtra("obj", item);
                        startActivityForResult(intent,AMEND);
                    }
                });
                viewHolder.setOnClickListener(R.id.iv_channel_del, new View.OnClickListener() {
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
                                                    doDeleteChannel(item.getOId());
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
        plv_channel_content.setAdapter(commonAdapter);
        plv_channel_content.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,AddConfigChannelActivity.class);
                intent.putExtra("name","添加网络通道");
                startActivityForResult(intent,AMEND);
            }
        });
    }
    /**
     * 查询网络通道列表
     */
    private void doNetworkChannelList() {
        try {
            RequestParams params = new RequestParams();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("used", "1");
            params.setBodyEntity(new StringEntity(jsonObject.toString()));
            http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.NetworkChannelList,params, new MyRequestCallBack(mContext, true) {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                super.onSuccess(responseInfo);
                plv_channel_content.onRefreshComplete();
                if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                    ChannelModel channelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                    if (channelModel != null) {
                        if ("0".equals(channelModel.getCode())) {
                            List<ChannelModel.ChannelListBean> channelList = channelModel.getChannelList();
                            if (channelList != null) {
                                channlModels.clear();
                                channlModels.addAll(channelList);
                                commonAdapter.notifyDataSetChanged();
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
                plv_channel_content.onRefreshComplete();
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)   {
            case AMEND:
                if(resultCode==RESULT_OK) {
                    doNetworkChannelList();
                }
                break;
        }
    }
    public void onPersonFinish(View view) {
        finish();
    }

    /**
     * 删除网络通道
     */
    private void doDeleteChannel(String oId) {
        try {
            RequestParams params = new RequestParams();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oId", oId);
            params.setBodyEntity(new StringEntity(jsonObject.toString()));
            http.send(HttpRequest.HttpMethod.GET, XTHttpUtil.delNetChannel, params, new MyRequestCallBack(mContext, true) {
                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    super.onSuccess(responseInfo);
                    if (!StringUtils.isEmpty(responseInfo.result.toString())) {
                        ChannelModel configChannelModel = GsonUtils.fromJson(responseInfo.result.toString(), ChannelModel.class);
                        if (configChannelModel != null) {
                            if ("0".equals(configChannelModel.getCode())) {
                                doNetworkChannelList();
                                Toast.makeText(mContext, R.string.del_channel_success, Toast.LENGTH_SHORT).show();
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
