package com.mail.pengaduanmail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mail.pengaduanmail.R;
import com.mail.pengaduanmail.app.AppController;
import com.mail.pengaduanmail.app.Koneksi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailAduan extends AppCompatActivity implements OnMapReadyCallback{

    TextView textId, texJudul, texDesk;
    ImageView imgDetail;
    String Getid;

    MapFragment mapFragment;
    GoogleMap gMap;
    String title = "Posisi Aduan";
    MarkerOptions markerOptions = new MarkerOptions();
    CameraPosition cameraPosition;
    LatLng latLngAduan;
    String deskrip, judul, gambar;

    public static final String LAT = "latitude";
    public static final String LNG = "longitude";

    String tag_json_obj = "json_obj_req";

    Koneksi conn = new Koneksi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_aduan);

        Intent i = getIntent();
        Getid = i.getStringExtra("id");

        texJudul = (TextView)findViewById(R.id.tvJudulDet);
        texDesk = (TextView)findViewById(R.id.tvDeskDet);
        imgDetail = (ImageView)findViewById(R.id.img_Detail);

        textId = (TextView)findViewById(R.id.tvId);
        textId.setText(Getid);

        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapDet);
        mapFragment.getMapAsync(this);
    }

    private void getMarker(){
        String url_detail = conn.BASE_URL+"get_aduan.php?id="+Getid;
        StringRequest strReq = new StringRequest(Request.Method.POST, url_detail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String getObject = jObj.getString("aduan");
                    JSONArray jsonArray = new JSONArray(getObject);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    judul = jsonObject.getString("judul");
                    deskrip = jsonObject.getString("image_tag");
                    gambar = jsonObject.getString("image_data");
                    latLngAduan = new LatLng(Double.parseDouble(jsonObject.getString(LAT)), Double.parseDouble(jsonObject.getString(LNG)));
                    texJudul.setText(judul);
                    texDesk.setText(deskrip);

                    Picasso.with(DetailAduan.this)
                            .load(gambar)
                            .into(imgDetail);

                    addMarker(latLngAduan, title);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
                Toast.makeText(DetailAduan.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
    });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void addMarker(LatLng latLng, String title) {
        markerOptions.position(latLng);
        markerOptions.title(title);
        gMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng center = new LatLng(-7.872195,110.2838473);
        cameraPosition = new CameraPosition.Builder().target(center).zoom(9).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        getMarker();
    }
}
