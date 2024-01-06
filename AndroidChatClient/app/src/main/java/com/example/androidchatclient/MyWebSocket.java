package com.example.androidchatclient;

import android.util.JsonWriter;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MyWebSocket extends WebSocketAdapter {

    final String MsTag = "MyWebSocket:Cj";

    // Reference to the ChatRoomPage for callbacks
    private ChatRoomPage chatRoomPage;

    // Constructor to set the ChatRoomPage reference
    public MyWebSocket(ChatRoomPage chatRoomPage) {
        this.chatRoomPage = chatRoomPage;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        super.onConnected(websocket, headers);
        // WebSocket connected successfully
        Log.d(MsTag, "Websocket is connected");

        // Send a 'join' message to the server with username and room name
        websocket.sendText( "join:" + chatRoomPage.getUsername() + ":" + chatRoomPage.getRoom());
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        // A text message is received
        Log.d(MsTag, "Message was received!");
        Log.d(MsTag, text);

        // Parse the incoming JSON message
        JSONObject jsonObject = new JSONObject(text);
        String type = jsonObject.getString("type");
        String user = jsonObject.getString("user");
        String room = jsonObject.getString("room");

        Log.d(MsTag, "Type: " + type + "------------------");
        Log.d(MsTag, "User: " + user + "------------------");
        Log.d(MsTag, "Room: " + room + "------------------");

        if(type.equals("message")){
            // Handle incoming chat messages
            String message = jsonObject.getString("message");
            Log.d(MsTag, "Message: " + message + "------------------");
            chatRoomPage.displayMessage(user, message);
        }
        if (type.equals("join")){
            // Handle a user joining the chat
            chatRoomPage.displayJoin(user);
        }
        if (type.equals("leave")){
            // Handle a user leaving the chat
            chatRoomPage.handleLeave(user);
        }
    }
    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        super.onError(websocket, cause);
        // Handle any WebSocket errors
        Log.d(MsTag, "Error message was received");
    }

}
