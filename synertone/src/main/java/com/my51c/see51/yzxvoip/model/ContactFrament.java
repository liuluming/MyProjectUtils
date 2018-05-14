package com.my51c.see51.yzxvoip.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my51c.see51.yzxvoip.AudioConverseActivity;
import com.my51c.see51.yzxvoip.ContactTools;
import com.my51c.see51.yzxvoip.MyListView;
import com.my51c.see51.yzxvoip.RestTools;
import com.my51c.see51.yzxvoip.UserInfoDBManager;
import com.synertone.netAssistant.R;
import com.yzxIM.data.db.ConversationInfo;
//import com.yzxtcp.tools.CustomLog;

public class ContactFrament extends Fragment {
    private static final String TAG = ContactFrament.class.getSimpleName();
    private MyListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (adapter != null) {
                        adapter.updateListView(ContactTools.getSourceDateList());
                    }
                    break;
            }
        }
    };
    private View mView;
    private String mLocalUser;
    private TextView group;
    private TextView chatroom;
    private TextView invite_tv;
    private AlertDialog.Builder invite_dialog;
    private Dialog base_dialog;
    private LinearLayout mChatRoomView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLocalUser = UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount();
        if (mLocalUser != null) {
            System.out.println("mLocalUser:" + mLocalUser);
        } else {
            System.err.println("��ȡ��½�˺�ʧ��");
        }
        mView = inflater.inflate(R.layout.fragment_contact, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (ContactTools.getSourceDateList().size() < 1) {
                    ContactTools.initContacts(getActivity());
                    mHandler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    public void initView() {
        sideBar = (SideBar) mView.findViewById(R.id.sidrbar);
        dialog = (TextView) mView.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {

                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position + 2);
                }

            }
        });

        adapter = new SortAdapter(getActivity(), ContactTools.getSourceDateList());
        sortListView = (MyListView) mView.findViewById(R.id.country_lvcountry);
        sortListView.setTopRefresh(false);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ConversationInfo info = new ConversationInfo();
                //CustomLog.e("position == " + position);
                if (position >= 2) {
                    String phone = ContactTools.getSourceDateList().get(position - 1).getId();
                    Intent intentVoice = new Intent(getActivity(), AudioConverseActivity.class);
                    intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intentVoice.putExtra("phoneNumber", phone);
                    intentVoice.putExtra("call_phone", phone);
                    intentVoice.putExtra("call_type", 2);//直拨电话
                    startActivity(intentVoice);
                }
            }
        });

        sortListView.setonRefreshListener(new MyListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ContactTools.initContacts(getActivity());
                        RestTools.queryGroupInfo(getActivity(), mLocalUser, null);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {

                        adapter.updateListView(ContactTools.getSourceDateList());
                        sortListView.onRefreshComplete();
                    }
                }.execute(null, null, null);
            }
        });

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /*mChatRoomView = (LinearLayout) inflater.inflate(R.layout.chatroom_item, null);
		group = (TextView) mChatRoomView.findViewById(R.id.group);
		group.setCompoundDrawablePadding(dp2px(getActivity(),10));
		group.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(), IMGroupListActivity.class));
			}
		});

		chatroom = (TextView) mChatRoomView.findViewById(R.id.chatroom);
		chatroom.setCompoundDrawablePadding(dp2px(getActivity(),10));
		chatroom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(), IMDiscussListActivity.class));
			}
		});

		invite_tv = (TextView) mChatRoomView.findViewById(R.id.input_numbers);
		invite_tv.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi") @Override
			public void onClick(View arg0) {
				base_dialog = new Dialog(getActivity(),R.style.basedialog);
				base_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				base_dialog.setContentView(R.layout.dialog_base);
				TextView tv= (TextView) base_dialog.findViewById(R.id.dialog_tv);
				tv.setText("��������Ҫ��ϵ���û��˺�");
				tv.setTextSize(16);
				final EditText et = (EditText) base_dialog.findViewById(R.id.dialog_et);
				et.setVisibility(View.VISIBLE);
				base_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						//�ڶԻ�����ʾ֮�󵯳����뷨
						InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputmanger.toggleSoftInput(0,
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				});
				base_dialog.show();
				base_dialog.findViewById(R.id.dialog_tv_cencel)
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								//���ؼ���
								InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
								inputmanger.hideSoftInputFromWindow(et.getWindowToken(), 0);
								base_dialog.dismiss();
							}
						});
				base_dialog.findViewById(R.id.dialog_tv_sure)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if(!"".equals(et.getText().toString().trim())){
									Intent intent = new Intent(getActivity(), IMMessageActivity.class);
									ConversationInfo info = new ConversationInfo();
									info.setTargetId(et.getText().toString().trim());
									info.setCategoryId(CategoryId.PERSONAL);
									info.setConversationTitle(info.getTargetId());
									intent.putExtra("conversation", info);
									startActivity(intent);
									//���ؼ���
									InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
									inputmanger.hideSoftInputFromWindow(et.getWindowToken(), 0);
									base_dialog.dismiss();
								}
							}
						});
			}
		});

		sortListView.addHeaderView(mChatRoomView);*/
        sortListView.setAdapter(adapter);
    }

    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public void dismissSideBarDialog() {
        if (null != sideBar) {
            sideBar.dimissDialog();
        }
    }

    public void resumeSortList() {
        if (sortListView != null) {
            sortListView.onRefreshComplete();
        } else {
//			Toast.makeText(getActivity(), "MyListView is null", 0).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

