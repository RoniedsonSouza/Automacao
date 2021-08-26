package com.example.automacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnLed1, btnLed2, btnLed3, btnTodos;
    TextView txtLdr;
    EditText txtResultado;

    Handler handler = new Handler();
    boolean statusRecebimento = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLed1 = findViewById(R.id.btnLed1);
        btnLed2 = findViewById(R.id.btnLed2);
        btnLed3 = findViewById(R.id.btnLed3);
        btnTodos = findViewById(R.id.btnTodos);

        txtLdr = findViewById(R.id.txtLdr);
        txtResultado = findViewById(R.id.txtResultado);

        handler.postDelayed(atualizaStatus, 0);

        btnLed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicita("led1");
            }
        });
        btnLed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicita("led2");
            }
        });
        btnLed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicita("led3");
            }
        });
        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicita("todos");
            }
        });
    }

    private Runnable atualizaStatus = new Runnable() {
        @Override
        public void run() {
            if(statusRecebimento) {
                solicita("");
                handler.postDelayed(this, 2000);
            }else{
                handler.removeCallbacks(atualizaStatus);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusRecebimento = false;
    }

    public void solicita(String comando)
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
            String url = "http://192.168.100.47/";

            new SolicitaDados().execute(url + comando);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
        }
    }

    private class SolicitaDados extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... url) {

            return Conexao.getDados(url[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {
            if(resultado != null)
            {
                txtResultado.setText(resultado);
                if(resultado.contains("led1on"))
                {
                    btnLed1.setText("LED 1 - ON");
                }
                else if(resultado.contains("led1off"))
                {
                    btnLed1.setText("LED 1 - OFF");
                }

                if(resultado.contains("led2on"))
                {
                    btnLed2.setText("LED 2 - ON");
                }
                else if(resultado.contains("led2off"))
                {
                    btnLed2.setText("LED 2 - OFF");
                }

                if(resultado.contains("led3on"))
                {
                    btnLed3.setText("LED 3 - ON");
                }
                else if(resultado.contains("led3off"))
                {
                    btnLed3.setText("LED 3 - OFF");
                }

                String[] dados_recebidos = resultado.split(",");
                txtLdr.setText(dados_recebidos[3]);

            }
            else
            {
                Toast.makeText(MainActivity.this, "Ocorreu um erro", Toast.LENGTH_LONG).show();
            }
        }
    }
}