package com.example.gestorlockes.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.gestorlockes.clases.Pokemon;
import com.example.gestorlockes.clases.Ruta;

import java.util.ArrayList;

public class gestorRuta extends dbHelperRuta {
    Context contexto;

    // Se crea el constructor.
    public gestorRuta(@Nullable Context context) {
        super(context);
        this.contexto = context;
    }

    // Método para insertar rutas nuevas a la base.
    public long insertarRuta (String nombre, int numero, int version, String pokemones){

        long id = 0;

        try{
            dbHelperRuta auxDB = new dbHelperRuta(contexto);
            SQLiteDatabase refDB = auxDB.getReadableDatabase();

            ContentValues valores = new ContentValues();
            valores.put("nombre", nombre);
            valores.put("numero", numero);
            valores.put("version", version);
            valores.put("pokemones", pokemones);

            id = refDB.insert(tableRutas, null, valores);
        }catch (Exception ex){
            ex.toString();
        }
        return id;
    }

    // Método que recoge todas las rutas de la base de datos y devuelve una lista.
    public ArrayList<Ruta> recogerRutas(){
        dbHelperRuta help = new dbHelperRuta(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Ruta> listaRutas = new ArrayList<>();

        Ruta ruta = null;
        Cursor cursorRuta = null;

        cursorRuta = db.rawQuery("SELECT * FROM " + tableRutas, null);

        if (cursorRuta.moveToFirst()){

            do{

                ruta = new Ruta();
                ruta.setNombre(cursorRuta.getString(0));
                ruta.setNumero(cursorRuta.getInt(1));
                ruta.setVersion(cursorRuta.getInt(2));
                String pokemonesAux = cursorRuta.getString(3);
                String[] pokemones = pokemonesAux.split(",");

                ArrayList<Pokemon> pokemonesPorRuta = new ArrayList<>();
                gestorPokemon aniadirPokemon = new gestorPokemon(contexto);

                for (int i = 0; i < pokemonesPorRuta.size();i++){
                    pokemonesPorRuta.add(aniadirPokemon.recogerUnPokemon(pokemones[i]));
                }
                ruta.setListaPokemones(pokemonesPorRuta);

                listaRutas.add(ruta);

            } while (cursorRuta.moveToNext());

        }

        cursorRuta.close();

        return listaRutas;
    }

    // Método que recoge todas las rutas de la base de datos y devuelve una lista.
    public ArrayList<Ruta> recogerRutasVersion(int version){
        dbHelperRuta help = new dbHelperRuta(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Ruta> listaRutas = new ArrayList<>();

        Ruta ruta = null;
        Cursor cursorRuta = null;

        cursorRuta = db.rawQuery("SELECT * FROM " + tableRutas+" WHERE version = '" + version + "' OR version = '" + 0 + "'", null);

        if (cursorRuta.moveToFirst()){

            do{

                ruta = new Ruta();
                ruta.setNombre(cursorRuta.getString(0));
                ruta.setNumero(cursorRuta.getInt(1));
                ruta.setVersion(cursorRuta.getInt(2));
                String pokemonesAux = cursorRuta.getString(3);
                String[] pokemones = pokemonesAux.split(",");

                ArrayList<Pokemon> pokemonesPorRuta = new ArrayList<>();
                gestorPokemon aniadirPokemon = new gestorPokemon(contexto);

                for (int i = 0; i < pokemonesPorRuta.size();i++){
                    pokemonesPorRuta.add(aniadirPokemon.recogerUnPokemon(pokemones[i]));
                }
                ruta.setListaPokemones(pokemonesPorRuta);

                listaRutas.add(ruta);

            } while (cursorRuta.moveToNext());

        }

        cursorRuta.close();

        return listaRutas;
    }

    public ArrayList<Pokemon> recogerPokemonPorRuta(String nombreRuta, int version){
        dbHelperRuta help = new dbHelperRuta(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Ruta> listaRutas = new ArrayList<>();
        ArrayList<Pokemon> listaPokes = new ArrayList<>();

        Ruta ruta = null;
        Cursor cursorRuta = null;

        cursorRuta = db.rawQuery("SELECT * FROM " + tableRutas+" WHERE version = '" + version + "' OR version = '" + 0 + "'", null);

        if (cursorRuta.moveToFirst()){

            do{

                ruta = new Ruta();
                ruta.setNombre(cursorRuta.getString(0));
                ruta.setNumero(cursorRuta.getInt(1));
                ruta.setVersion(cursorRuta.getInt(2));
                String pokemonesAux = cursorRuta.getString(3);
                String[] pokemones = pokemonesAux.split(",");

                ArrayList<Pokemon> pokemonesPorRuta = new ArrayList<>();
                gestorPokemon aniadirPokemon = new gestorPokemon(contexto);

                for (int i = 0; i < pokemones.length;i++){
                    pokemonesPorRuta.add(aniadirPokemon.recogerUnPokemon(pokemones[i]));
                }
                ruta.setListaPokemones(pokemonesPorRuta);

                listaRutas.add(ruta);

            } while (cursorRuta.moveToNext());

        }

        cursorRuta.close();

        for (int j = 0; j < listaRutas.size();j++){
            if(listaRutas.get(j).getNombre().equals(nombreRuta)){
                for(int h = 0; h<listaRutas.get(j).getListaPokemones().size();h++){
                    listaPokes.add(listaRutas.get(j).getListaPokemones().get(h));
                }
                break;
            }
        }

        return listaPokes;
    }
}
