package com.example.MateusVere;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UsuarioLogado extends AppCompatActivity {
    Button buttonFav_;
    SearchView searchView;
    RecyclerView recyclerView;
    ArrayList<Foto> fotoArrayList = new ArrayList<>();
    Handler handler = new Handler();
    RecyclerAdapter recyclerAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_logado);

        recyclerView = findViewById(R.id.recyclerView);
        buttonFav_ = findViewById(R.id.buttonFav);
        searchView = findViewById(R.id.searchView1);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        searchView.setIconified(false);
        searchView.clearFocus();

        try {
            setInfo("");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        setRecyclerView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    setInfo(s);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                recyclerAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    try {
                        setInfo(s);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    recyclerAdapter.notifyDataSetChanged();
                }, 400);
                return true;
            }
        });

        buttonFav_.setOnClickListener(view -> {
            Intent i = new Intent(UsuarioLogado.this, UsuarioFavoritos.class);
            startActivity(i);
        });
    }

    private void setInfo(String busca) throws ExecutionException, InterruptedException {
        String buscaCate = "";

        if (busca.trim().equals("")) {
            buscaCate = "curated";
        } else {
            buscaCate = "search?query=" + busca;
        }

        fotoArrayList.clear();
        Log.e("URL", buscaCate);
        DownloadDados downloadDados = new DownloadDados();
        downloadDados.run("https://api.pexels.com/v1/" + buscaCate, (url, dados) -> {
            fotoArrayList.addAll(processarDados(dados));
            runOnUiThread(() -> recyclerAdapter.notifyDataSetChanged());

        });
    }

    private void setRecyclerView() {
        recyclerAdapter = new RecyclerAdapter(fotoArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
    private List<Foto> processarDados(String dados) {
        List<Foto> fotos = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(dados);
            JSONArray photosArray = json.getJSONArray("photos");

            for (int i = 0; i < photosArray.length(); i++) {
                JSONObject photoObject = photosArray.getJSONObject(i);

                String autor = photoObject.getString("photographer");
                String imagemSmall = photoObject.getJSONObject("src").getString("small");
                String urlAutor = photoObject.getString("photographer_url");

                Foto foto = new Foto(autor, urlAutor, imagemSmall);
                fotos.add(foto);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fotos;
    }
}