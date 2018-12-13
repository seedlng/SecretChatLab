package ank.com.secretchatlab.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.connection.NetworkManager;
import ank.com.secretchatlab.ui.activity.XabberAccountInfoActivity;
import ank.com.secretchatlab.ui.activity.XabberLoginActivity;

/**
 * Created by valery.miller on 25.07.17.
 */

public class XabberLoginFragment extends Fragment implements View.OnClickListener {

    private EditText edtLogin;
    private EditText edtPass;
    private Button btnLogin;
    private RelativeLayout rlForgotPass;
    private RelativeLayout rlSignUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.include_login_fields, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtLogin = (EditText) view.findViewById(R.id.edtLogin);
        edtPass = (EditText) view.findViewById(R.id.edtPass);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        rlForgotPass = (RelativeLayout) view.findViewById(R.id.rlForgotPass);
        //rlSignUp = (RelativeLayout) view.findViewById(R.id.rlSignUp);

        btnLogin.setOnClickListener(this);
        rlForgotPass.setOnClickListener(this);
        //rlSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                onLoginClick();
                break;
            case R.id.rlForgotPass:
                ((XabberLoginActivity)getActivity()).onForgotPassClick();
                break;
//            case R.id.rlSignUp:
//                Intent intent = XabberAccountInfoActivity.createIntent(getActivity());
//                intent.putExtra(XabberAccountInfoActivity.CALL_FROM, XabberAccountInfoActivity.CALL_FROM_LOGIN);
//                startActivity(intent);
//                break;
        }
    }

    private void onLoginClick() {
        // hide keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtPass.getWindowToken(), 0);

        String login = edtLogin.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        if (login.isEmpty()) {
            edtLogin.setError("empty field");
            return;
        }

        if (pass.isEmpty()) {
            edtPass.setError("empty field");
            return;
        }

        if (NetworkManager.isNetworkAvailable()) {
            ((XabberLoginActivity)getActivity()).login(login, pass);
        } else
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
    }
}
