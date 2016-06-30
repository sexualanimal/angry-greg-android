package com.persilab.angrygregapp.domain;

import com.persilab.angrygregapp.adapter.ItemListAdapter;

/**
 * Created by Dmitry on 29.07.2015.
 */
public interface Findable {
    boolean find(ItemListAdapter.FilterEvent query);
}
