package co.edu.uniandes.isis2503.tbc.movilapp;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReservaVcubActivity extends AppCompatActivity{

    /**
     * Context Constants
     */
    public static final String USER_VCUBS = "user_vcubs";
    public static final String USER_STATION = "user_station";

    /**
     * Users data.
     */
    private String usersCC;
    private String email;

    /**
     * List view to render info.
     */
    ListView estacionesLV;
    /**
     * TBC available station info
     * String info, id,name
     */
    ArrayList<String> estacionesInfo;
    /**
     * Selected item from list.
     */
    String selected;

    /**
     * Variables to follow tasks tracks.
     */
    ProgressDialog progressBar;
    ReservaVcubTask reservaTask;
    ConseguirEstaciones estacionesTask;
    boolean finish;

    /**
     * Amount of vcubs rented by the actual user.
     */
    String usuarioVcubs;

    /**
     * Succesful message for a booking activity
     */
    String vcubBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent management.
        Intent intent = getIntent();
        usersCC = intent.getStringExtra(LoginActivity.USER_CC);
        email = intent.getStringExtra(LoginActivity.USER_EMAIl);
        estacionesInfo = new ArrayList<String>();

        estacionesInfo.add("1,Germania");
        estacionesInfo.add("2,Suba");

        vcubBook = "";
        setContentView(R.layout.activity_reserva_vcub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Execute a task to retrieve stations info
        finish = false;
        estacionesTask = new ConseguirEstaciones();
        estacionesTask.execute();
        /*progressBar = new ProgressDialog();
        progressBar.setCancelable(true);
        progressBar.setMessage("Getting info...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        */
        while(!finish){
        }
        progressBar.dismiss();
        //Array management for stations.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, estacionesInfo);
        estacionesLV = (ListView)findViewById(R.id.lista_estaciones);
        estacionesLV.setAdapter(adapter);
        estacionesLV.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setBackgroundColor(Color.BLUE);
                        selected = (String) parent.getItemAtPosition(position);
                    }
                }
        );

        //Add Actionlisteners to Buttons
        Button confirmarReserva= (Button) findViewById(R.id.confirmar_reserva);
        confirmarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==null){
                    AlertDialog alertDialog = new AlertDialog.Builder(ReservaVcubActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Debes seleccionar una estación");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    reservaTask = new ReservaVcubTask();
                    reservaTask.execute(selected);
                }

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * On successfull login show the next view.
     */
    public void mostrarReservas(String email, String cedulaC){
        Intent intent = new Intent(this, ReservasActivity.class);
        intent.putExtra(LoginActivity.USER_EMAIl, email);
        intent.putExtra(LoginActivity.USER_CC, cedulaC);
        startActivity(intent);
    }

    public void succesfull(){
    }


    public class ConseguirEstaciones extends AsyncTask<String[], Void, String> {

        private final String urlGetEstaciones ="http://172.24.100.49:9000/estacionvcub";
        private final String LOG_TAG = ConseguirEstaciones.class.getSimpleName();

        @Override
        protected String doInBackground(String[]... params) {

            HttpURLConnection conn = null;
            BufferedReader buff = null;
            try {
                URL url = new URL(urlGetEstaciones);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    Log.e(LOG_TAG, "Error in HTTP request: "+conn.getResponseCode() +" //  " +conn.getResponseMessage());
                }
                buff = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output = buff.readLine();
                String at1[] = output.split(",");
                Long idEst = Long.valueOf(0).longValue();
                String nombEst = "";
                String estacion = "";
                for (String string : at1) {
                    String at2[] = string.split(":");
                    if ("[{\"id\"".equals(at2[0]))
                        idEst = Long.valueOf(at2[0]).longValue();
                    if ("\"nombre\"".equals(at2[0]))
                        nombEst = at2[0];
                    if (idEst != 0 && !nombEst.equals("")) {
                        estacion = idEst + "," + nombEst;
                        estacionesInfo.add(estacion);
                    }
                }
            } catch (Exception e1) {
                Log.e(LOG_TAG, "Error closing stream", e1);
            } finally {
                finish = true;
                if (conn != null) {
                    conn.disconnect();
                }
                if(buff != null){
                    try {
                        buff.close();
                    }catch(Exception e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

    public class ReservaVcubTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = ReservaVcubTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection httpCon = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String userJsonStr = null;

            try {
                if(params.length==0){
                    return "Hubo un error al reservar";
                }
                String p = params[0];
                Log.e(LOG_TAG, "adsa: " + p);
                String[] temp = params[0].split(",");

                URL url = new URL("http://172.24.100.35:9000/estacionvcub/"+temp[0]+ "/usuario/"+usersCC);

                //Open the http connection with the default URL.
                httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("PUT");
                OutputStreamWriter out = new OutputStreamWriter(
                        httpCon.getOutputStream());
                out.write("{}");
                out.close();
                if(httpCon.getResponseCode()!=200){
                    Log.e(LOG_TAG, "Error in HTTP request: "+httpCon.getResponseCode() +" //  " +httpCon.getResponseMessage());
                }
                vcubBook += selected.split(",")[1] + ":";
                BufferedReader buff = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                String output;
                System.out.println("Output from server .... \n");
                while((output = buff.readLine())!=null){
                    String[] at1 = output.split(",");
                    for (String string : at1) {
                        String[] at2=string.split(":");
                        if(at2[0].equals("\"vcubsEnUso\""))
                            vcubBook += at2[1];
                    }
                    Log.e(LOG_TAG, vcubBook);
                }
                httpCon.disconnect();
                AlertDialog alertDialog = new AlertDialog.Builder(ReservaVcubActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Debes seleccionar una estación");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
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
                mostrarReservas(email, usersCC);
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                usuarioVcubs=result;
                succesfull();
                // New data is back from the server.  Hooray!
            }
        }
    }
}
