package com.funk.jajo.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.funk.jajo.R;
import com.funk.jajo.customtypes.DialogListener;
import com.funk.jajo.customtypes.Money;
import com.funk.jajo.customtypes.Payment;

import java.util.Calendar;
import java.util.Date;

/**
 * This {@link AddPaymentDialog} allows the user to specify details for a new expense entry.
 */
public class AddPaymentDialog extends DialogFragment {

    private DialogListener listener;

    /**
     * The {@link Money} the user has specified on this {@link AddPaymentDialog}.
     */
    private Money expenseAmount;
    /**
     * The {@link String} that gives the {@link Payment} entered by the user a proper description.
     */
    private String expenseName;
    /**
     * The {@link Calendar} that stores the date, the {@link Payment} has been done.
     */
    private Calendar expenseDate;

    @Override
    public Dialog onCreateDialog ( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        /* Inflate layout */
        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        /* Set View to according dialog add visualization view. Parent root is set to null. */
        final View v = inflater.inflate(R.layout.dialog_add_expense, null);
        builder.setView(v);

        final EditText enterMoney = v.findViewById(R.id.expense_amount_edit);
        final EditText enterDescr = v.findViewById(R.id.expense_name_edit);
        final DatePicker datePicker = v.findViewById(R.id.expense_date_edit);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if ( enterMoney != null ) {
                    String moneyStr = enterMoney.getText().toString();
                    double value = 0;
                    try {
                        value = Double.parseDouble(moneyStr);
                    } catch ( NumberFormatException e ) {
                        e.printStackTrace();
                    }
                    AddPaymentDialog.this.expenseAmount = new Money ( value );
                }

                if ( enterDescr != null ) {
                    AddPaymentDialog.this.expenseName = enterDescr.getText().toString();
                }

                if ( datePicker != null ) {
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    int year = datePicker.getYear();

                    Calendar c = Calendar.getInstance();
                    c.set( year, month, day );

                    AddPaymentDialog.this.expenseDate = c;

                }

                if ( AddPaymentDialog.this.listener != null ) {
                    AddPaymentDialog.this.listener.onDialogPositiveClick( AddPaymentDialog.this );
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( AddPaymentDialog.this.listener != null ) {
                    AddPaymentDialog.this.listener.onDialogNegativeClick( AddPaymentDialog.this );
                }
            }
        });

        return builder.create();
    }

    /**
     * Set up the {@link DialogListener} that is supposed to listen to events emitted by this {@link AddPaymentDialog}.
     * @param listener - The {@link DialogListener} that is supposed to listen to the events.
     */
    public void setListener (DialogListener listener ) {
        this.listener = listener;
    }

    /**
     * Get access to the {@link Payment} the user has specified on this {@link AddPaymentDialog}.
     * @return the {@link Payment} having been specified on this {@link AddPaymentDialog}
     */
    public Payment getExpense ( ) {
        return new Payment( this.expenseName, this.expenseAmount, this.expenseDate);
    }
}
