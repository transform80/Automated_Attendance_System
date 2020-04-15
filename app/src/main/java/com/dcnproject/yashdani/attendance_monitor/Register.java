package com.dcnproject.yashdani.attendance_monitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    private Button mRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressDialog mProgress;
    List<String> list;
    int list_index ;
    Spinner classSelectDropDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mNameField = (EditText) findViewById(R.id.nameRegister);
        mEmailField=(EditText) findViewById(R.id.emailRegister);
        mPasswordField=(EditText) findViewById(R.id.passwordRegister);
        mRegister=(Button)findViewById(R.id.studentreg);
        mRegister=(Button) findViewById(R.id.studentreg);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
        classSelectDropDown=(Spinner) findViewById(R.id.classRegisterSpinner);
        list = new ArrayList<>();
        list.add("SYBTechComputers");
        list.add("SYBTechIT");
        list.add("Faculty");
        /*list.add("S.Y.B.Tech EXTC");
        list.add("S.Y.B.Tech Electrical");
        list.add("S.Y.B.Tech Civil");
        list.add("S.Y.B.Tech Production");
        list.add("S.Y.B.Tech Mechanical");
        list.add("S.Y.B.Tech Textile");*/
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSelectDropDown.setAdapter(adapter);
        classSelectDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                list_index = i;
                String str = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();
        final String class_name = list.get(list_index);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        if (class_name == "Faculty") {
                            DatabaseReference current_user_db = mDatabaseRef.child("Faculty").child(user_id);
                            current_user_db.child("name").setValue(name);
                            current_user_db.child("email").setValue(email);
                            Intent regIntent = new Intent(Register.this, Login.class);
                            regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(regIntent);
                            finish();
                        } else {
                            DatabaseReference current_user_db = mDatabaseRef.child("Student").child(user_id);
                            current_user_db.child("name").setValue(name);
                            current_user_db.child("email").setValue(email);
                            current_user_db.child("class").setValue(class_name);
                            Intent regIntent = new Intent(Register.this, EnterPin.class);
                            regIntent.putExtra("UID", user_id);
                            regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(regIntent);
                            finish();
                        }
                    } else {
                        Toast.makeText(Register.this, "Error registration", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}