package com.funk.jajo;

import android.arch.lifecycle.ViewModel;

public class AppViewModel extends ViewModel {

    private AppBarActivity.FragmentType currentFragment;

    public AppBarActivity.FragmentType getCurrentFragment ( ) {
        return this.currentFragment;
    }

    public void setCurrentFragment ( AppBarActivity.FragmentType type ) {
        this.currentFragment = type;
    }
}
