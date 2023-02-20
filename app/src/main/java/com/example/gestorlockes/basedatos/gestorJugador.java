package com.example.gestorlockes.basedatos;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.gestorlockes.clases.Jugador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
// Una vez se ha realizado una clase para crear y actualizar una base de datos, es
// necesario crear una clase que gestione sus operaciones.
public class gestorJugador extends dbHelperJugador {
    Context contexto;

    // Se crear el constructor.
    public gestorJugador(@Nullable Context context) {
        super(context);
        this.contexto = context;
    }

    // Método para insertar jugadores nuevos a la base.
    public long insertarJugador(String nickname, String password, int imagen) {

        long id = 0;

        try {
            dbHelperJugador auxDB = new dbHelperJugador(contexto);
            SQLiteDatabase refDB = auxDB.getReadableDatabase();

            ContentValues valores = new ContentValues();
            valores.put("nickname", nickname);
            valores.put("password", password);
            valores.put("imagen", imagen);

            id = refDB.insert(tableJugadores, null, valores);
        } catch (Exception ex) {
            ex.toString();
        }
        return id;
    }

    // Método que recoge todos los jugadores de la base de datos y devuelve una lista.
    public ArrayList<Jugador> recogerJugadores() {

        ArrayList<Jugador> listaJuga = new ArrayList<>();
        try {
            dbHelperJugador help = new dbHelperJugador(contexto);
            SQLiteDatabase db = help.getWritableDatabase();

            Jugador jug = null;
            Cursor cursorJug = null;

            cursorJug = db.rawQuery("SELECT * FROM " + tableJugadores, null);

            if (cursorJug.moveToFirst()) {

                do {

                    jug = new Jugador();
                    jug.setNickname(cursorJug.getString(0));
                    jug.setPassword(cursorJug.getString(1));
                    jug.setImagen(cursorJug.getInt(2));

                    listaJuga.add(jug);

                } while (cursorJug.moveToNext());

            }

            cursorJug.close();

        } catch (Exception ex) {
            ex.toString();
        }
        return listaJuga;
    }

    // Método que comprueba los usuarios que hay en la base de datos.
    public int comprobarUsuario(String nick, String pass) {
        dbHelperJugador help = new dbHelperJugador(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Jugador> listaJuga = new ArrayList<>();

        Jugador jug = null;
        Cursor cursorJug = null;

        cursorJug = db.rawQuery("SELECT * FROM " + tableJugadores, null);

        if (cursorJug.moveToFirst()) {

            do {

                jug = new Jugador();
                jug.setNickname(cursorJug.getString(0));
                jug.setPassword(cursorJug.getString(1));
                jug.setImagen(cursorJug.getInt(2));

                listaJuga.add(jug);

            } while (cursorJug.moveToNext());

        }

        cursorJug.close();

        boolean comprobacionNombre = false;
        boolean comprobacionPass = false;

        for (int i = 0; i < listaJuga.size(); i++) {
            if (listaJuga.get(i).getNickname().equals(nick) && listaJuga.get(i).getPassword().equals(pass)) {
                comprobacionNombre = true;
                comprobacionPass = true;
                break;
            }

            if (listaJuga.get(i).getNickname().equals(nick)) {
                comprobacionNombre = true;
            }
        }

        if (comprobacionNombre && comprobacionPass) {
            return 1;
        } else if (comprobacionNombre && !comprobacionPass) {
            return 0;
        } else {
            return -1;
        }
    }

    public int obtenerImagen(String nick) {
        dbHelperJugador help = new dbHelperJugador(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Jugador> listaJuga = new ArrayList<>();

        Jugador jug = null;
        Cursor cursorJug = null;

        cursorJug = db.rawQuery("SELECT * FROM " + tableJugadores, null);

        if (cursorJug.moveToFirst()) {

            do {

                jug = new Jugador();
                jug.setNickname(cursorJug.getString(0));
                jug.setPassword(cursorJug.getString(1));
                jug.setImagen(cursorJug.getInt(2));

                listaJuga.add(jug);

            } while (cursorJug.moveToNext());

        }

        cursorJug.close();

        int imagenUsuario = -1;
        for (int i = 0; i < listaJuga.size(); i++) {
            if (listaJuga.get(i).getNickname().equals(nick)) {
                imagenUsuario = listaJuga.get(i).getImagen();
                break;
            }
        }

        return imagenUsuario;
    }

    public String getPass(String nick){
        dbHelperJugador help = new dbHelperJugador(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Jugador> listaJuga = new ArrayList<>();

        Jugador jug = null;
        Cursor cursorJug = null;

        cursorJug = db.rawQuery("SELECT * FROM " + tableJugadores, null);

        if (cursorJug.moveToFirst()) {

            do {

                jug = new Jugador();
                jug.setNickname(cursorJug.getString(0));
                jug.setPassword(cursorJug.getString(1));
                jug.setImagen(cursorJug.getInt(2));

                listaJuga.add(jug);

            } while (cursorJug.moveToNext());

        }

        cursorJug.close();

        String password = "";
        for (int i = 0; i < listaJuga.size(); i++) {
            if (listaJuga.get(i).getNickname().equals(nick)) {
                password = listaJuga.get(i).getPassword();
                break;
            }
        }

        return password;
    }


}
