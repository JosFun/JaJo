package com.funk.jajo.recycleradapters;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funk.jajo.R;
import com.funk.jajo.customtypes.Money;
import com.funk.jajo.customtypes.Payment;
import com.funk.jajo.customtypes.Person;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_HEADER_ITEM = 0;
    private static final int VIEW_TYPE_LIST_ITEM = 1;

    private Person person;
    private Context context;
    private ArrayList<Payment> selectedPayments;

    private Date start, end;
    private boolean dateFilteringActive = false;

    private boolean isPositionHeader ( int position ) {
        return ( position == 0);
    }


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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if ( viewType == VIEW_TYPE_HEADER_ITEM ) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_item,
                    viewGroup, false );
            return new HeaderItem(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_row,
                    viewGroup, false );
            return new ListItem(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vH, int i) {
        if ( i >= this.selectedPayments.size() + 1 ) return;

        Payment selected = null;
        if ( i >= 1 ) {
            selected = this.selectedPayments.get ( i - 1 );
            if ( selected == null ) return;
        }

        if ( vH instanceof ListItem ) {
            ListItem listItem = (ListItem) vH;

            if ( listItem.money != null ) {
                listItem.money.setText( Person.makeToCentsFormat( selected.getMoney()) );
            }
            if ( listItem.description != null ) {
                listItem.description.setText( selected.getDescription());
            }
            if ( listItem.date != null ) {
                listItem.date.setText( selected.getDateString() );
            }
            if ( listItem.card != null ) {
                if ( this.person.getName().equals ( this.context.getString( R.string.first))) {
                    listItem.card.setCardBackgroundColor(ContextCompat.getColor( this.context, R.color.colorFirst));
                    listItem.originalColor = ContextCompat.getColor( this.context, R.color.colorFirst);
                    listItem.currentColor = listItem.originalColor;

                } else {
                    listItem.card.setCardBackgroundColor(ContextCompat.getColor( this.context, R.color.colorSecond));
                    listItem.originalColor = ContextCompat.getColor( this.context, R.color.colorSecond);
                    listItem.currentColor = listItem.originalColor;

                }
            }
        } else if ( vH instanceof HeaderItem ){
            HeaderItem headerItem = ( HeaderItem ) vH;

            if ( headerItem.constraint != null && headerItem.card != null) {
                if ( this.person.getName().equals( this.context.getString(R.string.first))) {
                    headerItem.constraint.setBackgroundColor(ContextCompat.getColor( this.context, R.color.colorFirst));
                    headerItem.card.setCardBackgroundColor(ContextCompat.getColor( this.context, R.color.colorAccent));
                } else {
                    headerItem.constraint.setBackgroundColor( ContextCompat.getColor( this.context, R.color.colorSecond));
                    headerItem.card.setCardBackgroundColor(ContextCompat.getColor( this.context, R.color.colorAccent));
                }
            }

            if ( headerItem.name != null ) {
                headerItem.name.setText( this.person.getName() );
            }

            if ( headerItem.money != null ) {
                headerItem.money.setText( Person.makeToCentsFormat( new Money( person.getTotalPayments())));
            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return VIEW_TYPE_HEADER_ITEM;
        }
        return VIEW_TYPE_LIST_ITEM;
    }

    @Override
    public int getItemCount() {
        if ( this.selectedPayments == null ) return 1;
        else return this.selectedPayments.size() + 1;
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

    public static class ListItem extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView description;
        public TextView money;
        public TextView date;

        public int currentColor;
        public int originalColor;

        public ListItem(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.list_item_descr);
            this.card = itemView.findViewById(R.id.list_item_card);
            this.money = itemView.findViewById( R.id.list_item_money );
            this.date = itemView.findViewById( R.id.list_item_date );

        }
    }

    public static class HeaderItem extends RecyclerView.ViewHolder {

        protected CardView card;
        protected TextView name;
        protected TextView money;
        protected ConstraintLayout constraint;

        public HeaderItem(@NonNull View itemView) {
            super(itemView);
            this.card = itemView.findViewById(R.id.header_item_card);
            this.name = itemView.findViewById(R.id.header_item_person);
            this.money = itemView.findViewById(R.id.header_item_sum);
            this.constraint = itemView.findViewById(R.id.header_constraint);
        }


    }
}
