package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.gestorlockes.basedatos.gestorJugador;
import com.example.gestorlockes.basedatos.gestorRuta;
import com.example.gestorlockes.clases.Jugador;
import com.example.gestorlockes.clases.Ruta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {

    private ImageView logoRegistro;
    private EditText nombreUsuario;
    private EditText passwordUsuario;
    private Button siguiente;

    private ImageButton sonidoRegistro;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int sonidoClick, sonidoSalir;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Con esto forzamos el modo vertical del móvil
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        logoRegistro = (ImageView) findViewById(R.id.logoRegistro);
        logoRegistro.setVisibility(View.VISIBLE);
        nombreUsuario = (EditText) findViewById(R.id.textNick);
        passwordUsuario = (EditText) findViewById(R.id.textPass);
        siguiente = (Button) findViewById(R.id.siguiente);
        sonidoRegistro = (ImageButton) findViewById(R.id.musicLogin);

        // Creamos el reproductor musical, le incluímos la canción de ambiente y reproducimos.
        musicaAmbiente = MediaPlayer.create(this, R.raw.musicafondo);
        musicaAmbiente.setVolume(0.05f, 0.05f);
        musicaAmbiente.setLooping(true);
        sonidoActivo = getIntent().getBooleanExtra("estadoAudio", false);

        if (sonidoActivo) {
            sonidoRegistro.setForeground(getDrawable(R.drawable.muteado));
        } else {
            sonidoRegistro.setForeground(getDrawable(R.drawable.consonido));
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

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = nombreUsuario.getText().toString();
                String password = passwordUsuario.getText().toString();
                AlertDialog.Builder builderNick = new AlertDialog.Builder(MainMenuActivity.this);

                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }

                if (usuario.equals("") || usuario.toLowerCase().equals("nombre")) {
                    builderNick.setMessage("Usuario no válido. Hay que poner un nombre único.");
                    builderNick.setCancelable(false);
                    builderNick.setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderNick.create().show();
                } else if (password.equals("") || password.length() < 5) {
                    builderNick.setMessage("Usuario no válido. Recuerda que la contraseña debe tener al menos 5 caracteres.");
                    builderNick.setCancelable(false);
                    builderNick.setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderNick.create().show();
                } else if (usuario.length() > 9 || usuario.length() < 3) {
                    builderNick.setMessage("Usuario no válido. Recuerda que el usuario debe tener al menos 3 caracteres y máximo 9.");
                    builderNick.setCancelable(false);
                    builderNick.setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderNick.create().show();
                } else {
                    gestorJugador gestorComproba = new gestorJugador(MainMenuActivity.this);
                    int comprobacion;

                    comprobacion = gestorComproba.comprobarUsuario(usuario, password);
                    switch (comprobacion) {
                        case -1:
                            builderNick.setMessage("Usuario no creado,¿quieres crearlo?.");
                            builderNick.setCancelable(false);
                            builderNick.setPositiveButton("¡Sí!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (sonidoActivo) {
                                        efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                    }
                                    Jugador playerNuevo = new Jugador();
                                    playerNuevo.setNickname(usuario);
                                    playerNuevo.setPassword(password);
                                    playerNuevo.setImagen(0);
                                    try {
                                        gestorComproba.insertarJugador(usuario,password,0);
                                        actualizarJugadores(playerNuevo);
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent toSettingActivity = new Intent(getApplicationContext(), SettingActivity.class);
                                    toSettingActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                                    toSettingActivity.putExtra("estadoAudio", sonidoActivo);
                                    toSettingActivity.putExtra("nombreUsuario", usuario);
                                    toSettingActivity.putExtra("imagenUsuario", 0);
                                    startActivity(toSettingActivity);
                                    finish();
                                }

                            });
                            builderNick.setNegativeButton("No, elijo otros.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (sonidoActivo) {
                                        efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                    }
                                }
                            });

                            builderNick.create().show();
                            break;
                        case 0:
                            builderNick.setMessage("Contraseña no válida.");
                            builderNick.setCancelable(false);
                            builderNick.setPositiveButton("Volver a intentarlo.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (sonidoActivo) {
                                        efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                    }
                                }
                            });
                            builderNick.create().show();
                            break;
                        case 1:
                            builderNick.setMessage("Usuario encontrado. ¡Disfruta de tu locke!");
                            builderNick.setCancelable(false);
                            builderNick.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (sonidoActivo) {
                                        efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                    }
                                    Intent toSettingActivity = new Intent(getApplicationContext(), SettingActivity.class);
                                    toSettingActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                                    toSettingActivity.putExtra("estadoAudio", sonidoActivo);
                                    toSettingActivity.putExtra("nombreUsuario", usuario);
                                    toSettingActivity.putExtra("imagenUsuario", gestorComproba.obtenerImagen(usuario));
                                    startActivity(toSettingActivity);
                                    finish();
                                }
                            });
                            builderNick.create().show();
                            break;
                    }

                }
            }
        });

        sonidoRegistro.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(sonidoActivo){
                    sonidoRegistro.setForeground(getDrawable(R.drawable.consonido));
                    sonidoActivo = false;
                    musicaAmbiente.pause();
                } else {
                    sonidoRegistro.setForeground(getDrawable(R.drawable.muteado));
                    sonidoActivo = true;
                    musicaAmbiente.start();
                }
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

    public void actualizarJugadores(Jugador player) throws JSONException, IOException {

        //Lectura de los ranking
        ArrayList<Jugador> jugadores = new ArrayList<>();
        gestorJugador gestorActualizar = new gestorJugador(MainMenuActivity.this);

        try {
            jugadores = gestorActualizar.recogerJugadores();

        } catch (Exception e) {
            e.printStackTrace();
        }
        jugadores.add(player);

        //Escritura en los ranking
        JSONArray arrayAux = new JSONArray();
        JSONObject objAux;

        for (int i = 0; i < jugadores.size(); i++) {
            objAux = new JSONObject();
            objAux.put("nickname", jugadores.get(i).getNickname());
            objAux.put("password", jugadores.get(i).getPassword());
            objAux.put("imagen", jugadores.get(i).getImagen());
            arrayAux.put(objAux);
        }
        String JSONtext = arrayAux.toString();
        Log.i("JSONText", "" + JSONtext);

        FileOutputStream flujoEscritura = null;

        try {
            flujoEscritura = openFileOutput("listaJugadores.json", MODE_PRIVATE);
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