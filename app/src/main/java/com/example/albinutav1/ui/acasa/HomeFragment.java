package com.example.albinutav1.ui.acasa;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.anychart.AnyChartView;
import com.example.albinutav1.MySingleton;
import com.example.albinutav1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    //    TextView id_stup, nr_rame, id_stupina, greutate, temperatura, temp_ex, vizibilitate, umiditate, presiune;
    private AnyChartView anyChartView;
    private Context contextMain;
    private JSONArray responsePrime;
    private PopupWindow mPopupWindow;
    private ConstraintLayout mConstraintLayout;
    private AlertDialog alertDialog;
    private String responsePrimeS;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        contextMain = getContext();
        new takeDataLater().execute();
        mConstraintLayout = (ConstraintLayout) root.findViewById(R.id.doctor_message);
        ImageView img = root.findViewById(R.id.imageView9);
//        img.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//        });
        return root;
    }

    private void showOption() {
        LayoutInflater li = LayoutInflater.from(contextMain);
        View promptsView = li.inflate(R.layout.notificationa_ai, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                contextMain);
        alertDialogBuilder.setView(promptsView);

        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

        // for reset password
        Button close = promptsView.findViewById(R.id.button17);
        Button resolved = promptsView.findViewById(R.id.button18);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        resolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("E ok!");
                new resolvProblem().execute();
            }
        });
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/set_data_ai";
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
                                Toast.makeText(getContext(), "S-a petrecut o eroare", Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG, responsePrime.toString());
                                createTable();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error: " + error);
                            Toast.makeText(getContext(), "S-a petrecut o eroare." +
                                    "Verificațivă conexiunea la internet", Toast.LENGTH_LONG).show();
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
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                String id, rez;
                try {
                    index = responsePrime.getJSONObject(i);
                    id = index.getString("id");
                    rez = index.getString("rezultat");
                    System.out.println("Receive: " + id + ", " + rez);
                    if (rez.equals("0")) {
                        showOption();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    // here is modifyed cod
    private class resolvProblem extends AsyncTask<String, String, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/set_data_ai";
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);// 1MB cap
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue requestQueue = new RequestQueue(cache, network);
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("Success")){
                                alertDialog.dismiss();
                            } else {
                                System.out.println(response);
                            }
                            // Display the first 500 characters of the response string.
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(stringRequest);
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
            return responsePrime;
        }

        @Override
        protected void onPostExecute(JSONArray response1) {
            super.onPostExecute(response1);
        }

        private void createTable() {


        }
    }

}