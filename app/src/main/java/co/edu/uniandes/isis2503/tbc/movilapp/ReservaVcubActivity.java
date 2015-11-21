package co.edu.uniandes.isis2503.tbc.movilapp;

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

import java.util.List;

public class ReservaVcubActivity extends AppCompatActivity {

    ListView estacionesLV;
    List<String> estacionesInfo;
    String selected;

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
            public void onClick(View view) {metodoTemporal();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    public void metodoTemporal(){

    }
}
