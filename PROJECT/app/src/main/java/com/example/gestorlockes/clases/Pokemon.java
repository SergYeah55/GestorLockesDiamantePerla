package com.example.gestorlockes.clases;

// Se refieren a las entradas de cada uno de los pokémones que puede haber.
public class Pokemon {

    // Declaración de las variables que conforman los objetos pokémon.
    private String nombre;
    private int numero;

    // En la version se indicará en qué juego aparece y en cuál no, siendo 0 diamante
    // 1 perla y 2 ambos de los juegos, sin haber exclusividad.
    private int version;

    private String sexo;
    private String tipo1;
    private String tipo2;
    private int forma;

    // Se realiza un constructor vacío.
    public Pokemon(){
        this.nombre = "NULL";
        this.numero = 0;
        this.version = -1;
        this.sexo = "";
        this.tipo1="";
        this.tipo2="";
        this.forma = -1;
    }

    // Elaboramos un constructor normal del propio objeto.
    public Pokemon(String nom, int num, int ver, String sex, String tip1, String tip2, int form){
        this.nombre = nom;
        this.numero = num;
        this.version = ver;
        this.sexo = sex;
        this.tipo1 = tip1;
        this.tipo2 = tip2;
        this.forma = form;
    }

    // Se elaboran los getters y los setters de las diferentes características

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

    public String getSexo() {
        return this.sexo;
    }

    public void setSexo(String nuevoSexo) {
        this.sexo = nuevoSexo;
    }

    public String getTipo1() {
        return this.tipo1;
    }

    public void setTipo1(String nuevoTipo1) {
        this.tipo1 = nuevoTipo1;
    }

    public String getTipo2() {
        return this.tipo2;
    }

    public void setTipo2(String nuevoTipo2) {
        this.tipo2 = nuevoTipo2;
    }

    public int getForma() {
        return this.forma;
    }

    public void setForma(int nuevaForma) {
        this.forma = nuevaForma;
    }
}
