package android.app.whatsapp.fragment;


import android.app.whatsapp.activity.ConversaActivity;
import android.app.whatsapp.adapter.ConversaAdapter;
import android.app.whatsapp.config.ConfiguracaoFirebase;
import android.app.whatsapp.helper.Base64Custom;
import android.app.whatsapp.helper.Preferencias;
import android.app.whatsapp.model.Contato;
import android.app.whatsapp.model.Conversa;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.whatsapp.R;
import android.view.WindowManager;
import android.widget.AdapterView;
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
public class ConversasFragment extends Fragment {

    private ListView listView;
    private List<Conversa> conversas;
    private ConversaAdapter adapter;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ConversasFragment() {
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
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //Monta listview e adapter
        conversas = new ArrayList<>();
        listView = view.findViewById(R.id.lv_conversas);
        adapter = new ConversaAdapter(getActivity(), (ArrayList<Conversa>) conversas);
        listView.setAdapter(adapter);

        //Recuperar dados do usuário
        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuarioLogado = preferencias.getIdentificador();

        //Recuperar conversar do Firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("conversas")
                .child(idUsuarioLogado);
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversas.clear();
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //Adicionar evento de clique na lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                //Recupera Dados a serem passados
                Conversa conversa = conversas.get(position);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                //Enviando dados para conversa activity
                intent.putExtra("nome", conversa.getNome());
                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                intent.putExtra("email", email);

                //Esconder EditText ao abrir conversa pelo fragment de Conversa
                Boolean hideEditText = true;
                intent.putExtra("hideEditText", hideEditText);

                startActivity(intent);

            }
        });


        return view;
    }

}
