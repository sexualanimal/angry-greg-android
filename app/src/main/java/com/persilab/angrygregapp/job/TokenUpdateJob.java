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
import com.persilab.angrygregapp.domain.event.Event;
import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.domain.event.TokenUpdateEvent;
import com.persilab.angrygregapp.net.RestClient;
import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 0shad on 01.03.2016.
 */
public class TokenUpdateJob extends Job {

    private static final String TAG = TokenUpdateJob.class.getSimpleName();
    private static User user;
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
                token = RestClient.serviceApi().accessToken(user.getPhone(), user.getPassword()).execute().body();
                getSnappyHelper().storeSerializable(token);
            } else {
                Token refreshToken = RestClient.serviceApi().refreshToken(token.getRefreshToken()).execute().body();
                token.setAccessExpires(refreshToken.getAccessExpires());
                token.setAccessToken(refreshToken.getAccessToken());
                getSnappyHelper().storeSerializable(token);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unknown exception", e);
            postEvent(new TokenUpdateEvent(ResponseEvent.Status.FAILURE, null));
            return Result.FAILURE;
        } finally {
            SnappyHelper.close(getSnappyHelper());
        }
        postEvent(new TokenUpdateEvent(ResponseEvent.Status.SUCCESS, token));
        return Result.SUCCESS;
    }


    public SnappyHelper getSnappyHelper() {
        if (snappyHelper == null) {
            snappyHelper = new SnappyHelper(getContext(), TAG);
        }
        return snappyHelper;
    }

    protected void postEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    public static Token updateToken(Context context, User user) {
        SnappyHelper snappyHelper = new SnappyHelper(context);
        try {
            TokenUpdateJob.user = user;
            start();
            return snappyHelper.getSerializable(Token.class);
        } catch (Exception e) {
            Log.e(TAG, "Unknown exception", e);
        } finally {
            SnappyHelper.close(snappyHelper);
        }
        return null;
    }

    public static void stop() {
        if (jobId > 0) {
            JobManager.instance().cancel(jobId);
        }
    }

    public static void start() {
        jobId = AppJobCreator.request(JobType.UPDATE_TOKEN)
                .setPeriodic(200000)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();

    }
}
