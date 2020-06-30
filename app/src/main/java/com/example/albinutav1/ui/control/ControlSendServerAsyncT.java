package com.example.albinutav1.ui.control;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ControlSendServerAsyncT {
    private static String result;

    public void sendPost(final String method, final String urlSend, final String id_stupina, final String data_control,
                         final String stare, final String examinare, final String proba,
                         final String concluzii, final String veterinar, final String observatii) {

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
                    jsonParam.put("id_stupina", id_stupina);
                    jsonParam.put("data_control", data_control);
                    jsonParam.put("examinare", examinare);
                    jsonParam.put("stare", stare);
                    jsonParam.put("proba", proba);
                    jsonParam.put("concluzii", concluzii);
                    jsonParam.put("veterinar", veterinar);
                    jsonParam.put("observatii", observatii);

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

    public String getResult(){
        return result;
    }
}
