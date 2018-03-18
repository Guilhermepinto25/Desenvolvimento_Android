package android.app.signos;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ListView listaSignos;
    private String[] signos = {
            "Áries", "Touro", "Gêmeos", "Câncer", "Leão", "Virgem",
            "Libra", "Escorpão", "Sagitário", "Capricórnio", "Aquário",
            "Peixes"
    };

    private String[] perfis = {
            "Arianos são animados, independentes, individualistas, dinâmicos, corajosos e aventureiros",
            "Taurinos são positivos, pacientes, determinados, noturnos, leais e românticos",
            "Geminianos são positivos, mutáveis, racionais e bastante voláteis",
            "...","...","...","...","...","...","...","...","..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaSignos = (ListView) findViewById(R.id.listViewId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                signos
        );

        listaSignos.setAdapter(adapter);

        listaSignos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(MainActivity.this, perfis[position], Toast.LENGTH_LONG).show();
            }
        });
    }
}
