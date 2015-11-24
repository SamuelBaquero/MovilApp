package co.edu.uniandes.isis2503.tbc.movilapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;

import java.util.Date;

public class ReservaMovibusActivity extends AppCompatActivity implements LocationListener{

    /*Para pedir un movibus se envía el id del usuario por parámetro y los datos del pedido como JSON de la siguiente manera
        fecha de ejecucion -fechaEjecucion
        posicion usuario  -latitudUsuario, longitudUsuario
        posicion destino -latitudDestino, longitudDestino
        tiempo estimado como valor predeterminado - tiempoEstimado*/

    /**
     * Users data.
     */
    private String usersCC;
    private String email;
    private Long userID;

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
                sendReq=true;
                requestMovibus();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void requestMovibus() {

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
}
