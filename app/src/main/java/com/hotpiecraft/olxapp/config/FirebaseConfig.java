package com.hotpiecraft.olxapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hotpiecraft.olxapp.util.Base64Custom;

public class FirebaseConfig {

    private static DatabaseReference database;
    private static StorageReference storage;
    private static FirebaseAuth authentication;

    public static String getLoggedUserId() {
        String email = authentication.getCurrentUser().getEmail();
        return Base64Custom.encodeString(email);
    }

    public static DatabaseReference getFirebaseDB() {
        if(database ==null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return  database;
    }

    public static StorageReference getFirebaseStorage() {
       if(storage == null) {
           storage = FirebaseStorage.getInstance().getReference();
       }
        return storage;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if(authentication ==null) {
            authentication = FirebaseAuth.getInstance();
        }
        return authentication;
    }
}
