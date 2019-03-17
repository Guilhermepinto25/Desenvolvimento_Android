package android.app.whatsapp.activity;

import android.app.whatsapp.R;
import android.app.whatsapp.adapter.MensagemAdapter;
import android.app.whatsapp.config.ConfiguracaoFirebase;
import android.app.whatsapp.helper.Base64Custom;
import android.app.whatsapp.helper.Permissao;
import android.app.whatsapp.helper.Preferencias;
import android.app.whatsapp.model.Conversa;
import android.app.whatsapp.model.Mensagem;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private CircleImageView btMensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private List<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;
    private Boolean hideEditText;


    //dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btMensagem = (CircleImageView) findViewById(R.id.bt_enviar);
        listView = findViewById(R.id.lv_conversas);

        //dados do usuário logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();
        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailUsuarioDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailUsuarioDestinatario);
            hideEditText = extra.getBoolean("hideEditText");
        }

        if (hideEditText){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        //Configura toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Monta ListView e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, (ArrayList<Mensagem>) mensagens);
        listView.setAdapter(adapter);

        //Recuperar mensagens do Firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //Criar listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Limpar Mensagens
                mensagens.clear();

                //Recupera Mensagens
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);

        //Enviar Mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Não é possível enviar mensagem vazia", Toast.LENGTH_LONG).show();
                }else{
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //Salva mensagem para o Remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    if(!retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity.this, "Erro ao salvar mensagem, tente novamente!", Toast.LENGTH_LONG).show();
                    }else{
                        //Salva mensagem para o Destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                    }

                    //Salva Conversa para o Remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if (!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this, "Erro ao salvar conversa, tente novamente!", Toast.LENGTH_LONG).show();
                    }else {
                        //Salva Conversa para o Destinatario
                        Conversa conversa1 = new Conversa();
                        conversa1.setIdUsuario(idUsuarioRemetente);
                        conversa1.setNome(nomeUsuarioRemetente);
                        conversa1.setMensagem(textoMensagem);
                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa1);
                        if (!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity.this, "Erro ao salvar conversa para o destinatário, tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    }
                    editMensagem.setText("");
                }

            }
        });
    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);


            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);


            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
