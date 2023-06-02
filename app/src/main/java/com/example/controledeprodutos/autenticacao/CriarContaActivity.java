package com.example.controledeprodutos.autenticacao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.controledeprodutos.R;
import com.example.controledeprodutos.activity.MainActivity;
import com.example.controledeprodutos.databinding.ActivityCriarContaBinding;
import com.example.controledeprodutos.helper.FirebaseHelper;
import com.example.controledeprodutos.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;

public class CriarContaActivity extends AppCompatActivity {

    ActivityCriarContaBinding binding;

    private EditText edit_nome, edit_email, edit_senha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();
        configCliques();
    }

    private void configCliques() {
        binding.include3.ibVoltar.setOnClickListener(view -> finish());
    }

    public void validaDados(View view) {
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);

                    Usuario usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);

                    salvarCadastro(usuario);
                } else {
                    edit_senha.requestFocus();
                    edit_senha.setError("Informe sua senha");
                }
            } else {
                edit_email.requestFocus();
                edit_email.setError("Informe seu e-mail");
            }
        } else {
            edit_nome.requestFocus();
            edit_nome.setError("Informe seu nome");
        }
    }

    private void salvarCadastro(Usuario usuario){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String id = task.getResult().getUser().getUid();
                usuario.setId(id);

                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        });
    }

    private void iniciarComponentes() {
        edit_nome = binding.editNome;
        edit_email = binding.editEmail;
        edit_senha = binding.editSenha;
        progressBar = binding.progressBar;

        TextView txt_titulo = binding.include3.txtTitulo;
        txt_titulo.setText("Criar Conta");
    }
}