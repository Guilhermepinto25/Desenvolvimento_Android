package android.app.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String NOME_ARQUIVO = "whatsapp-preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_NOME = "nomeUsuarioLogado";

    public Preferencias(Context contextoParamentro) {
        context = contextoParamentro;
        sharedPreferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = sharedPreferences.edit();
    }

    public void salvarDados (String identificadorUsuario, String nomeUsuario){

        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.commit();
    }

    public String getIdentificador(){
        return sharedPreferences.getString(CHAVE_IDENTIFICADOR, null);
    }

    public String getNome(){
        return sharedPreferences.getString(CHAVE_NOME, null);
    }


}
