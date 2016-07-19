package com.persilab.angrygregapp.net;

import android.net.Uri;
import com.google.gson.*;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;

import com.persilab.angrygregapp.domain.entity.json.JsonEntity;
import com.persilab.angrygregapp.net.adapter.BigDecimalTypeAdapter;
import com.persilab.angrygregapp.net.adapter.UriTypeAdapter;
import okhttp3.*;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.http.*;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 0shad on 25.02.2016.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();
    public static final String ACCOUNTS = "accounts";
    public static final String AUTH = "auth";

    public interface RestServiceApi {

        @GET(ACCOUNTS)
        Call<List<User>> accounts(@Header("Authentication") String authentication,
                                  @Query("phone") String phone,
                                  @Query("name") String name,
                                  @Query("is_admin") Boolean admin,
                                  @Query("offset") Integer offset,
                                  @Query("limit") Integer limit);

        @GET(ACCOUNTS + "/{id}")
        Call<User> getAccount(@Path("id") String id);

        @DELETE(ACCOUNTS + "/{id}")
        Call<JsonEntity> deleteAccount(@Path("id") String id);

        @PUT(ACCOUNTS + "/{id}")
        Call<User> changeAccount(@Path("id") String id,
                                 @Query("name") String name,
                                 @Query("phone") String phone,
                                 @Query("birthday") String birthday);

        @POST(ACCOUNTS)
        Call<User> createAccount(@Query("name") String name,
                                 @Query("phone") String phone,
                                 @Query("password") String password,
                                 @Query("birthday") String birthday);

        @POST(AUTH + "/access")
        Call<Token> accessToken(@Query("phone") String phone,
                                @Query("password") String password);

        @POST(AUTH + "/refresh/{refreshToken}")
        Call<Token> refreshToken(@Path("refreshToken") String refreshToken);

        @PUT(ACCOUNTS + "/{id}/addpoints/{amountOfPoints}")
        Call<User> addPoints(@Path("id") String userId, @Path("amountOfPoints") Integer amount);
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

}
