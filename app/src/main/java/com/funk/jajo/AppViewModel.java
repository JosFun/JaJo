package com.funk.jajo;

import android.arch.lifecycle.ViewModel;

import com.funk.jajo.customtypes.Person;

public class AppViewModel extends ViewModel {

    private AppBarActivity.FragmentType currentFragment;

    private Person jasmin;

    private Person joshua;

    public AppBarActivity.FragmentType getCurrentFragment ( ) {
        return this.currentFragment;
    }

    public void setJoshua ( Person josh ) {
        this.joshua = josh;
    }

    public void setJasmin ( Person jasmin ) {
        this.jasmin = jasmin;
    }

    public void setCurrentFragment ( AppBarActivity.FragmentType type ) {
        this.currentFragment = type;
    }
}
