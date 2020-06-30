package com.example.albinutav1.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albinutav1.MainActivity;
import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Context context = this;
    private String email_reset;
    private JSONObject responsePrime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editText12);
        password = findViewById(R.id.editText20);
        TextView newCont = findViewById(R.id.textView59);

        if (verifyLog()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }

//      on press button new account show a new activity
        newCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });

//      show for reset password
        TextView resetPass = findViewById(R.id.textView67);
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.custom, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                //        for reset password
                Button reset = promptsView.findViewById(R.id.button13);
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        email_reset = userInput.getText().toString();
                        System.out.println("Aici " + userInput.getText().toString());
                        Toast toast = Toast.makeText(context, "Am trimis un email la adresa: "
                                + email_reset + " \nVerificați emailul", Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if (v != null) v.setGravity(Gravity.CENTER);
                        toast.show();

                        Map<String, String> postData = new HashMap<>();
                        System.out.println(email_reset);
                        postData.put("email", email_reset);
                        HttpPostAsyncTaskForReset task = new HttpPostAsyncTaskForReset(postData);
                        String baseUrl = Objects.requireNonNull(context).getResources().getString(R.string.ip);
                        task.execute(baseUrl + "/recover_pass");
                        alertDialog.dismiss();
                    }
                });

            }
        });


//        switch for show password
        Switch viewPass = findViewById(R.id.switch1);
        viewPass.setChecked(true);
        viewPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    password.setTransformationMethod(null);
                }
            }
        });

//        for connection to the app
        Button button = findViewById(R.id.button11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyDate()) {
                    email.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    password.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    Map<String, String> postData = new HashMap<>();
                    postData.put("email", email.getText().toString());
                    postData.put("pass", password.getText().toString());
                    HttpPostAsyncTask task = new HttpPostAsyncTask(postData);
                    String baseUrl = Objects.requireNonNull(context).getResources().getString(R.string.ip);
                    task.execute(baseUrl + "/user");
                } else {
                    Toast.makeText(LoginActivity.this, "Nu ati completat toate campurile", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //    function which read from local storage and if find some int that mean user is logged;
    public Boolean verifyLog() {
        File internalStorageDir = getFilesDir();
        try {
            File test = new File(internalStorageDir, "dataLog.csv");
            BufferedReader br = new BufferedReader(new FileReader(test));
            String st;
            String textToShow = "";
            while ((st = br.readLine()) != null) {
                textToShow = textToShow + " " + st;
            }
            if (textToShow != "") {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return null;
    }

    //    verify if exist some input which is not write
    public Boolean verifyDate() {
        utilityFunction ob = new utilityFunction();
        if ("".equals(ob.changeTheDiacritics(email.getText().toString())) &&
                "".equals(ob.changeTheDiacritics(password.getText().toString())) &&
                password.getText().toString().length() < 8) {
            return false;
        } else {
            return true;
        }
    }

    //    function for save data in local storage
    public void dataSave(String user) {
        File internalStorageDir = getFilesDir();
        File dataLog = new File(internalStorageDir, "dataLog.csv");
        try {
            FileOutputStream fos = new FileOutputStream(dataLog);
            fos.write(user.getBytes());
            Toast.makeText(LoginActivity.this, "Am salvat!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Eroare:" + e, Toast.LENGTH_LONG).show();
        }
    }


    public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {
        JSONObject postData;

        public HttpPostAsyncTask(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                System.out.println("Am ajuns aici----------");
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("DELETE");
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String decodedString;
                    while ((decodedString = in.readLine()) != null) {
                        System.out.println(decodedString);
                        if (!(decodedString.equals("\"false\""))) {
//                            go to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                            finish();

                            //save date isLoged for don't logout when string exist;
                            dataSave(decodedString);
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Datele introduse sunt gresite",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    }
                    in.close();
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "S-a petrecut o eroare, verificati conexiunea la internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
            return null;
        }
    }


    public class HttpPostAsyncTaskForReset extends AsyncTask<String, Void, Void> {
        JSONObject postData;

        public HttpPostAsyncTaskForReset(Map<String, String> postData) {
            System.out.println(postData.toString());
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                System.out.println("Am ajuns aici----------");
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String decodedString;
                    while ((decodedString = in.readLine()) != null) {
                        System.out.println(decodedString);
                        if (!(decodedString.equals("\"Success\""))) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Datele introduse sunt corecte",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Datele introduse sunt gresite",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    }
                    in.close();
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "S-a petrecut o eroare, verificati conexiunea la internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
            return null;
        }
    }

}
