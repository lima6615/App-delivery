package com.project.deliveryapp.activity.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterPedido;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Pedido;
import com.project.deliveryapp.activity.listener.RecyclerItemClickListener;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidosActivity extends AppCompatActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ImageView imgVoltaPedido;
    private RecyclerView recyclerPedidos;
    private FirebaseFirestore firestore;
    private AdapterPedido adapterPedido;
    private String idUsuario = "";
    private String idPedido = "";
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

        recyclerPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerPedidos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                String data = sdf.format(new Date());
                                Pedido pedido = pedidos.get(position);
                                pedido.setStatus("Finalizado");
                                pedido.setBaixaPedido(true);
                                pedido.setData(data);
                                finalizarPedido(pedido);
                                pedidos.clear();
                                showToast("Pedido Finalizado com sucesso");
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }));

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

    private void showToast(String message) {
        ViewGroup view = findViewById(R.id.container_toast);
        View v = getLayoutInflater().inflate(R.layout.custom_toast, view);

        TextView txtMessage = v.findViewById(R.id.textMessagem);
        txtMessage.setText(message);

        Toast toast = new Toast(this);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void finalizarPedido(Pedido pedido) {

        firestore.collection("historicoPedidos")
                .add(pedido).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("DbPedido", "Sucesso ao Salvar Pedido: " + task.getResult());
                            deletarPedido();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DB", "Falha ao salvo dados do pediso: " + e.getMessage());
                    }
                });
    }

    private void deletarPedido() {
        firestore.collection("pedido")
                .document(idPedido)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("PedidoDelete", "Pedido Deletado: " + idPedido);
                    }
                });
    }

    private void recuperarPedidos() {

        firestore.collection("pedido")
                .whereEqualTo("idEmpresa", idUsuario)
                .whereEqualTo("baixaPedido", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            pedidos.clear();
                            for (DocumentSnapshot query : value) {
                                Log.d("ListaPedido", "lista de pedidos " + query.getData());
                                idPedido = query.getId();
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