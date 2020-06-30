package com.example.albinutav1.ui.tratamente;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MoreAboutTratamente extends Fragment {
    //    private String id;
//    private JSONArray responsePrime;
//    private android.content.Context contextMain;
//    private ArrayAdapter<String> arrayAdapter;
//    private List<String> elementsForAdd;
    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txt5;
    private TextView txt6;
    private TextView txt7;
    private TextView txt8;
    private TextView txt9;
    private TextView txt10;
    private TextView txt11;
    private TextView txt12;
    private TextView txt13;
    private TextView txt14;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private String id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detallii_tratamente, container, false);

        id = getArguments().getString("idTratament");

        contextMain = getContext();

        txt1 = root.findViewById(R.id.editText5); // id_stup
        txt2 = root.findViewById(R.id.textView30); // id stupina
        txt3 = root.findViewById(R.id.textView21); // numar rame
        txt4 = root.findViewById(R.id.textView27); // tip stup
        txt5 = root.findViewById(R.id.textView33); //rasa
        txt6 = root.findViewById(R.id.textView34); // mod
        txt7 = root.findViewById(R.id.textView23); // varsta
        txt8 = root.findViewById(R.id.textView32); // culaore
        txt9 = root.findViewById(R.id.editText6); // data tratament


        String[] elements = new String[]{};

//        elementsForAdd = new ArrayList<String>(Arrays.asList(elements));
//        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, elementsForAdd) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView tv = (TextView) view.findViewById(android.R.id.text1);
//                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//                tv.setText(Html.fromHtml(" - "+ tv.getText().toString()));
//                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
////                tv.setTypeface(Typeface.create("casual", Typeface.BOLD));
//                return view;
//            }
//        };
//
//        listView.setAdapter(arrayAdapter);

        takeDataLater object = new takeDataLater();
        object.execute();
        return root;
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/tratamente/" + id;
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
                            System.out.println("Am primit de data aceasta" + response.toString());
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

                    String id_stup = index.getString("id_stup");
                    txt2.setText(id_stup);

                    String data_tratament = index.getString("data_tratament");
                    txt3.setText(data_tratament);

                    String afectiune = index.getString("afectiune");
                    txt4.setText(afectiune);

                    String produs = index.getString("produs");
                    txt5.setText(produs);

                    String mod_administrare = index.getString("mod_administrare");
                    txt6.setText(mod_administrare);

                    String familii_albine = index.getString("familii_albine");
                    txt6.setText(familii_albine);

                    String doza = index.getString("doza");
                    txt7.setText(doza);

                    String cantitate = index.getString("cantitate");
                    txt8.setText(cantitate);

                    String observatii = index.getString("observatii");
                    txt9.setText(observatii);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
