package com.example.esp32fb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ControlActivity extends AppCompatActivity {

    TextView lblEstado, lblFecha;
    ImageView imgPuerta;
    Button btnOpen, btnClose;
    DatabaseReference refEstado, refHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        lblEstado = findViewById(R.id.lblEstado);
        lblFecha = findViewById(R.id.lblFecha);
        imgPuerta = findViewById(R.id.imgPuerta);
        btnOpen = findViewById(R.id.btnOpen);
        btnClose = findViewById(R.id.btnClose);

        refEstado = FirebaseDatabase.getInstance().getReference("EstadoPuerta");
        refHistorial = FirebaseDatabase.getInstance().getReference("Historial");

        btnOpen.setOnClickListener(v -> cambiarEstado("abierta"));
        btnClose.setOnClickListener(v -> cambiarEstado("cerrada"));

        refEstado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String estado = snapshot.child("estado").getValue(String.class);
                String fecha = snapshot.child("fecha").getValue(String.class);

                lblEstado.setText("Estado: Puerta " + estado);
                lblFecha.setText("Fecha: " + fecha);

                if ("abierta".equals(estado)) {
                    imgPuerta.setImageResource(R.drawable.puerta_abierta);
                } else {
                    imgPuerta.setImageResource(R.drawable.puerta_cerrada);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ControlActivity.this, "Error al leer estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarEstado(String nuevoEstado) {
        String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> datos = new HashMap<>();
        datos.put("estado", nuevoEstado);
        datos.put("fecha", fechaHora);

        refEstado.setValue(datos);
        refHistorial.push().setValue(datos);
    }
}
