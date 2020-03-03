package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String Section = "mypref";
    public static final String VEmail = "VEmail";
    Button btn_reset_password, btn_back_login_from_reset;
    EditText edtResetEmail;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        btn_reset_password = findViewById(R.id.btn_reset_password);
        btn_back_login_from_reset = findViewById(R.id.btn_back_login_from_reset);
        edtResetEmail=findViewById(R.id.edtResetEmail);
        btn_reset_password.setOnClickListener(this);
        btn_back_login_from_reset.setOnClickListener(this);
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
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_password:
                String eemail=edtResetEmail.getText().toString();
                editor = sharedPreferences.edit();
                editor.putString(VEmail, eemail);
                editor.apply();
                if (btn_reset_password.getText().equals(getString(R.string.verivikasibutonpass)))
                {
                    intent = new Intent(ResetPasswordActivity.this, VerivikasiPasswordActivity.class);
                    startActivity(intent);
                }else {
                    if (eemail.equals("")) {
                        Toast.makeText(this, "Masukkan Email Anda",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        reportforgot(eemail);
                    }
                }
                break;
            case R.id.btn_back_login_from_reset:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
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
                loading = ProgressDialog.show(ResetPasswordActivity.this, "Mengirim Verifikasi",
                        "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                intent = new Intent(ResetPasswordActivity.this, VerivikasiPasswordActivity.class);
                btn_reset_password.setText(getString(R.string.verivikasibutonpass));
                startActivity(intent);
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
    }
}
