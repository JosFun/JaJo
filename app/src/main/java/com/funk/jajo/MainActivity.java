package com.funk.jajo;

import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.funk.jajo.Messaging.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.funk.jajo.customtypes.Action;
import com.funk.jajo.customtypes.Change;
import com.funk.jajo.customtypes.Changelog;
import com.funk.jajo.customtypes.ChangelogStorable;
import com.funk.jajo.customtypes.ChangelogStorer;
import com.funk.jajo.customtypes.FTPChangelogStorer;
import com.funk.jajo.customtypes.FTPStorable;
import com.funk.jajo.customtypes.FTPStorer;
import com.funk.jajo.customtypes.Payment;
import com.funk.jajo.customtypes.PaymentChange;
import com.funk.jajo.customtypes.Person;
import com.funk.jajo.customtypes.PersonStorer;
import com.funk.jajo.customtypes.ShoppingEntryChange;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.InvalidParameterException;

public class MainActivity extends AppBarActivity {
    private static final String FIRST_SUFFIX = "_FIRST";
    private static final String SECOND_SUFFIX = "_SECOND";

    private FirebaseAuth firebaseAuth;

    private AusgabenFragment ausgabenFragment;
    private EinkaufslisteFragment listenFragment;
    private SettingsFragment settingsFragment;
    private AppViewModel viewModel;
    private BottomNavigationView navigation;

    private NotificationBroadcastReceiver notificationBroadcastReceiver;

    protected void loadData( ) {
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

        if ( this.viewModel == null ) {
            this.viewModel = ViewModelProviders.of ( this ).get( AppViewModel.class );
        }

        if ( ( onlineFirst == null || onlineSecond == null) && ( offlineFirst == null || offlineSecond == null ) ) {
            return;
        } else if ( onlineFirst == null || onlineSecond == null ) {
            p1 = offlineFirst;
            p2 = offlineSecond;
        } else if ( offlineFirst == null || offlineSecond == null ) {
            p1 = onlineFirst;
            p2 = onlineSecond;
            /* A local and an online version could have been fetched. Therefore, we have to take
            * a look at the changelog in order to process the changes correctly. */
        } else {
            /* Start with the offline versions of both the offline persons. */
            p1 = offlineFirst;
            p2 = offlineSecond;

            /* Download both the remote and local changelog from the ftp server. */
            this.loadChangelogData();

            /* Take a look at the remote changelog*/
            Changelog remoteChangelog = this.viewModel.getRemoteChanges();

            /* Iterate through the remote changelog, inspect all the Changes and,
            depending on its type, insert the changes into the offline version of the person. */
            for ( Change c: remoteChangelog.getChanges() ) {
                if ( c instanceof PaymentChange ) {
                    PaymentChange p = ( PaymentChange ) c;
                    Payment pay = new Payment( p.getDescription(), p.getMoneyAmount(), p.getCalendar());

                    if ( p.getAction() == Action.ADD ) {

                        if ( p.getPersonName().equals( p1.getName()) && !p1.getPayments().contains(pay)) {
                            p1.addPayment(pay);
                        } else if ( !p2.getPayments().contains(pay)){
                            p2.addPayment(pay);
                        }
                    } else if ( p.getAction() == Action.DELETE )  {

                        Person payer = null;
                        if ( p.getPersonName().equals ( p1.getName())) {
                            payer = p1;
                        } else {
                            payer = p2;
                        }

                        payer.deletePayment( pay );
                    }
                } else if ( c instanceof ShoppingEntryChange ) {
                    /* TODO: Insert ShoppingEntryChanges into the Application. */
                }
            }
            /* After implementing all the changes in the local version of the Changelog, we can
            * clear the remoteChangelog stored on the device locally.*/
            remoteChangelog.getChanges().clear();

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

        if ( remoteChangelog == null ) {
            remoteChangelog = new Changelog ( this.viewModel.getDeviceName() );
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
            } catch ( NullPointerException n ) {
                n.printStackTrace();
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

    protected void storeData( ) {

        /* Then, store the persons on the device locally.*/
        if ( this.viewModel != null && this.viewModel.getFirst() != null && this.viewModel.getSecond() != null ) {
            {
                /* First: store it locally */
                SharedPreferences sP = this.getSharedPreferences(getString(R.string.PERSON_SHARED_PREF), MODE_PRIVATE);
                PersonStorer pS1 = new PersonStorer(this.viewModel.getFirst(),
                        getString(R.string.PERSON_KEY) + FIRST_SUFFIX, sP);
                PersonStorer pS2 = new PersonStorer(this.viewModel.getSecond(),
                        getString(R.string.PERSON_KEY) + SECOND_SUFFIX, sP);
            }
            /* Afterwards: Store both the persons on the ftp server */
            {
                FTPStorer storer = new FTPStorer( new FTPStorable( this.viewModel.getFirst(), this.viewModel.getSecond()), this.getApplicationContext());
            }
        }
        /* Finally, store the changelog data both locally and online. */
        this.storeChangelogData();
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
            for ( int i = 0; i < navigation.getMenu().size(); ++i ) {
                navigation.getMenu().getItem ( i ).setChecked( false );
            }

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if ( MainActivity.this.viewModel.getCurrentFragment() == FragmentType.AUSGABEN ) return true;
                    MainActivity.this.viewModel.setCurrentFragment( FragmentType.AUSGABEN );
                    MainActivity.this.setToolBarText( FragmentType.AUSGABEN );
                    item.setChecked(true);
                    MainActivity.this.fragmentTransition( R.id.frame_layout, MainActivity.this.ausgabenFragment, 0 );
                    return true;
                case R.id.navigation_dashboard:
                    if ( MainActivity.this.viewModel.getCurrentFragment() == FragmentType.EINKAUFSLISTE ) return true;
                    MainActivity.this.viewModel.setCurrentFragment( FragmentType.EINKAUFSLISTE );
                    MainActivity.this.setToolBarText( FragmentType.EINKAUFSLISTE );
                    item.setChecked(true);
                    MainActivity.this.fragmentTransition( R.id.frame_layout, MainActivity.this.listenFragment, 1 );
                    return true;
                case R.id.navigation_settings:
                    if ( MainActivity.this.viewModel.getCurrentFragment() == FragmentType.SETTINGS ) return true;
                    MainActivity.this.viewModel.setCurrentFragment( FragmentType.SETTINGS );
                    MainActivity.this.setToolBarText( FragmentType.SETTINGS );
                    item.setChecked(true);
                    MainActivity.this.fragmentTransition( R.id.frame_layout, MainActivity.this.settingsFragment, 2);
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

        if ( this.viewModel.getCurrentFragment() == FragmentType.NONE) {
            this.viewModel.setCurrentFragment( FragmentType.AUSGABEN );
        }

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
        } else if ( this.viewModel.getCurrentFragment() == FragmentType.SETTINGS) {
            this.fragmentTransition( R.id.frame_layout, this.settingsFragment, 2 );
        }

        this.navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /* Instantiate the Firebase authentication service */
        this.firebaseAuth = FirebaseAuth.getInstance();

        /* Start the firebase messaging service. */
        Intent serviceIntent = new Intent(this, MyFirebaseMessagingService.class);
        serviceIntent.putExtra( "device", this.viewModel.getDeviceName());
        startService(serviceIntent);

        /* Create new instance for the NotificationBroadCastReceiver of this MainActivity */
        this.notificationBroadcastReceiver = new NotificationBroadcastReceiver();
    }

    @Override
    public void onStart ( ) {
        super.onStart();

        /* Check if a user is already signed in and if so, process the information */
        FirebaseUser user = this.firebaseAuth.getCurrentUser();

        if ( user != null ) {
            /* A user is already signed in */
            Log.d("FireBase", "A user is already signed in ");
            this.viewModel.setFireBaseUser( user );
        } else {
            this.firebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if ( task.isSuccessful()) {
                        // New anonymous user has been created successully
                        FirebaseUser user = MainActivity.this.firebaseAuth.getCurrentUser();
                        Log.d("FireBase", "Signed in anonoymous user");

                        MainActivity.this.viewModel.setFireBaseUser( user );
                    } else {
                        Log.d( "FireBase", "Sign-in process failed");
                    }
                }
            });
        }

