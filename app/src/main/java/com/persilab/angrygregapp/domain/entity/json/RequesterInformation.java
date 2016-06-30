package com.persilab.angrygregapp.domain.entity.json;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 0shad on 28.02.2016.
 */

@Data
public class RequesterInformation {
    private String id;
    private String fingerprint;
    private String remoteIP;
    private Map<String,String> receivedParams = new LinkedHashMap<>();
}
