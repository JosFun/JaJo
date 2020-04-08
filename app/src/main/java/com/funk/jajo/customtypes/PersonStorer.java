package com.funk.jajo.customtypes;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Stores a {@link Person} on the phone persistently.
 */
public class PersonStorer {
    /**
     * The key being used to load persons, stored on the device, from.
     */
    private String loadKey = null;
    /**
     * The {@link SharedPreferences} using which {@link Person}s have been stored on the device and
     * can now be loaded from.
     */
    private SharedPreferences sP = null;


    /**
     * Create a new instance of {@link PersonStorer} in order to store a new {@link Person} on the device.
     * @param p - The {@link Person} that is to be stored on the phone.
     * @param key - The key as a {@link String} under which the {@link Person } is to be put on the device.
     * @param sP - The {@link SharedPreferences} being used to store the {@link Person} on the device.
     */
    public PersonStorer ( Person p, String key, SharedPreferences sP ) {

        String personFile = new Gson().toJson( p );

        SharedPreferences.Editor editor =   sP.edit();
        editor.putString ( key, personFile );
        editor.commit();
    }

    /**
     * Create a new instance of {@link PersonStorer} in order to load a {@link Person} from the
     * {@link SharedPreferences} this {@link PersonStorer} has been created with.
     * @param loadKey - The key under which the {@link Person } is to be loaded from.
     * @param sP - The {@link SharedPreferences} the {@link Person} is to be loaded from.
     */
    public PersonStorer ( String loadKey, SharedPreferences sP ) {
        this.sP = sP;
        this.loadKey = loadKey;
    }

    /**
     * Load a {@link Person} that has is stored inside the {@link SharedPreferences} of this {@link PersonStorer}
     * @return
     */
    public Person loadPerson ( ) {
        if ( this.loadKey == null || this.sP == null ) return null;

        String defaultValue = "NONE";
        String personFile = this.sP.getString( this.loadKey, defaultValue );

        if ( !personFile.equals ( defaultValue )) {
            Person p = null;
            try {
                p = new Gson().fromJson ( personFile,
                        new TypeToken<Person>() {}.getType());
            } catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
            return p;
        }

        return null;
    }
}
