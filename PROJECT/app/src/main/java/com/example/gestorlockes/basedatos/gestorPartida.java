package com.example.gestorlockes.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.gestorlockes.clases.Jugador;
import com.example.gestorlockes.clases.Partida;
import com.example.gestorlockes.clases.Pokemon;
import com.example.gestorlockes.clases.Ruta;

import java.util.ArrayList;

public class gestorPartida extends dbHelperPartida {
    Context contexto;

    // Se crear el constructor.
    public gestorPartida(@Nullable Context context) {
        super(context);
        this.contexto = context;
    }

    // Método para insertar jugadores nuevos a la base.
    public long insertarPartida(String nickname,String nombrePartida, int version, String rutasJuego, String pokemones,String estadoRuta) {

        long id = 0;
        Log.i("NOTENGOIDEA",pokemones);
        try {
            dbHelperPartida auxDB = new dbHelperPartida(contexto);
            SQLiteDatabase refDB = auxDB.getReadableDatabase();

            ContentValues valores = new ContentValues();
            valores.put("nickname", nickname);
            valores.put("version", version);
            valores.put("nombrePartida",nombrePartida);
            valores.put("rutasJuego",rutasJuego);
            valores.put("pokemones",pokemones);
            valores.put("estadoRuta", estadoRuta);

            id = refDB.insert(tablePartidas, null, valores);
        } catch (Exception ex) {
            ex.toString();
        }
        return id;
    }

    public ArrayList<Partida> recogerPartidas() {

        ArrayList<Partida> listaPartidas = new ArrayList<>();
        try {
            dbHelperPartida help = new dbHelperPartida(contexto);
            SQLiteDatabase db = help.getWritableDatabase();

            Partida part = null;
            Cursor cursorPart = null;

            cursorPart = db.rawQuery("SELECT * FROM " + tablePartidas, null);

            gestorRuta gestorAuxRuta = new gestorRuta(contexto);
            gestorPokemon gestorAuxPoke = new gestorPokemon(contexto);

            if (cursorPart.moveToFirst()) {

                do {

                    part = new Partida();
                    part.setNickname(cursorPart.getString(0));
                    part.setNombrePartida(cursorPart.getString(1));
                    part.setVersion(cursorPart.getInt(2));

                    ArrayList<Ruta> rutasVersion = gestorAuxRuta.recogerRutasVersion(part.getVersion());
                    part.setRutasJuego(rutasVersion);

                    String[] rutasCapturadas = (cursorPart.getString(5).split(","));
                    ArrayList<Integer> estadoRutas = new ArrayList<Integer>();
                    for (int i = 0; i< rutasCapturadas.length;i++){
                        if(rutasCapturadas[i].equals("0")){
                            estadoRutas.add(0);
                        } else if (rutasCapturadas[i].equals("1")){
                            estadoRutas.add(1);
                        }else{
                            estadoRutas.add(-1);
                        }
                    }
                    part.setEstadoRuta(estadoRutas);

                    String[] pokemonesVistos = (cursorPart.getString(4).split(","));
                    ArrayList<Pokemon> pokemonesAvistados = new ArrayList<Pokemon>();
                    for (int i = 0; i< pokemonesVistos.length;i++){
                        if(!pokemonesVistos[i].equals("NULL")){
                            Pokemon pokemonNuevo = gestorAuxPoke.recogerUnPokemon(pokemonesVistos[i]);
                            pokemonesAvistados.add(pokemonNuevo);
                        } else {
                            Pokemon pokemonNuevo = new Pokemon();
                            pokemonesAvistados.add(pokemonNuevo);
                        }
                    }
                    part.setPokemonesJuego(pokemonesAvistados);

                    listaPartidas.add(part);

                } while (cursorPart.moveToNext());

            }

            cursorPart.close();

        } catch (Exception ex) {
            ex.toString();
        }
        return listaPartidas;
    }
    public ArrayList<Partida> recogerPartidas(String nick) {

        ArrayList<Partida> listaPartidas = new ArrayList<>();
        try {
            dbHelperPartida help = new dbHelperPartida(contexto);
            SQLiteDatabase db = help.getWritableDatabase();

            Partida part = null;
            Cursor cursorPart = null;

            cursorPart = db.rawQuery("SELECT * FROM " + tablePartidas+" WHERE nickname = '" + nick + "'", null);

            gestorRuta gestorAuxRuta = new gestorRuta(contexto);
            gestorPokemon gestorAuxPoke = new gestorPokemon(contexto);

            if (cursorPart.moveToFirst()) {

                do {

                    part = new Partida();
                    part.setNickname(cursorPart.getString(0));
                    part.setNombrePartida(cursorPart.getString(1));
                    part.setVersion(cursorPart.getInt(2));

                    ArrayList<Ruta> rutasVersion = gestorAuxRuta.recogerRutasVersion(part.getVersion());
                    part.setRutasJuego(rutasVersion);

                    String[] rutasCapturadas = (cursorPart.getString(5).split(","));
                    ArrayList<Integer> estadoRutas = new ArrayList<Integer>();
                    for (int i = 0; i< rutasCapturadas.length;i++){
                        if(rutasCapturadas[i].equals("0")){
                            estadoRutas.add(0);
                        } else if (rutasCapturadas[i].equals("1")){
                            estadoRutas.add(1);
                        }else{
                            estadoRutas.add(-1);
                        }
                    }
                    part.setEstadoRuta(estadoRutas);

                    String[] pokemonesVistos = (cursorPart.getString(4).split(","));
                    ArrayList<Pokemon> pokemonesAvistados = new ArrayList<Pokemon>();
                    for (int i = 0; i< pokemonesVistos.length;i++){
                        if(!pokemonesVistos[i].equals("NULL")){
                            Pokemon pokemonNuevo = gestorAuxPoke.recogerUnPokemon(pokemonesVistos[i]);
                            pokemonesAvistados.add(pokemonNuevo);
                        } else {
                            Pokemon pokemonNuevo = new Pokemon();
                            pokemonesAvistados.add(pokemonNuevo);
                        }
                    }
                    part.setPokemonesJuego(pokemonesAvistados);

                    listaPartidas.add(part);

                } while (cursorPart.moveToNext());

            }

            cursorPart.close();

        } catch (Exception ex) {
            ex.toString();
        }
        return listaPartidas;
    }

