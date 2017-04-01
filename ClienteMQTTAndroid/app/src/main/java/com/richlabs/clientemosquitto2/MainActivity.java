package com.richlabs.clientemosquitto2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    EditText txtMensaje;
    SeekBar Brillo;
    ToggleButton BtnFoco1;

    static String MosquittoServer = "tcp://192.168.8.1:1883";
    //static String MosquittoUsr = "";
    //static String MosquittoPass = "";
    static String Topic = "ARDUINODAY";

    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        Brillo = (SeekBar) findViewById(R.id.Dimmer);
        Brillo.setMax(19);
        Brillo.setProgress(1);

        Brillo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar barra, int i, boolean b){
                sendCMDtoFoco2(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar barra){

            }

            @Override
            public void onStopTrackingTouch(SeekBar barra){

            }
        });

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MosquittoServer, clientId);

        //MqttConnectOptions opciones = new MqttConnectOptions();
        //opciones.setUserName(MosquittoUsr);
        //opciones.setPassword(MosquittoPass.toCharArray());

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Conectado", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Falló conexión", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publicar(View v){
        String Mensaje = txtMensaje.getText().toString();
        try {
            client.publish(Topic, Mensaje.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void onFoco1StateChanged(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        String Mensaje = "FOCO1";

        if (on) {
            Mensaje = "PRENDE " + Mensaje;
        } else {
            Mensaje = "APAGAR " + Mensaje;
        }
        try {
            client.publish(Topic, Mensaje.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private void sendCMDtoFoco2(int valor) {
        String Mensaje = "DIMMER FOCO2 " + String.format("%02d", valor);;

        try {
            client.publish(Topic, Mensaje.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
