package com.persilab.angrygregapp.net;

import com.persilab.shoppingbox.util.ReflectionUtils;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

/**
 * Created by 0shad on 28.02.2016.
 */
public class DefaultCallFactory implements CallAdapter.Factory {

    @Override
    public CallAdapter<Call<?>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (ReflectionUtils.getRawType(returnType) != Call.class) {
            return null;
        }
        final Type responseType = getCallResponseType(returnType);
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public <R> Call<?> adapt(retrofit2.Call<R> call) {
                return new Call<>(call);
            }
        };
    }

    Type getCallResponseType(Type returnType) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        return ReflectionUtils.getParameterUpperBound(0, (ParameterizedType) returnType);
    }
}
