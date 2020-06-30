package com.example.albinutav1.ui.tratamente;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TratamenteSendServerAsyncT {
    public static String result;
    public void sendPost(final String method,
                         final String urlSend,
                         final String id_stup,
                         final String data,
                         final String afectiune,
                         final String produs,
                         final String mod_admin,
                         final String familii_albine,
                         final String doza,
                         final String cantitate,
                         final String observatii) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlSend);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(method);
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id_stup", id_stup);
                    jsonParam.put("data_tratament", data);
                    jsonParam.put("afectiune", afectiune);
                    jsonParam.put("produs", produs);
                    jsonParam.put("mod_administrare", mod_admin);
                    jsonParam.put("familii_albine", familii_albine);
                    jsonParam.put("doza", doza);
                    jsonParam.put("cantitate", cantitate);
                    jsonParam.put("observatii", observatii);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
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
