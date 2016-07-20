package com.persilab.angrygregapp.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.evernote.android.job.Job;;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.persilab.angrygregapp.activity.MainActivity;
import com.persilab.angrygregapp.database.SnappyHelper;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.entity.json.JsonError;
import com.persilab.angrygregapp.domain.event.Event;
import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.fragments.LoginFragment;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.TextUtils;
import com.snappydb.SnappydbException;
import net.vrallev.android.cat.Cat;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Response;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 0shad on 01.03.2016.
 */
public class TokenUpdateJob extends Job {

    private static final String TAG = TokenUpdateJob.class.getSimpleName();
    private static User user;
    private static Token token;
    private SnappyHelper snappyHelper = null;
    public static int jobId = -1;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Token token = null;
        if (isCanceled()) {
            SnappyHelper.close(getSnappyHelper());
            return Result.SUCCESS;
        }
        try {
            token = getSnappyHelper().getSerializable(Token.class);
            if (token == null || token.isRefreshExpired()) {
                //TODO: Call login fragment if user is not found
                if(userIsValid()) {
                    Response response = RestClient.serviceApi().accessToken(user.getPhone(), user.getPassword()).execute();
                    if (response.body() instanceof JsonError) {
                        postEvent(new TokenUpdateEvent(ResponseEvent.Status.FAILURE, null));
                    }
                    if (response.body() instanceof Token) {
                        token = (Token) response.body();
                        TokenUpdateJob.token = token;
                        getSnappyHelper().storeSerializable(token);
                        postEvent(new TokenUpdateEvent(ResponseEvent.Status.SUCCESS, token));
                    }
                }
            } else {
                Token refreshToken = RestClient.serviceApi().refreshToken(token.getRefreshToken()).execute().body();
                token.setAccessExpires(refreshToken.getAccessExpires());
                token.setAccessToken(refreshToken.getAccessToken());
                TokenUpdateJob.token = token;
                getSnappyHelper().storeSerializable(token);
            }
        } catch (Exception e) {
            Cat.e("Unknown exception", e);
            postEvent(new TokenUpdateEvent(ResponseEvent.Status.FAILURE, token));
            return Result.FAILURE;
        } finally {
            SnappyHelper.close(getSnappyHelper());
        }
        return Result.SUCCESS;
    }


    public SnappyHelper getSnappyHelper() {
        if (snappyHelper == null) {
            snappyHelper = new SnappyHelper(getContext(), TAG);
        }
        return snappyHelper;
    }

    public static User getUser() {
        return user;
    }

    public static Token getToken(Context context) {
        if(token == null) {
            SnappyHelper helper = null;
            try {
                helper = new SnappyHelper(context, TAG);;
                token = helper.getSerializable(Token.class);
                if(token != null) {
                    user = token.getAccount();
                }
            } catch (SnappydbException e) {
                Log.e(TAG, "Unknown exception", e);
            } finally {
                SnappyHelper.close(helper);
            }
        }
        return token;
    }

    public static Token getToken() {
        return token;
    }


    protected void postEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    public static void stop() {
        if (jobId > 0) {
            JobManager.instance().cancel(jobId);
        }
    }

    public static void start(Token current, User user) {
        if(current != null) {
            TokenUpdateJob.token = current;
            TokenUpdateJob.user = current.getAccount();
        }
        if(user != null) {
            TokenUpdateJob.user = user;
        }
        if(tokenIsValid() || userIsValid()) {
            jobId = AppJobCreator.request(JobType.UPDATE_TOKEN)
                    .setPeriodic(200000)
                    .setUpdateCurrent(true)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule();
        }
    }

    public static boolean tokenIsValid() {
        return token != null && !token.isRefreshExpired();
    }

    public static  boolean userIsValid() {
       return user != null && !TextUtils.isEmpty(user.getPhone()) && !TextUtils.isEmpty(user.getPassword());
    }

}
