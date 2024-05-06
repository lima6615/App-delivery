package com.project.deliveryapp.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.deliveryapp.R;
import com.project.deliveryapp.activity.adapter.AdapterProduto;
import com.project.deliveryapp.activity.config.FirebaseConfig;
import com.project.deliveryapp.activity.entities.Empresa;
import com.project.deliveryapp.activity.entities.Endereco;
import com.project.deliveryapp.activity.entities.ItemPedido;
import com.project.deliveryapp.activity.entities.Pedido;
import com.project.deliveryapp.activity.entities.Produto;
import com.project.deliveryapp.activity.entities.Usuario;
import com.project.deliveryapp.activity.listener.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    private ImageView imgVoltaCardapio;
    private ImageView imgEmpresaCardapio, imgLimparCarrinho;
    private TextView textNomeEmpresaCardapio, textCategoriaEmpresaCardapio, textTempoEmpresaCardapio,
            textEntregaEmpresaCardapio, confirmarPedido, textQuantidade, textSubTotal;
    private RecyclerView recyclerProdutosCardapio;
    private Empresa empresaSelecionado;
    private AdapterProduto adapterProduto;
    private Pedido pedido;
    private String idUsuario;
    private String metodoPagamento;
    private Usuario usuarioRecuperado;
    private Endereco enderecoRecuperado;
    private FirebaseFirestore firestore;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> carrinho = new ArrayList<>();


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

        idUsuario = FirebaseConfig.getIdUsuario();
        firestore = FirebaseConfig.getFirestore();
        inicializacaoComponentes();

        imgVoltaCardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retornaTelaHome();
            }
        });

        imgLimparCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limparCarrinho();
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
        recuperarDadosUsuario();

        recyclerProdutosCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerProdutosCardapio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                ));

        confirmarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!carrinho.isEmpty()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CardapioActivity.this);
                    dialog.setTitle("Selecione um método de Pagamento ?");
                    CharSequence[] itens = new CharSequence[]{
                            "Dinheiro", "Cartão Fisico"
                    };
                    dialog.setSingleChoiceItems(itens, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int itemSelecionado) {
                            if (itemSelecionado < 1) {
                                metodoPagamento = "Dinheiro";
                            } else {
                                metodoPagamento = "Cartão Fisico";
                            }
                        }
                    });

                    EditText editObservacao = new EditText(CardapioActivity.this);
                    editObservacao.setHint("Digite uma observação");
                    dialog.setView(editObservacao);

                    dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String observacao = editObservacao.getText().toString();
                            pedido = new Pedido();
                            pedido.setIdEmpresa(empresaSelecionado.getIdUsuario());
                            pedido.setIdUsuario(idUsuario);
                            pedido.setBaixaPedido(false);
                            pedido.setNome(usuarioRecuperado.getNome());
                            pedido.setEmail(usuarioRecuperado.getEmail());
                            pedido.setTelefone(usuarioRecuperado.getTelefone());
                            pedido.setEndereco(enderecoRecuperado);
                            pedido.setStatus("Confirmado");
                            pedido.setTaxa(Double.parseDouble(empresaSelecionado.getTaxa()));
                            pedido.setItens(carrinho);
                            pedido.setMetodoPagamento(metodoPagamento);
                            pedido.setObservacao(observacao);
                            pedido.total();
                            salvarPedido(pedido);

                            showToast("Pedido realizado com sucesso");
                        }
                    });

                    dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    dialog.show();
                } else {
                    Toast.makeText(CardapioActivity.this,
                            "Selecione um produto para efetuar pagamento"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void inicializacaoComponentes() {
        imgVoltaCardapio = (ImageView) findViewById(R.id.imageVoltarCardapio);
        imgEmpresaCardapio = (ImageView) findViewById(R.id.imageEmpresaCardapio);
        imgLimparCarrinho = (ImageView) findViewById(R.id.limparCarrinho);
        textNomeEmpresaCardapio = (TextView) findViewById(R.id.textNomeEmpresaCardapio);
        textCategoriaEmpresaCardapio = (TextView) findViewById(R.id.textCategoriaEmpresaCardapio);
        textTempoEmpresaCardapio = (TextView) findViewById(R.id.textTempoEmpresaCardapio);
        textEntregaEmpresaCardapio = (TextView) findViewById(R.id.textEntregaEmpresaCardapio);
        textQuantidade = (TextView) findViewById(R.id.textCarrinhoQuantidade);
        textSubTotal = (TextView) findViewById(R.id.textCarrinhoSubTotal);
        confirmarPedido = (TextView) findViewById(R.id.confirmarPedido);
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

    private void showToast(String message) {
        ViewGroup view = findViewById(R.id.container_toast);
        View v = getLayoutInflater().inflate(R.layout.custom_toast, view);

        TextView txtMessage = v.findViewById(R.id.textMessagem);
        txtMessage.setText(message);

        Toast toast = new Toast(this);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void confirmarQuantidade(int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Quantidade");
        dialog.setMessage("Informe a quantidade de itens ?");

        EditText editQuantidade = new EditText(this);
        dialog.setView(editQuantidade);
        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String quantidade = editQuantidade.getText().toString();
                Produto produtoSelecionado = produtos.get(position);

                ItemPedido itemPedido = new ItemPedido(produtoSelecionado.getId(), produtoSelecionado.getNome(),
                        Integer.parseInt(quantidade), produtoSelecionado.getDescricao(), Double.parseDouble(produtoSelecionado.getPrice()));

                for (ItemPedido pedido : carrinho) {
                    if (pedido.getIdProduto().equals(itemPedido.getIdProduto())) {
                        if (itemPedido.getQuantidade() > 0) {
                            Integer quant = Integer.parseInt(quantidade);
                            itemPedido.setQuantidade(quant + 1);
                        } else {
                            Toast.makeText(CardapioActivity.this, "Não é possivel informar quantidade zero"
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                }

                carrinho.add(itemPedido);
                int quant = 0;
                double somar = 0.0;
                for (ItemPedido pedido : carrinho) {
                    quant += pedido.getQuantidade();
                    textQuantidade.setText("qtd: " + String.valueOf(quant));

                    DecimalFormat df = new DecimalFormat("0.00");
                    somar += pedido.subTotal();
                    textSubTotal.setText("R$: " + df.format(somar));
                }
            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }

    private void limparCarrinho() {
        if (!carrinho.isEmpty()) {
            carrinho.clear();
        }
        textQuantidade.setText("qtd: 0");
        textSubTotal.setText("R$: 0.00");
    }

    private void recuperarDadosUsuario() {

        CollectionReference refUsuario = firestore.collection("usuario");
        CollectionReference refEndereco = firestore.collection("endereco");

        refUsuario.document(idUsuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {

                            usuarioRecuperado = new Usuario();
                            usuarioRecuperado.setNome(documentSnapshot.get("nome").toString());
                            usuarioRecuperado.setEmail(documentSnapshot.get("email").toString());
                            usuarioRecuperado.setTelefone(documentSnapshot.get("telefone").toString());
                            refEndereco.document(idUsuario).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    enderecoRecuperado = new Endereco();
                                    enderecoRecuperado.setBairro(documentSnapshot.get("bairro").toString());
                                    enderecoRecuperado.setCep(documentSnapshot.get("cep").toString());
                                    enderecoRecuperado.setCidade(documentSnapshot.get("cidade").toString());
                                    enderecoRecuperado.setIdUsuario(documentSnapshot.get("idUsuario").toString());
                                    enderecoRecuperado.setRua(documentSnapshot.get("rua").toString());
                                }
                            });
                        }
                    }
                });
    }

    private void recuperarProdutos() {

        firestore.collection("produto")
                .whereEqualTo("id", empresaSelecionado.getIdUsuario())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            produtos.clear();
                            for (DocumentSnapshot query : value.getDocuments()) {
                                Produto produto = new Produto();
                                produto.setId(query.getId().toString());
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

    private void salvarPedido(Pedido pedido) {

        firestore.collection("pedido")
                .add(pedido).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("DbPedido", "Sucesso ao Salvar Pedido: " + task.getResult());
                            limparCarrinho();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DB", "Falha ao salvo dados do pediso: " + e.getMessage());
                    }
                });
    }

    private void retornaTelaHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}