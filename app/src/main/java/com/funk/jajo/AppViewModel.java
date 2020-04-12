package com.funk.jajo;

import android.arch.lifecycle.ViewModel;

import com.funk.jajo.customtypes.Person;

public class AppViewModel extends ViewModel {

    private AppBarActivity.FragmentType currentFragment;

    /**
     * Specifies what percentage of the overall sum the first person has to pay.
     * */
    private double shareRatio = 0.4;

    private Person first;

    private Person second;

    public AppBarActivity.FragmentType getCurrentFragment ( ) {
        return this.currentFragment;
    }

    public void setSecond(Person second ) {
        this.second = second;
    }

    public void setFirst(Person first) {
        this.first = first;
    }

    public void setCurrentFragment ( AppBarActivity.FragmentType type ) {
        this.currentFragment = type;
    }

    public Person getSecond( ) { return this.second; }

    public Person getFirst( ) { return this.first; }

    public double getShareRatio (){ return this.shareRatio; }
}
