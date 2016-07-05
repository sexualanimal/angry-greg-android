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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.database.SuggestionProvider;
import com.persilab.angrygregapp.domain.Constants;
import com.persilab.angrygregapp.domain.entity.Token;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.entity.json.JsonEntity;
import com.persilab.angrygregapp.domain.entity.json.JsonError;
import com.persilab.angrygregapp.domain.event.*;
import com.persilab.angrygregapp.fragments.BaseFragment;
import com.persilab.angrygregapp.fragments.ErrorFragment;
import com.persilab.angrygregapp.fragments.LoginFragment;
import com.persilab.angrygregapp.net.RestClient;
import net.vrallev.android.cat.Cat;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Response;


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
        getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
    public void onEvent(NetworkEvent networkEvent) {
        System.out.println(networkEvent.message.toString());
        String path = networkEvent.request.url().encodedPath().substring(5);
        String method = networkEvent.request.method();
        if (networkEvent.status == NetworkEvent.Status.FAILURE) {
            Cat.e(networkEvent.message.toString());
            if(networkEvent.message instanceof JsonError) {
                JsonError error = (JsonError) networkEvent.message;
                if(path.contains(RestClient.AUTH)) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, null));
                    return;
                }
                if(path.matches(RestClient.ACCOUNTS + "/[a-z0-9]+")) {
                    if(method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, null));
                    }
                    if(method.equals("DELETE")) {
                        postEvent(new UserDeletedEvent(networkEvent.status, null));
                    }
                }
            }
            ErrorFragment.show((BaseFragment) getCurrentFragment(), R.string.error_network);
        } else {
            if (networkEvent.message instanceof Response) {
                Response response = (Response) networkEvent.message;
                if (response.body() instanceof Token) {
                    postEvent(new TokenUpdateEvent(networkEvent.status, (Token) response.body()));
                }
                if (path.contains("addpoints")) {
                    postEvent(new AddRateEvent(networkEvent.status, (User) response.body()));
                }
                if(path.matches(RestClient.ACCOUNTS + "/[a-z0-9]+")) {
                    if(method.equals("GET")) {
                        postEvent(new UserFoundEvent(networkEvent.status, (User) response.body()));
                    }
                    if(method.equals("DELETE")) {
                        postEvent(new UserDeletedEvent(networkEvent.status, path.substring(path.indexOf('/') + 1)));
                    }
                }
            }
        }
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
