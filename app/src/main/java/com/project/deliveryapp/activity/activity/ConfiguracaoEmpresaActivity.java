package com.project.deliveryapp.activity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Empresa;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ConfiguracaoEmpresaActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaTaxa;
    private Button botaoSalvarEmpresa;
    private ImageView imagePerfilEmpresa, imageVoltar;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String usuarioId = null;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracao_empresa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarComponenetes();
        storageReference = FirebaseConfig.getStorage().getReference();
        firestore = FirebaseConfig.getFirestore();
        usuarioId = FirebaseConfig.getIdUsuario();
        imageVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaHome();
            }
        });


        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        botaoSalvarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDadosEmpresa();
            }
        });

        recuperarDadosEmpresa();
    }


    private void inicializarComponenetes() {
        imagePerfilEmpresa = (ImageView) findViewById(R.id.imagePerfilEmpresa);
        imageVoltar = (ImageView) findViewById(R.id.imageVoltar);
        editEmpresaNome = (EditText) findViewById(R.id.editProdutoNome);
        editEmpresaCategoria = (EditText) findViewById(R.id.editProdutoDescricao);
        editEmpresaTempo = (EditText) findViewById(R.id.editProdutoPreco);
        editEmpresaTaxa = (EditText) findViewById(R.id.editEmpresaTaxa);
        botaoSalvarEmpresa = (Button) findViewById(R.id.btSalvarProduto);
    }

    private void validarDadosEmpresa() {

        String nome = editEmpresaNome.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String taxa = editEmpresaTaxa.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();

        if (!nome.isEmpty() && !categoria.isEmpty() && !taxa.isEmpty() && !tempo.isEmpty() && urlImagemSelecionada != null) {
            Empresa empresa = new Empresa(usuarioId, urlImagemSelecionada, nome, tempo, categoria, taxa);
            insertEmpresa(empresa);
            exibirMensagem("Empresa cadastrada com sucesso");
            finish();
        } else {
            exibirMensagem("Todos os campos são obrigatórios");
        }
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(ConfiguracaoEmpresaActivity.this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECAO_GALERIA) {

            try {
                Uri localImagem = data.getData();
                Bitmap imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);

                if (imagem != null) {
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresa")
                            .child(usuarioId + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracaoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    urlImagemSelecionada = task.getResult().toString();
                                }
                            });

                            Toast.makeText(ConfiguracaoEmpresaActivity.this, "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void insertEmpresa(Empresa empresa) {

        DocumentReference documentReference = firestore.collection("empresa").document(usuarioId);
        documentReference.set(empresa).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void recuperarDadosEmpresa() {

        firestore.collection("empresa")
                .document(usuarioId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult() != null) {

                            editEmpresaNome.setText(task.getResult().getString("nome"));
                            editEmpresaCategoria.setText(task.getResult().getString("categoria"));
                            editEmpresaTaxa.setText(task.getResult().getString("taxa"));
                            editEmpresaTempo.setText(task.getResult().getString("tempo"));
                            urlImagemSelecionada = task.getResult().getString("urlImagem");

                            if (urlImagemSelecionada != "") {
                                Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                            }
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
        startActivity(new Intent(ConfiguracaoEmpresaActivity.this, EmpresaActivity.class));
        finish();
    }
}