package com.dcnproject.yashdani.attendance_monitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeAttendance extends AppCompatActivity {

    private String subject,class_name,studentId,studentClass;
    private Button mConfirm,mDone;
    private DatabaseReference mUser,mAttendance;
    private EditText mPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        subject = getIntent().getStringExtra("SubjectName");
        class_name = getIntent().getStringExtra("Class");
        Log.d("Debug", "onDataChange: " + class_name + " "+ subject);
        mPin = (EditText) findViewById(R.id.pin);
        mDone = (Button) findViewById(R.id.doneButton);
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Student");
        mAttendance = FirebaseDatabase.getInstance().getReference().child("Classes").child(class_name);
        mConfirm = (Button) findViewById(R.id.confirmBtn);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Activity Progress", "onClick: Started COnfirming ");
                startConfirm();
            }
        });
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takeAttenIntent = new Intent(TakeAttendance.this,AdminHome.class);
                startActivity(takeAttenIntent);
                finish();
            }
        });
    }
    private void startConfirm() {
        String pin = mPin.getText().toString().trim();
        mUser.orderByChild("Pin").equalTo(pin).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Checked", "onDataChange:1 ");
                for(DataSnapshot USER: dataSnapshot.getChildren()) {
                    studentClass = USER.child("class").getValue().toString();
                    studentId = USER.getKey().toString();
                }
                Log.d("IF", "onDataChange: " + studentClass + " == " +class_name + " Student Id = " +studentId );
                    Log.d("Is", "onDataChange: " + studentClass + " == " +class_name );
                    mAttendance.orderByChild("SubjectName").equalTo(subject).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("CHecked", "onDataChange:1 ");
                        Log.d("Getting Subject Code", "onDataChange: " + dataSnapshot.getValue());
                        SimpleDateFormat sdate = new SimpleDateFormat("dd_MM_yyyy");
                        String format = sdate.format(new Date());
                        String subjectCode = null;
                        for(DataSnapshot subj: dataSnapshot.getChildren())
                            subjectCode =  subj.getKey().toString();
                        if(subjectCode != null && format != null)
                            mAttendance.child(subjectCode).child("Attendance").child(format).child(studentId).setValue("Present");
                        Log.d("this", "onDataChange: " + class_name + "" + subjectCode);
                        Intent takeAttenIntent = new Intent(TakeAttendance.this,TakeAttendance.class);
                        takeAttenIntent.putExtra("Class",class_name);
                        takeAttenIntent.putExtra("SubjectName",subject);
                        startActivity(takeAttenIntent);
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}