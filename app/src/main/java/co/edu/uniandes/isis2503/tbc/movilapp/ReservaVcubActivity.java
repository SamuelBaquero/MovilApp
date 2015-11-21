package co.edu.uniandes.isis2503.tbc.movilapp;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ReservaVcubActivity extends AppCompatActivity{

    /**
     * List view to render info.
     */
    ListView estacionesLV;
    /**
     * TBC available station info
     * String info, id,name
     */
    List<String> estacionesInfo;
    /**
     * Selected item from list.
     */
    String selected;

    /**
     * Variable to follow the task track.
     */
    ReservaVcubTask reservaTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_vcub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, estacionesInfo);
        estacionesLV = (ListView)findViewById(R.id.lista_estaciones);
        estacionesLV.setAdapter(adapter);
        estacionesLV.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected = (String) parent.getItemAtPosition(position);
                    }
                }
        );
        //Add Actionlisteners to Buttons
        Button confirmarReserva= (Button) findViewById(R.id.confirmar_reserva);
        confirmarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservaTask = new ReservaVcubTask();
                String[] temp = selected.split(",");
                String[] temp2 = {temp[0], temp[1]};
                reservaTask.execute(temp2);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class ReservaVcubTask extends AsyncTask<String[], Void, String> {

        private final String LOG_TAG = ReservaVcubTask.class.getSimpleName();

        @Override
        protected String doInBackground(String[]... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String userJsonStr = null;

            try {

                URL url = new URL("192.168.0.5:9000/"+params[0]+"/usuario/"+params[1]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                userJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + userJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final Exception e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(userJsonStr);
                jsonObject.getInt("vcubsEnUso");
                return "Tienes "+jsonObject.getInt("vcubsEnUso")+" vcubs en uso.";
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
    }
}
