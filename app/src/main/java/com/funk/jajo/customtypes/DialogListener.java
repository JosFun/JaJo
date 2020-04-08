package com.funk.jajo.customtypes;

import android.support.v4.app.DialogFragment;

public interface DialogListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
}
