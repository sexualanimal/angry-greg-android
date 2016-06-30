package com.persilab.angrygregapp.net;

import android.util.Log;
import com.google.gson.Gson;
import com.persilab.shoppingbox.domain.entity.JsonError;
import com.persilab.shoppingbox.domain.event.NetworkEvent;
import com.persilab.shoppingbox.util.ReflectionUtils;
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
            else postResponseEvent(response);
        } else if (response.errorBody() != null) {
            try {
                JsonError error = new Gson().fromJson(response.errorBody().string(), JsonError.class);
                error.setAction(error.getRequesterInformation().getReceivedParams().get("action"));
                postErrorEvent(error);
            } catch (IOException e) {
                Log.e(TAG, "Cant read error", e);
            }
        } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
            System.out.println("Forbidden!");
            postErrorEvent(response);
        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
            System.out.println("Not found :(");
            postErrorEvent(response);
            // TODO: think about adding other stuff here
        } else {
            postErrorEvent(response);
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        System.out.println(errorMsg + ": " + throwable.getLocalizedMessage());
        if (onSuccess != null) onFailure.response(throwable, this);
        else postErrorEvent(throwable);
    }

    public void postErrorEvent(Object response) {
        EventBus.getDefault().post(new NetworkEvent<>(NetworkEvent.Status.FAILURE, response));
    }

    public void postResponseEvent(Response<T> response) {
        EventBus.getDefault().post(new NetworkEvent<>(NetworkEvent.Status.SUCCESS, response));
    }

}
