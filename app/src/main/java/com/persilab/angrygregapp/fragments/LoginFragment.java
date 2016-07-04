package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.activity.MainActivity;
import com.persilab.angrygregapp.database.SnappyHelper;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.NetworkEvent;
import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.job.TokenUpdateJob;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;
import com.persilab.angrygregapp.util.TextUtils;
import com.snappydb.SnappydbException;
import net.vrallev.android.cat.Cat;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by 0shad on 17.06.2016.
 */
public class LoginFragment extends BaseFragment {

    private static final String PHONE = "phone";

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
        ButterKnife.bind(this, rootView);
        SnappyHelper helper = new SnappyHelper(getContext(), "login");
        try {
            String phone = helper.getString(PHONE);
            if (phone != null) {
                loginPhone.setText(phone);
            }
        } catch (SnappydbException e) {
            Cat.e("Unknown exception", e);
        } finally {
            helper.close();
        }

        if (TokenUpdateJob.getToken() != null && TokenUpdateJob.getUser() != null) {
            ((MainActivity) getActivity()).replaceFragment(UserListFragment.class);
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
            RestClient.serviceApi().accessToken(loginPhone.getText().toString(), loginPassword.getText().toString()).enqueue();
        }
    }

    @Subscribe
    public void onEvent(TokenUpdateEvent updateEvent) {
        if (updateEvent.status.equals(ResponseEvent.Status.SUCCESS)) {
            User user = new User();
            user.setPassword(loginPassword.getText().toString());
            user.setPhone(loginPhone.getText().toString());
            TokenUpdateJob.start(user, updateEvent.message);
            SnappyHelper helper = new SnappyHelper(getContext(), "login");
            try {
                 helper.storeString(PHONE, loginPhone.getText().toString());
            } catch (SnappydbException e) {
                Cat.e("Unknown exception", e);
            } finally {
                helper.close();
            }
            ((MainActivity) getActivity()).replaceFragment(UserListFragment.class);
        }
        if (updateEvent.status.equals(ResponseEvent.Status.FAILURE)) {
            loginMessage.setVisibility(View.VISIBLE);
        }
    }
}
