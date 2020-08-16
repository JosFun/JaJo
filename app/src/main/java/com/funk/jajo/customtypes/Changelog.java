package com.funk.jajo.customtypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
    private Queue<Change> changes;

    public Changelog ( String origin ) {
        this.originHost = origin;
    }

    public String getOriginHost ( ) {
        return this.originHost;
    }

    public Queue<Change> getChanges ( ) {
        if ( this.changes == null ) {
            return new LinkedList<Change>( );
        } else {
            return this.changes;
        }

    }
}
