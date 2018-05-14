package com.my51c.see51.app.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.my51c.see51.app.domian.CoreModuleDiskPart;
import com.my51c.see51.app.http.XTHttpUtil;
import com.my51c.see51.app.routbw.FirewallInfoActivity;
import com.my51c.see51.app.routbw.SystemInfoActivity;
import com.my51c.see51.app.utils.XTHttpJSON;
import com.synertone.netAssistant.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//核心控制模块工作状态
@SuppressLint("ValidFragment")
public class CoreModuleFragment extends BaseFragment implements OnClickListener {
    protected static final String TAG = "CoreModuleFragment";
    int diskNum;
    private TextView mCoreVer, mCoreWork, mCoreDiskNum, mCoreStorage,
            mCoreDisPart, mFireWallTv, mSysDailyTv;
    private LinearLayout mCoreFireWall, mCoreSysDaily, mCoreStaticRoute,
            mCoreNeicun, mCoreYingpan, mCoreNeiCunLL, mCoreYingPanLL;
    private RequestQueue mRequestQueue;
    // private LinearLayout
    private ListView mStaticroutLv;
    private List<CoreModuleDiskPart> mDiskParts;
    private CoreModuleDiskPart mCDiskPart;
    private View view;
    private String coreUrl;
    private TextView mMenTotal, mMenUsed, mMenFree, mMenShared, mMenBuffer;
    private TextView mBufferTotal, mBufferUsed, mBufferFree, mBufferShared,
            mBufferBuffers;
    private TextView mSwapTotal, mSwapUsed, mSwapFree, mSwapShared,
            mSwapBuffer;
    private TextView mDiskPart01, mDiskPartTotal01, mDiskPartLeft01,
            mDiskPartFileSys01;
    private TextView mDiskPart02, mDiskPartTotal02, mDiskPartLeft02,
            mDiskPartFileSys02;
    private TextView mDiskPart03, mDiskPartTotal03, mDiskPartLeft03,
            mDiskPartFileSys03;
    private boolean isPrepared;
    private String memTotal, menUsed, menFree, menShared, menBuffers,
            menCached;
    private String bufferTotal, bufferUsed, bufferFree, bufferShared,
            bufferBuffers, bufferCached;
    private String swapTotal, swapUsed, swapFree, swapShared, swapBuffers,
            swapCached;
    private String disStatus, corestatusVer;

