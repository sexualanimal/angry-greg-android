package com.persilab.angrygregapp.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.evernote.android.job.Job;;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.persilab.angrygregapp.database.SnappyHelper;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.entity.json.JsonError;
import com.persilab.angrygregapp.domain.event.Event;
import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.net.RestClient;
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
        Token token;
        if (isCanceled()) {
            SnappyHelper.close(getSnappyHelper());
            return Result.SUCCESS;
        }
        try {
            token = getSnappyHelper().getSerializable(Token.class);
            if (token == null || token.isRefreshExpired()) {
                //TODO: Call login fragment if user is not found
                Response response = RestClient.serviceApi().accessToken(user.getPhone(), user.getPassword()).execute();
                if(response.body() instanceof JsonError) {
                    postEvent(new TokenUpdateEvent(ResponseEvent.Status.FAILURE, null));
                }
                if(response.body() instanceof Token) {
                    token = (Token) response.body();
                    TokenUpdateJob.token = token;
                    getSnappyHelper().storeSerializable(token);
                    postEvent(new TokenUpdateEvent(ResponseEvent.Status.SUCCESS, token));
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
            postEvent(new TokenUpdateEvent(ResponseEvent.Status.FAILURE, null));
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

    public static void start(User user, Token current) {
        TokenUpdateJob.token = current;
        TokenUpdateJob.user = user;
        jobId = AppJobCreator.request(JobType.UPDATE_TOKEN)
                .setPeriodic(200000)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();

    }
}
