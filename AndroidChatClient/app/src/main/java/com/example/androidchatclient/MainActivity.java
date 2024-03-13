package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;
import java.util.*;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    final String MsTag = "MainActivity:Cj";
    public TextView proximityText;
    public TextView lightText;

    public static final String roomNameKey = "roomNameKey";  // Key for passing room name between activities
    public static final String userNameKey="userNameKey";   // Key for passing username between activities

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
    }

    public void handleLogin(View view) {
        // Log what's happening in the terminal
        Log.d(MsTag, "Button was pressed...");

        // Get the user input from the EditText fields
        String userCheck = "TestPhone";
        String channelCheck = "TestChannel";


        // Check if both the username and chat room have at least one letter
        boolean userHasLtrs = userCheck.matches(".*[a-zA-Z].*");
        boolean roomHasLtrs = channelCheck.matches(".*[a-zA-Z].*");

        if (userHasLtrs && roomHasLtrs) {
            // Create an intent to switch to the ChatRoomPage activity
            Intent intent = new Intent(this, ChatRoomPage.class);
            // Pass the room name and username as extras to the ChatRoomPage activity
            intent.putExtra(userNameKey, userCheck);
            intent.putExtra(roomNameKey, channelCheck);
            // Start the ChatRoomPage activity
            startActivity(intent);
        } else {
            // Display an error message if either the username or chat room text is empty
//            error.setText("Username and chat room are required fields.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityText.setText(String.valueOf(event.values[0]));
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near
                Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
            } else {
                //far
                Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightText.setText(String.valueOf(event.values[0]));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
