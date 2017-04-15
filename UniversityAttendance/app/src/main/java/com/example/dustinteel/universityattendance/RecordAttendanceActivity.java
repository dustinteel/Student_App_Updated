package com.example.dustinteel.universityattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class RecordAttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This code is run when the activity is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_attendance);
        // Check to see if the student has entered his Student ID on the app before.  If so, use their ID
        // and do not let them change it to prevent cheating
        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String storedStudentID = preferences.getString("Student ID", "");
        if(!storedStudentID.equals("")){
            EditText studentID = (EditText)findViewById(R.id.studentID);
            studentID.setText(storedStudentID);
            studentID.setEnabled(false);
        }

        //Set change listeners

        EditText codeText = (EditText) findViewById(R.id.roomCode1);
        EditText studentText = (EditText) findViewById(R.id.studentID);

        codeText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        studentText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view){
        InputMethodManager manager = (InputMethodManager)getSystemService(RecordAttendanceActivity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void sendAttendance(View view){
        Log.i("Info:", "Attendance info sent.");
        EditText editText = (EditText)findViewById(R.id.roomCode1);
        EditText editText1 = (EditText)findViewById(R.id.studentID);
        // If any of the fields are blank, display errors and do not allow script to be called
        boolean validInput = true;
        if(editText.getText().toString().trim().equals("")){
            editText.setError("Room code required!");
            validInput = false;
        }
        if(editText1.getText().toString().trim().equals("")){
            editText1.setError("Student ID required!");
            validInput = false;
        }

        if(validInput){

            SendAttendanceInfo attend = new SendAttendanceInfo(this, editText.getText().toString(), editText1.getText().toString());
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if(activeNetworkInfo != null) {
                attend.execute();
            }
        }

    }

}

class SendAttendanceInfo extends AsyncTask<String, Void, String> {

    String urlString = "http://universityattendance.000webhostapp.com/mysql.php";

    private final String TAG = "Send Attendance Info";
    private String roomCode;
    private String studentID;
    public RecordAttendanceActivity activity;


    public SendAttendanceInfo(RecordAttendanceActivity activity, String roomCode, String studentID){
        this.activity = activity;
        this.roomCode = roomCode;
        this.studentID = studentID;
    }

    @Override
    protected void onPreExecute(){
        Log.i("Info:", "Send Attendance Info Task About to Execute...");
    }

    @Override
    protected String doInBackground(String... params) {
        try{

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
            writer.write("func=1&student=" + this.studentID + "&code=" + this.roomCode);
            Log.i("PARAMS: ", "func=1&student=" + this.studentID + "&code=" + this.roomCode);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK){
                String response = readInputStreamToString(conn);
                Log.i("response", response);
                // Update text view to tell user result of action
                return response;
            }else{
                Log.i("Response (failed) : ", "" + responseCode);
                return new String("false : " + responseCode);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String("false");
    }

    @Override
    protected void onPostExecute(String result){
        //Display on screen
        if(result.trim().contains("Attendance Recorded")){
            //If input was valid, save Student ID and send attendance info to script
            SharedPreferences preferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putString("Student ID", this.studentID);
            editor.commit();

            //Make studentID uneditable
            EditText temp = (EditText) activity.findViewById(R.id.studentID);
            temp.setEnabled(false);

            TextView resultText = (TextView) activity.findViewById(R.id.result);
            resultText.setTextColor(Color.GREEN);
            resultText.setText("Attendance Recorded!");
        }else{
            TextView resultText = (TextView) activity.findViewById(R.id.result);
            resultText.setTextColor(Color.RED);
            resultText.setText("Attendance record not open; Attendance not recorded!");
        }
    }

    public String getPostDataString(JSONObject params) throws Exception{
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(!itr.hasNext()){
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

    /**
     * @param connection object; note: before calling this function,
     *   ensure that the connection is already be open, and any writes to
     *   the connection's output stream should have already been completed.
     * @return String containing the body of the connection response or
     *   null if the input stream could not be read correctly
     */
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
