package com.dcnproject.yashdani.attendance_monitor;

import android.content.Intent;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.BatchUpdateException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

public class EnterPin extends AppCompatActivity {
    private Button mContinue;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;
    private EditText mPin;
    private String UID;
    private static final String     AndroidKeyStore = "AndroidKeyStore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);
        UID = getIntent().getStringExtra("UID");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users").child("Student").child(UID);
        mDatabase = FirebaseDatabase.getInstance();
        mPin =(EditText) findViewById(R.id.enterIdentifier);
        mContinue =(Button) findViewById(R.id.contButton);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startContinue();
            }
        });
    }

    private void startContinue(){
        String pin = mPin.getText().toString();
        mDatabaseRef.child("Pin").setValue(pin);
        startActivity(new Intent(EnterPin.this,Login.class));
        finish();
    }
}