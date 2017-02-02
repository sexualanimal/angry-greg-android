package com.persilab.angrygregapp.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.persilab.angrygregapp.App;
import com.persilab.angrygregapp.R;
import com.persilab.angrygregapp.domain.entity.User;
import com.persilab.angrygregapp.domain.event.AddRateEvent;
import com.persilab.angrygregapp.net.RestClient;
import com.persilab.angrygregapp.util.GuiUtils;


/**
 * Created by 0shad on 18.03.2016.
 */
public class AddPointsDialog extends BaseDialog {

    private int userId;

    public static void show(FragmentManager fragmentManager, User user) {
        AddPointsDialog dialog = (AddPointsDialog) fragmentManager.findFragmentByTag(AddPointsDialog.class.getSimpleName());
        if (dialog == null) {
            dialog = new AddPointsDialog();
            dialog.setUserId(user.getId());
            dialog.show(fragmentManager, AddPointsDialog.class.getSimpleName());
        }
    }

    @Bind(R.id.dialog_picker_numberPicker)
    NumberPicker numberPicker;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = GuiUtils.inflate(getActivity(), R.layout.dialog_picker);
        ButterKnife.bind(this, root);
        numberPicker.setMinValue(1);
        numberPicker.setValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this)
                .setView(root);
        return adb.create();
    }

    @Override
    public void onButtonPositive(DialogInterface dialog) {
        RestClient.serviceApi().addPoints(App.getActualToken().getAccessToken(),userId, numberPicker.getValue()).enqueue();
    }

}