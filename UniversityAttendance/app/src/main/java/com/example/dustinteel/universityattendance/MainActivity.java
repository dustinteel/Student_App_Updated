package com.example.dustinteel.universityattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This code is run when the activity is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void recordAttendance(View view){
        //This function is run when the Record Attendance button is clicked
        //This should open the new activity for recording attendance
        Log.i("Info", "RECORD ATTENDACE BUTTON PRESSED");
        Intent intent = new Intent(this, RecordAttendanceActivity.class);
        startActivity(intent);

    }

    public void checkAttendance(View view){
        Log.i("Info", "CHECK ATTENDACE BUTTON PRESSED");
        Intent intent = new Intent(this, CheckAttendanceActivity.class);
        startActivity(intent);
    }

    public void settingsClicked(View view){
        Log.i("Info", "SETTINGS BUTTON PRESSED");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
