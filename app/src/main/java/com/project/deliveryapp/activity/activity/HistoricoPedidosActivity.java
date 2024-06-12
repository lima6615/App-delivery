package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterHistoricoPedido;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Empresa;
import com.project.deliveryapp.activity.entities.Pedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricoPedidosActivity extends AppCompatActivity {

    private EditText editTextDate;
    private ImageView imgVoltarHistorico;
    private RecyclerView recyclerHistorico;
    private String idUsuario = "";
    private Empresa empresa = new Empresa();
    private FirebaseFirestore firestore;
    private AdapterHistoricoPedido adapterHistoricoPedido;
    private List<Pedido> historicoPedidos = new ArrayList<>();
    private Map<String, Empresa> empresaMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico_pedidos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseConfig.getFirestore();
        idUsuario = FirebaseConfig.getIdUsuario();
        inicializandoComponentes();
        initRecyclerView();

        imgVoltarHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaHomeCliente();
            }
        });
        recuperarHistoricoPedidos();

        editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<Pedido> historicoCopia = new ArrayList<>();
                String date = editable.toString();

                for(Pedido ped : historicoPedidos){
                    if(ped.getData().contains(date)){
                        historicoCopia.add(ped);
                        adapterHistoricoPedido = new AdapterHistoricoPedido(historicoCopia);
                        recyclerHistorico.setAdapter(adapterHistoricoPedido);
                    }
                }
            }
        });
    }

    private void inicializandoComponentes() {
        imgVoltarHistorico = (ImageView) findViewById(R.id.imgVoltarHistoricoPedido);
        recyclerHistorico = (RecyclerView) findViewById(R.id.recyclerHistorico);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerHistorico.setLayoutManager(layoutManager);
        recyclerHistorico.setHasFixedSize(true);
        adapterHistoricoPedido = new AdapterHistoricoPedido(historicoPedidos);
        recyclerHistorico.setAdapter(adapterHistoricoPedido);
    }

    private void abrirTelaHomeCliente() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void recuperarHistoricoPedidos() {

        firestore.collection("historicoPedidos")
                .whereEqualTo("idUsuario", idUsuario)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Error", "Error ao buscar dados da tabela historicoPedidos", error);
                            return;
                        }

                        if (value != null && !value.isEmpty()) {

                            historicoPedidos.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Pedido pedido = document.toObject(Pedido.class);
                                pedido.setNome("");
                                if (pedido != null) {
                                    String idEmpresa = pedido.getIdEmpresa();
                                    if (empresaMap.containsKey(idEmpresa)) {
                                        Empresa empresa = empresaMap.get(idEmpresa);
                                        pedido.setNome(empresa.getNome());
                                    } else {
                                        firestore.collection("empresa").whereEqualTo("idUsuario", idEmpresa).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                            for (QueryDocumentSnapshot query : task.getResult()) {
                                                                Empresa empresa = query.toObject(Empresa.class);
                                                                if (empresa != null) {
                                                                    empresaMap.put(idEmpresa, empresa);
                                                                    pedido.setNome(empresa.getNome());
                                                                    adapterHistoricoPedido.notifyDataSetChanged();
                                                                }
                                                            }
                                                        } else {
                                                            Log.e("DbEmpresa", "Error ao recuperar dados da empresa ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                    historicoPedidos.add(pedido);
                                }
                            }
                            adapterHistoricoPedido.notifyDataSetChanged();
                        } else {
                            historicoPedidos.clear();
                            adapterHistoricoPedido.notifyDataSetChanged();
                        }
                    }
                });
    }
}