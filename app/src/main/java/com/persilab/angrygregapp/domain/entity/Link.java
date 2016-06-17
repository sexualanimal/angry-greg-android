package com.persilab.angrygregapp.domain.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.persilab.angrygregapp.domain.Linkable;
import com.persilab.angrygregapp.domain.Validatable;

import java.io.Serializable;

/**
 * Created by Rufim on 01.07.2015.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@DatabaseTable
public class Link implements Validatable, Linkable, Serializable {

    @DatabaseField(id = true)
    protected Long id;

    protected String title;
    protected String link;
    protected String annotation;

    @Override
    public String toString() {
        return link;
    }

    @Override
    public boolean validate() {
        return link != null && title != null;
    }

}
