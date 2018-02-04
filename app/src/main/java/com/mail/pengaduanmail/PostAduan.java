package com.mail.pengaduanmail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mail.pengaduanmail.R;
import com.mail.pengaduanmail.app.JSONParser;
import com.mail.pengaduanmail.app.Koneksi;
import com.mail.pengaduanmail.utils.PreferenceHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PostAduan extends AppCompatActivity {

    int PLACE_PICKER_REQUEST = 2;

    Button GetImageFromGalleryButton, UploadImageOnServerButton;
    ImageView ShowSelectedImage;
    Bitmap FixBitmap;
    String ImageTag = "image_tag" ;
    String ImageName = "image_data" ;
    String Perihal = "perihal";
    String Judul = "judul";
    String Latitude = "latitude";
    String Longitude = "longitude";
    String Email = "email";

    String ServerUploadPath ="http://192.168.43.2/pengaduan_mail/upload-image-server.php" ;
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;

    String emailSession, hal, lat, ling;

    String GetImageNameFromEditText, GetJudulAduan;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    boolean check = true;

    EditText JudulAduan, DeskripsiAduan, latitude, longitude;
    LatLng latLng;
    Koneksi koneksi = new Koneksi();
    Spinner kategori;
    public static final String TAG_ID = "id";
    public static final String TAG_HAL = "perihal";
    final ArrayList<HashMap<String, String>> ArrPerihal = new ArrayList<>();

    SimpleAdapter simpleAdapter;
    JSONParser jsonParser = new JSONParser();
    PreferenceHelper preferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_aduan);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkStoragePermission();
        }

        GetImageFromGalleryButton = (Button)findViewById(R.id.button);
        UploadImageOnServerButton = (Button)findViewById(R.id.button2);

        JudulAduan = (EditText)findViewById(R.id.etJudulAduan);
        ShowSelectedImage = (ImageView)findViewById(R.id.imageView);
        DeskripsiAduan = (EditText)findViewById(R.id.etDeskripAduan);
        kategori = (Spinner)findViewById(R.id.kategori);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);

        preferenceHelper = new PreferenceHelper(this);
        emailSession = preferenceHelper.getName();

        byteArrayOutputStream = new ByteArrayOutputStream();

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);

            }
        });


        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = DeskripsiAduan.getText().toString();
                GetJudulAduan = JudulAduan.getText().toString();

                UploadImageToServer();

            }
        });

        KategoriClick();
        onLokasiClick();

        new Spin().execute();
    }

    private void onLokasiClick() {
        LinearLayout lokasi = (LinearLayout)findViewById(R.id.lokasi);
        if (lokasi != null){
            lokasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(PostAduan.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    class Spin extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String url_kategori = koneksi.BASE_URL+"kategori.php";
            JSONParser jsonParser = new JSONParser();

            List<NameValuePair> param = new ArrayList<>();
            JSONObject json = jsonParser.makeHttpRequest(url_kategori, "GET", param);

            try {
                JSONArray string_json = json.getJSONArray("posts");
                for (int i = 0; i < string_json.length(); i++){
                    JSONObject c = string_json.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String perihal = c.getString(TAG_HAL);

                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_ID, id);
                    map.put(TAG_HAL, perihal);
                    ArrPerihal.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            simpleAdapter = new SimpleAdapter(PostAduan.this, ArrPerihal, R.layout.item_spinner, new String[]{TAG_HAL}, new int[]{R.id.item_kategori});
            kategori.setAdapter(simpleAdapter);
        }
    }

    private void KategoriClick() {
        kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hal = ArrPerihal.get(position).get(TAG_HAL).toString();
                Toast.makeText(PostAduan.this, "Iyes "+hal, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                FixBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ShowSelectedImage.setImageBitmap(FixBitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }
        } else if (RC == 2 && RQC == RESULT_OK && I != null) {
            Place place = PlacePicker.getPlace(I, this);
            latLng = place.getLatLng();
            latitude.setText(String.valueOf(latLng.latitude));
            longitude.setText(String.valueOf(latLng.longitude));
            lat = latitude.getText().toString().trim();
            ling = longitude.getText().toString().trim();
        }
    }

    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(PostAduan.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(PostAduan.this,string1,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PostAduan.this, ListAduan.class);
                startActivity(intent);
                finish();
            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);
                HashMapParams.put(ImageName, ConvertImage);
                HashMapParams.put(Perihal, hal);
                HashMapParams.put(Latitude, lat);
                HashMapParams.put(Longitude, ling);
                HashMapParams.put(Email, emailSession);
                HashMapParams.put(Judul, GetJudulAduan);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 99;

    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }
}
