package com.persilab.angrygregapp.net;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.gson.*;
import com.persilab.angrygregapp.database.SnappyHelper;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;

import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.net.adapter.BigDecimalTypeAdapter;
import com.persilab.angrygregapp.net.adapter.StateTypeAdapter;
import com.persilab.angrygregapp.net.adapter.UriTypeAdapter;
import com.snappydb.SnappydbException;
import okhttp3.*;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import retrofit2.*;
import retrofit2.Response;
import retrofit2.http.*;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 0shad on 25.02.2016.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();

    public interface RestServiceApi {

        @GET("account")
        Call account(@Header("Authentication") String authentication,
                     @Query("state") String name,
                     @Query("gender") String phone);

        @POST("auth/access")
        Call<Token> accessToken(@Query("username") String username,
                                @Query("password") String password);

        @POST("auth/refresh/{refreshToken}")
        Call<Token> refreshToken(@Path("refreshToken") String refreshToken);


    }

    private final static RestServiceApi service = buildApi();

    public static RestServiceApi serviceApi() {
        return service;
    }

    private static RestServiceApi buildApi() {
        if (service == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(interceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }).build();

            Gson gson = new GsonBuilder()
                    .setDateFormat(Constants.Pattern.DATA_ISO_8601_24H_FULL_FORMAT)
                    .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                    .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .setFieldNamingStrategy(f -> {
                        String name = f.getName();
                        if (name.equals("id")) name = "_id";
                        if (name.equals("version")) name = "__v";
                        return name;
                    })
                    .create();

            return new Retrofit.Builder()
                    .baseUrl(Constants.Net.BASE_DOMAIN + "api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(new DefaultCallFactory())
                    .client(okHttpClient)
                    .build()
                    .create(RestServiceApi.class);
        }
        return service;
    }

    public static void updateToken(final User user, final Context context) {
        serviceApi().accessToken(user.getPhone(), user.getPassword()).enqueue(new DefaultCallback<Token>((response, callback) -> {
            SnappyHelper helper = new SnappyHelper(context);
            try {
                helper.storeSerializable(response.body());
            } catch (SnappydbException e) {
                Log.e(TAG, "Unknown exception", e);
            } finally {
                helper.close();
            }
            EventBus.getDefault().post(new TokenUpdateEvent(ResponseEvent.Status.SUCCESS, response.body()));
        }));
    }

}
