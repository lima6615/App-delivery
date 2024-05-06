package com.project.deliveryapp.activity.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterPedido;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Pedido;

import java.util.ArrayList;
import java.util.List;

public class PedidosActivity extends AppCompatActivity {

    private ImageView imgVoltaPedido;
    private RecyclerView recyclerPedidos;
    private FirebaseFirestore firestore;
    private AdapterPedido adapterPedido;
    private String idUsuario = "";
    private List<Pedido> pedidos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pedidos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseConfig.getFirestore();
        idUsuario = FirebaseConfig.getIdUsuario();
        inicializacaoComponentes();
        initRecyclerView();

        imgVoltaPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recuperarPedidos();
    }

    private void inicializacaoComponentes() {
        imgVoltaPedido = (ImageView) findViewById(R.id.imgVoltarPedido);
        recyclerPedidos = (RecyclerView) findViewById(R.id.recyclerPedidos);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerPedidos.setLayoutManager(layoutManager);
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);
    }

    private void recuperarPedidos() {

        firestore.collection("pedido")
                .whereEqualTo("baixaPedido", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot query : value) {
                                Log.d("ListaPedido", "lista de pedidos " + query.get("itens"));
                                Pedido pedido = query.toObject(Pedido.class);
                                pedidos.add(pedido);
                            }
                            adapterPedido.notifyDataSetChanged();
                        } else {
                            pedidos.clear();
                            adapterPedido.notifyDataSetChanged();
                        }
                    }
                });
    }
}