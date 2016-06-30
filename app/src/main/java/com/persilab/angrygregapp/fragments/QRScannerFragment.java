package com.persilab.angrygregapp.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.zxing.Result;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.util.GuiUtils;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by 0shad on 27.06.2016.
 */
public class QRScannerFragment extends BaseFragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    protected static QRScannerFragment show(BaseFragment fragment) {
        return show(fragment, QRScannerFragment.class);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        setHasOptionsMenu(true);
        getActivity().setTitle("");
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        EditUserFragment.show(QRScannerFragment.this, new User());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(QRScannerFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
