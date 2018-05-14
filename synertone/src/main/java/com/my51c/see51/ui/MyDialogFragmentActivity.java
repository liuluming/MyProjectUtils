package com.my51c.see51.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.AccountInfo;
import com.synertone.netAssistant.R;

public class MyDialogFragmentActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    static public class ErrorAlertDialogFragment extends SherlockDialogFragment {

        public static ErrorAlertDialogFragment newInstance(int title, String message) {
            ErrorAlertDialogFragment frag = new ErrorAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putString("message", message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            int title = getArguments().getInt("title");
            String message = getArguments().getString("message");
            AlertDialog ad = new AlertDialog.Builder(getActivity())
                    .setTitle(title).setMessage(message).setInverseBackgroundForced(true)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    })
                    .create();
            return ad;

        }
    }


    //register
    static public class MyAlertDialogFragment extends SherlockDialogFragment {

        private final int MAX_USER_NAME_LENGTH = 20;
        private final int MIN_USER_NAME_LENGTH = 3;

        private final int MAX_PASSWORD_LENGTH = 20;
        private final int MIN_PASSWORD_LENGTH = 6;

        private final int MAX_NICKNAME_LENGTH = 40;
        private final int MIN_NICKNAME_LENGTH = 0;

        private final int MSG_UPDATE = 0;
        private final int MSG_GVAP = 1;
        AlertDialog ad;
        private EditText usernameEditText;
        private EditText nicknameEditText;
        private EditText passwordEditText;
        private EditText passwordAgainEditText;
        private OnRegisterListener mOnRegisterListener;
        private String username;
        private String nickname;
        private String password;
        private String passwordAgain;
        private AppData appData;

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            appData = (AppData) activity.getApplication();
            mOnRegisterListener = (OnRegisterListener) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //super.onCreateDialog(savedInstanceState);
            LayoutInflater factory = LayoutInflater.from(this.getSherlockActivity().getApplicationContext());
            int title = getArguments().getInt("title");
            View view = factory.inflate(R.layout.layout_register, null);

            usernameEditText = (EditText) view.findViewById(R.id.edit_username);
            nicknameEditText = (EditText) view.findViewById(R.id.edit_nickname);
            passwordEditText = (EditText) view.findViewById(R.id.edit_password);
            passwordAgainEditText = (EditText) view.findViewById(R.id.edit_passwordagain);

            nicknameEditText.setVisibility(View.GONE);
            ad = new AlertDialog.Builder(getSherlockActivity())
                    .setTitle(title)
                    .setView(view)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String message = verifyInput();
                                    if (message == null) {
                                        AccountInfo accountInfo = new AccountInfo(username, password);
                                        mOnRegisterListener.onRegisterClicked(accountInfo);
                                        dialog.cancel();
                                    } else {
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        SherlockDialogFragment fm = ErrorAlertDialogFragment.newInstance(R.string.registerfailed, message);
                                        ft.add(fm, "errorAlertDialog");
                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                }
                            }
                    )
                    .setNegativeButton(R.string.back,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            }
                    )
                    .create();
            return ad;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return null;
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        private String verifyInput() {
            StringBuffer stringBuffer = new StringBuffer();
            username = usernameEditText.getText().toString().trim();
            nickname = nicknameEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();
            passwordAgain = passwordAgainEditText.getText().toString().trim();

            Boolean isValidUsernameLength = username.length() >= MIN_USER_NAME_LENGTH && username.length() <= MAX_USER_NAME_LENGTH;
            //Boolean isValidNicknameLength = nickname.length()>=MIN_NICKNAME_LENGTH && nickname.length()<=MAX_NICKNAME_LENGTH;
            Boolean isValidPasswordLength = password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
            Boolean isValidPasswordAgainLength = passwordAgain.length() >= MIN_PASSWORD_LENGTH && passwordAgain.length() <= MAX_PASSWORD_LENGTH;
            Boolean confirmPassword = password.equals(passwordAgain);
            if (!isValidUsernameLength) {
                stringBuffer.append(getString(R.string.userNameLenRequire)).append("\n");
            }
//        	if (!isValidNicknameLength){
//        		stringBuffer.append(getString(R.string.nicknameLenRequire)).append("\n");
//        	}
            if (!isValidPasswordLength || !isValidPasswordAgainLength) {
                stringBuffer.append(getString(R.string.passwordLenRequire)).append("\n");
            }
            if (!confirmPassword) {
                stringBuffer.append(getString(R.string.passwordinmatch)).append("\n");
            }

            //Boolean isValidUsernameInput = true;
            //isValidUsernameInput = username.matches("^[a-zA-Z][a-zA-Z0-9_@.-]{5,19}$");
            /*
    		if(!isValidUsernameInput){
    			stringBuffer.append(getString(R.string.userNameCharRequire));
    		}
    		*/
            if (/*isValidNicknameLength &&*/ isValidPasswordAgainLength
                    && isValidPasswordLength /*&& isValidUsernameInput*/
                    && isValidUsernameLength && confirmPassword) {
                return null;
            }
            return stringBuffer.toString();
        }

        public interface OnRegisterListener {
            void onRegisterClicked(AccountInfo account);
        }
    }
}