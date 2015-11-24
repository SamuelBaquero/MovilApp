package co.edu.uniandes.isis2503.tbc.movilapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReservasActivity extends AppCompatActivity {

    /**
     * Atributos del usuario.
     */
    private String email;
    private String userCC;
    private Long userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);
        //Intent management
        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.USER_EMAIl);
        userCC = intent.getStringExtra(LoginActivity.USER_CC);
        userID = intent.getLongExtra(LoginActivity.USER_ID, 0);
    }

    public void reservarVcub(View view){
        //Crea el intent
        Intent intent = new Intent(this, ReservaVcubActivity.class);
        intent.putExtra(LoginActivity.USER_EMAIl, email);
        intent.putExtra(LoginActivity.USER_CC, userCC);
        intent.putExtra(LoginActivity.USER_ID, userID);
        startActivity(intent);
    }

    public void reservarMovibus(View view){
        //Crea el intent
        Intent intent = new Intent(this, ReservaMovibusActivity.class);
        intent.putExtra(LoginActivity.USER_EMAIl, email);
        intent.putExtra(LoginActivity.USER_CC, userCC);
        intent.putExtra(LoginActivity.USER_ID, userID);
        startActivity(intent);
    }
}
