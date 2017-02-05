package com.persilab.angrygregapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.util.GuiUtils;
import com.persilab.angrygregapp.util.SystemUtils;
import com.whinc.widget.ratingbar.RatingBar;

import net.glxn.qrgen.android.QRCode;
import net.vrallev.android.cat.Cat;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    Handler handler;
    WebSocket webSocket;
    private User user;

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
            int width = GuiUtils.getScreenSize(getContext()).y;
            cardQr.setImageBitmap(QRCode.from(String.valueOf(user.getId())).withCharset("UTF-8").withColor(onColor, offColor).withSize(width, width).bitmap());
            getActivity().setTitle(user.getName());
            initPoints();
        }
        connect();
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

    // Start the connection. Should be called in a new thread
    public void connect() {
        WebSocketFactory factory = new WebSocketFactory();
        try {
            webSocket = factory.createSocket(Constants.Net.BASE_DOMAIN + "primus");
            webSocket.addListener(websocketAdapter);
            webSocket.setPingPayloadGenerator(() -> ("primus::ping::" + new Date().toString()).getBytes());
            webSocket.setPingInterval(10 * 1000);
            webSocket.connectAsynchronously();
        } catch (IOException e) {
            Cat.e("Unknown exception", e);
        }
    }

    @Override
    public void onDestroyView() {
        webSocket.disconnect();
        super.onDestroyView();
    }


    WebSocketAdapter websocketAdapter = new WebSocketAdapter() {

        @Override
        public void onTextMessage(WebSocket websocket, String message) throws Exception {
            User user = new Gson().fromJson(message, WebSockedResponse.class).data;
            if (user != null) {
                final User current = UserFragment.this.user;
                final int coffee = current.getAmountOfFreeCoffe();
                current.setAmountOfFreeCoffe(user.getAmountOfFreeCoffe());
                current.setAmountOfPoints(user.getAmountOfPoints());
                getArguments().putSerializable(Constants.ArgsName.USER, current);
                handler.post(() -> {
                    initPoints();
                    if (coffee > 0 && current.getAmountOfFreeCoffe() != coffee) {
                        FreeCoffeeFragment.show(UserFragment.this);
                    }
                });
            }
            Cat.i("String message from server: " + message);
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            Cat.i("Websocket connected");
            websocket.sendText(new Gson().toJson(new WebSocketMessage(WebSocketMessage.Action.init, user.getId())));
        }


        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            websocket.sendText(new Gson().toJson(new WebSocketMessage(WebSocketMessage.Action.remove, user.getId())));
            SystemUtils.sleepQuietly(1000);
            connect();
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            Cat.e(cause);
            SystemUtils.sleepQuietly(1000);
            connect();
        }
    };


    public static class WebSocketMessage {

        enum Action {init, remove}


        public final String action;
        public final int id;

        public WebSocketMessage(Action action, int id) {
            this.action = action.name();
            this.id = id;
        }
    }


    public static class WebSockedResponse {
        String type;
        User data;
        String context;
        Integer messageCount;
    }

}
