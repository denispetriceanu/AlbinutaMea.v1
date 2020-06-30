package com.example.albinutav1.ui.stup;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

public class EditStup extends Fragment {
    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txt5;
    private TextView txt6;
    private TextView txt7;
    private TextView txt8, txt9, txt10;
    //    private TextView txt9;
//    private TextView txt10;
//    private TextView txt11;
//    private TextView txt12;
//    private TextView txt13;
//    private TextView txt14;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private String id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contextMain = getContext();
        id = getArguments().getString("idStupDetail");
        View root = inflater.inflate(R.layout.fragment_edit_stup, container, false);
        txt1 = root.findViewById(R.id.editText5); // id_stup
        txt2 = root.findViewById(R.id.textView30); // id stupina
        txt3 = root.findViewById(R.id.textView21); // numar rame
        txt4 = root.findViewById(R.id.textView27); // tip stup
        txt5 = root.findViewById(R.id.textView33); //rasa
        txt6 = root.findViewById(R.id.textView34); // mod
        txt7 = root.findViewById(R.id.textView23); // varsta
        txt8 = root.findViewById(R.id.textView32); // culoare
        txt9 = root.findViewById(R.id.rame_puiet_edit);
        txt10 = root.findViewById(R.id.rame_hranire_edit);


        Button btnEdit = root.findViewById(R.id.button6);
        Button btnCancel = root.findViewById(R.id.button7);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StupSendServerAsyncT ob = new StupSendServerAsyncT();
                String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                final String url = ip + "/stup/" + id + "/any";
                ob.sendPost("PUT", url, txt2.getText().toString(), txt8.getText().toString(),
                        txt5.getText().toString(), txt4.getText().toString(), txt3.getText().toString(),
                        txt6.getText().toString(), txt7.getText().toString(), txt9.getText().toString(), txt10.getText().toString());
                Toast.makeText(getContext(), "Modificarea a avut succes", Toast.LENGTH_SHORT).show();
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Nu ați făcut nici o modificare!", Toast.LENGTH_LONG).show();
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        takeDataLater obj = new takeDataLater();
        obj.execute();

        return root;
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String url = "http://192.168.1.83:5000/stup/" + id + "/any";
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
                                utilityFunction ob = new utilityFunction();
                                Toast.makeText(getContext(), "Nu există date despre acest stup"
                                        , Toast.LENGTH_LONG).show();
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
                    id = index.getString("id_stup");
                    txt1.setText(id);
                    String idStupina = index.getString("id_stupina");
                    txt2.setText(idStupina);
                    String culoare = index.getString("culoare_stup");
                    txt8.setText(culoare);
                    String rasa = index.getString("rasa_albine");
                    txt5.setText(rasa);
                    String tip = index.getString("tip_stup");
                    txt4.setText(tip);
                    String numar = index.getString("numarRame");
                    txt3.setText(numar);
                    String mod = index.getString("mod_constituire");
                    txt6.setText(mod);
                    String varsta = index.getString("varsta_matca");
                    txt7.setText(varsta);
                    String ram_puiet = index.getString("rame_puiet");
                    txt9.setText(ram_puiet);
                    String ram_hrana = index.getString("rame_hrana");
                    txt10.setText(ram_hrana);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}