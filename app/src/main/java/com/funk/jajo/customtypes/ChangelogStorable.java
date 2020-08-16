package com.funk.jajo.customtypes;

/**
 * Represents the Changelog that is to be stored on the ftp server.
 */
public class ChangelogStorable {
    private Changelog firstChangelog;
    private Changelog secondChangelog;

    public ChangelogStorable ( Changelog first, Changelog second ) {
        this.firstChangelog = first;
        this.secondChangelog = second;
    }
}
