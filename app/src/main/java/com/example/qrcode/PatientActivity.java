package com.example.qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PatientActivity extends AppCompatActivity {

    String myUrl = "https://c64e-106-154-161-119.jp.ngrok.io/search.php?id=";
    ProgressDialog progressDialog;
    public static TextView scanText2, patientName, patientWeight, patientBlood, patientSpo2, patientRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Patient Information");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        scanText2 = (TextView) findViewById(R.id.scanText2);

        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        scanText2.setText(str);

        String[] myTaskParams = { str, str, str };
        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute(myTaskParams);

        patientName = (TextView) findViewById(R.id.patientName);
        patientWeight = (TextView) findViewById(R.id.patientWeight);
        patientBlood = (TextView) findViewById(R.id.patientBlood);
        patientSpo2 = (TextView) findViewById(R.id.patientSpo2);
        patientRoom = (TextView) findViewById(R.id.roomNumber);


    }

    public class MyAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(PatientActivity.this);
            progressDialog.setMessage("processing results");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Fetch data from the API in the background
            String qrId, param2, param3;
            qrId=strings[0];
            param2=strings[1];
            param3=strings[2];

            String result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(String.format("%s%s", myUrl, qrId));
                    //open a URL coonnection

                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();

                    while (data != -1) {
                        result += (char) data;
                        data = isw.read();

                    }

                    // return the data to onPostExecute method
                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            // dismiss the progress dialog after receiving data from API
            progressDialog.dismiss();

            try {
                JSONArray jsonArray1 = new JSONArray(s);
                for (int i = 0; i < jsonArray1.length(); i++)
                {
                    JSONObject jsonObj = jsonArray1.getJSONObject(i);
                    Toast.makeText(getApplicationContext(),
                            jsonObj.getString("patient_name"),
                            Toast.LENGTH_SHORT).show();
                    patientName.setText(jsonObj.getString("patient_name"));
                    patientWeight.setText(jsonObj.getString("patient_weight"));
                    patientBlood.setText(jsonObj.getString("patient_blood_pressure"));
                    patientSpo2.setText(jsonObj.getString("patient_spo2"));
                    patientRoom.setText(jsonObj.getString("patient_room_number"));
                }
//
//                JSONObject jsonObject1 =jsonArray1.getJSONObject(index_no);
//                String id = jsonObject1.getString("id");
//                String name = jsonObject1.getString("name");
//                String my_users = "User ID: "+id+"\n"+"Name: "+name;
//
//                //Show the Textview after fetching data
//                resultsTextView.setVisibility(View.VISIBLE);
//
//                //Display data with the Textview
//                resultsTextView.setText(my_users);
//
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



}