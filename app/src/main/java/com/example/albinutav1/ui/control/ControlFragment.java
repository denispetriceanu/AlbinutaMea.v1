package com.example.albinutav1.ui.control;

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

public class ControlFragment extends Fragment {
    private ControlAdapter adapter;
    private android.content.Context contextMain;
    private JSONArray responsePrime;
    private RecyclerView rvControl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_control, container, false);
        contextMain = getContext();

        rvControl = root.findViewById(R.id.rvControl);

        ImageView imgView = root.findViewById(R.id.image_add_control);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilityFunction ob = new utilityFunction();
                ob.move(view, "", R.id.nav_addcontrol, "");
            }
        });

//            final SwipeRefreshLayout refreshLayout = root.findViewById(R.id.fragment_to_swipe_control);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast.makeText(getContext(), "Pagina a fost reîncărcată", Toast.LENGTH_SHORT).show();
//                takeDataLater obj = new takeDataLater();
//                table.removeViews(1, table.getChildCount()-1);
//                obj.execute();
//                refreshLayout.setRefreshing(false);
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
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/data_controlVeterinar/" + id_user;
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
                                Toast.makeText(getContext(), "Nu aveți control inregistrat", Toast.LENGTH_LONG).show();
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
            ArrayList<String> idControl = new ArrayList<>();
            ArrayList<String> dataList = new ArrayList<>();
            ArrayList<String> stare = new ArrayList<>();
            ArrayList<String> idStupina = new ArrayList<>();
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    idControl.add(index.getString("id_control"));
                    idStupina.add(index.getString("id_stupina"));
                    dataList.add(index.getString("data_control"));
                    stare.add(index.getString("stare"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RecyclerView recyclerView = rvControl;
            recyclerView.setLayoutManager(new LinearLayoutManager(contextMain));
            adapter = new ControlAdapter(contextMain, idControl, dataList, stare, idStupina);
            recyclerView.setAdapter(adapter);
        }
    }
}
