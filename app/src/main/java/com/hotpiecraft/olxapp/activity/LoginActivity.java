package com.hotpiecraft.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.hotpiecraft.olxapp.R;
import com.hotpiecraft.olxapp.config.FirebaseConfig;
import com.hotpiecraft.olxapp.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText editLoginEmail, editLoginPassword;
    private SwitchCompat switchLoginRegister;
    private ProgressBar progressBar;

    private FirebaseAuth authentication;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();
        progressBar.setVisibility(View.GONE);
    }

    private void initializeComponents() {
        loginButton = findViewById(R.id.buttonLogin);
        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        switchLoginRegister = findViewById(R.id.switchLoginRegister);

        authentication = FirebaseConfig.getFirebaseAuth();
        database = FirebaseConfig.getFirebaseDB();

        setListeners();
    }

    private void registerUser() {
        final String email = editLoginEmail.getText().toString();
        final String password = editLoginPassword.getText().toString();
        final UserModel user = new UserModel();

        if (!email.equals("")) {
            if (!password.equals("") && password.length() >= 6) {
                progressBar.setVisibility(View.VISIBLE);
                authentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            user.setEmail(email);
                            user.setPassword(password);
                            user.save();
                            Toast.makeText(getApplicationContext(), "User was registered with success!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            String exception;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                exception = "Please enter a stronger password!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                exception = "Please enter a valid email!";
                            } catch (FirebaseAuthUserCollisionException e) {
                                exception = "Account already registered!";
                            } catch (Exception e) {
                                exception = "Error registering user: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Password must contain 6 digits!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {

        final String email = editLoginEmail.getText().toString();
        final String password = editLoginPassword.getText().toString();

        if (!email.equals("")) {
            if (!password.equals("") && password.length() >= 6) {
                progressBar.setVisibility(View.VISIBLE);
                authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        } else {
                            String exception;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                exception = "Email address does not exist!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                exception = "Password does not match with email!";
                            } catch (Exception e) {
                                exception = "Error logging in: " + e.getMessage();
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Password must contain 6 digits!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
        }
    }

    private void processAccess() {

        if (switchLoginRegister.isChecked()) {
            registerUser();
        } else {
            login();
        }
    }

    private void setListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAccess();
            }
        });
    }

   /* private void verifyIfLogged() {
        if(authentication.getCurrentUser() != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        //verifyIfLogged();
    }

}