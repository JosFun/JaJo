package com.funk.jajo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.funk.jajo.customtypes.Changelog;
import com.funk.jajo.customtypes.ChangelogStorable;
import com.funk.jajo.customtypes.ChangelogStorer;
import com.funk.jajo.customtypes.FTPChangelogStorer;
import com.funk.jajo.customtypes.FTPStorable;
import com.funk.jajo.customtypes.FTPStorer;
import com.funk.jajo.customtypes.Person;
import com.funk.jajo.customtypes.PersonStorer;

import java.security.InvalidParameterException;

public class MainActivity extends AppBarActivity {
    private static final String FIRST_SUFFIX = "_FIRST";
    private static final String SECOND_SUFFIX = "_SECOND";

    private AusgabenFragment ausgabenFragment;
    private EinkaufslisteFragment listenFragment;
    private AppViewModel viewModel;

    private void loadData( ) {
        FTPStorable loadedData = null;
        /* First, check whether or not we can load already existing data from the internet.*/
        if (FTPStorer.checkForInternetConnection( this.getApplicationContext())) {
            FTPStorer storer = new FTPStorer( this.getApplicationContext());
            loadedData = storer.getFTPStorable();
        }

        Person onlineFirst = null;
        Person onlineSecond = null;
        Person offlineFirst = null;
        Person offlineSecond = null;


        /* If an FTPStorable could have been fetched from the server: Load its data into the app.*/
        if ( loadedData != null ) {
            onlineFirst = loadedData.getFirst();
            onlineSecond = loadedData.getSecond();
        }

        /* Look for existing offline data on the device.*/
        {
            SharedPreferences sP = this.getSharedPreferences(getString(R.string.PERSON_SHARED_PREF), MODE_PRIVATE);
            String loadKey1 = getString(R.string.PERSON_KEY) + FIRST_SUFFIX;
            String loadKey2 = getString(R.string.PERSON_KEY) + SECOND_SUFFIX;

            PersonStorer ps1 = new PersonStorer(loadKey1, sP);
            PersonStorer ps2 = new PersonStorer(loadKey2, sP);

            offlineFirst = ps1.loadPerson();
            offlineSecond = ps2.loadPerson();
        }

        Person p1 = null;
        Person p2 = null;

        if ( ( onlineFirst == null || onlineSecond == null) && ( offlineFirst == null || offlineSecond == null ) ) {
            return;
        } else if ( onlineFirst == null || onlineSecond == null ) {
            p1 = offlineFirst;
            p2 = offlineSecond;
        } else if ( offlineFirst == null || offlineSecond == null ) {
            p1 = onlineFirst;
            p2 = onlineSecond;
            /* A local and an online version could have been fetched. Compare them and merge!*/
        } else {
            p1 = onlineFirst.merge ( offlineFirst);
            p2 = onlineSecond.merge( offlineSecond);
        }

        if ( this.viewModel == null ) {
            this.viewModel = ViewModelProviders.of ( this ).get( AppViewModel.class );
        }

        if ( p1 != null && p2 != null ) {
            this.viewModel.setFirst( p1 );
            this.viewModel.setSecond( p2 );
        }
    }

    /**
     * Load both the remote device's changelog that is being stored online and the local device's
     * changelog being stored locally
     * @return the remote device's changelog data
     */
    private void loadChangelogData ( ) {
        ChangelogStorable data = null;
        /*
            Check whether or not we can connect to the ftp server.
        */
        if ( FTPChangelogStorer.checkForInternetConnection( this.getApplicationContext())) {
            FTPChangelogStorer storer = new FTPChangelogStorer( this.getApplicationContext());
            data = storer.getStorable();
        }

        Changelog localChangelog = null;
        Changelog remoteChangelog = null;

        /* If data is valid, try to get access to the remote device's changelog by passing the name
        * of the local device to the Getter method of the data.*/
        if ( data != null ) {
            remoteChangelog = data.getRemoteChangelog( this.viewModel.getDeviceName());
        }

        /*
        * Load a potentially existing version of the local changelog from the device's storage */
        {
            SharedPreferences sP = this.getSharedPreferences( getString ( R.string.CHANGELOG_SHARED_PREF), MODE_PRIVATE );
            String loadKey = getString(R.string.CHANGELOG_KEY);

            /* Instantiate a new ChangelogStorer that works with the appropriate loadKey and
            * SharedPreferences */
            ChangelogStorer cStorer = new ChangelogStorer( loadKey, sP );

            /* Get the ChangelogStorable from the device's storage. */
            ChangelogStorable offlineStorable = cStorer.loadChangelogStorable();

            try {
                localChangelog = offlineStorable.getLocalChangelog( this.viewModel.getDeviceName());
            } catch ( InvalidParameterException e ) {
                e.printStackTrace();
                localChangelog = new Changelog( this.viewModel.getDeviceName());
            }

            if ( localChangelog == null ) {
                localChangelog = new Changelog( ( this.viewModel.getDeviceName()));
            }
        }

        /* Set up the local Changelog and the remoteChangelog within the app's viewModel. */
        this.viewModel.setLocalChanges( localChangelog );
        this.viewModel.setRemoteChanges( remoteChangelog );
    }

