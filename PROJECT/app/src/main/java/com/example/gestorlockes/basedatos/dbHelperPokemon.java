package com.example.gestorlockes.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// Clase encargada de crear la base de datos de los pokemones del juego.
public class dbHelperPokemon extends SQLiteOpenHelper {

    // Se indican los elementos básicos para una base de datos.
    public static int databaseVersion = 1;
    private static final String databaseName = "listaPokemones.basedatos";
    public static final String tablePokemones = "tabla_pokemones";

    // Constructor de la clase para elaborar la bd.
    public dbHelperPokemon(@Nullable Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    // Creación de la tabla de SQLite de los pokemones.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ tablePokemones + "("+
                "nombre TEXT,"+
                "numero INTEGER,"+
                "version INTEGER,"+
                "sexo TEXT,"+
                "tipo1 TEXT,"+
                "tipo2 TEXT,"+
                "forma INTEGER"+
                ")");
    }

    // Método para poder actualizar la base de datos, en caso de necesitarse.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //databaseVersion = newVersion;
        sqLiteDatabase.execSQL("DROP TABLE " + tablePokemones);
        onCreate(sqLiteDatabase);
    }
}
