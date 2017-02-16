package com.persilab.angrygregapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;
import com.whinc.widget.ratingbar.RatingBar;

import net.glxn.qrgen.android.QRCode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;

/**
 * Created by 0shad on 21.06.2016.
 */
public class UserFragment extends BaseFragment {


    public static UserFragment show(BaseFragment baseFragment, User user) {
        return show(baseFragment, UserFragment.class, Constants.ArgsName.USER, user);
    }

    @Bind(R.id.user_rating)
    RatingBar ratingBar;
    @Bind(R.id.user_card_qr)
    ImageView cardQr;
    @Bind(R.id.user_card_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    Token newToken;
    Handler handler;
    private User user;
    int width;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        handler = new Handler();
        bind(rootView);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.user);

        user = (User) getArguments().getSerializable(Constants.ArgsName.USER);
        if (user != null) {
            int offColor = Color.TRANSPARENT;
            int onColor = Color.BLACK;
            width = GuiUtils.getScreenSize(getContext()).y;
            cardQr.setImageBitmap(QRCode.from(String.valueOf(user.getId())).withCharset("UTF-8").withColor(onColor, offColor).withSize(width, width).bitmap());
            getActivity().setTitle(user.getName());
            initPoints();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RestClient.serviceApi().refreshToken(App.getActualToken().getRefreshToken()).enqueue();
            }
        });

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.exit, menu);
    }

    private void initPoints() {
        int left = ratingBar.getMaxCount() - user.getAmountOfPoints();
        ratingBar.setCount(user.getAmountOfPoints());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Subscribe
    public void onEvent(TokenUpdateEvent event) {
        newToken = event.message;
        if (newToken != null) {
            App.setActualToken(newToken);
            user = newToken.getAccount();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cardQr.setImageBitmap(QRCode.from(String.valueOf(user.getId())).withCharset("UTF-8").withColor(Color.BLACK, Color.TRANSPARENT).withSize(width, width).bitmap());
                    getActivity().setTitle(user.getName());
                    initPoints();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
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
