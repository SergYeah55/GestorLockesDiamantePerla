package com.example.gestorlockes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestorlockes.R;
import com.example.gestorlockes.clases.Partida;
import com.example.gestorlockes.clases.Pokemon;

import java.util.List;
import java.util.Locale;

public class pokemonpokedexadapter extends RecyclerView.Adapter<pokemonpokedexadapter.ViewHolder> {

    private List<Pokemon> listaPokes;
    private Context contexto;

    public pokemonpokedexadapter(List<Pokemon> list, Context cont){
        this.listaPokes=list;
        this.contexto = cont;
    }

    @NonNull
    @Override
    public pokemonpokedexadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_pokedex,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pokemon pokeAux = listaPokes.get(position);
        if (pokeAux.getNombre().equals("Mr. Mime")){
            holder.imagenPokemon.setForeground(contexto.getDrawable(R.drawable.mr_mime));
        } else if (pokeAux.getNombre().equals("Mime Jr.")){
            holder.imagenPokemon.setForeground(contexto.getDrawable(R.drawable.mime_jr));
        } else {
            int drawableResourceId = contexto.getResources().getIdentifier(pokeAux.getNombre().toLowerCase(Locale.ROOT), "drawable", contexto.getPackageName());
            Log.i("PruebaCarga","" +pokeAux.getNombre());
            holder.imagenPokemon.setForeground(contexto.getDrawable(drawableResourceId));
        }

        holder.nombrePokemon.setText(listaPokes.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return listaPokes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagenPokemon;
        private TextView nombrePokemon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagenPokemon = itemView.findViewById(R.id.imagenPokemonPokedex);
            nombrePokemon = itemView.findViewById(R.id.nombrePokemonPokedex);
        }
    }
}
