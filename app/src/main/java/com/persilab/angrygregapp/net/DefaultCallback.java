package com.persilab.angrygregapp.net;

import android.util.Log;
import com.google.gson.Gson;

import com.persilab.angrygregapp.domain.entity.json.JsonError;
import com.persilab.angrygregapp.domain.event.NetworkEvent;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by 0shad on 28.02.2016.
 */
public class DefaultCallback<T> implements Callback<T> {

    private static final String TAG = DefaultCallback.class.getSimpleName();

    interface OnSuccess<T> {
        void response(Response<T> response, DefaultCallback<T> callback);
    }

    interface OnFailure<T> {
        void response(Throwable throwable, DefaultCallback<T> callback);
    }

    private String errorMsg = "";
    private OnSuccess<T> onSuccess;
    private OnFailure<T> onFailure;

    public DefaultCallback() {
    }

    public DefaultCallback(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DefaultCallback(OnSuccess<T> onSuccess) {
        this(onSuccess, null);
    }

    public DefaultCallback(OnSuccess<T> onSuccess, OnFailure<T> onFailure) {
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    public void onResponse(Response<T> response) {
        if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
            if (onSuccess != null) onSuccess.response(response, this);
            else postResponseEvent(response, response.raw().request());
        } else if (response.errorBody() != null) {
            try {
                JsonError error = new Gson().fromJson(response.errorBody().string(), JsonError.class);
                if (error.getRequesterInformation() != null) {
                    error.setAction(error.getRequesterInformation().getReceivedParams().get("action"));
                }
                postErrorEvent(error, response.raw().request());
            } catch (IOException e) {
                Log.e(TAG, "Cant read error", e);
            }
        } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
            System.out.println("Forbidden!");
            postErrorEvent(response, response.raw().request());
        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
            System.out.println("Not found :(");
            postErrorEvent(response, response.raw().request());
            // TODO: think about adding other stuff here
        } else {
            postErrorEvent(response, response.raw().request());
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        System.out.println(errorMsg + ": " + throwable.getLocalizedMessage());
        if (onFailure != null) onFailure.response(throwable, this);
        else postErrorEvent(throwable, null);
    }

    public void postErrorEvent(Object response, okhttp3.Request request) {
        EventBus.getDefault().post(new NetworkEvent<>(NetworkEvent.Status.FAILURE, response, request));
    }

    public void postResponseEvent(Response<T> response, okhttp3.Request request) {
        EventBus.getDefault().post(new NetworkEvent<>(NetworkEvent.Status.SUCCESS, response, request));
    }

}
