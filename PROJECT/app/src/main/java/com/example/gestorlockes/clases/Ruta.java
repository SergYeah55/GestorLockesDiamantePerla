package com.example.gestorlockes.clases;

import java.util.List;

public class Ruta {

    // Declaración de variables básicas para el objeto ruta
    private String nombre;
    private int numero;

    // En la version se indicará los posibles pokemones que puede haber
    // y en cuál no, siendo 0 diamante,1 perla y 2 ambos de los juegos,
    // sin haber exclusividad.
    private int version;
    private List<Pokemon> listaPokemones;

    // Se elabora el constructor vacío y un constructor recibiendo parámetros.
    public void Ruta(){
        this.nombre = "";
        this.numero = 0;
        this.version = -1;
        this.listaPokemones = null;
    }

    public void Ruta(String nom, int num, int ver, List<Pokemon> list){
        this.nombre = nom;
        this.numero = num;
        this.version = ver;
        this.listaPokemones = list;
    }

    // Se declaran los getters y setters de los diferentes atributos.
    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    public int getNumero() {
        return this.numero;
    }

    public void setNumero(int nuevoNumero) {
        this.numero = nuevoNumero;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int nuevaVersion) {
        this.version = nuevaVersion;
    }

    public List<Pokemon> getListaPokemones() {
        return this.listaPokemones;
    }

    public void setListaPokemones(List<Pokemon> nuevaListaPokemones) {
        this.listaPokemones = nuevaListaPokemones;
    }

}
