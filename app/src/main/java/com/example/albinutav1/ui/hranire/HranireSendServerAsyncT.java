package com.example.albinutav1.ui.hranire;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HranireSendServerAsyncT {
    private static String result;

    public void sendPost(final String method, final String urlSend, final String idStup, final String data_hranire,
                         final String tip_hranare, final String tip_hrana, final String produs,
                         final String producator, final String cantitate, final String nota) {

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
                    jsonParam.put("id_stup", idStup);
                    jsonParam.put("data_hranire", data_hranire);
                    jsonParam.put("tip_hrana", tip_hrana);
                    jsonParam.put("tip_hranire", tip_hranare);
                    jsonParam.put("produs", produs);
                    jsonParam.put("producator", producator);
                    jsonParam.put("cantitate", cantitate);
                    jsonParam.put("nota", nota);

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
