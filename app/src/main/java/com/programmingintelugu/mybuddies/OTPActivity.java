package com.programmingintelugu.mybuddies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    Button verifyOTP;
    TextInputEditText textInputEditText;
    TextView resendOTP;

    String otp;

    String verificationID,phoneNumber;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCalBacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        verifyOTP = findViewById(R.id.verifyOTP);
        textInputEditText = findViewById(R.id.OTPEditText);

        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        verificationID = getIntent().getExtras().getString("verificationID");

        resendOTP = findViewById(R.id.resendOTP);

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = textInputEditText.getText().toString();
                if (otp.length() < 6){
                    Toast.makeText(OTPActivity.this, "OTP must be 6 digits", Toast.LENGTH_SHORT).show();
                }else{
                    verifyOTP();
                }
            }
        });
    }

    void verifyOTP(){
     PhoneAuthCredential phoneAuthCredential =  PhoneAuthProvider.getCredential(verificationID,otp);
     signInWithCredetials(phoneAuthCredential);
    }

    void signInWithCredetials(PhoneAuthCredential phoneAuthCredential){

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(OTPActivity.this, "Sign in Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTPActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OTPActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
