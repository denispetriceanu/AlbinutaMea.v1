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
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddStupina extends Fragment {
    private double lon, lat, alti;
    private TextView locatie;
    private TextView nr_stupi;
    private TextView data;
    private boolean varTest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addstupina, container, false);
        locatie = root.findViewById(R.id.editTextFA);
        nr_stupi = root.findViewById(R.id.editText3FA);
        data = root.findViewById(R.id.editText4FA);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        data.setText(dateFormat.format(date));
        Switch swtich3 = root.findViewById(R.id.switch3);
        Button btnSend = root.findViewById(R.id.button2FA);
        Button btnCancel = root.findViewById(R.id.button3FA);

        swtich3.setChecked(false);
        swtich3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    varTest = true;
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (varTest) {
                    System.out.println("Incercam: " + lon + ", " + lat + ", " + alti);
                    System.out.println(locatie.getText().toString());
                    System.out.println(data.getText().toString());
                    System.out.println(nr_stupi.getText().toString());
                    if (!("".equals(locatie.getText().toString()) && "".equals(nr_stupi.getText().toString()) &&
                            "".equals(data.getText().toString()))) {
                        data.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        nr_stupi.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        locatie.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        utilityFunction forMove = new utilityFunction();

                        StupineSendServerAsyncT send = new StupineSendServerAsyncT();
                        String id_user = new utilityFunction().readFromStorage(Objects.requireNonNull(getContext()));
                        gShareFragment(locatie.getText().toString());
                        String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                        send.sendPost("POST",
                                ip + "/stupina_post/" + id_user,
                                forMove.changeTheDiacritics(locatie.getText().toString()),
                                nr_stupi.getText().toString(),
                                data.getText().toString(),
                                String.valueOf(lon),
                                String.valueOf(lat),
                                String.valueOf(alti));
                        Toast.makeText(getContext(), "Stupina a fost adăugată", Toast.LENGTH_SHORT).show();
                        forMove.move(view, "", R.id.nav_gallery, "");
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Vă rugăm să completați toate datele",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new getLocationService().onCreate(getContext());
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        getContext(),
                        "Nu ati adăugat nici o stupină!",
                        Toast.LENGTH_SHORT).show();
                utilityFunction forMove = new utilityFunction();
                data.onEditorAction(EditorInfo.IME_ACTION_DONE);
                nr_stupi.onEditorAction(EditorInfo.IME_ACTION_DONE);
                locatie.onEditorAction(EditorInfo.IME_ACTION_DONE);
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });
        return root;
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
            System.out.println(locatie.getText().toString());
            System.out.println(data.getText().toString());
            System.out.println(nr_stupi.getText().toString());
            if (!("".equals(locatie.getText().toString()) && "".equals(nr_stupi.getText().toString()) &&
                    "".equals(data.getText().toString()))) {
                data.onEditorAction(EditorInfo.IME_ACTION_DONE);
                nr_stupi.onEditorAction(EditorInfo.IME_ACTION_DONE);
                locatie.onEditorAction(EditorInfo.IME_ACTION_DONE);
                utilityFunction forMove = new utilityFunction();

                StupineSendServerAsyncT send = new StupineSendServerAsyncT();
                String id_user = new utilityFunction().readFromStorage(Objects.requireNonNull(getContext()));
                send.sendPost("POST",
                        "http://192.168.1.83:5000/stupina_post/" + id_user,
                        forMove.changeTheDiacritics(locatie.getText().toString()),
                        nr_stupi.getText().toString(),
                        data.getText().toString(),
                        String.valueOf(lon),
                        String.valueOf(lat),
                        String.valueOf(alti));
                Toast.makeText(getContext(), "Stupina a fost adăugată", Toast.LENGTH_SHORT).show();
                forMove.move(view, "", R.id.nav_gallery, "");
            } else {
                Toast.makeText(
                        getContext(),
                        "Vă rugăm să completați toate datele",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
