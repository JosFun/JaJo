package com.funk.jajo;

import android.arch.lifecycle.ViewModel;

import com.funk.jajo.customtypes.Person;

public class AppViewModel extends ViewModel {

    private AppBarActivity.FragmentType currentFragment;

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
}
