package com.funk.jajo.customtypes;

import java.security.InvalidParameterException;
import java.util.LinkedList;

/**
 * Represents the Changelog that is to be stored on the ftp server.
 */
public class ChangelogStorable {
    private Changelog firstChangelog;
    private Changelog secondChangelog;

    public ChangelogStorable(Changelog first, Changelog second) {
        this.firstChangelog = first;
        this.secondChangelog = second;
    }

    /**
     * Get access to the changelog of the remote device that is being stored online
     *
     * @param deviceName This device's name
     * @return The remote device's changelog
     * @throws InvalidParameterException
     */
    public Changelog getRemoteChangelog(String deviceName) {
        if (!this.firstChangelog.getOriginHost().equals(deviceName) &&
                this.secondChangelog.getOriginHost().equals(deviceName)) {
            return this.firstChangelog;
        } else if (!this.secondChangelog.getOriginHost().equals(deviceName) &&
                this.firstChangelog.getOriginHost().equals(deviceName)) {
            return this.secondChangelog;
        } else return null;
    }

    /**
     * Get access to the changelog of the local device that is supposedly being stored on the
     * local device
     * @param deviceName This device's name
     * @return The local device's changelog
     * @throws InvalidParameterException
     */
    public Changelog getLocalChangelog(String deviceName) {
        if (this.firstChangelog.getOriginHost().equals(deviceName) &&
                !this.secondChangelog.getOriginHost().equals(deviceName)) {
            return this.firstChangelog;
        } else if (this.secondChangelog.getOriginHost().equals(deviceName) &&
                this.firstChangelog.getOriginHost().equals(deviceName)) {
            return this.secondChangelog;
        } else return null;
    }
}
