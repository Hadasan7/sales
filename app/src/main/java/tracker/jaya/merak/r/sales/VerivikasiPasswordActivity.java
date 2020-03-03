package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import static tracker.jaya.merak.r.sales.ResetPasswordActivity.VEmail;

public class VerivikasiPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtVerivicationCode, edtNewPassword, edtConfirmPassword;
    Button btn_update_pass, btn_forgot_new;
    SharedPreferences sharedPreferences;
    public static final String Section = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verivikasi_password);
        edtVerivicationCode = findViewById(R.id.edtVerivicationCode);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btn_update_pass = findViewById(R.id.btn_update_pass);
        btn_forgot_new = findViewById(R.id.btn_forgot_new);
        btn_update_pass.setOnClickListener(this);
        btn_forgot_new.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
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
    }


    private void updatepassword() {


        @SuppressLint("StaticFieldLeak")
        class UPDATEPASSWORD extends AsyncTask<Void, Void, String> {

            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(VerivikasiPasswordActivity.this, "Sedang Mengambil",
                        "Mohon Tunggu...", false, false);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("success")) {
                    Toast.makeText(VerivikasiPasswordActivity.this, "Password Telah diperbarui",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerivikasiPasswordActivity.this,
                            ActivasiActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerivikasiPasswordActivity.this, "Gagal diperbarui",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();


                params.put(Akses.ListForgot1, edtVerivicationCode.getText().toString());
                params.put(Akses.ListForgot2, edtNewPassword.getText().toString());
                res = rh.sendPostRequest(Akses.URL_RESET, params);

                Log.e("PASSWORD ", res);
                return res;
            }
        }

        UPDATEPASSWORD UP = new UPDATEPASSWORD();
        UP.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_pass:
                if (edtVerivicationCode.getText().toString().equals("")) {
                    Toast.makeText(this, "Masukkan Kode Verivikasi Email",
                            Toast.LENGTH_SHORT).show();
                } else if (edtNewPassword.getText().toString().equals("")) {
                    Toast.makeText(this, "Masukkan Password yang baru", Toast.LENGTH_SHORT).show();
                } else if (edtNewPassword.getText().toString().equals(
                        edtConfirmPassword.getText().toString())) {
                    updatepassword();
                }
                break;
            case R.id.btn_forgot_new:
                final DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                    @SuppressLint("ApplySharedPref")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                String vemail = sharedPreferences.getString(VEmail, "");
                                reportforgot(vemail);
                                dialogInterface.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.dismiss();
                                break;
                        }

                    }
                };
                AlertDialog.Builder Builder = new AlertDialog.Builder(this);
                Builder.setMessage("Kirim Ulang Kode Verivikasi?").setPositiveButton("Kirim Ulang",
                        dialog).setNegativeButton("Tutup", dialog).show();
                break;
        }
    }

    private void reportforgot(final String S0) {


        @SuppressLint("StaticFieldLeak")
        class REPORTFORGOT extends AsyncTask<Void, Void, String> {
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(VerivikasiPasswordActivity.this,
                        "Mengirim Verifikasi",
                        "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();
                params.put(Akses.ListForgot0, S0);
                res = rh.sendPostRequest(Akses.URL_FORGOT, params);

                Log.e("Reset ", S0 + "Lapor Reset: " + res);
                return res;
            }
        }

        REPORTFORGOT RF = new REPORTFORGOT();
        RF.execute();
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

    @Override
    public void onBackPressed() {
        finish();
    }

}
