package com.project.deliveryapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;

public class EmpresaActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ImageView imglogout, imgAdd, imgConfig;

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
    }

    private void inicializarComponenetes() {
        imglogout = (ImageView) findViewById(R.id.imgLogout);
        imgAdd = (ImageView) findViewById(R.id.imageAdd);
        imgConfig = (ImageView) findViewById(R.id.imgConfig);
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

    private void abrirConfiguracoes() {
        startActivity(new Intent(EmpresaActivity.this, ConfiguracaoEmpresaActivity.class));
    }

    private void abrirNovoProduto() {
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
}