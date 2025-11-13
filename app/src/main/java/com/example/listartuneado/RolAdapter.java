package com.example.listartuneado;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RolAdapter extends RecyclerView.Adapter<RolAdapter.RolViewHolder> {

    private ArrayList<Rol> listaRoles;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Rol rol);
    }

    public RolAdapter(ArrayList<Rol> listaRoles) {
        this.listaRoles = listaRoles;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rol, parent, false);
        return new RolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RolViewHolder holder, int position) {
        Rol rol = listaRoles.get(position);
        holder.bind(rol);
    }

    @Override
    public int getItemCount() {
        return listaRoles != null ? listaRoles.size() : 0;
    }

    public void actualizarLista(ArrayList<Rol> nuevaLista) {
        this.listaRoles = nuevaLista;
        notifyDataSetChanged();
    }

    class RolViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreRol;
        private TextView tvDescripcionRol;
        private TextView tvIdRol;

        public RolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRol = itemView.findViewById(R.id.tvNombreRol);
            tvDescripcionRol = itemView.findViewById(R.id.tvDescripcionRol);
            tvIdRol = itemView.findViewById(R.id.tvIdRol);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(listaRoles.get(position));
                }
            });
        }

        public void bind(Rol rol) {
            tvNombreRol.setText(rol.getNombre());
            if (rol.getDescripcion() != null && !rol.getDescripcion().isEmpty()) {
                tvDescripcionRol.setText(rol.getDescripcion());
                tvDescripcionRol.setVisibility(View.VISIBLE);
            } else {
                tvDescripcionRol.setVisibility(View.GONE);
            }
            tvIdRol.setText("ID: " + rol.getId());
        }
    }
}

