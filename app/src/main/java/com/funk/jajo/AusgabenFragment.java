package com.funk.jajo;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.funk.jajo.customtypes.Change;
import com.funk.jajo.customtypes.Changelog;
import com.funk.jajo.customtypes.DialogListener;
import com.funk.jajo.customtypes.Person;
import com.funk.jajo.dialogs.AddPaymentDialog;
import com.funk.jajo.recycleradapters.PaymentListAdapter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

public class AusgabenFragment extends Fragment implements DialogListener {

    private View fragView = null;

    private AppViewModel viewModel = null;

    private RecyclerView recyclerFirst = null;
    private RecyclerView recyclerSecond = null;

    private PaymentListAdapter paymentAdapterFirst = null;
    private PaymentListAdapter paymentAdapterSecond = null;

    private void updateNextPayer ( ) {
        TextView nextPayer = this.fragView.findViewById(R.id.next_payer);
        if ( nextPayer == null ) return;
        double sum =
                viewModel.getFirst().getTotalPayments() + viewModel.getSecond().getTotalPayments();
        if ( viewModel.getFirst().getTotalPayments() < 0.4 * sum ) {
            nextPayer.setText(viewModel.getFirst().getName() + " zahlt!");
            nextPayer.setTextColor(ContextCompat.getColor( this.getContext(), R.color.colorFirst));
        } else {
            nextPayer.setText ( viewModel.getSecond().getName() + " zahlt!");
            nextPayer.setTextColor( ContextCompat.getColor( this.getContext(), R.color.colorSecond));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.fragView = inflater.inflate(R.layout.fragment_ausgaben, container, false);

        if ( this.getActivity() != null ) {
            this.viewModel = ViewModelProviders.of ( this.getActivity()).get(AppViewModel.class);
        }

        FloatingActionButton addButton = this.fragView.findViewById(R.id.floating_action_expenses);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPaymentDialog dialog = new AddPaymentDialog();
                dialog.setListener( AusgabenFragment.this );
                dialog.show( AusgabenFragment.this.getFragmentManager(), "Create new payment");
            }
        });

        this.recyclerFirst = this.fragView.findViewById( R.id.first_list);
        this.recyclerSecond = this.fragView.findViewById( R.id.second_list );
        this.paymentAdapterFirst = new PaymentListAdapter( this.viewModel.getFirst(), this.getContext() );
        this.paymentAdapterSecond = new PaymentListAdapter( this.viewModel.getSecond(), this.getContext());

        LinearLayoutManager llm1 = new LinearLayoutManager(this.getContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerFirst.setLayoutManager(llm1);

        LinearLayoutManager llm2 = new LinearLayoutManager(this.getContext());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerSecond.setLayoutManager(llm2);

        this.recyclerFirst.setAdapter( paymentAdapterFirst );
        this.recyclerSecond.setAdapter( paymentAdapterSecond );

        this.updateNextPayer();

        return this.fragView;
    }

    /**
     * Once the {@link AddPaymentDialog} calls this method on its listener {@link AusgabenFragment},
     * assign the selected {@link com.funk.jajo.customtypes.Payment} on it to the correct {@link Person}.
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if ( dialog instanceof  AddPaymentDialog ) {
            if ( this.viewModel != null ) {
                AddPaymentDialog paymentDialog = ( AddPaymentDialog ) dialog;

                /*
                 * Add the dialog's info to a the payment list of the associated person
                 */
                if ( paymentDialog.getSelectedPerson().equals( this.viewModel.getFirst().getName())) {
                    this.viewModel.getFirst().addPayment( paymentDialog.getExpense());
                    this.paymentAdapterFirst.updateData();
                } else {
                    this.viewModel.getSecond().addPayment( paymentDialog.getExpense());
                    this.paymentAdapterSecond.updateData();
                }

                /*
                 * Add a changelog entry in the changelog of the local device.
                 */
                Change change = paymentDialog.getPaymentChange();
                Changelog localChanges = this.viewModel.getLocalChanges();

                if ( change != null && localChanges != null ) {
                    localChanges.addChange( change );
                }

                this.updateNextPayer ( );
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}