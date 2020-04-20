package com.programmingintelugu.mybuddies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.programmingintelugu.mybuddies.Utilities.FirebaseUtilities;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText emailEditText,passwordEditText;
    Button login;
    FirebaseUtilities firebaseUtilities;

    TextView registerHere;

    SignInButton googleSignIn;

    GoogleSignInClient googleSignInClient;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        login = findViewById(R.id.login);
        registerHere = findViewById(R.id.registerHere);
        googleSignIn = findViewById(R.id.googleSignIn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signin processing ...");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseUtilities = new FirebaseUtilities(this);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoogleSignIn();
            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);




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

    void startGoogleSignIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(googleSignInAccount);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "google Sign in error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount){
        Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle: ");
        progressDialog.show();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "google failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
