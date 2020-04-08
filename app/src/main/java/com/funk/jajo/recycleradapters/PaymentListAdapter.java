package com.funk.jajo.recycleradapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.funk.jajo.R;
import com.funk.jajo.customtypes.Payment;
import com.funk.jajo.customtypes.Person;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;

public class PaymentListAdapter extends RecyclerView.Adapter<ListItem>{

    private Person person;
    private ArrayList<Payment> selectedPayments;

    private Date start, end;
    private boolean dateFilteringActive = false;

    public PaymentListAdapter ( Person p ) {
        this.person = p;
        this.selectedPayments = person.getPayments();
    }

    public void enableDateFiltering ( Date start, Date end ) {
        this.dateFilteringActive = true;
        this.start = start;
        this.end = end;

        this.selectedPayments = person.getPayments( this.start, this.end );
    }

    public void disableDateFiltering ( ) {
        this.dateFilteringActive = false;
        this.selectedPayments = this.person.getPayments();
    }

    @NonNull
    @Override
    public ListItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_row,
                viewGroup, false );
        return new ListItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItem listItem, int i) {
        if ( i >= this.selectedPayments.size() ) return;

        Payment selected = this.selectedPayments.get ( i );

        if ( selected == null ) return;

        if ( listItem.description != null ) {
            listItem.description.setText( selected.getDescription() );
        }
        if ( listItem.money != null ) {
            listItem.money.setText( String.valueOf ( selected.getAmount()) );
        }
        if ( listItem.date != null ) {
            listItem.date.setText( selected.getDateString() );
        }

    }

    @Override
    public int getItemCount() {
        if ( this.selectedPayments == null ) return 0;
        else return this.selectedPayments.size();
    }
}
