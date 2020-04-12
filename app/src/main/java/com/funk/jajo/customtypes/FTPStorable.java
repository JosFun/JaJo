package com.funk.jajo.customtypes;

public class FTPStorable {
    private Person first;
    private Person second;

    public FTPStorable ( Person first, Person second ) {
        this.first = first;
        this.second = second;
    }

    public Person getFirst ( ) { return this.first; }
    public Person getSecond ( ) { return this.second; }
}
