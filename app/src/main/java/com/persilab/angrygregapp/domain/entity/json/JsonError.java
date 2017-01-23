package com.persilab.angrygregapp.domain.entity.json;

import lombok.Data;

/**
 * Created by Dmitry on 26.04.2016.
 */
@Data
public class JsonError extends JsonEntity {
    private String action;
    private String error;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
