package com.example.controledeprodutos.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.controledeprodutos.R;
import com.example.controledeprodutos.databinding.ActivityRecuperarContaBinding;
import com.example.controledeprodutos.helper.FirebaseHelper;
import com.google.android.material.snackbar.Snackbar;

public class RecuperarContaActivity extends AppCompatActivity {

    ActivityRecuperarContaBinding binding;

    private ProgressBar progressBar;
    private EditText edit_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();
    }

    public void recuperarSenha(View view) {
        String email = edit_email.getText().toString().trim();

        if (!email.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            enviaEmail(email);
        } else {
            edit_email.requestFocus();
            edit_email.setError("Informe seu E-mail.");
        }
    }

    private void enviaEmail(String email) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "E-mail enviado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String error = task.getException().getMessage();
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void iniciaComponentes() {
        progressBar = binding.progressBar;
        edit_email = binding.editEmail;
    }
}