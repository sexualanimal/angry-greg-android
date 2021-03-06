package com.persilab.angrygregapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.adapter.FragmentPagerAdapter;
import com.persilab.angrygregapp.lister.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by 0shad on 26.10.2015.
 */
public abstract class PagerFragment<I, F extends BaseFragment> extends BaseFragment {

    private static final String TAG = PagerFragment.class.getSimpleName();

    @Bind(R.id.load_more)
    protected ProgressBar loadMoreBar;
    @Bind(R.id.pager_header)
    protected PagerTabStrip pagerHeader;
    @Bind(R.id.pager)
    protected ViewPager pager;
    protected FragmentPagerAdapter<I, F> adapter;
    protected DataSource<I> dataSource;
    protected volatile boolean isLoading = false;
    protected volatile boolean isEnd = false;
    protected int pagesSize = 50;
    protected int currentCount = 0;
    protected PagerDataTask dataTask;

    protected int currentItem = 0;
    protected List<I> currentItems;

    public PagerFragment() {
    }

    public PagerFragment(DataSource<I> dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource<I> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
        bind(rootView);
        if (currentItems == null) {
            currentItems = new ArrayList<>();
        }
        adapter = getAdapter(currentItems);
        currentItems = adapter.getItems();
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                PagerFragment.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                PagerFragment.this.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                PagerFragment.this.onPageScrollStateChanged(state);
            }
        });
        if (adapter != null) {
            if (dataSource != null) {
                if (dataTask != null) {
                    dataTask.cancel(true);
                }
                loadItems(pagesSize, true);
            } else {
                stopLoading();
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setCurrentItem(currentItem);
    }

    public void startLoading(boolean showProgress) {
        isLoading = true;
        if (loadMoreBar != null && showProgress) {
            loadMoreBar.setVisibility(View.VISIBLE);
        }
    }

    public void stopLoading() {
        isLoading = false;
        if (loadMoreBar != null) {
            loadMoreBar.setVisibility(View.INVISIBLE);
        }
    }

    protected void loadItems(int count, boolean showProgress, AsyncTask onElementsLoadedTask, Object... params) {
        if (isLoading || isEnd) {
            return;
        }
        startLoading(showProgress);
        if (dataSource != null) {
            PagerDataTask dataTask = new PagerDataTask(count, onElementsLoadedTask, params);
            if (this.dataTask == null) {
                dataTask.execute();
            }
            this.dataTask = dataTask;
        }
    }

    protected void loadItems(int count, boolean showProgress) {
        loadItems(count, showProgress, null, null);
    }

    protected void clearData() {
        currentCount = 0;
        isEnd = false;
        if (adapter != null) {
            adapter.getItems().clear();
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshData(boolean showProgress) {
        clearData();
        loadItems(pagesSize, showProgress);
    }


    public abstract FragmentPagerAdapter<I, F> getAdapter(List<I> currentItems);

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onPageSelected(int position) {
        if (!isEnd && position == currentCount - 1) {
            loadItems(pagesSize, true);
        }
        currentItem = position;
    }

    public void onPageScrollStateChanged(int state) {

    }

    public class PagerDataTask extends AsyncTask<Void, Void, List<I>> {

        private int count = 0;
        private AsyncTask onElementsLoadedTask;
        private Object[] LoadedTaskParams;

        public PagerDataTask(int count) {
            this.count = count;
        }

        public PagerDataTask(int count, AsyncTask onElementsLoadedTask, Object[] LoadedTaskParams) {
            this.count = count;
            this.onElementsLoadedTask = onElementsLoadedTask;
            this.LoadedTaskParams = LoadedTaskParams;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<I> doInBackground(Void... params) {
            List<I> items = null;
            try {
                items = dataSource.getItems(currentCount, count);
                if (items.size() == 0) {
                    return items;
                }
            } catch (IOException e) {
                Log.e(TAG, "Cant get new Items", e);
                ErrorFragment.show(PagerFragment.this, R.string.error_network);
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<I> result) {
            super.onPostExecute(result);
            if (pager != null) {
                if (result.size() == 0) {
                    isEnd = true;
                } else {
                    adapter.addItems(result);
                }
                stopLoading();
                currentCount = adapter.getCount();
                if (onElementsLoadedTask != null) {
                    onElementsLoadedTask.execute(LoadedTaskParams);
                }
                if (this != dataTask) {
                    dataTask.execute();
                }
                dataTask = null;
            }
        }
    }


}
