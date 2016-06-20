package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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

/**
 * Created by 0shad on 17.06.2016.
 */
public class LoginFragment extends BaseFragment {

    @Bind(R.id.login_logo)
    ImageView loginLogo;
    @Bind(R.id.login_phone)
    EditText loginPhone;
    @Bind(R.id.login_password)
    EditText loginPassword;
    @Bind(R.id.login_continue)
    TextView loginContinue;


    public LoginFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        bind(rootView);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.login_continue)
    public void onClick() {
        ((MainActivity) getActivity()).replaceFragment(UserListFragment.class);
    }
}
