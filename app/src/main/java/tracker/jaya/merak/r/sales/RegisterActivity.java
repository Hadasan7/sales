package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText EdtNamaLengkap;
    EditText EdtUserName;
    EditText EdtNoHp;
    EditText EdtRegEmail;
    EditText EdtRegPassword;
    EditText EdtTryPassword;
    Button btn_create_new_account, btnback_login;
    Spinner SpnDepo;
    String DepoID;
    private String stringJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EdtNamaLengkap = findViewById(R.id.EdtNamaLengkap);
        EdtUserName = findViewById(R.id.EdtUserName);
        EdtNoHp = findViewById(R.id.EdtNoHp);
        EdtRegEmail = findViewById(R.id.EdtRegEmail);
        EdtRegPassword = findViewById(R.id.EdtRegPassword);
        EdtTryPassword = findViewById(R.id.EdtTryPassword);
        SpnDepo = findViewById(R.id.SpnDepo);
        btn_create_new_account = findViewById(R.id.btn_create_new_account);
        btnback_login = findViewById(R.id.btnback_login);
        btn_create_new_account.setOnClickListener(this);
        btnback_login.setOnClickListener(this);
        listdepo();
        SpnDepo.setOnItemSelectedListener(this);
//make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }


    private void tambahsales() {


        @SuppressLint("StaticFieldLeak")
        class TAMBAHSALES extends AsyncTask<Void, Void, String> {

            private final String SNamaLengkap = EdtNamaLengkap.getText().toString();
            private final String SEdtUserName = EdtUserName.getText().toString();
            private final String SEdtNoHp = EdtNoHp.getText().toString();
            private final String SEdtRegEmail = EdtRegEmail.getText().toString();
            private final String SEdtRegPassword = EdtRegPassword.getText().toString();
            private final String SSpnDepo = DepoID;
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, "Sedang Mendaftarkan",
                        "Mohon Tunggu...", false, false);
                
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                switch (s){
                    case  "Registrasi berhasil!" :
                        EdtNamaLengkap.setText("");
                        EdtUserName.setText("");
                        EdtNoHp.setText("");
                        EdtRegEmail.setText("");
                        EdtRegPassword.setText("");
                        EdtTryPassword.setText("");
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }
                loading.dismiss();
                Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();

                params.put(Akses.ListSales1, SNamaLengkap);
                params.put(Akses.ListSales2, SEdtUserName);
                params.put(Akses.ListSales3, SEdtNoHp);
                params.put(Akses.ListSales4, SEdtRegEmail);
                params.put(Akses.ListSales5, SEdtRegPassword);
                params.put(Akses.ListSales6, SSpnDepo);
                res = rh.sendPostRequest(Akses.URL_TAMBAH_SALES, params);

                Log.e("Lokasi ", "doInBackground: " + res);
                return res;
            }
        }

        TAMBAHSALES TS = new TAMBAHSALES();
        TS.execute();
    }


    private void listdepo() {


        @SuppressLint("StaticFieldLeak")
        class LISTDEPO extends AsyncTask<Void, Void, String> {

            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, "LOADING", "Mohon Tunggu...",
                        false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                stringJson = s;
                showlistdepo();
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... v) {
                String res;
                Proses rh = new Proses();

                res = rh.sendGetRequest();

                Log.e("Lokasi ", "doInBackground: " + res);
                return res;
            }
        }

        LISTDEPO LD = new LISTDEPO();
        LD.execute();
    }

    private void showlistdepo() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> arrayListStoreLocation = new ArrayList<>();
        try {
            jsonObject = new JSONObject(stringJson);
            JSONArray result = jsonObject.getJSONArray(Akses.TAG_JSON_ARRAY);
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String SringJsonDepoId, SringJsonDepoName;
                HashMap<String, String> hashMapDepo = new HashMap<>();
                SringJsonDepoId = jo.getString(Akses.ListDepo0);
                SringJsonDepoName = jo.getString(Akses.ListDepo1);
                hashMapDepo.put(Akses.ListDepo0, SringJsonDepoId);
                hashMapDepo.put(Akses.ListDepo1, SringJsonDepoName);
                arrayListStoreLocation.add(hashMapDepo);
                ListAdapter listAdapterDepo;
                listAdapterDepo = new SimpleAdapter(RegisterActivity.this,
                        arrayListStoreLocation, R.layout.item_depo,
                        new String[]{Akses.ListDepo0, Akses.ListDepo1},
                        new int[]{R.id.TVstoreId, R.id.TVstoreName});
                SpnDepo.setAdapter((SpinnerAdapter) listAdapterDepo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_new_account:
                if (EdtNamaLengkap.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Masukkan Nama Lengkap", Toast.LENGTH_LONG).show();
                }else if (EdtUserName.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Masukkan Username", Toast.LENGTH_LONG).show();
                }else if (EdtNoHp.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Masukkan No. HP", Toast.LENGTH_LONG).show();
                }else if (EdtRegEmail.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Masukkan Email", Toast.LENGTH_LONG).show();
                }else if (EdtRegPassword.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Masukkan Password", Toast.LENGTH_LONG).show();
                } else {
                    tambahsales();
                }
                break;
            case R.id.btnback_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //               moveTaskToBack(true);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String Pilih = SpnDepo.getItemAtPosition(position).toString();
        int IndAwal=Pilih.indexOf("d=")+2;
        int IndAkhr;
        if (IndAwal<10){
             IndAkhr=Pilih.indexOf(",");
        }else {
             IndAkhr = Pilih.length() - 1;
        }
        Log.e("Hasil Depo", Pilih+""+IndAwal+""+IndAkhr);
        String SubPilih = Pilih.substring(IndAwal,IndAkhr);
        Log.e("Hasil Depo",""+SubPilih);
        DepoID = SubPilih;


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
