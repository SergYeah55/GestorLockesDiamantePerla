package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorlockes.basedatos.dbHelperJugador;
import com.example.gestorlockes.basedatos.dbHelperPartida;
import com.example.gestorlockes.basedatos.gestorPartida;
import com.example.gestorlockes.basedatos.gestorRuta;
import com.example.gestorlockes.clases.Partida;
import com.example.gestorlockes.clases.Pokemon;
import com.example.gestorlockes.clases.Ruta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PartidaActivity extends AppCompatActivity {

    private ImageButton salirButon;
    private ImageButton infoButon;

    private ImageButton imagenUsuario;
    private ImageView edicion;
    private TextView nombreUsuario;
    private TextView nombrePartida;

    private ImageView logoRutasTotales;
    private Spinner spinnerRutas;

    private TextView pokemonPosLogo;
    private Spinner pokemonesRuta;
    private CheckBox capturadoState;
    private CheckBox inhabilitadoState;

    private ImageButton accederPokedex;
    private ImageButton accederPokemonesPropios;

    private Button volver;
    private Button agregar;

    private ImageButton sonidoPartida;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int sonidoClick,sonidoSalir;

    private String nombreEscogido;
    private String nombrePartidaEscogido;
    private int imagenEscogida;
    private int edicionEscogida;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);

        imagenUsuario = (ImageButton) findViewById(R.id.imagenUsuarioPartida);
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

        edicion = findViewById(R.id.edicionPartidaLogo);
        edicion.setVisibility(View.VISIBLE);
        edicionEscogida = getIntent().getIntExtra("versionPartida",0);
        switch(edicionEscogida){
            case 1:
                edicion.setForeground(getDrawable(R.drawable.diamantelogop));
                break;
            case 2:
                edicion.setForeground(getDrawable(R.drawable.perlalogop));
                break;
        }

        nombreUsuario = (TextView)findViewById(R.id.nombreUsuarioPartida);
        nombreEscogido = getIntent().getStringExtra("nombreUsuario");
        nombreUsuario.setText(nombreEscogido);

        nombrePartida = (TextView)findViewById(R.id.nombrePartidaP);
        nombrePartidaEscogido = getIntent().getStringExtra("nombrePartida");
        nombrePartida.setText(nombrePartidaEscogido);

        logoRutasTotales = (ImageView)findViewById(R.id.logoRutasTotal);
        logoRutasTotales.setVisibility(View.VISIBLE);

        initSpinnerRutas();

        pokemonPosLogo = (TextView)findViewById(R.id.pokemonEncontradoEnun);
        pokemonPosLogo.setVisibility(View.VISIBLE);

        initSpinnerPokemones(spinnerRutas.getSelectedItem().toString(),edicionEscogida);

        capturadoState = (CheckBox)findViewById(R.id.capturado);
        inhabilitadoState = (CheckBox)findViewById(R.id.Inhabilitado);

        spinnerRutas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initSpinnerPokemones(spinnerRutas.getSelectedItem().toString(),edicionEscogida);
                capturadoState.setChecked(false);
                inhabilitadoState.setChecked(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sonidoPartida = (ImageButton) findViewById(R.id.musicPartida);

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

        volver = (Button)findViewById(R.id.volverAjustesButon);
        volver.setOnClickListener(new View.OnClickListener() {
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
        agregar = (Button) findViewById(R.id.agregarBoton);

        infoButon = (ImageButton)findViewById(R.id.infoButonPartida);
        infoButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderInfo = new AlertDialog.Builder(PartidaActivity.this);

                builderInfo.setMessage("Esta es la interfaz para gestionar tus capturas. Para ver todas tus posibilidades" +
                        " deberas entrar en la pokedex. Para ver los estados de tus pokemon, entra en capturados.");
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

        salirButon = (ImageButton) findViewById(R.id.salirButonPartida);
        salirButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                    musicaAmbiente.stop();
                }

                String despedida = "¡Vuelve pronto!";
                Toast.makeText(PartidaActivity.this, despedida, Toast.LENGTH_LONG).show();
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

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderAgregarPoke = new AlertDialog.Builder(PartidaActivity.this);
                gestorPartida comprobaPartida = new gestorPartida(PartidaActivity.this);
                int comprobacion;
                int estado;
                if (capturadoState.isChecked() && inhabilitadoState.isChecked()){
                    estado = 2;
                }else if(capturadoState.isChecked() && !inhabilitadoState.isChecked()){
                    estado = 1;
                }else if(!capturadoState.isChecked() && inhabilitadoState.isChecked()){
                    estado =0;
                } else {
                    estado = -1;
                }
                comprobacion = comprobaPartida.comprobarPokemonEnRuta(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),estado);
                Log.i("comprobarNUM","Estado:"+estado+" Comprobacion:"+comprobacion);
                switch(comprobacion){
                    case 0:
                        builderAgregarPoke.setMessage("¿Vas a pasar este pokemon a inhabilitado?");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),0);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case 1:
                        builderAgregarPoke.setMessage("¿Vas a pasar este pokemon a capturado?");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),1);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case 2:
                        builderAgregarPoke.setMessage("¿Vas a pasar este pokemon a muerto?");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),2);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }

                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case 3:
                        builderAgregarPoke.setMessage("¿Vas cambiar el pokemon de esta ruta?");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),1);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case 4:
                        builderAgregarPoke.setMessage("¿Vas a poner un pokemon en inhabilitado");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),0);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case 5:
                        builderAgregarPoke.setMessage("¿Vas a poner un pokemon en capturado?");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),1);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case 6:
                        builderAgregarPoke.setMessage("¿Vas a poner un pokemon en muerto?");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),2);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.setNegativeButton("Cambio de opinión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                    case (-1):
                        builderAgregarPoke.setMessage("No has generado ningún cambio.");
                        builderAgregarPoke.setCancelable(false);
                        builderAgregarPoke.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sonidoActivo) {
                                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                                }
                                ArrayList<Partida> pruebaModificacion = comprobaPartida.modificarPartida(nombreEscogido,nombrePartidaEscogido,edicionEscogida,
                                        spinnerRutas.getSelectedItem().toString(),pokemonesRuta.getSelectedItem().toString(),estado);
                                try {
                                    actualizarPartidas(pruebaModificacion);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builderAgregarPoke.create().show();
                        break;
                }
            }
        });

        accederPokedex = (ImageButton)findViewById(R.id.botonPokedex);
        accederPokedex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                }
                Intent toPokedexActivity = new Intent(getApplicationContext(), PokedexActivity.class);
                toPokedexActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                toPokedexActivity.putExtra("estadoAudio", sonidoActivo);
                toPokedexActivity.putExtra("nombreUsuario", nombreEscogido);
                toPokedexActivity.putExtra("imagenUsuario", imagenEscogida);
                toPokedexActivity.putExtra("nombrePartida",nombrePartidaEscogido);
                toPokedexActivity.putExtra("versionEscogida", edicionEscogida);
                startActivity(toPokedexActivity);
                finish();
            }
        });

        accederPokemonesPropios = (ImageButton) findViewById(R.id.botonPokemonesPropios);
        accederPokemonesPropios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                }
                Intent toPokemonPropiosActivity = new Intent(getApplicationContext(), PokemonPropiosActivity.class);
                toPokemonPropiosActivity.putExtra("positionSonido", musicaAmbiente.getCurrentPosition());
                toPokemonPropiosActivity.putExtra("estadoAudio", sonidoActivo);
                toPokemonPropiosActivity.putExtra("nombreUsuario", nombreEscogido);
                toPokemonPropiosActivity.putExtra("imagenUsuario", imagenEscogida);
                toPokemonPropiosActivity.putExtra("nombrePartida",nombrePartidaEscogido);
                toPokemonPropiosActivity.putExtra("versionEscogida", edicionEscogida);
                startActivity(toPokemonPropiosActivity);
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

    private void initSpinnerRutas() {
        spinnerRutas = (Spinner) findViewById(R.id.spinnerRutas);
        gestorRuta recolectorRutas = new gestorRuta(PartidaActivity.this);
        ArrayList<Ruta> rutasRecolectadas;
        rutasRecolectadas = recolectorRutas.recogerRutasVersion(edicionEscogida);

        String[] rutasEncontradas = new String[rutasRecolectadas.size()];
        for (int i = 0; i<rutasRecolectadas.size();i++){
            rutasEncontradas[i] = rutasRecolectadas.get(i).getNombre();
        }

        //Se elabora el adaptador para poner el spinner por pantalla
        ArrayAdapter auxVersiones = new ArrayAdapter(this, android.R.layout.simple_spinner_item, rutasEncontradas);

        auxVersiones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRutas.setAdapter(auxVersiones);
    }

    private void initSpinnerPokemones(String nombreRuta, int version){
        pokemonesRuta = (Spinner) findViewById(R.id.spinnerPokemon);
        gestorRuta recolectorPokemones = new gestorRuta(PartidaActivity.this);
        ArrayList<Pokemon> pokemonesRecolectados;
        pokemonesRecolectados = recolectorPokemones.recogerPokemonPorRuta(nombreRuta,version);

        String[] pokemonesEncontrados = new String[pokemonesRecolectados.size()];
        for (int i = 0; i<pokemonesRecolectados.size();i++){
            pokemonesEncontrados[i] = pokemonesRecolectados.get(i).getNombre();
        }

        ArrayAdapter auxVersiones = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pokemonesEncontrados);

        auxVersiones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pokemonesRuta.setAdapter(auxVersiones);
    }

    public void actualizarPartidas(ArrayList<Partida> nuevaLista) throws JSONException, IOException {

        dbHelperPartida baseDatosPartida = new dbHelperPartida(PartidaActivity.this);
        //Lectura de los ranking
        ArrayList<Partida> partidas = new ArrayList<>();
        gestorPartida gestorActualizar = new gestorPartida(this);

        partidas = nuevaLista;

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

        baseDatosPartida.onUpgrade(baseDatosPartida.getWritableDatabase(), baseDatosPartida.databaseVersion, baseDatosPartida.databaseVersion+1);
        SQLiteDatabase refBaseJugador = baseDatosPartida.getWritableDatabase();

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

            JSONArray arrayAux2 = new JSONArray(contenido);

            for (int i = 0; i < arrayAux2.length(); i++) {
                JSONObject objetoAux = arrayAux2.getJSONObject(i);
                String nickname = objetoAux.getString("nickname");
                String nombrePartida = objetoAux.getString("nombrePartida");
                int version = objetoAux.getInt("version");
                String rutasJuego = objetoAux.getString("rutasJuego");
                String pokemones = objetoAux.getString("pokemones");
                String estadoRuta = objetoAux.getString("estadoRuta");

                gestorActualizar.insertarPartida(nickname,nombrePartida,version,rutasJuego,pokemones,estadoRuta);
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


}