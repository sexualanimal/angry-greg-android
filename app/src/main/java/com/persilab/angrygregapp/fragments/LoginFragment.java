package com.persilab.angrygregapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.activity.MainActivity;
import com.persilab.angrygregapp.database.SnappyHelper;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.FragmentBuilder;
import com.persilab.angrygregapp.util.TextUtils;
import com.snappydb.SnappydbException;

import net.vrallev.android.cat.Cat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;

import static com.persilab.angrygregapp.domain.Constants.Net.RESET_TOKEN;

/**
 * Created by 0shad on 17.06.2016.
 */
public class LoginFragment extends BaseFragment {

    private static final String PHONE = "phone";
    private SharedPreferences prefs;

    @Bind(R.id.login_logo)
    ImageView loginLogo;
    @Bind(R.id.login_phone)
    EditText loginPhone;
    @Bind(R.id.login_password)
    EditText loginPassword;
    @Bind(R.id.login_continue)
    TextView loginContinue;
    @Bind(R.id.login_message)
    TextView loginMessage;
    @Bind(R.id.load_progress)
    protected ProgressBar progressBar;
    @Bind(R.id.loading_text)
    protected TextView loadingText;
    @Bind(R.id.progress_layout)
    protected RelativeLayout progress;


    private boolean acceptEvents = false;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        ((MainActivity) getActivity()).getSupportActionBar().show();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        bind(rootView);
        SnappyHelper helper = new SnappyHelper(getContext(), "login");
        try {
            String phone = helper.getString(PHONE);
            if (phone != null) {
                loginPhone.setText(phone);
                loginPassword.requestFocus();
            }
        } catch (SnappydbException e) {
            Cat.e("Unknown exception", e);
        } finally {
            helper.close();
        }
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        loadingText.setText(R.string.login_loading);
        progress.setVisibility(View.GONE);
        String storedRefreshToken = prefs.getString(RESET_TOKEN, "");
        if (storedRefreshToken.length() > 0) {
            progress.setVisibility(View.VISIBLE);
            acceptEvents = true;
            RestClient.serviceApi().refreshToken(storedRefreshToken).enqueue();
        }
        return rootView;
    }

    @OnClick(R.id.login_continue)
    public void onClick() {
        User user = new User();
        if (TextUtils.isEmpty(loginPhone.getText())) {
            loginPhone.setError(getString(R.string.login_phone_error));
        } else if (TextUtils.isEmpty(loginPassword.getText())) {
            loginPassword.setError(getString(R.string.login_password_error));
        } else {
            acceptEvents = true;
            RestClient.serviceApi().accessToken(loginPhone.getText().toString(), loginPassword.getText().toString()).enqueue();
            progress.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TokenUpdateEvent updateEvent) {
        progress.setVisibility(View.GONE);
        if (acceptEvents) {
            acceptEvents = false;
            if (updateEvent.status.equals(ResponseEvent.Status.SUCCESS)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(RESET_TOKEN, updateEvent.message.getRefreshToken());
                editor.commit();
                App.setActualToken(updateEvent.message);
                SnappyHelper helper = new SnappyHelper(getContext(), "login");
                try {
                    helper.storeString(PHONE, loginPhone.getText().toString());
                } catch (SnappydbException e) {
                    Cat.e("Unknown exception", e);
                } finally {
                    helper.close();
                }
                if (updateEvent.message.getAccount().getIs_admin()) {
                    getMainActivity().replaceFragment(UserListFragment.class);
                } else {
                    FragmentBuilder builder = new FragmentBuilder(getFragmentManager());
                    builder.putArg(Constants.ArgsName.USER, updateEvent.message.getAccount());
                    getMainActivity().replaceFragment(UserFragment.class, builder);
                }
            }
            if (updateEvent.status.equals(ResponseEvent.Status.FAILURE)) {
                loginMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
