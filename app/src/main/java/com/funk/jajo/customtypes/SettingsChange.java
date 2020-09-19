package com.funk.jajo.customtypes;

public class SettingsChange extends Change {
    /**
     * The {@link Person}, this {@link SettingsChange} is associated with
     */
    private String personName;

    /**
     * The {@link SettingsOption}, this {@link SettingsChange} is associated with
     */
    private SettingsOption option;

    /**
     * Stores the new value for the changed {@link SettingsOption}
     */
    private OptionContainer container;

    public SettingsChange ( String person, SettingsOption option, OptionContainer container ) {
        this.personName = person;
        this.option = option;
        this.container = container;
    }

    /**
     * @return the name of the person associated with this {@link SettingsChange}
     */
    public String getPersonName ( ){ return this.personName; }

    /**
     * @return the {@link SettingsOption} associated with this {@link SettingsChange}
     */
    public SettingsOption getOption ( ) { return this.option; }
}
