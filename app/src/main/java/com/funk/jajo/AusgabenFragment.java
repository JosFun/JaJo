package com.funk.jajo;

import android.app.ListActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        /* Set up ItemTouchHelpers in order to react to swipe events */
        ItemTouchHelper.SimpleCallback swipeCallBackFirst = new ItemTouchHelper.SimpleCallback(
                0 , ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int dir) {
                Toast.makeText(AusgabenFragment.this.getActivity(), "Gelöscht", Toast.LENGTH_SHORT).show();

                /* Get the position of the swiped ViewHolder, there is an offset of 1 because
                of the HeaderItem */
                int position = viewHolder.getAdapterPosition() - 1;
                /* Delete the item from the viewHolder data */
                AusgabenFragment.this.viewModel.getFirst().deletePayment( position );

                AusgabenFragment.this.paymentAdapterFirst.updateData();

            }

            /**
             * Once a ListItem is started to being swiped, its color will turn into Red.
             * @param viewHolder The ViewHolder being swiped
             * @param actionState The Action being performed on the viewHolder
             */
            @Override
            public void onSelectedChanged( RecyclerView.ViewHolder viewHolder, int actionState ) {
                if ( viewHolder instanceof PaymentListAdapter.ListItem ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        PaymentListAdapter.ListItem item = (PaymentListAdapter.ListItem) viewHolder;
                        if (item.currentColor == item.originalColor) {
                            item.card.setCardBackgroundColor(Color.RED);
                        }
                    }
                }
            }

            /**
             * This method is being invoked after the onSwiped method is finished.
             * It will change the color of the background of the viewHolder, so that if the swipe
             * has not been concluded, it will change its color back to its original color.
             * @param recycler The recycler this ItemTouchHelper is being attached to.
             * @param viewHolder The viewHolder that has been selected.
             */
            @Override
            public void clearView ( RecyclerView recycler, RecyclerView.ViewHolder viewHolder ) {
                if ( viewHolder instanceof PaymentListAdapter.ListItem ) {
                    PaymentListAdapter.ListItem item = (PaymentListAdapter.ListItem) viewHolder;
                    item.card.setCardBackgroundColor(item.originalColor);
                }
            }
        };

        /* Set up ItemTouchHelpers in order to react to swipe events */
        ItemTouchHelper.SimpleCallback swipeCallBackSecond = new ItemTouchHelper.SimpleCallback(
                0 , ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int dir) {
                Toast.makeText(AusgabenFragment.this.getActivity(), "Gelöscht", Toast.LENGTH_SHORT).show();

                /* Get the position of the swiped ViewHolder, there is an offset of 1 because
                of the HeaderItem */
                int position = viewHolder.getAdapterPosition() - 1;
                /* Delete the item from the viewHolder data */
                AusgabenFragment.this.viewModel.getSecond().deletePayment( position );

                AusgabenFragment.this.paymentAdapterSecond.updateData();

            }

            /**
             * Once a ListItem is started to being swiped, its color will turn into Red.
             * @param viewHolder The ViewHolder being swiped
             * @param actionState The Action being performed on the viewHolder
             */
            @Override
            public void onSelectedChanged( RecyclerView.ViewHolder viewHolder, int actionState ) {
                if ( viewHolder instanceof PaymentListAdapter.ListItem ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        PaymentListAdapter.ListItem item = (PaymentListAdapter.ListItem) viewHolder;
                        if (item.currentColor == item.originalColor) {
                            item.card.setCardBackgroundColor(Color.RED);
                        }
                    }
                }
            }

            /**
             * This method is being invoked after the onSwiped method is finished.
             * It will change the color of the background of the viewHolder, so that if the swipe
             * has not been concluded, it will change its color back to its original color.
             * @param recycler The recycler this ItemTouchHelper is being attached to.
             * @param viewHolder The viewHolder that has been selected.
             */
            @Override
            public void clearView ( RecyclerView recycler, RecyclerView.ViewHolder viewHolder ) {
                if ( viewHolder instanceof PaymentListAdapter.ListItem ) {
                    PaymentListAdapter.ListItem item = (PaymentListAdapter.ListItem) viewHolder;
                    item.card.setCardBackgroundColor(item.originalColor);
                }
            }
        };

        ItemTouchHelper swipeHelperFirst = new ItemTouchHelper( swipeCallBackFirst );
        ItemTouchHelper swipeHelperSecond = new ItemTouchHelper ( swipeCallBackSecond );
        swipeHelperFirst.attachToRecyclerView(recyclerFirst);
        swipeHelperSecond.attachToRecyclerView(recyclerSecond);

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