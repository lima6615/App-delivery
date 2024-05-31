package com.project.deliveryapp.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Usuario;
import com.santalu.maskara.widget.MaskEditText;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class CadastrarActivity extends AppCompatActivity {

    private EditText textoNome, textoEmail, textoSenha;
    private MaskEditText textoTelefone, textoCpf, textoCnpj;
    private RadioGroup radioGroup;
    private RadioButton radioUsuario, radioEmpresa;
    private Button botaoSalvar, botaoCancelar;
    private Usuario usuario;
    private String cpfCnpj = "";
    private String tipoConta = null;
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                RadioButton radioSelect = findViewById(checkedID);
                if (radioSelect.getId() == R.id.radioUsuario) {
                    textoCnpj.setEnabled(false);
                    textoCpf.setEnabled(true);
                    tipoConta = "usuario";
                } else {
                    textoCnpj.setEnabled(true);
                    textoCpf.setEnabled(false);
                    tipoConta = "empresa";
                }
            }
        });

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = textoNome.getText().toString();
                String email = textoEmail.getText().toString();
                String senha = textoSenha.getText().toString();
                String telefone = "";

                boolean telefoneValid = textoTelefone.isDone();
                if (telefoneValid) {
                    telefone = textoTelefone.getMasked();
                } else {
                    Toast.makeText(CadastrarActivity.this,
                            "Informe telefone válido.", Toast.LENGTH_LONG).show();
                }

                if (tipoConta == "usuario") {
                    boolean isDone = textoCpf.isDone();
                    if (isDone) {
                        cpfCnpj = textoCpf.getMasked();
                    } else {
                        Toast.makeText(CadastrarActivity.this,
                                "Informe CPF válido.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    boolean isDone = textoCnpj.isDone();
                    if (isDone) {
                        cpfCnpj = textoCnpj.getMasked();
                    } else {
                        Toast.makeText(CadastrarActivity.this,
                                "Informe CNPJ válido.", Toast.LENGTH_LONG).show();
                    }
                }

                if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty() || tipoConta.isEmpty()) {
                    Toast.makeText(CadastrarActivity.this,
                            "Todos os campos são obrigatórios !", Toast.LENGTH_LONG).show();
                } else {
                    if (!cpfCnpj.isEmpty() && !telefone.isEmpty()) {
                        String hashSenha = BCrypt.withDefaults().hashToString(12, senha.toCharArray());
                        usuario = new Usuario(nome, email, telefone, hashSenha, tipoConta, cpfCnpj);
                        cadastrarUsuario(usuario.getEmail(), senha);
                    }
                }
            }
        });

        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaLogin();
            }
        });
    }

    private void inicializacaoComponentes() {
        textoNome = (EditText) findViewById(R.id.campoNomeCadastro);
        textoEmail = (EditText) findViewById(R.id.campoEmailCadastro);
        textoSenha = (EditText) findViewById(R.id.campoSenhaCadastro);
        textoTelefone = (MaskEditText) findViewById(R.id.campoTelefoneCadastro);
        textoCpf = (MaskEditText) findViewById(R.id.campoCpfCadastro);
        textoCnpj = (MaskEditText) findViewById(R.id.campoCnpjCadastro);
        radioGroup = (RadioGroup) findViewById(R.id.radioTipoConta);
        radioUsuario = (RadioButton) findViewById(R.id.radioUsuario);
        radioEmpresa = (RadioButton) findViewById(R.id.radioEmpresa);
        botaoSalvar = (Button) findViewById(R.id.btSalvarProduto);
        botaoCancelar = (Button) findViewById(R.id.btCancelar);
    }

    private void cadastrarUsuario(String email, String senha) {

        firebaseAuth = FirebaseConfig.getAuth();
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            salvarDadosUsuario(usuario);

                            Toast.makeText(CadastrarActivity.this,
                                    "Cadastro Criado com Sucesso!", Toast.LENGTH_LONG).show();

                            abrirTelaLogin();

                        } else {
                            String error = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                error = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                error = "Por favor, digite um e-mail valido!";
                            } catch (FirebaseAuthUserCollisionException e) {
                                error = "Esta conta já foi cadastrada!";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastrarActivity.this,
                                    error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void salvarDadosUsuario(Usuario usuario) {

        firestore = FirebaseConfig.getFirestore();
        String usuarioId = FirebaseConfig.getIdUsuario();

        DocumentReference documentReference = firestore.collection("usuario").document(usuarioId);
        documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DB", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DB", "Error ao salvar os dados " + e.getMessage());
            }
        });
    }

    private void abrirTelaLogin() {
        startActivity(new Intent(CadastrarActivity.this, AutenticacaoActivity.class));
        finish();
    }
}