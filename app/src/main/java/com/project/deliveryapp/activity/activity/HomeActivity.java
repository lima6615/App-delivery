package com.project.deliveryapp.activity.activity;

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

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ImageView imgLogoutHome, imgBuscarHome, imgConfigHome;

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

        firebaseAuth = FirebaseConfig.getAuth();
        inicializacaoComponentes();
        imgLogoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deslogarUsuario();
            }
        });
    }

    private void inicializacaoComponentes() {
        imgLogoutHome = (ImageView) findViewById(R.id.imgLogoutHome);
        imgBuscarHome = (ImageView) findViewById(R.id.imgBuscarHome);
        imgConfigHome = (ImageView) findViewById(R.id.imgConfigHome);
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
}