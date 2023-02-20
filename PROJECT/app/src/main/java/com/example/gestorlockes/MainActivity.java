package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gestorlockes.basedatos.dbHelperJugador;
import com.example.gestorlockes.basedatos.dbHelperPartida;
import com.example.gestorlockes.basedatos.dbHelperPokemon;
import com.example.gestorlockes.basedatos.dbHelperRuta;
import com.example.gestorlockes.basedatos.gestorJugador;
import com.example.gestorlockes.basedatos.gestorPartida;
import com.example.gestorlockes.basedatos.gestorPokemon;
import com.example.gestorlockes.basedatos.gestorRuta;
import com.example.gestorlockes.clases.Jugador;
import com.example.gestorlockes.clases.Partida;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Se declaran las variables que van a tener interacción con el usuario
    private ImageView logo;
    private Button comenzar;
    private Button salir;
    private ImageButton sonido;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int sonidoClick,sonidoSalir;

    private Button prueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Con esto forzamos el modo vertical del móvil
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos los botones y variables que aparecen por el menú
        logo = (ImageView)findViewById(R.id.logo);
        logo.setVisibility(View.VISIBLE);
        comenzar = (Button)findViewById(R.id.comenzar);
        salir = (Button) findViewById(R.id.salir);
        sonido = (ImageButton)findViewById(R.id.music);
        sonidoActivo = true;

        // Creamos el reproductor musical, le incluímos la canción de ambiente y reproducimos.
        musicaAmbiente = MediaPlayer.create(this,R.raw.musicafondo);
        musicaAmbiente.setVolume(0.05f,0.05f);
        musicaAmbiente.setLooping(true);
        musicaAmbiente.start();

        // Creamos la pila de sonidos que van a utilizarse según diferentes funcionalidades
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            efectos = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else{
            efectos = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        }
        sonidoClick = efectos.load(this,R.raw.sonidoclick,1);
        sonidoSalir = efectos.load(this,R.raw.sonidosalir,1);

        // Se establece el comportamiento del botón para comenzar.
        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }

                // Se crea y se rellena la base de datos de jugadores accediendo a un JSON.
                dbHelperJugador baseDatosJugador = new dbHelperJugador(MainActivity.this);
                baseDatosJugador.onUpgrade(baseDatosJugador.getWritableDatabase(), baseDatosJugador.databaseVersion, baseDatosJugador.databaseVersion+1);
                SQLiteDatabase refBaseJugador = baseDatosJugador.getWritableDatabase();
                rellenarBaseDatosJugadores();

                // Se crea y se rellena la base de datos de pokemones accediendo a un JSON
                dbHelperPokemon baseDatosPokemon = new dbHelperPokemon(MainActivity.this);
                baseDatosPokemon.onUpgrade(baseDatosPokemon.getWritableDatabase(), baseDatosPokemon.databaseVersion, baseDatosPokemon.databaseVersion+1);
                SQLiteDatabase refBase = baseDatosPokemon.getWritableDatabase();
                rellenarBaseDatosPokemones();

                // Se crea y se rellena la base de datos de rutas accediendo a un JSON
                dbHelperRuta baseDatosRutas = new dbHelperRuta(MainActivity.this);
                baseDatosRutas.onUpgrade(baseDatosRutas.getWritableDatabase(), baseDatosRutas.databaseVersion, baseDatosRutas.databaseVersion+1);
                SQLiteDatabase refBaseRutas = baseDatosRutas.getWritableDatabase();
                rellenarBaseDatosRutas();

                // Se crea y se rellena la base de datos de partidas accediendo a un JSON
                dbHelperPartida baseDatosPartidas = new dbHelperPartida(MainActivity.this);
                baseDatosPartidas.onUpgrade(baseDatosPartidas.getWritableDatabase(), baseDatosPartidas.databaseVersion, baseDatosPartidas.databaseVersion+1);
                SQLiteDatabase refBasePartidas = baseDatosPartidas.getWritableDatabase();
                rellenarBaseDatosPartidas();

                Intent toMainMenuActivity = new Intent(getApplicationContext(),MainMenuActivity.class);
                toMainMenuActivity.putExtra("positionSonido",musicaAmbiente.getCurrentPosition());
                toMainMenuActivity.putExtra("estadoAudio",sonidoActivo);
                startActivity(toMainMenuActivity);

                finish();
            }
        });

        // Se establece el comportamiento para el botón salir
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                    musicaAmbiente.stop();
                }

                String despedida = "¡Vuelve pronto!";
                Toast.makeText(MainActivity.this, despedida, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // Se establece el comportamiento para el botón del mute
        sonido.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(sonidoActivo){
                    sonido.setForeground(getDrawable(R.drawable.consonido));
                    sonidoActivo = false;
                    musicaAmbiente.pause();
                } else {
                    sonido.setForeground(getDrawable(R.drawable.muteado));
                    sonidoActivo = true;
                    musicaAmbiente.start();
                }
            }
        });

        prueba = (Button) findViewById(R.id.pruebaButon);
        prueba.setVisibility(View.GONE);
        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaciarJSONPartidas();
                Log.i("JSON VACIADO","VACIADO");
            }
        });
    }

    // Se configura el audio para cuando esté en estado Start la activity.
    @Override
    public void onStart() {
        super.onStart();
        if (sonidoActivo) {
            musicaAmbiente.start();
        } else {
            musicaAmbiente.pause();
        }
    }

    // Se configura el audio para cuando esté en estado Stop la activity.
    @Override
    public void onStop() {
        super.onStop();
        musicaAmbiente.pause();
    }
    //Se inutiliza el botón hacia atrás de Android
    @Override
    public void onBackPressed() { }

    public void rellenarBaseDatosPokemones(){
        gestorPokemon prueba = new gestorPokemon(MainActivity.this);

        BufferedReader lectorAux = null;
        try{
            lectorAux = new BufferedReader(new InputStreamReader(MainActivity.this.getAssets().open("Pokemon.json"),"UTF-8"));
            String contenido = "";
            String linea;

            while((linea = lectorAux.readLine())!= null){
                contenido = contenido + linea;
            }

            JSONArray arrayAux = new JSONArray(contenido);

            for (int i = 0; i<arrayAux.length(); i++){
                JSONObject objetoAux = arrayAux.getJSONObject(i);
                String nombre = objetoAux.getString("nombre");
                int numero = objetoAux.getInt("numero");
                int version = objetoAux.getInt("version");
                String sexo = objetoAux.getString("sexo");
                String tipo1 = objetoAux.getString("tipo1");
                String tipo2 = objetoAux.getString("tipo2");
                int forma = objetoAux.getInt("forma");

                long id = prueba.insertarPokemon(nombre, numero, version, sexo,
                        tipo1, tipo2, forma);
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void rellenarBaseDatosJugadores() {
        gestorJugador prueba = new gestorJugador(MainActivity.this);
        FileInputStream jugadoresFlujo = null;
        BufferedReader lectorAux = null;
        try {
            jugadoresFlujo = openFileInput("listaJugadores.json");

            InputStreamReader jugadoresLector = new InputStreamReader(jugadoresFlujo);
            lectorAux = new BufferedReader(jugadoresLector);
            String contenido = "";
            String linea;

            while ((linea = lectorAux.readLine()) != null) {
                contenido = contenido + linea;
            }
            lectorAux.close();

            JSONArray arrayAux = new JSONArray(contenido);

            for (int i = 0; i < arrayAux.length(); i++) {
                JSONObject objetoAux = arrayAux.getJSONObject(i);
                String nickname = objetoAux.getString("nickname");
                String password = objetoAux.getString("password");
                int imagen = objetoAux.getInt("imagen");

                prueba.insertarJugador(nickname,password,imagen);
            }
        } catch (Exception e) {
            Log.i("Database", "Creando nueva base de datos");
        } finally {
            if (jugadoresFlujo != null) {
                try {
                    jugadoresFlujo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rellenarBaseDatosRutas() {
        gestorRuta prueba = new gestorRuta(MainActivity.this);
        BufferedReader lectorAux = null;
        try {
            lectorAux = new BufferedReader(new InputStreamReader(MainActivity.this.getAssets().open("Ruta.json"), "UTF-8"));
            String contenido = "";
            String linea;

            while ((linea = lectorAux.readLine()) != null) {
                contenido = contenido + linea;
            }

            JSONArray arrayAux = new JSONArray(contenido);

            for (int i = 0; i < arrayAux.length(); i++) {
                JSONObject objetoAux = arrayAux.getJSONObject(i);
                String nombre = objetoAux.getString("nombre");
                int numero = objetoAux.getInt("numero");
                int version = objetoAux.getInt("version");
                String pokemones = objetoAux.getString("pokemones");

                long id = prueba.insertarRuta(nombre, numero, version, pokemones);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void rellenarBaseDatosPartidas() {
        gestorPartida prueba = new gestorPartida(MainActivity.this);
        FileInputStream partidasFlujo = null;
        BufferedReader lectorAux = null;
        try {
            partidasFlujo = openFileInput("listaPartidas.json");

            InputStreamReader partidasLector = new InputStreamReader(partidasFlujo);
            lectorAux = new BufferedReader(partidasLector);
            String contenido = "";
            String linea;

            while ((linea = lectorAux.readLine()) != null) {
                contenido = contenido + linea;
            }
            lectorAux.close();

            JSONArray arrayAux = new JSONArray(contenido);

            for (int i = 0; i < arrayAux.length(); i++) {
                JSONObject objetoAux = arrayAux.getJSONObject(i);
                String nickname = objetoAux.getString("nickname");
                String nombrePartida = objetoAux.getString("nombrePartida");
                int version = objetoAux.getInt("version");
                String rutasJuego = objetoAux.getString("rutasJuego");
                String pokemones = objetoAux.getString("pokemones");
                String estadoRuta = objetoAux.getString("estadoRuta");

                prueba.insertarPartida(nickname,nombrePartida,version,rutasJuego,pokemones,estadoRuta);
            }
        } catch (Exception e) {
            Log.i("Database", "Creando nueva base de datos");
        } finally {
            if (partidasFlujo != null) {
                try {
                    partidasFlujo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void vaciarJSONPartidas(){
        //Escritura en los ranking
        JSONArray arrayAux = new JSONArray();

        String JSONtext = arrayAux.toString();
        Log.i("JSONText", "" + JSONtext);

        FileOutputStream flujoEscritura = null;

        try {
            flujoEscritura = openFileOutput("listaPartidas.json", MODE_PRIVATE);
            flujoEscritura.write(JSONtext.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (flujoEscritura != null) {
                try {
                    flujoEscritura.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}