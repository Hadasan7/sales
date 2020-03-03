package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static tracker.jaya.merak.r.sales.LoginActivity.SDepoID;
import static tracker.jaya.merak.r.sales.LoginActivity.Section;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,EditText.OnKeyListener{

    String stringJson,idtoko,namatoko,itemid,itemname,Stagihan;
    Spinner SpnItem;
    TextView NamaToko,Harga,Tagihan;
    EditText EdtJumlah;
    Double HARGA= Double.valueOf(0);
    Button order;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
        stringJson= String.valueOf(getIntent().getExtras().get("Json"));
        idtoko= String.valueOf(getIntent().getExtras().get("id"));
        namatoko= String.valueOf(getIntent().getExtras().get("nama"));
        Log.e( "onCreate: ", stringJson);
        SpnItem=findViewById(R.id.SpnItem);
        NamaToko=findViewById(R.id.NamaToko);
        Harga=findViewById(R.id.Harga);
        Tagihan=findViewById(R.id.Tagihan);
        EdtJumlah=findViewById(R.id.EdtJumlah);
        order=findViewById(R.id.order);
        showlistitem();
        SpnItem.setOnItemSelectedListener(this);
        EdtJumlah.setOnKeyListener(this);
        NamaToko.setText(namatoko);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cekjumlah=EdtJumlah.getText().toString();
                if (cekjumlah.equals("")){
                    Toast.makeText(OrderActivity.this, "Masukan Jumlah Barang Yang Dipesan", Toast.LENGTH_SHORT).show();
                }else {
                    tambahorder();
                }
            }
        });
    }

    private void showlistitem() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> arrayListStoreLocation = new ArrayList<>();
        Log.e( "showlistitem: ",stringJson );
        try {
            jsonObject = new JSONObject(stringJson);
            JSONArray result = jsonObject.getJSONArray(Akses.TAG_JSON_ARRAY);
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String SringJsonDepoId, SringJsonDepoName;
                HashMap<String, String> hashMapDepo = new HashMap<>();
                SringJsonDepoId = jo.getString(Akses.ListItem0);
                SringJsonDepoName = jo.getString(Akses.ListItem1);
                hashMapDepo.put(Akses.ListItem0, SringJsonDepoId);
                hashMapDepo.put(Akses.ListItem1, SringJsonDepoName);
                arrayListStoreLocation.add(hashMapDepo);
                ListAdapter listAdapterDepo;
                listAdapterDepo = new SimpleAdapter(OrderActivity.this,
                        arrayListStoreLocation, R.layout.item_depo,
                        new String[]{Akses.ListItem0, Akses.ListItem1},
                        new int[]{R.id.TVstoreId, R.id.TVstoreName});
                SpnItem.setAdapter((SpinnerAdapter) listAdapterDepo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String Pilih= SpnItem.getItemAtPosition(position).toString();
//        Log.e("Index: ", String.valueOf(Pilih.indexOf("itemId=",0)));
        if (Pilih.indexOf("itemId=",0)>10) {
            itemid = Pilih.substring(Pilih.indexOf("itemId=", 0) + 7, Pilih.length() - 1);
            itemname = Pilih.substring(Pilih.indexOf("nama=", 0) + 5, Pilih.indexOf(",", 0));
        }else {
            itemname = Pilih.substring(Pilih.indexOf("nama=", 0) + 5, Pilih.length() - 1);
            itemid = Pilih.substring(Pilih.indexOf("itemId=", 0) + 7, Pilih.indexOf(",", 0));
        }
 //       Log.e( "ItemName: ",itemid );
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringJson);
            JSONArray result = jsonObject.getJSONArray(Akses.TAG_JSON_ARRAY);
            JSONObject jo = result.getJSONObject(position);
            Double harga = Double.valueOf(jo.getString(Akses.ListItem2));
            String A= String.valueOf((harga));
            String B=currencyFormat(A);
            Harga.setText("Rp."+B+",00");
            HARGA=harga;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Double jumlah=0.0;
        if (!EdtJumlah.getText().equals("")){
            //Log.e("onKey: ",EdtJumlah.getText().toString() );
            String value=EdtJumlah.getText().toString();
            if (!value.equals("")) {
                jumlah = Double.valueOf(value);
            }else {
                jumlah=0.0;
            }
            Double harga= HARGA;
            Log.e( "onKey: ",jumlah+" x "+harga +" = "+(jumlah*harga));
            String A= String.valueOf((jumlah*harga));
            String B=currencyFormat(A);
            Tagihan.setText("Rp."+B+",00");
            Stagihan= String.valueOf(jumlah*harga);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //Log.e( "onKey: "," "+event );
        Double jumlah=0.0;
        if (!EdtJumlah.getText().equals("")){
            //Log.e("onKey: ",EdtJumlah.getText().toString() );
            String value=EdtJumlah.getText().toString();
            if (!value.equals("")) {
                jumlah = Double.valueOf(value);
            }else {
                jumlah=0.0;
            }
            Double harga= HARGA;
            Log.e( "onKey: ",jumlah+" x "+harga +" = "+(jumlah*harga));
            String A= String.valueOf((jumlah*harga));
            String B=currencyFormat(A);
            Tagihan.setText("Rp."+B+",00");
            Stagihan= String.valueOf(jumlah*harga);
        }
/*
        if (EdtJumlah.getText().equals("")){

        }else {

        }
*/

        return false;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(amount));
    }

    private void tambahorder() {


        @SuppressLint("StaticFieldLeak")
        class ORDER extends AsyncTask<Void, Void, String> {

            private final String Sidtotko=idtoko;
            private final String Sitemid=itemid;
            private final String Sitemname=itemname;
            private final String Siddepo=sharedPreferences.getString(SDepoID,"");
            private final String Sjumlah=EdtJumlah.getText().toString();
            private final String Sbill= String.valueOf(Stagihan);


            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderActivity.this, "Sedang Memesan",
                        "Mohon Tunggu...", false, false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                switch (s){
                    case  "Berhasil Menambahkan Order" :
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }
                loading.dismiss();
                Toast.makeText(OrderActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();

                params.put(Akses.ListStore0, Sidtotko);
                params.put(Akses.ListItem0, Sitemid);
                params.put(Akses.ListItem3, Sitemname);
                params.put(Akses.ListDepo0, Siddepo);
                params.put(Akses.ListItem4, Sjumlah);
                params.put(Akses.ListItem5, Sbill);
                res = rh.sendPostRequest(Akses.URL_ORDER, params);

                Log.e("Order ", "doInBackground: " + res);
                return res;
            }
        }

        ORDER TS = new ORDER();
        TS.execute();
    }

}
