package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterProduto;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Produto;
import com.project.deliveryapp.activity.listener.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView imglogout, imgAdd, imgConfig;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private RecyclerView recyclerProdutos;
    private String usuarioId = null;

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

        recyclerProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Produto produto = produtos.get(position);
                        deleteProduto(produto);
                        Toast.makeText(EmpresaActivity.this, "Produto deletado com sucesso",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                }));
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

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerProdutos.setLayoutManager(layoutManager);
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);
    }

    private void recuperarProdutos() {

        firestore.collection("produto")
                .whereEqualTo("usuarioId", usuarioId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            produtos.clear();
                            for (DocumentSnapshot query : value.getDocuments()) {
                                Produto produto = new Produto();
                                //produto.setId(query.getId());
                                produto.setUsuarioId(query.getId().toString());
                                produto.setNome(query.get("nome").toString());
                                produto.setDescricao(query.get("descricao").toString());
                                produto.setPrice(query.get("price").toString());
                                produtos.add(produto);
                            }
                            adapterProduto.notifyDataSetChanged();
                        } else {
                            produtos.clear();
                            adapterProduto.notifyDataSetChanged();
                        }
                    }
                });
    }


    private void deleteProduto(Produto produto) {
        DocumentReference reference = firestore.collection("produto")
                .document(produto.getUsuarioId());
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DB", "Produto Deletado.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error", "Error ao deletar produto: " + e.getMessage());
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