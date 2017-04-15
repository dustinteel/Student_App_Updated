package com.example.dustinteel.universityattendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This code is run when the activity is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void clearProf(View view){
        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Professors");
        editor.apply();
    }

    public void clearClass(View view){
        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Classes");
        editor.apply();
    }

    public void doneTapped(View view){
        //Send them back to the Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
