package com.example.gestorlockes.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbHelperPartida extends SQLiteOpenHelper {

    // Se indican los elementos básicos para una base de datos.
    public static int databaseVersion = 1;
    private static final String databaseName = "listaPartidas.basedatos";
    public static final String tablePartidas = "tabla_partidas";

    // Constructor de la clase para elaborar la bd.
    public dbHelperPartida(@Nullable Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    // Creación de la tabla de SQLite de los pokemones.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ tablePartidas + "("+
                "nickname TEXT,"+
                "nombrePartida TEXT,"+
                "version INTEGER,"+
                "rutasJuego TEXT,"+
                "pokemones TEXT,"+
                "estadoRuta TEXT"+
                ")");
    }

    // Método para poder actualizar la base de datos, en caso de necesitarse.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //databaseVersion = newVersion;
        sqLiteDatabase.execSQL("DROP TABLE " + tablePartidas);
        onCreate(sqLiteDatabase);
    }
}
