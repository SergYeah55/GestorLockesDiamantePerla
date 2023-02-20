package com.example.gestorlockes.clases;

public class Jugador {

    private String nickname;
    private String password;
    private int imagen;

    public void Jugador(){
        this.nickname = "";
        this.password = "";
        this.imagen = 0;
    }

    public void Jugador(String nick, String pass, int imag){
        this.nickname = nick;
        this.password = pass;
        this.imagen = imag;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nuevoNickname) {
        this.nickname = nuevoNickname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String nuevoPassword) {
        this.password = nuevoPassword;
    }

    public int getImagen() {
        return this.imagen;
    }

    public void setImagen(int nuevaImagen) {
        this.imagen = nuevaImagen;
    }
}
