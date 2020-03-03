package tracker.jaya.merak.r.sales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static tracker.jaya.merak.r.sales.LoginActivity.ModeAddStore;
import static tracker.jaya.merak.r.sales.LoginActivity.SLocationNow;
import static tracker.jaya.merak.r.sales.LoginActivity.SSalesID;
import static tracker.jaya.merak.r.sales.LoginActivity.SStatus;
import static tracker.jaya.merak.r.sales.LoginActivity.Section;
import static tracker.jaya.merak.r.sales.LoginActivity.StatusActivity;
import static tracker.jaya.merak.r.sales.LoginActivity.StatusStoreAddress;
import static tracker.jaya.merak.r.sales.LoginActivity.StatusStoreName;
import static tracker.jaya.merak.r.sales.LoginActivity.StoreLatLong;
import static tracker.jaya.merak.r.sales.MainActivity.SumStore;
import static tracker.jaya.merak.r.sales.R.*;

@SuppressWarnings("ALL")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;
    Button btn_to_add_store, btn_add_store, btn_cancel_store;
    EditText EdtStoreName, EdtAddress, EdtLokasi,EdtOwn;
    LinearLayout Linear_Store;
    int Condition_View_Maps, DateHistoryMinutes, DateHistoryNow;
    Double latt, lang;
    String stringStoreName, stringStoreAddress;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private String JSON_STRING;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker, MstoreMarker;
    private Location mLastLocation;
    private CameraPosition mCameraPosition;
    boolean mapss = false;
    boolean markerremover=false;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = getSharedPreferences(Section, MODE_PRIVATE);
        latt = -7.7111805;
        lang = 110.8074665;
        if (sharedPreferences.contains(SStatus)) {
            Log.e(TAG, "onCreate: " + sharedPreferences.getString(SStatus, "").toString().trim());
            switch (sharedPreferences.getString(StatusActivity, "").toString()) {
                case "Awal":
                    editor = sharedPreferences.edit();
                    editor.putString(StatusActivity, "Akhir");
                    editor.commit();
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    break;
                case "Akhir":
                    if (sharedPreferences.contains(StatusStoreName)) {
                        stringStoreName = sharedPreferences.getString(StatusStoreName,
                                "").toString().toUpperCase();
                        stringStoreAddress = sharedPreferences.getString(StatusStoreAddress,
                                "").toString().toLowerCase();
                        String MapsPosition = sharedPreferences.getString(StoreLatLong,
                                "").toString();
                        latt = Double.valueOf(
                                MapsPosition.substring(0, MapsPosition.indexOf(","))).doubleValue();
                        lang = Double.valueOf(MapsPosition.substring(MapsPosition.indexOf(",") + 1,
                                MapsPosition.length())).doubleValue();
                    }
                    break;
            }
        }
        Linear_Store = findViewById(id.Linear_Store);
        btn_to_add_store = findViewById(id.btn_to_add_store);
        btn_add_store = findViewById(id.btn_add_store);
        btn_cancel_store = findViewById(id.btn_cancel_store);
        EdtStoreName = findViewById(id.EdtStoreName);
        EdtAddress = findViewById(id.EdtAddress);
        EdtLokasi = findViewById(id.EdtLokasi);
        EdtOwn=findViewById(id.EdtOwnerName);
        btn_to_add_store.setOnClickListener(this);
        btn_add_store.setOnClickListener(this);
        btn_cancel_store.setOnClickListener(this);
        Condition_View_Maps = 0;
        DateHistoryMinutes = 0;
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Tambah Home Lokasi
        LatLng Home = new LatLng(-7.530072803404151, 110.73265492916107);
        mMap.addMarker(new MarkerOptions().position(Home).title("Kantor PT. Merak Jaya").snippet(
                "Jl. Tentara Pelajar No.16, Plempungan, Bolon, Colomadu, Karanganyar, Jawa Tengah 57177").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Home));
        //Memulai Google Play Services
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Prompt the user for permission.
            getLocationPermission();
            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
            return;
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.btn_to_add_store:
                Linear_Store.setVisibility(View.VISIBLE);
                btn_to_add_store.setVisibility(View.INVISIBLE);
                break;
            case id.btn_cancel_store:
                Linear_Store.setVisibility(View.INVISIBLE);
                break;
            case id.btn_add_store:
                tambahstore();
                break;
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi
                        .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                Log.e("Code", status.toString());
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e("Code", "Succ");
                        // All location settings are satisfied.
                        // You can initialize location requests here.
                        int permissionLocation = ContextCompat
                                .checkSelfPermission(MapsActivity.this,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied.
                        // But could be fixed by showing the user a dialog.
                        Log.e("Code", "Req");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            // Ask to turn on GPS automatically
                            status.startResolutionForResult(MapsActivity.this,
                                    REQUEST_CHECK_SETTINGS_GPS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("Code", "Un");
                        // Location settings are not satisfied.
                        // However, we have no way
                        // to fix the
                        // settings so we won't show the dialog.
                        // finish();
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latLng.latitude, latLng.longitude)).zoom(16).build();

        switch (Condition_View_Maps) {
            case 0:
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Log.e(TAG, new StringBuilder().append("onLocationChanged: ").append(
                        latLng.toString()).toString());
                Condition_View_Maps = 3;
                break;
            case 1:
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Log.e(TAG, new StringBuilder().append("onLocationChanged: ").append(
                        latLng.toString()).toString());
                Condition_View_Maps = 2;
                break;
            case 2:
                LatLng PositionStore = new LatLng(latt, lang);
                MarkerOptions marker = new MarkerOptions().position(PositionStore).title(
                        stringStoreName).snippet(stringStoreAddress).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                MstoreMarker = mMap.addMarker(marker);
                CameraPosition cameraPositionStore = new CameraPosition.Builder().target(
                        PositionStore).zoom(16).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionStore));
                Condition_View_Maps = 3;
                break;
            case 3:

                break;
        }
        if (sharedPreferences.contains(SSalesID)) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm");
            String StringMinutes = dateFormat.format(calendar.getTime());
            DateHistoryNow = Integer.parseInt(StringMinutes);
            lokasisales();
        }
        if (markerremover==true){
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            markerremover=false;
        }
        if (sharedPreferences.contains(SumStore)) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(final LatLng point) {
                    if (sharedPreferences.getInt(SumStore, 0) < Akses.LimitStore) {

                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker.remove();
                        }
                        if (Linear_Store.getVisibility() == View.INVISIBLE) {
                            MarkerOptions marker = new MarkerOptions().position(
                                    new LatLng(point.latitude, point.longitude)).title("Toko Baru");
                            mCurrLocationMarker = mMap.addMarker(marker);
                            try {
                                Geocoder geo = new Geocoder(
                                        MapsActivity.this.getApplicationContext(),
                                        Locale.getDefault());
                                List<Address> addresses = geo.getFromLocation(point.latitude,
                                        point.longitude, 1);
                                if (addresses.isEmpty()) {
                                    Toast.makeText(MapsActivity.this, "Belum Dapat",
                                            Toast.LENGTH_SHORT);
                                } else {
                                    EdtAddress.setText(addresses.get(0).getAddressLine(0));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String latid = String.valueOf(point.latitude);
                            String longtid = String.valueOf(point.longitude);
                            EdtLokasi.setText(latid + "," + longtid);
                            btn_to_add_store.setVisibility(View.VISIBLE);
                        } else {
                            Linear_Store.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker.remove();
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(
                this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void lokasisales() {


        class LOKASISALES extends AsyncTask<Void, Void, String> {

            final String SSales = sharedPreferences.getString(SSalesID, "").toString().trim();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res = "a";
                Proses rh = new Proses();
                LatLng latLng = new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude());
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
                editor = sharedPreferences.edit();

                editor.putString(SLocationNow,
                        String.valueOf(latLng.latitude + "," + latLng.longitude));
                editor.commit();

                params.put(Akses.ListSales0, SSales);
                params.put(Akses.ListSales8,
                        String.valueOf(latLng.latitude + "," + latLng.longitude));
                params.put(Akses.ListSales9, formattedDate);
                if (DateHistoryNow == DateHistoryMinutes) {
                    Log.e(TAG, "Sama");
                    res = rh.sendPostRequest(Akses.URL_LOKASI_SALES, params);
                } else {
                    Log.e(TAG, DateHistoryMinutes + "Beda" + DateHistoryNow);
                    DateHistoryMinutes = DateHistoryNow;
                    res = rh.sendPostRequest(Akses.URL_HISTORY_SALES, params);
                }

                Log.e("Lokasi ", "doInBackground: " + res);
                return res;
            }
        }

        LOKASISALES LS = new LOKASISALES();
        LS.execute();
    }

    private void tambahstore() {


        class TAMBAHSTORE extends AsyncTask<Void, Void, String> {

            final String SSales = sharedPreferences.getString(SSalesID, "").toString().trim();
            final String SEdtStoreName = EdtStoreName.getText().toString();
            final String SEdtOwnName = EdtOwn.getText().toString();
            final String SEdtAddress = EdtAddress.getText().toString();
            final String SEdtLokasi = EdtLokasi.getText().toString();
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this, "Menambahkan Toko",
                        "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                EdtStoreName.setText("");
                Linear_Store.setVisibility(View.INVISIBLE);
                loading.dismiss();
                Toast.makeText(MapsActivity.this, s, Toast.LENGTH_LONG).show();
                markerremover=true;
                int NSumStore = sharedPreferences.getInt(SumStore, 0);
                editor = sharedPreferences.edit();
                editor.putInt(String.valueOf(SumStore), NSumStore + 1);
                editor.apply();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                String res = "a";
                Proses rh = new Proses();
                LatLng latLng = new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude());
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());

                params.put(Akses.ListStore1, SEdtStoreName);
                params.put(Akses.ListStore6, SEdtOwnName);
                params.put(Akses.ListStore2, SEdtAddress);
                params.put(Akses.ListStore3, SEdtLokasi);
                params.put(Akses.ListStore4, SSales);
                res = rh.sendPostRequest(Akses.URL_TAMBAH_STORE, params);

                Log.e("Lokasi ", "doInBackground: " + res);
                return res;
            }
        }

        TAMBAHSTORE TS = new TAMBAHSTORE();
        TS.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.contains(ModeAddStore)) {
            switch (sharedPreferences.getString(ModeAddStore, "").toString()) {
                case "0":
                    Condition_View_Maps = 0;
                    break;
                case "1":
                    Condition_View_Maps = 1;
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        final DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
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
        Builder.setMessage("Selesai?").setPositiveButton("Kembali", dialog).setNeutralButton(
                "Tetap Disini", dialog).setNegativeButton("Tutup", dialog).show();
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
