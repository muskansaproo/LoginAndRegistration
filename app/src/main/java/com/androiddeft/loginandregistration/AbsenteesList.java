package com.androiddeft.loginandregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AbsenteesList extends AppCompatActivity {

    Button save,submit;
    ArrayList<String> absentArray = new ArrayList <String>();
    ArrayList<Integer> absentArrayId= new ArrayList <>();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    EditText absentID,absentName;
    public EditText timtetableresult;
    ListView show;
    private String username = "";
    final String AB_URL = "http://192.168.43.21:8080/member/uploadAbsentees.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absentees_list);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        username = (String) b.get("username");
        show=(ListView)findViewById(R.id.show);
        save=(Button)findViewById(R.id.done_button);
        absentID=(EditText)findViewById(R.id.teacher_id_input);
        absentName=(EditText)findViewById(R.id.teacher_name_input);
        submit = (Button) findViewById(R.id.submitList);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getAbName= absentName.getText().toString();
                String getAbId= absentID.getText().toString();
                if (getAbName.equals("") || getAbId.equals("")) {
                    Toast.makeText(AbsenteesList.this, "Can't be empty", Toast.LENGTH_SHORT).show();
                } else if (hashMap.containsKey(getAbId.toUpperCase() )) {
                    Toast.makeText(AbsenteesList.this, "ID already exists", Toast.LENGTH_SHORT).show();
                }
               /*else if( absentArrayId.contains(getAbId))
                {
                    Toast.makeText(AbsenteesList.this, "Id already exists", Toast.LENGTH_SHORT).show();
                }*/
                else {
                    hashMap.put(getAbId.toUpperCase(), getAbName);
                    absentArray.add(getAbName);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AbsenteesList.this, android.R.layout.simple_list_item_1, absentArray);
                    show.setAdapter(adapter);
                    ((EditText) findViewById(R.id.teacher_name_input)).setText("");
                    ((EditText) findViewById(R.id.teacher_id_input)).setText("");
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAbsentIDs send = new sendAbsentIDs();
                send.execute(hashMap);
            }
        });
    }
    public class sendAbsentIDs extends AsyncTask<HashMap<String, String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(AbsenteesList.this);

        @Override
        protected String doInBackground(HashMap<String, String>... hashMaps) {
            HashMap map = hashMaps[0];
            int i = 1;
            String ID = "ID";
            String Name = "NAME";
            Iterator it = map.entrySet().iterator();
            InputStream is;
            String result = "";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("principal_user", username));
            nameValuePairs.add(new BasicNameValuePair("size", Integer.toString(map.size())));
            while (it.hasNext()) {
                String temp_id = ID + "_" + i;
                String temp_name = Name + "_" + i;
                Map.Entry pair = (Map.Entry) it.next();
                nameValuePairs.add(new BasicNameValuePair(temp_id, String.valueOf(pair.getKey())));
                nameValuePairs.add(new BasicNameValuePair(temp_name, (String) pair.getValue()));
                i++;
            }
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(AB_URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait...");
            this.dialog.show();
        }


        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonObject = new JSONObject(result);
                int err_upload = jsonObject.getInt("Error_UploadData");
                //Toast.makeText(getApplicationContext(),err_upload,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                String message_upload = jsonObject.getString("Message_UploadData");
                /*if(err_upload==1){
                    //Toast.makeText(getApplicationContext(),message_upload,Toast.LENGTH_SHORT).show();
                }
                else {
                    int err_timetable = jsonObject.getInt("Error_TimeTable");
                    String timetable_result = jsonObject.getString("TimeTable_result");
                    if (err_timetable == 1) {
                        Toast.makeText(getApplicationContext(), timetable_result, Toast.LENGTH_SHORT).show();
                    } else {
                        //timtetableresult=(EditText)findViewById(R.id.timetable_result);
                        //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                        //timtetableresult.setText(jsonObject.);
                        //Intent i = new Intent(AbsenteesList.this, TimeTable.class);
                        //i.putExtra("TimeTable",result);
                        //Bundle args = new Bundle();
                        //args.putSerializable("TimeTable", (Serializable) jsonObject);
                        //startActivity(i);
                    }
                }*/
                } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        }
}
