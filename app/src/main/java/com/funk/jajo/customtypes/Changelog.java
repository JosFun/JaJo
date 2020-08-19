package com.funk.jajo.customtypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Represents all the changes that have been applied to the online version of {@link FTPStorable}
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
    private Queue<PaymentChange> changes;

    public Changelog ( String origin ) {
        this.originHost = origin;
        this.changes = new LinkedList<PaymentChange>();
    }

    public String getOriginHost ( ) {
        return this.originHost;
    }

    public void addChange ( PaymentChange ch ) {
        if ( this.changes == null ) {
            this.changes = new LinkedList<>();
        }
        this.changes.add( ch );
    }

    public Queue<PaymentChange> getChanges ( ) {
        if ( this.changes == null ) {
            return new LinkedList<PaymentChange>( );
        } else {
            return this.changes;
        }

    }
}
