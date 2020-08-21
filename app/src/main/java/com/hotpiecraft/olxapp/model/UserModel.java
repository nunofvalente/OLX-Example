package com.hotpiecraft.olxapp.model;

import android.util.Base64;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.hotpiecraft.olxapp.config.FirebaseConfig;
import com.hotpiecraft.olxapp.util.Base64Custom;

public class UserModel {

    private String id;
    private String name;
    private String email;
    private String password;

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void save() {
        DatabaseReference database = FirebaseConfig.getFirebaseDB();
        String email = this.email;

        String userId = Base64Custom.encodeString(email);

        DatabaseReference userRef = database.child(userId);
        userRef.setValue(this);

    }
}
