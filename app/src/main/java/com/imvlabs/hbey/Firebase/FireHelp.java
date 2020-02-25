package com.imvlabs.hbey.Firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.imvlabs.hbey.Entities.FireModel;

/**
 * Created by Kautsar Fadly F on 19/09/2017.
 */

public class FireHelp {
    DatabaseReference db,ref;
    Boolean saved = null;
    private static final String TAG = "FireHelp";
    private String folder;

    public FireHelp(DatabaseReference ref){
        this.ref = ref;
    }

    public String getFolder() {
        return folder;
    }

    public Boolean save(FireModel recent){
        if(recent==null){
            saved = null;
            Log.i(TAG, "save: object null");
        } else {
            try{
                DatabaseReference db = ref.child("Data").push();
//                db.child("Data").push().setValue(recent);
                folder = db.getKey();
//                char[] charFolder = folder.toCharArray();
//                char[] charNaming = null;
//                int n = folder.length();
//                for(int i=0;i<n-38;i++){
//                    charNaming[i] = charFolder[38+i];
//                }
//                folder = String.valueOf(charNaming);
                db.setValue(recent);
                saved = true;
                Log.i(TAG, "save: upload complete");
            } catch (DatabaseException e){
                Log.e(TAG, "Save : upload failed");
                saved = false;
            }
        }
        return saved;
    }
}
