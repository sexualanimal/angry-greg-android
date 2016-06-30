package com.persilab.angrygregapp.net.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.persilab.shoppingbox.domain.entity.AbstractItem;

import java.io.IOException;

/**
 * Created by 0shad on 03.03.2016.
 */
public class StateTypeAdapter extends TypeAdapter<AbstractItem.State> {
    @Override
    public void write(JsonWriter out, AbstractItem.State value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.name("state");
        out.beginArray();
        out.value(value.name().toLowerCase());
        out.endArray();
    }

    @Override
    public AbstractItem.State read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL || in.peek() == JsonToken.END_ARRAY) {
            in.nextNull();
            return null;
        }
        AbstractItem.State state = null;
        in.beginArray();
        try {
            state = AbstractItem.State.valueOf(in.nextString().toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        in.endArray();
        return state;
    }
}
