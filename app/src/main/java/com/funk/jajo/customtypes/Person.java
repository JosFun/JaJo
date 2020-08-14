package com.funk.jajo.customtypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Person implements Comparable<Person>{

    private String name;
    private ArrayList<Payment> payments;

    public static String makeToCentsFormat ( Money money ) {
        String s = "";
        try {
            s = Double.toString( money.getValue() );
            char [ ] ar = s.toCharArray();
            if ( ar [ ar.length - 2] == '.') {
                s += "0";
            }
        } catch ( NumberFormatException e ) {
            return "0.00€";
        }

        return s + "€";
    }

    public Person ( String name ) {
        this.name = name;
        this.payments = new ArrayList<>();
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

        return new Money (sum).getValue();
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

       return new Money(sum).getValue();
    }

    /**
     * Get access to all the {@link Payment}s having been done by this {@link Person}
     * @return The {@link ArrayList} of {@link Payment}s associated with this {@ink Person}
     */
    public ArrayList<Payment> getPayments ( ) {
        if ( this.payments == null || this.payments.size() == 0 ) {
            return new ArrayList<>();
        }
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

    public Person merge ( Person p ) {
        if ( !p.getName().equals(this.name)) {
            return this;
        }

        for ( Payment payment: p.payments ) {
            if ( !this.payments.contains( payment)) {
                this.payments.add ( payment );
                this.sortByDate();
            }
        }

        return this;
    }

    public void sortByDate ( ) {
        Collections.sort(this.payments, new Comparator<Payment>() {
            @Override
            public int compare(Payment o1, Payment o2) {
                return o1.compareTo(o2);
            }
        });
    }

    /**
     * This method is to be used if data is to be changed or deleted as long as editing
     * isn't possible yet.
     */
    public void correct ( ) {

    }

    public String getName( ) {
        return this.name;
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
