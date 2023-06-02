package com.example.controledeprodutos.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.controledeprodutos.helper.FirebaseHelper;
import com.example.controledeprodutos.model.Produto;
import com.example.controledeprodutos.ProdutoDAO;
import com.example.controledeprodutos.databinding.ActivityFormProdutoBinding;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class FormProdutoActivity extends AppCompatActivity {

    private static final int REQUEST_GALERIA = 100;

    private ActivityFormProdutoBinding binding;
    private EditText edt_produto, edt_quantidade, edt_valor;
    private ImageView img_produto;
    private String caminhoImagem;
    private Bitmap imagem;

   // private ProdutoDAO produtoDAO;
    private ImageButton ib_back;
    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    //    produtoDAO = new ProdutoDAO(this);

        iniciarComponentes();
        ib_back.setOnClickListener(view -> finish());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            produto = (Produto) bundle.getSerializable("produto");
            editProduto();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    public void verificaPermissaoGaleria(View view){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormProdutoActivity.this, "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissão(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private void showDialogPermissão(PermissionListener permissionListener, String[] permissoes){
        TedPermission.create().setPermissionListener(permissionListener)
                .setDeniedTitle("Permissões")
                .setDeniedMessage("Você negou a permissão para acessar a galeria do dispositivo, deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void iniciarComponentes() {
        edt_produto = binding.editProduto;
        edt_quantidade = binding.editQuantidade;
        edt_valor = binding.editValor;
        ib_back = binding.include2.ibVoltar;
        img_produto = binding.imgProduto;
    }

    public void editProduto(){
        edt_produto.setText(produto.getNome());
        edt_quantidade.setText(String.valueOf(produto.getEstoque()));
        edt_valor.setText(String.valueOf(produto.getValor()));
        Picasso.get().load(produto.getUrlImagem()).into(img_produto);
    }

    public void salvarProduto(View view) {

        String nome = edt_produto.getText().toString();
        String quantidade = edt_quantidade.getText().toString();
        String valor = edt_valor.getText().toString();

        if (!nome.isEmpty()) {
            if (!quantidade.isEmpty()) {

                int qtd = Integer.parseInt(quantidade);
                if (qtd >= 1) {

                    if (!valor.isEmpty()) {
                        double valorProduto = Double.parseDouble(valor);
                        if (valorProduto > 0) {

                            if (produto == null)produto = new Produto();
                            produto.setNome(nome);
                            produto.setEstoque(qtd);
                            produto.setValor(valorProduto);

                            if (caminhoImagem == null && produto.getUrlImagem() == null)
                                Toast.makeText(this, "Selecione uma imagem", Toast.LENGTH_SHORT).show();
                            else if(caminhoImagem != null){
                                salvarImagemProduto();
                                finish();

                            } else {
                                produto.salvarProduto();
                                finish();
                            }

//                            if (produto.getId().equals("0")){
//                                produtoDAO.atualizaPorduto(produto);
//                            } else {
//                                produtoDAO.salvarProduto(produto);
//                            }

                        } else {
                            edt_valor.requestFocus();
                            edt_valor.setError("Informe um valor maior que 0.");
                        }
                    } else {
                        edt_valor.requestFocus();
                        edt_valor.setError("Informe o valor do produto.");
                    }
                } else {
                    edt_quantidade.requestFocus();
                    edt_quantidade.setError("Informe um valor maior que 0.");
                }
            } else {
                edt_quantidade.requestFocus();
                edt_quantidade.setError("Informe a quantidade do produto");
            }

        } else {
            edt_produto.requestFocus();
            edt_produto.setError("Informe o nome do produto");
        }
    }

    public void salvarImagemProduto(){
        StorageReference reference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(produto.getId());

        UploadTask uploadTask = reference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnCompleteListener(task -> {
            produto.setUrlImagem(task.getResult().toString());
            produto.salvarProduto();
            finish();

        })).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GALERIA){
                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                if (Build.VERSION.SDK_INT < 28){
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                    try {
                        imagem = ImageDecoder.decodeBitmap(source);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            img_produto.setImageBitmap(imagem);
        }
    }
}