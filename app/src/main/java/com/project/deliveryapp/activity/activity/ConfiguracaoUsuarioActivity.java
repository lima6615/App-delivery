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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Endereco;

public class ConfiguracaoUsuarioActivity extends AppCompatActivity {

    private ImageView imgVoltaHomeUsuario;
    private EditText editEnderecoBairro, editEnderecoCidade, editEnderecoCep, editEnderecoRua;
    private Button btSalvarEndereco;
    private FirebaseFirestore firestore;
    private String idUsuario = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracao_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseConfig.getFirestore();
        idUsuario = FirebaseConfig.getIdUsuario();
        inicializacaoComponentes();

        imgVoltaHomeUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaHome();
            }
        });

        btSalvarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDadosEndereco();
            }
        });

        recuperarDadosEndereco();
    }

    private void inicializacaoComponentes() {
        imgVoltaHomeUsuario = (ImageView) findViewById(R.id.imageVoltarUsuario);
        editEnderecoBairro = (EditText) findViewById(R.id.editEnderecoBairro);
        editEnderecoCidade = (EditText) findViewById(R.id.editEnderecoCidade);
        editEnderecoCep = (EditText) findViewById(R.id.editEnderecoCep);
        editEnderecoRua = (EditText) findViewById(R.id.editEnderecoRua);
        btSalvarEndereco = (Button) findViewById(R.id.btSalvarEndereco);
    }


    private void validarDadosEndereco() {
        String bairro = editEnderecoBairro.getText().toString();
        String cidade = editEnderecoCidade.getText().toString();
        String cep = editEnderecoCep.getText().toString();
        String rua = editEnderecoRua.getText().toString();

        if (!bairro.isEmpty() && !cidade.isEmpty() && !cep.isEmpty() && !rua.isEmpty()) {
            Endereco endereco = new Endereco(idUsuario, cep, bairro, rua, cidade);
            insertEndereco(endereco);
            exibirMensagem("Endereço cadastrada com sucesso");
            finish();
        } else {
            exibirMensagem("Todos os campos são obrigatórios");
        }
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(ConfiguracaoUsuarioActivity.this, texto, Toast.LENGTH_SHORT).show();
    }

    private void insertEndereco(Endereco endereco) {

        DocumentReference documentReference = firestore.collection("endereco")
                .document(endereco.getIdUsuario());
        documentReference.set(endereco).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void recuperarDadosEndereco() {

        firestore.collection("endereco")
                .document(idUsuario).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult() != null) {

                            editEnderecoBairro.setText(task.getResult().getString("bairro"));
                            editEnderecoCidade.setText(task.getResult().getString("cidade"));
                            editEnderecoCep.setText(task.getResult().getString("cep"));
                            editEnderecoRua.setText(task.getResult().getString("rua"));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", "Erro ao Buscar dados do usuario: " + e.getMessage());
                    }
                });
    }

    private void abrirTelaHome() {
        startActivity(new Intent(ConfiguracaoUsuarioActivity.this, HomeActivity.class));
        finish();
    }
}