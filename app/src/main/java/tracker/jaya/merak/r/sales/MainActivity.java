package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static android.os.Build.VERSION_CODES.KITKAT;
import static tracker.jaya.merak.r.sales.LoginActivity.ModeAddStore;
import static tracker.jaya.merak.r.sales.LoginActivity.SLocationNow;
import static tracker.jaya.merak.r.sales.LoginActivity.SSalesID;
import static tracker.jaya.merak.r.sales.LoginActivity.SStatus;
import static tracker.jaya.merak.r.sales.LoginActivity.Section;
import static tracker.jaya.merak.r.sales.LoginActivity.StatusStoreAddress;
import static tracker.jaya.merak.r.sales.LoginActivity.StatusStoreName;
import static tracker.jaya.merak.r.sales.LoginActivity.StoreLatLong;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {

    public static final String SumStore = "SumStore";
    Button Btn_Store_Location;
    Button btn_to_maps_add_Store;
    ListView List_Store;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String stringJson;
    int  Imenu, IDATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Btn_Store_Location = findViewById(R.id.Btn_Store_Location);
        btn_to_maps_add_Store = findViewById(R.id.btn_to_maps_add_Store);
        List_Store = findViewById(R.id.List_Store);
        int ITitle = btn_to_maps_add_Store.getLayoutParams().height;
        Log.e("Itle", "onCreate: " + ITitle);
        btn_to_maps_add_Store.setOnClickListener(this);
        Btn_Store_Location.setBackgroundResource(R.drawable.texttitel);
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
        List_Store.setOnItemClickListener(this);
        liststorelocation();
        Imenu = 1;
        IDATA = 0;
    }


    @SuppressLint("ApplySharedPref")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_maps_add_Store:
                editor = sharedPreferences.edit();
                editor.putString(ModeAddStore, "0");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

    }

    private void liststorelocation() {


        @SuppressLint("StaticFieldLeak")
        class LISTSTORELOCATION extends AsyncTask<Void, Void, String> {

            private final String SPregistBy = sharedPreferences.getString(SSalesID, "");
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Membuka  Data", "Mohon Tunggu...",
                        false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                stringJson = s;
                showliststorelocation();
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();

                params.put(Akses.ListStore4, SPregistBy);
                res = rh.sendPostRequest(Akses.URL_LIST_STORE_LOCATION, params);

                Log.e("Store ", "StoreLocatiion: " + res);
                return res;
            }
        }

        LISTSTORELOCATION LSL = new LISTSTORELOCATION();
        LSL.execute();
    }

    @SuppressLint("SetTextI18n")
    private void showliststorelocation() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> arrayListStoreLocation = new ArrayList<>();
        ArrayList<HashMap<String, String>> KosongKosong1 = new ArrayList<>();
        try {
            jsonObject = new JSONObject(stringJson);
            JSONArray result = jsonObject.getJSONArray(Akses.TAG_JSON_ARRAY);
            IDATA = result.length();
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String SringJsonstoreId, SringJsonstoreName, SringJsonaddress, SringJsonlocation, SringJsontimestamp, SringJsonpiuting, SringJsonkreditlimit;
                HashMap<String, String> hashMapStore = new HashMap<>();
                SringJsonstoreId = jo.getString(Akses.ListStore0);
                SringJsonstoreName = jo.getString(Akses.ListStore1);
                SringJsonaddress = jo.getString(Akses.ListStore2);
                SringJsontimestamp = jo.getString(Akses.ListStore6);
                SringJsonlocation = jo.getString(Akses.ListStore3);
                SringJsonpiuting= jo.getString(Akses.ListStore7);
                SringJsonkreditlimit= jo.getString(Akses.ListStore8);
                hashMapStore.put(Akses.ListStore0, SringJsonstoreId);
                hashMapStore.put(Akses.ListStore1, "Nama Toko: " + SringJsonstoreName);
                hashMapStore.put(Akses.ListStore2, "Alamat: " + SringJsonaddress);
                hashMapStore.put(Akses.ListStore6, "Pemilik: " + SringJsontimestamp);
                hashMapStore.put(Akses.ListStore8, "Kredit Limit: " + SringJsonkreditlimit);
                hashMapStore.put(Akses.ListStore7, "Piutang: " + SringJsonpiuting);
                hashMapStore.put(Akses.ListStore3, SringJsonlocation);
                arrayListStoreLocation.add(hashMapStore);
                ListAdapter listAdapterStoreLocation;
                listAdapterStoreLocation = new SimpleAdapter(MainActivity.this,
                        arrayListStoreLocation, R.layout.item_store,
                        new String[]{Akses.ListStore0, Akses.ListStore1, Akses.ListStore2, Akses.ListStore6, Akses.ListStore3,Akses.ListStore8,Akses.ListStore7},
                        new int[]{R.id.TVstoreId, R.id.TVstoreName, R.id.TVaddress, R.id.TVOwnName, R.id.TVlocation,R.id.TVtimestamp,R.id.status});
                List_Store.setAdapter(listAdapterStoreLocation);
            }
            editor = sharedPreferences.edit();
            editor.putInt(String.valueOf(SumStore), result.length());
            editor.apply();
            if (result.length() == 0) {
                HashMap<String, String> Kosong1 = new HashMap<>();
                Kosong1.put("Title", "Data Toko Masih Kosong");

                Kosong1.put("Diskrip", "Tambahkan Toko?");
                KosongKosong1.add(Kosong1);
                ListAdapter listAdapterStoreLocation;
                listAdapterStoreLocation = new SimpleAdapter(MainActivity.this,
                        KosongKosong1, R.layout.item_kosong,
                        new String[]{"Title", "Diskrip"},
                        new int[]{R.id.Kosong_Title, R.id.Kosong_Diskrip});
                List_Store.setAdapter(listAdapterStoreLocation);
            }
            if (result.length()>=Akses.LimitStore){
                btn_to_maps_add_Store.setVisibility(View.INVISIBLE);
            }
            else {
                btn_to_maps_add_Store.setVisibility(View.VISIBLE);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void liststoreprodustsales() {


        @SuppressLint("StaticFieldLeak")
        class LISTSTOREPRODUCTSALES extends AsyncTask<Void, Void, String> {

            private final String SPregistBy = sharedPreferences.getString(SSalesID,
                    "");
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Membuka  Data", "Mohon Tunggu...",
                        false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                stringJson = s;
                Log.e( "onPostExecute: ",s );
                showliststoreprodustsales();
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res = "a";
                Proses rh = new Proses();
                params.put(Akses.ListSales0, SPregistBy);
                switch (Imenu) {
                    case 2:
                        res = rh.sendPostRequest(Akses.URL_LIST_TASK_PEND, params);
                        break;
                    case 3:
                        res = rh.sendPostRequest(Akses.URL_LIST_TASK_SEND, params);
                        break;
                }

                Log.e("Store ", SPregistBy + "StoreLocatiion: " + res);
                return res;
            }
        }

        LISTSTOREPRODUCTSALES LSPS = new LISTSTOREPRODUCTSALES();
        LSPS.execute();
    }

    private void showliststoreprodustsales() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> arrayListStoreLocation = new ArrayList<>();
        ArrayList<HashMap<String, String>> KosongKosong = new ArrayList<>();
        try {
            jsonObject = new JSONObject(stringJson);
            JSONArray result = jsonObject.getJSONArray(Akses.TAG_JSON_ARRAY);
            IDATA = result.length();
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String SringJsonProductId, SringJsonProductName, SringJsonStockProduct, SringJsonEtc;
                HashMap<String, String> hashMapStore = new HashMap<>();
                SringJsonProductId = jo.getString(Akses.ListTask0);
                SringJsonProductName = jo.getString(Akses.ListTask1);
                SringJsonStockProduct = jo.getString(Akses.ListTask2);
                SringJsonEtc = jo.getString(Akses.ListTask3);
                hashMapStore.put(Akses.ListTask0, SringJsonProductId);
                hashMapStore.put(Akses.ListTask1, "StoreName: " + SringJsonProductName);
                hashMapStore.put(Akses.ListTask2, "Quality: " + SringJsonStockProduct);
                hashMapStore.put(Akses.ListTask3, "Status: " + SringJsonEtc);
                arrayListStoreLocation.add(hashMapStore);
                ListAdapter listAdapterStoreLocation;
                listAdapterStoreLocation = new SimpleAdapter(MainActivity.this,
                        arrayListStoreLocation, R.layout.item_store,
                        new String[]{Akses.ListTask0, Akses.ListTask1, Akses.ListTask2, Akses.ListTask3},
                        new int[]{R.id.TVstoreId, R.id.TVstoreName, R.id.TVaddress, R.id.TVtimestamp});
                List_Store.setAdapter(listAdapterStoreLocation);
            }
            if (result.length() == 0) {
                IDATA = 0;
                HashMap<String, String> Kosong = new HashMap<>();
                if (Imenu == 2) {
                    Kosong.put("Title", "Data Tugas Masih Kosong");
                } else if (Imenu == 3) {
                    Kosong.put("Title", "Data Laporan Masih Kosong");
                }
                Kosong.put("Diskrip", " ");
                KosongKosong.add(Kosong);
                ListAdapter listAdapterStoreLocation;
                listAdapterStoreLocation = new SimpleAdapter(MainActivity.this, KosongKosong,
                        R.layout.item_kosong, new String[]{"Title", "Diskrip"},
                        new int[]{R.id.Kosong_Title, R.id.Kosong_Diskrip});
                List_Store.setAdapter(listAdapterStoreLocation);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updtlocationtask(final String S0, final String S2) {


        @SuppressLint("StaticFieldLeak")
        class UPDTLOCATIONTASK extends AsyncTask<Void, Void, String> {

            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Membuka  Data", "Mohon Tunggu...",
                        false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                liststoreprodustsales();
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();
                params.put(Akses.ListTask0, S0);
                params.put(Akses.ListTask3, "send");
                params.put(Akses.ListTask4, S2);
                res = rh.sendPostRequest(Akses.URL_LIST_UPDATE_TASK, params);

                Log.e("TASK ", "uPDATE TASK: " + res);
                return res;
            }
        }

        UPDTLOCATIONTASK ULT = new UPDTLOCATIONTASK();
        ULT.execute();
    }

    private void reportempty(final String S0,final String S1) {


        @SuppressLint("StaticFieldLeak")
        class REPORTEMPTY extends AsyncTask<Void, Void, String> {
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Membuka Menu Order",
                        "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.e("onPostExecute: ", S0+" "+S1);
                Intent intent=new Intent(MainActivity.this,OrderActivity.class);
                intent.putExtra("Json",s);
                intent.putExtra("id",S0);
                intent.putExtra("nama",S1);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... v) {
                String res;
                Proses rh = new Proses();
                res = rh.sendGetRequest2();

                Log.e("TASK ", "Lapor Item: " + res);
                return res;
            }
        }

        REPORTEMPTY RE = new REPORTEMPTY();
        RE.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final HashMap map = (HashMap) parent.getItemAtPosition(position);
        if (IDATA > 0) {
            Log.e("GATA", "onItemClick: " + IDATA);
            switch (Imenu) {
                case 1:
                    final DialogInterface.OnClickListener dialog1 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    String MapsStoreId =  map.get(Akses.ListStore0).toString();
                                    String MapsStore = map.get(Akses.ListStore1).toString();
                                    reportempty(MapsStoreId,MapsStore );
                                    dialogInterface.dismiss();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    String MapsStoreName = null;
                                    if (Build.VERSION.SDK_INT >= KITKAT) {
                                        MapsStoreName = Objects.requireNonNull(
                                                map.get(Akses.ListStore1)).toString();
                                    }
                                    String MapsPosition = null;
                                    if (Build.VERSION.SDK_INT >= KITKAT) {
                                        MapsPosition = Objects.requireNonNull(map.get(
                                                Akses.ListStore3)).toString();
                                    }
                                    String MapsStoreAddress = null;
                                    if (Build.VERSION.SDK_INT >= KITKAT) {
                                        MapsStoreAddress = Objects.requireNonNull(
                                                map.get(Akses.ListStore2)).toString();
                                    }
                                    String snipStorename = null;
                                    if (Build.VERSION.SDK_INT >= KITKAT) {
                                        snipStorename = MapsStoreName.substring(
                                                Objects.requireNonNull(MapsStoreName).indexOf(
                                                        ":") + 1, MapsStoreName.length());
                                    }
                                    String snipAdress = null;
                                    if (Build.VERSION.SDK_INT >= KITKAT) {
                                        snipAdress = MapsStoreAddress.substring(
                                                Objects.requireNonNull(MapsStoreAddress).indexOf(
                                                        ":") + 1,
                                                MapsStoreAddress.length());
                                    }
                                    Log.e("Image", "onItemClick: " + Imenu);
                                    editor = sharedPreferences.edit();
                                    editor.putString(StoreLatLong, MapsPosition);
                                    editor.putString(StatusStoreName, snipStorename);
                                    editor.putString(StatusStoreAddress, snipAdress);
                                    editor.putString(ModeAddStore, "1");
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this,
                                            MapsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    dialogInterface.dismiss();
                                    break;
                            }

                        }
                    };
                    AlertDialog.Builder Builder1 = new AlertDialog.Builder(this);
                    Builder1.setMessage("Keperluan").setPositiveButton("Order",
                            dialog1).setNegativeButton("Lokasi", dialog1).show();
                    break;
                case 2:
                    final DialogInterface.OnClickListener dialog2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    final String SendID = String.valueOf(map.get(Akses.ListTask0));
                                    updtlocationtask(SendID,
                                            sharedPreferences.getString(SLocationNow, ""));
                                    dialogInterface.dismiss();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.dismiss();
                                    break;
                            }

                        }
                    };
                    AlertDialog.Builder Builder2 = new AlertDialog.Builder(this);
                    Builder2.setMessage("Terkirim?").setPositiveButton("Ya",
                            dialog2).setNegativeButton("Tidak", dialog2).show();

                    break;
            }
        }


    }

    @Override
    public void onBackPressed() {
        final DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        editor = sharedPreferences.edit();
                        editor.putString(SStatus, "Keluar");
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        dialogInterface.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        moveTaskToBack(true);
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialogInterface.dismiss();
                        break;
                }

            }
        };
        AlertDialog.Builder Builder = new AlertDialog.Builder(this);
        Builder.setMessage("Selesai?").setPositiveButton("Keluar", dialog).setNeutralButton(
                "Tetap Disini", dialog).setNegativeButton("Tutup", dialog).show();
//        super.onBackPressed();
    }

    private int substractDates(Date date1, Date date2) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormatDIFERENCE = new SimpleDateFormat(
                "dd");
        int Idate1 = Integer.parseInt(simpleDateFormatDIFERENCE.format(date1.getTime()));
        int Idate2 = Integer.parseInt(simpleDateFormatDIFERENCE.format(date2.getTime()));
        return Idate1 - Idate2;
    }
}
