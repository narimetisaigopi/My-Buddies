package com.programmingintelugu.mybuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.programmingintelugu.mybuddies.Utilities.FirebaseUtilities;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailEditText,passwordEditText;
    Button register;
    FirebaseUtilities firebaseUtilities;

    TextView loginHere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        register = findViewById(R.id.register);
        loginHere = findViewById(R.id.loginHere);

        firebaseUtilities = new FirebaseUtilities(this);

        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
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

                firebaseUtilities.registerWithEmail(email,password);

            }
        });
    }
}
