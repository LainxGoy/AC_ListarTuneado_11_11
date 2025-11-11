package com.example.listartuneado;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etNombreRol, etDescripcionRol;
    Button btnGuardarRol, btnEditarRol, btnEliminarRol;
    ListView listViewRoles;

    DBSalar db;
    ArrayList<Rol> listaRoles;
    ArrayAdapter<Rol> adapter;

    Rol rolSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNombreRol = findViewById(R.id.etNombreRol);
        etDescripcionRol = findViewById(R.id.etDescripcionRol);
        btnGuardarRol = findViewById(R.id.btnGuardarRol);
        btnEditarRol = findViewById(R.id.btnEditarRol);
        btnEliminarRol = findViewById(R.id.btnEliminarRol);
        listViewRoles = findViewById(R.id.listViewRoles);
        db = new DBSalar(this);
        cargarRoles();
        btnEditarRol.setEnabled(false);
        btnEliminarRol.setEnabled(false);
        btnGuardarRol.setOnClickListener(v -> guardarRol());
        btnEditarRol.setOnClickListener(v -> editarRol());
        btnEliminarRol.setOnClickListener(v -> eliminarRol());
        listViewRoles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rolSeleccionado = listaRoles.get(position);
                etNombreRol.setText(rolSeleccionado.getNombre());
                etDescripcionRol.setText(rolSeleccionado.getDescripcion());

                btnEditarRol.setEnabled(true);
                btnEliminarRol.setEnabled(true);
            }
        });
    }

    void guardarRol() {
        String nombre = etNombreRol.getText().toString().trim();
        String descripcion = etDescripcionRol.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre del rol es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);

        boolean exito = db.insertarRol(rol);

        if (exito) {
            Toast.makeText(this, "Rol guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarRoles();
        } else {
            Toast.makeText(this, "Error al guardar el rol (¿nombre duplicado?)", Toast.LENGTH_SHORT).show();
        }
    }

    void editarRol() {
        if (rolSeleccionado == null) {
            Toast.makeText(this, "Selecciona un rol de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoNombre = etNombreRol.getText().toString().trim();
        String nuevaDescripcion = etDescripcionRol.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        rolSeleccionado.setNombre(nuevoNombre);
        rolSeleccionado.setDescripcion(nuevaDescripcion);

        boolean actualizado = db.actualizarRol(rolSeleccionado);

        if (actualizado) {
            Toast.makeText(this, "Rol actualizado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarRoles();
        } else {
            Toast.makeText(this, "Error al actualizar rol", Toast.LENGTH_SHORT).show();
        }
    }

    void eliminarRol() {
        if (rolSeleccionado == null) {
            Toast.makeText(this, "Selecciona un rol de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = db.eliminarRol(rolSeleccionado.getId());

        if (eliminado) {
            Toast.makeText(this, "Rol eliminado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarRoles();
        } else {
            Toast.makeText(this, "Error al eliminar rol", Toast.LENGTH_SHORT).show();
        }
    }

    void cargarRoles() {
        listaRoles = db.obtenerRoles();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaRoles);
        listViewRoles.setAdapter(adapter);
        rolSeleccionado = null;
        btnEditarRol.setEnabled(false);
        btnEliminarRol.setEnabled(false);
    }

    void limpiarCampos() {
        etNombreRol.setText("");
        etDescripcionRol.setText("");
        etNombreRol.requestFocus();

        rolSeleccionado = null;
        btnEditarRol.setEnabled(false);
        btnEliminarRol.setEnabled(false);
    }
}
