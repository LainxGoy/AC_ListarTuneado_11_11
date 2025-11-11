package com.example.listartuneado;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBSalar extends SQLiteOpenHelper {

    private static final String DB_NAME = "Salar.db";
    private static final int DB_VERSION = 1;
    public DBSalar(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE roles(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE NOT NULL," +
                "descripcion TEXT)");

        db.execSQL("CREATE TABLE usuarios(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "apellido TEXT," +
                "usuario TEXT UNIQUE NOT NULL," +
                "password TEXT," +
                "correo TEXT UNIQUE NOT NULL," +
                "telefono TEXT," +
                "fecha_nacimiento TEXT," +
                "verificado INTEGER DEFAULT 0," +
                "id_rol INTEGER," +
                "FOREIGN KEY(id_rol) REFERENCES roles(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS roles");
        onCreate(db);
    }
    public boolean insertarRol(String nombre, String descripcion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);

        long resultado = db.insert("roles", null, values);
        db.close();
        return resultado != -1;
    }
    public boolean insertarRol(Rol rol) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", rol.getNombre());
        values.put("descripcion", rol.getDescripcion());

        long resultado = db.insert("roles", null, values);
        db.close();

        return resultado != -1;
    }
    public ArrayList<Rol> obtenerRoles() {
        ArrayList<Rol> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT id, nombre, descripcion FROM roles ORDER BY id DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    Rol rol = new Rol();
                    rol.setId(cursor.getInt(0));
                    rol.setNombre(cursor.getString(1));
                    rol.setDescripcion(cursor.getString(2));
                    lista.add(rol);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }
    public boolean actualizarRol(Rol rol) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", rol.getNombre());
        values.put("descripcion", rol.getDescripcion());

        int filas = db.update("roles", values, "id = ?", new String[]{String.valueOf(rol.getId())});
        db.close();

        return filas > 0;
    }
    public boolean eliminarRol(int idRol) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("roles", "id = ?", new String[]{String.valueOf(idRol)});
        db.close();
        return filas > 0;
    }
}
