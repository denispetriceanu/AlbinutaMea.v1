package com.example.albinutav1.ui.stupine;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StupineSendServerAsyncT {
    public static String result;

    public void sendPost(final String method,
                         final String urlSend,
                         final String locatie,
                         final String nr_stupi,
                         final String data_plasare,
                         final String lon,
                         final String lat,
                         final String alti) {

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
                    jsonParam.put("locatie", locatie);
                    jsonParam.put("nr_stupi", nr_stupi);
                    jsonParam.put("data_plasare", data_plasare);
                    jsonParam.put("longitudinea", lon);
                    jsonParam.put("latitudinea", lat);
                    jsonParam.put("altitudinea", alti);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                    result = conn.getResponseMessage();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    result = e.toString();
                }
            }
        });

        thread.start();
    }
}
