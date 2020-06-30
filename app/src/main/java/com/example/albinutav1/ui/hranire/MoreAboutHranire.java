package com.example.albinutav1.ui.hranire;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class MoreAboutHranire extends Fragment {
    private String id;
    private JSONArray responsePrime;
    private android.content.Context contextMain;

    private EditText id_Hranire;
    private EditText id_stup;
    private EditText data_hranire;
    private EditText tipHranire;
    private EditText tipHrana;
    private EditText produs;
    private EditText producator;
    private EditText cantitate;
    private EditText nota;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detalii_hranire, container, false);
        id = getArguments().getString("idHranire");
        contextMain = getContext();

        id_Hranire = root.findViewById(R.id.editText5);
        id_stup = root.findViewById(R.id.textView30);
        data_hranire = root.findViewById(R.id.textView21);
        tipHranire = root.findViewById(R.id.textView27);
        tipHrana = root.findViewById(R.id.textView33);
        produs = root.findViewById(R.id.textView34);
        producator = root.findViewById(R.id.textView23);
        cantitate = root.findViewById(R.id.textView32);
        nota = root.findViewById(R.id.editText6);


        System.out.println("Am intrat in functie");

        takeDataLater ob = new takeDataLater();
        ob.execute();

        return root;
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            System.out.println("Am intrat in functie");
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/hranire/" + id;
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
                            System.out.println("RESPONSE PRIME: " + responsePrime);
                            if (responsePrime.length() == 0) {
                                utilityFunction ob = new utilityFunction();
                                Toast.makeText(getContext(), "S-a petrecu o eroare!", Toast.LENGTH_SHORT).show();
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
            String id;
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    id = index.getString("id_hranire");
                    id_Hranire.setText(id);

                    String id_stup_re = index.getString("id_stup");
                    id_stup.setText(id_stup_re);
                    System.out.println("Id: " + id_stup_re);
                    String data_hranire_re = index.getString("data_hranire");
                    data_hranire.setText(data_hranire_re);

                    String tip_hrana_re = index.getString("tip_hrana");
                    tipHrana.setText(tip_hrana_re);

                    String tip_hranire = index.getString("tip_hranire");
                    tipHranire.setText(tip_hranire);

                    String produs_re = index.getString("produs");
                    produs.setText(produs_re);

                    String producator_re = index.getString("producator");
                    producator.setText(producator_re);

                    String cantitate_re = index.getString("cantitate");
                    cantitate.setText(cantitate_re);

                    String nota_re = index.getString("nota");
                    nota.setText(nota_re);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