        /* Once the activity is started, register the notificationBroadCastReceiver of this activity
        * for receiving new Notification intents */
        LocalBroadcastManager.getInstance(this).registerReceiver(this.notificationBroadcastReceiver,
                new IntentFilter( getString(R.string.INTENT_FILTER_NEW_NOTIFICATION)));
    }

    @Override
    public void onStop ( ) {
        super.onStop();
        /* Once the activity is stopped, unregister the notificationBroadCastReceiver of this
        * activity for receiving new Notification intents. */
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(this.notificationBroadcastReceiver);
    }

    @Override
    protected void createFragments() {
        this.ausgabenFragment = new AusgabenFragment();
        this.listenFragment = new EinkaufslisteFragment();
        this.settingsFragment = new SettingsFragment();
    }

    @Override
    public void onPause ( ) {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.app_bar_menu, menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {

                return super.onOptionsItemSelected( item );
    }

    /**
     * This {@link BroadcastReceiver is supposed to receive Broadcasts from the
     * {@link MyFirebaseMessagingService} of this application and load the most recent data
     * accordingly.
     */
    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* Update the application data by downloading it. */
            MainActivity.this.loadData();

            /* Rearrange the data being visualized in the recycler on the Ausgabenfragment */
            MainActivity.this.ausgabenFragment.reArrangeFirstPaymentRecycler();
            MainActivity.this.ausgabenFragment.reArrangeSecondPaymentRecycler();

            /* Recalculate the next payer */
            MainActivity.this.ausgabenFragment.updateNextPayer();

            /* Finally, update the data being stored online */
            MainActivity.this.storeData();
        }
    }

}
