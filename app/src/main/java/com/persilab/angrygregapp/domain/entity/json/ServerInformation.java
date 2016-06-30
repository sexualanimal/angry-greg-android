package com.persilab.angrygregapp.domain.entity.json;

import lombok.Data;

/**
 * Created by 0shad on 28.02.2016.
 */

@Data
public class ServerInformation {
    private String serverName;
    private String apiVersion;
    private int requestDuration;
    private long currentTime;
}
