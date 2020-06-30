package com.example.albinutav1.ui.control;

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

public class MoreAboutControl extends Fragment {

    private EditText txt1;
    private EditText txt2;
    private EditText txt3;
    private EditText txt4;
    private EditText txt5;
    private EditText txt6;
    private EditText txt7;
    private EditText txt8;
    private EditText txt9;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private String id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalii_control, container, false);
        contextMain = getContext();
        id = getArguments().getString("idControl");

        txt1 = root.findViewById(R.id.editTextControl5); // id_stup
        txt2 = root.findViewById(R.id.textViewControl30); // id stupina
        txt3 = root.findViewById(R.id.textViewControl21); // numar rame
        txt4 = root.findViewById(R.id.textViewControl27); // tip stup
        txt5 = root.findViewById(R.id.textViewControl33); //rasa
        txt6 = root.findViewById(R.id.textViewControl34); // mod
        txt7 = root.findViewById(R.id.textViewControl23); // varsta
        txt8 = root.findViewById(R.id.textViewControl32); // culoare
        txt9 = root.findViewById(R.id.editTextControl6); // culoare

        takeDataLater ob = new takeDataLater();
        ob.execute();

        return root;
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/controlVeterinar/" + id;
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
                            System.out.println("Id-ul apelat este: " + id);
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
                    id = index.getString("id_control");
                    txt1.setText(id);

                    String id_stup_re = index.getString("id_stupina");
                    txt2.setText(id_stup_re);
                    System.out.println("Id: " + id_stup_re);
                    String data_hranire_re = index.getString("data_control");
                    txt3.setText(data_hranire_re);

                    String tip_hrana_re = index.getString("examinare");
                    txt4.setText(tip_hrana_re);

                    String tip_hranire = index.getString("stare");
                    txt5.setText(tip_hranire);

                    String produs_re = index.getString("proba");
                    txt6.setText(produs_re);

                    String producator_re = index.getString("concluzii");
                    txt7.setText(producator_re);

                    String cantitate_re = index.getString("veterinar");
                    txt8.setText(cantitate_re);

                    String observatii = index.getString("observatii");
                    txt9.setText(observatii);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
