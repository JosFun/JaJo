package com.funk.jajo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


/**
 * {@link AppBarActivity} is the main Activity class every other activity in the
 * project should extend, since every other activity in the project is meant to provide an
 * AppBar.
 */
public abstract class AppBarActivity extends AppCompatActivity {

    protected enum FragmentType {
        AUSGABEN,
        EINKAUFSLISTE
    }
    /**
     * indicates whether or not the toolBar is currently present on the activity.
     */
    protected boolean appBarPresent = false;
    protected Toolbar appBar = null;

    /**
     * Show a toast with the specified text for a specified amount of time.
     * @param context The ApplicationContext of the current application
     * @param text The Text that should be shown in a toast
     * @param duration The duration the toast will be visible for
     */

    public static void makeToast (Context context,  String text, int duration ) {
        Toast t = Toast.makeText ( context, text, duration );
        t.show ( );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Set app bar for the current activity to the toolbar, which is to be included in every Activity's xml file
     * @return whether or not the toolbar could have been found.
     */
    public boolean setAppBar ( ) {
        this.appBar = findViewById(R.id.toolbar);

        // Return false if the appBar could not have been found
        if ( this.appBar == null ) return (false);

        this.setSupportActionBar(this.appBar);
        return (true);

    }

    /**
     * Sets the text of the toolbar according to the current activity's id.
     * To be invoked by every non abstract sublass which is extending this class.
     * @param type - the id of the activity we want to display the name of in the appBar
     */
    public void setToolBarText ( FragmentType type ) {
        if ( this.appBarPresent ) {
            switch ( type ) {
                case AUSGABEN:
                    this.getSupportActionBar().setTitle( "Ausgaben" );
                    break;
                case EINKAUFSLISTE:
                    this.getSupportActionBar().setTitle( "Einkaufsliste" );
                    break;
                default:
                    this.getSupportActionBar().setTitle(R.string.app_name);
            }
        }
    }

    /**
     * Set text of the toolbar to the specified string.
     * @param toolBarText The text the user wants to be displayed on the toolbar.
     */
    public void setToolBarText ( String toolBarText ) {
        if ( this.appBarPresent ) {
            this.getSupportActionBar().setTitle(toolBarText);
        }
    }


    /**
     * PaymentChange Fragment from current fragment to the fragment specified.
     * @param frame_layout_id The id of the layout the new fragement will be visualized on
     * @param destFragment The fragment we want to be shown from now on.
     * @param fragID The ID of the fragment we would like to visualize
     *
     */
    public boolean fragmentTransition ( int frame_layout_id, Fragment destFragment, int fragID ) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

        // Check whether or not an element with the specified id even exists.
        if ( findViewById(frame_layout_id) == null )
            return ( false );

        ft.replace ( frame_layout_id, destFragment, Integer.toString (fragID) );
        ft.commit ( );

        return ( true );
    }


    /**
     * Create instances of the Fragments used in the instantiation of this activity.
     */
    protected abstract void createFragments ( );
}
