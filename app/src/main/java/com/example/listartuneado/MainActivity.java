package com.example.listartuneado;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etNombreRol, etDescripcionRol;
    MaterialButton btnGuardarRol, btnEditarRol, btnEliminarRol;
    RecyclerView recyclerViewRoles;
    TextView tvListaVacia;

    DBSalar db;
    ArrayList<Rol> listaRoles;
    RolAdapter adapter;

    Rol rolSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inicializarVistas();
        inicializarBaseDatos();
        configurarRecyclerView();
        configurarBotones();
        cargarRoles();
    }

    private void inicializarVistas() {
        etNombreRol = findViewById(R.id.etNombreRol);
        etDescripcionRol = findViewById(R.id.etDescripcionRol);
        btnGuardarRol = findViewById(R.id.btnGuardarRol);
        btnEditarRol = findViewById(R.id.btnEditarRol);
        btnEliminarRol = findViewById(R.id.btnEliminarRol);
        recyclerViewRoles = findViewById(R.id.recyclerViewRoles);
        tvListaVacia = findViewById(R.id.tvListaVacia);
    }

    private void inicializarBaseDatos() {
        db = new DBSalar(this);
    }

    private void configurarRecyclerView() {
        listaRoles = new ArrayList<>();
        adapter = new RolAdapter(listaRoles);
        
        recyclerViewRoles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRoles.setAdapter(adapter);
        
        adapter.setOnItemClickListener(rol -> {
            rolSeleccionado = rol;
            etNombreRol.setText(rol.getNombre());
            etDescripcionRol.setText(rol.getDescripcion());
            
            btnEditarRol.setEnabled(true);
            btnEliminarRol.setEnabled(true);
            
            // Scroll suave al formulario
            recyclerViewRoles.smoothScrollToPosition(0);
        });
    }

    private void configurarBotones() {
        btnEditarRol.setEnabled(false);
        btnEliminarRol.setEnabled(false);
        
        btnGuardarRol.setOnClickListener(v -> guardarRol());
        btnEditarRol.setOnClickListener(v -> editarRol());
        btnEliminarRol.setOnClickListener(v -> confirmarEliminacion());
    }

    void guardarRol() {
        String nombre = etNombreRol.getText().toString().trim();
        String descripcion = etDescripcionRol.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreRol.setError("El nombre del rol es obligatorio");
            etNombreRol.requestFocus();
            return;
        }

        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);

        boolean exito = db.insertarRol(rol);

        if (exito) {
            Toast.makeText(this, "✓ Rol guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarRoles();
        } else {
            Toast.makeText(this, "✗ Error: El nombre del rol ya existe", Toast.LENGTH_SHORT).show();
            etNombreRol.setError("Este nombre ya está en uso");
            etNombreRol.requestFocus();
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
            etNombreRol.setError("El nombre no puede estar vacío");
            etNombreRol.requestFocus();
            return;
        }

        // Verificar si el nombre cambió y si ya existe
        if (!nuevoNombre.equals(rolSeleccionado.getNombre())) {
            // Aquí podrías agregar validación adicional si es necesario
        }

        rolSeleccionado.setNombre(nuevoNombre);
        rolSeleccionado.setDescripcion(nuevaDescripcion);

        boolean actualizado = db.actualizarRol(rolSeleccionado);

        if (actualizado) {
            Toast.makeText(this, "✓ Rol actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarRoles();
        } else {
            Toast.makeText(this, "✗ Error al actualizar el rol", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmarEliminacion() {
        if (rolSeleccionado == null) {
            Toast.makeText(this, "Selecciona un rol de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar el rol \"" + rolSeleccionado.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarRol())
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void eliminarRol() {
        if (rolSeleccionado == null) {
            return;
        }

        boolean eliminado = db.eliminarRol(rolSeleccionado.getId());

        if (eliminado) {
            Toast.makeText(this, "✓ Rol eliminado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarRoles();
        } else {
            Toast.makeText(this, "✗ Error al eliminar el rol", Toast.LENGTH_SHORT).show();
        }
    }

    void cargarRoles() {
        listaRoles = db.obtenerRoles();
        adapter.actualizarLista(listaRoles);
        
        // Mostrar mensaje si la lista está vacía
        if (listaRoles.isEmpty()) {
            tvListaVacia.setVisibility(View.VISIBLE);
            recyclerViewRoles.setVisibility(View.GONE);
        } else {
            tvListaVacia.setVisibility(View.GONE);
            recyclerViewRoles.setVisibility(View.VISIBLE);
        }
        
        rolSeleccionado = null;
        btnEditarRol.setEnabled(false);
        btnEliminarRol.setEnabled(false);
    }

    void limpiarCampos() {
        etNombreRol.setText("");
        etDescripcionRol.setText("");
        etNombreRol.clearFocus();
        etDescripcionRol.clearFocus();
        etNombreRol.setError(null);
        etDescripcionRol.setError(null);

        rolSeleccionado = null;
        btnEditarRol.setEnabled(false);
        btnEliminarRol.setEnabled(false);
    }
}
