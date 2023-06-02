package com.example.controledeprodutos.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.controledeprodutos.R;
import com.example.controledeprodutos.activity.MainActivity;
import com.example.controledeprodutos.databinding.ActivityLoginBinding;
import com.example.controledeprodutos.helper.FirebaseHelper;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    TextView txtCriarConta;
    TextView txtRecuperarConta;
    ProgressBar progressBar;

    private EditText edit_email, edit_senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        iniciarComponentes();
        configCliques();
    }

    private void iniciarComponentes() {
        txtCriarConta = binding.txtCriarConta;
        txtRecuperarConta = binding.txtRecConta;
        edit_email = binding.editEmail;
        edit_senha = binding.editSenha;
        progressBar = binding.progressBar;

    }

    public void logar(View view) {
        String email = edit_email.getText().toString().trim();
        String senha = edit_senha.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                validaLogin(email, senha);
            } else {
                edit_senha.requestFocus();
                edit_senha.setError("Informe sua Senha");
            }
        } else {
            edit_email.requestFocus();
            edit_email.setError("Informe seu E-mail");
        }
    }

    private void validaLogin(String email, String senha) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                finish();
                startActivity(new Intent(this, MainActivity.class));

            } else {
                progressBar.setVisibility(View.GONE);
                String error = task.getException().getMessage();
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void configCliques() {
        txtCriarConta.setOnClickListener(view ->
                startActivity(new Intent(this, CriarContaActivity.class)));

        txtRecuperarConta.setOnClickListener(view ->
                startActivity(new Intent(this, RecuperarContaActivity.class)));
    }
}