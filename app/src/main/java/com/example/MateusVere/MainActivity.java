package com.example.MateusVere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_EMAIL_RECOVERY = "extra_email_recovery";
    private static final String EXTRA_EMAIL_CREATE = "extra_email_create";

    private EditText editTextEmail, editTextSenha;
    private TextView textViewRecSenha, textViewCriaUsuario;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        textViewCriaUsuario = findViewById(R.id.textViewCriaUsuario);
        textViewRecSenha = findViewById(R.id.textViewEsqueciSenha);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        textViewCriaUsuario.setOnClickListener(this);
        textViewRecSenha.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        // Barra de progresso
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                logar();
                break;
            case R.id.textViewEsqueciSenha:
                intent = new Intent(MainActivity.this, RecuperaSenha.class);
                intent.putExtra(EXTRA_EMAIL_RECOVERY, editTextEmail.getText().toString());
                startActivity(intent);
                break;
            case R.id.textViewCriaUsuario:
                intent = new Intent(MainActivity.this, CriaUsuario.class);
                intent.putExtra(EXTRA_EMAIL_CREATE, editTextEmail.getText().toString());
                startActivity(intent);
                break;
        }
    }

    private void logar() {
        String email = editTextEmail.getText().toString();
        String senha = editTextSenha.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Preencha corretamente");
            editTextEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            editTextSenha.setError("Preencha corretamente");
            editTextSenha.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (mAuth.getCurrentUser() != null ) {
                        intent = new Intent(MainActivity.this, UsuarioLogado.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Email não verificado", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao logar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
