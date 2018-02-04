package com.mail.pengaduanmail;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class Daftar extends Activity {

    private EditText etNama, etEmail, etPassword ,etHobby;
    private Button btDaftar;
    private ParseContent parseContent;
    private PreferenceHelper preferenceHelper;
    private final int RegTask = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        preferenceHelper = new PreferenceHelper(this);
        parseContent = new ParseContent(this);

        if (preferenceHelper.getIsLogin()){
            Intent intent = new Intent(Daftar.this, Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }

        etNama = (EditText)findViewById(R.id.txtUser);
        etEmail = (EditText)findViewById(R.id.txtEmail);
        etPassword = (EditText)findViewById(R.id.txtPass);
        etHobby = (EditText)findViewById(R.id.txtHobby);

        btDaftar = (Button)findViewById(R.id.btnDaf);

        btDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    register();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void register() throws IOException, JSONException {
        if (!Utils.isNetworkAvailable(Daftar.this)){
            Toast.makeText(Daftar.this, "Internet is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.showSimpleProgressDialog(Daftar.this);
        final HashMap<String, String> map = new HashMap<>();
        map.put(ConfigConstants.Params.NAME, etNama.getText().toString());
        map.put(ConfigConstants.Params.HOBBY, etHobby.getText().toString());
        map.put(ConfigConstants.Params.USERNAME, etEmail.getText().toString());
        map.put(ConfigConstants.Params.PASSWORD, etPassword.getText().toString());
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void[] params) {
                String response="";
                try {
                    HttpRequest req = new HttpRequest(ConfigConstants.ServiceType.REGISTER);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (IOException e) {
                    response = e.getMessage();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("newwwss", result);
                onTaskComplete(result, RegTask);
            }
        }.execute();
    }

    private void onTaskComplete(String response, int task) {
        Log.d("responsejson", response.toString());
        Utils.removeSimpleProgressDialog();
        switch (task){
            case RegTask:

                if (parseContent.isSuccess(response)){
                    parseContent.saveInfo(response);
                    Toast.makeText(Daftar.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Daftar.this, Main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    this.finish();
                } else {
                    Toast.makeText(Daftar.this, parseContent.getErrorMessage(response), Toast.LENGTH_SHORT).show();
                }
        }
    }
}