	/*
     * //一进页面就查询 private void devstatuCore(){ progresshow = true; showDia();
	 * JsonObjectRequest request = new JsonObjectRequest(Method.GET, coreUrl,
	 * null, new Listener<JSONObject>() {
	 * 
	 * @Override public void onResponse(JSONObject response) {
	 * //pdDismiss(response); Log.i(TAG, "接收回来的数据===》" + response.toString());
	 * 
	 * loadData(response);// 加载数据 } }, new ErrorListener() {
	 * 
	 * @Override public void onErrorResponse(VolleyError error) { Log.i(TAG,
	 * error.toString()); if (pd.isShowing()) { pd.dismiss(); } } });
	 * mRequestQueue.add(request); }
	 */
    private String coreStorageText;
    private Intent intent;
    private ProgressDialog pd;
    private boolean progresshow;
    public CoreModuleFragment(Bundle bundle) {

    }
    @SuppressLint("ValidFragment")
    public CoreModuleFragment(String coreUrl) {
        this.coreUrl = coreUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.device_start_coremodule_fragment,
                container, false);
        mRequestQueue = Volley.newRequestQueue(CoreModuleFragment.this.getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(view);
        isPrepared = true;
        lazyLoad();
        //devstatuCore();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        } else {
            CoreModuleQuery();
        }
    }

    private void initView(View view) {
        mMenTotal = (TextView) view.findViewById(R.id.men_total);
        mMenUsed = (TextView) view.findViewById(R.id.men_used);
        mMenFree = (TextView) view.findViewById(R.id.men_free);
        mMenShared = (TextView) view.findViewById(R.id.men_shared);
        mMenBuffer = (TextView) view.findViewById(R.id.men_buffer);

        mBufferTotal = (TextView) view.findViewById(R.id.buffer_total);
        mBufferUsed = (TextView) view.findViewById(R.id.buffer_used);
        mBufferFree = (TextView) view.findViewById(R.id.buffer_free);
        mBufferShared = (TextView) view.findViewById(R.id.buffer_shared);
        mBufferBuffers = (TextView) view.findViewById(R.id.buffer_buffers);

        mSwapTotal = (TextView) view.findViewById(R.id.swap_total);
        mSwapUsed = (TextView) view.findViewById(R.id.swap_used);
        mSwapFree = (TextView) view.findViewById(R.id.swap_free);
        mSwapShared = (TextView) view.findViewById(R.id.swap_shared);
        mSwapBuffer = (TextView) view.findViewById(R.id.swap_buffer);

        mDiskPart01 = (TextView) view.findViewById(R.id.diskpart_01);
        mDiskPart02 = (TextView) view.findViewById(R.id.diskpart_02);
        mDiskPart03 = (TextView) view.findViewById(R.id.diskpart_03);

        mDiskPartTotal01 = (TextView) view.findViewById(R.id.diskpart_total01);
        mDiskPartTotal02 = (TextView) view.findViewById(R.id.diskpart_total02);
        mDiskPartTotal03 = (TextView) view.findViewById(R.id.diskpart_total03);

        mDiskPartLeft01 = (TextView) view.findViewById(R.id.diskpart_left01);
        mDiskPartLeft02 = (TextView) view.findViewById(R.id.diskpart_left02);
        mDiskPartLeft03 = (TextView) view.findViewById(R.id.diskpart_left03);

        mDiskPartFileSys01 = (TextView) view
                .findViewById(R.id.diskpart_filesys01);
        mDiskPartFileSys02 = (TextView) view.findViewById(R.id.diskpart_free02);
        mDiskPartFileSys03 = (TextView) view.findViewById(R.id.diskpart_free03);

        mCoreVer = (TextView) view.findViewById(R.id.device_corestatus_ver);
        mCoreStorage = (TextView) view
                .findViewById(R.id.device_corestatus_storage);
        mCoreWork = (TextView) view.findViewById(R.id.device_corestatus_work);
        mCoreDiskNum = (TextView) view
                .findViewById(R.id.device_corestatus_disknum);
        mCoreDisPart = (TextView) view
                .findViewById(R.id.device_corestatus_diskpart);

        mStaticroutLv = (ListView) view
                .findViewById(R.id.device_corestatus_staticroutlv);
        mStaticroutLv.setDivider(null); // 去除item中的分割线
        mFireWallTv = (TextView) view
                .findViewById(R.id.device_corestatus_firewalltv);

        mSysDailyTv = (TextView) view
                .findViewById(R.id.device_corestatus_sysdailytv);

        // mStaticRoutTv = (TextView) view
        // .findViewById(R.id.device_corestatus_staticrouttv);
        mCoreNeicun = (LinearLayout) view.findViewById(R.id.neicunxinxi);
        mCoreYingpan = (LinearLayout) view.findViewById(R.id.yingpanzhuangtai);
        mCoreFireWall = (LinearLayout) view
                .findViewById(R.id.device_corestatus_firewall);
        mCoreSysDaily = (LinearLayout) view
                .findViewById(R.id.device_corestatus_sysdaily);
        mCoreStaticRoute = (LinearLayout) view
                .findViewById(R.id.device_corestatus_staticroute);
        mCoreNeiCunLL = (LinearLayout) view
                .findViewById(R.id.device_corestatus_staticneicun);
        mCoreYingPanLL = (LinearLayout) view
                .findViewById(R.id.device_corestatus_staticyingpan);
        mCoreFireWall.setOnClickListener(this);
        mCoreSysDaily.setOnClickListener(this);
        mCoreStaticRoute.setOnClickListener(this);
        // mCoreNeicun.setOnClickListener(this);
        mCoreNeiCunLL.setOnClickListener(this);
        mCoreYingPanLL.setOnClickListener(this);

    }

    private void CoreModuleQuery() {
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_DEVSTATU_CORESTATUS_ONE, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!isPrepared || !isVisible){
                            return;
                        }
                        Log.i(TAG, "接收到数据-->" + response.toString());
                        pdDismiss(response);
                        coreModuleloadData(response);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!isPrepared || !isVisible){
                    return;
                }
                if(CoreModuleFragment.this.getActivity()!=null){
                    Toast.makeText(CoreModuleFragment.this.getActivity(), "连接网元服务器失败", 0).show();
                }

                pd.dismiss();
            }
        });
        mRequestQueue.add(request);
    }

    public void coreModuleloadData(JSONObject response) {
        //if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
        Log.i("=======================", response.toString());
        try {
            corestatusVer = response.getString("ver");// 版本号

            JSONObject menObject = response.getJSONObject("mem");
            memTotal = menObject.getString("total");
            menUsed = menObject.getString("used");
            menFree = menObject.getString("free");
            menShared = menObject.getString("shared");
            menBuffers = menObject.getString("buffers");
            menCached = menObject.getString("cached");

            JSONObject bufferObject = response.getJSONObject("buffer");
            bufferTotal = bufferObject.getString("total");
            bufferUsed = bufferObject.getString("used");
            bufferFree = bufferObject.getString("free");
            bufferShared = bufferObject.getString("shared");
            bufferBuffers = bufferObject.getString("buffers");
            bufferCached = bufferObject.getString("cached");

            JSONObject swapObject = response.getJSONObject("swap");
            swapTotal = swapObject.getString("total");
            swapUsed = swapObject.getString("used");
            swapFree = swapObject.getString("free");
            swapShared = swapObject.getString("shared");
            swapBuffers = swapObject.getString("buffers");
            swapCached = swapObject.getString("cached");

            // 内存状态
            // String
            // coreStorage="\t\t\t\t\t\t"+"total"+"used"+"free"+"shared"+"buffers";

            String menStr = "Men：" + "\t\t\t" + memTotal + "\t\t\t\t" + menUsed
                    + "\t\t\t\t" + menFree + "\t\t\t\t" + menShared
                    + "\t\t\t\t" + menBuffers + "\t\t\t\t" + menCached;

            String bufferStr = "Buffer：" + "\t\t\t" + bufferTotal + "\t\t\t\t"
                    + bufferUsed + "\t\t\t\t" + bufferFree + "\t\t\t\t"
                    + bufferShared + "\t\t\t\t" + bufferBuffers + "\t\t\t\t"
                    + bufferCached;

            String swapStr = "Swap：" + "\t\t\t" + swapTotal + "\t\t\t\t"
                    + swapUsed + "\t\t\t\t" + swapFree + "\t\t\t\t"
                    + swapShared + "\t\t\t\t" + swapBuffers + "\t\t\t\t"
                    + swapCached;

            coreStorageText = menStr + "\n" + bufferStr + "\n" + swapStr;// 内存状态文本

            Log.i("=================", coreStorageText);

            disStatus = response.getString("diskstatus");// 硬盘工作状态
            diskNum = response.getInt("disknum");// 硬盘个数

            if (diskNum > 0 && mDiskParts == null) {
                mDiskParts = new ArrayList<CoreModuleDiskPart>();
            }

            for (int i = 1; i <= diskNum; i++) {
                JSONObject diskpart = response.getJSONObject("diskpart" + i);
                mCDiskPart = new CoreModuleDiskPart();

                String disTotal = diskpart.getString("total");
                String disLeft = diskpart.getString("left");
                String disFilesys = diskpart.getString("filesys");

                mCDiskPart.setDiskPartName("diskpart" + i);
                mCDiskPart.setTotalDiskPart(disTotal);
                mCDiskPart.setLeftDiskPart(disLeft);
                mCDiskPart.setFilesysDiskPart(disFilesys);

                mDiskParts.add(mCDiskPart);
            }
            upDateUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
		/* }
		 else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")){
			 Toast.makeText(CoreModuleFragment.this.getActivity(), "查询失败，请重新尝试！",
						Toast.LENGTH_SHORT).show();
		 }*/
    }

    private void upDateUi() {
		/*
		 * getActivity().runOnUiThread(new Runnable() {
		 *
		 * @Override public void run() {
		 */
        mCoreVer.setText(corestatusVer);
        mCoreStorage.setText(coreStorageText);
        mMenTotal.setText(memTotal);
        mMenUsed.setText(menUsed);
        mMenFree.setText(menFree);
        mMenShared.setText(menShared);
        mMenBuffer.setText(menBuffers);

        mBufferTotal.setText(bufferTotal);
        mBufferUsed.setText(bufferUsed);
        mBufferFree.setText(bufferFree);
        mBufferShared.setText(bufferShared);
        mBufferBuffers.setText(bufferBuffers);

        mSwapTotal.setText(swapTotal);
        mSwapUsed.setText(swapUsed);
        mSwapFree.setText(swapFree);
        mSwapShared.setText(swapShared);
        mSwapBuffer.setText(swapBuffers);

        if (disStatus.equals("0")) {
            mCoreWork.setText("良好");
        } else if (disStatus.equals("1")) {
            mCoreWork.setText("故障");
        }
        mCoreDiskNum.setText("" + diskNum);

        for (int i = 0; i < diskNum; i++) {

            if (i == 0) {
                CoreModuleDiskPart diskPart = mDiskParts.get(i);
                mDiskPart01.setText(diskPart.getDiskPartName());
                mDiskPartTotal01.setText(diskPart.getTotalDiskPart());
                mDiskPartLeft01.setText(diskPart.getLeftDiskPart());
                mDiskPartFileSys01.setText(diskPart.getFilesysDiskPart());
            }
            if (i == 1) {
                CoreModuleDiskPart diskPart = mDiskParts.get(i);
                mDiskPart02.setText(diskPart.getDiskPartName());
                mDiskPartTotal02.setText(diskPart.getTotalDiskPart());
                mDiskPartLeft02.setText(diskPart.getLeftDiskPart());
                mDiskPartFileSys02.setText(diskPart.getFilesysDiskPart());
            }
            if (i == 2) {
                CoreModuleDiskPart diskPart = mDiskParts.get(i);
                mDiskPart03.setText(diskPart.getDiskPartName());
                mDiskPartTotal03.setText(diskPart.getTotalDiskPart());
                mDiskPartLeft03.setText(diskPart.getLeftDiskPart());
                mDiskPartFileSys03.setText(diskPart.getFilesysDiskPart());
            }
            mCoreDisPart.setText("");
        }
    }

    // private boolean isShow;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.device_corestatus_firewall:

                intent = new Intent(CoreModuleFragment.this.getActivity(),
                        FirewallInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.device_corestatus_sysdaily:

                intent = new Intent(CoreModuleFragment.this.getActivity(),
                        SystemInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.device_corestatus_staticroute:
                if (mStaticroutLv.getVisibility() == 0) {

                    mStaticroutLv.setVisibility(View.GONE);
                } else {

                    mStaticroutLv.setVisibility(View.VISIBLE);
                    devstatuRout();
                }
                break;
            case R.id.device_corestatus_staticneicun:
                if (mCoreNeicun.getVisibility() == 0) {

                    mCoreNeicun.setVisibility(View.GONE);
                } else {

                    mCoreNeicun.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.device_corestatus_staticyingpan:
                if (mCoreYingpan.getVisibility() == 0) {
                    mCoreYingpan.setVisibility(View.GONE);
                } else {
                    mCoreYingpan.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    // 查看路由列表
    private void devstatuRout() {
        RequestQueue mRequestQueue = Volley
                .newRequestQueue(CoreModuleFragment.this.getActivity());
        progresshow = true;
        showDia();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                XTHttpUtil.GET_DEVSTATU_CORESTATUS_ROUTLIST, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdDismiss(response);
                        Log.i(TAG, "接收回来的数据===》" + response.toString());
                        loadDataRout(response);// 加载数据
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
        mRequestQueue.add(request);
    }

    private void loadDataRout(JSONObject response) {
        if (XTHttpJSON.getJSONString(response.toString()).equals("0")) {
            try {
                int firNum = response.getInt("num");
                List<String> Routlist = new ArrayList<String>();
                for (int i = 1; i <= firNum; i++) {
                    String firMsg = response.getString("msg" + i);
                    Routlist.add(firMsg);
                }
                // 设置适配
                mStaticroutLv.setAdapter(new ArrayAdapter<>(
                        CoreModuleFragment.this.getActivity(),
                        R.layout.coremodule_simple_list, Routlist));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (XTHttpJSON.getJSONString(response.toString()).equals("-1")) {
        }
    }

    private void showDia() {
        pd = new ProgressDialog(getActivity());
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

}
