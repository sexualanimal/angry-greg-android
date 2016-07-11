package com.persilab.angrygregapp.job;

import android.util.Log;
import com.evernote.android.job.Job;

import lombok.experimental.ExtensionMethod;

/**
 * Created by 0shad on 01.03.2016.
 */
public enum JobType {
    UPDATE_TOKEN(TokenUpdateJob.class);

    private static final String TAG = JobType.class.getSimpleName();

    final Class<? extends Job> jobClass;

    JobType(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }

    @SuppressWarnings("unchecked")
    public <J extends Job> J instantiate() {
        try {
            return (J) jobClass.newInstance();
        } catch (InstantiationException e) {
            Log.e(TAG, "Job must have no args public constructor", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Job must have no args public constructor\"", e);
        }
        return null;
    }
}