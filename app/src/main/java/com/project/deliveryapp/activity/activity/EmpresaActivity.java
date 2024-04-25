package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterProduto;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Produto;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView imglogout, imgAdd, imgConfig;
    private AdapterProduto adapterProduto;
    private String usuarioId = null;
    private List<Produto> produtos = new ArrayList<>();
    private RecyclerView recyclerProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empresa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioId = FirebaseConfig.getIdUsuario();
        firestore = FirebaseConfig.getFirestore();
        firebaseAuth = FirebaseConfig.getAuth();
        inicializarComponenetes();

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deslogarUsuario();
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirNovoProduto();
            }
        });

        imgConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirConfiguracoes();
            }
        });

        initRecyclerView();
        recuperarProdutos();
    }

    private void inicializarComponenetes() {
        imglogout = (ImageView) findViewById(R.id.imgLogout);
        imgAdd = (ImageView) findViewById(R.id.imageAdd);
        imgConfig = (ImageView) findViewById(R.id.imgConfig);
        recyclerProdutos = (RecyclerView) findViewById(R.id.recyclerProdutos);
    }

    private void deslogarUsuario() {
        try {
            firebaseAuth.signOut();
            startActivity(new Intent(EmpresaActivity.this, AutenticacaoActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerProdutos.setLayoutManager(layoutManager);
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);
    }
    private void recuperarProdutos() {
        firestore.collection("produto").whereEqualTo("usuarioId", usuarioId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        produtos.clear();
                        for (QueryDocumentSnapshot query : task.getResult()) {
                            Produto produto = new Produto();
                            produto.setUsuarioId(query.get("usuarioId").toString());
                            produto.setNome(query.get("nome").toString());
                            produto.setDescricao(query.get("descricao").toString());
                            String price = query.get("price").toString();
                            produto.setPrice(Double.parseDouble(price));
                            produtos.add(produto);
                        }
                        adapterProduto.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DB", "Erro na consulta de produtos: " + e.getMessage());
                    }
                });
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(EmpresaActivity.this, ConfiguracaoEmpresaActivity.class));
    }

    private void abrirNovoProduto() {
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
}