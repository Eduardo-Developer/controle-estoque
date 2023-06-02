package com.example.controledeprodutos.adapter;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.controledeprodutos.R;
import com.example.controledeprodutos.model.Produto;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    private List<Produto> produtoList;
    private Onclick onClick;
    public static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public AdapterProduto(List<Produto> produtoList, Onclick onClick) {
        this.produtoList = produtoList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);

        holder.tv_produto.setText(produto.getNome());
        holder.tv_estoque.setText("Estoque: " + produto.getEstoque());
        holder.tv_valor.setText(currencyFormat.format(produto.getValor()));
        Picasso.get().load(produto.getUrlImagem()).into(holder.img_produto);

        holder.itemView.setOnClickListener(view -> onClick.onClickListener(produto));
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public interface Onclick{
        void onClickListener(Produto produto);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_produto, tv_estoque, tv_valor;
        ImageView img_produto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_produto = itemView.findViewById(R.id.tv_nome_produto);
            tv_estoque = itemView.findViewById(R.id.tv_estoque);
            tv_valor = itemView.findViewById(R.id.tv_valor);
            img_produto = itemView.findViewById(R.id.img_produto);

        }
    }
}
