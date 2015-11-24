package co.edu.uniandes.isis2503.tbc.movilapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReservaMovibusActivity extends AppCompatActivity implements LocationListener {

    /*Para pedir un movibus se envía el id del usuario por parámetro y los datos del pedido como JSON de la siguiente manera
        fecha de ejecucion -fechaEjecucion
        posicion usuario  -latitudUsuario, longitudUsuario
        posicion destino -latitudDestino, longitudDestino
        tiempo estimado como valor predeterminado - tiempoEstimado*/

    solicitarMovibusTask solicitarMovis;
    /**
     * Users data.
     */
    private String usersCC;
    private String email;
    private Long userID;
    private String nomCon;

    /**
     * Movibus request parameters
     */
    private Double originLatPos;
    private Double originLonPos;
    private Double destLatPos;
    private Double destLonPos;
    private Date reqDate;
    private int predTime;

    /**
     * Booleans to manage Async Tasks
     */
    private boolean getPos;
    private boolean sendReq;
    private boolean finish;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_movibus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intent management.
        Intent intent = getIntent();
        usersCC = intent.getStringExtra(LoginActivity.USER_CC);
        email = intent.getStringExtra(LoginActivity.USER_EMAIl);
        userID = intent.getLongExtra(LoginActivity.USER_ID, 0);

        //Set listeners to buttons
        Button mGetPosButton = (Button) findViewById(R.id.get_position_button);
        mGetPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPos = true;
                getActualPosition();
            }
        });
        Button mRequestMovibusButton = (Button) findViewById(R.id.reservar_movibus_button);
        mRequestMovibusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReq = true;
                requestMovibus();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Show progress while the stations load.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        final View mProgressView = findViewById(R.id.estaciones_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void requestMovibus() {
        EditText posInic = (EditText) findViewById(R.id.posicion_inicial);
        String[] positions = posInic.getText().toString().split(",");
        originLatPos = Double.parseDouble(positions[0]);
        originLonPos = Double.parseDouble(positions[1]);
        EditText posDest = (EditText) findViewById(R.id.posicion_destino);
        String[] destinations = posDest.getText().toString().split(",");
        destLatPos = Double.parseDouble(destinations[0]);
        destLonPos = Double.parseDouble(destinations[1]);
        EditText fech = (EditText) findViewById(R.id.fecha_ejecucion);
        String[] tiemp = posDest.getText().toString().split(",");
        reqDate = new Date();
        predTime = 40;
        //Execute a task to retrieve stations info
        finish = false;
        solicitarMovis = new solicitarMovibusTask();
        solicitarMovis.execute();
        showProgress(true);
        while(!finish){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //Do Nothing.
            }
        }
        showProgress(false);
    }

    private void getActualPosition() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //checkPermission(LOCATION_SERVICE);
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        originLatPos = location.getLatitude();
        originLonPos = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("Latitude", "status");
    }

    /**
     * On successfull login show the next view.
     */
    public void mostrarReservas() {
        Intent intent = new Intent(this, ReservasActivity.class);
        intent.putExtra(LoginActivity.USER_EMAIl, email);
        intent.putExtra(LoginActivity.USER_CC, usersCC);
        intent.putExtra(LoginActivity.USER_ID, userID);
        startActivity(intent);
    }

    /**
     * Muestra un mensaje de reserva exitosa, y vuelve a la pantalla de reservas.
     */
    public void reservaExitosa() {
        AlertDialog alertDialog = new AlertDialog.Builder(ReservaMovibusActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Se ha realizado la reserva exitosamente");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mostrarReservas();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ReservaMovibus Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://co.edu.uniandes.isis2503.tbc.movilapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ReservaMovibus Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://co.edu.uniandes.isis2503.tbc.movilapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class solicitarMovibusTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = solicitarMovibusTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection httpCon = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String userJsonStr = null;

            try {
                if (params.length == 0) {
                    return "Hubo un error al reservar";
                }
                String p = params[0];
                Log.e(LOG_TAG, "adsa: " + p);
                String[] temp = params[0].split(",");

                URL url = new URL("http://172.24.100.35:9000/usuario/" + userID + "/solicitarMovibus/");

                //Open the http connection with the default URL.
                httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(
                        httpCon.getOutputStream());
                JSONObject js = new JSONObject();

                js.put("fechaEjecucion", reqDate);
                js.put("latitudUsuario", originLatPos);
                js.put("longitudUsuario", originLonPos);
                js.put("latitudDestino", destLatPos);
                js.put("LongitudDestino", destLonPos);
                js.put("tiempoEstimado", predTime);
                out.write(js.toString());
                out.close();
                if (httpCon.getResponseCode() != 200) {
                    Log.e(LOG_TAG, "Error in HTTP request: " + httpCon.getResponseCode() + " //  " + httpCon.getResponseMessage());
                }
                BufferedReader buff = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                String output = buff.readLine();
                JSONObject j = new JSONObject(output);
                JSONObject conductor = j.getJSONObject("conductor");
                nomCon = conductor.getString("nombre");

                httpCon.disconnect();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (httpCon != null) {
                    httpCon.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final Exception e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                reservaExitosa();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
    }
}
