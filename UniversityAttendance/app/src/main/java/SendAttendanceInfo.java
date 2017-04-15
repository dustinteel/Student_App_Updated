import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by TylerBowlen on 4/10/17.
 */

public class SendAttendanceInfo extends AsyncTask<String, Void, String> {

    String urlString = "http://universityattendance.000webhostapp.com/mysql.php";

    private final String TAG = "Send Attendace Info";
    private Context context;
    private String roomCode;
    private String studentID;


    public SendAttendanceInfo(Context context, String roomCode, String studentID){
        this.context = context;
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

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("code", this.roomCode);
            postDataParams.put("student_id", this.studentID);
            postDataParams.put("date", DateFormat.getDateTimeInstance().format(new Date()));
            Log.i("Info:", "Params loaded");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null){
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();
            }else{
                return new String("false : " + responseCode);
            }


        } catch (Exception  e) {
            e.printStackTrace();
        }
        return new String("false");
    }

    @Override
    protected void onPostExecute(String result){
        //Display on screen
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
}
