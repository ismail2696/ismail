package com.mail.pengaduanmail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mail.pengaduanmail.R;
import com.mail.pengaduanmail.utils.ConfigConstants;
import com.mail.pengaduanmail.utils.HttpRequest;
import com.mail.pengaduanmail.utils.ParseContent;
import com.mail.pengaduanmail.utils.PreferenceHelper;
import com.mail.pengaduanmail.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

public class Login extends Activity {


    public String username, password;

    private Button bt_login, bt_sighup;
    private EditText txt_username, txt_password;
    private ParseContent parseContent;
    private final int loginTask = 1;
    private PreferenceHelper preferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parseContent = new ParseContent(this);
        preferenceHelper = new PreferenceHelper(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (preferenceHelper.getIsLogin()){
            Intent in = new Intent(Login.this, Main.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            this.finish();
        }


        bt_login = (Button) findViewById(R.id.btnLogin);
        bt_sighup = (Button) findViewById(R.id.btnDaftar);
        txt_username = (EditText) findViewById(R.id.txtUsername);
        txt_password = (EditText) findViewById(R.id.txtPassword);

        bt_sighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Daftar.class);
                startActivity(intent);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();
                if (username.equals("") && password.equals("")){
                    Toast.makeText(Login.this, "Username dan Password harus di Isi", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        login();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    private void login() throws IOException, JSONException {
        if (!Utils.isNetworkAvailable(Login.this)){
            Toast.makeText(Login.this, "Internet is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.showSimpleProgressDialog(Login.this);
        final HashMap<String, String> map = new HashMap<>();
        map.put(ConfigConstants.Params.USERNAME, txt_username.getText().toString());
        map.put(ConfigConstants.Params.PASSWORD, txt_password.getText().toString());
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void[] params) {
                String response="";
                try {
                    HttpRequest req = new HttpRequest(ConfigConstants.ServiceType.LOGIN);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (IOException e) {
                    response = e.getMessage();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("newwwss", result);
                onTaskComplete(result, loginTask);
            }
        }.execute();
    }

    private void onTaskComplete(String response, int task) {
        Log.d("response", response.toString());
        Utils.removeSimpleProgressDialog();
        switch (task){
            case loginTask:

                if (parseContent.isSuccess(response)){

                    parseContent.saveInfo(response);
                    Toast.makeText(Login.this, "Login Success...!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, Main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(Login.this, parseContent.getErrorMessage(response), Toast.LENGTH_SHORT).show();
                }
        }
    }
}