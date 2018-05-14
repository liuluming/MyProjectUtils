package com.my51c.see51.guide;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.AccountInfo;
import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.service.GvapEvent;
import com.my51c.see51.service.GvapEvent.GvapEventListener;
import com.my51c.see51.ui.MainActivity;
import com.synertone.netAssistant.R;

import java.lang.ref.WeakReference;

public class Login extends SherlockFragmentActivity implements GvapEventListener {
    private final static String TAG = "Guide Login";
    boolean isRigister;
    private Button btnConfirm, btnRegisterUp, btnLogin, btnRegister;
    private ViewFlipper viewFlipLG;
    private LinearLayout linLayout;
    private LayoutInflater inflater;
    private button_OnClickListener btnListener = new button_OnClickListener();
    private Intent intent = new Intent();
    private EditText regAccount;
    private EditText regPassword;
    private EditText regPasswordAgain;
    private EditText loginAccount;
    private EditText loginPassword;
    private String strBindtype;
    private AppData appData;
    private AccountInfo account;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            GvapCommand cmd = (GvapCommand) msg.obj;
            switch (msg.what) {
                case 0:
                    handleSucess(cmd);
                    break;
                case 1:
                    handleFailed(cmd);
                    break;
                default:
                    break;
            }
        }

        @SuppressWarnings("deprecation")
        private void handleSucess(GvapCommand commandID) {

            switch (commandID) {
                case CMD_LOGIN:
                    appData.setAccountInfo(account);
                    removeDialog(0);
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    //intent.putExtra("BindStyle", strBindtype);
                    startActivity(intent);
                    break;
                case CMD_REGISTER:
                    removeDialog(1);
                    Toast.makeText(getApplicationContext(), getString(R.string.registersuccess), Toast.LENGTH_SHORT).show();
                    MainActivity.account = account;
                    appData.getGVAPService().logout();
                    appData.getGVAPService().login(account);
                    showDialog(0);
                    break;
                default:
                    break;
            }
        }

        @SuppressWarnings("deprecation")
        private void handleFailed(GvapCommand commandID) {
            // TODO Auto-generated method stub
            if (commandID == null) {
                return;
            }
            switch (commandID) {
                case CMD_LOGIN:
                    removeDialog(0);
                    Toast.makeText(getApplicationContext(), getString(R.string.guideAccountOrPwdError), Toast.LENGTH_LONG).show();
                    break;
                case CMD_REGISTER:
                    removeDialog(1);
                    Toast.makeText(getApplicationContext(), getString(R.string.registerfailed), Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guide_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable title_bg = getResources().getDrawable(R.drawable.title_bg);
        actionBar.setBackgroundDrawable(title_bg);
        isRigister = getIntent().getBooleanExtra("isRigister", false);
        inflater = LayoutInflater.from(this);
        findView();
        setInterface();
        setOnClickListener();
        strBindtype = getIntent().getStringExtra("BindStyle");
        appData = (AppData) getApplication();
        appData.addActivity(new WeakReference<Activity>(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
//		menu.add("exit").setIcon(R.drawable.x)
//				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
//		if (item.getTitle().equals("exit"))
//		{
//			appData.exit();
//			//Log.d(TAG, "Login exit$$$$$$$$$$$$$$$$$");
//		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        appData.getGVAPService().addGvapEventListener(this);
        //appData.getGVAPService().removeGvapEventListener(appData.getMainActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        appData.getGVAPService().removeGvapEventListener(this);
        //appData.getGVAPService().addGvapEventListener(appData.getMainActivity());
    }

    private void findView() {
        linLayout = (LinearLayout) findViewById(R.id.LayoutLogin);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnRegisterUp = (Button) findViewById(R.id.btnRegisterUp);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        viewFlipLG = (ViewFlipper) findViewById(R.id.viewFlipLG);

        regAccount = (EditText) findViewById(R.id.regAccount);
        regPassword = (EditText) findViewById(R.id.regPassword);
        regPasswordAgain = (EditText) findViewById(R.id.regPasswordAgain);
        loginAccount = (EditText) findViewById(R.id.loginAccount);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
    }

    private void setOnClickListener() {
        btnConfirm.setOnClickListener(btnListener);
        btnRegisterUp.setOnClickListener(btnListener);
        btnLogin.setOnClickListener(btnListener);
        btnRegister.setOnClickListener(btnListener);
    }

    private void setInterface() {
        if (!isRigister) {
            btnConfirm
                    .setBackgroundResource(R.drawable.btn_confirmation_style01);
            btnRegisterUp
                    .setBackgroundResource(R.drawable.btn_register_style02);
            viewFlipLG.showNext();
        } else {
            btnConfirm
                    .setBackgroundResource(R.drawable.btn_confirmation_style02);
            btnRegisterUp
                    .setBackgroundResource(R.drawable.btn_register_style01);
        }
    }

    String verifyLoginInput() {
        String name = loginAccount.getText().toString();
        String pwd = loginPassword.getText().toString();
        if (name != null && pwd != null) {
            account = new AccountInfo(name, pwd);
            MainActivity.account = account;
            appData.getGVAPService().logout();
            appData.getGVAPService().login(account);
            showDialog(0);
            return null;
        } else {
            return getString(R.string.userNameLenRequire) + " and " + getString(R.string.passwordLenRequire);
        }
    }

    String verifyRegistInput() {
        StringBuffer stringBuffer = new StringBuffer();
        String username = regAccount.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String passwordAgain = regPasswordAgain.getText().toString().trim();

        Boolean isValidUsernameLength = username.length() >= 6 && username.length() <= 20;
        Boolean isValidPasswordLength = password.length() >= 6 && password.length() <= 20;
        Boolean isValidPasswordAgainLength = passwordAgain.length() >= 6 && passwordAgain.length() <= 20;
        Boolean confirmPassword = password.equals(passwordAgain);
        if (!isValidUsernameLength) {
            stringBuffer.append(getString(R.string.userNameLenRequire)).append("\n");
        }

        if (!isValidPasswordLength || !isValidPasswordAgainLength) {
            stringBuffer.append(getString(R.string.passwordLenRequire)).append("\n");
        }
        if (!confirmPassword) {
            stringBuffer.append(getString(R.string.passwordinmatch)).append("\n");
        }

        Boolean isValidUsernameInput = true;
        isValidUsernameInput = username.matches("^[a-zA-Z][a-zA-Z0-9_@.-]{5,19}$");

        if (!isValidUsernameInput) {
            stringBuffer.append(getString(R.string.userNameCharRequire));
        }
        if (isValidPasswordAgainLength
                && isValidPasswordLength && isValidUsernameInput
                && isValidUsernameLength && confirmPassword) {


            appData.getGVAPService().removeGvapEventListener(this);
            appData.getGVAPService().startRegServer();
            appData.getGVAPService().addGvapEventListener(this);
            appData.getGVAPService().register(new AccountInfo(username, password));
            account = new AccountInfo(username, password);
            showDialog(1);
            return null;
        }
        return stringBuffer.toString();
    }

    @Override
    public void onGvapEvent(GvapEvent event) {
        // TODO Auto-generated method stub
        Message msg;
        switch (event) {
            case OPERATION_SUCCESS: {
                msg = mHandler.obtainMessage(0, event.getCommandID());
                mHandler.sendMessage(msg);
                break;
            }
            case OPERATION_TIMEOUT:
            case OPERATION_FAILED:
            case CONNECTION_RESET:
            case CONNECT_TIMEOUT:
            case CONNECT_FAILED:
            case NETWORK_ERROR: {
                //Log.e(TAG , "gvap error: "+event);
                msg = mHandler.obtainMessage(1, event.getCommandID());
                mHandler.sendMessage(msg);
                break;
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        ProgressDialog dialog = new ProgressDialog(this);
        switch (id) {
            case 0:
                dialog.setTitle(R.string.login);
                dialog.setMessage(getString(R.string.userlogining));
                dialog.setCancelable(true);
                break;
            case 1:
                dialog.setTitle(R.string.register);
                dialog.setMessage(getString(R.string.registerring));
                dialog.setCancelable(true);
            default:
                break;
        }

        return dialog;
    }

    class button_OnClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
//			case R.id.exitGuideLogin:
//				appData.exit();
//				break;

                case R.id.btnConfirm:
                    if (viewFlipLG.getDisplayedChild() == 0) {
                        viewFlipLG.showPrevious();
                        btnConfirm
                                .setBackgroundResource(R.drawable.btn_confirmation_style01);
                        btnRegisterUp
                                .setBackgroundResource(R.drawable.btn_register_style02);
                    }
                    break;
                case R.id.btnRegisterUp:
                    if (viewFlipLG.getDisplayedChild() == 1) {
                        viewFlipLG.showPrevious();
                        btnConfirm
                                .setBackgroundResource(R.drawable.btn_confirmation_style02);
                        btnRegisterUp
                                .setBackgroundResource(R.drawable.btn_register_style01);
                    }
                    break;
                case R.id.btnLogin:
                    String retval = verifyLoginInput();
                    if (retval != null) {
                        Toast.makeText(getApplicationContext(), retval, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnRegister:
                    String ret = verifyRegistInput();
                    if (ret != null) {
                        Toast.makeText(getApplicationContext(), ret, Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }

    }
}
