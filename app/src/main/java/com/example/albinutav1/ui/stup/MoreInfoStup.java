package com.example.albinutav1.ui.stup;

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
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian3d;
import com.anychart.core.cartesian.series.Area3d;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.hatchfill.HatchFillType;
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

public class MoreInfoStup extends Fragment {
    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txt5;
    private TextView txt6;
    private TextView txt7;
    private TextView txt8;
    private AnyChartView anyChartView;
    private String id_stup_show;
    private TextView txt9;
    private TextView txt10;
    private TextView txt11;
    private TextView txt12;
    private TextView txt13;
    private TextView txt14, txt15, txt16;
    private JSONArray responsePrime;
    private android.content.Context contextMain;
    private String id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contextMain = getContext();
        id = getArguments().getString("idStupDetail");

        View root = inflater.inflate(R.layout.fragment_detalii_stup, container, false);
        txt1 = root.findViewById(R.id.editText5); // id_stup
        txt2 = root.findViewById(R.id.textView30); // id stupina
        txt3 = root.findViewById(R.id.textView21); // numar rame
        txt4 = root.findViewById(R.id.textView27); // tip stup
        txt5 = root.findViewById(R.id.textView33); //rasa
        txt6 = root.findViewById(R.id.textView34); // mod
        txt7 = root.findViewById(R.id.textView23); // varsta
        txt8 = root.findViewById(R.id.textView32); // culaore
        txt9 = root.findViewById(R.id.editText6); // data tratament
        txt10 = root.findViewById(R.id.editText7); // afectiune
        txt11 = root.findViewById(R.id.editText8); // observatii
        txt12 = root.findViewById(R.id.editText9); // data hranire
        txt13 = root.findViewById(R.id.editText10); // tip hrana
        txt14 = root.findViewById(R.id.editText11); // note
        txt16 = root.findViewById(R.id.rame_hrana);
        txt15 = root.findViewById(R.id.rame_puiet);


        anyChartView = root.findViewById(R.id.any_chart_stup_detail);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar_stup_detail));

        takeDataLater obj = new takeDataLater();
        obj.execute();
        new takeDataLaterForGraph().execute();
        return root;
    }

    private void create_graph(List<DataEntry> received) {
        Cartesian3d area3d = AnyChart.area3d();

        area3d.xAxis(0).labels().format("{%Value}h");

        area3d.animation(true);

        area3d.yAxis(0).title("Grade Celsius");
        area3d.xAxis(0).title("12 Ore");
        area3d.xAxis(0).labels().padding(5d, 5d, 0d, 5d);
        area3d.title("Temperatura stupului " + id_stup_show + "<br/>' +\n" +
                "    '<span style=\"color:#212121; font-size: 13px;\">Datele sunt colectate în ultimele 12 ore</span>");

        area3d.title().useHtml(true);
        area3d.title().padding(0d, 0d, 20d, 0d);

        List<DataEntry> seriesData;
        seriesData = received;

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

        Area3d series1 = area3d.area(series1Data);
        series1.name("Temperatura în interiorul stupului");
        series1.hovered().markers(false);
        series1.hatchFill("diagonal", "#000", 0.6d, 10d);

        Area3d series2 = area3d.area(series2Data);
        series2.name("Temperatura din exteriorul stupului: ");
        series2.hovered().markers(false);
        series2.hatchFill(HatchFillType.DIAGONAL_BRICK, "#000", 0.6d, 10d);

        area3d.tooltip()
                .position(Position.CENTER_TOP)
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.LEFT_BOTTOM)
                .offsetX(5d)
                .offsetY(5d);

        area3d.interactivity().hoverMode(HoverMode.BY_X);
        area3d.zAspect("100%");

        anyChartView.setChart(area3d);
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/stup/" + id + "/more";
            System.out.print("URL: " + url);
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
                                System.out.println("Este gol!");
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
//                    txt2.setText(idStupina);
                    String rame_puiet = index.getString("rame_puiet");
                    txt15.setText(rame_puiet);
                    String rame_hrana = index.getString("rame_hrana");
                    txt16.setText(rame_hrana);
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
                    String afectiune = index.getString("afectiune");
                    txt10.setText(afectiune);
                    String data_hranire = index.getString("data_hranire");
                    txt12.setText(data_hranire);
                    String data_tratament = index.getString("data_tratament");
                    txt9.setText(data_tratament);
                    String nota_hranire = index.getString("nota_hranire");
                    txt14.setText(nota_hranire);
                    String observatii = index.getString("observatii");
                    txt11.setText(observatii);
                    String tip_hrana = index.getString("tip_hrana");
                    txt13.setText(tip_hrana);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class takeDataLaterForGraph extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
            String url = ip + "/save_data/" + id;
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
                                new utilityFunction().showToast(getContext(), "Nu preluati date cu Arduino", true);
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
            String data, temp_ex, temp;
            boolean forShow = false;
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
//                    if (index.getString("id_stupina").equals(id)) {
                    temp = index.getString("temperatura");
                    temp_ex = index.getString("temp_ex");
                    id_stup_show = index.getString("id_stup");
                    data = Integer.toString(i);
                    forShow = true;
                    forSend.add(new CustomDataEntry(data, Float.parseFloat(temp), Float.parseFloat(temp_ex)));
//                    }
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
        CustomDataEntry(String x, Float value, Float value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
}
