package android.app.whatsapp.adapter;

import android.app.whatsapp.R;
import android.app.whatsapp.model.Conversa;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ConversaAdapter extends ArrayAdapter<Conversa> {
    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter(Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        //Verifica se a lista esta vazia
        if (conversas != null){
            //inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Monta view a partir do XML
            view = inflater.inflate(R.layout.lista_conversa, parent, false);

            //Recupera elemento para exibicao
            TextView nome = view.findViewById(R.id.tv_nome_destinatario);
            TextView ultimaMensagem = view.findViewById(R.id.tv_ultima_mensagem);

            Conversa conversa = conversas.get(position);
            nome.setText(conversa.getNome());
            ultimaMensagem.setText(conversa.getMensagem());
        }

        return view;

    }
}
