package com.example.albinutav1.ui.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.albinutav1.MySingleton;
import com.example.albinutav1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class WheatherFragment extends Fragment {

    private String CITY = "Suceava";
    private double lat, lon;

    private TextView updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;
    private EditText addressTxt;
    private ProgressBar loader;
    private RelativeLayout mainContainer;
    private TextView errorText;
    private JSONObject responsePrime;
    private android.content.Context contextMain;
    private ProgressDialog progress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        super.onCreate(savedInstanceState);
        contextMain = getContext();
        progress = new ProgressDialog(getContext());
        progress.setTitle("Se încarcă");
        progress.setMessage("Vă rugăm asteptați...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progress.show();
        Button btn = root.findViewById(R.id.button10);
        addressTxt = root.findViewById(R.id.adress);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CITY = addressTxt.getText().toString();
                if(CITY.equals("")){
                    Toast.makeText(getContext(), "Nu ai introdus o localitate", Toast.LENGTH_LONG).show();
                } else {
                    new weatherTask().execute();
                }
            }
        });

        updated_atTxt = root.findViewById(R.id.updated_at);
        statusTxt = root.findViewById(R.id.status);
        tempTxt = root.findViewById(R.id.temp);
        temp_minTxt = root.findViewById(R.id.temp_min);
        temp_maxTxt = root.findViewById(R.id.temp_max);
        sunriseTxt = root.findViewById(R.id.sunrise);
        sunsetTxt = root.findViewById(R.id.sunset);
        windTxt = root.findViewById(R.id.wind);
        pressureTxt = root.findViewById(R.id.pressure);
        humidityTxt = root.findViewById(R.id.humidity);
        loader = root.findViewById(R.id.loader);
        mainContainer = root.findViewById(R.id.mainContainer);
        errorText = root.findViewById(R.id.errorText);

        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.fragment_to_swipe_wheather);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new weatherTask().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new getLocationService().onCreate(getContext());
        return root;
    }

    public class getLocationService extends AppCompatActivity {

        int PERMISSION_ID = 44;
        //        public Double lon, lat, alti;
        FusedLocationProviderClient mFusedLocationClient;
        private android.content.Context context;
        private LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                lon = mLastLocation.getLongitude();
                lat = mLastLocation.getLatitude();
                try {
                    showResults();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        protected void onCreate(android.content.Context context) {
            this.context = context;
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            getLastLocation();
        }

        @SuppressLint("MissingPermission")
        private void getLastLocation() {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(
                            new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Location location = task.getResult();
                                    if (location == null) {
                                        requestNewLocationData();
                                    } else {
                                        lat = location.getLatitude();
                                        lon = location.getLongitude();
                                        try {
                                            showResults();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                    );
                } else {
                    System.out.println("Ceva nu a mers bine!");
                }
            } else {
                requestPermissions();
            }
        }

        @SuppressLint("MissingPermission")
        private void requestNewLocationData() {

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(0);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    Looper.myLooper()
            );

        }

        private boolean checkPermissions() {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }

        private void requestPermissions() {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ID
            );
        }

        private boolean isLocationEnabled() {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            );
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_ID) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (checkPermissions()) {
                getLastLocation();
            }

        }

        private void showResults() throws IOException {
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lon, 1);
            CITY = addresses.get(0).getLocality();
            new weatherTask().execute();
        }
    }


    class weatherTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing the ProgressBar, Making the main design GONE */
            loader.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);
        }

        protected JSONObject doInBackground(String... args) {
            final android.content.Context context = contextMain;
            String API = "584a6117034305b72c474d5f963c69a5";
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API;
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);// 1MB cap
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue requestQueue = new RequestQueue(cache, network);
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responsePrime = response;
                            System.out.println("Am primit de data aceasta" + response.toString());
                            if (responsePrime.length() == 0) {
                                System.out.println("We received the JSON empty");
                            } else {
                                Log.d(TAG, responsePrime.toString());
                                prelucrateJSON(responsePrime);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error: " + error);
                        }
                    }
            );
            requestQueue.add(jsonArrayRequest);

            MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
            return responsePrime;
        }

        @Override
        protected void onPostExecute(JSONObject response ) {
            System.out.println("Nimic");
        }

        private void prelucrateJSON(JSONObject responsePrime){

            try {
                JSONObject jsonObj = responsePrime;
                System.out.println(responsePrime.toString());
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Actualizat la: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Temp min: " + main.getString("temp_min") + "°C";
                String tempMax = "Temp max: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name");

                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);

                /* Views populated, Hiding the loader, Showing the main design */
                loader.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);
                progress.dismiss();
            } catch (JSONException e) {
                loader.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
            }
        }
    }
}