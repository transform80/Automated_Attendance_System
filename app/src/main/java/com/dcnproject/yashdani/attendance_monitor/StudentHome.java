package com.dcnproject.yashdani.attendance_monitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentHome extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String subjectNameValue,subjectCode;
    private String classNameValue;
    String UID;
    private DatabaseReference mDatabaseSubjectRef,mDatabaseClassRef,mDatabaseAttendance;
    List<String> subject_list = new ArrayList<>();
    List<String> subject_code = new ArrayList<>();
    List<String> attendance_list = new ArrayList<>();
    Spinner subjectDropDown;
    private ListView mAttendanceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        mAttendanceList = (ListView) findViewById(R.id.attendanceList);
        mAuth = FirebaseAuth.getInstance();
        UID = getIntent().getStringExtra("UID");
        mDatabaseClassRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Student").child(UID);
        populateClass();
    }
    public void  populateClass(){
        mDatabaseClassRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("haha", "onDataChange: "+dataSnapshot.child("class").getValue().toString());
                //classNameValue = dataSnapshot.child("class").getValue().toString();
                classNameValue="SYBTechComputers";
                subject_list.clear();
                populateSubjectDropDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void populateSubjectDropDown() {
        subjectDropDown = (Spinner) findViewById(R.id.subjectSelectSpinner);
        mDatabaseSubjectRef = FirebaseDatabase.getInstance().getReference().child("Classes").child(classNameValue);
        mDatabaseSubjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot subjname: dataSnapshot.getChildren()){
                    Log.d("What", "onDataChange: "+ subjname.getKey().toString());
                    subject_list.add(subjname.child("SubjectName").getValue().toString());
                    subject_code.add(subjname.getKey().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(StudentHome.this, android.R.layout.simple_spinner_item, subject_list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectDropDown.setAdapter(adapter);
                subjectDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        subjectNameValue = adapterView.getItemAtPosition(i).toString();
                        subjectCode = subject_code.get(i);
                        attendance_list.clear();
                        populateAttendanceView();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void populateAttendanceView() {
        mDatabaseAttendance = FirebaseDatabase.getInstance().getReference().child("Classes").child(classNameValue).child(subjectCode).child("Attendance");
        mDatabaseAttendance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot date: dataSnapshot.getChildren()){
                if(date.hasChild(UID)){
                    attendance_list.add(date.getKey().toString());
                    Log.d("Attendance", "onDataChange: " + date.getKey().toString());
                }}
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,attendance_list);
                mAttendanceList.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //and this to handle actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logout) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent logoutIntent = new Intent(StudentHome.this, MainActivity.class);
            startActivity(logoutIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}



