package com.mail.pengaduanmail;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.mail.pengaduanmail.R;
import com.mail.pengaduanmail.app.AduanModel;
import com.mail.pengaduanmail.app.Koneksi;
import com.mail.pengaduanmail.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListAduan extends AppCompatActivity {

    private GridView grvAduan;
    PreferenceHelper preferenceHelper;
    String email;

    Koneksi conn = new Koneksi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_aduan);

        preferenceHelper = new PreferenceHelper(this);
        email = preferenceHelper.getName();

        String url = conn.BASE_URL+"get_aduan_by_email.php?email="+email;

        new JSONTask().execute(url);
        grvAduan = (GridView)findViewById(R.id.gvAduan);
    }

    private class JSONTask extends AsyncTask<String, String, List<AduanModel>> {

        private AduanModel aduanModel;

        @Override
        protected List<AduanModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine())!= null){
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJSON);
                JSONArray parentArray = parentObject.getJSONArray("aduan");

                List<AduanModel> aduanModelList = aduanModelList = new ArrayList<>();
                for (int i=0; i<parentArray.length(); i++){
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    aduanModel = new AduanModel();
                    aduanModel.setId(finalObject.getString("id"));
                    aduanModel.setPerihal(finalObject.getString("perihal"));
                    aduanModel.setJudul(finalObject.getString("judul"));
//                    aduanModel.setLatt(finalObject.getDouble("latitude"));
//                    aduanModel.setLongi(finalObject.getDouble("longitude"));

                    aduanModelList.add(aduanModel);
                }
                return aduanModelList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<AduanModel> result) {
            super.onPostExecute(result);

            AduanAdapter adapter = new AduanAdapter(getApplicationContext(), R.layout.row, result);

//            Buat intent untuk detail
            grvAduan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    aduanModel = (AduanModel)grvAduan.getAdapter().getItem(position);
                    String id_aduan = aduanModel.getId().toString();

                    Intent intent = new Intent(ListAduan.this, DetailAduan.class);
                    intent.putExtra("id", id_aduan);
                    startActivity(intent);
                }
            });

            grvAduan.setAdapter(adapter);
        }
    }

    private class AduanAdapter extends ArrayAdapter{
        private List<AduanModel>aduanModelList;
        private int resource;
        private LayoutInflater inflater;
        public AduanAdapter(Context context, int resource, List<AduanModel> objects) {
            super(context, resource, objects);
            aduanModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = inflater.inflate(resource, null);
            }

            TextView texAduan, texPerihal;

            texAduan = (TextView)convertView.findViewById(R.id.tvAduan);
            texPerihal = (TextView)convertView.findViewById(R.id.tvPerihal);

            texAduan.setText(aduanModelList.get(position).getJudul());
            texPerihal.setText(aduanModelList.get(position).getPerihal());

            return convertView;
        }
    }
}
