package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class ChatRoomPage extends AppCompatActivity  implements SensorEventListener {
    private final String MsTag = "ChatRoomPage:Cj"; // Log tag for debugging
    private String username = "TestPhone";
    private String room = "TestChannel";

    // UI elements
    private TextView roomName;
    private Button sendBtn;
    private EditText messageFld;
    //XML containers
    private LinearLayout messageContainer;
    private LinearLayout userContainer;
    //WebSocket server URL
    private static final String WS_URL = "ws://10.0.2.2:8080/endpoint";
    private WebSocket ws = null;

    public TextView proximityText;
    public TextView lightText;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private Sensor mLight;
    private static final int SENSOR_SENSITIVITY = 4;
    private final static String NOT_SUPPORTED_MESSAGE = "Sorry, sensor not available for this device.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize and link the username, room, and error views from the XML layout
        proximityText = findViewById(R.id.proximityText);
        lightText = findViewById(R.id.lightText);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        try {
            ws = new WebSocketFactory().createSocket(WS_URL);
            // Listen for events using MyWebSocket class
            ws.addListener(new MyWebSocket(this));
            ws.connectAsynchronously();

        } catch (IOException e) {
            // Handle any potential errors
            Log.d(MsTag, "ChatRoomPage Error");
        }
    }

    public void handleSend(View view){
        // Handle sending a message
        String messageText = messageFld.getText().toString();
        if (!messageText.equals(null)) {
            // Send the message to the server
            ws.sendText( "message:" + username + ":" + room + ":" + messageText);
            // Clear the message input field after sending
            messageFld.setText("");
        }
    }

    public String getUsername(){
        //Return the username variable
        return username;
    }
    public String getRoom(){
        //Return the room variable
        return room;
    }
    public void displayMessage(final String username, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(MsTag, "Send message function called");
                String msg = message;
                String user = username;
                // Create a text view to display the message
                TextView messageView = new TextView(ChatRoomPage.this);
                messageView.setPadding(50, 10, 10, 50);
                messageView.setText(user + ": " + msg);
                // Add the message view to the container
                messageContainer.addView(messageView);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ws.sendText( "message:" + username + ":" + room + ":" + "~~~~");
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityText.setText(String.valueOf(event.values[0]));
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near
                Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
                ws.sendText( "message:" + username + ":" + room + ":" + "近");
            } else {
                //far
                Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
                ws.sendText( "message:" + username + ":" + room + ":" + "遠");
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightText.setText(String.valueOf(event.values[0]));
            ws.sendText( "message:" + username + ":" + room + ":" + String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

