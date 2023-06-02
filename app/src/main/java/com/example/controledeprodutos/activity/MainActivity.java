package com.example.controledeprodutos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.controledeprodutos.adapter.AdapterProduto;
import com.example.controledeprodutos.autenticacao.LoginActivity;
import com.example.controledeprodutos.helper.FirebaseHelper;
import com.example.controledeprodutos.model.Produto;
import com.example.controledeprodutos.ProdutoDAO;
import com.example.controledeprodutos.R;
import com.example.controledeprodutos.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterProduto.Onclick {

    private AdapterProduto adapterProduto;
    private List<Produto> produtoList = new ArrayList<>();
    private ActivityMainBinding binding;
    private SwipeableRecyclerView rvProdutos;
    private ProgressBar progressBar;

    private TextView textInfo;

    private ImageButton ib_add;
    private ImageButton ib_more;

//    private ProdutoDAO produtoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

//        produtoDAO = new ProdutoDAO(this);
//       produtoList = produtoDAO.getListProdutos();

        configRecyclerView();
        initClickListeners();
    }

    private void configRecyclerView() {

        recuperaProdutos();

        rvProdutos.setLayoutManager(new LinearLayoutManager(this));
        rvProdutos.setHasFixedSize(true);

        adapterProduto = new AdapterProduto(produtoList, this);
        rvProdutos.setAdapter(adapterProduto);

        rvProdutos.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                Produto produto = produtoList.get(position);
                abrirActivityEdicao(produto);
            }

            @Override
            public void onSwipedRight(int position) {
                Produto produto = produtoList.get(position);
//                produtoDAO.deleteProduto(produto);
                produtoList.remove(produto);
                produto.deletaProduto();
                adapterProduto.notifyItemRemoved(position);

                verificaQtdLista();
            }
        });
    }

    private void initViews(){
        ib_add = binding.include.btnAdd;
        ib_more = binding.include.btnMore;
        textInfo = binding.textInfo;
        progressBar = binding.progressList;
        rvProdutos = binding.rvProdutos;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recuperaProdutos();
    }

    private void verificaQtdLista() {
        progressBar.setVisibility(View.VISIBLE);
        textInfo.setVisibility(View.VISIBLE);

        if (produtoList.size() == 0) {
            textInfo.setText("Nenhum Produto Encontrado.");
            textInfo.setVisibility(View.VISIBLE);
        } else {
            textInfo.setVisibility(View.GONE);
        }

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClickListener(Produto produto) {
        abrirActivityEdicao(produto);
    }

    private void abrirActivityEdicao(Produto produto){
        Intent intent = new Intent(this, FormProdutoActivity.class);
        intent.putExtra("produto", produto);
        startActivity(intent);
    }

    private void recuperaProdutos(){
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase());
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtoList.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    Produto produto = snap.getValue(Produto.class);
                    produtoList.add(produto);
                }
                verificaQtdLista();
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initClickListeners() {
        ib_add.setOnClickListener(view -> {
            startActivity(new Intent(this, FormProdutoActivity.class));
        });

        ib_more.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, ib_more);
            popupMenu.getMenuInflater().inflate(R.menu.menu_toolbar, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_more) {
                    Snackbar.make(binding.getRoot(), "More", Snackbar.LENGTH_SHORT).show();

                } else if (menuItem.getItemId() == R.id.menu_sair) {
                    FirebaseHelper.getAuth().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
            });

            popupMenu.show();
        });
    }
}