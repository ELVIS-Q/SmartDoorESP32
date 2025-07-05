package com.example.esp32fb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etUser, etPassword;
    Button btnLogin, btnRegister;
    DatabaseReference refUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        refUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        btnRegister.setOnClickListener(v -> {
                    Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(i);
                });

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sanitizamos el email para que sea válido como path en Firebase
            String userSanitized = sanitizeEmail(user);

            refUsuarios.child(userSanitized).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String passwordCorrecta = snapshot.child("Password").getValue(String.class);
                        if (pass.equals(passwordCorrecta)) {
                            Intent i = new Intent(MainActivity.this, ControlActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Función para reemplazar caracteres no permitidos en la ruta de Firebase
    private String sanitizeEmail(String email) {
        return email.replace(".", ",")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_");
    }
}
