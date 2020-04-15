package com.dcnproject.yashdani.attendance_monitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    Spinner classDropDown;
    Spinner subjectDropDown;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabaseSubs;
    private String classNameValue,subjectNameValue;
    private FirebaseAuth mAuth;
    private Button mTake;
    private boolean para_passed = false;
    List<String> class_list = new ArrayList<>();
    private DatabaseReference mDatabaseClassRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        mTake=(Button) findViewById(R.id.takeAttendance);
        mDatabaseClassRef = FirebaseDatabase.getInstance().getReference().child("Classes");
        populateDropDown();
    }
    public void populateDropDown() {
        final List<String> groupNames = new ArrayList<>();
        groupNames.clear();
        classDropDown = (Spinner) findViewById(R.id.classSpinner);
        mDatabaseClassRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot subjKey: dataSnapshot.getChildren()){
                    groupNames.add(subjKey.getKey().toString());
                    Log.d("Class", "onDataChange: " + subjKey.getKey().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminHome.this, android.R.layout.simple_spinner_item, groupNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classDropDown.setAdapter(adapter);
                classDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        classNameValue = adapterView.getItemAtPosition(i).toString();
                        class_list.clear();
                        populatePayeeDropDown();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }
    public void populatePayeeDropDown(){
        subjectDropDown = (Spinner) findViewById(R.id.subjectSpinner);
        mDatabaseClassRef.child(classNameValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot subj: dataSnapshot.getChildren()){
                    class_list.add(subj.child("SubjectName").getValue().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminHome.this, android.R.layout.simple_spinner_item, class_list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectDropDown.setAdapter(adapter);
                subjectDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        subjectNameValue = adapterView.getItemAtPosition(i).toString();
                        mTake.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent takeAttenIntent = new Intent(AdminHome.this,TakeAttendance.class);
                                takeAttenIntent.putExtra("Class",classNameValue);
                                takeAttenIntent.putExtra("SubjectName",subjectNameValue);
                                startActivity(takeAttenIntent);
                                finish();
                            }
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
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
            Intent logoutIntent = new Intent(AdminHome.this, MainActivity.class);
            startActivity(logoutIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
