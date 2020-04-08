package com.funk.jajo.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.funk.jajo.R;
import com.funk.jajo.customtypes.DialogListener;

public class AddPaymentDialog extends DialogFragment {

    private DialogListener listener;

    @Override
    public Dialog onCreateDialog ( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        /* Inflate layout */
        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        /* Set View to according dialog add visualization view. Parent root is set to null. */
        final View v = inflater.inflate(R.layout.dialog_add_expense, null);
        builder.setView(v);

        return builder.create();
    }

    public void setListener (DialogListener listener ) {
        this.listener = listener;
    }
}
