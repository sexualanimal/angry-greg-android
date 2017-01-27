package com.persilab.angrygregapp.net;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.google.gson.*;
import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.activity.MainActivity;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;

import com.persilab.angrygregapp.domain.entity.json.JsonEntity;
import com.persilab.angrygregapp.net.adapter.BigDecimalTypeAdapter;
import com.persilab.angrygregapp.net.adapter.UriTypeAdapter;

import okhttp3.*;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.http.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();
    public static final String ACCOUNTS = "accounts";
    public static final String AUTH = "auth";

    public interface RestServiceApi {

        @GET(ACCOUNTS)
        Call<List<User>> accounts(@Header("Authorization") String authentication);

        @GET(ACCOUNTS + "/{id}")
        Call<User> getAccount(@Path("id") Integer id);

        @DELETE(ACCOUNTS + "/{id}")
        Call<JsonEntity> deleteAccount(@Path("id") Integer id);

        @PUT(ACCOUNTS + "/{id}")
        Call<User> changeAccount(@Path("id") Integer id,
                                 @Query("name") String name,
                                 @Query("phone") String phone,
                                 @Query("birthday") String birthday,
                                 @Query("points") Integer points,
                                 @Query("is_admin") Integer is_admin,
                                 @Query("password") String password);

        @POST(ACCOUNTS)
        Call<User> createAccount(@Query("name") String name,
                                 @Query("phone") String phone,
                                 @Query("birthday") String birthday,
                                 @Query("points") Integer points,
                                 @Query("is_admin") Integer is_admin,
                                 @Query("password") String password);

        @POST(AUTH + "/access")
        Call<Token> accessToken(@Query("username") String username,
                                @Query("password") String password);

        @POST(AUTH + "/refresh/{refreshToken}")
        Call<Token> refreshToken(@Path("refreshToken") String refreshToken);

        @PUT(ACCOUNTS + "/{id}/addpoints/{amountOfPoints}")
        Call<User> addPoints(@Path("id") Integer id, @Path("points") Integer points);
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
                    .authenticator(new TokenAuthenticator()).build();

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

    public static class TokenAuthenticator implements Authenticator {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            System.out.println("--------------------------------------------------------try to auto auth");
            Context context = App.getInstance();
            if (context != null) {
                Token oldToken = App.getActualToken();

                // Refresh your access_token using a synchronous api request
                Token newAccessToken = service.refreshToken(oldToken.getRefreshToken()).execute().body();
                if (newAccessToken != null) {
                    App.setActualToken(newAccessToken);
                } else {
                    return null;
                }

                // Add new header to rejected request and retry it
                return response.request().newBuilder()
                        .header("Authentication", newAccessToken.getAccessToken())
                        .build();
            } else {
                return null;
            }
        }

    }

}
