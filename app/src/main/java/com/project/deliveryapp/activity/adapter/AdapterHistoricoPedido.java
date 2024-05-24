package com.project.deliveryapp.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.entities.Endereco;
import com.project.deliveryapp.activity.entities.ItemPedido;
import com.project.deliveryapp.activity.entities.Pedido;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterHistoricoPedido extends RecyclerView.Adapter<AdapterHistoricoPedido.MyViewHolder> {

    private List<Pedido> Historicopedidos;

    public AdapterHistoricoPedido(List<Pedido> Historicopedidos) {
        this.Historicopedidos = Historicopedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_historico_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DecimalFormat df = new DecimalFormat("0.00");
        Pedido pedido = Historicopedidos.get(position);
        holder.nomeEmpresa.setText(pedido.getNome());
        holder.data.setText("Data: " + pedido.getData());
        holder.metodoPagamentoPedido.setText("Pgto: " + pedido.getMetodoPagamento());
        holder.taxa.setText("Taxa: R$ " + df.format(pedido.getTaxa()));
        holder.valorTotal.setText("ValorTotal: R$ " + df.format(pedido.getValorTotal()));
        holder.status.setText("Status: " + pedido.getStatus());

        Endereco endereco = pedido.getEndereco();
        String enderecoUsuario = "Endereço: " + endereco.getBairro() + " - " + endereco.getCidade() + " , Rua: "
                + endereco.getRua();
        holder.enderecoUsuario.setText(enderecoUsuario);

        List<ItemPedido> itens = pedido.getItens();
        String descricaoItens = "";

        double somar = 0.0;
        int numeroItem = 1;
        for (ItemPedido itemPedido : itens) {

            if (itemPedido.equals(holder.itensPedidos.getText())) {
                itens.remove(itemPedido);
            } else {
                String nome = itemPedido.getNomeProduto();
                descricaoItens += numeroItem + ") " + nome + " / (" + itemPedido.getQuantidade() + " x R$ " + df.format(itemPedido.getPreco()) + ") \n";
                somar += itemPedido.subTotal() + pedido.getTaxa();
                numeroItem++;
            }
        }
        //descricaoItens += "Total: R$ " + df.format(somar) + " (C/Taxa)" + "\n";
        holder.itensPedidos.setText(descricaoItens);
    }

    @Override
    public int getItemCount() {
        return Historicopedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomeEmpresa;
        TextView enderecoUsuario;
        TextView data;
        TextView metodoPagamentoPedido;
        TextView taxa;
        TextView valorTotal;
        TextView status;
        TextView itensPedidos;

        public MyViewHolder(View itemView) {
            super(itemView);
            nomeEmpresa = itemView.findViewById(R.id.textHistoricoNomeEmpresa);
            enderecoUsuario = itemView.findViewById(R.id.textHistoricoEndereco);
            data = itemView.findViewById(R.id.textHistoricoData);
            metodoPagamentoPedido = itemView.findViewById(R.id.textHistoricoPgto);
            taxa = itemView.findViewById(R.id.textHistoricoTaxa);
            valorTotal = itemView.findViewById(R.id.textHistoricoValorTotal);
            status = itemView.findViewById(R.id.textHistoricoStatus);
            itensPedidos = itemView.findViewById(R.id.textHistoricoPedidoItens);
        }
    }
}
