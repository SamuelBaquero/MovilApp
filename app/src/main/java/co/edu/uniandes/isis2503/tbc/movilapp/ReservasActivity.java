package co.edu.uniandes.isis2503.tbc.movilapp;

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
    //private String userCC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent management
        Intent intent = getIntent();
        email = intent.getStringExtra(LoginActivity.USER_EMAIl);
        //userCC = intent.getStringExtra(LoginActivity.USER_CC);

        //Add Actionlisteners to Buttons
        Button reservarVcubButton= (Button) findViewById(R.id.reservar_vcub);
        reservarVcubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservarVcub();
            }
        });

        Button reservarMovibusButton= (Button) findViewById(R.id.reservar_movibus);
        reservarMovibusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservarMovibus();
            }
        });
    }

    protected void reservarVcub(){
        //Crea el intent
        Intent intent = new Intent(this, ReservaVcubActivity.class);
        intent.putExtra(LoginActivity.USER_EMAIl, email);
        //intent.putExtra(LoginActivity.USER_CC, userCC);
        startActivity(intent);
    }

    protected void reservarMovibus(){
        //Crea el intent
        Intent intent = new Intent(this, ReservaMovibusActivity.class);
        intent.putExtra(LoginActivity.USER_EMAIl, email);
        startActivity(intent);
    }
}
