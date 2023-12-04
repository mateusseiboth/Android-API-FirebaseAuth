package com.example.MateusVere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsuarioFavoritos extends AppCompatActivity {

    private static final String NODE_PHOTOS = "Fotos";

    private TextView textViewLogout;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<Foto> fotoArrayListFavoritos = new ArrayList<>();

    private RecyclerAdapter recyclerAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_favoritos);

        recyclerView = findViewById(R.id.recyclerViewFav);
        textViewLogout = findViewById(R.id.textViewLogout);
        searchView = findViewById(R.id.searchViewFav);
        searchView.setIconified(false);
        searchView.clearFocus();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        setInfo();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                filterRecyclerView(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterRecyclerView(s);
                return true;
            }
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(UsuarioFavoritos.this, "Logout", Toast.LENGTH_LONG).show();
        startActivity(new Intent(UsuarioFavoritos.this, MainActivity.class));
    }

    private void setInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userPhotosRef = databaseReference.child(user.getUid()).child(NODE_PHOTOS);
            userPhotosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        fotoArrayListFavoritos.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Foto f = snapshot.getValue(Foto.class);
                            fotoArrayListFavoritos.add(f);
                        }
                        setRecyclerView();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void setRecyclerView() {
        recyclerAdapter = new RecyclerAdapter(fotoArrayListFavoritos);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void filterRecyclerView(String searchText) {
        recyclerAdapter.filtrar(searchText);
        recyclerAdapter.notifyDataSetChanged();
    }
}
