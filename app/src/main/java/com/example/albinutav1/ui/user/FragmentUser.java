package com.example.albinutav1.ui.user;

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

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class FragmentUser extends Fragment {
    private TextView nume;
    private TextView prenume;
    private TextView adresa;
    private TextView email;
    private TextView telefon;
    private String id;
    private JSONArray responsePrime;
    private android.content.Context contextMain;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        contextMain = getContext();
        id = new utilityFunction().readFromStorage(contextMain);

        nume = root.findViewById(R.id.editText29);
        prenume = root.findViewById(R.id.editText30);
        adresa = root.findViewById(R.id.editText31);
        email = root.findViewById(R.id.editText32);
        telefon = root.findViewById(R.id.editText33);
        //for load data in input
        takeDataLater getDate = new takeDataLater();
        getDate.execute();


//         for edit
        Button btn = root.findViewById(R.id.button14);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendServerAsyncT send = new SendServerAsyncT();
                utilityFunction modify = new utilityFunction();
                String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                send.sendPost(
                        "PUT",
                        ip + "/user",
                        modify.changeTheDiacritics(nume.getText().toString()),
                        modify.changeTheDiacritics(prenume.getText().toString()),
                        modify.changeTheDiacritics(adresa.getText().toString()),
                        modify.changeTheDiacritics(email.getText().toString()),
                        modify.changeTheDiacritics(telefon.getText().toString()));
            }
        });

        return root;
    }

    //class for edit
    public class SendServerAsyncT {

        public void sendPost(final String method,
                             final String urlSend,
                             final String nume,
                             final String prenume,
                             final String adresa,
                             final String email,
                             final String telefon) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(urlSend);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(method);
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("nume", nume);
                        jsonParam.put("prenume", prenume);
                        jsonParam.put("email", email);
                        jsonParam.put("adresa", adresa);
                        jsonParam.put("telefon", telefon);

                        Log.i("JSON", jsonParam.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(jsonParam.toString());

                        os.flush();
                        os.close();

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        System.out.println(Integer.parseInt(String.valueOf(conn.getResponseCode())));
                        if (Integer.parseInt(String.valueOf(conn.getResponseCode())) == 200) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "Editare realizată cu succes"
                                            , Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Log.i("MSG", conn.getResponseMessage());
                            conn.disconnect();
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "Se pare că s-a petrecut o eroare! Încercați mai târziu",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getContext(), "Eroare: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            });

            thread.start();
        }
    }

    //class for getData
    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            String id_user = new utilityFunction().readFromStorage(Objects.requireNonNull(getContext()));
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);

            String url = ip + "/data_user/" + id_user;
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
            String id;
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    nume.setText(index.getString("nume"));
                    prenume.setText(index.getString("prenume"));
                    adresa.setText(index.getString("adresa"));
                    telefon.setText(index.getString("telefon"));
                    email.setText(index.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
