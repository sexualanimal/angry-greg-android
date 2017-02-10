package com.persilab.angrygregapp.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.event.LoadedRefreshTokenEvent;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.FragmentBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.persilab.angrygregapp.domain.Constants.Net.RESET_TOKEN;

public class LogoFragment extends BaseFragment {

    private static final String PHONE = "phone";
    private SharedPreferences prefs;
    Runnable replaceFragmentRunnable;
    Runnable choiceRunnable;
    Runnable sendRunnable;
    Handler handler = new Handler();
    String storedRefreshToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logo, container, false);
        bind(rootView);
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        storedRefreshToken = prefs.getString(RESET_TOKEN, "");
        sendRunnable = new Runnable() {
            @Override
            public void run() {
                if (!storedRefreshToken.isEmpty()) {
                    RestClient.serviceApi().refreshToken(storedRefreshToken).enqueue();
                }
            }
        };
        choiceRunnable = new Runnable() {
            @Override
            public void run() {
                if (replaceFragmentRunnable != null) {
                    replaceFragmentRunnable.run();
                } else {
                    getMainActivity().replaceFragment(LoginFragment.class);
                }
            }
        };
        handler.postDelayed(sendRunnable, 1000);
        handler.postDelayed(choiceRunnable, 5000);
        return rootView;
    }

    @Subscribe
    public void onEvent(LoadedRefreshTokenEvent event) {
        replaceFragmentRunnable = new Runnable() {
            @Override
            public void run() {
                App.setActualToken(event.token);
                if (event.token.getAccount().getIs_admin()) {
                    getMainActivity().replaceFragment(UserListFragment.class);
                } else {
                    FragmentBuilder builder = new FragmentBuilder(getFragmentManager());
                    builder.putArg(Constants.ArgsName.USER, event.token.getAccount());
                    getMainActivity().replaceFragment(UserFragment.class, builder);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
