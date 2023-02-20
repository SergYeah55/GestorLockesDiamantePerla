package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ipsec.ike.exceptions.InvalidMajorVersionException;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorlockes.adapter.partidaAdapter;
import com.example.gestorlockes.adapter.pokemonpokedexadapter;
import com.example.gestorlockes.basedatos.gestorPartida;
import com.example.gestorlockes.basedatos.gestorPokemon;
import com.example.gestorlockes.clases.Partida;
import com.example.gestorlockes.clases.Pokemon;

import java.util.List;

public class PokedexActivity extends AppCompatActivity {

    private ImageButton salirButon;
    private ImageButton infoButon;

    private ImageButton imagenUsuario;
    private ImageView edicion;
    private TextView nombreUsuario;
    private TextView nombrePartida;

    private ImageView logoPokedex;
    private RecyclerView recyclerPokedex;
    private pokemonpokedexadapter adaptadorPokedex;
    private Button volver;

    private ImageButton sonidoPartida;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int position;
    private int sonidoClick,sonidoSalir;

    private String nombreEscogido;
    private String nombrePartidaEscogido;
    private int imagenEscogida;
    private int edicionEscogida;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        imagenUsuario = (ImageButton) findViewById(R.id.imagenUsuarioPokedex);
        imagenUsuario.setVisibility(View.VISIBLE);
        imagenEscogida = getIntent().getIntExtra("imagenUsuario",0);
        switch (imagenEscogida){
            case 0:
                imagenUsuario.setForeground(getDrawable(R.drawable.usuariouno));
                break;
            case 1:
                imagenUsuario.setForeground(getDrawable(R.drawable.usuariodos));
                break;
            case 2:
                imagenUsuario.setForeground(getDrawable(R.drawable.usuariotres));
                break;
            case 3:
                imagenUsuario.setForeground(getDrawable(R.drawable.usuariocuatro));
                break;
        }

        edicion = findViewById(R.id.edicionPokedex);
        edicion.setVisibility(View.VISIBLE);
        edicionEscogida = getIntent().getIntExtra("versionEscogida",0);
        Log.i("pruebaEdicion",""+edicionEscogida);
        switch(edicionEscogida){
            case 1:
                edicion.setForeground(getDrawable(R.drawable.diamantelogop));
                break;
            case 2:
                edicion.setForeground(getDrawable(R.drawable.perlalogop));
                break;
        }

        nombreUsuario = (TextView)findViewById(R.id.nombreUsuarioPokedex);
        nombreEscogido = getIntent().getStringExtra("nombreUsuario");
        nombreUsuario.setText(nombreEscogido);

        nombrePartida = (TextView)findViewById(R.id.nombrePartidaPokedex);
        nombrePartidaEscogido = getIntent().getStringExtra("nombrePartida");
        nombrePartida.setText(nombrePartidaEscogido);

        logoPokedex = (ImageView) findViewById(R.id.logoPokedex);
        logoPokedex.setVisibility(View.VISIBLE);

        volver = (Button) findViewById(R.id.volverDePokedex);

        sonidoPartida = (ImageButton) findViewById(R.id.musicaPokedex);

        // Creamos el reproductor musical, le incluímos la canción de ambiente y reproducimos.
        musicaAmbiente = MediaPlayer.create(this, R.raw.musicafondo);
        musicaAmbiente.setVolume(0.05f, 0.05f);
        musicaAmbiente.setLooping(true);
        sonidoActivo = getIntent().getBooleanExtra("estadoAudio", false);

        if (sonidoActivo) {
            sonidoPartida.setForeground(getDrawable(R.drawable.muteado));
        } else {
            sonidoPartida.setForeground(getDrawable(R.drawable.consonido));
        }

        position = getIntent().getIntExtra("positionSonido", 0);

        musicaAmbiente.seekTo(position);
        musicaAmbiente.start();

        // Creamos la pila de sonidos que van a utilizarse según diferentes funcionalidades
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            efectos = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            efectos = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        sonidoClick = efectos.load(this, R.raw.sonidoclick, 1);
        sonidoSalir = efectos.load(this, R.raw.sonidosalir, 1);
        sonidoPartida.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(sonidoActivo){
                    sonidoPartida.setForeground(getDrawable(R.drawable.consonido));
                    sonidoActivo = false;
                    musicaAmbiente.pause();
                } else {
                    sonidoPartida.setForeground(getDrawable(R.drawable.muteado));
                    sonidoActivo = true;
                    musicaAmbiente.start();
                }
            }
        });

        infoButon = (ImageButton)findViewById(R.id.infoButonPokedex);
        infoButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderInfo = new AlertDialog.Builder(PokedexActivity.this);

                builderInfo.setMessage("Esta es la interfaz para poder ver todas las posibilidades de Pokemon que hay en tu versión.");
                builderInfo.setCancelable(false);
                builderInfo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sonidoActivo) {
                            efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                        }
                    }
                });
                builderInfo.create().show();
            }
        });

        salirButon = (ImageButton) findViewById(R.id.salirButonPokedex);
        salirButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                    musicaAmbiente.stop();
                }

                String despedida = "¡Vuelve pronto!";
                Toast.makeText(PokedexActivity.this, despedida, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (sonidoActivo) {
            musicaAmbiente.start();
        } else {
            musicaAmbiente.pause();
        }

        gestorPokemon pruebaGestor = new gestorPokemon(this);

        recyclerPokedex = (RecyclerView)findViewById(R.id.recyclerPokedex);
        recyclerPokedex.setLayoutManager(new LinearLayoutManager(this));

        List<Pokemon> pruebaPokemon = pruebaGestor.recogerPokemonVersion((edicionEscogida-1));

        adaptadorPokedex= new pokemonpokedexadapter(pruebaPokemon,this);

        recyclerPokedex.setAdapter(adaptadorPokedex);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPartidaActivity = new Intent(getApplicationContext(), PartidaActivity.class);
                toPartidaActivity.putExtra("positionSonido", position);
                toPartidaActivity.putExtra("estadoAudio", sonidoActivo);
                toPartidaActivity.putExtra("nombreUsuario", nombreEscogido);
                toPartidaActivity.putExtra("nombrePartida",nombrePartidaEscogido);
                toPartidaActivity.putExtra("versionPartida",edicionEscogida);
                toPartidaActivity.putExtra("imagenUsuario", imagenEscogida);
                startActivity(toPartidaActivity);
                finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        musicaAmbiente.pause();
    }

    //Se inutiliza el botón hacia atrás de Android
    @Override
    public void onBackPressed() {
    }
}