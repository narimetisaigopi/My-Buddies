package com.programmingintelugu.mybuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.programmingintelugu.mybuddies.Utilities.FirebaseUtilities;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText emailEditText,passwordEditText;
    Button login;
    FirebaseUtilities firebaseUtilities;

    TextView registerHere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        login = findViewById(R.id.login);
        registerHere = findViewById(R.id.registerHere);

        firebaseUtilities = new FirebaseUtilities(this);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.length() == 0 || !email.contains("@")){
                    emailEditText.setError("Enter valid Email");
                    return;
                }

                if (password.length() < 6){
                    passwordEditText.setError("Password should be 6 characters");
                    return;
                }

                firebaseUtilities.loginWithEmail(email,password);

            }
        });
    }
}
