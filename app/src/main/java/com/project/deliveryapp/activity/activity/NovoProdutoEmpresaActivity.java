package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private ImageView imgVoltar;
    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private Button botaoProdutoSalvar;
    private FirebaseFirestore firestore;
    private String usuarioId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_novo_produto_empresa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializacaoComponentes();
        firestore = FirebaseConfig.getFirestore();
        usuarioId = FirebaseConfig.getIdUsuario();

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornaTelaHome();
            }
        });

        botaoProdutoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarProduto();
            }
        });
    }


    private void inicializacaoComponentes() {
        imgVoltar = (ImageView) findViewById(R.id.imageVoltar);
        editProdutoNome = (EditText) findViewById(R.id.editProdutoNome);
        editProdutoDescricao = (EditText) findViewById(R.id.editProdutoDescricao);
        editProdutoPreco = (EditText) findViewById(R.id.editProdutoPreco);
        botaoProdutoSalvar = (Button) findViewById(R.id.btSalvarProduto);
    }

    private void validarProduto() {

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if (!nome.isEmpty() && !descricao.isEmpty() && !preco.isEmpty()) {
            Produto produto = new Produto(usuarioId, nome, descricao, Double.parseDouble(preco));
            salvarProduto(produto);
            exibirMensagem("Produto cadastrado com sucesso");
            finish();
        } else {
            exibirMensagem("Todos os campos são obrigatórios");
        }
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this,
                texto, Toast.LENGTH_SHORT).show();
    }

    private void salvarProduto(Produto produto) {

        firestore.collection("produto")
                .add(produto).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DB", "Produto cadastrado com sucesso:  " + documentReference.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DB", "Error ao salvar os dados " + e.getMessage());
                    }
                });
    }

    private void retornaTelaHome() {
        startActivity(new Intent(NovoProdutoEmpresaActivity.this, EmpresaActivity.class));
        finish();
    }
}