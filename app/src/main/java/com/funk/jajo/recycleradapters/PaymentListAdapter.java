package com.funk.jajo.recycleradapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funk.jajo.R;
import com.funk.jajo.customtypes.Payment;
import com.funk.jajo.customtypes.Person;

import java.util.ArrayList;
import java.util.Date;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ListItem>{

    private Person person;
    private Context context;
    private ArrayList<Payment> selectedPayments;

    private Date start, end;
    private boolean dateFilteringActive = false;

    public PaymentListAdapter (Person p, Context c) {
        this.context = c;
        this.person = p;
        this.selectedPayments = person.getPayments();
    }

    public void enableDateFiltering ( Date start, Date end ) {
        this.dateFilteringActive = true;
        this.start = start;
        this.end = end;
    }

    public void disableDateFiltering ( ) {
        this.dateFilteringActive = false;
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

        if ( listItem.money != null ) {
            listItem.money.setText( String.valueOf ( selected.getAmount()) + "€" );
        }
        if ( listItem.description != null ) {
            listItem.description.setText( selected.getDescription());
        }
        if ( listItem.date != null ) {
            listItem.date.setText( selected.getDateString() );
        }
        if ( listItem.card != null ) {
            if ( this.person.getName().equals ( this.context.getString( R.string.first))) {
                listItem.card.setCardBackgroundColor(ContextCompat.getColor( this.context, R.color.colorPrimaryDark));
            } else {
                listItem.card.setCardBackgroundColor(ContextCompat.getColor( this.context, R.color.colorAccent));
            }
        }

    }

    @Override
    public int getItemCount() {
        if ( this.selectedPayments == null ) return 0;
        else return this.selectedPayments.size();
    }

    @UiThread
    public void updateData ( ) {
        if ( this.dateFilteringActive ) {
            this.selectedPayments = new ArrayList<>( person.getPayments( this.start, this.end ) );
        } else {
            this.selectedPayments = new ArrayList<>( person.getPayments() );
        }

        this.notifyDataSetChanged();
    }

    public class ListItem extends RecyclerView.ViewHolder {
        protected CardView card;
        protected TextView description;
        protected TextView money;
        protected TextView date;

        public ListItem(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.list_item_descr);
            this.card = itemView.findViewById(R.id.list_item_card);
            this.money = itemView.findViewById( R.id.list_item_money );
            this.date = itemView.findViewById( R.id.list_item_date );
        }
    }
}
