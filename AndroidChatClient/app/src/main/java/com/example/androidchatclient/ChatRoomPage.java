package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class ChatRoomPage extends AppCompatActivity {
    private final String MsTag = "ChatRoomPage:Cj"; // Log tag for debugging
    private String username;
    private String room;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_page);

        Bundle extras=getIntent().getExtras();

        if (extras!=null) {
            //Grabbing extras sent from previous page
            // Retrieve the username
            username = extras.getString(MainActivity.userNameKey);
            // Retrieve the chat room name
            room = extras.getString(MainActivity.roomNameKey);
        }

        // Getting references to various UI elements
        roomName = findViewById(R.id.RmName);
        sendBtn = findViewById(R.id.sendBtn);
        messageFld = findViewById(R.id.msgFld);
        messageContainer = findViewById(R.id.messageContainer);
        userContainer = findViewById(R.id.usersFld);

        // Set the room name
        roomName.setText(room);

        // Add "Users:" label to the user container
        TextView userText = new TextView(this);
        userText.setPadding(50,10,10,10);
        userText.setText("Users: ");
        userContainer.addView(userText);


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

    public void handleLeave (View view){
        ws.sendText( "leave:" + username + ":" + room);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        public void displayJoin(final String username) {
            // Display a user joining the chat
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String user=username;
                    TextView addedUser = new TextView(ChatRoomPage.this);
                    addedUser.setPadding(50,10,10,10);
                    addedUser.setText(user);
                    userContainer.addView(addedUser);
                }
            });
    }

    public void handleLeave(final String username) {
        // Handle a user leaving the chat
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Find and remove the view with the username from userContainer
                for (int i = 0; i < userContainer.getChildCount(); i++) {
                    View child = userContainer.getChildAt(i);
                    if (child instanceof TextView) {
                        TextView textView = (TextView) child;
                        if (textView.getText().toString().equals(username)) {
                            userContainer.removeView(textView);
                            break; // Exit the loop once the user is removed
                        }
                    }
                }
            }
        });
    }

}
