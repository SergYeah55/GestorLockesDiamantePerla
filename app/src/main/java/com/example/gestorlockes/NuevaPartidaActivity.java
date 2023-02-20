package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorlockes.basedatos.gestorJugador;
import com.example.gestorlockes.basedatos.gestorPartida;
import com.example.gestorlockes.clases.Jugador;
import com.example.gestorlockes.clases.Partida;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NuevaPartidaActivity extends AppCompatActivity {

    private ImageButton salirButon;
    private ImageButton infoButon;

    private ImageButton imagenUsuario;
    private TextView nombreUsuario;

    private ImageView logoNuevaP;
    private Spinner elegirVersion;
    private TextView enunciadoNuevaP;
    private EditText nuevoNombrePartida;
    private ImageView diamante;
    private ImageView perla;
    private Button crearPartida;
    private Button volverAjustes;

    private ImageButton sonidoNuevaP;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int sonidoClick,sonidoSalir;

    private String nombreEscogido;
    private int imagenEscogida;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_partida);

        nombreEscogido = getIntent().getStringExtra("nombreUsuario");
        imagenUsuario = (ImageButton) findViewById(R.id.imagenUsuarioNuevaP);
        imagenEscogida = getIntent().getIntExtra("imagenUsuario", 0);
        switch (imagenEscogida) {
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

        nombreUsuario = (TextView) findViewById(R.id.nombreUsuarioNuevaP);
        String nombreEscogido = getIntent().getStringExtra("nombreUsuario");
        nombreUsuario.setText(nombreEscogido);

        logoNuevaP = (ImageView) findViewById(R.id.nuevaPartidaLogo);
        logoNuevaP.setVisibility(View.VISIBLE);

        initSpinner();

        enunciadoNuevaP = (TextView) findViewById(R.id.enunciadoNuevaPartida);
        nuevoNombrePartida = (EditText)findViewById(R.id.nombreNuevaPartida);

        diamante = (ImageView) findViewById(R.id.diamanteVersion);
        diamante.setVisibility(View.VISIBLE);
        perla = (ImageView) findViewById(R.id.perlaVersion);
        perla.setVisibility(View.GONE);

        crearPartida = (Button) findViewById(R.id.crearBoton);
        volverAjustes = (Button) findViewById(R.id.volverBotonNP);

        sonidoNuevaP = (ImageButton) findViewById(R.id.musicaNuevaPartida);

        // Creamos el reproductor musical, le incluímos la canción de ambiente y reproducimos.
        musicaAmbiente = MediaPlayer.create(this, R.raw.musicafondo);
        musicaAmbiente.setVolume(0.05f, 0.05f);
        musicaAmbiente.setLooping(true);
        sonidoActivo = getIntent().getBooleanExtra("estadoAudio", false);

        if (sonidoActivo) {
            sonidoNuevaP.setForeground(getDrawable(R.drawable.muteado));
        } else {
            sonidoNuevaP.setForeground(getDrawable(R.drawable.consonido));
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

        sonidoNuevaP.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    sonidoNuevaP.setForeground(getDrawable(R.drawable.consonido));
                    sonidoActivo = false;
                    musicaAmbiente.pause();
                } else {
                    sonidoNuevaP.setForeground(getDrawable(R.drawable.muteado));
                    sonidoActivo = true;
                    musicaAmbiente.start();
                }
            }
        });

        elegirVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (elegirVersion.getSelectedItem().toString() == "Diamante") {
                    diamante.setVisibility(View.VISIBLE);
                    perla.setVisibility(View.GONE);
                } else {
                    diamante.setVisibility(View.GONE);
                    perla.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        crearPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = nuevoNombrePartida.getText().toString();
                String version = elegirVersion.getSelectedItem().toString();
                gestorPartida gestorComproba = new gestorPartida(NuevaPartidaActivity.this);
                AlertDialog.Builder builderNombrePartida = new AlertDialog.Builder(NuevaPartidaActivity.this);

                if (nuevoNombre.equals("") || nuevoNombre.toLowerCase().equals("nombre")) {
                    builderNombrePartida.setMessage("Nombre de partida no válido.");
                    builderNombrePartida.setCancelable(false);
                    builderNombrePartida.setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderNombrePartida.create().show();
                } else {
                    boolean comprobacion;

                    if (version.equals("Diamante")) {
                        comprobacion = gestorComproba.comprobarPartida(nombreEscogido, nuevoNombre,1);
                    } else{
                        comprobacion = gestorComproba.comprobarPartida(nombreEscogido, nuevoNombre,2);
                    }

                    if(comprobacion){
                        builderNombrePartida.setMessage("Partida ya existente.");
                        builderNombrePartida.setCancelable(false);
                        builderNombrePartida.setPositiveButton("Crear otra.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderNombrePartida.create().show();
                    } else{

                        if (version.equals("Diamante")) {
                            gestorComproba.partidaNueva(nombreEscogido, nuevoNombre, 1);
                            try {
                                actualizarPartidas();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else{
                            gestorComproba.partidaNueva(nombreEscogido, nuevoNombre, 2);
                            try {
                                actualizarPartidas();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        builderNombrePartida.setMessage("Partida creada con éxito.");
                        builderNombrePartida.setCancelable(false);
                        builderNombrePartida.setPositiveButton("Continuar.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                                Intent toSettingActivity = new Intent(getApplicationContext(), SettingActivity.class);
                                toSettingActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                                toSettingActivity.putExtra("estadoAudio", sonidoActivo);
                                toSettingActivity.putExtra("nombreUsuario", nombreEscogido);
                                toSettingActivity.putExtra("imagenUsuario", imagenEscogida);
                                startActivity(toSettingActivity);
                                finish();
                            }
                        });
                        builderNombrePartida.create().show();
                    }
                }
            }
        });

        volverAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                }
                Intent toSettingActivity = new Intent(getApplicationContext(), SettingActivity.class);
                toSettingActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                toSettingActivity.putExtra("estadoAudio", sonidoActivo);
                toSettingActivity.putExtra("nombreUsuario", nombreEscogido);
                toSettingActivity.putExtra("imagenUsuario", imagenEscogida);
                startActivity(toSettingActivity);
                finish();
            }
        });
        infoButon = (ImageButton)findViewById(R.id.infoButonNuevaPartida);
        infoButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderInfo = new AlertDialog.Builder(NuevaPartidaActivity.this);

                builderInfo.setMessage("Esta es la interfaz para crear una nueva partida. Elige la edición y su nombre.");
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

        salirButon = (ImageButton) findViewById(R.id.salirButonNuevaPartida);
        salirButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                    musicaAmbiente.stop();
                }

                String despedida = "¡Vuelve pronto!";
                Toast.makeText(NuevaPartidaActivity.this, despedida, Toast.LENGTH_LONG).show();
                finish();
            }
        });
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

    private void initSpinner() {
        //Creación e inicialización del spinner que permite elegir la version
        elegirVersion = (Spinner)findViewById(R.id.elegirVersion);

        String[] versiones = {"Diamante", "Perla"};

        //Se elabora el adaptador para poner el spinner por pantalla
        ArrayAdapter auxVersiones = new ArrayAdapter(this, android.R.layout.simple_spinner_item, versiones);

        auxVersiones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        elegirVersion.setAdapter(auxVersiones);
    }

    public void actualizarPartidas() throws JSONException, IOException {

        //Lectura de los ranking
        ArrayList<Partida> partidas = new ArrayList<>();
        gestorPartida gestorActualizar = new gestorPartida(this);

        try {
            partidas = gestorActualizar.recogerPartidas();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Escritura en los ranking
        JSONArray arrayAux = new JSONArray();
        JSONObject objAux;

        for (int i = 0; i < partidas.size(); i++) {
            objAux = new JSONObject();
            objAux.put("nickname", partidas.get(i).getNickname());
            objAux.put("nombrePartida", partidas.get(i).getNombrePartida());
            objAux.put("version", partidas.get(i).getVersion());

            String rutas = "";
            String pokemones = "";
            String estados ="";

            for (int j = 0; j < partidas.get(i).getRutasJuego().size(); j++){
                if (j < (partidas.get(i).getRutasJuego().size() - 1)){
                    rutas += partidas.get(i).getRutasJuego().get(j).getNombre() + ",";
                    pokemones += partidas.get(i).getPokemonesJuego().get(j).getNombre() + ",";
                    estados += partidas.get(i).getEstadoRuta().get(j) + ",";
                } else {
                    rutas += partidas.get(i).getRutasJuego().get(j).getNombre();
                    pokemones += partidas.get(i).getPokemonesJuego().get(j).getNombre();
                    estados += partidas.get(i).getEstadoRuta().get(j);
                }
            }
            Log.i("pruebaPokemones",""+pokemones);
            objAux.put("rutasJuego", rutas);
            objAux.put("pokemones", pokemones);
            objAux.put("estadoRuta", estados);
            arrayAux.put(objAux);
        }
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