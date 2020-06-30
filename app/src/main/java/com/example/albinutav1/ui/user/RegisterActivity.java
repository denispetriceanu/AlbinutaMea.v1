package com.example.albinutav1.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextView nume;
    private TextView prenume;
    private TextView email;
    private TextView parola;
    private TextView adresa;
    private Context context = this;
    private TextView telefon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        this.context = getContext();
        nume = findViewById(R.id.editText22);
        prenume = findViewById(R.id.editText23);
        email = findViewById(R.id.editText24);
        parola = findViewById(R.id.editText25);
        adresa = findViewById(R.id.editText26);
        telefon = findViewById(R.id.editText27);
        TextView txt = findViewById(R.id.textView66);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });

        Switch viewPass = findViewById(R.id.switch2);
        viewPass.setChecked(true);
        viewPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    parola.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    parola.setTransformationMethod(null);
                }
            }
        });

        Button btn = findViewById(R.id.button12);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyIfIs()) {
                    UserSendServerAsyncT ob = new UserSendServerAsyncT();
                    utilityFunction change = new utilityFunction();
                    String ip = Objects.requireNonNull(context).getResources().getString(R.string.ip);
                    ob.sendPost("POST", ip + "/user/any",
                            change.changeTheDiacritics(nume.getText().toString()),
                            change.changeTheDiacritics(prenume.getText().toString()),
                            change.changeTheDiacritics(email.getText().toString()),
                            parola.getText().toString(),
                            change.changeTheDiacritics(adresa.getText().toString()),
                            change.changeTheDiacritics(telefon.getText().toString()));
                }
            }
        });
    }

    public Boolean verifyIfIs() {
        if (nume.getText().toString() != "" && prenume.getText().toString() != "" && parola.getText().toString() != "" &&
                email.getText().toString() != "" && telefon.getText().toString() != "") {
            return true;
        } else {
            return false;
        }
    }

    private class UserSendServerAsyncT {

        public void sendPost(final String method,
                             final String urlSend,
                             final String nume,
                             final String prenume,
                             final String email,
                             final String parola,
                             final String adresa,
                             final String telefon) {

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
                        jsonParam.put("nume", nume);
                        jsonParam.put("prenume", prenume);
                        jsonParam.put("email", email);
                        jsonParam.put("parola", parola);
                        jsonParam.put("adresa", adresa);
                        jsonParam.put("telefon", telefon);

                        Log.i("JSON", jsonParam.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(jsonParam.toString());

                        os.flush();
                        os.close();

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));

                        if (Integer.parseInt(String.valueOf(conn.getResponseCode())) == 666) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "Acest cont exista deja!"
                                            , Toast.LENGTH_LONG).show();
                                }
                            });
                            System.out.println("Am intrat in if");
                        } else {
                            Log.i("MSG", conn.getResponseMessage());
                            conn.disconnect();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "Contul a fost facut!", Toast.LENGTH_LONG).show();
                                }
                            });
                            Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(myIntent);
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "Eroare: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            });

            thread.start();
        }
    }
}
