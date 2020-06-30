package com.example.albinutav1.ui.stup;

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
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.albinutav1.MySingleton;
import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class StupFragment extends Fragment {
    private StupAdapter adapter;
    private String id;
    private AnyChartView anyChartView;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private RecyclerView rvStup;
    private String id_stup_show;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stup, container, false);

        assert getArguments() != null;
        id = getArguments().getString("idStupina");

        contextMain = getContext();
        rvStup = root.findViewById(R.id.rvStup);

        anyChartView = root.findViewById(R.id.any_chart_stup);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar_stup));

        ImageView imgView = root.findViewById(R.id.image_add_stup);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilityFunction ob = new utilityFunction();
                ob.move(view, id, R.id.nav_addstup, "id_stupina_autocomplete");
            }
        });

        final SwipeRefreshLayout mSwipeRefreshLayout = root.findViewById(R.id.fragment_to_swipe_stup);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "Pagina a fost reîncărcată!", Toast.LENGTH_SHORT).show();
                new takeDataLater().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        new takeDataLater().execute();
        new takeDataLaterForGraph().execute();


        return root;
    }

    private void create_graph(List<DataEntry> received) {
        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Date afisate in timp real de la stupinda.");

//        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

//        List<DataEntry> seriesData = received;
//        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
//        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
//        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
//        seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
//        seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
//        seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
//        seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
//        seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));
//        seriesData.add(new CustomDataEntry("1994", 13.2, 24.7, 19.2));
//        seriesData.add(new CustomDataEntry("1995", 12.0, 18.0, 14.4));
//        seriesData.add(new CustomDataEntry("1996", 3.2, 15.1, 9.2));
//        seriesData.add(new CustomDataEntry("1997", 4.1, 11.3, 5.9));
//        seriesData.add(new CustomDataEntry("1998", 6.3, 14.2, 5.2));
//        seriesData.add(new CustomDataEntry("1999", 9.4, 13.7, 4.7));
//        seriesData.add(new CustomDataEntry("2000", 11.5, 9.9, 4.2));
//        seriesData.add(new CustomDataEntry("2001", 13.5, 12.1, 1.2));
//        seriesData.add(new CustomDataEntry("2002", 14.8, 13.5, 5.4));
//        seriesData.add(new CustomDataEntry("2003", 16.6, 15.1, 6.3));
//        seriesData.add(new CustomDataEntry("2004", 18.1, 17.9, 8.9));
//        seriesData.add(new CustomDataEntry("2005", 17.0, 18.9, 10.1));
//        seriesData.add(new CustomDataEntry("2006", 16.6, 20.3, 11.5));
//        seriesData.add(new CustomDataEntry("2007", 14.1, 20.7, 12.2));
//        seriesData.add(new CustomDataEntry("2008", 15.7, 21.6, 10));
//        seriesData.add(new CustomDataEntry("2009", 12.0, 22.5, 8.9));

        System.out.println("date: " + received.toString());
        Set set = Set.instantiate();
        set.data(received);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Temperatura");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Vizibilitate");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Presiune");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {


        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String id_user = new utilityFunction().readFromStorage(getContext());
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);

            String url = ip + "/stup/usstupina/" + id + "/" + id_user;
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
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Nu aveți nici " +
                                                "un stup în această stupină", Toast.LENGTH_LONG).show();
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "S-a petrecut o eroare." +
                                            "Verificați conexiunea la internet", Toast.LENGTH_LONG).show();
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
            ArrayList<String> culoare = new ArrayList<>();
            ArrayList<String> varsta = new ArrayList<>();
            ArrayList<String> idList = new ArrayList<>();
            ArrayList<String> rasa = new ArrayList<>();

            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {

                    index = responsePrime.getJSONObject(i);
                    idList.add(index.getString("id_stup"));
                    culoare.add(index.getString("culoare_stup"));
                    rasa.add(index.getString("rasa_albine"));
                    varsta.add(index.getString("varsta_matca"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            RecyclerView recyclerView = rvStup;
            System.out.println("id--ul primit: " + idList.toString());
            recyclerView.setLayoutManager(new LinearLayoutManager(contextMain));
            adapter = new StupAdapter(contextMain, idList, culoare, varsta, rasa);
            recyclerView.setAdapter(adapter);

        }
    }

    private class takeDataLaterForGraph extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;

            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);

            String url = ip + "/get_data_stupina/" + id;
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
                                anyChartView.setVisibility(View.GONE);
                                new utilityFunction().showToast(getContext(), "Nu aveti date de la Arduino", false);
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
            List<DataEntry> forSend = new ArrayList<>();
            String data, temp_ex, vizibilitate, presiune;
            boolean forShow = false;
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    if (index.getString("id_stupina").equals(id)) {
                        vizibilitate = index.getString("vizibilitate");
                        presiune = index.getString("presiune");
                        temp_ex = index.getString("temp_ex");
                        id_stup_show = index.getString("id_stup");
                        data = Integer.toString(i);
                        forShow = true;
                        forSend.add(new CustomDataEntry(data, Float.parseFloat(temp_ex), Integer.parseInt(vizibilitate), Float.parseFloat(presiune) / 10000.0));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (forShow) {
                create_graph(forSend);
                anyChartView.setZoomEnabled(true);
            } else {
                anyChartView.setVisibility(View.GONE);
            }
        }
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

    }
}
