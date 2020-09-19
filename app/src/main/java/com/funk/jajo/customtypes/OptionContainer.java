package com.funk.jajo.customtypes;

/**
 * Encapsulates a selected option by storing the updated value for a specific option.
 * @param <T> The type of the new value for a specific option
 */
public class OptionContainer<T> {
    /**
     * The contained new value for the option
     */
    private T contained;

    /**
     * Create a new OptionContainer by specifying the value that is going to be contained
     * @param toBeContained
     */
    public OptionContainer ( T toBeContained ) {
        this.contained = toBeContained;
    }

    /**
     * Get access to the contained new value
     * @return the new value for the option
     */
    public T getContained ( ) {
        return this.contained;
    }
}
