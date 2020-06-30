package com.example.albinutav1.ui.stupine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.example.albinutav1.MainActivity;
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

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Edit extends Fragment {
    private JSONObject responsePrime;

    private String id;
    private TextView locationText;
    private TextView nrStupiText;
    private TextView dataText;
    private String locatieTest;
    private double lon, lat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_stupina, container, false);

        ((MainActivity) getActivity()).setActionBarTitle(Html.fromHtml("<font color='#000'>Editeaza</font>"));
        id = getArguments().getString("idStupina");

        TextView textId = root.findViewById(R.id.editText2);
        locationText = root.findViewById(R.id.editText);
        nrStupiText = root.findViewById(R.id.editText3);
        dataText = root.findViewById(R.id.editText4);
        Button editBtn = root.findViewById(R.id.button2);
        Button cancelBtn = root.findViewById(R.id.button3);
        textId.setText(id);
        Edit.takeDataLater object = new Edit.takeDataLater();
        object.execute();
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final utilityFunction ob = new utilityFunction();

//                verity if exists some modification in that field
                System.out.println("Textul initial: " + locationText.getText().toString() + ", locatie test: " + locatieTest);
                if (!locatieTest.equals(ob.changeTheDiacritics(locationText.getText().toString()))) {
//                    We show a pop_up message for question user if want keep last coordinated location
//                    or get after name location or get now location
                    LayoutInflater li = LayoutInflater.from(getContext());
                    View promptsView = li.inflate(R.layout.pop_up_edit_stupina, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            Objects.requireNonNull(getContext()));
                    alertDialogBuilder.setView(promptsView);
// verify which coordinate want to save
                    final CheckBox checkBox = promptsView.findViewById(R.id.checkBox);
                    final CheckBox checkBox1 = promptsView.findViewById(R.id.checkBox2);
                    final CheckBox checkBox2 = promptsView.findViewById(R.id.checkBox3);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                checkBox1.setChecked(false);
                                checkBox2.setChecked(false);
                            }
                        }
                    });
                    checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {

                                checkBox.setChecked(false);
                                checkBox2.setChecked(false);
                            }
                        }
                    });
                    checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                checkBox1.setChecked(false);
                                checkBox.setChecked(false);

                            }
                        }
                    });

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    final StupineSendServerAsyncT asyncT = new StupineSendServerAsyncT();

                    Button save = promptsView.findViewById(R.id.button);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (checkBox.isChecked()) {
                                asyncT.sendPost(
                                        "PUT", "http://192.168.1.83:5000/stupina/" + id + "/any",
                                        locationText.getText().toString(), nrStupiText.getText().toString(),
                                        dataText.getText().toString(), String.valueOf(lon), String.valueOf(lat), ""
                                );
                                closeKeyboard();
                                Toast.makeText(getContext(), "Am realizat modificarea! Iar coordonatele au" +
                                        "rămas aceleași", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                                ob.move(getView(), "", R.id.nav_gallery, "");
                            }
                            if (checkBox1.isChecked()) {
                                gShareFragment(locationText.getText().toString());
                                asyncT.sendPost(
                                        "PUT", "http://192.168.1.83:5000/stupina/" + id + "/any",
                                        locationText.getText().toString(), nrStupiText.getText().toString(),
                                        dataText.getText().toString(), String.valueOf(lon), String.valueOf(lat), ""
                                );
                                closeKeyboard();
                                alertDialog.dismiss();
                                Toast.makeText(getContext(), "Am realizat modificarea! Iar coordonatele" +
                                        "au fost salvate după denumire", Toast.LENGTH_LONG).show();
                                ob.move(getView(), "", R.id.nav_gallery, "");
                            }
                            if (checkBox2.isChecked()) {
                                new getLocationService().onCreate(getContext());
                                closeKeyboard();
                                alertDialog.dismiss();
                                Toast.makeText(getContext(), "Am realizat modificarea! Iar coordonatele" +
                                        "au fost salvate după denumire", Toast.LENGTH_LONG).show();
                                ob.move(getView(), "", R.id.nav_gallery, "");
                            } else {
                                closeKeyboard();
                                ob.showToast(getContext(), "Trebuie să alegeți cel puțin o opțiune", true);
                            }
                        }
                    });
                } else {
//                    In case not exist modification;
                    StupineSendServerAsyncT asyncT = new StupineSendServerAsyncT();
                    asyncT.sendPost(
                            "PUT", "http://192.168.1.83:5000/stupina/" + id + "/any",
                            locationText.getText().toString(), nrStupiText.getText().toString(),
                            dataText.getText().toString(), String.valueOf(lon), String.valueOf(lat), ""
                    );
                    Toast.makeText(getContext(), "Am realizat modificarea!", Toast.LENGTH_LONG).show();
                    ob.move(getView(), "", R.id.nav_gallery, "");
                }

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Nu ati realizat nici o modificare!", Toast.LENGTH_LONG).show();
                utilityFunction ob = new utilityFunction();
                closeKeyboard();
                ob.move(view, "", R.id.nav_gallery, "");
            }
        });
        return root;
    }

    private void closeKeyboard() {
        dataText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        locationText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        nrStupiText.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private void gShareFragment(String strAddress) {

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                System.out.println("Nu a fost gasit!");
            }
            Address location = address.get(0);
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private class takeDataLater extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/stupina/one/" + id;
            Cache cache = new DiskBasedCache(getContext().getCacheDir(), 1024 * 1024);// 1MB cap
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
                            if (responsePrime.length() > 0) {
                                Log.d(TAG, responsePrime.toString());
                                completData();
                            } else {
                                Log.d(TAG, "S-a petrecut o eroare");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error get data: " + error);
                        }
                    }
            );
            requestQueue.add(jsonArrayRequest);

            MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
            return responsePrime;
        }

        @Override
        protected void onPostExecute(JSONObject response1) {
            super.onPostExecute(response1);
        }

        private void completData() {
            String locatie, nrStupi, data_show;
            try {
                locatie = responsePrime.getString("locatie");
                nrStupi = responsePrime.getString("nr_stupi");
                data_show = responsePrime.getString("data_plasare");
                lon = Double.parseDouble(responsePrime.getString("longitudine"));
                lat = Double.parseDouble(responsePrime.getString("latitudine"));
                locationText.setText(locatie);
                locatieTest = locatie;
                nrStupiText.setText(nrStupi);
                dataText.setText(data_show);
            } catch (Exception e) {
                System.out.println("Error on try convert: " + e);
            }
        }
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
            StupineSendServerAsyncT send = new StupineSendServerAsyncT();
            String id_user = new utilityFunction().readFromStorage(Objects.requireNonNull(getContext()));
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            send.sendPost("POST",
                    ip + "/stupina_post/" + id_user,
                    new utilityFunction().changeTheDiacritics(locationText.getText().toString()),
                    nrStupiText.getText().toString(),
                    dataText.getText().toString(),
                    String.valueOf(lon),
                    String.valueOf(lat),
                    "");
            Toast.makeText(getContext(), "Stupina a fost adăugată", Toast.LENGTH_SHORT).show();
            new utilityFunction().move(view, "", R.id.nav_gallery, "");
        }
    }

}
