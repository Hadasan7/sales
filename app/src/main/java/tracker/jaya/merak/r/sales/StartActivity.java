package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;

import static tracker.jaya.merak.r.sales.LoginActivity.MainActive;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String Section = "mypref";
    private static final String DivideID = "DivideID";
    private static final String DivideStatus = "DivideStatus";
    Button btn_device_aktif, btn_Aktivasi_Device, btn_help;
    EditText edtDeviceCode, edtAktivasiDevice;
    String number;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MainActive=true;
        number = getMyPhoneNO();
        Log.e("DiviceId", number);
        btn_device_aktif = findViewById(R.id.btn_device_aktif);
        btn_Aktivasi_Device = findViewById(R.id.btn_Aktivasi_Device);
        btn_help = findViewById(R.id.btn_help);
        edtDeviceCode = findViewById(R.id.edtDeviceCode);
        edtAktivasiDevice = findViewById(R.id.edtAktivasiDevice);
        btn_device_aktif.setOnClickListener(this);
        btn_Aktivasi_Device.setOnClickListener(this);
        btn_help.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
        if (sharedPreferences.contains(DivideID)) {
            edtDeviceCode.setText(sharedPreferences.getString(DivideID, ""));
            edtDeviceCode.setEnabled(false);
        }
/*
        if (sharedPreferences.contains(DivideStatus)) {
            Intent intent = new Intent(StartActivity.this, ActivasiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
*/
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        // Versi Lama Scrip Bawah Hapus
        // ambilcode();
    }

    @SuppressLint("HardwareIds")
    private String getMyPhoneNO() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String codedevice(String s){
        String S1,S2,S3;
        S1=s.substring(0,5);
        S2=s.substring(6,10);
        S3=s.substring(11,s.length());
        Log.e( "codedevice: ", S1+"-"+S2+"-"+S3);
        String s1=new BigInteger(S1,16).toString(36);
        String s2=new BigInteger(S2,16).toString(36);
        String s3=new BigInteger(S3,16).toString(36);
        Log.e( "codedevice: ",  s1+"-"+s2+"-"+s3);
        return s1+"-"+s2+"-"+s3;
    }
    private String codeactivation(String s){
        String S1,S2,S3;
        S1=s.substring(0,5);
        S2=s.substring(6,10);
        S3=s.substring(11,s.length());
        String s1=new BigInteger(S1,16).toString(36);
        String s2=new BigInteger(S2,16).toString(36);
        String s3=new BigInteger(S3,16).toString(36);
        int i1=Integer.parseInt(s1,36)-1111;
        int i2=Integer.parseInt(s2,36)+3333;
        int i3=Integer.parseInt(s3,36)-5555;
        String p1=i1+""+i3;
        String p2=i2+"";
        String P1=new BigInteger(p1,10).toString(30);
        String P2=new BigInteger(p2,10).toString(30);
        Log.e("codeactivation: ", P1+"-"+P2);
        return P1+"-"+P2;
    }



    private void ambilcode() {


        @SuppressLint("StaticFieldLeak")
        class AMBILCODE extends AsyncTask<Void, Void, String> {
            private JSONObject jsonObject = null;

            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(StartActivity.this, "Sedang Mengambil",
                        "Mohon Tunggu...", false, false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals("Gagal")) {
                    Toast.makeText(StartActivity.this, "Gagal Mengambil",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    jsonObject = new JSONObject(s);
                    String jsonStringer = jsonObject.getString(Akses.ListAktif1);
                    edtDeviceCode.setText(jsonStringer);
                    edtDeviceCode.setEnabled(false);
                    editor = sharedPreferences.edit();
                    editor.putString(DivideID, jsonStringer);
                    editor.apply();
                    loading.dismiss();
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(
                            CLIPBOARD_SERVICE);
                    clipboard.setText(jsonStringer);
                    Toast.makeText(StartActivity.this, "Device Code Telah Dicopy",
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

/*
                //Scrip Hapus Untuk versi Lama
                Toast.makeText(StartActivity.this, "Device Telah diaktifkan",
                        Toast.LENGTH_SHORT).show();
                editor = sharedPreferences.edit();
                editor.putString(DivideStatus, s);
                editor.apply();
                Intent intent = new Intent(StartActivity.this, ActivasiActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
*/

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();

                params.put(Akses.ListAktif0, number);
                res = rh.sendPostRequest(Akses.URL_AKTIF_CODE, params);

                Log.e("Device Code ", res);
                return res;
            }
        }

        AMBILCODE AC = new AMBILCODE();
        AC.execute();
    }

    private void cekaktifasi() {


        @SuppressLint("StaticFieldLeak")
        class CEKAKTIFASI extends AsyncTask<Void, Void, String> {

            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(StartActivity.this, "Sedang Mengambil",
                        "Mohon Tunggu...", false, false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("AKTIF")) {
                    Toast.makeText(StartActivity.this, "Device Telah diaktifkan",
                            Toast.LENGTH_SHORT).show();
                    editor = sharedPreferences.edit();
                    editor.putString(DivideStatus, s);
                    editor.apply();
                    Intent intent = new Intent(StartActivity.this, ActivasiActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(StartActivity.this, "Gagal diaktifkan",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();

                params.put(Akses.ListAktif0, number);
                params.put(Akses.ListAktif1, edtDeviceCode.getText().toString());
                params.put(Akses.ListAktif2, edtAktivasiDevice.getText().toString());
                res = rh.sendPostRequest(Akses.URL_AKTIFKAN, params);

                Log.e("Device Code ", res);
                return res;
            }
        }

        CEKAKTIFASI CA = new CEKAKTIFASI();
        CA.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_device_aktif:
                //ambilcode();
                Log.e( "onClick: ", number);
                edtDeviceCode.setText(codedevice(number));
                edtDeviceCode.setEnabled(false);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(
                        CLIPBOARD_SERVICE);
                clipboard.setText(codedevice(number));
                editor = sharedPreferences.edit();
                editor.putString(DivideID, codedevice(number));
                editor.apply();
                Toast.makeText(StartActivity.this, "Device Code Telah Dicopy",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_Aktivasi_Device:
                if (edtDeviceCode.getText().toString().equals("")) {
                    Toast.makeText(this, "Ambil Device Code", Toast.LENGTH_SHORT).show();
                } else if (edtAktivasiDevice.getText().toString().equals("")) {
                    Toast.makeText(this, "Masukkan Kode Aktivasi", Toast.LENGTH_SHORT).show();
                } else {
                    //cekaktifasi();
                    String saktif=edtAktivasiDevice.getText().toString();
                    if (saktif.equals(codeactivation(number))){
                        editor = sharedPreferences.edit();
                        editor.putString(DivideStatus, "Aktif");
                        editor.apply();
                        Intent intent = new Intent(StartActivity.this, ActivasiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(this, "Kode Aktivasi Anda Salah \n Silahkan hubungi Admin", Toast.LENGTH_SHORT).show();
                    }
/*
                    Log.e("onClick: ", codeactivation(number));
                    Log.e("onClick: ", saktif);
*/
                }
                break;
            case R.id.btn_help:
                help();
                break;
        }
    }

    private void help() {
        String Sdialog = "Sebelum menggunakan Aplikasi ini, anda cukup ambil device code. Kemudian Masukkan Kode Aktivasi dengan menunjukan Device Code ke operator. Lalu Aktifkan";
        final DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialogInterface.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialogInterface.dismiss();
                        break;
                }

            }
        };
        AlertDialog.Builder Builder = new AlertDialog.Builder(this);
        Builder.setMessage(Sdialog).setPositiveButton("Mengerti", dialog).show();
//        super.onBackPressed();
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
