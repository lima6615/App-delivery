package com.project.deliveryapp.activity;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.User;

public class CadastrarActivity extends AppCompatActivity {

    private EditText textoNome, textoEmail, textoSenha;
    private RadioButton radioUsuario, radioEmpresa;

    private Button botaoSalvar;

    private FirebaseFirestore firestore;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializacaoComponentes();

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = textoNome.getText().toString();
                String email = textoEmail.getText().toString();
                String senha = textoSenha.getText().toString();
                String tipoConta = null;

                if (radioUsuario.isChecked()) {
                    tipoConta = "usuario";
                } else if (radioEmpresa.isChecked()) {
                    tipoConta = "empresa";
                }

                User user = new User(nome, email, tipoConta);
                insertUsuario(user);
                autenticarUsuario(user.getEmail(), senha);
            }
        });
    }

    private void inicializacaoComponentes() {
        textoNome = (EditText) findViewById(R.id.campoNomeCadastro);
        textoEmail = (EditText) findViewById(R.id.campoEmailCadastro);
        textoSenha = (EditText) findViewById(R.id.campoSenhaCadastro);
        radioUsuario = (RadioButton) findViewById(R.id.radioUsuario);
        radioEmpresa = (RadioButton) findViewById(R.id.radioEmpresa);
        botaoSalvar = (Button) findViewById(R.id.btSalvar);
    }

    private void insertUsuario(User user) {

        firestore = FirebaseConfig.getFirestore();

        firestore.collection("usuario").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CadastrarActivity.this,
                                "Conta criada com sucesso", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadastrarActivity.this,
                                "Falhar ao criar conta", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void abrirTelaHome() {
        startActivity(new Intent(CadastrarActivity.this, AutenticacaoActivity.class));
    }

    private void autenticarUsuario(String email, String senha) {

        firebaseAuth = FirebaseConfig.getAuth();
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            abrirTelaHome();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CadastrarActivity.this,
                                    "Error ao criar conta,Por favor tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}