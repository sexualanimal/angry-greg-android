package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.adapter.ItemListAdapter;
import com.persilab.angrygregapp.adapter.MultiItemListAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Dmitry on 11.11.2015.
 */
public class DirectoryChooserFragment extends ListFragment<File> {

    private static final String TAG = DirectoryChooserFragment.class.getSimpleName();

    private String[] fileTypes = new String[]{};
    private File[] mFileList;
    private File currentDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
    private TextView pathTextView;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.choose_file));
        changePath();
    }

    @Override
    protected ItemListAdapter<File> getAdapter() {
        return new FileListAdapter();
    }

    public class FileListAdapter extends MultiItemListAdapter<File> {

        public FileListAdapter() {
            super(true, R.layout.header_path, R.layout.item_file);
        }

        @Override
        public int getLayoutId(File item) {
            return R.layout.item_file;
        }

        @Override
        public void onClick(View view, int position) {
            if(position == 0) return;
            if (currentDir.getParent() != null && currentDir.getParentFile().equals(getItem(position))) {
                currentDir = currentDir.getParentFile();
                changePath();
            } else {
                File currentFile = getItem(position);
                if(currentFile.isDirectory()) {
                    currentDir = currentFile;
                    changePath();
                } else {
                    try {
                        BaseFragment.show(DirectoryChooserFragment.this, currentFile.getAbsolutePath());
                    } catch (Exception e) {
                        Log.e(TAG, "Unknown exception", e);
                        Snackbar.make(getView(), "Не могу открыть файл! Ошибка: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case R.layout.header_path:
                    pathTextView = holder.getView(R.id.file_path);
                    pathTextView.setText(currentDir.getAbsolutePath());
                    break;
                case R.layout.item_file:
                    if(currentDir.getParent() != null && currentDir.getParentFile().equals(getItem(position))) {
                        com.persilab.angrygregapp.util.GuiUtils.setText(holder.getView(R.id.file_name), "\u21A9");
                    } else {
                        com.persilab.angrygregapp.util.GuiUtils.setText(holder.getView(R.id.file_name), getItem(position).getName());
                    }
                    break;
            }
        }
    }

    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }

    private void changePath() {
        loadFileList();
        adapter.getItems().clear();
        adapter.addItems(Arrays.asList(mFileList));
    }

    private void loadFileList() {
        try {
            currentDir.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (currentDir.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    boolean isNotHidden = !sel.isHidden();
                    if (!sel.isDirectory()) {
                        if (fileTypes != null) {
                            if (fileTypes.length == 0) {
                                return isNotHidden;
                            }
                            int index = filename.lastIndexOf("build/intermediates/exploded-aar/io.fabric.sdk.android/fabric/1.3.10/res");
                            if (index != -1) {
                                return isNotHidden && Arrays.asList(fileTypes).contains(filename.substring(index + 1));
                            }
                        }
                        return false;
                    }
                    return isNotHidden;
                }
            };
            File [] mFileList = currentDir.listFiles(filter);
            if (mFileList == null) {
                mFileList = new File[0];
            }
            Arrays.sort(mFileList, new Comparator<File>() {
                @Override
                public int compare(File lf, File rf) {
                    String lfs = lf.getName();
                    String rfs = lf.getName();
                    lfs = lfs.toLowerCase();
                    rfs = rfs.toLowerCase();
                    if (lf.isDirectory()) {
                        if (rf.isDirectory()) {
                            return lfs.compareTo(rfs);
                        } else {
                            return -1;
                        }
                    } else if (rf.isDirectory()) {
                        return 1;
                    }
                    return lfs.compareTo(rfs);
                }
            });
            if (currentDir.getParent() != null) {
                this.mFileList = new File[mFileList.length + 1];
                this.mFileList[0] = currentDir.getParentFile();
                System.arraycopy(mFileList, 0, this.mFileList, 1, mFileList.length);
            } else {
                this.mFileList = mFileList;
            }
        } else {
            this.mFileList = new File[0];
        }
    }
}
