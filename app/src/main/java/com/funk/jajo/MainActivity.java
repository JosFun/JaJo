package com.funk.jajo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.funk.jajo.customtypes.FTPStorable;
import com.funk.jajo.customtypes.FTPStorer;
import com.funk.jajo.customtypes.Person;
import com.funk.jajo.customtypes.PersonStorer;

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

        p1.sortByDate();
        p2.sortByDate();

        p1.correct();
        p2.correct();

        if ( p1 != null && p2 != null ) {
            this.viewModel.setFirst( p1 );
            this.viewModel.setSecond( p2 );
        }
    }

    private void storeData( ) {
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

        /* Try to load existing persons from the phone's storage */
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
