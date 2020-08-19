package com.funk.jajo.customtypes;

/**
 * Represents a change, having been applied to some kind of data within the application.
 */
public abstract class Change {
    /**
     * The {@link Action} that is performed by this {@link Change}.
     */
    private Action action;

    public Change ( ) {

    }

    public Change ( Action performedAction ) {
        this.action = performedAction;
    }

    /**
     * Get access to the {@link Action} having been performed in this {@link Change}.
     * @return The {@link Action} having been performed
     */
    public Action getAction ( ) { return this.action; }
}
