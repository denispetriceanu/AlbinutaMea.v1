package com.example.albinutav1.ui.tratamente;

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
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class TratamenteFragment extends Fragment {
    private android.content.Context contextMain;
    private JSONArray responsePrime;
    private TratamenteAdapter adapter;
    private RecyclerView rV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tratamente, container, false);
        contextMain = getContext();

        final ImageView img = root.findViewById(R.id.image_add_tratamente);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilityFunction ob = new utilityFunction();
                ob.move(view, "", R.id.nav_addTratament, "");
            }
        });

        rV = root.findViewById(R.id.rvTratamente);


//        final SwipeRefreshLayout mSwipeRefreshLayout = root.findViewById(R.id.fragment_to_swipe_tratamente);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast.makeText(getContext(), "Pagina a fost reîncărcată!", Toast.LENGTH_SHORT).show();
//                tableTratamente.removeViews(1, tableTratamente.getChildCount()-1);
//                final takeDataLater obj = new takeDataLater();
//                obj.execute();
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });
        final takeDataLater obj = new takeDataLater();
        obj.execute();
        return root;
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String id_user = new utilityFunction().readFromStorage(getContext());

            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/tratamentefacute/" + id_user;

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

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getContext(), "Nu aveți nici " +
                                                "un tratament realizat până acum", Toast.LENGTH_LONG).show();
                                    }
                                });
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
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "S-a petrecut o eroare." +
                                            "Verificati conexiunea la internet", Toast.LENGTH_LONG).show();
                                }
                            });
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
            ArrayList<String> data = new ArrayList<String>();
            ArrayList<String> afectiune = new ArrayList<String>();
            ArrayList<String> idStup = new ArrayList<String>();
            ArrayList<String> idList = new ArrayList<String>();
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {

                    index = responsePrime.getJSONObject(i);
                    idList.add(index.getString("id_tratament"));
                    idStup.add(index.getString("id_stup"));
                    data.add(index.getString("data_tratament"));
                    afectiune.add(index.getString("afectiune"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RecyclerView recyclerView = rV;
            recyclerView.setLayoutManager(new LinearLayoutManager(contextMain));
            adapter = new TratamenteAdapter(contextMain, idList, data, afectiune, idStup);
            recyclerView.setAdapter(adapter);
        }
    }

}