package ank.com.secretchatlab.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.NetworkException;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.xaccount.XabberAccount;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.activity.AccountAddActivity;
import ank.com.secretchatlab.ui.activity.AccountActivity;
import ank.com.secretchatlab.ui.activity.ContactListActivity;
import ank.com.secretchatlab.ui.dialog.OrbotInstallerDialog;
import ank.com.secretchatlab.ui.helper.OrbotHelper;

public class AccountAddFragment extends Fragment implements View.OnClickListener {

    private CheckBox storePasswordView;
    private CheckBox chkSync;
    private CheckBox useOrbotView;
    private CheckBox createAccountCheckBox;
    private LinearLayout passwordConfirmView;
    private EditText userView;
    private EditText passwordView;
    private EditText passwordConfirmEditText;

    public static AccountAddFragment newInstance() {
        return new AccountAddFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_add, container, false);

        storePasswordView = (CheckBox) view.findViewById(R.id.store_password);
        chkSync = (CheckBox) view.findViewById(R.id.chkSync);
        if (XabberAccountManager.getInstance().getAccount() == null) {
            chkSync.setVisibility(View.GONE);
            chkSync.setChecked(false);
        }

        useOrbotView = (CheckBox) view.findViewById(R.id.use_orbot);
        createAccountCheckBox = (CheckBox) view.findViewById(R.id.register_account);
        createAccountCheckBox.setOnClickListener(this);

        userView = (EditText) view.findViewById(R.id.account_user_name);
        passwordView = (EditText) view.findViewById(R.id.account_password);
        passwordConfirmEditText = (EditText) view.findViewById(R.id.confirm_password);

        passwordConfirmView = (LinearLayout) view.findViewById(R.id.confirm_password_layout);

        ((TextView) view.findViewById(R.id.account_help))
                .setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_account:
                if(createAccountCheckBox.isChecked()) {
                    passwordConfirmView.setVisibility(View.VISIBLE);
                } else {
                    passwordConfirmView.setVisibility(View.GONE);
                }
            default:
                break;
        }
    }

    public void addAccount() {
        if (useOrbotView.isChecked() && !OrbotHelper.isOrbotInstalled()) {
            OrbotInstallerDialog.newInstance().show(getFragmentManager(), OrbotInstallerDialog.class.getName());
            return;
        }

        if (createAccountCheckBox.isChecked() &&
                !passwordView.getText().toString().contentEquals(passwordConfirmEditText.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.CONFIRM_PASSWORD), Toast.LENGTH_LONG).show();
            return;
        }

        AccountJid account;
        try {
            account = AccountManager.getInstance().addAccount(
                    userView.getText().toString().trim(),
                    passwordView.getText().toString(),
                    "",
                    false,
                    storePasswordView.isChecked(),
                    chkSync.isChecked(),
                    useOrbotView.isChecked(),
                    createAccountCheckBox.isChecked(), true);
        } catch (NetworkException e) {
            Application.getInstance().onError(e);
            return;
        }

        // update remote settings
        if (chkSync.isChecked()) XabberAccountManager.getInstance().updateSettingsWithSaveLastAccount(account);

        getActivity().setResult(Activity.RESULT_OK, AccountAddActivity.createAuthenticatorResult(account));
        startActivity(ContactListActivity.createIntent(getActivity()));
        //getActivity().finish();
    }
}
