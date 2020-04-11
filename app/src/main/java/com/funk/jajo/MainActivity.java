package com.funk.jajo;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.funk.jajo.customtypes.Person;

public class MainActivity extends AppBarActivity {

    private AusgabenFragment ausgabenFragment;
    private EinkaufslisteFragment listenFragment;
    private AppViewModel viewModel;

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

}
