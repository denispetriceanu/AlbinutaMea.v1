package com.example.albinutav1.ui.harta_stupine;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.albinutav1.MySingleton;
import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ShareFragment extends Fragment {

    private SupportMapFragment mapFragment;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private String test = "";

    private ProgressDialog progress;

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextMain = getContext();
        new takeDataLater().execute();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_share, container, false);
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        progress = new ProgressDialog(getContext());
        progress.setTitle("Se încarcă");
        progress.setMessage("Vă rugăm asteptați...");
        progress.setCancelable(true); //dismiss by tapping outside of the dialog
        progress.show();
        if (!test.equals("")) {
            progress.dismiss();
        }
        return rootView;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createMarks(final ArrayList<String> locatie, final ArrayList<String> date, final ArrayList<String> nr_stupi
            , final ArrayList<String> id_list, final ArrayList<String> lat, final ArrayList<String> lon) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

//                gShareFragment(locatie.get(0));
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(46.400748, 24.888551))//                        .target(new LatLng(37.4219999, -122.0862462))
                        .zoom(6)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);
                if (locatie.size() >= 1) {

                    for (int i = 0; i < locatie.size(); i++) {
                        String name = locatie.get(i);
//                        gShareFragment(name);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(lon.get(i)), Double.parseDouble(lat.get(i))))
                                .title(name)
                                .snippet("Nr stupi: " + nr_stupi.get(i)));

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                utilityFunction ob = new utilityFunction();
                                System.out.println(marker.getId());
                                ob.move(getView(), id_list.get(Integer.parseInt(marker.getId().replace("m", ""))), R.id.nav_stup, "idStupina");
                            }
                        });
                    }
                }
                test = "bifat";
                progress.dismiss();
            }
        });
    }

//

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

//        @Override
//        protected void onPreExecute() {
//
//            super.onPreExecute();
//        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            // function for verify time which run this app
            long started = SystemClock.elapsedRealtime();
            System.out.println("Am intrat in clasa");
            final android.content.Context context = contextMain;

            utilityFunction ob = new utilityFunction();
            String id_user = ob.readFromStorage(Objects.requireNonNull(getContext()));

            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/data/" + id_user;
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
                            Log.d(TAG, responsePrime.toString());
                            if (responsePrime.length() == 0) {
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getContext(), "Nu s-a găsit nici o stupină",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                createTable();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "S-a petrecut o eroare." +
                                            "Verificati conexiunea la internet", Toast.LENGTH_LONG).show();
                                }
                            });
                            System.out.println("Error: " + error);
                        }
                    }
            );
            requestQueue.add(jsonArrayRequest);

            MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
            System.out.println("Time: ");
            System.out.println(SystemClock.elapsedRealtime() - started);
            return responsePrime;
        }

        @Override
        protected void onPostExecute(JSONArray response1) {
            super.onPostExecute(response1);
        }

        private void createTable() {
            ArrayList<String> idList = new ArrayList<>();
            ArrayList<String> dateList = new ArrayList<>();
            ArrayList<String> locationList = new ArrayList<>();
            ArrayList<String> nr_combList = new ArrayList<>();
            ArrayList<String> lon = new ArrayList<>();
            ArrayList<String> lat = new ArrayList<>();
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    locationList.add(index.getString("locatie"));
                    dateList.add(index.getString("data_plasare"));
                    nr_combList.add(index.getString("nr_stupi"));
                    idList.add(index.getString("id_stupina"));
                    lat.add(index.getString("latitudine"));
                    lon.add(index.getString("longitudine"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            createMarks(locationList, dateList, nr_combList, idList, lon, lat);
        }
    }
}