package com.funk.jajo.recycleradapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.funk.jajo.R;

public class ListItem extends RecyclerView.ViewHolder {
    protected TextView description;
    protected TextView money;
    protected TextView date;

    public ListItem(@NonNull View itemView) {
        super(itemView);
        this.description = itemView.findViewById( R.id.list_item_descr );
        this.money = itemView.findViewById( R.id.list_item_money );
        this.date = itemView.findViewById( R.id.list_item_date );
    }
}
