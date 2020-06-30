package com.example.albinutav1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import com.example.albinutav1.ui.acasa.HomeFragment;
import com.example.albinutav1.ui.user.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public String nume;
    private AppBarConfiguration mAppBarConfiguration;
    private android.content.Context contextMain;
    private NavigationView navigationView;
    private JSONArray responsePrime;
    private TextView prenume, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        prenume = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_user);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LayoutInflater li = LayoutInflater.from(getBaseContext());
//                View promptsView = li.inflate(R.layout.pop_up_edit_stupina, null);
//                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                        Objects.requireNonNull(getBaseContext()));
//                alertDialogBuilder.setView(promptsView);

//                final CheckBox checkBox = promptsView.findViewById(R.id.checkBox5);
//
//                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        if (b) {
//                            Toast.makeText(getBaseContext(), "Ceva de afisat", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//                final AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//                new utilityFunction().move(view, "", R.id.user_detail, "");
//            }
//        });

        contextMain = getBaseContext();
        // ToDo: Daca ceva nu merge decomenteaza de aici
//        Bundle text = new Bundle();
//        text.putString("Deniss", "nume");
        HomeFragment home = new HomeFragment();
//        home.setArguments(text);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // R.id.nav_send,
        // linia de mai sus a fost scoasa odata cu eliminarea butonului de backup din meniu din fisierul activity_main_drawer.xml
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_control, R.id.nav_wether, R.id.user_detail)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        new takeDataLater().execute();

        userData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setActionBarTitle(Spanned title) {
        getSupportActionBar().setTitle(title);
    }

    // function for log out the user
    public void logOut() {
        File dir = getFilesDir();
        File file = new File(dir, "dataLog.csv");
        boolean deleted = file.delete();
        Toast.makeText(MainActivity.this, "Ati fost delogat!", Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }

    public void userData() {
        View hView = navigationView.getHeaderView(0);
        final ImageView logOut = hView.findViewById(R.id.imageView6More);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private class takeDataLater extends AsyncTask<String, String, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            final android.content.Context context = contextMain;
            String id_user = new utilityFunction().readFromStorage(getBaseContext());
            String ip = Objects.requireNonNull(context).getResources().getString(R.string.ip);
            String url = ip + "/data_user/" + id_user;
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
                                Toast.makeText(MainActivity.this, "S-a petrecut o eroare", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(MainActivity.this, "S-a petrecut o eroare." +
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
            String emailData = "", prenumeData = "", numeT = "";
            for (int i = 0; i < responsePrime.length(); i++) {
                JSONObject index = null;
                try {
                    index = responsePrime.getJSONObject(i);
                    numeT = index.getString("nume");
                    prenumeData = index.getString("prenume");
                    emailData = index.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            nume = numeT;

//            Bundle text = new Bundle();
//            text.putString(numeT, "nume");
//            HomeFragment home = new HomeFragment();
//            home.setArguments(text);
            prenume.setText(prenumeData + " " + nume);
            email.setText(emailData);
        }
    }
}