package android.app.frasedodia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textoNovaFrase;
    private Button botaoNovaFrase;
    private String[] frases = {
            "Se você traçar metas absurdamente altas e falhar, seu fracasso será muito melhor que o sucesso de todos",
            "O sucesso vem normalmente para quem está ocupado demais para procurar por ele",
            "Se você não está disposto a arriscar, esteja disposto a uma vida comum"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textoNovaFrase = (TextView) findViewById(R.id.textoNovaFraseId);
        botaoNovaFrase = (Button) findViewById(R.id.botaoNovaFraseId);

        botaoNovaFrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textoNovaFrase.setText(frases[geraNumAleatorio(frases.length)]);
            }
        });
    }

    public int geraNumAleatorio(int intervalo){
        Random randomico = new Random();
        int numeroAleatorio = randomico.nextInt(frases.length);

        return numeroAleatorio;
    }
}
