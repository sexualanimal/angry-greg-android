package com.persilab.angrygregapp.domain.entity.json;

import lombok.Data;

/**
 * Created by Dmitry on 26.04.2016.
 */
@Data
public class JsonError extends JsonEntity {
    private String action;
    private String error;
}
