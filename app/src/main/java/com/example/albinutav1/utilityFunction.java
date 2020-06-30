package com.example.albinutav1;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Vector;

public class utilityFunction {
    private static String url = "http://192.168.1.83:5000/";
    private static Vector<String> stupNewStupina = new Vector<>();

    public static String getUrl() {
        return url;
    }

    public Vector<String> getStupNewStupina() {
        return stupNewStupina;
    }

    public void setStupNewStupina(Vector<String> stupNewStupina) {
        utilityFunction.stupNewStupina = stupNewStupina;
    }

    public void move(View view, String id, int fragment, String key) {
        System.out.println("ID apelat: " + id);
        Bundle bundle = new Bundle();
        bundle.putString(key, id);
        Navigation.findNavController(view).navigate(fragment, bundle);
    }

    public String changeTheDiacritics(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String readFromStorage(Context context) {
        File internalStorageDir = context.getFilesDir();
        File test = new File(internalStorageDir, "dataLog.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(test));
            String st;
            String textToShow = "";
            while ((st = br.readLine()) != null) {
                textToShow = textToShow + " " + st;
            }
            return textToShow;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showToast(android.content.Context context, String text, boolean shortOrLong) {
        if (shortOrLong) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }
}
