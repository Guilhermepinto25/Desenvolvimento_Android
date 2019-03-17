package android.app.whatsapp.fragment;


import android.app.whatsapp.activity.ConversaActivity;
import android.app.whatsapp.adapter.ContatoAdapter;
import android.app.whatsapp.config.ConfiguracaoFirebase;
import android.app.whatsapp.helper.Preferencias;
import android.app.whatsapp.model.Contato;
import android.app.whatsapp.model.Usuario;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.whatsapp.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ContatoAdapter adapter;
    private List<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        contatos = new ArrayList<>();

        //Monta listview e adapter
        listView = view.findViewById(R.id.lv_contatos);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contato,
                contatos
        );*/
        adapter = new ContatoAdapter(getActivity(), (ArrayList<Contato>) contatos);
        listView.setAdapter(adapter);

        //Recuperar Usuario Logado
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();

        //Recuperar contatos do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("contatos")
                    .child(identificadorUsuarioLogado);
        //Listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Limpar contatos
                contatos.clear();
                //Listar contador
                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //Recupera Dados a serem passados
                Contato contato = contatos.get(position);

                //Enviando dados para conversa activity
                intent.putExtra("nome", contato.getNome());
                intent.putExtra("email", contato.getEmail());

                //Abrir EditText ao abrir conversa pelo fragment de Contatos
                Boolean hideEditText = false;
                intent.putExtra("hideEditText", hideEditText);

                startActivity(intent);



            }
        });


        return view;
    }

}
