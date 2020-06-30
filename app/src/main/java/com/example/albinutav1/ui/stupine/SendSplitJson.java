package com.example.albinutav1.ui.stupine;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class SendSplitJson {
    private static String result;

    public void sendPost(final String method, final String urlSend, final String idStupinaVeche,
                         final String id_user, final String locatieStupinaNoua,
                         final String dataStupinaNoua, final Vector<String> id, final Double lon, final Double lat, final Double alti) {

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
                    jsonParam.put("id_stupina_veche", idStupinaVeche);
                    jsonParam.put("nr_stupi", id.size());
                    jsonParam.put("locatie_stupina_noua", locatieStupinaNoua);
                    jsonParam.put("data_stupina_noua", dataStupinaNoua);
                    jsonParam.put("lista_stupi", id);
                    jsonParam.put("id_user", id_user);
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

    public String getResult() {
        return result;
    }
}
