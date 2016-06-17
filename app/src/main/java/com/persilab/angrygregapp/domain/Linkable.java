package com.persilab.angrygregapp.domain;

import com.persilab.angrygregapp.util.TextUtils;

/**
 * Created by Rufim on 02.07.2015.
 */
public interface Linkable {
    public String getLink();

    public String getTitle();

    public String getAnnotation();

    public default String getFullLink() {
        return Constants.Net.BASE_DOMAIN + TextUtils.cleanupSlashes(getLink());
    }

}


