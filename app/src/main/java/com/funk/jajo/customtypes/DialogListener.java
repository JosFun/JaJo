package com.funk.jajo.customtypes;

import androidx.fragment.app.DialogFragment;

public interface DialogListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
}