    private void storeData( ) {
        /*
            First of all, load the remote data from the server
        */
        this.loadData();
        if ( this.viewModel != null && this.viewModel.getSecond() != null && this.viewModel.getSecond() != null ) {
            {
                /* First: store it locally */
                SharedPreferences sP = this.getSharedPreferences(getString(R.string.PERSON_SHARED_PREF), MODE_PRIVATE);
                PersonStorer pS1 = new PersonStorer(this.viewModel.getFirst(),
                        getString(R.string.PERSON_KEY) + FIRST_SUFFIX, sP);
                PersonStorer pS2 = new PersonStorer(this.viewModel.getSecond(),
                        getString(R.string.PERSON_KEY) + SECOND_SUFFIX, sP);
            }
            /* Afterwards: Store it on the ftp server */
            {
                FTPStorer storer = new FTPStorer( new FTPStorable( this.viewModel.getFirst(), this.viewModel.getSecond()), this.getApplicationContext());
            }
        }

    }

    /**
     * Store the current ChangelogData both online and locally
     */
    private void storeChangelogData ( ) {

        if ( this.viewModel != null && this.viewModel.getLocalChanges() != null
                && this.viewModel.getRemoteChanges() != null ) {
            ChangelogStorable toBeStored = new ChangelogStorable(
                    this.viewModel.getLocalChanges(), this.viewModel.getRemoteChanges() );

            /* First: Store it locally */
            SharedPreferences sP = this.getSharedPreferences(
                    getString ( R.string.CHANGELOG_SHARED_PREF), MODE_PRIVATE);
            ChangelogStorer cStorer = new ChangelogStorer( toBeStored,
                    getString(R.string.CHANGELOG_KEY), sP );

            /* Afterwards: Store it on the ftp server */
            {
                FTPChangelogStorer storer = new FTPChangelogStorer( toBeStored, this.getApplicationContext());
            }
        }


    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if ( MainActivity.this.viewModel.getCurrentFragment() == FragmentType.AUSGABEN ) return true;

                    MainActivity.this.viewModel.setCurrentFragment( FragmentType.AUSGABEN );
                    MainActivity.this.setToolBarText( FragmentType.AUSGABEN );
                    MainActivity.this.fragmentTransition( R.id.frame_layout, MainActivity.this.ausgabenFragment, 0 );
                    return true;
                case R.id.navigation_dashboard:
                    if ( MainActivity.this.viewModel.getCurrentFragment() == FragmentType.EINKAUFSLISTE ) return true;
                    MainActivity.this.viewModel.setCurrentFragment( FragmentType.EINKAUFSLISTE );
                    MainActivity.this.setToolBarText( FragmentType.EINKAUFSLISTE );
                    MainActivity.this.fragmentTransition( R.id.frame_layout, MainActivity.this.listenFragment, 1 );
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.createFragments();
        this.appBarPresent = this.setAppBar();

        this.viewModel = ViewModelProviders.of ( this ).get( AppViewModel.class );
        this.viewModel.setCurrentFragment( FragmentType.AUSGABEN );

        /* Try to load existing persons from both the phone's storage and the ftp server */
        this.loadData();

        if ( this.viewModel.getFirst() == null && this.viewModel.getSecond() == null ) {
            /* Create the two persons that use this app in order to share their payments with each other. */
            this.viewModel.setFirst( new Person( getString ( R.string.first)));
            this.viewModel.setSecond( new Person ( getString ( R.string.second)));
        }

        this.setToolBarText( this.viewModel.getCurrentFragment());

        if ( this.viewModel.getCurrentFragment() == FragmentType.AUSGABEN ) {
            this.fragmentTransition( R.id.frame_layout, this.ausgabenFragment, 0 );
        } else if ( this.viewModel.getCurrentFragment() == FragmentType.EINKAUFSLISTE ) {
            this.fragmentTransition( R.id.frame_layout, this.listenFragment, 1 );
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void createFragments() {
        this.ausgabenFragment = new AusgabenFragment();
        this.listenFragment = new EinkaufslisteFragment();
    }

    @Override
    public void onPause ( ) {
        super.onPause();
        /* Store the Persons on the phone. */
        this.storeData();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.app_bar_menu, menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        switch ( item.getItemId()) {
            case R.id.settings_button:
                /* Show settings menu */
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

}
