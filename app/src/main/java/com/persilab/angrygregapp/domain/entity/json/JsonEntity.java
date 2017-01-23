package com.persilab.angrygregapp.domain.entity.json;

import lombok.Data;

/**
 * Created by 0shad on 28.02.2016.
 */

@Data
public class JsonEntity {
    private ServerInformation serverInformation;
    private RequesterInformation requesterInformation;

    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    public void setServerInformation(ServerInformation serverInformation) {
        this.serverInformation = serverInformation;
    }

    public RequesterInformation getRequesterInformation() {
        return requesterInformation;
    }

    public void setRequesterInformation(RequesterInformation requesterInformation) {
        this.requesterInformation = requesterInformation;
    }
}
