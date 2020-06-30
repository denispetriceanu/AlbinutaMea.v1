package com.example.albinutav1.ui.stupine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.albinutav1.MySingleton;
import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Splite extends Fragment {

    String id;
    private SpliteAdapter adapter;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private RecyclerView rvStup;
    private TextView locatie, data;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_splite, container, false);
        assert getArguments() != null;
        id = getArguments().getString("idStupina");
        contextMain = getContext();
        locatie = root.findViewById(R.id.locatie_split);
        data = root.findViewById(R.id.data_split2);

        rvStup = root.findViewById(R.id.recyclerViewSplite);
        Button btn = root.findViewById(R.id.button15);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(new utilityFunction().getStupNewStupina().toString());
                new getLocationService().onCreate(getContext());
                getFragmentManager().popBackStack();
            }
        });
        new takeDataLater().execute();

        return root;

    }

    public class getLocationService extends AppCompatActivity {
        int PERMISSION_ID = 44;
        FusedLocationProviderClient mFusedLocationClient;
        private Double lon, lat, alti;
        private android.content.Context context;
        private LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                lon = mLastLocation.getLongitude();
                lat = mLastLocation.getLatitude();
                alti = mLastLocation.getAltitude();
                showResults(getView());
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
                                        alti = location.getAltitude();
                                        showResults(getView());
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

        private void showResults(android.view.View view) {
            System.out.println("Incercam: " + lon + ", " + lat + ", " + alti);
            new SendSplitJson().sendPost("POST", utilityFunction.getUrl() + "stupina_split", id, new utilityFunction().readFromStorage(contextMain),
                    new utilityFunction().changeTheDiacritics(locatie.getText().toString()),
                    data.getText().toString(), new utilityFunction().getStupNewStupina(), lon, lat, alti);
            System.out.println(utilityFunction.getUrl() + "stupina_split");
//            new utilityFunction().move(view, "", R.id.nav_gallery, "");
        }
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {


        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String id_user = new utilityFunction().readFromStorage(getContext());
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/stup/usstupina/" + id + "/" + id_user;
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);// 1MB cap
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue requestQueue = new RequestQueue(cache, network);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            responsePrime = response;

                            if (responsePrime.length() == 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Nu aveți nici " +
                                                "un stup în această stupină", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Log.d(TAG, responsePrime.toString());
                                createTable();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "S-a petrecut o eroare." +
                                            "Verificați conexiunea la internet", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
            );
            requestQueue.add(jsonArrayRequest);

            MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
            return responsePrime;
        }

        @Override
        protected void onPostExecute(JSONArray response1) {
            super.onPostExecute(response1);
        }

        private void createTable() {
            ArrayList<String> idList = new ArrayList<>();

            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    idList.add(index.getString("id_stup"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            RecyclerView recyclerView = rvStup;
            recyclerView.setLayoutManager(new LinearLayoutManager(contextMain));
            adapter = new SpliteAdapter(contextMain, idList);
            recyclerView.setAdapter(adapter);

        }
    }
}
