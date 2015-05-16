package com.diandian.coolco.emilie.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.Event;

import de.greenrobot.event.EventBus;

@SuppressLint("ValidFragment")
public class ClothesInfoDialogFragment extends DialogFragment {

    private CharSequence message;
    private Activity activity;

    public ClothesInfoDialogFragment(CharSequence message, Activity activity) {
        this.message = message;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                EventBus.getDefault().post(new Event.GuideFeedbackDialogDismissEvent());
            }
        });
        return builder.create();
    }
}
