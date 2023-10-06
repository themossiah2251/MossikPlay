package com.example.mossikplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        signupRedirectText = findViewById(R.id.signUpNow);
        loginButton = findViewById(R.id.login_button);


        loginButton.setOnClickListener(v-> {
            Log.d("LoginActivity", "Login button clicked");
                if(!validateUserName() | !validatePassword()){


                    checkUser();

            }
        });
        signupRedirectText.setOnClickListener(v-> {

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
        });







    }
    public Boolean validateUserName(){
        String val = loginUsername.getText().toString();
        if( val.isEmpty()){
            Log.d("LoginActivity", "Username is empty");
            loginUsername.setError("Username cannot be empty");
            return false;
        }else{
            Log.d("LoginActivity", "Username is empty");
            loginUsername.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if( val.isEmpty()){
            Log.d("LoginActivity", "Username is empty");
            loginPassword.setError("Password cannot be empty");
            return false;
        }else{
            Log.d("LoginActivity", "Username is empty");
            loginPassword.setError(null);
            return true;
        }
    }
    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if(Objects.equals(passwordFromDB, userPassword)){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();

                    }
                }else{
                    loginUsername.setError(" User does not exist!");
                    loginUsername.requestFocus();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error querying database", error.toException());
            }
        });


    }
}
