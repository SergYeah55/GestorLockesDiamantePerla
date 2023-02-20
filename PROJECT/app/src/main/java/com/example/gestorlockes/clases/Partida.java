package com.example.gestorlockes.clases;

import java.util.List;

public class Partida {

    // Declaramos las variables principales que manejará una partida.
    private String nickname;
    private String nombrePartida;
    private int version;
    private List<Ruta> rutasJuego;
    private List<Pokemon> pokemonesJuego;

    // En un Locke en las rutas solo se puede capturar al primer pokémon de la ruta, pero
    // se puede dar el caso de que se pierda el pokémon o que se debilite. 0 indicará que
    // todavía no se ha visto nada, 1 que se ha encontrado y 2 que se ha gastado ruta sin exito.
    private List<Integer> estadoRuta;

    // Se declaran los constructores vacío y con algunos elementos.
    public void Partida(){
        this.nickname = "";
        this.nombrePartida ="";
        this.version = -1;
        this.rutasJuego = null;
        this.pokemonesJuego = null;
        this.estadoRuta = null;
    }

    public void Partida(String nick,String nombP, int vers, List<Ruta> rutas, List<Pokemon> pokemones, List<Integer> estado){
        this.nickname = nick;
        this.nombrePartida = nombP;
        this.version = vers;
        this.rutasJuego = rutas;
        this.pokemonesJuego = pokemones;
        this.estadoRuta = estado;
    }

    // Declaración de getters y setters de los diferentes elementos.
    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nuevoNickname) {
        this.nickname = nuevoNickname;
    }

    public String getNombrePartida(){
        return this.nombrePartida;
    }

    public void setNombrePartida(String nuevoNombrePartida){
        this.nombrePartida = nuevoNombrePartida;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int nuevaVersion) {
        this.version = nuevaVersion;
    }

    public List<Ruta> getRutasJuego() {
        return this.rutasJuego;
    }

    public void setRutasJuego(List<Ruta> nuevasRutasJuego) {
        this.rutasJuego = nuevasRutasJuego;
    }

    public List<Pokemon> getPokemonesJuego() {
        return this.pokemonesJuego;
    }

    public void setPokemonesJuego(List<Pokemon> nuevosPokemonesJuego) {
        this.pokemonesJuego = nuevosPokemonesJuego;
    }

    public List<Integer> getEstadoRuta() {
        return this.estadoRuta;
    }

    public void setEstadoRuta(List<Integer> nuevoEstadoRuta) {
        this.estadoRuta = nuevoEstadoRuta;
    }
}
