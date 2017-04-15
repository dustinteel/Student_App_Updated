package com.example.dustinteel.universityattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplayAttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_attendance);

        String attendanceInfo = getIntent().getSerializableExtra("Attendance").toString();
        Log.i("Info passed", attendanceInfo);

        // Spilt the results up
        String[] result1 = attendanceInfo.split(",");

        // Remove the ", {, and } from the strings
        for(int i = 0; i < result1.length; i++){
            result1[i] = result1[i].replace("\"", "");
            result1[i] = result1[i].replace("{", "");
            result1[i] = result1[i].replace("}", "");
        }
        int noOfAbsences = 0;
        int noOfPresents = 0;
        // Now we go through the list, split, and display in table
        TableLayout table = (TableLayout) findViewById(R.id.table);
        for(int i = 0; i < result1.length; i++){
            String[] result2 = result1[i].split(":");
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            row.setLayoutParams(lp);

            //Count things
            if(result2[1].contains("Absent")){
                noOfAbsences++;
            }else if(result2[1].contains("Present")){
                noOfPresents++;
            }
            lp.setMargins(225, 20, 0, 20);
            TextView date = new TextView(this);
            date.setLayoutParams(lp);
            date.setText(result2[0] + " - " + result2[1]);

            row.addView(date);

            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        TableRow blankRow = new TableRow(this);
        TableRow.LayoutParams lp4 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp4.gravity = Gravity.CENTER_HORIZONTAL;
        blankRow.setLayoutParams(lp4);

        table.addView(blankRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));



        TableRow row = new TableRow(this);
        row.setLayoutParams(lp4);

        //Print total number of absences and presents
        lp4.setMargins(225, 20, 0, 20);
        TextView absences = new TextView(this);
        absences.setLayoutParams(lp4);
        absences.setText("Total Classes Missed: " + noOfAbsences);

        row.addView(absences);
        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        TableRow row2 = new TableRow(this);
        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp2.gravity = Gravity.CENTER_HORIZONTAL;
        row2.setLayoutParams(lp2);

        lp2.setMargins(225, 20, 0, 20);
        TextView presents = new TextView(this);
        presents.setLayoutParams(lp2);
        presents.setText("Total Classes Attended: " + noOfPresents);

        row2.addView(presents);
        table.addView(row2, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TableRow row3 = new TableRow(this);
        TableRow.LayoutParams lp3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp3.gravity = Gravity.CENTER_HORIZONTAL;
        row3.setLayoutParams(lp3);

        lp3.setMargins(225, 20, 0, 20);
        Button back = new Button(this);
        back.setText("Done");
        back.setLayoutParams(lp3);
        back.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               Intent intent = new Intent(v.getContext(), MainActivity.class);
               startActivity(intent);
           }
        });

        row3.addView(back);
        table.addView(row3, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


    }


}
