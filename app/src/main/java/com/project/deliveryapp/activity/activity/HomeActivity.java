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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterEmpresa;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Empresa;
import com.project.deliveryapp.activity.listener.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ImageView imgLogoutHome, imgConfigHome;
    private RecyclerView recyclerEmpresas;
    private List<Empresa> empresas = new ArrayList<>();
    private AdapterEmpresa adapterEmpresa;
    private String idUsuario = null;
    private boolean verificarEndereco = false;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseConfig.getFirestore();
        firebaseAuth = FirebaseConfig.getAuth();
        idUsuario = FirebaseConfig.getIdUsuario();
        inicializacaoComponentes();
        initRecyclerView();
        imgLogoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deslogarUsuario();
            }
        });

        imgConfigHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaConfiguracaoUsuario();
            }
        });

        recuperarEmpresas();
        recuperarEnderecoUsuario();

        recyclerEmpresas.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerEmpresas, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (verificarEndereco) {
                            Empresa empresaSelecionada = empresas.get(position);
                            Intent intent = new Intent(HomeActivity.this, CardapioActivity.class);
                            intent.putExtra("empresa", empresaSelecionada);
                            startActivity(intent);
                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "Cadastre um endereço para efetuação de pedidos", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                })
        );
    }

    private void inicializacaoComponentes() {
        imgLogoutHome = (ImageView) findViewById(R.id.imgLogoutHome);
        imgConfigHome = (ImageView) findViewById(R.id.imgConfigHome);
        recyclerEmpresas = (RecyclerView) findViewById(R.id.recyclerEmpresa);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerEmpresas.setLayoutManager(layoutManager);
        recyclerEmpresas.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresas.setAdapter(adapterEmpresa);
    }

    private void deslogarUsuario() {
        try {
            firebaseAuth.signOut();
            startActivity(new Intent(HomeActivity.this, AutenticacaoActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recuperarEmpresas() {

        firestore.collection("empresa").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("DB", "Dados de empresa: " + task.getResult());
                            empresas.clear();
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Empresa empresa = new Empresa();
                                empresa.setIdUsuario(documentSnapshot.get("idUsuario").toString());
                                empresa.setNome(documentSnapshot.get("nome").toString());
                                empresa.setCategoria(documentSnapshot.get("categoria").toString());
                                empresa.setTaxa(documentSnapshot.get("taxa").toString());
                                empresa.setTempo(documentSnapshot.get("tempo").toString());
                                empresa.setUrlImagem(documentSnapshot.get("urlImagem").toString());
                                empresas.add(empresa);
                            }
                            adapterEmpresa.notifyDataSetChanged();
                        } else {
                            empresas.clear();
                            adapterEmpresa.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void recuperarEnderecoUsuario() {

        firestore.collection("endereco")
                .document(idUsuario).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {
                            verificarEndereco = true;
                            Log.d("DB", "Sucesso ao Buscar endereço do usuario: "
                                    + documentSnapshot.getData());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        verificarEndereco = false;
                        Log.d("Error", "Erro ao Buscar dados do usuario: " + e.getMessage());
                    }
                });
    }


    private void abrirTelaConfiguracaoUsuario() {
        startActivity(new Intent(HomeActivity.this, ConfiguracaoUsuarioActivity.class));
    }
}