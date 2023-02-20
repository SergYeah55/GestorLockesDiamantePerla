package com.example.gestorlockes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestorlockes.R;
import com.example.gestorlockes.clases.Pokemon;
import com.example.gestorlockes.clases.Ruta;

import java.util.List;
import java.util.Locale;

public class pokemonRutaAdapter extends RecyclerView.Adapter<pokemonRutaAdapter.ViewHolder> {

    private List<Pokemon> listaPokes;
    private List<Ruta> listaRutas;
    private List<Integer> listaEstados;
    private Context contexto;

    public pokemonRutaAdapter(List<Pokemon> list, List<Ruta> list2, List<Integer> list3, Context cont){
        this.listaPokes=list;
        this.listaRutas=list2;
        this.listaEstados=list3;
        this.contexto = cont;
    }


    @NonNull
    @Override
    public pokemonRutaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_ruta,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull pokemonRutaAdapter.ViewHolder holder, int position) {
        Pokemon pokeAux = listaPokes.get(position);
        if (pokeAux.getNombre().equals("Mr. Mime")){
            holder.imagenPokemon.setForeground(contexto.getDrawable(R.drawable.mr_mime));
        } else if (pokeAux.getNombre().equals("Mime Jr.")){
            holder.imagenPokemon.setForeground(contexto.getDrawable(R.drawable.mime_jr));
        } else {
            if(pokeAux.getNombre().equals("NULL")){
                holder.imagenPokemon.setForeground(contexto.getDrawable(R.drawable.nopoke));
            } else {
                int drawableResourceId = contexto.getResources().getIdentifier(pokeAux.getNombre().toLowerCase(Locale.ROOT), "drawable", contexto.getPackageName());
                Log.i("PruebaCarga","" +pokeAux.getNombre());
                holder.imagenPokemon.setForeground(contexto.getDrawable(drawableResourceId));
            }

        }

        holder.nombreRuta.setText(listaRutas.get(position).getNombre());

        if(listaEstados.get(position) == 0){
            holder.estadoPokemon.setText("Escapado");
            holder.imagenPokemon.setBackgroundColor(Color.BLUE);
        } else if(listaEstados.get(position) == 1){
            holder.estadoPokemon.setText("Capturado");
            holder.imagenPokemon.setBackgroundColor(Color.GREEN);
        } else {
            if(listaPokes.get(position).getNombre().equals("NULL")){
                holder.estadoPokemon.setText("");
                holder.imagenPokemon.setBackgroundColor(Color.TRANSPARENT);
            }else {
                holder.estadoPokemon.setText("Muerto");
                holder.imagenPokemon.setBackgroundColor(Color.RED);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listaPokes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagenPokemon;
        private TextView nombreRuta;
        private TextView estadoPokemon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagenPokemon = itemView.findViewById(R.id.imagenPokemonCapturado);
            nombreRuta = itemView.findViewById(R.id.nombreRutaCapturados);
            estadoPokemon = itemView.findViewById(R.id.estadoPokemonCapturado);
        }
    }
}
