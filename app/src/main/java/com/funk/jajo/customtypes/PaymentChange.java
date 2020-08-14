package com.funk.jajo.customtypes;

import java.util.Calendar;

/**
 * Represents a change, having been made the list of  payments of a {@link Person}.
 */
public class PaymentChange extends Change {
    /**
     * The {@link Person}, this {@link PaymentChange} is associated with.
     */
    private String personName;

    /**
     * The amount of {@link Money} associated with this {@link PaymentChange}.
     * */
    private Money amount;

    /**
     * The description associated with this {@link PaymentChange}.
     */
    private String description;

    /**
     * The date as an instance of {@Calendar} associated with this {@link PaymentChange}
     */
    private Calendar calendar;

    public PaymentChange ( Action performedAction, String person, String newDescription, Money expenseAmount, Calendar date ) {
        super ( performedAction);
        this.personName = person;
        this.description = newDescription;
        this.amount = expenseAmount;
        this.calendar = date;
    }



}
