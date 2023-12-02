package com.example.MateusVere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//Adapter do Recycler View
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    ArrayList<Foto> fotoArrayListLocal;
    ArrayList<Foto> fotoArrayListCopia;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public RecyclerAdapter(ArrayList<Foto> fotoArrayListLocal_) {
        this.fotoArrayListLocal = fotoArrayListLocal_;
        fotoArrayListCopia = new ArrayList<>(fotoArrayListLocal);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String autor = fotoArrayListLocal.get(position).getAutor();
        String data = fotoArrayListLocal.get(position).getData();
        String imagem = fotoArrayListLocal.get(position).getUrlImg();

        holder.mTextViewTit.setText(autor);
        holder.mTextViewAno.setText(data);
        Glide.with(holder.mImageView.getContext()).load(imagem).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return fotoArrayListLocal.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextViewTit;
        TextView mTextViewAno;
        ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTit = itemView.findViewById(R.id.textViewTitulo);
            mTextViewAno = itemView.findViewById(R.id.textViewAno);

            mImageView = itemView.findViewById(R.id.imageView);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getContext().toString().contains("UsuarioLogado")) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Salvar filme")
                        .setMessage("Confirma salvar nos favoritos?")
                        .setIcon(R.drawable.ic_baseline_favorite_border_24)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(view.getContext(), "Título salvo nos favoritos.", Toast.LENGTH_SHORT).show();
                                inserirEm(getLayoutPosition());

                            }
                        })
                        .setNegativeButton("Não", null).show();
            }
            else {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Remover")
                        .setMessage("Confirma remover dos favoritos?")
                        .setIcon(R.drawable.ic_baseline_delete_outline_24)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            //click no botão de ok, remover do Firebase, método "removerEm()"
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(view.getContext(), "Título removido dos favoritos.", Toast.LENGTH_SHORT).show();
                                removerEm(getLayoutPosition());
                            }
                        })
                        .setNegativeButton("Não", null).show();
            }
        }

        private void inserirEm(int layoutPosition) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Foto f = fotoArrayListLocal.get(layoutPosition);
            databaseReference.child(user.getUid()).
                    child("Fotos").
                    child(f.getAutor()).
                    setValue(f);

        }

    }
    public void removerEm(int layoutPosition) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Foto f = fotoArrayListLocal.get(layoutPosition);
        fotoArrayListLocal.clear();

        databaseReference.child(user.getUid()).child("Fotos").
                child(f.getAutor()).
                removeValue();
    }

    public void filtrar(String text) {
        fotoArrayListLocal.clear();
        if (text.isEmpty()) {
            fotoArrayListLocal.addAll(fotoArrayListCopia);
        } else {

            text = text.toLowerCase();
            for (Foto item : fotoArrayListCopia) {
                if (item.getAutor().toLowerCase().contains(text) || item.getData().toLowerCase().contains(text)) {
                    fotoArrayListLocal.add(item);
                }
            }
        }
    }
}
