package com.persilab.angrygregapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import com.google.zxing.Result;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.event.UserFoundEvent;
import com.persilab.angrygregapp.domain.event.ResponseEvent;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
        RestClient.serviceApi().getAccount(Integer.parseInt(result.getText())).enqueue();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(UserFoundEvent event) {
        if(event.status.equals(ResponseEvent.Status.SUCCESS)) {
            AddPointsUserFragment.show(QRScannerFragment.this, event.message);
            Handler handler = new Handler();
            handler.postDelayed(() -> mScannerView.resumeCameraPreview(QRScannerFragment.this), 2000);
        } else {
            GuiUtils.toast(getContext(), R.string.qr_scanner_qr_not_found);
        }
    }

}
