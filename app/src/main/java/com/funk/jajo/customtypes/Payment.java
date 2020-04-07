package com.funk.jajo.customtypes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Payment implements Comparable<Payment> {
    private String description;
    private double amount;
    private Calendar calendar;

    public Payment ( String descr, double amount ) {
        this ( descr, amount, new Date( ));
    }

    public Payment ( String descr, double amount, Date date ) {
        this.description = descr;
        this.amount = amount;
        this.calendar = Calendar.getInstance();
        this.calendar.setTime ( date );
    }

    public double getAmount ( ) { return this.amount; }

    public String getDescription ( ) { return this.description; }

    public Date getDate ( ) { return this.calendar.getTime ( ); }

    public String getDateString ( ) {
        SimpleDateFormat df = new SimpleDateFormat( "dd-mm-yyyy");
        df.setTimeZone(TimeZone.getDefault());
        return df.format ( this.calendar.getTime() );
    }

    @Override
    public int compareTo(Payment o) {
        if ( o == null ) return 1;

       else return this.calendar.compareTo( o.calendar );
    }
}
