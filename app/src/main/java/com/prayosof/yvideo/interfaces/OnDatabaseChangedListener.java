package com.prayosof.yvideo.interfaces;

/**
 * Created by Daniel on 1/3/2015.
 * Listen for add/rename items in database
 */
public interface OnDatabaseChangedListener {
    void onNewDatabaseEntryAdded();
    void onDatabaseEntryRenamed();
}