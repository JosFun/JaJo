package com.funk.jajo.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.funk.jajo.R;
import com.funk.jajo.customtypes.DialogListener;
import com.funk.jajo.customtypes.Money;
import com.funk.jajo.customtypes.Payment;
import com.funk.jajo.customtypes.Person;

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
     * The {@link Calendar} that stores the date, the {@link Payment} has been done.
     */
    private Calendar expenseDate;

    private String description;

    /**
     * The {@link String} representing the {@link Person} the user has selected on this {@link AddPaymentDialog}
     */
    private String personSelection = null;

    @Override
    public Dialog onCreateDialog ( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        /* Inflate layout */
        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        /* Set View to according dialog add visualization view. Parent root is set to null. */
        final View v = inflater.inflate(R.layout.dialog_add_expense, null);
        builder.setView(v);

        final EditText enterMoney = v.findViewById(R.id.expense_amount_edit);
        final EditText enterDescr = v.findViewById(R.id.expense_descr_edit);
        final DatePicker datePicker = v.findViewById(R.id.expense_date_edit);
        final Spinner personSpinner = v.findViewById(R.id.expense_person_edit);

        /* Setup the spinner on this dialog needed for entering the name of the Person
        * responsible for the payment. */
        if ( personSpinner != null ) {
            String [ ] persons = { getString( R.string.first ), getString( R.string.second ) };
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    AddPaymentDialog.this.getActivity(),
                    R.layout.support_simple_spinner_dropdown_item, persons );

            personSpinner.setAdapter( spinnerAdapter );
            personSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AddPaymentDialog.this.personSelection = spinnerAdapter.getItem( position );
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    AddPaymentDialog.this.personSelection = spinnerAdapter.getItem( 0 );
                }
            });

        }

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
                        return;
                    }
                    AddPaymentDialog.this.expenseAmount = new Money ( value );
                }

                if ( enterDescr != null ) {
                    String s = enterDescr.getText().toString();
                    if ( s.length()  == 0 ) {
                        return;
                    }
                    AddPaymentDialog.this.description = s;
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
        return new Payment( this.description, this.expenseAmount, this.expenseDate);
    }

    /**
     * Get access to the {@link String} representing the {@link Person} that has made the {@link Payment}
     * specified on this {@link AddPaymentDialog}.
     * @return the {@link String} representation of the associated {@link Person}.
     */
    public String getSelectedPerson ( ) {
        if ( this.personSelection == null ) {
            this.personSelection = getString( R.string.first );
        }
        return this.personSelection;
    }
}