    // Método que comprueba los usuarios que hay en la base de datos.
    public boolean comprobarPartida(String nick, String nombre,int version) {
        dbHelperPartida help = new dbHelperPartida(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Partida> listaPart = new ArrayList<>();

        Partida part = null;
        Cursor cursorPart = null;

        cursorPart = db.rawQuery("SELECT * FROM " + tablePartidas+" WHERE nickname = '" + nick + "'", null);

        gestorRuta gestorAuxRuta = new gestorRuta(contexto);
        gestorPokemon gestorAuxPoke = new gestorPokemon(contexto);

        if (cursorPart.moveToFirst()) {

            do {

                part = new Partida();
                part.setNickname(cursorPart.getString(0));
                part.setNombrePartida(cursorPart.getString(1));
                part.setVersion(cursorPart.getInt(2));

                ArrayList<Ruta> rutasVersion = gestorAuxRuta.recogerRutasVersion(part.getVersion());
                part.setRutasJuego(rutasVersion);

                String[] rutasCapturadas = (cursorPart.getString(5).split(","));
                ArrayList<Integer> estadoRutas = new ArrayList<Integer>();
                for (int i = 0; i< rutasCapturadas.length;i++){
                    if(rutasCapturadas[i].equals("0")){
                        estadoRutas.add(0);
                    } else if (rutasCapturadas[i].equals("1")){
                        estadoRutas.add(1);
                    }else{
                        estadoRutas.add(-1);
                    }
                }
                part.setEstadoRuta(estadoRutas);

                String[] pokemonesVistos = (cursorPart.getString(4).split(","));
                ArrayList<Pokemon> pokemonesAvistados = new ArrayList<Pokemon>();
                for (int i = 0; i< pokemonesVistos.length;i++){
                    if(!pokemonesVistos[i].equals("NULL")){
                        Pokemon pokemonNuevo = gestorAuxPoke.recogerUnPokemon(pokemonesVistos[i]);
                        pokemonesAvistados.add(pokemonNuevo);
                    } else {
                        Pokemon pokemonNuevo = new Pokemon();
                        pokemonesAvistados.add(pokemonNuevo);
                    }
                }
                part.setPokemonesJuego(pokemonesAvistados);

                listaPart.add(part);


            } while (cursorPart.moveToNext());

        }

        cursorPart.close();

        boolean comprobacionNombre = false;

        for (int i = 0; i < listaPart.size();i++){
            if(listaPart.get(i).getNombrePartida().equals(nombre) && listaPart.get(i).getVersion()==version){
                comprobacionNombre = true;
                break;
            }
        }

        return comprobacionNombre;
    }

    public Partida recogerPartidaConcreta(String nick, String nombrePartida, int version){
        Partida partidaAux = new Partida();
        ArrayList<Partida> listaPartidas = new ArrayList<>();
        try {
            dbHelperPartida help = new dbHelperPartida(contexto);
            SQLiteDatabase db = help.getWritableDatabase();

            Partida part = null;
            Cursor cursorPart = null;

            cursorPart = db.rawQuery("SELECT * FROM " + tablePartidas+" WHERE nickname = '" + nick + "'", null);

            gestorRuta gestorAuxRuta = new gestorRuta(contexto);
            gestorPokemon gestorAuxPoke = new gestorPokemon(contexto);

            if (cursorPart.moveToFirst()) {

                do {

                    part = new Partida();
                    part.setNickname(cursorPart.getString(0));
                    part.setNombrePartida(cursorPart.getString(1));
                    part.setVersion(cursorPart.getInt(2));

                    ArrayList<Ruta> rutasVersion = gestorAuxRuta.recogerRutasVersion(part.getVersion());
                    part.setRutasJuego(rutasVersion);

                    String[] rutasCapturadas = (cursorPart.getString(5).split(","));
                    ArrayList<Integer> estadoRutas = new ArrayList<Integer>();
                    for (int i = 0; i< rutasCapturadas.length;i++){
                        if(rutasCapturadas[i].equals("0")){
                            estadoRutas.add(0);
                        } else if (rutasCapturadas[i].equals("1")){
                            estadoRutas.add(1);
                        }else{
                            estadoRutas.add(-1);
                        }
                    }
                    part.setEstadoRuta(estadoRutas);

                    String[] pokemonesVistos = (cursorPart.getString(4).split(","));
                    ArrayList<Pokemon> pokemonesAvistados = new ArrayList<Pokemon>();
                    for (int i = 0; i< pokemonesVistos.length;i++){
                        if(!pokemonesVistos[i].equals("NULL")){
                            Pokemon pokemonNuevo = gestorAuxPoke.recogerUnPokemon(pokemonesVistos[i]);
                            pokemonesAvistados.add(pokemonNuevo);
                        } else {
                            Pokemon pokemonNuevo = new Pokemon();
                            pokemonesAvistados.add(pokemonNuevo);
                        }
                    }
                    part.setPokemonesJuego(pokemonesAvistados);

                    listaPartidas.add(part);

                } while (cursorPart.moveToNext());

            }

            cursorPart.close();
        } catch (Exception ex) {
            ex.toString();
        }

            for (int i = 0; i<listaPartidas.size();i++){
                if(listaPartidas.get(i).getVersion() == version && listaPartidas.get(i).getNombrePartida().equals(nombrePartida)){
                    partidaAux = listaPartidas.get(i);
                    break;
                }
            }
        return partidaAux;
    }

    public void partidaNueva(String nick, String nombrePartida, int version){
        gestorRuta gestorAuxRuta = new gestorRuta(contexto);
        ArrayList<Ruta> rutasAux = gestorAuxRuta.recogerRutasVersion(version);
        String rutasJuego ="";
        String pokemones ="";
        String estadoRutas ="";

        for (int i = 0; i<rutasAux.size();i++){
            if(i != (rutasAux.size()-1)) {
                rutasJuego += (rutasAux.get(i) + ",");
                pokemones += ("NULL,");
                estadoRutas +=("-1,");
            } else {
                rutasJuego += (rutasAux.get(i));
                pokemones += ("NULL");
                estadoRutas +=("-1");
            }
        }

        long id = insertarPartida(nick,nombrePartida,version,rutasJuego,pokemones,estadoRutas);
    }

    public int comprobarPokemonEnRuta(String nick, String nombrePartida, int version, String ruta, String poke, int estado){
        dbHelperPartida help = new dbHelperPartida(contexto);
        SQLiteDatabase db = help.getWritableDatabase();

        ArrayList<Partida> listaPart = new ArrayList<>();

        Partida part = null;
        Cursor cursorPart = null;

        cursorPart = db.rawQuery("SELECT * FROM " + tablePartidas+" WHERE nickname = '" + nick + "'", null);

        gestorRuta gestorAuxRuta = new gestorRuta(contexto);
        gestorPokemon gestorAuxPoke = new gestorPokemon(contexto);

        if (cursorPart.moveToFirst()) {

            do {

                part = new Partida();
                part.setNickname(cursorPart.getString(0));
                part.setNombrePartida(cursorPart.getString(1));
                part.setVersion(cursorPart.getInt(2));

                ArrayList<Ruta> rutasVersion = gestorAuxRuta.recogerRutasVersion(part.getVersion());
                part.setRutasJuego(rutasVersion);

                String[] rutasCapturadas = (cursorPart.getString(5).split(","));
                ArrayList<Integer> estadoRutas = new ArrayList<Integer>();
                for (int i = 0; i< rutasCapturadas.length;i++){
                    if(rutasCapturadas[i].equals("0")){
                        estadoRutas.add(0);
                    } else if (rutasCapturadas[i].equals("1")){
                        estadoRutas.add(1);
                    }else{
                        estadoRutas.add(-1);
                    }
                }
                part.setEstadoRuta(estadoRutas);

                String[] pokemonesVistos = (cursorPart.getString(4).split(","));
                ArrayList<Pokemon> pokemonesAvistados = new ArrayList<Pokemon>();
                for (int i = 0; i< pokemonesVistos.length;i++){
                    if(!pokemonesVistos[i].equals("NULL")){
                        Pokemon pokemonNuevo = gestorAuxPoke.recogerUnPokemon(pokemonesVistos[i]);
                        pokemonesAvistados.add(pokemonNuevo);
                    } else {
                        Pokemon pokemonNuevo = new Pokemon();
                        pokemonesAvistados.add(pokemonNuevo);
                    }
                }
                part.setPokemonesJuego(pokemonesAvistados);

                listaPart.add(part);


            } while (cursorPart.moveToNext());

        }

        cursorPart.close();

        Log.i("pruebaSIZEPOKEMONES", ""+listaPart.get(0).getPokemonesJuego().size());
        int comprobacion = -1;
        for (int i = 0; i<listaPart.size();i++){
            if(listaPart.get(i).getNickname().equals(nick) &&
            listaPart.get(i).getNombrePartida().equals(nombrePartida) &&
            listaPart.get(i).getVersion() == version){
                for (int j =0; j<listaPart.get(i).getRutasJuego().size();j++){
                    if(listaPart.get(i).getRutasJuego().get(j).getNombre().equals(ruta)){
                        if(listaPart.get(i).getPokemonesJuego().get(j).getNombre().equals(poke)
                        && listaPart.get(i).getEstadoRuta().get(j) == estado){
                            comprobacion = -1;
                        } else if(listaPart.get(i).getPokemonesJuego().get(j).getNombre().equals(poke)
                        && listaPart.get(i).getEstadoRuta().get(j) != estado){
                            if(estado == 0){
                                comprobacion = 0;
                            } else if(estado == 1){
                                comprobacion = 1;
                            } else {
                                comprobacion = 2;
                            }
                        } else if (!listaPart.get(i).getPokemonesJuego().get(j).getNombre().equals(poke)
                                && listaPart.get(i).getEstadoRuta().get(j) == estado){
                            comprobacion = 3;
                        } else {
                            if (estado == 0){
                                comprobacion = 4;
                            } else if(estado == 1){
                                comprobacion = 5;
                            } else{
                                comprobacion = 6;
                            }
                        }
                    }
                }
            }
        }
        return comprobacion;
    }

    public ArrayList<Partida> modificarPartida(String nick, String nombrePartida,int version, String ruta, String poke, int estado){
        gestorPokemon gestorAuxPoke = new gestorPokemon(contexto);
        ArrayList<Partida> todasPartidas = recogerPartidas();

        for (int i = 0; i < todasPartidas.size();i++){
            if (todasPartidas.get(i).getNickname().equals(nick) &&
             todasPartidas.get(i).getNombrePartida().equals(nombrePartida)&&
            todasPartidas.get(i).getVersion()==version){

                for(int j = 0; j < todasPartidas.get(i).getRutasJuego().size();j++){
                    if(todasPartidas.get(i).getRutasJuego().get(j).getNombre().equals(ruta)){
                        todasPartidas.get(i).getEstadoRuta().set(j,estado);
                        todasPartidas.get(i).getPokemonesJuego().set(j,gestorAuxPoke.recogerUnPokemon(poke));
                        break;
                    }
                }
                break;
            }
        }

        for (int j = 0; j< todasPartidas.get(0).getPokemonesJuego().size();j++){
            Log.i("ESTADOMOD",""+todasPartidas.get(0).getEstadoRuta().get(j));
        }
        return todasPartidas;
    }

}
