package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.project.deliveryapp.activity.adapter.AdapterProduto;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Empresa;
import com.project.deliveryapp.activity.entities.Produto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    private ImageView imgVoltaCardapio;
    private ImageView imgEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, textCategoriaEmpresaCardapio, textTempoEmpresaCardapio, textEntregaEmpresaCardapio;
    private RecyclerView recyclerProdutosCardapio;
    private Empresa empresaSelecionado;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cardapio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseConfig.getFirestore();
        inicializacaoComponentes();

        imgVoltaCardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornaTelaHome();
            }
        });

        //Recuperar Empresa Selecionada
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            empresaSelecionado = (Empresa) bundle.getSerializable("empresa");
            textNomeEmpresaCardapio.setText(empresaSelecionado.getNome());
            textCategoriaEmpresaCardapio.setText(empresaSelecionado.getCategoria());
            textTempoEmpresaCardapio.setText(empresaSelecionado.getTempo() + " min R$");
            textEntregaEmpresaCardapio.setText(empresaSelecionado.getTaxa());
            String urlImagem = empresaSelecionado.getUrlImagem();
            Picasso.get().load(urlImagem).into(imgEmpresaCardapio);
        }

        initRecyclerView();
        recuperarProdutos();
    }

    private void inicializacaoComponentes() {
        imgVoltaCardapio = (ImageView) findViewById(R.id.imageVoltarCardapio);
        imgEmpresaCardapio = (ImageView) findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = (TextView) findViewById(R.id.textNomeEmpresaCardapio);
        textCategoriaEmpresaCardapio = (TextView) findViewById(R.id.textCategoriaEmpresaCardapio);
        textTempoEmpresaCardapio = (TextView) findViewById(R.id.textTempoEmpresaCardapio);
        textEntregaEmpresaCardapio = (TextView) findViewById(R.id.textEntregaEmpresaCardapio);
        recyclerProdutosCardapio = (RecyclerView) findViewById(R.id.recyclerProdutosCardapio);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerProdutosCardapio.setLayoutManager(layoutManager);
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);
    }

    private void recuperarProdutos() {

        firestore.collection("produto")
                .whereEqualTo("usuarioId", empresaSelecionado.getIdUsuario())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            produtos.clear();
                            for (DocumentSnapshot query : value.getDocuments()) {
                                Produto produto = new Produto();
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

    private void retornaTelaHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}