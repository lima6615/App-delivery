package com.project.deliveryapp.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Endereco;
import com.project.deliveryapp.activity.entities.ItemPedido;
import com.project.deliveryapp.activity.entities.Pedido;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {
    private FirebaseFirestore firestore = FirebaseConfig.getFirestore();
    private List<Pedido> pedidos;

    public AdapterPedido(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DecimalFormat df = new DecimalFormat("0.00");
        Pedido pedido = pedidos.get(position);
        holder.nomeUsuario.setText(pedido.getNome());
        holder.metodoPagamento.setText("Pgto: " + pedido.getMetodoPagamento());
        holder.taxa.setText("Taxa: R$ " + df.format(pedido.getTaxa()));
        holder.observacao.setText("Obs: " + pedido.getObservacao());

        Endereco endereco = pedido.getEndereco();
        String enderecoUsuario = "Endereço: " + endereco.getBairro() + " - " + endereco.getCidade() + " , Rua: "
                + endereco.getRua();
        holder.endereco.setText(enderecoUsuario);

        List<ItemPedido> itens = pedido.getItens();
        String descricaoItens = "";

        double somar = 0.0;
        int numeroItem = 1;
        for (ItemPedido itemPedido : itens) {

            if (itemPedido.equals(holder.itemPedido.getText())) {
                itens.remove(itemPedido);
            } else {
                String nome = itemPedido.getNomeProduto();
                descricaoItens += numeroItem + ") " + nome + " / (" + itemPedido.getQuantidade() + " x R$ " + df.format(itemPedido.getPreco()) + ") \n";
                somar += itemPedido.subTotal() + pedido.getTaxa();
                numeroItem++;
            }
        }
        descricaoItens += "Total: R$ " + df.format(somar) + " (C/Taxa)" + "\n";
        holder.itemPedido.setText(descricaoItens);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomeUsuario;
        TextView endereco;
        TextView metodoPagamento;
        TextView taxa;
        TextView observacao;
        TextView itemPedido;

        public MyViewHolder(View itemView) {
            super(itemView);
            nomeUsuario = itemView.findViewById(R.id.textPedidoNome);
            endereco = itemView.findViewById(R.id.textPedidoEndereco);
            metodoPagamento = itemView.findViewById(R.id.textPedidoPgto);
            taxa = itemView.findViewById(R.id.textPedidoTaxa);
            observacao = itemView.findViewById(R.id.textPedidoObs);
            itemPedido = itemView.findViewById(R.id.textPedidoItens);
        }
    }
}
