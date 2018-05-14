package com.my51c.see51.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.common.AppData;
import com.my51c.see51.data.AccountInfo;
import com.my51c.see51.listener.OnRegisterSucessListener;
import com.my51c.see51.ui.MyDialogFragmentActivity.MyAlertDialogFragment.OnRegisterListener;
import com.synertone.netAssistant.R;
import com.xqe.method.DelEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment implements OnClickListener, OnRegisterSucessListener {
    private static final String TAG = "LoginFragment";
    private final int MAX_USER_NAME_LENGTH = 20;
    private final int MIN_USER_NAME_LENGTH = 3;
    private final int MAX_PASSWORD_LENGTH = 20;
    private final int MIN_PASSWORD_LENGTH = 6;
    Activity mActivity;
    OnLoginListener mLoginListener;
    private DelEditText field_name;
    private DelEditText field_pass;
    private CheckBox rememberMe;
    private CheckBox autoLogIn;
    private Button btnLogin;
    private LinearLayout btnRegister;
    private AccountInfo account;
    private ImageView showPassImg;
    private Boolean doAutoLogin = false; //
    private boolean isPassShow = false;
    private LinearLayout regertLayout;
    private ImageView pullDownBtn;
    private RelativeLayout topLayout;
    private DelEditText nameEdit, pswEdit, rePswEdit;
    private Button regestBtn;
    private ImageView showRegestPswImg;
    private boolean isRegestPswShow = false;
    private String regstUsername;
    private String regstPassword;
    private String regstRePsw;
    //public final static String LOGIN_FRA_ACTION = "login_fragment_action";  //snt-del
    //public final static String LOGIN_SUC_ACTION = "login_success_action";   //snt-del
    private OnRegisterListener mOnRegisterListener;
    private AppData appData;
    private Dialog erroDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG,"onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //Log.d(TAG,"onActivityCreated(Bundle savedInstanceState)");
        super.onActivityCreated(savedInstanceState);

        String userName = null;
        String password = null;

        SharedPreferences settings = getActivity().getSharedPreferences(
                "rememberred_user_info", 0);
        userName = settings.getString("rememberred_user_name", "aaa");    // username:
        // yhkj;
        // aaa
        password = settings.getString("rememberred_password", "111");    // password:
        // yhkj;
        // 111
        boolean remember = settings.getBoolean("is_rememberred", false);
        boolean autoLog = settings.getBoolean("is_auto_login", false);


//--snt-del begain

		/*
		if (remember)
		{
			field_name.setText(userName);
			field_pass.setText(password);
			rememberMe.setChecked(true);
		}
		if (autoLog)
		{
			autoLogIn.setChecked(true);
			if (!doAutoLogin)
			{
				btnLogin.performClick();// ����ǰ����ؼ�ע����½���Ͳ���Ҫ�Զ���¼
			}
		}
		*/
//--snt-del end
//--snt-modi begain
        field_name.setText("snt1073");
        field_pass.setText("111111");
//--snt-modi end

        btnLogin.performClick();// 如果是按返回键注销登陆，就不需要自动登录
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.d(TAG,"onCreateView()");
        View v = inflater.inflate(R.layout.login, container, false);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnRegister = (LinearLayout) v.findViewById(R.id.showRegstLayout);

        showPassImg = (ImageView) v.findViewById(R.id.showpass);
        field_name = (DelEditText) v.findViewById(R.id.login_edit_account);
        field_pass = (DelEditText) v.findViewById(R.id.login_edit_pwd);
        rememberMe = (CheckBox) v.findViewById(R.id.remember);
        autoLogIn = (CheckBox) v.findViewById(R.id.autolog);
        regertLayout = (LinearLayout) v.findViewById(R.id.regest_layout);
        pullDownBtn = (ImageView) v.findViewById(R.id.pullDownBtn);
        topLayout = (RelativeLayout) v.findViewById(R.id.topLayout);
        nameEdit = (DelEditText) v.findViewById(R.id.regstName);
        pswEdit = (DelEditText) v.findViewById(R.id.regstPsw);
        rePswEdit = (DelEditText) v.findViewById(R.id.rePsw);
        showRegestPswImg = (ImageView) v.findViewById(R.id.showRegstPsw);
        regestBtn = (Button) v.findViewById(R.id.regestBtn);


        field_name.setSelection(field_name.length());
        field_pass.setSelection(field_name.length());

        btnLogin.setOnClickListener(this);
        autoLogIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        showPassImg.setOnClickListener(this);
        topLayout.setOnClickListener(this);
        regestBtn.setOnClickListener(this);
        showRegestPswImg.setOnClickListener(this);
        ((MainActivity) getActivity()).setOnRegisterSucessListener(this);
        return v;
    }

    @Override
    public void onResume() {
        MainActivity.FromPlatAcy = false;
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        //Log.d(TAG,"onAttach(Activity activity)");
        super.onAttach(activity);
        try {
            mActivity = activity;
            mLoginListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "LoginActivity attach failed!");
        }
    }

    @Override
    public void onStop() {
        //Log.d(TAG,"onStop()");
        super.onStop();
		/*
		if (true)
		{//在这里判断帐号是否登录成功，如果登录成功，就记下用户名和密码.此处调试时用true.
			SharedPreferences settings = getActivity().getSharedPreferences(
					"rememberred_user_info", 0);
			SharedPreferences.Editor editor = settings.edit();
			if (autoLogIn.isChecked())
			{
				editor.putString("rememberred_user_name", field_name.getText()
						.toString().trim());
				editor.putString("rememberred_password", field_pass.getText()
						.toString().trim());
				editor.putBoolean("is_auto_login", true);
				editor.putBoolean("is_rememberred", true);
			} else
			{
				editor.putBoolean("is_auto_login", false);
				if (rememberMe.isChecked())
				{
					editor.putString("rememberred_user_name", field_name
							.getText().toString().trim());
					editor.putString("rememberred_password", field_pass
							.getText().toString().trim());
					editor.putBoolean("is_rememberred", true);
				} else
				{
					editor.putBoolean("is_rememberred", false);
				}
			}
			editor.commit();
		}
		*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mActivity = null;
        //	unReceiver();
        //	unReceiverLogin();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.showpass:
                if (isPassShow) {
                    showPassImg.setImageResource(R.drawable.show_pass_shap_nor);
                    field_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    field_pass.setSelection(field_pass.length());
                    isPassShow = false;
                } else {
                    showPassImg.setImageResource(R.drawable.show_pass_shap_yes);
                    field_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    field_pass.setSelection(field_pass.length());
                    isPassShow = true;
                }

                break;

            case R.id.btnLogin:

                MainActivity.isLoginClicked = true;
                String userName = field_name.getText().toString().trim();
                String password = field_pass.getText().toString().trim();
                boolean isInputOK = userName.length() > 0 && password.length() > 0;
                if (isInputOK) {
                    ConnectivityManager cm =
                            (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if (isConnected) {
                        account = new AccountInfo(userName, password);
                        doAutoLogin = true;
                        //	setLoginReceiver();  //snt-del

                        mLoginListener.onLoginClicked(account);
                    } else {
                        toastMessage(R.string.timeout);
                    }

                } else {
                    toastMessage(R.string.inputIncomplete);
                }

                break;
            case R.id.showRegstLayout:

//			FragmentTransaction ft = getFragmentManager().beginTransaction();
//			MyAlertDialogFragment registerDialog = MyAlertDialogFragment.newInstance(R.string.register);
//			ft.add(registerDialog, "RegisterDialog");
//			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			ft.addToBackStack(null);
//			ft.commit();
                appData = (AppData) getActivity().getApplication();
                mOnRegisterListener = (OnRegisterListener) getActivity();
                ShowLayout(regertLayout, R.anim.buttom_to_top, true);

                break;
            case R.id.topLayout:
                ShowLayout(regertLayout, R.anim.top_to_buttom, false);
                break;
            case R.id.regestBtn:

                ConnectivityManager cm =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    String message = verifyInput();
                    if (message == null) {
                        //setReceiver(); //snt-del

                        AccountInfo accountInfo = new AccountInfo(regstUsername, regstPassword);
                        mOnRegisterListener.onRegisterClicked(accountInfo);
                    } else {
                        showErroDialog(message);
                    }
                } else {
                    toastMessage(R.string.timeout);
                }
                break;
            case R.id.erro_ok:
                erroDialog.dismiss();
                break;
            case R.id.showRegstPsw:
                if (isRegestPswShow) {
                    showRegestPswImg.setImageResource(R.drawable.show_pass_shap_nor);
                    pswEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    rePswEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pswEdit.setSelection(pswEdit.length());
                    rePswEdit.setSelection(rePswEdit.length());
                    isRegestPswShow = false;
                } else {
                    showRegestPswImg.setImageResource(R.drawable.show_pass_shap_yes);
                    pswEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    rePswEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    pswEdit.setSelection(pswEdit.length());
                    rePswEdit.setSelection(rePswEdit.length());
                    isRegestPswShow = true;
                }

                break;

            default:
                break;
        }
    }

    public void showErroDialog(String s) {
        erroDialog = new Dialog(getActivity(), R.style.Erro_Dialog);
        erroDialog.setContentView(R.layout.regst_erro_dialog);
        erroDialog.setTitle(null);
        TextView erroInfo = (TextView) erroDialog.findViewById(R.id.erroTx);
        Button okBtn = (Button) erroDialog.findViewById(R.id.erro_ok);
        erroInfo.setText(s);
        okBtn.setOnClickListener(this);
        erroDialog.show();
    }

    public void ShowLayout(View v, int id, boolean isVisible) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), id);
        v.setAnimation(anim);
        anim.start();
        if (isVisible) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    private void toastMessage(int resId) {
        Toast toast;
        toast = Toast.makeText(mActivity, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onRegisterSucess() {
        // TODO Auto-generated method stub
        field_name.setText(MainActivity.registerAccountInfo.getUsername());
        field_pass.setText(MainActivity.registerAccountInfo.getPassword());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    private String verifyInput() {
        StringBuffer stringBuffer = new StringBuffer();
        regstUsername = nameEdit.getText().toString().trim();
        regstPassword = pswEdit.getText().toString().trim();
        regstRePsw = rePswEdit.getText().toString().trim();

        Boolean isValidUsernameLength = regstUsername.length() >= MIN_USER_NAME_LENGTH && regstUsername.length() <= MAX_USER_NAME_LENGTH;
        Boolean isValidPasswordLength = regstPassword.length() >= MIN_PASSWORD_LENGTH && regstPassword.length() <= MAX_PASSWORD_LENGTH;
        Boolean isValidPasswordAgainLength = regstRePsw.length() >= MIN_PASSWORD_LENGTH && regstRePsw.length() <= MAX_PASSWORD_LENGTH;
        Boolean confirmPassword = regstPassword.equals(regstRePsw);
        if (!isValidUsernameLength) {
            stringBuffer.append(getString(R.string.userNameLenRequire)).append("\n\n");
        }
        if (!isValidPasswordLength || !isValidPasswordAgainLength) {
            stringBuffer.append(getString(R.string.passwordLenRequire)).append("\n\n");
        }
        if (!confirmPassword) {
            stringBuffer.append(getString(R.string.passwordinmatch)).append("\n");
        }

        String emailcheck = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern emailregex = Pattern.compile(emailcheck);

        String namecheck = "^[a-zA-Z0-9_]+$";
        Pattern nameregex = Pattern.compile(namecheck);

        Matcher useremailmatcher = emailregex.matcher(regstUsername);
        Matcher usernamematcher = nameregex.matcher(regstUsername);

        boolean isUseremailMatched = useremailmatcher.matches();
        boolean isUsernameMatched = usernamematcher.matches();


        if (isValidPasswordAgainLength && isValidPasswordLength && isValidUsernameLength && confirmPassword) {

            if (!isUseremailMatched && !isUsernameMatched) {
                stringBuffer.append(getString(R.string.userNameCharRequire)).append("\n");
            } else {
                return null;
            }
        }

        return stringBuffer.toString();
    }

    private void setRememberAccount() {
        if (true) {// 在这里判断帐号是否登录成功，如果登录成功，就记下用户名和密码.此处调试时用true.
            if (getActivity() == null)
                return;
            SharedPreferences settings = getActivity().getSharedPreferences(
                    "rememberred_user_info", 0);
            SharedPreferences.Editor editor = settings.edit();
            if (autoLogIn.isChecked()) {
                editor.putString("rememberred_user_name", field_name.getText()
                        .toString().trim());
                editor.putString("rememberred_password", field_pass.getText()
                        .toString().trim());
                editor.putBoolean("is_auto_login", true);
                editor.putBoolean("is_rememberred", true);
            } else {
                editor.putBoolean("is_auto_login", false);
                if (rememberMe.isChecked()) {
                    editor.putString("rememberred_user_name", field_name
                            .getText().toString().trim());
                    editor.putString("rememberred_password", field_pass
                            .getText().toString().trim());
                    editor.putBoolean("is_rememberred", true);
                } else {
                    editor.putBoolean("is_rememberred", false);
                }
            }
            editor.commit();
        }
    }

//---snt-del begain	
	/*
	private class RegstReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			ShowLayout(regertLayout, R.anim.top_to_buttom, false);
			getActivity().unregisterReceiver(this);
		}
	}
	private void setReceiver(){
		IntentFilter filter = new IntentFilter(LOGIN_FRA_ACTION);
		RegstReceiver receiver = new RegstReceiver();
		getActivity().registerReceiver(receiver, filter);
	}
	*/
	
	/*private class LoginReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			setRememberAccount();
			if(getActivity() != null)
			getActivity().unregisterReceiver(this);
		}
	}*/
	
	/*private void setLoginReceiver(){
		IntentFilter filter = new IntentFilter(LOGIN_SUC_ACTION);
		LoginReceiver receiver = new LoginReceiver();
		getActivity().registerReceiver(receiver, filter);
	}*/
//---snt-del end


    public interface OnLoginListener {
        void onLoginClicked(AccountInfo account);
    }

}
