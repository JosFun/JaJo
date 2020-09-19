package com.funk.jajo.customtypes;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents all the paymentChanges that have been applied to the online version of {@link FTPStorable}
 * and
 */
public class Changelog {
    /**
     * The HostName of the device that uploaded this {@link Changelog}
     */
    private String originHost;

    /**
     * The Queue containing all the {@link Change}s that still need to be corrected.
     */
    private Queue<PaymentChange> paymentChanges;


    public Changelog ( String origin ) {
        this.originHost = origin;
        this.paymentChanges = new LinkedList<PaymentChange>();
    }

    public String getOriginHost ( ) {
        return this.originHost;
    }

    public void addPaymentChange( PaymentChange ch ) {
        if ( this.paymentChanges == null ) {
            this.paymentChanges = new LinkedList<>();
        }
        this.paymentChanges.add( ch );
    }

    public Queue<PaymentChange> getPaymentChanges( ) {
        if ( this.paymentChanges == null ) {
            return new LinkedList<PaymentChange>( );
        } else {
            return this.paymentChanges;
        }

    }
}
