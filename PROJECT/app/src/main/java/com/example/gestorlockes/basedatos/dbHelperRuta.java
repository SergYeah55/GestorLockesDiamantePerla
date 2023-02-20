package com.example.gestorlockes.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// Clase encargada de elaborar la base de datos de las rutas.
public class dbHelperRuta extends SQLiteOpenHelper {

    // Variables necesarias para el control de la base de datos.
    public static int databaseVersion = 1;
    private static final String databaseName = "listaRutas.basedatos";
    public static final String tableRutas = "tabla_rutas";

    // Constructor de la propia herramienta para crear la base de datos.
    public dbHelperRuta(@Nullable Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    // Acciones que ocurren al momento de crear el ayudante, que no es
    // otra cosa que la creación de la tabla en SQLite.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ tableRutas + "("+
                "nombre TEXT,"+
                "numero INTEGER,"+
                "version INTEGER,"+
                "pokemones TEXT"+
                ")");
    }

    // Método para actualizar la tabla en caso necesario.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //databaseVersion = newVersion;
        sqLiteDatabase.execSQL("DROP TABLE " + tableRutas);
        onCreate(sqLiteDatabase);
    }
}