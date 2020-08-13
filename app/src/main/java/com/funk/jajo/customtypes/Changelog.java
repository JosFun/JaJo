package com.funk.jajo.customtypes;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Represents all the changes that have been applied to the online version of {@link FTPStorable}
 * and
 */
public class Changelog {
    private Queue<PaymentChange> paymentChanges;
    private Queue<ShoppingEntryChange> shoppingChanges;
}
