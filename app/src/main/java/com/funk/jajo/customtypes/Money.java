package com.funk.jajo.customtypes;

public class Money {
    private double value;

    public Money ( double value ) {
        this.value = Math.round ( 100 * value ) / 100.0;
    }

    public Money( Money m ) {
        this.value = m.value;
    }

    public double getValue ( ) { return this.value; }
}
