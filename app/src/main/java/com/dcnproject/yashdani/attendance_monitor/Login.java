package com.dcnproject.yashdani.attendance_monitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLogin;
    private TextView mSignup;
    private SharedPreferences mPreferences;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.keepSynced(true);
        mProgress= new ProgressDialog(this);
        mLoginPasswordField = (EditText) findViewById(R.id.passwordLogin);
        mLoginEmailField = (EditText) findViewById(R.id.emailLogin);
        mLogin = (Button) findViewById(R.id.buttonLogin);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }
    private void checkLogin() {
        final String email = mLoginEmailField.getText().toString().trim();
        final String password = mLoginPasswordField.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Checking Login..");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mProgress.dismiss();
                        checkUserExist();
                    }
                    else{
                        mProgress.dismiss();
                        Toast.makeText(Login.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }
    }
    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        Log.d("Check", "checkUserExist: "+user_id);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Faculty").hasChild(user_id)) {
                    Intent mainIntent = new Intent(Login.this, AdminHome.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                    Toast.makeText(Login.this, "Logged in", Toast.LENGTH_LONG).show();
                    //finish();
                }
                else  if(dataSnapshot.child("Student").hasChild(user_id)){
                    Intent mainIntent = new Intent(Login.this, StudentHome.class);
                    mainIntent.putExtra("UID",user_id);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                    Toast.makeText(Login.this, "Logged in", Toast.LENGTH_LONG).show();
                    //finish();
                }
                else{
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
