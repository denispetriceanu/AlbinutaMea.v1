package com.example.albinutav1.ui.stupine;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.albinutav1.MySingleton;
import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class StupinaFragment extends Fragment {
    private StupinaAdapter adapter;
    private JSONArray responsePrime;
    //    private AnyChartView anyChartView;
    private android.content.Context contextMain;
    private RecyclerView rvStupina;
    private String stupina1, stupina2;
    private ArrayList<String> idList = new ArrayList<>();
    private AlertDialog alertDialog;
    private String id_stupina;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stupina, container, false);
        contextMain = getContext();
        rvStupina = (RecyclerView) root.findViewById(R.id.rvStupine);
//        anyChartView = root.findViewById(R.id.any_chart_stupina);
//        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar_stupina));
        ImageView joinImg = root.findViewById(R.id.imageView8);

        joinImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.pop_up_join, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        requireContext());
                alertDialogBuilder.setView(promptsView);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(), R.layout.row_spinner, idList) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.parseColor("#FF7C7967"));
                        tv.setBackgroundColor(Color.parseColor("#FFC3C0AA"));
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                final Spinner stupinaBaza = promptsView
                        .findViewById(R.id.spinner);
                stupinaBaza.setAdapter(null);
                stupinaBaza.setAdapter(adapter);
                final Spinner stupinaSec = promptsView
                        .findViewById(R.id.spinner2);
                stupinaSec.setAdapter(null);
                stupinaSec.setAdapter(adapter);

                alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                //        for reset password
                Button reset = promptsView.findViewById(R.id.buttonJoin);
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stupina1 = stupinaBaza.getSelectedItem().toString();
                        stupina2 = stupinaSec.getSelectedItem().toString();
                        new makeJoin().execute();
                    }
                });

            }
        });

        ImageView imgView = root.findViewById(R.id.image_add_stupina);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilityFunction ob = new utilityFunction();
                ob.move(view, "", R.id.nav_add, "");
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.fragment_to_swipe_stupine);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new takeDataLater().execute();
                Toast.makeText(getContext(), "Pagina a fost reîncărcată!", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        new takeDataLater().execute();
//        new takeDataLaterForGraph().execute();
        return root;
    }

//    private void create_graph(List<DataEntry> received) {
//        Cartesian cartesian = AnyChart.column();
//        System.out.println(received.toString());
//        cartesian.removeAllSeries();
//        List<DataEntry> data;
//        data = received;
//
//        Column column = cartesian.column(data);
//
//        column.tooltip()
//                .titleFormat("ora: {%X}")
//                .position(Position.CENTER_BOTTOM)
//                .anchor(Anchor.CENTER_BOTTOM)
//                .offsetX(0d)
//                .offsetY(5d)
//                .format("{%Value}{groupsSeparator: }°C");
//
//        cartesian.animation(true);
//        cartesian.title("Temperatura din stupina " + id_stupina + " în ultimele 12 ore");
//
//        cartesian.yScale().minimum(0d);
//
//        cartesian.yAxis(0).labels().format("°C{%Value}{groupsSeparator: }");
//
//        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
//        cartesian.interactivity().hoverMode(HoverMode.BY_X);
//
//        cartesian.xAxis(0).title("Ore");
//        cartesian.yAxis(0).title("Temperatura");
//
//        anyChartView.setChart(cartesian);
//    }

    @SuppressLint("StaticFieldLeak")
    private class takeDataLater extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            // function for verify time which run this app
            long started = SystemClock.elapsedRealtime();

            final android.content.Context context = contextMain;

            utilityFunction ob = new utilityFunction();
            String id_user = ob.readFromStorage(requireContext());

            String ip = requireContext().getResources().getString(R.string.ip);
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
                                requireActivity().runOnUiThread(new Runnable() {
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
                            requireActivity().runOnUiThread(new Runnable() {
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
//            System.out.println("Time: ");
//            System.out.println(SystemClock.elapsedRealtime() - started);
            return responsePrime;
        }

        @Override
        protected void onPostExecute(JSONArray response1) {
            super.onPostExecute(response1);
        }

        private void createTable() {
            ArrayList<String> dateList = new ArrayList<>();
            ArrayList<String> locationList = new ArrayList<>();
            ArrayList<String> nr_combList = new ArrayList<>();
            idList.removeAll(idList);
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    locationList.add(index.getString("locatie"));
                    dateList.add(index.getString("data_plasare"));
                    nr_combList.add(index.getString("nr_stupi"));
                    idList.add(index.getString("id_stupina"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RecyclerView recyclerView = rvStupina;
            recyclerView.setLayoutManager(new LinearLayoutManager(contextMain));
            adapter = new StupinaAdapter(contextMain, idList, locationList, nr_combList, dateList);
            recyclerView.setAdapter(adapter);
        }
    }

//    private class takeDataLaterForGraph extends AsyncTask<String, String, JSONArray> {
//
//        @Override
//        protected JSONArray doInBackground(String... strings) {
//            final android.content.Context context = contextMain;
//
//            String url = "http://192.168.1.83:5000/save_data";
//            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);// 1MB cap
//            Network network = new BasicNetwork(new HurlStack());
//            RequestQueue requestQueue = new RequestQueue(cache, network);
//            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                    Request.Method.GET,
//                    url,
//                    null,
//                    new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            responsePrime = response;
//                            if (responsePrime.length() == 0) {
//                                Toast.makeText(getContext(), "S-a petrecut o eroare", Toast.LENGTH_LONG).show();
//                            } else {
//                                Log.d(TAG, responsePrime.toString());
//                                createTable();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            System.out.println("Error: " + error);
//                            Toast.makeText(getContext(), "S-a petrecut o eroare." +
//                                    "Verificațivă conexiunea la internet", Toast.LENGTH_LONG).show();
//                        }
//                    }
//            );
//            requestQueue.add(jsonArrayRequest);
//            MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
//            return responsePrime;
//        }
//
//        @Override
//        protected void onPostExecute(JSONArray response1) {
//            super.onPostExecute(response1);
//        }
//
//        private void createTable() {
//            List<DataEntry> forSend = new ArrayList<>();
//            String data = "", temp_ex = "";
//            for (int i = 0; i < responsePrime.length(); i++) {
//                JSONObject index = null;
//                try {
//                    index = responsePrime.getJSONObject(i);
//                    id_stupina = index.getString("id_stupina");
//                    temp_ex = index.getString("temp_ex");
//                    data = Integer.toString(i);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                forSend.add(new ValueDataEntry(data, Float.parseFloat(temp_ex)));
//            }
//            create_graph(forSend);
//        }
//    }

    private class makeJoin extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            String ip = requireContext().getResources().getString(R.string.ip);
            String url = ip + "/stupina_modify/" + stupina1 + "/" + stupina2;
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);// 1MB cap
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue requestQueue = new RequestQueue(cache, network);
            StringRequest jsonArrayRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Răspunsul este:" + response);
                            if (response.equals("Success")) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new utilityFunction().showToast(getContext(), "Am reușit să realizăm modificarea cu succes", true);
                                    }
                                });
                                alertDialog.dismiss();
                            } else {
                                Log.d(TAG, response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error: " + error);
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "S-a petrecut o eroare." +
                                            "Verificațivă conexiunea la internet", Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    }
            );
            requestQueue.add(jsonArrayRequest);
            MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
            return "Success";
        }

        @Override
        protected void onPostExecute(String response1) {
            super.onPostExecute(response1);
        }
    }
}