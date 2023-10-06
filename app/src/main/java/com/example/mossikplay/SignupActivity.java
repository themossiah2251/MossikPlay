package com.example.mossikplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity  {
    EditText Email, Username, Password;
    TextView textView;
    Button buttonReg;
    FirebaseDatabase database;
    DatabaseReference reference;
    String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Email = findViewById(R.id.email);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        buttonReg = findViewById(R.id.signup_button);
        textView = findViewById(R.id.loginNow);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        buttonReg.setOnClickListener(v-> {
            {


                email = Email.getText().toString();
                username = Username.getText().toString();
                password = Password.getText().toString();

                if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
                    Email.setError("Enter a valid email address");
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    Username.setError("Enter a username");
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    Password.setError("Password must be at least 6 characters");
                    return;
                }

                HelperClass helperClass = new HelperClass(email, username, password);
                reference.child(username).setValue(helperClass);

                Toast.makeText(SignupActivity.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, com.example.mossikplay.LoginActivity.class);
                startActivity(intent);
            }
        });

        textView.setOnClickListener(v->{
            {
                Intent intent = new Intent(SignupActivity.this, com.example.mossikplay.LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailPattern);
    }
}

