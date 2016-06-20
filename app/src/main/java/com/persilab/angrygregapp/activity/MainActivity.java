package com.persilab.angrygregapp.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.database.SuggestionProvider;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.event.FragmentAttachedEvent;
import com.persilab.angrygregapp.fragments.LoginFragment;
import org.greenrobot.eventbus.Subscribe;


public class MainActivity extends BaseActivity {

    private CharSequence title;

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Востанавливаем фрагмент при смене ориентации экрана.
        Fragment sectionFragment = getLastFragment(savedInstanceState);
        if (sectionFragment != null) {
            replaceFragment(sectionFragment);
        } else {
            replaceFragment(LoginFragment.class);
        }
    }

    @Override
    protected void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
           // SearchFragment.show(getCurrentFragment(), query); // Implement SearchFragment
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            // searchView.setOnQueryTextListener(this);
            // MenuItemCompat.setOnActionExpandListener(searchItem, this);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onDrawerClosed(View drawerView) {

    }

    @Override
    protected void onDrawerOpened(View drawerView) {

    }

    @Subscribe
    public void onEvent(FragmentAttachedEvent fragmentAttached) {
        title = fragmentAttached.fragment.getArguments().getString(Constants.ArgsName.TITLE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(container, R.string.back_to_exit, Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
        }
    }

}
