package com.example.ccswwf.dailycoupon;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText idText;
    EditText passwordText;

    TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = (EditText) findViewById(R.id.user_id);
        passwordText = (EditText) findViewById(R.id.user_password);
        loginButton = (TextView) findViewById(R.id.login_btn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userId = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                if(userId.equals("") || userPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드를 모두 입력해주세요!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        ContentValues values = new ContentValues();
                        values.put("user_id", idText.getText().toString());
                        values.put("user_password", passwordText.getText().toString());
                        LoginTask task = new LoginTask(values);
                        task.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        String url = "http://prography.org/login";
        ContentValues values;

        LoginTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject resultObject = new JSONObject(result);
                if (resultObject.getInt("error") == 1) {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 다시 확인해주세요!", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences userInformation = getSharedPreferences("user", 0);
                    SharedPreferences.Editor editor = userInformation.edit();

                    editor.putString("user_id", idText.getText().toString());
                    editor.commit();

                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivity);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}