package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorlockes.adapter.partidaAdapter;
import com.example.gestorlockes.basedatos.gestorPartida;
import com.example.gestorlockes.clases.Partida;
import com.example.gestorlockes.clases.Ruta;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private ImageButton salirButon;
    private ImageButton infoButon;

    private ImageButton perfilUsuario;
    private TextView nombreUsuario;
    private ImageView logoPartidas;
    private RecyclerView recyclerPartidas;
    private partidaAdapter adaptadorPartidas;
    private Button volver;
    private Button nuevaPartida;

    private ImageButton sonidoAjustes;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int sonidoClick,sonidoSalir;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        perfilUsuario = (ImageButton)findViewById(R.id.imagenUsuario);
        int imagenEscogida = getIntent().getIntExtra("imagenUsuario",0);
        switch (imagenEscogida){
            case 0:
                perfilUsuario.setForeground(getDrawable(R.drawable.usuariouno));
                break;
            case 1:
                perfilUsuario.setForeground(getDrawable(R.drawable.usuariodos));
                break;
            case 2:
                perfilUsuario.setForeground(getDrawable(R.drawable.usuariotres));
                break;
            case 3:
                perfilUsuario.setForeground(getDrawable(R.drawable.usuariocuatro));
                break;
        }

        nombreUsuario = (TextView)findViewById(R.id.nombreUsuario);
        String nombreEscogido = getIntent().getStringExtra("nombreUsuario");
        nombreUsuario.setText(nombreEscogido);

        logoPartidas = (ImageView)findViewById(R.id.partidasLogo);
        logoPartidas.setVisibility(View.VISIBLE);

        volver = (Button)findViewById(R.id.volver);
        nuevaPartida = (Button)findViewById(R.id.nuevaPartida);

        sonidoAjustes = (ImageButton) findViewById(R.id.musicAjustes);

        // Creamos el reproductor musical, le incluímos la canción de ambiente y reproducimos.
        musicaAmbiente = MediaPlayer.create(this, R.raw.musicafondo);
        musicaAmbiente.setVolume(0.05f, 0.05f);
        musicaAmbiente.setLooping(true);
        sonidoActivo = getIntent().getBooleanExtra("estadoAudio", false);

        if (sonidoActivo) {
            sonidoAjustes.setForeground(getDrawable(R.drawable.muteado));
        } else {
            sonidoAjustes.setForeground(getDrawable(R.drawable.consonido));
        }

        int position = getIntent().getIntExtra("positionSonido", 0);

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

        sonidoAjustes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(sonidoActivo){
                    sonidoAjustes.setForeground(getDrawable(R.drawable.consonido));
                    sonidoActivo = false;
                    musicaAmbiente.pause();
                } else {
                    sonidoAjustes.setForeground(getDrawable(R.drawable.muteado));
                    sonidoActivo = true;
                    musicaAmbiente.start();
                }
            }
        });

        gestorPartida pruebaGestor = new gestorPartida(this);

        recyclerPartidas = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerPartidas.setLayoutManager(new LinearLayoutManager(this));

        List<Partida> pruebaPartidas = pruebaGestor.recogerPartidas(nombreEscogido);

        adaptadorPartidas= new partidaAdapter(pruebaPartidas,this,imagenEscogida,position,sonidoActivo);

        recyclerPartidas.setAdapter(adaptadorPartidas);

        perfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                Intent toPersonalizarActivity = new Intent(getApplicationContext(), PersonalizarActivity.class);
                toPersonalizarActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                toPersonalizarActivity.putExtra("estadoAudio", sonidoActivo);
                toPersonalizarActivity.putExtra("nombreUsuario", nombreEscogido);
                toPersonalizarActivity.putExtra("imagenUsuario", imagenEscogida);
                startActivity(toPersonalizarActivity);
                finish();
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                }
                Intent toMainMenuActivity = new Intent(getApplicationContext(), MainMenuActivity.class);
                toMainMenuActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                toMainMenuActivity.putExtra("estadoAudio", sonidoActivo);
                startActivity(toMainMenuActivity);
                finish();
            }
        });

        nuevaPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                Intent toNuevaPartidaActivity = new Intent(getApplicationContext(), NuevaPartidaActivity.class);
                toNuevaPartidaActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                toNuevaPartidaActivity.putExtra("estadoAudio", sonidoActivo);
                toNuevaPartidaActivity.putExtra("nombreUsuario", nombreEscogido);
                toNuevaPartidaActivity.putExtra("imagenUsuario", imagenEscogida);
                startActivity(toNuevaPartidaActivity);
                finish();
            }
        });

        infoButon = (ImageButton)findViewById(R.id.infoButonSetting);
        infoButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderInfo = new AlertDialog.Builder(SettingActivity.this);

                builderInfo.setMessage("Esta es la interfaz para controlar tus partidas.");
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

        salirButon = (ImageButton) findViewById(R.id.salirButtonSettings);
        salirButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                    musicaAmbiente.stop();
                }

                String despedida = "¡Vuelve pronto!";
                Toast.makeText(SettingActivity.this, despedida, Toast.LENGTH_LONG).show();
                finish();
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
    public void onBackPressed() {
    }

}