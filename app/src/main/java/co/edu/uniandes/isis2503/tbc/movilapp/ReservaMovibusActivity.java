package co.edu.uniandes.isis2503.tbc.movilapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ReservaMovibusActivity extends AppCompatActivity {

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
