package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;

public class AutenticacaoActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button btAcessar;
    private Switch tipoAcesso;
    private String tipoConta;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_autenticacao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializaComponentes();
        firestore = FirebaseConfig.getFirestore();
        firebaseAuth = FirebaseConfig.getAuth();
        firebaseAuth.signOut();
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startActivity(new Intent(AutenticacaoActivity.this, CadastrarActivity.class));
                }
            }
        });

        btAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty() && !senha.isEmpty()) {

                    firebaseAuth.signInWithEmailAndPassword(email, senha)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com sucesso!", Toast.LENGTH_LONG).show();
                                        findByid();
                                    } else {
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao Efetuar Login!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    if (email.isEmpty()) {
                        Toast.makeText(AutenticacaoActivity.this,
                                "Informer o Email", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AutenticacaoActivity.this,
                                "Informer a Senha", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void inicializaComponentes() {
        campoEmail = (EditText) findViewById(R.id.campoEmail);
        campoSenha = (EditText) findViewById(R.id.campoSenha);
        btAcessar = (Button) findViewById(R.id.btAcessar);
        tipoAcesso = (Switch) findViewById(R.id.tipoAcesso);
    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuario = firebaseAuth.getCurrentUser();
        if (usuario != null) {
            abrirTelaHome(tipoConta);
        }
    }

    private void abrirTelaHome(String tipoConta) {
        if ("usuario".equals(tipoConta)) {
            startActivity(new Intent(AutenticacaoActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(AutenticacaoActivity.this, EmpresaActivity.class));
        }
    }

    private void findByid() {
        String id = FirebaseConfig.getIdUsuario();
        firestore.collection("usuario")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        tipoConta = task.getResult().getString("tipoConta");
                        abrirTelaHome(tipoConta);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", "Erro ao buscar dados do usuario: " + e.getMessage());
                    }
                });
    }
}