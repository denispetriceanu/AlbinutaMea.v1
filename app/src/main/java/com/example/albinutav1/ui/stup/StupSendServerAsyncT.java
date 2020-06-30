package com.example.albinutav1.ui.stup;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StupSendServerAsyncT {
    private static String result;

    public void sendPost(final String method, final String urlSend, final String idStupina, final String culoare,
                         final String rasa, final String tip, final String num,
                         final String modC, final String varsta, final String rame_puiet, final String rame_hrana) {

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
                    jsonParam.put("id_stupina", idStupina);
                    jsonParam.put("culoare_stup", culoare);
                    jsonParam.put("rasa_albine", rasa);
                    jsonParam.put("tip_stup", tip);
                    jsonParam.put("numarRame", num);
                    jsonParam.put("mod_constituire", modC);
                    jsonParam.put("varsta_matca", varsta);
                    jsonParam.put("rame_puiet", rame_puiet);
                    jsonParam.put("rame_hrana", rame_hrana);

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
