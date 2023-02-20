package com.example.gestorlockes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorlockes.basedatos.dbHelperJugador;
import com.example.gestorlockes.basedatos.gestorJugador;
import com.example.gestorlockes.clases.Jugador;

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

public class PersonalizarActivity extends AppCompatActivity {

    private ImageButton salirButon;
    private ImageButton infoButon;

    private String nombreEscogido;
    private int imagenEscogida;
    AlertDialog.Builder builderImagen;
    AlertDialog.Builder builderPass;

    private ImageView logoPersonalizacion;

    private Button cambioPass;
    private TextView enunciadoPass;
    private EditText nuevaPass;
    private ImageButton validacionPass;

    private Button cambioImagen;
    private ImageButton opcion1;
    private ImageButton opcion2;
    private ImageButton opcion3;
    private ImageButton opcion4;

    private Button volverPersonalizar;

    private ImageButton sonidoPersonalizar;
    private boolean sonidoActivo;
    private MediaPlayer musicaAmbiente;
    private SoundPool efectos;
    private int sonidoClick,sonidoSalir;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizar);

        builderImagen = new AlertDialog.Builder(PersonalizarActivity.this);
        builderPass = new AlertDialog.Builder(PersonalizarActivity.this);

        nombreEscogido = getIntent().getStringExtra("nombreUsuario");
        imagenEscogida = getIntent().getIntExtra("imagenUsuario",0);

        logoPersonalizacion = (ImageView) findViewById(R.id.logoPersonalizacion);
        logoPersonalizacion.setVisibility(View.VISIBLE);

        cambioPass = (Button)findViewById(R.id.cambioPass);
        cambioPass.setVisibility(View.VISIBLE);
        enunciadoPass = (TextView)findViewById(R.id.enunciadoPass);
        enunciadoPass.setVisibility(View.GONE);
        nuevaPass = (EditText)findViewById(R.id.nuevaPass);
        nuevaPass.setVisibility(View.GONE);
        validacionPass = (ImageButton)findViewById(R.id.validacionPass);
        validacionPass.setVisibility(View.GONE);

        cambioImagen = (Button)findViewById(R.id.cambioImagen);
        cambioImagen.setVisibility(View.VISIBLE);
        opcion1 = (ImageButton)findViewById(R.id.opcion1);
        opcion1.setForeground(getDrawable(R.drawable.usuariouno));
        opcion1.setVisibility(View.GONE);
        opcion2 = (ImageButton)findViewById(R.id.opcion2);
        opcion2.setForeground(getDrawable(R.drawable.usuariodos));
        opcion2.setVisibility(View.GONE);
        opcion3 = (ImageButton)findViewById(R.id.opcion3);
        opcion3.setForeground(getDrawable(R.drawable.usuariotres));
        opcion3.setVisibility(View.GONE);
        opcion4 = (ImageButton)findViewById(R.id.opcion4);
        opcion4.setForeground(getDrawable(R.drawable.usuariocuatro));
        opcion4.setVisibility(View.GONE);

        volverPersonalizar = (Button)findViewById(R.id.volverPersonalizar);

        sonidoPersonalizar = (ImageButton) findViewById(R.id.musicPersonalizar);

        // Creamos el reproductor musical, le incluímos la canción de ambiente y reproducimos.
        musicaAmbiente = MediaPlayer.create(this, R.raw.musicafondo);
        musicaAmbiente.setVolume(0.05f, 0.05f);
        musicaAmbiente.setLooping(true);
        sonidoActivo = getIntent().getBooleanExtra("estadoAudio", false);

        if (sonidoActivo) {
            sonidoPersonalizar.setForeground(getDrawable(R.drawable.muteado));
        } else {
            sonidoPersonalizar.setForeground(getDrawable(R.drawable.consonido));
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

        sonidoPersonalizar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(sonidoActivo){
                    sonidoPersonalizar.setForeground(getDrawable(R.drawable.consonido));
                    sonidoActivo = false;
                    musicaAmbiente.pause();
                } else {
                    sonidoPersonalizar.setForeground(getDrawable(R.drawable.muteado));
                    sonidoActivo = true;
                    musicaAmbiente.start();
                }
            }
        });

        volverPersonalizar.setOnClickListener(new View.OnClickListener() {
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

        infoButon = (ImageButton)findViewById(R.id.infoButonPersonalizacion);
        infoButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderInfo = new AlertDialog.Builder(PersonalizarActivity.this);

                builderInfo.setMessage("Esta es la interfaz para cambiar datos de usuario.");
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

        salirButon = (ImageButton) findViewById(R.id.salirButonPersonalizacion);
        salirButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sonidoActivo) {
                    efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                    musicaAmbiente.stop();
                }

                String despedida = "¡Vuelve pronto!";
                Toast.makeText(PersonalizarActivity.this, despedida, Toast.LENGTH_LONG).show();
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

        cambioPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                enunciadoPass.setVisibility(View.VISIBLE);
                nuevaPass.setVisibility(View.VISIBLE);
                validacionPass.setVisibility(View.VISIBLE);
            }
        });

        validacionPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                String nuevaPassRellenada = nuevaPass.getText().toString();
                gestorJugador gestorPass = new gestorJugador(PersonalizarActivity.this);
                String passAntigua = gestorPass.getPass(nombreEscogido);

                if (nuevaPassRellenada.equals(passAntigua)){
                    builderPass.setMessage("Es la misma contraseña que tenías.");
                    builderPass.setCancelable(false);
                    builderPass.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderPass.create().show();
                } else if (nuevaPassRellenada.equals("") || nuevaPassRellenada.length() < 5){
                    builderPass.setMessage("No es válida la nueva contraseña.");
                    builderPass.setCancelable(false);
                    builderPass.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderPass.create().show();
                } else {
                    try {
                        cambiarPass(nombreEscogido,nuevaPassRellenada);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    builderPass.setMessage("Has cambiado correctamente la contraseña.");
                    builderPass.setCancelable(false);
                    builderPass.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderPass.create().show();
                }
                enunciadoPass.setVisibility(View.GONE);
                nuevaPass.setVisibility(View.GONE);
                validacionPass.setVisibility(View.GONE);
            }
        });

        cambioImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                switch (imagenEscogida){
                    case 0:
                        opcion1.setBackgroundColor(Color.RED);
                        break;
                    case 1:
                        opcion2.setBackgroundColor(Color.RED);
                        break;
                    case 2:
                        opcion3.setBackgroundColor(Color.RED);
                        break;
                    case 3:
                        opcion4.setBackgroundColor(Color.RED);
                        break;
                }
                opcion1.setVisibility(View.VISIBLE);
                opcion2.setVisibility(View.VISIBLE);
                opcion3.setVisibility(View.VISIBLE);
                opcion4.setVisibility(View.VISIBLE);
            }
        });

        opcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                if (imagenEscogida == 1 || imagenEscogida == 2 || imagenEscogida == 3) {
                    switch (imagenEscogida){
                        case 1:
                            opcion2.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 2:
                            opcion3.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 3:
                            opcion4.setBackgroundColor(Color.TRANSPARENT);
                            break;
                    }
                    imagenEscogida = 0;
                    try {
                        cambiarImagen(nombreEscogido,imagenEscogida);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    builderImagen.setMessage("¡Has cambiado tu imagen de perfil!");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                } else {
                    builderImagen.setMessage("No has cambiado tu imagen de perfil.");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                }
                opcion1.setVisibility(View.GONE);
                opcion2.setVisibility(View.GONE);
                opcion3.setVisibility(View.GONE);
                opcion4.setVisibility(View.GONE);
            }
        });

        opcion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                if (imagenEscogida == 0 || imagenEscogida == 2 || imagenEscogida == 3) {
                    switch (imagenEscogida){
                        case 0:
                            opcion1.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 2:
                            opcion3.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 3:
                            opcion4.setBackgroundColor(Color.TRANSPARENT);
                            break;
                    }
                    imagenEscogida = 1;
                    try {
                        cambiarImagen(nombreEscogido,imagenEscogida);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    builderImagen.setMessage("¡Has cambiado tu imagen de perfil!");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                } else {
                    builderImagen.setMessage("No has cambiado tu imagen de perfil.");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                }
                opcion1.setVisibility(View.GONE);
                opcion2.setVisibility(View.GONE);
                opcion3.setVisibility(View.GONE);
                opcion4.setVisibility(View.GONE);
            }
        });

        opcion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                if (imagenEscogida == 0 || imagenEscogida == 1 || imagenEscogida == 3) {
                    switch (imagenEscogida){
                        case 0:
                            opcion1.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 1:
                            opcion2.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 3:
                            opcion4.setBackgroundColor(Color.TRANSPARENT);
                            break;
                    }
                    imagenEscogida = 2;
                    try {
                        cambiarImagen(nombreEscogido,imagenEscogida);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    builderImagen.setMessage("¡Has cambiado tu imagen de perfil!");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                } else {
                    builderImagen.setMessage("No has cambiado tu imagen de perfil.");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                }
                opcion1.setVisibility(View.GONE);
                opcion2.setVisibility(View.GONE);
                opcion3.setVisibility(View.GONE);
                opcion4.setVisibility(View.GONE);
            }
        });
        opcion4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidoActivo) {
                    efectos.play(sonidoClick, 1, 1, 0, 0, 1);
                }
                if (imagenEscogida == 0 || imagenEscogida == 1 || imagenEscogida == 2) {
                    switch (imagenEscogida){
                        case 0:
                            opcion1.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 1:
                            opcion2.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case 2:
                            opcion3.setBackgroundColor(Color.TRANSPARENT);
                            break;
                    }
                    imagenEscogida = 3;
                    try {
                        cambiarImagen(nombreEscogido,imagenEscogida);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    builderImagen.setMessage("¡Has cambiado tu imagen de perfil!");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                } else {
                    builderImagen.setMessage("No has cambiado tu imagen de perfil.");
                    builderImagen.setCancelable(false);
                    builderImagen.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sonidoActivo) {
                                efectos.play(sonidoSalir, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    builderImagen.create().show();
                }
                opcion1.setVisibility(View.GONE);
                opcion2.setVisibility(View.GONE);
                opcion3.setVisibility(View.GONE);
                opcion4.setVisibility(View.GONE);
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

    public void cambiarPass (String nick, String pass) throws JSONException, IOException {

        dbHelperJugador baseDatosJugador = new dbHelperJugador(PersonalizarActivity.this);
        ArrayList<Jugador> listaJuga = new ArrayList<>();
        gestorJugador gestorCambiarPass = new gestorJugador(PersonalizarActivity.this);

        try {
            listaJuga = gestorCambiarPass.recogerJugadores();

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < listaJuga.size(); i++) {
            if (listaJuga.get(i).getNickname().equals(nick)) {
                listaJuga.get(i).setPassword(pass);
                break;
            }
        }

        //Escritura en los ranking
        JSONArray arrayAux = new JSONArray();
        JSONObject objAux;

        for (int i = 0; i < listaJuga.size(); i++) {
            objAux = new JSONObject();
            objAux.put("nickname", listaJuga.get(i).getNickname());
            objAux.put("password", listaJuga.get(i).getPassword());
            objAux.put("imagen", listaJuga.get(i).getImagen());
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
        baseDatosJugador.onUpgrade(baseDatosJugador.getWritableDatabase(), baseDatosJugador.databaseVersion, baseDatosJugador.databaseVersion+1);
        SQLiteDatabase refBaseJugador = baseDatosJugador.getWritableDatabase();
        rellenarBaseDatosJugadoresConCambios();
    }

    public void cambiarImagen (String nick, int imag) throws JSONException, IOException {

        dbHelperJugador baseDatosJugador = new dbHelperJugador(PersonalizarActivity.this);
        ArrayList<Jugador> listaJuga = new ArrayList<>();
        gestorJugador gestorCambiarPass = new gestorJugador(PersonalizarActivity.this);

        try {
            listaJuga = gestorCambiarPass.recogerJugadores();

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < listaJuga.size(); i++) {
            if (listaJuga.get(i).getNickname().equals(nick)) {
                listaJuga.get(i).setImagen(imag);
                break;
            }
        }

        //Escritura en los ranking
        JSONArray arrayAux = new JSONArray();
        JSONObject objAux;

        for (int i = 0; i < listaJuga.size(); i++) {
            objAux = new JSONObject();
            objAux.put("nickname", listaJuga.get(i).getNickname());
            objAux.put("password", listaJuga.get(i).getPassword());
            objAux.put("imagen", listaJuga.get(i).getImagen());
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
        baseDatosJugador.onUpgrade(baseDatosJugador.getWritableDatabase(), baseDatosJugador.databaseVersion, baseDatosJugador.databaseVersion+1);
        SQLiteDatabase refBaseJugador = baseDatosJugador.getWritableDatabase();
        rellenarBaseDatosJugadoresConCambios();
    }


    public void rellenarBaseDatosJugadoresConCambios() {
        gestorJugador prueba = new gestorJugador(PersonalizarActivity.this);

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

}