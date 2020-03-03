package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static boolean MainActive=false;

    public static final String Section = "mypref";
    public static final String SEmail = "Email";
    public static final String SPassword = "Pass";
    public static final String SStatus = "Status";
    public static final String SSalesID = "SalesID";
    public static final String SDepoID = "SDepoID";
    public static final String StatusActivity = "StatusActivity";
    public static final String StoreLatLong = "StoreLatLong";
    public static final String StatusStoreName = "StatusStoreName";
    public static final String StatusStoreAddress = "StatusStoreAddress";
    public static final String ModeAddStore = "ModeAddStore";
    public static final String SLocationNow = "SLocationNow";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;

    EditText edtEmail , edtPassword;
    Button btn_email_sign_in , btn_to_register,btn_forgot_password ;
    WindowManager windowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btn_email_sign_in = findViewById(R.id.btn_email_sign_in);
        btn_to_register = findViewById(R.id.btn_to_register);
        btn_forgot_password=findViewById(R.id.btn_forgot_password);
        windowManager=(WindowManager)LoginActivity.this.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        Log.e("DISPLAY",
                String.format("%d x %d", display.getHeight(), display.getWidth()));
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
        if (sharedPreferences.contains(SEmail))
            edtEmail.setText(sharedPreferences.getString(SEmail, ""));
        if (sharedPreferences.contains(SPassword)) {
            edtPassword.setText(sharedPreferences.getString(SPassword, ""));
        }
/*
        if (sharedPreferences.contains(SStatus)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                switch (Objects.requireNonNull(sharedPreferences.getString(SStatus, ""))) {
                    case "Masuk":
                        Intent intent = new Intent(this, ActivasiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                        ceksales();
                        break;
                    case "Keluar":

                        break;
                }
            }
        }

*/
        btn_email_sign_in.setOnClickListener(this);
        btn_to_register.setOnClickListener(this);
        btn_forgot_password.setOnClickListener(this);

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
        MainActive=true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_email_sign_in:
                editor = sharedPreferences.edit();
                editor.putString(SEmail, edtEmail.getText().toString());
                editor.putString(SPassword, edtPassword.getText().toString());
                editor.apply();
                ceksales();
                break;
            case R.id.btn_to_register:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_forgot_password:
                intent = new Intent(this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void ceksales() {


        /*



         */
        @SuppressLint("StaticFieldLeak")
        class CEKSALES extends AsyncTask<Void, Void, String> {

            private final String SedtEmail = edtEmail.getText().toString();
            private final String SedtPassword = edtPassword.getText().toString();
            private ProgressDialog loading;
            private JSONObject jsonObject = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Verivikasi Pengguna",
                        "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    jsonObject = new JSONObject(s);
                    String jsonStringer = jsonObject.getString("status");
                    String jsonSalesID = jsonObject.getString(Akses.ListSales0);
                    String jsonDepoID = jsonObject.getString(Akses.ListDepo0);
                    Log.e("Status", jsonStringer);
                    if (jsonStringer.equals("Succes")){
                    }else {
                        editor = sharedPreferences.edit();
                        editor.putString(SStatus, "Keluar");
                        editor.apply();
                    }
                    Toast.makeText(LoginActivity.this, jsonStringer, Toast.LENGTH_LONG).show();
                    switch (jsonStringer) {
                        case "Succes":
                            editor = sharedPreferences.edit();
                            editor.putString(SStatus, "Masuk");
                            editor.putString(SSalesID, jsonSalesID);
                            editor.putString(SDepoID, jsonDepoID);
                            editor.putString(StatusActivity, "Awal");
                            editor.commit();
                            intent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivity(intent);
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res;
                Proses rh = new Proses();

                params.put(Akses.ListSales2, SedtEmail);
                params.put(Akses.ListSales5, SedtPassword);
                res = rh.sendPostRequest(Akses.URL_CEK_SALES, params);

                Log.e("Lokasi ", "doInBackground: " + res);
                return res;
            }
        }

        CEKSALES cs = new CEKSALES();
        cs.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActive=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActive=false;
    }

    @Override
    public void onBackPressed() {
        finish();
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
