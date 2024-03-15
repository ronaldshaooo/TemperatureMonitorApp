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

public class MainActivity extends AppCompatActivity {
    public static final String roomNameKey = "roomNameKey";  // Key for passing room name between activities
    public static final String userNameKey="userNameKey";   // Key for passing username between activities

    final String MsTag = "MainActivity:Cj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


}

