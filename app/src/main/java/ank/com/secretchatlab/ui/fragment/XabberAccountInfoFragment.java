package ank.com.secretchatlab.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ank.com.secretchatlab.BuildConfig;
import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.SettingsManager;
import ank.com.secretchatlab.data.xaccount.AuthManager;
import ank.com.secretchatlab.data.xaccount.XMPPAccountSettings;
import ank.com.secretchatlab.data.xaccount.XabberAccount;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.activity.XabberAccountInfoActivity;
import ank.com.secretchatlab.ui.dialog.AccountSyncDialogFragment;
import ank.com.secretchatlab.utils.RetrofitErrorConverter;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by valery.miller on 27.07.17.
 */

public class XabberAccountInfoFragment extends Fragment {

    private static final String LOG_TAG = XabberAccountInfoFragment.class.getSimpleName();

    private TextView tvAccountName;
    private TextView tvAccountUsername;
    private TextView tvLanguage;
    private TextView tvPhone;
    private TextView tvLastSyncDate;
    private RelativeLayout rlLogout;
    private RelativeLayout rlSync;
    private boolean dialogShowed;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xabber_account_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPhone = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvAccountName = (TextView) view.findViewById(R.id.tvAccountName);
        tvAccountUsername = (TextView) view.findViewById(R.id.tvAccountUsername);
        tvLanguage = (TextView) view.findViewById(R.id.tvLanguage);
        tvLastSyncDate = (TextView) view.findViewById(R.id.tvLastSyncDate);

        rlLogout = (RelativeLayout) view.findViewById(R.id.rlLogout);
        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialogShowed) {
                    dialogShowed = true;
                    showLogoutDialog();
                }
            }
        });

        rlSync = (RelativeLayout) view.findViewById(R.id.rlSync);
        rlSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialogShowed) {
                    dialogShowed = true;
                    showSyncDialog(false);
                }
            }
        });

        if (getArguments().getBoolean("SHOW_SYNC", false))
            showSyncDialog(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        XabberAccount account = XabberAccountManager.getInstance().getAccount();
        if (account != null) updateData(account);
        else ((XabberAccountInfoActivity)getActivity()).showLoginFragment();
        updateLastSyncTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    public void updateData(@NonNull XabberAccount account) {
        String accountName = account.getFirstName() + " " + account.getLastName();
        if (accountName.trim().isEmpty())
            accountName = getString(R.string.title_xabber_account);

        tvAccountName.setText(accountName);
        if (account.getUsername() != null && !account.getUsername().isEmpty())
            tvAccountUsername.setText(account.getUsername());

        if (account.getLanguage() != null && !account.getLanguage().equals("")) {
            tvLanguage.setText(account.getLanguage());
            tvLanguage.setVisibility(View.VISIBLE);
        } else tvLanguage.setVisibility(View.GONE);

        if (BuildConfig.FLAVOR.equals("ru")) {
            tvPhone.setVisibility(View.VISIBLE);
            String phone = account.getPhone();
            tvPhone.setText(phone != null ? phone : getString(R.string.no_phone));
        } else tvPhone.setVisibility(View.GONE);
    }

    public void updateLastSyncTime() {
        tvLastSyncDate.setText(getString(R.string.last_sync_date, SettingsManager.getLastSyncDate()));
    }

    public void showSyncDialog(final boolean noCancel) {
        Subscription getSettingsSubscription = AuthManager.getClientSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<XMPPAccountSettings>>() {
                    @Override
                    public void call(List<XMPPAccountSettings> list) {
                        List<XMPPAccountSettings> items = XabberAccountManager.getInstance().createSyncList(list);

                        if (items != null && items.size() > 0) {
                            // save full list to list for sync
                            XabberAccountManager.getInstance().setXmppAccountsForSync(items);
                            // show dialog
                            AccountSyncDialogFragment.newInstance(noCancel)
                                    .show(getFragmentManager(), AccountSyncDialogFragment.class.getSimpleName());
                            dialogShowed = false;
                        } else Toast.makeText(getActivity(), "Не удалось начать синхронизацию", Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleErrorGetSettings(throwable);
                    }
                });
        compositeSubscription.add(getSettingsSubscription);
    }

    private void handleErrorGetSettings(Throwable throwable) {
        String message = RetrofitErrorConverter.throwableToHttpError(throwable);
        if (message != null) {
            if (message.equals("Invalid token")) {
                XabberAccountManager.getInstance().onInvalidToken();
                ((XabberAccountInfoActivity)getActivity()).showLoginFragment();
                Toast.makeText(getActivity(), "Аккаунт был удален", Toast.LENGTH_LONG).show();
            } else {
                Log.d(LOG_TAG, "Error while synchronization: " + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(LOG_TAG, "Error while synchronization: " + throwable.toString());
            Toast.makeText(getActivity(), "Error while synchronization: " + throwable.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showLogoutDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_logout_xabber_account, null);
        final CheckBox chbDeleteAccounts = (CheckBox) view.findViewById(R.id.chbDeleteAccounts);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.progress_title_quit)
                .setMessage(R.string.logout_summary)
                .setView(view)
                .setPositiveButton(R.string.button_quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((XabberAccountInfoActivity)getActivity()).onLogoutClick(chbDeleteAccounts.isChecked());
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        Dialog dialog = builder.create();
        dialog.show();
        dialogShowed = false;
    }
}
