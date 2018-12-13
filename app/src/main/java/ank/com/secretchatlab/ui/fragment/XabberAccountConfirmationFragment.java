package ank.com.secretchatlab.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.xaccount.XabberAccount;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.activity.XabberAccountInfoActivity;

/**
 * Created by valery.miller on 27.07.17.
 */

public class XabberAccountConfirmationFragment extends Fragment {

    private TextView tvSignType;
    private TextView tvAccountName;
    //private RelativeLayout rlLogout;

    private EditText edtCode;
    private Button btnConfirm;
    private Button btnResend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xabber_account_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSignType = (TextView) view.findViewById(R.id.tvSignType);
        tvAccountName = (TextView) view.findViewById(R.id.tvAccountName);

//        rlLogout = (RelativeLayout) view.findViewById(R.id.rlLogout);
//        rlLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((XabberAccountInfoActivity)getActivity()).onLogoutClick();
//            }
//        });

        edtCode = (EditText) view.findViewById(R.id.edtCode);
        btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyFields();
            }
        });
        btnResend = (Button) view.findViewById(R.id.btnResend);
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((XabberAccountInfoActivity)getActivity()).onResendClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        XabberAccount account = XabberAccountManager.getInstance().getAccount();
        if (account != null) updateData(account);
        else ((XabberAccountInfoActivity)getActivity()).showLoginFragment();
    }

    private void verifyFields() {
        String code = edtCode.getText().toString().trim();

        if (code.isEmpty()) {
            edtCode.setError(getString(R.string.empty_field));
            return;
        }

        ((XabberAccountInfoActivity)getActivity()).onConfirmClick(code);
    }

    public void updateData(@NonNull XabberAccount account) {
        if (account.getEmails().size() > 0)
            tvAccountName.setText(account.getEmails().get(0).getEmail());
    }

}
