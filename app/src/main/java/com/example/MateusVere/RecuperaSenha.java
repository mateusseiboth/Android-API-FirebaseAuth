package com.example.MateusVere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperaSenha extends AppCompatActivity {

    private EditText editTextRecSenha;
    private Button buttonRecSenha;
    private FirebaseAuth mAuthRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_senha);

        editTextRecSenha = findViewById(R.id.editRecSenha);
        buttonRecSenha = findViewById(R.id.buttonRecSenha);

        mAuthRec = FirebaseAuth.getInstance();

        buttonRecSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperaSenha();
            }
        });
    }

    private void recuperaSenha() {
        String email = editTextRecSenha.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
            editTextRecSenha.setError("Preencha corretamente");
            return;
        }

        mAuthRec.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RecuperaSenha.this, "E-mail enviado. Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RecuperaSenha.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Fechar a atividade para evitar que o usuário retorne
                } else {
                    Toast.makeText(RecuperaSenha.this, "Erro ao enviar o e-mail de recuperação. Verifique o endereço de e-mail.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
