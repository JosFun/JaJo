package com.funk.jajo.customtypes;

/**
 * Represents a change, having been applied to some kind of data within the application.
 */
public abstract class Change {
    /**
     * The {@link Action} that is performed by this {@link Change}.
     */
    private Action action;

    public Change ( Action performedAction ) {
        this.action = performedAction;
    }
}
