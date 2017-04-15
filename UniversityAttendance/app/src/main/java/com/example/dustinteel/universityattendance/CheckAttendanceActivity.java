package com.example.dustinteel.universityattendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CheckAttendanceActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        // Check to see if the student has entered his Student ID on the app before.  If so, use their ID
        // and do not let them change it to prevent cheating
        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String storedStudentID = preferences.getString("Student ID", "");
        if (!storedStudentID.equals("")) {
            EditText studentID = (EditText) findViewById(R.id.studentid);
            studentID.setText(storedStudentID);
            studentID.setEnabled(false);
        }


        AutoCompleteTextView classText = (AutoCompleteTextView) findViewById(R.id.className);
        AutoCompleteTextView professorText = (AutoCompleteTextView) findViewById(R.id.professor);
        EditText studentidText = (EditText) findViewById(R.id.studentid);
        EditText startDateText = (EditText) findViewById(R.id.startDate);
        EditText endDateText = (EditText) findViewById(R.id.endDate);

        //Set AutoCompletes for Professor and Class
        Set<String> professors = new HashSet<String>();
        professors = new HashSet<String>(preferences.getStringSet("Professors", professors));

        Set<String> classes = new HashSet<String>();
        classes = new HashSet<String>(preferences.getStringSet("Classes", classes));

        String[] professorArray = professors.toArray(new String[professors.size()]);
        String[] classArray = classes.toArray(new String[classes.size()]);
        if (classArray.length > 0) Log.i("Class:", classArray[0]);
        if (professorArray.length > 0) Log.i("Professor:", professorArray[0]);


        classText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classArray));
        professorText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, professorArray));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This code is run when the activity is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        // Check to see if the student has entered his Student ID on the app before.  If so, use their ID
        // and do not let them change it to prevent cheating
        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String storedStudentID = preferences.getString("Student ID", "");
        if (!storedStudentID.equals("")) {
            EditText studentID = (EditText) findViewById(R.id.studentid);
            studentID.setText(storedStudentID);
            studentID.setEnabled(false);
        }


        AutoCompleteTextView classText = (AutoCompleteTextView) findViewById(R.id.className);
        AutoCompleteTextView professorText = (AutoCompleteTextView) findViewById(R.id.professor);
        EditText studentidText = (EditText) findViewById(R.id.studentid);
        EditText startDateText = (EditText) findViewById(R.id.startDate);
        EditText endDateText = (EditText) findViewById(R.id.endDate);


        //Set change listeners
        classText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        professorText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        studentidText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        startDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        endDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(CheckAttendanceActivity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void getAttendance(View view) {
        Log.i("Info:", "Attendance info sent.");
        AutoCompleteTextView classView = (AutoCompleteTextView) findViewById(R.id.className);
        AutoCompleteTextView professorView = (AutoCompleteTextView) findViewById(R.id.professor);
        EditText studentView = (EditText) findViewById(R.id.studentid);
        EditText startDateView = (EditText) findViewById(R.id.startDate);
        EditText endDateView = (EditText) findViewById(R.id.endDate);

        // If any of the fields are blank, display errors and do not allow script to be called
        boolean validInput = true;
        if (classView.getText().toString().trim().equals("")) {
            classView.setError("Class name required!");
            validInput = false;
        }
        if (professorView.getText().toString().trim().equals("")) {
            professorView.setError("Professor required!");
            validInput = false;
        }
        if (studentView.getText().toString().trim().equals("")) {
            studentView.setError("Student ID required!");
            validInput = false;
        }
        if (endDateView.getText().toString().trim().equals("")) {
            endDateView.setError("End date required!");
            validInput = false;
        } else if (!endDateView.getText().toString().trim().matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$")) {
            endDateView.setError("Date must be in the format YYYY-MM-DD!");
            validInput = false;
        }
        if (startDateView.getText().toString().trim().equals("")) {
            startDateView.setError("Start date required!");
            validInput = false;
        } else if (!startDateView.getText().toString().trim().matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$")) {
            startDateView.setError("Date must be in the format YYYY-MM-DD!");
            validInput = false;
        }
        // Save the entered StudentID to prevent cheating
        if (validInput) {
            GetAttendanceInfo attend = new GetAttendanceInfo(this, classView.getText().toString(), professorView.getText().toString(), studentView.getText().toString(), startDateView.getText().toString(), endDateView.getText().toString());


            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if(activeNetworkInfo != null) {
                attend.execute();
            }
        }

    }
}

    class GetAttendanceInfo extends AsyncTask<String, Void, String> {

        String urlString = "http://universityattendance.000webhostapp.com/mysql.php";

        private final String TAG = "Send Attendance Info";
        private String className;
        private String profUsername;
        private String studentID;
        private String startDate;
        private String endDate;
        private CheckAttendanceActivity activity;


        public GetAttendanceInfo(CheckAttendanceActivity activity, String className, String profUsername, String studentID, String startDate, String endDate) {
            this.activity = activity;
            this.className = className;
            this.profUsername = profUsername;
            this.studentID = studentID;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Info:", "Send Attendance Info Task About to Execute...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL(urlString);
                Log.i("Info:", "Params loaded");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoInput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                Log.i("Info:", "HI");
                writer.write("func=2&class=" + this.className + "&prof=" + this.profUsername + "&student=" + this.studentID + "&startdate=" + this.startDate + "&enddate=" + this.endDate);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String response = readInputStreamToString(conn);
                    Log.i("response", response);
                    // Update text view to tell user result of action
                    return response;
                } else {
                    Log.i("Response (failed) : ", "" + responseCode);
                    return new String("false : " + responseCode);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String("false");
        }

        @Override
        protected void onPostExecute(String result) {
            //Display on screen
            if(result.trim().contains("Present") || result.trim().contains("Absent")) {
                //If input was valid, save Student ID and send attendance info to script
                SharedPreferences preferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                //Save typed classes and professors for auto complete text view
                Set<String> classes = new HashSet<String>();
                classes = new HashSet<String>(preferences.getStringSet("Classes", classes));
                classes.add(this.className);

                Set<String> professors = new HashSet<String>();
                professors = new HashSet<String>(preferences.getStringSet("Professors", professors));
                professors.add(this.profUsername);

                editor.clear();
                editor.putString("Student ID", this.studentID);
                editor.putStringSet("Classes", classes);
                editor.putStringSet("Professors", professors);
                editor.commit();
                Intent intent = new Intent(activity, DisplayAttendanceActivity.class);
                intent.putExtra("Attendance", result);
                activity.startActivity(intent);
            }else{
                TextView resultText = (TextView) activity.findViewById(R.id.checkError);
                resultText.setTextColor(Color.RED);
                resultText.setText("Incorrect info provided.  Try again!");
            }
        }

        public String getPostDataString(JSONObject params) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (!itr.hasNext()) {
                String key = itr.next();
                Object value = params.get(key);


                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
            return result.toString();
        }

        private String readInputStreamToString(HttpURLConnection connection) {
            String result = null;
            StringBuffer sb = new StringBuffer();
            InputStream is = null;

            try {
                is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
            }
            catch (Exception e) {
                Log.i(TAG, "Error reading InputStream");
                result = null;
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        Log.i(TAG, "Error closing InputStream");
                    }
                }
            }

            return result;
        }
    }

