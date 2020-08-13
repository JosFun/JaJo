package com.funk.jajo.customtypes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Payment implements Comparable<Payment> {
    private Money amount;
    private String description;
    private Calendar calendar;

    public Payment ( String descr, double amount ) {
        this ( descr, amount, new Date( ));
    }

    public Payment ( String descr, double amount, Date date ) {
        this.amount = new Money( amount );
        this.calendar = Calendar.getInstance();
        this.calendar.setTime ( date );
    }

    public Payment ( String descr, Money amount, Calendar c ) {
        this.amount = new Money ( amount );
        this.description = descr;
        this.calendar = c;
    }

    public double getAmount ( ) { return this.amount.getValue(); }

    public Money getMoney ( ) { return this.amount; }

    public Date getDate ( ) { return this.calendar.getTime ( ); }

    public String getDateString ( ) {
        SimpleDateFormat df = new SimpleDateFormat( "dd.MM");
        df.setTimeZone(TimeZone.getDefault());
        String s = df.format ( this.calendar.getTime() );
        return s;
    }

    public String getDescription ( ) {
        return this.description;
    }

    @Override
    public int compareTo(Payment o) {
        if ( o == null ) return 1;

       else return o.calendar.compareTo(this.calendar);
    }

    /**
     * Two {@link Payment}s are equal, if their money amounts and their dates are equal.
     * @param o - An arbitrary Object
     * @return Whether or not this {@link Payment} and the other {@link Object} are equal.
     * */
    @Override
    public boolean equals ( Object o ) {
        if ( o == null ) return false;
        if ( o instanceof Payment) {
            return (( Payment ) o).amount.getValue() == this.amount.getValue()
                    && this.compareTo( (Payment)o) == 0;
        } else return false;
    }
}
