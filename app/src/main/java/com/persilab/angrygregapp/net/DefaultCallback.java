package com.persilab.angrygregapp.net;

import com.persilab.angrygregapp.domain.event.NetworkEvent;

import net.vrallev.android.cat.Cat;

import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;

import retrofit2.Callback;
import retrofit2.Response;

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
        switch(response.code()){
            case HttpURLConnection.HTTP_ACCEPTED:
                System.out.println("202: Accepted");
                break;
            case HttpURLConnection.HTTP_BAD_GATEWAY:
                System.out.println("502: Bad Gateway");
                break;
            case HttpURLConnection.HTTP_BAD_METHOD:
                System.out.println("405: Bad Method");
                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                System.out.println("400: Bad Request");
                break;
            case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                System.out.println("408: Client Timeout");
                break;
            case HttpURLConnection.HTTP_CONFLICT:
                System.out.println("409: Conflict");
                break;
            case HttpURLConnection.HTTP_CREATED:
                System.out.println("201: Created");
                break;
            case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                System.out.println("413: Entity too large");
                break;
            case HttpURLConnection.HTTP_FORBIDDEN:
                System.out.println("403: Forbidden");
                break;
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                System.out.println("504: Gateway timeout");
                break;
            case HttpURLConnection.HTTP_GONE:
                System.out.println("410: Gone");
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                System.out.println("500: Internal error");
                break;
            case HttpURLConnection.HTTP_LENGTH_REQUIRED:
                System.out.println("411: Length required");
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
                System.out.println("301 Moved permanently");
                break;
            case HttpURLConnection.HTTP_MOVED_TEMP:
                System.out.println("302: Moved temporarily");
                break;
            case HttpURLConnection.HTTP_MULT_CHOICE:
                System.out.println("300: Multiple choices");
                break;
            case HttpURLConnection.HTTP_NO_CONTENT:
                System.out.println("204: No content");
                break;
            case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
                System.out.println("406: Not acceptable");
                break;
            case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
                System.out.println("203: Not authoritative");
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                System.out.println("404: Not found");
                break;
            case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
                System.out.println("501: Not implemented");
                break;
            case HttpURLConnection.HTTP_NOT_MODIFIED:
                System.out.println("304: Not modified");
                break;
            case HttpURLConnection.HTTP_OK:
                System.out.println("200: OK");
                break;
            case HttpURLConnection.HTTP_PARTIAL:
                System.out.println("206: Partial");
                break;
            case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
                System.out.println("402: Payment required");
                break;
            case HttpURLConnection.HTTP_PRECON_FAILED:
                System.out.println("412: Precondition failed");
                break;
            case HttpURLConnection.HTTP_PROXY_AUTH:
                System.out.println("407: Proxy authentication required");
                break;
            case HttpURLConnection.HTTP_REQ_TOO_LONG:
                System.out.println("414: Request too long");
                break;
            case HttpURLConnection.HTTP_RESET:
                System.out.println("205: Reset");
                break;
            case HttpURLConnection.HTTP_SEE_OTHER:
                System.out.println("303: See other");
                break;
            case HttpURLConnection.HTTP_USE_PROXY:
                System.out.println("305: Use proxy");
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                System.out.println("401: Unauthorized");
                break;
            case HttpURLConnection.HTTP_UNSUPPORTED_TYPE:
                System.out.println("415: Unsupported type");
                break;
            case HttpURLConnection.HTTP_UNAVAILABLE:
                System.out.println("503: Unavailable");
                break;
            case HttpURLConnection.HTTP_VERSION:
                System.out.println("505: Version not supported");
                break;
        }



//        if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
//            if (onSuccess != null) {
//                onSuccess.response(response, this);
//            } else {
//                postResponseEvent(response, response.raw().request());
//            }
//        } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
//            System.out.println("Forbidden!");
//            postErrorEvent(response, response.raw().request());
//        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
//            System.out.println("Not found :(");
//            postErrorEvent(response, response.raw().request());
//            // TODO: think about adding other stuff here
//        } else {
//            postErrorEvent(response, response.raw().request());
//        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        Cat.e(throwable);
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
