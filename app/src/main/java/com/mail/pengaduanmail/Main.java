package com.mail.pengaduanmail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mail.pengaduanmail.R;
import com.mail.pengaduanmail.utils.PreferenceHelper;

public class Main extends AppCompatActivity implements View.OnClickListener{

    ImageView profil, input, aduan, allAduan;
    TextView textNama;
    PreferenceHelper preferenceHelper;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        preferenceHelper = new PreferenceHelper(this);
        email = preferenceHelper.getName();

        textNama = (TextView)findViewById(R.id.tvNama);
        textNama.setText(email);

        profil = (ImageView) findViewById(R.id.img_profil);
        input = (ImageView) findViewById(R.id.img_input);
        aduan = (ImageView) findViewById(R.id.img_aduan);
        allAduan = (ImageView) findViewById(R.id.img_all_aduan);

        profil.setOnClickListener(this);
        input.setOnClickListener(this);
        aduan.setOnClickListener(this);
        allAduan.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int menu = view.getId();

        if (menu == R.id.img_profil) {
            Intent i = new Intent(Main.this, Profil.class);
            startActivity(i);
        } else if (menu == R.id.img_input) {
            Intent i = new Intent(Main.this, PostAduan.class);
            startActivity(i);
        } else if (menu == R.id.img_aduan) {
            Intent i = new Intent(Main.this, ListAduan.class);
            startActivity(i);
        } else if (menu == R.id.img_all_aduan) {
            preferenceHelper.putIsLogin(false);
            Intent intent = new Intent(Main.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Main.this.finish();
        }
    }
}
