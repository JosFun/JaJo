package com.funk.jajo.customtypes;

import java.util.ArrayList;
import java.util.Date;

public class Person implements Comparable<Person>{

    private Name name;
    private ArrayList<Payment> payments;

    public Person ( Name name ) {
        this.name = name;
    }

    public void addPayment ( Payment p ) {
        this.payments.ensureCapacity( this.payments.size() + 1 );
        this.payments.add ( p );
    }

    public void deletePayment ( Payment p ) {
        this.payments.remove ( p );
    }
    /**
     * Get total sum of payments this {@link Person} has done.
     * @return The total sum of payments this {@link Person} has done.
     */
    public double getTotalPayments ( ) {
        double sum = 0;
        for ( Payment p: payments ) {
            sum += p.getAmount();
        }

        return sum;
    }

    /**
     * Get total sum of payments that happended between the two specified dates.
     * @param d1 - The first date
     * @param d2 - The second date
     * @return The total sum of payments having happened between the two specified dates.
     */
    public double getTotalPayments (Date d1, Date d2) {
       if ( d2.compareTo(d1) >= 0 ) return 0;

       double sum = 0;
       for ( Payment p: this.payments ) {
           if ( p.getDate().compareTo( d1 ) >= 0 && p.getDate().compareTo( d2 ) <= 0 ) {
                sum += p.getAmount();
           }
       }

       return sum;
    }

    /**
     * Get access to all the {@link Payment}s having been done by this {@link Person}
     * @return The {@link ArrayList} of {@link Payment}s associated with this {@ink Person}
     */
    public ArrayList<Payment> getPayments ( ) {
        return new ArrayList<>( this.payments);
    }

    /**
     * Get access to all the {@link Payment}s having been done by this {@link Person} between
     * the two specified dates
     * @param d1 - The {@link Date} marking the beginning of the relevant period of time.
     * @param d2 - The {@link Date} marking the end of the relevant period of time.
     * @return The {@link ArrayList} of {@link Payment}s associated with this {@ink Person}
     */
    public ArrayList<Payment> getPayments ( Date d1, Date d2 ) {
        ArrayList<Payment> payments = new ArrayList<>();

        for ( Payment p: this.payments ) {
            if ( p.getDate().compareTo( d1 ) >= 0 && p.getDate().compareTo( d2 ) <= 0 ) {
                payments.ensureCapacity( payments.size() + 1 );
                payments.add ( p );
            }
        }

        return payments;
    }

    @Override
    public int compareTo(Person o) {
        if ( o == null ) return 1;

        return 0;
    }

    public enum Name {
        JOSH, JASMIN
    };


}
