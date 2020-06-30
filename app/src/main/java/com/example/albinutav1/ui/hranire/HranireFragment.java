package com.example.albinutav1.ui.hranire;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class HranireFragment extends Fragment {

    private android.content.Context contextMain;
    private JSONArray responsePrime;
    private HranireAdapter adapter;
    private String id;
    private RecyclerView rvStupina;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_hranire, container, false);
        contextMain = getContext();

        rvStupina = root.findViewById(R.id.rvHranire);

        ImageView imgAdd = root.findViewById(R.id.image_add_hranire);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilityFunction object = new utilityFunction();
                object.move(view, "", R.id.nav_addHranire, "");
            }
        });

//
//        final SwipeRefreshLayout mSwipeRefreshLayout = root.findViewById(R.id.fragment_to_swipe_hranire);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast.makeText(getContext(), "Pagina a fost reîncărcată!", Toast.LENGTH_SHORT).show();
//                tableHranire.removeViews(1, tableHranire.getChildCount()-1);
//                takeDataLater obj = new takeDataLater();
//                obj.execute();
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });

        takeDataLater obj = new takeDataLater();
        obj.execute();
        return root;
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            String id_user = new utilityFunction().readFromStorage(getContext());

            String url = "http://192.168.1.83:5000/data_hranire/" + id_user;
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
                                System.out.println("Este gol!");
                                utilityFunction ob = new utilityFunction();
                                Toast.makeText(getContext(), "Nu aveți nici o " +
                                        "hrănire înregistrată în acest moment", Toast.LENGTH_LONG).show();
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
            ArrayList<String> dataHranire = new ArrayList<String>();
            ArrayList<String> isStup = new ArrayList<String>();
            ArrayList<String> idList = new ArrayList<String>();
            ArrayList<String> tipHranire = new ArrayList<String>();
            String id;
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {

                    index = responsePrime.getJSONObject(i);
                    idList.add(index.getString("id_hranire"));
                    isStup.add(index.getString("id_stup"));
                    dataHranire.add(index.getString("data_hranire"));
                    tipHranire.add(index.getString("tip_hranire"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RecyclerView recyclerView = rvStupina;
            recyclerView.setLayoutManager(new LinearLayoutManager(contextMain));
            adapter = new HranireAdapter(contextMain, idList, dataHranire, isStup, tipHranire);
            recyclerView.setAdapter(adapter);
        }
    }
}