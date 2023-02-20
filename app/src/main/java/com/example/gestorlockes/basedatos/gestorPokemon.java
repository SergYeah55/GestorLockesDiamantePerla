package com.example.gestorlockes.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.gestorlockes.clases.Pokemon;

import java.util.ArrayList;

// Una vez se ha realizado una clase para crear y actualizar una base de datos, es
// necesario crear una clase que gestione sus operaciones.
public class gestorPokemon extends dbHelperPokemon {
    Context contexto;

    // Se crear el constructor.
    public gestorPokemon(@Nullable Context context) {
        super(context);
        this.contexto = context;
    }

    // Método para insertar pokemones nuevos a la base.
    public long insertarPokemon (String nombre, int numero, int version, String sexo, String tipo1, String tipo2, int forma){

        long id = 0;

        try{
            dbHelperPokemon auxDB = new dbHelperPokemon(contexto);
            SQLiteDatabase refDB = auxDB.getReadableDatabase();

            ContentValues valores = new ContentValues();
            valores.put("nombre", nombre);
            valores.put("numero", numero);
            valores.put("version", version);
            valores.put("sexo", sexo);
            valores.put("tipo1", tipo1);
            valores.put("tipo2", tipo2);
            valores.put("forma", forma);

            id = refDB.insert(tablePokemones, null, valores);
        }catch (Exception ex){
            ex.toString();
        }
        return id;
    }

    // Método que recoge todos los pokemones de la base de datos y devuelve una lista.
    public ArrayList<Pokemon> recogerPokemones(){
        dbHelperPokemon help = new dbHelperPokemon(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Pokemon> listaPokes = new ArrayList<>();

        Pokemon poke = null;
        Cursor cursorPoke = null;

        cursorPoke = db.rawQuery("SELECT * FROM " + tablePokemones, null);

        if (cursorPoke.moveToFirst()){

            do{

                poke = new Pokemon();
                poke.setNombre(cursorPoke.getString(0));
                poke.setNumero(cursorPoke.getInt(1));
                poke.setVersion(cursorPoke.getInt(2));
                poke.setSexo(cursorPoke.getString(3));
                poke.setTipo1(cursorPoke.getString(4));
                poke.setTipo2(cursorPoke.getString(5));
                poke.setForma(cursorPoke.getInt(6));

                listaPokes.add(poke);

            } while (cursorPoke.moveToNext());

        }

        cursorPoke.close();

        return listaPokes;
    }

    // Método que recoge todos los pokemones en función de la version del juego y devuelve una lista.
    public ArrayList<Pokemon> recogerPokemonVersion(int version){
        dbHelperPokemon help = new dbHelperPokemon(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Pokemon> listaPokes = new ArrayList<>();

        Pokemon poke = null;
        Cursor cursorPoke = null;

        cursorPoke = db.rawQuery("SELECT * FROM " + tablePokemones + " WHERE version = '" + version + "' OR version = '" + 2 + "'", null);


        if (cursorPoke.moveToFirst()){

            do{

                poke = new Pokemon();
                poke.setNombre(cursorPoke.getString(0));
                poke.setNumero(cursorPoke.getInt(1));
                poke.setVersion(cursorPoke.getInt(2));
                poke.setSexo(cursorPoke.getString(3));
                poke.setTipo1(cursorPoke.getString(4));
                poke.setTipo2(cursorPoke.getString(5));
                poke.setForma(cursorPoke.getInt(6));

                listaPokes.add(poke);

            } while (cursorPoke.moveToNext());

        }

        cursorPoke.close();

        return listaPokes;
    }

    // Método que devuelve un pokemon en concreto
    public Pokemon recogerUnPokemon(String nombre){
        Pokemon pokemonADevolver = new Pokemon();

        ArrayList<Pokemon> listaTotal = recogerPokemones();

        for (int i = 0; i<listaTotal.size();i++){
            if (listaTotal.get(i).getNombre().equals(nombre)){
                pokemonADevolver = listaTotal.get(i);
                break;
            }
        }

        return pokemonADevolver;
    }

}
