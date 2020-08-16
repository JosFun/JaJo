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

    /**
     * Create a new PaymnentChange by specifying all necessary parameter values
     * @param performedAction The {@link Action} that has been performed
     * @param person The name of the {@link Person} being associated with this {@link PaymentChange}
     * @param newDescription The new description of the {@link Payment}
     * @param expenseAmount The {@link Money} value representing the changed expense amount
     * @param date The updated date in form of a {@link Calendar}
     */
    public PaymentChange ( Action performedAction, String person, String newDescription, Money expenseAmount, Calendar date ) {
        super ( performedAction);
        this.personName = person;
        this.description = newDescription;
        this.amount = expenseAmount;
        this.calendar = date;
    }

    /**
     * Create a new {@link PaymentChange} that deletes the {@link Payment} with the
     * specified properties
     * @param person The name of the {@link Person} being associated with this {@link PaymentChange}
     * @param toBeDeleted The {@link Payment} that is to be deleted.
     */
    public PaymentChange ( String person,Payment toBeDeleted )  {
        super ( Action.DELETE );
        this.personName = person;
        this.description = toBeDeleted.getDescription();
        this.amount = toBeDeleted.getMoney();
        this.calendar = toBeDeleted.getCalendar();
    }


}
