package com.funk.jajo;

import androidx.lifecycle.ViewModel;

import com.funk.jajo.customtypes.Change;
import com.funk.jajo.customtypes.Changelog;
import com.funk.jajo.customtypes.PaymentChange;
import com.funk.jajo.customtypes.Person;
import com.google.firebase.auth.FirebaseUser;

public class AppViewModel extends ViewModel {

    /**
    * The name of the device, this app is running on.
    */
    private final String deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;

    /**
     * The {@link FirebaseUser} that is represented by this {@link AppViewModel}.
     */
    private FirebaseUser fBUser;

    private AppBarActivity.FragmentType currentFragment = AppBarActivity.FragmentType.NONE;

    /**
     * Specifies what percentage of the overall sum the first person has to pay.
     * */
    private double shareRatio = 0.4;

    private Person first;

    private Person second;

    /**
     * Represents all the changes that have been introduced on the local device but have not yet
     * been applied on the remote device.
     */
    private Changelog localChanges;

    /**
     * Represents all the changes that have been introduced on the remote device but have not yet
     * been applied on this local device.
     */
    private Changelog remoteChanges;

    public String getDeviceName ( ) {
        return this.deviceName;
    }

    public AppBarActivity.FragmentType getCurrentFragment ( ) {
        return this.currentFragment;
    }

    public void setSecond(Person second ) {
        this.second = second;
    }

    public void setFirst(Person first) {
        this.first = first;
    }

    public void setLocalChanges ( Changelog localChanges ) { this.localChanges = localChanges; }

    public void setRemoteChanges ( Changelog remoteChanges ) { this.remoteChanges = remoteChanges; }

    public void addLocalChange (PaymentChange localChange) {
        if ( this.localChanges == null ) {
            this.localChanges = new Changelog( this.deviceName );
        }
        this.localChanges.addChange( localChange );
    }

    public void setCurrentFragment ( AppBarActivity.FragmentType type ) {
        this.currentFragment = type;
    }

    public void setFireBaseUser ( FirebaseUser user ) {
        this.fBUser = user;
    }

    public void setShareRatio ( double newRatio ) { this.shareRatio = newRatio; }

    public String getUID( ) {
        if ( this.fBUser != null ) {
            return this.fBUser.getUid();
        } else return null;
    }

    public Person getSecond( ) { return this.second; }

    public Person getFirst( ) { return this.first; }

    public Changelog getLocalChanges ( ) {
        if ( this.localChanges == null ) {
            this.localChanges = new Changelog( this.getDeviceName());
        }
        return this.localChanges;
    }

    public Changelog getRemoteChanges ( ) {
        if ( this.remoteChanges == null ) {
            this.remoteChanges = new Changelog ( this.getDeviceName() );
        }
        return this.remoteChanges;
    }

    public double getShareRatio (){ return this.shareRatio; }
}
