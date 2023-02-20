package com.example.gestorlockes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestorlockes.PartidaActivity;
import com.example.gestorlockes.R;
import com.example.gestorlockes.clases.Partida;
import com.example.gestorlockes.clases.Pokemon;

import java.util.List;

public class partidaAdapter extends RecyclerView.Adapter<partidaAdapter.ViewHolder> {

    private List<Partida> listaPartidas;
    private Context contexto;
    private int img;
    private int posMusic;
    private boolean musicActiva;
    private int version;

    public partidaAdapter(List<Partida> list, Context cont, int img,int pos,boolean music){
        this.listaPartidas = list;
        this.contexto = cont;
        this.img = img;
        this.posMusic = pos;
        this.musicActiva = music;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partida_usuario,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Partida partidaAux = listaPartidas.get(position);
        if (listaPartidas.get(position).getVersion() == 1){
            int drawableResourceId = contexto.getResources().getIdentifier("diamanteversion", "drawable", contexto.getPackageName());
            holder.imagenVersion.setImageResource(drawableResourceId);
        } else{
            holder.imagenVersion.setImageResource(R.drawable.perlaversion);
        }

        holder.nombreUsuario.setText(listaPartidas.get(position).getNombrePartida());

        List<Pokemon> listaAux = listaPartidas.get(position).getPokemonesJuego();
        int cuenta = 0;
        for (int i = 0; i<listaAux.size();i++){
            if (!listaAux.get(i).getNombre().equals("NULL")){
                cuenta = cuenta+1;
            }
        }
        float porcentaje = ((float)cuenta/(float)listaAux.size()) *100;
        holder.porcentajeRutas.setText(""+(int)porcentaje+"% de rutas visitadas.");

        holder.pasarPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderPass = new AlertDialog.Builder(contexto);

                builderPass.setMessage("¿Quieres acceder a esta partida ?");
                builderPass.setCancelable(false);
                builderPass.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent toPartidaActivity = new Intent(contexto, PartidaActivity.class);
                        toPartidaActivity.putExtra("positionSonido", posMusic);
                        toPartidaActivity.putExtra("estadoAudio", musicActiva);
                        toPartidaActivity.putExtra("nombreUsuario", partidaAux.getNickname());
                        toPartidaActivity.putExtra("nombrePartida",partidaAux.getNombrePartida());
                        toPartidaActivity.putExtra("versionPartida",partidaAux.getVersion());
                        toPartidaActivity.putExtra("imagenUsuario", img);
                        contexto.startActivity(toPartidaActivity);
                        ((Activity)contexto).finish();
                    }
                });
                builderPass.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builderPass.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagenVersion;
        private TextView nombreUsuario;
        private TextView porcentajeRutas;
        private ImageButton pasarPartida;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagenVersion = itemView.findViewById(R.id.imagenVersionPartida);
            nombreUsuario = itemView.findViewById(R.id.nombreRutaTarjeta);
            porcentajeRutas = itemView.findViewById(R.id.porcentajeCaptura);
            pasarPartida = itemView.findViewById(R.id.botonEntrarPartida);

        }
    }

}
